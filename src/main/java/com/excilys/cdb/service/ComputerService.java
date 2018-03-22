package com.excilys.cdb.service;

import com.excilys.cdb.ParamDescription;
import com.excilys.cdb.dao.ComputerDAO;
import com.excilys.cdb.dao.DAO;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.Computer;

@org.springframework.stereotype.Service
@ServiceClass(name = "computers")
public class ComputerService extends Service<Computer, ComputerDAO> {

	private final ComputerDAO computerDAO;

	private ComputerService(ComputerDAO computerDAO) { 
		this.computerDAO = computerDAO;
	}
	
	@ServiceMethod(name = "Add a new computer")
	public void create(@ParamDescription(name = "computer to add (id is automatically set)") Computer computer) throws FailedDAOOperationException {
		computer.setId(0);
		try {
			computerDAO.create(computer);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : create computer failed");
			throw e;
		}
	}

	@ServiceMethod(name = "Remove a computer (based on id)")
	public void delete(@ParamDescription(name = "id of the computer ") long id) throws FailedDAOOperationException {
		try {
			computerDAO.delete(id);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : delete computer failed ");
			throw e;
		}
	}

	@Override
	public String getDaoClassFullName() {
		return computerDAO.getClass().getName();
	}

	@ServiceMethod(name = "Update a computer")
	public void update(@ParamDescription(name = "computer to update") Computer computer) throws FailedDAOOperationException {
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
