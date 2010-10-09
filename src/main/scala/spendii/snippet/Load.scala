/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import net.liftweb.common.Loggable
import xml.{NodeSeq, Text}
import scala.collection.JavaConversions._
import net.liftweb.util.Helpers._
import bootstrap.liftweb.MongoBoot
import spendii.model.Common._
import net.liftweb.http.SHtml
import com.mongodb.{DBObject, DBCursor, BasicDBList, BasicDBObject}

class Load extends Loggable {

  def spends(xhtml:NodeSeq): NodeSeq = {
    val dailySpends = MongoBoot.db.getCollection("sanj.spends")
    val allSpends = dailySpends.find.asInstanceOf[DBCursor]
    if (allSpends != null) {
        <DIV>
              <TABLE>
                <TR>
                  <TH>Date</TH>
                  <TH>Label</TH>
                  <TH>Cost</TH>
                  <TH>Description</TH>
                </TR>
              {
                for(ds <- allSpends.iterator;
                val spendtry = ds.get("spends").asInstanceOf[BasicDBObject]) yield
                 <TR>
                    <TD>{formattedDate(ds.get("date").toString.toLong)}</TD>
                    <TD>{spendtry.getString("tag")}</TD>
                    <TD>{spendtry.getDouble("cost")}</TD>
                    <TD>{spendtry.getString("description")}</TD>
                  </TR>
              }
            </TABLE>
        </DIV>
    } else {
      <h3>No Spends</h3>
    }
  }
}