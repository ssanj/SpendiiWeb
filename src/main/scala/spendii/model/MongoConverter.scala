/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.model

import com.mongodb.{BasicDBList, DBObject}
import collection.mutable.ListBuffer

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