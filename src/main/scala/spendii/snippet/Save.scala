/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import net.liftweb.common.Loggable
import xml.NodeSeq
import scala.collection.JavaConversions._
import java.util.{Calendar => Cal}
import java.util.Calendar._
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import bootstrap.liftweb.MongoBoot
import com.mongodb.BasicDBObject

class Save extends Loggable {

  private var cost:String = ""
  private var label:String = ""
  private var description:String = ""

  def spend(xhtml:NodeSeq): NodeSeq = {
    bind("spendii", xhtml,
      "current_date" -> currentDateAsString,
      "label" -> SHtml.text(label, label = _, ("id", "label")),
      "cost" -> SHtml.text(cost, cost = _, ("id", "cost")),
      "description" -> SHtml.textarea(description, description = _, ("rows", "3"), ("cols", "50")),
      "save_spend" -> SHtml.submit("save", () => saveSpend))
  }

  private def saveSpend {
    logger.info("label -> " + label + ", cost -> " + cost + ", description -> " + description)
    val dailySpends = MongoBoot.db.getCollection("sanj.spends")
    dailySpends.insert(createSpends)
  }

  private def createSpends: BasicDBObject = {
    //TODO: Add validation
    new BasicDBObject(Map[String, Any]("date" -> currentDateAsTime, "spends" -> createSpend(label, cost.toDouble, description)))
  }

 private def createSpend(tag:String, cost:Double, description:String): BasicDBObject = {
    new BasicDBObject(Map[String, Any]("tag" -> tag, "cost" -> cost, "description" -> description))
  }

//  private def sListTojList[T](seq:Seq[T]): java.util.Collection[T] = {
//    val javaList = new ArrayList[T]
//    for (item <- seq) javaList.add(item)
//    javaList
//  }

  private def removeTime(cal:Cal): Cal = {
    cal.set(HOUR, 0)
    cal.set(MINUTE, 0)
    cal.set(SECOND, 0)
    cal.set(MILLISECOND, 0)
    cal
  }

  private def currentDate: Cal = removeTime(Cal.getInstance)

  private def currentDateAsTime: Long = currentDate.getTimeInMillis

  private def currentDateAsString: String = {
    String.format("%1$tA %1$te %1$tB %1$tY", currentDate)
  }
}