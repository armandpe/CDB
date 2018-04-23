package com.excilys.cdb.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "authorities")
public class UserRole {
 	@Column(name = "authority")
	private String authority;
 	
 	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
 	@JoinColumn(name = "username")
 	private User user;
 	
	@Id
 	@GeneratedValue(strategy = GenerationType.IDENTITY) 
 	@Column(name = "user_role_id")
 	private int userRoleId;

	public UserRole() {
		this.authority = "ROLE_USER";
	}

	public UserRole(User user, String role) {
		this.user = user;
		this.authority = role;
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
		if (authority == null) {
			if (other.authority != null)
				return false;
		} else if (!authority.equals(other.authority))
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
		return authority;
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
		result = prime * result + ((authority == null) ? 0 : authority.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + userRoleId;
		return result;
	}

	public void setRole(String role) {
		this.authority = role;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUserRoleId(int userRoleId) {
		this.userRoleId = userRoleId;
	}
}
