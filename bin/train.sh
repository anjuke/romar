#!/bin/sh

cd $(dirname $0)/
cd ..
ROMAR_HOME=`pwd`



for f in $ROMAR_HOME/romar-core-*.jar; do
    CLASSPATH=$CLASSPATH:$f;
done


for f in $ROMAR_HOME/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done


java -server -Xmn512M -Xmx1G -Xms1G -XX:PermSize=64M -XX:MaxPermSize=64m \
    -XX:+UseParallelGC -XX:+UseParallelOldGC   \
    -Dromar.home=$ROMAR_HOME \
    -Dromar.config=$ROMAR_HOME/conf/romar.yaml \
    -cp $CLASSPATH \
    com.anjuke.romar.train.LocalFileTrainer