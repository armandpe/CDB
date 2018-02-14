package com.excilys.cdb.Service;

import com.excilys.cdb.DAO.CompanyDAO;
import com.excilys.cdb.DAO.ComputerDAO;
import com.excilys.cdb.Model.Company;

public class CompanyService extends Service<Company, CompanyDAO>{
	
	private static CompanyService service;
	
	private CompanyService() {}
	
	public static CompanyService getInstance() {
		if(service == null) {
			service = new CompanyService();
		}
		return service;
	}
	
	@Override
	public String getDaoClassFullName() {
		return CompanyDAO.class.getName();
	}
	
}
