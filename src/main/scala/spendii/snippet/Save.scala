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
import spendii.mongo.MongoTypes.MongoObject._
import spendii.snippet.LiftWithEase._
import spendii.model.TemplateKeys.SaveSpendFormLabels._
import net.liftweb.http.js.JsCmds._
import spendii.validate.ValidationStatus._

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
      case Right(Some(ds)) => editDailySpend(col, ds)
      case Right(None) => error(cantFindExpenditure)
    }
  }

  private def cantFindExpenditure: String = "Could not find expenditure for " +  currentDateAsString

  private def saveSpend {
    import spendii.validate.Validator._
    import spendii.validate.ValidatorTypes._
    validator.validate(label, emptyLabelError).
              validate(description, emptyDescriptionError).
              validate(StringToDouble(cost), nonNumericCostError).andThen(_.value.toDouble, nonPositiveCostError).
              fold(formErrors, performSave)
  }

  private def emptyLabelError() { error(label_error, "Please enter a label.") }

  private def emptyDescriptionError() { error(description_error, "Please enter a description.") }

  private def nonNumericCostError() { error(cost_error, "Please enter a numeric cost.") }

  private def nonPositiveCostError() { error(cost_error, "Please enter a cost greater than $0.") }

  private def formErrors() { error(form_error, "There are form errors") }

  private def performSave() {
    val col = MongoBoot.getDailySpend(user)
    col.update("date" -> currentDateAsTime, push("spends", Spend(description, cost.toDouble, label)), true) match {
      case Left(me:MongoError) => error(me)
      case Right(_) =>  notice("Saved Spend")
    }
  }

  private def editDailySpend(col:MongoCollection, ds:DailySpend) {
    col.update(ds, ds.replace(Spend(oDescription, oCost.toDouble, oLabel), Spend(description, cost.toDouble, label)), false) match {
      case Left(me:MongoError) => error(me)
      case Right(_) =>  notice("Edited Spend")
    }
  }  
}