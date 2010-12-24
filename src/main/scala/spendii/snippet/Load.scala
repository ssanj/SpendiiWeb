/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import xml.{NodeSeq}
import bootstrap.liftweb.MongoBoot._
import spendii.model.Common._
import spendii.mongo.MongoTypes.MongoError
import spendii.mongo.MongoTypes.MongoObject._
import net.liftweb.util.Helpers._
import net.liftweb.common.{Empty, Failure, Full, Loggable}
import net.liftweb.http.js.JsCmds._
import spendii.model.{Spend, DailySpend}
import net.liftweb.http.{S, SHtml, TemplateFinder}
import net.liftweb.http.js.{JE, JsCmd}
import net.liftweb.http.js.JE.{Str, JsRaw}
import LiftWithEase._
import spendii.model.TemplateKeys.LoadSpendFormLabels._
import spendii.common.Rounding
import bootstrap.liftweb.BootConfig._

//TODO: Split this class up so it can be tested.
class Load extends Loggable with Rounding {

  val user:String = "sanj"

  def spends(xhtml:NodeSeq): NodeSeq = {
    val dailySpend:Either[MongoError, Option[DailySpend]] = getDailySpend(user).findOne[DailySpend]("date" -> currentDateAsTime)
    dailySpend match {
      case Right(Some(ds)) => displaySpends(xhtml, ds)
      case Right(None) => displayNoSpends
      case Left(ex) => error(load_form_error, ex); NodeSeq.Empty
    }
  }
  private def displaySpends(xhtml:NodeSeq, ds:DailySpend): NodeSeq = {
      bind("spends", xhtml,
        "total" -%> <span>${roundUp(ds.total, scale)}</span>,
        "row" -> displayRowWithDeletes(ds) _)
  }

  def displayRowWithDeletes(ds:DailySpend)(xhtml:NodeSeq): NodeSeq = {
    ds.indexedSpends.flatMap(indexSpend =>
      bind("content", xhtml,
        AttrBindParam("idval", ("sp" + indexSpend.index), "id"),
        "count" -> indexSpend.index.toString,
        "label" -> indexSpend.spend.label,
        "cost" -%> <span>{roundUp(indexSpend.spend.cost, scale)}</span>,
        "description" -> indexSpend.spend.description,
        "delete" -> SHtml.a(<span class="action">delete</span>)(deleteSpend(indexSpend.spend, indexSpend.index)),
        "edit" -> SHtml.a(<span class="action">edit</span>)(editSpend(indexSpend.spend, indexSpend.index))
      ))
  }

  def editSpend(sp:Spend, count:Int): JsCmd = JE.Call("update_form_for_edit", Str(sp.description), Str(sp.cost.toString), Str(sp.label))

  def deleteSpend(sp:Spend, count:Int): JsCmd = {
    getDailySpend(user).findAndModify[DailySpend]("date" -> currentDateAsTime, empty, pull("spends", sp), true) match {
      case Left(me) => callErrorFunc(me.message)
      case Right(ds) => calSuccessFunc(roundUp(ds.total, scale), "sp" + count)
    }
  }

  def callErrorFunc(message:String): JsCmd =  JE.Call("show_deletion_error", Str(message))

  def calSuccessFunc(total:String, value:String): JsCmd = JE.Call("delete_spend", Str(total), Str(value))
}