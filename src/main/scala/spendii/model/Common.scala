/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.model

import java.util.{Calendar => Cal}
import Cal._

object Common {

  def formattedDate(date: Long): String =  String.format("%1$tA %1$te %1$tB %1$tY", {
    val cal = Cal.getInstance
    cal.setTimeInMillis(date)
    cal
  })

  def removeTime(cal:Cal): Cal = {
    cal.set(HOUR, 0)
    cal.set(MINUTE, 0)
    cal.set(SECOND, 0)
    cal.set(MILLISECOND, 0)
    cal
  }

  def currentDate: Cal = removeTime(Cal.getInstance)

  def currentDateAsTime: Long = currentDate.getTimeInMillis

  def currentDateAsString = formattedDate(currentDateAsTime)
}