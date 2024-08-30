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

SETLOCAL ENABLEDELAYEDEXPANSION

SET root=%~d0%~p0
SET root="%root:"=%"

SET MGM_SERVER_LOCATION=${management_server_url}

SET IGNORE_SSL_CERT=
${@#ee} SET USERNAME=""
${@#ee} SET PASSWORD=""
${@#ee} SET AGENT_ID=""

:PARSE_ARGS_LOOP
IF '%1'=='' (  GOTO PARSE_ARGS_END
${@#ee} ) ELSE IF '%1'=='-u' ( SHIFT & set USERNAME=%2
${@#ee} ) ELSE IF '%1'=='-p' ( SHIFT & set PASSWORD=%2
${@#ee} ) ELSE IF '%1'=='-a' ( SHIFT & set AGENT_ID=%2
) ELSE IF '%1'=='-l' ( SHIFT & set MGM_SERVER_LOCATION=%2
) ELSE IF '%1'=='-k' ( set IGNORE_SSL_CERT=-k
) ELSE (+
${@#oss}  ECHO Usage: %0 [-l TSA Management Server URL]
${@#ee}  ECHO Usage: %0 [-l TMS URL] [-u username] [-p password] [-k]
  ECHO   -l specify the Management server location with no trailing "/", defaults to %MGM_SERVER_LOCATION%
${@#ee}  ECHO   -u specify username, only required if TMS has authentication enabled 
${@#ee}  ECHO   -p specify password, only required if TMS has authentication enabled 
${@#ee}  ECHO   -a specify agent ID to run the cluster dumper on. If not set, a list of agent IDs configured in the TMS will be returned
${@#ee}  ECHO   -k ignore invalid SSL certificate 
  ECHO   -h this help message
  GOTO:EOF
)
SHIFT
GOTO PARSE_ARGS_LOOP
:PARSE_ARGS_END

${@#ee} IF '%AGENT_ID%'=='""' (
${@#ee}  echo Missing agent ID, available IDs :
${@#ee}  CALL %root%list-agent-ids.bat %IGNORE_SSL_CERT% -u %USERNAME% -p %PASSWORD% -l %MGM_SERVER_LOCATION%
${@#ee}  EXIT /B
${@#ee} )

${@#ee} echo Starting cluster dump on %AGENT_ID%
${@#ee} call set AGENT_ID=!AGENT_ID:%%=%%%%%%%%!
${@#ee} CALL %root%rest-client.bat %IGNORE_SSL_CERT% -p "%MGM_SERVER_LOCATION%/tmc/api/v2/agents;ids=%AGENT_ID%/diagnostics/dgc" "" %USERNAME% %PASSWORD%

${@#oss} echo Starting DGC on %MGM_SERVER_LOCATION%
${@#oss} CALL %root%rest-client.bat  -p "%MGM_SERVER_LOCATION%/tc-management-api/v2/agents/diagnostics/dgc" 

ENDLOCAL
