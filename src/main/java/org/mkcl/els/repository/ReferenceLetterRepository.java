package org.mkcl.els.repository;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.ReferenceLetter;
import org.springframework.stereotype.Repository;

@Repository
public class ReferenceLetterRepository extends BaseRepository<ReferenceLetter, Long>{

	public ReferenceLetter findLatestHavingGivenDevice(final String deviceId, final String referenceFor, final String locale) {
		ReferenceLetter latestReferenceLetterHavingGivenDevice = null;
		
		String queryString = "SELECT * FROM reference_letters"
				+ " WHERE ("
			    + " 		device_ids=:deviceId"
			    + "  	 OR device_ids LIKE CONCAT(:deviceId, ',%')"
			    + "  	 OR device_ids LIKE CONCAT('%,', :deviceId, ',%')"
			    + "  	 OR device_ids LIKE CONCAT('%,', :deviceId)"
			    + " )"
			    + " AND reference_for=:referenceFor"
				+ " AND locale=:locale"
				+ " ORDER BY dispatch_date DESC";
		
		Query query = this.em().createNativeQuery(queryString, ReferenceLetter.class);
		query.setParameter("deviceId", deviceId);
		query.setParameter("referenceFor", ApplicationConstants.INTIMATION_FOR_REPLY_FROM_DEPARTMENT);
		query.setParameter("locale", locale);
		
		@SuppressWarnings("unchecked")
		List<ReferenceLetter> refLetters = query.getResultList();
		if(refLetters!=null && !refLetters.isEmpty()) {
			latestReferenceLetterHavingGivenDevice = refLetters.get(0);
		}
		
		return latestReferenceLetterHavingGivenDevice;
	}

}