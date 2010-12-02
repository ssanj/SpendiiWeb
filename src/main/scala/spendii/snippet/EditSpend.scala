/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import bootstrap.liftweb.MongoBoot
import spendii.model.Common._
import spendii.model.{Spend, DailySpend}
import spendii.mongo.MongoTypes._
import spendii.snippet.LiftWithEase._

trait EditSpend { this:SaveVariables =>

  protected def editSpend {
    val col = MongoBoot.getDailySpend(user)
    var found = col.findOne[DailySpend]("date", currentDateAsTime)
    found match {
      case Left(me) => error(getError(cantFindExpenditure, me))
      case Right(Some(ds)) => editDailySpend(col, ds)
      case Right(None) => error(cantFindExpenditure)
    }
  }

  private def cantFindExpenditure: String = "Could not find expenditure for " +  currentDateAsString

  private def editDailySpend(col:MongoCollection, ds:DailySpend) {
    col.update(ds, ds.replace(Spend(oDescription, oCost.toDouble, oLabel), Spend(description, cost.toDouble, label)), false) match {
      case Left(me:MongoError) => error(me)
      case Right(_) =>  notice("Edited Spend")
    }
  }
}