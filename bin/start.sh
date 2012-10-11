#!/bin/sh
mvn exec:java -Dexec.mainClass=com.anjukeinc.service.recommender.http.jetty.RecommenderMain -Dexec.args=$1
