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

class Load extends Loggable {

  def spends(xhtml:NodeSeq): NodeSeq = {
    val dailySpend = on("sanj.dailyspend").findOne[DailySpend]("date", currentDateAsTime)
    dailySpend match {
      case Some(ds) =>
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
      case None =>
        <div>
          <h3 class="nospends">No Spends</h3>
        </div>

    }
  }
}