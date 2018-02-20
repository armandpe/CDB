package com.excilys.cdb.DAO;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Level;

import com.excilys.cdb.Main;
import com.excilys.cdb.Model.Computer;

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
			c.setIntroduced(Optional.ofNullable(temp == null ? null : temp.toLocalDateTime().toLocalDate()));
			temp = rs.getTimestamp("discontinued");
			c.setDiscontinued(Optional.ofNullable(temp == null ? null : temp.toLocalDateTime().toLocalDate()));
			c.setCompanyId(Optional.ofNullable(rs.getLong("company_id")));
			return Optional.of(c);
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return Optional.ofNullable(null);
		}
	}

	public int createComputer(Computer computer) {
		Map<String, String> mapperSQLFields = getMapperSQLFields();
		Set<String> keys = mapperSQLFields.keySet();

		LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues = generateFieldsClassValues(mapperSQLFields, computer);

		HashMap<String, Integer> keyOrder = getKeyOrder(fieldsClassValues);

		String query = generateCreateQuery(fieldsClassValues, keys);

		return executeStatement(query, fieldsClassValues, keyOrder);
	}

	private LinkedHashMap<String, SimpleEntry<Field, Object>> generateFieldsClassValues(Map<String, String> mapperSQLFields, Computer computer) {
		LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues = new LinkedHashMap<>();

		Field field = null;

		for(Entry<String, String> entry : mapperSQLFields.entrySet())
		{
			Object value = null;
			try {
				field = Computer.class.getDeclaredField(entry.getValue());
				field.setAccessible(true);
				value = field.get(computer);
				
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				logger.log(Level.ERROR, "Error in method " + Main.getMethodName() + " : " + e.getMessage());
			}
			fieldsClassValues.put(entry.getKey(), new SimpleEntry<Field, Object>(field, value));
		}

		return fieldsClassValues;
	}

	private String generateCreateQuery(LinkedHashMap<String, SimpleEntry<Field, Object>> paramValues, Set<String> keys) {

		String[] template = {};
		String query = "INSERT INTO " + getTable() + " ( " + arrayToString(paramValues.keySet().toArray(template)) + " ) VALUES ( ";

		for(int i = 0; i < keys.size(); ++i)
			query += "?,";

		query = query.substring(0, query.length()-1);

		query += " )";

		return query;
	}

	public int updateComputer(Computer computer) {
		Map<String, String> mapperSQLFields = getMapperSQLFields();
		Set<String> keys = mapperSQLFields.keySet();

		LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues = generateFieldsClassValues(mapperSQLFields, computer);

		String primaryKey = getPrimaryKey().getKey();

		HashMap<String, Integer> keyOrder = getKeyOrder(fieldsClassValues, primaryKey);

		String query = generateUpdateQuery(fieldsClassValues, keys, primaryKey);

		return executeStatement(query, fieldsClassValues, keyOrder);
	}

	private HashMap<String, Integer> getKeyOrder(LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues){
		HashMap<String, Integer> keyOrder = new HashMap<>(); 

		int i = 0;
		for( Entry<String, SimpleEntry<Field, Object>> entry : fieldsClassValues.entrySet())
		{
			keyOrder.put(entry.getKey(), ++i);
		}

		return keyOrder;
	}

	private HashMap<String, Integer> getKeyOrder(LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues,
			String primaryKey) {
		HashMap<String, Integer> keyOrder = new HashMap<>(); 

		int i = 0;
		for( Entry<String, SimpleEntry<Field, Object>> entry : fieldsClassValues.entrySet())
		{
			if(!entry.getKey().equals(primaryKey))
				keyOrder.put(entry.getKey(), ++i);
		}

		keyOrder.put(primaryKey, ++i);

		return keyOrder;
	}

	private String generateUpdateQuery(LinkedHashMap<String, SimpleEntry<Field, Object>> paramValues, Set<String> keys, String primaryKey) {

		String query = "UPDATE " + getTable() + " SET ";

		for(String name : paramValues.keySet()) {
			if(!name.equals(primaryKey))
				query += name + " = ?,";
		}

		query = query.substring(0, query.length()-1);

		query += " WHERE " + primaryKey + " = ?";

		return query;
	}

	public int deleteComputer(long id) {
		return deleteByPrimaryKey(id);
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
