/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.model

import collection.mutable.ListBuffer
import spendii.mongo.MongoTypes.{MongoObject, MongoObjectId}
import MongoConverter._

case class Spend(val description:String, val cost:Double, val label:String)

object Spend {
  implicit def spendToMongo(sp:Spend): MongoObject = SpendConverter.convert(sp)
}

case class DailySpend(val id:Option[MongoObjectId], val date:Long, val spends:Seq[Spend]) {

  def add(sp:Spend): DailySpend = DailySpend(id, date, (new ListBuffer() ++= spends += sp).toSeq)

  def replace(sp1:Spend, sp2:Spend): DailySpend = DailySpend(id, date, spends.map(sp => if (sp == sp1) sp2 else sp))

  def remove(sp:Spend): DailySpend = DailySpend(id, date, spends.filterNot(_ == sp))

  def total: Double = spends.foldLeft(0D)(_ + _.cost)

  def indexedSpends: Seq[IndexedSpend] =  spends.zipWithIndex.map(t => IndexedSpend(t._1, t._2 + 1))
}

object DailySpend {  
  implicit def dsToMongo(ds:DailySpend): MongoObject = DailySpendConverter.convert(ds)
}

case class IndexedSpend(spend:Spend, index:Int)
