package main.java.com.excilys.cdb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.com.excilys.cdb.dto.ComputerDTO;
import main.java.com.excilys.cdb.dto.ComputerMapper;
import main.java.com.excilys.cdb.model.Computer;
import main.java.com.excilys.cdb.service.ComputerService;
import main.java.com.excilys.cdb.service.PageManager;
import main.java.com.excilys.cdb.servlet.tag.PageData;

@SuppressWarnings("serial")
@WebServlet("/dashboard")
public class Dashboard extends HttpServlet {

	protected ComputerService computerService;
	protected long limit = 10;
	protected Logger logger = LogManager.getLogger(this.getClass());
	protected PageManager pageManager = null;
	protected long currentPage = 1;
	protected PageData<ComputerDTO> pageData = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long count;

		String limitString = request.getParameter("limit");
		limit = limitString == null ? limit : Long.parseLong(limitString);
		
		String pageString = request.getParameter("page");
		currentPage = pageString == null ? limit : Long.parseLong(pageString);
		
		if (pageManager == null) {
			computerService = ComputerService.getInstance();
			count = computerService.getCount();
			pageManager = new PageManager(limit, count, (x, y) -> computerService.getAll(x, y));
			pageData = new PageData<>();
		} else {
			pageData.getDataList().clear();
			count = computerService.getCount();
			pageManager.setLimit(limit);
		}

		pageManager.gotTo(currentPage);
		pageManager.getPageData().forEach(computer -> pageData.getDataList().add(ComputerMapper.toDTO((Computer) computer)));
		pageData.setCount(count);
		pageData.setCurrentPage(pageManager.getPage());
		pageData.setMaxPage(pageManager.getMaxPage());
		
		request.setAttribute("pageData", pageData);

		this.getServletContext().getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
	}
}
