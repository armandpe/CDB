package main.java.com.excilys.cdb.dto;

import main.java.com.excilys.cdb.model.Company;

public class CompanyMapper {
	
	public static Company getCompany(CompanyDTO dto) {
		
		return new Company(dto.getId(), dto.getName());
	}
	
	public static CompanyDTO getCompanyDTO(Company company) {
		
		CompanyDTO result = new CompanyDTO();
		result.setId(company.getId());
		result.setName(company.getName());		
		return result;
	}
	
}
