
document.getElementById("addButton").onclick = function () {
	
    var enteredValue = new String(document.getElementById("computerName").value);
    
    var pattern = new RegExp("[!@#$%^&*()<>]");
    
    if (pattern.test(enteredValue)) {
        alert("Invalid character(s) in the computer name");
        return false;
    } 
    
    return true;
};