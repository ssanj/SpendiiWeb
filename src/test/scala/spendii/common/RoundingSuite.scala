/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package spendii.common

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import scala.math.BigDecimal.RoundingMode.UP
import scala.math.BigDecimal.RoundingMode.DOWN

final class RoundingSuite extends FunSuite with ShouldMatchers with Rounding {

  test("Rounding should round a Double to a specified scale and by rounding mode") {
    round(123.456789D, 2, UP) should equal (123.46D)
  }

  test("Rounding should round a Double to another specified scale and by rounding mode") {
    round(123.456789D, 5, DOWN) should equal (123.45678D)
  }

  test("Rounding should pad a value with zeros to match scale") {
    round(123D, 2, UP) should equal (123.00D)
  }

  test("Rounding should roundUp") {
    roundUp(199.987D, 1) should  equal (200.0D)
  }
}