package com.excilys.cdb.web.spring.controller;

import static com.excilys.cdb.constant.Spring.NAME_DASHBOARD;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.excilys.cdb.constant.Spring;
import com.excilys.cdb.dao.ComputerOrderBy;
import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.User;
import com.excilys.cdb.service.ICompanyService;
import com.excilys.cdb.service.IComputerService;
import com.excilys.cdb.service.UserService;
import com.excilys.cdb.service.pagemanager.PageManagerComplete;
import com.excilys.cdb.utils.LogMessageGenerator;
import com.excilys.cdb.web.dto.CompanyDTO;
import com.excilys.cdb.web.dto.CompanyMapper;
import com.excilys.cdb.web.dto.ComputerDTO;
import com.excilys.cdb.web.dto.ComputerMapper;
import com.excilys.cdb.web.validator.ComputerValidator;
import com.excilys.cdb.web.validator.InputValidator;
import com.excilys.cdb.web.validator.InvalidInputException;
import com.excilys.cdb.web.validator.UserValidator;
import com.google.gson.Gson;

@Controller
public class ComputerController {

	private IComputerService computerService;
	private ICompanyService companyService;
	private UserService userService;
	private ComputerValidator computerValidator;
	private UserValidator userValidator;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public ComputerController(IComputerService computerService, ICompanyService companyService, UserService userService,
			ComputerValidator computerValidator, UserValidator userValidator) {
		this.computerService = computerService;
		this.computerValidator = computerValidator;
		this.companyService = companyService;
		this.userValidator = userValidator;
		this.userService = userService;
	}

	@InitBinder("computer")
	protected void initComputerBinder(WebDataBinder binder) {
		binder.setValidator(computerValidator);
	}
	
	@InitBinder("user")
	protected void initUserBinder(WebDataBinder binder) {
		binder.setValidator(userValidator);
	}

	@GetMapping("/")
	public String index(Model model, Principal principal) {
		model.addAttribute("message", "You are logged in as " + principal.getName());
		return "redirect:" + Spring.NAME_DASHBOARD;
	}

	@GetMapping(Spring.NAME_LOGIN)
	public ModelAndView login(@RequestParam(value = "error", required = false) String error, @RequestParam(value = "logout", required = false) String logout) {
	  ModelAndView model = new ModelAndView();
	  if (error != null) {
		logger.error(error);
		model.addObject("error", "Invalid username and password!");
	  }
	  
	  if (logout != null) {
		model.addObject("logout", "You've been logged out successfully.");
	  } 
	  
	  model.setViewName(Spring.NAME_LOGIN);

	  return model;
	}

	@GetMapping(Spring.NAME_REGISTER)
	public String getRegister(@RequestParam(value = Spring.ERRORS, required = false) List<String> errors, Model model) {
		errors = errors == null ? new ArrayList<>() : errors;
		model.addAttribute(Spring.USER, new User());
		return Spring.NAME_REGISTER;
	}
	
	@PostMapping(Spring.NAME_REGISTER)
	public String postRegister(@ModelAttribute(Spring.USER) @Validated(User.class) User user, BindingResult result,
			Model model, RedirectAttributes redirectAttributes) {
		
		if (result.hasErrors()) {
			List<String> errors = Arrays.asList(result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getCode).toArray(String[]::new));
			logger.error(errors.toString());
			model.addAttribute(Spring.ERRORS, new Gson().toJson(errors));
			return Spring.NAME_REGISTER;
		} else {
			List<String> errors = new ArrayList<>();

			try {
				userService.create(user);
			} catch (FailedDAOOperationException e) {
				errors.add(e.getMessage());
				model.addAttribute(Spring.ERRORS, new Gson().toJson(errors));
				return Spring.NAME_REGISTER;
			}

			redirectAttributes.addAttribute(Spring.ERRORS, new Gson().toJson(errors));
			return "redirect:" + Spring.NAME_LOGIN;
		}
	}
	
	@GetMapping("/" + Spring.NAME_403)
	public String error404() throws ServletException, IOException {
		return Spring.NAME_403;
	}
	
	@GetMapping("/" + Spring.NAME_404)
	public String error403() throws ServletException, IOException {
		return Spring.NAME_404;
	}
	
	@GetMapping("/" + Spring.NAME_500)
	public String error500() throws ServletException, IOException {
		return Spring.NAME_500;
	}
	
	@PostMapping("/" + Spring.NAME_ADD)
	@PreAuthorize("hasRole('ADMIN')")
	public String addComputer(@ModelAttribute(Spring.COMPUTER_DTO) @Validated(ComputerDTO.class) ComputerDTO computerDTO, BindingResult result,
			Model model, RedirectAttributes redirectAttributes)
					throws ServletException, IOException {
		addUsernameAsParameter(model);

		if (result.hasErrors()) {
			logger.info(result.getAllErrors().toString());
			setRequestCompanies(model);
			List<String> errors = Arrays.asList(result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getCode).toArray(String[]::new));
			model.addAttribute(Spring.ERRORS, new Gson().toJson(errors));
			return Spring.NAME_ADD;
		} else {

			List<String> errors = new ArrayList<>();

			try {
				computerService.create(ComputerMapper.toComputer(computerDTO));
			} catch (FailedDAOOperationException e) {
				errors.add(e.getMessage());

			}

			redirectAttributes.addAttribute(Spring.ERRORS, new Gson().toJson(errors));
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
	
	public void addUsernameAsParameter(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetail = (UserDetails) auth.getPrincipal();	
			model.addAttribute("username", userDetail.getUsername());
		}
	}

	@DeleteMapping("/" + Spring.NAME_DASHBOARD)
	@PreAuthorize("hasRole('ADMIN')")
	public String deleteComputers(@RequestParam(value = Spring.DELETE_SELECTION, required = false) String selection, Model model)
			throws ServletException, IOException {
		List<String> errors = new ArrayList<>();

		addUsernameAsParameter(model);
		
		if (selection != null) {
			String[] toDeleteList = selection.split(",");

			for (String toDelete : toDeleteList) {
				long id = Long.parseLong(toDelete);
				try {
					computerService.deleteById(id);
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
	@PreAuthorize("hasRole('ADMIN')")
	public String editComputer(@ModelAttribute(Spring.COMPUTER_DTO) @Validated(ComputerDTO.class) ComputerDTO computerDTO, BindingResult result,
			Model model, RedirectAttributes redirectAttributes)
					throws ServletException, IOException {

		addUsernameAsParameter(model);
		
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
	@PreAuthorize("hasRole('ADMIN') OR hasRole('USER')")
	public String getAddComputer(Model model) throws ServletException, IOException {		

		addUsernameAsParameter(model);
		List<String> errors = setRequestCompanies(model);
		model.addAttribute(Spring.ERRORS, new Gson().toJson(errors));
		model.addAttribute(Spring.COMPUTER_DTO, new ComputerDTO());
		return Spring.NAME_ADD;
	}

	@GetMapping("/" + Spring.NAME_DASHBOARD)
	@PreAuthorize("hasRole('ADMIN') OR hasRole('USER')")
	public String getDashboard(@RequestParam(value = Spring.LIMIT, required = false) String limitString,
			@RequestParam(value = Spring.SEARCH, required = false) String searchString,
			@RequestParam(value = Spring.PAGE, required = false) String pageString,
			@RequestParam(value = Spring.ORDER_BY, required = false) String orderByString,
			@RequestParam(value = Spring.ORDER, required = false) String orderByChangedString,
			Model model)
					throws ServletException, IOException {

		addUsernameAsParameter(model);
		
		boolean orderByChanged = !Spring.ORDER_BY_DESC.equals(orderByChangedString);
		limitString = limitString != null ? limitString : Spring.DEFAULT_LIMIT;
		searchString = searchString != null ? searchString : Spring.DEFAULT_SEARCH;
		pageString = pageString != null ? pageString : Spring.DEFAULT_PAGE;
		orderByString = orderByString != null ? orderByString : Spring.DEFAULT_ORDER_BY;
		PageData<ComputerDTO> pageData = new PageData<>();
		PageManagerComplete pageManager = new PageManagerComplete(computerService);

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
	@PreAuthorize("hasRole('ADMIN')")
	public String getEditComputer(@RequestParam(value = Spring.ID, required = true) String idString,
			@RequestParam(value = Spring.ERRORS, required = false) List<String> errors,
			Model model, RedirectAttributes redirectAttributes)
					throws ServletException, IOException {
		errors = errors == null ? new ArrayList<String>() : errors;
		addUsernameAsParameter(model);
		long id = 0;
		try {
			id = Long.parseLong(idString);
		} catch (NumberFormatException e) {
			logger.error(LogMessageGenerator.getErrorMessage("Couldn't parse " + idString, e.getMessage()));
			errors.add("Invalid computer id");
			redirectAttributes.addFlashAttribute(Spring.ERRORS, new Gson().toJson(errors));
			return "redirect:" + Spring.NAME_404;
		}

		Optional<Computer> gottenComputer = Optional.empty();

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

	public void setPageDataDashboard(PageData<?> pageData, PageManagerComplete pageManager, String orderBy, long limit, String order) {
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
			companyService.getAll().forEach(company -> companyList.add(CompanyMapper.toDTO(company)));
		} catch (FailedDAOOperationException e) {
			errors.add(e.getMessage());
		}
		model.addAttribute(Spring.COMPANY_LIST, companyList);
		return errors;
	}

	public void setPageManagerDataDashboard(PageManagerComplete pageManager, long limit, long page, String searchString, String orderByString, boolean orderByChanged) {
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
