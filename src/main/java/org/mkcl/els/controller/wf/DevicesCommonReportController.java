package org.mkcl.els.controller.wf;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("devicescommonreport/report")
public class DevicesCommonReportController extends BaseController {

	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/generalreport", method=RequestMethod.GET)
	public String getReport(HttpServletRequest request, Model model, Locale locale){
        AuthUser authUser = this.getCurrentUser();
        Member member=null;
        member=Member.findMember(authUser.getFirstName(),authUser.getMiddleName(),authUser.getLastName(),authUser.getBirthDate(),locale.toString());
		Map<String, String[]> requestMap = request.getParameterMap();
		Map<String, String[]> requestMap2  = new HashMap<String, String[]>(requestMap);
        requestMap2.put("memberId",new String[]{member.getId().toString()});
		List report = Query.findReport(request.getParameter("report"), requestMap2);
		if(report != null && !report.isEmpty()){
			Object[] obj = (Object[])report.get(0);
			if(obj != null){
				
				model.addAttribute("topHeader", obj[0].toString().split(";"));
			}
			List<String> serialNumbers = populateSerialNumbers(report, locale);
			model.addAttribute("serialNumbers", serialNumbers);
		}
			model.addAttribute("formater", new FormaterUtil());
			model.addAttribute("locale", locale.toString());
			model.addAttribute("report", report);
		
		return "workflow/myTasks/reports/"+request.getParameter("reportout");		
	}


}