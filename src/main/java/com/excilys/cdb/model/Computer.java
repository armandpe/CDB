package com.excilys.cdb.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "computer")
public class Computer implements ModelClass {
	
	public static class ComputerBuilder {
		
		private Company company;

		private LocalDate discontinued;

		private long id = 0;

		private LocalDate introduced;

		private String name = "Unnamed";
		
		public Computer build() {
			return new Computer(id, name, introduced, discontinued, company);
		}
		
		public ComputerBuilder withCompany(Company company) {
			this.company = company;
			return this;
		}
		
		public ComputerBuilder withDiscontinued(LocalDate discontinued) {
			this.discontinued = discontinued;
			return this;
		}
		
		public ComputerBuilder withId(long id) {
			this.id = id;
			return this;
		}
		
		public ComputerBuilder withIntroduced(LocalDate introduced) {
			this.introduced = introduced;
			return this;
		} 
		
		public ComputerBuilder withName(String name) {
			this.name = name;
			return this;
		}
	}

	static final Logger LOGGER = LoggerFactory.getLogger(Computer.class);

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "discontinued")
	private LocalDate discontinued;

	@Id
	@Column(name = "id")
	private long id;

    @Column(name = "introduced")
	private LocalDate introduced;

    @Column(name = "name")
	private String name;

    private Computer() { }
    
	private Computer(long id, String name,  LocalDate introduced,  LocalDate discontinued, Company company) {
		this.id = id;
		this.name = name;
		this.introduced = introduced;
		this.discontinued = discontinued;
		this.company = company;
	}

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
	
	public Company getCompany() {
		return company;
	}

	public LocalDate getDiscontinued() {
		return discontinued;
	}

	public long getId() {
		return id;
	}

	public LocalDate getIntroduced() {
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

	public void setCompany(Company company) {
		this.company = company;
	}

	public void setDiscontinued(LocalDate discontinued) {
		this.discontinued = discontinued;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setIntroduced(LocalDate localDate) {
		this.introduced = localDate;
	}

	public void setName(String name) {
		this.name = name;
	}

}
