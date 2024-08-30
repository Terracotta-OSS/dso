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
package com.tc.exception;


/**
 * Thrown when someone tries to lookup an object with an ObjectID and the ObjectID does not exist.
 */
public class TCObjectNotFoundException extends TCRuntimeException {
  public final static String            CLASS_SLASH = "com/tc/exception/TCObjectNotFoundException";

  private static final ExceptionWrapper wrapper     = new ExceptionWrapperImpl();

  public TCObjectNotFoundException(String missingObjectID) {
    super(wrapper.wrap("Requested Object is missing : " + missingObjectID));
  }
}
