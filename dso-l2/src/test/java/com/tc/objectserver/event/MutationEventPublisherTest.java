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
package com.tc.objectserver.event;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.google.common.collect.Sets;
import com.tc.net.ClientID;
import com.tc.object.ObjectID;
import com.tc.object.gtx.GlobalTransactionID;
import com.tc.objectserver.managedobject.CDSMValue;
import com.tc.server.BasicServerEvent;
import com.tc.server.CustomLifespanVersionedServerEvent;
import com.tc.server.ServerEventType;

import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * @author Eugene Shelestovich
 */
public class MutationEventPublisherTest {
  @Rule public MockitoRule mockito = MockitoJUnit.rule();

  private static final String CACHE_NAME = "cache";
  private static final byte[] VALUE = new byte[] { 1 };
  private static final ObjectID OID = new ObjectID(1);

  private MutationEventPublisher publisher;
  private GlobalTransactionID    gtxId;
  private Set<ClientID>          clientIds;
  @Mock private ServerEventBuffer serverEventBuffer;

  @Before
  public void setUp() throws Exception {
    gtxId = new GlobalTransactionID(1);
    publisher = new DefaultMutationEventPublisher(gtxId, serverEventBuffer);
    clientIds = Sets.newHashSet();
    clientIds.add(new ClientID(1));
    clientIds.add(new ClientID(2));
  }

  @Test
  public void testNoPublishWithoutValue() throws Exception {
    publisher.publishEvent(clientIds, ServerEventType.PUT, 1, new CDSMValue(OID), CACHE_NAME);
    verifyNoInteractions(serverEventBuffer);
  }

  @Test
  public void testPublishWhenBytesComeSecond() throws Exception {
    publisher.publishEvent(clientIds, ServerEventType.PUT, 1, new CDSMValue(OID, 1, 2, 3, 4, 5), CACHE_NAME);
    publisher.setBytesForObjectID(OID, VALUE);
    verify(serverEventBuffer).storeEvent(gtxId, new CustomLifespanVersionedServerEvent(
              new BasicServerEvent(ServerEventType.PUT, 1, VALUE, 5, CACHE_NAME), 1, 3, 4), clientIds);
  }

  @Test
  public void testPublishWhenBytesComeFirst() throws Exception {
    publisher.setBytesForObjectID(OID, VALUE);
    publisher.publishEvent(clientIds, ServerEventType.PUT, 1, new CDSMValue(OID, 1, 2, 3, 4, 5), CACHE_NAME);
    verify(serverEventBuffer).storeEvent(gtxId, new CustomLifespanVersionedServerEvent(
              new BasicServerEvent(ServerEventType.PUT, 1, VALUE, 5, CACHE_NAME), 1, 3, 4), clientIds);
  }

  @Test
  public void testPublishWhenNoValue() throws Exception {
    publisher.publishEvent(clientIds, ServerEventType.REMOVE, "foo", new CDSMValue(ObjectID.NULL_ID), CACHE_NAME);
    verify(serverEventBuffer).storeEvent(gtxId, new CustomLifespanVersionedServerEvent
              (new BasicServerEvent(ServerEventType.REMOVE, "foo", new byte[0], 0, CACHE_NAME), 0, 0, 0), clientIds);
  }

  @Test
  public void testMultipleEventsOneObjectID() throws Exception {
    publisher.publishEvent(clientIds, ServerEventType.PUT, 1, new CDSMValue(OID, 1, 2, 3, 4, 5), CACHE_NAME);
    publisher.setBytesForObjectID(OID, VALUE);
    verify(serverEventBuffer).storeEvent(gtxId, new CustomLifespanVersionedServerEvent(
              new BasicServerEvent(ServerEventType.PUT, 1, VALUE, 5, CACHE_NAME), 1, 3, 4), clientIds);
    publisher.publishEvent(clientIds, ServerEventType.PUT_LOCAL, 1, new CDSMValue(OID, 5, 5, 3, 2, 1), CACHE_NAME);
    verify(serverEventBuffer).storeEvent(gtxId, new CustomLifespanVersionedServerEvent(
              new BasicServerEvent(ServerEventType.PUT_LOCAL, 1, VALUE, 1, CACHE_NAME), 5, 3, 2), clientIds);
  }
}
