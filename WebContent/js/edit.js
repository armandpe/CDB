//Validation
document.getElementById("editButton").onclick = function () {
	
    var enteredValue = new String(document.getElementById("computerName").value);
    
    var pattern = new RegExp("[!@#$%^&*()<>]");
    
    if (pattern.test(enteredValue)) {
        alert("Invalid character(s) in the computer name");
        return false;
    } 
    
    return true;
};

//Errors
(function ( $ ) {

	$.fn.alert = function(listMessage) {
		
		if(listMessage.length > 0) {
			var errors = "";
			
			$.each(listMessage, function(index, value) {
				errors += index + " : " + value + "\n";
			});
			alert(errors);
		}
	};
}( jQuery ));