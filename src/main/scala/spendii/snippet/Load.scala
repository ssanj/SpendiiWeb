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
      case Right(Some(ds)) =>
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
      case Right(None) =>
        <div>
          <h3 class="nospends">No Spends</h3>
        </div>
      case Left(ex) =>
        <div>
          <h2>Could not Perform Load due to the following error:</h2>
          <h3 class="exception_message">{ex.message}</h3>
          <p>
            <h4 class="exception_stacktrace">{ex.stackTrace}</h4>
          </p>
        </div>
    }
  }
}