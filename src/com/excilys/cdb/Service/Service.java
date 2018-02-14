package com.excilys.cdb.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import com.excilys.cdb.DAO.DAO;
import com.excilys.cdb.Model.ModelClass;

public abstract class Service<T extends ModelClass, U extends DAO<T>> {

	String daoClassName;
	
	public DAO<T> getDAO()
	{
		Class c = null;
		DAO<T> dao = null;
		Method method = null;
		
		try {
			c = Class.forName(daoClassName);
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
	
	public List<T> getAll(){
		DAO<T> dao = getDAO();
		return dao.getAll();
	}
	
	public Optional<T> getById(long id) {
		DAO<T> dao = getDAO();
		return dao.getById(id);
	}
	
	public List<T> getItems(String paramName, String paramValue) {
		DAO<T> dao = getDAO();
		return dao.getItems(paramName, paramValue);
	}
}
