package com.excilys.cdb.dao;

import com.excilys.cdb.model.Computer;

public interface IComputerDAO extends DAO<Computer> {
	
	void create(Computer computer) throws FailedDAOOperationException;

	void update(Computer computer) throws FailedDAOOperationException;
	
}
