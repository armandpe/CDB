package com.excilys.cdb.pagemanager;

import java.util.List;

import com.excilys.cdb.Main;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.utils.BiFunctionException;
import com.excilys.cdb.utils.FunctionException;

public class PageManagerLimit<T> extends PageManagerAbstract<T> {
	
	private BiFunctionException<Long, Long, List<T>, FailedDAOOperationException> getList;

	public PageManagerLimit(FunctionException<String, Long, FailedDAOOperationException> getMaxFunction, BiFunctionException<Long, Long, List<T>, FailedDAOOperationException> getList) {
		this.getMaxFunction = getMaxFunction;
		this.getList = getList;
	}
	
	protected boolean getItems() throws FailedDAOOperationException {
		setMax();
		pageData.clear();
		try {
			(getList.apply(offset, limit)).forEach(x -> pageData.add((T) x));
		} catch (FailedDAOOperationException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
		return true;
	}

}
