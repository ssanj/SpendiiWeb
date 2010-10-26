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

//  def spend(xhtml:NodeSeq): NodeSeq = {
//    bind("spendii", xhtml,
//      "current_date" -> currentDateAsString,
//      "label" -> SHtml.text(label, label = _, ("id", "label")),
//      "cost" -> SHtml.text(cost, cost = _, ("id", "cost")),
//      "description" -> SHtml.textarea(description, description = _, ("rows", "3"), ("cols", "50")),
//      "save_spend" -> SHtml.submit("save", () => {}))
//  }

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
    if (parametersAreValid) {
      val col = MongoBoot.onDailySpends
      var found:Either[MongoError, Option[DailySpend]] = MongoBoot.onDailySpends.findOne[DailySpend]("date", currentDateAsTime)
      found match {
        case Left(me) => S.error("There are database errors ->" + me)
        case Right(Some(ds)) => //found one
        case Right(None) => {
          logger.info("creating new collecting for date -> " + currentDateAsTime)
          col.save(col.put[DailySpend](createBlankDailySpend))
        }
      }
      col.findOne[DailySpend]("date", currentDateAsTime).fold(l => S.error("There are database errors -> " + l), op => {
        op.map{ds =>
          col.save(col.put[DailySpend](ds.add(Spend(description, cost.toDouble, label))))
          S.notice("notices.id","Saved Spend")
        }
      })
    } else {
      S.error("There are form errors")
    }
  }

  private def createBlankDailySpend: DailySpend =  DailySpend(None, currentDateAsTime, List[Spend]())

  def renderLabel: NodeSeq = {
    if (!isLabelValid) <div>Please enter a label</div>
    else NodeSeq.Empty
  }

  def isLabelValid: Boolean = !label.trim.isEmpty

  def parametersAreValid: Boolean = {
    val validLabel = !label.trim.isEmpty
    val validCost = isNumber(cost)
    val validDescription = !description.trim.isEmpty

    if (!validLabel){
      S.error("label.error", "Please enter a label.")
    }

    if (!validCost) {
      S.error("cost.error", "Please enter a numeric cost.")
    }

    if (!validDescription) {
      S.error("description.error", "Please enter a description.")
    }

    validLabel && validCost && validDescription
  }

  private def isNumber(value:String): Boolean = {
    try {
      value.toDouble
      true
    } catch {
      case _ => false
    }
  }

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