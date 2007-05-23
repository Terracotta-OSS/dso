/*
 * All content copyright (c) 2003-2006 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.util.concurrent.locks;

import com.tc.exception.TCNotSupportedMethodException;
import com.tc.exception.TCRuntimeException;
import com.tc.object.bytecode.ManagerUtil;
import com.tc.object.lockmanager.api.LockLevel;
import com.tc.util.Stack;
import com.tc.util.UnsafeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ReentrantLock implements Lock, java.io.Serializable {
  private boolean  isFair;

  /** Current owner thread */
  transient Thread owner            = null;
  transient int    numOfHolds       = 0;
  transient List   waitingQueue     = new ArrayList();
  transient int    state            = 0;
  transient int    numQueued        = 0;
  transient Stack  lockInUnShared   = new Stack();
  transient List   tryLockWaitQueue = new LinkedList();

  transient Object lock             = new Object();

  public ReentrantLock() {
    this.isFair = false;

    initialize();
  }

  public ReentrantLock(boolean fair) {
    this.isFair = fair;

    initialize();
  }

  private void initialize() {
    this.owner = null;
    this.numOfHolds = 0;
    this.waitingQueue = new ArrayList();
    this.state = 0;
    this.numQueued = 0;
    this.lock = new Object();
    this.lockInUnShared = new Stack();
    this.tryLockWaitQueue = new LinkedList();
  }

  public void lock() {
    Thread currentThread = Thread.currentThread();
    boolean isInterrupted = false;
    synchronized (lock) {
      while (owner != null && owner != currentThread && lockInUnShared.contains(Boolean.TRUE)) {
        try {
          lock.wait();
        } catch (InterruptedException e) {
          isInterrupted = true;
        }
      }

      waitingQueue.add(currentThread);
      numQueued++;
    }

    ManagerUtil.monitorEnter(this, LockLevel.WRITE);
    UnsafeUtil.monitorEnter(this);

    synchronized (lock) {
      innerSetLockState();
      waitingQueue.remove(currentThread);
      numQueued--;
    }
    if (isInterrupted) {
      currentThread.interrupt();
    }
  }

  public void lockInterruptibly() throws InterruptedException {
    if (Thread.interrupted()) throw new InterruptedException();
    lock();
    if (Thread.interrupted()) {
      if (isHeldByCurrentThread()) {
        unlock();
      }
      throw new InterruptedException();
    }
  }

  public boolean tryLock() {
    boolean canLock = false;
    synchronized (lock) {
      canLock = canProceedToLock();
      if (ManagerUtil.isManaged(this) && canLock) {
        canLock = ManagerUtil.tryMonitorEnter(this, 0, LockLevel.WRITE);
        if (canLock) {
          UnsafeUtil.monitorEnter(this);
        }
      } else {
        if (canLock) {
          UnsafeUtil.monitorEnter(this);
        }
      }
      if (canLock) {
        innerSetLockState();
      }
      return canLock;
    }
  }
  
  private void addCurrentThreadToQueue() {
    waitingQueue.add(Thread.currentThread());
    numQueued++;
  }
  
  private void removeCurrentThreadFromQueue() {
    waitingQueue.remove(Thread.currentThread());
    numQueued--;
  }

  public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
    Thread currentThread = Thread.currentThread();

    long totalTimeoutInNanos = unit.toNanos(timeout);

    while (totalTimeoutInNanos > 0 && !tryLock()) {
      synchronized (lock) {
        addCurrentThreadToQueue();

        if (owner != null && owner != currentThread && lockInUnShared.contains(Boolean.TRUE)) {
          try {
            totalTimeoutInNanos = waitForLocalLock(lock, totalTimeoutInNanos);
          } finally {
            removeCurrentThreadFromQueue();
          }
          continue;
        }
      }

      try {
        boolean isLocked = ManagerUtil.tryMonitorEnter(this, totalTimeoutInNanos, LockLevel.WRITE);
        if (isLocked) {
          UnsafeUtil.monitorEnter(this);
          synchronized (lock) {
            innerSetLockState();
          }
          return true;
        } else {
          synchronized(lock) {
            if (ManagerUtil.isManaged(this)) {
              return false;
            } else {
              totalTimeoutInNanos = waitForLocalLock(lock, totalTimeoutInNanos);
              continue;
            }
          }
        }
      } finally {
        synchronized (lock) {
          removeCurrentThreadFromQueue();
        }
      }
    }

    return (totalTimeoutInNanos > 0);
  }

  private long waitForLocalLock(Object waitObject, long totalTimeoutInNanos) throws InterruptedException {
    long startTime = System.nanoTime();
    synchronized (waitObject) {
      TimeUnit.NANOSECONDS.timedWait(waitObject, totalTimeoutInNanos);
    }
    long endTime = System.nanoTime();
    totalTimeoutInNanos -= (endTime - startTime);
    return totalTimeoutInNanos;
  }

  public void unlock() {
    boolean needDSOUnlock = false;
    synchronized (lock) {
      boolean isLockedInUnSharedMode = ((Boolean) this.lockInUnShared.pop()).booleanValue();
      needDSOUnlock = !isLockedInUnSharedMode && ManagerUtil.isManaged(this) && !ManagerUtil.isCreationInProgress();

      if (--numOfHolds == 0) {
        owner = null;
        setState(0);
        this.lockInUnShared.remove(Thread.currentThread());
      }
      UnsafeUtil.monitorExit(this);
      if (!needDSOUnlock) {
        lock.notifyAll();

        /*
         * int tryLockWaitObjectIndex = tryLockWaitQueue.size() - 1; if (tryLockWaitObjectIndex >= 0) { Object
         * tryLockWaitObject = this.tryLockWaitQueue.remove(tryLockWaitObjectIndex); synchronized (tryLockWaitObject) {
         * tryLockWaitObject.notify(); } }
         */
      }
    }
    if (needDSOUnlock) {
      ManagerUtil.monitorExit(this);
    }
  }

  public Condition newCondition() {
    return new ConditionObject(new SyncCondition(), this);
  }

  public int getHoldCount() {
    return (owner == Thread.currentThread()) ? numOfHolds : 0;
  }

  public boolean isHeldByCurrentThread() {
    Thread owner = null;
    int state = 0;
    synchronized (lock) {
      owner = this.owner;
      state = getState();
    }
    return state != 0 && owner == Thread.currentThread();
  }

  public boolean isLocked() {
    if (ManagerUtil.isManaged(this)) {
      return isHeldByCurrentThread() || ManagerUtil.isLocked(this, LockLevel.WRITE);
    } else {
      return getState() > 0;
    }
  }

  public final boolean isFair() {
    return isFair;
  }

  protected Thread getOwner() {
    if (ManagerUtil.isManaged(this)) {
      throw new TCNotSupportedMethodException();
    } else {
      return (numOfHolds == 0) ? null : owner;
    }
  }

  public final boolean hasQueuedThreads() {
    if (ManagerUtil.isManaged(this)) {
      return ManagerUtil.queueLength(this) > 0;
    } else {
      return numQueued > 0;
    }
  }

  public final boolean hasQueuedThread(Thread thread) {
    if (ManagerUtil.isManaged(this)) {
      throw new TCNotSupportedMethodException();
    } else {
      List waitingThreads = null;
      synchronized (lock) {
        waitingThreads = waitingQueue;
      }
      return waitingThreads.contains(thread);
    }
  }

  public final int getQueueLength() {
    if (ManagerUtil.isManaged(this)) {
      return ManagerUtil.queueLength(this);
    } else {
      return numQueued;
    }
  }

  protected Collection getQueuedThreads() {
    if (ManagerUtil.isManaged(this)) {
      throw new TCNotSupportedMethodException();
    } else {
      List waitingThreads = null;
      synchronized (lock) {
        waitingThreads = waitingQueue;
      }
      return waitingThreads;
    }
  }

  public boolean hasWaiters(Condition condition) {
    if (condition == null) throw new NullPointerException();
    if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject)) throw new IllegalArgumentException(
                                                                                                               "not owner");
    return false;
  }

  public int getWaitQueueLength(Condition condition) {
    if (condition == null) throw new NullPointerException();
    if (!(condition instanceof ConditionObject)) throw new IllegalArgumentException("not owner");
    return ((ConditionObject) condition).getWaitQueueLength(this);
  }

  protected Collection getWaitingThreads(Condition condition) {
    if (ManagerUtil.isManaged(this)) throw new TCNotSupportedMethodException();

    if (condition == null) throw new NullPointerException();
    if (!(condition instanceof ConditionObject)) throw new IllegalArgumentException("not owner");
    return ((ConditionObject) condition).getWaitingThreads(this);
  }

  private String getLockState() {
    return (isLocked() ? (isHeldByCurrentThread() ? "[Locally locked]" : "[Remotelly locked]") : "[Unlocked]");
  }

  public String toString() {
    Thread owner = null;
    return ManagerUtil.isManaged(this) ? (new StringBuilder()).append(super.toString()).append(getLockState())
        .toString() : (new StringBuilder()).append(super.toString())
        .append(
                (owner = getOwner()) != null ? (new StringBuilder()).append("[Locked by thread ")
                    .append(owner.getName()).append("]").toString() : "[Unlocked]").toString();

  }

  private boolean canProceedToLock() {
    boolean canLock = waitingQueue.isEmpty() && (getState() == 0);
    return (owner == null && canLock) || (owner == Thread.currentThread());
  }

  private void innerSetLockState() {
    if (!ManagerUtil.isManaged(this)
        || !ManagerUtil.isHeldByCurrentThread(this, LockLevel.WRITE)) {
      this.lockInUnShared.push(Boolean.TRUE);
    } else {
      this.lockInUnShared.push(Boolean.FALSE);
    }

    owner = Thread.currentThread();
    numOfHolds++;
    setState(1);
  }

  private void setState(int state) {
    this.state = state;
  }

  private int getState() {
    return this.state;
  }

  private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
    s.defaultReadObject();
    isFair = s.readBoolean();
    initialize();
  }

  private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
    s.defaultWriteObject();
    s.writeBoolean(isFair);
  }

  private static class SyncCondition implements java.io.Serializable {
    private final static byte SIGNALLED     = 0;
    private final static byte NOT_SIGNALLED = 1;

    private int               version;
    private byte              signalled;

    public SyncCondition() {
      super();
      this.version = 0;
      this.signalled = NOT_SIGNALLED;
    }

    public boolean isSignalled() {
      return signalled == SIGNALLED;
    }

    public void setSignalled() {
      signalled = SIGNALLED;
    }

    public void incrementVersionIfSignalled() {
      if (isSignalled()) {
        this.version++;
        resetSignalled();
      }
    }

    public int getVersion() {
      return this.version;
    }

    public boolean hasNotSignalledOnVersion(int targetVersion) {
      return !isSignalled() && (this.version == targetVersion);
    }

    private void resetSignalled() {
      this.signalled = NOT_SIGNALLED;
    }
  }

  private static class ConditionObject implements Condition, java.io.Serializable {
    private transient List      waitingThreads;
    private transient int       numOfWaitingThreards;
    private transient Map       waitOnUnshared;

    private final ReentrantLock originalLock;
    private final SyncCondition realCondition;

    private static long getSystemNanos() {
      return System.nanoTime();
    }

    public ConditionObject(SyncCondition realCondition, ReentrantLock originalLock) {
      this.originalLock = originalLock;
      this.realCondition = realCondition;
      this.waitingThreads = new ArrayList();
      this.numOfWaitingThreards = 0;
      this.waitOnUnshared = new HashMap();
    }

    public ConditionObject() {
      this.originalLock = null;
      this.realCondition = null;
      this.waitingThreads = new ArrayList();
      this.numOfWaitingThreards = 0;
      this.waitOnUnshared = new HashMap();
    }

    private void fullRelease() {
      if (originalLock.getHoldCount() > 0) {
        while (originalLock.getHoldCount() > 0) {
          originalLock.unlock();
        }
      } else {
        // The else part is needed only when the await of the Condition object is executed
        // in an applicator as ManagerUtil.monitorEnter() is short circuited in applicator.
        while (Thread.holdsLock(originalLock)) {
          UnsafeUtil.monitorExit(originalLock);
        }
      }
    }

    private void reacquireLock(int numOfHolds) {
      if (originalLock.getHoldCount() >= numOfHolds) { return; }
      while (originalLock.getHoldCount() < numOfHolds) {
        originalLock.lock();
      }
    }

    private void checkCauseAndThrowInterruptedExceptionIfNecessary(TCRuntimeException e) throws InterruptedException {
      if (e.getCause() instanceof InterruptedException) {
        throw (InterruptedException) e.getCause();
      } else {
        throw e;
      }
    }

    private void addWaitOnUnshared() {
      waitOnUnshared.put(Thread.currentThread(), ManagerUtil.isManaged(realCondition) ? Boolean.FALSE : Boolean.TRUE);
    }

    private boolean isLockRealConditionInUnshared() {
      if (!ManagerUtil.isManaged(realCondition) || !ManagerUtil.isHeldByCurrentThread(realCondition, LockLevel.WRITE)) { return true; }
      return false;
    }

    public void await() throws InterruptedException {
      Thread currentThread = Thread.currentThread();

      if (!originalLock.isHeldByCurrentThread()) { throw new IllegalMonitorStateException(); }
      if (Thread.interrupted()) { throw new InterruptedException(); }

      int numOfHolds = originalLock.getHoldCount();

      realCondition.incrementVersionIfSignalled();
      int version = realCondition.getVersion();
      fullRelease();
      try {
        ManagerUtil.monitorEnter(realCondition, LockLevel.WRITE);
        UnsafeUtil.monitorEnter(realCondition);
        boolean isLockInUnshared = isLockRealConditionInUnshared();
        try {
          if (realCondition.hasNotSignalledOnVersion(version)) {
            waitingThreads.add(currentThread);
            numOfWaitingThreards++;

            addWaitOnUnshared();
            try {
              ManagerUtil.objectWait0(realCondition);
            } finally {
              waitOnUnshared.remove(currentThread);
              waitingThreads.remove(currentThread);
              numOfWaitingThreards--;
            }
          }
        } finally {
          UnsafeUtil.monitorExit(realCondition);
          if (!isLockInUnshared) {
            ManagerUtil.monitorExit(realCondition);
          }
        }
      } catch (TCRuntimeException e) {
        checkCauseAndThrowInterruptedExceptionIfNecessary(e);
      } finally {
        reacquireLock(numOfHolds);
      }
    }

    public void awaitUninterruptibly() {
      Thread currentThread = Thread.currentThread();

      if (!originalLock.isHeldByCurrentThread()) { throw new IllegalMonitorStateException(); }

      int numOfHolds = originalLock.getHoldCount();
      boolean isInterrupted = false;
      realCondition.incrementVersionIfSignalled();
      int version = realCondition.getVersion();
      fullRelease();
      try {
        ManagerUtil.monitorEnter(realCondition, LockLevel.WRITE);
        UnsafeUtil.monitorEnter(realCondition);
        boolean isLockInUnshared = isLockRealConditionInUnshared();
        try {
          if (realCondition.hasNotSignalledOnVersion(version)) {
            while (true) {
              waitingThreads.add(currentThread);
              numOfWaitingThreards++;

              addWaitOnUnshared();
              try {
                ManagerUtil.objectWait0(realCondition);
                break;
              } catch (InterruptedException e) {
                isInterrupted = true;
              } finally {
                waitOnUnshared.remove(currentThread);
                waitingThreads.remove(currentThread);
                numOfWaitingThreards--;
              }
            }
          }
        } finally {
          UnsafeUtil.monitorExit(realCondition);
          if (!isLockInUnshared) {
            ManagerUtil.monitorExit(realCondition);
          }
        }
      } finally {
        reacquireLock(numOfHolds);
      }

      if (isInterrupted) {
        currentThread.interrupt();
      }
    }

    public long awaitNanos(long nanosTimeout) throws InterruptedException {
      Thread currentThread = Thread.currentThread();

      if (!originalLock.isHeldByCurrentThread()) { throw new IllegalMonitorStateException(); }
      if (Thread.interrupted()) { throw new InterruptedException(); }

      int numOfHolds = originalLock.getHoldCount();
      realCondition.incrementVersionIfSignalled();
      int version = realCondition.getVersion();
      fullRelease();
      try {
        ManagerUtil.monitorEnter(realCondition, LockLevel.WRITE);
        UnsafeUtil.monitorEnter(realCondition);
        boolean isLockInUnshared = isLockRealConditionInUnshared();
        try {
          if (realCondition.hasNotSignalledOnVersion(version)) {
            waitingThreads.add(currentThread);
            numOfWaitingThreards++;

            addWaitOnUnshared();
            try {
              long startTime = getSystemNanos();
              TimeUnit.NANOSECONDS.timedWait(realCondition, nanosTimeout);
              long remainingTime = nanosTimeout - (getSystemNanos() - startTime);
              return remainingTime;
            } finally {
              waitOnUnshared.remove(currentThread);
              waitingThreads.remove(currentThread);
              numOfWaitingThreards--;
            }
          } else {
            return nanosTimeout;
          }
        } finally {
          UnsafeUtil.monitorExit(realCondition);
          if (!isLockInUnshared) {
            ManagerUtil.monitorExit(realCondition);
          }
        }
      } catch (TCRuntimeException e) {
        checkCauseAndThrowInterruptedExceptionIfNecessary(e);
        return 0;
      } finally {
        reacquireLock(numOfHolds);
      }
    }

    public boolean await(long time, TimeUnit unit) throws InterruptedException {
      if (unit == null) { throw new NullPointerException(); }
      return awaitNanos(unit.toNanos(time)) > 0;
    }

    public boolean awaitUntil(Date deadline) throws InterruptedException {
      if (deadline == null) { throw new NullPointerException(); }

      long abstime = deadline.getTime();
      if (System.currentTimeMillis() > abstime) { return true; }
      return !await(abstime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    private boolean hasWaitOnUnshared() {
      return waitOnUnshared.values().contains(Boolean.TRUE);
    }

    public void signal() {
      if (!originalLock.isHeldByCurrentThread()) { throw new IllegalMonitorStateException(); }

      ManagerUtil.monitorEnter(realCondition, LockLevel.WRITE);
      UnsafeUtil.monitorEnter(realCondition);
      boolean isLockInUnshared = isLockRealConditionInUnshared();
      try {
        ManagerUtil.objectNotify(realCondition);
        if (hasWaitOnUnshared()) {
          realCondition.notify();
        }
        realCondition.setSignalled();
      } finally {
        UnsafeUtil.monitorExit(realCondition);
        if (!isLockInUnshared) {
          ManagerUtil.monitorExit(realCondition);
        }
      }
    }

    public void signalAll() {
      if (!originalLock.isHeldByCurrentThread()) { throw new IllegalMonitorStateException(); }
      ManagerUtil.monitorEnter(realCondition, LockLevel.WRITE);
      UnsafeUtil.monitorEnter(realCondition);
      boolean isLockInUnshared = isLockRealConditionInUnshared();
      try {
        ManagerUtil.objectNotifyAll(realCondition);
        if (hasWaitOnUnshared()) {
          realCondition.notifyAll();
        }
        realCondition.setSignalled();
      } finally {
        UnsafeUtil.monitorExit(realCondition);
        if (!isLockInUnshared) {
          ManagerUtil.monitorExit(realCondition);
        }
      }
    }

    int getWaitQueueLength(ReentrantLock lock) {
      if (originalLock != lock) throw new IllegalArgumentException("not owner");
      if (!ManagerUtil.isManaged(originalLock)) {
        return numOfWaitingThreards;
      } else {
        return ManagerUtil.waitLength(realCondition);
      }
    }

    Collection getWaitingThreads(ReentrantLock lock) {
      if (originalLock != lock) throw new IllegalArgumentException("not owner");
      return waitingThreads;
    }

    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.waitingThreads = new ArrayList();
      this.numOfWaitingThreards = 0;
      this.waitOnUnshared = new HashMap();
    }

    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
      s.defaultWriteObject();
    }

  }
}
