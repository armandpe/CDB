package main.java.com.excilys.cdb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import main.java.com.excilys.cdb.Main;
import main.java.com.excilys.cdb.model.Company;

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

}
