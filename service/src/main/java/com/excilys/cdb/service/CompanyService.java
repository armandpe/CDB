package com.excilys.cdb.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.dao.ICompanyDAO;
import com.excilys.cdb.dao.IComputerDAO;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

@org.springframework.stereotype.Service
public class CompanyService implements ICompanyService {
	
	private final ICompanyDAO companyDAO;
	
	private final IComputerDAO computerDAO;

    @SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CompanyService(ICompanyDAO companyDAO, IComputerDAO computerDAO) {
		this.companyDAO = companyDAO;
		this.computerDAO = computerDAO;
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

	@Override
	@Transactional
	public void delete(long id) throws FailedDAOOperationException {
        boolean currentMethodException = false;
		try {
        	if (companyDAO.getById(id).isPresent()) {
        		for(Computer computer : computerDAO.getAllByCompany(id)) {
        			computerDAO.deleteById(computer.getId());
        		}
            	companyDAO.delete(id);
        	} else {
        		currentMethodException = true;
        		FailedDAOOperationException e = new FailedDAOOperationException();
        		e.setMessage(getDaoClassFullName() + " : delete method failed - invalid comapny id");
        	}
        } catch (FailedDAOOperationException e) {
        	if (!currentMethodException) {
        		e.setMessage(getDaoClassFullName() + " : delete method failed");
        	}
            throw e;
        }
	}
}
