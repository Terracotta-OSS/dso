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

rem Set BigMemory to the base directory of the BigMemory Distribution
@SET WD=%~d0%~p0
@SET BIGMEMORY=%WD%..\..

@rem setup the class path...
CALL "%WD%"..\bin\buildcp.bat
SET BIGMEMORY_CP=%TMP_CP%


%JAVA_HOME%\bin\java.exe  -cp %BIGMEMORY_CP% -Xmx200m  -Dcom.tc.productkey.path="%BIGMEMORY%"/terracotta-license.key   com.bigmemory.samples.cache.BigMemoryAsACacheConfiguration
