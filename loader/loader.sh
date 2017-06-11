#!/bin/bash
find . -name "*java" -type f > files.txt
export CLASSPATH="../out/production/common:../lib/hazelcast-3.8.2.jar:../lib/hazelcast-enterprise-3.8.2.jar:../lib/hazelcast-enterprise-client-3.8.2.jar:../lib/joda-time-2.7.jar:../lib/cache-api-1.0.0.jar:."
javac  -cp $CLASSPATH  @files.txt -d target
cp -r src/main/resources/* target/
java -cp $CLASSPATH:target com.hazelcast.certification.process.LoaderMain


