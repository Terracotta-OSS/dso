/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 */
package com.terracotta.toolkit.api;

import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.ToolkitInstantiationException;
import org.terracotta.toolkit.api.ToolkitFactoryService;

import com.terracotta.toolkit.client.TerracottaClientConfig;
import com.terracotta.toolkit.client.TerracottaClientConfigParams;
import com.terracotta.toolkit.client.TerracottaToolkitCreator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class TerracottaToolkitFactoryService implements ToolkitFactoryService {

  private static final char       COMMA                       = ',';
  private final static String     TERRACOTTA_TOOLKIT_TYPE     = "terracotta";
  private static final String     TUNNELLED_MBEAN_DOMAINS_KEY = "tunnelledMBeanDomains";
  private static final String     TC_CONFIG_SNIPPET_KEY       = "tcConfigSnippet";
  private static final String     REJOIN_KEY                  = "rejoin";
  private static final Properties EMPTY_PROPERTIES            = new Properties();

  @Override
  public boolean canHandleToolkitType(String type, String subName) {
    return TERRACOTTA_TOOLKIT_TYPE.equals(type);
  }

  @Override
  public Toolkit createToolkit(String type, String subName, Properties properties) throws ToolkitInstantiationException {
    if (!canHandleToolkitType(type, subName)) {
      //
      throw new ToolkitInstantiationException("Cannot handle toolkit of type: " + type + ", subName: " + subName);
    }
    properties = properties == null ? EMPTY_PROPERTIES : properties;
    TerracottaClientConfig config = createTerracottaClientConfig(type, subName, properties);
    try {
      return createToolkit(config);
    } catch (Throwable t) {
      throw new ToolkitInstantiationException("There were some problem in creating toolkit", t);
    }
  }

  /**
   * Overridden by enterprise to create enterprise toolkit
   */
  protected Toolkit createToolkit(TerracottaClientConfig config) {
    return new TerracottaToolkitCreator(config, false).createToolkit();
  }

  private TerracottaClientConfig createTerracottaClientConfig(String type, String subName, Properties properties)
      throws ToolkitInstantiationException {
    String tcConfigSnippet = properties.getProperty(TC_CONFIG_SNIPPET_KEY, "");
    final String terracottaUrlOrConfig;
    final boolean isUrl;
    if (tcConfigSnippet == null || tcConfigSnippet.trim().equals("")) {
      // if no tcConfigSnippet, assume url
      terracottaUrlOrConfig = getTerracottaUrlFromSubName(subName);
      isUrl = true;
    } else {
      terracottaUrlOrConfig = tcConfigSnippet;
      isUrl = false;
    }
    Set<String> tunnelledMBeanDomains = getTunnelledMBeanDomains(properties);
    return new TerracottaClientConfigParams().tcConfigSnippetOrUrl(terracottaUrlOrConfig).isUrl(isUrl)
        .tunnelledMBeanDomains(tunnelledMBeanDomains).rejoin(isRejoinEnabled(properties)).newTerracottaClientConfig();
  }

  private boolean isRejoinEnabled(Properties properties) {
    if (properties == null || properties.size() == 0) { return false; }
    String rejoin = properties.getProperty(REJOIN_KEY);
    return "true".equals(rejoin);
  }

  private String getTerracottaUrlFromSubName(String subName) throws ToolkitInstantiationException {
    // toolkitUrl is of form: 'toolkit:terracotta://server:port'
    if (subName == null || !subName.startsWith("//")) {
      //
      throw new ToolkitInstantiationException(
                                              "'subName' in toolkitUrl for toolkit type 'terracotta' should start with '//', "
                                                  + "and should be of form: 'toolkit:terracotta://server:port' - "
                                                  + subName);
    }
    String terracottaUrl = subName.substring(2);
    // terracottaUrl can only be form of server:port, or a csv of same
    if (terracottaUrl == null || terracottaUrl.equals("")) {
      //
      throw new ToolkitInstantiationException(
                                              "toolkitUrl should be of form: 'toolkit:terracotta://server:port', server:port not specified after 'toolkit:terracotta://' in toolkitUrl - "
                                                  + subName);
    }
    // ignore last comma, if any
    terracottaUrl = terracottaUrl.trim();
    if (terracottaUrl.charAt(terracottaUrl.length() - 1) == COMMA) {
      terracottaUrl = terracottaUrl.substring(0, terracottaUrl.length() - 2);
    }
    String[] serverPortTokens = terracottaUrl.split(",");
    for (String serverPortToken : serverPortTokens) {
      String[] tokens = serverPortToken.split(":");
      if (tokens.length != 2) {
        //
        throw new ToolkitInstantiationException(
                                                "toolkitUrl should be of form: 'toolkit:terracotta://server:port', invalid server:port specified - '"
                                                    + serverPortToken + "'");
      }
      if (!isValidInteger(tokens[1])) {
        //
        throw new ToolkitInstantiationException(
                                                "toolkitUrl should be of form: 'toolkit:terracotta://server:port', invalid server:port specified in token - '"
                                                    + serverPortToken + "', 'port' is not a valid integer - "
                                                    + tokens[1]);
      }
    }
    return terracottaUrl;
  }

  private boolean isValidInteger(String value) {
    try {
      Integer.parseInt(value);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private static Set<String> getTunnelledMBeanDomains(Properties properties) {
    if (properties == null || properties.size() == 0) { return Collections.EMPTY_SET; }
    String domainsCSV = properties.getProperty(TUNNELLED_MBEAN_DOMAINS_KEY);
    if (domainsCSV == null || domainsCSV.equals("")) { return Collections.EMPTY_SET; }
    return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(domainsCSV.split(","))));
  }

}
