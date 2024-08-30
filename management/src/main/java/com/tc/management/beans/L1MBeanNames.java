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
package com.tc.management.beans;

import com.tc.management.TerracottaManagement;
import com.tc.management.TerracottaManagement.Subsystem;
import com.tc.management.TerracottaManagement.Type;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class L1MBeanNames {

  public static final ObjectName CLUSTER_BEAN_PUBLIC;
  public static final ObjectName L1INFO_PUBLIC;
  public static final ObjectName ENTERPRISE_TC_CLIENT;

  static {
    try {
      CLUSTER_BEAN_PUBLIC = TerracottaManagement.createObjectName(Type.Cluster, Subsystem.None, null,
                                                                  "Terracotta Cluster Bean",
                                                                  TerracottaManagement.MBeanDomain.PUBLIC);
      L1INFO_PUBLIC = TerracottaManagement.createObjectName(Type.DsoClient, Subsystem.None, null, "L1 Info Bean",
                                                            TerracottaManagement.MBeanDomain.PUBLIC);
      ENTERPRISE_TC_CLIENT = TerracottaManagement.createObjectName(Type.DsoClient, Subsystem.Logging, null,
                                                                   "Terracotta Enterprise Bean",
                                                                   TerracottaManagement.MBeanDomain.PUBLIC);
    } catch (MalformedObjectNameException mone) {
      throw new RuntimeException(mone);
    } catch (NullPointerException npe) {
      throw new RuntimeException(npe);
    }
  }

}
