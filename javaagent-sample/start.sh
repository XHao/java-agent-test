#!/usr/bin/sh
echo "start!"
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -jar ./target/javaagent-sample-1.0-SNAPSHOT-jar-with-dependencies.jar
echo "end"
