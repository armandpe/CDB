package main.java.com.excilys.cdb.servlet;

import static main.java.com.excilys.cdb.constant.Servlet.PATH_ADD;
import static main.java.com.excilys.cdb.constant.Servlet.NAME_DASHBOARD;
import static main.java.com.excilys.cdb.constant.Servlet.NAME_ADD;



import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import main.java.com.excilys.cdb.service.ComputerService;

@SuppressWarnings("serial")
@WebServlet("/" + NAME_ADD)
public class AddComputer extends HttpServlet  {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	ComputerFormManager computerFormManager = new ComputerFormManager();

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

		List<String> errors = computerFormManager.processInput(computerName, introduced, discontinued, companyId, computer -> ComputerService.getInstance().create(computer));
		request.setAttribute("errors", new Gson().toJson(errors));

		if (errors.size() > 0) {
			computerFormManager.setRequestCompanies(request);
			this.getServletContext().getRequestDispatcher(PATH_ADD).forward(request, response);
		} else {
			response.sendRedirect(NAME_DASHBOARD);
		}
	}


}
