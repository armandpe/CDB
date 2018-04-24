package com.excilys.cdb.dao;

import java.util.List;

import com.excilys.cdb.model.Company;

public interface ICompanyDAO extends DAO<Company> {
	List<Company> getAll() throws FailedDAOOperationException;

	long getCount() throws FailedDAOOperationException;
	
    void delete(long id) throws FailedDAOOperationException;
    
    public void create(Company company) throws FailedDAOOperationException;
    
    public void update(Company company) throws FailedDAOOperationException;
}
