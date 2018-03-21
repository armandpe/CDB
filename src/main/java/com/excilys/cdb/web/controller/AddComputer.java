package com.excilys.cdb.web.servlet;

import static com.excilys.cdb.constant.Servlet.NAME_ADD;
import static com.excilys.cdb.constant.Servlet.NAME_DASHBOARD;
import static com.excilys.cdb.constant.Servlet.PATH_ADD;

import java.io.IOException;
import java.util.List;

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

import com.excilys.cdb.service.ComputerService;
import com.google.gson.Gson;

@SuppressWarnings("serial")
@WebServlet("/" + NAME_ADD)
@Controller
public class AddComputer extends HttpServlet  {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ComputerFormManager computerFormManager;
	
	@Autowired
	ComputerService computerService;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext servletContext = config.getServletContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		AutowireCapableBeanFactory autowireCapableBeanFactory = webApplicationContext.getAutowireCapableBeanFactory();
		autowireCapableBeanFactory.autowireBean(this);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		

		List<String> errors = computerFormManager.setRequestCompanies(request);
		request.setAttribute("errors", new Gson().toJson(errors));
		this.getServletContext().getRequestDispatcher(PATH_ADD).forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String computerName = request.getParameter("computerName");
		String introduced = request.getParameter("introduced");
		String discontinued = request.getParameter("discontinued");
		String companyId = request.getParameter("companyId");

		List<String> errors = computerFormManager.processInput(computerName, introduced, discontinued, companyId, computer -> computerService.create(computer));
		request.setAttribute("errors", new Gson().toJson(errors));

		if (errors.size() > 0) {
			computerFormManager.setRequestCompanies(request);
			this.getServletContext().getRequestDispatcher(PATH_ADD).forward(request, response);
		} else {
			response.sendRedirect(NAME_DASHBOARD);
		}
	}


}