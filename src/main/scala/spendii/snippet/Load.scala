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

class Load extends Loggable {

  def spends(xhtml:NodeSeq): NodeSeq = {
    val dailySpend:Either[MongoError, Option[DailySpend]] = on("sanj.dailyspend").findOne[DailySpend]("date", currentDateAsTime)
    dailySpend match {
      case Right(Some(ds)) => displaySpends(ds)
      case Right(None) => displayNoSpends
      case Left(ex) => displayError(ex)
    }
  }
  private def displaySpends(ds:DailySpend): NodeSeq = {
    <div>
          <table>
            <tr>
              <th>Date</th>
              <th>Label</th>
              <th>Cost</th>
              <th>Description</th>
            </tr>
          {
            for(spend <- ds.spends) yield
             <tr>
                <td>{formattedDate(ds.date)}</td>
                <td>{spend.label}</td>
                <td>{spend.cost}</td>
                <td>{spend.description}</td>
              </tr>
          }
        </table>
    </div>
  }

  private def displayNoSpends: NodeSeq = {
    <div>
      <h3 class="nospends">No Spends</h3>
    </div>
  }

  private def displayError(me:MongoError): NodeSeq = {
      <div>
        <h2>Could not Perform Load due to the following error:</h2>
        <h3 class="exception_message">{me.message}</h3>
        <p>
          <h4 class="exception_stacktrace">{me.stackTrace}</h4>
        </p>
      </div>
    }

  def loadAll(xhtml:NodeSeq): NodeSeq = {
    val dailySpends:Either[MongoError, Seq[DailySpend]] = on("sanj.dailyspend").find[DailySpend](Map.empty)
    dailySpends match {
      case Right(Nil) => displayNoSpends
      case Right(seqOfDailySpends) =>
        for (ds <- seqOfDailySpends) yield
          <p>
            {displaySpends(ds)}
          </p>
      case Left(ex) => displayError(ex)
    }
  }
}