package com.excilys.cdb.service;

import com.excilys.cdb.dao.ComputerOrderBy;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.dao.IComputerDAO;
import com.excilys.cdb.model.Computer;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ComputerService implements IComputerService {

	private final IComputerDAO computerDAO;

	private ComputerService(IComputerDAO computerDAO) {
		this.computerDAO = computerDAO;
	}

    @Override
	public void create(Computer computer) throws FailedDAOOperationException {
		computer.setId(0);
		try {
			computerDAO.create(computer);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : create computer failed");
			throw e;
		}
	}

    @Override
	public void update(Computer computer) throws FailedDAOOperationException {
		try {
			computerDAO.update(computer);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : update failed ");
			throw e;
		}
	}

    @Override
    public String getDaoClassFullName() {
        return computerDAO.getClass().getName();
    }

    @Override
    public List<Computer> getAll(long offset, long limit, String toSearch, ComputerOrderBy orderByVar, boolean ascd) throws FailedDAOOperationException {

		try {
			return computerDAO.getAll(offset, limit, toSearch, orderByVar, ascd);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : Get all method failed ");
			throw e;
		}
	}

    @Override
    public long getCount(String search) throws FailedDAOOperationException {
        try {
            return computerDAO.getCount(search);
        } catch (FailedDAOOperationException e) {
            e.setMessage(getDaoClassFullName() + " : getCount failed ");
            throw e;
        }
    }

    @Override
    public void deleteById(long id) throws FailedDAOOperationException {
        try {
            computerDAO.deleteById(id);
        } catch (FailedDAOOperationException e) {
            e.setMessage(getDaoClassFullName() + " : delete failed ");
            throw e;
        }
    }

    @Override
    public Optional<Computer> getById(long id) throws FailedDAOOperationException {
        try {
            return computerDAO.getById(id);
        } catch (FailedDAOOperationException e) {
            e.setMessage(getDaoClassFullName() + " : Get by id method failed ");
            throw e;
        }
    }

    @Override
    public long getCount() throws FailedDAOOperationException {
	    return getCount(null);
    }

}
