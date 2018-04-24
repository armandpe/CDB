package com.excilys.cdb.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.service.ICompanyService;

@RestController
@CrossOrigin(origins = "http://localhost:4200/web")
public class CompanyRestController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final ICompanyService companyService;
	
	public CompanyRestController(ICompanyService companyService) {
		this.companyService = companyService;
	}
	
	@GetMapping("/companies")
	@CrossOrigin(origins = "http://localhost:4200/web")
    public List<Company> getAll() {
		try {
			return companyService.getAll();
		} catch (FailedDAOOperationException e) {
			return new ArrayList<>();
		}
	}
	
	@GetMapping("/companies/{id}")
    public Company getById(@PathVariable("id") Long companyID) {
		try {
			Optional<Company> company = companyService.getById(companyID);
			if (company.isPresent()) {
				return company.get();
			}
		} catch (FailedDAOOperationException e) {}
		return null;
	}
	
	@GetMapping("/companies/count")
    public long getCount() {
		try {
			return companyService.getCount();
		} catch (FailedDAOOperationException e) {
			return -1;
		}
	}
	
	@DeleteMapping("/companies/{id}")
	public HttpStatus delete(@PathVariable("id") Long companyID) {
		try {
			companyService.delete(companyID);
		} catch (FailedDAOOperationException e) {
			return HttpStatus.NOT_FOUND;
		}
		return HttpStatus.OK;
	}
	
	@PostMapping("/companies")
	public HttpStatus create(@RequestBody Company company) {
		if (company == null || company.getId() == null) {
			logger.error("Create - Null company");
			return HttpStatus.I_AM_A_TEAPOT;
		}
		try {
			company.setId(null);
			companyService.create(company);
		} catch (FailedDAOOperationException e) {
			return HttpStatus.NOT_FOUND;
		}
		return HttpStatus.OK;
	}
	
	@PutMapping("/companies")
	public HttpStatus update(@RequestBody Company company) {
		if (company == null || company.getId() == null) {
			logger.error("Update - Null company");
			return HttpStatus.I_AM_A_TEAPOT;
		}
		try {
			companyService.update(company);
		} catch (FailedDAOOperationException e) {
			return HttpStatus.NOT_FOUND;
		}
		return HttpStatus.OK;
	}
}