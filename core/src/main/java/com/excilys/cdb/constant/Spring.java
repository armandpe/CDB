package com.excilys.cdb.constant;

public final class Spring {
	public static final String PATH = "/WEB-INF/views/";
	public static final String EXTENSION = ".jsp";

	public static final String NAME_ADD = "addComputer";
	public static final String NAME_EDIT = "editComputer";
	public static final String NAME_DASHBOARD = "dashboard";
	public static final String NAME_LOGIN = "login";
	public static final String NAME_LOGOUT = "logout";
	public static final String NAME_REGISTER = "register";
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
	public static final String ORDER = "order";
	public static final String COMPANY_LIST = "companyList";
	public static final String COMPUTER_NAME = "computerName";
	public static final String INTRODUCED = "introduced";
	public static final String DISCONTINUED = "discontinued";
	public static final String COMPANY_ID = "company_id";
	public static final String ID = "id";
	public static final String COMPUTER_DTO = "computerDTO";
	public static final String USER = "user";

	
	//Request attributes
	public static final String PAGE_DATA = "pageData";
	public static final String ERRORS = "errors";

	//Request both
	public static final String SEARCH = "search";
	
	public static final String ORDER_BY_ASC = "asc";
	public static final String ORDER_BY_DESC = "desc";
	
	public static final String ORDER_BY_NAME = "name";
	public static final String ORDER_BY_INTRODUCED = "introduced";
	public static final String ORDER_BY_DISCONTINUED = "discontinued";
	public static final String ORDER_BY_COMPANY_NAME = "company";
	
	public static final long DEFAULT_LIMIT_VALUE = 10;
	public static final String DEFAULT_LIMIT = String.valueOf(DEFAULT_LIMIT_VALUE);
	public static final String DEFAULT_SEARCH = "";
	public static final long DEFAULT_PAGE_VALUE = 1;
	public static final String DEFAULT_PAGE = String.valueOf(DEFAULT_PAGE_VALUE);
	public static final String DEFAULT_ORDER_BY = ORDER_BY_NAME;
	public static final String DEFAULT_ORDER = ORDER_BY_ASC;
	
	
}
