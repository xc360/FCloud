#日志路径
logPath="/home/log"
#用户传入的配置路径
if [ -n "$1" ]; then
    logPath="$1/"
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

#-----------------------------获取日志目录-----------------------------
#获取日志输出目录
logPath=$logPath"/"${JAR%.jar*}"/"
logFile=${JAR%.jar*}".log"
if [ -f $logPath$logFile ]; then
	echo "日志文件存在！"
else
	mkdir $logPath
	touch $logPath$logFile
	echo "创建日志文件！"
fi

#-----------------------------日志输出-----------------------------
#sleep 3s
tail -f $logPath$logFile
