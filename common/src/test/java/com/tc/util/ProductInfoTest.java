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
package com.tc.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Enumeration;

import junit.framework.TestCase;

public class ProductInfoTest extends TestCase {

  public void testNullCodeSource() throws Exception {
    ClassLoaderWithoutCodeSource loader = new ClassLoaderWithoutCodeSource();

    loader.nullCodeSource = true;
    Class<?> productInfoClass = loader.loadClass(ProductInfo.class.getName());
    assertNull(productInfoClass.getProtectionDomain().getCodeSource());
    assertEquals(loader, productInfoClass.getClassLoader());

    Object productInfo = productInfoClass.getMethod("getInstance").invoke(null);
    assertEquals(ProductInfo.getInstance().toString(), productInfo.toString());
  }

  public void testNullCodeSourceLocation() throws Exception {
    ClassLoaderWithoutCodeSource loader = new ClassLoaderWithoutCodeSource();

    loader.nullLocation = true;
    Class<?> productInfoClass = loader.loadClass(ProductInfo.class.getName());
    assertNull(productInfoClass.getProtectionDomain().getCodeSource().getLocation());
    assertEquals(loader, productInfoClass.getClassLoader());

    Object productInfo = productInfoClass.getMethod("getInstance").invoke(null);
    assertEquals(ProductInfo.getInstance().toString(), productInfo.toString());
  }

  public void testOpenSourceEditionWithPatch() {
    try {
      InputStream buildData = ProductInfo.getData("TestBuildData.txt");
      InputStream patchData = ProductInfo.getData("TestPatchData.txt");
      ProductInfo info = new ProductInfo(buildData, patchData);
      verifyOpenSourceBuildData(info);
      verifyPatchInfo(info);
      assertEquals("20080620-235959 (Revision 12112 from thepatchbranch)",
                   info.patchBuildID());
      assertEquals("Patch Level 5, as of 20080620-235959 (Revision 12112 from thepatchbranch)",
                   info.toLongPatchString());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  public void testOpenSourceEditionWithoutPatch() {
    try {
      InputStream buildData = ProductInfo.getData("TestBuildData.txt");
      ProductInfo info = new ProductInfo(buildData, null);
      verifyOpenSourceBuildData(info);
      verifyNoPatchInfo(info);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  public void testEnterpriseEditionWithPatch() {
    try {
      InputStream buildData = ProductInfo.getData("TestEnterpriseBuildData.txt");
      InputStream patchData = ProductInfo.getData("TestPatchData.txt");
      ProductInfo info = new ProductInfo(buildData, patchData);
      verifyEnterpriseBuildData(info);
      verifyPatchInfo(info);
      assertEquals("20080620-235959 (Revision 12112 from thepatchbranch)",
                   info.patchBuildID());
      assertEquals("Patch Level 5, as of 20080620-235959 (Revision 12112 from thepatchbranch)",
                   info.toLongPatchString());

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  private void verifyOpenSourceBuildData(ProductInfo info) {
    assertEquals("thebranch", info.buildBranch());
    assertEquals("20080616-130651 (Revision 12345 from thebranch)", info.buildID());
    assertEquals("12345", info.buildRevision());
    assertEquals(ProductInfo.UNKNOWN_VALUE, info.buildRevisionFromEE());
    assertEquals("20080616-130651", info.buildTimestampAsString());

    String copyright = info.copyright();
    assertNotNull(copyright);
    assertTrue(copyright.indexOf("Copyright (c)") >= 0);
    assertTrue(copyright.indexOf("Terracotta, Inc.") >= 0);
    assertTrue(copyright.indexOf("All rights reserved.") >= 0);

    assertEquals("Opensource", info.edition());
    assertTrue(info.isOpenSource());
    assertFalse(info.isEnterprise());
    assertEquals("1.2.3", info.kitID());
    assertEquals("Unlimited development", info.license());
    assertEquals("1.2.3-SNAPSHOT", info.mavenArtifactsVersion());
    assertEquals("Terracotta", info.moniker());
    System.out.println(info.toLongString());
    assertEquals("Terracotta 1.2.3-SNAPSHOT, as of 20080616-130651 (Revision 12345 from thebranch)",
                 info.toLongString());
    assertEquals("Terracotta 1.2.3-SNAPSHOT", info.toShortString());
    assertEquals("1.2.3-SNAPSHOT", info.version());
  }

  private void verifyEnterpriseBuildData(ProductInfo info) {
    assertEquals("thebranch", info.buildBranch());
    assertEquals("20080616-130651 (Revision 12345 from thebranch)", info.buildID());
    assertEquals("12345", info.buildRevision());
    assertEquals("98765", info.buildRevisionFromEE());
    assertEquals("20080616-130651", info.buildTimestamp());
    assertEquals("20080616-130651", info.buildTimestampAsString());

    String copyright = info.copyright();
    assertNotNull(copyright);
    assertTrue(copyright.indexOf("Copyright (c)") >= 0);
    assertTrue(copyright.indexOf("Terracotta, Inc.") >= 0);
    assertTrue(copyright.indexOf("All rights reserved.") >= 0);

    assertEquals("Opensource", info.edition());
    assertFalse(info.isEnterprise());
    assertTrue(info.isOpenSource());
    assertEquals("1.2.3", info.kitID());
    assertEquals("Unlimited development", info.license());
    assertEquals("1.2.3-SNAPSHOT", info.mavenArtifactsVersion());
    assertEquals("Terracotta", info.moniker());
    assertEquals("Terracotta 1.2.3-SNAPSHOT, as of 20080616-130651 (Revision 12345 from thebranch)",
                 info.toLongString());
    assertEquals("Terracotta 1.2.3-SNAPSHOT", info.toShortString());
    assertEquals("1.2.3-SNAPSHOT", info.version());
  }

  private void verifyPatchInfo(ProductInfo info) {
    assertEquals(true, info.isPatched());
    assertEquals("thepatchbranch", info.patchBranch());
    assertEquals("5", info.patchLevel());
    assertEquals("12112", info.patchRevision());
    assertEquals("9999", info.patchEERevision());
    assertEquals("20080620-235959", info.patchTimestampAsString());
    assertEquals("Patch Level 5", info.toShortPatchString());
  }

  private void verifyNoPatchInfo(ProductInfo info) {
    assertEquals(false, info.isPatched());
    assertEquals(ProductInfo.UNKNOWN_VALUE, info.patchBranch());
    assertEquals("[unknown] (Revision [unknown] from [unknown])", info.patchBuildID());
    assertEquals(ProductInfo.UNKNOWN_VALUE, info.patchLevel());
    assertEquals(ProductInfo.UNKNOWN_VALUE, info.patchRevision());
    assertEquals(ProductInfo.UNKNOWN_VALUE, info.patchTimestamp());
    assertEquals(ProductInfo.UNKNOWN_VALUE, info.patchTimestampAsString());
    assertEquals(ProductInfo.UNKNOWN_VALUE, info.patchEERevision());
    assertEquals("Patch Level [unknown], as of [unknown] (Revision [unknown] from [unknown])",
                 info.toLongPatchString());
    assertEquals("Patch Level [unknown]", info.toShortPatchString());
  }

  private static URL[] systemPaths() {
    String pathSeparator = System.getProperty("path.separator");
    String[] classPathEntries = System.getProperty("java.class.path").split(pathSeparator);
    return Arrays.stream(classPathEntries).map(s -> {
      try {
        return new File(s).toURI().toURL();
      } catch (MalformedURLException e) {
        e.printStackTrace();
        return null;
      }
    }).toArray(URL[]::new);
  }

  private static class ClassLoaderWithoutCodeSource extends URLClassLoader {

    boolean nullLocation   = false;
    boolean nullCodeSource = false;

    ClassLoaderWithoutCodeSource() {
      super(systemPaths(), null);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
      Class<?> c = findLoadedClass(name);
      if (c != null) { return c; }

      URL url = findResource(name.replace('.', '/').concat(".class"));
      if (url == null) { return super.loadClass(name); }

      ByteBuffer b = getUrl(url);

      ProtectionDomain pd = new ProtectionDomain(codeSource(url), new PermissionCollection() {
        @Override
        public boolean implies(Permission permission) {
          return false;
        }

        @Override
        public Enumeration<Permission> elements() {
          return new Enumeration<Permission>() {

            @Override
            public boolean hasMoreElements() {
              return false;
            }

            @Override
            public Permission nextElement() {
              throw new AssertionError();
            }
          };
        }

        @Override
        public void add(Permission permission) {
          //
        }
      });

      return defineClass(name, b, pd);
    }

    private CodeSource codeSource(URL url) {
      if (nullCodeSource) return null;
      return new CodeSource(nullLocation ? null : url, new Certificate[] {});
    }

    private ByteBuffer getUrl(URL url) {
      InputStream in = null;
      try {
        in = url.openStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) >= 0) {
          baos.write(b);
        }

        return ByteBuffer.wrap(baos.toByteArray());
      } catch (IOException e) {
        throw new RuntimeException(e);
      } finally {
        if (in != null) {
          try {
            in.close();
          } catch (IOException ioe) {
            // ignore
          }
        }
      }
    }
  }

}
