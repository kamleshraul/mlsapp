package org.mkcl.els.repository;

import java.io.Serializable;

import javax.persistence.Query;

import org.mkcl.els.domain.Ordinance;
import org.springframework.stereotype.Repository;

@Repository
public class OrdinanceRepository extends BaseRepository<Ordinance, Serializable> {
	
	public Ordinance findByYearAndNumber(final Integer ordYear, final Integer ordNumber) {
		String queryString = "SELECT ord From Ordinance ord WHERE ord.year=:ordYear AND ord.number=:ordNumber";
		Query query = this.em().createQuery(queryString);
		query.setParameter("ordYear", ordYear);
		query.setParameter("ordNumber", ordNumber);
		try {
			Ordinance ordinance = (Ordinance) query.getSingleResult();
			return ordinance;
		} catch(Exception ex) {
			return null;
		}
		
	}
	
}
