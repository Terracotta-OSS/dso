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

case "$1" in
  "--help"|"-h"|"-?")
    echo "Syntax: $0 [-f /path/to/tc-config.xml] [-n server_name] [--safe-mode]"
    echo
    echo "         -f : start the server with your own Terracotta configuration instead of the default one"
    echo "         -n : specify which server you want to start when you have more than one servers configured"
    echo "--safe-mode : start the server in safe mode"
    echo
    echo "NOTE: An external trigger will be required to resume operations if server is started in safe mode"
    exit
    ;;
esac

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
case "`uname`" in
CYGWIN*) cygwin=true;;
esac

THIS_DIR=`dirname $0`
TC_INSTALL_DIR=`cd $THIS_DIR;pwd`/../..

if [ -r "$TC_INSTALL_DIR"/server/bin/setenv.sh ] ; then
  . "$TC_INSTALL_DIR"/server/bin/setenv.sh
fi

if test \! -d "${JAVA_HOME}"; then
  echo "$0: the JAVA_HOME environment variable is not defined correctly"
  exit 2
fi

# For Cygwin, convert paths to Windows before invoking java
if $cygwin; then
  [ -n "$TC_INSTALL_DIR" ] && TC_INSTALL_DIR=`cygpath -m "$TC_INSTALL_DIR"`
fi

for JAVA_COMMAND in \
"\"${JAVA_HOME}/bin/java\" -d64 -server -XX:MaxDirectMemorySize=1048576g" \
"\"${JAVA_HOME}/bin/java\" -server -XX:MaxDirectMemorySize=1048576g" \
"\"${JAVA_HOME}/bin/java\" -d64 -client  -XX:MaxDirectMemorySize=1048576g" \
"\"${JAVA_HOME}/bin/java\" -client -XX:MaxDirectMemorySize=1048576g" \
"\"${JAVA_HOME}/bin/java\" -XX:MaxDirectMemorySize=1048576g"
do
  eval ${JAVA_COMMAND} -version > /dev/null 2>&1
  if test "$?" = "0" ; then break; fi
done

#rmi.dgc.server.gcInterval is set an year to avoid system gc in case authentication is enabled
#users may change it accordingly
args="$@"

#[TAB-8127] There is an issue with passing -Dcom.tc.productkey.path on the command line.
# Remove the -Dcom.tc.productkey.path argument from the command line. Add it to JAVA_OPTS if it is found.
license_path=""
unset mod_args
for var in $args; do
  case "$var" in
    *-Dcom.tc.productkey.path=* )
    license_path=$var
    ;;
    * )
    mod_args="$mod_args $var"
    ;;
  esac
done

if [ "$license_path" ]; then
  JAVA_OPTS="$JAVA_OPTS $license_path"
fi
args="$mod_args"

start=true
while "$start"
do
unset mod_args
eval ${JAVA_COMMAND} -Xms2g -Xmx2g -XX:+HeapDumpOnOutOfMemoryError \
   -Dtc.install-root="${TC_INSTALL_DIR}" \
   -Dsun.rmi.dgc.server.gcInterval=31536000000\
   ${JAVA_OPTS} \
   -cp "${TC_INSTALL_DIR}/server/lib/tc.jar" \
   com.tc.server.TCServerMain $args
 exitValue=$?
 start=false;

 if test "$exitValue" = "11"; then
   start=true;
   # The --active flag needs to be removed from the startup options when a server gets auto-restarted.
   # When a server node is auto-restarted, the intention is to make it join the cluster as a passive.
   # So in that case it doesn't make sense to start the server with the active flag.
   # The --safe-mode flag needs to be removed for auto-restarts
   for var in $args; do
     if [ '--active' != "$var" ] && [ '-a' != "$var" ] && [ "--safe-mode" != "$var" ]; then
       mod_args="$mod_args $var";
     fi
   done
   args="$mod_args"
   echo "start-tc-server: Restarting the server..."
 elif test "$exitValue" = "12"; then
   start=true;
   # The --active flag needs to be removed from the startup options when a server gets auto-restarted.
   # When a server node is auto-restarted, the intention is to make it join the cluster as a passive.
   # So in that case it doesn't make sense to start the server with the active flag.
   # The --safe-mode flag needs to be removed for auto-restarts
   for var in $args; do
     if [ '--active' != "$var" ] && [ '-a' != "$var" ] && [ "--safe-mode" != "$var" ]; then
       mod_args="$mod_args $var";
     fi
   done
   args="$mod_args --safe-mode"
   echo "start-tc-server: Restarting the server in Safe Mode..."
 else
   exit $exitValue
 fi
done
