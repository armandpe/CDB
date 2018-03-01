package main.java.com.excilys.cdb.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.com.excilys.cdb.dto.ComputerDTO;
import main.java.com.excilys.cdb.dto.ComputerMapper;
import main.java.com.excilys.cdb.service.ComputerService;

@SuppressWarnings("serial")
@WebServlet("/dashboard.html")
public class Dashboard extends HttpServlet {

	Logger logger = LogManager.getLogger(this.getClass());

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		long count = ComputerService.getInstance().getCount();

		request.setAttribute("count", count);

		ArrayList<ComputerDTO> dtoList = new ArrayList<>();
		
		ComputerService.getInstance().getAll(0, 600).forEach(x -> dtoList.add(ComputerMapper.toDTO(x)));
		
		request.setAttribute("computerList", dtoList);

		this.getServletContext().getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
	}
}
