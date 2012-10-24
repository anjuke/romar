#!/bin/sh
mvn exec:java -Dexec.mainClass=com.anjuke.romar.http.jetty.RomarMain -Dexec.args=$1
