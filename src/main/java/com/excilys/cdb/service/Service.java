package main.java.com.excilys.cdb.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import main.java.com.excilys.cdb.ParamDescription;
import main.java.com.excilys.cdb.dao.DAO;
import main.java.com.excilys.cdb.model.ModelClass;

public abstract class Service<T extends ModelClass, U extends DAO<T>> {

	String daoClassName;
	
	final Logger logger = Logger.getLogger(this.getClass());
	
	public abstract String getDaoClassFullName();
	
	@SuppressWarnings("unchecked")
	public DAO<T> getDAO()
	{
		Class<?> c = null;
		DAO<T> dao = null;
		Method method = null;
		
		try {
			c = Class.forName(getDaoClassFullName());
		} catch (ClassNotFoundException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}
		
		try {
			method = c.getMethod("getInstance");
		} catch (NoSuchMethodException | SecurityException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}
		
		try {
			dao = (DAO<T>) method.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}
		
		return dao;
	}
	
	@ServiceMethod(name = "Get the list of all elements", forUser = false, fullName = "com.excilys.cdb.Service.getAll")
	public List<T> getAll(long offset, long limit){
		DAO<T> dao = getDAO();
		return dao.getAll(offset, limit);
	}
	
	@ServiceMethod(name = "Get an element (by id)")
	public Optional<T> getById(@ParamDescription(name = "element id") long id) {
		DAO<T> dao = getDAO();
		return dao.getById(id);
	}
	
	public long getCount() {
		DAO<T> dao = getDAO();
		return dao.getCount();
	}
}
