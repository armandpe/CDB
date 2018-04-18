package com.excilys.cdb.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	private Boolean enabled;
 	
 	private String password;

	@Id
 	private String username;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
 	private List<UserRole> userRoles;

	public User() {
	}

	public User(String username, String password, Boolean enabled) {
 		this.username = username;
 		this.password = password;
 		this.enabled = enabled;
 	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (userRoles == null) {
			if (other.userRoles != null)
				return false;
		} else if (!userRoles.equals(other.userRoles))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public List<UserRole> getUserRoles() {
		return userRoles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((userRoles == null) ? 0 : userRoles.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}
 	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
 	
 	public void setPassword(String password) {
		this.password = password;
	}
 	
 	public void setUsername(String username) {
		this.username = username;
	}
 
	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}
}
