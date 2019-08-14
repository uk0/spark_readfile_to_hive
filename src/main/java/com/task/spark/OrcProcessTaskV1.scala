package com.task.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SparkSession, _}
import org.apache.spark.{SparkConf, SparkContext}


object OrcProcessTaskV1 {

  /**
    * 将数据写入hive 区表中
    *
    * @param rDD          封装RDD[T]的数据集
    * @param sparkSession spark当前上下文
    *
    *      val schema = StructType(row.schema.fields) //根据模式字符串生成模式
    */
  def saveToHive(rDD: RDD[Row], sparkSession: SparkSession, saveParquetFilePath: String, tempTableName: String): Unit = {
    val df = sparkSession.createDataFrame(rDD, rDD.first().schema)
    df.createOrReplaceTempView(tempTableName)
    df.write.format("parquet").save(saveParquetFilePath)
  }

  /**
    * 从hdfs上直接读取hive分区文件
    *
    * @param sparkSession sparksission
    * @return
    */
  def getOrcFiles(sparkSession: SparkSession, filePath: String) = {
    val rdd = sparkSession.read.format("orc").load(filePath).rdd.map { row => {
      row
    }
    }
    rdd
  }

  def main(args: Array[String]) {

    var dt = ""
    args.foreach(r => {
      println(r)
    })

    if (args.length<=0) {
      println("Usage:\r\n  [0]=orcFileDirorFile \r\n  [1]=saveParquetFilePath  \r\n  [2]=tempTableName  \r\n  [3]=intoTableName")
      return
    }


    val orcFileDirorFile = args(0)
    val saveParquetFilePath = args(1)
    val tempTableName = args(2)
    val intoTableName = args(3)

    if (args.length==5){
      dt = args(4)
    }
    val appName = tempTableName + "_orc_conversion_parquet.job"

    val conf = new SparkConf().setAppName(appName)
    val sc = new SparkContext(conf)

    val spark = SparkSession
      .builder()
      .appName(appName)
      .master("local[*]")
        .config("spark.sql.warehouse.dir", "/home/hdfs/spark-warehouse")
      .enableHiveSupport()
      .getOrCreate()

    val sqlContext =new org.apache.spark.sql.hive.HiveContext(sc)
    var rData = OrcProcessTaskV1.getOrcFiles(spark, orcFileDirorFile);

    OrcProcessTaskV1.saveToHive(rData, spark, saveParquetFilePath, tempTableName)


    val test = sqlContext.read.format("parquet").load(saveParquetFilePath)
    test.createOrReplaceTempView(tempTableName)
//                 检查临时表
    sqlContext.sql(s"SELECT * FROM ${tempTableName} limit 10").foreach(data => {
      println(data)
    })
//                区分分区表
    if(!"".equals(dt)){
      sqlContext.sql(s"create table if not exists ods.${intoTableName} partitioned by (dt string) stored as parquet  as select * from ${tempTableName}")
      sqlContext.sql(s"insert overwrite table ods.${intoTableName}  partition (dt='${dt}') select * from ${tempTableName}")
    }else{
      sqlContext.sql(s"create table if not exists ods.${intoTableName} stored as parquet  as select * from ${tempTableName}")
      sqlContext.sql(s"insert into table ods.${intoTableName} select * from ${tempTableName}")
    }

//                检查倒入后的表
    sqlContext.sql(s"SELECT * FROM ods.${intoTableName}  limit 10").foreach(data => {
      println(data)
    })
//                检查倒入后的数量
    sqlContext.sql(s"SELECT * FROM ods.${intoTableName}").count()
    sc.stop()
  }

}

