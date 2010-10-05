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

class Save extends Loggable {

  def spend(xhtml:NodeSeq): NodeSeq = {
    bind("spendii", xhtml,
      "current_date" -> currentDate,
      "save_spend" -> SHtml.submit("save", () => {logger.info("saved!")}))
  }

  private def currentDate: String = {
    String.format("%1$tA %1$te %1$tB %1$tY", Calendar.getInstance)
  }
}