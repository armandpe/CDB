package com.excilys.cdb.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaginationTag extends SimpleTagSupport {

	private long currentPage;
	private long maxPage;
	private String orderBy;
	private String search;
	private long limit;
	private String order;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	private long span = 7; //Should be odd

	public long getCurrentPage() {
		return currentPage;
	}

	public long getMaxPage() {
		return maxPage;
	}

	public void setCurrentPage(long currentPage) {
		this.currentPage = currentPage;
	}

	public void setMaxPage(long nbPage) {
		this.maxPage = nbPage;
	}

	public void doTag() throws IOException {

		JspWriter out = getJspContext().getOut();
		StringBuilder pagination = new StringBuilder();

		long normalStart = (long) (currentPage - Math.floor(span / 2));
		long normalEnd = (long) (currentPage + Math.floor(span / 2));

		long start = Math.max(1, normalStart);
		long end = Math.min(maxPage, normalEnd);

		if (start > normalStart) {
			end = Math.min(maxPage, normalEnd + (start - normalStart));
		}

		if (end < normalEnd) {
			start = Math.max(1, normalStart - (normalEnd - end));
		}

		if (currentPage > 1) {
			pagination.append(getCode(1, "<<", false));
			pagination.append(getCode((currentPage - 1), "<", false));
		}		

		for (long i = start; i <= end; ++i) {
			if (i == currentPage) {
				pagination.append(getCode(i, "-", true));
			} else {
				pagination.append(getCode(i, "" + i, false));
			}
		}

		if (currentPage < (maxPage)) {
			pagination.append(getCode(currentPage + 1, ">", false));
			pagination.append(getCode(maxPage, ">>", false));
		}

		out.print(pagination.toString());
	}

	private String getCode(long page, String toPrint, boolean disabled) {
		StringBuilder stringBuilder = new StringBuilder("<li><a ");
		if (!disabled) {
			LinkTag linkTag = new LinkTag(page, orderBy, search, limit, order);
			stringBuilder.append("href=");
			stringBuilder.append(linkTag.getHref());
		}
		stringBuilder.append("> ");
		stringBuilder.append(toPrint);
		stringBuilder.append(" </a><li>\n");
		
		return stringBuilder.toString();
	}

}
