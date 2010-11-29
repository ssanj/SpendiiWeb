/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

final class ValidationStatusWithAndForFailureSuite extends FunSuite with ShouldMatchers {

  test("A ValidationStatus should return a Failure when andThen is called on a Failure") {
    Failure(Seq(Some(() => {}))).andThen((_:Any) => "123", () => {}) match {
      case Success(_, _) => fail("expected Failure")
      case Failure(failures) => {
        failures.size should equal (1)
      }
    }
  }
}