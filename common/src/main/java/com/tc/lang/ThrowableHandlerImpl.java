/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tc.lang;

import com.google.common.base.Throwables;
import com.tc.config.schema.setup.ConfigurationSetupException;
import com.tc.exception.DatabaseException;
import com.tc.exception.ExceptionHelper;
import com.tc.exception.ExceptionHelperImpl;
import com.tc.exception.RuntimeExceptionHelper;
import com.tc.handler.CallbackStartupExceptionLoggingAdapter;
import com.tc.logging.CallbackOnExitHandler;
import com.tc.logging.CallbackOnExitState;
import com.tc.logging.TCLogger;
import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;
import com.tc.util.TCDataFileLockingException;
import com.tc.util.concurrent.ThreadUtil;
import com.tc.util.startuplock.FileNotCreatedException;
import com.tc.util.startuplock.LocationNotCreatedException;

import java.net.BindException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Arrays.stream;

/**
 * Handles Throwable appropriately by printing messages to the logger, etc. Deal with nasty problems that can occur as
 * the Terracotta Client is shutting down.
 */
public class ThrowableHandlerImpl implements ThrowableHandler {
  // XXX: The dispatching in this class is retarded, but I wanted to move as much of the exception handling into a
  // single place first, then come up with fancy ways of dealing with them. --Orion 03/20/2006

  // instantiating message here to avoid any allocations on OOME
  private static final String                        OOME_ERROR_MSG                  = "Fatal error: out of available memory. Exiting...";
  protected final TCLogger                           logger;
  private final ExceptionHelperImpl                  helper;
  private final List<CallbackOnExitHandler>          callbackOnExitDefaultHandlers   = new CopyOnWriteArrayList();
  private final Map<Class<?>, CallbackOnExitHandler> callbackOnExitExceptionHandlers = new HashMap();
  private final Object                               dumpLock                        = new Object();

  private static final long                          TIME_OUT                        = TCPropertiesImpl
                                                                                         .getProperties()
                                                                                       .getLong(TCPropertiesConsts.L2_DUMP_ON_EXCEPTION_TIMEOUT)
                                                                                       * 1000;
  private boolean                                    isExitScheduled;
  private boolean                                    isDumpTaken;

  /**
   * Construct a new ThrowableHandler with a logger
   * 
   * @param logger Logger
   */
  public ThrowableHandlerImpl(TCLogger logger) {
    this.logger = logger;
    helper = new ExceptionHelperImpl();
    helper.addHelper(new RuntimeExceptionHelper());
    registerStartupExceptionCallbackHandlers();
  }

  @Override
  public void addHelper(ExceptionHelper toAdd) {
    helper.addHelper(toAdd);
  }

  protected void registerStartupExceptionCallbackHandlers() {
    addCallbackOnExitExceptionHandler(ConfigurationSetupException.class, new CallbackStartupExceptionLoggingAdapter());
    String bindExceptionExtraMessage = ".  Please make sure the server isn't already running or choose a different port.";
    addCallbackOnExitExceptionHandler(BindException.class,
                                      new CallbackStartupExceptionLoggingAdapter(bindExceptionExtraMessage));
    addCallbackOnExitExceptionHandler(DatabaseException.class, new CallbackStartupExceptionLoggingAdapter());
    addCallbackOnExitExceptionHandler(LocationNotCreatedException.class, new CallbackStartupExceptionLoggingAdapter());
    addCallbackOnExitExceptionHandler(FileNotCreatedException.class, new CallbackStartupExceptionLoggingAdapter());
    addCallbackOnExitExceptionHandler(TCDataFileLockingException.class, new CallbackStartupExceptionLoggingAdapter());
  }

  @Override
  public void addCallbackOnExitDefaultHandler(CallbackOnExitHandler callbackOnExitHandler) {
    callbackOnExitDefaultHandlers.add(callbackOnExitHandler);
  }

  @Override
  public void addCallbackOnExitExceptionHandler(Class c, CallbackOnExitHandler exitHandler) {
    callbackOnExitExceptionHandlers.put(c, exitHandler);
  }

  /**
   * Handle throwable occurring on thread
   * 
   * @param thread Thread receiving Throwable
   * @param t Throwable
   */
  @Override
  public void handleThrowable(final Thread thread, final Throwable t) {
    handlePossibleOOME(t);

    if (isThreadGroupDestroyed(thread, t)) {
      logger.warn("Ignoring an attempt to start a JMX thread during shutdown.", t);
      return;
    }

    if (isJMXTerminatedException(t)) {
      logger.warn("Ignoring a Thread Service termination error from JMX.", t);
      return;
    }

    if (isNotificationFetcherThread(t)) {
      // DEV-5006 -- Do not exit L2.
      logger.warn("Got Exception in JMX Notification forwarding", t);
      return;
    }

    final CallbackOnExitState throwableState = new CallbackOnExitState(t);

    // TAB-5348
    // scheduleExit(throwableState);

    final Throwable proximateCause = helper.getProximateCause(t);
    final Throwable ultimateCause = helper.getUltimateCause(t);

    Object registeredExitHandlerObject;
    try {
      if ((registeredExitHandlerObject = callbackOnExitExceptionHandlers.get(proximateCause.getClass())) != null) {
        ((CallbackOnExitHandler) registeredExitHandlerObject).callbackOnExit(throwableState);
      } else if ((registeredExitHandlerObject = callbackOnExitExceptionHandlers.get(ultimateCause.getClass())) != null) {
        ((CallbackOnExitHandler) registeredExitHandlerObject).callbackOnExit(throwableState);
      } else {
        handleDefaultException(thread, throwableState);
      }
    } catch (Throwable throwable) {
      logger.error("Error while handling uncaught expection" + t.getCause(), throwable);
    }

    exit(throwableState);
  }

  private static boolean isThreadGroupDestroyed(Thread thread, Throwable t) {
    // see EHCTERR-32
    if (t instanceof IllegalThreadStateException) {
      StackTraceElement[] stack = t.getStackTrace();
      StackTraceElement bottom = stack[stack.length - 1];
      if (stack[0].getClassName().equals("java.lang.ThreadGroup") && stack[0].getMethodName().equals("addUnstarted")
          && bottom.getClassName().equals("javax.management.remote.generic.GenericConnectorServer$Receiver")
          && bottom.getMethodName().equals("run")) { return true; }
    }

    return false;
  }

  /**
   * Makes sure we don't allocate any heap objects on OOME.
   * {@code -XX:+HeapDumpOnOutOfMemoryError} should take care of debug information.
   * Considering {@code -XX:OnOutOfMemoryError=<cmd>} option might be also a good idea.
   */
  @Override
  public void handlePossibleOOME(final Throwable t) {
    Throwable rootCause = Throwables.getRootCause(t);
    if (rootCause instanceof OutOfMemoryError) {
      try {
        logger.error(OOME_ERROR_MSG);
        String msg = rootCause.getMessage();
        if (msg != null && msg.length() > 0) {
          logger.error(msg);
        }
      } finally {
        exit(ServerExitStatus.EXITCODE_FATAL_ERROR);
      }
    }
  }

  private void handleDefaultException(Thread thread, CallbackOnExitState throwableState) {
    logException(thread, throwableState);

    synchronized (dumpLock) {
      if (!isDumpTaken) {
        isDumpTaken = true;
        for (CallbackOnExitHandler handler : callbackOnExitDefaultHandlers) {
          handler.callbackOnExit(throwableState);
        }
      }
    }
  }

  private void logException(Thread thread, CallbackOnExitState throwableState) {
    try {
      // We need to make SURE that our stacktrace gets printed, when using just the logger sometimes the VM exits
      // before the stacktrace prints
      throwableState.getThrowable().printStackTrace(System.err);
      System.err.flush();
      logger.error("Thread:" + thread + " got an uncaught exception. calling CallbackOnExitDefaultHandlers.",
                   throwableState.getThrowable());
    } catch (Exception e) {
      // IGNORE EXCEPTION HERE
    }
  }

  private void exit(CallbackOnExitState throwableState) {
    boolean autoRestart = TCPropertiesImpl.getProperties().getBoolean(TCPropertiesConsts.L2_NHA_AUTORESTART);

    logger.info("ExitState : " + throwableState + "; AutoRestart: " + autoRestart);
    if (autoRestart && throwableState.isRestartNeeded()) {
      exit(ServerExitStatus.EXITCODE_RESTART_REQUEST);
    } else {
      exit(ServerExitStatus.EXITCODE_STARTUP_ERROR);
    }
  }

  protected synchronized void exit(int status) {
    // let all the logging finish
    ThreadUtil.reallySleep(2000);
    System.exit(status);
  }

  private static boolean isNotificationFetcherThread(Throwable t) {
    return stream(t.getStackTrace()).anyMatch(ste ->
      ste.getClassName().equals("com.sun.jmx.remote.opt.internal.ClientNotifForwarder$NotifFetcher")
        && ste.getMethodName().equals("fetchNotifs"));
  }

  private static boolean isJMXTerminatedException(Throwable throwable) {
    return throwable instanceof IllegalStateException &&
           throwable.getMessage().contains("The Thread Service has been terminated.");
  }
}
