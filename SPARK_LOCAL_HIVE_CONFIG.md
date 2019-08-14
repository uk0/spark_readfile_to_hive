## 安装spark倒计群内部



### 将压缩包发送到集群上
```bash
scp spark-2.4.0-bin-hadoop2.7.tgz root@172.17.0.195:/home/
```
### 解压压缩包

```bash
# 登陆到服务器
# 进入scp的目录 解压 
# tar -zxvf spark-2.4.0-bin-hadoop2.7.tgz -C /home/spark-2.4.0
```

### 配置hdfs yarn hive
* 修改spark-env.sh

```bash
export SPARK_LAUNCH_WITH_SCALA=0
# 目录需要创建
export SPARK_LOG_DIR=/home/spark-2.4.0/log/spark
export SPARK_PID_DIR='/home/spark-2.4.0/run/spark/'

if [ -n "$HADOOP_HOME" ]; then
  export LD_LIBRARY_PATH=:/usr/lib/hadoop/lib/native
fi

export HADOOP_CONF_DIR=${HADOOP_CONF_DIR:-/etc/hadoop/conf}

if [[ -d $SPARK_HOME/python ]]
then
    for i in
    do
        SPARK_DIST_CLASSPATH=${SPARK_DIST_CLASSPATH}:$i
    done
fi

SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:$HADOOP_CONF_DIR"
SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:/usr/lib/hadoop/lib/*"
SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:/usr/lib/hadoop/*"
SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:/usr/lib/hadoop-hdfs/lib/*"
SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:/usr/lib/hadoop-hdfs/*"
SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:/usr/lib/hadoop-mapreduce/lib/*"
SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:/usr/lib/hadoop-mapreduce/*"
SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:/usr/lib/hadoop-yarn/lib/*"
SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:/usr/lib/hadoop-yarn/*"
SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:/usr/lib/hive/lib/*"
SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:/usr/lib/flume-ng/lib/*"
SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:/usr/lib/parquet/lib/*"
SPARK_DIST_CLASSPATH="$SPARK_DIST_CLASSPATH:/usr/lib/avro/lib/*"

```
* 将最新的 hive配置复制到spark/conf

```bash
##这里以hue的版本为准(未配置sentry之前)
cp /run/cloudera-scm-agent/process/XXX-hue-HUE_SERVER/hive-conf/hive-site.xml 

```

### 启动 master

* 启动 `master`

```bash
cd /home/spark-2.4.0/sbin && ./start-all.sh
```

### 测试spark-sql

```bash
cd /home/spark-2.4.0/bin && ./spark-sql --master spark://cdh1:7077
```
