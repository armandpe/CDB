(function ( $ ) {
	$.fn.alert = function(listMessage) {
		if(listMessage.length > 0) {
			var errors = "Some errors were found : \n\r";
			$.each(listMessage, function(index, value) {
				errors += "- " + value + "\n\r";
			});
			alert(errors);
		}
	};
}( jQuery ));