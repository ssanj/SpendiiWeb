/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package spendii.common

import scala.math.BigDecimal.RoundingMode._

trait Rounding {

  def round(value:Double, places:Int, mode:RoundingMode): BigDecimal =  BigDecimal.valueOf(value).setScale(places, mode)

  def roundUp(value:Double, places:Int): BigDecimal = round(value, places, UP)

  implicit def bigDecimalToDouble(value:BigDecimal): Double = value.doubleValue

  implicit def bigDecimalToString(value:BigDecimal): String = value.toString
}