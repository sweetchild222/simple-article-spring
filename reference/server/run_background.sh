#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
kill $(ps aux | grep 'java -jar' | awk '{print $2}')
nohup java -jar ~/simple/simple-article-0.0.1.jar --spring.profiles.active=prod > /dev/null &
