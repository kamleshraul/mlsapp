package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * Captures the configuration parameters for each device.
 * 
 * Even though the class extends BaseDomain, all the objects of the class
 * (and thus the corresponding entries in the database table) must be locale 
 * insensitive. 
 */
@Configurable
@Entity
@Table(name="device_config")
public class DeviceConfig extends BaseDomain implements Serializable {

	private static final long serialVersionUID = -6947818703664546029L;
	
	/**
	 * If a Device class is added in the future, change the type to
	 * Device.
	 */
	@Column(length=1000)
	private String device;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="role_id")
	private Role role;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="device_config_groups",
			joinColumns={@JoinColumn(name="device_config_id", referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="group_id", referencedColumnName="id")})
	private List<Group> groups;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="device_config_departments",
			joinColumns={@JoinColumn(name="device_config_id", referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="department_id", referencedColumnName="id")})
	private List<Department> departments;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="device_config_subdepartments",
			joinColumns={@JoinColumn(name="device_config_id", referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="subdepartment_id", referencedColumnName="id")})
	private List<SubDepartment> subDepartments;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	//=============== Constructor(s) ===============
	public DeviceConfig() {
		super();
	}

	//=============== Getter & Setters ===============
	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public List<SubDepartment> getSubDepartments() {
		return subDepartments;
	}

	public void setSubDepartments(List<SubDepartment> subDepartments) {
		this.subDepartments = subDepartments;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
}
