#!/usr/bin/env bash

wget https://raw.githubusercontent.com/streamok/streamok/master/distribution/cmd/src/main/bash/cmd.sh -O /usr/bin/streamok > /dev/null 2>&1
chmod +x /usr/bin/streamok
echo 'Streamok client has been installed into /usr/bin/streamok .'