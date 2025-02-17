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
package com.tc.config.schema.repository;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.tc.config.schema.listen.ConfigurationChangeListener;
import com.tc.config.schema.validate.ConfigurationValidator;

/**
 * A mock {@link BeanRepository}, for use in tests.
 */
public class MockBeanRepository implements MutableBeanRepository {

  private int                         numEnsureBeanIsOfClasses;
  private Class                       lastClass;
  private RuntimeException            exceptionOnEnsureBeanIsOfClass;

  private int                         numBeans;
  private XmlObject                   returnedBean;

  private int                         numSetBeans;
  private XmlObject                   lastSetBean;
  private String                      lastSourceDescription;
  private XmlException                exceptionOnSetBean;

  private int                         numAddListeners;
  private ConfigurationChangeListener lastListener;

  private int                         numAddValidators;
  private ConfigurationValidator      lastValidator;

  private int                         numRootBeanSchemaTypes;
  private SchemaType                  returnedRootBeanSchemaType;

  public MockBeanRepository() {
    this.returnedBean = null;
    this.returnedRootBeanSchemaType = null;
    this.exceptionOnEnsureBeanIsOfClass = null;
    this.exceptionOnSetBean = null;

    reset();
  }

  public void reset() {
    this.numEnsureBeanIsOfClasses = 0;
    this.lastClass = null;

    this.numBeans = 0;

    this.numSetBeans = 0;
    this.lastSetBean = null;
    this.lastSourceDescription = null;

    this.numAddListeners = 0;
    this.lastListener = null;

    this.numAddValidators = 0;
    this.lastValidator = null;

    this.numRootBeanSchemaTypes = 0;
  }

  public void setExceptionOnEnsureBeanIsOfClass(RuntimeException exceptionOnEnsureBeanIsOfClass) {
    this.exceptionOnEnsureBeanIsOfClass = exceptionOnEnsureBeanIsOfClass;
  }

  @Override
  public void saveCopyOfBeanInAnticipationOfFutureMutation() {
    // nothing here yet
  }

  @Override
  public void didMutateBean() {
    // nothing here yet
  }

  @Override
  public void ensureBeanIsOfClass(Class theClass) {
    ++this.numEnsureBeanIsOfClasses;
    this.lastClass = theClass;
    if (this.exceptionOnEnsureBeanIsOfClass != null) throw this.exceptionOnEnsureBeanIsOfClass;
  }

  @Override
  public XmlObject bean() {
    ++this.numBeans;
    return this.returnedBean;
  }

  @Override
  public void setBean(XmlObject bean, String sourceDescription) throws XmlException {
    ++this.numSetBeans;
    this.lastSetBean = bean;
    this.lastSourceDescription = sourceDescription;
    if (this.exceptionOnSetBean != null) throw this.exceptionOnSetBean;
  }

  @Override
  public void addListener(ConfigurationChangeListener listener) {
    ++this.numAddListeners;
    this.lastListener = listener;
  }

  @Override
  public void addValidator(ConfigurationValidator validator) {
    ++this.numAddValidators;
    this.lastValidator = validator;
  }

  public ConfigurationChangeListener getLastListener() {
    return lastListener;
  }

  public XmlObject getLastSetBean() {
    return lastSetBean;
  }

  public String getLastSourceDescription() {
    return lastSourceDescription;
  }

  public ConfigurationValidator getLastValidator() {
    return lastValidator;
  }

  public int getNumAddListeners() {
    return numAddListeners;
  }

  public int getNumAddValidators() {
    return numAddValidators;
  }

  public int getNumBeans() {
    return numBeans;
  }

  public int getNumSetBeans() {
    return numSetBeans;
  }

  public void setReturnedBean(XmlObject returnedBean) {
    this.returnedBean = returnedBean;
  }

  @Override
  public SchemaType rootBeanSchemaType() {
    ++this.numRootBeanSchemaTypes;
    return this.returnedRootBeanSchemaType;
  }

  public int getNumRootBeanSchemaTypes() {
    return numRootBeanSchemaTypes;
  }

  public void setReturnedRootBeanSchemaType(SchemaType returnedRootBeanSchemaType) {
    this.returnedRootBeanSchemaType = returnedRootBeanSchemaType;
  }

  public void setExceptionOnSetBean(XmlException exceptionOnSetBean) {
    this.exceptionOnSetBean = exceptionOnSetBean;
  }

  public Class getLastClass() {
    return lastClass;
  }

  public int getNumEnsureBeanIsOfClasses() {
    return numEnsureBeanIsOfClasses;
  }

}
