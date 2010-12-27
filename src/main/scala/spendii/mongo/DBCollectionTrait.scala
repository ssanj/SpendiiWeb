/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.mongo

import com.mongodb.{DBObject, DBCollection, DBCursor}
import spendii.common.JavaToScala

trait DBCollectionTrait {

  import com.mongodb.DBCursor

  def findOne(dbo:DBObject): Option[DBObject]

  def find(dbo:DBObject): DBCursor
}

object DBCollectionTrait extends JavaToScala {

  implicit def createDBCollectionTrait(dbc:DBCollection): DBCollectionTrait = new DBCollectionTrait {

      def findOne(dbo:DBObject): Option[DBObject] = nullToOption(dbc.findOne(dbo))

      def find(dbo:DBObject): DBCursor = dbc.find(dbo)
    }
}