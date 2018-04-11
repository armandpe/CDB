package com.excilys.cdb.model;

public interface ModelClass {
	default String getClassName() {
		return getClass().getName();
	}
}
