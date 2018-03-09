package main.java.com.excilys.cdb.pagemanager;

import java.util.List;

import main.java.com.excilys.cdb.Main;
import main.java.com.excilys.cdb.dao.FailedDAOOperationException;
import main.java.com.excilys.cdb.utils.TriFunctionException;

public class PageManagerSearchLimit<T> extends PageManagerAbstract<T> {
	
	protected TriFunctionException<Long, Long, String, List<T>, FailedDAOOperationException> getList;
	protected String toSearch = null;

	public String getToSearch() {
		return toSearch;
	}

	public void setToSearch(String toSearch) {
		this.toSearch = toSearch;
	}

	public PageManagerSearchLimit(long limit, long max, TriFunctionException<Long, Long, String, List<T>, FailedDAOOperationException> getList) {
		this.offset = 0;
		this.limit = limit;
		this.max = max;
		refreshMaxPage();
		this.getList = getList;
		this.getItems();
	}
	
	protected boolean getItems() {
		pageData.clear();
		try {
			(getList.apply(offset, limit, toSearch)).forEach(x -> pageData.add((T) x));
		} catch (FailedDAOOperationException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		return true;
	}

}
