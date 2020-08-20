pg_ctl -o "-c unix_socket_directories=/tmp/$LOGNAME/sockets" -D $PGDATA -l /tmp/$LOGNAME/logfile start
