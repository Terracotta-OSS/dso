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
package com.tc.object.handler;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.EventContext;
import com.tc.async.api.EventHandlerException;
import com.tc.management.ManagementServicesManager;
import com.tc.net.protocol.tcm.TCMessageType;
import com.tc.object.management.RemoteCallDescriptor;
import com.tc.object.management.RemoteCallHolder;
import com.tc.object.management.ResponseHolder;
import com.tc.object.management.ServiceID;
import com.tc.object.management.TCManagementSerializationException;
import com.tc.object.msg.InvokeRegisteredServiceMessage;
import com.tc.object.msg.InvokeRegisteredServiceResponseMessage;
import com.tc.object.msg.ListRegisteredServicesMessage;
import com.tc.object.msg.ListRegisteredServicesResponseMessage;
import com.tc.util.Assert;

import java.util.Set;

/**
 *
 */
public class ClientManagementHandler extends AbstractEventHandler {
  private final ManagementServicesManager managementServicesManager;

  public ClientManagementHandler(ManagementServicesManager managementServicesManager) {
    this.managementServicesManager = managementServicesManager;
  }

  @Override
  public void handleEvent(EventContext context) throws EventHandlerException {
    if (context instanceof InvokeRegisteredServiceMessage) {
      final InvokeRegisteredServiceMessage request = (InvokeRegisteredServiceMessage)context;
      RemoteCallHolder remoteCallHolder = request.getRemoteCallHolder();

      managementServicesManager.asyncCall(remoteCallHolder, new ManagementServicesManager.ResponseListener() {
        @Override
        public void onResponse(Object responseObject, Exception exception) {
          ResponseHolder responseHolder = new ResponseHolder();
          try {
            responseHolder.setResponse(responseObject);
            responseHolder.setException(exception);
          } catch (TCManagementSerializationException se) {
            responseHolder.setException(se);
          }

          InvokeRegisteredServiceResponseMessage response = (InvokeRegisteredServiceResponseMessage)request.getChannel()
              .createMessage(TCMessageType.INVOKE_REGISTERED_SERVICE_RESPONSE_MESSAGE);
          response.setResponseHolder(responseHolder);
          response.setManagementRequestID(request.getManagementRequestID());
          response.send();
        }
      });
    } else if (context instanceof ListRegisteredServicesMessage) {
      ListRegisteredServicesMessage request = (ListRegisteredServicesMessage)context;

      Set<ServiceID> serviceIDs = request.getServiceIDs();
      boolean includeCallDescriptors = request.isIncludeCallDescriptors();

      Set<RemoteCallDescriptor> descriptors = managementServicesManager.listServices(serviceIDs, includeCallDescriptors);

      ListRegisteredServicesResponseMessage response = (ListRegisteredServicesResponseMessage)request.getChannel().createMessage(TCMessageType.LIST_REGISTERED_SERVICES_RESPONSE_MESSAGE);
      response.setRemoteCallDescriptors(descriptors);
      response.setManagementRequestID(request.getManagementRequestID());
      response.send();
    } else {
      Assert.fail("Unknown event type " + context.getClass().getName());
    }
  }

}
