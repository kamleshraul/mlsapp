package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.CutMotionDate;
import org.mkcl.els.domain.CutMotionDateDraft;
import org.mkcl.els.domain.CutMotionDepartmentDatePriority;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

@Repository
public class CutMotionDateRepository extends BaseRepository<CutMotionDate, Serializable> {

	public Boolean removeDepartmentDatePriority(final Long cutMotionDateId, final Long cutMotionDepartmentDatePriorityId) {
		try {
			Query query1 = Query.findByFieldName(Query.class, "keyField", ApplicationConstants.CUTMOTIONDATE_CUTMOTIONDEPARTMENTDATEPRIORITY_DELETE,"");
			String strquery1 = query1.getQuery();
			javax.persistence.Query q = this.em().createNativeQuery(strquery1);
			q.setParameter("cutMotionDateId", cutMotionDateId);
			q.setParameter("cutMotionDepartmentDatePriorityId", cutMotionDepartmentDatePriorityId);
			q.executeUpdate();
			
			Query query2 = Query.findByFieldName(Query.class, "keyField", ApplicationConstants.CUTMOTIONDEPARTMENTDATEPRIORITY_DELETE, "");
			String strquery2 = query2.getQuery();
			javax.persistence.Query q1 = this.em().createNativeQuery(strquery2);
			q1.setParameter("cutMotionDepartmentDatePriorityId", cutMotionDepartmentDatePriorityId);
			q1.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
	
	
	public CutMotionDate findCutMotionDateSessionDeviceType(final Session session, final DeviceType deviceType, final String locale)
	throws Exception{
		StringBuffer strQuery = new StringBuffer("SELECT cmd FROM CutMotionDate cmd" +
								" WHERE cmd.session.id=:sessionId" + 
								" AND cmd.deviceType.id=:deviceTypeId" + 
								" AND cmd.locale=:locale");
		
		TypedQuery<CutMotionDate> query = this.em().createQuery(strQuery.toString(), CutMotionDate.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("locale", locale);
		CutMotionDate cutMotionDate = query.getSingleResult();
		
		return cutMotionDate;
	}
	
	public List<CutMotionDepartmentDatePriority> findDepartmentDatePriorityDetailsForGivenCutMotionDate(final Long cutMotionDateId) {
		if(cutMotionDateId==null) {
			return null;
		}
		List<CutMotionDepartmentDatePriority> drafts = null;
		
		String queryString = "SELECT cdp FROM CutMotionDepartmentDatePriority cdp WHERE cutMotionDateId=:cutMotionDateId";
		TypedQuery<CutMotionDepartmentDatePriority> query = this.em().createQuery(queryString, CutMotionDepartmentDatePriority.class);
		query.setParameter("cutMotionDateId", cutMotionDateId.toString());
		drafts = query.getResultList();
		return drafts;
	} 
	
	public List<CutMotionDateDraft> findDraftsForGivenCutMotionDate(final Long cutMotionDateId) {
		if(cutMotionDateId==null) {
			return null;
		}
		List<CutMotionDateDraft> drafts = null;
		
		String queryString = "SELECT cdd FROM CutMotionDateDraft cdd WHERE deviceId=:cutMotionDateId";
		TypedQuery<CutMotionDateDraft> query = this.em().createQuery(queryString, CutMotionDateDraft.class);
		query.setParameter("cutMotionDateId", cutMotionDateId.toString());
		drafts = query.getResultList();
		return drafts;
	}
	
	public Date findPublishingDateOfCutMotionDate(final CutMotionDate cutMotionDate) {
		Date publishingDate = null;
		try {
			StringBuffer strQuery = new StringBuffer("SELECT cmdd.editedOn FROM CutMotionDate cmd" +
								" JOIN cmd.drafts cmdd" +
								" WHERE cmd.session.id=:sessionId" + 
								" AND cmd.deviceType.id=:deviceTypeId" + 
								" AND cmd.locale=:locale" +
								" AND cmdd.internalStatus.type=:cutMotionDateAdmission" +
								" ORDER BY cmdd.editedOn");
			
			TypedQuery<Date> query = this.em().createQuery(strQuery.toString(), Date.class);
			query.setParameter("sessionId", cutMotionDate.getSession().getId());
			query.setParameter("deviceTypeId", cutMotionDate.getDeviceType().getId());
			query.setParameter("cutMotionDateAdmission", ApplicationConstants.CUTMOTIONDATE_FINAL_DATE_ADMISSION);
			query.setParameter("locale", cutMotionDate.getLocale());
			query.setMaxResults(1);
			publishingDate = query.getSingleResult();
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}		
		
		return publishingDate;
	}
	
	public List<Date> findActiveDiscussionDates(final CutMotionDate cutMotionDate) {
		List<Date> activeDiscussionDates = new ArrayList<Date>();
		try {
			StringBuffer strQuery = new StringBuffer("SELECT DISTINCT cmdd.discussionDate FROM CutMotionDate cmd" +
								" JOIN cmd.departmentDates cmdd" +
								" WHERE cmd.session.id=:sessionId" + 
								" AND cmd.deviceType.id=:deviceTypeId" + 
								" AND cmd.locale=:locale" +								
								" ORDER BY cmdd.discussionDate");
			
			TypedQuery<Date> query = this.em().createQuery(strQuery.toString(), Date.class);
			query.setParameter("sessionId", cutMotionDate.getSession().getId());
			query.setParameter("deviceTypeId", cutMotionDate.getDeviceType().getId());
			query.setParameter("locale", cutMotionDate.getLocale());			
			activeDiscussionDates = query.getResultList();
			
		} catch(Exception e) {
			e.printStackTrace();
			return activeDiscussionDates;
		}		
		
		return activeDiscussionDates;
	}
	
	public List<CutMotionDepartmentDatePriority> findDepartmentDatesForDiscussionDate(final CutMotionDate cutMotionDate, final Date discussionDate) {
		String queryString = "SELECT DISTINCT cmdd FROM CutMotionDate cmd " +
							 "JOIN cmd.departmentDates cmdd " +
							 "WHERE cmd.id=:cutMotionDateId " +
							 "AND cmdd.discussionDate=:discussionDate " +
							 "ORDER BY cmdd.priority";
		
		TypedQuery<CutMotionDepartmentDatePriority> query = this.em().createQuery(queryString, CutMotionDepartmentDatePriority.class);
		query.setParameter("cutMotionDateId", cutMotionDate.getId());
		query.setParameter("discussionDate", discussionDate);
		
		return query.getResultList();
	}
	
	public List<CutMotionDepartmentDatePriority> findDepartmentDatesForDepartment(final CutMotionDate cutMotionDate, final Department department) {
		String queryString = "SELECT DISTINCT cmdd FROM CutMotionDate cmd " +
							 "JOIN cmd.departmentDates cmdd " +
							 "JOIN cmdd.department dept " +
							 "WHERE cmd.id=:cutMotionDateId " +
							 "AND dept.id=:departmentId " +
							 "ORDER BY cmdd.priority";
		
		TypedQuery<CutMotionDepartmentDatePriority> query = this.em().createQuery(queryString, CutMotionDepartmentDatePriority.class);
		query.setParameter("cutMotionDateId", cutMotionDate.getId());
		query.setParameter("departmentId", department.getId());
		
		return query.getResultList();
	}

}
