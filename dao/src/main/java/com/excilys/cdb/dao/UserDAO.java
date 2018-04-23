package com.excilys.cdb.dao;

import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.cdb.model.QUser;
import com.excilys.cdb.model.User;
import com.querydsl.jpa.impl.JPAQuery;

@Repository
public class UserDAO {

	@PersistenceContext
	private EntityManager entityManager;

	private QUser qUser = QUser.user;

	public Optional<User> getByName(String name) {

		Optional<User> result = Optional.ofNullable(new JPAQuery<Void>(entityManager).select(qUser).from(qUser).where(qUser.username.eq(name)).fetchOne());
		return result;
	}

	@Transactional
	public void create(User user) throws FailedDAOOperationException {
		try {
			entityManager.persist(user);
		} catch (EntityExistsException e) {
			FailedDAOOperationException ex = new FailedDAOOperationException();
			ex.setMessage("User creation failed : " + e.getMessage());
			throw ex;
		}
	}
}
