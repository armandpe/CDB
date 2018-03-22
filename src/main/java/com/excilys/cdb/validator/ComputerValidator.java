package com.excilys.cdb.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.excilys.cdb.constant.DateConstant;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.service.ComputerService;

@Component
public class ComputerValidator {

	private List<InvalidInputException> exceptions = new ArrayList<>();
	
	private ComputerService computerService;

	public ComputerValidator(ComputerService computerService) {
		this.computerService = computerService;
	}
	
	public void check(String computerName, String introduced, String discontinued, long companyId)
			throws InvalidInputException {

		exceptions.clear();

		try {
			checkName(computerName);
		} catch (InvalidNameException e) {
			exceptions.add(e);
		}

		LocalDate introducedDate = null;
		try {
			introducedDate = checkDate(introduced);
		} catch (InvalidDateException e) {
			exceptions.add(e);
		}
		LocalDate discontinuedDate = null;
		try {
			discontinuedDate = checkDate(discontinued);
		} catch (InvalidDateException e) {
			exceptions.add(e);
		}

		try {
			if (introducedDate != null && discontinuedDate != null && introducedDate.compareTo(discontinuedDate) > 0) {
				throw new InvalidDateException("Discontinued date is prior to introduced date");
			}
		} catch (InvalidDateException e) {
			exceptions.add(e);
		}
		try {
			checkCompanyId(companyId);
		} catch (InvalidIdException e) {
			exceptions.add(e);
		}

		if (!exceptions.isEmpty()) {
			throw new InvalidInputException("Some input were invalid : " + exceptions.toString());
		}
	}
	
	public void check(long id, String computerName, String introduced, String discontinued, long companyId)
			throws InvalidInputException {
		
		checkId(id);
		
		check(computerName, introduced, discontinued, companyId);
	}

	private void checkId(long id) throws InvalidIdException {
		
		if (id < 0 && id != 0) {
			try {
				if (!computerService.getById(id).isPresent()) {
					throw new InvalidIdException("Incorrect id value");
				}
			} catch (FailedDAOOperationException e) {
				throw new InvalidIdException("Incorrect id value");
			}
		}
		
	}

	public void checkCompanyId(long companyId) throws InvalidIdException {

		if (companyId < 0 && companyId != 0) {
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
		} else if (computerName.trim() == null || computerName.trim() == "") {
			throw new InvalidNameException("Empty name");
		}
	}

	public List<InvalidInputException> getExceptions() {
		return exceptions;
	}

}
