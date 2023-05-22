#!/bin/bash

set -eo pipefail

# Setting Environment Build Context
if [[ "${CIRCLE_BRANCH}" == "QA" ]] || [[ "${CIRCLE_BRANCH}" =~ ^hotfix\/.*$ ]]; then
    export CI_CONTEXT="Azure_QA"
    export CI_APP_GROUP="qa-webapps-ehr"
    export CI_ENVIRONMENT="qa"
    export CI_SLOT=""

elif [[ "${CIRCLE_BRANCH}" == "main" ]]; then
    export CI_CONTEXT="Azure_Prod"
    export CI_APP_GROUP="prod-webapps-ehr"
    export CI_ENVIRONMENT="prod"
    # export CI_SLOT="--slot staging"
    export CI_SLOT=""

else
    export CI_CONTEXT="Azure"
    export CI_APP_GROUP="dev-webapps-ehr"
    export CI_ENVIRONMENT="dev"
    export CI_SLOT=""
# Uncomment below and specify what should occur conditions are not fulfilled.
# Do a debug build perhaps?
#else
#    export CI_CONTEXT="Azure"
#    export CI_TRANSFORM="env.sample"
#    export CI_NET_TRANSFORM="Debug"
#    export CI_YARN_TRANSFORM="dev"
#    export CI_APP_GROUP="dev-webapps-ehr"
#    export CI_ENVIRONMENT="dev"
#    export CI_SLOT=""
fi

echo "Setting Circle-CI Build Context to ${CI_CONTEXT}"

echo "Persisting Pipeline Variables to /tmp"
jq -n --arg CI_CONTEXT "$CI_CONTEXT" \
      --arg CI_APP_GROUP "$CI_APP_GROUP" \
      --arg CI_ENVIRONMENT "$CI_ENVIRONMENT" \
      --arg CI_SLOT "$CI_SLOT" \
      '{
        "dynamic-context": "'$CI_CONTEXT'",
        "resource-group": "'$CI_APP_GROUP'",
        "az-environment": "'$CI_ENVIRONMENT'",
        "production-slot": "'"$CI_SLOT"'"
      }' > /tmp/ci_parameters.json