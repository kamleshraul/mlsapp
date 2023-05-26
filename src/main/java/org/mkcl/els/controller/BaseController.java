/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.BaseController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.fop.apps.MimeConstants;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.xmlvo.XmlVO;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.Title;
import org.mkcl.els.service.impl.ReportServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;


/**
 * The Class BaseController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public abstract class BaseController {

    /** The logger. */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Gets the current user.
     *
     * @return the current user
     */
    protected AuthUser getCurrentUser() {
        return (AuthUser) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    }

    /**
     * Gets the user locale.
     *
     * @return the user locale
     */
    protected Locale getUserLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale;
    }

    /**
     * Checks if is session valid.
     *
     * @return true, if is session valid
     */
    protected boolean isSessionValid() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
            return false;
        } else {
            return true;
        }
    }
    
    protected boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
          isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    /**** Report Generation Using FOP with default fop config file 
     * @throws Exception ****/
    protected File generateReportUsingFOP(XmlVO data, String xsltFileName, String reportFormat, String reportFileName, String locale) throws Exception {
    	return this.generateReportUsingFOP(data, "fopConfig_autodetect", xsltFileName, reportFormat, reportFileName, locale);    	
    }
    
    protected File generateReportUsingFOP(final Object[] report, String xsltFileName, String reportFormat, String reportFileName, String locale) throws Exception {
    	return this.generateReportUsingFOP(report, "fopConfig_autodetect", xsltFileName, reportFormat, reportFileName, locale);    	
    }
    
    protected File generateReportFromHtmlUsingFOP(String htmlFileName, String reportFormat, String reportFileName, String locale) throws Exception {
    	return this.generateReportFromHtmlUsingFOP(htmlFileName, "fopConfig_autodetect", ApplicationConstants.STANDARD_XSLT_FOR_HTML_TO_FO, reportFormat, reportFileName, locale);	
    }
    
    protected File generateReportFromHtmlUsingFOP(String htmlFileName, String reportFormat, String xsltFileName, String reportFileName, String locale) throws Exception {
    	return this.generateReportFromHtmlUsingFOP(htmlFileName, "fopConfig_autodetect", xsltFileName, reportFormat, reportFileName, locale);
    }
    
    /**** Report Generation Using FOP with custom fop config file 
     * @throws Exception ****/
    protected File generateReportUsingFOP(XmlVO data, String fopConfigFileName, String xsltFileName, String reportFormat, String reportFileName, String locale) throws Exception {
    	File reportFile = null;    	
        ReportServiceImpl reportGenerator = null;
        data.setLocale(locale);
        if(data.getModuleName()==null) {
        	CustomParameter moduleNameCP = CustomParameter.findByName(CustomParameter.class, xsltFileName.toUpperCase() + "_MODULE_NAME", "");
    		if(moduleNameCP!=null && moduleNameCP.getValue()!=null && !moduleNameCP.getValue().isEmpty()) {
    			data.setModuleName(moduleNameCP.getValue());
    		} else {
    			data.setModuleName("generic");
    		}
        }
        if(data.getReportDate()==null) {
			CustomParameter reportDateFormatParameter = CustomParameter.findByName(CustomParameter.class, xsltFileName.toUpperCase() + "_REPORTDATE_FORMAT", "");
			if(reportDateFormatParameter!=null && reportDateFormatParameter.getValue()!=null) {
				String formattedReportDate = FormaterUtil.formatDateToString(new Date(), reportDateFormatParameter.getValue(), locale);
				if(reportDateFormatParameter.getValue().equals("dd MMM, yyyy")) {
					String[] strDate=formattedReportDate.split(",");
					String[] strMonth=strDate[0].split(" ");
					String month=FormaterUtil.getMonthInLocaleLanguage(strMonth[1], locale.toString());
					formattedReportDate = strMonth[0] + " " + month + ", " + strDate[1];
				}
				data.setReportDate(formattedReportDate);
			} else {
				data.setReportDate(FormaterUtil.formatDateToString(new Date(), ApplicationConstants.REPORT_DATEFORMAT, locale));
			}
		}        
        if(reportFormat.equals("PDF")) {
        	data.setOutputFormat(MimeConstants.MIME_PDF);
        	
    		//initialize fop report generator with configuration filename, xslt filename, report format & report output file
    		reportGenerator = new ReportServiceImpl(fopConfigFileName, xsltFileName, MimeConstants.MIME_PDF, reportFileName);
    		
    		//generate report
    		reportFile = reportGenerator.generateReport(data);    		
        }
        else if(reportFormat.equals("RTF")) {
        	data.setOutputFormat(MimeConstants.MIME_RTF);
        	
    		//initialize fop report generator with configuration filename, xslt filename, report format & report output file
    		reportGenerator = new ReportServiceImpl(fopConfigFileName, xsltFileName, MimeConstants.MIME_RTF, reportFileName);
    		
    		//generate report
    		reportFile = reportGenerator.generateReport(data);    		
        }
        else if(reportFormat.equals("WORD")) {
        	data.setOutputFormat("WORD");
        	
        	//initialize fop report generator with configuration filename, xslt filename, report format & report output file
    		reportGenerator = new ReportServiceImpl(fopConfigFileName, xsltFileName, "WORD", reportFileName);
    		
    		//generate report
    		reportFile = reportGenerator.generateReport(data);    		
        } 
        else if(reportFormat.equals("HTML")) {
        	data.setOutputFormat("HTML");
        	
    		//initialize fop report generator with configuration filename, xslt filename, report format & report output file
        	reportGenerator = new ReportServiceImpl(fopConfigFileName, xsltFileName, "HTML", reportFileName);
    		
        	//generate report            	
    		reportFile = reportGenerator.generateReport(data);  		
        }
    	return reportFile;
    }
    
    protected File generateReportUsingFOP(final Object[] reportFields, String fopConfigFileName, String xsltFileName, String reportFormat, String reportFileName, String locale) throws Exception {
    	File reportFile = null;    	
        ReportServiceImpl reportGenerator = null;
        
        //initialize fop report generator with configuration filename, xslt filename, report format & report output file
        if(reportFormat.equals("PDF")) {
        	reportGenerator = new ReportServiceImpl(fopConfigFileName, xsltFileName, MimeConstants.MIME_PDF, reportFileName);
        }
        else if(reportFormat.equals("WORD")) {
        	reportGenerator = new ReportServiceImpl(fopConfigFileName, xsltFileName, "WORD", reportFileName);
        }
		
		//generate report
		reportFile = reportGenerator.generateReport(reportFields, locale);
        
    	return reportFile;
    }
    
    protected File generateReportFromHtmlUsingFOP(String htmlFileName, String fopConfigFileName, String xsltFileName, String reportFormat, String reportFileName, String locale) throws Exception {
    	File reportFile = null;    	
        ReportServiceImpl reportGenerator = null;
        
        //initialize fop report generator with configuration filename, xslt filename, report format & report output file
        if(reportFormat.equals("PDF")) {
        	reportGenerator = new ReportServiceImpl(fopConfigFileName, htmlFileName, xsltFileName, MimeConstants.MIME_PDF, reportFileName);
        }
        else if(reportFormat.equals("WORD")) {
        	reportGenerator = new ReportServiceImpl(fopConfigFileName, htmlFileName, xsltFileName, "WORD", reportFileName);
        }
		
		//generate report
		reportFile = reportGenerator.generateReportFromHtml();
        
    	return reportFile;
    }
    
    protected void openOrSaveReportFileFromBrowser(HttpServletResponse response, File file, String reportFormat) {
    	FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		response.setContentLength((int)file.length());
		
		//open as dialog box having options 'open with' & 'save file'
		response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
		
		//response.setHeader("Cache-Control", "cache, must-revalidate");
		response.addHeader("Cache-Control", "cache, must-revalidate"); 
		response.setHeader("Pragma", "public");
		
		if(reportFormat.equals("PDF")) {
			response.setContentType("application/pdf");
		} else if(reportFormat.equals("WORD")) {
			response.setContentType("application/msword");
		} else {
			response.setContentType("text/html");
		}		
		//or
		//open directly in browser
		//response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
		
		try {
			FileCopyUtils.copy(fis, response.getOutputStream());
			//try deleting report file once it is copied into response to be displayed (remove this code if we need to keep the server copy saved using cdn or so)
			file.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    protected List<Object[]> populateDateList(List<Date> dates, String dateFormat, String locale) {
    	List<Object[]> dateList = new ArrayList<Object[]>();
    	for(Date d: dates) {
    		Object[] dateArray = new Object[2];
    		dateArray[0] = FormaterUtil.formatDateToString(d, ApplicationConstants.SERVER_DATEFORMAT, "en_US");
    		dateArray[1] = FormaterUtil.formatDateToString(d, dateFormat, locale);
    		dateList.add(dateArray);    		
    	}
    	return dateList;
    }
    
    protected List<Object[]> populateDateListUsingCustomParameterFormat(List<Date> dates, String customParameterName, String locale) throws ELSException {
    	if(dates==null || dates.isEmpty()) {
    		return new ArrayList<Object[]>();
    	}
    	CustomParameter dateFormatCustomParameter = CustomParameter.findByName(CustomParameter.class, customParameterName, "");
		if(dateFormatCustomParameter==null || dateFormatCustomParameter.getValue()==null || dateFormatCustomParameter.getValue().isEmpty()) {
			throw new ELSException();
		}
    	List<Object[]> dateList = new ArrayList<Object[]>();    	
    	for(Date d: dates) {
    		Object[] dateArray = new Object[2];
    		dateArray[0] = FormaterUtil.formatDateToString(d, ApplicationConstants.SERVER_DATEFORMAT, "en_US");
    		dateArray[1] = FormaterUtil.formatDateToString(d, dateFormatCustomParameter.getValue(), locale);
    		dateList.add(dateArray);    		
    	}
    	return dateList;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected <U extends BaseDomain> U populateObjectExtendingBaseDomainByID(final HttpServletRequest request, final String pname, final Class clazz,  final String locale) throws ELSException {
    	String rp = request.getParameter(pname);
    	if(rp!=null && !rp.isEmpty()) {    		
    		return (U) BaseDomain.findById(clazz, Long.parseLong(rp));
    	} else {
    		return null;
    	}    	
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected <U extends BaseDomain> U populateObjectExtendingBaseDomainByStringFieldName(final HttpServletRequest request, final String pname, final Class clazz, final String fieldName, final String locale) throws ELSException {
    	String rp = request.getParameter(pname);
    	if(rp!=null && !rp.isEmpty()) {    		
    		return (U) BaseDomain.findByFieldName(clazz, fieldName, rp, locale);
    	} else {
    		return null;
    	}    	
    }    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected <U extends BaseDomain> List<U> populateListOfObjectExtendingBaseDomainByDelimitedTypes(final Class clazz, final String delimitedTypes,
			final String delimiter,
			final String locale) {
		List<U> objectList = new ArrayList<U>();
		
		String[] typeNames = delimitedTypes.split(delimiter);
		for(String typeName : typeNames) {
			U obj = (U) BaseDomain.findByFieldName(clazz, "type", typeName, locale);
			objectList.add(obj);
		}		
		return objectList;
	}
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected <U extends BaseDomain> List<U> populateListOfObjectExtendingBaseDomainByDelimitedFieldName(final Class clazz, final String fieldName, final String delimitedNames,
			final String delimiter,
			final String locale) {
		List<U> objectList = new ArrayList<U>();
		
		String[] values = delimitedNames.split(delimiter);
		for(String value : values) {
			U obj = (U) BaseDomain.findByFieldName(clazz, fieldName, value, locale);
			objectList.add(obj);
		}		
		return objectList;
	}
    
    public <U extends BaseDomain> boolean isObjectExtendingBaseDomainAvailableInList(final List<U> objectList, final U obj) {
		for(BaseDomain i : objectList) {
			if(i != null && obj != null){
				if(obj.getId().equals(i.getId())) {
					return true;
				}
			}
		}		
		return false;
	}
    
    protected String populateDelimitedSupportingMemberNames(final List<SupportingMember> supportingMembers, final String locale) {
		String memberNames = "";
		if(supportingMembers != null){
			if(!supportingMembers.isEmpty()){
				StringBuffer bufferFirstNamesFirst = new StringBuffer();
				for(SupportingMember i:supportingMembers){
					Member m = i.getMember();
					if(m.isActiveMemberOn(new Date(), locale)){
						bufferFirstNamesFirst.append(m.getFullname() + ",");
					}
				}
				if(bufferFirstNamesFirst.length()>0){
					bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length()-1);
					memberNames = bufferFirstNamesFirst.toString();
				}
			}
		}
		return memberNames;
	}
    
    protected List<DeviceVO> populateDevicesForNumberedYaadi(List<Device> devices, final String locale) {
    	List<DeviceVO> devicesForNumberedYaadi = new ArrayList<DeviceVO>();
    	
    	if(devices!=null && !devices.isEmpty()) {    		
    		for(Device device: devices) {
    			if(device instanceof Question) {
    				Question q = (Question) device;
    				if(q!=null && q.getId()!=null) {
    					DeviceVO deviceForNumberedYaadi = new DeviceVO();
    					deviceForNumberedYaadi.setId(q.getId());
    					deviceForNumberedYaadi.setNumber(q.getNumber());
    					deviceForNumberedYaadi.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(q.getNumber(), locale));
    					if(q.getYaadiNumber()!=null) {
    						deviceForNumberedYaadi.setIsPresentInYaadi(true);
    					} else {
    						deviceForNumberedYaadi.setIsPresentInYaadi(false);
    					}
    					if(q.getGroup()!=null) {
    						deviceForNumberedYaadi.setGroupNumber(q.getGroup().getNumber());
    						deviceForNumberedYaadi.setFormattedGroupNumber(FormaterUtil.formatNumberNoGrouping(q.getGroup().getNumber(), locale));
    					}
    					/**** member names ****/
    					String allMemberNames = "";	
    					String houseTypeType = q.getHouseType().getType();	
    					String memberNameFormat = null;
    					CustomParameter memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_YADI_MEMBERNAMEFORMAT_"+houseTypeType.toUpperCase(), "");
    					if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
    						memberNameFormat = memberNameFormatParameter.getValue();						
    					} else {
    						memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;
    					}
    					if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
    						allMemberNames = q.findAllMemberNamesWithConstituencies(memberNameFormat);
    					} else {
    						allMemberNames = q.findAllMemberNames(memberNameFormat);
    					}
    					/**** add below code in case space between title & member name should be removed ****/
    					if(allMemberNames!=null && !allMemberNames.isEmpty()) {
    						List<Title> titles = Title.findAll(Title.class, "name", ApplicationConstants.ASC, locale);
    						if(titles!=null && !titles.isEmpty()) {
    							for(Title t: titles) {
    								if(t.getName().trim().endsWith(".")) {
    									allMemberNames = allMemberNames.replace(t.getName().trim()+" ", t.getName().trim());
    								}
    							}
    						}
    					}
    					deviceForNumberedYaadi.setMemberNames(allMemberNames);
    					/**** subject ****/
    					if(q.getRevisedSubject()!=null && !q.getRevisedSubject().isEmpty()) {
    						deviceForNumberedYaadi.setSubject(q.getRevisedSubject());
    					} else if(q.getSubject()!=null && !q.getSubject().isEmpty()) {
    						deviceForNumberedYaadi.setSubject(q.getSubject());
    					} else {
    						deviceForNumberedYaadi.setSubject("");
    					}
    					/**** content ****/
    					String content = q.getRevisedQuestionText();
    					if(content!=null && !content.isEmpty()) {
    						if(content.endsWith("<br><p></p>")) {
    							content = content.substring(0, content.length()-11);						
    						} else if(content.endsWith("<p></p>")) {
    							content = content.substring(0, content.length()-7);					
    						}    								
    					} else {
    						content = q.getQuestionText();
    						if(content!=null && !content.isEmpty()) {
    							if(content.endsWith("<br><p></p>")) {
    								content = content.substring(0, content.length()-11);							
    							} else if(content.endsWith("<p></p>")) {
    								content = content.substring(0, content.length()-7);					
    							}    										
    						} else {
    							content = "";
    						}
    					}
    					deviceForNumberedYaadi.setContent(content);
    					/**** answer ****/
    					String answer = q.getAnswer();
    					if(answer != null) {
    						if(answer.endsWith("<br><p></p>")) {
    							answer = answer.substring(0, answer.length()-11);						
    						} else if(answer.endsWith("<p></p>")) {
    							answer = answer.substring(0, answer.length()-7);					
    						}
    					} else {
    						answer = "";
    					}
    					deviceForNumberedYaadi.setAnswer(answer);
    					/**** short details ****/
    					deviceForNumberedYaadi.setShortDetails(q.findShortDetailsTextForYaadi(false));
    					devicesForNumberedYaadi.add(deviceForNumberedYaadi);
    				}
    				
    			} //add remaining cases in else parts..  			
    		}
    	}
    	
    	return devicesForNumberedYaadi;
    }
    
    protected List<String> populateSerialNumbers(@SuppressWarnings("rawtypes") List dataList, Locale locale) {
    	List<String> serialNumbers = new ArrayList<String>();
    	if(dataList!=null && !dataList.isEmpty()) {
    		for(int i=1; i<=dataList.size(); i++) {
    			serialNumbers.add(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
    		}
    	}    	
		return serialNumbers;
    }
    
    protected List<String> populateSerialNumbers(@SuppressWarnings("rawtypes") List dataList, String locale) {
    	List<String> serialNumbers = new ArrayList<String>();
    	if(dataList!=null && !dataList.isEmpty()) {
    		for(int i=1; i<=dataList.size(); i++) {
    			serialNumbers.add(FormaterUtil.formatNumberNoGrouping(i, locale));
    		}
    	}    	
		return serialNumbers;
    }
    
    @SuppressWarnings("unchecked")
	protected void generateTabularFOPReport(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		this.generateTabularFOPReport(request, response, request.getParameterMap(), locale);
	}
    
    @SuppressWarnings("unchecked")
	protected void generateTabularFOPReport(HttpServletRequest request, HttpServletResponse response, Map<String, String[]> parametersMap, Locale locale) {
		File reportFile = null;
		Boolean isError = false;
		MessageResource errorMessage = null;
		
		String reportQuery = request.getParameter("reportQuery");
		String xsltFileName = request.getParameter("xsltFileName");
		String outputFormat = request.getParameter("outputFormat");
		String reportFileName = request.getParameter("reportFileName");
		
		if(reportQuery!=null && !reportQuery.isEmpty()
				&& xsltFileName!=null && !xsltFileName.isEmpty()
				&& outputFormat!=null && !outputFormat.isEmpty()
				&& reportFileName!=null && !reportFileName.isEmpty()) {
			try {
				//Map<String, String[]> requestMap = request.getParameterMap();
				/** Populate Headers **/
				List<Object[]> reportHeaders = Query.findReport(request.getParameter("reportQuery")+"_HEADERS", parametersMap);
				/** Populate Data **/
				@SuppressWarnings("rawtypes")
				List reportData = Query.findReport(request.getParameter("reportQuery"), parametersMap);
				/**** generate fop report ****/
				/** create report in reportFile **/
				reportFile = generateReportUsingFOP(new Object[] {reportHeaders, reportData}, xsltFileName, outputFormat, reportFileName, locale.toString());
				/** open reportFile for view/download in browser **/
	    		if(reportFile!=null) {
	    			System.out.println("Report generated successfully in " + outputFormat + " format!");
	    			openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
	    		}
			} catch(Exception e) {
				e.printStackTrace();
				isError = true;					
				errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.exception_occured", locale.toString());
			}
		} else {
			isError = true;
			logger.error("**** Check request parameters reportQuery, xsltFileName, outputFormat, reportFileName for null values ****");
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.reqparam.null", locale.toString());
		}
		if(isError) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
				if(errorMessage != null) {
					if(!errorMessage.getValue().isEmpty()) {
						response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + errorMessage.getValue() + "</h3></body></html>");
					} else {
						response.getWriter().println("<h3>Some Error In Report Generation. Please Contact Administrator.</h3>");
					}
				} else {
					response.getWriter().println("<h3>Some Error In Report Generation. Please Contact Administrator.</h3>");
				}

				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}
	}
    
//    protected String insertLineBreaksInContent(String content) {
//		StringBuffer lineDividedContent = new StringBuffer("");
//		int lineCharLength = 150;
//		int beginIndex = 0;
//		int splitCharIndex = lineCharLength-1;
//		while(splitCharIndex<=content.length()) {				
//			String lineText = content.substring(beginIndex, splitCharIndex+1);				
//			char[] lineTextCharArray = lineText.toCharArray();
//			int spaceCount = 0;
//			for(int i=0; i < lineTextCharArray.length; i++) {
//				System.out.println("beginIndex: "+beginIndex+" , splitCharIndex: "+splitCharIndex);
//				if(spaceCount<10) {
//					if(lineText.contains("<br>")) {
//						int indexOfExistingLineBreak = lineText.indexOf("<br>");
//						lineDividedContent.append(lineText.substring(0, indexOfExistingLineBreak));
//						lineDividedContent.append("<br>");
//						beginIndex += indexOfExistingLineBreak+4;
//						splitCharIndex += indexOfExistingLineBreak+4;						
//						break;
//						
//					} else if(lineText.contains("<br/>")) {
//						int indexOfExistingLineBreak = lineText.indexOf("<br/>");
//						lineDividedContent.append(lineText.substring(0, indexOfExistingLineBreak));
//						lineDividedContent.append("<br>");
//						beginIndex += indexOfExistingLineBreak+5;
//						splitCharIndex += indexOfExistingLineBreak+5;						
//						break;
//						
//					} else if(lineText.contains("<br />")) {
//						int indexOfExistingLineBreak = lineText.indexOf("<br />");
//						lineDividedContent.append(lineText.substring(0, indexOfExistingLineBreak));
//						lineDividedContent.append("<br />");
//						beginIndex += indexOfExistingLineBreak+6;
//						splitCharIndex += indexOfExistingLineBreak+6;			
//						break;
//						
//					}
//				}
//				if(lineTextCharArray[i]==' ') {
//					boolean isSpaceAfterText = false;
//					if((i==0 || lineTextCharArray[i-1]!=' ') && (i==lineTextCharArray.length-1 || lineTextCharArray[i+1]!=' ')) {
//						//String lineTextAfterSpace = lineText.substring(i, lineTextCharArray.length);
//						if(content.indexOf(">", beginIndex+i) == -1
//								|| content.indexOf(">", beginIndex+i) > content.indexOf("<", beginIndex+i)) {
//							isSpaceAfterText = true;
//						}
//					}
//					if(isSpaceAfterText) {
//						if(spaceCount==10) {
//							lineDividedContent.append(lineText.substring(0, i));
//							lineDividedContent.append("<br>");
//							beginIndex += i+1;
//							splitCharIndex += i+1;						
//							break;
//						} else {
//							spaceCount++;			
//						}
//					}					
//				}
//				if(i == lineTextCharArray.length-1) {
//					lineDividedContent.append(lineText.substring(0, lineCharLength));
//					beginIndex += lineCharLength;
//					splitCharIndex += lineCharLength;						
//					break;
//				}					
//			}
//			if(splitCharIndex >= content.length()) {
//				lineDividedContent.append(content.substring(beginIndex, content.length()));
//				break;
//			}
//		}
//		return lineDividedContent.toString();
//	}
    
}
