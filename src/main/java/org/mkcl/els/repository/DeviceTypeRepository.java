package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class DeviceTypeRepository extends BaseRepository<DeviceType, Serializable>{

    //get only device types starting with a particular pattern like questions
    public List<DeviceType> findDeviceTypesStartingWith(final String pattern,final String locale) throws ELSException{
        String strquery="SELECT dt FROM DeviceType dt" +
        		" WHERE dt.locale=:locale AND type LIKE:pattern";
        TypedQuery<DeviceType> jpQuery = null;
        List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
        
        try{
	        jpQuery = this.em().createQuery(strquery,DeviceType.class);
	        jpQuery.setParameter("pattern", pattern + "%");
	        jpQuery.setParameter("locale", locale);
        
	        deviceTypes = jpQuery.getResultList();
        }catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("DepartmentRepository_List<DeviceType>_findDeviceTypesStartingWith", "No device type found.");
			throw elsException;
		}
        return deviceTypes;
    }

	
	public List<DeviceType> getAllowedTypesInStarredClubbing(final String locale) throws ELSException {
		String starred = ApplicationConstants.STARRED_QUESTION;
		String unstarred = ApplicationConstants.UNSTARRED_QUESTION;
		String query="SELECT m FROM DeviceType m" +
						" WHERE (m.type=:starred OR m.type=:unstarred)" +
						" AND m.locale=:locale" +
						" ORDER BY m.name "+ApplicationConstants.ASC;
		
		List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
		TypedQuery<DeviceType> jpQuery = null;
		
		try{
			
			jpQuery = this.em().createQuery(query,DeviceType.class); 
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("starred", starred);
			jpQuery.setParameter("unstarred", unstarred);
        
			deviceTypes = jpQuery.getResultList();
        	
        }catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("DepartmentRepository_List<DeviceType>_findDeviceTypesStartingWith", "No device type found.");
			throw elsException;
		}
        
		return deviceTypes;
	}
	
	public List<DeviceType> getAllowedTypesInMotionClubbing(final String locale) throws ELSException {
		
		String query="SELECT m FROM DeviceType m" +
						" WHERE m.locale=:locale" +
						" AND (m.type LIKE :questions" +
						" OR m.type LIKE :motions)" +
						" ORDER BY m.name "+ApplicationConstants.ASC;
		
		List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
		TypedQuery<DeviceType> jpQuery = null;
		
		try{
			
			jpQuery = this.em().createQuery(query,DeviceType.class); 
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("questions", ApplicationConstants.DEVICE_QUESTIONS + "%");
			jpQuery.setParameter("motions", ApplicationConstants.DEVICE_MOTIONS + "%");
        
			deviceTypes = jpQuery.getResultList();
        	
        }catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("DepartmentRepository_List<DeviceType>getAllowedTypesInMotionClubbing", "No device type found.");
			throw elsException;
		}
        
		return deviceTypes;
	}
}
