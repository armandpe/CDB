package main.java.com.excilys.cdb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.apache.log4j.Level;

import main.java.com.excilys.cdb.model.Company;

public class CompanyDAO extends DAO<Company> {
	
	private static CompanyDAO companyDAO;
	
	private CompanyDAO() { }
	
	public static CompanyDAO getInstance() {
		if (companyDAO == null) {
			companyDAO = new CompanyDAO();
		}
		
		return companyDAO;
	}
	
	protected Optional<Company> buildItem(ResultSet rs) {
		try {
			Company c = new Company();
			c.setId(rs.getLong("id"));
			c.setName(rs.getString("name"));
			return Optional.of(c);
		} catch (SQLException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
			return Optional.empty();
		}
	}
	
	@Override
	protected String getTable() {
		return "company";
	}

	@Override
	public String getModelClassFullName() {
		return Company.class.getName();
	}

}
