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
import spendii.model.Common._
import spendii.mongo.MongoTypes.MongoError

class Delete extends Loggable {

  def spend(xhtml:NodeSeq): NodeSeq = {
      MongoBoot.onDailySpends.drop.fold(
        displayError,
        r => {
          S.notice("notice.id", "Successfully deleted spends")
          S.redirectTo("home")
          NodeSeq.Empty  //never used because redirect throws an exception!
      })
  }
}