/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import collection.mutable.ListBuffer
import spendii.validate.FailureCollector._

final class FailureCollectorWithFoldSuite extends FunSuite with ShouldMatchers {

  test("A FailureCollector should apply errors and then run error function on errors") {
    val buffer = ListBuffer[String]()
    failure.collect("", () => buffer += "fail1").fold(() => buffer += "error", () => buffer += "success")
    buffer.size should equal (2)
    buffer.head should equal ("fail1")
    buffer.tail.head should equal ("error")
  }

  test("A FailureCollector should run succes function if there are no errors") {
    val buffer = ListBuffer[String]()
    failure.collect("abc", () => buffer += "fail1").fold(() => buffer += "error", () => buffer += "success")
    buffer.size should equal (1)
    buffer.head should equal ("success")
  }
}