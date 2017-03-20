#!/bin/bash

NEWRELIC_API_KEY=new-relic-key-here
NEWRELIC_APP_ID=new-relic-app-id-here
ROLLBAR_ACCESS_TOKEN=rollbar-key-here
ENVIRONMENT=production

REVISION=`git log -n 1 --pretty=format:"%H"`
SHORT_MESSAGE=`git log -n 1 --pretty=format:"%B" | head -n 1 | cut -c 1-80`
MESSAGE=`git log -n 1 --pretty=format:"%B"`
USER_EMAIL=`git config user.email | cut -c 1-31`

echo
echo ">>> Deploying to Heroku..."

# Do the deployment
cd "$( dirname "$0" )"
git subtree push --prefix app heroku master

# Notify New Relic (https://docs.newrelic.com/docs/apm/new-relic-apm/maintenance/recording-deployments)
echo ">>> Recording deployment with New Relic..."

curl                                                                                       \
  -X POST "https://api.newrelic.com/v2/applications/${NEWRELIC_APP_ID}/deployments.json"   \
  -H "X-Api-Key:${NEWRELIC_API_KEY}"                                                       \
  -H "Content-Type: application/json"                                                      \
  --data @<(cat <<EOF
{
  "deployment": {
    "revision": "${REVISION}",
    "changelog": "${SHORT_MESSAGE}",
    "description": "${MESSAGE}",
    "user": "${USER_EMAIL}"
  }
}
EOF
) > /dev/null 2>&1

# Notify Rollbar (https://rollbar.com/docs/deploys/bash/)
echo ">>> Recording deployment with Rollbar..."

curl https://api.rollbar.com/api/1/deploy/  \
  -F access_token=${ROLLBAR_ACCESS_TOKEN}   \
  -F environment=${ENVIRONMENT}             \
  -F revision=${REVISION}                   \
  -F local_username=${USER_EMAIL}           \
  -F comment="${SHORT_MESSAGE}"             \
  > /dev/null 2>&1

echo ">>> Done!"
