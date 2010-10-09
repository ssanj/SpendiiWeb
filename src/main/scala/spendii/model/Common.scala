/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.model

import java.util.{Calendar => Cal}

object Common {

  def formattedDate(date: Long): String =  String.format("%1$tA %1$te %1$tB %1$tY", {
    val cal = Cal.getInstance
    cal.setTimeInMillis(date)
    cal
  })
}