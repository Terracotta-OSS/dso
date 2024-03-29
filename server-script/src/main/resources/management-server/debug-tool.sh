#!/bin/bash


rest_client=`dirname $0`/rest-client.sh
mgm_server_location=${management_server_url}

thread_dump=threadDump
thread_dump_archive=threadDumpArchive
function usage {
${@#loss}  echo "Usage: $0 [-l TSA Management Server URL]  [-z]"
${@#lee}  echo "Usage: $0 [-l TMS URL] [-u username] [-p password] [-a agentId] [-k] [-z]"
  echo "  -l specify the Management server location with no trailing \"/\" (defaults to ${mgm_server_location})"
  echo "  -z create a ZIP file with the result instead of displaying it"
${@#lee}  echo "  -u specify username, only required if TMS has authentication enabled"
${@#lee}  echo "  -p specify password, only required if TMS has authentication enabled"
${@#lee}  echo "  -a specify agent ID to get the thread dump from. If not set, a list of agent IDs configured in the TMS will be returned"
${@#lee}  echo "  -k ignore invalid SSL certificate"
  echo "  -h this help message"
  exit 1
}

${@#loss} while getopts l:zkh opt
${@#lee} while getopts l:u:p:a:zkh opt
do
   case "${opt}" in
      l) mgm_server_location=$OPTARG;;
      z) doZip="-z";;
${@#lee}      u) username=$OPTARG;;
${@#lee}      p) password=$OPTARG;;
${@#lee}      a) agentId=$OPTARG;;
${@#lee}      k) ignoreSslCert="-k";;
      h) usage & exit 0;;
      *) usage;;
   esac
done

${@#lee} if [[ "${agentId}" == "" ]]; then
${@#lee}  echo "Missing agent ID, available IDs:"
${@#lee}  exec `dirname $0`/list-agent-ids.sh ${ignoreSslCert} -u "${username}" -p "${password}" -l "${mgm_server_location}"
${@#lee} fi

${@#loss} echo "Getting cluster thread dump of ${mgm_server_location} ..."
${@#lee} echo "Getting cluster thread dump of ${agentId} ..."
if [[ "${doZip}" == "" ]]; then
${@#loss}  ${rest_client} ${ignoreSslCert} -g "${mgm_server_location}/tc-management-api/v2/agents/diagnostics/${thread_dump}"  '$.entities[*].dump'
${@#lee} ${rest_client} ${ignoreSslCert} -g "${mgm_server_location}/tmc/api/v2/agents;ids=${agentId}/diagnostics/threadDump" "" "${username}" "${password}" '$.entities[*].dump'
else
${@#lee} if ! ${rest_client} ${ignoreSslCert} -e -f -g "${mgm_server_location}/tmc/api/v2/agents" "" "${username}" "${password}" '$.entities[?(@.agencyOf == 'TSA')].[?(@.agentId == '${agentId}')].agentId' &> /dev/null ; then
${@#lee}    echo "Invalid agent ID, available IDs:"
${@#lee}    exec `dirname $0`/list-agent-ids.sh ${ignoreSslCert} -u "${username}" -p "${password}" -l "${mgm_server_location}"
${@#lee}  fi
${@#lee}  ${rest_client} ${ignoreSslCert} -g "${mgm_server_location}/tmc/api/v2/agents;ids=${agentId}/diagnostics/threadDumpArchive" "" "${username}" "${password}" > ${agentId}-ThreadDump.zip
${@#loss}  ${rest_client} ${ignoreSslCert} -g "${mgm_server_location}/tc-management-api/v2/agents/diagnostics/${thread_dump_archive}"  > ThreadDump.zip
fi
exit $?
