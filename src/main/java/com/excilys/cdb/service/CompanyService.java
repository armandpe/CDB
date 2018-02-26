package main.java.com.excilys.cdb.service;

import main.java.com.excilys.cdb.dao.CompanyDAO;
import main.java.com.excilys.cdb.model.Company;

@ServiceClass(name = "companies")
public class CompanyService extends Service<Company, CompanyDAO> {
	
	private static CompanyService service;
	
	public static CompanyService getInstance() {
		if (service == null) {
			service = new CompanyService();
		}
		return service;
	}
	
	private CompanyService() { }
	
	@Override
	public String getDaoClassFullName() {
		return CompanyDAO.class.getName();
	}
	
}
