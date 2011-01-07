/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package plugins

import sbt._

class Plugins(info:ProjectInfo) extends PluginDefinition(info) {
  val jsTester = "ssahayam" % "jstestrunner" % "1.0.6.14"
}