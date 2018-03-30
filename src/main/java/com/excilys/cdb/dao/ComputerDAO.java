package com.excilys.cdb.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.QComputer;
import com.excilys.cdb.pagemanager.PageManagerLimit;
import com.excilys.cdb.service.ComputerOrderBy;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;

@Repository
public class ComputerDAO implements IComputerDAO {

	private final Logger logger = LoggerFactory.getLogger(PageManagerLimit.class);

	private EntityManagerFactory entityManagerFactory;

	private QComputer qComputer = QComputer.computer;

	public ComputerDAO(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}
	
	@Override
	public List<Computer> getAll(long offset, long limit, String search, ComputerOrderBy orderByVar, boolean asc) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		
		JPAQuery<Computer> query = new JPAQuery<Void>(entityManager).select(qComputer).from(qComputer).offset(offset).limit(limit);
		
		if (search != null) {
			query = query.where(qComputer.name.startsWith(search));
		}
		
		switch (orderByVar) {
		case COMPANY_NAME:
			query = query.orderBy(asc ? qComputer.company.name.asc() : qComputer.company.name.desc());
			break;
		case DISCONTINUED:
			query = query.orderBy(asc ? qComputer.discontinued.asc() : qComputer.discontinued.desc());
			break;
		case INTRODUCED:
			query = query.orderBy(asc ? qComputer.introduced.asc() : qComputer.introduced.desc());
			break;
		case NAME:
			query = query.orderBy(asc ? qComputer.name.asc() : qComputer.name.desc());
			break;
		}
		
		
		List<Computer> result = query.fetch();
		entityManager.close();
		return result;
	}

	@Override
	public Optional<Computer> getById(long id) throws FailedDAOOperationException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Optional<Computer> result = Optional.ofNullable(new JPAQuery<Void>(entityManager).select(qComputer).from(qComputer).where(qComputer.id.eq(id)).fetchOne());
		entityManager.close();
		return result;
	}

	@Override
	public long getCount(String search) throws FailedDAOOperationException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		long count = new JPAQuery<Void>(entityManager).select(qComputer).from(qComputer).fetchCount();
		entityManager.close();
		return count;
	}

	@Override
	public void deleteById(long id) throws FailedDAOOperationException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		new JPADeleteClause(entityManager, qComputer).where(qComputer.id.eq(id)).execute();	
		entityTransaction.commit();
		entityManager.close();
	}

	@Override
	public void create(Computer computer) throws FailedDAOOperationException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();

		new JPAUpdateClause(entityManager, qComputer).set(qComputer.introduced, computer.getIntroduced())
		.set(qComputer.discontinued, computer.getDiscontinued())
		.set(qComputer.company, computer.getCompany())
		.execute();

		entityTransaction.commit();
		entityManager.close();
	}

	@Override
	@Transactional
	public void update(Computer computer) throws FailedDAOOperationException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();

		entityTransaction.begin();
		
		new JPAUpdateClause(entityManager, qComputer).where(qComputer.id.eq(computer.getId()))
				.set(qComputer.name, computer.getName())
				.set(qComputer.introduced, computer.getIntroduced())
				.set(qComputer.discontinued, computer.getDiscontinued())
				.set(qComputer.company, computer.getCompany())
				.execute();
		entityTransaction.commit();
		entityManager.close();
	}

}
