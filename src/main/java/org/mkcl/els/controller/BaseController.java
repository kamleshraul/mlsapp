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
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.fop.apps.MimeConstants;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.xmlvo.XmlVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.service.impl.ReportServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
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

    /**** Report Generation Using FOP with default fop config file 
     * @throws Exception ****/
    protected File generateReportUsingFOP(XmlVO data, String xsltFileName, String reportFormat, String reportFileName, String locale) throws Exception {
    	return this.generateReportUsingFOP(data, "fopConfig_autodetect", xsltFileName, reportFormat, reportFileName, locale);    	
    }
    
    protected File generateReportUsingFOP(final Object[] report, String xsltFileName, String reportFormat, String reportFileName, String locale) throws Exception {
    	return this.generateReportUsingFOP(report, "fopConfig_autodetect", xsltFileName, reportFormat, reportFileName, locale);    	
    }
    
    /**** Report Generation Using FOP with custom fop config file 
     * @throws Exception ****/
    protected File generateReportUsingFOP(XmlVO data, String fopConfigFileName, String xsltFileName, String reportFormat, String reportFileName, String locale) throws Exception {
    	File reportFile = null;    	
        ReportServiceImpl reportGenerator = null;
        data.setLocale(locale);
        CustomParameter reportDateFormatParameter = CustomParameter.findByName(CustomParameter.class, xsltFileName.toUpperCase() + "_REPORTDATE_FORMAT", "");
		if(reportDateFormatParameter!=null && reportDateFormatParameter.getValue()!=null) {					
			data.setReportDate(FormaterUtil.formatDateToString(new Date(), reportDateFormatParameter.getValue(), locale));
		} else {
			data.setReportDate(FormaterUtil.formatDateToString(new Date(), ApplicationConstants.REPORT_DATEFORMAT, locale));
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
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
