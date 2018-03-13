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

import main.java.com.excilys.cdb.dao.FailedDAOOperationException;
import main.java.com.excilys.cdb.dto.ComputerDTO;
import main.java.com.excilys.cdb.dto.ComputerMapper;
import main.java.com.excilys.cdb.model.Computer;
import main.java.com.excilys.cdb.pagemanager.PageManagerComplete;
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

		String limitString = request.getParameter("limit");
		String searchString = request.getParameter("search");
		String pageString = request.getParameter("page");

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

		try {
			pageData.getDataList().clear();
			
			if (limitString != null) {
				pageManager.setLimit(Long.parseLong(limitString));
			}
			if (searchString != null) {
				pageManager.setToSearch(searchString.equals("") ? null : searchString);				
			}
			
			pageManager.gotTo(pageString == null ? pageManager.getPage() : Long.parseLong(pageString));
			pageManager.getPageData().stream().map(ComputerMapper::toDTO).forEach(pageData.getDataList()::add);

			pageData.setCurrentPage(pageManager.getPage());
			pageData.setMaxPage(pageManager.getMaxPage());
			pageData.setCount(pageManager.getMax());
			
			request.setAttribute("pageData", pageData);
			request.setAttribute("errors", new Gson().toJson(errors));
			request.setAttribute("search", pageManager.getToSearch());
			errors.clear();
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
