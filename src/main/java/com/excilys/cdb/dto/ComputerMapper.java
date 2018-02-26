package main.java.com.excilys.cdb.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import main.java.com.excilys.cdb.model.Computer;

public class ComputerMapper {
	
	public static Computer toComputer(ComputerDTO dto) {
				
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		formatter = formatter.withLocale(Locale.FRANCE); 
		
		Computer.ComputerBuilder builder = new Computer.ComputerBuilder().withId(dto.getId()).withCompanyId(dto.getCompanyId()).withName(dto.getName());
		builder.withDiscontinued(dto.getDiscontinued() == null ? null : LocalDate.parse(dto.getDiscontinued(), formatter));
		builder.withIntroduced(dto.getIntroduced() == null ? null : LocalDate.parse(dto.getIntroduced(), formatter));
		
		return builder.build();
	}
	
	public static ComputerDTO toDTO(Computer computer) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		formatter = formatter.withLocale(Locale.FRANCE); 
		
		ComputerDTO dto = new ComputerDTO();
		
		dto.setDiscontinued(computer.getDiscontinued().isPresent() ? computer.getDiscontinued().get().format(formatter) : null);
		dto.setIntroduced(computer.getIntroduced().isPresent() ? computer.getIntroduced().get().format(formatter) : null);
		dto.setCompanyId(computer.getCompanyId().isPresent() ? computer.getCompanyId().get() : 0);
		dto.setId(computer.getId());
		dto.setName(computer.getName());
		
		return dto;
	}

}
