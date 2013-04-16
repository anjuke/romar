cd $(dirname $0)/
cd ..
ROMAR_HOME=`pwd`

project_name="romar"
project_pid="$ROMAR_HOME/logs/${project_name}.pid"

echo stopping $project_name
if [ ! -z "$project_pid" ]; then
    if [ -f "$project_pid" ]; then
        if [ -s "$project_pid" ]; then
            kill -0 `cat "$project_pid"` >/dev/null 2>&1
            if [ $? -gt 0 ]; then
                echo "PID file found but no matching process was found. Stop aborted."
                exit 1
            fi
        else
            echo "PID file is empty and has been ignored."
        fi
    else
        echo "$project_pid file does not exist. Is $project_name running? Stop aborted."
        exit 1
    fi
fi
if [ -z "$project_pid" ]; then
    echo "Kill failed: \$project_pid not set"
else
    if [ -f "$project_pid" ]; then
        PID=`cat "$project_pid"`
        echo "Killing $project_name with the PID: $PID"
        kill $PID
        echo "Waiting $project_name stopping"
        while kill -0 $PID 2>/dev/null; do sleep 1; done
        echo "$project_name stopped"
        rm -f "$project_pid" >/dev/null 2>&1
        if [ $? != 0 ]; then
            echo "$project was killed but the PID file could not be removed."
        fi
    fi
fi