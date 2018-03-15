package com.excilys.cdb.pagemanager;

import com.excilys.cdb.Main;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.ModelClass;
import com.excilys.cdb.service.Service;

public class PageManagerLimit<T extends ModelClass> extends PageManagerAbstract<T> {
	
	public PageManagerLimit(Service<T, ?> service) {
		this.service = service;
	}
	
	@SuppressWarnings("unchecked")
	protected boolean getItems() throws FailedDAOOperationException {
		setMax();
		pageData.clear();
		try {
			service.getAll(offset, limit).forEach(x -> pageData.add((T) x));
		} catch (FailedDAOOperationException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		return true;
	}

}
