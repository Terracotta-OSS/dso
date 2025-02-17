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
package com.tc.net.protocol.tcm;

import com.tc.exception.TCException;

/**
 * @author Orion Letizi
 */
public class TCMessageException extends TCException {

  /**
   * 
   */
  public TCMessageException() {
    super();
  }

  /**
   * @param arg0
   */
  public TCMessageException(String arg0) {
    super(arg0);
  }

  /**
   * @param arg0
   */
  public TCMessageException(Throwable arg0) {
    super(arg0);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public TCMessageException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

}
