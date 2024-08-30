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
package org.terracotta.management.cli.rest;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.terracotta.management.cli.Command;
import org.terracotta.management.cli.CommandInvocationException;
import org.terracotta.management.cli.DefaultCli;
import org.terracotta.management.cli.UserAbortingException;

/**
 * @author Ludovic Orban
 */
public class RestCli extends DefaultCli<CliCommand, Context> {

  protected RestCli(final String... args) {
    super(args);
  }

  public static void main(String[] args) throws CommandInvocationException {
    try {
      new RestCli(args).defaultExecution();
    } catch (IllegalArgumentException e) {
      System.exit(255);
    } catch (IllegalStateException e) {
      if(e.getCause() instanceof UserAbortingException){
        System.out.println("Aborting!");
      }
      System.exit(255);
    }
  }

  @Override
  protected Command<Context> getDefaultCommand() {
    return CliCommand.HELP.getCommand();
  }

  @Override
  protected boolean vetoExecution() throws CommandInvocationException {
    if (cliCommands.contains(CliCommand.HELP)) {
      CliCommand.HELP.getCommand().execute(context);
      return true;
    }
    if (cliCommands.contains(CliCommand.GET) && cliCommands.contains(CliCommand.POST)) {
      CliCommand.HELP.getCommand().execute(context);
      return true;
    }
    return false;
  }

  @Override
  protected void preExecution() throws CommandInvocationException {
    try {
      HttpServices.initHttpClient();

      if (cliCommands.contains(CliCommand.ENCODE)) {
        DisplayServices.setPerformUrlEncoding(true);
      }
      if (cliCommands.contains(CliCommand.IGNORE_SSL_ERRORS)) {
          Command<Context> command = CliCommand.IGNORE_SSL_ERRORS.getCommand();
          command.execute(null);
        }

    } catch (Exception e) {
      throw new CommandInvocationException("Error initializing HTTP client", e);
    }
  }

  @Override
  protected void postExecution() throws CommandInvocationException {
    try {
      HttpServices.disposeOfHttpClient();
    } catch (Exception e) {
      throw new CommandInvocationException("Error disposing of HTTP client", e);
    }
  }

  @Override
  protected Context parseContext(String[] args, Set<CliCommand> cliCommands) {
    String url = null;
    List<String> jsonQueries = new ArrayList<String>();
    String data = null;
    String username = null;
    String password = null;

    int position = 0;
    for (String arg : args) {
      if(!isSwitch(arg)) {
        switch(position++) {
          case 0:
            url = arg;
            break;
          case 1:
            data = arg;
            break;
          case 2:
            username = arg;
            break;
          case 3:
            password = arg;
            break;
          default:
            jsonQueries.add(arg);
        }
      }
    }

    return new Context(url, jsonQueries, data, username, password, cliCommands.contains(CliCommand.FAIL_ON_EMPTY));
  }

  @Override
  protected EnumSet<CliCommand> onErrorCommands() {
    return EnumSet.of(CliCommand.HELP);
  }

}




