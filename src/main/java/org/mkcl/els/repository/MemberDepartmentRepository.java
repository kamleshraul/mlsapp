package org.mkcl.els.repository;

import java.util.Date;
import java.util.List;
import javax.persistence.TypedQuery;

import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.MemberMinister;
import org.springframework.stereotype.Repository;


@Repository
public class MemberDepartmentRepository extends BaseRepository<MemberMinister, Long> {
	
	public List<Department> findActiveDepartmentsOnDate(final Date onDate, 
			final String locale) {
		List<Department> activeDepartmentsOnDate = null;
		
		try {
			String strQuery = "SELECT DISTINCT d FROM MemberDepartment md " +
					"JOIN md.department d " +
					"WHERE md.locale=:locale " +
					"AND d.isExpired=:isExpired " +
					"AND (md.toDate IS NULL OR md.toDate>=:onDate)";
			
			TypedQuery<Department> query = this.em().createQuery(strQuery, Department.class);
			query.setParameter("locale", locale);
			query.setParameter("isExpired", false);			
			query.setParameter("onDate", onDate);
			
			activeDepartmentsOnDate = query.getResultList();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return activeDepartmentsOnDate;
	}	
	
}