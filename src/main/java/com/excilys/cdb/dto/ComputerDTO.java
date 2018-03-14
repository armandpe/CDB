package com.excilys.cdb.dto;

public class ComputerDTO {
	
	private long companyId;
	
	private String companyName;

	private String discontinued;

	private long id = 0;

	private String introduced;

	private String name;

	public long getCompanyId() {
		return companyId;
	}

	public String getCompanyName() {
		return companyName;
	}
	
	public String getDiscontinued() {
		return discontinued;
	}

	public long getId() {
		return id;
	}

	public String getIntroduced() {
		return introduced;
	}

	public String getName() {
		return name;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setDiscontinued(String discontinued) {
		this.discontinued = discontinued;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setIntroduced(String introduced) {
		this.introduced = introduced;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
