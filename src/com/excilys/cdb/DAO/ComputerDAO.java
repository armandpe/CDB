package com.excilys.cdb.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.excilys.cdb.ConnectionManager.ConnectionManager;
import com.excilys.cdb.Model.Computer;;

public class ComputerDAO {
	
	
	
	private static ComputerDAO computerDAO;
	
	private Connection connection;
	
	private ComputerDAO() {}
	
	public static ComputerDAO getInstance() {
		if(computerDAO == null) {
			computerDAO = new ComputerDAO();
		}
		
		return computerDAO;
	}
	
	private Computer buildItem(ResultSet rs) {
		try {
			Computer c = new Computer();
			c.setId(rs.getLong("id"));
			c.setName(rs.getString("name"));
			c.setIntroduced(rs.getTimestamp("introduced"));
			c.setDiscontinued(rs.getTimestamp("discontinued"));
			c.setCompanyId(rs.getLong("company_id"));
			return c;
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public List<Computer> getAll(){
		ConnectionManager cManager = ConnectionManager.getInstance(); 
		Connection connection = cManager.getConnection();
		 
		//computer - company
		String query = "SELECT id, name, introduced, "
				+ "discontinued, company_id FROM computer;";
		ResultSet sqlResults = null;
		
		ArrayList<Computer> result = new ArrayList<>();
		
		try {
			Statement stmt = connection.createStatement();
			sqlResults = stmt.executeQuery(query);
		}catch(Exception e) { 
			e.printStackTrace();
			System.out.println("Erreur requete : " + e.getMessage());
		}
		
		try {
		while(sqlResults.next()) {
			Computer c = buildItem(sqlResults);
			if(c != null)
				result.add(c);
		}
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Erreur resultat SQL : " + e.getMessage());
		}
		
		cManager.closeConnection();
		return result;
	}
	
	public Computer getItem(long id){
		ConnectionManager cManager = ConnectionManager.getInstance(); 
		Connection connection = cManager.getConnection();
		 
		//computer - company
		String query = "SELECT id, name, introduced, "
				+ "discontinued, company_id FROM computer WHERE id =" + id + ";";
		ResultSet sqlResults = null;
		
		Computer result = null;
		
		try {
			Statement stmt = connection.createStatement();
			sqlResults = stmt.executeQuery(query);
		}catch(Exception e) { 
			e.printStackTrace();
			System.out.println("Erreur requete : " + e.getMessage());
		}
		
		try {
			sqlResults.next();
			result = buildItem(sqlResults);
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Erreur resultat SQL : " + e.getMessage());
		}
		
		cManager.closeConnection();
		return result;
	}
	
	public List<Computer> getItems(String paramName, String paramValue) {
		ConnectionManager cManager = ConnectionManager.getInstance(); 
		Connection connection = cManager.getConnection();
		 
		//computer - company
		String query = "SELECT id, name, introduced, "
				+ "discontinued, company_id FROM computer WHERE "+ paramName + "=" + paramValue + ";";
		ResultSet sqlResults = null;
		
		ArrayList<Computer> result = new ArrayList<>();
		
		try {
			Statement stmt = connection.createStatement();
			sqlResults = stmt.executeQuery(query);
		}catch(Exception e) { 
			e.printStackTrace();
			System.out.println("Erreur requete : " + e.getMessage());
		}
		
		try {
		while(sqlResults.next()) {
			Computer c = buildItem(sqlResults);
			if(c != null)
				result.add(c);
		}
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Erreur resultat SQL : " + e.getMessage());
		}
		
		cManager.closeConnection();
		return result;
	}
	
}
