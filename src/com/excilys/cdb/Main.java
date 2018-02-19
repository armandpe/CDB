package com.excilys.cdb;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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

		ArrayList<Class<? extends Service<?, ?>>> services = new ArrayList<>();
		services.add(ComputerService.class);
		services.add(CompanyService.class);

		boolean continuer = true;
		Scanner sc = new Scanner(System.in);
		LinkedHashMap<Class<? extends Service<?, ?>>, Method[]> servicesMethods = getSQLMethods(services);

		while (continuer) {			
			int maxChoice = PrintChoices(services, servicesMethods);

			int choice = -1; 
			try {
				choice = sc.nextInt();
				sc.nextLine();
			}catch(InputMismatchException e) {
				sc.nextLine();
				continue;
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

	private static Object applyChoice(int choice, LinkedHashMap<Class<? extends Service<?,?>>, Method[]> servicesMethods, Scanner sc) {

		Class<? extends Service<?, ?>> usedClass = null;
		int counter = 1;

		for(Entry<Class<? extends Service<?, ?>>, Method[]> entry : servicesMethods.entrySet()) {	
			counter += entry.getValue().length;
			if(choice < counter) {
				usedClass = entry.getKey();
				counter -= entry.getValue().length;
				//We set the choice to the number of the method in the class
				choice -= counter;
				break;
			}			
		}

		Method chosenMethod = servicesMethods.get(usedClass)[choice];

		if(!chosenMethod.getAnnotation(ServiceMethod.class).forUser()) {

			switch(chosenMethod.getAnnotation(ServiceMethod.class).fullName()) {
			case "com.excilys.cdb.Service.getAll":
				return serviceGetAll(usedClass, chosenMethod, sc);
			default :
				logger.log(Level.ERROR, "The method " + chosenMethod.getName() + " is undefined in " + getMethodName());
				return "The method " + chosenMethod.getName() + " is undefined in " + getMethodName() + ". We couldn't execute your request.";
			}
		} else {

			Parameter[] params = chosenMethod.getParameters();

			Object[] parameters = new Object[0];

			parameters = AskParameters(params, sc).toArray();

			return applyServiceMethod(usedClass, chosenMethod, parameters);
		}
	}

	private static Object applyServiceMethod(Class<? extends Service<?,?>> usedClass, Method chosenMethod, Object[] parameters) {
		try {
			return chosenMethod.invoke(getServiceInstance(usedClass), parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.log(Level.ERROR, "Error in method " + getMethodName() + " : " + e.getMessage());
		}


		logger.log(Level.ERROR, "An error occured in " + getMethodName() + " using method " + chosenMethod.getName());
		return "An error occured in " + getMethodName() + " using method " + chosenMethod.getName() +". We couldn't execute your request.";
	}

	public static String getMethodName() {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		return ste[2].getMethodName();
	}

	private static Object serviceGetAll(Class<? extends Service<?, ?>> usedClass, Method chosenMethod, Scanner sc) {
		boolean keepGoing = true;
		long offset = 0; 
		long limit = 10;
		long max = getServiceInstance(usedClass).getCount();
		long maxPage = (long) Math.ceil(((double)max)/(double)limit);
		while(keepGoing && offset<max) {
			Object[] parameters = {offset, limit};
			long page = (offset/limit)+1;
			print("Elements " + offset+1 + " to " + Math.min(offset+limit, max) + " (page " + page + "/" + maxPage + ") :");
			print(applyServiceMethod(usedClass, chosenMethod, parameters).toString());

			boolean first = true;
			boolean invalidInput = true;
			int choice = -1;
			while(invalidInput && offset<max) {
				if(!first)
					print("Invalid input. Please enter a correct value");
				first = false;
				print("\nWhat do you want to do ? (exit(0), next(1), previous(2))");

				try {
					choice = sc.nextInt();
					if(choice < 3 && choice >= 0)
						invalidInput = false;
					else
						logger.log(Level.INFO, "Invalid input");
				}catch(InputMismatchException e) {
					logger.log(Level.INFO, "Invalid input : " + e.getMessage());
				}
				sc.nextLine();
			}
			switch(choice)
			{
			case 0:
				keepGoing = false;
				break;
			case 1:
				offset += limit;
				break;
			case 2:
				offset -= limit;
				if(offset < 0) {
					offset += limit;
				}
				break;
			}
		}
		return "\n Ended.";
	}

	private static Service<?, ?> getServiceInstance(Class<? extends Service<?,?>> serviceClass){

		Service<?,?> service = null;

		Method method = null;
		try {
			method = serviceClass.getMethod("getInstance");
		} catch (NoSuchMethodException | SecurityException e) {
			logger.log(Level.ERROR, "Error in method " + getMethodName() + " : " + e.getMessage());
		}

		try {
			service = (Service<?,?>) method.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.log(Level.ERROR, "Error in method " + getMethodName() + " : " + e.getMessage());
		}

		return service;
	}

	private static List<?> AskParameters(Parameter[] params, Scanner sc) {
		Class<?>[] acceptedTypes = { long.class, Long.class, LocalDate.class, String.class };
		ArrayList<Class<?>> acceptedTypesList = new ArrayList<>(Arrays.asList(acceptedTypes));

		LinkedList<Object> parameters = new LinkedList<>();

		for(Parameter param : params) {

			String name = param.getName();
			boolean optional = false;

			if(param.isAnnotationPresent(ParamDescription.class)) {
				name = param.getAnnotation(ParamDescription.class).name();
				optional = param.getAnnotation(ParamDescription.class).optional();
			}

			Object defaultReturn = new Object();
			Object result = defaultReturn;
			boolean firstTime = true;

			while(result == defaultReturn) {
				try {
					if(!firstTime)
						print("Entry error - please try again...");
					firstTime = false;
					if(optional){
						print("The " + name + " is optional, do you wan't to set it ? (0 - yes, 1 - no)");
						long l = sc.nextLong();
						sc.nextLine();
						if(l == 0) {
							optional = false;
						}else if(l == 1) {
							optional = true;
						}else {
							continue;
						}
					}
					if(!optional) {
						print("Enter the " + name + " :");
						if(acceptedTypesList.contains(param.getType())) {
							result = AskParameter(param, sc);
						} else {
							result = AskOtherParameter(parameters, param, sc);
						}
					} else {
						result = null;
					}

				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | InputMismatchException e) {
					if(sc.hasNextLine())
						sc.nextLine();
					logger.log(Level.ERROR, "Error in method " + getMethodName() + e.getMessage() == null ? "" : " : " + e.getMessage());
					result = defaultReturn;
				}
			}
			parameters.add(result);
		}
		return parameters;
	}

	private static Object AskOtherParameter(LinkedList<Object> parameters, Parameter param, Scanner sc) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> type = param.getType();
		Constructor<?>[] cs = type.getConstructors();
		Constructor<?> maxArgs = cs[0];

		for(Constructor<?> c : cs) {
			if(c.getParameterCount() > maxArgs.getParameterCount())
				maxArgs = c;
		}
		return maxArgs.newInstance(AskParameters(maxArgs.getParameters(), sc).toArray());
	}

	private static Object AskParameter(Parameter param, Scanner sc) throws InputMismatchException {
		try {
			if(param.getType() == long.class || param.getType() == Long.class) {
				long l = sc.nextLong();
				sc.nextLine();

				return l;

			} else if(param.getType() == LocalDate.class) {
				int year = 0;
				Month month = Month.of(1);
				int dayOfMonth = 0;

				try {
					print("Enter the year :");
					year = sc.nextInt();
					sc.nextLine();

					print("Enter the month :");
					month = Month.of(sc.nextInt());
					sc.nextLine();

					print("Enter the day :");
					dayOfMonth = sc.nextInt();
					sc.nextLine();
				}catch(InputMismatchException |DateTimeException e) {
					sc.nextLine();
					throw new InputMismatchException("The input is invalid for a date");
				}

				return LocalDate.of(year, month, dayOfMonth);
			} else if(param.getType() == String.class) {
				return sc.nextLine();
			}

			return null;
		}catch(InputMismatchException e) {
			if(e.getMessage() == null) {
				sc.nextLine();
				throw new InputMismatchException("The input is invalid for the type " + param.getType());
			}
			else {
				throw e;
			}
		}
	}

	private static void print(String s) {
		System.out.println(s);
	}

	private static int PrintChoices(ArrayList<Class<? extends Service<?, ?>>> services, LinkedHashMap<Class<? extends Service<?, ?>>, Method[]> servicesMethods) {
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

	private static LinkedHashMap<Class<? extends Service<?, ?>>, Method[]> getSQLMethods(ArrayList<Class<? extends Service<?, ?>>> services){

		LinkedHashMap<Class<? extends Service<?, ?>>, Method[]> result = new LinkedHashMap<>();

		for(Class<? extends Service<?, ?>> currentClass : services) {
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
