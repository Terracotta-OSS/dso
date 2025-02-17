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
package com.terracotta.toolkit.object.serialization;

import com.tc.abortable.AbortedOperationException;
import com.tc.exception.TCRuntimeException;
import com.tc.logging.TCLogger;
import com.tc.object.ClientObjectManager;
import com.tc.object.LogicalOperation;
import com.tc.object.ObjectID;
import com.tc.object.TCObject;
import com.tc.object.TraversedReferences;
import com.tc.object.applicator.BaseApplicator;
import com.tc.object.dna.api.DNA;
import com.tc.object.dna.api.DNACursor;
import com.tc.object.dna.api.DNAEncoding;
import com.tc.object.dna.api.DNAWriter;
import com.tc.object.dna.api.LogicalAction;
import com.tc.platform.PlatformService;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

public class SerializerMapImplApplicator extends BaseApplicator {

  public SerializerMapImplApplicator(DNAEncoding encoding, TCLogger logger) {
    super(encoding, logger);
  }

  @Override
  public void hydrate(ClientObjectManager objectManager, TCObject tcObject, DNA dna, Object pojo) throws IOException,
      ClassNotFoundException {
    final DNACursor cursor = dna.getCursor();

    while (cursor.next(this.encoding)) {
      final LogicalAction action = cursor.getLogicalAction();
      final LogicalOperation method = action.getLogicalOperation();
      final Object[] params = action.getParameters();
      apply(objectManager, pojo, method, params);
    }
  }

  private void apply(final ClientObjectManager objectManager, final Object po, final LogicalOperation method, final Object[] params)
      throws ClassNotFoundException {
    final SerializerMapImpl m = (SerializerMapImpl) po;
    switch (method) {
      case PUT:
        Object k = params[0];
        Object v = params[1];
        if (v instanceof ObjectID) {
          try {
            v = objectManager.lookupObject((ObjectID) v);
          } catch (AbortedOperationException e) {
            throw new TCRuntimeException(e);
          }
        }
        m.internalput(k, v);
        break;
      default:
        throw new AssertionError("invalid action:" + method);
    }
  }

  @Override
  public void dehydrate(ClientObjectManager objectManager, TCObject tcObject, DNAWriter writer, Object pojo) {
    SerializerMapImpl m = (SerializerMapImpl) pojo;
    Map<String, Object> serMap = m.internalGetMap();
    for (Entry<String, Object> entry : serMap.entrySet()) {
      final Object addValue = getDehydratableObject(entry.getValue(), objectManager);
      if (addValue == null) {
        continue;
      }
      writer.addLogicalAction(LogicalOperation.PUT, new Object[] { entry.getKey(), addValue });
    }
  }

  @Override
  public TraversedReferences getPortableObjects(Object pojo, TraversedReferences addTo) {
    // No other instances
    return addTo;
  }

  @Override
  public Object getNewInstance(ClientObjectManager objectManager, DNA dna, PlatformService platformService) {
    return new SerializerMapImpl(platformService);
  }

}
