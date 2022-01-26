package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.YaadiDetails;
import org.springframework.stereotype.Repository;

@Repository
public class YaadiDetailsRepository extends BaseRepository<YaadiDetails, Long> {

	@SuppressWarnings("unchecked")
	public <D extends Device> List<D> getDevices(final YaadiDetails yaadiDetails) {
		List<D> devices = null;
		
		String deviceTableName = "";
		CustomParameter deviceTableNameCP = CustomParameter.findByName(CustomParameter.class, yaadiDetails.getDeviceType().getType().toUpperCase()+"_DEVICE_TABLE_NAME", "");
		if(deviceTableNameCP!=null && deviceTableNameCP.getValue()!=null 
				&& !deviceTableNameCP.getValue().isEmpty()) {
			deviceTableName = deviceTableNameCP.getValue();
		} else {
			if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)) {
				deviceTableName = "questions";
			} else if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)) {
				deviceTableName = "resolutions";
			} else if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_BILLS)) {
				deviceTableName = "bills";
			} else if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_MOTIONS)) {
				deviceTableName = "motions";
			}
		}
		
		String queryString = "SELECT device.* FROM " + deviceTableName + " device"
				+ " WHERE device.id IN ("
				+ " SELECT device_id FROM yaadi_details_devices"
				+ " WHERE yaadi_details_id=:yaadiDetailsId"
				+ ")";
		
		if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)) {
			TypedQuery<Question> query = (TypedQuery<Question>) this.em().createNativeQuery(queryString, Question.class);
			query.setParameter("yaadiDetailsId", yaadiDetails.getId());
			devices = (List<D>) query.getResultList();
		} else if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)) {
			TypedQuery<Resolution> query = (TypedQuery<Resolution>) this.em().createNativeQuery(queryString, Resolution.class);
			query.setParameter("yaadiDetailsId", yaadiDetails.getId());
			devices = (List<D>) query.getResultList();
		} else if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_BILLS)) {
			TypedQuery<Bill> query = (TypedQuery<Bill>) this.em().createNativeQuery(queryString, Bill.class);
			query.setParameter("yaadiDetailsId", yaadiDetails.getId());
			devices = (List<D>) query.getResultList();
		} else if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_MOTIONS)) {
			TypedQuery<Motion> query = (TypedQuery<Motion>) this.em().createNativeQuery(queryString, Motion.class);
			query.setParameter("yaadiDetailsId", yaadiDetails.getId());
			devices = (List<D>) query.getResultList();
		} //Add Remaining Cases Here...		
		 
		return devices;
	}
	
	@SuppressWarnings("unchecked")
	public <D extends Device> List<D> getRemovedDevices(final YaadiDetails yaadiDetails) {
		List<D> removedDevices = null;
		
		String deviceTableName = "";
		CustomParameter deviceTableNameCP = CustomParameter.findByName(CustomParameter.class, yaadiDetails.getDeviceType().getType().toUpperCase()+"_DEVICE_TABLE_NAME", "");
		if(deviceTableNameCP!=null && deviceTableNameCP.getValue()!=null 
				&& !deviceTableNameCP.getValue().isEmpty()) {
			deviceTableName = deviceTableNameCP.getValue();
		} else {
			if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)) {
				deviceTableName = "questions";
			} else if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)) {
				deviceTableName = "resolutions";
			} else if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_BILLS)) {
				deviceTableName = "bills";
			} else if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_MOTIONS)) {
				deviceTableName = "motions";
			}
		}
		
		String queryString = "SELECT device.* FROM " + deviceTableName + " device"
				+ " WHERE device.id IN ("
				+ " SELECT removed_device_id FROM yaadi_details_removed_devices"
				+ " WHERE yaadi_details_id=:yaadiDetailsId"
				+ ")";
		
		if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)) {
			TypedQuery<Question> query = (TypedQuery<Question>) this.em().createNativeQuery(queryString, Question.class);
			query.setParameter("yaadiDetailsId", yaadiDetails.getId());
			removedDevices = (List<D>) query.getResultList();
		} else if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)) {
			TypedQuery<Resolution> query = (TypedQuery<Resolution>) this.em().createNativeQuery(queryString, Resolution.class);
			query.setParameter("yaadiDetailsId", yaadiDetails.getId());
			removedDevices = (List<D>) query.getResultList();
		} else if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_BILLS)) {
			TypedQuery<Bill> query = (TypedQuery<Bill>) this.em().createNativeQuery(queryString, Bill.class);
			query.setParameter("yaadiDetailsId", yaadiDetails.getId());
			removedDevices = (List<D>) query.getResultList();
		} else if(yaadiDetails.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_MOTIONS)) {
			TypedQuery<Motion> query = (TypedQuery<Motion>) this.em().createNativeQuery(queryString, Motion.class);
			query.setParameter("yaadiDetailsId", yaadiDetails.getId());
			removedDevices = (List<D>) query.getResultList();
		} //Add Remaining Cases Here...		
		 
		return removedDevices;
	}
	
	public Integer findHighestYaadiNumber(final DeviceType deviceType, final Session session, final String locale) throws ELSException {
		Integer highestYaadiNumber = null;
		if(session!=null && deviceType!=null) {			
			String queryString = "SELECT MAX(yd.number) FROM YaadiDetails yd"
					+ " WHERE yd.deviceType.id=:deviceTypeId"
					+ " AND yd.number IS NOT NULL"
					+ " AND yd.session.house.id=:houseId";
			String yaadiNumberingParameter = session.getParameter(deviceType.getType() + "_" + "yaadiNumberingParameter");
			if(yaadiNumberingParameter!=null) {
				if(yaadiNumberingParameter.equals("session")) {
					queryString += " AND yd.layingDate>=:sessionStartDate";
					queryString += " AND yd.layingDate<=:sessionEndDate";
				}				
			} else {
				logger.error("**** Session parameter 'yaadiNumberingParameter' is not set for session with ID = " + session.getId() +". ****");
				throw new ELSException("error", "device.yaadiNumberingParameterNotSet");
			}
			queryString += " AND yd.locale=:locale";
			TypedQuery<Integer> query = this.em().createQuery(queryString, Integer.class);
			query.setParameter("houseId", session.getHouse().getId());
			query.setParameter("deviceTypeId", deviceType.getId());
			if(yaadiNumberingParameter.equals("session")) {
				query.setParameter("sessionStartDate", session.getStartDate());
				query.setParameter("sessionEndDate", session.getEndDate());
			}				
			query.setParameter("locale", locale);
			try {				
				highestYaadiNumber = query.getSingleResult();
			} catch(Exception e) {
				highestYaadiNumber = 0;
			}	
			if(highestYaadiNumber==null) {
				highestYaadiNumber = 0;
			}
		}
		return highestYaadiNumber;
	}
	
	public List<YaadiDetails> findAll(final DeviceType deviceType, final Session session, final String locale) throws ELSException {
		List<YaadiDetails> yaadiDetailsList = null;
		if(session!=null && deviceType!=null) {
			String queryString = "SELECT yd FROM YaadiDetails yd"
					+ " WHERE yd.deviceType.id=:deviceTypeId"
					+ " AND yd.session.house.id=:houseId";
			String yaadiNumberingParameter = session.getParameter(deviceType.getType() + "_" + "yaadiNumberingParameter");
			if(yaadiNumberingParameter!=null) {
				if(yaadiNumberingParameter.equals("session")) {
					queryString += " AND yd.layingDate>=:sessionStartDate";
					queryString += " AND yd.layingDate<=:sessionEndDate";
				}				
			} else {
				logger.error("**** Session parameter 'yaadiNumberingParameter' is not set for session with ID = " + session.getId() +". ****");
				throw new ELSException("error", "device.yaadiNumberingParameterNotSet");
			}
			queryString += " AND yd.locale=:locale";
			TypedQuery<YaadiDetails> query = this.em().createQuery(queryString, YaadiDetails.class);
			query.setParameter("houseId", session.getHouse().getId());			
			query.setParameter("deviceTypeId", deviceType.getId());
			if(yaadiNumberingParameter.equals("session")) {
				query.setParameter("sessionStartDate", session.getStartDate());
				query.setParameter("sessionEndDate", session.getEndDate());
			}				
			query.setParameter("locale", locale);
			try {				
				yaadiDetailsList = query.getResultList();
			} catch(Exception e) {
				yaadiDetailsList = null;
			}			
		}
		return yaadiDetailsList;
	}
	
	public YaadiDetails find(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final String locale) throws ELSException {
		YaadiDetails yaadiDetails = null;
		if(session!=null && deviceType!=null && yaadiNumber!=null) {			
			String queryString = "SELECT yd FROM YaadiDetails yd"
					+ " WHERE yd.deviceType.id=:deviceTypeId"
					+ " AND yd.number=:yaadiNumber"
					+ " AND yd.session.house.id=:houseId";
			String yaadiNumberingParameter = session.getParameter(deviceType.getType() + "_" + "yaadiNumberingParameter");
			if(yaadiNumberingParameter!=null) {
				if(yaadiNumberingParameter.equals("session")) {
					queryString += " AND yd.layingDate>=:sessionStartDate";
					queryString += " AND yd.layingDate<=:sessionEndDate";
				}				
			} else {
				logger.error("**** Session parameter 'yaadiNumberingParameter' is not set for session with ID = " + session.getId() +". ****");
				throw new ELSException("error", "device.yaadiNumberingParameterNotSet");
			}
			queryString += " AND yd.locale=:locale";
			TypedQuery<YaadiDetails> query = this.em().createQuery(queryString, YaadiDetails.class);
			query.setParameter("houseId", session.getHouse().getId());			
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("yaadiNumber", yaadiNumber);
			if(yaadiNumberingParameter.equals("session")) {
				query.setParameter("sessionStartDate", session.getStartDate());
				query.setParameter("sessionEndDate", session.getEndDate());
			}				
			query.setParameter("locale", locale);
			try {				
				yaadiDetails = query.getSingleResult();
			} catch(Exception e) {
				yaadiDetails = null;
			}			
		}
		return yaadiDetails;
	}	
	
	public List<YaadiDetails> findAllGreaterThanGivenYaadiNumber(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final String locale) throws ELSException {
		List<YaadiDetails> yaadiDetailsList = null;
		if(session!=null && deviceType!=null && yaadiNumber!=null) {			
			String queryString = "SELECT yd FROM YaadiDetails yd"
					+ " WHERE yd.deviceType.id=:deviceTypeId"
					+ " AND yd.number>:yaadiNumber"
					+ " AND yd.session.house.id=:houseId";
			String yaadiNumberingParameter = session.getParameter(deviceType.getType() + "_" + "yaadiNumberingParameter");
			if(yaadiNumberingParameter!=null) {
				if(yaadiNumberingParameter.equals("session")) {
					queryString += " AND yd.layingDate>=:sessionStartDate";
					queryString += " AND yd.layingDate<=:sessionEndDate";
				}				
			} else {
				logger.error("**** Session parameter 'yaadiNumberingParameter' is not set for session with ID = " + session.getId() +". ****");
				throw new ELSException("error", "device.yaadiNumberingParameterNotSet");
			}
			queryString += " AND yd.locale=:locale ORDER BY yd.number ASC";
			TypedQuery<YaadiDetails> query = this.em().createQuery(queryString, YaadiDetails.class);
			query.setParameter("houseId", session.getHouse().getId());			
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("yaadiNumber", yaadiNumber);
			if(yaadiNumberingParameter.equals("session")) {
				query.setParameter("sessionStartDate", session.getStartDate());
				query.setParameter("sessionEndDate", session.getEndDate());
			}				
			query.setParameter("locale", locale);
			try {				
				yaadiDetailsList = query.getResultList();
			} catch(Exception e) {
				yaadiDetailsList = null;
			}			
		}
		return yaadiDetailsList;
	}
	
	public Integer findDevicesCount(final YaadiDetails yaadiDetails) {
		Integer devicesCount = 0;
		
		List<Device> devicesInYaadi = yaadiDetails.getDevices();
		if(devicesInYaadi!=null && !devicesInYaadi.isEmpty()) {
			devicesCount = devicesInYaadi.size();
		}
		
		return devicesCount;
	}
	
	public boolean isNumberedYaadiFilled(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final String locale) throws ELSException {
		boolean isNumberedYaadiFilled = false;
		if(session!=null && deviceType!=null && yaadiNumber!=null) {
			YaadiDetails numberedYaadi = YaadiDetails.find(deviceType, session, yaadiNumber, locale);
			if(numberedYaadi!=null) {
				Integer questionsFilledInYaadiCount = numberedYaadi.findDevicesCount();
				if(questionsFilledInYaadiCount!=null && questionsFilledInYaadiCount>0) {
					String numberOfQuestionsInYaadiParameter = session.getParameter(deviceType.getType() + "_" + "numberOfQuestionsInYaadi");
					if(numberOfQuestionsInYaadiParameter!=null) {
						Integer numberOfQuestionsInYaadi = Integer.parseInt(numberOfQuestionsInYaadiParameter);
						if(questionsFilledInYaadiCount.intValue()==numberOfQuestionsInYaadi.intValue()) {
							isNumberedYaadiFilled = true;
						}
					} else {
						logger.error("**** Session parameter 'numberOfQuestionsInYaadi' is not set for session with ID = " + session.getId() +". ****");
						throw new ELSException("error", "device.numberOfQuestionsInYaadiParameterNotSet");
					}				
				}
			}
		}	
		return isNumberedYaadiFilled;
	}
	
	public boolean isNumberedYaadiFilled(final YaadiDetails yaadiDetails) throws ELSException {
		boolean isNumberedYaadiFilled = false;
		if(yaadiDetails!=null) {
			if(yaadiDetails!=null) {
				Integer questionsFilledInYaadiCount = yaadiDetails.findDevicesCount();
				if(questionsFilledInYaadiCount!=null && questionsFilledInYaadiCount>0) {
					String numberOfQuestionsInYaadiParameter = yaadiDetails.getSession().getParameter(yaadiDetails.getDeviceType().getType() + "_" + "numberOfQuestionsInYaadi");
					if(numberOfQuestionsInYaadiParameter!=null) {
						Integer numberOfQuestionsInYaadi = Integer.parseInt(numberOfQuestionsInYaadiParameter);
						if(questionsFilledInYaadiCount.intValue()>=numberOfQuestionsInYaadi.intValue()) {
							isNumberedYaadiFilled = true;
						}
					} else {
						logger.error("**** Session parameter 'numberOfQuestionsInYaadi' is not set for session with ID = " + yaadiDetails.getSession().getId() +". ****");
						throw new ELSException("error", "device.numberOfQuestionsInYaadiParameterNotSet");
					}				
				}
			}
		}	
		return isNumberedYaadiFilled;
	}
	
	@SuppressWarnings("unchecked")
	public <D extends Device> List<D> findDevicesEligibleForNumberedYaadi(final DeviceType deviceType, final Session session, final Integer numberOfDevicesSetInYaadi, final String locale) throws ELSException {
		List<D> devicesEligibleForNumberedYaadi = new ArrayList<D>();
		
		if(deviceType!=null) {
			if(deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				devicesEligibleForNumberedYaadi = (List<D>) Question.findQuestionsEligibleForNumberedYaadi(deviceType, session, numberOfDevicesSetInYaadi, locale);
			} //add remaining cases for other devicetypes if any..
		}
		
		return devicesEligibleForNumberedYaadi;
	}
	
	public void allowDeviceInYaadiDetails(final Device device) {
		if(device==null || device.getId()==null) {
			return;
		}
    	String queryString = "DELETE FROM yaadi_details_removed_devices WHERE removed_device_id=:deviceId";
    	Query query = this.em().createNativeQuery(queryString);
    	query.setParameter("deviceId", device.getId());
    	query.executeUpdate();
    }
	
	public String findYaadiLayingStatus(final Device device) {    	
    	if(device==null || device.getId()==null) {
			return null;
		}
    	String queryString = "SELECT yd_status.`type`"
					+ " FROM yaadi_details yd"
					+ " INNER JOIN yaadi_details_devices ydd ON (ydd.`yaadi_details_id`=yd.`id`)"
					+ " LEFT JOIN `status` yd_status ON (yd_status.`id`=yd.`layingstatus_id`)"
					+ " WHERE device_id=:deviceId";
    	try {    		
    		Query query = this.em().createNativeQuery(queryString);
        	query.setParameter("deviceId", device.getId());
        	return query.getSingleResult().toString();
        	
    	} catch(NoResultException e) {
    		return null;
    		
    	} catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}    	
    }

	public boolean updateDevices(final YaadiDetails yd, final boolean isStatusUpdateRequiredForDevices, final Status yaadiLaidStatus, final boolean isLayingDateUpdateRequiredForDevices, final Date yaadiLayingDate) {
		boolean updateDone = false;
		if(yd!=null && yd.getId()!=null) {
			org.mkcl.els.domain.Query queryDB = org.mkcl.els.domain.Query.findByFieldName(Query.class, "keyField", yd.getDevice().toUpperCase()+"_YAADI_DETAILS_DEVICES_UPDATE", yd.getLocale());
			if(queryDB!=null && queryDB.getId()!=null) {
				Query query = this.em().createNativeQuery(queryDB.getQuery());
				query.setParameter("isStatusUpdateRequiredForDevices", isStatusUpdateRequiredForDevices);
				query.setParameter("yaadiLaidStatusId", yaadiLaidStatus.getId());
				query.setParameter("isLayingDateUpdateRequiredForDevices", isLayingDateUpdateRequiredForDevices);
				query.setParameter("yaadiLayingDate", yaadiLayingDate);
				List<Long> yaadiDevices = new ArrayList<Long>();
				for(Device d: yd.getDevices()) {
					yaadiDevices.add(d.getId());
				}
				query.setParameter("yaadiDevices", yaadiDevices);
				try {
					query.executeUpdate();
					updateDone = true;
				} catch(Exception e) {
					
				}			
			}
		}		
		return updateDone;
	}
	
}
