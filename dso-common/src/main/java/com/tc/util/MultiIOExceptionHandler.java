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
package com.tc.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiIOExceptionHandler {

  private final List<Throwable> exceptions = new ArrayList<>();

  public void doSafely(SafeRunnable safeRunnable) {
    try {
      safeRunnable.run();
    } catch (MultiIOException e) {
      exceptions.addAll(Arrays.asList(e.getSuppressed()));
    } catch (Exception e) {
      exceptions.add(e);
    }
  }

  public void done(String errorMsg) throws IOException {
    if (!exceptions.isEmpty()) {
      MultiIOException ioException = new MultiIOException(errorMsg);
      exceptions.forEach(ioException::addSuppressed);
      exceptions.clear();
      throw ioException;
    }
  }

  public void addAsSuppressedTo(Throwable t) {
    exceptions.forEach(t::addSuppressed);
    exceptions.clear();
  }

  static class MultiIOException extends IOException {
    MultiIOException(String message) {
      super(message);
    }
  }

  @FunctionalInterface
  public interface SafeRunnable {
    void run() throws Exception;
  }

}
