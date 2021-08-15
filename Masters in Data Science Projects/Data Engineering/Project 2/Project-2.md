# Project 2: Tracking User Activity - ETL Data Pipeline

In this project, you work at an ed tech firm. You've created a service that delivers assessments, and now lots of different customers (e.g., Pearson) want to publish their assessments on it. You need to get ready for data scientists who work for these customers to run queries on the data. 

#### Tasks

Prepare the infrastructure to land the data in the form and structure it needs to be to be queried.  You will need to:

- Publish and consume messages with Kafka
- Use Spark to transform the messages. 
- Use Spark to transform the messages so that you can land them in HDFS

#### Requirement Files for this Project:

- Your history file

- A report either as a markdown file or a jupyter notebook.
  The report should describe your queries and spark SQL to answer business questions
  that you select. Describe your assumptions, your thinking, what the parts of the
  pipeline do. What is given? What do you set up yourself? Talk about the data.
  What issues do you find with the data? Do you find ways to solve it? How?
  If not describe what the problem is.

- Any other files needed for the project, e.g., your docker-compose.yml, scripts used, etc

- Make sure you have a good structure in your git repo and explain your repo in the README.

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

Cloudera is a data cloud platform that distributes tools for data engineering and analysis such as Hadoop. We connect to the image `midsw205/cdh-minimal` and getting the latest version and expose the ports needed to access Hadoop tools:

```
  cloudera:
    image: midsw205/cdh-minimal:latest
    expose:
      - "8020" # nn
      - "50070" # nn http
      - "8888" # hue
```

#### Spark

Spark is a service used for data processing. With Spark, we can access tools such as pyspark and spark SQL. We grab the image `midsw205/spark-python` using version 0.0.5. We use `stdin_open` and `tty` to open a shell within the container and have the ability to interact on the command line. We also use `volumes` to mount and bind the host system to the contaainer in order to give it read and write access on the host system. In this case we are binding out home directory `~/w205` with the contiainer in `/w205`. We use `command: bash` to open up a shell. Furthermore, we establish the connection between spark and cloudera when running docker by using `depend_on` and `HADOOP_NAMENODE`:

```
  spark:
    image: midsw205/spark-python:0.0.5
    stdin_open: true
    tty: true
    volumes:
      - ~/w205:/w205
    command: bash
    depends_on:
      - cloudera
    environment:
      HADOOP_NAMENODE: cloudera
```

#### Mids

Our last service we want to connect to is mids. This service is created by UC Berkeley and has tools such as kafkacat that make it easy to write command lines. Here we also open up a shell and create the bind with our home directory and the container:

```
  mids:
    image: midsw205/base:latest
    stdin_open: true
    tty: true
    volumes:
      - ~/w205:/w205
```

## Loading Data into Kafka

In this section, we will be tackling the event logs and queing them using Kafka to prepare it for the Streaming Context in terms of our Data Pipeline. To do this, we will collect the data, visualize its components, run our revelant containers using Docker-Compose, and load the data into a Kafka Topic.

First, we load the data provided to us using the curl command. This is a JSON file of different types of assessments taken by different users, the questions, results, scores, attempts, etc. This file is kept in our Project 2 Directory:

```
curl -L -o assessment-attempts-20180128-121051-nested.json https://goo.gl/ME6hjp
```

Next, we use our docker file called `docker-compose.yml` to spin up Docker and run the relevant containers outlined in our file. This includes but is not limited to zookeeper, kafka, and mids. ZooKeeper, in conjunction with Kafka, allows us to track the status of nodes in Kafka clusters and maintain a list of Kafka topics and messages. Meanwhile, Kafka provides us with a framework for storing, reading and analysing streaming data. The MIDS container is used because it contains Kafkacat, an command-line friendly way to write and run kafka commands.

```
docker-compose up -d
```

Now, we want check to see the containers that are running in the context of our docker-compose.yml file:

```
docker-compose ps
```

Everything seems to be running smootly. For sanity check, we want to check and make sure kafka is also running. We use logs to get the messages and grep for regular expressions:

```
docker-compose logs kafka | grep -i started
```

Everything seems to be good. Now before we load the data into Kafka, we want to conduct some exploration on the data so we use `cat`, a command that allows us to create files, view contents of file, concatenate files and redirect output in terminal or files. With cat, we want to read the JSON file using `jq`, a command that helps us process JSON files. We use this to filter out the first data record in the JSON file (condensed):

```
cat assessment-attempts-20180128-121051-nested.json | jq '.[0]' -c
```

Here, we can see the components of the first data record. This includes features such as: timestamp, user exam ID, Exam Name, etc. We check to see how many data records are in our JSON file. When we run this we see there are 3,280 records.

```
cat assessment-attempts-20180128-121051-nested.json | jq '.[]' -c | wc -l
```

Now, we want to prepare our Kafka Topic to queue our data. To do this, we use docker-compose to execute our Kafka container and create a Kafka topic in order to group the data together that we need. We create a topic called `assessments` using `—-create —-topic` and if the topic does not exist then we create it. Naming it assesssments will allow others on this project who want to access the data undestand the context of our data. Next, we establish a connection with zookeeper on port 32181. An output “Created topic assessment” should tell us that it was successfully created.

```
docker-compose exec kafka kafka-topics --create --topic assessments --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zookeeper:32181
```

Next, we want to use kafkacat, found in the mids container, to load the data. Therefore, we execute the mids container, access the shell, and send a command line using bash -c while including the command line message in quotes. 

This message uses `cat` to open our JSON datafile. Then it uses jq to to load each data record in the JSON file as one message in Kafka. Next, we use kafkacat to enter into Kafka producer mode `-P` in order to publish a message. We want to go into the bootstrap server `b` in Kafka using the Kafka host server on port 29092. Then we tell Kafka the topic to use with `-t`. In this case, it is `assessments` and use echo to print a message to make sure everything ran smoothly:

```
docker-compose exec mids bash -c "cat /w205/project-2-Elias326/assessment-attempts-20180128-121051-nested.json | jq '.[]' -c | kafkacat -P -b kafka:29092 -t assessments && echo 'Uploaded JSON file.'"
```

Next, we want to consume the message and make sure it was successfully loaded into our Kafka topic, assessments. Therefore, we use a similar command, but this time using Kafkacat’s consumer mode (-C). It reads from the topic `-t` assessments, offset `-o`, and exit `-e` when done. We us `wc -l` to count the number of lines and make sure it matches our original data.

```
docker-compose exec mids bash -c "kafkacat -C -b Kafka:29092 -t assessments -o beginning -e" | wc -l
```

The output of the number of lines is 3281, which is perfect. Our original data is 3280 items and Kafka adds an addition message at the end of our data.

## Streaming Context: Transforming Messages with Spark

In this section, we will be working within the Streaming Context in terms of our Data Pipeline. To do this, we will read the data we placed in our Kafka topic, transform the data into tables using Spark, and prepare it for our Distributated Storage.

First, we run Spark by executing the spark container and using the pyspark tool:

```
docker-compose exec spark pyspark
```

Then, we load our data into a variable in Spark. We start with `read` to read our file in batch mode from kafka. We connect to the kafka server on the port number 29092 as mentioned in our `docker-compose.yml` file. Then we subscribe to our topic assessments where we currently have the data, collect it from the earliest to the latest, and load it:

```
raw_assessments = spark \
  .read \
  .format("kafka") \
  .option("kafka.bootstrap.servers", "kafka:29092") \
  .option("subscribe","assessments") \
  .option("startingOffsets", "earliest") \
  .option("endingOffsets", "latest") \
  .load() 
```

Then, we cache our data so that we keep close to work with by doing `raw_assessments.cache()`.

Next, we want to print the schema to see a description of the data. To do this, we run `raw_assessments.printSchema()`. We see that it contains keys, values, topics, partition, offset, timestamp, etc. When doing `raw_assessments.show()` it gives us the first 20 rows. However, this is all meta information about kafka. We want the data; therefore, in order to get it out of that format, we select our data and cast our values (which are in binary) into strings:

```
assessments = raw_assessments.select(raw_assessments.value.cast('string'))
```

This only provides us a Dataframe with a single column called value where each row is a line in our JSON file. We want to unroll the data, so we use `rdd.map` from Spark and provide it with a function that loads each line in our `assessments` Dataframe and unrolls the JSON data record into a Dataframe that we can run SQL queries on:

```
import json
from pyspark.sql import Row
extracted_assessments = assessments.rdd.map(lambda x: Row(**json.loads(x.value))).toDF()
```

However, when we run `extracted_assessments.show()`, we see that the values are stored in a Map object. Furthermore, when we run `extracted_assessments.printSchema()`, we can see that everything is nested and in an unstructured NoSQL format. To put in a SQL format to run queries on, we use the tool Spark SQL. First, we create a temporary table called `assessments` using the command below and run the following query to view the first 10 rows:

```
extracted_assessments.registerTempTable('assessments')
assessments = spark.sql("select * from assessments")
assessments.show()
```

Here, we see a problem. Even though part of our data was successfully loaded into the temporary table, in the sequences column, we still have unstructured data (sequences >> questions >> options >> etc.) stored in a Map object. This makes it difficult to unroll. Therefore, due to the complexity of this unstructured data, we will not solve this problem. Instead, we will simply keep the other features we believe are relevant for queries from our data (e.g. user exam id) as outlined below:

```
assessments = spark.sql("select started_at, user_exam_id, exam_name from assessments")
assessments.show()
```

## Example Queries

To showcase the limited potential of this dataset, we will run some example queries that provide some information about these exams taken.

First, we would like to see what is the top 3 most popular course taken. So we group by the exam name, count the number of exams taken, and order them in descending order. The query and result is show below:

```
spark.sql("select exam_name, count(exam_name) from assessments group by exam_name order by count(exam_name) desc limit 3").show()
```

| exam_name              | count(exam_name) |
|------------------------|------------------|
| Learning Git           | 394              |
| Introduction to Python | 162              |
| Introduction to Java 8 | 158              |

Next, we would like to see how many students took Normal Forms and All That Jazz Master Class, for example. We do this by utlizing the `where` clause for SQL and get the result below:

```
spark.sql("select exam_name, count(exam_name) from assessments where exam_name='Normal Forms and All That Jazz Master Class' group by exam_name").show()
```

| exam_name                                   | count(exam_name) |
|---------------------------------------------|------------------|
| Normal Forms and All That Jazz Master Class | 7                |

## Loading Data into HDFS

The last part of our Data Engineering Pipeline is to load our assessments data in Spark onto our Distributed Storage: Hadoop File System (HDFS). To do this, we simply use the `write.parquet` command and provide it with a location on Cloudera:

```
assessments.write.parquet("/tmp/assessments")
```

Next, we open a new terminal with docker-compose running and execute our cloudera service to make sure our data was loaded onto Hadoop. We use the `hadoop fs` to access HDFS and list out the containers in our /tmp/ folder:

```
docker-compose exec cloudera hadoop fs -ls /tmp/
```

After running this we successfully see 3 existing items: hive, hadoop-yarn, and assessments. We can see assessments was successfully create and hive and hadoop-yarn are used to access tools in our distributed storage.

Finally, we check our assessments container in a human-readable format using `-h`:

```
docker-compose exec cloudera hadoop fs -ls -h /tmp/assessments
```

And we finally see our data was successfully when we see `_SUCCESS`and `snappy.parquet` loaded with a size of 158.0 KB.
