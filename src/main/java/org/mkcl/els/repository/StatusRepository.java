package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Status;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class StatusRepository extends BaseRepository<Status, Serializable>{

	public List<Status> findStartingWith(final String pattern,final String sortBy,final String sortOrder,final String locale) throws ELSException{
		String strQuery="SELECT s FROM Status s" +
				" WHERE s.type LIKE:pattern ORDER BY s.type "+sortOrder;
		try{
			TypedQuery<Status> query=this.em().createQuery(strQuery, Status.class);
			query.setParameter("pattern", pattern+"%");
			List<Status> statuses=new ArrayList<Status>();
			statuses=query.getResultList();
			return statuses;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StatusRepository_List<Status>_findStartingWith", "Status Not found");
			throw elsException;
		}


	}

	public List<Status> findAssistantQuestionStatus(final String sortBY,
			final String sortOrder,final String locale) throws ELSException {
		try{
			String strQuery="SELECT m FROM Status m" +
					" WHERE m.locale=:locale" +
					" AND (m.type LIKE 'question_%' OR m.type='questions_submit')"+
					" AND m.type<>'questions_complete' and m.type<>'questions_incomplete'"+
					" ORDER BY m.priority "+ApplicationConstants.ASC;
			TypedQuery<Status> query=this.em().createQuery(strQuery, Status.class);
			query.setParameter("locale", locale);
			return query.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StatusRepository_List<Status>_findStartingWith", "Status Not found");
			throw elsException;
		}

	}

	public List<Status> findStatusContainedIn(final String commadelimitedStatusTypes,
			final String locale) throws ELSException {
		try{
			String initialQuery="SELECT s FROM Status s WHERE s.locale='"+locale+"' ";
			StringBuffer buffer=new StringBuffer();
			String[] statusAllowed=commadelimitedStatusTypes.split(",");
			for(String i:statusAllowed){
				if(!i.isEmpty()){
					/**** trim i since there can be extra white spaces present ****/
					buffer.append(" (s.type='"+i.trim()+"') OR");
				}
			}
			buffer.deleteCharAt(buffer.length()-1);
			buffer.deleteCharAt(buffer.length()-1);
			String query=initialQuery+" AND ("+buffer.toString()+") ORDER BY s.priority "+ApplicationConstants.DESC
					+",s.name "+ApplicationConstants.ASC;
			return this.em().createQuery(query, Status.class).getResultList();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StatusRepository_List<Status>_findStatusContainedIn", "Status Not found");
			throw elsException;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Status> findStatusContainedIn(final String commadelimitedStatusTypes, final String locale, final String sortOrder) {
 		String initialQuery="SELECT s FROM Status s WHERE s.locale='"+locale+"' ";
		StringBuffer buffer=new StringBuffer();
		String[] statusAllowed=commadelimitedStatusTypes.split(",");
		for(String i:statusAllowed){
			if(!i.isEmpty()){
				/**** trim i since there can be extra white spaces present ****/
				buffer.append(" (s.type='"+i.trim()+"') OR");
			}
		}
		if(buffer.length()!=0) {
			buffer.deleteCharAt(buffer.length()-1);
			buffer.deleteCharAt(buffer.length()-1);
		}
		String query = null;
		if(!buffer.toString().isEmpty()) {
			query=initialQuery+" AND ("+buffer.toString()+") ORDER BY s.priority "+sortOrder
					+",s.name "+ApplicationConstants.ASC;
		} else {
			query=initialQuery+" ORDER BY s.priority "+sortOrder
					+",s.name "+ApplicationConstants.ASC;
		}
		return this.em().createQuery(query).getResultList();		
	}

	public List<Status> findStatusWithSupportOrderContainedIn(final String commadelimitedStatusTypes,
			final String locale) throws ELSException {
		try{
			String initialQuery="SELECT s FROM Status s WHERE s.locale='"+locale+"' ";
			StringBuffer buffer=new StringBuffer();
			String[] statusAllowed=commadelimitedStatusTypes.split(",");
			for(String i:statusAllowed){
				if(!i.isEmpty()){
					/**** trim i since there can be extra white spaces present ****/
					buffer.append(" (s.type='"+i.trim()+"') OR");
				}
			}
			buffer.deleteCharAt(buffer.length()-1);
			buffer.deleteCharAt(buffer.length()-1);
			String query=initialQuery+" AND ("+buffer.toString()+") AND s.supportOrder>0 ORDER BY s.supportOrder "+ApplicationConstants.ASC
					+",s.name "+ApplicationConstants.ASC;
			return this.em().createQuery(query, Status.class).getResultList();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StatusRepository_List<Status>_findStatusContainedIn", "Status Not found");
			throw elsException;
		}
	}

}
