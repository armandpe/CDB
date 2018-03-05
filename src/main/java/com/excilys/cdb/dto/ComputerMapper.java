package main.java.com.excilys.cdb.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import main.java.com.excilys.cdb.dao.CompanyDAO;
import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;

public class ComputerMapper {
	
	public static Computer toComputer(ComputerDTO dto) {
				
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		formatter = formatter.withLocale(Locale.FRANCE); 
		
		Computer.ComputerBuilder builder = new Computer.ComputerBuilder().withId(dto.getId()).withName(dto.getName());
		builder.withDiscontinued(dto.getDiscontinued() == null ? null : LocalDate.parse(dto.getDiscontinued(), formatter));
		builder.withIntroduced(dto.getIntroduced() == null ? null : LocalDate.parse(dto.getIntroduced(), formatter));
		builder.withCompany(dto.getCompanyId() == 0 ? null : new Company(dto.getCompanyId(), dto.getCompanyName()));
		return builder.build();
	}
	
	public static ComputerDTO toDTO(Computer computer) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		formatter = formatter.withLocale(Locale.FRANCE); 
		
		ComputerDTO dto = new ComputerDTO();
		dto.setDiscontinued(computer.getDiscontinued().isPresent() ? computer.getDiscontinued().get().format(formatter) : null);
		dto.setIntroduced(computer.getIntroduced().isPresent() ? computer.getIntroduced().get().format(formatter) : null);
		dto.setCompanyId(computer.getCompany().isPresent() ? computer.getCompany().get().getId() : 0);
		dto.setCompanyName(computer.getCompany().isPresent() ? computer.getCompany().get().getName() : null);
		dto.setId(computer.getId());
		dto.setName(computer.getName());
		
		Optional<Company> company = CompanyDAO.getInstance().getById(computer.getId());
		company.ifPresent(x -> dto.setCompanyName(x.getName()));
		return dto;
	}
	
	public static ComputerDTO toDTO(String name, String introduced, String discontinued, String companyId) {
		ComputerDTO dto = new ComputerDTO();
		dto.setCompanyName(name);
		dto.setIntroduced(introduced);
		dto.setDiscontinued(discontinued);
		dto.setCompanyId(Long.parseLong(companyId));
		dto.setId(0);
		
		return dto;
	}

}
