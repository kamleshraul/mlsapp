package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.DeviceNumberInformation;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceNumberInformationRepository extends BaseRepository<DeviceNumberInformation, Serializable>{
	
	public DeviceNumberInformation find(final DeviceType deviceType, final String locale) throws ELSException {
		return find(deviceType, null, null, locale);
	}
	
	public DeviceNumberInformation find(final DeviceType deviceType, final HouseType houseType, final String locale) throws ELSException {
		return find(deviceType, houseType, null, locale);
	}
	
	public DeviceNumberInformation find(final DeviceType deviceType, final Session session, final String locale) throws ELSException {
		return find(deviceType, null, session, locale);
	}

    public DeviceNumberInformation find(final DeviceType deviceType, final HouseType houseType, final Session session, final String locale) throws ELSException {
		DeviceNumberInformation deviceNumberInformation = null;
		
		StringBuffer strQuery = new StringBuffer("SELECT m FROM DeviceNumberInformation m WHERE m.locale=:locale AND m.deviceType=:deviceType");
		if(houseType!=null) {
			strQuery.append(" AND m.houseType=:houseType");
		}
		if(session!=null) {
			strQuery.append(" AND m.session=:session");
		}

		List<DeviceNumberInformation> deviceNumberInformationList = new ArrayList<DeviceNumberInformation>();
		TypedQuery<DeviceNumberInformation> jpQuery = null;
		
		try{			
			jpQuery = this.em().createQuery(strQuery.toString(),DeviceNumberInformation.class);
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("deviceType", deviceType);
			jpQuery.setParameter("houseType", houseType);
			jpQuery.setParameter("session", session);
		
			deviceNumberInformationList = jpQuery.getResultList();
			
			if(deviceNumberInformationList!=null && !deviceNumberInformationList.isEmpty()) {
				deviceNumberInformation = deviceNumberInformationList.get(0);
			}
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("DeviceNumberInformationRepository_DeviceNumberInformation_find", "No deviceNumberInformation found.");
			throw elsException;
		}
		
		return deviceNumberInformation;
	}
    
}