package com.excilys.cdb.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.excilys.cdb.ParamDescription;
import com.excilys.cdb.DAO.DAO;
import com.excilys.cdb.Model.ModelClass;

public abstract class Service<T extends ModelClass, U extends DAO<T>> {

	String daoClassName;
	
	final Logger logger = Logger.getLogger(this.getClass());
	
	public abstract String getDaoClassFullName();
	
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
	
	@ServiceMethod(name = "Get the list of all elements")
	public List<T> getAll(){
		DAO<T> dao = getDAO();
		return dao.getAll();
	}
	
	@ServiceMethod(name = "Get an element (by id)")
	public Optional<T> getById(@ParamDescription(name = "element id") long id) {
		DAO<T> dao = getDAO();
		return dao.getById(id);
	}
}
