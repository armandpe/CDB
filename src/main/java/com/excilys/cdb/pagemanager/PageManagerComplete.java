package com.excilys.cdb.pagemanager;

import com.excilys.cdb.Main;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.ModelClass;
import com.excilys.cdb.service.ComputerOrderBy;
import com.excilys.cdb.service.Service;

public class PageManagerComplete<T extends ModelClass> extends PageManagerAbstract<T> {
	
	protected boolean asc = true;
	protected ComputerOrderBy orderBy = ComputerOrderBy.NAME;
	protected String toSearch = null;
	
	public PageManagerComplete(Service<T, ?> service) {
		this.service = service;
	}

	public ComputerOrderBy getOrderBy() {
		return orderBy;
	}

	public String getToSearch() {
		return toSearch;
	}

	public boolean isAscd() {
		return asc;
	}

	public void setAscd(boolean ascd) {
		this.asc = ascd;
	}

	public void setOrderBy(ComputerOrderBy orderBy) {
		if (orderBy == this.orderBy) {
			asc = !asc;
		}
		
		this.orderBy = orderBy;
	}

	public void setToSearch(String toSearch) {
		this.toSearch = toSearch;
	}
	
	protected boolean getItems() throws FailedDAOOperationException {
		setMax();
		pageData.clear();
		try {
			service.getAll(offset, limit, toSearch, orderBy, asc).forEach(x -> pageData.add((T) x));
		} catch (FailedDAOOperationException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		return true;
	}
	
	@Override
	protected void setMax() throws FailedDAOOperationException {
		this.max = service.getCount(toSearch);
		refreshMaxPage();
	}

}
