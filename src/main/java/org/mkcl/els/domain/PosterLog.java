package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(value={"deviceType"},ignoreUnknown=true)
public class PosterLog extends BaseDomain implements Serializable {
	

	
	private String workflowActivity;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timeOfAction;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="devicetype_id")
	private DeviceType deviceType;

	@Column(name = "support_username")
	private String supportUserName;

	private String posterUrl;

	private String supportId;

	@Column(length = 30000)
	private String requestParameters;
	
	
	private String posterResponse;
	

	public String getWorkflowActivity() {
		return workflowActivity;
	}

	public void setWorkflowActivity(String workflowActivity) {
		this.workflowActivity = workflowActivity;
	}

	public Date getTimeOfAction() {
		return timeOfAction;
	}

	public void setTimeOfAction(Date timeOfAction) {
		this.timeOfAction = timeOfAction;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getSupportUserName() {
		return supportUserName;
	}

	public void setSupportUserName(String supportUserName) {
		this.supportUserName = supportUserName;
	}

	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public String getSupportId() {
		return supportId;
	}

	public void setSupportId(String supportId) {
		this.supportId = supportId;
	}

	public String getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(String requestParameters) {
		this.requestParameters = requestParameters;
	}

	public String getPosterResponse() {
		return posterResponse;
	}

	public void setPosterResponse(String posterResponse) {
		this.posterResponse = posterResponse;
	}
	
	
	
}
