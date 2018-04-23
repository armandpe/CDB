package com.excilys.cdb.web.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.excilys.cdb.model.User;
import com.excilys.cdb.service.UserService;

@Component
public class UserValidator implements Validator {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private UserService userService;

	public UserValidator(UserService userService) {
		this.userService = userService;
	}

	public void checkString(String string, String parameterName) throws InvalidInputException {
		if (string == null) {
			throw new InvalidInputException("Null " + parameterName);
		} else if (string.trim() == "") {
			throw new InvalidInputException("Empty " + parameterName);
		} else {
			Pattern regex = Pattern.compile("[$,:;=?@#|'<>.^*()%!-]");
			Matcher matcher = regex.matcher(string);
			if (matcher.find()) {
				throw new InvalidInputException("The " + parameterName + " contains some special characters");
			}
		}
	}

	@Override
	public boolean supports(Class<?> type) {
		logger.error("User validator analysing : " + type.getName());

		return User.class.equals(type);
	}

	@Override
	public void validate(Object object, Errors errors) {
		logger.error("User validator analysing : " + object.getClass().getName());
		
		User user = (User) object;

		try {
			checkString(user.getUsername(), "username");
			if (userService.getByName(user.getUsername()).isPresent()) {
				throw new InvalidInputException("The username is already taken");
			}
		} catch (InvalidInputException e) {
			errors.reject(e.getMessage());
		}

		try {
			checkString(user.getPassword(), "password");
		} catch (InvalidInputException e) {
			errors.reject(e.getMessage());
		}
	}

}
