/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.webservices.MemberSearchWebService.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.webservices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.vo.MemberIdentityVO;
import org.mkcl.els.common.vo.MemberInfo;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * The Class MemberSearchWebService.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/ws/membersearch")
public class MemberSearchWebService {

    /**
     * Search.
     *
     * @param housetype the housetype
     * @param criteria1 the criteria1
     * @param criteria2 the criteria2
     * @param locale the locale
     * @return the member search page
     */
    @RequestMapping(value = "/{housetype}/{house}/{criteria1}/{criteria2}/{locale}")
    public @ResponseBody List<MemberInfo> search(@PathVariable final String housetype ,
            @PathVariable final Long house,
            @PathVariable final String criteria1 ,
            @PathVariable final Long criteria2 ,
            @PathVariable final String locale,
            @RequestParam final String parameterUH,
            @RequestParam final String date1,
            @RequestParam final String date2){
    	String[] councilCriteria={parameterUH,date1,date2};
        return Member.search(housetype ,house , criteria1 , criteria2
                , locale,councilCriteria);
    }
    


    @RequestMapping(value = "/list/{housetype}/{house}/{criteria1}/{criteria2}/{locale}")
    public @ResponseBody List<MemberIdentityVO> searchForAccounting(HttpServletRequest request,
    		@PathVariable final String housetype ,
            @PathVariable final Long house,
            @PathVariable final String criteria1 ,
            @PathVariable final Long criteria2 ,
            @PathVariable final String locale){
    	String durationUH = request.getParameter("durationUH");
    	String date = request.getParameter("date");
    	String fromDate = request.getParameter("fromDate");
    	String toDate = request.getParameter("toDate");
    	String year = request.getParameter("year");
    	String parameterUH = "";
    	String date1 = "";
    	String date2 = "";
    	if(durationUH!=null && !durationUH.isEmpty()){
    		if(durationUH.equals("CURRENTDATE")){
    			SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
    			date=format.format(new Date());
    			parameterUH="DATE";
    			date1=date;
    			date2=date;		
			}else if(durationUH.equals("DATE")){
				parameterUH="DATE";
				date1=date;
				date2=date;		
			}else if(durationUH.equals("YEAR")){
				parameterUH="YEAR";
				date1="01/01/"+year;
				date2="31/12/"+year;				
			}else if(durationUH.equals("RANGE")){
				parameterUH="RANGE";
				date1=fromDate;
				date2=toDate;
			} 
		} else {
			SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
			date=format.format(new Date());
			parameterUH="DATE";
			date1=date;
			date2=date;
		}
    	String[] councilCriteria={parameterUH,date1,date2};
        return Member.searchForAccounting(housetype ,house , criteria1 , criteria2
                , locale,councilCriteria);
    }
}
