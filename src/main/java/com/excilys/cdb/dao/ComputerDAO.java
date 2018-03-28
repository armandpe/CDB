package com.excilys.cdb.dao;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.Computer;
import com.excilys.cdb.pagemanager.PageManagerLimit;

@Repository
public class ComputerDAO implements IComputerDAO {

	protected Logger logger = LoggerFactory.getLogger(PageManagerLimit.class);

	@Override
	public List<Computer> getAll(long offset, long limit, String search, String orderByVar, boolean asc)
			throws FailedDAOOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Computer> getById(long id) throws FailedDAOOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getCount(String search) throws FailedDAOOperationException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteById(long id) throws FailedDAOOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create(Computer computer) throws FailedDAOOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Computer computer) throws FailedDAOOperationException {
		// TODO Auto-generated method stub
		
	}
	


}
