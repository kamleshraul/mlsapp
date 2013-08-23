package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="vemp")
public class VEmployee extends BaseDomain  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(length=100)
	private String empName;
		
	@ManyToOne
	@JoinColumn(name="dept_id", referencedColumnName="id")
	private VDepartment departmet;
	
	@ManyToMany
	@JoinTable(name="vemp_projects", 
			joinColumns={@JoinColumn(name="emp_id", referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="prj_id", referencedColumnName="id")})
	private List<VProject> projects;

	public VEmployee() {
		super();
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public VDepartment getDepartmet() {
		return departmet;
	}

	public void setDepartmet(VDepartment departmet) {
		this.departmet = departmet;
	}

	public List<VProject> getProjects() {
		return projects;
	}

	public void setProjects(List<VProject> projects) {
		this.projects = projects;
	}	
}
