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
package org.terracotta.kit;

import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EhcacheAllIT {

    @Test
    public void testEhcacheAllManifest() throws IOException, ClassNotFoundException {
        String version = System.getProperty("project.version");
        Path path = Paths.get("target", "terracotta-" + version, "terracotta-" + version, "apis", "ehcache-terracotta-client-all.jar");
        System.out.println(path.toAbsolutePath());

        try (URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[] {path.toUri().toURL()}, null)) {
            urlClassLoader.loadClass("org.terracotta.util.ToolkitVersion");
            urlClassLoader.loadClass("net.sf.ehcache.Cache");
            urlClassLoader.loadClass("org.slf4j.Logger");
        }
    }
}
