package com.employee.entity;

import java.util.Date;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "employee")
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_id")
	private Integer employeeId;

	@Column(name = "Role_id", nullable = true)
	private Integer roleId;

	@Column(name = "job_title_id", nullable = true)
	private Integer jobTitleId;

	@NotBlank(message = "姓名不能為空")
	@Pattern(regexp = "^[\u4e00-\u9fff]+$", message = "姓名只能包含中文字符")
	@Size(max = 50, message = "姓名長度不能超過50個字符")
	@Column(name = "Name", nullable = false, length = 50)
	private String name;

	@Column(name = "Status", nullable = false)
	private Boolean status = true;

	@NotNull(message = "建立日期不能為空")
	@Column(name = "Created_date", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date createdDate;

	@NotBlank(message = "密碼不能為空")
	@Size(min = 4, max = 12, message = "密碼長度須為4-12個字符")
	@Column(name = "Password", nullable = false, length = 50)
	private String password;

	@Lob
	@Column(name = "employee_photo")
	private byte[] employeePhoto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Role_id", insertable = false, updatable = false)
	private Role role;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_title_id", insertable = false, updatable = false)
	private JobTitle jobTitle;

	public Employee() {}

	public Employee(String name, Integer roleId, Integer jobTitleId, String password) {
		this.name = name;
		this.roleId = roleId;
		this.jobTitleId = jobTitleId;
		this.password = password;
		this.status = true;
		this.createdDate = new Date();
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getJobTitleId() {
		return jobTitleId;
	}

	public void setJobTitleId(Integer jobTitleId) {
		this.jobTitleId = jobTitleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public byte[] getEmployeePhoto() {
		return employeePhoto;
	}

	public void setEmployeePhoto(byte[] employeePhoto) {
		this.employeePhoto = employeePhoto;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public JobTitle getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(JobTitle jobTitle) {
		this.jobTitle = jobTitle;
	}

	@Override
	public String toString() {
		return "Employee{" +
				"employeeId=" + employeeId +
				", name='" + name + '\'' +
				", roleId=" + roleId +
				", jobTitleId=" + jobTitleId +
				", status=" + status +
				", createdDate=" + createdDate +
				'}';
	}
}
