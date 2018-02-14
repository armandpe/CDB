package com.excilys.cdb.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import com.excilys.cdb.ConnectionManager.ConnectionManager;
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

	
	protected int createComputer(Computer c) {
		
		ConnectionManager cManager = ConnectionManager.getInstance(); 
		Connection connection = cManager.getConnection();
		Map<String, String> map = getMapperSQLFields();
		String[] template = {};
		String query = "INSERT INTO " + getTable() + "(" + a2Str(map.keySet().toArray(template)) + ") ";
		
		ArrayList<String> paramValues = new ArrayList<>();
		
		for(String name : map.values())
		{
			String value = null;
			try {
				value = Computer.class.getDeclaredField(name).get(c).toString();
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(value != null)
				paramValues.add(value);
		}
		
		query += "VALUES (" + a2Str(paramValues.toArray(template)) + ");";
		
		try {
			Statement stmt = connection.createStatement();
			System.out.println(query);
			return stmt.executeUpdate(query);
		}catch(Exception e) { 
			e.printStackTrace();
			System.out.println("Erreur requete : " + e.getMessage());
			return -1;
		}
		
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
