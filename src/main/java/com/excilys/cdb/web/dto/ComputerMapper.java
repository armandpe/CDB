package com.excilys.cdb.web.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.constant.DateConstant;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

public class ComputerMapper {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public static Computer toComputer(ComputerDTO dto) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstant.FORMAT);
		formatter = formatter.withLocale(Locale.FRANCE);

		Computer.ComputerBuilder builder = new Computer.ComputerBuilder().withId(dto.getId()).withName(dto.getName());
		builder.withDiscontinued(
				isNullOrEmpty(dto.getDiscontinued()) ? null : LocalDate.parse(dto.getDiscontinued(), formatter));
		builder.withIntroduced(
				isNullOrEmpty(dto.getIntroduced()) ? null : LocalDate.parse(dto.getIntroduced(), formatter));
		builder.withCompany(dto.getCompanyId() == 0 ? null : new Company(dto.getCompanyId(), dto.getCompanyName()));
		return builder.build();
	}

	public static ComputerDTO toDTO(Computer computer) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstant.FORMAT);
		formatter = formatter.withLocale(Locale.FRANCE);

		ComputerDTO dto = new ComputerDTO();
		dto.setDiscontinued(
				computer.getDiscontinued().isPresent() ? computer.getDiscontinued().get().format(formatter) : null);
		dto.setIntroduced(
				computer.getIntroduced().isPresent() ? computer.getIntroduced().get().format(formatter) : null);
		dto.setCompanyId(computer.getCompany().isPresent() ? computer.getCompany().get().getId() : 0);
		dto.setCompanyName(computer.getCompany().isPresent() ? computer.getCompany().get().getName() : null);
		dto.setId(computer.getId());
		dto.setName(computer.getName());

		return dto;
	}

	public static ComputerDTO toDTO(String name, String introduced, String discontinued, String companyId) {
		ComputerDTO dto = new ComputerDTO();
		dto.setName(name);
		dto.setIntroduced(introduced);
		dto.setDiscontinued(discontinued);
		dto.setCompanyId(companyId == null ? 0 : Long.parseLong(companyId));
		dto.setId(0);

		return dto;
	}

	public static ComputerDTO toDTO(String id, String name, String introduced, String discontinued, String companyId) {
		ComputerDTO dto = toDTO(name, introduced, discontinued, companyId);
		dto.setId(Long.parseLong(id));
		return dto;
	}


	private static boolean isNullOrEmpty(String s) {
		if (s == null || s.equals("")) {
			return true;
		}
		return false;
	}

}
