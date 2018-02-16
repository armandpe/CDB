package com.excilys.cdb.DAO;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.excilys.cdb.ConnectionManager.ConnectionManager;
import com.excilys.cdb.Model.ModelClass;
import com.excilys.cdb.Model.SQLInfo;

public abstract class DAO<T extends ModelClass> {

	final Logger logger = Logger.getLogger(this.getClass());

	protected abstract String getTable();

	public abstract String getModelClassFullName();

	public Map<String, String> getMapperSQLFields(){
		HashMap<String, String> res = new HashMap<String,String>();
		Field[] fields = {};

		try {
			fields = Class.forName(getModelClassFullName()).getDeclaredFields();
		} catch (SecurityException | ClassNotFoundException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}

		for(Field field : fields) {
			if(field.isAnnotationPresent(SQLInfo.class))
				res.put(field.getAnnotation(SQLInfo.class).name(), field.getName());
		}

		return res;
	}

	protected String[] getSQLArgs() {
		String[] template = {};
		return this.getMapperSQLFields().keySet().toArray(template);
	}

	protected abstract Optional<T> buildItem(ResultSet result);

	public String arrayToString(String[] array) {
		String res = "";
		for(String s : array)
			res += s + ',';

		res = res.substring(0, res.length()-1);

		return res;
	}

	public <V> V executeWithConnection(Function<Object[], V> f, Object[] objects) {
		ConnectionManager cManager = ConnectionManager.getInstance(); 
		try(Connection connection = cManager.getConnection()){

			return f.apply(append(objects, connection));

		} catch (SQLException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}
		return null;
	}

	static <T> T[] append(T[] arr, T element) {
		final int N = arr.length;
		arr = Arrays.copyOf(arr, N + 1);
		arr[N] = element;
		return arr;
	}

	public Optional<T> getById(Object...objects){
		long id = (long) objects[0];
		Connection connection = (Connection) objects[1];
		
		Optional<T> result = Optional.ofNullable(null);
		ConnectionManager cManager = ConnectionManager.getInstance(); 

		String query = "SELECT " + arrayToString(getSQLArgs());
		query += " FROM " + getTable() + " WHERE id = " + id + ";";

		ResultSet sqlResults = null;

		try {
			Statement stmt = connection.createStatement();
			System.out.println(query);
			sqlResults = stmt.executeQuery(query);
		}catch(Exception e) { 
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}

		try {
			if(sqlResults.next())
				result = buildItem(sqlResults);
		}catch(SQLException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}
		try {
			sqlResults.close();
		} catch (SQLException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}

		return result;
	}

	public Optional<T> getById(long id){
		Object[] objects = {id};
		return executeWithConnection(x -> this.getById(x), objects);
	}

	public List<T> getAll(){
		return executeWithConnection(x -> this.getAll(x), new Object[0]);
	}

	private List<T> getAll(Object...params){
		Connection connection = (Connection) params[0];

		ArrayList<T> result = new ArrayList<>();

		String query = "SELECT " + arrayToString(getSQLArgs());

		query += " FROM " + getTable() + ";";

		ResultSet sqlResults = null;

		try {
			Statement stmt = connection.createStatement();
			System.out.println(query);
			sqlResults = stmt.executeQuery(query);
		}catch(Exception e) { 
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}

		try {
			while(sqlResults.next()) {
				Optional<T> c = buildItem(sqlResults);
				if(c.isPresent())
					result.add(c.get());
			} 
		}catch(SQLException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}
		try {
			sqlResults.close();
		} catch (SQLException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}

		return result;
	}

}
