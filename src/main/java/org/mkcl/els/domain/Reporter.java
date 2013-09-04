package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="reporters")
@JsonIgnoreProperties({})
public class Reporter extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	private User user;
	
	private Integer position;
	
	private Boolean isActive;
	
	/**** Constructor ****/
	public Reporter() {
		super();
	}
	
	/**** Domain Methods ****/
	
	/**** Getters and Setters ****/
	public void setUser(User user) {
		this.user = user;
	}	

	public User getUser() {
		return user;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getPosition() {
		return position;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsActive() {
		return isActive;
	}
}
