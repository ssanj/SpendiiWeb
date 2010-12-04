/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package bootstrap.liftweb

import net.liftweb.common.Loggable
import net.liftweb.sitemap.{Menu, SiteMap}
import spendii.mongo.MongoTypes._
import net.liftweb.http.LiftRules
import spendii.dispatch.LoadAll

class Boot extends Loggable {

  def boot {
    LiftRules.early.append{ _.setCharacterEncoding("UTF-8")}
    LiftRules.addToPackages("spendii")
    LiftRules.snippetDispatch.append{
      case "loadAllSpends" => LoadAll
    }
    LiftRules.setSiteMap(SiteMap(
      Menu("Home") / "home",
      Menu("Load All Spends") / "load-all-dailyspends",
      Menu("Delete") / "delete"))
  }
}

object MongoBoot extends Loggable {

    object Collections {
      val dailyspend = "dailyspend"
    }

    lazy val (server, database) = connect("spendii")

    def deleteCollection(name:String) { on(name).drop }

    //all the "on" methods currently are for a hardcoded user.

    def on = getCollection("sanj") _

    def getCollection(username:String)(collectionName:String): MongoCollection = database getCollection userCollection(username, collectionName)

    def getDailySpend = getCollection(_:String)(Collections.dailyspend)

    def userCollection(username:String, collectionName:String): String = username + "." + collectionName
}
