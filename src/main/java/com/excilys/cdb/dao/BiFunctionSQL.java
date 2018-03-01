package main.java.com.excilys.cdb.dao;

import java.sql.SQLException;

@FunctionalInterface
public interface BiFunctionSQL<T, U, R> {
	R apply(T t, U u) throws SQLException;
}
