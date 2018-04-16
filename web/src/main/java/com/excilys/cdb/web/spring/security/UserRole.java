package com.excilys.cdb.web.spring.security;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class UserRole {
	private String role;
 	
 	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
 	@JoinColumn(name = "username")
 	private User user;
	@Id
 	@GeneratedValue
 	@Column(name = "user_role_id")
 	private int userRoleId;

	public UserRole() {
		this.role = "ROLE_USER";
	}

	public UserRole(User user, String role) {
		this.user = user;
		this.role = role;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserRole other = (UserRole) obj;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (userRoleId != other.userRoleId)
			return false;
		return true;
	}

	public String getRole() {
		return role;
	}

	public User getUser() {
		return user;
	}

	public int getUserRoleId() {
		return userRoleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + userRoleId;
		return result;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUserRoleId(int userRoleId) {
		this.userRoleId = userRoleId;
	}
}
