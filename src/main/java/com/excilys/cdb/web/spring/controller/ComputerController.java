package com.excilys.cdb.web.spring.controller;

import static com.excilys.cdb.constant.Servlet.NAME_DASHBOARD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.excilys.cdb.Main;
import com.excilys.cdb.constant.Servlet;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.pagemanager.PageManagerComplete;
import com.excilys.cdb.service.ComputerOrderBy;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.validator.InputValidator;
import com.excilys.cdb.validator.InvalidInputException;
import com.excilys.cdb.web.dto.ComputerDTO;
import com.excilys.cdb.web.dto.ComputerMapper;
import com.google.gson.Gson;

@Controller
public class ComputerController {

	private ComputerFormManager computerFormManager;
	
	private ComputerService computerService;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ComputerController(ComputerService computerService, ComputerFormManager computerFormManager) {
		this.computerService = computerService;
		this.computerFormManager = computerFormManager;
	}
	
	@PostMapping("/" + Servlet.NAME_ADD)
	public String addComputer(@RequestParam(value = Servlet.COMPUTER_NAME, required = false) String computerName,
			@RequestParam(value = Servlet.INTRODUCED, required = false) String introduced,
			@RequestParam(value = Servlet.DISCONTINUED, required = false) String discontinued,
			@RequestParam(value = Servlet.COMPANY_ID, required = false) String companyId,
			Model model, RedirectAttributes redirectAttributes)
			throws ServletException, IOException {
		
		List<String> errors = computerFormManager.processInput(computerName, introduced, discontinued, companyId, computer -> computerService.create(computer));
		
		if (errors.size() > 0) {
			computerFormManager.setRequestCompanies(model);
			model.addAttribute(Servlet.ERRORS, new Gson().toJson(errors));
			return Servlet.NAME_ADD;
		} else {
			redirectAttributes.addAttribute(Servlet.ERRORS, new Gson().toJson(errors));
			return "redirect:" + NAME_DASHBOARD;
		}
	}

	public void controlInput(String limitString, String pageString, String orderByString) throws InvalidInputException {
		List<Function<Long, Boolean>> limitTests = new ArrayList<>();
		limitTests.add(limit -> (limit > 0));
		try {
			InputValidator.isCorrectString(limitString, toParse -> Long.parseLong(toParse), true, false, limitTests);
		} catch (InvalidInputException | NumberFormatException e) {
			logger.info(e.getClass().getName() + " : Incorrect limit value " + e.getMessage());
			throw new InvalidInputException("Invalid limit");
		}

		List<Function<Long, Boolean>> pageTests = new ArrayList<>();
		try {
			InputValidator.isCorrectString(pageString, toParse -> Long.parseLong(toParse), true, false, pageTests);
		} catch (InvalidInputException | NumberFormatException e) {
			logger.info(e.getClass().getName() + " : Incorrect page value " + e.getMessage());
			throw new InvalidInputException("Invalid page");
		}	

		List<Function<String, Boolean>> orderByTest = new ArrayList<>();
		orderByTest.add(s -> s.equals(Servlet.ORDER_BY_NAME) || 
				s.equals(Servlet.ORDER_BY_COMPANY_NAME) || 
				s.equals(Servlet.ORDER_BY_DISCONTINUED) || 
				s.equals(Servlet.ORDER_BY_INTRODUCED));
		try {
			InputValidator.isCorrectString(orderByString, x -> x, true, false, orderByTest);
		} catch (InvalidInputException | NumberFormatException e) {
			logger.info(e.getClass().getName() + " : Incorrect orderby value " + orderByString + e.getMessage());
			throw new InvalidInputException("Invalid orderby value");
		}	
	}
	
	@PostMapping("/" + Servlet.NAME_DASHBOARD)
	public String deleteComputers(@RequestParam(value = Servlet.DELETE_SELECTION, required = false) String selection, Model model)
			throws ServletException, IOException {
		List<String> errors = new ArrayList<>();

		if (selection != null) {
			String[] toDeleteList = selection.split(",");

			for (String toDelete : toDeleteList) {
				long id = Long.parseLong(toDelete);
				try {
					computerService.delete(id);
				} catch (FailedDAOOperationException e) {
					logger.info(e.getMessage());
					errors.add(e.getMessage());
					model.addAttribute(Servlet.ERRORS, new Gson().toJson(errors));
					return NAME_DASHBOARD;
				}
			}
		}
		return "redirect:" + NAME_DASHBOARD;
	}
	
	@PostMapping("/" + Servlet.NAME_EDIT)
	public String editComputer(@RequestParam(value = Servlet.COMPUTER_NAME, required = true) String computerName,
			@RequestParam(value = Servlet.INTRODUCED, required = true) String introduced,
			@RequestParam(value = Servlet.DISCONTINUED, required = true) String discontinued,
			@RequestParam(value = Servlet.COMPANY_ID, required = false) String companyId,
			@RequestParam(value = Servlet.ID, required = true) String idString,
			Model model, RedirectAttributes redirectAttributes)
			throws ServletException, IOException {

		List<String> errors = computerFormManager.processInput(idString, computerName, introduced, discontinued, companyId,
				computer -> computerService.update(computer));

		if (errors.size() > 0) {
			logger.info(errors.toString());
			computerFormManager.setRequestCompanies(model);
			return getEditComputer(idString, errors, model, redirectAttributes);
		}
		
		return "redirect:" + NAME_DASHBOARD;
	}
	
	@GetMapping("/" + Servlet.NAME_ADD)
	public String getAddComputer(Model model) throws ServletException, IOException {		
		
		List<String> errors = computerFormManager.setRequestCompanies(model);
		model.addAttribute(Servlet.ERRORS, new Gson().toJson(errors));
		return Servlet.NAME_ADD;
	}

	@GetMapping("/" + Servlet.NAME_DASHBOARD)
	public String getDashboard(@RequestParam(value = Servlet.LIMIT, required = false) String limitString,
			@RequestParam(value = Servlet.SEARCH, required = false) String searchString,
			@RequestParam(value = Servlet.PAGE, required = false) String pageString,
			@RequestParam(value = Servlet.ORDER_BY, required = false) String orderByString,
			@RequestParam(value = Servlet.ORDER, required = false) String orderByChangedString,
			Model model)
			throws ServletException, IOException {

		boolean orderByChanged = !Servlet.ORDER_BY_DESC.equals(orderByChangedString);
		limitString = limitString != null ? limitString : Servlet.DEFAULT_LIMIT;
		searchString = searchString != null ? searchString : Servlet.DEFAULT_SEARCH;
		pageString = pageString != null ? pageString : Servlet.DEFAULT_PAGE;
		orderByString = orderByString != null ? orderByString : Servlet.DEFAULT_ORDER_BY;

		PageData<ComputerDTO> pageData = new PageData<>();
		PageManagerComplete<Computer> pageManager = new PageManagerComplete<>(computerService);

		try {
			controlInput(limitString, pageString, orderByString);
		} catch (InvalidInputException e1) {
			return "redirect:" + Servlet.NAME_404;
		}

		try {
			long limit = Long.parseLong(limitString);
			long page = Long.parseLong(pageString);
			
			setPageManagerDataDashboard(pageManager, limit, page, searchString, orderByString, orderByChanged);
			pageManager.getPageData().stream().map(ComputerMapper::toDTO).forEach(pageData.getDataList()::add);

			String orderValue = orderByChanged ? Servlet.ORDER_BY_ASC : Servlet.ORDER_BY_DESC;
			
			setPageDataDashboard(pageData, pageManager, orderByString, limit, orderValue);
			
		} catch (FailedDAOOperationException e) {
			logger.error(e.getMessage());
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			model.addAttribute(Servlet.ERRORS, new Gson().toJson(errors));
		}
		
		model.addAttribute(Servlet.PAGE_DATA, pageData);
		
		return Servlet.NAME_DASHBOARD;
	}
	
	@GetMapping("/" + Servlet.NAME_EDIT)
	public String getEditComputer(@RequestParam(value = Servlet.ID, required = true) String idString,
			@RequestParam(value = Servlet.ERRORS, required = false) List<String> errors,
			Model model, RedirectAttributes redirectAttributes)
			throws ServletException, IOException {
		long id = 0;
		try {
			id = Long.parseLong(idString);
		} catch (NumberFormatException e) {
			logger.error(Main.getErrorMessage("Couldn't parse " + idString, e.getMessage()));
			errors.add("Invalid computer id");
			redirectAttributes.addFlashAttribute(Servlet.ERRORS, new Gson().toJson(errors));
			return "redirect:" + Servlet.NAME_404;
		}
		
		Optional<Computer> gottenComputer = Optional.empty();
		errors = errors == null ? new ArrayList<>() : errors;
		
		try {
			gottenComputer = computerService.getById(id);
		} catch (FailedDAOOperationException e) {
			errors.add(e.getMessage());
			logger.info(e.getMessage());
			redirectAttributes.addFlashAttribute(Servlet.ERRORS, new Gson().toJson(errors));
			return "redirect:" + Servlet.NAME_DASHBOARD;
		}
			
		if (!gottenComputer.isPresent()) {
			errors.add("Cannot find target computer");
			redirectAttributes.addFlashAttribute(Servlet.ERRORS, new Gson().toJson(errors));
			return "redirect:" + Servlet.NAME_DASHBOARD;
		} else {
			ComputerDTO computerDTO = ComputerMapper.toDTO(gottenComputer.get());
			
			model.addAttribute("computer", computerDTO);

			errors.addAll(computerFormManager.setRequestCompanies(model));
			
			model.addAttribute("errors", new Gson().toJson(errors));
			
			return Servlet.NAME_EDIT;
		}
	}
	
	public void setPageDataDashboard(PageData<?> pageData, PageManagerComplete<?> pageManager, String orderBy, long limit, String order) {
		pageData.setOrder(order);
		pageData.setOrderby(orderBy);
		pageData.setLimit(limit);
		pageData.setSearch(pageManager.getToSearch());
		pageData.setCurrentPage(pageManager.getPage());
		pageData.setMaxPage(pageManager.getMaxPage());
		pageData.setCount(pageManager.getMax());
	}

	public void setPageManagerDataDashboard(PageManagerComplete<Computer> pageManager, long limit, long page, String searchString, String orderByString, boolean orderByChanged) {
		pageManager.setLimit(limit);
		pageManager.setToSearch(searchString.equals("") ? null : searchString);	

		switch (orderByString) {
		case Servlet.ORDER_BY_NAME :
			pageManager.setOrderBy(ComputerOrderBy.NAME, orderByChanged);
			break;
		case Servlet.ORDER_BY_COMPANY_NAME :
			pageManager.setOrderBy(ComputerOrderBy.COMPANY_NAME, orderByChanged);
			break;
		case Servlet.ORDER_BY_DISCONTINUED :
			pageManager.setOrderBy(ComputerOrderBy.DISCONTINUED, orderByChanged);
			break;
		case Servlet.ORDER_BY_INTRODUCED :
			pageManager.setOrderBy(ComputerOrderBy.INTRODUCED, orderByChanged);
			break;
		}
		pageManager.gotTo(page);
	}
}
