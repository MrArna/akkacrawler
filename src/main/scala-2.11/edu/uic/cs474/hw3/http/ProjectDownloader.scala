package edu.uic.cs474.hw3.http

import akka.NotUsed
import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import edu.uic.cs474.hw3.Config
import edu.uic.cs474.hw3.messages.{CheckoutVersion, GetLastMaxNVersions, Start}
import org.json4s.JsonAST.JArray

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import org.json4s.jackson._

import sys.process._

/**
  * Created by andrea on 23/10/16.
  */


class ProjectDownloader extends Actor with ActorLogging {

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val http = Http(context.system)


  def download(numberOfProject: Int,keyword:String, lang: String) = {
    val http = Http(context.system)
    import HttpMethods._

    val userData = ByteString("abc")

    val request:HttpRequest=
      HttpRequest(
        GET,
        uri = "https://api.github.com/search/repositories?q=" + keyword + "+language:" + lang + "&per_page=" + numberOfProject
      )

    val fut : Future[HttpResponse] = http.singleRequest(request)

    val response = Await.result(fut,Duration.Inf)

    val src : Source[ByteString,Any] = response.entity.dataBytes
    val stringFlow : Flow[ByteString,String, NotUsed] = Flow[ByteString].map(chunk => chunk.utf8String)
    val sink : Sink[String,Future[String]] = Sink.fold("")(_ + _)

    val content : RunnableGraph[Future[String]] = (src via stringFlow toMat sink) (Keep.right)

    val aggregation : Future[String] = content.run()

    Await.result(aggregation,Duration.Inf)

    val json = parseJson(aggregation.value.get.get)


    //val xml = XML.loadString(aggregation.value.get.get)
    //var list : List[JValue] = List()

    //for(account <- xml \\ "account") list = list :+ toJson(account).removeField { _ == JField("badges",JNothing)}


    //println(prettyJson(json \\ "clone_url"))

    val urls = json \\ "clone_url"


    var index = 1

    for (url <- (urls \ "clone_url").values.asInstanceOf[List[String]])
    {
      "git clone " + url  + " " + keyword + index !!;
      //TODO
      index = index + 1
    }

  }

  def receive = {
    case Start =>
      //download(2,"tetris","Java")
      sender ! GetLastMaxNVersions("picasso", "/Users/Alessandro/Dropbox/Universita/UIC/OOP/marco_arnaboldi_alessandro_pappalardo_andrea_tirinzoni_hw3/picasso", Config.maxNVersions)
      //sender ! GetLastMaxNVersions("tetris2", "/Users/Alessandro/Dropbox/Universita/UIC/OOP/marco_arnaboldi_alessandro_pappalardo_andrea_tirinzoni_hw3/tetris2", 2)
      println("Sent two get last max 2 versions")
  }

}
