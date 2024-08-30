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

public class MBeanNames {

  public static final ObjectName L1DUMPER_INTERNAL;
  public static final ObjectName OPERATOR_EVENTS_PUBLIC;

  static {
    try {
      L1DUMPER_INTERNAL = TerracottaManagement.createObjectName(Type.DsoClient, Subsystem.None, null,
                                                                "DSO Client Dump Bean", TerracottaManagement.MBeanDomain.INTERNAL);
      
      OPERATOR_EVENTS_PUBLIC = TerracottaManagement.createObjectName(Type.TcOperatorEvents, Subsystem.None, null,
                                                                     "Terracotta Operator Events Bean",
                                                                     TerracottaManagement.MBeanDomain.PUBLIC);
    } catch (MalformedObjectNameException mone) {
      throw new RuntimeException(mone);
    } catch (NullPointerException npe) {
      throw new RuntimeException(npe);
    }
  }

}
