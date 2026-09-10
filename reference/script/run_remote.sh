#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
pkill -f 'simple-article.jar'
java -jar ~/simple/simple-article.jar --enable-native-access=ALL-UNNAMED --spring.profiles.active=prod
