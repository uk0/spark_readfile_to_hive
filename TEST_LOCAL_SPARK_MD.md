
### Spark read orc write parquet


* 文件地址

```bash

hadoop fs -ls  /ods_bdw_bw_operate_voice_2
#
#/ods_bdw_bw_operate_voice_2/

```

## spark by local and hadoop



```bash
## 添加到spark-env.sh

export SPARK_DIST_CLASSPATH=$(/usr/local/Cellar/hadoop/3.1.1/bin/hadoop classpath)

./stop-master.sh
# start master
start-master.sh

# start works
spark-class org.apache.spark.deploy.worker.Worker spark://zhangjianxindeMacBook-Pro-2.local:7077

```