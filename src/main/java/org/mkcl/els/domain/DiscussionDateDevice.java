package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.repository.DiscussionDateDeviceRepository;
import org.mkcl.els.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="discussiondatedevices")
public class DiscussionDateDevice extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Temporal(value = TemporalType.DATE)
	private Date discussionDate;
	
	@Column(length = 10000)
	private String devices;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id")
	private Session session;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "devicetype_id")
	private DeviceType deviceType;
	
	 @Autowired
	 private transient DiscussionDateDeviceRepository discussionDateDeviceRepository;

	public DiscussionDateDevice() {
		super();
	}

	/**** Method ****/
	
	public static DiscussionDateDeviceRepository getDiscussionDateDeviceRepository(){
		DiscussionDateDeviceRepository discussionDateDeviceRepository = new DiscussionDateDevice().discussionDateDeviceRepository;
		if (discussionDateDeviceRepository == null) {
			throw new IllegalStateException(
					"DiscussionDateDeviceRepository has not been injected in DiscussionDateDevice Domain");
		}
		return discussionDateDeviceRepository;
	}
	
	
	public static List<DiscussionDateDevice> findBySessionDeviceType(final Session session, final DeviceType deviceType, final String order, final String locale){
		return getDiscussionDateDeviceRepository().findBySessionDeviceType(session, deviceType, order, locale);
	}
	
	public static DiscussionDateDevice findBySessionDeviceTypeDate(final Session session, final DeviceType deviceType, final Date discussionDate, final String locale){
		return getDiscussionDateDeviceRepository().findBySessionDeviceTypeDate(session, deviceType, discussionDate, locale);
	}
	
	/**** Method ****/
	
	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	public String getDevices() {
		return devices;
	}

	public void setDevices(String devices) {
		this.devices = devices;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}	
}
