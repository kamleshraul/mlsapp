/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.mis.MemberListController.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.controller.mis;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.node.ObjectNode;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MemberCompleteDetailVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Query;
import org.mkcl.els.service.ISecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class MemberListController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("member")
public class MemberListController extends GenericController<Member> {
	
	@Autowired 
	private ISecurityService securityService;

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateModule(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest, java.lang.String, org.mkcl.els.common.vo.AuthUser)
	 */
	@Override
	protected void populateModule(final ModelMap model,
			final HttpServletRequest request, final String locale,
			final AuthUser currentUser) {
		//This is changed to accomodate separate menus for assembly/council
		//This is used to set houseType parameter in module.jsp which will be passed
		//as query string in module/list
		model.addAttribute("housetype", request.getParameter("houseType"));
	}

	@Override
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(@RequestParam(required = false) final String formType,
            final ModelMap model, final Locale locale,
            final HttpServletRequest request) {
	    String urlPattern=null;
	    String houseType=request.getParameter("houseType");
	    if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
	        urlPattern=ApplicationConstants.LOWERHOUSEGRID;
	    }else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
	        urlPattern=ApplicationConstants.UPPERHOUSEGRID;
	    }
	    Grid grid;
		try {
			grid = Grid.findByDetailView(urlPattern, locale.toString());
			 model.addAttribute("gridId", grid.getId());
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
       
        populateList(model, request, locale.toString(), this.getCurrentUser());
        String currentDate=request.getParameter("currentDate");
        if(currentDate==null){
        currentDate=FormaterUtil.getDateFormatter("en_US").format(new Date());
	    }
        model.addAttribute("selectedDate", currentDate);
        return "member/list";
	}


	@Override
	protected void populateList(final ModelMap model, final HttpServletRequest request,
			final String locale, final AuthUser currentUser) {
		//Here depending on the link vidhan parishad or vidhan sabha clicked by user appropriate houses are loaded so
		//as to populate the select assembly/council select box in list.jsp.The houses are sorted according to their formation date
		//so that current will always be at the top
		String houseType=request.getParameter("houseType");
		model.addAttribute("houseType",houseType);
		List<House> houses;
		try {
			houses = House.findByHouseType(houseType, locale);
			model.addAttribute("assemblies",houses);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
	}

	@RequestMapping(value="/view/{member}",method=RequestMethod.GET)
	public String view(@PathVariable("member") final Long member,final ModelMap model,final Locale locale){
		MemberCompleteDetailVO memberCompleteDetailVO=Member.getCompleteDetail(member,locale.toString());
		model.addAttribute("member",memberCompleteDetailVO);
		return "member/view";
	}
	

	
	@RequestMapping(value="/printCredentials",method=RequestMethod.GET)
	public String printCredentials(final HttpServletRequest request,final ModelMap model,final Locale locale){
			String returnValue = "member/error"; 
		try{

		
				String member=request.getParameter("member");
				Long houseId=Long.parseLong(request.getParameter("house"));
			/**** find report data ****/
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
	
			queryParameters.put("houseId", new String[]{houseId.toString()});
			queryParameters.put("memberId", new String[]{member.toString()});
			queryParameters.put("locale", new String[]{locale.toString()});
	
			List<Object[]> reportData = Query.findReport("MIS_REPORT_CREDENTIAL", queryParameters);
			
			model.addAttribute("report", reportData);
		
			if(reportData!=null && !reportData.isEmpty()) {
				String username = reportData.get(0)[9].toString();
				if(username!=null && !username.isEmpty()) {
					Credential credential = Credential.findByFieldName(Credential.class, "username", username, "");
					if(credential != null) {
						String strPassword = Credential.generatePassword(Integer.parseInt(ApplicationConstants.DEFAULT_PASSWORD_LENGTH));
						String encodedPassword = securityService.getEncodedPassword(strPassword);
						credential.setPassword(encodedPassword);
						credential.setPasswordChangeCount(1);
						credential.setPasswordChangeDateTime(new Date());
						credential.merge();
						//reportData.get(0)[12] = credential.getPassword();
						reportData.get(0)[10] = strPassword;
					}
				}
			}
	
			//generate report
			//generateReportUsingFOP(new Object[]{reportData}, "template_ris_totalwork", "PDF", "Total Work Report", locale.toString());
			returnValue = "member/printCredentials";
					
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		return returnValue;
	}
	
	@RequestMapping(value="/printCredentials/instructions_vm", method=RequestMethod.GET)
	public String getInstructionsForPrintCredentialsVM(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		response.setContentType("text/html; charset=utf-8");
		return "member/printCredentials_instructions";
	}

	@RequestMapping(value="/print",method=RequestMethod.GET)
    public String print(final ModelMap model,final Locale locale){
        return "member/print";
    }
	
	
	@RequestMapping(value="/exportMemberList",method=RequestMethod.POST)
	public @ResponseBody void exportMemberList(Model model, HttpServletRequest request
									, HttpServletResponse response
									,@RequestBody ObjectNode objectNode
									, Locale locale) {
		String houseType = objectNode.get("houseTypeId").getTextValue();
		String fromDate = objectNode.get("fromDate").getTextValue();
		String toDate = objectNode.get("toDate").getTextValue();
		HouseType houseTypeVO = HouseType.findByType(houseType, locale.toString());
		
		
		
		List<String> reportData = Member.findMemberByHouseDates(houseTypeVO.getId(), fromDate, toDate, locale.toString());
		List<String> serialNo1 = this.populateSerialNumbers(reportData, locale);
		File reportFile =null;
		try {
			reportFile = generateReportUsingFOP(new Object[] {reportData,serialNo1,houseTypeVO.getType()},"member_details_template", "WORD", "member_details_report", locale.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
	}
}
