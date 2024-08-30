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
package com.terracotta.toolkit.nonstop;

import org.junit.Before;
import org.junit.Test;
import org.terracotta.toolkit.ToolkitObjectType;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Ludovic Orban
 */
public class NonStopConfigurationLookupTest {

  private NonStopContext nonStopContext = mock(NonStopContext.class);

  @Before
  public void setUp() {
    when(nonStopContext.getNonStopConfigurationRegistry()).thenReturn(new NonStopConfigRegistryImpl());
    when(nonStopContext.isEnabledForCurrentThread()).thenReturn(true);
  }

  @Test
  public void testEnabledForCurrentThreadGetNonStopConfiguration() throws Exception {
    NonStopConfigurationLookup lookup = new NonStopConfigurationLookup(nonStopContext, ToolkitObjectType.CACHE, "testName");
    assertThat(lookup.getNonStopConfiguration().isEnabled(), is(true));
  }

  @Test
  public void testDisabledForCurrentThreadGetNonStopConfiguration() throws Exception {
    when(nonStopContext.isEnabledForCurrentThread()).thenReturn(false);
    NonStopConfigurationLookup lookup = new NonStopConfigurationLookup(nonStopContext, ToolkitObjectType.CACHE, "testName");
    assertThat(lookup.getNonStopConfiguration().isEnabled(), is(false));
  }

}
