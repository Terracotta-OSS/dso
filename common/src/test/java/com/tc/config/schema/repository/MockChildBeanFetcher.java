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
package com.tc.config.schema.repository;

import org.apache.xmlbeans.XmlObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock {@link ChildBeanFetcher}, for use in tests.
 */
public class MockChildBeanFetcher implements ChildBeanFetcher {

  private int         numGetChilds;
  private XmlObject[] returnedChildren;
  private int         returnedChildrenPos;
  private List        lastParents;

  public MockChildBeanFetcher() {
    this.returnedChildren = new XmlObject[] { null };
    this.lastParents = new ArrayList();

    reset();
  }

  public void reset() {
    this.numGetChilds = 0;
    this.returnedChildrenPos = 0;
    this.lastParents.clear();
  }

  @Override
  public XmlObject getChild(XmlObject parent) {
    ++this.numGetChilds;
    this.lastParents.add(parent);
    XmlObject out = this.returnedChildren[this.returnedChildrenPos++];
    if (this.returnedChildrenPos >= this.returnedChildren.length) this.returnedChildrenPos = 0;
    return out;
  }

  public XmlObject getLastParent() {
    return (XmlObject) this.lastParents.get(this.lastParents.size() - 1);
  }
  
  public XmlObject[] getLastParents() {
    return (XmlObject[]) this.lastParents.toArray(new XmlObject[this.lastParents.size()]);
  }

  public int getNumGetChilds() {
    return numGetChilds;
  }

  public void setReturnedChild(XmlObject returnedChild) {
    setReturnedChildren(new XmlObject[] { returnedChild });
  }

  public void setReturnedChildren(XmlObject[] returnedChildren) {
    this.returnedChildren = returnedChildren;
  }

}
