package com.excilys.cdb.utils;

@FunctionalInterface
public interface BiFunctionException<T, U, R, E extends Exception> {
	R apply(T t, U u) throws E;
}
