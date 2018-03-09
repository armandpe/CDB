package main.java.com.excilys.cdb.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.cdb.ParamDescription;
import main.java.com.excilys.cdb.dao.DAO;
import main.java.com.excilys.cdb.dao.FailedDAOOperationException;
import main.java.com.excilys.cdb.model.ModelClass;

public abstract class Service<T extends ModelClass, U extends DAO<T>> {

	String daoClassName;
	
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
		DAO<T> dao = getDAO();
		try {
			return dao.getCount();
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : Get count failed ");
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public DAO<T> getDAO() {
		Class<?> c = null;
		DAO<T> dao = null;
		Method method = null;
		
		try {
			c = Class.forName(getDaoClassFullName());
		} catch (ClassNotFoundException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.error("Error in method " + methodName + " : " + e.getMessage());
		}
		
		try {
			method = c.getMethod("getInstance");
		} catch (NoSuchMethodException | SecurityException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.error("Error in method " + methodName + " : " + e.getMessage());
		}
		
		try {
			dao = (DAO<T>) method.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.error("Error in method " + methodName + " : " + e.getMessage());
		}
		
		return dao;
	}
	
	public abstract String getDaoClassFullName();
}
