/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.mongo

import spendii.model.MongoConverter
import com.mongodb.{DBCollection}
import spendii.mongo.MongoTypes._

trait MongoCollectionTrait {

  case class MongoCollection(dbc:DBCollection) {

    def findOne[T](mo:MongoObject)(implicit con:MongoConverter[T]): Either[MongoError, Option[T]] = {
      wrapWith{
        val find = dbc.findOne(mo.toDBObject)
        if (find == null) None else  Some(con.convert(find))
      }
    }

    def find[T](mo:MongoObject)(implicit con:MongoConverter[T]): Either[MongoError, Seq[T]] = {
      wrapWith{
        val mc:MongoCursor = dbc.find(mo.toDBObject)
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