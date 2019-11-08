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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MemberCompleteDetailVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
		
			
	
			//generate report
			//generateReportUsingFOP(new Object[]{reportData}, "template_ris_totalwork", "PDF", "Total Work Report", locale.toString());
			returnValue = "member/printCredentials";
					
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		return returnValue;
	}

	@RequestMapping(value="/print",method=RequestMethod.GET)
    public String print(final ModelMap model,final Locale locale){
        return "member/print";
    }
}
