package com.excilys.cdb.DAO;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.excilys.cdb.ConnectionManager.ConnectionManager;
import com.excilys.cdb.Model.ModelClass;
import com.excilys.cdb.Model.SQLName;

public abstract class DAO<T extends ModelClass> {
	
	protected abstract String getTable();
	
	public abstract String getModelClassFullName();
	
	public Map<String, String> getMapperSQLFields(){
		HashMap<String, String> res = new HashMap<String,String>();
		Field[] fields = {};
		
		try {
			fields = Class.forName(getModelClassFullName()).getDeclaredFields();
		} catch (SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Field field : fields) {
			if(field.isAnnotationPresent(SQLName.class))
				res.put(field.getAnnotation(SQLName.class).name(), field.getName());
		}
		
		return res;
	}
	
	protected String[] getSQLArgs() {
		String[] template = {};
		return this.getMapperSQLFields().keySet().toArray(template);
	}
	
	protected abstract Optional<T> buildItem(ResultSet result);
	
	public String a2Str(String[] array) {
		String res = "";
		for(String s : array)
			res += s + ',';
	
		res = res.substring(0, res.length()-1);
		
		return res;
	}
	
	public List<T> getItems(String paramName, String paramValue){
		{
			ConnectionManager cManager = ConnectionManager.getInstance(); 
			Connection connection = cManager.getConnection();
			 
			String query = "SELECT " + a2Str(getSQLArgs());
			
			query += " FROM " + getTable() + " WHERE "+ paramName + " = " + paramValue + ";";
			
			
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
		 
		String query = "SELECT " + a2Str(getSQLArgs());
		
		query += " FROM " + getTable() + " WHERE id = " + id + ";";
				
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
		 
		String query = "SELECT " + a2Str(getSQLArgs());
		
		query += " FROM " + getTable() + ";";
		
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
 