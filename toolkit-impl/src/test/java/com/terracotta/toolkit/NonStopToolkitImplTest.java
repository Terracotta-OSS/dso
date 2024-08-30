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
package com.terracotta.toolkit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.terracotta.toolkit.internal.ToolkitInternal;

import com.tc.abortable.AbortableOperationManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class NonStopToolkitImplTest {

  @Test
  public void testShutdownWhenToolkitFailsToInitialize() throws Exception {
    @SuppressWarnings("unchecked")
    FutureTask<ToolkitInternal> toolkitDelegateFutureTask = mock(FutureTask.class);
    when(toolkitDelegateFutureTask.get()).thenThrow(new ExecutionException("oops", new RuntimeException()));

    NonStopToolkitImpl nonStopToolkit = new NonStopToolkitImpl(toolkitDelegateFutureTask,
                                                               mock(AbortableOperationManager.class), "uuid");
    nonStopToolkit.shutdown();
  }

  @Test
  public void testShutdownProperlyShutsDownToolkit() throws Exception {
    @SuppressWarnings("unchecked")
    FutureTask<ToolkitInternal> toolkitDelegateFutureTask = mock(FutureTask.class);
    ToolkitInternal toolkitInternal = mock(ToolkitInternal.class);
    when(toolkitDelegateFutureTask.get()).thenReturn(toolkitInternal);

    NonStopToolkitImpl nonStopToolkit = new NonStopToolkitImpl(toolkitDelegateFutureTask,
                                                               mock(AbortableOperationManager.class), "uuid");
    nonStopToolkit.shutdown();

    verify(toolkitInternal).shutdown();
  }

}