/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.mongo

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import MongoTypes.MongoObject._

final class MongoCollectionForFindSuite extends FunSuite with ShouldMatchers with MongoCollectionTrait with MongoDummies {

  import com.mongodb.DBCursor

  test("find should find all existing objects that match a criteria") {
    val col = MongoCollection(null, ObjectsFoundDBCollection)
    col.find[Person](empty) match {
      case Left(me) => fail("Expected Right(Seq[Person]] but got Left(me) -> " + me.message)
      case Right(people) => people should equal (Seq(Person("jazzy", 6), Person("sam", 26)))
    }
  }

  trait DBCollectionForFindTrait extends DBCollectionTrait {
    import com.mongodb.{DBCursor, DBObject}
    def findOne(dbo:DBObject): Option[DBObject] = throw new IllegalStateException("findOne should not be called from this context")
  }

  private class TestDBCursor extends DBCursor(null, null, null) {

    import com.mongodb.DBObject
    import scala.collection.JavaConversions.asIterator
    override def iterator: java.util.Iterator[DBObject] = {
      val it:java.util.Iterator[DBObject] = Array(mongoObject("name" -> "jazzy", "age" -> 6).toDBObject,
        mongoObject("name" -> "sam", "age" -> 26)toDBObject).toIterator
      it
    }
  }

  object ObjectsFoundDBCollection extends DBCollectionForFindTrait {

    import com.mongodb.{DBObject, DBCursor}
    def find(dbo:DBObject): DBCursor = new TestDBCursor
  }
}