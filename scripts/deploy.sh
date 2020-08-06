#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/unicorn.pem \
    target/sweater-1.0-SNAPSHOT.jar \
    ubuntu@18.158.238.136:/home/ubuntu/

echo 'Restart server...'

ssh -i ~/.ssh/unicorn.pem -tt ubuntu@18.158.238.136 << EOF

pgrep java | xargs kill -9
nohup java -jar sweater-1.0-SNAPSHOT.jar > log.txt &

EOF

echo 'Bye'
