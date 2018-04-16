package com.excilys.cdb.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

}
