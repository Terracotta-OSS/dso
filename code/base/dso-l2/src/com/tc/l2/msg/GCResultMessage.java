/*
 * All content copyright (c) 2003-2007 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.l2.msg;

import com.tc.async.api.EventContext;
import com.tc.net.groups.AbstractGroupMessage;
import com.tc.util.Assert;
import com.tc.util.ObjectIDSet2;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Set;

public class GCResultMessage extends AbstractGroupMessage implements EventContext {
  public static final int GC_RESULT = 0;
  private Set             gcedOids;

  // To make serialization happy
  public GCResultMessage() {
    super(-1);
  }

  public GCResultMessage(int type, Set deleted) {
    super(type);
    this.gcedOids = deleted;
  }

  protected void basicReadExternal(int msgType, ObjectInput in) throws IOException, ClassNotFoundException {
    Assert.assertEquals(GC_RESULT, msgType);
    // Instead of in.readObject(), using externalizable api for DNADecoding
    gcedOids = new ObjectIDSet2();
    ((ObjectIDSet2)gcedOids).readExternal(in);
  }

  protected void basicWriteExternal(int msgType, ObjectOutput out) throws IOException {
    Assert.assertEquals(GC_RESULT, msgType);
    // XXX::Directly serializing instead of using writeObjectIDs() to avoid HUGE messages. Since the (wrapped) set is
    // ObjectIDSet2 and since it has optimized externalization methods, this should result in far less data written out.
    // out.writeObject(gcedOids);
    // Instead of out.writeObject, using externalizable api for DNAEncoding
    ((ObjectIDSet2)gcedOids).writeExternal(out);
  }

  public Set getGCedObjectIDs() {
    return gcedOids;
  }

  public String toString() {
    return "GCResultMessage@" + System.identityHashCode(this) + " : GC Result size = "
           + (gcedOids == null ? "null" : "" + gcedOids.size());
  }

}
