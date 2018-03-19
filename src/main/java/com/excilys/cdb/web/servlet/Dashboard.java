package com.excilys.cdb.web.servlet;

import static com.excilys.cdb.constant.Servlet.NAME_DASHBOARD;
import static com.excilys.cdb.constant.Servlet.PATH_403;
import static com.excilys.cdb.constant.Servlet.PATH_DASHBOARD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.excilys.cdb.constant.Servlet;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.pagemanager.PageManagerComplete;
import com.excilys.cdb.service.ComputerOrderBy;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.validator.InputValidator;
import com.excilys.cdb.validator.InvalidInputException;
import com.excilys.cdb.web.dto.ComputerDTO;
import com.excilys.cdb.web.dto.ComputerMapper;
import com.google.gson.Gson;

@SuppressWarnings("serial")
@Controller
@WebServlet("/" + NAME_DASHBOARD)
public class Dashboard extends HttpServlet {

	@Autowired
	private ComputerService computerService;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext servletContext = config.getServletContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		AutowireCapableBeanFactory autowireCapableBeanFactory = webApplicationContext.getAutowireCapableBeanFactory();
		autowireCapableBeanFactory.autowireBean(this);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String limitString = request.getParameter(Servlet.LIMIT);
		String searchString = request.getParameter(Servlet.SEARCH);
		String pageString = request.getParameter(Servlet.PAGE);
		String orderByString = request.getParameter(Servlet.ORDER_BY);
		boolean orderByChanged = Servlet.ORDER_BY_ASC.equals(request.getParameter(Servlet.ORDER_BY_CHANGED));
		
		limitString = limitString != null ? limitString : Servlet.DEFAULT_LIMIT;
		searchString = searchString != null ? searchString : Servlet.DEFAULT_SEARCH;
		pageString = pageString != null ? pageString : Servlet.DEFAULT_PAGE;
		orderByString = orderByString != null ? orderByString : Servlet.DEFAULT_ORDER_BY;

		PageData<ComputerDTO> pageData = new PageData<>();
		PageManagerComplete<Computer> pageManager = new PageManagerComplete<>(computerService);

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
			
			long limit = Long.parseLong(limitString);
			long page = Long.parseLong(pageString);
			
			pageManager.setLimit(Long.parseLong(limitString));

			pageManager.setToSearch(searchString.equals("") ? null : searchString);	

			switch (orderByString) {
			case Servlet.ORDER_BY_NAME :
				pageManager.setOrderBy(ComputerOrderBy.NAME, orderByChanged);
				break;
			case Servlet.ORDER_BY_COMPANY_NAME :
				pageManager.setOrderBy(ComputerOrderBy.COMPANY_NAME, orderByChanged);
				break;
			case Servlet.ORDER_BY_DISCONTINUED :
				pageManager.setOrderBy(ComputerOrderBy.DISCONTINUED, orderByChanged);
				break;
			case Servlet.ORDER_BY_INTRODUCED :
				pageManager.setOrderBy(ComputerOrderBy.INTRODUCED, orderByChanged);
				break;
			}

			pageManager.gotTo(page);
			pageManager.getPageData().stream().map(ComputerMapper::toDTO).forEach(pageData.getDataList()::add);

			pageData.setCurrentPage(pageManager.getPage());
			pageData.setMaxPage(pageManager.getMaxPage());
			pageData.setCount(pageManager.getMax());

			request.setAttribute(Servlet.PAGE_DATA, pageData);
			request.setAttribute(Servlet.ERRORS, new Gson().toJson(new String[0]));
			request.setAttribute(Servlet.SEARCH, pageManager.getToSearch());
			request.setAttribute(Servlet.ORDER_BY, orderByString);
			request.setAttribute(Servlet.LIMIT, limit);
			request.setAttribute(Servlet.PAGE, page);
			
			this.getServletContext().getRequestDispatcher(PATH_DASHBOARD).forward(request, response);

		} catch (FailedDAOOperationException e) {
			logger.error(e.getMessage());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String selection = (String) request.getParameter(Servlet.DELETE_SELECTION);
		List<String> errors = new ArrayList<>();

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
					return;
				}
			}
		}
		response.sendRedirect(NAME_DASHBOARD);
	}
}
