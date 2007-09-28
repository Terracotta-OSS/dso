/*
 * All content copyright (c) 2003-2007 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.net.groups;

import com.tc.io.TCByteBufferInput;
import com.tc.io.TCByteBufferOutput;
import com.tc.io.TCSerializable;
import com.tc.io.serializer.TCObjectInputStream;
import com.tc.io.serializer.TCObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This is a helper class to hide the serialization and deserialization of NodeID implementations from external world.
 * Having it here makes it easy to abstract the differences from everywhere else. The downside is that when a new
 * implementation comes around this class needs to be updated.
 */
public class NodeIDSerializer implements TCSerializable {

  private static final byte CLIENT_ID    = 0x01;
  private static final byte NODE_ID_IMPL = 0x02;

  private NodeID            nodeID;

  public NodeIDSerializer() {
    // NOP
  }

  public NodeIDSerializer(NodeID nodeID) {
    this.nodeID = nodeID;
  }

  public NodeID getNodeID() {
    return nodeID;
  }

  public static void writeNodeID(NodeID n, ObjectOutput out) throws IOException {
    byte type = getType(n);
    out.writeByte(type);
    n.writeExternal(out);
  }

  private static byte getType(NodeID nodeID) {
    if (nodeID instanceof ClientID) {
      return CLIENT_ID;
    } else if (nodeID instanceof NodeIDImpl) {
      return NODE_ID_IMPL;
    } else {
      throw new AssertionError("Unknown type : " + nodeID.getClass().getName() + " : " + nodeID);
    }
  }

  public static NodeID readNodeID(ObjectInput in) throws IOException, ClassNotFoundException {
    byte type = in.readByte();
    NodeID n = getImpl(type);
    n.readExternal(in);
    return n;
  }

  private static NodeID getImpl(byte type) {
    switch (type) {
      case CLIENT_ID:
        return new ClientID();
      case NODE_ID_IMPL:
        return new NodeIDImpl();
      default:
        throw new AssertionError("Unknown type : " + type);
    }
  }

  // XXX:: These are not very efficient ways to serialize and deserialize NodeIDs, this is here coz it is used by two
  // different stack implementation
  public byte[] getBytes(NodeID n) {
    try {
      ByteArrayOutputStream bao = new ByteArrayOutputStream(64);
      // XXX::NOTE:: We are using TCObjectOutputStream which can only serialize known types. @see writeObject()
      TCObjectOutputStream tos = new TCObjectOutputStream(bao);
      writeNodeID(n, tos);
      tos.close();
      return bao.toByteArray();
    } catch (IOException ioe) {
      throw new AssertionError(ioe);
    }
  }

  // XXX:: These are not very efficient ways to serialize and deserialize NodeIDs, this is here coz it is used by two
  // different stack implementation
  public NodeID createFrom(byte[] data) {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      TCObjectInputStream tci = new TCObjectInputStream(bais);
      return readNodeID(tci);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  public Object deserializeFrom(TCByteBufferInput serialInput) throws IOException {
    byte type = serialInput.readByte();
    this.nodeID = getImpl(type);
    this.nodeID.deserializeFrom(serialInput);
    return this;
  }

  public void serializeTo(TCByteBufferOutput serialOutput) {
    serialOutput.writeByte(getType(this.nodeID));
    this.nodeID.serializeTo(serialOutput);
  }

}
