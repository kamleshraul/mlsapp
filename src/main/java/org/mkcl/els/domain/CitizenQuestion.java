/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.User.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.CitizenQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Citizen.
 *
 * @author Rajeshs
 */
@Configurable
@Entity
@Table(name="citizenquestions")
@JsonIgnoreProperties()
public class CitizenQuestion extends BaseDomain implements Serializable {



	// ---------------------------------Attributes------------------------------------------
	/** The Constant serialVersionUID. */
	private transient static final long serialVersionUID = 1L;


	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="citizen_id")
	private Citizen citizen;
	
    /** The mobile. */
    @Column(length = 1000)
    private String questionText;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;
	
    /** The department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;
    
    /** The department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "constituency_id")
    private Constituency constituency;
    
    /** The department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    /** The creation date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

	/** The Citizen repository. */
	@Autowired
	private transient CitizenQuestionRepository citizenQuestionRepository;
	

	// ---------------------------------Constructors----------------------------------------------
	/**
	 * Gets the member repository.
	 *
	 * @return the member repository
	 */
	public static CitizenQuestionRepository getCitizenQuestionRepository() {
		CitizenQuestionRepository citizenQuestionRepository = new CitizenQuestion().citizenQuestionRepository;
		if (citizenQuestionRepository == null) {
			throw new IllegalStateException(
					"MemberRepository has not been injected in Member Domain");
		}
		return citizenQuestionRepository;
	}

	
	

	public Citizen getCitizen() {
		return citizen;
	}




	public void setCitizen(Citizen citizen) {
		this.citizen = citizen;
	}




	public String getQuestionText() {
		return questionText;
	}




	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}




	public Member getMember() {
		return member;
	}




	public void setMember(Member member) {
		this.member = member;
	}




	public District getDistrict() {
		return district;
	}




	public void setDistrict(District district) {
		this.district = district;
	}




	public Constituency getConstituency() {
		return constituency;
	}




	public void setConstituency(Constituency constituency) {
		this.constituency = constituency;
	}




	public Department getDepartment() {
		return department;
	}




	public void setDepartment(Department department) {
		this.department = department;
	}




	public Date getCreationDate() {
		return creationDate;
	}




	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}




	public static String AddCitizenQuestion(final String citizenID,final String districtID,final String constituencyID,final String departmentID,
			final String questionText,final String memberID,final String locale) throws ELSException {
		 return getCitizenQuestionRepository().AddCitizenQuestion(citizenID,districtID,constituencyID,departmentID,questionText,memberID,locale);
		
	}

}