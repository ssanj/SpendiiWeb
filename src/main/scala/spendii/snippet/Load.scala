/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import net.liftweb.common.Loggable
import xml.{NodeSeq}
import bootstrap.liftweb.MongoBoot._
import spendii.model.Common._
import spendii.model.DailySpend
import spendii.mongo.MongoTypes.MongoError
import net.liftweb.util.Helpers._

class Load extends Loggable {

  def spends(xhtml:NodeSeq): NodeSeq = {
    val dailySpend:Either[MongoError, Option[DailySpend]] = on("sanj.dailyspend").findOne[DailySpend]("date", currentDateAsTime)
    dailySpend match {
      case Right(Some(ds)) => displaySpends(xhtml, ds)
      case Right(None) => displayNoSpends
      case Left(ex) => displayError(ex)
    }
  }
  private def displaySpends(xhtml:NodeSeq, ds:DailySpend): NodeSeq = {
      bind("spends", xhtml,
        "total" -%> <span>${ds.total}</span>,
        "content" -> getSpendContent(ds))
  }

  private def displaySingleSpend(xhtml:NodeSeq, ds:DailySpend): NodeSeq = {
      bind("spends", xhtml,
        "date" -> formattedDate(ds.date),
        "total" -%> <span>${ds.total}</span>,
        "content" -> getSpendContent(ds))
  }

  private def getSpendContent(ds:DailySpend): NodeSeq = {
    val count = (1 to ds.spends.length).iterator
    for{spend <- ds.spends} yield
     <tr>
        <td>{count.next}</td>
        <td>{spend.label}</td>
        <td class="cost">{spend.cost}</td>
        <td>{spend.description}</td>
        <td><a href="/delete_single_spend">X</a></td>
      </tr>
  }

  def loadAll(xhtml:NodeSeq): NodeSeq = {
    val dailySpends:Either[MongoError, Seq[DailySpend]] = on("sanj.dailyspend").find[DailySpend](Map.empty)
    dailySpends match {
      case Right(Nil) => displayNoSpends
      case Right(seqOfDailySpends) =>
        for (ds <- seqOfDailySpends) yield
          <div>
            {displaySingleSpend(xhtml, ds)}
          </div>
      case Left(ex) => displayError(ex)
    }
  }
}