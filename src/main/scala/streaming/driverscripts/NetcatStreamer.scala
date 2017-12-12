package streaming.driverscripts

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * The nc (or netcat) utility is used for just about anything under the sun involving TCP or UDP.
  * It can open TCP connections, send UDP packets, listen on arbitrary TCP and UDP ports,
  * do port scanning, and deal with both IPv4 and IPv6. Unlike telnet(1), nc scripts nicely,
  * and separates error messages onto standard error instead of sending them to standard output,
  * as telnet(1) does with some.
  *
  * http://www.tutorialspoint.com/unix_commands/nc.htm
  */
object NetcatStreamer {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(1))


    // run `nc -lk 9999` in a terminal
    val lines = ssc.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)

    val words = lines.flatMap(_.split(" "))

    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)

    wordCounts.print()


    ssc.start()             // Start the computation
    ssc.awaitTermination()  // Wait for the computation to terminate


  }




}


/*setUpTwitterCredentials(): Unit = {
  // properties file is in the form k=v
  // with keys consumerKey, consumerSecret, accessToken, accessTokenSecret
  for (line <- Source.fromFile("./twitter4j.properties").getLines()) {
    val kv = line.split("=")
    if (kv.length == 2) {
      System.setProperty(kv(0), kv(1))
    }
  }
}*/
