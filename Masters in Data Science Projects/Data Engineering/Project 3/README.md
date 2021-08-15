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

#### Files

This repository includes the following files and their purpose:

- **Project-3.md:** A report for Project 3. This Markdown file includes an introduction, explanation of each line in the docker-compose.yml file, the data engineering pipeline, and explanation of each command line utilized to run this project successfully.

- **docker-compose.yml:** The Docker file to run relevant services and containers needed to execute this project.

- **generate_data.sh:** Bash Shell File to run commands that allow for the generation of data using Flask.

- **stream_and_hive.py:** Python script that creates a Spark session, filters and casts data for processing, and streamlines it to land into HDFS.

- **game_api.py:** Python Script that uses Flask to simulate the creation of events and sends the data to Kafka.
