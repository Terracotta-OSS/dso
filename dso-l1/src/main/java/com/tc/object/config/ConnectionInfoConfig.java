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
package com.tc.object.config;

import org.apache.commons.lang3.StringUtils;

import com.tc.config.schema.L2ConfigForL1.L2Data;
import com.tc.logging.CustomerLogging;
import com.tc.logging.TCLogger;
import com.tc.net.core.ConnectionInfo;
import com.tc.net.core.SecurityInfo;
import com.tc.util.stringification.OurStringBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Returns a {@link ConnectionInfo} array from the L2 data.
 */
public class ConnectionInfoConfig {
  static TCLogger consoleLogger = CustomerLogging.getConsoleLogger();
  private final ConnectionInfo[] connectionInfos;

  public ConnectionInfoConfig(L2Data[] l2sData) {
    this(l2sData, new SecurityInfo());
  }

  public ConnectionInfoConfig(L2Data[] l2sData, SecurityInfo securityInfo) {
    this.connectionInfos = createValueFrom(l2sData, securityInfo);
  }

  private ConnectionInfo[] createValueFrom(L2Data[] l2sData, final SecurityInfo securityInfo) {
    ConnectionInfo[] out;

    String serversProperty = System.getProperty("tc.server");
    if (serversProperty != null && (serversProperty = serversProperty.trim()) != null && serversProperty.length() > 0) {
      consoleLogger.info("tc.server: " + serversProperty);

      String[] serverDescs = StringUtils.split(serversProperty, ",");
      int count = serverDescs.length;

      out = new ConnectionInfo[count];
      for (int i = 0; i < count; i++) {
        String[] serverDesc = StringUtils.split(serverDescs[i], ":");
        String host = serverDesc.length > 0 ? serverDesc[0] : "localhost";
        int tsaPort = 9510;

        if (serverDesc.length == 2) {
          try {
            tsaPort = Integer.parseInt(serverDesc[1]);
          } catch (NumberFormatException nfe) {
            consoleLogger.warn("Cannot parse port for tc.server element '" + serverDescs[i]
                               + "'; Using default of 9510.");
          }
        }

        boolean secure = false;
        String urlUsername = null;
        int userSeparatorIndex = host.indexOf('@');
        if (userSeparatorIndex > -1) {
          secure = true;
          urlUsername = host.substring(0, userSeparatorIndex);
          try {
            urlUsername = URLDecoder.decode(urlUsername, "UTF-8");
          } catch (UnsupportedEncodingException uee) {
            // cannot happen
          }
          host = host.substring(userSeparatorIndex + 1);
        }

        out[i] = new ConnectionInfo(host, tsaPort, new SecurityInfo(secure, urlUsername));
      }
    } else {
      out = new ConnectionInfo[l2sData.length];

      for (int i = 0; i < out.length; ++i) {
        out[i] = new ConnectionInfo(l2sData[i].host(), l2sData[i].tsaPort(), l2sData[i].getGroupId(),
                                    l2sData[i].getGroupName(), securityInfo);
      }
    }

    return out;
  }

  public ConnectionInfo[] getConnectionInfos(){
    return this.connectionInfos;
  }

  @Override
  public String toString() {
    StringBuilder l2sDataString = new StringBuilder();
    for (ConnectionInfo connectionInfo : this.connectionInfos) {
      l2sDataString.append(connectionInfo.toString());
    }
    return new OurStringBuilder(this, OurStringBuilder.COMPACT_STYLE).appendSuper(l2sDataString.toString()).toString();
  }
}
