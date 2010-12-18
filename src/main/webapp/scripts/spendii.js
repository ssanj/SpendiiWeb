$(document).ready(function() {
    $('#enabled').hide();
    $('#edit_button').hide();
    $('#save_button').show();
    hideSaveFormErrorsIfNoErrrors();
    hideLoadFormErrorsIfNoErrors();
});

function delete_spend(total, rowName) {
	var row = '#' + rowName;
 	$(row).animate({'backgroundColor' : 'red'}, 'fast', function() { removeRow($(this)); });

   function removeRow(row) {
   	$(row).fadeOut('fast', function() { updateTotal(); $(row).remove(); renumber(); });
   }

   function updateTotal() {
    var totalTag = $('span.total');
    totalTag.fadeOut('fast', function() { $(this).text('$' + total).fadeIn();})
   }

   function renumber() {
    $('tbody tr').each(function() { $(this).find('td:first').text($(this).index() + 1);	});
   }
}

function show_ajax_error(id, message) {
 $('#'+id).fadeIn().text(message);
}

function update_form_for_edit(description, cost, label) {

  setHiddenFormValues();
  setUserFormValues();
  showEditButton();

  function setHiddenFormValues() {
      $('#odescription').val(description);
      $('#ocost').val(cost);
      $('#olabel').val(label);
  }

  function setUserFormValues() {
      $('#description').text(description);
      $('#cost').val(cost);
      $('#label').val(label);
  }

  function showEditButton() {
      $('#save_button').hide();
      $('#edit_button').show();
  }
}

function hideSaveFormErrorsIfNoErrrors() {
   hideFormError("save_form_error", "save_form_error_container");
}

function hideLoadFormErrorsIfNoErrors() {
    hideFormError("load_form_error", "load_form_error_container");
}

function hideFormError(spanId, parentId) {
	var text = $('#' + spanId).text();
    if (isWhitespaceOrEmpty(text)) {
		$('#' + parentId).hide();
    }
}

function isWhitespaceOrEmpty(text) {
   return !/[^\s]/.test(text);
}
