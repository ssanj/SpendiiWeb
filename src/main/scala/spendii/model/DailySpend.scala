/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.model

import scala.collection.mutable.ListBuffer
import spendii.mongo.MongoTypes.{MongoObject, MongoObjectId}
import spendii.common.Rounding
import spendii.mongo.MongoConverter

case class Spend private (val description:String, val cost:Double, val label:String)

object Spend extends Rounding {
  implicit def spendToMongo(sp:Spend): MongoObject = SpendConverter.convert(sp)

  implicit object SpendConverter extends MongoConverter[Spend] {

    def convert(mgo:MongoObject): Spend = {
       createSpend(mgo.get[String]("description"), mgo.get[Double]("cost"), mgo.get[String]("label"))
     }

    def convert(sp:Spend): MongoObject = {
      val mo = new MongoObject()
      mo.put("label", sp.label)
      mo.put("cost", sp.cost)
      mo.put("description", sp.description)
      mo
    }
  }

  import bootstrap.liftweb.BootConfig._
  def createSpend(description:String, cost:Double, label:String): Spend =  Spend(description, roundUp(cost, scale), label)
}

case class DailySpend(val id:Option[MongoObjectId], val date:Long, val spends:Seq[Spend]) {

  def add(sp:Spend): DailySpend = DailySpend(id, date, (new ListBuffer() ++= spends += sp).toSeq)

  def replace(sp1:Spend, sp2:Spend): DailySpend = DailySpend(id, date, spends.map(sp => if (sp == sp1) sp2 else sp))

  def remove(sp:Spend): DailySpend = DailySpend(id, date, spends.filterNot(_ == sp))

  def total: Double = spends.foldLeft(0D)(_ + _.cost)

  def indexedSpends: Seq[IndexedSpend] =  spends.zipWithIndex.map(t => IndexedSpend(t._1, t._2 + 1))
}

object DailySpend {

  import Spend.SpendConverter
  implicit object DailySpendConverter extends MongoConverter[DailySpend] {
    def convert(mgo:MongoObject): DailySpend = {
       val id = mgo.getId
       val date = mgo.get[Long]("date")
       DailySpend(Some(id), date, mgo.getArray[Spend]("spends")(SpendConverter))
     }

    def convert(ds:DailySpend): MongoObject = {
      val mo = new MongoObject()
      ds.id.foreach(mo.putId)
      mo.put("date", ds.date)
      mo.putArray("spends", ds.spends.map(SpendConverter.convert(_)))
      mo
    }
  }

  implicit def dsToMongo(ds:DailySpend): MongoObject = DailySpendConverter.convert(ds)
}

case class IndexedSpend(spend:Spend, index:Int)
