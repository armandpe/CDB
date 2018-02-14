package com.excilys.cdb.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.excilys.cdb.Model.Computer;;

public class ComputerDAO extends DAO<Computer> {
	
	private static ComputerDAO computerDAO;
	
	private ComputerDAO() {}
	
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
			c.setIntroduced( rs.getTimestamp("introduced").toLocalDateTime().toLocalDate());
			c.setDiscontinued(rs.getTimestamp("discontinued").toLocalDateTime().toLocalDate());
			c.setCompanyId(rs.getLong("company_id"));
			return Optional.of(c);
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return Optional.ofNullable(null);
		}
	}

	
	protected long createComputer(Computer c) {
		String query = "INSERT INTO " + getTable() + "(";
		
		return 0;
	}
	
	protected long updateComputer(Computer c) {

		return 0;
	}
	
	protected long deleteComputer(long id) {

		return 0;
	}

	@Override
	protected String getTable() {
		return "computer";
	}

	@Override
	public String getModelClassFullName() {
		return Computer.class.getName();
	}

}
