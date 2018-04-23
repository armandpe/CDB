package com.excilys.cdb.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.QComputer;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;

@Repository
public class ComputerDAO implements IComputerDAO {

	@PersistenceContext
	private EntityManager entityManager;

	private QComputer qComputer = QComputer.computer;

	@Override
	public List<Computer> getAll(long offset, long limit, String search, ComputerOrderBy orderByVar, boolean asc) {

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
		return result;
	}

	@Override
	public Optional<Computer> getById(long id) throws FailedDAOOperationException {
		Optional<Computer> result = Optional.ofNullable(new JPAQuery<Void>(entityManager).select(qComputer).from(qComputer).where(qComputer.id.eq(id)).fetchOne());
		return result;
	}

	@Override
	public long getCount(String search) throws FailedDAOOperationException {
		JPAQuery<Computer> jpaQuery = new JPAQuery<Void>(entityManager).select(qComputer).from(qComputer);
		
		if (search != null) {
			jpaQuery = jpaQuery.where(qComputer.name.startsWith(search));
		}
				
		long count = jpaQuery.fetchCount();
		return count;
	}

	@Override
	@Transactional
	public void deleteById(long id) throws FailedDAOOperationException {
		new JPADeleteClause(entityManager, qComputer).where(qComputer.id.eq(id)).execute();	
	}

	@Override
	@Transactional
	public void create(Computer computer) throws FailedDAOOperationException {
		entityManager.persist(computer);
	}

	@Override
	@Transactional
	public void update(Computer computer) throws FailedDAOOperationException {

		new JPAUpdateClause(entityManager, qComputer).where(qComputer.id.eq(computer.getId()))
		.set(qComputer.name, computer.getName())
		.set(qComputer.introduced, computer.getIntroduced())
		.set(qComputer.discontinued, computer.getDiscontinued())
		.set(qComputer.company, computer.getCompany())
		.execute();
	}

	@Override
	public List<Computer> getAllByCompany(long companyId) {
		return new JPAQuery<Void>(entityManager).select(qComputer).from(qComputer).where(qComputer.id.eq(companyId)).fetch();
	}
}
