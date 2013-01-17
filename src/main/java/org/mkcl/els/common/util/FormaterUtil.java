/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.util.FormaterUtil.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.common.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.CustomParameter;

/**
 * The Class FormaterUtil.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class FormaterUtil {

    private static final String JAN="जानेवारी";

    private static final String FEB="फेब्रुवारी";

    private static final String MAR="मार्च";

    private static final String APR="एप्रिल";

    private static final String MAY="मे";

    private static final String JUNE="जून";

    private static final String JULY="जुलै";

    private static final String AUG="ऑगस्ट";

    private static final String SEP="सप्टेंबर";

    private static final String OCT="ऑक्टोबर";

    private static final String NOV="नोव्हेंबर";

    private static final String DEC="डिसेंबर";

    private static final String TUE="मंगळवार";


    /**
     * Gets the number formatter no grouping.
     *
     * @param locale the locale
     * @return the number formatter no grouping
     */
    public static NumberFormat getNumberFormatterNoGrouping(final String locale) {
        NumberFormat formatWithoutGrouping = null;
        if(locale.equals("mr_IN") || locale.equals("hi_IN")) {
            formatWithoutGrouping = NumberFormat.getInstance(new Locale("hi", "IN"));
            formatWithoutGrouping.setGroupingUsed(false);
        }else{
            formatWithoutGrouping = NumberFormat.getInstance(new Locale("en", "US"));
            formatWithoutGrouping.setGroupingUsed(false);
        }
        return formatWithoutGrouping;
    }

    /**
     * Gets the number formatter grouping.
     *
     * @param locale the locale
     * @return the number formatter grouping
     */
    public static NumberFormat getNumberFormatterGrouping(final String locale) {
        NumberFormat formatWithGrouping = null;
        if(locale.equals("mr_IN") || locale.equals("hi_IN")){
            formatWithGrouping = NumberFormat.getInstance(new Locale("hi", "IN"));
            formatWithGrouping.setGroupingUsed(true);
        }else{
            formatWithGrouping = NumberFormat.getInstance(new Locale("en", "US"));
            formatWithGrouping.setGroupingUsed(true);
        }
        return formatWithGrouping;
    }

    /**
     * Gets the date formatter.
     *
     * @param locale the locale
     * @return the date formatter
     */
    public static SimpleDateFormat getDateFormatter(final String locale) {
        SimpleDateFormat dateFormatter = null;
        if(locale.equals("mr_IN") || locale.equals("hi_IN")){
            dateFormatter = new SimpleDateFormat(ApplicationConstants.SERVER_DATEFORMAT,
            		new Locale("hi", "IN"));
        }else{
            dateFormatter = new SimpleDateFormat(ApplicationConstants.SERVER_DATEFORMAT,
            		new Locale("en", "US"));
        }
        return dateFormatter;
    }

    /**
     * Gets the date formatter.
     *
     * @param dateFormat the date format
     * @param locale the locale
     * @return the date formatter
     */
    public static SimpleDateFormat getDateFormatter(final String dateFormat, final String locale) {
        SimpleDateFormat dateFormatter = null;
        if(locale.equals("mr_IN") || locale.equals("hi_IN")){
            dateFormatter = new SimpleDateFormat(dateFormat,new Locale("hi", "IN"));
        }else{
            dateFormatter = new SimpleDateFormat(dateFormat,new Locale("en", "US"));
        }
        return dateFormatter;
    }

    public static String formatMonthsMarathi(final String date, final String locale) {
        if(locale.equals("mr_IN")) {
            String parts[]=date.split(" ");
            if(parts.length>0){
                String month=parts[1].trim();
                String newDate="";
                if(month.equals("जनवरी")) {
                    newDate=date.replace(month,JAN);
                } else if(month.equals("फ़रवरी")) {
                    newDate=date.replace(month,FEB);
                } else if(month.equals("मार्च")) {
                    newDate=date.replace(month,MAR);
                } else if(month.equals("अप्रैल")) {
                    newDate=date.replace(month,APR);
                } else if(month.equals("मई")) {
                    newDate=date.replace(month,MAY);
                } else if(month.equals("जून")) {
                    newDate=date.replace(month,JUNE);
                } else if(month.equals("जुलाई")) {
                    newDate=date.replace(month,JULY);
                } else if(month.equals("अगस्त")) {
                    newDate=date.replace(month,AUG);
                } else if(month.equals("सितंबर")) {
                    newDate=date.replace(month,SEP);
                } else if(month.equals("अक्‍तूबर")) {
                    newDate=date.replace(month,OCT);
                } else if(month.equals("नवंबर")) {
                    newDate=date.replace(month,NOV);
                } else if(month.equals("दिसंबर")) {
                    newDate=date.replace(month,DEC);
                }else {
                    newDate=date;
                }
                return newDate;
            }else{
                return date;
            }
        }else{
            return date;
        }
    }

    public static String getMonthInMarathi(final String month, final String locale) {
        String formattedMonth="";
        if(locale.equals("mr_IN")) {
            if(month.equals("जनवरी")) {
                formattedMonth=JAN;
            } else if(month.equals("फ़रवरी")) {
                formattedMonth=FEB;;
            } else if(month.equals("मार्च")) {
                formattedMonth=MAR;;
            } else if(month.equals("अप्रैल")) {
                formattedMonth=APR;;
            } else if(month.equals("मई")) {
                formattedMonth=MAY;;
            } else if(month.equals("जून")) {
                formattedMonth=JUNE;;
            } else if(month.equals("जुलाई")) {
                formattedMonth=JULY;;
            } else if(month.equals("अगस्त")) {
                formattedMonth=AUG;;
            } else if(month.equals("सितंबर")) {
                formattedMonth=SEP;;
            } else if(month.equals("अक्‍तूबर")) {
                formattedMonth=OCT;;
            } else if(month.equals("नवंबर")) {
                formattedMonth=NOV;;
            } else if(month.equals("दिसंबर")) {
                formattedMonth=DEC;;
            }
        }else{
            return month;
        }
        return formattedMonth;
    }

    public static String getDayInMarathi(final String day, final String locale) {
        String formattedDay="";
        if(locale.equals("mr_IN")) {
            if(day.equals("Tuesday") || day.equals("Tue") || day.contains("Tue")
            		|| day.equals("मंगलवार") || day.equals("मंगल") || day.contains("मंगल")) {
                formattedDay = TUE;
            }else {
                return day;
            }
        }else {
            return day;
        }
        return formattedDay;
    }

    public static String formatToINS(final String unformattedString) {
        String withoutCommas = unformattedString.replaceAll(",", "");
        String formattedString = null;
        StringBuffer buffer = new StringBuffer(withoutCommas);
        StringBuffer reversedBuffer = buffer.reverse();
        if(withoutCommas.length() > 3) {
            for(int i=3; i<reversedBuffer.length(); i=i+3) {
                reversedBuffer.insert(i, ",");
            }
            formattedString=reversedBuffer.reverse().toString();
        }else {
            formattedString=unformattedString;
        }
        return formattedString;
    }

    public static List<MasterVO> getMonths(final String locale) {
        String [] months = new DateFormatSymbols(new Locale(locale)).getMonths();
        List<MasterVO> masterVOs = new ArrayList<MasterVO>();
        int count=1;
        for(String i:months) {
            MasterVO masterVO=new MasterVO();
            if(locale.equals("mr_IN")) {
                masterVO.setId(new Long(count));
                masterVO.setName(convertToMarathiMonth(count));
            }else {
                masterVO.setId(new Long(count));
                masterVO.setName(i);
            }
            count++;
            if(!i.isEmpty()) {
                masterVOs.add(masterVO);
            }
        }
        return masterVOs;
    }

    private static String convertToMarathiMonth(final int count) {
        if(count==1) {
            return JAN;
        } else if(count==2) {
            return FEB;
        } else if(count==3) {
            return MAR;
        }else if(count==4) {
            return APR;
        }else if(count==5) {
            return MAY;
        }else if(count==6) {
            return JUNE;
        }else if(count==7) {
            return JULY;
        }else if(count==8) {
            return AUG;
        }else if(count==9) {
            return SEP;
        }else if(count==10) {
            return OCT;
        }else if(count==11) {
            return NOV;
        }else {
            return DEC;
        }
    }
    
    public static Date getCurrentDate() {
		CustomParameter parameter = 
			CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
		return FormaterUtil.getCurrentDate(parameter.getValue());
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
		
		String startYearDateStr = "01/01/" + yearStr;
		String endYearDateStr = "31/12/" + yearStr;		
				
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date startYearDate = null;
		Date endYearDate = null;
		try {
			startYearDate = dateFormat.parse(startYearDateStr);
			endYearDate = dateFormat.parse(endYearDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat sf=new SimpleDateFormat("EEEE");
		if(startYearDate != null && endYearDate !=null) {
			Calendar start = Calendar.getInstance();
	    	start.setTime(startYearDate);    	
	    	Calendar end = Calendar.getInstance();
	    	end.setTime(endYearDate);		
	    	for (; ! start.after(end); start.add(Calendar.DATE, 1)) {
	    		Date current = start.getTime();
	    		if(sf.format(current).equals("Sunday")){
	    			sundays.add(current);
	    		}
	    	}
		}
		return sundays;
	}	

    public static Date formatStringToDate(final String strDate, final String formatType) {
        DateFormat df = new SimpleDateFormat(formatType);
        Date formatDate = null;
        try {
            formatDate = df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatDate;
    }
    
    public static String formatDateToString(final Date date, final String formatType) {
        DateFormat df = new SimpleDateFormat(formatType);
        String strFormatDate = null;
        try {
            strFormatDate = df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strFormatDate;
    }
    
    public static Date formatStringToDate(final String strDate, 
    		final String formatType, 
    		final String locale) {
    	SimpleDateFormat sdf = new SimpleDateFormat(formatType, new Locale(locale));
        Date formatDate = null;
        try {
            formatDate = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatDate;
    }
}
