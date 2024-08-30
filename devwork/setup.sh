#
# Copyright Terracotta, Inc.
# Copyright Super iPaaS Integration LLC, an IBM Company 2024
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#



# To run this on Windows, use the following command line:
# type setup.sh | findstr /V /R "[ ]*#" | findstr /R /C:"[^ ]" | CMD

# trunk: https://svn.terracotta.org/repo/internal/enterprise/branches/java11-cwj

svn co https://svn.terracotta.org/repo/forge/projects/terracotta-toolkit-api/branches/java11-cwj terracotta-toolkit-api

# Choose one of the following ehcache items:
# svn co https://svn.terracotta.org/repo/ehcache/branches/java11-cwj ehcache
svn co https://svn.terracotta.org/repo/internal/ehcache-enterprise/ehcache-core-ee/branches/java11-cwj ehcache-ee

# quartz
# git clone https://github.com/quartz-scheduler/quartz.git quartz
# git clone https://github.com/cljohnso/quartz.git quartz
# Use of 'git worktree add' is preferred
## svn co https://svn.terracotta.org/repo/quartz/trunk quartz  <<< Moved to GitHub

svn co https://svn.terracotta.org/repo/forge/enterprise/quartz-ee/branches/java11-cwj quartz-ee

# svn co https://svn.terracotta.org/repo/forge/projects/management-core/trunk management-core -- now under management-common
svn co https://svn.terracotta.org/repo/forge/projects/management-common/branches/java11-cwj management-common
svn co https://svn.terracotta.org/repo/forge/enterprise/management-apps/branches/java11-cwj management-apps

svn co https://svn.terracotta.org/repo/forge/projects/core-storage-api/branches/java11-cwj core-storage-api
svn co https://svn.terracotta.org/repo/forge/projects/heap-core-storage/branches/java11-cwj heap-core-storage
svn co https://svn.terracotta.org/repo/forge/projects/bigmemory-core-storage/branches/java11-cwj bigmemory-core-storage

svn co https://svn.terracotta.org/repo/internal/search/branches/java11-cwj search

svn co https://svn.terracotta.org/repo/forge/enterprise/terracotta-license/branches/java11-cwj terracotta-license

# ipc-eventbus
# git clone https://github.com/Terracotta-OSS/ipc-eventbus.git ipc-eventbus
# git clone https://github.com/cljohnso/ipc-eventbus.git ipc-eventbus
# Use of 'git worktree add' is preferred

svn co https://svn.terracotta.org/repo/forge/projects/statistics/branches/java11-cwj statistics
svn co https://svn.terracotta.org/repo/forge/enterprise/security-modules/branches/java11-cwj security-modules
svn co https://svn.terracotta.org/repo/internal/wan40/branches/java11-cwj wan40
svn co https://svn.terracotta.org/repo/forge/enterprise/terracotta-session/branches/java11-cwj terracotta-sessions

## Modules not directly build by this POM:
svn co https://svn.terracotta.org/repo/forge/projects/bigmemory/max/branches/java11-cwj/code-samples code-samples
svn co https://svn.terracotta.org/repo/forge/projects/bigmemory/max/branches/java11-cwj/code-samples-test code-samples-test
svn co https://svn.terracotta.org/repo/internal/offheap-restartable-store/branches/java11-cwj offheap-restartable-store
svn co https://svn.terracotta.org/repo/internal/bigmemory-restartable-core-storage/branches/java11-cwj bigmemory-restartable-core-storage
svn co https://svn.terracotta.org/repo/internal/ehcache-enterprise/x-platform/branches/java11-cwj x-platform
svn co https://svn.terracotta.org/repo/forge/enterprise/clustered-entity-management/branches/java11-cwj clustered-entity-management
svn co https://svn.terracotta.org/repo/forge/projects/jmxremote_optional_tc/branches/java11-cwj jmxremote_optional_tc
svn co https://svn.terracotta.org/repo/forge/projects/test-listeners/branches/java11-cwj test-listeners
svn co https://svn.terracotta.org/repo/forge/projects/linked-child-process/branches/java11-cwj linked-child-process
svn co https://svn.terracotta.org/repo/forge/projects/product-upgradability-testing-utils/branches/java11-cwj product-upgradability-testing-utils
svn co https://svn.terracotta.org/repo/forge/projects/tc-config-schema/branches/java11-cwj tc-config-schema
svn co https://svn.terracotta.org/repo/forge/projects/toolkit-tck/branches/java11-cwj toolkit-tck
svn co https://svn.terracotta.org/repo/tc/tc-messaging/branches/java11-cwj tc-messaging
svn co https://svn.terracotta.org/repo/forge/projects/osgi-test-tool/branches/java11-cwj osgi-test-tool
svn co https://svn.terracotta.org/repo/forge/projects/bigmemory/go/branches/java11-cwj/code-samples go-code-samples
svn co https://svn.terracotta.org/repo/forge/projects/bigmemory/go/branches/java11-cwj/code-samples-test go-code-samples-test

svn co https://svn.terracotta.org/repo/internal/fast-restartable-store/branches/java11-cwj fast-restartable-store
## svn co https://svn.terracotta.org/repo/internal/fast-restartable-store/branches/java11-cwj-1.0.x fast-restartable-store-1.0.x ** Replaced by trunk **

svn co https://svn.terracotta.org/repo/forge/projects/terracotta-toolkit-api-internal/branches/java11-cwj terracotta-toolkit-api-internal


# offheap-store moved to GitHub at release 2.0.0
# git clone https://github.com/Terracotta-OSS/offheap-store.git offheap-store
# git clone https://github.com/cljohnso/offheap-store.git offheap-store
# Use of 'git worktree add' is preferred.
## svn co https://svn.terracotta.org/repo/internal/offheap-store/branches/java11-cwj-1.1.x ehcache-ee/offheap-store  ** Replaced by Terracotta-OSS/offheap-store **

