package com.excilys.cdb.Model;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

public class Computer {
	private long id;
	private String name;
	private Timestamp introduced;
	private Timestamp discontinued;
	private long companyId;
	
	public Computer() {
		this(0, "Unnamed", null, null, 0);
	}
	
	public Computer(long id, String name, Timestamp introduced, 
			Timestamp discontinued, long companyId) {
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

	public Timestamp getIntroduced() {
		return introduced;
	}

	public void setIntroduced(Timestamp introduced) {
		this.introduced = introduced;
	}

	public Timestamp getDiscontinued() {
		return discontinued;
	}

	public void setDiscontinued(Timestamp discontinued) {
		this.discontinued = discontinued;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	
	
}
