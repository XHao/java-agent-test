#!/bin/sh 

java -jar ./target/javaagent-1.0-SNAPSHOT-jar-with-dependencies.jar -cl io.xhao.javaagent.Service -p $@

