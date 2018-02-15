package com.excilys.cdb.DAO;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.excilys.cdb.ConnectionManager.ConnectionManager;
import com.excilys.cdb.Model.Computer;
import com.excilys.cdb.Model.SQLInfo;

import jdk.internal.util.xml.impl.Pair;

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
			Timestamp temp = rs.getTimestamp("introduced");
			c.setIntroduced(temp == null ? null : temp.toLocalDateTime().toLocalDate());
			temp = rs.getTimestamp("discontinued");
			c.setDiscontinued(temp == null ? null : temp.toLocalDateTime().toLocalDate());
			c.setCompanyId(rs.getLong("company_id"));
			return Optional.of(c);
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return Optional.ofNullable(null);
		}
	}

	public int createComputer(Computer computer) {
		ConnectionManager cManager = ConnectionManager.getInstance(); 
		Connection connection = cManager.getConnection();
		Map<String, String> mapperSQLFields = getMapperSQLFields();
		Set<String> keys = mapperSQLFields.keySet();

		LinkedHashMap<String, SimpleEntry<Class<?>, Object>> fieldsClassValues = generateFieldsClassValues(mapperSQLFields, computer);

		HashMap<String, Integer> keyOrder = getKeyOrder(fieldsClassValues);

		String query = generateCreateQuery(fieldsClassValues, keys);

		return executeStatement(query, fieldsClassValues, keyOrder, connection);
	}

	private LinkedHashMap<String, SimpleEntry<Class<?>, Object>> generateFieldsClassValues(Map<String, String> mapperSQLFields, Computer computer) {
		LinkedHashMap<String, SimpleEntry<Class<?>, Object>> fieldsClassValues = new LinkedHashMap<>();

		Class<?> fieldClass = null;

		for(Entry<String, String> entry : mapperSQLFields.entrySet())
		{
			Object value = null;
			try {
				Field field = Computer.class.getDeclaredField(entry.getValue());
				field.setAccessible(true);
				value = field.get(computer);
				fieldClass = field.getType();
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fieldsClassValues.put(entry.getKey(), new SimpleEntry<Class<?>, Object>(fieldClass, value));
		}

		return fieldsClassValues;
	}

	private String generateCreateQuery(LinkedHashMap<String, SimpleEntry<Class<?>, Object>> paramValues, Set<String> keys) {

		String[] template = {};
		String query = "INSERT INTO " + getTable() + " ( " + a2Str(paramValues.keySet().toArray(template)) + " ) VALUES ( ";

		for(int i = 0; i < keys.size(); ++i)
			query += "?,";

		query = query.substring(0, query.length()-1);

		query += " )";

		return query;
	}

	private int executeStatement(String query, LinkedHashMap<String, SimpleEntry<Class<?>, Object>> fieldsClassValues, HashMap<String, Integer> keyOrder, Connection connection) {
		PreparedStatement ps;

		int result = -1;

		try {
			ps = connection.prepareStatement(query);

			for(Entry<String, SimpleEntry<Class<?>, Object>> fieldClassValue : fieldsClassValues.entrySet())
			{
				addValueToStatement(ps, fieldClassValue, keyOrder);
			}

			System.out.println(query);
			result = ps.executeUpdate();
		}catch(Exception e) { 
			e.printStackTrace();
			System.out.println("Erreur requete : " + e.getMessage());
		}

		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public void addValueToStatement(PreparedStatement ps, Entry<String, SimpleEntry<Class<?>, Object>> fieldClassValue, Map<String, Integer> keyOrder) throws SQLException {

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
			ps.setInt(order, (Integer) value);
		}
		else if(c == Long.class || c == long.class) {
			ps.setLong(order, (Long) value);
		}
		else {
			System.out.println("Type non définit : " + c.getName());
		}

	}

	public long updateComputer(Computer computer) {
		ConnectionManager cManager = ConnectionManager.getInstance(); 
		Connection connection = cManager.getConnection();
		Map<String, String> mapperSQLFields = getMapperSQLFields();
		Set<String> keys = mapperSQLFields.keySet();

		LinkedHashMap<String, SimpleEntry<Class<?>, Object>> fieldsClassValues = generateFieldsClassValues(mapperSQLFields, computer);

		String primaryKey = getPrimaryKey(computer);
		
		HashMap<String, Integer> keyOrder = getKeyOrder(fieldsClassValues, primaryKey);
		
		String query = generateUpdateQuery(fieldsClassValues, keys, primaryKey);

		return executeStatement(query, fieldsClassValues, keyOrder, connection);
	}
	
	private HashMap<String, Integer> getKeyOrder(LinkedHashMap<String, SimpleEntry<Class<?>, Object>> fieldsClassValues){
		HashMap<String, Integer> keyOrder = new HashMap<>(); 

		int i = 0;
		for( Entry<String, SimpleEntry<Class<?>, Object>> entry : fieldsClassValues.entrySet())
		{
			keyOrder.put(entry.getKey(), ++i);
		}

		return keyOrder;
	}
	
	private HashMap<String, Integer> getKeyOrder(LinkedHashMap<String, SimpleEntry<Class<?>, Object>> fieldsClassValues,
			String primaryKey) {
		HashMap<String, Integer> keyOrder = new HashMap<>(); 

		int i = 0;
		for( Entry<String, SimpleEntry<Class<?>, Object>> entry : fieldsClassValues.entrySet())
		{
			if(!entry.getKey().equals(primaryKey))
				keyOrder.put(entry.getKey(), ++i);
		}
		
		keyOrder.put(primaryKey, ++i);

		return keyOrder;
	}

	private String getPrimaryKey(Object o) {
		
		Field[] fields = o.getClass().getDeclaredFields();
		
		for(Field field : fields) {
			if(field.isAnnotationPresent(SQLInfo.class) && field.getAnnotation(SQLInfo.class).primaryKey())
				return field.getName();
		}
		return null;
	}
	
	private String generateUpdateQuery(LinkedHashMap<String, SimpleEntry<Class<?>, Object>> paramValues, Set<String> keys, String primaryKey) {

		String query = "UPDATE " + getTable() + " SET ";
		
		for(String name : paramValues.keySet()) {
			if(!name.equals(primaryKey))
				query += name + " = ?,";
		}

		query = query.substring(0, query.length()-1);

		query += " WHERE " + primaryKey + " = ?";

		return query;
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
