# Project 2: Tracking User Activity

In this project, you work at an ed tech firm. You've created a service that delivers assessments, and now lots of different customers (e.g., Pearson) want to publish their assessments on it. You need to get ready for data scientists who work for these customers to run queries on the data. 

# Tasks

Prepare the infrastructure to land the data in the form and structure it needs to be to be queried. You will need to:

- Publish and consume messages with Kafka
- Use Spark to transform the messages. 
- Use Spark to transform the messages so that you can land them in HDFS

## Files

This repository includes the following files and their purpose:

- **Project-2.md:** A report for Project 2. This Markdown file includes an introduction, explanation of each line in the `docker-compose.yml` file, the data engineering pipeline, and explanation of each command line utilized to run this project successfully.

- **docker-compose.yml:** The Docker file to run relevant services and containers needed to execute this project.

- **history_files:** Includes all relavant command line history files:

    - **Elias326-history.txt:** This is the history file of the ALL command lines utilized in this project. This includes git, docker-compose, kafka, hadoop, and spark commands.

    - **Elias326-history-clean.txt:** This is also a cleaned up version of the history file with only selected command lines relevant to the project. This includes git, docker-compose, kafka, hadoop, and spark commands.

    - **Elias326-spark-history.txt:** This is a history file for all Spark commands for this project.

## Data

To get the data, run 
```
curl -L -o assessment-attempts-20180128-121051-nested.json https://goo.gl/ME6hjp`
```

Note on the data: This dataset is much more complicated than the one we'll
cover in the live sessions. It is a nested JSON file, where you need to unwrap it
carefully to understand what's really being displayed. There are many fields
that are not that important for your analysis, so don't be afraid of not using
them. The main problem will be the multiple questions field. Think about what the 
problem is here and give your thoughts. We recommend for
you to read schema implementation in Spark [Here is the documenation from
Apache](https://spark.apache.org/docs/2.3.0/sql-programming-guide.html).
It is NOT necessary to get to that data.
