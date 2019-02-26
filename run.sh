
#server
#description: Auto-starts boot

Tag="syh_server_java"
PORT=8000
echo $Tag $PORT
RETVAL="0"

# See how we were called.
function start(){
    pid=$(ps -ef | grep -v 'grep' | grep $Tag | grep "$PORT" | awk '{printf $2 " "}')
    if [ "$pid" == "" ]; then
        nohup java -jar syh_server_java-0.0.1-SNAPSHOT.jar --spring.profiles.active=online-211-$PORT  >> catalina.out  2>&1 &
        fi
    status
}


function stop() {
    pid=$(ps -ef | grep -v 'grep' | grep $Tag | grep "$PORT" | awk '{printf $2 " "}')
    if [ "$pid" != "" ]; then
        echo -n "boot ( pid $pid) is running"
        echo
        echo -n $"Shutting down boot: "
        pid=$(ps -ef | grep -v 'grep' | grep $Tag | grep "$PORT" | awk '{printf $2 " "}')
        if [ "$pid" != "" ]; then
            echo "kill boot process"
            kill -9 "$pid"
        fi
        fi

    status
}

function status()
{
    pid=$(ps -ef | grep -v 'grep' | grep $Tag | grep "$PORT" | awk '{printf $2 " "}')
    #echo "$pid"
    if [ "$pid" != "" ]; then
        echo "boot is running,pid is $pid"
    else
        echo "boot is stopped"
    fi
}



function usage()
{
   echo "Usage: $0 {start|stop|restart|status}"
   RETVAL="2"
}

# See how we were called.
RETVAL="0"
case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        start
        ;;
    reload)
        RETVAL="3"
        ;;
    status)
        status
        ;;
    *)
      usage
      ;;
esac

exit $RETVAL


#task
# description: Auto-starts boot

Tag="syh_server_task"
Active="active=pro"
echo $Tag
echo $Active
RETVAL="0"

# See how we were called.
function start() {
    pid=$(ps -ef | grep -v 'grep' | grep $Tag | grep $Active | awk '{printf $2 " "}')
    if [ "$pid" == "" ]; then
        nohup java -jar syh_server_task-0.0.1-SNAPSHOT.jar --spring.profiles.active=pro  >> catalina.out  2>&1 &
        fi
    status
}


function stop() {
    pid=$(ps -ef | grep -v 'grep' | grep $Tag | grep $Active | awk '{printf $2 " "}')
    if [ "$pid" != "" ]; then
        echo -n "boot ( pid $pid) is running"
        echo
        echo -n $"Shutting down boot: "
        pid=$(ps -ef | grep -v 'grep' | grep $Tag | grep $Active | awk '{printf $2 " "}')
        if [ "$pid" != "" ]; then
            echo "kill boot process"
            kill -9 "$pid"
        fi
        fi

    status
}

function status()
{
    pid=$(ps -ef | grep -v 'grep' | grep $Tag | grep $Active | awk '{printf $2 " "}')
    #echo "$pid"
    if [ "$pid" != "" ]; then
        echo "boot is running,pid is $pid"
    else
        echo "boot is stopped"
    fi
}



function usage()
{
   echo "Usage: $0 {start|stop|restart|status}"
   RETVAL="2"
}

# See how we were called.
RETVAL="0"
case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        start
        ;;
    reload)
        RETVAL="3"
        ;;
    status)
        status
        ;;
    *)
      usage
      ;;
esac

exit $RETVAL