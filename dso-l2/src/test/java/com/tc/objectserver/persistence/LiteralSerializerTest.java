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
package com.tc.objectserver.persistence;

import com.tc.test.TCTestCase;

import java.nio.ByteBuffer;

/**
 * @author tim
 */
public class LiteralSerializerTest extends TCTestCase {
  public void testNestedStrings() throws Exception {
    ByteBuffer s1 = LiteralSerializer.INSTANCE.transform("foo");
    ByteBuffer s2 = LiteralSerializer.INSTANCE.transform("bar");
    ByteBuffer combined = ByteBuffer.allocate(s1.remaining() + s2.remaining());
    combined.put(s1).put(s2).flip();
    assertEquals("foo", LiteralSerializer.INSTANCE.recover(combined));
    assertEquals("bar", LiteralSerializer.INSTANCE.recover(combined));
  }
}
