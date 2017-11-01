package streaming.driverscripts

import kafka.serializer.StringDecoder
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaStreamer {

  def main(args: Array[String]): Unit = {

    val ssc = streamingContext()

    val kafkaConfig = Map("metadata.broker.list" -> "localhost:9092")

    val topics: Set[String] = List("topic1", "topic2").toSet

/*
    val lines = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaConfig, topics).map(_._2)
*/


  }

  def streamingContext(): Unit = {
    new StreamingContext("local[*]", "AppName", Seconds(1))
  }
}
