package com.excilys.cdb.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.excilys.cdb.Model.Computer;;

public class ComputerDAO extends DAO<Computer> {
	
	private static ComputerDAO computerDAO;
	
	private ComputerDAO() {
		table = "computer";
	}
	
	public static ComputerDAO getInstance() {
		if(computerDAO == null) {
			computerDAO = new ComputerDAO();
		}
		
		return computerDAO;
	}
	
	@Override
	protected Optional<Computer> buildItem(ResultSet rs) {
		try {
			Computer c = new Computer();
			c.setId(rs.getLong("id"));
			c.setName(rs.getString("name"));
			c.setIntroduced(rs.getTimestamp("introduced"));
			c.setDiscontinued(rs.getTimestamp("discontinued"));
			c.setCompanyId(rs.getLong("company_id"));
			return Optional.of(c);
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return Optional.ofNullable(null);
		}
	}

	@Override
	protected String[] getSQLArgs() {
		String[] res = {"id", "name", "introduced", "discontinued", "company_id"};
		return res;
	}


}
