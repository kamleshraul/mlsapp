package org.mkcl.els.repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.SessionVO;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionDates;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.User;
import org.springframework.stereotype.Repository;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.trg.search.Search;

@Repository
public class SessionRepository extends BaseRepository<Session, Long>{

    public Session findLatestSession(final HouseType houseType,final Integer sessionYear) throws ELSException{
        //Inorder to find latest session we need two things.first housetype and then session year
        //This is because session are numbered from 1,2,3,4,.. each year.The session whose start date occurs
        //before is given a small number than the one that occurs after.e.g budget session which occurs
        //in the month of march is given number 1 and so on.
        //we will sort all the sessions of given house type and session year according to number in descending
        //order and the one at position 0(top) will be the latest session
      try{
    	  String strQuery="SELECT s FROM Session s WHERE s.house.type=:houseType AND s.year=:sessionYear"+
       		   " ORDER BY s.number DESC";
          TypedQuery<Session> query=this.em().createQuery(strQuery, Session.class);
          query.setParameter("houseType", houseType);
          query.setParameter("sessionYear", sessionYear);
          List<Session> sessions= query.getResultList();
          if(!sessions.isEmpty()){
      			return sessions.get(0);
      		}else{
      		return new Session();
      		}
      }catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("SessionRepository_Session_findLatestSession", "Latest Session  Not found");
			throw elsException;
		}
    	
    }

    public List<Session> findSessionsByHouseTypeAndYear(final House house,final Integer year) throws ELSException{
    	String strQuery="SELECT s FROM Session s WHERE s.house.type=:houseType AND s.year=:year"+
     		   " ORDER BY s.startDate DESC";
    	try{
    		TypedQuery<Session> query=this.em().createQuery(strQuery, Session.class);
        	query.setParameter("houseType", house.getType());
        	query.setParameter("year", year);
            List<Session> sessions=query.getResultList();
            return sessions;
    	}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("SessionRepository_List<Session>_findSessionsByHouseAndYear", "Session  Not found");
			throw elsException;
		}
    	

    }
    
    public List<Session> findSessionsByHouseAndDateLimits(final House house,final Date lowerLimit, final Date upperLimit){
    	List<Session> sessionsByGivenHouseAndDateLimits = new ArrayList<Session>();    			
    	String queryString = "SELECT s FROM Session s WHERE s.house.id="+house.getId()+
    							" AND s.startDate >= :lowerLimit" +
    							" AND s.startDate <= :upperLimit" +
    							" ORDER BY s.startDate " + ApplicationConstants.ASC;
    	Query query = this.em().createQuery(queryString);
    	query.setParameter("lowerLimit", lowerLimit, TemporalType.DATE);
    	query.setParameter("upperLimit", upperLimit, TemporalType.DATE);
    	List resultList = query.getResultList();
    	if(resultList!=null) {
    		sessionsByGivenHouseAndDateLimits = resultList;
    	}        
        return sessionsByGivenHouseAndDateLimits;
    }

	public Session findSessionByHouseSessionTypeYear(final House house,final SessionType sessionType,
			final Integer sessionYear) throws ELSException {
		String strQuery="SELECT DISTINCT s FROM Session s WHERE s.house=:house"+
	    		   " AND s.type=:sessionType AND s.year=:sessionYear"+
	    		   " ORDER BY s.number DESC";
		 Query query=this.em().createQuery(strQuery);
		 query.setParameter("house", house);
		 query.setParameter("sessionType", sessionType);
		 query.setParameter("sessionYear", sessionYear);
		 try{
			 Session session=(Session) query.getSingleResult();
		     return session;
		 }catch(NoResultException e){
			 logger.warn(e.getMessage());
			return null;
		 }catch(EntityNotFoundException e){
			logger.error(e.getMessage());
			return null;
		 }catch(Exception e){
				e.printStackTrace();
				logger.error(e.getMessage());
				ELSException elsException=new ELSException();
				elsException.setParameter("SessionRepository_Session_findSessionByHouseSessionTypeYear", "Session  Not found");
				throw elsException;
			}
	     
	}

    public Session findSessionByHouseTypeSessionTypeYear(final HouseType houseType,
            final SessionType sessionType, final Integer sessionYear) throws ELSException {
    	String strQuery="SELECT s FROM Session s WHERE s.house.type=:houseType"+
	    		   " AND s.type=:sessionType AND s.year=:sessionYear ORDER BY s.number DESC";
	    Query query=this.em().createQuery(strQuery);
	    query.setParameter("houseType", houseType);
	    query.setParameter("sessionType", sessionType);
	    query.setParameter("sessionYear", sessionYear);
	    try{
			 Session session=(Session) query.getSingleResult();
		     return session;
		 }catch(NoResultException e){
			 logger.warn(e.getMessage());
			return null;
		 }catch(EntityNotFoundException e){
			logger.error(e.getMessage());
			return null;
		 }catch(Exception e){
				e.printStackTrace();
				logger.error(e.getMessage());
				ELSException elsException=new ELSException();
				elsException.setParameter("SessionRepository_Session_findSessionByHouseTypeSessionTypeYear", "Session  Not found");
				throw elsException;
			}
    }

    public Session findLatestSession(final HouseType houseType) throws ELSException {
    	/*
    	 * to find the most recent session by housetype we select all those sessions having given house
    	 * arranged according to their start date in decreasing order.The entry at 0 position is
    	 * the most recent session.Also if no entries are present in session then we return an empty session object
    	 * which must be checked before performing any operation.This is done to catch JPA exception "NoEntityFound"
    	 */
    	String strQuery="SELECT s FROM Session s WHERE s.house.type=:houseTypeId ORDER BY s.startDate DESC";
    	try{
    		TypedQuery<Session> query=this.em().createQuery(strQuery, Session.class);
        	query.setParameter("houseTypeId", houseType);
        	List<Session> sessions= query.getResultList();
        	if(!sessions.isEmpty()){
        		return sessions.get(0);
        	}else{
        		return new Session();
        	}
    	}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("SessionRepository_Session_findLatestSession", "Session  Not found");
			throw elsException;
		}
    	
    }
    
    public Session findLatestSessionHavingGivenDeviceTypeEnabled(final HouseType houseType, final DeviceType deviceType) throws ELSException {
    	/*
    	 * to find the most recent session by housetype we select all those sessions having given house
    	 * arranged according to their start date in decreasing order.The entry at 0 position is
    	 * the most recent session.Also if no entries are present in session then we return an empty session object
    	 * which must be checked before performing any operation.This is done to catch JPA exception "NoEntityFound"
    	 */
    	String strQuery="SELECT s FROM Session s WHERE s.house.type.id=:houseTypeId AND s.deviceTypesEnabled LIKE :deviceTypePattern ORDER BY s.startDate DESC";
    	try{
    		TypedQuery<Session> query=this.em().createQuery(strQuery, Session.class);
        	query.setParameter("houseTypeId", houseType.getId());
        	query.setParameter("deviceTypePattern", "%" + deviceType.getType().trim() + "%");
        	List<Session> sessions= query.getResultList();
        	if(!sessions.isEmpty()){
        		return sessions.get(0);
        	}else{
        		return new Session();
        	}
    	}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("SessionRepository_Session_findLatestSession", "Session  Not found");
			throw elsException;
		}
    	
    }

    public List<Session> findSessionsByHouseTypeAndYear(final HouseType houseType,
    		final Integer sessionYear) throws ELSException {
    	String strQuery="SELECT s FROM Session s WHERE s.house.type=:houseType"+
    			" AND s.year=:sessionYear ORDER BY s.startDate DESC";
    	try{
    		TypedQuery<Session> query=this.em().createQuery(strQuery, Session.class);
        	query.setParameter("houseType", houseType);
        	query.setParameter("sessionYear", sessionYear);
        	List<Session> sessions=query.getResultList();
        	return sessions;
    	}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("SessionRepository_List<Session>_findSessionsByHouseTypeAndYear", "Session  Not found");
			throw elsException;
		}
    	
    }
    
    public List<Session> findSessionsByHouseAndYearForGivenDeviceTypeEnabled(final House house,
    		final Integer sessionYear,
    		final DeviceType deviceType) throws ELSException {
    	String strQuery="SELECT s FROM Session s WHERE s.house.id=:houseId"+
    			" AND s.year=:sessionYear" +
    			" AND (s.deviceTypesEnabled LIKE '"+deviceType.getType()+",%'" +
    			" OR s.deviceTypesEnabled LIKE '%,"+deviceType.getType()+",%'" +
    			" OR s.deviceTypesEnabled LIKE '%,"+deviceType.getType()+"')" +
    			" ORDER BY s.startDate DESC";
    	try{
    		TypedQuery<Session> query=this.em().createQuery(strQuery, Session.class);
        	query.setParameter("houseId", house.getId());
        	query.setParameter("sessionYear", sessionYear);
        	List<Session> sessions=query.getResultList();
        	return sessions;
    	}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("SessionRepository_List<Session>_findSessionsByHouseTypeAndYear", "Session  Not found");
			throw elsException;
		}
    	
    }

    public Session find(final Integer sessionyear, final String sessiontype,
            final String housetype) throws ELSException {
    	String strQuery="SELECT s FROM Session s WHERE s.house.type.type=:houseType"+
	    		   " AND s.type.type=:sessiontype AND s.year=:sessionyear"+
	    		   " ORDER BY s.number DESC";
    	Query query=this.em().createQuery(strQuery);
    	query.setParameter("houseType",housetype);
    	query.setParameter("sessiontype",sessiontype);
    	query.setParameter("sessionyear",sessionyear);
    	try{
			 Session session=(Session) query.getSingleResult();
		     return session;
		 }catch(NoResultException e){
			logger.warn(e.getMessage());
			return null;
		 }catch(EntityNotFoundException e){
			logger.error(e.getMessage());
			return null;
		 }catch(Exception e){
				e.printStackTrace();
				logger.error(e.getMessage());
				ELSException elsException=new ELSException();
				elsException.setParameter("SessionRepository_Session_find", "Session  Not found");
				throw elsException;
			}
    }
    
    public List<SessionDates> findSessionDates(final Session session,final String sessionDate) throws ELSException {
    	String strQuery=
	    		   "   SELECT s FROM Session s "
    			   +"  INNER JOIN FETCH s.sessionDates sd "
	    		   +"  WHERE s.id =:sessionId "
	    		   +"  AND sd.sessionDate =:sessionDate ";
	    		   
    	Query query=this.em().createQuery(strQuery);
    	query.setParameter("sessionId",session.getId());
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    	String dateInString =sessionDate ;
    	Date date=null;
		try {
			date = formatter.parse(dateInString);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	query.setParameter("sessionDate", date);
    	
    	try{
    		Session  sessionD= (Session)query.getSingleResult();
    		List<SessionDates> sessionDates = sessionD.getSessionDates();

		     return sessionDates;
		 }catch(NoResultException e){
			logger.warn(e.getMessage());
			return null;
		 }catch(EntityNotFoundException e){
			logger.error(e.getMessage());
			return null;
		 }catch(Exception e){
				e.printStackTrace();
				logger.error(e.getMessage());
				ELSException elsException=new ELSException();
				elsException.setParameter("SessionRepository_Session_find", "Session  Not found");
				throw elsException;
			}
    }

    //--------------------------23012013---------------------------------
  	public List<String> getParametersSetForDeviceType(final Long sessionId,final String deviceType) throws ELSException {
  		org.mkcl.els.domain.Query nativeQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class,
  												"keyField", ApplicationConstants.SESSION_GET_PARAMETER_SET_FOR_DEVICETYPE_QUERY, "");
  		try{
  			String queryStr =nativeQuery.getQuery();	
  	  		Query query = this.em().createNativeQuery(queryStr);
  	  		query.setParameter("deviceType", deviceType+"%");
  	  		query.setParameter("sessionId", sessionId);
  	  		List results = query.getResultList();
  	  		List<String> parameterKeys = new ArrayList<String>();
  	  		for(Object i:results){
  	  			String parameterKey = i.toString();  
  	  			parameterKeys.add(parameterKey);
  	  		}
  	  		
  	  		return parameterKeys;
  		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("SessionRepository_List<String>_getParametersSetForDeviceType", "Session  Not found");
			throw elsException;
		}
  		
  	}
  	//-------------------------------------------------------------------
  	
  	public List<SessionVO> findAllSessionDetailsForGivenHouseType(final HouseType houseType, final Date fromDate, final String locale) {
		List<SessionVO> sessionVOs = new ArrayList<SessionVO>();
		if(houseType!=null) {
			org.mkcl.els.domain.Query query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "SESSION_DETAILS_FOR_GIVEN_HOUSETYPE", locale);
			if(query!=null) {
				Query persistenceQuery=this.em().createNativeQuery(query.getQuery());
				persistenceQuery.setParameter("houseTypeId",houseType.getId());
				if(fromDate==null) {
					persistenceQuery.setParameter("fromDate","");
				} else {
					String fromDateValue = FormaterUtil.formatDateToString(fromDate, ApplicationConstants.DB_DATEFORMAT, "en_US");
					persistenceQuery.setParameter("fromDate",fromDateValue);
				}
				@SuppressWarnings("unchecked")
				List<Object[]> resultList = persistenceQuery.getResultList();
				if(resultList!=null && !resultList.isEmpty()) {
					long order=1;
					for(Object[] i: resultList) {
						SessionVO sessionVO = new SessionVO();
						sessionVO.setId(Long.parseLong(i[0].toString()));
						sessionVO.setDescription(i[1].toString());
						sessionVO.setOrder(order);						
						sessionVOs.add(sessionVO);
						order++;
					}
				}				
			}
		}
		return sessionVOs;
	}
  	
  	public boolean loadSubmissionDatesForDeviceTypeInSession(final Session session, final DeviceType deviceType, final Date fromDate, final Date toDate) {
		boolean isLoadingSuccessful = false;
		
		try {
			this.em().createNativeQuery("call load_submissiondates_for_session_and_devicetype(?,?,?,?)").setParameter(1,session.getId()).setParameter(2,deviceType.getId()).setParameter(3,fromDate).setParameter(4,toDate).executeUpdate();
			isLoadingSuccessful = true;
		} catch(Exception e) {
			logger.error("/****** load_submissiondates_for_session_and_devicetype failed to execute ******/");
			isLoadingSuccessful = false;
		}
		
		return isLoadingSuccessful;
	}
}
