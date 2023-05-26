package org.mkcl.els.mobileApiServices;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.GeneralReportVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.WorkflowDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/mobileApiService/workflow")
public class WorkflowTaskWebService extends BaseController{

	@RequestMapping(value="/{housetype}/{assignee}/{locale}")
	public @ResponseBody MasterVO getAllPendingTaskOfSpecificUser(@PathVariable("housetype") final String housetype,
			@PathVariable("assignee") final String assignee,
			@PathVariable("locale") final String locale,
			HttpServletRequest request, HttpServletResponse response) throws ELSException{
		
		MasterVO data = new MasterVO();
		
		try {
			HouseType houseType = HouseType.findByType(housetype, locale);
			Session session = Session.findLatestSession(houseType);
			
			int pendingTasksCount = 0;
			int pendingTasksInSessionCount = 0;
			int pendingTasksCountForDeviceType = 0;
			String strSessionYear = FormaterUtil.formatNumberNoGrouping(session.getYear(),locale);
			String strSessionType = session.getType().getSessionType().toString();
			//String strHouseType = "";
			
			HouseType lowerHouseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
			HouseType upperHouseType = HouseType.findByType(ApplicationConstants.UPPER_HOUSE, locale);
			
			List<DeviceType> devices = DeviceType.findAll(DeviceType.class, "name", ApplicationConstants.ASC, locale);
			System.out.println(devices);      
			String allowedDeviceTypesForCounts = "##";
			
			
			for(int i=0;i<devices.size();i++) {
				allowedDeviceTypesForCounts += devices.get(i).getName().toString() + "##";
			}
			

			if(devices != null) {
		
			  Map<String, String> parameters = new LinkedHashMap<String,String>();
			  parameters.put("assignee", assignee);
			  parameters.put("status", ApplicationConstants.MYTASK_PENDING);
			  parameters.put("locale", locale);
			  parameters.put("assignmentTimeStartLimit", ApplicationConstants.LATEST_ASSSEMBLY_HOUSE_FORMATION_DATE);
			  CustomParameter csptAllowedDeviceTypesForPastPendingCounts = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.ALLOWED_DEVICETYPES_FOR_VIEWING_PAST_PENDING_TASKS_COUNTS, locale.toString());
			  String allowedDeviceTypesForPastPendingCounts = "";
				if(csptAllowedDeviceTypesForPastPendingCounts!=null && csptAllowedDeviceTypesForPastPendingCounts.getValue()!=null) {
					allowedDeviceTypesForPastPendingCounts = csptAllowedDeviceTypesForPastPendingCounts.getValue();
				}
			  for(String allowedDeviceType : allowedDeviceTypesForCounts.split("##")){
		  
				  if(!allowedDeviceType.isEmpty()) {
					  parameters.put("deviceType", allowedDeviceType);
					  
					  if(allowedDeviceTypesForPastPendingCounts.contains("##"+allowedDeviceType+"##"))
						{
							parameters.put("assignmentTimeStartLimit", ApplicationConstants.LATEST_ASSSEMBLY_HOUSE_FORMATION_DATE);
							
							parameters.remove("sessionYear");
							parameters.remove("sessionType");
							//parameters.remove("houseType");
							
							//below result includes pending counts of previous sessions also subject to assignmentTimeStartLimit parameter
							pendingTasksCountForDeviceType = WorkflowDetails.findPendingWorkflowCountOfCurrentUser(parameters, lowerHouseType.getName(), upperHouseType.getName(), "assignmentTime", ApplicationConstants.ASC);
							
							pendingTasksCount += pendingTasksCountForDeviceType;
							
							parameters.put("sessionYear", strSessionYear);
							parameters.put("sessionType",strSessionType);
							//parameters.put("houseType", strHouseType);		
							
							parameters.remove("assignmentTimeStartLimit");
							
							//below result includes pending counts of only current session as per session parameters
							pendingTasksCountForDeviceType = WorkflowDetails.findPendingWorkflowCountOfCurrentUser(parameters, "assignmentTime", ApplicationConstants.ASC);
							
							pendingTasksInSessionCount += pendingTasksCountForDeviceType;
						}
						else 
						{
							parameters.put("sessionYear", strSessionYear);
							parameters.put("sessionType", strSessionType);
							//parameters.put("houseType", strHouseType);
							
							parameters.remove("assignmentTimeStartLimit");
							
							pendingTasksCountForDeviceType = WorkflowDetails.findPendingWorkflowCountOfCurrentUser(parameters, "assignmentTime", ApplicationConstants.ASC);
							
							pendingTasksInSessionCount += pendingTasksCountForDeviceType;
							
							pendingTasksCount += pendingTasksCountForDeviceType;
						}				
						 
				  }
				  
			  }
			  
            
			}
			data.setId((long) pendingTasksCount);
			data.setName(String.valueOf(pendingTasksCount));
			data.setNumber(pendingTasksInSessionCount);
			data.setValue(String.valueOf(pendingTasksInSessionCount));
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		response.setHeader("Access-Control-Allow-Origin", "*");
		return data;
		
	}
	
	
	
	@RequestMapping(value="/generalReport/{housetype}/{assignee}/{report}/{locale}")
	public @ResponseBody GeneralReportVO genReport(@PathVariable("housetype") final String housetype,
			@PathVariable("assignee") final String assignee,
			@PathVariable("locale") final String locale,
			@PathVariable("report") final String report,
			HttpServletRequest request, HttpServletResponse response)  throws ELSException{
	
		List reports = null;

		GeneralReportVO generalReport = new GeneralReportVO();
		try {
		HouseType houseType = HouseType.findByType(housetype, locale);
		Session session = Session.findLatestSession(houseType);
		String[] assignees = {assignee};
		String[] locales = {locale};
		String[] houseTypeName = {houseType.getName().toString()};
		
		String[] strSessionYear = {FormaterUtil.formatNumberNoGrouping(session.getYear(),locale)};
		String[] strSessionType = {session.getType().getSessionType().toString()};
		
		CustomParameter csptLatestAssemblyHouseFormationDate = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CSPT_LATEST_ASSSEMBLY_HOUSE_FORMATION_DATE, "");
		String[] latestAssemblyHouseFormationDate = {csptLatestAssemblyHouseFormationDate.getValue()+" 00:00:00"};
	    String[] onlineDepartmentReplyBeginningDate = {ApplicationConstants.STARTING_DATE_FOR_FULLY_ONLINE_DEPARTMENT_PROCESSING_OF_DEVICES+" 00:00:00"};
	    
	    
	    Map<String, String[]> parameters = new HashMap<String,String[]>();
	    
	    parameters.put("houseType", houseTypeName);
	    parameters.put("sessionYear", strSessionYear);
	    parameters.put("sessionType", strSessionType);
		parameters.put("assignee", assignees);
		parameters.put("latestAssemblyHouseFormationDate", latestAssemblyHouseFormationDate);
		parameters.put("onlineDepartmentReplyBeginningDate", onlineDepartmentReplyBeginningDate);
		parameters.put("locale", locales);
		
		reports = Query.findReport(report, parameters);
	
		generalReport.setObj(reports);
		
		Object[] obj = (Object[])reports.get(0);
		generalReport.setTopHeader(obj[0].toString().split(";"));
		
		generalReport.setSerialNumbers(populateSerialNumbers(reports,locale));
		}catch(Exception e) {
			e.printStackTrace();
		}
	
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		return generalReport;
		
		
	}
	
}
