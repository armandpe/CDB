package com.excilys.cdb.service;

import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.dao.ICompanyDAO;
import com.excilys.cdb.model.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class CompanyService implements ICompanyService {
	
	private final ICompanyDAO companyDAO;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private CompanyService(ICompanyDAO computerDAO) {
		this.companyDAO = computerDAO;
	}
	
	@Override
	public String getDaoClassFullName() {
		return companyDAO.getClass().getName();
	}

	@Override
	public List<Company> getAll() throws FailedDAOOperationException {
		try {
			return companyDAO.getAll();
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : Get all method failed ");
			throw e;
		}
	}

    @Override
    public Optional<Company> getById(long id) throws FailedDAOOperationException {
        try {
            return companyDAO.getById(id);
        } catch (FailedDAOOperationException e) {
            e.setMessage(getDaoClassFullName() + " : Get by id method failed ");
            throw e;
        }
    }

    @Override
    public long getCount() throws FailedDAOOperationException {
        try {
            return companyDAO.getCount();
        } catch (FailedDAOOperationException e) {
            e.setMessage(getDaoClassFullName() + " : Get by id method failed ");
            throw e;
        }
    }
}
