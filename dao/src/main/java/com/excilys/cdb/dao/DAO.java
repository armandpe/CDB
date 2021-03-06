package com.excilys.cdb.dao;

import java.util.Optional;

import com.excilys.cdb.model.ModelClass;

public interface DAO<T extends ModelClass> {
	
	Optional<T> getById(long id) throws FailedDAOOperationException;

}
