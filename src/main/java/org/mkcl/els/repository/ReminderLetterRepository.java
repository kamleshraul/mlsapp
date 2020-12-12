package org.mkcl.els.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.ReminderLetter;
import org.mkcl.els.domain.Role;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class ReminderLetterRepository extends BaseRepository<ReminderLetter, Long>{	

	public String findDelimitedQISRoles(final String locale) throws ELSException {
		String strquery="SELECT m FROM Role m" +
						" WHERE m.locale=:locale"+
						" AND (m.type LIKE :pattern" +
						" OR m.type='SUPER_ADMIN') ORDER BY m.type";
		try{
			TypedQuery<Role> query=this.em().createQuery(strquery, Role.class);
			query.setParameter("locale", locale);
			query.setParameter("pattern", "QIS_%");
			List<Role> roles=query.getResultList();
			StringBuffer buffer=new StringBuffer();
			for(Role i:roles){
				buffer.append(i.getType()+",");
			}
			buffer.deleteCharAt(buffer.length()-1);
			return buffer.toString();
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("RoleRepository_String_findDelimitedQISRoles", "Roles Not found");
			throw elsException;
        }
	
	}
	
	public ReminderLetter findLatestByFieldNames(final Map<String, String> reminderLetterIdentifiers, final String locale) throws ELSException {
		ReminderLetter latestReminderLetter = null;
		
		String queryString = " SELECT r FROM ReminderLetter r " +
							 " WHERE r.locale=:locale " +
							 " AND r.houseType=:houseType " +
							 " AND r.deviceType=:deviceType " +
							 " AND r.reminderFor=:reminderFor " +
							 " AND r.reminderTo=:reminderTo " +
							 " AND r.reminderNumberStartLimitingDate=:reminderNumberStartLimitingDate " +
							 " ORDER BY r.id DESC ";
		
		try {
			TypedQuery<ReminderLetter> query=this.em().createQuery(queryString, ReminderLetter.class);
			query.setParameter("locale", locale);
			query.setParameter("houseType", reminderLetterIdentifiers.get("houseType"));
			query.setParameter("deviceType", reminderLetterIdentifiers.get("deviceType"));
			query.setParameter("reminderFor", reminderLetterIdentifiers.get("reminderFor"));
			query.setParameter("reminderTo", reminderLetterIdentifiers.get("reminderTo"));
			query.setParameter("reminderNumberStartLimitingDate", reminderLetterIdentifiers.get("reminderNumberStartLimitingDate"));
			List<ReminderLetter> reminderLettersList = query.getResultList();
			if(reminderLettersList!=null) {
				Date reminderNumberEndLimitingDate = FormaterUtil.formatStringToDate(reminderLetterIdentifiers.get("reminderNumberEndLimitingDate"), ApplicationConstants.DB_DATEFORMAT);;
				for(ReminderLetter r: reminderLettersList) {
					Date rNumberEndLimitingDate = FormaterUtil.formatStringToDate(r.getReminderNumberEndLimitingDate(), ApplicationConstants.DB_DATEFORMAT);
					if(rNumberEndLimitingDate.compareTo(reminderNumberEndLimitingDate)<=0) {
						latestReminderLetter = r;
						break;
					}
				}
			}
		} catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ReminderLetterRepository_ReminderLetter_findLatestByFieldNames", "Latest ReminderLetter Not found");
			throw elsException;
        }
		
		return latestReminderLetter;
	}

}
