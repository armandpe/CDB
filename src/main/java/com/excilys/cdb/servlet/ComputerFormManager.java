package com.excilys.cdb.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.dto.CompanyDTO;
import com.excilys.cdb.dto.CompanyMapper;
import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.dto.ComputerMapper;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.utils.ConsumerException;
import com.excilys.cdb.validator.ComputerValidator;
import com.excilys.cdb.validator.InvalidInputException;

public class ComputerFormManager {


	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected List<CompanyDTO> companyList = new ArrayList<>();	
	protected CompanyService companyService = CompanyService.getInstance();


	public List<String> setRequestCompanies(HttpServletRequest request) {
		List<String> errors = new ArrayList<>();
		companyList.clear();
		errors.clear();
		try {
			companyService.getAll(0, companyService.getCount()).forEach(company -> companyList.add(CompanyMapper.toDTO(company)));
		} catch (FailedDAOOperationException e) {
			errors.add(e.getMessage());
		}
		request.setAttribute("companyList", companyList);
		return errors;
	}

	public List<String> processInput(String id, String computerName, String introduced, String discontinued, 
			String companyId, ConsumerException<Computer, FailedDAOOperationException> processComputer) {

		List<String> errors = new ArrayList<>();
		
		try {
			ComputerValidator.check(id, computerName, introduced, discontinued, companyId);
			ComputerDTO dto = ComputerMapper.toDTO(id, computerName, introduced, discontinued, companyId);
			Computer computer = ComputerMapper.toComputer(dto);
			processComputer.accept(computer);
		} catch (InvalidInputException e) {
			logger.info(e.getMessage());
		} catch (FailedDAOOperationException e) {
			errors.add(e.getMessage());
		}
		
		ComputerValidator.getExceptions().forEach(exception -> errors.add(exception.getMessage()));
		
		return errors;
	}

	public List<String> processInput(String computerName, String introduced, String discontinued, 
			String companyId, ConsumerException<Computer, FailedDAOOperationException> processComputer) {
		return processInput("0", computerName, introduced, discontinued, companyId, processComputer);
	}

}
