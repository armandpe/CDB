package main.java.com.excilys.cdb.servlet;

import static main.java.com.excilys.cdb.constant.Servlet.NAME_DASHBOARD;
import static main.java.com.excilys.cdb.constant.Servlet.PATH_DASHBOARD;
import static main.java.com.excilys.cdb.constant.Servlet.PATH_403;

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

import main.java.com.excilys.cdb.constant.Servlet;
import main.java.com.excilys.cdb.dao.FailedDAOOperationException;
import main.java.com.excilys.cdb.dto.ComputerDTO;
import main.java.com.excilys.cdb.dto.ComputerMapper;
import main.java.com.excilys.cdb.model.Computer;
import main.java.com.excilys.cdb.pagemanager.PageManagerComplete;
import main.java.com.excilys.cdb.service.ComputerOrderBy;
import main.java.com.excilys.cdb.service.ComputerService;
import main.java.com.excilys.cdb.validator.InputValidator;
import main.java.com.excilys.cdb.validator.InvalidInputException;

@SuppressWarnings("serial")
@WebServlet("/" + NAME_DASHBOARD)
public class Dashboard extends HttpServlet {

	protected ComputerService computerService = ComputerService.getInstance();
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected PageManagerComplete<Computer> pageManager = new PageManagerComplete<Computer>(computerService::getCount, computerService::getAll);
	protected PageData<ComputerDTO> pageData = new PageData<>();
	protected List<String> errors = new ArrayList<>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String limitString = request.getParameter(Servlet.LIMIT);
		String searchString = request.getParameter(Servlet.SEARCH);
		String pageString = request.getParameter(Servlet.PAGE);
		String orderByString = request.getParameter(Servlet.ORDER_BY);

		List<Function<Long, Boolean>> limitTests = new ArrayList<>();
		limitTests.add(limit -> (limit > 0));
		try {
			InputValidator.isCorrectString(limitString, toParse -> Long.parseLong(toParse), true, false, limitTests);
		} catch (InvalidInputException | NumberFormatException e) {
			logger.info(e.getClass().getName() + " : Incorrect limit value " + e.getMessage());
			this.getServletContext().getRequestDispatcher(PATH_403).forward(request, response);	
			return;
		}

		List<Function<Long, Boolean>> pageTests = new ArrayList<>();
		try {
			InputValidator.isCorrectString(pageString, toParse -> Long.parseLong(toParse), true, false, pageTests);
		} catch (InvalidInputException | NumberFormatException e) {
			logger.info(e.getClass().getName() + " : Incorrect page value " + e.getMessage());
			this.getServletContext().getRequestDispatcher(PATH_403).forward(request, response);	
			return;
		}	

		List<Function<String, Boolean>> orderByTest = new ArrayList<>();
		orderByTest.add(s -> s.equals(Servlet.ORDER_BY_NAME) || 
				s.equals(Servlet.ORDER_BY_COMPANY_NAME) || 
				s.equals(Servlet.ORDER_BY_DISCONTINUED) || 
				s.equals(Servlet.ORDER_BY_INTRODUCED));
		try {
			InputValidator.isCorrectString(orderByString, x -> x, true, false, orderByTest);
		} catch (InvalidInputException | NumberFormatException e) {
			logger.info(e.getClass().getName() + " : Incorrect orderby value " + orderByString + e.getMessage());
			this.getServletContext().getRequestDispatcher(PATH_403).forward(request, response);	
			return;
		}	

		try {
			pageData.getDataList().clear();

			if (limitString != null) {
				pageManager.setLimit(Long.parseLong(limitString));
			}
			if (searchString != null) {
				pageManager.setToSearch(searchString.equals("") ? null : searchString);				
			}
			if (orderByString != null) {
				switch (orderByString) {
				case Servlet.ORDER_BY_NAME :
					pageManager.setOrderBy(ComputerOrderBy.NAME);
					break;
				case Servlet.ORDER_BY_COMPANY_NAME :
					pageManager.setOrderBy(ComputerOrderBy.COMPANY_NAME);
					break;
				case Servlet.ORDER_BY_DISCONTINUED :
					pageManager.setOrderBy(ComputerOrderBy.DISCONTINUED);
					break;
				case Servlet.ORDER_BY_INTRODUCED :
					pageManager.setOrderBy(ComputerOrderBy.INTRODUCED);
					break;
				}
			}

			pageManager.gotTo(pageString == null ? pageManager.getPage() : Long.parseLong(pageString));
			pageManager.getPageData().stream().map(ComputerMapper::toDTO).forEach(pageData.getDataList()::add);

			pageData.setCurrentPage(pageManager.getPage());
			pageData.setMaxPage(pageManager.getMaxPage());
			pageData.setCount(pageManager.getMax());

			request.setAttribute(Servlet.PAGE_DATA, pageData);
			request.setAttribute(Servlet.ERRORS, new Gson().toJson(errors));
			request.setAttribute(Servlet.SEARCH, pageManager.getToSearch());
			errors.clear();
			this.getServletContext().getRequestDispatcher(PATH_DASHBOARD).forward(request, response);

		} catch (FailedDAOOperationException e) {
			logger.error(e.getMessage());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String selection = (String) request.getParameter(Servlet.DELETE_SELECTION);
		errors.clear();

		if (selection != null) {
			String[] toDeleteList = selection.split(",");

			for (String toDelete : toDeleteList) {
				long id = Long.parseLong(toDelete);
				try {
					computerService.delete(id);
				} catch (FailedDAOOperationException e) {
					logger.info(e.getMessage());
					errors.clear();
					errors.add(e.getMessage());
					doGet(request, response);
				}
			}
		}
		response.sendRedirect(NAME_DASHBOARD);
	}
}
