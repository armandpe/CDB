package main.java.com.excilys.cdb.dao;

import java.lang.reflect.Field;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.com.excilys.cdb.Main;
import main.java.com.excilys.cdb.connectionmanager.ConnectionManager;
import main.java.com.excilys.cdb.model.ModelClass;
import main.java.com.excilys.cdb.model.SQLInfo;

public abstract class DAO<T extends ModelClass> {

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

	protected final Logger logger = LogManager.getLogger(this.getClass());

	public String arrayToString(String[] array) {
		return String.join(",", array);
	}

	public <V> V executeWithConnection(Function<Object[], V> f, Object[] objects) {
		ConnectionManager cManager = ConnectionManager.getInstance();
		try (Connection connection = cManager.getConnection()) {

			return f.apply(append(objects, connection));

		} catch (SQLException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}

		return null;
	}

	public List<T> getAll(long offset, long limit) {

		Object[] objects = {offset, limit};
		return executeWithConnection(x -> this.getAll(x), objects);
	}

	public Optional<T> getById(long id) {
		Object[] objects = {id};
		return executeWithConnection(x -> this.getById(x), objects);
	}

	public Optional<T> getById(Object...objects) {
		long id = (long) objects[0];
		Connection connection = (Connection) objects[1];

		Optional<T> result = Optional.empty();

		String query = "SELECT " + arrayToString(getSQLArgs());
		query += " FROM " + getTable() + " WHERE id = " + id + ";";

		ResultSet sqlResults = null;

		try {
			Statement stmt = connection.createStatement();
			sqlResults = stmt.executeQuery(query);
		} catch (Exception e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}

		try {
			if (sqlResults.next()) {
				result = buildItem(sqlResults);
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

	public long getCount() {
		return executeWithConnection(x -> this.getCount(x), new Object[0]);
	}

	public Map<String, String> getMapperSQLFields() {
		HashMap<String, String> res = new HashMap<>();
		Field[] fields = {};

		try {
			fields = Class.forName(getModelClassFullName()).getDeclaredFields();
		} catch (SecurityException | ClassNotFoundException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}

		for (Field field : fields) {
			if (field.isAnnotationPresent(SQLInfo.class)) {
				res.put(field.getAnnotation(SQLInfo.class).name(), field.getName());
			}
		}

		return res;
	}

	public abstract String getModelClassFullName();

	protected void addValueToStatement(PreparedStatement ps, Entry<String, SimpleEntry<Field, Object>> fieldClassValue, Map<String, Integer> keyOrder) throws SQLException {

		Object value = fieldClassValue.getValue().getValue();

		Field field = fieldClassValue.getValue().getKey();
		Class<?> type = field.getType();

		if (type == Optional.class) {
			type = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
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
		} else {
			if (type == LocalDate.class) {
				if (!isNull(value)) {
					ps.setDate(order, Date.valueOf((LocalDate) value));
				} else {
					ps.setDate(order, null);
				}
			} else {
				if (type == Integer.class || type == int.class) {
					if (!isNull(value)) {
						ps.setInt(order, (Integer) value);
					} else {
						ps.setNull(order, Types.INTEGER);
					}
				} else {
					if (type == Long.class || type == long.class) {
						if (!isNull(value)) {
							ps.setLong(order, (Long) value);
						} else {
							ps.setNull(order, Types.LONGNVARCHAR);
						}
					} else {
						logger.error(Main.getErrorMessage("maybe theres no implementation for type " + type.getName(), null));
					}
				}
			}
		}

	}

	protected abstract Optional<T> buildItem(ResultSet result);

	protected int deleteByPrimaryKey(Object primaryKeyValue) {
		SimpleEntry<String, Field> primaryKey = getPrimaryKey();

		String query = "DELETE FROM " + getTable() + " WHERE " + primaryKey.getKey() + " = ?";

		LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues = new LinkedHashMap<>();
		fieldsClassValues.put(primaryKey.getKey(), new SimpleEntry<Field, Object>(primaryKey.getValue(), primaryKeyValue));

		HashMap<String, Integer> keyOrder = new HashMap<>();
		keyOrder.put(primaryKey.getKey(), 1);

		return executeStatement(query, fieldsClassValues, keyOrder);
	}

	protected int executeStatement(String query, LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues, HashMap<String, Integer> keyOrder) {
		PreparedStatement ps;
		ConnectionManager cManager = ConnectionManager.getInstance();
		int result = -1;

		try (Connection connection = cManager.getConnection()) {
			ps = connection.prepareStatement(query);

			for (Entry<String, SimpleEntry<Field, Object>> fieldClassValue : fieldsClassValues.entrySet()) {
				addValueToStatement(ps, fieldClassValue, keyOrder);
			}
			result = ps.executeUpdate();
			ps.close();

		} catch (SQLException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		return result;
	}

	protected List<T> getAll(Object...params) {
		long offset = (long) params[0];
		long limit = (long) params[1];
		Connection connection = (Connection) params[2];

		ArrayList<T> result = new ArrayList<>();

		String query = "SELECT " + arrayToString(getSQLArgs());

		query += " FROM " + getTable() + " LIMIT " + offset  + ", " + limit;

		ResultSet sqlResults = null;

		try {
			Statement stmt = connection.createStatement();
			sqlResults = stmt.executeQuery(query);
		} catch (Exception e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}

		try {
			while (sqlResults.next()) {
				Optional<T> c = buildItem(sqlResults);
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

	protected long getCount(Object...objects) {
		Connection connection = (Connection) objects[0];

		String query = "SELECT count(" + getPrimaryKey().getKey() + ") as nbComputer FROM " + getTable();

		ResultSet sqlResults = null;

		int result = -1;

		try {
			Statement stmt = connection.createStatement();
			sqlResults = stmt.executeQuery(query);
		} catch (Exception e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}

		try {
			if (sqlResults.next()) {
				result = sqlResults.getInt("nbComputer");
			} else {
				logger.error(Main.getErrorMessage("This isn't supposed to happend", null));
			}
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage("Error getting nbComputer", e.getMessage()));
		}
		try {
			sqlResults.close();
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage("Error closing sqlResult", e.getMessage()));
		}

		return result;
	}

	protected SimpleEntry<String, Field> getPrimaryKey() {
		Class<?> objectClass;
		try {
			objectClass = Class.forName(getModelClassFullName());
			Field[] fields = objectClass.getDeclaredFields();

			for (Field field : fields) {
				if (field.isAnnotationPresent(SQLInfo.class) && field.getAnnotation(SQLInfo.class).primaryKey()) {
					return new SimpleEntry<String, Field>(field.getAnnotation(SQLInfo.class).name(), field);
				}
			}
		} catch (ClassNotFoundException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		return null;
	}

	protected String[] getSQLArgs() {
		String[] template = {};
		return this.getMapperSQLFields().keySet().toArray(template);
	}

	protected abstract String getTable();


}
