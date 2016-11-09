package edu.uic.cs474.hw3.http

import java.io.File

import akka.NotUsed
import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import edu.uic.cs474.hw3.Config
import edu.uic.cs474.hw3.messages.{GetLastMaxNVersions, Start}
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JString
import org.json4s.jackson._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.sys.process._

/**
  * Created by andrea on 23/10/16.
  */


class ProjectDownloader extends Actor with ActorLogging {

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  implicit val formats = DefaultFormats


  val http = Http(context.system)


  def download(numberOfProject: Int,keyword:String, lang: String) = {
    val http = Http(context.system)
    import HttpMethods._


    //initialize the HTTP request
    val userData = ByteString("abc")

    val request:HttpRequest=
      HttpRequest(
        GET,
        uri = "https://api.github.com/search/repositories?q=" + keyword + "+language:" + lang + "&per_page=" + numberOfProject
      )

    //make the request
    val fut : Future[HttpResponse] = http.singleRequest(request)

    //wait for response
    val response = Await.result(fut,Duration.Inf)

    //move response data in a flow in order to retrieve a string
    val src : Source[ByteString,Any] = response.entity.dataBytes
    val stringFlow : Flow[ByteString,String, NotUsed] = Flow[ByteString].map(chunk => chunk.utf8String)
    val sink : Sink[String,Future[String]] = Sink.fold("")(_ + _)

    //run the flow
    val content : RunnableGraph[Future[String]] = (src via stringFlow toMat sink) (Keep.right)

    val aggregation : Future[String] = content.run()

    //wait for the completion of the aggregation flow
    Await.result(aggregation,Duration.Inf)

    //response into  json
    val json = parseJson(aggregation.value.get.get)

    //find URLs in json
    val urls = json \\ "clone_url"
    var index = 1

    //create tmp directory if not exists
    val currentDirectory = new java.io.File(".").getCanonicalPath
    val tmpDir = new File(currentDirectory + "/tmp")

    if(!tmpDir.exists())
    {
      tmpDir.mkdir()
    }

    //clone repo in tmp directory using the URLs retrieved
    if(urls.isInstanceOf[JString])
    {
      "git clone " + urls.extract[String] + " tmp/" + keyword + index !!;
      val currentDirectory = new java.io.File(".").getCanonicalPath
      sender ! GetLastMaxNVersions(keyword + index, currentDirectory + "/tmp/" + keyword + index, Config.maxNVersions)

    }
    else
    {
      for (url <- (urls \ "clone_url").values.asInstanceOf[List[String]])
      {
        "git clone " + url  + " tmp/" + keyword + index !!;
        sender ! GetLastMaxNVersions(keyword + index, currentDirectory + "/tmp/" + keyword + index, Config.maxNVersions)
        index = index + 1
      }
    }

  }

  def receive = {
    case Start(nrProjects,keyword,lang) =>
      download(nrProjects,keyword,lang)
  }

}
