package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.domain.Language;
import org.springframework.stereotype.Repository;

@Repository
public class LanguageRepository extends BaseRepository<Language, Long> {

	@SuppressWarnings("unchecked")
	public List<Language> findAllSortedByPriorityAndName(String locale){
		String query="SELECT m FROM Language m WHERE m.locale='"+locale+"' ORDER BY m.priority,m.name";
		return this.em().createQuery(query).getResultList();
	}
}
