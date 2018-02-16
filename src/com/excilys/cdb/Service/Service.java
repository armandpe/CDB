package com.excilys.cdb.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import com.excilys.cdb.ParamDescription;
import com.excilys.cdb.DAO.DAO;
import com.excilys.cdb.Model.ModelClass;

public abstract class Service<T extends ModelClass, U extends DAO<T>> {

	String daoClassName;
	
	public abstract String getDaoClassFullName();
	
	public DAO<T> getDAO()
	{
		Class<?> c = null;
		DAO<T> dao = null;
		Method method = null;
		
		try {
			c = Class.forName(getDaoClassFullName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			method = c.getMethod("getInstance");
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			dao = (DAO<T>) method.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	@ServiceMethod(name = "Get the list of all elements verifying a condition")
	public List<T> getItems(@ParamDescription(name = "parameter name") String paramName, @ParamDescription(name = "parameter value") String paramValue) {
		DAO<T> dao = getDAO();
		return dao.getItems(paramName, paramValue);
	}
}
