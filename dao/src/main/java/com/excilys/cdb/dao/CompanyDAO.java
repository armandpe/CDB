package com.excilys.cdb.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.QCompany;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;

@Repository
public class CompanyDAO implements ICompanyDAO {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private QCompany qCompany = QCompany.company;

    @PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Optional<Company> getById(long id) throws FailedDAOOperationException {
		Optional<Company> result = Optional.ofNullable(new JPAQuery<Void>(entityManager).select(qCompany).from(qCompany).where(qCompany.id.eq(id)).fetchOne());
		entityManager.close();
		return result;
	}

	@Override
	public long getCount() throws FailedDAOOperationException {
		long result = new JPAQuery<Void>(entityManager).select(qCompany).from(qCompany).fetchCount();
		entityManager.close();
		return result;
	}

	@Override
	public List<Company> getAll() throws FailedDAOOperationException {
		List<Company> result = new JPAQuery<Void>(entityManager).select(qCompany).from(qCompany).fetch();
		entityManager.close();
		return result;
	}

	@Override
	public void delete(long id) throws FailedDAOOperationException {
		new JPADeleteClause(entityManager, qCompany).where(qCompany.id.eq(id)).execute();	
	}
	
	@Override
	@Transactional
	public void create(Company company) throws FailedDAOOperationException {
		entityManager.persist(company);
	}
	
	@Override
	@Transactional
	public void update(Company company) throws FailedDAOOperationException {
		new JPAUpdateClause(entityManager, qCompany).where(qCompany.id.eq(company.getId()))
		.set(qCompany.name, company.getName())
		.execute();
	}
}
