/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

final class ValidationStatusWithAndThenForSuccessSuite extends FunSuite with ShouldMatchers {

   test("A ValidationStatus should return a Success when andThen is called on a successful validation") {
    new Success(previous="123").andThen(_.toDouble, () => {}) match {
      case Success(failure, previous) => previous should equal (123D)
      case Failure(_) => fail("expected Success")
    }
   }

  test("A ValidationStatus should return a Failure when andThen is called on a failed validation") {
    new Success(previous="-123").andThen(_.toDouble, () =>  {}) match {
      case Success(_, _) => fail("expected a Failure")
      case Failure(failures) => failures.size should equal (1)
    }
  }
}