package com.excilys.cdb.constant;

public final class Servlet {
	public static final String PATH = "/WEB-INF/views/";
	public static final String EXTENSION = ".jsp";

	public static final String NAME_ADD = "addComputer";
	public static final String NAME_EDIT = "editComputer";
	public static final String NAME_DASHBOARD = "dashboard";
	public static final String NAME_403 = "403";
	public static final String NAME_404 = "404";
	public static final String NAME_500 = "500";

	public static final String PATH_ADD = PATH + NAME_ADD + EXTENSION;
	public static final String PATH_EDIT = PATH + NAME_EDIT + EXTENSION;
	public static final String PATH_DASHBOARD = PATH + NAME_DASHBOARD + EXTENSION;
	public static final String PATH_403 = PATH + NAME_403 + EXTENSION;
	public static final String PATH_404 = PATH + NAME_404 + EXTENSION;
	public static final String PATH_500 = PATH + NAME_500 + EXTENSION;
	
	//Request parameters
	public static final String LIMIT = "limit";
	public static final String PAGE = "page";
	public static final String ORDER_BY = "orderby";
	public static final String DELETE_SELECTION = "selection";
	
	//Request attributes
	public static final String PAGE_DATA = "pageData";
	public static final String ERRORS = "errors";

	//Request both
	public static final String SEARCH = "search";
	
	public static final String ORDER_BY_NAME = "name";
	public static final String ORDER_BY_INTRODUCED = "introduced";
	public static final String ORDER_BY_DISCONTINUED = "discontinued";
	public static final String ORDER_BY_COMPANY_NAME = "company";
}
