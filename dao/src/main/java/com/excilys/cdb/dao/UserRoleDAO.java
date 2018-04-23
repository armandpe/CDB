package com.excilys.cdb.dao;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.cdb.model.UserRole;

@Repository
public class UserRoleDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void create(UserRole userRole) throws FailedDAOOperationException {
		try {
			entityManager.persist(userRole);
		} catch (EntityExistsException e) {
			FailedDAOOperationException ex = new FailedDAOOperationException();
			ex.setMessage("User role creation failed : " + e.getMessage());
			throw ex;
		}
	}
}
