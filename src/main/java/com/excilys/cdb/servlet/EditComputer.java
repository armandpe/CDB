package main.java.com.excilys.cdb.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

@SuppressWarnings("serial")
@WebServlet("/editComputer")
public class EditComputer extends HttpServlet {

	protected Logger logger = LogManager.getLogger(this.getClass());
	protected ComputerFormManager computerFormManager = new ComputerFormManager();
	protected ComputerService computerService = ComputerService.getInstance();
	protected ComputerDTO lastComputer = new ComputerDTO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idString = request.getParameter("id");
		long id = Long.parseLong(idString);
		Optional<Computer> gottenComputer = computerService.getById(id);

		if (!gottenComputer.isPresent()) {
			request.getRequestDispatcher("/WEB-INF/views/403.jsp").forward(request, response);
		} else {
			lastComputer = ComputerMapper.toDTO(gottenComputer.get());
			request.setAttribute("computer", lastComputer);

			List<String> errors = computerFormManager.setRequestCompanies(request);

			request.setAttribute("errors", errors);

			request.getRequestDispatcher("/WEB-INF/views/editComputer.jsp").forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String computerName = request.getParameter("computerName");
		String introduced = request.getParameter("introduced");
		String discontinued = request.getParameter("discontinued");
		String companyId = request.getParameter("companyId");
		String idString = request.getParameter("id");

		List<String> errors = computerFormManager.processInput(idString, computerName, introduced, discontinued, companyId,
				computer -> ComputerService.getInstance().updateComputer(computer));

		logger.info(errors);
		
		if (errors.size() > 0) {
			
			computerFormManager.setRequestCompanies(request);
			request.setAttribute("computer", lastComputer);
			
			request.setAttribute("errors", errors);
			request.getRequestDispatcher("/WEB-INF/views/editComputer.jsp").forward(request, response);
			
		} else {
			response.sendRedirect("dashboard");
		}
	}
}
