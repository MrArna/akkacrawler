-----

# CS474 - Homework 3

-----

## How to run the code

## Implementation Description

###Main Program

###System Structure

The system is built on top of the Akka library. The main actors and their relationship are shown in the following picture:

![akka.png](https://bitbucket.org/repo/dq9bdA/images/1376771233-akka.png)

The actors are:

+ Master: it manages all other actors and restart them in case of failure;
+ ProjectDownloader: it downloads a specified number of GitHub projects and forwards each of them to the ProjectRouter;

###Project Download

###Project Parsing

###Project Analysis

###Bonus Features