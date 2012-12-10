package org.mkcl.els.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.mkcl.els.domain.CustomParameter;

public class DateUtil {

	public static Date getCurrentDate() {
		CustomParameter parameter = 
			CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
		return DateUtil.getCurrentDate(parameter.getValue());
	}
	
	public static Date getCurrentDate(final String dateFormat) {
		Date currentDate = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String strCurrentDate = sdf.format(currentDate);
		Date date = currentDate;
		try {
			date = new SimpleDateFormat(dateFormat).parse(strCurrentDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
}
