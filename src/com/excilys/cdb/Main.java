package com.excilys.cdb;

import java.util.List;
import java.util.Optional;

import com.excilys.cdb.Model.Company;
import com.excilys.cdb.Model.Computer;
import com.excilys.cdb.Service.CompanyService;
import com.excilys.cdb.Service.ComputerService;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

		ComputerService computerService = ComputerService.getInstance();
		CompanyService companyService = CompanyService.getInstance();
//		
//		List<Computer> computerList = computerService.getAll();
//		
//		for(Computer c : computerList) {
//			System.out.println(c);
//		}
//		
//		Optional<Computer> c = computerService.getById(573);
//		
//		if(c.isPresent())
//			System.out.println(c.get());
//		
//		System.out.println(computerService.getItems("company_id", "27"));
		
		List<Company> companyList = companyService.getAll();
		
		System.out.println(companyList);
		
		Optional<Company> c = companyService.getById(42);
			
		if(c.isPresent())
			System.out.println(c.get());
		
		System.out.println(companyService.getItems("id", "37"));
		
	}
	
	

}
