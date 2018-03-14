package com.excilys.cdb.utils;

@FunctionalInterface
public interface ConsumerException<T, E extends Exception> {
	void accept(T t) throws E;
}
