/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import ValidatorTypes._

object ValidatorTypes {
  type AnyFunc = () => Any
}

trait Validator[T] {
  def validate(value:T, f:AnyFunc): Option[AnyFunc]
}

object Validator {
  implicit object EmptyStringValidator extends Validator[String] {
    def validate(value:String, f:() => Any): Option[AnyFunc] = if (value.trim.isEmpty) Some(f) else None
  }

  implicit object PositiveNonZeroDoubleValidator extends Validator[Double] {
    def validate(value:Double, f:() => Any): Option[AnyFunc] = if (value <= 0.0D) Some(f) else None
  }

  object StringToDoubleValidator extends Validator[String] {
    def validate(value:String, f:() => Any): Option[AnyFunc] = {
      try {
        value.toDouble
        None
      } catch { case _ => Some(f) }
    }
  }
}

final class FailureCollector[+P](failures:Seq[Option[AnyFunc]] = Seq[Option[AnyFunc]](), previous:Option[P] = None) {

  def collect[V](value:V, f:AnyFunc) (implicit validator:Validator[V]): FailureCollector[V] = {
    validator.validate(value, f) match {
      case Some(error) => new FailureCollector[V](failures :+ Some(error))
      case None => new FailureCollector[V](failures, Some(value))
    }
  }

  def and[V](p:(P) => V, f:AnyFunc)(implicit validator:Validator[V]): FailureCollector[V] = {
    previous match {
      case Some(success:P) => collect[V](p(success), f)(validator)
      case None => new FailureCollector[V](failures)
    }
  }

  def onSuccess(f:AnyFunc) { fold(() => {}, f) }

  def fold(error:AnyFunc, success:AnyFunc) {
    failures.flatten match {
      case Nil => success()
      case xs => {
            xs.foreach(_.apply)
            error()
      }
    }
  }
}