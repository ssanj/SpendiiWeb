/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.mongo

import com.mongodb.{DBObject, DBCollection}
import spendii.common.JavaToScala

trait DBCollectionTrait {

  def findOne(mo:DBObject): Option[DBObject]
}

object DBCollectionTrait extends JavaToScala {

  implicit def createDBCollectionTrait(dbc:DBCollection): DBCollectionTrait = new DBCollectionTrait {
      def findOne(dbo:DBObject): Option[DBObject] = nullToOption(dbc.findOne(dbo))
    }
}