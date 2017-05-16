
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Citizen.
 *
 * @author Rajeshs
 */
@Configurable
@Entity
@Table(name="citizens")
@JsonIgnoreProperties()
public class Citizen extends BaseDomain implements Serializable {

	// ---------------------------------Attributes------------------------------------------
	/** The Constant serialVersionUID. */
	private transient static final long serialVersionUID = 1L;

	/** The first name. */
	@Column(length=300)
	private String name;
	
    /** The mobile. */
    @Column(length = 1000)
    private String mobile;

    /** The email. */
    @Column(length = 1000)
    private String email;    

	/** The Citizen repository. */
	@Autowired
	private transient CitizenRepository citizenRepository;
	

	// ---------------------------------Constructors----------------------------------------------
	/**
	 * Instantiates a new user.
	 */
	public Citizen() {
		super();

	}
	
	
	// ---------------------------------Domain Methods----------------------------------------------
	/**
	 * Gets the member repository.
	 *
	 * @return the member repository
	 */
	public static CitizenRepository getCitizenRepository() {
		CitizenRepository citizenRepository = new Citizen().citizenRepository;
		if (citizenRepository == null) {
			throw new IllegalStateException(
					"MemberRepository has not been injected in Member Domain");
		}
		return citizenRepository;
	}
	
	public static Citizen AddCitizen(final String name,
			final String mobile,final String email,final String locale) throws ELSException {
		return getCitizenRepository().AddCitizen(name,mobile,email,locale);
		
		
	}

	
	// ---------------------------------Getters and Setters----------------------------------------------
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(final String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

}