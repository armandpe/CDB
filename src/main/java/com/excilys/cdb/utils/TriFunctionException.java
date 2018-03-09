package main.java.com.excilys.cdb.utils;

@FunctionalInterface
public interface TriFunctionException<T, U, V, R, E extends Exception> {

	R apply(T t, U u, V v) throws E;
	
}
