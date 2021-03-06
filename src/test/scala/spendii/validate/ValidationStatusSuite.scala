/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import spendii.validate.Validator._
import spendii.validate.ValidationStatus._
import collection.mutable.ListBuffer

final class ValidationStatusSuite extends FunSuite with ShouldMatchers {

  test("A ValidationStatus should add new failures to previous failures") {
    val buffer = ListBuffer[String]()
    val failure = Failure(Seq(Some(() => buffer += "fail1")))
    failure.validate("", () => buffer += "fail2") match {
      case Success(_, _) => fail("expected Failure")
      case Failure(failures) => {
        failures.size should equal (2)
        failures.flatten.foreach(_.apply)
        buffer.size should equal (2)
        buffer.contains("fail1") should equal (true)
        buffer.contains("fail2") should equal (true)
      }
    }
  }

  test("A ValidationStatus should have a previous value on success and retain failures") {
    val buffer = ListBuffer[String]()
    val failure = Failure(Seq(Some(() => buffer += "fail1")))
    failure.validate("Blah", () => {}) match {
      case Success(failures, prev) => {
        failures.size should equal (1)
        failures.flatten.foreach(_.apply)
        buffer.size should equal (1)
        buffer.contains("fail1") should equal (true)
        prev should equal ("Blah")
      }
      case Failure(failures) => fail("expected Success")
    }
  }

  test("A ValidationStatus should have a retain failures across a Success") {
    val buffer = ListBuffer[String]()
    Success(previous="ABC").validate("", () => buffer += "fail1") match {
      case Success(_, _) => fail("expected Success")
      case Failure(failures) => {
        failures.size should equal (1)
        failures.flatten.foreach(_.apply)
        buffer.size should equal (1)
        buffer.contains("fail1") should equal (true)
      }
    }
  }

  test("A ValidationStatus should have a the current value on multiple successes") {
    Success(previous="ABC").validate(5D, () => {}) match {
      case Success(_, prev) => prev should equal (5D)
      case Failure(_) => fail("expected Success")
    }
  }
}
