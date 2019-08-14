package com.task.spark

import org.apache.commons.beanutils.BeanUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

import scala.beans.BeanProperty


object OrcProcessTask {


  /**
    *
    * `id` bigint,
    * `trade_type` int,
    * `trade_time` varchar(20),
    * `call_time` varchar(20),
    * `trade_addr` varchar(50),
    * `receive_phone` varchar(20),
    * `call_type` int,
    * `borrower_id` bigint,
    * `update_time` string
    **/
  class OdsTest() {
    @BeanProperty
    var id = 0L; //必须初始化字段
    @BeanProperty
    var trade_time = ""; //必须初始化字段
    @BeanProperty
    var trade_type = 0; //必须初始化字段
    @BeanProperty
    var call_time = ""; //必须初始化字段
    @BeanProperty
    var borrower_id = 0L; //必须初始化字段
    @BeanProperty
    var update_time = ""; //必须初始化字段
    @BeanProperty
    var receive_phone = ""; //必须初始化字段
    @BeanProperty
    var trade_addr = ""; //必须初始化字段

    def toStr(): String = {
      this.id + " " + this.trade_time + " " + this.trade_addr
    }
  }

  /**
    * 将数据写入hive demo分区表中
    *
    * @param rDD          封装Bean的数据集
    * @param sparkSession spark当前上下文
    */
  def saveToHive(rDD: RDD[OdsTest], sparkSession: SparkSession,saveParquetFilePath:String,tempTableName:String): Unit = {
    val df = sparkSession.createDataFrame(rDD, new OdsTest().getClass)
    df.createOrReplaceTempView(tempTableName)
    df.write.format("parquet").save(saveParquetFilePath)
  }

  /**
    * 从hdfs上直接读取hive分区文件
    *
    * @param sparkSession sparksission
    * @return
    */
  def getHive(sparkSession: SparkSession ,filePath:String) = {
//    val path = s"ods_bdw_bw_operate_voice_2/part-m-00000_a_17"
    val rdd = sparkSession.read.orc(filePath).rdd.map { row => {
      val ods = new OdsTest()
      ods.getClass.getDeclaredFields.foreach { case field => {
        val name = field.getName
        val schema = row.schema
        val schemaName = name.toLowerCase()
        if (schema.fields.map(_.name).contains(schemaName)) {
          val index = schema.fieldIndex(schemaName)
          BeanUtils.copyProperty(ods, name, row.get(index))
        }
      }
      }
      ods
    }
    }
    rdd
  }

  def main(args: Array[String]) {


    args.foreach(r=>{println(r)})

    if(args.length!=4){
      println("Usage:\r\n  [0]=orcFileDirorFile \r\n  [1]=saveParquetFilePath  \r\n  [2]=tempTableName  \r\n  [3]=intoTableName")
      return
    }


    val orcFileDirorFile = args(0)
    val saveParquetFilePath = args(1)
    val tempTableName = args(2)
    val intoTableName = args(3)
    val appName = tempTableName + "_orc_conversion_parquet.job"

    val conf = new SparkConf().setAppName(appName)
    val sc = new SparkContext(conf)

    val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)
    val spark = SparkSession
      .builder()
      .appName(appName)
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "/home/hdfs/spark-warehouse")
      .enableHiveSupport()
      .getOrCreate()


    var rData = OrcProcessTask.getHive(spark,orcFileDirorFile);

    OrcProcessTask.saveToHive(rData, spark,saveParquetFilePath,tempTableName)


    val test = sqlContext.read.format("parquet").load(saveParquetFilePath)
    test.registerTempTable(tempTableName)
    spark.sql(s"SELECT * FROM demo.${intoTableName} limit 10").foreach(data => {
      println(data)
    })

    sqlContext.sql(s"create table demo.${intoTableName} as select * from ${tempTableName}")
    sqlContext.sql(s"insert into table ${intoTableName} select * from ${tempTableName}")
    sqlContext.sql(s"insert overwrite table ${intoTableName} select * from ${tempTableName}")

//                                         ${intoTableName} ${tempTableName}
    sc.stop()
  }

}

