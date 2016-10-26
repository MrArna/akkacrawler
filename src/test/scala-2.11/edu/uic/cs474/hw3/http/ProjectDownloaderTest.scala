package edu.uic.cs474.hw3.http

import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.stream.ActorMaterializer
import akka.testkit.TestActorRef
import edu.uic.cs474.hw3.messages.Start
import org.scalatest.FunSuite

/**
  * Created by Marco on 23/10/16.
  */
class ProjectDownloaderTest extends FunSuite
{

  test("Http Connection")
  {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()


    /*val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(uri = "https://api.github.com/users/octocat/orgs"))

    println(responseFuture)*/


    val actorRef = TestActorRef(new ProjectDownloader)

    actorRef ! Start

  }

}
