package main.java.com.excilys.cdb.pagemanager;

import java.util.List;

import main.java.com.excilys.cdb.Main;
import main.java.com.excilys.cdb.dao.FailedDAOOperationException;
import main.java.com.excilys.cdb.service.ComputerOrderBy;
import main.java.com.excilys.cdb.utils.FunctionException;
import main.java.com.excilys.cdb.utils.PentiFunctionException;

public class PageManagerComplete<T> extends PageManagerAbstract<T> {
	
	protected PentiFunctionException<Long, Long, String, ComputerOrderBy, Boolean, List<T>, FailedDAOOperationException> getList;
	protected String toSearch = null;
	protected ComputerOrderBy orderBy = ComputerOrderBy.NAME;
	protected boolean ascd = true;
	
	public ComputerOrderBy getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(ComputerOrderBy orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isAscd() {
		return ascd;
	}

	public void setAscd(boolean ascd) {
		this.ascd = ascd;
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
			(getList.apply(offset, limit, toSearch, orderBy, ascd)).forEach(x -> pageData.add((T) x));
		} catch (FailedDAOOperationException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		return true;
	}

}
