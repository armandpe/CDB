package com.excilys.cdb.pagemanager;

import com.excilys.cdb.Main;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.service.ComputerOrderBy;
import com.excilys.cdb.service.IComputerService;

public class PageManagerComplete extends PageManagerAbstract {

	protected boolean asc = true;
	protected ComputerOrderBy orderBy = ComputerOrderBy.NAME;
	protected String toSearch = null;

	public PageManagerComplete(IComputerService service) {
		this.service = service;

		try {
			setMax();
		} catch (FailedDAOOperationException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
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

	public void setOrderBy(ComputerOrderBy orderBy, boolean asc) {
		this.asc = asc;
		this.orderBy = orderBy;
	}

	public void setToSearch(String toSearch) {
		this.toSearch = toSearch;
	}

	@SuppressWarnings("unchecked")
	protected boolean getItems() throws FailedDAOOperationException {
		setMax();
		pageData.clear();
		try {
			pageData.addAll(service.getAll(offset, limit, toSearch, orderBy, asc));
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
