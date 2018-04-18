package com.excilys.cdb.dao;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

import com.excilys.cdb.model.QUser;
import com.excilys.cdb.model.User;
import com.querydsl.jpa.impl.JPAQuery;

public class UserDAO {

@PersistenceContext
private EntityManager entityManager;

private QUser qUser = QUser.user;

public Optional<User> getByName(String name) {
	Optional<User> result = Optional.ofNullable(new JPAQuery<Void>(entityManager).select(qUser).from(qUser).where(qUser.username.eq(name)).fetchOne());
	return result;
}

public void create(User user) {
	EntityTransaction entityTransaction = entityManager.getTransaction();
	entityTransaction.begin();

	entityManager.persist(user);
	
	entityTransaction.commit();}
}
