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
package com.tc.stats;

import com.tc.management.AbstractTerracottaMBean;
import com.tc.object.ObjectID;
import com.tc.stats.api.DSORootMBean;

import javax.management.NotCompliantMBeanException;

public class DSORoot extends AbstractTerracottaMBean implements DSORootMBean {
  private final ObjectID           objectID;
  private final String             rootName;

  public DSORoot(ObjectID rootID, String name) throws NotCompliantMBeanException {
    super(DSORootMBean.class, false);

    this.objectID = rootID;
    this.rootName = name;
  }

  @Override
  public String getRootName() {
    return this.rootName;
  }

  @Override
  public ObjectID getObjectID() {
    return objectID;
  }

  @Override
  public void reset() {
    /**/
  }
}
