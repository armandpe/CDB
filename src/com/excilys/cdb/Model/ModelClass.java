package com.excilys.cdb.Model;

public interface ModelClass {
	default String getClassName() {
		return getClass().getName();
	}
}
