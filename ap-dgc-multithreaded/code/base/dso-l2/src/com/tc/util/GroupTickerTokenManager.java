/*
 * All content copyright (c) 2003-2009 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.util;

import com.tc.net.GroupID;
import com.tc.net.OrderedGroupIDs;
import com.tc.net.groups.GroupException;
import com.tc.net.groups.GroupManager;
import com.tc.util.Assert;
import com.tc.util.TickerTokenManager;
import com.tc.util.msg.TickerTokenMessage;

public class GroupTickerTokenManager extends TickerTokenManager {

  private final GroupManager          groupManager;
  private final OrderedGroupIDs orderedGroupIDs;

  public GroupTickerTokenManager(GroupID id, int timerPeriod,
                                 OrderedGroupIDs orderedGroupIDs, GroupManager activeServerGroupManager) {
    super(id.toInt(), timerPeriod, orderedGroupIDs.length());
    this.orderedGroupIDs = orderedGroupIDs;
    this.groupManager = activeServerGroupManager;
    Assert.assertTrue(orderedGroupIDs.length() > 0);
  }

  @Override
  public void sendMessage(TickerTokenMessage message) {
    try {
      groupManager.sendTo(getNext(getId()), message);
    } catch (GroupException e) {
      throw new AssertionError(e);
    }
  }

  public GroupID getNext(int id) {
    GroupID nextGroupID = null;
    for (int i = 0; i < orderedGroupIDs.length(); i++) {
      GroupID groupID = orderedGroupIDs.getGroup(i);
      if (groupID.toInt() == id) {
        if ((i + 1) < orderedGroupIDs.length()) {
          i += 1;
          nextGroupID = orderedGroupIDs.getGroup(i);
        }
        break;
      }
    }
    if (nextGroupID == null) {
      nextGroupID = orderedGroupIDs.getGroup(0);
    }
    return nextGroupID;
  }

}
