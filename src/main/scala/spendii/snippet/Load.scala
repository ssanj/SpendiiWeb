/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import net.liftweb.common.Loggable
import scala.collection.JavaConversions._
import xml.{NodeSeq}
import bootstrap.liftweb.MongoBoot
import spendii.model.Common._
import java.util.{Calendar => Cal}
import Cal._
import com.mongodb.{BasicDBList, DBObject, DBCursor, BasicDBObject}
import spendii.model.DailySpend

class Load extends Loggable {

  def spends(xhtml:NodeSeq): NodeSeq = {
    val dailySpend = MongoBoot.getCollection("sanj.dailyspend").findOne[DailySpend]("date", currentDateAsTime)
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

  private def removeTime(cal:Cal): Cal = {
    cal.set(HOUR, 0)
    cal.set(MINUTE, 0)
    cal.set(SECOND, 0)
    cal.set(MILLISECOND, 0)
    cal
  }

  private def currentDate: Cal = removeTime(Cal.getInstance)

  private def currentDateAsTime: Long = currentDate.getTimeInMillis
}

object B {
  def main(args: Array[String]) {
    println(currentDateAsTime)
  }

  private def removeTime(cal:Cal): Cal = {
    cal.set(HOUR, 0)
    cal.set(MINUTE, 0)
    cal.set(SECOND, 0)
    cal.set(MILLISECOND, 0)
    cal
  }

  private def currentDate: Cal = removeTime(Cal.getInstance)

  private def currentDateAsTime: Long = currentDate.getTimeInMillis


}
