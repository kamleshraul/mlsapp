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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.MessageResource;

import com.ibm.icu.util.IndianCalendar;

/**
 * The Class FormaterUtil.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class FormaterUtil {

    private static final String JAN_mr_IN="जानेवारी";

    private static final String FEB_mr_IN="फेब्रुवारी";

    private static final String MAR_mr_IN="मार्च";

    private static final String APR_mr_IN="एप्रिल";

    private static final String MAY_mr_IN="मे";

    private static final String JUNE_mr_IN="जून";

    private static final String JULY_mr_IN="जुलै";

    private static final String AUG_mr_IN="ऑगस्ट";

    private static final String SEP_mr_IN="सप्टेंबर";

    private static final String OCT_mr_IN="ऑक्टोबर";

    private static final String NOV_mr_IN="नोव्हेंबर";

    private static final String DEC_mr_IN="डिसेंबर";

    private static final String TUE_mr_IN="मंगळवार";
    
    private static final String SHAKE_FORMAT_LABEL_mr_IN="शके";
    
    private static final String SHAKE_FORMAT_LABEL_hi_IN="शक";
    
    private static final String SHAKE_FORMAT_LABEL_en_US="Shakae";
    
    /*** Indian month names ***/
    private static Map<Integer, String> indianMonths_mr_IN = new HashMap<Integer, String>();
    
    private static Map<Integer, String> indianMonths_hi_IN = new HashMap<Integer, String>();
    
    private static Map<Integer, String> indianMonths_en_US = new HashMap<Integer, String>();
    
    /**
     * Gets the number formatter no grouping.
     *
     * @param locale the locale
     * @return the number formatter no grouping
     */
    public static NumberFormat getNumberFormatterNoGrouping(final String locale) {
        NumberFormat formatWithoutGrouping = null;
        if(locale.equals("mr_IN") || locale.equals(ApplicationConstants.STANDARD_LOCALE_INDIA)) {
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
        if(locale.equals("mr_IN") || locale.equals(ApplicationConstants.STANDARD_LOCALE_INDIA)){
            formatWithGrouping = NumberFormat.getInstance(new Locale("hi", "IN"));
            formatWithGrouping.setGroupingUsed(true);
        }else{
            formatWithGrouping = NumberFormat.getInstance(new Locale("en", "US"));
            formatWithGrouping.setGroupingUsed(true);
        }
        return formatWithGrouping;
    }
    
    public static NumberFormat getDecimalFormatterWithNoGrouping(final int maxDecimalDigits,final String locale) {
        NumberFormat formatWithGrouping = null;
        if(locale.equals("mr_IN") || locale.equals(ApplicationConstants.STANDARD_LOCALE_INDIA)){
            formatWithGrouping = NumberFormat.getInstance(new Locale("hi", "IN"));
            formatWithGrouping.setGroupingUsed(false);
        }else{
            formatWithGrouping = NumberFormat.getInstance(new Locale("en", "US"));
            formatWithGrouping.setGroupingUsed(false);
        }
        formatWithGrouping.setMaximumFractionDigits(maxDecimalDigits);
        return formatWithGrouping;
    }
    
    public static NumberFormat getDecimalFormatterWithGrouping(final int maxDecimalDigits,final String locale) {
        NumberFormat formatWithGrouping = null;
        if(locale.equals("mr_IN") || locale.equals(ApplicationConstants.STANDARD_LOCALE_INDIA)){
            formatWithGrouping = NumberFormat.getInstance(new Locale("hi", "IN"));
            formatWithGrouping.setGroupingUsed(true);
        }else{
            formatWithGrouping = NumberFormat.getInstance(new Locale("en", "US"));
            formatWithGrouping.setGroupingUsed(true);
        }
        formatWithGrouping.setMaximumFractionDigits(maxDecimalDigits);
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
        if(locale.equals("mr_IN") || locale.equals(ApplicationConstants.STANDARD_LOCALE_INDIA)){
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
        if(locale.equals("mr_IN") || locale.equals(ApplicationConstants.STANDARD_LOCALE_INDIA)){
            dateFormatter = new SimpleDateFormat(dateFormat,new Locale("hi", "IN"));
        }else{
            dateFormatter = new SimpleDateFormat(dateFormat,new Locale("en", "US"));
        }
        return dateFormatter;
    }

    public static String formatMonthsForLocaleLanguage(final String date, final String locale) {
        if(locale.equals("mr_IN")) {
            String parts[]=date.split(" ");
            if(parts.length>0){
                String month=parts[1].trim();
                String newDate="";
                if(month.equals("जनवरी")) {
                    newDate=date.replace(month,JAN_mr_IN);
                } else if(month.equals("फ़रवरी")) {
                    newDate=date.replace(month,FEB_mr_IN);
                } else if(month.equals("मार्च")) {
                    newDate=date.replace(month,MAR_mr_IN);
                } else if(month.equals("अप्रैल")) {
                    newDate=date.replace(month,APR_mr_IN);
                } else if(month.equals("मई")) {
                    newDate=date.replace(month,MAY_mr_IN);
                } else if(month.equals("जून")) {
                    newDate=date.replace(month,JUNE_mr_IN);
                } else if(month.equals("जुलाई")) {
                    newDate=date.replace(month,JULY_mr_IN);
                } else if(month.equals("अगस्त")) {
                    newDate=date.replace(month,AUG_mr_IN);
                } else if(month.equals("सितंबर")) {
                    newDate=date.replace(month,SEP_mr_IN);
                } else if(month.equals("अक्‍तूबर")) {
                    newDate=date.replace(month,OCT_mr_IN);
                } else if(month.equals("नवंबर")) {
                    newDate=date.replace(month,NOV_mr_IN);
                } else if(month.equals("दिसंबर")) {
                    newDate=date.replace(month,DEC_mr_IN);
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

    public static String getMonthInLocaleLanguage(final String month, final String locale) {
        String formattedMonth="";
        if(locale.equals("mr_IN")) {
            if(month.equals("जनवरी")) {
                formattedMonth=JAN_mr_IN;
            } else if(month.equals("फ़रवरी") || month.equals("फरवरी")) {
                formattedMonth=FEB_mr_IN;;
            } else if(month.equals("मार्च")) {
                formattedMonth=MAR_mr_IN;;
            } else if(month.equals("अप्रैल")) {
                formattedMonth=APR_mr_IN;;
            } else if(month.equals("मई")) {
                formattedMonth=MAY_mr_IN;;
            } else if(month.equals("जून")) {
                formattedMonth=JUNE_mr_IN;;
            } else if(month.equals("जुलाई")) {
                formattedMonth=JULY_mr_IN;;
            } else if(month.equals("अगस्त")) {
                formattedMonth=AUG_mr_IN;;
            } else if(month.equals("सितंबर")) {
                formattedMonth=SEP_mr_IN;;
            } else if(month.equals("अक्‍तूबर")) {
                formattedMonth=OCT_mr_IN;;
            } else if(month.equals("नवंबर")) {
                formattedMonth=NOV_mr_IN;;
            } else if(month.equals("दिसंबर")) {
                formattedMonth=DEC_mr_IN;;
            } else {
            	formattedMonth = month;
            }
        }else{
        	formattedMonth = month;
        }
        return formattedMonth;
    }

    public static String getDayInLocaleLanguage(final String day, final String locale) {
        String formattedDay="";
        if(locale.equals("mr_IN")) {
            if(day.equals("Tuesday") || day.equals("Tue") || day.contains("Tue")
            		|| day.equals("मंगलवार") || day.equals("मंगल") || day.contains("मंगल")) {
                formattedDay = TUE_mr_IN;
            }else {
                return day;
            }
        }else {
            return day;
        }
        return formattedDay;
    }
    
    public static String formatMonthInLocaleLanguageDate(final String date, final String locale) {
        if(locale.equals("mr_IN")) {
        	if(date!=null) {
        		if(!date.isEmpty()) {
        			String newDate="";
                    if(date.contains("जनवरी")) {
                        newDate=date.replace("जनवरी",JAN_mr_IN);
                    } else if(date.contains("फ़रवरी")) {
                        newDate=date.replace("फ़रवरी",FEB_mr_IN);
                    } else if(date.contains("मार्च")) {
                        newDate=date.replace("मार्च",MAR_mr_IN);
                    } else if(date.contains("अप्रैल")) {
                        newDate=date.replace("अप्रैल",APR_mr_IN);
                    } else if(date.contains("मई")) {
                        newDate=date.replace("मई",MAY_mr_IN);
                    } else if(date.contains("जून")) {
                        newDate=date.replace("जून",JUNE_mr_IN);
                    } else if(date.contains("जुलाई")) {
                        newDate=date.replace("जुलाई",JULY_mr_IN);
                    } else if(date.contains("अगस्त")) {
                        newDate=date.replace("अगस्त",AUG_mr_IN);
                    } else if(date.contains("सितंबर")) {
                        newDate=date.replace("सितंबर",SEP_mr_IN);
                    } else if(date.contains("अक्‍तूबर")) {
                        newDate=date.replace("अक्‍तूबर",OCT_mr_IN);
                    } else if(date.contains("नवंबर")) {
                        newDate=date.replace("नवंबर",NOV_mr_IN);
                    } else if(date.contains("दिसंबर")) {
                        newDate=date.replace("दिसंबर",DEC_mr_IN);
                    }else {
                        newDate=date;
                    }
                    return newDate;
        		} else{
                    return date;
                }
        	} else{
                return date;
            }            
        } else{
            return date;
        }
    }
    
    public static String getMonthInLocaleLanguage(final Integer month, final String locale) {
        String formattedMonth="";
        if(locale.equals("mr_IN")) {
        	 if(month == 0) {
                 formattedMonth=JAN_mr_IN;
             } else if(month == 1) {
                 formattedMonth=FEB_mr_IN;
             } else if(month == 2) {
                 formattedMonth=MAR_mr_IN;
             } else if(month == 3) {
                 formattedMonth=APR_mr_IN;
             } else if(month == 4) {
                 formattedMonth=MAY_mr_IN;
             } else if(month == 5) {
                 formattedMonth=JUNE_mr_IN;
             } else if(month == 6) {
                 formattedMonth=JULY_mr_IN;
             } else if(month == 7) {
                 formattedMonth=AUG_mr_IN;
             } else if(month == 8) {
                 formattedMonth=SEP_mr_IN;
             } else if(month == 9) {
                 formattedMonth=OCT_mr_IN;
             } else if(month == 10) {
                 formattedMonth=NOV_mr_IN;
             } else if(month == 11) {
                 formattedMonth=DEC_mr_IN;
             }
        }           
        return formattedMonth;
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
            if(locale.equals("mr_IN")) { //add here for other regional locales if DateFormatSymbols not able to extract months for those locales
                masterVO.setId(new Long(count));
                masterVO.setName(convertToLocaleLanguageMonth(count, locale));
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

    private static String convertToLocaleLanguageMonth(final int count, final String locale) {
    	String localeMonth = "";
    	if(locale.equals("mr_IN")) {
    		if(count==1) {
    			localeMonth = JAN_mr_IN;
	        } else if(count==2) {
	            localeMonth = FEB_mr_IN;
	        } else if(count==3) {
	            localeMonth = MAR_mr_IN;
	        }else if(count==4) {
	            localeMonth = APR_mr_IN;
	        }else if(count==5) {
	            localeMonth = MAY_mr_IN;
	        }else if(count==6) {
	            localeMonth = JUNE_mr_IN;
	        }else if(count==7) {
	            localeMonth = JULY_mr_IN;
	        }else if(count==8) {
	            localeMonth = AUG_mr_IN;
	        }else if(count==9) {
	            localeMonth = SEP_mr_IN;
	        }else if(count==10) {
	            localeMonth = OCT_mr_IN;
	        }else if(count==11) {
	            localeMonth = NOV_mr_IN;
	        }else {
	            localeMonth = DEC_mr_IN;
	        }
    	}     
    	return localeMonth;
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
    
    public static List<Date> findAllSaturdayHolidaysInYear(final Integer year) {
		List<Date> saturdays = new ArrayList<Date>();
		
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
	    		if(sf.format(current).equals("Saturday")){
	    			saturdays.add(current);
	    		}
	    	}
		}
		return saturdays;
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
    
    public static String formatDecimalNumber(final Object number, final String locale) {
        NumberFormat nf = null;
        if(locale.equals("mr_IN") || locale.equals(ApplicationConstants.STANDARD_LOCALE_INDIA)) {
        	nf = NumberFormat.getInstance(new Locale("hi", "IN"));
        }else{
        	nf = NumberFormat.getInstance(new Locale("en", "US"));
        }
        DecimalFormat df = (DecimalFormat) nf;
        return df.format(number);
    }
    
    /**
     * @param value number to be formatted
     * @param locale locale in whi9ch number to be formatted 
     * @return formatted number as string
     */
    public static String formatNumberNoGrouping(Object value , String locale){
    	return getNumberFormatterNoGrouping(locale).format(value);
    }
    
    public static String formatNumberForIndianCurrency(Object value, String locale) {
    	return formatNumberForIndianCurrency(value, false, locale);
    }
    
    public static String formatNumberForIndianCurrency(Object value, Boolean includeZeroValueAfterDecimalPoint, String locale) {
    	Format formatForIndianCurrency = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        
        String indianCurrencyNumber = formatForIndianCurrency.format(value).substring(2); //substring is used to eliminate Indian Currency Symbol
        
        if(includeZeroValueAfterDecimalPoint) {
        	return formatNumbersInGivenText(indianCurrencyNumber, locale);
        } else {
        	if(indianCurrencyNumber.endsWith(".00")) {
        		return formatNumbersInGivenText(indianCurrencyNumber, locale).substring(0, indianCurrencyNumber.length()-3); //remove post decimal point
        	} else {
        		return formatNumbersInGivenText(indianCurrencyNumber, locale);
        	}
        }
    }
    
    public static String formatNumberForIndianCurrencyWithSymbol(Object value, String locale) {
    	return formatNumberForIndianCurrencyWithSymbol(value, false, locale);
    }
    
    public static String formatNumberForIndianCurrencyWithSymbol(Object value, Boolean includeZeroValueAfterDecimalPoint, String locale) {
    	Format formatForIndianCurrency = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        
    	String indianCurrencyNumber = formatForIndianCurrency.format(value);
        
    	if(includeZeroValueAfterDecimalPoint) {
        	return formatNumbersInGivenText(indianCurrencyNumber, locale);
        } else {
        	if(indianCurrencyNumber.endsWith(".00")) {
        		return formatNumbersInGivenText(indianCurrencyNumber, locale).substring(0, indianCurrencyNumber.length()-3); //remove post decimal point
        	} else {
        		return formatNumbersInGivenText(indianCurrencyNumber, locale);
        	}
        }
    }
    
    public static BigDecimal parseNumberForIndianCurrency(String formattedNumber, String locale) {
    	try {
    		formattedNumber = formattedNumber.replaceAll(",", "");
    		Number number = getNumberFormatterNoGrouping(locale).parse(formattedNumber);
			return new BigDecimal(getNumberFormatterNoGrouping(locale).format(number));
		} catch (ParseException e) {
			//e.printStackTrace();
			return null;
		}
    }
    
    public static BigDecimal parseNumberForIndianCurrencyWithSymbol(String formattedNumber, String locale) {   	   	
    	Format formatForIndianCurrency = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        
    	try {
    		formattedNumber = formattedNumber.replaceAll(",", "");
    		Number number = (Number) formatForIndianCurrency.parseObject(formattedNumber);
			return new BigDecimal(getNumberFormatterNoGrouping("en_US").format(number));
		} catch (ParseException e) {
			//e.printStackTrace();
			return null;
		}
    }

    
    //-------vikas dhananjay------------------
	public static SimpleDateFormat getDBDateParser(final String locale){
		 SimpleDateFormat dateFormatter = null;
	     if(locale.equals("mr_IN") || locale.equals(ApplicationConstants.STANDARD_LOCALE_INDIA)){
	         dateFormatter = new SimpleDateFormat(ApplicationConstants.DB_DATEFORMAT,
	         		new Locale("hi", "IN"));
	     }else{
	         dateFormatter = new SimpleDateFormat(ApplicationConstants.DB_DATEFORMAT,
	         		new Locale("en", "US"));
	     }
	     return dateFormatter;  	
	}
	
	public static String formatDateToString(final Date date, final String formatType, final String locale) {
		SimpleDateFormat df = null;
		String strFormatDate = null;
		if(formatType!=null && (formatType.equals(ApplicationConstants.ROTATIONORDER_DATEFORMAT)
				|| formatType.equals(ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT))
				|| formatType.equals(ApplicationConstants.SERVER_DATEFORMAT_DISPLAY_2)) {
			
			CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
			if(dbDateFormat!=null && dbDateFormat.getValue()!=null && !dbDateFormat.getValue().isEmpty()){
				df=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
				//Added the following code to solve the regional month and day issue
				String[] strDates=df.format(date).split(",");
				String day=FormaterUtil.getDayInLocaleLanguage(strDates[0],locale.toString());
				MessageResource dateResource = MessageResource.findByFieldName(MessageResource.class, "code", "generic.date", locale);
				if(dateResource!=null && dateResource.getValue()!=null && !dateResource.getValue().isEmpty()) {
					String[] strMonth=strDates[1].split(" ");
					String month=FormaterUtil.getMonthInLocaleLanguage(strMonth[1], locale.toString());
					if(formatType.equals(ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT)) {
						strFormatDate = day + ", " + dateResource.getValue() + " " + strMonth[0] + " " + month + ", " + strDates[2];
					} else if(formatType.equals(ApplicationConstants.ROTATIONORDER_DATEFORMAT)) {
						strFormatDate = dateResource.getValue() + " " + strMonth[0] + " " + month + ", " + strDates[2];
					} else if(formatType.equals(ApplicationConstants.SERVER_DATEFORMAT_DISPLAY_2)) {
						strFormatDate = strMonth[0] + " " + month + ", " + strDates[2];
					}				
				}
			}						
		} else if(formatType.equals(ApplicationConstants.SERVER_DATEFORMAT_DISPLAY_1)) {
			df=FormaterUtil.getDateFormatter(formatType, locale.toString());
			String[] strDate=df.format(date).split(" ");
			String zeroInLocale = FormaterUtil.formatNumberNoGrouping(0, locale);
			if(strDate[0].startsWith(zeroInLocale)) {
				strDate[0] = strDate[0].substring(1);
			}
			String[] strMonth=strDate[1].split(",");
			String month=FormaterUtil.getMonthInLocaleLanguage(strMonth[0], locale.toString());
			strFormatDate = strDate[0] + " " + month + ", " + strDate[2];			
		} else {
			df = getDateFormatter(formatType, locale);	    
		    try {
		        strFormatDate = df.format(date);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}	    
	    return strFormatDate;
	}
	
	public static String formatDateToStringUsingCustomParameterFormat(final Date date, final String customParameterName, final String locale) throws ELSException {
		String strFormatDate = null;
		
		try {
			SimpleDateFormat df = null;
			
			CustomParameter dateFormatCustomParameter = CustomParameter.findByName(CustomParameter.class, customParameterName, "");
			if(dateFormatCustomParameter==null || dateFormatCustomParameter.getValue()==null || dateFormatCustomParameter.getValue().isEmpty()) {
				throw new ELSException();
			}
			
			String dateFormat = dateFormatCustomParameter.getValue();
			df=FormaterUtil.getDateFormatter(dateFormat, locale.toString());	
			
			if(dateFormat.equals(ApplicationConstants.ROTATIONORDER_DATEFORMAT)
					|| dateFormat.equals(ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT)) {
				
				CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
				if(dbDateFormat!=null && dbDateFormat.getValue()!=null && !dbDateFormat.getValue().isEmpty()){
					df=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
					//Added the following code to solve the regional month and day issue
					String[] strDates=df.format(date).split(",");
					String day=FormaterUtil.getDayInLocaleLanguage(strDates[0],locale.toString());
					MessageResource dateResource = MessageResource.findByFieldName(MessageResource.class, "code", "generic.date", locale);
					if(dateResource!=null && dateResource.getValue()!=null && !dateResource.getValue().isEmpty()) {
						String[] strMonth=strDates[1].split(" ");
						String month=FormaterUtil.getMonthInLocaleLanguage(strMonth[1], locale.toString());
						if(dateFormat.equals(ApplicationConstants.ROTATIONORDER_WITH_DAY_DATEFORMAT)) {
							strFormatDate = day + ", " + dateResource.getValue() + " " + strMonth[0] + " " + month + ", " + strDates[2];
						} else if(dateFormat.equals(ApplicationConstants.ROTATIONORDER_DATEFORMAT)) {
							strFormatDate = dateResource.getValue() + " " + strMonth[0] + " " + month + ", " + strDates[2];
						}			
					}
				}
				
			} else if(dateFormat.equals(ApplicationConstants.SERVER_DATEFORMAT_DISPLAY_1)) {
				
				String[] strDate=df.format(date).split(" ");
				String zeroInLocale = FormaterUtil.formatNumberNoGrouping(0, locale);
				if(strDate[0].startsWith(zeroInLocale)) {
					strDate[0] = strDate[0].substring(1);
				}
				String[] strMonth=strDate[1].split(",");
				String month=FormaterUtil.getMonthInLocaleLanguage(strMonth[0], locale.toString());
				strFormatDate = strDate[0] + " " + month + ", " + strDate[2];
				
			} else {
				try {
			        strFormatDate = df.format(date);
			    } catch (Exception e) {
			       throw new ELSException();
			    }
			}			
		} catch(Exception e) {
			throw new ELSException();
		}	
		
		return strFormatDate;
	}
	
	/**
	 * Extract dates from a String of dates delimited by @param delimiter
	 * and of the format @param formatType.
	 */
	public static List<Date> formatDelimitedStringToDates(final String strDates,
			final String delimiter,
			final String formatType) {
		List<Date> dates = new ArrayList<Date>();
		
		String[] strDatesArr = strDates.split(delimiter);
		for(String s : strDatesArr) {
			Date date = FormaterUtil.formatStringToDate(s, formatType);			
			dates.add(date);
		}
		
		return dates;
	}
	
	/**
	 * Create a list of localized dates (of type string)
	 * in the format @param formatType, of the @param locale 
	 */
	public static List<String> formatDatesToString(final List<Date> dates,
			final String formatType,
			final String locale) {
		List<String> strs = new ArrayList<String>();
		
		for(Date d : dates) {
			String str = FormaterUtil.formatDateToString(d, formatType, locale);
			strs.add(str);
		}
		
		return strs;
	}
	
	
	/*** To get the Indian month name ***/
	public static String getIndianDate(final Date date, final Locale locale){
		IndianCalendar indianCalendar = new IndianCalendar(locale);
		indianCalendar.setTime(date);
		
		if(locale.toString().equals("mr_IN")){
			
			return indianMonths_mr_IN.get(indianCalendar.get(IndianCalendar.MONTH)) + " " + 
					FormaterUtil.formatNumberNoGrouping(
							indianCalendar.get(IndianCalendar.DAY_OF_MONTH), locale.toString())
							+ ", " + FormaterUtil.formatNumberNoGrouping(
									indianCalendar.get(IndianCalendar.YEAR), locale.toString());
			
		} else if(locale.toString().equals(ApplicationConstants.STANDARD_LOCALE_INDIA)){
			
			return indianMonths_hi_IN.get(indianCalendar.get(IndianCalendar.MONTH)) + " " + 
					FormaterUtil.formatNumberNoGrouping(
							indianCalendar.get(IndianCalendar.DAY_OF_MONTH), locale.toString())
							+ ", " + FormaterUtil.formatNumberNoGrouping(
									indianCalendar.get(IndianCalendar.YEAR), locale.toString());
			
		} else if(locale.toString().equals("en_US")){
			
			return indianMonths_en_US.get(indianCalendar.get(IndianCalendar.MONTH)) + " " + 
					FormaterUtil.formatNumberNoGrouping(
							indianCalendar.get(IndianCalendar.DAY_OF_MONTH), locale.toString())
							+ ", " + FormaterUtil.formatNumberNoGrouping(
									indianCalendar.get(IndianCalendar.YEAR), locale.toString());
			
		}
		
		return "";
	}
	
	/*** To get the Indian month name in shake format ***/
	public static String getIndianDateInShakeFormat(final Date date, final Locale locale){
		IndianCalendar indianCalendar = new IndianCalendar(locale);
		indianCalendar.setTime(date);
		
		if(locale.toString().equals("mr_IN")){
			
			return indianMonths_mr_IN.get(indianCalendar.get(IndianCalendar.MONTH)) + " " + 
					FormaterUtil.formatNumberNoGrouping(
							indianCalendar.get(IndianCalendar.DAY_OF_MONTH), locale.toString())
							+ ", " 
							+ FormaterUtil.SHAKE_FORMAT_LABEL_mr_IN
							+ " "
							+ FormaterUtil.formatNumberNoGrouping(
									indianCalendar.get(IndianCalendar.YEAR), locale.toString());
			
		} else if(locale.toString().equals(ApplicationConstants.STANDARD_LOCALE_INDIA)){
			
			return indianMonths_hi_IN.get(indianCalendar.get(IndianCalendar.MONTH)) + " " + 
					FormaterUtil.formatNumberNoGrouping(
							indianCalendar.get(IndianCalendar.DAY_OF_MONTH), locale.toString())
							+ ", " 
							+ FormaterUtil.SHAKE_FORMAT_LABEL_hi_IN
							+ " "
							+ FormaterUtil.formatNumberNoGrouping(
									indianCalendar.get(IndianCalendar.YEAR), locale.toString());
			
		} else if(locale.toString().equals("en_US")){
			
			return indianMonths_en_US.get(indianCalendar.get(IndianCalendar.MONTH)) + " " + 
					FormaterUtil.formatNumberNoGrouping(
							indianCalendar.get(IndianCalendar.DAY_OF_MONTH), locale.toString())
							+ ", " 
							+ FormaterUtil.SHAKE_FORMAT_LABEL_en_US
							+ " "
							+ FormaterUtil.formatNumberNoGrouping(
									indianCalendar.get(IndianCalendar.YEAR), locale.toString());
			
		}
		
		return "";
	}
	
	/*** To get the Indian month name ***/
	public static String getIndianDate(final Date date, final String locale){
		IndianCalendar indianCalendar = new IndianCalendar(new Locale(locale));
		indianCalendar.setTime(date);
		
		if(locale.equals("mr_IN")){
			
			return indianMonths_mr_IN.get(indianCalendar.get(IndianCalendar.MONTH)) + " " + 
					FormaterUtil.formatNumberNoGrouping(
							indianCalendar.get(IndianCalendar.DAY_OF_MONTH), locale)
							+ ", " + FormaterUtil.formatNumberNoGrouping(
									indianCalendar.get(IndianCalendar.YEAR), locale);
			
		} else if(locale.equals(ApplicationConstants.STANDARD_LOCALE_INDIA)){
			
			return indianMonths_hi_IN.get(indianCalendar.get(IndianCalendar.MONTH)) + " " + 
					FormaterUtil.formatNumberNoGrouping(
							indianCalendar.get(IndianCalendar.DAY_OF_MONTH), locale.toString())
							+ ", " + FormaterUtil.formatNumberNoGrouping(
									indianCalendar.get(IndianCalendar.YEAR), locale.toString());
			
		} else if(locale.equals("en_US")){
			
			return indianMonths_en_US.get(indianCalendar.get(IndianCalendar.MONTH)) + " " + 
					FormaterUtil.formatNumberNoGrouping(
							indianCalendar.get(IndianCalendar.DAY_OF_MONTH), locale)
							+ ", " + FormaterUtil.formatNumberNoGrouping(
									indianCalendar.get(IndianCalendar.YEAR), locale);
			
		}
		
		return "";
	}
	
	static{
    	indianMonths_mr_IN.put(0, "चैत्र");
		indianMonths_mr_IN.put(1, "वैशाख");
		indianMonths_mr_IN.put(2, "ज्येष्ठ");
		indianMonths_mr_IN.put(3, "आषाढ");
		indianMonths_mr_IN.put(4, "श्रावण");
		indianMonths_mr_IN.put(5, "भाद्रपद");
		indianMonths_mr_IN.put(6, "अश्विन");
		indianMonths_mr_IN.put(7, "कार्तिक");
		indianMonths_mr_IN.put(8, "आग्रह्नय");
		indianMonths_mr_IN.put(9, "पौष");
		indianMonths_mr_IN.put(10, "माघ");
		indianMonths_mr_IN.put(11, "फाल्गुन");	
    }	
	
	static{
    	indianMonths_en_US.put(0, "CHAITRA");
    	indianMonths_en_US.put(1, "VAISAKHA");
    	indianMonths_en_US.put(2, "JYAISTHA");
    	indianMonths_en_US.put(3, "ASADHA");
    	indianMonths_en_US.put(4, "SRAVANA");
    	indianMonths_en_US.put(5, "BHADRA");
    	indianMonths_en_US.put(6, "ASVINA");
    	indianMonths_en_US.put(7, "KARTIKA");
    	indianMonths_en_US.put(8, "AGRAHAYANA");
    	indianMonths_en_US.put(9, "PAUSA");
    	indianMonths_en_US.put(10, "MAGHA");
    	indianMonths_en_US.put(11, "PHALGUNA");	
    }
	
	static{
    	indianMonths_hi_IN.put(0, "चैत्र");
		indianMonths_hi_IN.put(1, "बैसाख");
		indianMonths_hi_IN.put(2, "ज्येष्ठ");
		indianMonths_hi_IN.put(3, "आषाढ");
		indianMonths_hi_IN.put(4, "सावन");
		indianMonths_hi_IN.put(5, "भादों");
		indianMonths_hi_IN.put(6, "आश्विन");
		indianMonths_hi_IN.put(7, "कार्तिक");
		indianMonths_hi_IN.put(8, "अगहन");
		indianMonths_hi_IN.put(9, "पूस");
		indianMonths_hi_IN.put(10, "माघ");
		indianMonths_hi_IN.put(11, "फागुन");	
    }
	
	public static String findIndexLetterByWord(final String word, final String locale){
		String letter = null;
		String lettersInLocaleLanguage[] = null;
		if(locale.equals("mr_IN")) {
			String letters[] = {"अ", "आ", "इ", "ई", "उ", "ऊ", "ए", "ऐ", "ओ", "औ", 
					"अं", "अः", "क", "ख", "ग", "घ", "च", "छ", "ज", "झ", "ञ", "ट", 
					"ठ", "ड", "ढ", "ण", "त", "थ", "द", "ध", "न", "प", "फ", "ब", 
					"भ", "म", "य", "र", "ल", "व", "श", "ष", "स", "ह", "ळ", "क्ष", "ज्ञ"};			
			lettersInLocaleLanguage = letters;
			
		} else if(locale.equals(ApplicationConstants.STANDARD_LOCALE_INDIA)) {
			String letters[] = {"अ", "आ", "इ", "ई", "उ", "ऊ", "ए", "ऐ", "ओ", "औ", 
					"अं", "अः", "क", "ख", "ग", "घ", "च", "छ", "ज", "झ", "ञ", "ट", 
					"ठ", "ड", "ढ", "ण", "त", "थ", "द", "ध", "न", "प", "फ", "ब", 
					"भ", "म", "य", "र", "ल", "व", "श", "ष", "स", "ह", "ळ", "क्ष", "ज्ञ"};			
			lettersInLocaleLanguage = letters;
		}		
		if(lettersInLocaleLanguage!=null) {
			for(String l : lettersInLocaleLanguage){
				if(word.indexOf(l) == 0){
					letter = l;
					break;
				}
			}
		}		
		return letter;
	}
	
	public static String formatNumbersInGivenText(final String givenText, final String locale) {
		String formattedText = givenText;
		
		if(formattedText!=null && !formattedText.isEmpty() && locale!=null && !locale.isEmpty() && !locale.equals(ApplicationConstants.STANDARD_LOCALE)) {
			
			formattedText = formattedText.replaceAll("0", FormaterUtil.formatNumberNoGrouping(0, locale));
			formattedText = formattedText.replaceAll("1", FormaterUtil.formatNumberNoGrouping(1, locale));
			formattedText = formattedText.replaceAll("2", FormaterUtil.formatNumberNoGrouping(2, locale));
			formattedText = formattedText.replaceAll("3", FormaterUtil.formatNumberNoGrouping(3, locale));
			formattedText = formattedText.replaceAll("4", FormaterUtil.formatNumberNoGrouping(4, locale));
			formattedText = formattedText.replaceAll("5", FormaterUtil.formatNumberNoGrouping(5, locale));
			formattedText = formattedText.replaceAll("6", FormaterUtil.formatNumberNoGrouping(6, locale));
			formattedText = formattedText.replaceAll("7", FormaterUtil.formatNumberNoGrouping(7, locale));
			formattedText = formattedText.replaceAll("8", FormaterUtil.formatNumberNoGrouping(8, locale));
			formattedText = formattedText.replaceAll("9", FormaterUtil.formatNumberNoGrouping(9, locale));
		}
		
		return formattedText;
	}
	
	public static Date stringToDate(final String strDate, final String formatType) throws ParseException {
        DateFormat df = new SimpleDateFormat(formatType);
        Date formatDate = df.parse(strDate);
        return formatDate;
    }
	
	public static String formatValueForIndianCurrency(String value, String locale) {
		String formattedCurrencyValue = value;
		BigDecimal currencyValue = FormaterUtil.parseNumberForIndianCurrency(value.split(":")[1], locale);
		if(currencyValue!=null) {
			if(value.split(":")[0].equals("currencyWithSymbol")) {
				formattedCurrencyValue = FormaterUtil.formatNumberForIndianCurrencyWithSymbol(currencyValue, locale);
			} else if(value.split(":")[0].equals("currency")) {
				formattedCurrencyValue = FormaterUtil.formatNumberForIndianCurrency(currencyValue, locale);
			}			
		}
		return formattedCurrencyValue;
	}

}
