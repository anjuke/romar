#!/bin/sh

cd $(dirname $0)/
cd ..
ROMAR_HOME=`pwd`

project_name="romar"
project_pid="$ROMAR_HOME/logs/${project_name}.pid"
echo starting $project_name
if [ ! -z "$project_pid" ]; then
    if [ -f "$project_pid" ]; then
        if [ -s "$project_pid" ]; then
            echo "Existing PID file found during start."
                if [ -r "$project_pid" ]; then
                    PID=`cat "$project_pid"`
                    ps -p $PID >/dev/null 2>&1
                    if [ $? -eq 0 ] ; then
                        echo "$project_name appears to still be running with PID $PID. Start aborted."
                        exit 1
                    else
                        echo "Removing/clearing stale PID file."
                        rm -f "$project_pid" >/dev/null 2>&1
                        if [ $? != 0 ]; then
                            if [ -w "$project_pid" ]; then
                                cat /dev/null > "$project_pid"
                            else
                                echo "Unable to remove or clear stale PID file. Start aborted."
                                exit 1
                            fi
                        fi
                    fi
                else
                    echo "Unable to read PID file. Start aborted."
                    exit 1
                fi
        else
            rm -f "$project_pid" >/dev/null 2>&1
            if [ $? != 0 ]; then
                if [ ! -w "$project_pid" ]; then
                    echo "Unable to remove or write to empty PID file. Start aborted."
                    exit 1
                fi
            fi
        fi
    fi
fi



for f in $ROMAR_HOME/romar-core-*.jar; do
    CLASSPATH=$CLASSPATH:$f;
done


for f in $ROMAR_HOME/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

nohup java -server -Xmn512M -Xmx1G -Xms1G -XX:PermSize=64M -XX:MaxPermSize=64m \
    -XX:+UseParallelGC -XX:+UseParallelOldGC   \
    -Dromar.home=$ROMAR_HOME \
    -Dlog4j.configuration=file://$ROMAR_HOME/conf/log4j.xml \
    -Dromar.config=$ROMAR_HOME/conf/romar.yaml \
    -cp $CLASSPATH \
    com.anjuke.romar.http.jetty.RomarRESTMain >$ROMAR_HOME/logs/romar.out 2>&1 &

PID=$!
echo $PID > $project_pid
echo "start $project_name success"

