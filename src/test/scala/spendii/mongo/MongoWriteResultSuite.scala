/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package spendii.mongo

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

final class MongoWriteResultSuite extends FunSuite with ShouldMatchers with MongoWriteResultTrait {

  test("A MongoWriteResult should a Some(MongoError) if there is a error") {
    MongoWriteResult(new WriteResultTrait {
      def getError = Some("There was an error")
      def getLastErrorTrace = Some("Some exception was thrown")
    }).getMongoError match {
      case Some(me) => {
        me.message should equal ("There was an error")
        me.stackTrace should equal ("Some exception was thrown")
      }
      case None => fail("Expected a MongoError")
    }
  }
}