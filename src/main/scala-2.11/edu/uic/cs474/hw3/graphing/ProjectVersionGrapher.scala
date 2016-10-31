package edu.uic.cs474.hw3.graphing

import akka.actor.Actor
import com.scitools.understand.Understand
import edu.uic.cs474.hw3.messages.{DoneGraphVersionDb, GraphVersionDb}
import edu.uic.cs474.hw3.undestand.DbManager

/**
  * Created by Alessandro on 30/10/16.
  */
class ProjectVersionGrapher extends Actor {
  override def receive: Receive = {
    case GraphVersionDb(repository, version, versionDbPath) =>
      val db = Understand.open(versionDbPath)
      val builder: ReferenceGraphBuilder = new ReferenceGraphBuilder(new DbManager(db))
      builder.build
      sender ! DoneGraphVersionDb(repository, version, builder.referenceGraph)
  }
}
