package com.excilys.cdb.web.spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.excilys.cdb.constant.Servlet;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.utils.ConsumerException;
import com.excilys.cdb.validator.ComputerValidator;
import com.excilys.cdb.validator.InvalidInputException;
import com.excilys.cdb.web.dto.CompanyDTO;
import com.excilys.cdb.web.dto.CompanyMapper;
import com.excilys.cdb.web.dto.ComputerDTO;
import com.excilys.cdb.web.dto.ComputerMapper;

@Component
public class ComputerFormManager {

	private CompanyService companyService;
	
	private ComputerValidator computerValidator;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ComputerFormManager(ComputerValidator computerValidator, CompanyService companyService) {
		this.computerValidator = computerValidator;
		this.companyService = companyService;
	};

	public List<String> processInput(String computerName, String introduced, String discontinued, 
			String companyId, ConsumerException<Computer, FailedDAOOperationException> processComputer) {
		return processInput("0", computerName, introduced, discontinued, companyId, processComputer);
	}

	public List<String> processInput(String id, String computerName, String introduced, String discontinued, 
			String companyId, ConsumerException<Computer, FailedDAOOperationException> processComputer) {

		List<String> errors = new ArrayList<>();
		
		try {
			computerValidator.check(id, computerName, introduced, discontinued, companyId);
			ComputerDTO dto = ComputerMapper.toDTO(id, computerName, introduced, discontinued, companyId);
			Computer computer = ComputerMapper.toComputer(dto);
			processComputer.accept(computer);
		} catch (InvalidInputException e) {
			logger.info(e.getMessage());
		} catch (FailedDAOOperationException e) {
			errors.add(e.getMessage());
		}
		
		computerValidator.getExceptions().forEach(exception -> errors.add(exception.getMessage()));
		return errors;
	}

	public List<String> setRequestCompanies(Model model) {
		
		List<CompanyDTO> companyList = new ArrayList<>();	
		List<String> errors = new ArrayList<>();
		try {
			companyService.getAll(0, companyService.getCount()).forEach(company -> companyList.add(CompanyMapper.toDTO(company)));
		} catch (FailedDAOOperationException e) {
			errors.add(e.getMessage());
		}
		model.addAttribute(Servlet.COMPANY_LIST, companyList);
		return errors;
	}

}
