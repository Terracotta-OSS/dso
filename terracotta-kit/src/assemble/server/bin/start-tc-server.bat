@echo off

@REM
@REM Copyright Terracotta, Inc.
@REM Copyright Super iPaaS Integration LLC, an IBM Company 2024
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM     http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

if "%1" == "--help" goto :PRINT_HELP
if "%1" == "-h" goto :PRINT_HELP
if "%1" == "-?" goto :PRINT_HELP
goto :START

:PRINT_HELP
echo Syntax: %~n0 [-f /path/to/tc-config.xml] [-n server_name] [--safe-mode]
echo.
echo          -f : start the server with your own Terracotta configuration instead of the default one
echo          -n : specify which server you want to start when you have more than one servers configured
echo --safe-mode : start the server in safe mode
echo.
echo NOTE: An external trigger will be required to resume operations if server is started in safe mode
exit /b 0


:START
setlocal
set TC_INSTALL_DIR=%~d0%~p0..\..
set TC_INSTALL_DIR="%TC_INSTALL_DIR:"=%"

if exist %TC_INSTALL_DIR%\server\bin\setenv.bat (
  call %TC_INSTALL_DIR%\server\bin\setenv.bat
)

if not defined JAVA_HOME (
  echo Environment variable JAVA_HOME needs to be set
  exit /b 1
)

set JAVA_HOME="%JAVA_HOME:"=%"

for %%C in ("\bin\java -d64 -server -XX:MaxDirectMemorySize=1048576g" "\bin\java -server -XX:MaxDirectMemorySize=1048576g" "\bin\java -d64 -client  -XX:MaxDirectMemorySize=1048576g" "\bin\java -client -XX:MaxDirectMemorySize=1048576g" "\bin\java -XX:MaxDirectMemorySize=1048576g") do (
  set JAVA_COMMAND=%JAVA_HOME%%%~C
  %JAVA_HOME%%%~C -version > NUL 2>&1
  if not errorlevel 1 (
    goto FOUND_COMMAND
  )
)

@REM rmi.dgc.server.gcInterval is set an year to avoid system gc in case authentication is enabled
@REM users may change it accordingly

:FOUND_COMMAND
set CLASSPATH=%TC_INSTALL_DIR%\server\lib\tc.jar
set OPTS=%SERVER_OPT% -Xms2g -Xmx2g -XX:+HeapDumpOnOutOfMemoryError
set OPTS=%OPTS% -Dsun.rmi.dgc.server.gcInterval=31536000000
set OPTS=%OPTS% -Dtc.install-root=%TC_INSTALL_DIR%

setlocal EnableDelayedExpansion

set ARGS=%*
set LICENSE_ARG=

@REM [TAB-8127] There is an issue with passing -Dcom.tc.productkey.path on the command line.
call :PARSE_ARGS_FOR_LICENSE_PATH

set JAVA_OPTS=%OPTS% %JAVA_OPTS% %LICENSE_ARG%

:START_TCSERVER
%JAVA_COMMAND% %JAVA_OPTS% -cp %CLASSPATH% com.tc.server.TCServerMain !ARGS!
if %errorlevel% EQU 11 (
  call :CLEAN_ARGS_FOR_AUTO_RESTART
  echo start-tc-server: Restarting the server...
  goto START_TCSERVER
)

if %errorlevel% EQU 12 (
  call :CLEAN_ARGS_FOR_AUTO_RESTART
  set ARGS=!ARGS! --safe-mode
  echo start-tc-server: Restarting the server in Safe Mode...
  goto START_TCSERVER
)

:CLEAN_ARGS_FOR_AUTO_RESTART
set MOD_ARGS=
@REM The --active flag needs to be removed from the startup options when a server gets auto-restarted.
@REM When a server node is auto-restarted, the intention is to make it join the cluster as a passive.
@REM So in that case it doesn't make sense to start the server with the active flag.
for %%I in (%ARGS%) do (
  if --active NEQ %%I if -a NEQ %%I if --safe-mode NEQ %%I (
    if "" EQU "!MOD_ARGS!" (
      set MOD_ARGS=%%I
    ) else (
      set MOD_ARGS=!MOD_ARGS! %%I
    )
  )
  set ARGS=!MOD_ARGS!
)
exit /b 0

:PARSE_ARGS_FOR_LICENSE_PATH
@REM Remove the -Dcom.tc.productkey.path argument from the command line. If the argument exists, add it
@REM to the LICENSE_ARG variable.
set /a found_path=0
set MOD_ARGS=
for %%I in (%ARGS%) do (
  if !found_path! NEQ 0 (
    set LICENSE_ARG=-Dcom.tc.productkey.path=%%I
  )
  if "-Dcom.tc.productkey.path"=="%%I" (
    set /a found_path+=1
  ) else (
    set MOD_ARGS=!MOD_ARGS! %%I
  )
)
set ARGS=!MOD_ARGS!
exit /b 0