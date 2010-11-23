/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.validate

import collection.mutable.ListBuffer

trait Validator[T] {
  def validate(value:T, f:() => Any): Option[() => Any]
}

object Validator {
  implicit object EmptyStringValidator extends Validator[String] {
    def validate(value:String, f:() => Any): Option[() => Any] = if (value.trim.isEmpty) Some(f) else None
  }

  implicit object PositiveNonZeroDoubleValidator extends Validator[Double] {
    def validate(value:Double, f:() => Any): Option[() => Any] = if (value <= 0.0D) Some(f) else None
  }

  object StringToDoubleValidator extends Validator[String] {
    def validate(value:String, f:() => Any): Option[() => Any] = {
      try {
        value.toDouble
        None
      } catch { case _ => Some(f) }
    }
  }
}

final class FailureCollector {

  val buffer = ListBuffer[Option[() => Any]]()

  def collect[V](value:V, f:() => Any) (implicit validator:Validator[V]): FailureCollector = {
    buffer += validator.validate(value, f)
    this
  }

  def collect[V](value:V, e:() => Any, s:(V) => Any) (implicit validator:Validator[V]): FailureCollector = {
    val result:Option[() => Any] = validator.validate(value, e)
    buffer += result
    if (result.isEmpty) s(value) else {}
    this
  }

  def onSuccess(f2:() => Any) {
    buffer.toSeq.flatten match {
      case Nil => f2()
      case xs => xs.foreach(_.apply)
    }
  }

  def validateAll { buffer.toSeq.flatten.foreach(_.apply) }
}