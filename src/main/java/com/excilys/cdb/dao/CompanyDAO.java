package com.excilys.cdb.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.QCompany;
import com.querydsl.jpa.impl.JPAQuery;

@Repository
public class CompanyDAO implements ICompanyDAO {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private QCompany qCompany = QCompany.company;

	private EntityManagerFactory entityManagerFactory;
	
	public CompanyDAO(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public Optional<Company> getById(long id) throws FailedDAOOperationException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Optional<Company> result = Optional.ofNullable(new JPAQuery<Void>(entityManager).select(qCompany).from(qCompany).where(qCompany.id.eq(id)).fetchOne());
		entityManager.close();
		return result;
	}

	@Override
	public long getCount() throws FailedDAOOperationException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		long result = new JPAQuery<Void>(entityManager).select(qCompany).from(qCompany).fetchCount();
		entityManager.close();
		return result;
	}

	@Override
	public List<Company> getAll() throws FailedDAOOperationException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List<Company> result = new JPAQuery<Void>(entityManager).select(qCompany).from(qCompany).fetch();
		entityManager.close();
		return result;
	}

}
