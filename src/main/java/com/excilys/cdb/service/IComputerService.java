package com.excilys.cdb.service;

import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.Computer;

import java.util.List;

public interface IComputerService extends Service<Computer> {
    void create(Computer computer) throws FailedDAOOperationException;

    void update(Computer computer) throws FailedDAOOperationException;

    List<Computer> getAll(long offset, long limit, String search, ComputerOrderBy orderByVar, boolean asc) throws FailedDAOOperationException;

    long getCount(String search) throws FailedDAOOperationException;

    void deleteById(long id) throws FailedDAOOperationException;
}
