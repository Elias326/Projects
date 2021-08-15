# Project 3: Understanding User Behavior

- You're a data scientist at a game development company.

- Your latest mobile game has two events you're interested in tracking: `buy a
  sword` & `join guild`

- Each has metadata characterstic of such events (i.e., sword type, guild name,
  etc)


## Tasks

- Instrument your API server to log events to Kafka

- Assemble a data pipeline to catch these events: use Spark streaming to filter
  select event types from Kafka, land them into HDFS/parquet to make them
  available for analysis using Presto. 

- Use Apache Bench to generate test data for your pipeline.

- Produce an analytics report where you provide a description of your pipeline
  and some basic analysis of the events. Explaining the pipeline is key for this project!

- Submit your work as a git PR as usual. AFTER you have received feedback you have to merge 
  the branch yourself and answer to the feedback in a comment. Your grade will not be 
  complete unless this is done!

Use a notebook to present your queries and findings. Remember that this
notebook should be appropriate for presentation to someone else in your
business who needs to act on your recommendations. 

It's understood that events in this pipeline are _generated_ events which make
them hard to connect to _actual_ business decisions.  However, we'd like
students to demonstrate an ability to plumb this pipeline end-to-end, which
includes initially generating test data as well as submitting a notebook-based
report of at least simple event analytics. That said the analytics will only be a small
part of the notebook. The whole report is the presentation and explanation of your pipeline 
plus the analysis!

---

## The Docker File

Docker-compose is a software that allows us to run mulitple containers in one service. In this project, we create a `docker-compose.yaml` file to connect to the following services needed for this project: zookeeper, kafka, cloudera, spark, and mids. In this section, we will explain the various elements of the docker file.

#### Zookeeper

Here, we list out our first service, Zookeeper, which allows us to manage Kafka. In the `image: confluentinc/cp-zookeeper:latest` section, we are asking docker to connect to the official confluent docker image for Zookeeper and get the latest version. Connecting to images allows us to get a set of instructions for creating a container that can run on the Docker platform. Our `ZOOKEEPER_CLIENT_PORT` is listening on port number `32181`. Meanwhile `ZOOKEEPER_TICK_TIME` is set to 2000 which allows us to manage cycles in sessions. Lastly, we `expose` the ports we are using to allow other containers in the same setup to connect to the zookeeper container as well:

```
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 32181
      ZOOKEEPER_TICK_TIME: 2000
    expose:
      - "2181"
      - "2888"
      - "32181"
      - "3888"
```

#### Kafka

Next, we list out our Kafka service, which helps us store, read and analyze streaming data. We tell Docker to spin up zookeeper first and then Kafka by stating `depends_on` and `zookeeper`. In our Kafka environment, we want it to have a broker ID of 1 and replication factor of 1. Meanwhile, we tell Docker to connect to the same zookeeper port on `32181` while running kafka on the port number `29092` using `KAFKA_ZOOKEEPER_CONNECT` and `KAFKA_ADVERTISED_LISTENERS`, respectively. We also `expose` the ports that are needed to run our Kafka service:

```
  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:32181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    expose:
      - "9092"
      - "29092"
```

#### Cloudera

Cloudera is a data cloud platform that distributes tools for data engineering and analysis such as Hadoop. We connect to the image `midsw205/hadoop:0.0.2` and getting the latest version. We expose the relevant ports to access the tools on Cloudera needed which includes Hadoop and Hive:

```
  cloudera:
    image: midsw205/hadoop:0.0.2
    hostname: cloudera
    expose:
      - "8020" # nn
      - "8888" # hue
      - "9083" # hive thrift
      - "10000" # hive jdbc
      - "50070" # nn http
        #    ports:
        #      - "8888:8888"
```

#### Spark

Spark is a service used for data processing. With Spark, we can access tools such as pyspark. We grab the image `midsw205/spark-python` using version 0.0.6. We use `stdin_open` and `tty` to open a shell within the container and have the ability to interact on the command line. We also use `volumes` to mount and bind the host system to the container in order to give it read and write access on the host system. In this case we are binding out home directory `~/w205` with the contiainer in `/w205`. 

Next, we expose the port 8888 and connect it to the host machine in case we want to run a Jupyter Notebook inside the host machine. Furthermore, We establish the connection between spark and cloudera when running docker by using `depend_on` and `HADOOP_NAMENODE` and specify `cloudera`. In addition, we add to our environment `HIVE_THRIFTSERVER` to give us access to the hive service which is on port 9083 and access via cloudera. Lastly, use `command: bash` to open up a shell:

```
  spark:
    image: midsw205/spark-python:0.0.6
    stdin_open: true
    tty: true
    volumes:
      - ~/w205:/w205
    expose:
      - "8888"
    ports:
      - "8888:8888"
    depends_on:
      - cloudera
    environment:
      HADOOP_NAMENODE: cloudera
      HIVE_THRIFTSERVER: cloudera:9083
    command: bash
```

#### Presto

Presto is a service that allows us to query data from our data storage in Hive. Here, we connect to the image `midsw205/presto` using version `0.0.1` and bind our home directory with the container using volumes. We expose the port 8080 which is the default port for presto. Lastly, we connect Presto with Hive and Cloudera by utilizing `HIVE_THRIFTSERVER: cloudera` on port 9083 (the port for Hive):

```
  presto:
    image: midsw205/presto:0.0.1
    hostname: presto
    volumes:
      - ~/w205:/w205
    expose:
      - "8080"
    environment:
      HIVE_THRIFTSERVER: cloudera:9083
```

#### MIDS

Our last service we want to connect to is mids. This service is created by UC Berkeley and has tools such as kafkacat that make it easy to write command lines. We connect to the image `midsw205/base` using the version `0.1.9`. Here we also open up a shell and create the bind with our home directory and the container. Lastly, we expose and connect to the host machine on port 5000 (where Flask will be running by default).

```
  mids:
    image: midsw205/base:0.1.9
    stdin_open: true
    tty: true
    volumes:
      - ~/w205:/w205
    expose:
      - "5000"
    ports:
      - "5000:5000"
```

## The App Server + Loading Data Into Kafka

In this section, we want to simulate the stream of data using a mock app game server. To do this, we utilize the Flask API, a tool that lets us build a web application. Flask will be the host for simulating a lot of the data we will want to collect. This data will eventually be loaded onto Kafka to prepare for our Streaming Context in our Data Pipeline. 

First we import the packages/services needed (in this case Flask, Kafka, and json). Then, we start the Flask instance using `app = Flask(__name__)`. In addition, we specify a Kafka Producer in order to produce and send messages to Kafka on port 29092:

```
import json
from kafka import KafkaProducer
from flask import Flask, request

app = Flask(__name__)
producer = KafkaProducer(bootstrap_servers='kafka:29092')
```

Then, we define a function that takes in a Kafka topic and event name and sends all the JSON data into Kafka. First, a request (e.g. purchase a sword or join a guild) is made and `.headers` allows us to collect the meta information about the request that is needed to make the protocol function (e.g. bytes, data size, protocol, etc.). Then, given our event name in Kafka, we use `.update` to add on the request headers. Using Kafka producer, we send our event to the topic and encode it in JSON readable format sending as a string in Kafka:

```
def log_to_kafka(topic, event):
    event.update(request.headers)
    producer.send(topic, json.dumps(event).encode())
```

We then begin to use the app by utilizing decorators. Before each event type, we use `@app.route` to change the behavior of a function. We use `"/"` to specify changes in the root directory. In order to not run into any 404 errors, we set a default response by creating an event type called "default" in JSON. By using our previous `log_to_kafka` function, we send this default message into Kafka and return a "Welcome to Legends of Zion!" message to the user:

```
@app.route("/")
def default_response():
    default_event = {'event_type': 'default'}
    log_to_kafka('events', default_event)
    return "Welcome to Legends of Zion!\n"
```

The same approach is used to specify other event types. In this case, we have the option for a user to purchase a sword, purchase a shield, join a guild, or purchase a potion. For example, for purchasing a sword, we send a get request in our path `/purchase_a_sword`. Then, we specify our event type and load it into Kafka:

```
@app.route("/purchase_a_sword")
def purchase_a_sword():
    purchase_sword_event = {'event_type': 'purchase_sword'}
    log_to_kafka('events', purchase_sword_event)
    return "Sword Purchased!\n"

@app.route("/purchase_a_shield")
def purchase_shield():
    purchase_shield_event = {'event_type': 'purchase_shield'}
    log_to_kafka('events', purchase_shield_event)
    return "Shield Purchased!\n"

@app.route("/join_guild")
def join_guild():
    join_guild_event = {'event_type': 'join_guild'}
    log_to_kafka('events', join_guild_event)
    return "Guild Joined!\n"

@app.route("/purchase_a_potion")
def purchase_potion():
    purchase_potion_event = {'event_type': 'purchase_potion'}
    log_to_kafka('events', purchase_potion_event)
    return "Potion Purchased!\n"
```

To run our Flask application, we simply use Docker on the command line, execute the mids container, and set the enviornment for Flask specified in the `game_api.py` file. Furthermore, we use flask run on the host address of `0.0.0.0`:

```
docker-compose exec mids env FLASK_APP=/w205/project-3-Elias326/game_api.py flask run --host 0.0.0.0
```

To test and see if Flask is running, we write the following command on another terminal. The output "Welcome to Legends of Zion!" lets us know that it ran successfully:

```
docker-compose exec mids curl http://localhost:5000/
```

## The Streaming Context

The next part in the process is to simulate and stream the data for our game. We want it to land in Kafka, then have it be processed using Spark, and land the data in HDFS. The chunk of the work happens in the code file `stream_and_hibe.py`.

For the first chunk of code, we import the necessary tools from pySpark. Then, we define a schema that will allow us to streamline the process:

```
import json
from pyspark.sql import SparkSession
from pyspark.sql.functions import udf, from_json
from pyspark.sql.types import StructType, StructField, StringType


def events_schema():
    """
    root
    |-- Accept: string (nullable = true)
    |-- Host: string (nullable = true)
    |-- User-Agent: string (nullable = true)
    |-- event_type: string (nullable = true)
    """
    return StructType([
        StructField("Accept", StringType(), True),
        StructField("Host", StringType(), True),
        StructField("User-Agent", StringType(), True),
        StructField("event_type", StringType(), True),
    ])
```

Next, we want to filter out the event types based off of sword purchases and joining guilds by loading the JSON event and searching where the string may equal to event type:

```
@udf('boolean')
def is_sword_purchase(event_as_json):
    """udf for filtering events
    """
    event = json.loads(event_as_json)
    if event['event_type'] == 'purchase_sword':
        return True
    return False

@udf('boolean')
def is_guild_join(event_as_json):
    """udf for filtering events
    """
    event = json.loads(event_as_json)
    if event['event_type'] == 'join_guild':
        return True
    return False
```

We then define a main method that conducts a majority of the streaming process. First we instantiate a SparkSession, giving it an application name, and enabling Hive to support.

```
    spark = SparkSession \
        .builder \
        .appName("ExtractEventsJob") \
        .enableHiveSupport() \
        .getOrCreate()
```
Next, we use our spark session to begin reading the stream from Kafka, connecting to the server on port 29092, subscribing to our `events` topic we created using Kafka, and loading the data:

```
    raw_events = spark \
        .readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "kafka:29092") \
        .option("subscribe", "events") \
        .load()
```

**Note:** In this example, I will be explaining the process for sword purchases event type. This can also be applied to the joining guilds event type.

For purchasing swords, for example, we take from the raw events data and filter the data to only sword purchases (using our previously defined function). Then, we take the value and transform/cast as a string type for easier processing. Then, we construct a table of each feature: raw_event, timestamp, and the value (which must be cast as a string). Lastly, we select those columns from the table and use our events schema:

```
    sword_purchases = raw_events \
        .filter(is_sword_purchase(raw_events.value.cast('string'))) \
        .select(raw_events.value.cast('string').alias('raw_event'),
                raw_events.timestamp.cast('string'),
                from_json(raw_events.value.cast('string'),
                          events_schema()).alias('json')) \
        .select('raw_event', 'timestamp', 'json.*')
```

We take it a step further and use Spark SQL to create an external table of sword purchases that we want to send HDFS. We collect information we would like such as: raw event, timestamp, host, user agent, event type, values, etc.:

```
    spark.sql("drop table if exists sword_purchases")
    sword_sql_string = """
        create external table if not exists sword_purchases (
            raw_event string,
            timestamp string,
            Accept string,
            Host string,
            `User-Agent` string,
            event_type string
            )
            stored as parquet
            location '/tmp/sword_purchases'
            tblproperties ("parquet.compress"="SNAPPY")
            """
    spark.sql(sword_sql_string)
```

Now, we want to send this data to HDFS. To do this, we write the stream using parquet and define a checkpoint, location, and path. Then, we trigger the processing to happen every 10 seconds. This process is assigned a variable called `swords_sink` to send the data to HDFS:

```
    sword_sink = sword_purchases \
        .writeStream \
        .format("parquet") \
        .option("checkpointLocation", "/tmp/checkpoints_for_sword_purchases") \
        .option("path", "/tmp/sword_purchases") \
        .trigger(processingTime="10 seconds") \
        .start()
```

Lastly, for each streaming process we must use `awaitAnyTermination()`, which waits until any of the queries on the associated SQLContext have terminated since the creation of the context. Since we have sword purchases and joining guilds, we use this method for both:

```
    sword_sink.awaitAnyTermination()
    guild_sink.awaitAnyTermination()
```

## Command Line Execution

To execute this streaming process and data collection, we run the following commands on seperate terminals for sword purchases:

**Running Docker:**

```
docker-compose up -d
```

**Creating a Kafka Topic:**
```
docker-compose exec kafka kafka-topics --create --topic events --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zookeeper:32181
```

**Running Flask:**
```
docker-compose exec mids env FLASK_APP=/w205/project-3-Elias326/game_api.py flask run --host 0.0.0.0
```

**Running Spark:**
```
docker-compose exec spark spark-submit /w205/project-3-Elias326/stream_and_hive.py
```

**Simulating Data:**
```
while true; do docker-compose exec mids ab -n 10 -H "Host: user1.comcast.com" http://localhost:5000/purchase_a_sword; sleep 10; done
```
or
```
bash generate_data.sh
```

**Checking Data Loading on HDFS:**
```
docker-compose exec cloudera hadoop fs -ls /tmp/sword_purchases
docker-compose exec cloudera hadoop fs -ls /tmp/join_guilds
```

## Simulating Using Apache Bench

In order to simulate the generation of data, we create a bash shell script to run command lines in a while loop. To do this, we use the mids container to access Apache Bench. For each command line, we generate 10 commands for each event type (purchase sword or join a guild). We also specify a host and make a web call to Flask:

```
#!/bin/bash

while true; do 
  docker-compose exec mids ab -n 10 -H "Host: user1.comcast.com" http://localhost:5000/
  docker-compose exec mids ab -n 10 -H "Host: user1.comcast.com" http://localhost:5000/purchase_a_sword
  docker-compose exec mids ab -n 10 -H "Host: user1.comcast.com" http://localhost:5000/join_guild
  docker-compose exec mids ab -n 10 -H "Host: user2.att.com" http://localhost:5000/
  docker-compose exec mids ab -n 10 -H "Host: user2.att.com" http://localhost:5000/purchase_a_sword
  docker-compose exec mids ab -n 10 -H "Host: user1.comcast.com" http://localhost:5000/join_guild
  sleep 10 
done
```

## Querying with Presto

After all the data has been landed in HDFS, we can use presto to load the data and run some queries that might provide us some insight about the data. Due to the limited information produced, we are only able to run a limited number of queries. First, we enter Presto using Docker:

```
docker-compose exec presto presto --server presto:8080 --catalog hive --schema default
```

After running Presto, we can begin to check if our tables are present:

Command Line:
```
presto:default> show tables;
```

Output:

| Table            |
|------------------|
| ---------------- |
| join_guilds      |
| sword_purchases  |

Next, we want to understand the contents and columns described in our data:

```
presto:default> describe sword_purchases;
```

Output:

| Column     | Type     | Comment  |
|------------|----------|----------|
| --------   | -------- | -------- |
| raw_event  | varchar  |          |
| timestamp  | varchar  |          |
| accept     | varchar  |          |
| host       | varchar  |          |
| user-agent | varchar  |          |
| event_type | varchar  |          |

We can now take a look at all the data produced by querying it:

```
presto:default> select * from join_guilds;
```

Output (Example):

| raw_event                                                                                                   | timestamp               | accept   | host        |
|-------------------------------------------------------------------------------------------------------------|-------------------------|----------|-------------|
| --------                                                                                                    | --------                | -------- | --------    |
| {"Host": "user1.comcast.com", "event_type": "join_guild", "Accept": "*/*", "User-Agent": "ApacheBench/2.3"} | 2021-08-10 00:09:06.648 | */*      | user1.comca |
| ...                                                                                                         | ...                     | ...      | ...         |

Now suppose we wanted to know how many guilds have been joined. We can run a count and see the total number of guilds joined:

```
presto:default> select count(*) from join_guilds;
```

Output:

| _col0  |
|--------|
| ------ |
| 440    |

Lastly, we can check how many guilds have been joined by a particular host. We check this by using `count(*)` and `group by` on SQL to generate the information:

```
presto:default> select Host, count(*) as count from join_guilds group by Host;
```

Output:

| Host              | count     |
|-------------------|-----------|
| ----------------- | --------- |
| user1.comcast.com | 560       |




