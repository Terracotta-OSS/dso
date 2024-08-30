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

 setlocal


@SET WD=%~d0%~p0

rem Set the path to your Terracotta server home here
@SET TC_HOME=%WD%..\..\server


IF NOT EXIST "%TC_HOME%\bin\start-tc-server.bat" (
echo "Modify the script to set TC_HOME"
exit /B
)

start "terracotta" "%TC_HOME%\bin\start-tc-server.bat"

endlocal
