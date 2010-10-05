/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import net.liftweb.common.Loggable
import xml.NodeSeq
import java.util.Calendar
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import bootstrap.liftweb.MongoBoot

class Save extends Loggable {

  private var cost:String = ""
  private var label:String = ""
  private var description:String = ""

  def spend(xhtml:NodeSeq): NodeSeq = {
    bind("spendii", xhtml,
      "current_date" -> currentDate,
      "label" -> SHtml.text(label, label = _, ("id", "label")),
      "cost" -> SHtml.text(cost, cost = _, ("id", "cost")),
      "description" -> SHtml.textarea(description, description = _, ("rows", "3"), ("cols", "50")),
      "save_spend" -> SHtml.submit("save", () => {doStuff}))
  }

  private def doStuff {
    logger.info("label -> " + label + ", cost -> " + cost + ", description -> " + description)
    MongoBoot.db
  }

  private def currentDate: String = {
    String.format("%1$tA %1$te %1$tB %1$tY", Calendar.getInstance)
  }
}