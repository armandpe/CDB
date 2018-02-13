package com.excilys.cdb;

import java.util.List;

import com.excilys.cdb.Model.Computer;
import com.excilys.cdb.Service.ComputerService;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

		ComputerService s = ComputerService.getInstance();
		
		List<Computer> computerList = s.getAll();
		
		for(Computer c : computerList) {
			System.out.println(c);
		}
		
		System.out.println(s.getItem(573));
		
		System.out.println(s.getItems("id", "574"));
	}

}
