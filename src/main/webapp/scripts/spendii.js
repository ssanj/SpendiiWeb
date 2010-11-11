$(document).ready(function() {
    $('#enabled').hide();
});

function delete_spend(rowName) {
	var row = '#' + rowName;
 	$(row).animate({'backgroundColor' : 'red'}, 'slow', function() { removeRow($(this)); });

   function removeRow(row) {
   	$(row).fadeOut('slow', function() { updateTotal(row); $(row).remove(); renumber(); });
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
