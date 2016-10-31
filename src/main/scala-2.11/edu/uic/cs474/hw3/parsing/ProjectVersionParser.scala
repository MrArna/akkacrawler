package edu.uic.cs474.hw3.parsing

import akka.actor.Actor
import akka.actor.Actor.Receive
import edu.uic.cs474.hw3.messages.{DoneParseVersion, ParseVersion}
import org.apache.commons.io.FilenameUtils

import sys.process._

/**
  * Created by Alessandro on 30/10/16.
  */
class ProjectVersionParser extends Actor {
  override def receive: Receive = {
    case ParseVersion(repository, version, versionPath) => {
      val (outputDbPath: String, command: String) = getUnderstandCommand(versionPath)
      val exitCode: Int = command.!
      //TODO: exception on bad exit code and check file exists
      sender ! DoneParseVersion(repository, version, outputDbPath)
    }
  }

  //versionDirPath must end without / TODO fix
  def getUnderstandCommand(versionDirPath: String): (String, String) = {
    val versionDirName = FilenameUtils.getBaseName(versionDirPath)
    val outputDbPath = FilenameUtils.concat(FilenameUtils.getPath(versionDirPath), versionDirName.concat("udb"))
    val command: String = "und create -db %s -languages java add %s analyze -all"
      .format(outputDbPath, versionDirPath)
    return (outputDbPath, command)
  }
}
