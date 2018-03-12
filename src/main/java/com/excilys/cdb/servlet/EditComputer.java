package main.java.com.excilys.cdb.servlet;

import static main.java.com.excilys.cdb.constant.Servlet.NAME_DASHBOARD;
import static main.java.com.excilys.cdb.constant.Servlet.NAME_EDIT;
import static main.java.com.excilys.cdb.constant.Servlet.PATH_403;
import static main.java.com.excilys.cdb.constant.Servlet.PATH_EDIT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

@SuppressWarnings("serial")
@WebServlet("/" + NAME_EDIT)
public class EditComputer extends HttpServlet {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected ComputerFormManager computerFormManager = new ComputerFormManager();
	protected ComputerService computerService = ComputerService.getInstance();
	protected ComputerDTO lastComputer = new ComputerDTO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idString = request.getParameter("id");
		long id = 0;
		try {
			id = Long.parseLong(idString);
		} catch (NumberFormatException e) {
			logger.error(e.getMessage() + " - Couldn't parse " + idString);
			request.getRequestDispatcher(PATH_403).forward(request, response);
		}
		
		Optional<Computer> gottenComputer = Optional.empty();
		List<String> errors = new ArrayList<>();
		
		try {
			gottenComputer = computerService.getById(id);
		} catch (FailedDAOOperationException e) {
			errors.add(e.getMessage());
			logger.info(e.getMessage());
		}

		if (!gottenComputer.isPresent()) {
			request.getRequestDispatcher(PATH_403).forward(request, response);
		} else {
			lastComputer = ComputerMapper.toDTO(gottenComputer.get());
			request.setAttribute("computer", lastComputer);

			errors.addAll(computerFormManager.setRequestCompanies(request));

			request.setAttribute("errors", new Gson().toJson(errors));
			
			request.getRequestDispatcher(PATH_EDIT).forward(request, response);
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
				computer -> ComputerService.getInstance().update(computer));

		if (errors.size() > 0) {
			logger.info(errors.toString());
			computerFormManager.setRequestCompanies(request);
			request.setAttribute("computer", lastComputer);
			
			request.setAttribute("errors", new Gson().toJson(errors));
			request.getRequestDispatcher(PATH_EDIT).forward(request, response);
			
		} else {
			response.sendRedirect(NAME_DASHBOARD);
		}
	}
}
