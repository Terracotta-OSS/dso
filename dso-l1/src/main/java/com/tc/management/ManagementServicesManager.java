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
package com.tc.management;

import com.tc.object.management.RemoteCallDescriptor;
import com.tc.object.management.RemoteCallHolder;
import com.tc.object.management.ServiceID;

import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Local representation of the registered management services
 */
public interface ManagementServicesManager {

  // L2 -> L1

  void registerService(ServiceID serviceID, Object service, ExecutorService executorService);

  void unregisterService(ServiceID serviceID);

  Set<RemoteCallDescriptor> listServices(Set<ServiceID> serviceIDs, boolean includeCallDescriptors);

  void asyncCall(RemoteCallHolder remoteCallHolder, ResponseListener responseListener);


  // L1 -> L2

  void sendEvent(TCManagementEvent event);


  // common

  static interface ResponseListener {
    void onResponse(Object response, Exception exception);
  }

}
