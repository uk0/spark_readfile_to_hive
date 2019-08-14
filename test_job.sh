#!/usr/bin/env bash

sudo -u hdfs hadoop fs -rmr hdfs://cdh3:8020/temp_db


sudo -u hdfs /home/spark-2.4.0-bin-hadoop2.7/bin/spark-submit \
--class com.task.spark.OrcProcessTaskV1 \
--master local  ORC2PREQUET-1.0-SNAPSHOT.jar \
hdfs://cdh3:8020/orc/ods_sqq_sys_user/ \
hdfs://cdh3:8020/temp_db \
temp_ods_sqq_sys_user \
ods_sqq_sys_user