$(document).ready(function() {
    $('#enabled').hide();
    $('#notices_id').slideUp(10000);
});

function delete_spend(rowId) {
   var node = document.getElementById(rowId);
   var parent = node.parentNode
   parent.removeChild(node);

   var index = 1;
   for (var x=0; x < parent.childNodes.length; x++) {
    var row = parent.childNodes[x];
    for (var y=0; y < row.childNodes.length; y++) {
      if (row.childNodes[y] instanceof HTMLTableCellElement) {
        if (row.childNodes[y].firstChild.data != '#') {
            row.childNodes[y].firstChild.data = index++;
        }
        break;
     }
    }
   }
}