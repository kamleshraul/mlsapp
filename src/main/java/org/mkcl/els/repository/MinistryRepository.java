package org.mkcl.els.repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Ministry;
import org.springframework.stereotype.Repository;

@Repository
public class MinistryRepository extends BaseRepository<Ministry, Long> {

	@SuppressWarnings("unchecked")
	public List<Ministry> findUnassignedMinistries(String locale) {
		CustomParameter dbDateFormat = 
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		Date currDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(dbDateFormat.getValue());
		String strCurrDate = sdf.format(currDate);
	
		/**
		 * I am trying to mimic mm.ministryToDate > CURDATE(), but since
		 * CURDATE() is MySQL specific i am using DB_DATEFORMAT from
		 * custom_parameters.
		 */
		String query = "SELECT m " +
		"FROM Ministry m " +
		"WHERE m.locale = '" + locale + "' AND " +
		"m.id NOT IN " +
			"(SELECT m.id " +
			"FROM MemberMinister mm JOIN mm.ministry m " +
			"WHERE mm.ministryToDate IS NULL OR mm.ministryToDate > '" + strCurrDate + "') " +
		"ORDER BY m.name";
		List<Ministry> ministries = this.em().createQuery(query).getResultList();
		return ministries;
	}
	
}
