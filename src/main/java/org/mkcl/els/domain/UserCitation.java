package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.repository.UserCitationRepository;
import org.mkcl.els.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name="user_citations")
public class UserCitation extends BaseDomain implements Serializable{


    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The device type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="devicetype_id")
    private DeviceType deviceType;

    /** The text. */
    @Column(length=30000)
    private String text;

    /** The status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="status_id")
    private Status status;  
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id")
    private Credential credential;
    
    @Autowired
    private transient UserCitationRepository userCitationRepository;

	/*** Constructors and Private Method ***/
    
    public UserCitation() {
		super();
	}

	public UserCitation(DeviceType deviceType, String text, Status status, Credential credential) {
		super();
		this.deviceType = deviceType;
		this.text = text;
		this.status = status;
		this.credential = credential;
	}

	public static UserCitationRepository getUserCitationRepository() {
		UserCitationRepository userCitationRepository = new UserCitation().userCitationRepository;
        if (userCitationRepository == null) {
            throw new IllegalStateException(
                    "UserCitationRepository has not been injected in UserCitation Domain");
        }
        return userCitationRepository;
    }
	
	public static List<UserCitation> findByDeviceTypeAndCredential(DeviceType deviceType, Credential credential) {
		return getUserCitationRepository().findByDeviceTypeAndCredential(deviceType,credential);
	}
	/*** Getters and Setters ***/
	
	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Credential getCredential() {
		return credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}
    
}
