package org.mkcl.els.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.domain.ApplicationLocale;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationLocaleRepository extends BaseRepository<ApplicationLocale, Long> {

	public List<ApplicationLocale> findAllLocale() {
		String query = "Select m FROM ApplicationLocale m order by m.id";

		TypedQuery<ApplicationLocale> tQuery = this.em().createQuery(query, ApplicationLocale.class);

		List<ApplicationLocale> applicationLocale = tQuery.getResultList();

		return applicationLocale;
	}

}