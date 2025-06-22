package com.employee.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class RoleAccessRightId implements Serializable {
	@Column(name = "Role_id")
	private Integer roleId;

	@Column(name = "Access_id")
	private Integer accessId;

	public RoleAccessRightId() {
	}

	public RoleAccessRightId(Integer roleId, Integer accessId) {
		this.roleId = roleId;
		this.accessId = accessId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getAccessId() {
		return accessId;
	}

	public void setAccessId(Integer accessId) {
		this.accessId = accessId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		RoleAccessRightId that = (RoleAccessRightId) o;
		return Objects.equals(roleId, that.roleId) && Objects.equals(accessId, that.accessId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(roleId, accessId);
	}
}