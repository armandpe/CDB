package main.java.com.excilys.cdb.ihm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
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
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.cdb.Main;
import main.java.com.excilys.cdb.ParamDescription;
import main.java.com.excilys.cdb.dao.FailedDAOOperationException;
import main.java.com.excilys.cdb.model.ModelClass;
import main.java.com.excilys.cdb.pagemanager.PageManagerLimit;
import main.java.com.excilys.cdb.service.CompanyService;
import main.java.com.excilys.cdb.service.ComputerService;
import main.java.com.excilys.cdb.service.Service;
import main.java.com.excilys.cdb.service.ServiceClass;
import main.java.com.excilys.cdb.service.ServiceMethod;

public class CLI {

	static final Logger LOGGER = LoggerFactory.getLogger(CLI.class);
	static ArrayList<Class<? extends Service<?, ?>>> services = new ArrayList<>();
	
	public static void start() {
		
		services.add(ComputerService.class);
		services.add(CompanyService.class);

		boolean continuer = true;
		Scanner sc = new Scanner(System.in);
		LinkedHashMap<Class<? extends Service<?, ?>>, Method[]> servicesMethods = getSQLMethods(services);

		while (continuer) {
			int maxChoice = printChoices(services, servicesMethods);
			int choice = -1;
			try {
				choice = sc.nextInt();
				sc.nextLine();
			} catch (InputMismatchException e) {
				sc.nextLine();
				continue;
			}
			if (choice == 0) {
				break;
			}

			if (choice > maxChoice || choice < 0) {
				print("Invalid input - Please try again ...");
				continue;
			}
			applyChoice(choice, servicesMethods, sc);
			print("\n Press ENTER");
			sc.nextLine();
			for (int i = 0; i < 10; ++i) {
				print("");
			}
		}
		print("Goodbye ! :) ");
		sc.close();
	}

	private static Object applyChoice(int choice,
			LinkedHashMap<Class<? extends Service<?, ?>>, Method[]> servicesMethods, Scanner sc) {

		Class<? extends Service<?, ?>> usedClass = null;
		int counter = 1;

		for (Entry<Class<? extends Service<?, ?>>, Method[]> entry : servicesMethods.entrySet()) {
			counter += entry.getValue().length;
			if (choice < counter) {
				usedClass = entry.getKey();
				counter -= entry.getValue().length;
				// We set the choice to the number of the method in the class
				choice -= counter;
				break;
			}
		}

		Method chosenMethod = servicesMethods.get(usedClass)[choice];

		if (!chosenMethod.getAnnotation(ServiceMethod.class).forUser()) {

			switch (chosenMethod.getAnnotation(ServiceMethod.class).fullName()) {
			case "com.excilys.cdb.Service.getAll":
				try {
					return serviceGetAll(usedClass, chosenMethod, sc);
				} catch (FailedDAOOperationException e) {
					return "Failure";
				}
			default:
				LOGGER.error(Main.getErrorMessage("the method " + chosenMethod.getName() + "is undefined", null));
				return "The method " + chosenMethod.getName() + " is undefined. We couldn't execute your request.";
			}
		} else {

			Parameter[] params = chosenMethod.getParameters();

			Object[] parameters = new Object[0];

			parameters = askParameters(params, sc).toArray();

			return applyServiceMethod(usedClass, chosenMethod, parameters);
		}
	}

	private static Object applyServiceMethod(Class<? extends Service<?, ?>> usedClass, Method chosenMethod,
			Object[] parameters) {
		try {
			return chosenMethod.invoke(getServiceInstance(usedClass), parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error(Main.getErrorMessage(null, e.getMessage()));
		}

		LOGGER.error(Main.getErrorMessage("error using method " + chosenMethod.getName(), null));
		return null;
	}

	private static Object askOtherParameter(LinkedList<Object> parameters, Parameter param, Scanner sc)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> type = param.getType();
		
		boolean isOptional = false;
		if (type == Optional.class) {
			type = (Class<?>) ((ParameterizedType) param.getParameterizedType()).getActualTypeArguments()[0];
			isOptional = true;
		}
		
		Constructor<?>[] cs = type.getConstructors();
		Constructor<?> maxArgs = cs[0];

		for (Constructor<?> c : cs) {
			if (c.getParameterCount() > maxArgs.getParameterCount()) {
				maxArgs = c;
			}
		}
		Object result = maxArgs.newInstance(askParameters(maxArgs.getParameters(), sc).toArray());
		
		return isOptional ? Optional.ofNullable(result) : result;
	}

	private static Object askParameter(Parameter param, Scanner sc) throws InputMismatchException {
		try {

			Object result = null;

			Class<?> paramClass = param.getType();
			boolean isOptional = false;

			if (paramClass == Optional.class) {
				paramClass = (Class<?>) ((ParameterizedType) param.getParameterizedType()).getActualTypeArguments()[0];
				isOptional = true;
			}

			if (paramClass == long.class || paramClass == Long.class) {
				long l = sc.nextLong();
				sc.nextLine();

				result = l;

			} else if (paramClass == LocalDate.class) {
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
				} catch (InputMismatchException | DateTimeException e) {
					sc.nextLine();
					throw new InputMismatchException("The input is invalid for a date");
				}

				result = LocalDate.of(year, month, dayOfMonth);
			} else if (paramClass == String.class) {
				result = sc.nextLine();
			} else {
				LOGGER.error(Main.getErrorMessage("class not implemented : " + paramClass.getName(), null));
				throw new IllegalArgumentException("Class not implemented : " + paramClass.getName());
			}

			if (isOptional) {
				result = Optional.ofNullable(result);
			}

			return result;
		} catch (InputMismatchException e) {
			if (e.getMessage() == null) {
				sc.nextLine();
				throw new InputMismatchException("The input is invalid for the type " + param.getType());
			} else {
				throw e;
			}
		}
	}

	private static List<?> askParameters(Parameter[] params, Scanner sc) {
		Class<?>[] acceptedTypes = {long.class, Long.class, LocalDate.class, String.class};
		ArrayList<Class<?>> acceptedTypesList = new ArrayList<>(Arrays.asList(acceptedTypes));

		LinkedList<Object> parameters = new LinkedList<>();

		for (Parameter param : params) {

			String name = param.getName();
			boolean optional = false;
			
			Class<?> paramClass = param.getType();			

			if (param.isAnnotationPresent(ParamDescription.class)) {
				name = param.getAnnotation(ParamDescription.class).name();
				optional = param.getAnnotation(ParamDescription.class).optional();
				if (optional) {
					paramClass = (Class<?>) ((ParameterizedType) param.getParameterizedType()).getActualTypeArguments()[0];
				}
			}

			Object defaultReturn = new Object();
			Object result = defaultReturn;
			boolean firstTime = true;

			while (result == defaultReturn) {
				try {
					if (!firstTime) {
						print("Entry error - please try again...");
					}
					firstTime = false;
					if (optional) {
						print("The " + name + " is optional, do you wan't to set it ? (0 - yes, 1 - no)");
						long l = sc.nextLong();
						sc.nextLine();
						if (l == 0) {
							optional = false;
						} else if (l == 1) {
							optional = true;
						} else {
							continue;
						}
					}
					if (!optional) {
						print("Enter the " + name + " :");
						if (acceptedTypesList.contains(paramClass)) {
							result = askParameter(param, sc);
						} else {
							result = askOtherParameter(parameters, param, sc);
						}
					} else {
						result = Optional.empty();
					}

				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | InputMismatchException e) {
					LOGGER.error(Main.getErrorMessage(null, e.getMessage()));
					result = defaultReturn;
				}
			}
			parameters.add(result);
		}
		return parameters;
	}

	@SuppressWarnings("unchecked")
	private static <T extends ModelClass> Service<T, ?> getServiceInstance(Class<? extends Service<T, ?>> serviceClass) {

		Service<T, ?> service = null;

		Method method = null;
		try {
			method = serviceClass.getMethod("getInstance");
		} catch (NoSuchMethodException | SecurityException e) {
			LOGGER.error(Main.getErrorMessage("reflexion error getting getInstance", e.getMessage()));
		}

		try {
			service = (Service<T, ?>) method.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error(Main.getErrorMessage("reflexion error invoking getInstance", e.getMessage()));
		}

		return service;
	}

	private static LinkedHashMap<Class<? extends Service<?, ?>>, Method[]> getSQLMethods(
			ArrayList<Class<? extends Service<?, ?>>> services) {

		LinkedHashMap<Class<? extends Service<?, ?>>, Method[]> result = new LinkedHashMap<>();

		for (Class<? extends Service<?, ?>> currentClass : services) {
			Set<Method> toAdd = new HashSet<>();

			for (Method method : currentClass.getMethods()) {
				if (method.isAnnotationPresent(ServiceMethod.class)) {
					toAdd.add(method);
				}
			}

			result.put(currentClass, toAdd.toArray(new Method[toAdd.size()]));
		}

		return result;
	}

	private static void print(String s) {
		System.out.println(s);
	}

	private static int printChoices(ArrayList<Class<? extends Service<?, ?>>> services,
			LinkedHashMap<Class<? extends Service<?, ?>>, Method[]> servicesMethods) {
		print("Here is the command list :");
		print("\t0 - Exit the program");
		int i = 0;
		for (Class<?> serviceClass : services) {
			print("For " + serviceClass.getAnnotation(ServiceClass.class).name() + " : ");

			for (Method method : servicesMethods.get(serviceClass)) {
				print("\t" + ++i + " - " + method.getAnnotation(ServiceMethod.class).name());
			}
			print("");
		}

		print("Enter the function code you want to use (0 to exit) :");

		return i;
	}

	private static <T extends ModelClass> Object serviceGetAll(Class<? extends Service<T, ?>> usedClass, Method chosenMethod, Scanner sc) throws FailedDAOOperationException {
		boolean keepGoing = true;
		Service<T, ?> myService = getServiceInstance(usedClass);
		PageManagerLimit<T> pageManager;
		
		pageManager = new PageManagerLimit<T>(search -> myService.getCount(search),  (x, y) -> myService.getAll(x, y));
		
		while (keepGoing) {
			ArrayList<T> data = pageManager.getPageData();
			print("Page " + pageManager.getPage() + "/" + pageManager.getMaxPage() + " :");
			data.forEach(element -> print("\t- " + element.toString() + "\n\r"));

			boolean first = true;
			boolean invalidInput = true;
			int choice = -1;
			ArrayList<Method> methods = new ArrayList<>();
			for (Method method : PageManagerLimit.class.getMethods()) {
				if (method.isAnnotationPresent(UserChoice.class)) {
					methods.add(method);
				}
			}

			while (invalidInput) {
				if (!first) {
					print("Invalid input. Please enter a correct value");
				}
				first = false;
				print("\nWhat do you want to do ?");
				print("\t0 - Exit");
				int i = 1;
				for (Method method : methods) {
					print("\t" + i++ + " - " + method.getAnnotation(UserChoice.class).name());
				}

				try {
					choice = sc.nextInt();
					if (choice < 5 && choice >= 0) {
						invalidInput = false;
					} else {
						LOGGER.info(Main.getErrorMessage("Invalid input", null));
					}
				} catch (InputMismatchException e) {
					LOGGER.info(Main.getErrorMessage("Invalid input", e.getMessage()));
				}
				sc.nextLine();
			}
			if (choice == 0) {
				keepGoing = false;
				break;
			} else {
				try {
					if (!(boolean) methods.get(choice - 1).invoke(pageManager, new Object[0])) {
						print("Request failure");
					} else {
						print(pageManager.getPageData().toString());
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					LOGGER.error(Main.getErrorMessage(null, e.getMessage()));
					print("Request failure");
				}
			}
		}
		return "\n Ended.";
	}

}
