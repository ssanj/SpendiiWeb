/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import collection.mutable.ListBuffer
import spendii.validate.ValidationStatus._

final class ValidationStatusWithFoldSuite extends FunSuite with ShouldMatchers {

  test("A ValidationStatus should apply errors and then run error function on errors") {
    val buffer = ListBuffer[String]()
    validator.validate("", () => buffer += "fail1").fold(() => buffer += "error", () => buffer += "success")
    buffer.size should equal (2)
    buffer.head should equal ("fail1")
    buffer.tail.head should equal ("error")
  }

  test("A ValidationStatus should run succes function if there are no errors") {
    val buffer = ListBuffer[String]()
    validator.validate("abc", () => buffer += "fail1").fold(() => buffer += "error", () => buffer += "success")
    buffer.size should equal (1)
    buffer.head should equal ("success")
  }
}