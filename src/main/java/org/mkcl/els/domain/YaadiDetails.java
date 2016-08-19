package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.repository.YaadiDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "yaadi_details")
public class YaadiDetails extends BaseDomain implements Serializable {

	// ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    @Column(length=100)
    private String device;
    
    /** The device type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="devicetype_id")
    private DeviceType deviceType;
    
    /** The house type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="housetype_id")
    private HouseType houseType;
    
    /** The session. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="session_id")
    private Session session;
    
    /** The list of devices. */
    @ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="yaadi_details_devices",
			joinColumns={ @JoinColumn(name="yaadi_details_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="device_id", referencedColumnName="id") })
    private List<Device> devices;
    
    /** The list of devices. */
    @ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="yaadi_details_removed_devices",
			joinColumns={ @JoinColumn(name="yaadi_details_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="removed_device_id", referencedColumnName="id") })
    private List<Device> removedDevices;
    
    /** The number. */
    private Integer number;
    
    /** The laying date. */
    @Temporal(TemporalType.DATE)
    private Date layingDate;
    
    /** The laying status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="layingstatus_id")
    private Status layingStatus;
    
    /** The yaadi details repository. */
    @Autowired
    private transient YaadiDetailsRepository yaadiDetailsRepository;
    
    //---------------------------------Domain Methods----------------------------------------
    public static Integer findHighestYaadiNumber(final DeviceType deviceType, final Session session, final String locale) throws ELSException {
		return getYaadiDetailsRepository().findHighestYaadiNumber(deviceType, session, locale);
	}
    
    public Integer findDevicesCount() {
    	return getYaadiDetailsRepository().findDevicesCount(this);
    }
    
    public static YaadiDetails find(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final String locale) throws ELSException {
    	return getYaadiDetailsRepository().find(deviceType, session, yaadiNumber, locale);
    }
    
    public static List<YaadiDetails> findAllGreaterThanGivenYaadiNumber(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final String locale) throws ELSException {
    	return getYaadiDetailsRepository().findAllGreaterThanGivenYaadiNumber(deviceType, session, yaadiNumber, locale);
    }
    
    public static Boolean isNumberedYaadiFilled(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final String locale) throws ELSException {
    	return getYaadiDetailsRepository().isNumberedYaadiFilled(deviceType, session, yaadiNumber, locale);
    }
    
    public Boolean isNumberedYaadiFilled() throws ELSException {
    	return getYaadiDetailsRepository().isNumberedYaadiFilled(this);
    }
    
    public static <D extends Device> List<D> findDevicesEligibleForNumberedYaadi(final DeviceType deviceType, final Session session, final Integer numberOfDevicesSetInYaadi, final String locale) throws ELSException {
    	return getYaadiDetailsRepository().findDevicesEligibleForNumberedYaadi(deviceType, session, numberOfDevicesSetInYaadi, locale);
    }
    
    public YaadiDetails regenerate(final int existingDevicesCount) throws ELSException {
    	List<YaadiDetails> yaadiDetailsReadyForRegeneration = YaadiDetails.findAllGreaterThanGivenYaadiNumber(deviceType, session, number, this.getLocale());
		if(yaadiDetailsReadyForRegeneration!=null && !yaadiDetailsReadyForRegeneration.isEmpty()) {
			//remove devices for regeneration
			for(YaadiDetails yd: yaadiDetailsReadyForRegeneration) {
				if(yd.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_DRAFTED)) {
					List<Device> devicesInDraftedYaadi = yd.getDevices();
					for(Device d: devicesInDraftedYaadi) {
						if(yd.getDevice()!=null && yd.getDevice().startsWith("question")) {
							Question q = (Question) d;
							q.setYaadiNumber(null);
							q.setYaadiLayingDate(null);
							q.simpleMerge();
						}
					}
					yd.setDevices(null);
					yd.merge();
				}
			}
			//regenerate current yaadi
			List<Device> newlyAddedDevices = YaadiDetails.findDevicesEligibleForNumberedYaadi(deviceType, session, existingDevicesCount, this.getLocale());
			if(newlyAddedDevices!=null && !newlyAddedDevices.isEmpty()) {
				for(Device d: newlyAddedDevices) {
					if(this.getDevice()!=null && this.getDevice().startsWith("question")) {
						Question q = (Question) d;
						q.setYaadiNumber(this.getNumber());
						q.setYaadiLayingDate(this.getLayingDate());		
						q.simpleMerge();
					}
				}
				List<Device> totalDevicesInYaadi = this.getDevices();
				if(totalDevicesInYaadi!=null && !totalDevicesInYaadi.isEmpty()) {
					totalDevicesInYaadi.addAll(newlyAddedDevices);				
					this.setDevices(totalDevicesInYaadi);
				} else { 
					this.setDevices(newlyAddedDevices);
				}
				/** save/update yaadi details **/
				if(this.getId()!=null) {
					this.merge();
				} else {
					this.persist();
				}
			}
			//regenerate greater yaadis in ascending order
			for(YaadiDetails yd: yaadiDetailsReadyForRegeneration) {
				if(yd.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_DRAFTED)) {
					newlyAddedDevices = YaadiDetails.findDevicesEligibleForNumberedYaadi(deviceType, session, 0, this.getLocale());
					if(newlyAddedDevices!=null && !newlyAddedDevices.isEmpty()) {
						for(Device d: newlyAddedDevices) {
							if(this.getDevice()!=null && this.getDevice().startsWith("question")) {
								Question q = (Question) d;
								q.setYaadiNumber(yd.getNumber());
								q.setYaadiLayingDate(yd.getLayingDate());		
								q.simpleMerge();
							}
						}
						yd.setDevices(newlyAddedDevices);
						yd.merge();
					}
				}
			}
		} else {
			//regenerate current yaadi only
			List<Device> newlyAddedDevices = YaadiDetails.findDevicesEligibleForNumberedYaadi(deviceType, session, existingDevicesCount, this.getLocale());
			if(newlyAddedDevices!=null && !newlyAddedDevices.isEmpty()) {
				for(Device d: newlyAddedDevices) {
					if(this.getDevice()!=null && this.getDevice().startsWith("question")) {
						Question q = (Question) d;
						q.setYaadiNumber(this.getNumber());
						q.setYaadiLayingDate(this.getLayingDate());		
						q.simpleMerge();
					}
				}
				List<Device> totalDevicesInYaadi = this.getDevices();
				if(totalDevicesInYaadi!=null && !totalDevicesInYaadi.isEmpty()) {
					totalDevicesInYaadi.addAll(newlyAddedDevices);				
					this.setDevices(totalDevicesInYaadi);
				} else { 
					this.setDevices(newlyAddedDevices);
				}
				/** save/update yaadi details **/
				if(this.getId()!=null) {
					this.merge();
				} else {
					this.persist();
				}
			}
		}
		return this;
    }
    
    public static List<YaadiDetails> findAll(final DeviceType deviceType, final Session session, final String locale) throws ELSException {
    	return getYaadiDetailsRepository().findAll(deviceType, session, locale);
    }
    
    public static List<Long> findRemovedDevicesNotEligibleForNumberedYaadi(final DeviceType deviceType, final Session session, final String locale) throws ELSException {
    	List<Long> removedDevicesNotEligibleForNumberedYaadi = new ArrayList<Long>();
    	List<YaadiDetails> eligibleYaadiDetailsList = YaadiDetails.findAll(deviceType, session, locale);
    	if(eligibleYaadiDetailsList!=null && !eligibleYaadiDetailsList.isEmpty()) {
    		for(YaadiDetails yd: eligibleYaadiDetailsList) {
    			List<Device> removedDevices = yd.getRemovedDevices();
    			if(removedDevices!=null && !removedDevices.isEmpty()) {
    				for(Device d: removedDevices) {
    					removedDevicesNotEligibleForNumberedYaadi.add(d.getId());
    				}    				
    			}
    		}
    	}
    	return removedDevicesNotEligibleForNumberedYaadi;
    }
    
    public static void allowDeviceInYaadiDetails(final Device device) {
    	getYaadiDetailsRepository().allowDeviceInYaadiDetails(device);
    }
    
    public static String findYaadiLayingStatus(final Device device) {
    	return getYaadiDetailsRepository().findYaadiLayingStatus(device);
    }
    
    // ---------------------------------Getters and Setters----------------------------------------
    /**
     * Gets the yaadi details repository.
     *
     * @return the yaadi details repository
     */
    private static YaadiDetailsRepository getYaadiDetailsRepository() {
        YaadiDetailsRepository yaadiDetailsRepository = new YaadiDetails().yaadiDetailsRepository;
        if (yaadiDetailsRepository == null) {
            throw new IllegalStateException(
            	"YaadiDetailsRepository has not been injected in YaadiDetails Domain");
        }
        return yaadiDetailsRepository;
    }
    
	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public List<Device> getDevices() {	
		if(this.getId()!=null) {
			return getYaadiDetailsRepository().getDevices(this);
		} else {
			return null;
		}		
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	
	public List<Device> getRemovedDevices() {	
		if(this.getId()!=null) {
			return getYaadiDetailsRepository().getRemovedDevices(this);
		} else {
			return null;
		}		
	}

	public void setRemovedDevices(List<Device> removedDevices) {
		this.removedDevices = removedDevices;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getLayingDate() {
		return layingDate;
	}

	public void setLayingDate(Date layingDate) {
		this.layingDate = layingDate;
	}

	public Status getLayingStatus() {
		return layingStatus;
	}

	public void setLayingStatus(Status layingStatus) {
		this.layingStatus = layingStatus;
	}    

}
