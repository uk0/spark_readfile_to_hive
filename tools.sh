#!/usr/bin/env bash
basepath=$(cd `dirname $0`; pwd)


function start_hadoop_spark() {
    cd /usr/local/Cellar/hadoop/3.1.1/sbin && sh start-all.sh
    cd /Users/zhangjianxin/spark/spark-2.4.0-bin-hadoop2.7/sbin && sh start-master.sh
    # start works
    spark-class org.apache.spark.deploy.worker.Worker spark://zhangjianxindeMacBook-Pro-2.local:7077
}


function stop_hadoop_spark() {
    cd /usr/local/Cellar/hadoop/3.1.1/sbin && sh stop-all.sh
    cd /Users/zhangjianxin/spark/spark-2.4.0-bin-hadoop2.7/sbin && sh stop-master.sh
    # stop works
    ps -ef | grep org.apache.spark.deploy.worker.Worker | awk '{print$2}' | xargs kill -9
}

case $1 in

"stop")
	stop_hadoop_spark
	;;
"start")
	start_hadoop_spark
	;;
*)
	echo -e "\033[1m usage: \n \t  [start | stop]  \n \t  [++++++++++] \n \t  [_hadoop_spark] \n \t  \033[0m"
	exit 2 # Command to come out of the program with status 1
	;;
esac
exit 0