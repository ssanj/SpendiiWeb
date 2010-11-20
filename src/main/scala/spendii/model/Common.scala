/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.model

import java.util.{Calendar => Cal}
import Cal._
import spendii.mongo.MongoTypes.MongoError
import net.liftweb.http.TemplateFinder
import net.liftweb.util.Helpers._
import net.liftweb.common.{Full, Failure}
import xml.{Text, NodeSeq}

object Common {

  def formattedDate(date: Long): String =  String.format("%1$tA %1$te %1$tB %1$tY", {
    val cal = Cal.getInstance
    cal.setTimeInMillis(date)
    cal
  })

  def removeTime(cal:Cal): Cal = {
    cal.set(HOUR, 0)
    cal.set(MINUTE, 0)
    cal.set(SECOND, 0)
    cal.set(MILLISECOND, 0)
    cal
  }

  def currentDate: Cal = removeTime(Cal.getInstance)

  def currentDateAsTime: Long = currentDate.getTimeInMillis

  def currentDateAsString = formattedDate(currentDateAsTime)

  implicit def displayError(me:MongoError): NodeSeq = {
    TemplateFinder.findAnyTemplate(List("exception")) match {
      case Full(xhtml) => bind("exception", xhtml,
          "context" -> "Could not perform function",
          "message" -> me.message,
          "stacktrace" -> me.stackTrace)
      case _ => <div>The following error occurred : {me.message}. Could not load error template to display additional information.</div>
    }
  }

  def getError(context:String, me:MongoError): NodeSeq = {
    TemplateFinder.findAnyTemplate(List("exception")) match {
      case Full(xhtml) => bind("exception", xhtml,
          "context" -> context,
          "message" -> me.message,
          "stacktrace" -> me.stackTrace)
      case _ => <div>The following error occurred : {me.message}. Could not load error template to display additional information.</div>
    }
  }

  def displayError(ex:String): NodeSeq = {
      <div>
        <h2>Could not Perform Operation due to the following error:</h2>
        <h3 class="exception_message">{ex}</h3>
      </div>
  }

  def displayNoSpends: NodeSeq = {
    <div>
      <h3 class="nospends">No Spends</h3>
    </div>
  }

  def displaySuccess(msg:String): NodeSeq = {
    <div>
      <h3 class="success">{msg}</h3>
    </div>
  }


}