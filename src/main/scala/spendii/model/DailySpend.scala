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
}
