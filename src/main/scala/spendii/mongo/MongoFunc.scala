/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.mongo

import com.mongodb._
import spendii.model.{Spend, DailySpend}
import collection.mutable.ListBuffer

object MongoFunc {

  trait MongoConverter[T] {
    def convert(dbo:DBObject): T
  }

  object MongoConverter {
    implicit object DailySpendConverter extends MongoConverter[DailySpend] {
       def convert(dbo:DBObject): DailySpend = {
         val date = dbo.get("date").toString.toLong
         DailySpend(date, getSpends(dbo.get("spends").asInstanceOf[BasicDBList]): _*)
       }

       def getSpends(spends:BasicDBList): Seq[Spend] = {
        import scala.collection.JavaConversions._
        val buffer = new ListBuffer[Spend]
        for(spend <- spends.iterator) {
          buffer += (getSpend(spend.asInstanceOf[DBObject]))
        }
        buffer
       }

       def getSpend(dbo:DBObject): Spend = {
         //clean this up to use MongoObject with type inference.
         Spend(dbo.get("description").toString, dbo.get("cost").toString.toDouble, dbo.get("label").toString)
       }
    }
  }

  private def jTosMap[A, B](jm:java.util.Map[A, B]): Map[String, AnyRef] = {
    import scala.collection.JavaConversions.JMapWrapper
    import scala.collection.mutable.{Map => MutMap}
    val map = JMapWrapper(jm)
    val map2 = MutMap[String, AnyRef]()
    for (i <- map) {
      map2 += (i._1.asInstanceOf[String] -> i._2.asInstanceOf[AnyRef])
    }
    map2.toMap
  }

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

  case class MongoCollection(dbc:DBCollection) {

    def findOne[T](key:String, value:Any)(implicit con:MongoConverter[T]): Option[T] = {
      val find = dbc.findOne(new BasicDBObject(key, value))
      if (find == null) None else  Some(con.convert(find))
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

  case class MongoServer(private val m:Mongo) {

    def getDatabase(key:String): Either[String, MongoDatabase] = {
      val mdb:MongoDatabase = m.getDB(key)
      wrapWithKey(mdb)(key)
    }
  }

  implicit def mongoToMongoDB(m:Mongo): MongoServer = MongoServer(m)

  implicit def dbToMongoDatabase(db:DB): MongoDatabase = MongoDatabase(db)

  implicit def dbCollectionToMongoCollection(dbc:DBCollection): MongoCollection = MongoCollection(dbc)

  def connect: MongoServer = new Mongo

  def connect(db:String, col:String) : (MongoServer, MongoDatabase, MongoCollection) = {
      val server = new Mongo
      val database = server.getDB(db)
      val collection = database.getCollection(col)
      (server, database, collection)
  }

  def connect(db:String) : (MongoServer, MongoDatabase) = {
      val server = new Mongo
      val database = server.getDB(db)
      (server, database)
  }

  def connect(host:String, port:Int): MongoServer = new Mongo(host, port)


  def wrap[T](f: => T): Either[String, T] = {
    try {
      Right(f)
    } catch {
      case e => Left(e.getMessage + System.getProperty("line.separator") + e.getStackTraceString)
    }
  }

  def wrapWithKey[T](f: => T)(key:String): Either[String, T] = {
    try {
      Right(f)
    } catch {
      case n:NullPointerException => Left(key + " is an invalid key." + System.getProperty("line.separator") + n.getStackTraceString)
      case e => Left(e.getMessage + System.getProperty("line.separator") + e.getStackTraceString)
    }
  }

    def main(args: Array[String]) {
      wrap{
        val (server, database, collection) = connect("spendii.sanj", "dailyspends")
        //collection.drop
//        collection.insert(Map("name" -> "sanj", "age" -> 36.asInstanceOf[AnyRef]))
//        val found = collection.findOne[Person]("name", "sanj")
//        val cursor = collection.find[Person](Map("name" ->"sanj"))
//        val it = cursor.toIterator
//        for (i <- it) println(i)
      }.fold(l => println("error -> " + l), r => println("success -> " + r))
    }

}

