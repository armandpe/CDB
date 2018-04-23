//Validation
document.getElementById("registerButton").onclick = function () {
	
	var pattern = new RegExp("[!@#$%^&*()<>]");
    
	var enteredValue = new String(document.getElementById("username").value);
    
    if (pattern.test(enteredValue)) {
        alert("Invalid character(s) in the username");
        return false;
    } 

	var password = new String(document.getElementById("password").value);
    
    if (pattern.test(password)) {
        alert("Invalid character(s) in the password");
        return false;
    } 
    
    var password2 = new String(document.getElementById("password2").value);
    if (!(password2.valueOf() === password.valueOf())) {
        alert("Passwords must be the same : " + password2 + " != " + password);
        return false;
    }

    return true;
};