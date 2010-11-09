/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.dispatch

import xml.NodeSeq
import bootstrap.liftweb.MongoBoot._
import net.liftweb.util.Helpers._
import spendii.model.Common._
import net.liftweb.http.DispatchSnippet
import spendii.model.DailySpend
import spendii.mongo.MongoTypes.MongoError

object LoadAll extends DispatchSnippet {

  def dispatch = {
    case _ => loadAll _
  }

  private def loadAll(xhtml:NodeSeq): NodeSeq = {
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

  private def displayTable(ds:DailySpend)(xhtml:NodeSeq): NodeSeq = {
    bind("table", xhtml, "row" -> displayRowNoDeletes(ds) _)
  }

  private def displayRowNoDeletes(ds:DailySpend)(xhtml:NodeSeq): NodeSeq = {
    ds.indexedSpends.flatMap(indexSpend =>
      bind("content", xhtml,
        "count" -> indexSpend.index.toString,
        "label" -> indexSpend.spend.label,
        "cost" -%> <span>{indexSpend.spend.cost}</span>,
        "description" -> indexSpend.spend.description))
  }
}