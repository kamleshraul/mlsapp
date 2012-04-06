package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="user")
public class User extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    private transient static final long serialVersionUID = 1L;

    @Column(length=300)
    private String title;

    @Column(length=300)
    private String firstName;

    @Column(length=300)
    private String middleName;

    @Column(length=300)
    private String lastName;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="credential_id")
    private Credential credential;

    @Autowired
    private transient UserRepository userRepository;

    // ---------------------------------Constructors----------------------------------------------

    public User() {
        super();
    }
    // ----------------Domain_Methods------------------------------------------
    public static UserRepository getUserRepository() {
        UserRepository userRepository = new User().userRepository;
        if (userRepository == null) {
            throw new IllegalStateException(
                    "UserRepository has not been injected in User Domain");
        }
        return userRepository;
    }

    public static User findByUserName(final String username,final String locale){
    	return getUserRepository().findByUserName(username, locale);
    }
    // ------------------------------------------Getters/Setters-----------------------------------

    public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(final String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public Credential getCredential() {
		return credential;
	}

	public void setCredential(final Credential credential) {
		this.credential = credential;
	}
}