package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.domain.associations.MemberDepartmentAssociation;
import org.springframework.beans.factory.annotation.Configurable;
@Configurable
@Entity
@Table(name = "departments")
public class Department extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The type. */
    @Column(length = 1000)
    @NotEmpty
    private String name;

    /** The house type. */
    @ManyToOne
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<MemberDepartmentAssociation> memberDepartmentAssociations;

	// ---------------------------------Constructors----------------------------------------------
    public Department() {
		super();
	}

	public Department(final String name, final Ministry ministry) {
		super();
		this.name = name;
		this.ministry = ministry;
	}
	// -------------------------------Domain_Methods----------------------------------------------

	// ------------------------------------------Getters/Setters-------------------------------

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Ministry getMinistry() {
		return ministry;
	}

	public void setMinistry(final Ministry ministry) {
		this.ministry = ministry;
	}


    public List<MemberDepartmentAssociation> getMemberDepartmentAssociations() {
        return memberDepartmentAssociations;
    }


    public void setMemberDepartmentAssociations(
            final List<MemberDepartmentAssociation> memberDepartmentAssociations) {
        this.memberDepartmentAssociations = memberDepartmentAssociations;
    }

}
