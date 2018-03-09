package main.java.com.excilys.cdb.model;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.cdb.ParamDescription;

@SQLTable(name = "computer")
public class Computer implements ModelClass {
	
	public static class ComputerBuilder {
		
		private Optional<Company> company = Optional.empty();

		private Optional<LocalDate> discontinued = Optional.empty();

		private long id = 0;

		private Optional<LocalDate> introduced = Optional.empty();

		private String name = "Unnamed";
		
		public Computer build() {
			return new Computer(id, name, introduced, discontinued, company);
		}
		
		public ComputerBuilder withCompany(Company company) {
			this.company = Optional.ofNullable(company);
			return this;
		}
		
		public ComputerBuilder withDiscontinued(LocalDate discontinued) {
			this.discontinued = Optional.ofNullable(discontinued);
			return this;
		}
		
		public ComputerBuilder withId(long id) {
			this.id = id;
			return this;
		}
		
		public ComputerBuilder withIntroduced(LocalDate introduced) {
			this.introduced = Optional.ofNullable(introduced);
			return this;
		} 
		
		public ComputerBuilder withName(String name) {
			this.name = name;
			return this;
		}
	}

	static final Logger LOGGER = LoggerFactory.getLogger(Computer.class);

	@SQLInfo(name = "company_id", foreignKey = true, searchable = true)
	private Optional<Company> company;

	@SQLInfo(name = "discontinued")
	private Optional<LocalDate> discontinued;

	@SQLInfo(name = "id", primaryKey = true)
	private long id;

	@SQLInfo(name = "introduced")
	private Optional<LocalDate> introduced;

	@SQLInfo(name = "name", searchable = true)
	private String name;

	public Computer(@ParamDescription(name = "computer id") long id, 
					@ParamDescription(name = "computer name") String name, 
					@ParamDescription(name = "date of introdution", optional = true) Optional<LocalDate> introduced, 
					@ParamDescription(name = "date of discontinuation", optional = true) Optional<LocalDate> discontinued, 
					@ParamDescription(name = "company", optional = true) Optional<Company> company) {
		this.id = id;
		this.name = name;
		this.introduced = introduced;
		this.discontinued = discontinued;
		this.company = company;
		
		if (this.introduced.isPresent() && this.discontinued.isPresent() && introduced.get().compareTo(discontinued.get()) > 0) {
			throw new IllegalArgumentException("Introduced date superior to discontinued date");
		}
		if ((introduced.isPresent() && introduced.get().getYear() < 1970) || (discontinued.isPresent() && discontinued.get().getYear() < 1970)) {
			throw new IllegalArgumentException("A date was inferior to 1970 - not managed by the database");
		}
	}

	@SuppressWarnings("unused")
	private Computer() { }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Computer other = (Computer) obj;
		if (id != other.id || !(other.company.equals(company)) || !other.discontinued.equals(discontinued) || !other.introduced.equals(introduced) || !other.name.equals(name)) {
			return false;
		}
				
		return true;
	}
	
	public Optional<Company> getCompany() {
		return company;
	}

	public Optional<LocalDate> getDiscontinued() {
		return discontinued;
	}

	public long getId() {
		return id;
	}

	public Optional<LocalDate> getIntroduced() {
		return introduced;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	public void setCompany(Optional<Company> company) {
		this.company = company;
	}

	public void setDiscontinued(Optional<LocalDate> discontinued) {
		this.discontinued = discontinued;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setIntroduced(Optional<LocalDate> localDate) {
		this.introduced = localDate;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Computer [id=" + id + 
				", name=" + name + 
				(introduced.isPresent() ? ", introduced=" + introduced.get() : "") + 
				(discontinued.isPresent() ? ", discontinued=" + discontinued.get() : "") + 
				(company.isPresent() ? ", company=" + company.get() : "") + 
				"]\n";
	}
}
