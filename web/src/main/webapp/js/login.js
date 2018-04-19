//Validation
document.getElementById("loginButton").onclick = function () {
	
	var pattern = new RegExp("[!@#$%^&*()<>]");
    
	var enteredValue = new String(document.getElementById("username").value);
    
    if (pattern.test(enteredValue)) {
        alert("Invalid character(s) in the username");
        return false;
    } 

	var enteredValue2 = new String(document.getElementById("password").value);
    
    if (pattern.test(enteredValue2)) {
        alert("Invalid character(s) in the password");
        return false;
    } 

    return true;
};