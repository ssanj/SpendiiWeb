/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.mongo

import spendii.model.MongoConverter
import com.mongodb.{BasicDBObject, DBCollection}
import spendii.mongo.MongoTypes._

trait MongoCollectionTrait {

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

    def save(mo:MongoObject): Either[MongoError, Unit] = {
      import spendii.mongo.MongoTypes.MongoWriteResult._
      dbc.save(mo.toDBObject).getMongoError match {
        case None => Right()
        case Some(me) => Left(me)
      }
    }

    def save[T](value:T)(implicit mc:MongoConverter[T]): Either[MongoError, Unit] =  save(mc.convert(value))

    def update[T](query:MongoObject, upate:MongoObject, upsert:Boolean):Either[MongoError, Unit] = {
      import spendii.mongo.MongoTypes.MongoWriteResult._
      wrapWith {
        dbc.update(query.toDBObject, upate.toDBObject, upsert, false)
      } match {
        case Right(result) => result.getMongoError match { case None => Right();  case Some(me) => Left(me) }
        case Left(me) => Left(me)
      }
    }

    def drop: Either[MongoError, Unit] = wrapWith(dbc.drop)
  }

  object MongoCollection {
    implicit def dbCollectionToMongoCollection(dbc:DBCollection): MongoCollection = MongoCollection(dbc)
  }

}