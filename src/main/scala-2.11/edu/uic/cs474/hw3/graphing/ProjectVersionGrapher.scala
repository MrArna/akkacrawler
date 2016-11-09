package edu.uic.cs474.hw3.graphing

import akka.actor.Actor
import com.scitools.understand.{Database, Understand}
import edu.uic.cs474.hw3.messages.{DoneGraphVersionDb, GraphVersionDb}
import edu.uic.cs474.hw3.undestand.{Class, DbManager, FieldVariable, Interface, LocalVariable, Method}

/**
  * Created by Alessandro on 30/10/16.
  */
class ProjectVersionGrapher extends Actor {
  override def receive: Receive = {
    case GraphVersionDb(repository, numberOfVersions, version, versionDbPath) =>
      println("Received %s version %s to graph in path %s".format(repository, version, versionDbPath))
      val builder: ReferenceGraphBuilder = new ReferenceGraphBuilder(new DbManager(versionDbPath))
      builder.buildGraph(List(FieldVariable, LocalVariable, Method, Class, Interface))
      sender ! DoneGraphVersionDb(repository, numberOfVersions, version, builder.referenceGraph)
  }
}
