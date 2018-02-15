package com.excilys.cdb.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.excilys.cdb.Model.Company;

public class CompanyDAO extends DAO<Company> {
	
	private static CompanyDAO companyDAO;
	
	private CompanyDAO() {}
	
	public static CompanyDAO getInstance() {
		if(companyDAO == null) {
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
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return Optional.ofNullable(null);
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
