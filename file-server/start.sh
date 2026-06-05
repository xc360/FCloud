#!/bin/sh

#-----------------------------基础配置-----------------------------
#配置jdk路径
JAVA=/home/service/jdk1.8.0_161/bin/
#JAVA=""

#jvm配置
#JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseParallelGC"
JAVA_OPTS="-Dfile.encoding=utf-8"

#-----------------------------项目读取配置文件路径-----------------------------
path="./" #默认配置路径
#用户传入的配置路径
if [ -n "$1" ]; then
    path="$1/"
fi

#-----------------------------jar包名称获取-----------------------------
JAR=""
#自动获取jar包包名,目录中只能有一个jar包
pkg_count=`find ./ -maxdepth 1 -name '*.jar' | wc -l` #获取jar包的个数
if [ $pkg_count -eq 1 ];then
    JAR=`find ./ -maxdepth 1 -name '*.jar' -or -name '*.war'| sed 's#.*/##' ` #获取war和jar的名称
else
    echo '[error]:目录中jar包超过两个,请手动指定如：sh start.sh ./ name.jar'
	exit
fi
#手动指定jar包地址
if [ -n "$2" ]; then
    JAR="$2"
fi

#-----------------------------项目启动-----------------------------
#进程id
PIDFILE=$path"service.pid"
#jar地址
ADDRESS=$path""$JAR
#判断服务是否启动
if [ -f $PIDFILE ]; then
    echo "服务已启动,请先关闭服务！"
	exit
else
    if [ -f $ADDRESS ]; then
		nohup $JAVA"java" $JAVA_OPTS -jar $ADDRESS --spring.config.location=$path >/dev/null 2>&1 &
        printf '%d' $! > $PIDFILE
        echo "服务启动中..."
    else
        echo "jar包不存在"
		exit
    fi
fi

