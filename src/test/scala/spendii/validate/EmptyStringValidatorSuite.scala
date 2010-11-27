/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import spendii.validate.Validator.{EmptyStringValidator}

final class EmptyStringValidatorSuite extends FunSuite with ShouldMatchers {

  test("An EmptyStringValidator should return the Some(error function)") {
    assertFailure(EmptyStringValidator.validate("", () => "The String supplied was empty."))
    assertFailure(EmptyStringValidator.validate("    ", () => "The String supplied was empty."))

    def assertFailure(result:Option[() => Any]) {
      result match {
        case Some(f) => f() should equal("The String supplied was empty.")
        case _ => fail("Expected Some(error function)")
      }
    }
  }

  test("An EmptyStringValidator should return None on success") {
    val result:Option[() => Any] = EmptyStringValidator.validate("some text", () => "A valid String")
    result match {
      case None =>
      case Some(_) => fail("Expected None but got Some.")
    }
  }
}