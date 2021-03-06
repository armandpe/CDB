package com.excilys.cdb.service.pagemanager;

import java.util.ArrayList;

import com.excilys.cdb.model.Computer;
import com.excilys.cdb.service.IComputerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.dao.FailedDAOOperationException;

public abstract class PageManagerAbstract {
	
	protected long limit = 10;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected long max;
	protected long maxPage;
	protected long offset = 0;
	protected ArrayList<Computer> pageData = new ArrayList<>();
	protected IComputerService service;
	
	public void first() {
		offset = 0;
	}
	
	public long getLimit() {
		return limit;
	}
	
	public long getMax() {
		return max;
	}
	
	public long getMaxPage() {
		return maxPage;
	}
	
	public long getOffset() {
		return offset;
	}
	
	public long getPage() {
		return (offset / limit) + 1;
	}

	public ArrayList<Computer> getPageData() throws FailedDAOOperationException {
		getItems();
		return pageData;
	}

	public void gotTo(long page) {
		page = Math.min(page, maxPage);
		page = Math.max(1, page);
		
		offset = (page - 1) * limit;
	}

	public boolean last() {
		offset = (maxPage - 1) * limit;
		return true;
	}

	public boolean next() {
		if (!(offset + limit > max - 1)) {
			offset += limit;
			return true;
		}
		return false;
	}

	public boolean previous() {
		if (offset - limit < 0) {
			return false;
		}
		offset -= limit;
		return true;
	}
	
	public void setLimit(long limit) {
		this.limit = limit;
		setOffset(0);
		refreshMaxPage();
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	protected abstract boolean getItems() throws FailedDAOOperationException;

	protected void refreshMaxPage() {
		this.maxPage = (long) Math.ceil(((double) max) / (double) limit);
		gotTo(getPage());
	}

	protected void setMax() throws FailedDAOOperationException {
		this.max = service.getCount();
		refreshMaxPage();
	}
	
}
