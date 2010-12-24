$(document).ready(function() {
    init();
});

function init() {
    $('#enabled').hide();
    $('#edit_button').hide();
    $('#save_button').show();
    hideSaveFormErrorsIfNoErrrors();
    hideLoadFormErrorsIfNoErrors();
}

function delete_spend(total, rowName) {
    $('#load_form_error_container').hide();
	var row = _id(rowName);
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

function show_deletion_error(message) {
 showIfHidden('load_form_error_container');
 $(_id('load_form_error')).fadeIn().text(message);
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

function showIfHidden(id) {
    var element = $(_id(id));
    if (element.is(':hidden')){
        element.show();
    }
}
function _id(id) {
    return "#"+id;
}

function resetLoadFormErrors() {
    $('#load_form_error').text("");
    $('#load_form_error_container').hide();
}

function hideFormError(spanId, parentId) {
	var text = $(_id(spanId)).text();
    if (isWhitespaceOrEmpty(text)) {
		$(_id(parentId)).hide();
    }
}

function isWhitespaceOrEmpty(text) {
   return !/[^\s]/.test(text);
}
