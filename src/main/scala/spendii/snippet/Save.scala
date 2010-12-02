/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import net.liftweb.common.Loggable
import xml.NodeSeq
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml}
import spendii.model.Common.currentDateAsString
import net.liftweb.http.js.JsCmds.FocusOnLoad

class Save extends SaveSpend with EditSpend with SaveVariables with Loggable {

  def spend(xhtml:NodeSeq): NodeSeq = {
    bind("spendii", xhtml,
      "current_date" -> currentDateAsString,
      "label" -> SHtml.text(label, label = _, ("id", "label")),
      "cost" -> SHtml.text(cost, cost = _, ("id", "cost")),
      "description" -> FocusOnLoad(SHtml.textarea(description, description = _, ("id", "description"), ("rows", "3"), ("cols", "50"))),
      "olabel" -> SHtml.hidden(oLabel = _, oLabel, ("id", "olabel")),
      "ocost" -> SHtml.hidden(oCost = _, oCost, ("id", "ocost")),
      "odescription" -> SHtml.hidden(oDescription = _, oDescription, ("id", "odescription")),
      "save_spend" -> SHtml.submit("save", () => saveSpend, ("id", "save_button")),
      "edit_spend" -> SHtml.submit("edit", () => editSpend, ("id", "edit_button"))
      )
  }
}