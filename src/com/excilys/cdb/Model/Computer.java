package com.excilys.cdb.Model;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.excilys.cdb.ParamDescription;

public class Computer implements ModelClass {
	
	final Logger logger = Logger.getLogger(this.getClass());
	
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
	}

	public Computer(String name,  LocalDate introduced, LocalDate discontinued,  long companyId) {
		this(0, name, introduced, discontinued, companyId);
	}

	@Override
	public String toString() {
		String res = "Computer " + this.name + "(" + this.id + ") : \n";

		Field [] attributes =  Computer.class.getDeclaredFields();
		for(Field attribute : attributes) {

			try {
				res += attribute.getName() + " : " + attribute.get(this) + "\n";
			} catch (IllegalArgumentException | IllegalAccessException e) {
				final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
				String methodName = ste[1].getMethodName(); 
				logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
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
