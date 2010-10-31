/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package bootstrap.liftweb

import net.liftweb.common.Loggable
import net.liftweb.http.LiftRules
import net.liftweb.sitemap.{Menu, SiteMap}
import spendii.mongo.MongoFunc._
import spendii.mongo.MongoTypes._

class Boot extends Loggable {

  def boot {
    LiftRules.early.append{ _.setCharacterEncoding("UTF-8")}
    LiftRules.addToPackages("spendii")
    LiftRules.setSiteMap(SiteMap(
      Menu("Home") / "home",
      Menu("Load All Spends") / "load-all-dailyspends",
      Menu("Test Data") / "test_data",
      Menu("Delete") / "delete"))
  }
}

object MongoBoot extends Loggable {

    lazy val (server, database) = connect("spendii")

    def deleteCollection(name:String) { on(name).drop }

    def on(collectionName:String): MongoCollection = database getCollection collectionName

    def onDailySpends: MongoCollection = database getCollection DAILY_SPENDS_KEY

    private val DAILY_SPENDS_KEY = "sanj.dailyspend"
}
