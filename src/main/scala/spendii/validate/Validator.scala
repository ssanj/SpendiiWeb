/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import ValidatorTypes._
import spendii.validate.ValidationStatus._

object ValidatorTypes {
  type AnyFunc = () => Any

  //Hint for type class resolution of Strings that need to be converted to Doubles.
  final case class StringToDouble(val value:String)
}

/**
 * Type class for Validators. The validate method takes a value of a type T, and a function to invoke if the validation fails.
 */
trait Validator[T] {
  /**
   * Validates a value of T. If the validation fails returns a Some(f), if  it succeeds returns a None.
   */
  def validate(value:T, f:AnyFunc): Option[AnyFunc]
}

object Validator {
  implicit object EmptyStringValidator extends Validator[String] {
    def validate(value:String, f:() => Any): Option[AnyFunc] = if (value.trim.isEmpty) Some(f) else None
  }

  implicit object PositiveNonZeroDoubleValidator extends Validator[Double] {
    def validate(value:Double, f:() => Any): Option[AnyFunc] = if (value <= 0.0D) Some(f) else None
  }

  implicit object StringToDoubleValidator extends Validator[StringToDouble] {
    def validate(strValue:StringToDouble, f:() => Any): Option[AnyFunc] = {
      try {
        strValue.value.toDouble
        None
      } catch { case _ => Some(f) }
    }
  }
}

/**
 * Class that collects validation successes and failures.
 * A ValidationStatus stores the previous success value (if any) and previous failures (if any).
 *
 * <P> Defines the type of the stored previous success value.
 */
sealed abstract class ValidationStatus[+P] private[validate] (failures:Seq[Option[AnyFunc]], previous:Option[P]) {

  /**
   * Collects any errors for the supplied value of type V and registers the function f, against that error. The function f is not
   * executed at this time. An implicit/supplied Validator of type V is matched/provided for the value supplied.
   *
   * If the value V fails the supplied Validator, the failure is registered with the function f along with any existing errors
   * in a new Failure.
   * If the value V passes the supplied Validator, the success is stored along with any existing errors in a new Success.
   */
  def validate[V](value:V, f:AnyFunc) (implicit validator:Validator[V]): ValidationStatus[V] = {
    validator.validate(value, f) match {
      case Some(error) => new Failure(failures :+ Some(error))
      case None => new Success[V](failures, value)
    }
  }

  /**
   * Call this method following a previous invocation to collect.
   *
   * If the current ValidationStatus is a Success, the function  p(success) => V is executed to provide a value of type V for validation
   * as per the validate method.
   *
   * If the current ValidationStatus is a Failure a new Failure is returned.
   */
  def andThen[V](p:(P) => V, f:AnyFunc)(implicit validator:Validator[V]): ValidationStatus[V]

  /**
   * Call this function once ready to run all supplied functions for each error.
   *
   * If there are no errors then the supplied function f, is invoked.
   * If errors exist, the registered functions against each error is executed. The supplied function f, is not executed.
   *
   */
  def onSuccess(f:AnyFunc) { fold(() => {}, f) }

  /**
   * Call this function once ready to run all supplied functions for each error.
   * If there are no errors then the supplied function success, is invoked.
   * If errors exist, the registered functions against each error is executed. The supplied function fail, is executed after all the error functions.
   */
  def fold(fail:AnyFunc, success:AnyFunc) {
    failures.flatten match {
      case Nil => success()
      case xs => {
            xs.foreach(_.apply)
            fail()
      }
    }
  }
}

/**
 * Success models a successful validation. It has previous failures from validations as well as the successfully validated value in previous.
 *
 * Calling and will transform the previous value into a value V, which can be validated.
 */
final case class Success[+P](failures:Seq[Option[AnyFunc]] = Seq[Option[AnyFunc]](), previous:P)
        extends ValidationStatus[P](failures, Some(previous)) {

  def andThen[V](p:(P) => V, f:AnyFunc)(implicit validator:Validator[V]): ValidationStatus[V] = validate[V](p(previous), f)(validator)
}

/**
 * Models a failed validation. It has previous failures from validations.
 *
 * Calling andThen will return another instance of this class.
 */
final case class Failure[Nothing](failures:Seq[Option[AnyFunc]] = Seq[Option[AnyFunc]]()) extends ValidationStatus[Nothing](failures, None) {

  def andThen[V](p:(Nothing) => V, f:AnyFunc)(implicit validator:Validator[V]): ValidationStatus[V] = new Failure(failures)
}

object ValidationStatus {
  def validator = Failure()
}