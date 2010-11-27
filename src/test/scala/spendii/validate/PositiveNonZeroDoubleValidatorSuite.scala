/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import spendii.validate.Validator._

final class PositiveNonZeroDoubleValidatorSuite extends FunSuite with ShouldMatchers {

  test("A PositiveNonZeroDoubleValidator should return Some(error function) on a zero value") {
    assertNotZero(PositiveNonZeroDoubleValidator.validate(0D, () => "You supplied Zero or less!"))
    assertNotZero(PositiveNonZeroDoubleValidator.validate(0.0D, () => "You supplied Zero or less!"))
    assertNotZero(PositiveNonZeroDoubleValidator.validate(-1D, () => "You supplied Zero or less!"))

    def assertNotZero(result:Option[() => Any]) {
      result match {
        case Some(f) => f() should equal ("You supplied Zero or less!")
        case _ => fail("Expected Some(error function)")
      }
    }
  }

  test("A PositiveNonZeroDoubleValidator should None if the value is greater than zero") {
    PositiveNonZeroDoubleValidator.validate(5D, () => 6D) match {
      case None =>
      case Some(_) => fail("Expected None but got Some")
    }
  }
}