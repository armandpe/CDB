package com.excilys.cdb.service;

import java.util.Optional;
import java.util.Set;

import com.excilys.cdb.dao.FailedDAOOperationException;
import com.excilys.cdb.dao.UserDAO;
import com.excilys.cdb.dao.UserRoleDAO;
import com.excilys.cdb.model.User;
import com.excilys.cdb.model.UserRole;

@org.springframework.stereotype.Service
public class UserService {

	UserDAO userDAO;
	UserRoleDAO userRoleDAO;

	public UserService(UserDAO userDAO, UserRoleDAO userRoleDAO) {
		this.userDAO = userDAO;
		this.userRoleDAO = userRoleDAO;
	}

	public Optional<User> getByName(String name) {
		return userDAO.getByName(name);
	}

	public void create(User user) throws FailedDAOOperationException {
		UserRole userRoleDefault = new UserRole();
		userRoleDefault.setUser(user);
		Set<UserRole> roles = user.getUserRoles();
		roles.add(userRoleDefault);
		userDAO.create(user);
		for (UserRole userRole : roles) {
			userRoleDAO.create(userRole);
		}
	}
}
