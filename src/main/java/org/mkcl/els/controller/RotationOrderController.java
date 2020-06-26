/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.RotationOrderController.java
 * Created On: Jul 20, 2013
 * @since v1.0.0
 */
package org.mkcl.els.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.QuestionDatesVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RotationOrderReportVO;
import org.mkcl.els.common.vo.RotationOrderVO;
import org.mkcl.els.common.xmlvo.AadwaChartXmlVO;
import org.mkcl.els.common.xmlvo.RotationOrderXmlVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.NumberInfo;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/rotationorder")
public class RotationOrderController extends BaseController {

	/**
	 * Gets the rotation order page.
	 *
	 * @param request the request
	 * @param model the model
	 * @param locale the locale
	 * @return the rotation order page
	 */
	@RequestMapping(value="/init",method=RequestMethod.GET)
	public String getRotationOrderPage(final HttpServletRequest request,final ModelMap model,
			final Locale locale){
		List<MasterVO> outputFormats = new ArrayList<MasterVO>();
		MasterVO pdfFormat = new MasterVO();
		pdfFormat.setName("PDF");
		pdfFormat.setValue("PDF");
		outputFormats.add(pdfFormat);		
		MasterVO wordFormat = new MasterVO();
		wordFormat.setName("WORD");
		wordFormat.setValue("WORD");
		outputFormats.add(wordFormat);
		MasterVO htmlFormat = new MasterVO();
		htmlFormat.setName("HTML");
		htmlFormat.setValue("HTML");
		outputFormats.add(htmlFormat);
		MasterVO rtfFormat = new MasterVO();
		rtfFormat.setName("RTF");
		rtfFormat.setValue("RTF");
		outputFormats.add(rtfFormat);
		model.addAttribute("outputFormats", outputFormats);
		return "rotationorder/rotationorderinit";
	}
	
	/**
	 * Prints the report.
	 */
	@RequestMapping(value="/aadwachart",method=RequestMethod.GET)
    public String printReport(final HttpServletRequest request,
    		final Locale locale,
    		final ModelMap model){      	
        String strHouseType=request.getParameter("houseType");
        model.addAttribute("aadwaHouseType", strHouseType);
        String strSessionType=request.getParameter("sessionType");
        String strSessionYear=request.getParameter("sessionYear");
        if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null){
            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
            Integer sessionYear=Integer.parseInt(strSessionYear);
            List<QuestionDatesVO> questionDates=Group.findAllGroupDatesFormatted(houseType, sessionType, sessionYear,locale.toString());
            model.addAttribute("dates",questionDates);
        }else{
        	model.addAttribute("error", "Some of the parameters are missing.");
        }
        return "rotationorder/aadwachart";
    }
	
	/**
	 * View aadwa chart report.
	 *
	 * @param request the request
	 * @param response the response
	 * @param locale the locale
	 * @param model the model
	 */
	@RequestMapping(value="/viewaadwachartreport",method=RequestMethod.GET)
    public @ResponseBody void viewAadwaChartReport(final HttpServletRequest request, 
    		final HttpServletResponse response,
    		final Locale locale,
    		final ModelMap model){      	
		File reportFile = null; 
		
		String strHouseType=request.getParameter("houseType");
        String strSessionType=request.getParameter("sessionType");
        String strSessionYear=request.getParameter("sessionYear");
        String reportFormat=request.getParameter("outputFormat");
        if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&reportFormat!=null){
        	if(!strHouseType.isEmpty()&&!strSessionType.isEmpty()&&!strSessionYear.isEmpty()&&!reportFormat.isEmpty()) {
        		HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
                SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
                Integer sessionYear=Integer.parseInt(strSessionYear);
                List<QuestionDatesVO> questionDates=Group.findAllGroupDatesFormatted(houseType, sessionType, sessionYear,locale.toString());

                AadwaChartXmlVO data = new AadwaChartXmlVO();                
                /**** In order to find the session number ****/
                /**** Find all the sessions for the year and sort ****/
                /**** them on start date and find the counter position ****/
                /****on which this session occurs ****/
                try {
					Session currentSession = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
					
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("locale", new String[]{locale.toString()});
					parameters.put("sessionId", new String[]{currentSession.getId().toString()});
					List count = Query.findReport("FIND_PREVIOUS_SESSIONS_COUNT", parameters);
					Integer counter =  (((BigInteger)count.get(0)).intValue() + 1);
					data.setSessionNumber((count != null && !count.isEmpty())? counter.toString(): "0");
				} catch (ELSException e1) {
					e1.printStackTrace();
					model.addAttribute("error", e1.getParameter());
				}
                
                data.setHouseType(houseType.getType());
                data.setHouseTypeName(houseType.getName());
                data.setSessionTypeName(sessionType.getSessionType());
                data.setSessionYearName(FormaterUtil.formatNumberNoGrouping(sessionYear, locale.toString()));
                data.setRotationOrderDatesList(questionDates);
                
                //generate report
        		try {
        			reportFile = generateReportUsingFOP(data, "template_aadwachart_report", reportFormat, "rotationOrder_aadwaChart", locale.toString());
        		} catch (Exception e) {
					e.printStackTrace();
				}
        		System.out.println("Rotation Order Aadwa Chart Report generated successfully in " + reportFormat + " format!");
                
        		openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
        	} else{
				logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,outputFormat' for empty values ****");
				try {
					response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,outputFormat' for empty values</h3>");
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}            
	    } else{
			logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,outputFormat' for null values ****");
			try {
				response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,outputFormat' for null values</h3>");
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}       
    }
	
	/**
	 * 
	 * Prints the rotation order.
	 */
	@RequestMapping(value="/viewrotationorder" ,method=RequestMethod.GET)
	public String printRotationOrder(final HttpServletRequest request, 
			final Locale locale,
			final ModelMap model){
		 String strHouseType=request.getParameter("houseType");
	     String strSessionType=request.getParameter("sessionType");
	     String strSessionYear=request.getParameter("sessionYear");
	     SimpleDateFormat dbFormat = null;
	     if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null){
	            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
	            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
	            Integer sessionYear=Integer.parseInt(strSessionYear);
	            Session session;
				try {
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				
	           
		            CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
		            if(dbDateFormat!=null){
		            	dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
		            }
		            NumberFormat numberFormat=FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
		            List<Group> groups = null;
				
					groups = Group.findByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				
		            List<RotationOrderVO> rotationOrderVOs= new ArrayList<RotationOrderVO>();
		            
		            for(Group g:groups){
		            	RotationOrderVO rotationOrderVO= new RotationOrderVO();
		            	List<Ministry> ministries=Group.findMinistriesByPriority(g);
		            	List<QuestionDates> dates= g.getQuestionDates();
		            	List<String> ministriesStr= new ArrayList<String>();
		            	List<String> numberOfMinisteries= new ArrayList<String>();
		            	int i=1;
		            	for(Ministry m:ministries){
		            		ministriesStr.add(m.getName());
		            		numberOfMinisteries.add(numberFormat.format(i++));
		            	}
		            	List<String> answeringDates= new ArrayList<String>();
		            	List<String> finalSubmissionDates=new ArrayList<String>();
		            	for(QuestionDates d:dates){
		            		//Added the following code to solve the marathi month and day issue
		            		String[] strAnsweringDates=dbFormat.format(d.getAnsweringDate()).split(",");
		            		String answeringDay=FormaterUtil.getDayInLocaleLanguage(strAnsweringDates[0],locale.toString());
		            		String[] strAnsweringMonth=strAnsweringDates[1].split(" ");
		            		String answeringMonth=FormaterUtil.getMonthInLocaleLanguage(strAnsweringMonth[1], locale.toString());
		            		
		            		MessageResource mrDate = MessageResource.findByFieldName(MessageResource.class, "code", "generic.date", locale.toString());
		            		String genericDateLabel  = (mrDate!=null)? mrDate.getValue():"";
		            		
		            		answeringDates.add(answeringDay+", "+ genericDateLabel + " " +strAnsweringMonth[0]+" "+ answeringMonth +","+strAnsweringDates[2]);
		            		
		            		
		            		String[] strSubmissionDates=dbFormat.format(d.getFinalSubmissionDate()).split(",");
		            		String submissionDay=FormaterUtil.getDayInLocaleLanguage(strSubmissionDates[0],locale.toString());
		            		String[] strSubmissionMonth=strSubmissionDates[1].split(" ");
		            		String submissionMonth=FormaterUtil.getMonthInLocaleLanguage(strSubmissionMonth[1], locale.toString());
		            		
		            		finalSubmissionDates.add(submissionDay+", " + genericDateLabel + " " +strSubmissionMonth[0]+" "+ submissionMonth +","+strSubmissionDates[2]);
		            	}
		            	rotationOrderVO.setGroup(numberFormat.format(g.getNumber()));
		            	rotationOrderVO.setMinistries(ministriesStr);
		            	rotationOrderVO.setNumberOfMinisteries(numberOfMinisteries);
		            	rotationOrderVO.setAnsweringDates(answeringDates);
		            	rotationOrderVO.setFinalSubmissionDates(finalSubmissionDates);
		            	rotationOrderVOs.add(rotationOrderVO);
		            }
		            model.addAttribute("rotationOrderHeader", session.getParameter("questions_starred_rotationOrderHeader"));
		            model.addAttribute("rotationOrderCover", session.getParameter("questions_starred_rotationOrderCover"));
		            model.addAttribute("rotationOrderFooter", session.getParameter("questions_starred_rotationOrderFooter"));
		            model.addAttribute("dates", rotationOrderVOs);
		            
		            
		            List ministryreport = getMinistryReport(locale, session.getStartDate(), model);
		            model.addAttribute("ministryreport", ministryreport);
				}catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
				}catch (Exception e) {
					String message = e.getMessage();
					
					if(message == null){
						message = "There is some problem, request may not complete successfully.";
					}
					
					model.addAttribute("error", message);
					e.printStackTrace();
				}
	        }
		return "rotationorder/viewrotationorder";
	}
	
	/**
	 * Generate rotation order report.
	 *
	 * @param request the request
	 * @param response the response
	 * @param locale the locale
	 * @param model the model
	 */
	@RequestMapping(value="/viewrotationorderreport" ,method=RequestMethod.GET)
	public @ResponseBody void generateRotationOrderReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		
		String strHouseType=request.getParameter("houseType");
	    String strSessionType=request.getParameter("sessionType");
	    String strSessionYear=request.getParameter("sessionYear");
	    String reportFormat=request.getParameter("outputFormat");
	    SimpleDateFormat dbFormat = null;
	    try{
		    if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&reportFormat!=null){
		    	if(!strHouseType.isEmpty()&&!strSessionType.isEmpty()&&!strSessionYear.isEmpty()&&!reportFormat.isEmpty()) {
		    		HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
			    	SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
			    	Integer sessionYear=Integer.parseInt(strSessionYear);
			    	Session session= Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
		           
			    	CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
			    	if(dbDateFormat!=null){
			    		dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
			    	}
			    	NumberFormat numberFormat=FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
			    	List<Group> groups= Group.findByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			    	List<RotationOrderReportVO> rotationOrderVOs= new ArrayList<RotationOrderReportVO>();
		            
			        for(Group g:groups){
			        	RotationOrderReportVO rotationOrderVO= new RotationOrderReportVO();
			        	List<Ministry> ministries=Group.findMinistriesByPriority(g);
			        	List<QuestionDates> dates= g.getQuestionDates();
			        	List<Reference> ministryVOs= new ArrayList<Reference>();			        	
			        	int i=1;
			        	for(Ministry m:ministries){
			        		Reference ministryVO = new Reference();
			        		ministryVO.setNumber(numberFormat.format(i++));
			        		ministryVO.setName(m.getName());
			        		ministryVOs.add(ministryVO);
			        		//numberOfMinisteries.add(numberFormat.format(i++));
			        	}
			        	List<String> answeringDates= new ArrayList<String>();
			        	List<String> finalSubmissionDates=new ArrayList<String>();
			        	for(QuestionDates d:dates){
			        		//Added the following code to solve the marathi month and day issue
			        		String[] strAnsweringDates=dbFormat.format(d.getAnsweringDate()).split(",");
			        		String answeringDay=FormaterUtil.getDayInLocaleLanguage(strAnsweringDates[0],locale.toString());
			        		String[] strAnsweringMonth=strAnsweringDates[1].split(" ");
			        		String answeringMonth=FormaterUtil.getMonthInLocaleLanguage(strAnsweringMonth[1], locale.toString());
			        		
			        		MessageResource mrDate = MessageResource.findByFieldName(MessageResource.class, "code", "generic.date", locale.toString());
		            		String genericDateLabel  = (mrDate!=null)? mrDate.getValue():"";
			        		
		            		answeringDates.add(answeringDay+", "+ genericDateLabel + " " +strAnsweringMonth[0]+" "+ answeringMonth +","+strAnsweringDates[2]);
			        		
			        		String[] strSubmissionDates=dbFormat.format(d.getFinalSubmissionDate()).split(",");
			        		String submissionDay=FormaterUtil.getDayInLocaleLanguage(strSubmissionDates[0],locale.toString());
			        		String[] strSubmissionMonth=strSubmissionDates[1].split(" ");
			        		String submissionMonth=FormaterUtil.getMonthInLocaleLanguage(strSubmissionMonth[1], locale.toString());
			        		
			        		finalSubmissionDates.add(submissionDay+", " + genericDateLabel + " " +strSubmissionMonth[0]+" "+ submissionMonth +","+strSubmissionDates[2]);
			        	}            	
			        	String groupNumberText = NumberInfo.findNumberText(new Long(g.getNumber()), locale.toString());
			        	rotationOrderVO.setGroup(groupNumberText);
			        	//rotationOrderVO.setGroup(numberFormat.format(g.getNumber()));
			        	rotationOrderVO.setMinistries(ministryVOs);
			        	//rotationOrderVO.setNumberOfMinisteries(numberOfMinisteries);
			        	rotationOrderVO.setAnsweringDates(answeringDates);
			        	rotationOrderVO.setFinalSubmissionDates(finalSubmissionDates);
			        	rotationOrderVOs.add(rotationOrderVO);
			        }
		            
		            RotationOrderXmlVO data = new RotationOrderXmlVO();  
		            data.setRotationOrderMainCover(session.getParameter("questions_starred_rotationOrderCover"));
		            data.setRotationOrderMainHeader(session.getParameter("questions_starred_rotationOrderHeader"));
		            data.setRotationOrderMainFooter(session.getParameter("questions_starred_rotationOrderFooter"));            
		            data.setRotationOrderForGroupList(rotationOrderVOs);
		            
		            //generate report
		    		try {
						reportFile = generateReportUsingFOP(data, "template_autodetect", reportFormat, "rotationOrder", locale.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
		    		System.out.println("Rotation Order Report generated successfully in " + reportFormat + " format!");
		        
		    		openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
		    	} else{
					logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,outputFormat' for empty values ****");
					try {
						response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,outputFormat' for empty values</h3>");
					} catch (IOException e) {
						e.printStackTrace();
					}				
				}	    	
		    } else{
				logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,outputFormat' for null values ****");
				try {
					response.getWriter().println("<h3>Check request parameter 'houseType,sessionType,sessionYear,outputFormat' for null values</h3>");
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
	    }catch (ELSException e) {
	    	logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,outputFormat' for empty values ****");
			try {
				response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,outputFormat' for empty values</h3>");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			model.addAttribute("error", e.getParameter());
		} catch (Exception e) {
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			
			model.addAttribute("error", message);
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/viewministryreport", method=RequestMethod.GET)
	public String viewMinistryReport(ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		try{
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strHouseType = request.getParameter("houseType");
			if(strSessionType != null && !strSessionType.isEmpty()
				&& strSessionYear != null && !strSessionYear.isEmpty()
				&& strHouseType != null && !strHouseType.isEmpty()){
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				Session session = Session.
						findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
				List ministryreport = getMinistryReport(locale, session.getStartDate(), model);
	            model.addAttribute("ministryreport", ministryreport);
			}
			
            
		}catch (Exception e) {
			String msg = e.getMessage();
			
			if(e instanceof ELSException){
				model.addAttribute("error", ((ELSException) e).getParameter());
			}else{
				if(msg == null){
					model.addAttribute("error", "Request may not complete successfully.");
				}else{
					model.addAttribute("error", msg);
				}
			}
			logger.error(msg);
			e.printStackTrace();			
		}
		
		return "rotationorder/viewministryreport";
	}
	
	@RequestMapping(value="/viewministrydepartmentreport", method=RequestMethod.GET)
	public void viewMinistryDepartmentReport(ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strHouseType = request.getParameter("houseType");
		if(strSessionType != null && !strSessionType.isEmpty()
			&& strSessionYear != null && !strSessionYear.isEmpty()
			&& strHouseType != null && !strHouseType.isEmpty()){
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
			try {
				Session session = Session.
						findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
				List ministryreport = getMinistryReport(locale, session.getStartDate(), model);
				model.addAttribute("ministryreport", ministryreport);
				
				Map reportFields = simplifyMinistryDepartmentReport(ministryreport);
				String strOutputFormat = request.getParameter("outputFormat");
				File reportFile = generateReportUsingFOP(new Object[]{reportFields}, "template_ministrydepartment", strOutputFormat, "ministry department report", locale.toString());
				openOrSaveReportFileFromBrowser(response, reportFile, strOutputFormat);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	}
	
	private List getMinistryReport(Locale locale, Date onDate, ModelMap model){
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		 parameters.put("onDate", new String[]{onDate.toString()});
        parameters.put("locale", new String[]{locale.toString()});
        List ministryreport = Query.findReport(ApplicationConstants.ROTATIONORDER_MINISTRY_DEPARTMENTS_REPORT, parameters);
        
        String ministryName = "";
        for(int index = 0, ministryCounter = 0, deptCounter = 1; index < ministryreport.size(); index++){
        	
        	Object[] row = (Object[])ministryreport.get(index);
        	String minName = (row[2] != null)? row[2].toString(): null; 
        	
        	if(!ministryName.equals(minName)){
        		deptCounter = 1;
        		ministryCounter++;		            		
        	}
        	((Object[])ministryreport.get(index))[0] = FormaterUtil.formatNumberNoGrouping(Integer.valueOf(ministryCounter), locale.toString());
        	((Object[])ministryreport.get(index))[4] = FormaterUtil.formatNumberNoGrouping(Integer.valueOf(deptCounter), locale.toString());
        	deptCounter++;
        	ministryName = row[2].toString();
        }
        return ministryreport;
	}
	
	@SuppressWarnings("rawtypes")
	private Map simplifyMinistryDepartmentReport(final List report){
		Map<String, List> ministryDepartmentData = new LinkedHashMap<String, List>();
		String ministryName = "";
		String ministryNameToPut = "";
		String currMinistry = "";
		List<List<Object>> departmentsForMinistry = new ArrayList<List<Object>>();
		
		for(Object o: report){
			Object[] objArr = (Object[])o;
			List<Object> reportFields = new ArrayList<Object>();
			if(!ministryName.equals(objArr[2].toString())){
				if(!ministryName.isEmpty()){
					ministryDepartmentData.put(ministryNameToPut, departmentsForMinistry);
					departmentsForMinistry = null;
					departmentsForMinistry = new ArrayList<List<Object>>();
				}
				ministryName = objArr[2].toString();
				ministryNameToPut = "(" + objArr[0].toString()+")   " + objArr[1].toString() + " --- " + objArr[2].toString();
				reportFields.add("    (" + objArr[4].toString() +") " + objArr[3].toString());
				departmentsForMinistry.add(reportFields);
				
			}else{
				reportFields.add("    (" + objArr[4].toString() +") " + objArr[3].toString());
				departmentsForMinistry.add(reportFields);
			}
			reportFields = null;
		}
		
		ministryDepartmentData.put(ministryNameToPut, departmentsForMinistry);
		
		return ministryDepartmentData;
	}
}
