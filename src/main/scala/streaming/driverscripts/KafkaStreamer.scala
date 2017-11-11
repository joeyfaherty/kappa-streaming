package streaming.driverscripts

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaStreamer {

  def main(args: Array[String]): Unit = {

    val logger: Logger = Logger.getLogger(getClass.getName)

    // ensure only error messages are logged
    Logger.getLogger("org").setLevel(Level.ERROR)

    val ssc = new StreamingContext("local[*]", "AppName", Seconds(5))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "com.joey",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topicName = "my-topic"
    val topics = List(topicName).toSet

    logger.info(s"Creating DStream of kafka events consuming from topic [$topicName]")
    val stream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
    )

    // do transformations on stream

    stream.foreachRDD { rdd =>
      val collected = rdd.map(record => ( record.key(), record.value() )).collect()
      for ( c <- collected )  {
        println(c)
      }
    }


    val checkpointDir = "/tmp/checkpoint"
    logger.info(s"Creating checkpoint directory [$checkpointDir]")
    ssc.checkpoint(checkpointDir)
    ssc.start()
    ssc.awaitTermination()

  }

}
