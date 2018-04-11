package com.excilys.cdb.service;

import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.Company;

import java.util.List;

public interface ICompanyService extends Service<Company> {
    List<Company> getAll() throws FailedDAOOperationException;

    long getCount() throws FailedDAOOperationException;
}
