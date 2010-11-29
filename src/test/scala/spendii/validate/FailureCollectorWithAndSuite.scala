/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import org.scalatest.matchers.ShouldMatchers
import collection.mutable.ListBuffer
import org.scalatest.FunSuite
import spendii.validate.Validator._
import spendii.validate.ValidatorTypes.StringToDouble

final class FailureCollectorWithAndSuite extends FunSuite with ShouldMatchers {

  test("A FailureCollector can chain validations to success") {
    val buffer = ListBuffer[String]()
    new FailureCollector().collect(StringToDouble("5.0"), () => buffer += "fail1").
                            and(_.value.toDouble, () => buffer += "fail2").
                            collect("rara", () => buffer += "fail3").
                            onSuccess(() => buffer+= "success")
    buffer.size should equal (1)
    buffer.head should equal ("success")
  }

  test("A FailureCollector can chain validations to failure") {
    val buffer = ListBuffer[String]()
    new FailureCollector().collect(StringToDouble("-6.0"), () => buffer += "fail1").
                            and(_.value.toDouble, () => buffer += "fail2").
                            collect("", () => buffer += "fail3").
                            onSuccess(() => buffer+= "success")
    buffer.size should equal (2)
    buffer.head should equal ("fail2")
    buffer.tail.head should equal ("fail3")
  }

  test("A FailureCollector should not chain validations if the first collect link fails") {
    val buffer = ListBuffer[String]()
    new FailureCollector().collect(StringToDouble("ABC"), () => buffer += "fail1").
                            and(_.value.toDouble, () => buffer += "fail2").
                            collect("", () => buffer += "fail3").
                            onSuccess(() => buffer+= "success")
    buffer.size should equal (2)
    buffer.head should equal ("fail1")
    buffer.tail.head should equal ("fail3")
  }

  test("A FailureCollector should handle multiple chain validations") {
    val buffer = ListBuffer[String]()
    new FailureCollector().collect(StringToDouble("ABC"), () => buffer += "fail1").
                            and(_.value.toDouble, () => buffer += "fail2").
                            collect("", () => buffer += "fail3").
                            collect("abc", () => buffer += "fail4").and(identity, () => buffer += "fail5")(ShouldHaveZedValidator).
                            onSuccess(() => buffer+= "success")
    buffer.size should equal (3)
    buffer.head should equal ("fail1")
    buffer.tail.head should equal ("fail3")
    buffer.tail.tail.head should equal ("fail5")
  }

  test("A FailureCollector should not propagate chain if one of the chained validators fails") {
    val buffer = ListBuffer[String]()
    new FailureCollector().collect("ABC", () => buffer += "fail1").
                            and(identity, () => buffer += "fail2")(ShouldHaveZedValidator).
                            and(identity, () => buffer += "fail3")(ShouldHaveFourCharsValidator).
                            collect(StringToDouble("blurb"), () => buffer += "fail4").
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