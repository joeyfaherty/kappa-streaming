package streaming.driverscripts

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaStreamer {

  def main(args: Array[String]): Unit = {

    val ssc = new StreamingContext("local[*]", "AppName", Seconds(1))

    val kafkaParams = Map("metadata.broker.list" -> "localhost:9092")

    val topics = List("topicA", "topicB").toSet

    val stream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
    )

    // do transformations on stream

    stream.print(5)

    ssc.checkpoint("/tmp/checkpoint")
    ssc.start()
    ssc.awaitTermination()

  }

}
