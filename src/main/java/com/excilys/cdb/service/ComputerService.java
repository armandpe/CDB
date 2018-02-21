package main.java.com.excilys.cdb.service;

import main.java.com.excilys.cdb.ParamDescription;
import main.java.com.excilys.cdb.dao.ComputerDAO;
import main.java.com.excilys.cdb.model.Computer;

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
	public int createComputer(@ParamDescription(name = "computer to add (id is automatically set)") Computer computer) {
		computer.setId(0);
		return ((ComputerDAO) getDAO()).createComputer(computer);
	}
	
	@ServiceMethod(name = "Update a computer")
	public int updateComputer(@ParamDescription(name = "computer to update") Computer computer) {
		return ((ComputerDAO) getDAO()).updateComputer(computer);
	}
	
	@ServiceMethod(name = "Remove a computer (based on id)")
	public int deleteComputer(@ParamDescription(name = "id of the computer ") long id) {
		return ((ComputerDAO) getDAO()).deleteComputer(id);
	}
}
