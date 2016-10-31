package edu.uic.cs474.hw3.versioning

import java.io.File
import java.lang.Iterable

import akka.actor.Actor
import akka.http.scaladsl.model.headers.LinkParams.rev
import edu.uic.cs474.hw3.messages.{CheckoutVersion, DoneCheckoutVersion, DoneGetLastMaxNVersions, GetLastMaxNVersions}
import org.apache.commons.io.{FileUtils, FilenameUtils}
import org.eclipse.jgit.api.{Git, LogCommand}
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit

import scala.collection.JavaConverters._


/**
  * Created by Alessandro on 30/10/16.
  */
class ProjectVersionManager extends Actor {
  val ShortHashLength = 7
  val Separator = "_"

  override def receive: Receive = {
    case GetLastMaxNVersions(repository, projectDirPath, n) => {
      val commitList: Iterable[RevCommit] = Git.open(new File(projectDirPath)).log()
        .setMaxCount(n)
        .call()
      val commitHashList: List[String] = commitList.asScala.map(_.getName).toList;
      sender ! DoneGetLastMaxNVersions(repository, projectDirPath, commitHashList)
    }

    case CheckoutVersion(repository, version, projectPath) => {
      val versionDirPath = createVersionDir(projectPath, version)
      checkoutVersion(versionDirPath, version)
      sender ! DoneCheckoutVersion(repository, version, versionDirPath)
    }
  }

  //projectDirPath must not end with / TODO add check and fix of /
  def createVersionDir(projectDirPath: String, version: String): String = {
  val versionDirPath: String = FilenameUtils.concat(FilenameUtils.getFullPathNoEndSeparator(projectDirPath),
    FilenameUtils.getBaseName(projectDirPath).concat(Separator).concat(version.substring(0,ShortHashLength)))
    FileUtils.copyDirectory(new File(projectDirPath), new File(versionDirPath))
    return versionDirPath
  }

  def checkoutVersion(versionDirPath: String, commitId: String) = {
    Git.open(new File(versionDirPath)).checkout().setName(commitId).call()
  }
}
