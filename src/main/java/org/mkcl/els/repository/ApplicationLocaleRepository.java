package org.mkcl.els.repository;

import java.util.List;

import javax.persistence.Query;
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
	
	public String findLanguageTypeFromLocale(final String locale) {
		String query = "Select m FROM ApplicationLocale m WHERE locale=:locale";

		TypedQuery<ApplicationLocale> tQuery = this.em().createQuery(query, ApplicationLocale.class);
		tQuery.setParameter("locale", locale);

		List<ApplicationLocale> applicationLocales = tQuery.getResultList();
		
		if(applicationLocales!=null && !applicationLocales.isEmpty()) {
			return applicationLocales.get(0).getLanguageType();
		} else {
			return "";
		}
	}
	
	public String findLocaleFromLanguageType(final String languageType) {
		String query = "Select m FROM ApplicationLocale m WHERE languageType=:languageType";

		TypedQuery<ApplicationLocale> tQuery = this.em().createQuery(query, ApplicationLocale.class);
		tQuery.setParameter("languageType", languageType);

		List<ApplicationLocale> applicationLocales = tQuery.getResultList();
		
		if(applicationLocales!=null && !applicationLocales.isEmpty()) {
			return applicationLocales.get(0).getLocale();
		} else {
			return "";
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findAllLocales() {
		String strQuery = "SELECT DISTINCT m.locale FROM ApplicationLocale m order by m.id";

		Query query = this.em().createQuery(strQuery);

		List<String> allLocales = query.getResultList();

		return allLocales;
	}

}