
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;
@Configurable
@Entity
@Table(name="qualifications")
public class Qualification extends BaseDomain implements Serializable{

	private transient static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="degree_id")
	private Degree degree;
	
	@Column(length=1000)
	private String details;

	public Qualification() {
		super();
	}

	public Qualification(Degree degree) {
		super();
		this.degree = degree;
	}

	public Degree getDegree() {
		return degree;
	}

	public void setDegree(Degree degree) {
		this.degree = degree;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}	
}
