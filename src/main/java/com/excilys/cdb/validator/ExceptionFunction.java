package main.java.com.excilys.cdb.validator;


@FunctionalInterface
public interface ExceptionFunction<A, R, E extends Exception> {
	
	R apply(A a) throws E;
}
