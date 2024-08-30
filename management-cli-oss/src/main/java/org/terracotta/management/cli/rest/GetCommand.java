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

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.terracotta.management.cli.CommandInvocationException;

/**
 * @author Ludovic Orban
 */
class GetCommand extends BaseOssHttpCommand {

  @Override
  public void doExecute(Context context) throws Exception {
    doGet(context);
  }

  private void doGet(Context context) throws IOException, CommandInvocationException {
    HttpGet httpGet = new HttpGet(context.getUrl());

    HttpResponse response = httpclient.execute(httpGet);
   
    processEntity(response.getEntity(), response.getFirstHeader("Content-Type"), context);
  }

  @Override
  public String helpMessage() {
    return "Perform HTTP GET";
  }
}
