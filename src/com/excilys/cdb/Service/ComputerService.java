package com.excilys.cdb.Service;

import java.util.List;
import java.util.Optional;

import com.excilys.cdb.DAO.ComputerDAO;
import com.excilys.cdb.Model.Computer;

public class ComputerService {
	
	private static ComputerService service;
	
	private ComputerService() {}
	
	public static ComputerService getInstance() {
		if(service == null) {
			service = new ComputerService();
		}
		return service;
	}
	
	public List<Computer> getAll(){
		ComputerDAO dao = ComputerDAO.getInstance();
		return dao.getAll();
	}
	
	public Optional<Computer> getById(long id) {
		ComputerDAO dao = ComputerDAO.getInstance();
		return dao.getById(id);
	}
	
	public List<Computer> getItems(String paramName, String paramValue) {
		ComputerDAO dao = ComputerDAO.getInstance();
		return dao.getItems(paramName, paramValue);
	}
	
}
