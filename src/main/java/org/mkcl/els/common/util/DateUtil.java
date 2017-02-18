package org.mkcl.els.common.util;

import java.util.Date;

public class DateUtil {
	
	/**
	 * Same as compareTo() method in java.util.Date except for this compares only date parts of given dates
	 * @param date1
	 * @param date2
	 * @return int
	 */
	public static int compareDatePartOnly(Date date1, Date date2) {
		
		String date1Formatted = FormaterUtil.formatDateToString(date1, ApplicationConstants.DB_DATEFORMAT);
		date1 = FormaterUtil.formatStringToDate(date1Formatted, ApplicationConstants.DB_DATEFORMAT);
		
		String date2Formatted = FormaterUtil.formatDateToString(date2, ApplicationConstants.DB_DATEFORMAT);
		date2 = FormaterUtil.formatStringToDate(date2Formatted, ApplicationConstants.DB_DATEFORMAT);
		
		return date1.compareTo(date2);

	}

}
