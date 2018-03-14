package com.excilys.cdb.service;

import com.excilys.cdb.ParamDescription;
import com.excilys.cdb.dao.CompanyDAO;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.Company;

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
	
	@ServiceMethod(name = "Remove a company (based on id)")
	public void delete(@ParamDescription(name = "id of the company ") long id) throws FailedDAOOperationException {
		try {
			((CompanyDAO) getDAO()).delete(id);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : delete company failed ");
			throw e;
		}
	}
	
}
