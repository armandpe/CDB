package com.excilys.cdb.web.validator;


@FunctionalInterface
public interface ExceptionFunction<A, R, E extends Exception> {
	
	R apply(A a) throws E;
}
