/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.mongo

import com.mongodb.WriteResult
import MongoTypes._

trait MongoWriteResultTrait {

  case class MongoWriteResult(wr:WriteResult) {
    def getMongoError: Option[MongoError] = {
      val error = wr.getError
      if (error == null) None else Some(MongoError(error, wrapWith(wr.getLastError.getException.getStackTraceString).toString))
    }
  }

  object MongoWriteResult {
    implicit def writeResultToMongoWriteResult(wr:WriteResult): MongoWriteResult = MongoWriteResult(wr)
  }

}