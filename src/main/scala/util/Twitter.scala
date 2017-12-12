package util

import scala.io.Source

/**
  * Util functions for twitter
  */
object Twitter {

  /**
    * twitter4j.properties file expected in project root.  Must be custom per user.
    * See https://apps.twitter.com/
    *
    * properties file is in the form k=v
    * with keys consumerKey, consumerSecret, accessToken, accessTokenSecret
    */
  def setupCredentials(): Unit = {
    for (line <- Source.fromFile("./twitter4j.properties").getLines()) {
      val kv = line.split("=")
      if (kv.length == 2) {
        System.setProperty(kv(0), kv(1))
      }
    }
  }

}
