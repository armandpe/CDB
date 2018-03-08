package main.java.com.excilys.cdb.service;

import main.java.com.excilys.cdb.ParamDescription;
import main.java.com.excilys.cdb.dao.ComputerDAO;
import main.java.com.excilys.cdb.dao.FailedDAOOperationException;
import main.java.com.excilys.cdb.model.Computer;

@ServiceClass(name = "computers")
public class ComputerService extends Service<Computer, ComputerDAO> {

	private static ComputerService service;

	public static ComputerService getInstance() {
		if (service == null) {
			service = new ComputerService();
		}
		return service;
	}

	private ComputerService() {
	}

	@ServiceMethod(name = "Add a new computer")
	public void createComputer(@ParamDescription(name = "computer to add (id is automatically set)") Computer computer) throws FailedDAOOperationException {
		computer.setId(0);
		try {
			((ComputerDAO) getDAO()).createComputer(computer);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : create computer failed");
			throw e;
		}
	}

	@ServiceMethod(name = "Remove a computer (based on id)")
	public void deleteComputer(@ParamDescription(name = "id of the computer ") long id) throws FailedDAOOperationException {
		try {
			((ComputerDAO) getDAO()).deleteComputer(id);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " : delete computer failed ");
			throw e;
		}
	}

	@Override
	public String getDaoClassFullName() {
		return ComputerDAO.class.getName();
	}

	@ServiceMethod(name = "Update a computer")
	public void updateComputer(@ParamDescription(name = "computer to update") Computer computer) throws FailedDAOOperationException {
		try {
			((ComputerDAO) getDAO()).updateComputer(computer);
		} catch (FailedDAOOperationException e) {
			e.setMessage(getDaoClassFullName() + " :  failed ");
			throw e;
		}
	}
}
