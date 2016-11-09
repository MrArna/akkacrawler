package edu.uic.cs474.hw3.versioning

import java.io.File
import java.lang.Iterable
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Path, SimpleFileVisitor, StandardCopyOption}

import akka.actor.Actor
import akka.http.scaladsl.model.headers.LinkParams.rev
import edu.uic.cs474.hw3.Config
import edu.uic.cs474.hw3.analysis.{AnalysisPolicy, NVersionsFirstLast, NVersionsTwoByTwo}
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

  def getCommits(repository: String, projectDirPath: String, n: Int, getAll: Boolean): Unit = {
    val commitList: Iterable[RevCommit] = Git.open(new File(projectDirPath)).log()
      .setMaxCount(n)
      .call()
    var commitHashList: List[String] = commitList
      .asScala
      .map(_.getName)
      .toList
    if (!getAll) {
      commitHashList = commitHashList.filter(ref => ref == commitHashList.head || ref == commitHashList.last)
    }
    println("commits hash list is: " + commitHashList)
    sender ! DoneGetLastMaxNVersions(repository, projectDirPath, commitHashList)
  }

  override def receive: Receive = {
    case GetLastMaxNVersions(repository, projectDirPath, n) =>
      (Config.analysisPolicy, Config.versionPolicy) match {
        case (NVersionsFirstLast, TagPolicy) => getTags(repository, projectDirPath, n, false)
        case (NVersionsFirstLast, CommitPolicy) => getCommits(repository, projectDirPath, n, false)
        case (NVersionsTwoByTwo, TagPolicy) => getTags(repository, projectDirPath, n, true)
        case (NVersionsTwoByTwo, CommitPolicy) => getCommits(repository, projectDirPath, n, true)

    }
    case CheckoutVersion(repository, nVersionList, version, projectPath) =>
      val versionDirPath = createVersionDir(projectPath, version)
      checkoutVersion(versionDirPath, version)
      sender ! DoneCheckoutVersion(repository, nVersionList, version, versionDirPath)
  }

  //get all true gives all the n tags, false gets the first and last
  def getTags(repository: String, projectDirPath: String, n: Int, getAll: Boolean) = {
    //take the n newest tags
    var tagsRefList: List[Ref] = Git.open(new File(projectDirPath))
      .tagList()
      .call()
      .asScala
      .toList
      .filter(ref => ref.isPeeled)
      .reverse
      .take(n)
    if (!getAll) {
      tagsRefList = tagsRefList.filter(ref => ref == tagsRefList.head || ref == tagsRefList.last)
    }
    println("tags ref list is: " + tagsRefList)
    //take the corresponding commit hashes
    val tagsHashCommitList: List[String] = tagsRefList
      .map(ref => Git.open(new File(projectDirPath)).log.add(ref.getPeeledObjectId).call().asScala.head.getName)
    print(tagsHashCommitList)
    sender ! DoneGetLastMaxNVersions(repository, projectDirPath, tagsHashCommitList)
  }

  //projectDirPath must not end with /
  def createVersionDir(projectDirPath: String, version: String): String = {
  val versionDirPath: String = FilenameUtils.concat(FilenameUtils.getFullPathNoEndSeparator(projectDirPath),
    FilenameUtils.getBaseName(projectDirPath).concat(Separator).concat(version.substring(0,ShortHashLength)))
    FileUtils.copyDirectory(new File(projectDirPath), new File(versionDirPath))
    return versionDirPath
  }

  def checkoutVersion(versionDirPath: String, commitId: String) = {
    println("path %s version %s".format(versionDirPath, commitId))
    Process("git checkout --force %s".format(commitId), new File(versionDirPath)).!!
  }
}
