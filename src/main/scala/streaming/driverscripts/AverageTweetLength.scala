package streaming.driverscripts

import java.util.concurrent.atomic.AtomicLong

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import twitter4j.{GeoLocation, Status}
import util.Twitter

object AverageTweetLength {

  def main(args: Array[String]): Unit = {

    val logger: Logger = Logger.getLogger(getClass.getName)
    // ensure only error messages are logged
    Logger.getLogger("org").setLevel(Level.ERROR)

    logger.info("Setting up twitter credentials to communicate with API")
    Twitter.setupCredentials()

    logger.debug("Creating a streaming context with a batch interval of 1 second")
    val ssc = new StreamingContext("local[*]", getClass.getName, Seconds(1)) // 1s batch interval

    logger.debug("Creating a DStream from Twitter using our ssc")
    val tweets: ReceiverInputDStream[Status] = TwitterUtils.createStream(ssc, None)

    logger.debug("Extract the status text and GeoLocation information from the tweet")
    val tweetLength: DStream[(Long)] = tweets.map(tweet => (tweet.getText.length))

    var totalTweets = new AtomicLong(0)
    var totalTweetLength = new AtomicLong(0)

    tweetLength.foreachRDD((rdd, time) => {
      // skip any empty rdd
      val rddCount: Long = rdd.count()
      if (rddCount > 0) {
        totalTweets.getAndAdd(rddCount)
        totalTweetLength.getAndAdd(rdd.reduce((x,y) => x + y))

        var totalTweetsCurrent: Long = totalTweets.get()
        var totalTweetsLengthCurrent: Long = totalTweetLength.get()
        var currentAverage: Long = totalTweetsLengthCurrent / totalTweetsCurrent

        println(s"total tweets: $totalTweetsCurrent\ntotal chars: $totalTweetsLengthCurrent\naverage: $currentAverage")

      }
    })

    ssc.checkpoint("/tmp")
    ssc.start()
    ssc.awaitTermination()

  }

}

