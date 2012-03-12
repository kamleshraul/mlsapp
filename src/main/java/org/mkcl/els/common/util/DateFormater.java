/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.util.DateFormater.java
 * Created On: Mar 7, 2012
 */
package org.mkcl.els.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Class DateFormater.
 *
 * @author nileshp
 */
public class DateFormater {

    /**
     * Format string to date.
     *
     * @param strDate the str date
     * @param formatType the format type
     * @return the date
     * @author nileshp
     * @since v1.0.0
     */
    public Date formatStringToDate(final String strDate, final String formatType) {
        DateFormat df = new SimpleDateFormat(formatType);
        Date formatDate = null;
        try {
            formatDate = df.parse(strDate);
            System.out.println("Formated date is = " + df.format(formatDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatDate;
    }

    /**
     * Format date to string.
     *
     * @param date the date
     * @param formatType the format type
     * @return the string
     * @author nileshp
     * @since v1.0.0
     */
    public String formatDateToString(final Date date, final String formatType) {
        DateFormat df = new SimpleDateFormat(formatType);
        String strFormatDate = null;
        try {
            strFormatDate = df.format(date);
            System.out.println("Formated date is = " + strFormatDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strFormatDate;
    }
}
