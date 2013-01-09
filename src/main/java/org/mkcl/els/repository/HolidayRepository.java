/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.DocumentRepository.java
 * Created On: Jan 5, 2012
 */
package org.mkcl.els.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.Holiday;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class DocumentRepository.
 * @author amitd
 * @author sandeeps
 * @version v1.0.0
 */
@Repository
public class HolidayRepository extends BaseRepository<Holiday, Long> {

	public List<Date> findAllSecondAndForthSaturdaysInYear(final Integer year) throws ParseException {    	
    	String yearStr = year.toString();
    	String query = "";    	
    	for(Integer i = 1; i <= 12; i++) {
    		String monthNumber = i.toString();
    		if(monthNumber.length() == 1) {
    			monthNumber = "0" + monthNumber;
    		}
	    	query += 
	    			"SELECT STR_TO_DATE('" + yearStr + "-" + monthNumber + "-01' - INTERVAL DAYOFWEEK('" + yearStr + "-" + monthNumber + "-01') DAY + INTERVAL 2 WEEK, '%Y-%m-%d') "
	    			+ "UNION ALL "
	    			+ "SELECT STR_TO_DATE('" + yearStr + "-" + monthNumber + "-01' - INTERVAL DAYOFWEEK('" + yearStr + "-" + monthNumber + "-01') DAY + INTERVAL 4 WEEK, '%Y-%m-%d')";
	    	if(i<12) {
	    		query += "UNION ALL ";
	    	}
	    }
		List<Date> dates=this.em().createNativeQuery(query).getResultList();
		return dates;
	}

	public List<Holiday> findAllByYear(final Integer year, String locale) { 
		List<Holiday> holidaysFromMasterInYear = new ArrayList<Holiday>();
		List<Holiday> allHolidaysFromMaster = Holiday.findAll(Holiday.class, "name", "asc", locale);	
		if(allHolidaysFromMaster != null) {
			for(Holiday i: allHolidaysFromMaster) {
				if(i.getYear().equals(year)) {
					holidaysFromMasterInYear.add(i);
				}
			}
		}
		return holidaysFromMasterInYear;
	}
}
