/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.webservices.MemberReportWebService.java
 * Created On: Apr 21, 2012
 */
package org.mkcl.els.webservices;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.vo.MemberChildrenWiseReportVO;
import org.mkcl.els.common.vo.MemberGeneralVO;
import org.mkcl.els.common.vo.MemberPartyDistrictWiseVO;
import org.mkcl.els.common.vo.MemberPartyWiseReportVO;
import org.mkcl.els.common.vo.MemberProfessionWiseReportVO;
import org.mkcl.els.common.vo.MemberQualificationWiseReportVO;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class MemberReportWebService.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("ws/memberreport")
public class MemberReportWebService {   

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/{report}")
	public @ResponseBody List findReport(@PathVariable("report") final String report,
			final HttpServletRequest request){
		Map<String,String[]> requestMap=request.getParameterMap();
		if(report.equals("MIS_REPORT_PARTYDISTRICT")){
			return Query.findMISPartyDistrictReport(report,requestMap);
		}else{
			return Query.findReport(report,requestMap);
		}
	}	
}
