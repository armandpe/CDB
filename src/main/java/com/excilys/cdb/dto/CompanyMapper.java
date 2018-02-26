package main.java.com.excilys.cdb.dto;

import main.java.com.excilys.cdb.model.Company;

public class CompanyMapper {
	
	public static Company toCompany(CompanyDTO dto) {
		
		return new Company(dto.getId(), dto.getName());
	}
	
	public static CompanyDTO toDTO(Company company) {
		
		CompanyDTO result = new CompanyDTO();
		result.setId(company.getId());
		result.setName(company.getName());		
		return result;
	}
	
}
