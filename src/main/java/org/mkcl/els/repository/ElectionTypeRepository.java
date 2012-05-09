package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.ElectionType;
import org.springframework.stereotype.Repository;

@Repository
public class ElectionTypeRepository extends BaseRepository<ElectionType, Long> {

	@SuppressWarnings("unchecked")
	public List<ElectionType> findByHouseType(final String houseType, final String locale) {
		String query = null;
		if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
			query = "SELECT e FROM ElectionType e WHERE e.locale='" + locale + "'";
		} else {
			query = "SELECT e FROM ElectionType e WHERE e.locale='" + locale 
				+ "' AND e.houseType.type='" + houseType + "'";
		} 
		return this.em().createQuery(query).getResultList();
	}
}
