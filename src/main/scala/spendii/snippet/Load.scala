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
import com.mongodb.{DBCursor, BasicDBObject}

class Load extends Loggable {

  def spends(xhtml:NodeSeq): NodeSeq = {
    val dailySpends = MongoBoot.db.getCollection("sanj.spends")
    val allSpends = dailySpends.find.asInstanceOf[DBCursor]
    if (allSpends != null) {
        <div>
              <table>
                <tr>
                  <th>Date</th>
                  <th>Label</th>
                  <th>Cost</th>
                  <th>Description</th>
                </tr>
              {
                for(ds <- allSpends.iterator;
                val spendtry = ds.get("spends").asInstanceOf[BasicDBObject]) yield
                 <tr>
                    <td>{formattedDate(ds.get("date").toString.toLong)}</td>
                    <td>{spendtry.getString("tag")}</td>
                    <td>{spendtry.getDouble("cost")}</td>
                    <td>{spendtry.getString("description")}</td>
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

  def test(xhtml:NodeSeq): NodeSeq = {
    <table>
      <tr>
        <th>Label</th>
        <th>Cost</th>
        <th>Description</th>
      </tr>
      <tr>
        <td>lunch</td>
        <td>$35.45</td>
        <td>Burger shots at the Purple Gorilla</td>
      </tr>
      <tr>
        <td>misc</td>
        <td>$7.20</td>
        <td>Coffee and Hot Chocolate at Chiasso Cafe at QUT</td>
      </tr>
    </table>
  }
}
