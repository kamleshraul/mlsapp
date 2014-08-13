package org.mkcl.els.repository;

import java.io.Serializable;

import javax.persistence.TypedQuery;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.CutMotionDate;
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

}
