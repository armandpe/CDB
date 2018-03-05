package main.java.com.excilys.cdb.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.com.excilys.cdb.dto.CompanyDTO;
import main.java.com.excilys.cdb.dto.CompanyMapper;
import main.java.com.excilys.cdb.dto.ComputerDTO;
import main.java.com.excilys.cdb.dto.ComputerMapper;
import main.java.com.excilys.cdb.model.Computer;
import main.java.com.excilys.cdb.service.CompanyService;
import main.java.com.excilys.cdb.service.ComputerService;
import main.java.com.excilys.cdb.validator.ComputerValidator;
import main.java.com.excilys.cdb.validator.InvalidInputException;

@SuppressWarnings("serial")
@WebServlet("/add/computer")
public class AddComputer extends HttpServlet  {
	
	protected List<CompanyDTO> companyList = new ArrayList<>();	
	protected CompanyService companyService = CompanyService.getInstance();
	protected List<String> errors = new ArrayList<>();
	protected Logger logger = LogManager.getLogger(this.getClass());

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		
		errors.clear();
		
		setRequestValues(request);
		this.getServletContext().getRequestDispatcher("/WEB-INF/views/addComputer.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String computerName = request.getParameter("computerName");
        String introduced = request.getParameter("introduced");
        String discontinued = request.getParameter("discontinued");
        String companyId = request.getParameter("companyId");
        
        errors.clear();
        
        try {
        	ComputerValidator.check(computerName, introduced, discontinued, companyId);
        	ComputerDTO dto = ComputerMapper.toDTO(computerName, introduced, discontinued, companyId);
        	Computer computer = ComputerMapper.toComputer(dto);
        	ComputerService.getInstance().createComputer(computer);
        } catch (InvalidInputException e) {
        	logger.info(e.getMessage());
        	ComputerValidator.getExceptions().forEach(exception -> errors.add(exception.getMessage()));
        }
        
        setRequestValues(request);
		this.getServletContext().getRequestDispatcher("/WEB-INF/views/addComputer.jsp").forward(request, response);
	}
	
	protected void setRequestValues(HttpServletRequest request) {
		companyList.clear();
		companyService.getAll(0, companyService.getCount()).forEach(company -> companyList.add(CompanyMapper.toDTO(company)));
		request.setAttribute("companyList", companyList);
		request.setAttribute("errors", errors);
	}
}
