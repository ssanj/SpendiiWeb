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
import spendii.validate.FailureCollector

class Save extends Loggable {

  private val user:String = "sanj"

  private var label:String = ""
  private var cost:String = ""
  private var description:String = ""

  private var oDescription:String = ""
  private var oCost:String = ""
  private var oLabel:String = ""

  def spend(xhtml:NodeSeq): NodeSeq = {
    bind("spendii", xhtml,
      "current_date" -> currentDateAsString,
      "label" -> SHtml.text(label, label = _, ("id", "label")),
      "cost" -> SHtml.text(cost, cost = _, ("id", "cost")),
      "description" -> FocusOnLoad(SHtml.textarea(description, description = _, ("id", "description"), ("rows", "3"), ("cols", "50"))),
      "olabel" -> SHtml.hidden(oLabel = _, oLabel, ("id", "olabel")),
      "ocost" -> SHtml.hidden(oCost = _, oCost, ("id", "ocost")),
      "odescription" -> SHtml.hidden(oDescription = _, oDescription, ("id", "odescription")),
      "save_spend" -> SHtml.submit("save", () => saveSpend, ("id", "save_button")),
      "edit_spend" -> SHtml.submit("edit", () => editSpend, ("id", "edit_button"))
      )
  }

  private def editSpend {
    val col = MongoBoot.getDailySpend(user)
    var found = col.findOne[DailySpend]("date", currentDateAsTime)
    found match {
      case Left(me) => error(getError(cantFindExpenditure, me))
      case Right(Some(ds)) => saveDailySpend(col, ds.replace(Spend(oDescription, oCost.toDouble, oLabel), Spend(description, cost.toDouble, label)))
      case Right(None) => error(cantFindExpenditure)
    }
  }

  private def cantFindExpenditure: String = "Could not find expenditure for " +  currentDateAsString

  private def saveSpend {
    import spendii.validate.Validator._
    new FailureCollector().
            collect(label, emptyLabelError).
            collect(description, emptyDescriptionError).
            collect(cost, nonNumericCostError)(StringToDoubleValidator).and(_.toDouble, nonPositiveCostError).
            fold(formErrors, performSave)
  }

  private def emptyLabelError() { error(label_error, "Please enter a label.") }

  private def emptyDescriptionError() { error(description_error, "Please enter a description.") }

  private def nonNumericCostError() { error(cost_error, "Please enter a numeric cost.") }

  private def nonPositiveCostError() { error(cost_error, "Please enter a cost greater than $0.") }

  private def formErrors() { error(form_error, "There are form errors") }

  private def performSave() {
    val col = MongoBoot.getDailySpend(user)
    //change this to use the atomic update method
    //col.update("date" -> currentDateAsTime, col.put[DailySpend](ds), true /* upsert*/, false /* multi*/)
    var found = col.findOne[DailySpend]("date", currentDateAsTime)
    found match {
      case Left(me) => error(me)
      case Right(Some(ds)) => saveDailySpend(col, ds.add(Spend(description, cost.toDouble, label)))
      case Right(None) => saveDailySpend(col, createNewSpend)
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

  private def displayErrorAndStay(me:MongoError): NodeSeq = {
    displayError(me)
  }
}