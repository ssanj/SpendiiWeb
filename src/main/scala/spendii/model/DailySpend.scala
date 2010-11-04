/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.model

import spendii.mongo.MongoTypes.{MongoObjectId}
import collection.mutable.ListBuffer

case class Spend(val description:String, val cost:Double, val label:String)

case class DailySpend(val id:Option[MongoObjectId], val date:Long, val spends:Seq[Spend]) {

  def add(sp:Spend): DailySpend = DailySpend(id, date, (new ListBuffer() ++= spends += sp).toSeq)

  def remove(sp:Spend): DailySpend = DailySpend(id, date, spends.filterNot(_ == sp))

  def total: Double = spends.foldLeft(0D)(_ + _.cost)

  def indexedSpends: Seq[IndexedSpend] =  spends.zipWithIndex.map(t => IndexedSpend(t._1, t._2 + 1))
}

case class IndexedSpend(spend:Spend, index:Int)
