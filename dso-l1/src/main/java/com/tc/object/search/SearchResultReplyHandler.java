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
package com.tc.object.search;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.EventContext;
import com.tc.object.msg.SearchResponseMessage;

public class SearchResultReplyHandler extends AbstractEventHandler {

  private final SearchResultManager searchResultMgr;

  public SearchResultReplyHandler(final SearchResultManager resultManager) {
    this.searchResultMgr = resultManager;
  }

  @Override
  public void handleEvent(final EventContext context) {
    if (context instanceof SearchResponseMessage) {
      final SearchResponseMessage responseMsg = (SearchResponseMessage) context;

      if (responseMsg.isError()) {
        this.searchResultMgr.addErrorResponse(responseMsg.getLocalSessionID(), responseMsg.getRequestID(),
                                              responseMsg.getGroupIDFrom(),
                                              responseMsg.getErrorMessage(), responseMsg.getSourceNodeID());
      } else {
        this.searchResultMgr.addResponse(responseMsg.getLocalSessionID(), responseMsg.getRequestID(),
                                         responseMsg.getGroupIDFrom(),
                                         responseMsg.getResults(), responseMsg.getSourceNodeID());
      }
    } else {
      throw new AssertionError("Unknown message type received from server - " + context.getClass().getName());
    }
  }

}
