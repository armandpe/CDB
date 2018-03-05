package main.java.com.excilys.cdb.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.java.com.excilys.cdb.dto.CompanyDTO;
import main.java.com.excilys.cdb.dto.CompanyMapper;
import main.java.com.excilys.cdb.dto.ComputerDTO;
import main.java.com.excilys.cdb.dto.ComputerMapper;
import main.java.com.excilys.cdb.model.Computer;
import main.java.com.excilys.cdb.service.CompanyService;
import main.java.com.excilys.cdb.service.ComputerService;
import main.java.com.excilys.cdb.validator.ComputerValidator;
import main.java.com.excilys.cdb.validator.InvalidDateException;
import main.java.com.excilys.cdb.validator.InvalidIdException;
import main.java.com.excilys.cdb.validator.InvalidNameException;

@SuppressWarnings("serial")
@WebServlet("/add/computer")
public class AddComputer extends HttpServlet  {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		

		CompanyService companyService = CompanyService.getInstance();
		List<CompanyDTO> companyList = new ArrayList<>();

		companyService.getAll(0, companyService.getCount()).forEach(company -> CompanyMapper.toDTO(company));
		request.setAttribute("companyList", companyList);
		this.getServletContext().getRequestDispatcher("/WEB-INF/views/addComputer.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        String computerName = request.getParameter("computerName");
        String introduced = request.getParameter("introduced");
        String discontinued = request.getParameter("discontinued");
        String companyId = request.getParameter("companyId");
        
        try {
        	ComputerValidator.check(computerName, introduced, discontinued, companyId);
        	ComputerDTO dto = ComputerMapper.toDTO(computerName, introduced, discontinued, companyId);
        	Computer computer = ComputerMapper.toComputer(dto);
        	ComputerService.getInstance().createComputer(computer);
        } catch (InvalidDateException | InvalidNameException | InvalidIdException e) {
            request.setAttribute("error", e.getMessage());
        }
         
		this.getServletContext().getRequestDispatcher("/WEB-INF/views/addComputer.jsp").forward(request, response);
	}
}
