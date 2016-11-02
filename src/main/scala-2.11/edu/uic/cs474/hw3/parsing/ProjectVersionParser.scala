package edu.uic.cs474.hw3.parsing

import java.io.File
import java.util

import akka.actor.Actor
import akka.actor.Actor.Receive
import edu.uic.cs474.hw3.messages.{DoneParseVersion, ParseVersion}
import org.apache.commons.io.filefilter.{DirectoryFileFilter, FalseFileFilter, NameFileFilter, WildcardFileFilter}
import org.apache.commons.io.{FileUtils, FilenameUtils}

import scala.collection.JavaConverters._
import sys.process._

/**
  * Created by Alessandro on 30/10/16.
  */
class ProjectVersionParser extends Actor {
  override def receive: Receive = {
    case ParseVersion(repository, numberOfVersions, version, versionDirPath) => {
      val (outputDbPath: String, command: (String => String)) = getUnderstandCommand(versionDirPath)
      println(getSrcDirList(versionDirPath))
      //call the command closed on the outputDbPath with each of the "src" folders for that version
      getSrcDirList(versionDirPath).foreach(f => command(f.getAbsolutePath)!)
      //TODO: exception on bad exit code and check file exists
      sender ! DoneParseVersion(repository, numberOfVersions, version, outputDbPath)
    }
  }

  //TODO fix: versionDirPath must end without /
  def getUnderstandCommand(versionDirPath: String): (String, String => String) = {
    val versionDirName = FilenameUtils.getBaseName(versionDirPath)
    val outputDbPath = FilenameUtils.concat(FilenameUtils.getFullPathNoEndSeparator(versionDirPath), versionDirName.concat(".udb"))
    def command = (srcDirPath: String) => "und create -db %s -languages java add %s analyze -all"
      .format(outputDbPath, srcDirPath)
    return (outputDbPath, command)
  }

  /*
   * Get subdirs named "src"
   */
  def getSrcDirList(versionDirPath: String): List[File] = {
    return FileUtils.listFilesAndDirs(new File(versionDirPath),
      FalseFileFilter.INSTANCE,
      DirectoryFileFilter.INSTANCE).asScala.filter(f => f.getName.==("src")).toList
  }
}
