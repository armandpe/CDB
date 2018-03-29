package com.excilys.cdb.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.QCompany;
import com.excilys.cdb.pagemanager.PageManagerLimit;
import com.querydsl.jpa.impl.JPAQuery;

@Repository
public class CompanyDAO implements ICompanyDAO {
	
	protected final Logger logger = LoggerFactory.getLogger(PageManagerLimit.class);
	
	private final ComputerDAO computerDAO;
	
	private QCompany qCompany = QCompany.company;

	private EntityManager entityManager;
	
	private CompanyDAO(ComputerDAO computerDAO, EntityManager entityManager) {
		this.computerDAO = computerDAO;
		this.entityManager = entityManager;
	}

	@Override
	public List<Company> getAll(long offset, long limit, String search, String orderByVar, boolean asc) {
		return new JPAQuery<Void>(entityManager).select(qCompany).from(qCompany).fetch();
	}

	@Override
	public Optional<Company> getById(long id) throws FailedDAOOperationException {
		return Optional.ofNullable(new JPAQuery<Void>(entityManager).select(qCompany).from(qCompany).where(qCompany.id.eq(id)).fetchOne());
	}

	@Override
	public long getCount(String search) throws FailedDAOOperationException {
		return new JPAQuery<Void>(entityManager).select(qCompany).from(qCompany).fetchCount();

	}

	@Override
	public void deleteById(long id) throws FailedDAOOperationException {
		throw new FailedDAOOperationException();
	}
	


}
