package main.java.com.excilys.cdb.servlet.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class PaginationTag extends SimpleTagSupport {

	private static long currentPage;

	private static long nbPage;

	private static long span = 5; //Should be odd

	public static long getCurrentPage() {
		return currentPage;
	}

	public static long getNbPage() {
		return nbPage;
	}

	public static void setCurrentPage(long currentPage) {
		PaginationTag.currentPage = currentPage;
	}

	public static void setNbPage(long nbPage) {
		PaginationTag.nbPage = nbPage;
	}

	public void doTag() throws JspException, IOException {

		JspWriter out = getJspContext().getOut();
		StringBuilder pagination = new StringBuilder();

		long normalStart = (long) (currentPage - Math.floor(span / 2));
		long normalEnd = (long) (currentPage + Math.floor(span / 2) + 1);

		long start = Math.max(1, normalStart);
		long end = Math.min(nbPage, normalEnd);

		if (start > normalStart) {
			end = Math.min(nbPage, normalEnd + (start - normalStart));
		}

		if (end < normalEnd) {
			start = Math.max(1, normalStart - (normalEnd - end));
		}

		if (currentPage > 1) {
			pagination.append(getCode(1, "<<", false));
			pagination.append(getCode((currentPage - 1), "<", false));
		}		

		for (long i = start; i < end; ++i) {
			if (i == currentPage) {
				pagination.append(getCode(i, "-", true));
			} else {
				pagination.append(getCode(i, "" + i, false));
			}
		}

		if (currentPage < (nbPage - 1)) {
			pagination.append(getCode((long) (currentPage + 1), ">", false));
			pagination.append(getCode(nbPage, ">>", false));
		}

		out.print(pagination.toString());
	}

	private String getCode(long page, String toPrint, boolean disabled) {
		return "<li><a " + (disabled ? "" : "href=dashboard?page=" + page) + "> " + toPrint + " </a><li>\n";
	}

}
