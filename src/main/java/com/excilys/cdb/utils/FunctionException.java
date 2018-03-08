package main.java.com.excilys.cdb.utils;

@FunctionalInterface
public interface FunctionException<T, R, E extends Exception> {
	R apply(T t) throws E;
}
