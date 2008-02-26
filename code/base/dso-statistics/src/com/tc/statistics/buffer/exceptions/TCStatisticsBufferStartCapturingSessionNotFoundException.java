/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright notice.  All rights reserved.
 */
package com.tc.statistics.buffer.exceptions;

public class TCStatisticsBufferStartCapturingSessionNotFoundException extends TCStatisticsBufferException {
  private final String sessionId;

  public TCStatisticsBufferStartCapturingSessionNotFoundException(final String sessionId) {
    super("The capture session with the cluster-wide ID '" + sessionId + "' could not be started since the session couldn't be found..", null);
    this.sessionId = sessionId;
  }

  public String getSessionId() {
    return sessionId;
  }
}