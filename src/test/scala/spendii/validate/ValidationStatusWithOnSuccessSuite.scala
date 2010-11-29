/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import collection.mutable.ListBuffer
import spendii.validate.Validator._
import spendii.validate.ValidationStatus._

final class ValidationStatusWithOnSuccessSuite extends FunSuite with ShouldMatchers {

  test("A ValidationStatus should run failured functions for failures when invoking onSuccess") {
    val buffer = ListBuffer[String]()
    validator.validate("blah", () => buffer += "fail1").
                           validate(-2D, () => buffer += "fail2").
                           validate("ra", () => buffer += "fail3").
                           onSuccess(() => buffer += "success")
    buffer.size should equal (1)
    buffer.contains("fail2") should equal (true)
  }

  test("A ValidationStatus should only run the successful function in the absence of failures") {
    val buffer = ListBuffer[String]()
    validator.validate("blah", () => buffer += "fail1").
                           validate(25.5D, () => buffer += "fail2").
                           onSuccess(() => buffer += "success")
    buffer.size should equal (1)
    buffer.head should equal ("success")
  }
}