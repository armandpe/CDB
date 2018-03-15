package com.excilys.cdb.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.excilys.cdb.ParamDescription;
import com.excilys.cdb.dao.CompanyDAO;
import com.excilys.cdb.dao.DAO;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.Company;

@org.springframework.stereotype.Service
@ServiceClass(name = "companies")
public class CompanyService extends Service<Company, CompanyDAO> {
	
	@Autowired
	private CompanyDAO companyDAO;
	
	@Override
	public String getDaoClassFullName() {
		return companyDAO.getClass().getName();
	}
	
	@ServiceMethod(name = "Remove a company (based on id)")
	public void delete(@ParamDescription(name = "id of the company ") long id) throws FailedDAOOperationException {
		try {
			companyDAO.delete(id);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : delete company failed ");
			throw e;
		}
	}

	@Override
	public DAO<Company> getDAO() {
		return companyDAO;
	}
	
}
