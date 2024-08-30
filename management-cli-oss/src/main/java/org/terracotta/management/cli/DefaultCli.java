/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
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
package org.terracotta.management.cli;

import java.lang.reflect.ParameterizedType;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Alex Snaps
 */
public abstract class DefaultCli<E extends Enum<E> & CommandProvider<T>, T> {

  private final Class<E> enumClazz;

  protected Set<E> cliCommands;
  protected T context;

  @SuppressWarnings("unchecked")
  protected DefaultCli(final String... args) {
    enumClazz = (Class<E>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    try {
      cliCommands = parseCommands(args);
      context = parseContext(args, cliCommands);
    } catch (Exception e) {
      for (E command : onErrorCommands()) {
        try {
          System.err.println(e);
          command.getCommand().execute(null);
        } catch (CommandInvocationException cie) {
          System.err.println("Error invoking " + command.getCommand().getClass().getSimpleName());
        }
      }
      throw new IllegalArgumentException("Couldn't build the required context", e);
    }
  }


  protected static boolean isSwitch(final String arg) {
    return arg.length() > 1 && arg.charAt(0) == '-';
  }

  protected void defaultExecution() {
    try {
      if (vetoExecution())
        return;
      preExecution();
      if(cliCommands.size() == 0) {
        if (cliCommands.size() == 0) {
          getDefaultCommand().execute(context);
        }
      } else {
        for (E cliCommand : cliCommands) {
          cliCommand.getCommand().execute(context);
        }
      }
      postExecution();
      return;
    } catch (CommandInvocationException e) {
      System.err.println("\n" + e.errorMessage());
      System.err.println("See help for more information (-h)");
    }
    throw new IllegalStateException();
  }

  protected abstract Command<T> getDefaultCommand();

  protected abstract boolean vetoExecution() throws CommandInvocationException;

  protected abstract void preExecution() throws CommandInvocationException;

  protected abstract void postExecution() throws CommandInvocationException;

  protected abstract T parseContext(final String[] args, Set<E> cliCommands);

  protected EnumSet<E> parseCommands(final String[] args) {
    final EnumSet<E> cliCommands = EnumSet.noneOf(enumClazz);
    for (String arg : args) {
      if (isSwitch(arg)) {
        for (int i = 1; i < arg.length(); i++) {
          boolean found = false;
          for (E value : EnumSet.allOf(enumClazz)) {
            if(value.getSwitchChar() == arg.charAt(i)) {
              cliCommands.add(value);
              found = true;
              break;
            }
          }
          if(!found) {
            throw new IllegalArgumentException("No switch for " + arg.charAt(i));
          }
        }
      }
    }
    return cliCommands;
  }

  protected abstract EnumSet<E> onErrorCommands();
}
