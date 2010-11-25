/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package test.scala.spendii.validate

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import spendii.validate.FailureCollector
import spendii.validate.Validator.{EmptyStringValidator, PositiveNonZeroDoubleValidator, StringToDoubleValidator}
import collection.mutable.ListBuffer

final class FailureCollectorSuite extends FunSuite with ShouldMatchers {

  test("A FailureCollector should run all failure functions on failures") {
    val buffer = ListBuffer[String]()
    new FailureCollector().collect("", () => buffer += "fail1").
                           collect(-23D, () => buffer += "fail2").
                           onSuccess(() => buffer += "success")
    buffer.size should equal (2)
    buffer.contains("success") should equal (false)
    buffer.contains("fail1") should equal (true)
    buffer.contains("fail2") should equal (true)
  }

  test("A FailureCollector should run only failured functions on failures") {
    val buffer = ListBuffer[String]()
    new FailureCollector().collect("blah", () => buffer += "fail1").
                           collect(-2D, () => buffer += "fail2").
                           collect("ra", () => buffer += "fail3").
                           onSuccess(() => buffer += "success")
    buffer.size should equal (1)
    buffer.contains("fail2") should equal (true)
  }

  test("A FailureCollector should only run the successful function in the absence of failures") {
    val buffer = ListBuffer[String]()
    new FailureCollector().collect("blah", () => buffer += "fail1").
                           collect(25.5D, () => buffer += "fail2").
                           onSuccess(() => buffer += "success")
    buffer.size should equal (1)
    buffer.head should equal ("success")
  }
}