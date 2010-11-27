/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import spendii.validate.Validator.StringToDoubleValidator

final class StringToDoubleValidatorSuite extends FunSuite with ShouldMatchers {

  test("A StringToDoubleValidator should return Some(error function) on error") {
    StringToDoubleValidator.validate("five", () => "We need number 5") match {
      case Some(f) => f() should equal ("We need number 5")
      case None => fail("Expected Some(error function")
    }
  }

  test("A StringToDoubleValidator should return None on success") {
    StringToDoubleValidator.validate("5", () => "This should succeed") match {
      case None =>
      case Some(_) => fail("Expected None but got Some")
    }
  }
}