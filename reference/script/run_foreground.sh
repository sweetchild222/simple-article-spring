#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
kill $(ps aux | grep 'simple-article.jar' | awk '{print $2}')
java -jar simple-article.jar --enable-native-access=ALL-UNNAMED --spring.profiles.active=prod
