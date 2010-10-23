/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.mongo

import com.mongodb._
import spendii.model.{AnyRefConverter, MongoConverter}
import collection.mutable.ListBuffer

object MongoTypes {

  def wrapWithKey[T](f: => T)(key:String): Either[MongoError, T] = {
    try {
      Right(f)
    } catch {
      case n:NullPointerException => Left(MongoError(key + " is an invalid key.", n.getStackTraceString))
      case e => Left(MongoError(e.getMessage, e.getStackTraceString))
    }
  }

  def wrapWith[T](f: => T): Either[MongoError, T] = {
    try {
      Right(f)
    } catch {
      case e => Left(MongoError(e.getMessage, e.getStackTraceString))
    }
  }

  /**
   * Captures an <code>Exception</code>'s error message and stacktrace and allows for the unification
   * of errors returned by <code>MongoType</code>s.
   */
  case class MongoError(val message:String, val stackTrace:String)

  case class MongoObject(dbo:DBObject) {
    def get[T](key:String)(implicit con:AnyRefConverter[T]): T = con.convert(dbo.get(key))

    def getArray[T](key:String)(implicit con:MongoConverter[T]): Seq[T] = {
      import scala.collection.JavaConversions._
      val buffer = new ListBuffer[T]
      for(element <- dbo.get(key).asInstanceOf[BasicDBList].iterator) {
        buffer += (con.convert(element.asInstanceOf[DBObject]))
      }

      buffer.toSeq
    }
  }

  object MongoObject {
    implicit def dbObjectToMongoObject(dbo:DBObject): MongoObject = MongoObject(dbo)
  }

  case class MongoCursor(private val dbc:DBCursor) {
    def toSeq[T](implicit con:MongoConverter[T]): Seq[T] = {
      import scala.collection.JavaConversions._
        val it:Iterator[DBObject] = dbc.iterator
        it.map(con.convert(_)).toSeq
    }
  }

  object MongoCursor {
    implicit def dbCursorToMongoCursor(dbc:DBCursor): MongoCursor = MongoCursor(dbc)
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

    def find[T](q:Map[String, AnyRef])(implicit con:MongoConverter[T]): Either[MongoError, Seq[T]] = {
      import scala.collection.JavaConversions._
      wrapWith{
        val mc:MongoCursor = dbc.find(new BasicDBObject(q))
        mc.toSeq[T]
      }
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