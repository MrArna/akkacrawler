package edu.uic.cs474.hw3.messages

//A trait representing messages
sealed trait Message


//Stop the receiving actor
case object Stop extends Message

//Start the receiving actor
case class Start(nrProjects:Int, keyword: String, lang: String) extends Message
//Tell a ProjectHandler to start parsing a project
case class Parse(repository:String,path:String) extends Message
//Tell a ProjectHandler that the parsing of one directory is complete
case class DoneParsing(repository:String,graph:Any)  extends Message

//Tell a ProjectAnalyzer to analyze the project's graphs
case class Analyze(graphV1:Any,graphV2:Any) extends Message
//Tell the ResultHandler that the analysis is complete
case class DoneAnalyzing(differences:Any) extends Message



