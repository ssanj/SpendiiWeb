/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import collection.mutable.ListBuffer
import spendii.validate.Validator._
import spendii.validate.FailureCollector._

final class FailureCollectorWithOnSuccessSuite extends FunSuite with ShouldMatchers {

  test("A FailureCollector should run failured functions for failures when invoking onSuccess") {
    val buffer = ListBuffer[String]()
    failure.collect("blah", () => buffer += "fail1").
                           collect(-2D, () => buffer += "fail2").
                           collect("ra", () => buffer += "fail3").
                           onSuccess(() => buffer += "success")
    buffer.size should equal (1)
    buffer.contains("fail2") should equal (true)
  }

  test("A FailureCollector should only run the successful function in the absence of failures") {
    val buffer = ListBuffer[String]()
    failure.collect("blah", () => buffer += "fail1").
                           collect(25.5D, () => buffer += "fail2").
                           onSuccess(() => buffer += "success")
    buffer.size should equal (1)
    buffer.head should equal ("success")
  }
}