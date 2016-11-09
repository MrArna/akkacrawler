package edu.uic.cs474.hw3.undestand

import java.util.Arrays

import com.scitools.understand.{Database, Entity, Understand}

/**
  * Created by Alessandro on 31/10/16.
  */
class DbManager(versionDbPath: String) {

  //Call the Understand lib on the versionDbPath and return a list of entities corresponding to tke kind passed as parameter
  def getEntityListByTypeListFromDb(entityKind: EntityKind): List[Entity] = {
    this.synchronized {
      val db: Database = Understand.open(versionDbPath)
      return db.ents("%s %s %s %s %s".format(entityKind.kind,
        NotUnknown.kind,
        NotUnresolved.kind,
        NotTypeVariable.kind,
        NotAnnotation.kind)).toList
    }
  }
}
