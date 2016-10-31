package edu.uic.cs474.hw3.undestand

import java.util.Arrays

import com.scitools.understand.{Database, Entity}

/**
  * Created by Alessandro on 31/10/16.
  */
class DbManager(val db: Database) {

  def getEntityListByTypeListFromDb(entityKindList: List[EntityKind]): List[Entity] = {
    return db.ents("%s %s %s %s %s".format(entityKindList.mkString(" "),
      NotUnknown.kind,
      NotUnresolved.kind,
      NotTypevariable.kind,
      NotAnnotation.kind)).toList
  }
}
