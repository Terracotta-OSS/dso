#!/bin/bash


rest_client=`dirname $0`/rest-client.sh
mgm_server_location=${management_server_url}

function usage {
${@#loss}  echo "Usage: $0 [-l TSA Management Server URL]"
  echo "  -l specify the Management server location with no trailing \"/\" (defaults to ${mgm_server_location})"
${@#lee}  echo "  -u specify username, only required if TMS has authentication enabled"
${@#lee}  echo "  -p specify password, only required if TMS has authentication enabled"
${@#lee}  echo "  -a specify agent ID to run the cluster dumper on. If not set, a list of agent IDs configured in the TMS will be returned"
${@#lee}  echo "  -k ignore invalid SSL certificate"
  echo "  -h this help message"
  exit 1
}

${@#loss} while getopts l:kh opt
${@#lee} while getopts l:u:p:a:kh opt
do
   case "${opt}" in
      l) mgm_server_location=$OPTARG;;
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

${@#lee} echo "starting DGC on ${agentId} ..."
${@#lee} ${rest_client} ${ignoreSslCert} -p "${mgm_server_location}/tmc/api/v2/agents;ids=${agentId}/diagnostics/dgc" "" "${username}" "${password}"

${@#loss} echo "starting DGC on ${mgm_server_location} ..."
${@#loss} ${rest_client} ${ignoreSslCert} -p "${mgm_server_location}/tc-management-api/v2/agents/diagnostics/dgc"

exit $?
