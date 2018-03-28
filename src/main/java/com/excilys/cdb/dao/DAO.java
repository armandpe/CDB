package com.excilys.cdb.dao;

import java.util.List;
import java.util.Optional;

import com.excilys.cdb.model.ModelClass;

public interface DAO<T extends ModelClass> {

	List<T> getAll(long offset, long limit, String search, String orderByVar, boolean asc) throws FailedDAOOperationException;

	Optional<T> getById(long id) throws FailedDAOOperationException;

	long getCount(String search) throws FailedDAOOperationException;
	
	void deleteById(long id) throws FailedDAOOperationException;
}
