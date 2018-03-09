package main.java.com.excilys.cdb.pagemanager;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.cdb.ihm.UserChoice;

public abstract class PageManagerAbstract<T> {
	
	protected Logger logger = LoggerFactory.getLogger(PageManagerLimit.class);

	protected long limit;
	protected long max;
	protected long maxPage;
	protected long offset;
	protected ArrayList<T> pageData = new ArrayList<>();
	
	@UserChoice(name = "Get first page")
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

	public ArrayList<T> getPageData() {
		getItems();
		return pageData;
	}

	@UserChoice(name = "Get last page")
	public void last() {
		offset = (maxPage - 1) * limit;
	}

	@UserChoice(name = "Get next page")
	public boolean next() {
		if (!(offset + limit > max - 1)) {
			offset += limit;
			return true;
		}
		return false;
	}

	@UserChoice(name = "Get previous page")
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
	
	protected void refreshMaxPage() {
		this.maxPage = (long) Math.ceil(((double) max) / (double) limit);
	}

	public void setMax(long max) {
		this.max = max;
		refreshMaxPage();
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	protected abstract boolean getItems();

	public void gotTo(long page) {
		page = Math.min(page, maxPage);
		page = Math.max(1, page);
		
		offset = (page - 1) * limit;
	}
	
}
