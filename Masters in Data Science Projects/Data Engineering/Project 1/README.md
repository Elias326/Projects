# Project 1: Query Project

- This change is temporary.
- In the Query Project, you will get practice with SQL while learning about
  Google Cloud Platform (GCP) and BiqQuery. You'll answer business-driven
  questions using public datasets housed in GCP. To give you experience with
  different ways to use those datasets, you will use the web UI (BiqQuery) and
  the command-line tools, and work with them in Jupyter Notebooks.

#### Problem Statement

- You're a data scientist at Lyft Bay Wheels (https://www.lyft.com/bikes/bay-wheels), formerly known as Ford GoBike, the
  company running Bay Area Bikeshare. You are trying to increase ridership, and
  you want to offer deals through the mobile app to do so. 
  
- What deals do you offer though? Currently, your company has several options which can change over time.  Please visit the website to see the current offers and other marketing information. Frequent offers include: 
  * Single Ride 
  * Monthly Membership
  * Annual Membership
  * Bike Share for All
  * Access Pass
  * Corporate Membership
  * etc.

- Through this project, you will answer these questions: 

  * What are the 5 most popular trips that you would call "commuter trips"? 
  
  * What are your recommendations for offers (justify based on your findings)?

- Please note that there are no exact answers to the above questions, just like in the proverbial real world.  This is not a simple exercise where each question above will have a simple SQL query. It is an exercise in analytics over inexact and dirty data. 

- You won't find a column in a table labeled "commuter trip".  You will find you need to do quite a bit of data exploration using SQL queries to determine your own definition of a communter trip.  In data exploration process, you will find a lot of dirty data, that you will need to either clean or filter out. You will then write SQL queries to find the communter trips.

- Likewise to make your recommendations, you will need to do data exploration, cleaning or filtering dirty data, etc. to come up with the final queries that will give you the supporting data for your recommendations. You can make any recommendations regarding the offers, including, but not limited to: 
  * market offers differently to generate more revenue 
  * remove offers that are not working 
  * modify exising offers to generate more revenue
  * create new offers for hidden business opportunities you have found
  * etc. 

#### All Work MUST be done in the Google Cloud Platform (GCP) / The Majority of Work MUST be done using BigQuery SQL / Usage of Temporary Tables, Views, Pandas, Data Visualizations

A couple of the goals of w205 are for students to learn how to work in a cloud environment (such as GCP) and how to use SQL against a big data data platform (such as Google BigQuery).  In keeping with these goals, please do all of your work in GCP, and the majority of your analytics work using BigQuery SQL queries.

You can make intermediate temporary tables or views in your own dataset in BigQuery as you like.  Actually, this is a great way to work!  These make data exploration much easier.  It's much easier when you have made temporary tables or views with only clean data, filtered rows, filtered columns, new columns, summary data, etc.  If you use intermediate temporary tables or views, you should include the SQL used to create these, along with a brief note mentioning that you used the temporary table or view.

In the final Jupyter Notebook, the results of your BigQuery SQL will be read into Pandas, where you will use the skills you learned in the Python class to print formatted Pandas tables, simple data visualizations using Seaborn / Matplotlib, etc.  You can use Pandas for simple transformations, but please remember the bulk of work should be done using Google BigQuery SQL.

#### GitHub Procedures

In your Python class you used GitHub, with a single repo for all assignments, where you committed without doing a pull request.  In this class, we will try to mimic the real world more closely, so our procedures will be enhanced. 

Each project, including this one, will have it's own repo.

Important:  In w205, please never merge your assignment branch to the master branch. 

Using the git command line: clone down the repo, leave the master branch untouched, create an assignment branch, and move to that branch:
- Open a linux command line to your virtual machine and be sure you are logged in as jupyter.
- Create a ~/w205 directory if it does not already exist `mkdir ~/w205`
- Change directory into the ~/w205 directory `cd ~/w205`
- Clone down your repo `git clone <https url for your repo>`
- Change directory into the repo `cd <repo name>`
- Create an assignment branch `git branch assignment`
- Checkout the assignment branch `git checkout assignment`

The previous steps only need to be done once.  Once you your clone is on the assignment branch it will remain on that branch unless you checkout another branch.

The project workflow follows this pattern, which may be repeated as many times as needed.  In fact it's best to do this frequently as it saves your work into GitHub in case your virtual machine becomes corrupt:
- Make changes to existing files as needed.
- Add new files as needed
- Stage modified files `git add <filename>`
- Commit staged files `git commit -m "<meaningful comment about your changes>"`
- Push the commit on your assignment branch from your clone to GitHub `git push origin assignment`

Once you are done, go to the GitHub web interface and create a pull request comparing the assignment branch to the master branch.  Add your instructor, and only your instructor, as the reviewer.  The date and time stamp of the pull request is considered the submission time for late penalties. 

If you decide to make more changes after you have created a pull request, you can simply close the pull request (without merge!), make more changes, stage, commit, push, and create a final pull request when you are done.  Note that the last data and time stamp of the last pull request will be considered the submission time for late penalties.

Make sure you receive the emails related to your repository! Your project feedback will be given as comment on the pull request. When you receive the feedback, you can address problems or simply comment that you have read the feedback. 
AFTER receiving and answering the feedback, merge you PR to master. Your project only counts as complete once this is done.

---

## Parts 1, 2, 3

We have broken down this project into 3 parts, about 1 week's work each to help you stay on track.

**You will only turn in the project once at the end of part 3!**

- In Part 1, we will query using the Google BigQuery GUI interface in the cloud.

- In Part 2, we will query using the Linux command line from our virtual machine in the cloud.

- In Part 3, we will query from a Jupyter Notebook in our virtual machine in the cloud, save the results into Pandas, and present a report enhanced by Pandas output tables and simple data visualizations using Seaborn / Matplotlib.

---

## Part 1 - Querying Data with BigQuery

### SQL Tutorial

Please go through this SQL tutorial to help you learn the basics of SQL to help you complete this project.

SQL tutorial: https://www.w3schools.com/sql/default.asp

### Google Cloud Helpful Links

Read: https://cloud.google.com/docs/overview/

BigQuery: https://cloud.google.com/bigquery/

Public Datasets: Bring up your Google BigQuery console, open the menu for the public datasets, and navigate to the the dataset san_francisco.

- The Bay Bike Share has two datasets: a static one and a dynamic one.  The static one covers an historic period of about 3 years.  The dynamic one updates every 10 minutes or so.  THE STATIC ONE IS THE ONE WE WILL USE IN CLASS AND IN THE PROJECT. The reason is that is much easier to learn SQL against a static target instead of a moving target.

- (USE THESE TABLES!) The static tables we will be using in this class are in the dataset **san_francisco** :

  * bikeshare_stations

  * bikeshare_status

  * bikeshare_trips

- The dynamic tables are found in the dataset **san_francisco_bikeshare**

### Some initial queries

Paste your SQL query and answer the question in a sentence.  Be sure you properly format your queries and results using markdown. 

- What's the size of this dataset? (i.e., how many trips)

  
  > The number of trips in the bikeshare_trips dataset is 983,648 trips. The SQL query to find this result is:
  
  ```sql
  SELECT COUNT(trip_id) FROM `bigquery-public-data.san_francisco.bikeshare_trips`
  
  ```

- What is the earliest start date and time and latest end date and time for a trip?

  > The earliest start date and time for a trip is 2013-08-29 09:08:00 UTC. The SQL query to find this result is:
  ```sql
  SELECT start_date FROM `bigquery-public-data.san_francisco.bikeshare_trips` ORDER BY start_date ASC LIMIT 1
  
  ```
  > The latest end date and time for a tip is 2016-08-31 23:48:00 UTC. The SQL query to find this result is:

  ```sql
  SELECT end_date FROM `bigquery-public-data.san_francisco.bikeshare_trips` ORDER BY end_date DESC LIMIT 1
  
  ```

- How many bikes are there?

  > There are 700 bikes in the bikeshare trips dataset. The SQL query to find this result is:
  ```sql
  SELECT COUNT(DISTINCT bike_number) FROM `bigquery-public-data.san_francisco.bikeshare_trips`
  
  ```


### Questions of your own
- Make up 3 questions and answer them using the Bay Area Bike Share Trips Data.  These questions MUST be different than any of the questions and queries you ran above.

- Question 1: What is the the most popular start and end stations for trips and how many trips were been made? (i.e. most number of bikes picked up in a station and most number of bikes dropped in a station?) 
  * Answer: The most popular start station was at San Francisco Caltrain (Townsend at 4th) and 72,683 trips started there. Meanwhile, the most popular end stations were at San Francisco Caltrain (Townsend at 4th) and 92,014 trips ended there.
  * SQL query: 
      For start trips:
      ```sql
      SELECT start_station_name, COUNT(trip_id) FROM `bigquery-public-data.san_francisco.bikeshare_trips`
      GROUP BY start_station_name
      ORDER BY COUNT(trip_id) DESC LIMIT 1
      ```
      For end trips:
       ```sql
      SELECT end_station_name, COUNT(trip_id) FROM `bigquery-public-data.san_francisco.bikeshare_trips`
      GROUP BY end_station_name
      ORDER BY COUNT(trip_id) DESC LIMIT 1
      ```

- Question 2: What was the longest trip made? Where did this trip start and end?
  * Answer: The longest trip was 17,270,400 seconds (approx. 200 days). The trip started at South Van Ness at Market and ended at 2nd at Folsom.
  * SQL query: 
      ```sql
      SELECT start_station_name, end_station_name, duration_sec 
      FROM `bigquery-public-data.san_francisco.bikeshare_trips`
      ORDER BY duration_sec DESC LIMIT 1
      ```

- Question 3: How many trips are customers and how many are subscribers?
  * Answer: From the trips, 136,809 of them were customers and 846,839 of them were subscribers.
  * SQL query:
      ```sql
      SELECT subscriber_type, COUNT(subscriber_type)
      FROM `bigquery-public-data.san_francisco.bikeshare_trips`
      GROUP BY subscriber_type
      ```

### Bonus activity queries (optional - not graded - just this section is optional, all other sections are required)

The bike share dynamic dataset offers multiple tables that can be joined to learn more interesting facts about the bike share business across all regions. These advanced queries are designed to challenge you to explore the other tables, using only the available metadata to create views that give you a broader understanding of the overall volumes across the regions(each region has multiple stations)

We can create a temporary table or view against the dynamic dataset to join to our static dataset.

Here is some SQL to pull the region_id and station_id from the dynamic dataset.  You can save the results of this query to a temporary table or view.  You can then join the static tables to this table or view to find the region:
```sql
#standardSQL
select distinct region_id, station_id
from `bigquery-public-data.san_francisco_bikeshare.bikeshare_station_info`
```

- Top 5 popular station pairs in each region

- Top 3 most popular regions(stations belong within 1 region)

- Total trips for each short station name in each region

- What are the top 10 used bikes in each of the top 3 region. these bikes could be in need of more frequent maintenance.

---

## Part 2 - Querying data from the BigQuery CLI 

- Use BQ from the Linux command line:

  * General query structure

    ```
    bq query --use_legacy_sql=false '
        SELECT count(*)
        FROM
           `bigquery-public-data.san_francisco.bikeshare_trips`'
    ```

### Queries

1. Rerun the first 3 queries from Part 1 using bq command line tool (Paste your bq
   queries and results here, using properly formatted markdown):

  * What's the size of this dataset? (i.e., how many trips)
    * SQL Query: 
    ```
    bq query --use_legacy_sql=false 
    'SELECT COUNT(trip_id) FROM `bigquery-public-data.san_francisco.bikeshare_trips`'
    ```
    * Result: 

        | f0_    |
        |--------|
        | 983648 |

  * What is the earliest start time and latest end time for a trip?

    * SQL Query (Earliest Start Time):
    ```
    bq query --use_legacy_sql=false 
    'SELECT start_station_name, COUNT(trip_id) FROM `bigquery-public-data.san_francisco.bikeshare_trips`
    GROUP BY start_station_name
    ORDER BY COUNT(trip_id) DESC LIMIT 1'
    ```

    * Result:

        | start_date          |
        |---------------------|
        | 2013-08-29 09:08:00 |


    * SQL Query (Latest End Time):
    ```
    bq query --use_legacy_sql=false 
    'SELECT end_station_name, COUNT(trip_id) FROM `bigquery-public-data.san_francisco.bikeshare_trips`
    GROUP BY end_station_name
    ORDER BY COUNT(trip_id) DESC LIMIT 1'
    ```

    * Result:

        | end_date            |
        |---------------------|
        | 2016-08-31 23:48:00 |


  * How many bikes are there?


    * SQL Query:
      ```
      bq query --use_legacy_sql=false 
      'SELECT end_station_name, COUNT(trip_id) FROM `bigquery-public-data.san_francisco.bikeshare_trips`
      GROUP BY end_station_name
      ORDER BY COUNT(trip_id) DESC LIMIT 1'
      ```

    * Result:
    
        | f0_ |
        |-----|
        | 700 |

2. New Query (Run using bq and paste your SQL query and answer the question in a sentence, using properly formatted markdown):

  * How many trips are in the morning vs in the afternoon?

  > **Note:** In this query, I am assuming that "morning" lies within the hours of 6 AM - 11:59 AM and "afternoon" is defined as 12 PM - 6 PM. Therefore, I created
  > a new table called `time_of_day_trips` that assigned their start dates to morning, afternoon, or other. 

  * SQL Query:
      ```
      bq query --use_legacy_sql=false 
      'SELECT *,
      CASE WHEN TIME (start_date) >= '6:00:00' AND TIME (start_date) < '12:00:00' THEN 'Morning'
           WHEN TIME (start_date) >= '12:00:00' AND TIME (start_date) <= '18:00:00' THEN 'Afternoon'
           ELSE 'Other'
           END AS time_of_day
      FROM `bigquery-public-data.san_francisco.bikeshare_trips`'
      ```
      
  > Next, I took the table called `time_of_day_trips` and grouped by the time of day and counted how many trips were in the morning vs in the afternoon:


  * SQL Query:
      ```
      bq query --use_legacy_sql=false 
      'SELECT time_of_day, COUNT(trip_id) FROM `durable-retina-315021.bikeshare_project.time_of_day_trips`
      GROUP BY time_of_day'
      ```
      
   * Result: There were 399,821 trips in the morning and 393,073 trips in the afternoon:

       | time_of_day | f0_    |
       |-------------|--------|
       | Afternoon   | 393073 |
       | Other       | 190754 |
       | Morning     | 399821 |


### Project Questions
Identify the main questions you'll need to answer to make recommendations (list
below, add as many questions as you need).

- Question 1: What are the top 3 hours customers and subscribers are taking the most number of trips? 

- Question 2: (Follow up from Question 1) Given the top 3 hours customers and subscribers are taking the most number of trips, what are the top 3 start and end locations for these trips?

- Question 3: What are the top 3 start and end trips for customers? What are the top 3 start and end trips for subscribers? What are the top 3 start and end trips for both customers and subscribers?

- Question 4: What is the average duration from all trips made? For customers? For subscribers?

### Answers

Answer at least 4 of the questions you identified above You can use either
BigQuery or the bq command line tool.  Paste your questions, queries and
answers below.

- Question 1: What are the top 3 hours customers and subscribers are taking the most number of trips? 
  * Answer: The top 3 hours customers and subscribers are taking the most number of trips are at 8 AM, 5 PM, and 9 AM with 132,464 trips, 126,302 trips, and 96,118 trips, respectively.
  * SQL query:
      ```sql
      SELECT EXTRACT (HOUR FROM TIME(start_date)) AS trip_hour, COUNT(trip_id)
      FROM `bigquery-public-data.san_francisco.bikeshare_trips`
      GROUP BY trip_hour
      ORDER BY COUNT(trip_id) DESC LIMIT 3
      ```
      
- Question 2: (Follow up from Question 1) Given the top 5 hours customers and subscribers are taking the most number of trips, what are the top 3 start and end locations for these trips?
  * Answer: 
    * The #1 trip is from Harry Bridges Plaza (Ferry Building) station to 2nd at Townsend station with at 8 AM with 2,922 trips.
    * The #2 trip is from Embarcadero at Sansome station to Steuart at Market station with at 5 PM with 1,898 trips.
    * The #3 trip is from 2nd at Townsend station to Harry Bridges Plaza (Ferry Building) with at 5 PM with 1,813 trips.
    * The #4 trip is from Harry Bridges Plaza (Ferry Building) station to Embarcadero at Sansome with at 8 AM with 1,649 trips.
    * The #5 trip is from Mountain View Caltrain station to Mountain View City Hall with at 9 AM with 1,608 trips.
  * SQL query:
      ```sql
      SELECT start_station_name, end_station_name,
      EXTRACT (HOUR FROM TIME(start_date)) AS trip_hour, COUNT(trip_id)
      FROM `bigquery-public-data.san_francisco.bikeshare_trips`
      WHERE EXTRACT (HOUR FROM TIME(start_date)) IN (8, 9, 17)
      GROUP BY trip_hour, start_station_name, end_station_name
      ORDER BY COUNT(trip_id) DESC LIMIT 5
      ```

- Question 3: What are the top 3 start and end trips for customers? What are the top 3 start and end trips for subscribers? What are the top 3 start and end trips for both customers and subscribers?
  * Answer: 
    * Top 3 start and end trips for customers:
      * #1: From Harry Bridges Plaza (Ferry Building) to Embarcadero at Sansome with 3,667 trips.
      * #2: From Embarcadero at Sansome to Embarcadero at Sansome with 2,545 trips.
      * #3: From Harry Bridges Plaza (Ferry Building) to Harry Bridges Plaza (Ferry Building) with 2,004 trips
    * Top 3 start and end trips for subscribers:
      * #1: From San Francisco Caltrain 2 (330 Townsend) to Townsend at 7th with 8,305 trips
      * #2: From 2nd at Townsend to Harry Bridges Plaza (Ferry Building) with 6,931 trips
      * #3: From Townsend at 7th to San Francisco Caltrain 2 (330 Townsend) with 6,641 trips
    * Top 3 start and end trips for customers and subscribers:
      * #1: From Harry Bridges Plaza (Ferry Building) to Embarcadero at Sansome with 9,150 trips.
      * #2: From San Francisco Caltrain 2 (330 Townsend) to Townsend at 7th with 8,508 trips.
      * #3: From 2nd at Townsend to Harry Bridges Plaza (Ferry Building) with 7,620 trips.
  * SQL query:
    * For customers:
    ```sql
    SELECT start_station_name, end_station_name, COUNT(trip_id)
    FROM `bigquery-public-data.san_francisco.bikeshare_trips`
    WHERE subscriber_type='Customer'
    GROUP BY start_station_name, end_station_name
    ORDER BY COUNT(trip_id) DESC LIMIT 3
    ```
    * For subscribers:
    ```sql
    SELECT start_station_name, end_station_name, COUNT(trip_id)
    FROM `bigquery-public-data.san_francisco.bikeshare_trips`
    WHERE subscriber_type='Subscriber'
    GROUP BY start_station_name, end_station_name
    ORDER BY COUNT(trip_id) DESC LIMIT 3
    ```
    * For both customers and subscribers:
    ```sql
    SELECT start_station_name, end_station_name, COUNT(trip_id)
    FROM `bigquery-public-data.san_francisco.bikeshare_trips`
    GROUP BY start_station_name, end_station_name
    ORDER BY COUNT(trip_id) DESC LIMIT 3
    ```
  
- Question 4: What is the average duration from all trips made? For customers? For subscribers?
  * Answer: 
    * The average duration for all trips is 1,018.93 seconds.
    * The average duration for customer trips is 3,718.78 seconds.
    * The average duration for subscriber trips is 582.76 seconds.
  * SQL query:
    * For all trips:
    ```sql
    SELECT AVG(duration_sec)
    FROM `bigquery-public-data.san_francisco.bikeshare_trips`
    ```
    * For customers:
    ```sql
    SELECT AVG(duration_sec)
    FROM `bigquery-public-data.san_francisco.bikeshare_trips`
    WHERE subscriber_type='Customer'
    ```
    * For subscribers:
    ```sql
    SELECT AVG(duration_sec)
    FROM `bigquery-public-data.san_francisco.bikeshare_trips`
    WHERE subscriber_type='Subscriber'
    ```

---

## Part 3 - Employ notebooks to synthesize query project results

### Get Going

Create a Jupyter Notebook against a Python 3 kernel named Project_1.ipynb in the assignment branch of your repo.

#### Run queries in the notebook 

At the end of this document is an example Jupyter Notebook you can take a look at and run.  

You can run queries using the "bang" command to shell out, such as this:

```
! bq query --use_legacy_sql=FALSE '<your-query-here>'
```

- NOTE: 
- Queries that return over 16K rows will not run this way, 
- Run groupbys etc in the bq web interface and save that as a table in BQ. 
- Max rows is defaulted to 100, use the command line parameter `--max_rows=1000000` to make it larger
- Query those tables the same way as in `example.ipynb`

Or you can use the magic commands, such as this:

```sql
%%bigquery my_panda_data_frame

select start_station_name, end_station_name
from `bigquery-public-data.san_francisco.bikeshare_trips`
where start_station_name <> end_station_name
limit 10
```

```python
my_panda_data_frame
```

#### Report in the form of the Jupter Notebook named Project_1.ipynb

- Using markdown cells, MUST definitively state and answer the two project questions:

  * What are the 5 most popular trips that you would call "commuter trips"? 
  
  * What are your recommendations for offers (justify based on your findings)?

- For any temporary tables (or views) that you created, include the SQL in markdown cells

- Use code cells for SQL you ran to load into Pandas, either using the !bq or the magic commands

- Use code cells to create Pandas formatted output tables (at least 3) to present or support your findings

- Use code cells to create simple data visualizations using Seaborn / Matplotlib (at least 2) to present or support your findings

### Resource: see example .ipynb file 

[Example Notebook](example.ipynb)

