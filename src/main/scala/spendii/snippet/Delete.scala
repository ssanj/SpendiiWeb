/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import xml.NodeSeq
import net.liftweb.common.Loggable
import bootstrap.liftweb.MongoBoot
import java.lang.System
import net.liftweb.http.{ResponseShortcutException, S}

class Delete extends Loggable {

  def spend(xhtml:NodeSeq): NodeSeq = {
    try {
      MongoBoot.deleteCollection("sanj.spends")
      S.notice("notice.id", "Successfully deleted spends")
      S.redirectTo("home")
    } catch {
      case r:ResponseShortcutException => throw r //rethrow redirect
      case e:Exception => {
        S.error("Could not delete spends due to: "  +
        System.getProperty("line.separator") + e.getStackTrace)
        NodeSeq.Empty
      }
    }
  }
}