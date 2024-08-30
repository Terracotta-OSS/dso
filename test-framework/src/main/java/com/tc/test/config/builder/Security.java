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
package com.tc.test.config.builder;

/**
 * @author Ludovic Orban
 */
public class Security {

  private Ssl ssl;
  private Keychain keychain;
  private Auth auth;
  private Management management;

  public Ssl getSsl() {
    return ssl;
  }

  public void setSsl(Ssl ssl) {
    this.ssl = ssl;
  }

  public Security ssl(Ssl ssl) {
    setSsl(ssl);
    return this;
  }

  public Keychain getKeychain() {
    return keychain;
  }

  public void setKeychain(Keychain keychain) {
    this.keychain = keychain;
  }

  public Security keychain(Keychain keychain) {
    setKeychain(keychain);
    return this;
  }

  public Auth getAuth() {
    return auth;
  }

  public void setAuth(Auth auth) {
    this.auth = auth;
  }

  public Security auth(Auth auth) {
    setAuth(auth);
    return this;
  }

  public Management getManagement() {
    return management;
  }

  public void setManagement(Management management) {
    this.management = management;
  }

  public Security management(Management management) {
    setManagement(management);
    return this;
  }

}
