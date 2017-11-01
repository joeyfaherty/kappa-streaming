import sbt.StdoutOutput

name := "kappa-streaming"

version := "0.1"

scalaVersion := "2.11.7"

val sparkVersion = "2.1.0"

libraryDependencies ++= Seq(
  // no need to package spark-core, spark-sql into jar file as we know they will be pre-installed on the cluster
  "org.apache.spark" %% "spark-core" % sparkVersion /*% "provided"*/ exclude("org.scalatest", "scalatest_2.11"),
  "org.apache.spark" %% "spark-sql" % sparkVersion /*% "provided"*/ ,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-streaming-kafka" % sparkVersion,
  "org.twitter4j" % "twitter4j-core" % "4.0.6",
  "org.twitter4j" % "twitter4j-stream" % "4.0.6",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

fork in run := true
javaOptions in run ++= Seq(
  "-Dlog4j.debug=true",
  "-Dlog4j.configuration=log4j.properties")
outputStrategy := Some(StdoutOutput)