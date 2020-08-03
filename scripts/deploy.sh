#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/spring_mvc.pem \
    target/sweater-1.0-SNAPSHOT.jar \
    ubuntu@18.158.156.116:/home/ubuntu/unicorn

echo 'Restart server...'

ssh -i ~/.ssh/id_rsa_drucoder ubuntu@18.158.156.116 << EOF

pgrep java | xargs kill -9
nohup java -jar sweater-1.0-SNAPSHOT.jar > log.txt &

EOF

echo 'Bye'
