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

class Load extends Loggable {

  def spends(xhtml:NodeSeq): NodeSeq = {
    val dailySpend = MongoBoot.db.getCollection("sanj.spends").findOne(new BasicDBObject("date", currentDateAsTime))
    if (dailySpend != null) {
        <div>
              <table>
                <tr>
                  <th>Date</th>
                  <th>Label</th>
                  <th>Cost</th>
                  <th>Description</th>
                </tr>
              {
                for(spend <- dailySpend.get("spends").asInstanceOf[BasicDBList]) yield
                 <tr>
                    <td>{formattedDate(dailySpend.get("date").toString.toLong)}</td>
                    <td>{spend.asInstanceOf[BasicDBObject].getString("label")}</td>
                    <td>{spend.asInstanceOf[BasicDBObject].getDouble("cost")}</td>
                    <td>{spend.asInstanceOf[BasicDBObject].getString("description")}</td>
                  </tr>
              }
            </table>
        </div>
    } else {
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