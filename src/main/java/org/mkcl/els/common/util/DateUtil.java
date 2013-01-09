package org.mkcl.els.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
	
	public static List<Date> findAllSundaysInYear(final Integer year) {
		List<Date> sundays = new ArrayList<Date>();
		
		String yearStr = year.toString();
		
		String startYearDateStr = "01/01/"+yearStr;
		String endYearDateStr = "31/12/"+yearStr;		
				
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date startYearDate = null;
		Date endYearDate = null;
		try {
			startYearDate = dateFormat.parse(startYearDateStr);
			endYearDate = dateFormat.parse(endYearDateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sf=new SimpleDateFormat("EEEE");
		if(startYearDate != null && endYearDate !=null) {
			Calendar start = Calendar.getInstance();
	    	start.setTime(startYearDate);    	
	    	Calendar end = Calendar.getInstance();
	    	end.setTime(endYearDate);		
	    	for (; !start.after(end); start.add(Calendar.DATE, 1)) {
	    		Date current = start.getTime();
	    		if(sf.format(current).equals("Sunday")){
	    			sundays.add(current);
	    		}
	    	}
		}
		return sundays;
	}	
}
