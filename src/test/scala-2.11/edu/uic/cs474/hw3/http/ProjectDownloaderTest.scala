package edu.uic.cs474.hw3.http

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestActorRef, TestKit}
import com.typesafe.config.ConfigFactory
import edu.uic.cs474.hw3.messages.{GetLastMaxNVersions, Start}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
  * Created by Marco on 23/10/16.
  */
class ProjectDownloaderTest extends TestKit(ActorSystem("DownloadTest",ConfigFactory.parseString(TestKitUsageSpec.config)))
  with DefaultTimeout
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
{

  "A Downloader" should {
    "clone the repo and return a parse message" in {
      val actorRef = TestActorRef[ProjectDownloader]


      actorRef ! Start(1,"tetris","Java")

      expectMsgClass(classOf[GetLastMaxNVersions])

    }

  }

}

