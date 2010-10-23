/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.mongo

import spendii.model.MongoConverter
import com.mongodb._

object MongoTypes {

  def wrapWithKey[T](f: => T)(key:String): Either[MongoError, T] = {
    try {
      Right(f)
    } catch {
      case n:NullPointerException => Left(MongoError(key + " is an invalid key.", n.getStackTraceString))
      case e => Left(MongoError(e.getMessage, e.getStackTraceString))
    }
  }

  case class MongoError(val message:String, val stackTrace:String)

  case class MongoCursor[T](private val dbc:DBCursor) {
    def toIterator()(implicit con:MongoConverter[T]): Iterator[T] = {
      import scala.collection.JavaConversions._
        val it:Iterator[DBObject] = dbc.iterator
        it.map(con.convert(_))
    }
  }

  case class MongoDatabase(private val db:DB) {

    def getCollection(key:String): MongoCollection = db.getCollection(key)

    def drop { db.dropDatabase }
  }

  object MongoDatabase {
        implicit def dbToMongoDatabase(db:DB): MongoDatabase = MongoDatabase(db)
  }

  case class MongoCollection(dbc:DBCollection) {

    def findOne[T](key:String, value:Any)(implicit con:MongoConverter[T]): Either[MongoError, Option[T]] = {
      wrapWithKey{
        val find = dbc.findOne(new BasicDBObject(key, value))
        if (find == null) None else  Some(con.convert(find))
      }(key)
    }

    def find[T](q:Map[String, AnyRef]): MongoCursor[T] = {
      import scala.collection.JavaConversions._
      MongoCursor[T](dbc.find(new BasicDBObject(q)))
    }

    def insert(key:String, value:Any) { dbc.insert(new BasicDBObject(key, value)) }

    def insert(map:Map[String, AnyRef]) {
      import scala.collection.JavaConversions._
      dbc.insert(new BasicDBObject(map))
    }

    def drop = dbc.drop
  }

  object MongoCollection {
    implicit def dbCollectionToMongoCollection(dbc:DBCollection): MongoCollection = MongoCollection(dbc)
  }

  case class MongoServer(private val m:Mongo)

  object MongoServer {
    implicit def mongoToMongoDB(m:Mongo): MongoServer = MongoServer(m)
  }
}