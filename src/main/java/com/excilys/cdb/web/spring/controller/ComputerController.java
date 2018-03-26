package com.excilys.cdb.web.spring.controller;

import static com.excilys.cdb.constant.Spring.NAME_DASHBOARD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.excilys.cdb.Main;
import com.excilys.cdb.constant.Spring;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.pagemanager.PageManagerComplete;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.service.ComputerOrderBy;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.validator.ComputerValidator;
import com.excilys.cdb.validator.InputValidator;
import com.excilys.cdb.validator.InvalidInputException;
import com.excilys.cdb.web.dto.CompanyDTO;
import com.excilys.cdb.web.dto.CompanyMapper;
import com.excilys.cdb.web.dto.ComputerDTO;
import com.excilys.cdb.web.dto.ComputerMapper;
import com.google.gson.Gson;

@Controller
public class ComputerController {

	private ComputerService computerService;
	private CompanyService companyService;
	private ComputerValidator computerValidator;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ComputerController(ComputerService computerService, CompanyService companyService, ComputerValidator computerValidator) {
		this.computerService = computerService;
		this.computerValidator = computerValidator;
		this.companyService = companyService;
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(computerValidator);
	}
	
	@PostMapping("/" + Spring.NAME_ADD)
	public String addComputer(@ModelAttribute(Spring.COMPUTER_DTO) @Validated(ComputerDTO.class) ComputerDTO computerDTO, BindingResult result,
			Model model, RedirectAttributes redirectAttributes)
			throws ServletException, IOException {
		
		
		if (result.hasErrors()) {
			logger.info(result.getAllErrors().toString());
			setRequestCompanies(model);
			List<String> errors = Arrays.asList(result.getAllErrors().stream().map(x -> x.getCode()).toArray(String[]::new));
			model.addAttribute(Spring.ERRORS, new Gson().toJson(errors));
			return Spring.NAME_ADD;
		} else {
			
			List<String> errors = new ArrayList<>();
			
			try {
				computerService.create(ComputerMapper.toComputer(computerDTO));
			} catch (FailedDAOOperationException e) {
				errors.add(e.getMessage());
				
			}

			redirectAttributes.addAttribute(Spring.ERRORS, new Gson().toJson(new ArrayList<String>()));
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
		orderByTest.add(s -> s.equals(Spring.ORDER_BY_NAME) || 
				s.equals(Spring.ORDER_BY_COMPANY_NAME) || 
				s.equals(Spring.ORDER_BY_DISCONTINUED) || 
				s.equals(Spring.ORDER_BY_INTRODUCED));
		try {
			InputValidator.isCorrectString(orderByString, x -> x, true, false, orderByTest);
		} catch (InvalidInputException | NumberFormatException e) {
			logger.info(e.getClass().getName() + " : Incorrect orderby value " + orderByString + e.getMessage());
			throw new InvalidInputException("Invalid orderby value");
		}	
	}
	
	@PostMapping("/" + Spring.NAME_DASHBOARD)
	public String deleteComputers(@RequestParam(value = Spring.DELETE_SELECTION, required = false) String selection, Model model)
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
					model.addAttribute(Spring.ERRORS, new Gson().toJson(errors));
					return NAME_DASHBOARD;
				}
			}
		}
		return "redirect:" + NAME_DASHBOARD;
	}
	
	@PostMapping("/" + Spring.NAME_EDIT)
	public String editComputer(@ModelAttribute(Spring.COMPUTER_DTO) @Validated(ComputerDTO.class) ComputerDTO computerDTO, BindingResult result,
			Model model, RedirectAttributes redirectAttributes)
			throws ServletException, IOException {
		
		if (result.hasErrors()) {
			logger.info(result.getAllErrors().toString());
			List<String> errors = Arrays.asList(result.getAllErrors().stream().map(x -> x.getCode()).toArray(String[]::new));
			return getEditComputer(Long.toString(computerDTO.getId()), errors, model, redirectAttributes);
		} else {
			try {
				computerService.update(ComputerMapper.toComputer(computerDTO));
			} catch (FailedDAOOperationException e) {
				List<String> errors = new ArrayList<>();
				errors.add(e.getMessage());
				return getEditComputer(Long.toString(computerDTO.getId()), errors, model, redirectAttributes);
			}
		}
		
		return "redirect:" + NAME_DASHBOARD;
	}
	
	@GetMapping("/" + Spring.NAME_ADD)
	public String getAddComputer(Model model) throws ServletException, IOException {		
		
		List<String> errors = setRequestCompanies(model);
		model.addAttribute(Spring.ERRORS, new Gson().toJson(errors));
		model.addAttribute(Spring.COMPUTER_DTO, new ComputerDTO());
		return Spring.NAME_ADD;
	}

	@GetMapping("/" + Spring.NAME_DASHBOARD)
	public String getDashboard(@RequestParam(value = Spring.LIMIT, required = false) String limitString,
			@RequestParam(value = Spring.SEARCH, required = false) String searchString,
			@RequestParam(value = Spring.PAGE, required = false) String pageString,
			@RequestParam(value = Spring.ORDER_BY, required = false) String orderByString,
			@RequestParam(value = Spring.ORDER, required = false) String orderByChangedString,
			Model model)
			throws ServletException, IOException {

		boolean orderByChanged = !Spring.ORDER_BY_DESC.equals(orderByChangedString);
		limitString = limitString != null ? limitString : Spring.DEFAULT_LIMIT;
		searchString = searchString != null ? searchString : Spring.DEFAULT_SEARCH;
		pageString = pageString != null ? pageString : Spring.DEFAULT_PAGE;
		orderByString = orderByString != null ? orderByString : Spring.DEFAULT_ORDER_BY;
		PageData<ComputerDTO> pageData = new PageData<>();
		PageManagerComplete<Computer> pageManager = new PageManagerComplete<>(computerService);

		try {
			controlInput(limitString, pageString, orderByString);
		} catch (InvalidInputException e1) {
			return "redirect:" + Spring.NAME_404;
		}

		try {
			long limit = Long.parseLong(limitString);
			long page = Long.parseLong(pageString);
			
			setPageManagerDataDashboard(pageManager, limit, page, searchString, orderByString, orderByChanged);
			pageManager.getPageData().stream().map(ComputerMapper::toDTO).forEach(pageData.getDataList()::add);
			String orderValue = orderByChanged ? Spring.ORDER_BY_ASC : Spring.ORDER_BY_DESC;
			setPageDataDashboard(pageData, pageManager, orderByString, limit, orderValue);
			
		} catch (FailedDAOOperationException e) {
			logger.error(e.getMessage());
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			model.addAttribute(Spring.ERRORS, new Gson().toJson(errors));
		}
		model.addAttribute(Spring.PAGE_DATA, pageData);
		return Spring.NAME_DASHBOARD;
	}
	
	@GetMapping("/" + Spring.NAME_EDIT)
	public String getEditComputer(@RequestParam(value = Spring.ID, required = true) String idString,
			@RequestParam(value = Spring.ERRORS, required = false) List<String> errors,
			Model model, RedirectAttributes redirectAttributes)
			throws ServletException, IOException {
		long id = 0;
		try {
			id = Long.parseLong(idString);
		} catch (NumberFormatException e) {
			logger.error(Main.getErrorMessage("Couldn't parse " + idString, e.getMessage()));
			errors.add("Invalid computer id");
			redirectAttributes.addFlashAttribute(Spring.ERRORS, new Gson().toJson(errors));
			return "redirect:" + Spring.NAME_404;
		}
		
		Optional<Computer> gottenComputer = Optional.empty();
		errors = errors == null ? new ArrayList<>() : errors;
		
		try {
			gottenComputer = computerService.getById(id);
		} catch (FailedDAOOperationException e) {
			errors.add(e.getMessage());
			logger.info(e.getMessage());
			redirectAttributes.addFlashAttribute(Spring.ERRORS, new Gson().toJson(errors));
			return "redirect:" + Spring.NAME_DASHBOARD;
		}
			
		if (!gottenComputer.isPresent()) {
			errors.add("Cannot find target computer");
			redirectAttributes.addFlashAttribute(Spring.ERRORS, new Gson().toJson(errors));
			return "redirect:" + Spring.NAME_DASHBOARD;
		} else {
			ComputerDTO computerDTO = ComputerMapper.toDTO(gottenComputer.get());
			model.addAttribute(Spring.COMPUTER_DTO, computerDTO);
			errors.addAll(setRequestCompanies(model));
			model.addAttribute(Spring.ERRORS, new Gson().toJson(errors));
			
			return Spring.NAME_EDIT;
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

	public List<String> setRequestCompanies(Model model) {
		
		List<CompanyDTO> companyList = new ArrayList<>();	
		List<String> errors = new ArrayList<>();
		try {
			companyService.getAll(0, companyService.getCount()).forEach(company -> companyList.add(CompanyMapper.toDTO(company)));
		} catch (FailedDAOOperationException e) {
			errors.add(e.getMessage());
		}
		model.addAttribute(Spring.COMPANY_LIST, companyList);
		return errors;
	}
	
 	public void setPageManagerDataDashboard(PageManagerComplete<Computer> pageManager, long limit, long page, String searchString, String orderByString, boolean orderByChanged) {
		pageManager.setLimit(limit);
		pageManager.setToSearch(searchString.equals("") ? null : searchString);	

		switch (orderByString) {
		case Spring.ORDER_BY_NAME :
			pageManager.setOrderBy(ComputerOrderBy.NAME, orderByChanged);
			break;
		case Spring.ORDER_BY_COMPANY_NAME :
			pageManager.setOrderBy(ComputerOrderBy.COMPANY_NAME, orderByChanged);
			break;
		case Spring.ORDER_BY_DISCONTINUED :
			pageManager.setOrderBy(ComputerOrderBy.DISCONTINUED, orderByChanged);
			break;
		case Spring.ORDER_BY_INTRODUCED :
			pageManager.setOrderBy(ComputerOrderBy.INTRODUCED, orderByChanged);
			break;
		}
		pageManager.gotTo(page);
	}

}
