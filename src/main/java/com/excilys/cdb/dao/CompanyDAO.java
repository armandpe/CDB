package main.java.com.excilys.cdb.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import main.java.com.excilys.cdb.Main;
import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;

public class CompanyDAO extends DAO<Company> {
	
	private static CompanyDAO companyDAO;
	
	public static CompanyDAO getInstance() {
		if (companyDAO == null) {
			companyDAO = new CompanyDAO();
		}
		return companyDAO;
	}
	
	private String modelClassFullName = null;
	
	private CompanyDAO() { }
	
	@Override
	public String getModelClassFullName() {
		if (modelClassFullName == null) {
			modelClassFullName = Company.class.getName();
		}
		
		return modelClassFullName;
	}
	
	protected Optional<Company> buildItem(ResultSet rs) {
		try {
			Company c = new Company();
			c.setId(rs.getLong("id"));
			c.setName(rs.getString("name"));
			return Optional.of(c);
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
			return Optional.empty();
		}
	}

	public void delete(long id) throws FailedDAOOperationException {
		Object[] args = {id};
		executeWithConnection(x -> delete(x), args);
	}
	
	protected int delete(Object...objects) throws FailedDAOOperationException {
		long id = (long) objects[0];
		Connection connection = (Connection) objects[1];
		delete(id, connection);
		return 0;
	}
	
	protected void delete(long id, Connection connection) throws FailedDAOOperationException {
		Entry<String, Field> primaryKey = getKey(getModelClassFullName(), x -> x.primaryKey());
		Map<String, Object> conditions = new HashMap<>();
		conditions.put(primaryKey.getKey(), id);
		
		ComputerDAO computerDAO = ComputerDAO.getInstance();
		
		List<Computer> computers = computerDAO.getByCompanyId(id, connection);
		
		for (long computerId : computers.stream().mapToLong(computer -> computer.getId()).toArray()) {
			computerDAO.deleteByPrimaryKey(computerId, connection);
		}
		
		deleteByPrimaryKey(id, connection);
	}

}
