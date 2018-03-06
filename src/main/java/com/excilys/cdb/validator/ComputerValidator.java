package main.java.com.excilys.cdb.validator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.excilys.cdb.service.CompanyService;

public class ComputerValidator {

	private static List<InvalidInputException> exceptions = new ArrayList<>();

	public static void check(String computerName, String introduced, String discontinued, String companyId)
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

	public static LocalDate checkDate(String date) throws InvalidDateException {

		if (date == null) {
			throw new InvalidDateException("Null date");
		}

		if (date.equals("")) {
			return null;
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

	public static void checkName(String computerName) throws InvalidNameException {
		if (computerName == null) {
			throw new InvalidNameException("Null name");
		} else if (computerName.trim() == null || computerName.trim() == "") {
			throw new InvalidNameException("Empty name");
		}
	}

	public static List<InvalidInputException> getExceptions() {
		return exceptions;
	}

}
