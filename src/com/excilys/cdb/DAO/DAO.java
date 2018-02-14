package com.excilys.cdb.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.excilys.cdb.ConnectionManager.ConnectionManager;
import com.excilys.cdb.Model.ModelClass;

public abstract class DAO<T extends ModelClass> {
	
	protected String table;
	
	protected String className;
	
	protected abstract String[] getSQLArgs();
	
	protected abstract Optional<T> buildItem(ResultSet result);
	
	public List<T> getItems(String paramName, String paramValue){
		{
			ConnectionManager cManager = ConnectionManager.getInstance(); 
			Connection connection = cManager.getConnection();
			 
			String query = "SELECT ";
			
			for(String s : getSQLArgs())
					query += s + ',';
			
			query = query.substring(0, query.length()-1);
			
			query += " FROM " + table + " WHERE "+ paramName + " = " + paramValue + ";";
			
			
			ResultSet sqlResults = null;
			
			ArrayList<T> result = new ArrayList<>();
			
			try {
				Statement stmt = connection.createStatement();
				System.out.println(query);
				sqlResults = stmt.executeQuery(query);
			}catch(Exception e) { 
				e.printStackTrace();
				System.out.println("Erreur requete : " + e.getMessage());
			}
			
			try { 
			while(sqlResults.next()) {
				Optional<T> c = buildItem(sqlResults);
				if(c.isPresent())
					result.add(c.get());
			}
			}catch(SQLException e) {
				e.printStackTrace();
				System.out.println("Erreur resultat SQL : " + e.getMessage());
			}
			
			cManager.closeConnection();
			return result;
		}
	}
	
	public Optional<T> getById(long id){
		ConnectionManager cManager = ConnectionManager.getInstance(); 
		Connection connection = cManager.getConnection();
		 
		String query = "SELECT ";
		
		for(String s : getSQLArgs())
				query += s + ',';
		
		query = query.substring(0, query.length()-1);
		
		query += " FROM " + table + " WHERE id = " + id + ";";
				
		ResultSet sqlResults = null;
		
		Optional<T> result = Optional.ofNullable(null);
		
		try {
			Statement stmt = connection.createStatement();
			System.out.println(query);
			sqlResults = stmt.executeQuery(query);
		}catch(Exception e) { 
			e.printStackTrace();
			System.out.println("Erreur requete : " + e.getMessage());
		}
		
		try {
			if(sqlResults.next())
				result = buildItem(sqlResults);
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Erreur resultat SQL : " + e.getMessage());
		}
		
		cManager.closeConnection();
		return result;
	}
	
	public List<T> getAll(){
		ConnectionManager cManager = ConnectionManager.getInstance(); 
		Connection connection = cManager.getConnection();
		 
		String query = "SELECT ";
		
		for(String s : getSQLArgs())
				query += s + ',';
		
		query = query.substring(0, query.length()-1);
		
		query += " FROM " + table + ";";
		
		ResultSet sqlResults = null;
		
		ArrayList<T> result = new ArrayList<>();
		
		try {
			Statement stmt = connection.createStatement();
			System.out.println(query);
			sqlResults = stmt.executeQuery(query);
		}catch(Exception e) { 
			e.printStackTrace();
			System.out.println("Erreur requete : " + e.getMessage());
		}
		
		try {
		while(sqlResults.next()) {
			Optional<T> c = buildItem(sqlResults);
			if(c.isPresent())
				result.add(c.get());
		}
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Erreur resultat SQL : " + e.getMessage());
		}
		
		cManager.closeConnection();
		return result;
	}
	
	
	
	
}
 