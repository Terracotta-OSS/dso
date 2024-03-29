@echo off

REM 
REM The contents of this file are subject to the Terracotta Public License Version
REM 2.0 (the "License"); You may not use this file except in compliance with the
REM License. You may obtain a copy of the License at 
REM 
REM      http://terracotta.org/legal/terracotta-public-license.
REM 
REM Software distributed under the License is distributed on an "AS IS" basis,
REM WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
REM the specific language governing rights and limitations under the License.
REM 
REM The Covered Software is Terracotta Platform.
REM 
REM The Initial Developer of the Covered Software is 
REM     Terracotta, Inc., a Software AG company
REM

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
${@#ee} ) ELSE IF '%1'=='-k' ( set IGNORE_SSL_CERT=-k
) ELSE (
${@#oss}  ECHO Usage: %0 [-l TSA Management Server URL]
${@#ee}  ECHO Usage: %0 [-l TMS URL] [-u username] [-p password] [-k]
  ECHO   -l specify the Management server location with no trailing "/", defaults to %MGM_SERVER_LOCATION%
${@#ee}  ECHO   -k ignore invalid SSL certificate 
${@#ee}  ECHO   -u specify username, only required if TMS has authentication enabled 
${@#ee}  ECHO   -p specify password, only required if TMS has authentication enabled 
${@#ee}  ECHO   -a specify agent ID to run the cluster dumper on. If not set, a list of agent IDs configured in the TMS will be returned
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
${@#ee} CALL %root%rest-client.bat %IGNORE_SSL_CERT% -p "%MGM_SERVER_LOCATION%/tmc/api/v2/agents;ids=%AGENT_ID%/diagnostics/dumpClusterState" "" %USERNAME% %PASSWORD%


${@#oss} echo Starting cluster dump on %MGM_SERVER_LOCATION%
${@#oss} CALL %root%rest-client.bat %IGNORE_SSL_CERT% -p "%MGM_SERVER_LOCATION%/tc-management-api/v2/agents/diagnostics/dumpClusterState"


ENDLOCAL
