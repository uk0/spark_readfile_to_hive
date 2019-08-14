#!/usr/bin/env bash

spark-submit --class com.gemantic.bigdata.WordCount --master yarn-cluster --executor-memory 512m /data/bigdata/spark/lib/bigdata-spark-1.0-SNAPSHOT.jar 10

spark-submit --class MySpark --master spark://spark1:7077 /Users/yangyibo/Idea/mySpark/out/artifacts/mySpark_jar/mySpark.jar

spark-submit --class main.scala.com.matthewrathbone.spark.Main --master local ./target/scala-spark-1.0-SNAPSHOT-jar-with-dependencies.jar /path/to/transactions_test.txt /path/to/users_test.txt /path/to/output_folder







spark-submit --class com.task.spark.OrcProcessTask2 \
--master local  \
target/ORC2PREQUET-1.0-SNAPSHOT.jar \
hdfs:///ods_bdw_bw_operate_voice_2/000007_0 \
hdfs:///test_db \
temptable1 \
intotable1



## 启动外置 spark
./start-master.sh
./spark-class org.apache.spark.deploy.worker.Worker spark://cdh1:7077

sudo -u hdfs /home/spark-2.4.0-bin-hadoop2.7/bin/spark-submit --class com.task.spark.OrcProcessTask \
--master local  \
ORC2PREQUET-1.0-SNAPSHOT.jar \
hdfs://cdh3:8020/orc/000007_0 \
hdfs://cdh3:8020/test_db \
temptable1 \
intotable1




sudo -u hdfs  /home/spark-2.4.0-bin-hadoop2.7/bin/spark-submit --class com.task.spark.OrcProcessTask \
--master local  \
ORC2PREQUET-1.0-SNAPSHOT.jar \
hdfs:///orc \
hdfs:///parquet \
temptable1 \
intotable1