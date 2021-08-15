#!/usr/bin/env python
"""Extract events from kafka and write them to hdfs
"""
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

def main():
    """main
    """
    spark = SparkSession \
        .builder \
        .appName("ExtractEventsJob") \
        .enableHiveSupport() \
        .getOrCreate()

    raw_events = spark \
        .readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "kafka:29092") \
        .option("subscribe", "events") \
        .load()
    
    # For Sword Purchases
    sword_purchases = raw_events \
        .filter(is_sword_purchase(raw_events.value.cast('string'))) \
        .select(raw_events.value.cast('string').alias('raw_event'),
                raw_events.timestamp.cast('string'),
                from_json(raw_events.value.cast('string'),
                          events_schema()).alias('json')) \
        .select('raw_event', 'timestamp', 'json.*')
    
    # For Joining Guilds
    guilds_joined = raw_events \
        .filter(is_guild_join(raw_events.value.cast('string'))) \
        .select(raw_events.value.cast('string').alias('raw_event'),
                raw_events.timestamp.cast('string'),
                from_json(raw_events.value.cast('string'),
                          events_schema()).alias('json')) \
        .select('raw_event', 'timestamp', 'json.*')
    
    # SQL For Sword Purchases
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
    
    # SQL For Guilds Joined
    spark.sql("drop table if exists join_guilds")
    join_guild_sql_string = """
        create external table if not exists join_guilds (
            raw_event string,
            timestamp string,
            Accept string,
            Host string,
            `User-Agent` string,
            event_type string
            )
            stored as parquet
            location '/tmp/join_guilds'
            tblproperties ("parquet.compress"="SNAPPY")
            """
    spark.sql(join_guild_sql_string)
    
    # Sinks for Swords
    sword_sink = sword_purchases \
        .writeStream \
        .format("parquet") \
        .option("checkpointLocation", "/tmp/checkpoints_for_sword_purchases") \
        .option("path", "/tmp/sword_purchases") \
        .trigger(processingTime="10 seconds") \
        .start()
    
    # Sinks for Joining Guilds
    guild_sink = guilds_joined \
        .writeStream \
        .format("parquet") \
        .option("checkpointLocation", "/tmp/checkpoints_for_join_guilds") \
        .option("path", "/tmp/join_guilds") \
        .trigger(processingTime="10 seconds") \
        .start()
    
    sword_sink.awaitTermination()
    guild_sink.awaitTermination()


if __name__ == "__main__":
    main()
