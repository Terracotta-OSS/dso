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




THIS_DIR=`dirname $0`
TC_INSTALL_DIR=`cd $THIS_DIR;pwd`/../..

if [ -r "$TC_INSTALL_DIR"/server/bin/setenv.sh ] ; then
  . "$TC_INSTALL_DIR"/server/bin/setenv.sh
fi
JAVA=$JAVA_HOME/bin/java

workdir=`dirname $0`
workdir=`cd ${workdir} && pwd`
BIGMEMORY=${workdir}/../..

. "${BIGMEMORY}"/code-samples/bin/buildcp.sh

if [ `uname | grep CYGWIN` ]; then
   BIGMEMORY=`cygpath -w -p $BIGMEMORY`
fi

"$JAVA" -Xmx200m  -Dcom.tc.productkey.path="${BIGMEMORY}"/terracotta-license.key  -classpath \'"$BIGMEMORY_CP"\'   com.bigmemory.samples.crud.BigMemoryCrud
