package com.excilys.cdb.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.excilys.cdb.Main;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.SQLInfo;

@Repository
public class ComputerDAO extends DAO<Computer> {

	public void create(Computer computer) throws FailedDAOOperationException {
		Map<String, Field> mapperSQLFields = getMapperSQLFields(getModelClassFullName());
		Set<String> keys = mapperSQLFields.keySet();

		LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues = generateFieldsClassValues(mapperSQLFields,
				computer);

		HashMap<String, Integer> keyOrder = getKeyOrder(fieldsClassValues);

		String query = generateCreateQuery(fieldsClassValues, keys);

		executeQuery(query, fieldsClassValues, keyOrder);
	}

	public void delete(long id) throws FailedDAOOperationException {
		deleteByPrimaryKey(id);
	}

	@Override
	public String getModelClassFullName() {
		return Computer.class.getName();
	}

	public void update(Computer computer) throws FailedDAOOperationException {
		Map<String, Field> mapperSQLFields = getMapperSQLFields(getModelClassFullName());
		Set<String> keys = mapperSQLFields.keySet();

		LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues = generateFieldsClassValues(mapperSQLFields,
				computer);

		String primaryKey = getKey(getModelClassFullName(), x -> x.primaryKey()).getKey();

		HashMap<String, Integer> keyOrder = getKeyOrder(fieldsClassValues, primaryKey);

		String query = generateUpdateQuery(fieldsClassValues, keys, primaryKey);

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
				}
			}

			int order = keyOrder.get(fieldClassValue.getKey());
		}

		executeQuery(query, fieldsClassValues, keyOrder);
	}

	protected String generateCreateQuery(LinkedHashMap<String, SimpleEntry<Field, Object>> paramValues,
			Set<String> keys) {

		String[] template = {};
		String query = "INSERT INTO " + getTable(getModelClassFullName()) + " ( "
				+ arrayToString(paramValues.keySet().toArray(template)) + " ) VALUES ( ";

		for (int i = 0; i < keys.size(); ++i) {
			query += "?,";
		}

		query = query.substring(0, query.length() - 1);
		query += " )";
		return query;
	}

	protected LinkedHashMap<String, SimpleEntry<Field, Object>> generateFieldsClassValues(
			Map<String, Field> mapperSQLFields, Computer computer) {
		LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues = new LinkedHashMap<>();

		Field field = null;

		for (Entry<String, Field> entry : mapperSQLFields.entrySet()) {
			Object value = null;
			try {
				field = entry.getValue();
				field.setAccessible(true);
				value = field.get(computer);

				if (field.getAnnotation(SQLInfo.class).foreignKey()) {
					Class<?> fieldType = field.getType();
					boolean isOptional = false;
					if (fieldType == Optional.class) {
						fieldType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
						isOptional = true;
					}

					if (isOptional) {
						value = ((Optional<?>) value).isPresent() ? ((Optional<?>) value).get() : null;
					}

					Entry<String, Field> primaryKey = getKey(fieldType.getName(), x -> x.primaryKey());
					Field primaryField = primaryKey.getValue();
					field = primaryKey.getValue();
					if (value == null) {
						try {
							if (primaryField.getType() == Long.class) {
								Optional<Long> l = Optional.empty();
								value = l;
							} else {
								value = primaryField.getType().getDeclaredConstructor(new Class<?>[0])
										.newInstance(new Object[0]);
							}
						} catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {
							logger.error(Main.getErrorMessage(
									"error invoking parameterless constructor of " + primaryField.getType(),
									e.getMessage()));
						}
					} else {
						primaryField.setAccessible(true);
						value = primaryField.get(value);
						value = isOptional ? Optional.ofNullable(value) : value;
					}

				}

			} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
				logger.error(Main.getErrorMessage("Reflexion error", e.getMessage()));
			}
			fieldsClassValues.put(entry.getKey(), new SimpleEntry<Field, Object>(field, value));
		}

		return fieldsClassValues;
	}

	protected String generateUpdateQuery(LinkedHashMap<String, SimpleEntry<Field, Object>> paramValues, Set<String> keys,
			String primaryKey) {

		String query = "UPDATE " + getTable(getModelClassFullName()) + " SET ";
		for (String name : paramValues.keySet()) {
			if (!name.equals(primaryKey)) {
				query += name + " = ?,";
			}
		}

		query = query.substring(0, query.length() - 1);
		query += " WHERE " + primaryKey + " = ?";
		return query;
	}

	protected List<Computer> getByCompanyId(long companyId) throws FailedDAOOperationException {
		Entry<String, Field> foreignKey = getKey(getModelClassFullName(), x -> x.foreignKey());
		Map<String, String> conditions = new HashMap<>();
		conditions.put(foreignKey.getKey(), "" + companyId);

		List<Computer> result = getWithConditions(conditions);

		return result;
	}

	protected HashMap<String, Integer> getKeyOrder(LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues) {
		HashMap<String, Integer> keyOrder = new HashMap<>();

		int i = 0;
		for (Entry<String, SimpleEntry<Field, Object>> entry : fieldsClassValues.entrySet()) {
			keyOrder.put(entry.getKey(), ++i);
		}

		return keyOrder;
	}

	protected HashMap<String, Integer> getKeyOrder(LinkedHashMap<String, SimpleEntry<Field, Object>> fieldsClassValues,
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
