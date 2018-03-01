package main.java.com.excilys.cdb.dao;

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

import main.java.com.excilys.cdb.Main;
import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;

/**
 * @author excilys
 *
 */
public class ComputerDAO extends DAO<Computer> {

	private static ComputerDAO computerDAO;

	public static ComputerDAO getInstance() {
		if (computerDAO == null) {
			computerDAO = new ComputerDAO();
		}

		return computerDAO;
	}

	private ComputerDAO() { }

	/**
	 * @param computer Computer to create
	 * @return see {@link executeStatement}
	 */
	public int createComputer(Computer computer) {
		Map<String, String> mapperSQLFields = getMapperSQLFields();
		Set<String> keys = mapperSQLFields.keySet();

		LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues = generateFieldsClassValues(mapperSQLFields, computer);

		HashMap<String, Integer> keyOrder = getKeyOrder(fieldsClassValues);

		String query = generateCreateQuery(fieldsClassValues, keys);

		return executeStatement(query, fieldsClassValues, keyOrder);
	}

	public int deleteComputer(long id) {
		return deleteByPrimaryKey(id);
	}

	@Override
	public String getModelClassFullName() {
		return Computer.class.getName();
	}

	public int updateComputer(Computer computer) {
		Map<String, Field> mapperSQLFields = getMapperSQLFields(getModelClassFullName());
		Set<String> keys = mapperSQLFields.keySet();

		LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues = generateFieldsClassValues(mapperSQLFields, computer);

		String primaryKey = getKey(getModelClassFullName(), x -> x.primaryKey()).getKey();

		HashMap<String, Integer> keyOrder = getKeyOrder(fieldsClassValues, primaryKey);

		String query = generateUpdateQuery(fieldsClassValues, keys, primaryKey);

		return executeStatement(query, fieldsClassValues, keyOrder);
	}

	private String generateCreateQuery(LinkedHashMap<String, SimpleEntry<Field, Object>> paramValues, Set<String> keys) {

		String[] template = {};
		String query = "INSERT INTO " + getTable() + " ( " + arrayToString(paramValues.keySet().toArray(template)) + " ) VALUES ( ";

		for (int i = 0; i < keys.size(); ++i) {
			query += "?,";
			}

		query = query.substring(0, query.length() - 1);

		query += " )";

		return query;
	}

	private LinkedHashMap<String, SimpleEntry<Field, Object>> generateFieldsClassValues(Map<String, String> mapperSQLFields, Computer computer) {
		LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues = new LinkedHashMap<>();

		Field field = null;

		for (Entry<String, String> entry : mapperSQLFields.entrySet()) {
			Object value = null;
			try {
				field = Computer.class.getDeclaredField(entry.getValue());
				field.setAccessible(true);
				value = field.get(computer);
				
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				logger.error(Main.getErrorMessage("Reflexion error", e.getMessage()));
			}
			fieldsClassValues.put(entry.getKey(), new SimpleEntry<Field, Object>(field, value));
		}

		return fieldsClassValues;
	}

	private String generateUpdateQuery(LinkedHashMap<String, SimpleEntry<Field, Object>> paramValues, Set<String> keys, String primaryKey) {

		String query = "UPDATE " + getTable() + " SET ";
		for (String name : paramValues.keySet()) {
			if (!name.equals(primaryKey)) {
				query += name + " = ?,";
			}
		}

		query = query.substring(0, query.length() - 1);
		query += " WHERE " + primaryKey + " = ?";
		return query;
	}

	private HashMap<String, Integer> getKeyOrder(LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues) {
		HashMap<String, Integer> keyOrder = new HashMap<>();

		int i = 0;
		for (Entry<String, SimpleEntry<Field, Object>> entry : fieldsClassValues.entrySet()) {
			keyOrder.put(entry.getKey(), ++i);
		}

		return keyOrder;
	}

	private HashMap<String, Integer> getKeyOrder(LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues,
			String primaryKey) {
		HashMap<String, Integer> keyOrder = new HashMap<>();

		int i = 0;
		for (Entry<String, SimpleEntry<Field, Object>> entry : fieldsClassValues.entrySet()) {
			if (!entry.getKey().equals(primaryKey)) {
				keyOrder.put(entry.getKey(), ++i);
			}
		}

		keyOrder.put(primaryKey, ++i);

		return keyOrder;
	}

}
