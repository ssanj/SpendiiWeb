/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import bootstrap.liftweb.MongoBoot
import spendii.model.Common._
import spendii.model.{Spend}
import spendii.mongo.MongoTypes._
import spendii.mongo.MongoTypes.MongoObject._
import spendii.snippet.LiftWithEase._
import spendii.model.TemplateKeys.SaveSpendFormLabels._
import spendii.validate.ValidationStatus._

trait SaveSpend { this:SaveVariables =>

  protected def saveSpend {
    import spendii.validate.ValidatorTypes._
    validator.validate(label, emptyLabelError).
              validate(description, emptyDescriptionError).
              validate(StringToDouble(cost), nonNumericCostError).andThen(_.value.toDouble, nonPositiveCostError).
              fold(formErrors, performSave)
  }

  private def performSave() {
    val col = MongoBoot.getDailySpend(user)
    import Spend._
    col.update("date" -> currentDateAsTime, push("spends", createSpend(description, cost.toDouble, label)), true) match {
      case Left(me:MongoError) => error(me)
      case Right(_) =>  notice("Saved Spend")
    }
  }

  protected def emptyLabelError() { error(label_error, "Please enter a label.") }

  protected def emptyDescriptionError() { error(description_error, "Please enter a description.") }

  protected def nonNumericCostError() { error(cost_error, "Please enter a numeric cost.") }

  protected def nonPositiveCostError() { error(cost_error, "Please enter a cost greater than $0.") }

  protected def formErrors() { error(form_error, "There are form errors") }
}