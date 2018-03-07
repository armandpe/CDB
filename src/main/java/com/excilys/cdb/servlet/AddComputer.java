package main.java.com.excilys.cdb.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.com.excilys.cdb.service.ComputerService;

@SuppressWarnings("serial")
@WebServlet("/addComputer")
public class AddComputer extends HttpServlet  {

	protected Logger logger = LogManager.getLogger(this.getClass());
	ComputerFormManager computerFormManager = new ComputerFormManager();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		

		List<String> errors = computerFormManager.setRequestCompanies(request);
		request.setAttribute("errors", errors);
		this.getServletContext().getRequestDispatcher("/WEB-INF/views/addComputer.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String computerName = request.getParameter("computerName");
		String introduced = request.getParameter("introduced");
		String discontinued = request.getParameter("discontinued");
		String companyId = request.getParameter("companyId");

		List<String> errors = computerFormManager.processInput(computerName, introduced, discontinued, companyId, computer -> ComputerService.getInstance().createComputer(computer));
		
		logger.error(errors);
		
		if (errors.size() > 0) {
			
			request.setAttribute("errors", errors);
			computerFormManager.setRequestCompanies(request);
			this.getServletContext().getRequestDispatcher("/WEB-INF/views/addComputer.jsp").forward(request, response);
		} else {
			response.sendRedirect("dashboard");		
		}
	}


}
