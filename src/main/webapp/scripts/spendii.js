$(document).ready(function() {
    $('#enabled').hide();
    $('#notices_id').slideUp(10000);
});

function delete_spend(rowId) {
    var node = document.getElementById(rowId);
    node.parentNode.removeChild(node);
}