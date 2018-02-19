package com.excilys.cdb.Model;

import java.time.LocalDate;

import org.apache.log4j.Logger;

import com.excilys.cdb.ParamDescription;

public class Computer implements ModelClass {
	
	static final Logger logger = Logger.getLogger(Computer.class);
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Computer other = (Computer) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@SQLInfo(name = "id", primaryKey = true)
	private long id;

	@SQLInfo(name = "name")
	private String name;

	@SQLInfo(name = "introduced")
	private LocalDate introduced;

	@SQLInfo(name = "discontinued")
	private LocalDate discontinued;

	@SQLInfo(name = "company_id")
	private long companyId;

	public Computer() {}

	public Computer(@ParamDescription(name = "computer id") long id, @ParamDescription(name = "computer name") String name, @ParamDescription(name = "date of introdution") LocalDate introduced, 
			@ParamDescription(name = "date of discontinuation") LocalDate discontinued, @ParamDescription(name = "company id") long companyId) {
		this.id = id;
		this.name = name;
		this.introduced = introduced;
		this.discontinued = discontinued;
		this.companyId = companyId;
		
		if(this.introduced == null ||this.discontinued == null || introduced.compareTo(discontinued) > 0)
			throw new IllegalArgumentException("Introduced date superior to discontinued date");
		if(introduced.getYear() < 1970 || discontinued.getYear() < 1970) {
			throw new IllegalArgumentException("A date was inferior to 1970 - not managed by the database");
		}
	}

	public Computer(String name,  LocalDate introduced, LocalDate discontinued,  long companyId) {
		this(0, name, introduced, discontinued, companyId);
	}

	@Override
	public String toString() {
		return "Computer [id=" + id + ", name=" + name + ", introduced=" + introduced + ", discontinued=" + discontinued
				+ ", companyId=" + companyId + "]\n";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getIntroduced() {
		return introduced;
	}

	public void setIntroduced(LocalDate localDate) {
		this.introduced = localDate;
	}

	public LocalDate getDiscontinued() {
		return discontinued;
	}

	public void setDiscontinued(LocalDate discontinued) {
		this.discontinued = discontinued;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}


}
