package streaming.driverscripts

import java.util.regex.Matcher

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import util.Regex

import scala.util.Try

/**
  * Parse incoming web server logs and notify when there is an error
  *
  * to run this example:
  *
  * nc -kl 1234 < apache_webserver.log
  */
object ErrorNotifier {

  def main(args: Array[String]): Unit = {

    // ssc with a 1s batch interval
    val ssc = new StreamingContext("local[*]", getClass.getName, Seconds(1))

    // regex pattern
    val pattern = Regex.apacheLogPattern()

    // open a socket stream listening on port 1234
    val lines = ssc.socketTextStream("localhost", 1234, StorageLevel.MEMORY_AND_DISK_SER)

    val statusCodes = lines.map(x => {
      val matcher: Matcher = pattern.matcher(x)
      if (matcher.matches()) {
        matcher.group(6)
      } else {
        "[error]"
      }
    })

    val successOrFailure = statusCodes.map(x => {
      val code = Try(x.toInt).getOrElse(0)
      if (code >= 200 && code < 300) {
        "Success"
      } else if (code >= 400 && code < 600) {
        "Failure"
      } else {
        "Other"
      }
    })

    // aggregate code counts over a 5 minute window sliding every 1s
    val statusCodeCounts = successOrFailure.countByValueAndWindow(Seconds(300), Seconds(1))


    // For each batch, get the RDD's representing data from our current window
    statusCodeCounts.foreachRDD((rdd, time) => {
      // Keep track of total success and error codes from each RDD
      var totalSuccess:Long = 0
      var totalError:Long = 0

      if (rdd.count() > 0) {
        val elements = rdd.collect()
        for (element <- elements) {
          val result = element._1
          val count = element._2
          if (result == "Success") {
            totalSuccess += count
          }
          if (result == "Failure") {
            totalError += count
          }
        }
      }

      // Print totals from current window
      println("Total success: " + totalSuccess + " Total failure: " + totalError)

      // notify if total is above a threshold
      if (totalError + totalSuccess > 100) {
        // Compute the error rate
        // Note use of util.Try to handle potential divide by zero exception
        val ratio:Double = Try(totalError.toDouble / totalSuccess.toDouble).getOrElse(1.0)
        // If there are more errors than successes, wake someone up
        if (ratio > 0.5) {
          println("Too many errors!!!! Wake somebody up!")
        } else {
          println("All systems go.")
        }
      }
    })

    // Kick it off
    ssc.checkpoint("/hdfs/or/s3/checkpoint")
    ssc.start()
    ssc.awaitTermination()

  }

}
