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

import org.terracotta.management.cli.Command;
import org.terracotta.management.cli.CommandInvocationException;

/**
 * @author Ludovic Orban
 */
class HelpCommand implements Command<Context> {

  @Override
  public void execute(final Context context) throws CommandInvocationException {
    System.out.println("Usage : ");
    System.out.println("rest-client <flags> <url> <POST data> <json query 1> <json query 2> ... <json query n>");
    for (CliCommand cliCommand : CliCommand.values()) {
      if (!cliCommand.isHidden() && (cliCommand.getCommand()!=null)) {
        System.out.println("  -" + cliCommand.getSwitchChar() + " " + cliCommand.getCommand().helpMessage());
      }
    }
    String exampleUsage = "Example usages:\n" +
                          "  rest-client -e -g http://localhost:9540/tc-management-api/v2/agents ";


    System.out.println(exampleUsage);
  }

  @Override
  public String helpMessage() {
    return "Prints this help message";
  }
}
