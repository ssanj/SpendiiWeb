$(document).ready(function() {
    $('#enabled').hide();
    $('#edit_button').hide();
    $('#save_button').show();
});

function delete_spend(rowName) {
	var row = '#' + rowName;
 	$(row).animate({'backgroundColor' : 'red'}, 'fast', function() { removeRow($(this)); });

   function removeRow(row) {
   	$(row).fadeOut('fast', function() { updateTotal(row); $(row).remove(); renumber(); });
   }

   function updateTotal(row) {
   	var rowValue = parseFloat($(row).find('td:nth-child(3)').text());
    var totalTag = $('span.total');
   	var oldTotal = parseFloat(totalTag.text().substring(1)); //jump over the $ sign
   	totalTag.text('$' + (oldTotal - rowValue).toFixed(1));//1 decimal place
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
