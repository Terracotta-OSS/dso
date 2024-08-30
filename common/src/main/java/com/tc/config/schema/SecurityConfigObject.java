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
package com.tc.config.schema;

import com.tc.config.schema.context.ConfigContext;
import com.terracottatech.config.Management;
import com.terracottatech.config.Security;
import com.terracottatech.config.Ssl;

public class SecurityConfigObject extends BaseConfigObject implements SecurityConfig {

  // TODO Break up config so that only EE code needs these props.
  public static final String TERRACOTTA_KEYCHAIN_LOCATION_PROP = "com.tc.security.keychain.url";
  public static final String TERRACOTTA_KEYCHAIN_IMPL_CLASS_PROP = "com.tc.security.keychain.impl";
  public static final String TERRACOTTA_CUSTOM_SECRET_PROVIDER_PROP = "com.terracotta.SecretProvider";

  public static final String VM_ARG_KEYCHAIN_SECRET_PROVIDER = System.getProperty(TERRACOTTA_CUSTOM_SECRET_PROVIDER_PROP);
  public static final String VM_ARG_KEYCHAIN_IMPL = System.getProperty(TERRACOTTA_KEYCHAIN_IMPL_CLASS_PROP);
  public static final String VM_ARG_KEYCHAIN_URL = System.getProperty(TERRACOTTA_KEYCHAIN_LOCATION_PROP);


  public SecurityConfigObject(ConfigContext context) {
    super(context);
    context.ensureRepositoryProvides(Security.class);
  }

  @Override
  public String getSslCertificateUri() {
    synchronized (this.context.syncLockForBean()) {
      Security bean = (Security) this.context.bean();
      if (bean == null) {
        return null;
      }
      Ssl ssl = bean.getSsl();
      if (ssl == null) {
        return null;
      }
      return ssl.getCertificate();
    }
  }

  @Override
  public String getKeyChainImplClass() {
    synchronized (this.context.syncLockForBean()) {
      if (VM_ARG_KEYCHAIN_IMPL != null) {
        return VM_ARG_KEYCHAIN_IMPL;
      }
      final Security bean = (Security) this.context.bean();
      if (bean == null) {
        return null;
      }
      return bean.getKeychain().getClass1();
    }
  }

  @Override
  public String getSecretProviderImplClass() {
    synchronized (this.context.syncLockForBean()) {
      if (VM_ARG_KEYCHAIN_SECRET_PROVIDER != null) {
        return VM_ARG_KEYCHAIN_SECRET_PROVIDER;
      }
      final Security bean = (Security) this.context.bean();
      if (bean == null) {
        return null;
      }
      return bean.getKeychain().getSecretProvider();
    }
  }

  @Override
  public String getKeyChainUrl() {
    synchronized (this.context.syncLockForBean()) {
      if (VM_ARG_KEYCHAIN_URL != null) {
        return VM_ARG_KEYCHAIN_URL;
      }
      final Security bean = (Security) this.context.bean();
      if (bean == null) {
        return null;
      }
      return bean.getKeychain().getUrl();
    }
  }

  @Override
  public String getRealmImplClass() {
    synchronized (this.context.syncLockForBean()) {
      final Security bean = (Security) this.context.bean();
      if (bean == null) {
        return null;
      }
      return bean.getAuth().getRealm();
    }
  }

  @Override
  public String getRealmUrl() {
    synchronized (this.context.syncLockForBean()) {
      final Security bean = (Security) this.context.bean();
      if (bean == null) {
        return null;
      }
      return bean.getAuth().getUrl();
    }
  }

  @Override
  public String getUser() {
    synchronized (this.context.syncLockForBean()) {
      final Security bean = (Security) this.context.bean();
      if (bean == null) {
        return null;
      }
      return bean.getAuth().getUser();
    }
  }

  @Override
  public String getSecurityServiceLocation() {
    synchronized (this.context.syncLockForBean()) {
      final Security bean = (Security) this.context.bean();
      if (bean == null) {
        return null;
      }
      Management management = bean.getManagement();
      if (management == null) {
        return null;
      }
      return management.getIa();
    }
  }

  @Override
  public Integer getSecurityServiceTimeout() {
    synchronized (this.context.syncLockForBean()) {
      final Security bean = (Security) this.context.bean();
      if (bean == null) {
        return null;
      }
      Management management = bean.getManagement();
      if (management == null) {
        return null;
      }
      return management.getTimeout();
    }
  }

  @Override
  public String getSecurityHostname() {
    synchronized (this.context.syncLockForBean()) {
      final Security bean = (Security) this.context.bean();
      if (bean == null) {
        return null;
      }
      Management management = bean.getManagement();
      if (management == null) {
        return null;
      }
      return management.getHostname();
    }
  }
}
