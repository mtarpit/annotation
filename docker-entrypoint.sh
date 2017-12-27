#!/bin/bash

set -x

#echo environment variables
env

# start annotation application
APP_PID="/annotation.pid"
APP_HOST=`hostname -f`

/app/bin/annotation -Dconfig.file=/app/conf/annotation.conf -Dlogger.file=/app/conf/logger.xml -Djdk.tls.rejectClientInitiatedRenegotiation=true -Djdk.tls.ephemeralDHKeySize=2048 -J-Xms${JVM_HEAP_SIZE} -J-Xmx${JVM_HEAP_SIZE} -Dhttp.port=${PORT} -Dpidfile.path=${APP_PID} -Dapp.host=${APP_HOST}

set +x
