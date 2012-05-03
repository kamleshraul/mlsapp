package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Cascade;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="members_ministries") //The name is kept such so as to avoid collision
                               // with a prior existing table with similar name
@JsonIgnoreProperties({"member", "designation", "memberDepartments"})
public class MemberMinister extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="designation_id")
    private Designation designation;

    @Temporal(TemporalType.DATE)
    private Date oathDate;

    @Temporal(TemporalType.DATE)
    private Date resignationDate;

    @Column(length=1500)
    private String minister;

    @Temporal(TemporalType.DATE)
    private Date ministryFromDate;

    @Temporal(TemporalType.DATE)
    private Date ministryToDate;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="member_minister_id", referencedColumnName="id")
    private List<MemberDepartment> memberDepartments;

    //------------------ Constructor ----------------------//
    public MemberMinister() {
        super();
    }


    //------------------ Getters & Setters ----------------------//
    public Member getMember() {
        return member;
    }


    public void setMember(final Member member) {
        this.member = member;
    }


    public Designation getDesignation() {
        return designation;
    }


    public void setDesignation(final Designation designation) {
        this.designation = designation;
    }


    public Date getOathDate() {
        return oathDate;
    }


    public void setOathDate(final Date oathDate) {
        this.oathDate = oathDate;
    }


    public Date getResignationDate() {
        return resignationDate;
    }


    public void setResignationDate(final Date resignationDate) {
        this.resignationDate = resignationDate;
    }


    public String getMinister() {
        return minister;
    }


    public void setMinister(final String minister) {
        this.minister = minister;
    }


    public Date getMinistryFromDate() {
        return ministryFromDate;
    }


    public void setMinistryFromDate(final Date ministryFromDate) {
        this.ministryFromDate = ministryFromDate;
    }


    public Date getMinistryToDate() {
        return ministryToDate;
    }


    public void setMinistryToDate(final Date ministryToDate) {
        this.ministryToDate = ministryToDate;
    }


	public List<MemberDepartment> getMemberDepartments() {
		return memberDepartments;
	}


	public void setMemberDepartments(List<MemberDepartment> memberDepartments) {
		this.memberDepartments = memberDepartments;
	}
}
