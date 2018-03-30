package com.excilys.cdb.dao;

import java.util.List;

import com.excilys.cdb.model.Computer;
import com.excilys.cdb.service.ComputerOrderBy;

public interface IComputerDAO extends DAO<Computer> {
	
	void create(Computer computer) throws FailedDAOOperationException;

	void update(Computer computer) throws FailedDAOOperationException;
	
	List<Computer> getAll(long offset, long limit, String search, ComputerOrderBy orderByVar, boolean asc) throws FailedDAOOperationException;
}
