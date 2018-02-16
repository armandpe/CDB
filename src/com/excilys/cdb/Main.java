package com.excilys.cdb;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.excilys.cdb.Service.CompanyService;
import com.excilys.cdb.Service.ComputerService;
import com.excilys.cdb.Service.Service;
import com.excilys.cdb.Service.ServiceClass;
import com.excilys.cdb.Service.ServiceMethod;


public class Main {

	final static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Class<?>[] services = {ComputerService.class, CompanyService.class};

		boolean continuer = true;
		Scanner sc = new Scanner(System.in);
		LinkedHashMap<Class<?>, Method[]> servicesMethods = getSQLMethods(services);

		while (continuer) {			
			int maxChoice = PrintChoices(services, servicesMethods);

			int choice = -1; 
			try {
				choice = sc.nextInt();
				sc.nextLine();
			}catch(InputMismatchException e) {

			}

			if(choice == 0)
				break;

			if(choice > maxChoice || choice < 0)
			{
				print("Invalid input - Please try again ...");
				continue;
			}

			print(applyChoice(choice, servicesMethods, sc).toString());
			print("\n Press ENTER");
			sc.nextLine();
			for(int i = 0; i < 10; ++i)
				print("");
		}

		print("Goodbye ! :) "); 
		sc.close();

	}

	private static Object applyChoice(int choice, LinkedHashMap<Class<?>, Method[]> servicesMethods, Scanner sc) {

		Class<?> usedClass = null;
		int counter = 1;

		for(Entry<Class<?>, Method[]> entry : servicesMethods.entrySet()) {	
			counter += entry.getValue().length;
			if(choice < counter) {
				usedClass = entry.getKey();
				counter -= entry.getValue().length;
				//On set le choice au numero de la methode dans la classe
				choice -= counter;
				break;
			}			
		}

		Method chosenMethod = servicesMethods.get(usedClass)[choice];

		Parameter[] params = chosenMethod.getParameters();

		Object[] parameters = AskParameters(params, sc).toArray();

		try {
			return chosenMethod.invoke(getServiceInstance(usedClass), parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}
		return "Error";
	}

	private static Service<?, ?> getServiceInstance(Class<?> serviceClass){

		Service<?,?> service = null;

		Method method = null;
		try {
			method = serviceClass.getMethod("getInstance");
		} catch (NoSuchMethodException | SecurityException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}

		try {
			service = (Service<?,?>) method.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}

		return service;
	}


	private static List<?> AskParameters(Parameter[] params, Scanner sc) {
		Class<?>[] acceptedTypes = { long.class, Long.class, LocalDate.class, String.class };
		ArrayList<Class<?>> acceptedTypesList = new ArrayList<>(Arrays.asList(acceptedTypes));

		LinkedList<Object> parameters = new LinkedList<>();

		for(Parameter param : params) {

			String name = param.getName();

			if(param.isAnnotationPresent(ParamDescription.class))
				name = param.getAnnotation(ParamDescription.class).name();

			print("Enter the " + name + " :");

			if(acceptedTypesList.contains(param.getType())) {
				parameters.add(AskParameter(param, sc));
			} else {
				Class<?> type = param.getType();
				Constructor<?>[] cs = type.getConstructors();
				Constructor<?> maxArgs = cs[0];

				for(Constructor<?> c : cs) {
					if(c.getParameterCount() > maxArgs.getParameterCount())
						maxArgs = c;
				}
				try {
					parameters.add(maxArgs.newInstance(AskParameters(maxArgs.getParameters(), sc).toArray()));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
					String methodName = ste[1].getMethodName(); 
					logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
				}
			}
		}
		return parameters;
	}

	private static Object AskParameter(Parameter param, Scanner sc) {

		if(param.getType() == long.class || param.getType() == Long.class) {
			long l = sc.nextLong();
			sc.nextLine();
			return l;

		} else if(param.getType() == LocalDate.class) {
			print("Enter the year :");
			int year = sc.nextInt();
			sc.nextLine();

			print("Enter the month :");
			Month month = Month.of(sc.nextInt());
			sc.nextLine();

			print("Enter the day :");
			int dayOfMonth = sc.nextInt();
			sc.nextLine();

			return LocalDate.of(year, month, dayOfMonth);
		} else if(param.getType() == String.class) {
			return sc.nextLine();
		}

		return null;

	}

	private static void print(String s) {
		System.out.println(s);
	}

	private static int PrintChoices(Class<?>[] services, Map<Class<?>, Method[]> servicesMethods) {
		print("Here is the command list :");
		print("\t0 - Exit the program");
		int i = 0;
		for(Class<?> serviceClass : services) {
			print("For " + serviceClass.getAnnotation(ServiceClass.class).name() + " : ");

			for(Method method : servicesMethods.get(serviceClass)) {
				print("\t" + ++i + " - " + method.getAnnotation(ServiceMethod.class).name());
			}
			print("");
		}

		print("Enter the function code you want to use (0 to exit) :");

		return i;
	}

	private static LinkedHashMap<Class<?>, Method[]> getSQLMethods(Class<?>[] classes){

		LinkedHashMap<Class<?>, Method[]> result = new LinkedHashMap<Class<?>, Method[]>();

		for(Class<?> currentClass : classes) {
			Set<Method> toAdd =  new HashSet<>();

			for(Method method : currentClass.getMethods()) {
				if(method.isAnnotationPresent(ServiceMethod.class))
					toAdd.add(method);
			}

			result.put(currentClass, toAdd.toArray(new Method[toAdd.size()]));
		}

		return result;		
	}
}
