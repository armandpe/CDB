package com.excilys.cdb.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

@Repository
public class CompanyDAO extends DAO<Company> {
	
	private final ComputerDAO computerDAO;
	
	private final String modelClassFullName = Company.class.getName();
	
	private CompanyDAO(JdbcTemplate jdbcTemplate, ComputerDAO computerDAO) {
		super(jdbcTemplate);
		this.computerDAO = computerDAO;
	}
	
	public void delete(long id) throws FailedDAOOperationException {
		Entry<String, Field> primaryKey = getKey(getModelClassFullName(), x -> x.primaryKey());
		Map<String, Object> conditions = new HashMap<>();
		conditions.put(primaryKey.getKey(), id);
		
		List<Computer> computers = computerDAO.getByCompanyId(id);
		
		for (long computerId : computers.stream().mapToLong(computer -> computer.getId()).toArray()) {
			computerDAO.deleteByPrimaryKey(computerId);
		}
		
		deleteByPrimaryKey(id);
	}
	
	@Override
	public String getModelClassFullName() {
		return modelClassFullName;
	}

}
