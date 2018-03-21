package com.excilys.cdb.web.servlet;

import java.util.ArrayList;
import java.util.List;

public class PageData<T> {
	
	private List<T> dataList = new ArrayList<>();
	private long count;
	private long currentPage;
	private long maxPage;
	
	public List<T> getDataList() {
		return dataList;
	}
	
	public long getCount() {
		return count;
	}
	
	public long getCurrentPage() {
		return currentPage;
	}
	
	public long getMaxPage() {
		return maxPage;
	}

	public void setCount(long count) {
		this.count = count;
	}
	
	public void setCurrentPage(long currentPage) {
		this.currentPage = currentPage;
	}
	
	public void setMaxPage(long maxPage) {
		this.maxPage = maxPage;
	}
	
}
