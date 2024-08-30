#!/bin/sh
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



if [ ! -d "$JAVA_HOME" ]; then
   echo "ERROR: JAVA_HOME must point to Java installation. Please see code-samples/README.txt for more information."
   echo "    $JAVA_HOME"
fi

# You May Need To Change this to your BigMemory installation root
workdir=`dirname $0`
workdir=`cd ${workdir} && pwd`
BIGMEMORY=${workdir}/../..



BIGMEMORY_CP=""



for jarfile in "$BIGMEMORY"/common/lib/*.jar; do
  BIGMEMORY_CP="$BIGMEMORY_CP":$jarfile
done

for jarfile in "$BIGMEMORY"/code-samples/lib/*.jar; do
  BIGMEMORY_CP="$BIGMEMORY_CP":$jarfile
done

for jarfile in "$BIGMEMORY"/apis/ehcache/lib/*.jar; do
  BIGMEMORY_CP="$BIGMEMORY_CP":$jarfile
done


for jarfile in "$BIGMEMORY"/apis/toolkit/lib/*.jar; do
  BIGMEMORY_CP="$BIGMEMORY_CP":$jarfile
done


BIGMEMORY_CP="$BIGMEMORY_CP":

# Convert to Windows path if cygwin detected
# This allows users to use .sh scripts in cygwin
if [ `uname | grep CYGWIN` ]; then
  BIGMEMORY_CP=`cygpath -w -p "$BIGMEMORY_CP"`
fi

