package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="adjournment_reasons")
@JsonIgnoreProperties({""})
public class AdjournmentReason extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(length=1000)
	private String reason;
	

	/**** Constuctor ****/
	public AdjournmentReason() {
		super();
	}
	
	/**** Domain Methods ****/

	/**** Getters and Setters ****/
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
