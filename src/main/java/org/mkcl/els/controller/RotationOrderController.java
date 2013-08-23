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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.QuestionDatesVO;
import org.mkcl.els.common.vo.RotationOrderReportVO;
import org.mkcl.els.common.vo.RotationOrderVO;
import org.mkcl.els.common.xmlvo.AadwaChartXmlVO;
import org.mkcl.els.common.xmlvo.RotationOrderXmlVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.NumberInfo;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
		            		String answeringDay=FormaterUtil.getDayInMarathi(strAnsweringDates[0],locale.toString());
		            		String[] strAnsweringMonth=strAnsweringDates[1].split(" ");
		            		String answeringMonth=FormaterUtil.getMonthInMarathi(strAnsweringMonth[1], locale.toString());
		            		
		            		answeringDates.add(answeringDay+","+strAnsweringMonth[0]+" "+ answeringMonth +","+strAnsweringDates[2]);
		            		
		            		String[] strSubmissionDates=dbFormat.format(d.getFinalSubmissionDate()).split(",");
		            		String submissionDay=FormaterUtil.getDayInMarathi(strSubmissionDates[0],locale.toString());
		            		String[] strSubmissionMonth=strSubmissionDates[1].split(" ");
		            		String submissionMonth=FormaterUtil.getMonthInMarathi(strSubmissionMonth[1], locale.toString());
		            		
		            		finalSubmissionDates.add(submissionDay+","+strSubmissionMonth[0]+" "+ submissionMonth +","+strSubmissionDates[2]);
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
			        	List<String> ministriesStr= new ArrayList<String>();
			        	List<String> numberOfMinisteries= new ArrayList<String>();
			        	int i=1;
			        	for(Ministry m:ministries){
			        		ministriesStr.add("(" + numberFormat.format(i++) + ") " + m.getName());
			        		//numberOfMinisteries.add(numberFormat.format(i++));
			        	}
			        	List<String> answeringDates= new ArrayList<String>();
			        	List<String> finalSubmissionDates=new ArrayList<String>();
			        	for(QuestionDates d:dates){
			        		//Added the following code to solve the marathi month and day issue
			        		String[] strAnsweringDates=dbFormat.format(d.getAnsweringDate()).split(",");
			        		String answeringDay=FormaterUtil.getDayInMarathi(strAnsweringDates[0],locale.toString());
			        		String[] strAnsweringMonth=strAnsweringDates[1].split(" ");
			        		String answeringMonth=FormaterUtil.getMonthInMarathi(strAnsweringMonth[1], locale.toString());
			        		
			        		answeringDates.add(answeringDay+","+strAnsweringMonth[0]+" "+ answeringMonth +","+strAnsweringDates[2]);
			        		
			        		String[] strSubmissionDates=dbFormat.format(d.getFinalSubmissionDate()).split(",");
			        		String submissionDay=FormaterUtil.getDayInMarathi(strSubmissionDates[0],locale.toString());
			        		String[] strSubmissionMonth=strSubmissionDates[1].split(" ");
			        		String submissionMonth=FormaterUtil.getMonthInMarathi(strSubmissionMonth[1], locale.toString());
			        		
			        		finalSubmissionDates.add(submissionDay+","+strSubmissionMonth[0]+" "+ submissionMonth +","+strSubmissionDates[2]);
			        	}            	
			        	String groupNumberText = NumberInfo.findNumberText(new Long(g.getNumber()), locale.toString());
			        	rotationOrderVO.setGroup(groupNumberText);
			        	//rotationOrderVO.setGroup(numberFormat.format(g.getNumber()));
			        	rotationOrderVO.setMinistries(ministriesStr);
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
}
