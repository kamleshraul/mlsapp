package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="device_status_update_info")
public class DeviceStatusUpdateInfo extends BaseDomain implements Serializable {
	
	// ---------------------------------Attributes------------------------------------------
	/** The Constant serialVersionUID. */
	private transient static final long serialVersionUID = 1L;
	
	/** The parliamentary device id. */
	@Column(length=20)
	private String deviceId;
	
	/** The update status type. */
	@Column(length=200)
	private String statusType;
	
	/** The updated by. */
	@Column(length = 1000)
	private String updatedBy;
	
	/** The update timestamp. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;
	
	/** The update related reference document tag. */
	@Column(length=100)
	private String updateReferenceDoc;

	/** The remarks. */
	@Column(length = 10000)
	private String updateRemarks;

	
	// --------------------------------- Constructors ---------------------------------------------
	public DeviceStatusUpdateInfo() {
		super();
	}
	

	// -------------------------------- Getters & Setters -----------------------------------------
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getUpdateReferenceDoc() {
		return updateReferenceDoc;
	}

	public void setUpdateReferenceDoc(String updateReferenceDoc) {
		this.updateReferenceDoc = updateReferenceDoc;
	}

	public String getUpdateRemarks() {
		return updateRemarks;
	}

	public void setUpdateRemarks(String updateRemarks) {
		this.updateRemarks = updateRemarks;
	}

}
