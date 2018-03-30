package com.excilys.cdb.service;

import com.excilys.cdb.dao.CompanyDAO;
import com.excilys.cdb.dao.DAO;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.dao.ICompanyDAO;
import com.excilys.cdb.model.Company;

@org.springframework.stereotype.Service
public class CompanyService extends Service<Company, CompanyDAO> {
	
	private final ICompanyDAO companyDAO;
	
	private CompanyService(ICompanyDAO computerDAO) { 
		this.companyDAO = computerDAO;
	}
	
	@Override
	public String getDaoClassFullName() {
		return companyDAO.getClass().getName();
	}
	
	public void delete(long id) throws FailedDAOOperationException {
		try {
			companyDAO.deleteById(id);
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
