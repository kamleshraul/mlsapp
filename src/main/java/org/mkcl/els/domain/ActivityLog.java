package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.Column;
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
@Table(name = "activitylog")
public class ActivityLog extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**** Attributes ****/
	private String eventClass;
	
	private String linkClicked;
	
	@Column(length=30000)
	private String requestParameters;
	
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
	public static ActivityLog logActivity(HttpServletRequest request, String locale) throws Exception
	{
		if(ApplicationConstants.environment!=null && ApplicationConstants.environment.acceptsProfiles("prod")) 
		{
			ActivityLog actLog = new ActivityLog();

			String userName = request.getRemoteUser();
			String url = request.getRequestURL().toString();	
			@SuppressWarnings("unchecked")
			Map<String, String[]> requestParametersMap = request.getParameterMap();
			String userAddress = request.getRemoteAddr();
			
			Credential credential = Credential.findByFieldName(Credential.class, "username", userName, null);
			
			actLog.setCredetial(credential);
			actLog.setTimeOfAction(new Date());
			actLog.setLinkClicked(url);
			
			CustomParameter linksRequiringRequestParametersInActivityLog = CustomParameter.findByName(CustomParameter.class, "LINKS_REQUIRING_REQUEST_PARAMETERS_IN_ACTIVITYLOG", "");
			if(linksRequiringRequestParametersInActivityLog!=null && linksRequiringRequestParametersInActivityLog.getValue()!=null) {
				boolean isLinkRequiringRequestParameters = false;
				for(String linkKeyword: linksRequiringRequestParametersInActivityLog.getValue().split("~")) {
					if(url.contains(linkKeyword)) {
						isLinkRequiringRequestParameters = true;
						break;
					}
				}
				if(isLinkRequiringRequestParameters) {
					StringBuffer requestParameters = new StringBuffer("");
					Iterator<String> i = requestParametersMap.keySet().iterator();
					while ( i.hasNext() ){

						 String key = (String) i.next();

						 String value = ((String[]) requestParametersMap.get( key ))[ 0 ];

						 requestParameters.append("Key: ["+key+"] - Val: ["+value+"]");
						 requestParameters.append(System.getProperty("line.separator"));
						 
					}		
					actLog.setRequestParameters(requestParameters.toString());
				}
				else if(
						//	request.getMethod().equalsIgnoreCase(ApplicationConstants.REQUEST_METHOD_POST)
						// 	|| 
						//	request.getMethod().equalsIgnoreCase(ApplicationConstants.REQUEST_METHOD_PUT)
						// 	|| 
							request.getMethod().equalsIgnoreCase(ApplicationConstants.REQUEST_METHOD_DELETE)) {
					StringBuffer requestParameters = new StringBuffer("");
					Iterator<String> i = requestParametersMap.keySet().iterator();
					while ( i.hasNext() ){

						 String key = (String) i.next();

						 String value = ((String[]) requestParametersMap.get( key ))[ 0 ];

						 requestParameters.append("Key: ["+key+"] - Val: ["+value+"]");
						 requestParameters.append(System.getProperty("line.separator"));
						 
					}		
					actLog.setRequestParameters(requestParameters.toString());
				}
			}
			
			actLog.setLocale(locale);
			actLog.setUserAddress(userAddress);
			
			Object supportUserName = request.getSession().getAttribute("supportUserName");
			if(supportUserName!=null) {
				actLog.setSupportUserName(supportUserName.toString());
			}
			
			return (ActivityLog)actLog.persist();
		}
		else {
			return null;
		}		
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

	public String getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(String requestParameters) {
		this.requestParameters = requestParameters;
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
