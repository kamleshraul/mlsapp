package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "supportlog")
public class SupportLog extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**** Attributes ****/
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "support_credential_id", referencedColumnName = "id")
	private Credential supportCredential;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "user_credential_id", referencedColumnName = "id")
	private Credential userCredential;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeOfAction;
	
	/** IP Address of User **/
	private String userAddress;
	
	private boolean isEntered=false;
	
	/**** Attributes ****/
	
	/**** Constructor ****/
	public SupportLog() {
		super();
	}

	/**** Domain Methods ****/
	public static SupportLog logActivity(String supportUserName, String userAddress, String locale) 
	{
		if(ApplicationConstants.environment!=null && ApplicationConstants.environment.acceptsProfiles("prod")) 
		{
			Credential supportCredential = Credential.findByFieldName(Credential.class, "username", supportUserName, null);
			
			SupportLog supportLog = new SupportLog();	
			supportLog.setSupportCredential(supportCredential);
			supportLog.setTimeOfAction(new Date());
			supportLog.setUserAddress(userAddress);
			
			return (SupportLog)supportLog.persist();
		} 
		else {
			return null;
		}
	}
	
	public static SupportLog findLatest(final String userAddress) {
		List<SupportLog> supportLogs = SupportLog.findAllByFieldName(SupportLog.class, "userAddress", userAddress, "timeOfAction", ApplicationConstants.DESC, "");
		if(supportLogs!=null) {
			for(SupportLog supportLog: supportLogs) {
				if(!supportLog.isEntered) {
					return supportLog;
				}
			}
		}
		return null;
	}
	
	/**** Getter Setter ****/
	public Credential getSupportCredential() {
		return supportCredential;
	}

	public void setSupportCredential(Credential supportCredential) {
		this.supportCredential = supportCredential;
	}

	public Credential getUserCredential() {
		return userCredential;
	}

	public void setUserCredential(Credential userCredential) {
		this.userCredential = userCredential;
	}

	public Date getTimeOfAction() {
		return timeOfAction;
	}

	public void setTimeOfAction(Date timeOfAction) {
		this.timeOfAction = timeOfAction;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public boolean isEntered() {
		return isEntered;
	}

	public void setEntered(boolean isEntered) {
		this.isEntered = isEntered;
	}
}
