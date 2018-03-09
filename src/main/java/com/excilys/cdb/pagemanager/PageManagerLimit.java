package main.java.com.excilys.cdb.pagemanager;

import java.util.List;

import main.java.com.excilys.cdb.Main;
import main.java.com.excilys.cdb.dao.FailedDAOOperationException;
import main.java.com.excilys.cdb.utils.BiFunctionException;

public class PageManagerLimit<T> extends PageManagerAbstract<T> {
	
	private BiFunctionException<Long, Long, List<T>, FailedDAOOperationException> getList;

	public PageManagerLimit(long limit, long max, BiFunctionException<Long, Long, List<T>, FailedDAOOperationException> getList) {
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
			(getList.apply(offset, limit)).forEach(x -> pageData.add((T) x));
		} catch (FailedDAOOperationException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		return true;
	}

}
