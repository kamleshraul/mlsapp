package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "activitylog")
public class ActivityLog extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**** Attributes ****/
	private String eventClass;
	
	private String linkClicked;
	
	private String classId;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeOfAction;
		
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "credential_id", referencedColumnName = "id")
	private Credential credetial;
	
	private String userAddress;
	
	@Column(name = "support_username")
	private String supportUserName;
	
	/**** Attributes ****/
	
	/**** Constructor ****/
	public ActivityLog() {
		super();
	}

	/**** Constructor ****/
	
	/**** Domain Methods ****/
	public static ActivityLog logActivity(HttpServletRequest request, String locale) throws Exception{
		ActivityLog actLog = new ActivityLog();

		String userName = request.getRemoteUser();
		String url = request.getRequestURL().toString();		
		String userAddress = request.getRemoteAddr();
		
		Credential credential = Credential.findByFieldName(Credential.class, "username", userName, null);
		
		actLog.setCredetial(credential);
		actLog.setTimeOfAction(new Date());
		actLog.setLinkClicked(url);
		actLog.setLocale(locale);
		actLog.setUserAddress(userAddress);
		
		Object supportUserName = request.getSession().getAttribute("supportUserName");
		if(supportUserName!=null) {
			actLog.setSupportUserName(supportUserName.toString());
		}
		
		return (ActivityLog)actLog.persist();
	}
	
	
	/**** Domain Methods ****/
	
	/**** Getter Setter ****/
	public String getEventClass() {
		return eventClass;
	}

	public void setEventClass(String eventClass) {
		this.eventClass = eventClass;
	}

	public String getLinkClicked() {
		return linkClicked;
	}

	public void setLinkClicked(String linkClicked) {
		this.linkClicked = linkClicked;
	}

	public String getClassId(){
		return classId;
	}
	
	public void setClassId(String classId){
		this.classId = classId;
	}
	
	public Date getTimeOfAction() {
		return timeOfAction;
	}

	public void setTimeOfAction(Date timeOfAction) {
		this.timeOfAction = timeOfAction;
	}

	public Credential getCredetial() {
		return credetial;
	}

	public void setCredetial(Credential credetial) {
		this.credetial = credetial;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getSupportUserName() {
		return supportUserName;
	}

	public void setSupportUserName(String supportUserName) {
		this.supportUserName = supportUserName;
	}
	
	/**** Getter Setter ****/
}
