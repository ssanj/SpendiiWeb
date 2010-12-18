/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package spendii.common

import scala.math.BigDecimal.RoundingMode._

trait Rounding {

  def round(value:Double, places:Int, mode:RoundingMode): Double =  BigDecimal.valueOf(value).setScale(places, mode).doubleValue

  def roundUp(value:Double, places:Int): Double =  BigDecimal.valueOf(value).setScale(places, UP).doubleValue
}