package com.excilys.cdb.service;

import com.excilys.cdb.dao.FailedDAOOperationException;

import java.util.Optional;

public interface Service<T> {
	String getDaoClassFullName();

	Optional<T> getById(long id) throws FailedDAOOperationException;

	long getCount() throws FailedDAOOperationException;
}
