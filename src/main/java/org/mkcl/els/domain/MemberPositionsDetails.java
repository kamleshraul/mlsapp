package org.mkcl.els.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Entity
@Table(name="member_positions")
@JsonIgnoreProperties({"member"})
public class MemberPositionsDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="member_id")
	private MemberDetails member;
	
	@Column(length=30)
	private String period;
	
	@Column(length=10000)
	private String details;

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MemberDetails getMember() {
		return member;
	}

	public void setMember(MemberDetails member) {
		this.member = member;
	}

	public MemberPositionsDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
