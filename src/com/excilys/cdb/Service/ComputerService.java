package com.excilys.cdb.Service;

import java.util.List;
import java.util.Optional;

import com.excilys.cdb.DAO.ComputerDAO;
import com.excilys.cdb.Model.Computer;

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
	
	public int createComputer(Computer computer) {
		return ((ComputerDAO) getDAO()).createComputer(computer);
	}
	
	public long updateComputer(Computer computer) {
		return ((ComputerDAO) getDAO()).updateComputer(computer);
	}
}
