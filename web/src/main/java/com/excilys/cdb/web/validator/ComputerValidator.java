package com.excilys.cdb.web.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.excilys.cdb.constant.DateConstant;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.web.dto.ComputerDTO;
import com.excilys.cdb.web.spring.controller.PageData;

@Component
public class ComputerValidator implements Validator {

	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ComputerService computerService;

	public ComputerValidator(ComputerService computerService) {
		this.computerService = computerService;
	}

	private void checkId(long id, Errors errors) {

		if (id > 0) {
			try {
				if (!computerService.getById(id).isPresent()) {
					errors.reject("Cannot find requested computer");
				}
			} catch (FailedDAOOperationException e) {
				errors.reject("Invalid computer id");
			}
		} else if (id < 0) {
			errors.reject("Invalid computer id");
		}
	}

	public void checkCompanyId(long companyId) throws InvalidIdException {

		if (companyId < 0) {
			try {
				if (!computerService.getById(companyId).isPresent()) {
					throw new InvalidIdException("Incorrect id value");
				}
			} catch (FailedDAOOperationException e) {
				throw new InvalidIdException("Incorrect id value");
			}
		}
	}

	public LocalDate checkDate(String date) throws InvalidDateException {

		if (date == null) {
			throw new InvalidDateException("Null date");
		}

		if (date.equals("")) {
			return null;
		}

		LocalDate parsedDate = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstant.FORMAT);
			formatter = formatter.withLocale(Locale.FRANCE);
			parsedDate = LocalDate.parse(date, formatter);
		} catch (DateTimeParseException e) {
			throw new InvalidDateException("Parse error : " + e.getMessage());
		}

		if (parsedDate.getYear() < 1970) {
			throw new InvalidDateException("We cannot accept a date prior to 1970");
		}

		return parsedDate;
	}

	public void checkName(String computerName) throws InvalidNameException {
		if (computerName == null) {
			throw new InvalidNameException("Null name");
		} else if (computerName.trim() == "") {
			throw new InvalidNameException("Empty name");
		} else {
			Pattern regex = Pattern.compile("[$,:;=?@#|'<>.^*()%!-]");
			Matcher matcher = regex.matcher(computerName);
			if (matcher.find()) {
				throw new InvalidNameException("The name contains some special characters");
			}
		}
	}

	@Override
	public boolean supports(Class<?> type) {
		return ComputerDTO.class.equals(type) || PageData.class.equals(type);
	}

	@Override
	public void validate(Object object, Errors errors) {
		if (object instanceof PageData) {
			return;
		}
		
		ComputerDTO computerDTO = (ComputerDTO) object;

		checkId(computerDTO.getId(), errors);

		try {
			checkName(computerDTO.getName());
		} catch (InvalidNameException e) {
			errors.reject(e.getMessage());
		}

		LocalDate introducedDate = null;
		try {
			introducedDate = checkDate(computerDTO.getIntroduced());
		} catch (InvalidDateException e) {
			errors.reject(e.getMessage());
		}
		LocalDate discontinuedDate = null;
		try {
			discontinuedDate = checkDate(computerDTO.getDiscontinued());
		} catch (InvalidDateException e) {
			errors.reject(e.getMessage());
		}

		if (introducedDate != null && discontinuedDate != null && introducedDate.compareTo(discontinuedDate) > 0) {
			errors.reject("Discontinued date is prior to introduced date");
		}

		try {
			checkCompanyId(computerDTO.getCompanyId());
		} catch (InvalidIdException e) {
			errors.reject(e.getMessage());
		}
	}

}
