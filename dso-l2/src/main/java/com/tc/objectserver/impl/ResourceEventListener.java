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
package com.tc.objectserver.impl;

import org.terracotta.corestorage.monitoring.MonitoredResource;

/**
 *
 * @author mscott
 */
public interface ResourceEventListener {
    void resourcesConstrained(MonitoredResource usage);
    void resourcesFreed(MonitoredResource usage);
    void resourcesUsed(MonitoredResource usage);
    void requestEvictions(MonitoredResource usage);
    void cancelEvictions(MonitoredResource usage);
    void requestThrottle(MonitoredResource usage);
    void cancelThrottle(MonitoredResource usage);
    void requestStop(MonitoredResource usage);
    void cancelStop(MonitoredResource usage);
}
