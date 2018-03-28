package com.excilys.cdb.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@SQLTable(name = "company")
@Entity
public class Company implements ModelClass {
	
	@SQLInfo(name = "id", primaryKey = true)
	@Column
	private Long id;

	@SQLInfo(name = "name", searchable = true)
	@Column
	private String name;

	public Company(long id, String name) {
		this.id = id;
		this.name = name;
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
		Company other = (Company) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}
	
	public long getId() {
		return id;
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

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		String res = "Company " + this.name + "(" + this.id + ")\n";
		
		return res;
	}
	
}
