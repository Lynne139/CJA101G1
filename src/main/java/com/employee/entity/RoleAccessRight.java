package com.employee.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "role_access_right")
public class RoleAccessRight {
	@EmbeddedId
	private RoleAccessRightId id;

	public RoleAccessRight() {
	}

	public RoleAccessRight(Integer roleId, Integer accessId) {
		this.id = new RoleAccessRightId(roleId, accessId);
	}

	public RoleAccessRightId getId() {
		return id;
	}

	public void setId(RoleAccessRightId id) {
		this.id = id;
	}

	public Integer getRoleId() {
		return id != null ? id.getRoleId() : null;
	}

	public void setRoleId(Integer roleId) {
		if (id == null)
			id = new RoleAccessRightId();
		id.setRoleId(roleId);
	}

	public Integer getAccessId() {
		return id != null ? id.getAccessId() : null;
	}

	public void setAccessId(Integer accessId) {
		if (id == null)
			id = new RoleAccessRightId();
		id.setAccessId(accessId);
	}
}