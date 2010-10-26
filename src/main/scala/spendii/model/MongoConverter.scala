/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.model

import spendii.mongo.MongoTypes.MongoObject
import com.mongodb.BasicDBObject

trait MongoConverter[T] {
  def convert(dbo:MongoObject): T
  def convert(t:T): MongoObject
}

trait AnyRefConverter[T] {
  def convert(anyRef:AnyRef): T
}

object AnyRefConverter {

  implicit object LongConverter extends AnyRefConverter[Long] {
    def convert(anyRef:AnyRef): Long = anyRef.toString.toLong
  }

  implicit object DoubleConverter extends AnyRefConverter[Double] {
    def convert(anyRef:AnyRef): Double = anyRef.toString.toDouble
  }

  implicit object StringConverter extends AnyRefConverter[String] {
    def convert(anyRef:AnyRef): String = anyRef.toString
  }
}

object MongoConverter {

  implicit object DailySpendConverter extends MongoConverter[DailySpend] {

    def convert(mgo:MongoObject): DailySpend = {
       val date = mgo.get[Long]("date")
       DailySpend(date, mgo.getArray[Spend]("spends")(SpendConverter))
     }

    def convert(ds:DailySpend): MongoObject = {
      val mo = new MongoObject()
      mo.put("date", ds.date)
      mo.putArray("spends", ds.spends.map(SpendConverter.convert(_)))
      mo
    }
  }

  implicit object SpendConverter extends MongoConverter[Spend] {

    def convert(mgo:MongoObject): Spend = {
       Spend(mgo.get[String]("description"), mgo.get[Double]("cost"), mgo.get[String]("label"))
     }

    def convert(sp:Spend): MongoObject = {
      val mo = new MongoObject()
      mo.put("label", sp.label)
      mo.put("cost", sp.cost)
      mo.put("description", sp.description)
      mo
    }
  }

}