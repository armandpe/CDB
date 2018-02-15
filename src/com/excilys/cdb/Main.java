package com.excilys.cdb;

import java.time.LocalDate;
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
		
		Computer computer = computerService.getById(587).get();
		
		computer.setName("updated");
		
		computerService.updateComputer(computer);
		
		System.out.println(computerService.getAll());
		
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
		
//		List<Company> companyList = companyService.getAll();
//		
//		System.out.println(companyList);
//		
//		Optional<Company> c = companyService.getById(42);
//			
//		if(c.isPresent())
//			System.out.println(c.get());
//		
//		System.out.println(companyService.getItems("id", "37"));
//		
//		System.out.println("Fonctions disponibles :\n list computers ()\n list companies ()\n computer details (id)\n create computer (name, introduced, discontinued, company_id)\n update computer (id, parameters to change)\n delete computer (id)");
//        System.out.println("ENTREZ LA COMMANDE");
	}
	
	

}
