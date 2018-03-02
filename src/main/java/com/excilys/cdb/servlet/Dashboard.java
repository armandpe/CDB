package main.java.com.excilys.cdb.servlet;

import java.io.IOException;
import java.util.ArrayList;

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

@SuppressWarnings("serial")
@WebServlet("/dashboard")
public class Dashboard extends HttpServlet {

	Logger logger = LogManager.getLogger(this.getClass());
	PageManager pageManager = null;
	ComputerService computerService;
	ArrayList<ComputerDTO> dtoList;
	long limit;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long count;
		
		String limit = request.getParameter("limit");
		
		if (pageManager == null) {
			computerService = ComputerService.getInstance();
			count = computerService.getCount();
			dtoList = new ArrayList<>();
			pageManager = new PageManager(limit == null ? 10 : Long.valueOf(limit), count, (x, y) -> computerService.getAll(x, y));
		} else {
			dtoList.clear();
			count = computerService.getCount();
		}
		
		pageManager.getPageData().forEach(computer -> dtoList.add(ComputerMapper.toDTO((Computer) computer)));
		
		request.setAttribute("count", count);
		request.setAttribute("computerList", dtoList);

		this.getServletContext().getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
	}
}
