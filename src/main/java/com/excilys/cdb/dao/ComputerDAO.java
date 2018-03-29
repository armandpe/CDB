package com.excilys.cdb.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.QComputer;
import com.excilys.cdb.pagemanager.PageManagerLimit;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;

@Repository
public class ComputerDAO implements IComputerDAO {

	private final Logger logger = LoggerFactory.getLogger(PageManagerLimit.class);

	private EntityManager entityManager;

	private QComputer qComputer = QComputer.computer;

	private ComputerDAO(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Computer> getAll(long offset, long limit, String search, String orderByVar, boolean asc) {
		return new JPAQuery<Void>(entityManager).select(qComputer).from(qComputer).fetch();
	}

	@Override
	public Optional<Computer> getById(long id) throws FailedDAOOperationException {
		return Optional.ofNullable(new JPAQuery<Void>(entityManager).select(qComputer).from(qComputer).where(qComputer.id.eq(id)).fetchOne());
	}

	@Override
	public long getCount(String search) throws FailedDAOOperationException {
		return new JPAQuery<Void>(entityManager).select(qComputer).from(qComputer).fetchCount();
	}

	@Override
	public void deleteById(long id) throws FailedDAOOperationException {
		new JPADeleteClause(entityManager, qComputer).where(qComputer.id.eq(id)).execute();	
	}

	@Override
	public void create(Computer computer) throws FailedDAOOperationException {
		
		new JPAUpdateClause(entityManager, qComputer).set(qComputer.introduced, computer.getIntroduced())
		.set(qComputer.discontinued, computer.getDiscontinued())
		.set(qComputer.company, computer.getCompany())
		.execute();

	}

	@Override
	@org.springframework.transaction.annotation.Transactional
	public void update(Computer computer) throws FailedDAOOperationException {
//		try {
//			userTransaction.begin();
//			entityManager.joinTransaction();
//			EntityTransaction entityTransaction = entityManager.getTransaction();
			
//			entityTransaction.begin();
			entityManager.joinTransaction();
			
			JPAUpdateClause clause = new JPAUpdateClause(entityManager, qComputer).where(qComputer.id.eq(computer.getId()))
					.set(qComputer.name, computer.getName())
					.set(qComputer.introduced, computer.getIntroduced())
					.set(qComputer.discontinued, computer.getDiscontinued())
					.set(qComputer.company, computer.getCompany());
			logger.error(clause.toString());
			
			clause.execute();
			
//			entityTransaction.commit();
//			
//			entityTransaction.notifyAll();
			
//			userTransaction.commit();
//		} catch (SecurityException | IllegalStateException | RollbackException | HeuristicMixedException
//				| HeuristicRollbackException | SystemException | NotSupportedException e) {
//			logger.error(Main.getErrorMessage("Update error - user transaction", e.getMessage()));
//			throw new FailedDAOOperationException();
//		}
	}

}
