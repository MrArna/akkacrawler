package edu.uic.cs474.hw3.versioning

import java.io.File
import java.lang.Iterable
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Path, SimpleFileVisitor, StandardCopyOption}

import akka.actor.Actor
import akka.http.scaladsl.model.headers.LinkParams.rev
import edu.uic.cs474.hw3.messages.{CheckoutVersion, DoneCheckoutVersion, DoneGetLastMaxNVersions, GetLastMaxNVersions}
import org.apache.commons.io.{FileUtils, FilenameUtils}
import org.eclipse.jgit.api.{Git, LogCommand}
import org.eclipse.jgit.lib.{ObjectId, Ref}
import org.eclipse.jgit.revwalk.RevCommit

import scala.collection.JavaConverters._
import scala.sys.process.Process


/**
  * Created by Alessandro on 30/10/16.
  */
class ProjectVersionManager extends Actor {
  val ShortHashLength = 7
  val Separator = "_"

  override def receive: Receive = {
    case GetLastMaxNVersions(repository, projectDirPath, n) => {
      //take the n newest tags
      val tagsRefList: List[Ref] = Git.open(new File(projectDirPath))
        .tagList()
        .call()
        .asScala
        .toList
        .filter(ref => ref.isPeeled)
        .reverse
        .take(n)
      println(tagsRefList)
      //take the corresponding commit hashes
      val tagsHashCommitList: List[String] = tagsRefList
        .map(ref => Git.open(new File(projectDirPath)).log.add(ref.getPeeledObjectId).call().asScala.head.getName)
      print(tagsHashCommitList)
      sender ! DoneGetLastMaxNVersions(repository, projectDirPath, tagsHashCommitList)
    }

    case CheckoutVersion(repository, nVersionList, version, projectPath) => {
      val versionDirPath = createVersionDir(projectPath, version)
      checkoutVersion(versionDirPath, version)
      sender ! DoneCheckoutVersion(repository, nVersionList, version, versionDirPath)
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
    println("path %s version %s".format(versionDirPath, commitId))
    //Git.open(new File(versionDirPath)).checkout().setForce(true).setName(commitId).call()
    Process("git checkout --force %s".format(commitId), new File(versionDirPath)).!
  }
}
