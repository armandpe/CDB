package com.excilys.cdb.Service;

import com.excilys.cdb.DAO.ComputerDAO;
import com.excilys.cdb.Model.Computer;

@ServiceClass(name = "computers")
public class ComputerService extends Service<Computer, ComputerDAO>{
	
	private static ComputerService service;
	
	private ComputerService() {}
	
	public static ComputerService getInstance() {
		if(service == null) {
			service = new ComputerService();
		}
		return service;
	}

	@Override
	public String getDaoClassFullName() {
		return ComputerDAO.class.getName();
	}
	
	@ServiceMethod(name = "Add a new computer")
	public int createComputer(Computer computer) {
		return ((ComputerDAO) getDAO()).createComputer(computer);
	}
	
	@ServiceMethod(name = "Update a computer")
	public int updateComputer(Computer computer) {
		return ((ComputerDAO) getDAO()).updateComputer(computer);
	}
	
	@ServiceMethod(name = "Remove a computer (based on id)")
	public int deleteComputer(long id) {
		return ((ComputerDAO) getDAO()).deleteComputer(id);
	}
}
