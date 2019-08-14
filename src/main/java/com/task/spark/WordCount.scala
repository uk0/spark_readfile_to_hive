package com.task.spark

/**
  * @version 2019-02-12.
  * @url github.com/uk0
  * @project ORC2PREQUET
  * @since JDK1.8.
  * @author firshme
  */
import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("WordCount");
    val sc = new SparkContext(conf)

    val input = sc.textFile("/tmp/data/wordcount.txt")
    val lines = input.flatMap(line => line.split(" "))
    val count = lines.map(word => (word, 1)).reduceByKey { case (x, y) => x + y }

    val output = count.saveAsTextFile("/tmp/data/output")
  }

}