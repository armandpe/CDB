package com.excilys.cdb.Service;

import com.excilys.cdb.DAO.CompanyDAO;
import com.excilys.cdb.Model.Company;

public class CompanyService extends Service<Company, CompanyDAO>{
	
	private static CompanyService service;
	
	private CompanyService() { daoClassName = "com.excilys.cdb.DAO.CompanyDAO"; }
	
	public static CompanyService getInstance() {
		if(service == null) {
			service = new CompanyService();
		}
		return service;
	}
	
}
