## *Streaming notes*

# Windowing concepts

Windowing example (top sellers for the last hour):
* Capture data every one second (batch interval)
* Process data every minute (slide interval)
* Maintain a window of one hour (window interval)


## Batch interval
* how often data is captured into a DStream
* Usually small interval (1s or 500ms). So that if there is a failure only 1s of data is lost.
* val ssc = new SparkContext("local", "my-app", Seconds(1)) // batch interval

## Slide interval
* how often a windowed transformation is computed
* In this example, the top sellers for the last hour would be updated every 1 minute in the UI. (slide interval)
* val hashtagCounts = hashtagKeyAndValue.reduceByKeyAndWindow(
(x,y) => x+y, (x,y) => x-y, Seconds(3600), Seconds(60)
)
* 60s is the 1 minute slide interval

## Window interval
* how far back in time the windowed transformation goes
* val hashtagCounts = hashtagKeyAndValue.reduceByKeyAndWindow(
(x,y) => x+y, (x,y) => x-y, Seconds(3600), Seconds(60)
)
* 3600s is the 1 hour window interval

# Fault Tolerance
* incoming data is replicated in at least 2 worker nodes
* use checkpoint directory for stateful data
** then you can restart from the checkpoint directpry

Receiver failure
* Use distributed, replicated receiver: eg. kafka, flume, hdfs etc
* Kafka: Can restart and reprocess from a certain point in history (offsets)


Driver Script failure
* Your driver node could be a SPOF (single point of failure)
* Use ssc.getOrCreate(checkpointDir)
* This will get a SSC from the checkpoint directory or create a new one if none exists. This will help failover scenarios
* Monitor the driver node for failure. Restart automatically on failure. Use --supervise on spark-submit
* Zookeeper/Spark Cluster manager will then restart the driver script


# Stateful Streaming
* You need to keep state across the stream. Eg. totals, averages etc.
* Group this stateful data by a key, eg. the session ID
* DStream.mapWithState()
* Takes incoming data
