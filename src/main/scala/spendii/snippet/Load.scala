/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import xml.{NodeSeq}
import bootstrap.liftweb.MongoBoot._
import spendii.model.Common._
import spendii.mongo.MongoTypes.MongoError
import net.liftweb.util.Helpers._
import net.liftweb.common.{Empty, Failure, Full, Loggable}
import net.liftweb.http.js.JsCmds._
import spendii.model.{Spend, DailySpend}
import net.liftweb.http.{S, SHtml, TemplateFinder}
import net.liftweb.http.js.{JE, JsCmd}
import net.liftweb.http.js.JE.{Str, JsRaw}
import LiftWithEase._
import spendii.model.TemplateKeys.LoadSpendFormLabels._

class Load extends Loggable {

  def spends(xhtml:NodeSeq): NodeSeq = {
    val dailySpend:Either[MongoError, Option[DailySpend]] = on("sanj.dailyspend").findOne[DailySpend]("date", currentDateAsTime)
    dailySpend match {
      case Right(Some(ds)) => displaySpends(xhtml, ds)
      case Right(None) => displayNoSpends
      case Left(ex) => error(form_error, ex); NodeSeq.Empty
    }
  }
  private def displaySpends(xhtml:NodeSeq, ds:DailySpend): NodeSeq = {
      bind("spends", xhtml,
        "total" -%> <span>${ds.total}</span>,
        "row" -> displayRowWithDeletes(ds) _)
  }

  def displayRowWithDeletes(ds:DailySpend)(xhtml:NodeSeq): NodeSeq = {
    ds.indexedSpends.flatMap(indexSpend =>
      bind("content", xhtml,
        AttrBindParam("idval", ("sp" + indexSpend.index), "id"),
        "count" -> indexSpend.index.toString,
        "label" -> indexSpend.spend.label,
        "cost" -%> <span>{indexSpend.spend.cost}</span>,
        "description" -> indexSpend.spend.description,
        "delete" -> SHtml.a(<span>delete</span>)(deleteSpend(indexSpend.spend, indexSpend.index))))
  }

  def deleteSpend(sp:Spend, count:Int): JsCmd = {
    val dailySpend:Either[MongoError, Option[DailySpend]] = on("sanj.dailyspend").findOne[DailySpend]("date", currentDateAsTime)
    dailySpend match {
      case Right(Some(ds)) => {
         val col = on("sanj.dailyspend")
         col.save(col.put[DailySpend](ds.remove(sp))).left.map(printError)
         logger.info("sp -> " + sp.label + " : " + count)
         JE.Call("delete_spend", Str("sp" + count))
      }
      case Right(None) => Noop
      case Left(ex) => printError(ex); Noop
    }
  }

  def printError(me:MongoError) {
    S.error("Could not delete spend. " + me.message + ", " + me.stackTrace)
  }

  def loadAll(xhtml:NodeSeq): NodeSeq = {
    val dailySpends:Either[MongoError, Seq[DailySpend]] = on("sanj.dailyspend").find[DailySpend](Map.empty)
    dailySpends match {
      case Right(Nil) => displayNoSpends
      case Right(seqOfDailySpends) =>
        seqOfDailySpends.flatMap(ds => {
          bind("spends", xhtml,
            "date" -> formattedDate(ds.date),
            "total" -%> <span>${ds.total}</span>,
            "table" -> displayTable(ds) _)})
      case Left(ex) => ex
    }
  }

  def displayTable(ds:DailySpend)(xhtml:NodeSeq): NodeSeq = {
    bind("table", xhtml, "row" -> displayRowNoDeletes(ds) _)
  }

  def displayRowNoDeletes(ds:DailySpend)(xhtml:NodeSeq): NodeSeq = {
    ds.indexedSpends.flatMap(indexSpend =>
      bind("content", xhtml,
        "count" -> indexSpend.index.toString,
        "label" -> indexSpend.spend.label,
        "cost" -%> <span>{indexSpend.spend.cost}</span>,
        "description" -> indexSpend.spend.description))
  }
}