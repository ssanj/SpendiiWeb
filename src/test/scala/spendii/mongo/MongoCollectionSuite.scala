/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.mongo

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import com.mongodb.DBObject
import spendii.model.MongoConverter
import MongoTypes.MongoObject._
import MongoTypes.MongoObject
import spendii.model.AnyRefConverter._

final class MongoCollectionSuite extends FunSuite with ShouldMatchers with MongoCollectionTrait {

  test("A MongoCollection should findOne existing object") {
    import PersonConverter._
    MongoCollection(null, ObjectFoundDBCollection).findOne[Person](empty) should equal (Right(Some(Person("sanj", 36))))
  }

  test("A MongoCollection should not findOne non-existant object") {
    MongoCollection(null, ObjectNotFoundDBCollection).findOne[Person](empty) should equal (Right(None))
  }

  test("A MongoCollection should handle findOne with Exceptions") {
    MongoCollection(null, ObjectThrowsExceptionDBCollection).findOne[Person](empty) match {
      case Left(me) => me.message should equal ("throwing an Exy")
      case Right(x) => fail("Expected an Left(MongoError) but got a Right(" + x + ")")
    }
  }

  case class Person(name:String, age:Int)

  implicit object PersonConverter extends MongoConverter[Person] {
    def convert(mo:MongoObject): Person =  Person(mo.get[String]("name"), mo.get[Int]("age"))

    def convert(person:Person): MongoObject = {
      val mo = new MongoObject
      mo.put("name", person.name)
      mo.put("age", person.age)
      mo
    }
  }

  object ObjectFoundDBCollection extends DBCollectionTrait {
    def findOne(q:DBObject): Option[DBObject] = Some(mongoObject("name" -> "sanj", "age" -> 36).toDBObject)
  }

  object ObjectNotFoundDBCollection extends DBCollectionTrait {
    def findOne(q:DBObject): Option[DBObject] = None
  }

  object ObjectThrowsExceptionDBCollection extends DBCollectionTrait {
    def findOne(q:DBObject): Option[DBObject] = throw new RuntimeException("throwing an Exy")
  }
}