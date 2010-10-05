/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package bootstrap.liftweb

import net.liftweb.common.Loggable
import net.liftweb.http.LiftRules
import net.liftweb.sitemap.{Menu, SiteMap}

class Boot extends Loggable {

  def boot {
    LiftRules.addToPackages("spendii")
    LiftRules.setSiteMap(SiteMap(Menu("Home") / "home"))
  }
}