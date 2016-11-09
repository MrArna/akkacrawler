-----

# CS474 - Homework 3

-----

## How to run the code

TODO come lo runniamo? sbt run?

## Implementation Description

###Main Program

The main program receives some command line arguments. These are detailed as follows:

+ **-n**: the number of projects to download.
+ **-keyword**: the keyword to filter the downloaded projects.
+ **-l**: the language the downloaded projects should be written in (as required, only *java* is supported).
+ **-vm**: the number of version managers (specify more than one to speed up the process).
+ **-vp**: the number of version parsers (specify more than one to speed up the process).
+ **-v**: the number of versions to compare.

The system can be run by passing such parameters. A suggested configuration is:

TODO AGGIUNGERE CONFIG MIGLIORE PER TESTARE (penso n=5,keyword="picasso",l="java".vm=??,vp=??,v=2)

After the system is run, the actors will start downloading projects, parsing them, analyzing them and finally showing the result. The latter is basically a list of functions that should be retested, together with the reason why they should be retested (a list of changes).

##System Structure

The system is built on top of the Akka library.

The semantically meaningful actors are:

+ **Master**: it manages all other actors and restart them in case of failure. It also keeps track of the progresses on the analysis of each project by storing intermediate data.

+ **ProjectDownloader**: it downloads a specified number of GitHub projects and forwards each of them to the Master.

+ **ProjectVersionManager**: it receives a project location, takes the required *versions* from the project according to the selected policies and creates a *folder* for each of them.

+ **ProjectVersionParser**: it receives a single version of a project in form of a path to the version's folder and generates an *Undestand database* from it.

+ **ProjectVersionGrapher**: it receives a an Undestand database corresponding to a version of a project and generates a *graph* for it.

+ **ProjectAnalyzer**: it receives a set of graphs (either 2 or v depending on the analysis policy) from the Master, which has been accumulating them, it orders them from newest to oldest and computes theit *differences* two by two.

+ **ResultHandler**: it receives a set of differences between two versions of a project and *presents* them to the user.

*ProjectVersionManager* and *ProjectVersionParser* are managed in pools by **ProjectVersionManagerRouter** and **ProjectVersionParserRouter**. The size of the pools is determined by the launching parameters. 
ProjectVersionGrapher cannot be parallelized because it requires to open the Understand database through it's native library, which has proven to be *not* thread safe.

###Project Download

The *Project Download* actor is in charge of cloning the retrieved repositories into a temporary folder. This 
actor is an Akka HTTP actor. It creates a HTTP request for the GitHub API server, then sends this request and wait 
for the response. 
Once the response is obtained, it's parsed in order to obtain a JSON object. The JSON object is
used to navigate through the information it contains, in particular to retrieve the clone_url fields. These fields
are then used to clone the repository via command line process invocation. Once the repo is successfully cloned, the 
actor send a Parse message where it indicates the location of the project and that its parsing can start.

###Project Versioning

The *Project Version Manager* actor takes a folder containing a cloned repository. Then, according to the combination of selected the policies, it takes the commits corresponding to the desired versions and generates a folder containing each of the. The two types of determining policies are:

+ **VersionPolicy**: it determines what is treated as a version. *TagPolicy* considers a single tag as a version, *CommitPolicy* considers a single commit as a version.

+ **AnalysisPolicy**: it determines how many version should be taken. *FirstLast* takes only the oldest and the newest out of v version, with v passed a parameter when the program is run and version defined as specified by the previous policy. *TwoByTwo* takes all of the v versions.

###Project Parsing

*The Project Parsing* actor generates an Undestand database for a given source folder, i.e. a version of a project.

###Project Graphing

The *Project Graphing* generates a graph for a given Understand database. The graph is a directed graph containing the following kind of vertices, each corresponding to an entity kind:

+ Class
+ Interface
+ Method
+ Variable
+ Enum Constant
+ Enum

The vertices are connected by the following kind of edges, each corresponding to a reference between two entities:

+ Class Extend
+ Interface Extend
+ Implement
+ Define Method
+ Call Method
+ Define Field Variable
+ Use Field Variable
+ Set Local Variable
+ Use Local Variable

###Project Analysis

The ProjectAnalyzer is the actor responsible for analyzing different versions of the same project. After a project is parsed and graphs for several versions are created, this information is forwarded to the ProjectAnalyzer, which can start the actual analysis. The goal of this actor is to produce a list of functions that need to be retested. In order to do this, different versions of each project are compared based on their graphs. If some meaningful change that affects a certain function is detected (e.g., the function calls a new function, uses a new field, etc.), the function is suggested as a candidate for testing. Given a set of graphs for each project, each corresponding to a different version (either commit or tag), this actor allows analyzing the differences between them. There are two policies for doing this:

+ **FirstLast**: given the v versions, it takes the most recent and the oldest of them and compars them.
+ **TwoByTwo**: compares the v versions two by two.

This extended comparison allows the collection of interesting statistics about the code, like the number of times a function needed retest or the priority of each function to be retested. Notice that this was not required by our assignment. We implemented it as a bonus feature since we think it might be interesting to perform this kind of comparison. We think that it would be much better to suggest the developer a list of functions to retest together with the priorities on which parts of the code to focus on the most (i.e., those that has changed many times). 

###Bonus Features

We implemented two different non-required bonus features that we think might be useful:

+ We allow the *GitHub* projects to be filtered by **keyword** before they are downloaded. We think that, besides the choice of the number of projects to download and their language, the user should be able to filter their content, so as to get only relevant projects;
+ We analyze the differences in the projects over several different versions (either commits or tags) instead of just two. This choice is detailed in sections *Project Versioning* and *Project Analysis*.