/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import net.liftweb.common.Loggable
import xml.NodeSeq
import net.liftweb.util.Helpers._
import bootstrap.liftweb.MongoBoot
import java.util.{ArrayList}
import net.liftweb.http.{RequestVar, S, SHtml}
import com.mongodb.{DBCollection, DBObject, BasicDBObject}
import spendii.model.Common._
import spendii.model.{Spend, DailySpend}
import spendii.mongo.MongoTypes._

class Save extends Loggable {

  private var label:String = ""
  private var cost:String = ""
  private var description:String = ""

  def spend(xhtml:NodeSeq): NodeSeq = {
    bind("spendii", xhtml,
      "current_date" -> currentDateAsString,
      "label" -> SHtml.text(label, label = _, ("id", "label")),
      "cost" -> SHtml.text(cost, cost = _, ("id", "cost")),
      "description" -> SHtml.textarea(description, description = _, ("rows", "3"), ("cols", "50")),
      "save_spend" -> SHtml.submit("save", () => {}))
  }
// col.put[DailySpend](key:String, DailySpend(currentDateAsTime, List(Spend("blah", 20.3, "blah2"))

//  def spend(xhtml:NodeSeq): NodeSeq = {
//    bind("spendii", xhtml,
//      "current_date" -> currentDateAsString,
//      "label" -> SHtml.text(label, label = _, ("id", "label")),
//      "cost" -> SHtml.text(cost, cost = _, ("id", "cost")),
//      "description" -> SHtml.textarea(description, description = _, ("rows", "3"), ("cols", "50")),
//      "save_spend" -> SHtml.submit("save", () => saveSpend))
//  }
//
//  private def saveSpend {
//    logger.info("label -> " + label + ", cost -> " + cost + ", description -> " + description)
//    if (parametersAreValid) {
//      val col = MongoBoot.getCollection("sanj.spends")
//      var ds = col.findOne(new BasicDBObject("date", currentDateAsTime))
//      if (ds.isEmpty) {
//        logger.info("creating new collecting for date -> " + currentDateAsTime)
//        col.insert(createBlankDailySpend)
//        ds = col.findOne(new BasicDBObject("date", currentDateAsTime)).asInstanceOf[BasicDBObject]
//      }
//
//      val spends = ds.get("spends").asInstanceOf[java.util.List[AnyRef]]
//      spends.add(createSpend(label, cost.toDouble, description))
//      col.save(ds)
//      S.notice("notices.id","Saved Spend")
//    } else {
//      S.error("There are form errors")
//    }
//  }
//
//  private def createBlankDailySpend: BasicDBObject = {
//    new BasicDBObject(Map[String, Any]("date" -> currentDateAsTime, "spends" -> new ArrayList[AnyRef]))
//  }
//
//  def renderLabel: NodeSeq = {
//    if (!isLabelValid) <div>Please enter a label</div>
//    else NodeSeq.Empty
//  }
//
//  def isLabelValid: Boolean = !label.trim.isEmpty
//
//  def parametersAreValid: Boolean = {
//    val validLabel = !label.trim.isEmpty
//    val validCost = isNumber(cost)
//    val validDescription = !description.trim.isEmpty
//
//    if (!validLabel){
//      S.error("label.error", "Please enter a label.")
//    }
//
//    if (!validCost) {
//      S.error("cost.error", "Please enter a numeric cost.")
//    }
//
//    if (!validDescription) {
//      S.error("description.error", "Please enter a description.")
//    }
//
//    validLabel && validCost && validDescription
//  }
//
//  private def isNumber(value:String): Boolean = {
//    try {
//      value.toDouble
//      true
//    } catch {
//      case _ => false
//    }
//  }
//
//  private def createSpends: BasicDBObject = {
//    new BasicDBObject(Map[String, Any]("spends" -> createSpend(label, cost.toDouble, description)))
//  }
//
// private def createSpend(label:String, cost:Double, description:String): BasicDBObject = {
//    new BasicDBObject(Map[String, Any]("label" -> label, "cost" -> cost, "description" -> description))
//  }
//
//  private def sListTojList[T](seq:Seq[T]): java.util.Collection[T] = {
//    val javaList = new ArrayList[T]
//    for (item <- seq) javaList.add(item)
//    javaList
//  }
//

  def testData(xhtml:NodeSeq): NodeSeq = {
    wrapWith{
      val m = MongoBoot.onDailySpends.put[DailySpend](DailySpend(None, currentDateAsTime,
        Seq(Spend("breakfast at PepperLounge", 30.50, "breakfast"), Spend("Chiro for sweets", 40.0, "chiro"))))
      MongoBoot.onDailySpends.save(m)
    }.fold(displayErrorAndStay, r => displaySuccessAndGoHome)
  }

  private def displayErrorAndStay(me:MongoError): NodeSeq = {
    displayError(me)
  }

  private def displaySuccessAndGoHome: NodeSeq = {
    S.notice("notice.id", "Successfully inserted spends")
    S.redirectTo("home")
    NodeSeq.Empty
  }
}