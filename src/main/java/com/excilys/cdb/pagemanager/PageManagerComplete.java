package com.excilys.cdb.pagemanager;

import java.util.List;

import com.excilys.cdb.Main;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.service.ComputerOrderBy;
import com.excilys.cdb.utils.FunctionException;
import com.excilys.cdb.utils.PentiFunctionException;

public class PageManagerComplete<T> extends PageManagerAbstract<T> {
	
	protected PentiFunctionException<Long, Long, String, ComputerOrderBy, Boolean, List<T>, FailedDAOOperationException> getList;
	protected String toSearch = null;
	protected ComputerOrderBy orderBy = ComputerOrderBy.NAME;
	protected boolean asc = true;
	
	public ComputerOrderBy getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(ComputerOrderBy orderBy) {
		if (orderBy == this.orderBy) {
			asc = !asc;
		}
		
		this.orderBy = orderBy;
	}

	public boolean isAscd() {
		return asc;
	}

	public void setAscd(boolean ascd) {
		this.asc = ascd;
	}

	public String getToSearch() {
		return toSearch;
	}

	public void setToSearch(String toSearch) {
		this.toSearch = toSearch;
	}

	public PageManagerComplete(FunctionException<String, Long, FailedDAOOperationException> getMaxFunction, PentiFunctionException<Long, Long, String, ComputerOrderBy, Boolean, List<T>, FailedDAOOperationException> getList) {
		this.getList = getList;
		this.getMaxFunction = getMaxFunction;
	}
	
	@Override
	protected void setMax() throws FailedDAOOperationException {
		this.max = getMaxFunction.apply(this.toSearch);	
		refreshMaxPage();
	}
	
	protected boolean getItems() throws FailedDAOOperationException {
		setMax();
		pageData.clear();
		try {
			(getList.apply(offset, limit, toSearch, orderBy, asc)).forEach(x -> pageData.add((T) x));
		} catch (FailedDAOOperationException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		return true;
	}

}
