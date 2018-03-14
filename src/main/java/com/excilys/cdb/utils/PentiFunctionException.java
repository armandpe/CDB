package com.excilys.cdb.utils;

@FunctionalInterface
public interface PentiFunctionException<T, U, V, W, X, R, E extends Exception> {

	R apply(T t, U u, V v, W w, X x) throws E;
	
}
