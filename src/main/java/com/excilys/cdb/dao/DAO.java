package com.excilys.cdb.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import com.excilys.cdb.Main;
import com.excilys.cdb.constant.DbConstant;
import com.excilys.cdb.model.ModelClass;
import com.excilys.cdb.model.SQLInfo;
import com.excilys.cdb.model.SQLTable;
import com.excilys.cdb.utils.BiFunctionException;

public abstract class DAO<T extends ModelClass> {

	private static Map<Class<?>, BiFunctionException<ResultSet, String, ?, SQLException>> staticResultSetFunctionMap = null; 

	public static <T> T[] append(T[] arr, T element) {
		final int len = arr.length;
		arr = Arrays.copyOf(arr, len + 1);
		arr[len] = element;
		return arr;
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

	protected static boolean isNull(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof Optional) {
			return !((Optional<?>) o).isPresent();
		}
		return false;
	}

	private final String AFTER_VARIABLE_SELECT = " FROM ";
	
	private final String AFTER_VARIABLE_COUNT = ") as " + DbConstant.COUNT_VAR + AFTER_VARIABLE_SELECT;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public String arrayToString(String[] array) {
		return String.join(", ", array);
	}

	public List<T> getAll(long offset, long limit, String search, String orderByVar, boolean asc) throws FailedDAOOperationException {
		Map<String, Field> foreignFields = new HashMap<>();
		String query = selectQuery();
		
		if (hasKey(getModelClassFullName(), x -> x.foreignKey())) {
			query = getForeignKeyQuery(query, foreignFields);
		}

		if (search != null) {
			SimpleEntry<Integer, String> nbToSearchAndQuery = getSearchQuery(query, foreignFields);
			query = nbToSearchAndQuery.getValue();
		}
		query += " ORDER BY " + orderByVar + (asc ? " ASC" : " DESC");
		query += " LIMIT " + offset + ", " + limit;

		List<T> result = new ArrayList<>();
		try {
			
			result = search == null ? jdbcTemplate.query(query, getRowMapper()) : jdbcTemplate.query(query, getRowMapper(), search + '%', search + '%');
			result.removeIf(x -> x == null);
		} catch (DataAccessException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
			throw new FailedDAOOperationException();
		}
		
		return result;
	}

	public Optional<T> getById(long id) throws FailedDAOOperationException {
		Entry<String, Field> primaryKey = getKey(getModelClassFullName(), x -> x.primaryKey());
		Map<String, String> conditions = new HashMap<>();
		conditions.put(primaryKey.getKey(), "" + id);

		List<T> result = getWithConditions(conditions);

		return result.size() == 0 ? Optional.empty() : Optional.ofNullable(result.get(0));
	}

	public long getCount(String search) throws FailedDAOOperationException {
		Map<String, Field> foreignFields = new HashMap<>();

		String query = countQuery();
		String foreignTableName = null;
		
		
		if (hasKey(getModelClassFullName(), x -> x.foreignKey())) {
			String fieldTypeName = getForeignFieldTypeName();
			foreignTableName = getTable(fieldTypeName);				
			foreignFields = getMapperSQLFields(fieldTypeName);	
			Map<String, String> constraints = new HashMap<>();
			constraints.put(getKey(fieldTypeName, x -> x.primaryKey()).getKey(),
					getKey(getModelClassFullName(), x -> x.foreignKey()).getKey());
			query = addLeftJoin(query, foreignTableName, constraints);
		}
		
		if (search != null) {
			SimpleEntry<Integer, String> nbToSearchAndQuery = getSearchQuery(query, foreignFields);
			query = nbToSearchAndQuery.getValue();
		}

		int result = -1;
		try {
			result = search == null ? jdbcTemplate.queryForObject(query, getRowMapperCount()) : jdbcTemplate.queryForObject(query, getRowMapperCount(), search + '%', search + '%');
		} catch (DataAccessException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
			throw new FailedDAOOperationException();
		}
		return result;
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

	protected String addConditions(String query, Map<String, String> conditions) {
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

	protected String addLeftJoin(String query, String table, Map<String, String> jointureCriterias) {
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

	protected String addLeftJoinWithFields(String query, String[] names, String table, Map<String, String> jointureCriterias) {

		String[] query2 = query.split(AFTER_VARIABLE_SELECT, 2);
		query = query2[0] + ", " + String.join(", ", names) + AFTER_VARIABLE_SELECT + query2[1];
		query = query.replace("\\", "");
		query = addLeftJoin(query, table, jointureCriterias);
		return query;
	}
	
	protected String addSearch(String query, String[] keySet) {
		//		SEARCH("SELECT computer.id, computer.name, introduced, discontinued, company_id, company.name FROM computer LEFT JOIN company ON company_id = company.id"
		//	            + " WHERE (computer.name LIKE ? OR company.name LIKE ?) LIMIT ? OFFSET ?;")

		query += " WHERE (";

		for (int i = 0; i < keySet.length; ++i) {
			query += keySet[i] + " LIKE ?" + (i == keySet.length - 1 ? "" : " OR ");
		}
		query += ")";

		return query;
	}

	protected void addSearchToStatement(String searchWith, int nbToSearch, PreparedStatement preparedStatement) {

		for (int i = 1; i <= nbToSearch; ++i) {
			try {
				preparedStatement.setString(i, searchWith + '%');
			} catch (SQLException e) {
				logger.error(Main.getErrorMessage("Error during search query", e.getMessage()));
			}
		}
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
	@Nullable
	protected T buildItem(String className, ResultSet resultSet) {

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
		return result;
	}

	protected String countQuery() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("SELECT count(");
		stringBuilder.append(getKey(getModelClassFullName(), x -> x.primaryKey()).getKey()); 
		stringBuilder.append(AFTER_VARIABLE_COUNT);
		stringBuilder.append(getTable(getModelClassFullName()));
		
		return stringBuilder.toString();
	}

	protected void deleteByPrimaryKey(Object primaryKeyValue) throws FailedDAOOperationException {
		Entry<String, Field> primaryKey = getKey(getModelClassFullName(), x -> x.primaryKey());

		String table = getTable(getModelClassFullName());
		String query = "DELETE FROM " + table + " WHERE " + primaryKey.getKey() + " = ?";

		LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues = new LinkedHashMap<>();
		fieldsClassValues.put(primaryKey.getKey(),
				new SimpleEntry<Field, Object>(primaryKey.getValue(), primaryKeyValue));

		HashMap<String, Integer> keyOrder = new HashMap<>();
		keyOrder.put(primaryKey.getKey(), 1);

		executeQuery(query, fieldsClassValues, keyOrder);
	}
	
	protected void executeQuery(String query, LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues, HashMap<String, Integer> keyOrder) throws FailedDAOOperationException {
		List<Object> values = new ArrayList<>(keyOrder.size());
		List<Class<?>> types = new ArrayList<>(keyOrder.size());
		
		for (int i = 0; i < keyOrder.size(); ++i) {
			values.add(null);
			types.add(null);
		}
		
		for (Entry<String, SimpleEntry<Field, Object>> fieldClassValue : fieldsClassValues.entrySet()) {
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
				} else {
					value = null;
				}
			}

			int order = keyOrder.get(fieldClassValue.getKey()) - 1;
			values.set(order, value);
			types.set(order, type);
		}

		try {			
			jdbcTemplate.update(query, values.toArray(new Object[values.size()]));	
		} catch (DataAccessException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
			throw new FailedDAOOperationException();
		}
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
				result = buildItem(valueType.getName(), resultSet);
			} else {
				result = resultSetFunctionMap.get(valueType).apply(resultSet, sqlEntry.getKey());
			}
			return optional ? Optional.ofNullable(result) : result;
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage("error exploiting resultSet for type " + valueType, e.getMessage()));
			return optional ? Optional.empty() : null;
		}
	}

	protected String getForeignFieldTypeName() {
		Entry<String, Field> foreign = getKey(getModelClassFullName(), x -> x.foreignKey());
		Class<?> fieldType = foreign.getValue().getType();

		if (fieldType == Optional.class) {
			fieldType = (Class<?>) ((ParameterizedType) foreign.getValue().getGenericType())
					.getActualTypeArguments()[0];
		}
		return fieldType.getName();
	}

	protected String getForeignKeyQuery(String query, Map<String, Field> foreignFields) {

		String fieldTypeName = getForeignFieldTypeName();
		foreignFields.putAll(getMapperSQLFields(fieldTypeName));
		Map<String, String> constraints = new HashMap<>();
		String tableName = getTable(fieldTypeName);

		constraints.put(getKey(fieldTypeName, x -> x.primaryKey()).getKey(),
				getKey(getModelClassFullName(), x -> x.foreignKey()).getKey());

		query = addLeftJoinWithFields(query, foreignFields.keySet().toArray(new String[foreignFields.keySet().size()]),
				tableName, constraints);

		return query;
	}

	protected Entry<String, Field> getKey(String className, Function<SQLInfo, Boolean> getKey) {

		for (Entry<String, Field> pair : getWithAnnotation(className, getKey).entrySet()) {
			return pair;
		}
		return null;
	}

	protected RowMapper<T> getRowMapper() {
		return new RowMapper<T>() { public T mapRow(ResultSet rs, int rownum) throws SQLException {
			return buildItem(getModelClassFullName(), rs);
		}
		};
	}
	
	protected RowMapper<Integer> getRowMapperCount() {
		return new RowMapper<Integer>() { public Integer mapRow(ResultSet rs, int rownum) throws SQLException {
			return rs.getInt(DbConstant.COUNT_VAR);
		}
		};
	}

	protected SimpleEntry<Integer, String> getSearchQuery(String query, Map<String, Field> foreignFields) {
		Map<String, Field> primaryFields = this.getMapperSQLFields(getModelClassFullName());
		Map<String, Field> fieldsToSearch = new HashMap<>();

		primaryFields.entrySet().forEach(field -> {
			if (field.getValue().getAnnotation(SQLInfo.class).searchable()) {
				fieldsToSearch.put(field.getKey(), field.getValue()); 
			}
		});

		foreignFields.entrySet().forEach(field -> {
			if (field.getValue().getAnnotation(SQLInfo.class).searchable()) {
				fieldsToSearch.put(field.getKey(), field.getValue()); 
			}
		});

		query = addSearch(query, fieldsToSearch.keySet().toArray(new String[fieldsToSearch.size()]));

		return new SimpleEntry<Integer, String>(fieldsToSearch.size(), query);
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

	protected Map<String, Field> getWithAnnotation(String className, Function<SQLInfo, Boolean> getValue) {
		Class<?> objectClass;
		Map<String, Field> result = new HashMap<>();
		try {
			objectClass = Class.forName(className);
			Field[] fields = objectClass.getDeclaredFields();

			for (Field field : fields) {
				if (field.isAnnotationPresent(SQLInfo.class) && getValue.apply(field.getAnnotation(SQLInfo.class))) {
					result.put(getTable(className) + "." + field.getAnnotation(SQLInfo.class).name(), field);
				}
			}
		} catch (ClassNotFoundException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		return result;
	}

	protected List<T> getWithConditions(Map<String, String> conditions) throws FailedDAOOperationException {
		String query = selectQuery();
		Map<String, Field> foreignFields = new HashMap<>();

		if (hasKey(getModelClassFullName(), x -> x.foreignKey())) {
			query = getForeignKeyQuery(query, foreignFields);
		}

		query = addConditions(query, conditions);

		List<T> result = new ArrayList<>();
		try {
			result = jdbcTemplate.query(query, getRowMapper());
			result.removeIf(x -> x == null);
		} catch (DataAccessException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
			throw new FailedDAOOperationException();
		}

		return result;
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

	protected String selectQuery() {
		String query = "SELECT " + arrayToString(getSQLArgs());
		query += AFTER_VARIABLE_SELECT + getTable(getModelClassFullName());
		return query;
	}
}
