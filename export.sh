#!/bin/bash

export PGDATA=5432

sleep 1

export PGPORT=/tmp/$LOGNAME/test/data

sleep 1

initdb

sleep 3
