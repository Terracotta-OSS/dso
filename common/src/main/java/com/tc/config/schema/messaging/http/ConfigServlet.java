/* 
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at 
 *
 *      http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Platform.
 *
 * The Initial Developer of the Covered Software is 
 *      Terracotta, Inc., a Software AG company
 */
package com.tc.config.schema.messaging.http;

import org.apache.commons.io.IOUtils;

import com.tc.config.schema.setup.L2ConfigurationSetupManager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfigServlet extends HttpServlet {

  public static final String                   CONFIG_ATTRIBUTE = ConfigServlet.class.getName() + ".config";

  private volatile L2ConfigurationSetupManager configSetupManager;

  @Override
  public void init() {
    configSetupManager = (L2ConfigurationSetupManager) getServletContext().getAttribute(CONFIG_ATTRIBUTE);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map params = request.getParameterMap();

    if (params.size() == 0) {
      OutputStream out = response.getOutputStream();
      int bytesCopied = IOUtils.copy(this.configSetupManager.effectiveConfigFile(), out);
      response.setContentLength(bytesCopied);
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      PrintWriter writer = response.getWriter();
      writer.println("request not understood");
    }

    response.flushBuffer();
  }
}
