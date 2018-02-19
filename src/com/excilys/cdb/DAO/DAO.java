package com.excilys.cdb.DAO;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.excilys.cdb.Main;
import com.excilys.cdb.ConnectionManager.ConnectionManager;
import com.excilys.cdb.Model.ModelClass;
import com.excilys.cdb.Model.SQLInfo;

public abstract class DAO<T extends ModelClass> {

	final Logger logger = Logger.getLogger(this.getClass());

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

	public static <T> T[] append(T[] arr, T element) {
		final int N = arr.length;
		arr = Arrays.copyOf(arr, N + 1);
		arr[N] = element;
		return arr;
	}

	public Optional<T> getById(Object...objects){
		long id = (long) objects[0];
		Connection connection = (Connection) objects[1];

		Optional<T> result = Optional.ofNullable(null);

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
			logger.log(Level.ERROR, "Error in method " + Main.getMethodName() + " : " + e.getMessage());
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

	public List<T> getAll(long offset, long limit){

		Object[] objects = {offset, limit};
		return executeWithConnection(x -> this.getAll(x), objects);
	}

	public long getCount() {
		return executeWithConnection(x -> this.getCount(x), new Object[0]);
	}

	protected abstract Optional<T> buildItem(ResultSet result);

	protected abstract String getTable();

	protected String[] getSQLArgs() {
		String[] template = {};
		return this.getMapperSQLFields().keySet().toArray(template);
	}

	protected List<T> getAll(Object...params){
		long offset = (long) params[0];
		long limit = (long) params[1];
		Connection connection = (Connection) params[2];

		ArrayList<T> result = new ArrayList<>();

		String query = "SELECT " + arrayToString(getSQLArgs());

		query += " FROM " + getTable() + " LIMIT "+ offset  + ", " + limit;

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

	protected long getCount(Object...objects) {
		Connection connection = (Connection) objects[0];

		String query = "SELECT count(" + getPrimaryKeyName() + ") as nbComputer FROM " + getTable();

		ResultSet sqlResults = null;

		int result = -1;

		try {
			Statement stmt = connection.createStatement();
			sqlResults = stmt.executeQuery(query);
		}catch(Exception e) { 
			logger.log(Level.ERROR, "Error in method " + Main.getMethodName() + " : " + e.getMessage());
		}

		try {
			if(sqlResults.next())
				result = sqlResults.getInt("nbComputer");
			else
				logger.log(Level.ERROR, "Error in method " + Main.getMethodName() + " : This isn't supposed to happen");
		}catch(SQLException e) {
			logger.log(Level.ERROR, "Error in method " + Main.getMethodName() + " : " + e.getMessage());
		}
		try {
			sqlResults.close();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Error in method " + Main.getMethodName() + " : " + e.getMessage());
		}

		return result;
	}

	protected String getPrimaryKeyName() {
		Class<?> objectClass;
		try {
			objectClass = Class.forName(getModelClassFullName());
			Field[] fields = objectClass.getDeclaredFields();

			for(Field field : fields) {
				if(field.isAnnotationPresent(SQLInfo.class) && field.getAnnotation(SQLInfo.class).primaryKey())
					return field.getAnnotation(SQLInfo.class).name();
			}
		} catch (ClassNotFoundException e) {
			logger.log(Level.ERROR, "Error in method " + Main.getMethodName() + " : " + e.getMessage());
		}
		return null;
	}

	protected int deleteByPrimaryKey(Object primaryKeyValue) {
		String primaryKey = getPrimaryKeyName();

		String query = "DELETE FROM " + getTable() + " WHERE " + primaryKey + " = ?";

		LinkedHashMap<String, SimpleEntry<Class<?>, Object>> fieldsClassValues = new LinkedHashMap<>();
		fieldsClassValues.put(primaryKey, new SimpleEntry<Class<?>, Object>(primaryKeyValue.getClass(), primaryKeyValue));

		HashMap<String, Integer> keyOrder = new HashMap<>();
		keyOrder.put(primaryKey, 1);

		return executeStatement(query, fieldsClassValues, keyOrder);
	}

	protected int executeStatement(String query, LinkedHashMap<String, SimpleEntry<Class<?>, Object>> fieldsClassValues, HashMap<String, Integer> keyOrder) {
		PreparedStatement ps;
		ConnectionManager cManager = ConnectionManager.getInstance();
		int result = -1;

		try(Connection connection = cManager.getConnection()){			
			ps = connection.prepareStatement(query);

			for(Entry<String, SimpleEntry<Class<?>, Object>> fieldClassValue : fieldsClassValues.entrySet())
			{
				addValueToStatement(ps, fieldClassValue, keyOrder);
			}
			System.out.println(query);
			result = ps.executeUpdate();
			ps.close();

		} catch (SQLException e) {
			logger.log(Level.ERROR, "Error in method " + Main.getMethodName() + " : " + e.getMessage());
		}
		return result;
	}

	protected void addValueToStatement(PreparedStatement ps, Entry<String, SimpleEntry<Class<?>, Object>> fieldClassValue, Map<String, Integer> keyOrder) throws SQLException {

		Object value = fieldClassValue.getValue().getValue();

		Class<?> c = fieldClassValue.getValue().getKey();

		System.out.println(keyOrder);

		int order = keyOrder.get(fieldClassValue.getKey());

		if(c == String.class) {
			ps.setString(order, (String) value);
		}
		else if(c == LocalDateTime.class) {
			if(value != null)
				ps.setDate(order, Date.valueOf(((LocalDateTime) value).toLocalDate()));
			else
				ps.setDate(order, null);
		}
		else if(c == LocalDate.class) {
			if(value != null)
				ps.setDate(order, Date.valueOf((LocalDate) value));
			else
				ps.setDate(order, null);
		}
		else if(c == Integer.class || c == int.class) {
			if(value != null) {
				ps.setInt(order, (Integer) value);
			}else {
				ps.setNull(4, Types.INTEGER);
			}
		}
		else if(c == Long.class || c == long.class) {
			ps.setLong(order, (Long) value);
		}
		else {
			logger.log(Level.ERROR, "Error in method " + Main.getMethodName() + " : no implementation for type " + c.getName());
		}

	}


}
