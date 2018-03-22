package com.excilys.cdb.web.controller;

import java.util.ArrayList;
import java.util.List;

public class PageData<T> {
	
	private List<T> dataList = new ArrayList<>();
	private long count;
	private long currentPage;
	private long maxPage;
	private String search;
	private String orderby;
	private long limit;
	private String order;
	
	public List<T> getDataList() {
		return dataList;
	}
	
	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
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
