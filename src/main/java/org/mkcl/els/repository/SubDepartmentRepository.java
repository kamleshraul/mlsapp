package org.mkcl.els.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.SubDepartment;
import org.springframework.stereotype.Repository;

@Repository
public class SubDepartmentRepository extends BaseRepository<ApplicationLocale, Long> {

	public List<SubDepartment> findAllSubDepartment(final String locale) {
		String query = "Select m FROM SubDepartment m where m.locale= '"+locale+"' and isExpired is false order by m.id";

		TypedQuery<SubDepartment> tQuery = this.em().createQuery(query, SubDepartment.class);

		List<SubDepartment> applicationLocale = tQuery.getResultList();

		return applicationLocale;
	}
	
	public List<SubDepartment> findAllCurrentSubDepartments(final String locale) {
		String query = "Select m FROM SubDepartment m where m.locale= '"+locale+"' and isExpired is false order by m.name";

		TypedQuery<SubDepartment> tQuery = this.em().createQuery(query, SubDepartment.class);

		List<SubDepartment> subdepartments = tQuery.getResultList();

		return subdepartments;
	}
	
	public Ministry findMinistry(final Long subdepartmentId, final Date onDate) {
		Ministry ministry = null;
		
		String strQuery =  "SELECT DISTINCT mi.* FROM members_ministries mm"
					    + " INNER JOIN ministries mi ON (mi.id=mm.ministry_id)"
					    + " INNER JOIN members_departments md ON (md.member_ministry_id=mm.id)"
					    + " INNER JOIN `memberdepartments_subdepartments` mdsd ON (mdsd.member_department_id=md.id)"
					    + " INNER JOIN subdepartments sd ON (sd.id=mdsd.subdepartment_id)"
					    + " WHERE mm.ministry_from_date<=:onDate AND (mm.ministry_to_date>=:onDate OR mm.ministry_to_date IS NULL)"
					    + " AND sd.id=:subdepartmentId";
		
		javax.persistence.Query query = this.em().createNativeQuery(strQuery, Ministry.class);
		query.setParameter("onDate", onDate);
		query.setParameter("subdepartmentId", subdepartmentId);
		
		try {
			ministry = (Ministry) query.getSingleResult();
		} catch(Exception e) {
			//logger.error(e.getMessage());
			ministry = null;
		}
		
		return ministry;
	}

}