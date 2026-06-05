#!/bin/sh
path="./" #默认路径
if [ -n "$1" ]; then
	path=$1"/"
fi
PIDFILE=$path"/service.pid"
if [ -f $PIDFILE ]; then
	kill -9 `cat $PIDFILE`
	rm -rf $PIDFILE
	echo "服务关闭中..."
else
	echo "服务已关闭,请先启动服务！"
fi
