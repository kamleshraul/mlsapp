package org.mkcl.els.repository;

import java.io.Serializable;

import javax.persistence.TypedQuery;

import org.mkcl.els.domain.SectionOrder;
import org.springframework.stereotype.Repository;

@Repository
public class SectionOrderRepository extends BaseRepository<SectionOrder, Serializable> {

	public Integer findSequenceNumberInSeries(final Long seriesId, final String name, final String locale) {
		Integer sequenceNumber = null;
		String queryString = "SELECT so.sequenceNumber FROM SectionOrder so"
				 + " WHERE so.sectionOrderSeries.id=:seriesId AND so.name=:name AND so.locale=:locale";
		TypedQuery<Integer> query = this.em().createQuery(queryString, Integer.class);
		query.setParameter("seriesId", seriesId);
		query.setParameter("name", name);
		query.setParameter("locale", locale);
		Object result = query.getSingleResult();
		if(result!=null) {
			sequenceNumber = (Integer) result;
		}
		return sequenceNumber;
	}
	
}
