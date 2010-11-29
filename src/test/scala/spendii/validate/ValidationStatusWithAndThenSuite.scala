/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import org.scalatest.matchers.ShouldMatchers
import collection.mutable.ListBuffer
import org.scalatest.FunSuite
import spendii.validate.Validator._
import spendii.validate.ValidationStatus._
import spendii.validate.ValidatorTypes.StringToDouble

final class ValidationStatusWithAndThenSuite extends FunSuite with ShouldMatchers {

  test("A ValidationStatus can andThen validations to success") {
    val buffer = ListBuffer[String]()
    validator.validate(StringToDouble("5.0"), () => buffer += "fail1").
                            andThen(_.value.toDouble, () => buffer += "fail2").
                            validate("rara", () => buffer += "fail3").
                            onSuccess(() => buffer+= "success")
    buffer.size should equal (1)
    buffer.head should equal ("success")
  }

  test("A ValidationStatus can andThen validations to failure") {
    val buffer = ListBuffer[String]()
    validator.validate(StringToDouble("-6.0"), () => buffer += "fail1").
                            andThen(_.value.toDouble, () => buffer += "fail2").
                            validate("", () => buffer += "fail3").
                            onSuccess(() => buffer+= "success")
    buffer.size should equal (2)
    buffer.head should equal ("fail2")
    buffer.tail.head should equal ("fail3")
  }

  test("A ValidationStatus should not run andThen validations if the first collect fails") {
    val buffer = ListBuffer[String]()
    validator.validate(StringToDouble("ABC"), () => buffer += "fail1").
                            andThen(_.value.toDouble, () => buffer += "fail2").
                            validate("", () => buffer += "fail3").
                            onSuccess(() => buffer+= "success")
    buffer.size should equal (2)
    buffer.head should equal ("fail1")
    buffer.tail.head should equal ("fail3")
  }

  test("A ValidationStatus should not continue to call andThens if one of the andThen validators fails") {
    val buffer = ListBuffer[String]()
    validator.validate("ABC", () => buffer += "fail1").
                            andThen(identity, () => buffer += "fail2")(ShouldHaveZedValidator).
                            andThen(identity, () => buffer += "fail3")(ShouldHaveFourCharsValidator).
                            validate(StringToDouble("blurb"), () => buffer += "fail4").
                          onSuccess(() => buffer += "success")
    buffer.size should equal (2)
    buffer.head should equal ("fail2")
    buffer.tail.head should equal ("fail4")
  }

  import ValidatorTypes._
  object ShouldHaveZedValidator extends Validator[String] {
    def validate(value:String, f:AnyFunc): Option[AnyFunc] = if (value.toLowerCase.contains('z')) None else Some(f)
  }

  object ShouldHaveFourCharsValidator extends Validator[String] {
    def validate(value:String, f:AnyFunc): Option[AnyFunc] = if (value.trim.length == 4) None else Some(f)
  }
}