#!/bin/sh
path="./" #默认路径
if [ -n "$1" ]; then
	path=$1"/"
fi
sh $path"stop.sh"
sh $path"start.sh"