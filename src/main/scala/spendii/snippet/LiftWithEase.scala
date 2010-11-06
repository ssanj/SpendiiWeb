/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.snippet

import net.liftweb.http.S
import spendii.model.TemplateKeys.Information._
import xml.NodeSeq
/**
 * Provides functions to help in the ease-of-use of Lift.
 */
object LiftWithEase {

  def notice(notice:String) { S.notice(notices_id, notice)  }

  def notice(notice:NodeSeq) { S.notice(notices_id, notice)  }

  def warning(warning:String) { S.warning(warnings_id, warning)  }

  def warning(warning:NodeSeq) { S.warning(warnings_id, warning)  }

  def error(error:String) { S.error(errors_id, error)  }

  def error(label:String, error:String) { S.error(label, error)  }

  def error(label:String, error:NodeSeq) { S.error(label, error)  }

  def error(error:NodeSeq) { S.error(errors_id, error)  }
}