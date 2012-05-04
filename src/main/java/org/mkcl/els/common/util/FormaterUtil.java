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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * The Class FormaterUtil.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class FormaterUtil {

	/**
	 * Gets the number formatter no grouping.
	 *
	 * @param locale the locale
	 * @return the number formatter no grouping
	 */
	public static NumberFormat getNumberFormatterNoGrouping(final String locale){
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

	/**
	 * Gets the number formatter grouping.
	 *
	 * @param locale the locale
	 * @return the number formatter grouping
	 */
	public static NumberFormat getNumberFormatterGrouping(final String locale){
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

	/**
	 * Gets the date formatter.
	 *
	 * @param locale the locale
	 * @return the date formatter
	 */
	public static SimpleDateFormat getDateFormatter(final String locale){
		SimpleDateFormat dateFormatter=null;
        if(locale.equals("mr_IN")||locale.equals("hi_IN")){
        	dateFormatter = new SimpleDateFormat(ApplicationConstants.SERVER_DATEFORMAT,new Locale("hi","IN"));
        }else{
        	dateFormatter = new SimpleDateFormat(ApplicationConstants.SERVER_DATEFORMAT,new Locale("en","US"));
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
	public static SimpleDateFormat getDateFormatter(final String dateFormat,final String locale){
		SimpleDateFormat dateFormatter=null;
        if(locale.equals("mr_IN")||locale.equals("hi_IN")){
        	dateFormatter = new SimpleDateFormat(dateFormat,new Locale("hi","IN"));
        }else{
        	dateFormatter = new SimpleDateFormat(dateFormat,new Locale("en","US"));
        }
        return dateFormatter;
	}


}
