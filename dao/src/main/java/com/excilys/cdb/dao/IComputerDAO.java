package com.excilys.cdb.dao;

import java.util.List;

import com.excilys.cdb.model.Computer;

public interface IComputerDAO extends DAO<Computer> {
	
	void create(Computer computer) throws FailedDAOOperationException;

	void update(Computer computer) throws FailedDAOOperationException;
	
	List<Computer> getAll(long offset, long limit, String search, ComputerOrderBy orderByVar, boolean asc) throws FailedDAOOperationException;

	long getCount(String search) throws FailedDAOOperationException;

	void deleteById(long id) throws FailedDAOOperationException;
}
