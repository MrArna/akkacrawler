-----

# CS474 - Homework 3

-----

## How to run the code

TODO come lo runniamo? sbt run?

## Implementation Description

###Main Program

The main program receives some command line arguments. These are detailed as follows:

+n: the number of projects to download;
+keyword: the keyword to filter the downloaded projects;
+l: the language the downloaded projects should be written in;
+vm: the number of version managers (specify more than one to speed up the process;
+vp: the number of version parsers (specify more than one to speed up the process;
+v: the number of versions to compare.

The system can be run by passing such parameters. A suggested configuration is:

TODO AGGIUNGERE CONFIG MIGLIORE PER TESTARE (penso n=5,keyword="picasso",l="java".vm=??,vp=??,v=2)

After the system is run, the actors will start downloading projects, parsing them, analyzing them and finally showing the result. The latter is basically a list of functions that should be retested, together with the reason why they should be retested (a list of changes).

###System Structure

The system is built on top of the Akka library.

The actors are:

+ Master: it manages all other actors and restart them in case of failure;
+ ProjectDownloader: it downloads a specified number of GitHub projects and forwards each of them to the ProjectRouter;
+ TODO

###Project Download

TODO

###Project Parsing

TODO

###Project Analysis

The ProjectAnalyzer is the actor responsible for analyzing different versions of the same project. After a project is parsed and graphs for several versions are created, this information is forwarded to the ProjectAnalyzer, which can start the actual analysis. The goal of this actor is to produce a list of functions that need to be retested. In order to do this, different versions of each project are compared based on their graphs. If some meaningful change that affects a certain function is detected (e.g., the function calls a new function, uses a new field, etc.), the function is suggested as a candidate for testing. The parsing module produces, for each project, a list of N graphs, each corresponding to a different version (or commit, or tag). This actor allows analyzing the differences between several versions of the same project. There are two policies for doing this:

+ FirstToLast: compares each of the N versions to the last one;
+ TwoByTwo: compares the versions two by two.

This extended comparison allows the collection of interesting statistics about the code, like the number of times a function needed retest or the priority of each function to be retested. Notice that this was not required by our assignment. We implemented it as a bonus feature since we think it might be interesting to perform this kind of comparison. We think that it would be much better to suggest the developer a list of functions to retest together with the priorities on which parts of the code to focus on the most (i.e., those that has changed many times). 

###Bonus Features

We implemented two different non-required bonus features that we think might be useful:

+ We allow the GitHub projects to be filtered by keyword before they are downloaded. We think that, besides the choice of the number of projects to download and their language, the user should be able to filter their content, so as to get only relevant projects;
+ We analyze the differences in the projects over several different versions (or commits, or tags) instead of just two. This choice is detailed in section "Project Analysis".