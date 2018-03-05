package main.java.com.excilys.cdb.validator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import main.java.com.excilys.cdb.service.CompanyService;

public class ComputerValidator {

	public static void check(String computerName, String introduced, String discontinued, String companyId) throws InvalidDateException, InvalidNameException, InvalidIdException {
		checkName(computerName);
		if (checkDate(introduced).compareTo(checkDate(discontinued)) > 0) {
			throw new InvalidDateException("Discontinued date is prior to introduced date");
		}
		checkCompanyId(companyId);
	}
	
	public static void checkName(String computerName) throws InvalidNameException {
		if (computerName == null) {
			throw new InvalidNameException("Null name");
		} else if (computerName.trim() == null || computerName.trim() == "") {
			throw new InvalidNameException("Empty name");
		}
	}
	
	public static LocalDate checkDate(String date) throws InvalidDateException {
		
		if (date == null) {
			throw new InvalidDateException("Null date");
		}
		
		LocalDate parsedDate = null;
		try {
			parsedDate = LocalDate.parse(date);
		} catch (DateTimeParseException e) {
			throw new InvalidDateException("Parse error : " + e.getMessage());
		}
		
		if (parsedDate.getYear() < 1970) {
			throw new InvalidDateException("We cannot accept a date prior to 1970");
		}
		return parsedDate;
	}
	
	public static void checkCompanyId(String companyId) throws InvalidIdException {
		
		if (companyId == null) {
			throw new InvalidIdException("Null id");
		}
		
		long id;
		try {
			id = Long.parseLong(companyId);
		} catch (NumberFormatException e) {
			throw new InvalidIdException("Couldn't parse the id");
		}
		
		if (id < 0 || id > CompanyService.getInstance().getCount()) {
			throw new InvalidIdException("Incorrect id value");
		}
		
	}

}
