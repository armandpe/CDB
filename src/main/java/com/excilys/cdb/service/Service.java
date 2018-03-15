package com.excilys.cdb.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.ParamDescription;
import com.excilys.cdb.dao.DAO;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.ModelClass;

public abstract class Service<T extends ModelClass, U extends DAO<T>> {
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@ServiceMethod(name = "Get the list of all elements", forUser = false, fullName = "com.excilys.cdb.Service.getAll")
	public List<T> getAll(long offset, long limit) throws FailedDAOOperationException {
		DAO<T> dao = getDAO();
		try {
		return dao.getAll(offset, limit);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : Get all method failed ");
			throw e;
		}
	}
	
	public List<T> getAll(long offset, long limit, String toSearch) throws FailedDAOOperationException {
		DAO<T> dao = getDAO();
		try {
		return dao.getAll(offset, limit, toSearch, null, true);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : Get all method failed ");
			throw e;
		}
	}
	
	public List<T> getAll(long offset, long limit, String toSearch, ComputerOrderBy orderByVar, boolean ascd) throws FailedDAOOperationException {
		DAO<T> dao = getDAO();
		try {
		return dao.getAll(offset, limit, toSearch, Optional.of(orderByVar), ascd);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : Get all method failed ");
			throw e;
		}
	}
	
	@ServiceMethod(name = "Get an element (by id)")
	public Optional<T> getById(@ParamDescription(name = "element id") long id) throws FailedDAOOperationException {
		DAO<T> dao = getDAO();
		try {
		return dao.getById(id);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : Get all method failed ");
			throw e;
		}
	}
	
	public long getCount() throws FailedDAOOperationException {
		return getCount(null);
	}
	
	public long getCount(String search) throws FailedDAOOperationException {
		DAO<T> dao = getDAO();
		try {
			return dao.getCount(search);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : Get count failed ");
			throw e;
		}
	}
	
	public abstract String getDaoClassFullName();
	
	public abstract DAO<T> getDAO();
}
