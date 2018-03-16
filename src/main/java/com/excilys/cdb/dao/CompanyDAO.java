package com.excilys.cdb.dao;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.Main;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

@Repository
public class CompanyDAO extends DAO<Company> {
	
	@Autowired
	private ComputerDAO computerDAO;
	
	private String modelClassFullName = null;
	
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
		if (modelClassFullName == null) {
			modelClassFullName = Company.class.getName();
		}
		
		return modelClassFullName;
	}
	
	protected Optional<Company> buildItem(ResultSet rs) {
		try {
			Company c = new Company();
			c.setId(rs.getLong("id"));
			c.setName(rs.getString("name"));
			return Optional.of(c);
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
			return Optional.empty();
		}
	}

}
