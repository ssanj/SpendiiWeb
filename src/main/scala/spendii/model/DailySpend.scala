/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.model

case class Spend(val description:String, val cost:Double, val label:String)

case class DailySpend(val date:Long, val spends:Seq[Spend])
