package org.mkcl.els.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.domain.ApplicationLocale;
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

}