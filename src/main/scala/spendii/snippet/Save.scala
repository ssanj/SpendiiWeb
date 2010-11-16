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
import spendii.snippet.LiftWithEase._
import spendii.model.TemplateKeys.SaveSpendFormLabels._
import net.liftweb.http.js.JsCmds._ 

class Save extends Loggable {

  private var label:String = ""
  private var cost:String = ""
  private var description:String = ""

  def spend(xhtml:NodeSeq): NodeSeq = {
    bind("spendii", xhtml,
      "current_date" -> currentDateAsString,
      "label" -> SHtml.text(label, label = _, ("id", "label")),
      "cost" -> SHtml.text(cost, cost = _, ("id", "cost")),
      "description" -> FocusOnLoad(SHtml.textarea(description, description = _, ("id", "description"), ("rows", "3"), ("cols", "50"))),
      "save_spend" -> SHtml.submit("save", () => saveSpend, ("id", "save_button")))
  }

  private def saveSpend {
    if (parametersAreValid) {
      val col = MongoBoot.onDailySpends
      var found = MongoBoot.onDailySpends.findOne[DailySpend]("date", currentDateAsTime)
      found match {
        case Left(me) => error(me)
        case Right(Some(ds)) => saveDailySpend(col, ds.add(Spend(description, cost.toDouble, label)))
        case Right(None) => saveDailySpend(col, createNewSpend)
      }
    } else {
      error(form_error, "There are form errors")
    }
  }

  private def saveDailySpend(col:MongoCollection, ds:DailySpend) {
    col.save(col.put[DailySpend](ds)).fold(me => error(me), r => notice("Saved Spend"))
  }

  private def createNewSpend: DailySpend =  DailySpend(None, currentDateAsTime, List(Spend(description, cost.toDouble, label)))

  def isLabelValid: Boolean = !label.trim.isEmpty

  def parametersAreValid: Boolean = {
    val validLabel = !label.trim.isEmpty
    val validCost = isNumber(cost)
    val validDescription = !description.trim.isEmpty

    if (!validLabel){
      error(label_error, "Please enter a label.")
    }

    if (!validCost) {
      error(cost_error, "Please enter a numeric cost.")
    }

    if (!validDescription) {
      error(description_error, "Please enter a description.")
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
    notice("Successfully inserted spends")
    S.redirectTo("home")
    NodeSeq.Empty
  }
}