package main.java.com.excilys.cdb.servlet;

import static main.java.com.excilys.cdb.constant.Servlet.PATH_403;
import static main.java.com.excilys.cdb.constant.Servlet.PATH_DASHBOARD;
import static main.java.com.excilys.cdb.constant.Servlet.NAME_DASHBOARD;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import main.java.com.excilys.cdb.dao.FailedDAOOperationException;
import main.java.com.excilys.cdb.dto.ComputerDTO;
import main.java.com.excilys.cdb.dto.ComputerMapper;
import main.java.com.excilys.cdb.model.Computer;
import main.java.com.excilys.cdb.service.ComputerService;
import main.java.com.excilys.cdb.service.PageManager;
import main.java.com.excilys.cdb.validator.InputValidator;
import main.java.com.excilys.cdb.validator.InvalidInputException;

@SuppressWarnings("serial")
@WebServlet("/" + NAME_DASHBOARD)
public class Dashboard extends HttpServlet {

	protected ComputerService computerService;
	protected long limit = 10;
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected PageManager<Computer> pageManager = null;
	protected long currentPage = 1;
	protected PageData<ComputerDTO> pageData = null;
	protected List<String> errors = new ArrayList<>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long count;

		String limitString = request.getParameter("limit");
		List<Function<Long, Boolean>> limitTests = new ArrayList<>();
		limitTests.add(limit -> (limit > 0));
		try {
			InputValidator.isCorrectString(limitString, toParse -> Long.parseLong(toParse), true, false, limitTests);
		} catch (InvalidInputException | NumberFormatException e) {
			logger.info(e.getClass().getName() + " : Incorrect limit value " + e.getMessage());
			this.getServletContext().getRequestDispatcher(PATH_403).forward(request, response);	
			return;
		}

		limit = limitString == null ? limit : Long.parseLong(limitString);


		String pageString = request.getParameter("page");
		List<Function<Long, Boolean>> pageTests = new ArrayList<>();
		try {
			InputValidator.isCorrectString(pageString, toParse -> Long.parseLong(toParse), true, false, pageTests);
		} catch (InvalidInputException | NumberFormatException e) {
			logger.info(e.getClass().getName() + " : Incorrect page value " + e.getMessage());
			this.getServletContext().getRequestDispatcher(PATH_403).forward(request, response);	
			return;
		}
		currentPage = pageString == null ? currentPage : Long.parseLong(pageString);

		try {

			if (pageManager == null) {
				computerService = ComputerService.getInstance();
				count = computerService.getCount();
				pageManager = new PageManager<Computer>(limit, count, (Long x, Long y) -> computerService.getAll(x, y));
				pageData = new PageData<>();
			} else {
				pageData.getDataList().clear();
				count = computerService.getCount();
				pageManager.setMax(count);
				pageManager.setLimit(limit);
			}

			pageManager.gotTo(currentPage);
			
			for (Computer computer : pageManager.getPageData()) {
				pageData.getDataList().add(ComputerMapper.toDTO((Computer) computer));
			}
			
			pageData.setCount(count);
			pageData.setCurrentPage(pageManager.getPage());
			pageData.setMaxPage(pageManager.getMaxPage());

			request.setAttribute("pageData", pageData);
			errors.clear();
			request.setAttribute("errors", new Gson().toJson(errors));

			this.getServletContext().getRequestDispatcher(PATH_DASHBOARD).forward(request, response);

		} catch (FailedDAOOperationException e) {
			logger.error(e.getMessage());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String selection = (String) request.getParameter("selection");
		errors.clear();
		
		if (selection != null) {
			String[] toDeleteList = selection.split(",");

			for (String toDelete : toDeleteList) {
				long id = Long.parseLong(toDelete);
				try {
					computerService.deleteComputer(id);
				} catch (FailedDAOOperationException e) {
					logger.info(e.getMessage());
					errors.clear();
					errors.add(e.getMessage());
					request.setAttribute("errors", new Gson().toJson(errors));
					this.getServletContext().getRequestDispatcher(PATH_DASHBOARD).forward(request, response);
				}
			}
		}
		request.setAttribute("errors", new Gson().toJson(errors));
		request.getRequestDispatcher(PATH_DASHBOARD).forward(request, response);
	}
}
