#!/bin/bash

pg_ctl -o "c unix_socket_directories=/tmp/$LOGNAME/sockets" -D /tmp/$LOGNAME/test/data stop

sleep 3

pg_ctl stop

sleep 3
