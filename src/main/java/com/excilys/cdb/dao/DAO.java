package main.java.com.excilys.cdb.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.cdb.Main;
import main.java.com.excilys.cdb.connectionmanager.ConnectionManager;
import main.java.com.excilys.cdb.model.ModelClass;
import main.java.com.excilys.cdb.model.SQLInfo;
import main.java.com.excilys.cdb.model.SQLTable;
import main.java.com.excilys.cdb.utils.BiFunctionException;
import main.java.com.excilys.cdb.utils.FunctionException;

public abstract class DAO<T extends ModelClass> {

	private static Map<Class<?>, BiFunctionException<ResultSet, String, ?, SQLException>> staticResultSetFunctionMap = null;

	public static <T> T[] append(T[] arr, T element) {
		final int len = arr.length;
		arr = Arrays.copyOf(arr, len + 1);
		arr[len] = element;
		return arr;
	}

	public static boolean isNull(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof Optional) {
			return !((Optional<?>) o).isPresent();
		}
		return false;
	}

	protected static Map<Class<?>, BiFunctionException<ResultSet, String, ?, SQLException>> getResultSetFunctionMap() {

		if (staticResultSetFunctionMap == null) {

			staticResultSetFunctionMap = new HashMap<>();

			staticResultSetFunctionMap.put(Integer.class, (x, y) -> x.getInt(y));

			staticResultSetFunctionMap.put(int.class, (x, y) -> x.getInt(y));

			staticResultSetFunctionMap.put(Long.class, (x, y) -> x.getLong(y));

			staticResultSetFunctionMap.put(long.class, (x, y) -> x.getLong(y));

			staticResultSetFunctionMap.put(String.class, (x, y) -> x.getString(y));

			staticResultSetFunctionMap.put(LocalDate.class, (x, y) -> {
				Date d;
				return (d = x.getDate(y)) == null ? null : d.toLocalDate();
			});
		}

		return staticResultSetFunctionMap;
	}

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public String addConditions(String query, Map<String, Object> conditions) {
		query = query + " WHERE ";
		Set<String> keys = conditions.keySet();
		Iterator<String> iterator = keys.iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			query += key + " = " + conditions.get(key);
			if (iterator.hasNext()) {
				query += " AND ";
			}
		}
		return query;
	}

	public String addLeftJoin(String query, String[] names, String table, Map<String, String> jointureCriterias) {

		String[] query2 = query.split(" FROM ", 2);
		query = query2[0] + ", " + String.join(", ", names) + " FROM " + query2[1];
		query += " LEFT JOIN " + table + " ON ";

		Set<String> keys = jointureCriterias.keySet();
		Iterator<String> iterator = keys.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			query += key + " = " + jointureCriterias.get(key);
			if (iterator.hasNext()) {
				query += " AND ";
			}
		}
		return query;
	}

	public String arrayToString(String[] array) {
		return String.join(", ", array);
	}

	public <V> V executeWithConnection(FunctionException<Object[], V, FailedDAOOperationException> f, Object[] objects) throws FailedDAOOperationException {
		ConnectionManager cManager = ConnectionManager.getInstance();
		try (Connection connection = cManager.getConnection()) {

			return f.apply(append(objects, connection));

		} catch (SQLException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}

		return null;
	}

	public List<T> getAll(long offset, long limit) throws FailedDAOOperationException {

		Object[] objects = {offset, limit};
		return executeWithConnection(x -> this.getAll(x), objects);
	}

	public Optional<T> getById(long id) throws FailedDAOOperationException {
		Object[] objects = {id};
		return executeWithConnection(x -> this.getById(x), objects);
	}

	public long getCount() throws FailedDAOOperationException {
		return executeWithConnection(x -> this.getCount(x), new Object[0]);
	}

	/**
	 * Get the SQL fields and field names of a class
	 * 
	 * @param className
	 *            : name of the class to get SQL fields
	 * @return Key : SQL Name, Value : Field Name
	 */
	public Map<String, Field> getMapperSQLFields(String className) {
		HashMap<String, Field> res = new HashMap<>();
		Field[] fields = {};

		try {
			fields = Class.forName(className).getDeclaredFields();
		} catch (SecurityException | ClassNotFoundException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}

		for (Field field : fields) {
			if (field.isAnnotationPresent(SQLInfo.class)) {
				res.put(getTable(className) + "." + field.getAnnotation(SQLInfo.class).name(), field);
			}
		}
		return res;
	}

	public abstract String getModelClassFullName();

	public String selectQuery() {
		String query = "SELECT " + arrayToString(getSQLArgs());
		query += " FROM " + getTable(getModelClassFullName());
		return query;
	}

	protected void addValueToStatement(PreparedStatement ps, Entry<String, SimpleEntry<Field, Object>> fieldClassValue,
			Map<String, Integer> keyOrder) throws SQLException {

		Object value = fieldClassValue.getValue().getValue();

		Field field = fieldClassValue.getValue().getKey();
		Class<?> type = field.getType();

		if (type == Optional.class) {
			type = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
			if (((Optional<?>) value).isPresent()) {
				value = ((Optional<?>) value).get();
			}
		}

		if (value != null && value.getClass() == Optional.class) {
			if (((Optional<?>) value).isPresent()) {
				value = ((Optional<?>) value).get();
			}
		}

		int order = keyOrder.get(fieldClassValue.getKey());

		if (type == String.class) {
			ps.setString(order, (String) value);
		} else if (type == LocalDateTime.class) {

			if (!isNull(value)) {
				ps.setDate(order, Date.valueOf(((LocalDateTime) value).toLocalDate()));
			} else {
				ps.setDate(order, null);
			}
		} else if (type == LocalDate.class) {

			if (!isNull(value)) {
				ps.setDate(order, Date.valueOf((LocalDate) value));
			} else {
				ps.setDate(order, null);
			}
		} else if (type == Integer.class || type == int.class) {

			if (!isNull(value)) {
				ps.setInt(order, (Integer) value);
			} else {
				ps.setNull(order, Types.INTEGER);
			}
		} else if (type == Long.class || type == long.class) {

			if (!isNull(value)) {
				ps.setLong(order, (Long) value);
			} else {
				ps.setNull(order, Types.LONGNVARCHAR);
			}
		} else {
			logger.error(Main.getErrorMessage("maybe theres no implementation for type " + type.getName(), null));
		}
	}

	@SuppressWarnings("unchecked")
	protected Optional<T> buildItem(String className, ResultSet resultSet) {

		Constructor<?> constructor = null;
		try {
			constructor = Class.forName(className).getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			logger.error(Main.getErrorMessage("error getting parameterless constructor", e.getMessage()));
		}

		T result = null;
		try {
			Object toCheck = constructor.newInstance(new Object[0]);
			result = (T) toCheck;

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			logger.error(Main.getErrorMessage("error invoking parameterless constructor", e.getMessage()));
		}

		Map<String, Field> sqlFieldsMap = getMapperSQLFields(className);

		for (Entry<String, Field> sqlEntry : sqlFieldsMap.entrySet()) {
			Field field = sqlEntry.getValue();
			field.setAccessible(true);
			try {
				field.set(result, getFieldValue(sqlEntry, resultSet));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error(Main.getErrorMessage("error setting field " + field.getName(), e.getMessage()));
			}
		}
		return Optional.ofNullable(result);
	}

	protected void deleteByPrimaryKey(Object primaryKeyValue) throws FailedDAOOperationException {
		SimpleEntry<String, Field> primaryKey = getKey(getModelClassFullName(), x -> x.primaryKey());

		String table = getTable(getModelClassFullName());
		String query = "DELETE FROM " + table + " WHERE " + primaryKey.getKey() + " = ?";

		LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues = new LinkedHashMap<>();
		fieldsClassValues.put(primaryKey.getKey(),
				new SimpleEntry<Field, Object>(primaryKey.getValue(), primaryKeyValue));

		HashMap<String, Integer> keyOrder = new HashMap<>();
		keyOrder.put(primaryKey.getKey(), 1);

		executeStatement(query, fieldsClassValues, keyOrder);
	}

	protected void executeStatement(String query, LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues,
			HashMap<String, Integer> keyOrder) throws FailedDAOOperationException {
		PreparedStatement preparedStatement;
		ConnectionManager cManager = ConnectionManager.getInstance();

		try (Connection connection = cManager.getConnection()) {
			preparedStatement = connection.prepareStatement(query);

			for (Entry<String, SimpleEntry<Field, Object>> fieldClassValue : fieldsClassValues.entrySet()) {
				addValueToStatement(preparedStatement, fieldClassValue, keyOrder);
			}
			preparedStatement.executeUpdate();
			preparedStatement.close();

		} catch (SQLException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
			throw new FailedDAOOperationException();
		}
		
	}

	protected List<T> getAll(Object... params) {
		long offset = (long) params[0];
		long limit = (long) params[1];
		Connection connection = (Connection) params[2];

		ArrayList<T> result = new ArrayList<>();

		// final static String QUERY_GET_ALL ="SELECT computer.id, computer.name,
		// introduced, discontinued, company_id, company.name, company.id
		// FROM computer LEFT JOIN company ON company_id = company.id ";

		String query = selectQuery();
		if (hasKey(getModelClassFullName(), x -> x.foreignKey())) {
			SimpleEntry<String, Field> foreign = getKey(getModelClassFullName(), x -> x.foreignKey());
			Class<?> fieldType = foreign.getValue().getType();

			if (fieldType == Optional.class) {
				fieldType = (Class<?>) ((ParameterizedType) foreign.getValue().getGenericType())
						.getActualTypeArguments()[0];
			}
			String fieldTypeName = fieldType.getName();

			Map<String, Field> sqlFieldsMap = getMapperSQLFields(fieldTypeName);
			Map<String, String> constraints = new HashMap<>();
			String tableName = getTable(fieldTypeName);

			constraints.put(getKey(fieldTypeName, x -> x.primaryKey()).getKey(),
					getKey(getModelClassFullName(), x -> x.foreignKey()).getKey());

			query = addLeftJoin(query, sqlFieldsMap.keySet().toArray(new String[sqlFieldsMap.keySet().size()]),
					tableName, constraints);
		}

		query += " LIMIT " + offset + ", " + limit;

		ResultSet sqlResults = null;

		try {
			Statement stmt = connection.createStatement();
			sqlResults = stmt.executeQuery(query);
		} catch (Exception e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}

		try {
			while (sqlResults.next()) {
				Optional<T> c = buildItem(getModelClassFullName(), sqlResults);
				if (c.isPresent()) {
					result.add(c.get());
				}
			}
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		try {
			sqlResults.close();
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}

		return result;
	}

	protected Optional<T> getById(Object... objects) {
		long id = (long) objects[0];
		Connection connection = (Connection) objects[1];

		Optional<T> result = Optional.empty();

		String query = selectQuery();

		SimpleEntry<String, Field> primaryKey = getKey(getModelClassFullName(), x -> x.primaryKey());

		Map<String, Object> conditions = new HashMap<>();
		conditions.put(primaryKey.getKey(), id);

		if (hasKey(getModelClassFullName(), x -> x.foreignKey())) {
			SimpleEntry<String, Field> foreign = getKey(getModelClassFullName(), x -> x.foreignKey());
			Class<?> fieldType = foreign.getValue().getType();

			if (fieldType == Optional.class) {
				fieldType = (Class<?>) ((ParameterizedType) foreign.getValue().getGenericType())
						.getActualTypeArguments()[0];
			}
			String fieldTypeName = fieldType.getName();

			Map<String, Field> sqlFieldsMap = getMapperSQLFields(fieldTypeName);
			Map<String, String> constraints = new HashMap<>();
			String tableName = getTable(fieldTypeName);

			constraints.put(getKey(fieldTypeName, x -> x.primaryKey()).getKey(),
					getKey(getModelClassFullName(), x -> x.foreignKey()).getKey());

			query = addLeftJoin(query, sqlFieldsMap.keySet().toArray(new String[sqlFieldsMap.keySet().size()]),
					tableName, constraints);
		}

		query = addConditions(query, conditions);

		ResultSet sqlResults = null;

		try {
			Statement stmt = connection.createStatement();
			sqlResults = stmt.executeQuery(query);
		} catch (Exception e) {
			logger.error(Main.getErrorMessage("query : " + query, e.getMessage()));
		}

		try {
			if (sqlResults.next()) {
				result = buildItem(getModelClassFullName(), sqlResults);
			}
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		try {
			sqlResults.close();
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}

		return result;
	}

	protected long getCount(Object... objects) throws FailedDAOOperationException {
		Connection connection = (Connection) objects[0];

		String query = "SELECT count(" + getKey(getModelClassFullName(), x -> x.primaryKey()).getKey()
				+ ") as nbComputer FROM " + getTable(getModelClassFullName());

		ResultSet sqlResults = null;
		int result = -1;
		Statement stmt = null;
		
		try {
			stmt = connection.createStatement();
			sqlResults = stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
			throw new FailedDAOOperationException();
		}

		try {
			if (sqlResults.next()) {
				result = sqlResults.getInt("nbComputer");
			} else {
				logger.error(Main.getErrorMessage("This isn't supposed to happend", null));
				throw new FailedDAOOperationException();
			}
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage("Error getting nbComputer", e.getMessage()));
			throw new FailedDAOOperationException();
		}
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage("Error closing sqlResult", e.getMessage()));
			throw new FailedDAOOperationException();
		}
		
		

		return result;
	}

	protected Object getFieldValue(Entry<String, Field> sqlEntry, ResultSet resultSet) {

		Map<Class<?>, BiFunctionException<ResultSet, String, ?, SQLException>> resultSetFunctionMap = getResultSetFunctionMap();
		Class<?> valueType = sqlEntry.getValue().getType();
		boolean optional = false;

		if (valueType == Optional.class) {
			valueType = (Class<?>) ((ParameterizedType) sqlEntry.getValue().getGenericType())
					.getActualTypeArguments()[0];
			optional = true;
		}

		try {
			Object result = null;
			if (sqlEntry.getValue().getAnnotation(SQLInfo.class).foreignKey()) {
				Optional<?> returned = buildItem(valueType.getName(), resultSet);
				result = returned.isPresent() ? returned.get() : null;
			} else {
				result = resultSetFunctionMap.get(valueType).apply(resultSet, sqlEntry.getKey());
			}
			return optional ? Optional.ofNullable(result) : result;
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage("error exploiting resultSet for type " + valueType, e.getMessage()));
			return optional ? Optional.empty() : null;
		}
	}

	protected SimpleEntry<String, Field> getKey(String className, Function<SQLInfo, Boolean> getKey) {
		Class<?> objectClass;
		try {
			objectClass = Class.forName(className);
			Field[] fields = objectClass.getDeclaredFields();

			for (Field field : fields) {
				if (field.isAnnotationPresent(SQLInfo.class) && getKey.apply(field.getAnnotation(SQLInfo.class))) {
					return new SimpleEntry<String, Field>(
							getTable(className) + "." + field.getAnnotation(SQLInfo.class).name(), field);
				}
			}
		} catch (ClassNotFoundException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		return null;
	}

	protected String[] getSQLArgs() {
		String[] template = {};
		return this.getMapperSQLFields(getModelClassFullName()).keySet().toArray(template);
	}

	protected String getTable(String className) {
		try {
			return Class.forName(className).getAnnotation(SQLTable.class).name();
		} catch (ClassNotFoundException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
			return null;
		}
	}

	protected boolean hasKey(String className, Function<SQLInfo, Boolean> getKey) {
		Class<?> objectClass;
		try {
			objectClass = Class.forName(className);
			Field[] fields = objectClass.getDeclaredFields();

			for (Field field : fields) {
				if (field.isAnnotationPresent(SQLInfo.class) && getKey.apply(field.getAnnotation(SQLInfo.class))) {
					return true;
				}
			}
		} catch (ClassNotFoundException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		return false;
	}
}
