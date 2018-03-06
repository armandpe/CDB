package main.java.com.excilys.cdb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import main.java.com.excilys.cdb.ihm.UserChoice;
import main.java.com.excilys.cdb.model.ModelClass;

public class PageManager {
	
	private BiFunction<Long, Long, List<?>> getList;
	private long limit;
	private long max;
	private long maxPage;
	private long offset;
	private ArrayList<ModelClass> pageData = new ArrayList<>();
	
	public PageManager(long limit, long max, BiFunction<Long, Long, List<?>> getList) {
		this.offset = 0;
		this.limit = limit;
		this.max = max;
		refreshMaxPage();
		this.getList = getList;
		this.getItems();
	}
	
	@UserChoice(name = "Get first page")
	public boolean first() {
		offset = 0;
		return getItems();
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

	public ArrayList<ModelClass> getPageData() {
		return pageData;
	}

	@UserChoice(name = "Get last page")
	public boolean last() {
		offset = (maxPage - 1) * limit;
		return getItems();
	}

	@UserChoice(name = "Get next page")
	public boolean next() {
		if (offset + limit > max - 1) {
			return false;
		}
		offset += limit;
		return getItems();
	}

	@UserChoice(name = "Get previous page")
	public boolean previous() {
		if (offset - limit < 0) {
			return false;
		}
		
		offset -= limit;
		return getItems();
	}

	public void setLimit(long limit) {
		this.limit = limit;
		setOffset(0);
		refreshMaxPage();
		getItems();
	}
	
	private void refreshMaxPage() {
		this.maxPage = (long) Math.ceil(((double) max) / (double) limit);
	}

	public void setMax(long max) {
		this.max = max;
		refreshMaxPage();
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	private boolean getItems() {
		pageData.clear();
		(getList.apply(offset, limit)).forEach(x -> pageData.add((ModelClass) x));
		return true;
	}

	public boolean gotTo(long page) {
		page = Math.min(page, maxPage);
		page = Math.max(1, page);
		
		offset = (page - 1) * limit;
		return getItems();
	}
	
}
