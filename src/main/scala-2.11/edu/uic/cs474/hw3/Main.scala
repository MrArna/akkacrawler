package edu.uic.cs474.hw3

import akka.actor.{ActorSystem, Props}
import edu.uic.cs474.hw3.analysis.NVersionsFirstLast
import edu.uic.cs474.hw3.messages.Start
import edu.uic.cs474.hw3.versioning.CommitPolicy

/**
  * The entry point of the application
  */
object Main {

  val usage =
    """
    Usage:  -n <nr-of-projects>
            -k <keyword>
            -l <lang>
            -vm <nr-of-version-manager>
            -vp <nr-of-version-parser>
            -v <nr-of-version>
              """

  type OptionMap = Map[Symbol, Any]


  //read args and map values
  def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
    def isSwitch(s : String) = (s(0) == '-')
    list match {
      case Nil => map
      case "-n" :: value :: tail => nextOption(map ++ Map('nrOfProjects -> value.toInt), tail)
      case "-k" :: value :: tail => nextOption(map ++ Map('keyword -> value.toInt), tail)
      case "-l" :: value :: tail => nextOption(map ++ Map('lang -> value.toInt), tail)
      case "-vm" :: value :: tail => nextOption(map ++ Map('vm -> value.toInt), tail)
      case "-vp" :: value :: tail => nextOption(map ++ Map('vp -> value.toInt), tail)
      case "-v" :: value :: tail => nextOption(map ++ Map('v -> value.toInt), tail)
      case string :: opt2 :: tail if isSwitch(opt2) => nextOption(map ++ Map('infile -> string), list.tail)
      case string :: Nil =>  nextOption(map ++ Map('infile -> string), list.tail)
    }
  }


  def main(args: Array[String])
  {

    if (args.length == 0) {
      println(usage)
      System.exit(0)
    }
    val arglist = args.toList

    val options = nextOption(Map(), arglist)

    val system = ActorSystem(
      "GithubAnalyzer")
    val master = system.actorOf (Props[Master])
    Config.
      maxProjectVersionManagers_=(options.get('vm).get.asInstanceOf[Int])
    Config.
      maxProjectVersionParsers_=(options.get('vp).get.asInstanceOf[Int])
      Config.maxNVersions_=(options.get('v).get.asInstanceOf[Int])
    Config.
      analysisPolicy_=(NVersionsFirstLast)
    Config.
      versionPolicy_=(CommitPolicy)
      master !
        Start(options.get('nrOfProjects).get.asInstanceOf[Int],
              options.get('keyword).get.asInstanceOf[String],
              options.get('lang).get.asInstanceOf[String]
              )
  }
}
