/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.mongo

import xml.NodeSeq
import net.liftweb.common.Full
import net.liftweb.http.TemplateFinder
import net.liftweb.util.Helpers._

trait MongoErrorTrait {

    /**
   * Captures an <code>Exception</code>'s error message and stacktrace and allows for the unification
   * of errors returned by <code>MongoType</code>s.
   */
  case class MongoError(val message:String, val stackTrace:String)

  object MongoError {
    implicit def mongoErrorToNodeSeq(me:MongoError): NodeSeq = {
      TemplateFinder.findAnyTemplate(List("exception")) match {
        case Full(xhtml) => bind("exception", xhtml,
            "context" -> "Could not perform function",
            "message" -> me.message,
            "stacktrace" -> me.stackTrace)
        case _ => <div>The following error occurred : {me.message}. Could not load error template to display additional information.</div>
      }
    }
  }
}