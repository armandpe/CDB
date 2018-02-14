package com.excilys.cdb.Model;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Computer implements ModelClass {
	private long id;
	private String name;
	private LocalDate introduced;
	private LocalDate discontinued;
	private long companyId;
	
	public Computer() {}
	
	public Computer(long id, String name, LocalDate introduced, 
			LocalDate discontinued, long companyId) {
		this.id = id;
		this.name = name;
		this.introduced = introduced;
		this.discontinued = discontinued;
		this.companyId = companyId;
	}
	
	@Override
	public String toString() {
		String res = "Computer " + this.name + "(" + this.id + ") : \n";
		
		Field [] attributes =  Computer.class.getDeclaredFields();
		for(Field attribute : attributes) {
			try {
				res += attribute.getName() + " : " + attribute.get(this) + "\n";
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return res;
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
