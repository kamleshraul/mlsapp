package org.mkcl.els.common.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormaterUtil {

	public static NumberFormat getNumberFormatterNoGrouping(String locale){
		NumberFormat formatWithoutGrouping=null;
        if(locale.equals("mr_IN")||locale.equals("hi_IN")){
            formatWithoutGrouping=NumberFormat.getInstance(new Locale("hi","IN"));
            formatWithoutGrouping.setGroupingUsed(false);
        }else{
            formatWithoutGrouping=NumberFormat.getInstance(new Locale("en","US"));
            formatWithoutGrouping.setGroupingUsed(false);
        }
        return formatWithoutGrouping;
	}
	
	public static NumberFormat getNumberFormatterGrouping(String locale){
		NumberFormat formatWithGrouping=null;
        if(locale.equals("mr_IN")||locale.equals("hi_IN")){
            formatWithGrouping=NumberFormat.getInstance(new Locale("hi","IN"));
            formatWithGrouping.setGroupingUsed(true);
        }else{
            formatWithGrouping=NumberFormat.getInstance(new Locale("en","US"));
            formatWithGrouping.setGroupingUsed(true);
        }
        return formatWithGrouping;
	}
	
	public static SimpleDateFormat getDateFormatter(String locale){
		SimpleDateFormat dateFormatter=null;
        if(locale.equals("mr_IN")||locale.equals("hi_IN")){
        	dateFormatter = new SimpleDateFormat(ApplicationConstants.SERVER_DATEFORMAT,new Locale("hi","IN"));
        }else{
        	dateFormatter = new SimpleDateFormat(ApplicationConstants.SERVER_DATEFORMAT,new Locale("en","US"));
        }
        return dateFormatter;
	}
	
	public static SimpleDateFormat getDateFormatter(String dateFormat,String locale){
		SimpleDateFormat dateFormatter=null;
        if(locale.equals("mr_IN")||locale.equals("hi_IN")){
        	dateFormatter = new SimpleDateFormat(dateFormat,new Locale("hi","IN"));
        }else{
        	dateFormatter = new SimpleDateFormat(dateFormat,new Locale("en","US"));
        }
        return dateFormatter;
	}
	
	
}
