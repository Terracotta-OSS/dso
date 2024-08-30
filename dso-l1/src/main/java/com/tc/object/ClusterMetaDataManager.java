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
package com.tc.object;

import com.tc.net.NodeID;
import com.tc.object.bytecode.TCMap;
import com.tc.object.bytecode.TCServerMap;
import com.tc.object.dna.api.DNAEncoding;
import com.tc.object.handshakemanager.ClientHandshakeCallback;
import com.tc.object.locks.ThreadID;
import com.tcclient.cluster.DsoNodeInternal;
import com.tcclient.cluster.DsoNodeMetaData;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ClusterMetaDataManager extends ClientHandshakeCallback {

  public DNAEncoding getEncoding();

  public Set<NodeID> getNodesWithObject(ObjectID id);

  public Map<ObjectID, Set<NodeID>> getNodesWithObjects(Collection<ObjectID> ids);

  public Set<?> getKeysForOrphanedValues(TCMap tcMap);

  public DsoNodeMetaData retrieveMetaDataForDsoNode(DsoNodeInternal node);

  public void setResponse(ThreadID threadId, Object response);

  public <K> Map<K, Set<NodeID>> getNodesWithKeys(TCMap tcMap, Collection<? extends K> keys);

  public <K> Map<K, Set<NodeID>> getNodesWithKeys(TCServerMap tcMap, Collection<? extends K> keys);
}