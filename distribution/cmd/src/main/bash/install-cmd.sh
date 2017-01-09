#!/usr/bin/env bash

if [ -z "${BRANDING}" ]; then
  BRANDING='streamok'
fi

wget https://raw.githubusercontent.com/streamok/streamok/master/distribution/cmd/src/main/bash/cmd.sh -O /usr/bin/${BRANDING} > /dev/null 2>&1
chmod +x /usr/bin/${BRANDING}
echo "Installed ${BRANDING} client into /usr/bin/${BRANDING} ."