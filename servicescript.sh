#!/bin/sh

### BEGIN INIT INFO
# Provides:          bpmlsdaemon
# Required-Start:    $local_fs $network $syslog
# Required-Stop:     $local_fs $network $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: BPMLS Daemon
# Description:       This will start all services needed to run the BPMLS(including xAPI LRS server if possible) and then start the BPMLS
### END INIT INFO

BPMLS="bpmls"
LOCAL_MYSQL="localmysql"
LOCAL_MONGODB="localmongodb"
LRS="LRS"

VENKAT_HOME="/home/venkat"
LOCAL_PID="$VENKAT_HOME/pids"
PATH="/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin"
JAVA_HOME="$VENKAT_HOME/java"
PATH="$JAVA_HOME/bin:$PATH"

MYSQL_HOME="$VENKAT_HOME/mysqllocal"
BASE_DIR="$MYSQL_HOME"
DATADIR="$BASE_DIR/data"
MYSQLBIN="$MYSQL_HOME/bin/mysqld"





# Include functions 
set -e
. /lib/lsb/init-functions

start() {
  printf "Starting '$LOCAL_MYSQL'... \n"
  $MYSQL_HOME/bin/mysqld --user=root --datadir=$DATADIR --basedir=$MYSQL_HOME --log-error=$MYSQL_HOME/log/mysql.err  --pid-file=$MYSQL_HOME/mysql.pid --socket=$MYSQL_HOME/socket --port=24099 &
  printf "done\n"
}




stop() {
  printf "Stopping '$LOCAL_MYSQL'... "
  echo "\n"
  $MYSQL_HOME/bin/mysqladmin --user=root --socket=$MYSQL_HOME/socket shutdown -p
  printf "done\n"
}

status() {
  $MYSQL_HOME/bin/mysqladmin --socket=$MYSQL_HOME/socket -u root -p status
}

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
  status)
    status
    ;;
  *)
    echo $1
    exit 1
    ;;
esac

exit 0
