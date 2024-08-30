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

import org.eclipse.jetty.util.MultiException;

/**
 * Deal with Jetty MultiException to extract useful info
 */
public class MortbayMultiExceptionHelper extends AbstractExceptionHelper<MultiException> {

  public MortbayMultiExceptionHelper() {
    super(MultiException.class);
  }

  /**
   * Get closest cause, which is defined here as the first exception in a MultiException.
   * 
   * @param t MultiException
   * @return First in the MultiException
   */
  @Override
  public Throwable getProximateCause(Throwable t) {
    if (t instanceof MultiException) {
      MultiException m = (MultiException) t;
      if (m.size() > 0) return m.getThrowable(0);
    }
    return t;
  }

}
