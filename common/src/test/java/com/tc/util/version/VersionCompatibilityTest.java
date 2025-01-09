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
package com.tc.util.version;

import junit.framework.TestCase;

public class VersionCompatibilityTest extends TestCase {

  private VersionCompatibility versionCompatibility;

  @Override
  protected void setUp() throws Exception {
    this.versionCompatibility = new VersionCompatibility();
  }

  public void testNull() {
    try {
      versionCompatibility.isCompatibleClientServer(v("1.0.0"), null);
      fail();
    } catch (NullPointerException npe) {
      // expected
    }

    try {
      versionCompatibility.isCompatibleClientServer(null, v("1.0.0"));
      fail();
    } catch (NullPointerException npe) {
      // expected
    }

    try {
      versionCompatibility.isCompatibleClientServer(null, null);
      fail();
    } catch (NullPointerException npe) {
      // expected
    }
  }

  public void testPersistenceCompatibleWithMinimum() throws Exception {
    assertTrue(versionCompatibility.isCompatibleServerPersistence(versionCompatibility.getMinimumCompatiblePersistence(),
        incrementedVersion(versionCompatibility.getMinimumCompatiblePersistence(), 0, 1, 0)));
  }

  public void testPersistenceIncompatibleWithLessThanMinimum() throws Exception {
    assertFalse(versionCompatibility.isCompatibleServerPersistence(
        incrementedVersion(versionCompatibility.getMinimumCompatiblePersistence(), -1, 0, 0),
        incrementedVersion(versionCompatibility.getMinimumCompatiblePersistence(), 0, 1, 0)));
  }

  public void testPersistenceCompatibleWithBetweenMinAndCurrent() throws Exception {
    assertTrue(versionCompatibility.isCompatibleServerPersistence(
        incrementedVersion(versionCompatibility.getMinimumCompatiblePersistence(), 0, 1, 0),
        incrementedVersion(versionCompatibility.getMinimumCompatiblePersistence(), 0, 2, 0)));
  }

  public void testPersistenceCompatibleWithinMinor() throws Exception {
    assertTrue(versionCompatibility.isCompatibleServerPersistence(
        incrementedVersion(versionCompatibility.getMinimumCompatiblePersistence(), 0, 0, 1),
        incrementedVersion(versionCompatibility.getMinimumCompatiblePersistence(), 0, 0, 2)));
    assertTrue(versionCompatibility.isCompatibleServerPersistence(
        incrementedVersion(versionCompatibility.getMinimumCompatiblePersistence(), 0, 0, 2),
        incrementedVersion(versionCompatibility.getMinimumCompatiblePersistence(), 0, 0, 1)));
  }

  public void testPersistedSameMinorAsMinButLowerDot() throws Exception {
    // Doesn't matter on .0's but check that the versions lower than the minimum are properly excluded.
    if (versionCompatibility.getMinimumCompatiblePersistence().micro() != 0) {
      assertFalse(versionCompatibility.isCompatibleServerPersistence(
          incrementedVersion(versionCompatibility.getMinimumCompatiblePersistence(), 0, 0, -1),
          incrementedVersion(versionCompatibility.getMinimumCompatiblePersistence(), 0, 0, 1)));
    }
  }

  private static Version incrementedVersion(Version base, int majorIncrement, int minorIncrement, int microIncrement) {
    return new Version((base.major() + majorIncrement) + "." +
                       (base.minor() + minorIncrement) + "." +
                       (base.micro() + microIncrement));
  }

  public void testSame() {
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.0"), v("1.0.0")));
  }

  public void testMajorBump() {
    assertFalse(versionCompatibility.isCompatibleClientServer(v("1.0.0"), v("2.0.0")));
  }

  public void testMajorDrop() {
    assertFalse(versionCompatibility.isCompatibleClientServer(v("2.0.0"), v("1.0.0")));
  }

  public void testMinorBump() {
    assertFalse(versionCompatibility.isCompatibleClientServer(v("1.0.0"), v("1.1.0")));
  }

  public void testMinorDrop() {
    assertFalse(versionCompatibility.isCompatibleClientServer(v("1.1.0"), v("1.0.0")));
  }

  public void testDotBump() {
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.0"), v("1.0.1")));
  }

  public void testDotDrop() {
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.1"), v("1.0.0")));
  }

  public void testPatchBump() {
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.1.1.134"), v("1.0.1.2.25")));
  }

  public void testPatchDrop() {
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.1.1.134"), v("1.0.1.0.25")));
  }

  public void testBuildBump() {
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.1.1.134"), v("1.0.1.1.142")));
  }

  public void testBuildDrop() {
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.1.1.134"), v("1.0.1.1.25")));
  }

  public void testSpecifierAdd() {
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.1.1.134"), v("1.0.1.1.134_fix1")));
  }

  public void testSpecifierDrop() {
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.1.1.134_fix1"), v("1.0.1.1.134")));
  }

  public void testSnapshots() {
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.0"), v("1.0.0-SNAPSHOT")));
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.0.1.34"), v("1.0.0-SNAPSHOT")));
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.0-SNAPSHOT"), v("1.0.0")));
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.0-SNAPSHOT"), v("1.0.0.1.54")));
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.0-SNAPSHOT"), v("1.0.0-SNAPSHOT")));
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.0"), v("1.0.1-SNAPSHOT")));
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.0-SNAPSHOT"), v("1.0.1")));
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.0-SNAPSHOT"), v("1.0.1-SNAPSHOT")));
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.1"), v("1.0.0-SNAPSHOT")));
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.1-SNAPSHOT"), v("1.0.0")));
    assertTrue(versionCompatibility.isCompatibleClientServer(v("1.0.1-SNAPSHOT"), v("1.0.0-SNAPSHOT")));

    assertFalse(versionCompatibility.isCompatibleClientServer(v("1.1.0"), v("1.0.0-SNAPSHOT")));
    assertFalse(versionCompatibility.isCompatibleClientServer(v("1.1.0-SNAPSHOT"), v("1.0.0")));
    assertFalse(versionCompatibility.isCompatibleClientServer(v("1.1.0-SNAPSHOT"), v("1.0.0-SNAPSHOT")));

    assertFalse(versionCompatibility.isCompatibleClientServer(v("2.0.0"), v("1.0.0-SNAPSHOT")));
    assertFalse(versionCompatibility.isCompatibleClientServer(v("2.0.0-SNAPSHOT"), v("1.0.0")));
    assertFalse(versionCompatibility.isCompatibleClientServer(v("2.0.0-SNAPSHOT"), v("1.0.0-SNAPSHOT")));
  }

  private static Version v(String version) {
    return new Version(version);
  }

}
