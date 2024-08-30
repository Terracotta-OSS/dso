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
package com.terracotta.toolkit.util.collections;

import com.tc.logging.TCLogger;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class LoggingBlockingQueueTest {

  @Test
  public void testLoggingThresholdBehaviors() throws InterruptedException {
    TCLogger mockLogger = mock(TCLogger.class);
    BlockingQueue<Boolean> queue = new LoggingBlockingQueue<>(new LinkedBlockingQueue<>(), 10, mockLogger, "%s");

    for (int i = 0; i < 9; i++) {
      queue.put(Boolean.TRUE);
    }
    verifyNoInteractions(mockLogger);
    queue.put(Boolean.TRUE);
    verify(mockLogger, times(1)).info("10");

    clearInvocations(mockLogger);

    for (int i = 0; i < 9; i++) {
      queue.put(Boolean.TRUE);
    }
    verifyNoInteractions(mockLogger);
    queue.put(Boolean.TRUE);
    verify(mockLogger, times(1)).info("20");

    clearInvocations(mockLogger);

    for (int i = 0; i < 9; i++) {
      queue.take();
    }
    verifyNoInteractions(mockLogger);
    queue.take();
    verify(mockLogger).info("10");

    clearInvocations(mockLogger);

    for (int i = 0; i < 9; i++) {
      queue.take();
    }
    verifyNoInteractions(mockLogger);
    queue.take();
    verify(mockLogger).info("0");
  }
}