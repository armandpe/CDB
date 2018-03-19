package com.excilys.cdb.web.servlet.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.excilys.cdb.constant.Servlet;

public class LinkTag extends SimpleTagSupport {

	private long limit;
	private String order;
	private String orderBy;
	private long page;
	private String search;
	private String variableName;

	public LinkTag() {
		this(Servlet.DEFAULT_PAGE_VALUE, Servlet.DEFAULT_ORDER_BY, 
				Servlet.DEFAULT_SEARCH, Servlet.DEFAULT_LIMIT_VALUE, 
				Servlet.DEFAULT_ORDER);
	}

	public LinkTag(long page, String orderBy, String search, long limit, String order) {
		super();
		this.page = page;
		this.orderBy = orderBy;
		this.search = search;
		this.limit = limit;
		this.order = order; 
	}

	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();

		String setVar = "<c:set var=";
		setVar += variableName;
		setVar += " value=";
		setVar += getHref();
		setVar += "/>";

		out.print(setVar);
	}

	public String getHref() {
		StringBuilder href = new StringBuilder();

		href.append("href=dashboard?");

		href.append("orderby=");
		href.append(orderBy);

		href.append("&order=");
		href.append(order);

		href.append("&limit=");
		href.append(limit);

		href.append("&search=");
		href.append(search);

		href.append("&page=");
		href.append(page);

		return href.toString();
	}

	public long getLimit() {
		return limit;
	}

	public String getOrder() {
		return order;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public long getPage() {
		return page;
	}

	public String getSearch() {
		return search;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setPage(long page) {
		this.page = page;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

}
