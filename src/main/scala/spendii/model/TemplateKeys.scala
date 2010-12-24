/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package spendii.model

object TemplateKeys {

  object Information {
    val notices_id = "notices_id"
    val warnings_id = "warnings_id"
    val errors_id = "errors_id"
  }

  object SaveSpendFormLabels {
      val form_error = "save_form_error"
      val label_error = "label_error"
      val cost_error = "cost_error"
      val description_error = "description_error"
  }

  object LoadSpendFormLabels {
      val load_form_error = "load_form_error"
      val load_form_error_container = "load_form_error_container"
      val spend_deletion_error = "spend_deletion_error"
  }
}