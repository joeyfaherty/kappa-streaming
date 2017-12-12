package streaming.driverscripts

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import twitter4j.{GeoLocation, Status}
import util.Twitter

/**
  * Use foreachRDD to manually dump each partition of the incoming stream to individual files
  *
  * .saveAsTextFiles() is also used.
  */
object SaveTweetsLocally {

  def main(args: Array[String]): Unit = {

    val logger: Logger = Logger.getLogger(getClass.getName)
    // ensure only error messages are logged
    //Logger.getLogger("org").setLevel(Level.ERROR)

    logger.info("Setting up twitter credentials to communicate with API")
    Twitter.setupCredentials()

    logger.debug("Creating a streaming context with a batch interval of 1 second")
    val ssc = new StreamingContext("local[*]", getClass.getName, Seconds(1)) // 1s batch interval

    logger.debug("Creating a DStream from Twitter using our ssc")
    val tweets: ReceiverInputDStream[Status] = TwitterUtils.createStream(ssc, None)

    logger.debug("Extract the status text and GeoLocation information from the tweet")
    val statusAndGeo: DStream[(String, GeoLocation)] = tweets.map(tweet => (tweet.getText, tweet.getGeoLocation))
                                                             .filter(x => x._2 != null)

    var totalTweets:Long = 0
    statusAndGeo.foreachRDD((rdd, time) => {
      // skip any empty rdd
      if (rdd.count() > 0) {
        // Combining each partition's results into a single RDD
        val repartitionedRdd: RDD[(String, GeoLocation)] = rdd.repartition(1).cache()
        // Create a text file for each RDD
        repartitionedRdd.saveAsTextFile("Tweet_and_geo" + time.milliseconds.toString)
        totalTweets += repartitionedRdd.count()
        if (totalTweets >= 2) {
          // Exiting system as max number of tweets has been reached
          System.exit(0)
        }
      }
    })

    ssc.checkpoint("/tmp")
    ssc.start()
    ssc.awaitTermination()
    
  }

}
