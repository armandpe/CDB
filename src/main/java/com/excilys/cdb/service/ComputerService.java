package com.excilys.cdb.service;

import com.excilys.cdb.dao.ComputerDAO;
import com.excilys.cdb.dao.DAO;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.dao.IComputerDAO;
import com.excilys.cdb.model.Computer;

@org.springframework.stereotype.Service
public class ComputerService extends Service<Computer, ComputerDAO> {

	private final IComputerDAO computerDAO;

	private ComputerService(IComputerDAO computerDAO) { 
		this.computerDAO = computerDAO;
	}
	
	public void create(Computer computer) throws FailedDAOOperationException {
		computer.setId(0);
		try {
			computerDAO.create(computer);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : create computer failed");
			throw e;
		}
	}

	public void delete(long id) throws FailedDAOOperationException {
		try {
			computerDAO.deleteById(id);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : delete computer failed ");
			throw e;
		}
	}

	@Override
	public String getDaoClassFullName() {
		return computerDAO.getClass().getName();
	}

	public void update(Computer computer) throws FailedDAOOperationException {
		try {
			computerDAO.update(computer);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " :  failed ");
			throw e;
		}
	}

	@Override
	public DAO<Computer> getDAO() {
		return computerDAO;
	}


}
