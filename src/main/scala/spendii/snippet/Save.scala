/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import net.liftweb.common.Loggable
import xml.NodeSeq
import scala.collection.JavaConversions._
import java.util.Calendar._
import net.liftweb.util.Helpers._
import bootstrap.liftweb.MongoBoot
import com.mongodb.BasicDBObject
import java.util.{ArrayList, Calendar => Cal}
import net.liftweb.http.{S, SHtml}

class Save extends Loggable {

  private var cost:String = ""
  private var label:String = ""
  private var description:String = ""

  def spend(xhtml:NodeSeq): NodeSeq = {
    bind("spendii", xhtml,
      "current_date" -> currentDateAsString,
      "label" -> SHtml.text(label, label = _, ("id", "label")),
      "cost" -> SHtml.text(cost, cost = _, ("id", "cost")),
      "description" -> SHtml.textarea(description, description = _, ("rows", "3"), ("cols", "50")),
      "save_spend" -> SHtml.submit("save", () => saveSpend))
  }

  private def saveSpend {
    logger.info("label -> " + label + ", cost -> " + cost + ", description -> " + description)
    if (parametersAreValid) {
      MongoBoot.getCollection("sanj.spends").insert(createSpends)
    } else {
      S.error("There are form errors")
    }
  }

  def renderLabel: NodeSeq = {
    if (!isLabelValid) <div>Please enter a label</div>
    else NodeSeq.Empty
  }

  def isLabelValid: Boolean = !label.trim.isEmpty

  def parametersAreValid: Boolean = {
    val validLabel = !label.trim.isEmpty
    val validCost = isNumber(cost)
    val validDescription = !description.trim.isEmpty

    if (!validLabel){
      S.error("label.error", "Please enter a label.")

    }

    if (!validCost) {
      S.error("Please enter a numeric cost.")
    }

    if (!validDescription) {
      S.error("Please enter a description.")
    }

    validLabel && validCost && validDescription
  }

  private def isNumber(value:String): Boolean = {
    try {
      value.toDouble
      true
    } catch {
      case _ => false
    }
  }

  private def createSpends: BasicDBObject = {
    new BasicDBObject(Map[String, Any]("date" -> currentDateAsTime, "spends" -> createSpend(label, cost.toDouble, description)))
  }

 private def createSpend(tag:String, cost:Double, description:String): BasicDBObject = {
    new BasicDBObject(Map[String, Any]("tag" -> tag, "cost" -> cost, "description" -> description))
  }

  private def sListTojList[T](seq:Seq[T]): java.util.Collection[T] = {
    val javaList = new ArrayList[T]
    for (item <- seq) javaList.add(item)
    javaList
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

  private def currentDateAsString: String = {
    String.format("%1$tA %1$te %1$tB %1$tY", currentDate)
  }
}