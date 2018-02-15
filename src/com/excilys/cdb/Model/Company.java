package com.excilys.cdb.Model;

public class Company implements ModelClass {
	
	@SQLInfo(name = "id", primaryKey = true)
	private long id;
	
	@SQLInfo(name = "name")
	private String name;
	
	public Company() {}
	
	public Company(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String toString() {
		String res = "Company " + this.name + "(" + this.id + ")\n";
		
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
	
}
