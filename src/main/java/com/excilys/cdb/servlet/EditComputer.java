package main.java.com.excilys.cdb.servlet;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.java.com.excilys.cdb.dto.ComputerDTO;
import main.java.com.excilys.cdb.dto.ComputerMapper;
import main.java.com.excilys.cdb.model.Computer;
import main.java.com.excilys.cdb.service.ComputerService;

@SuppressWarnings("serial")
@WebServlet("/edit/computer")
public class EditComputer extends HttpServlet {

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String idString = request.getParameter("id");
		long id =  Long.parseLong(idString);
		
		Optional<Computer> gottenComputer = ComputerService.getInstance().getById(id);
		
		if (!gottenComputer.isPresent()) {
			request.getRequestDispatcher("/WEB-INF/views/403.jsp").forward(request, response);
		} else {
			
			ComputerDTO computerDTO = ComputerMapper.toDTO(gottenComputer.get());
						
			request.setAttribute("computer", computerDTO);
			
			request.getRequestDispatcher("/WEB-INF/views/editComputer.jsp").forward(request, response);
			
		}
	}
	
}
