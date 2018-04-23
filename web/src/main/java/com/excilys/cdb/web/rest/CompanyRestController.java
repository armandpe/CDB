package com.excilys.cdb.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.service.ICompanyService;

@RestController
public class CompanyRestController {
	
	private final ICompanyService companyService;
	
	public CompanyRestController(ICompanyService companyService) {
		this.companyService = companyService;
	}
	
	@GetMapping("/companies")
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
			return company.get();
		} catch (FailedDAOOperationException e) {
			return null;
		}
	}
	
	@GetMapping("/companies/count")
    public long getCount() {
		try {
			return companyService.getCount();
		} catch (FailedDAOOperationException e) {
			return -1;
		}
	}
	
	@DeleteMapping("/companies/delete/{id}")
	public HttpStatus delete(@PathVariable("id") Long companyID) {
		try {
			companyService.delete(companyID);
		} catch (FailedDAOOperationException e) {
			return HttpStatus.NOT_FOUND;
		}
		return HttpStatus.OK;
	}
}