#!/bin/sh
mvn exec:java -Dexec.mainClass=com.anjuke.romar.http.jetty.RomarMain -Dsimilarity-classname=org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity -Dexec.args=$1
