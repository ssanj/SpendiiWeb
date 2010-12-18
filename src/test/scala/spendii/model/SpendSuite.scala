/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.model

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

final class SpendSuite extends FunSuite with ShouldMatchers {

  test("A Spend should round costs to 2 decimal places") {
    import Spend._
    createSpend("blah", 2059.4589D, "blee").cost should equal (2059.46D)
  }
}