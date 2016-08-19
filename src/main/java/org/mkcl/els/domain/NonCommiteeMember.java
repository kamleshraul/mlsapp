package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.NonCommitteeMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="NonCommiteeMembers")
@JsonIgnoreProperties({"noncommiteememberinformation","committeeMeeting","nonCommitteeMemberType"})
public class NonCommiteeMember extends BaseDomain implements Serializable{
	
	private static final long serialVersionUID = -717179637452424318L;

	
	@Column(length=1000)
	private String departmentName;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="NonCommitteeMemberType_id")
	private NonCommitteeMemberType nonCommitteeMemberType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CommitteeMeeting_id")
	private CommitteeMeeting committeeMeeting;
	
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="NonCommiteeMember_NonCommiteeMember_information",
    joinColumns={@JoinColumn(name="noncommiteemember_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="noncommiteemember_information_id", referencedColumnName="id")})
	private List<NonCommiteeMemberInformation> noncommiteememberinformation;
	
	
	@Autowired
	private transient NonCommitteeMemberRepository repository;
	
	
	//=============== INTERNAL METHODS =========
	private static NonCommitteeMemberRepository getRepository() {
		NonCommitteeMemberRepository repository = new NonCommiteeMember().repository;
		
		if(repository == null) {
			throw new IllegalStateException("PrashnavaliRepository has not been injected in Prashnavali Domain");
		}
		
		return repository;
	}
	
	public NonCommiteeMember() {
		super();
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}



	public NonCommitteeMemberType getNonCommitteeMemberType() {
		return nonCommitteeMemberType;
	}

	public void setNonCommitteeMemberType(
			NonCommitteeMemberType nonCommitteeMemberType) {
		this.nonCommitteeMemberType = nonCommitteeMemberType;
	}

	public CommitteeMeeting getCommitteeMeeting() {
		return committeeMeeting;
	}

	public void setCommitteeMeeting(CommitteeMeeting committeeMeeting) {
		this.committeeMeeting = committeeMeeting;
	}

	public List<NonCommiteeMemberInformation> getNoncommiteememberinformation() {
		return noncommiteememberinformation;
	}

	public void setNoncommiteememberinformation(
			List<NonCommiteeMemberInformation> noncommiteememberinformation) {
		this.noncommiteememberinformation = noncommiteememberinformation;
	}
	


	
}
