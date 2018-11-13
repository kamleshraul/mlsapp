package org.mkcl.els.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("departmentdashboard")
public class DepartmentDashboardController extends BaseController {
	
	@RequestMapping(value="/getpendingtask",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getPendingTask(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model){
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		List<MasterVO> pendingTaskVOs=new ArrayList<MasterVO>();
		String strStatus=request.getParameter("status");
	
		
		if(customParameter!=null){
			String param=request.getParameter("subdepartment");
			 Map<String, String[]> parameters = new HashMap<String, String[]>();
             parameters.put("subdepartment", new String[]{param});
             parameters.put("status", new String[]{strStatus});
             parameters.put("locale", new String[]{locale.toString()});
             List result = Query.findReport("DEPARTMENT_STATUSWISE_DEVICES_COUNT", parameters);
             for(int i=0;i<result.size();i++){
            	 Object[] row = (Object[])result.get(i);
            	 MasterVO departmentPendingDeviceCount = new MasterVO();
            	 //name of respective subdepartment
            	 departmentPendingDeviceCount.setName(row[0].toString());
            	 //sessionyear
            	 int sessionYear = Integer.parseInt(row[1].toString());
            	 departmentPendingDeviceCount.setType(FormaterUtil.formatNumberNoGrouping(sessionYear, locale.toString()));
            	 //sessiontype
            	 departmentPendingDeviceCount.setDisplayName(row[2].toString());
            	 //Assembly Count
            	 departmentPendingDeviceCount.setOrder(Integer.parseInt(row[3].toString()));
            	 //council count
            	 departmentPendingDeviceCount.setFormattedOrder(row[4].toString());
            	
       
            	 
            	 pendingTaskVOs.add(departmentPendingDeviceCount);
            }
		}
		return pendingTaskVOs;
	}
	
	@RequestMapping(value="/getassemblypendingtask",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getAssemblyPendingTask(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model){
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		List<MasterVO> assemblyPendingTaskVOs=new ArrayList<MasterVO>();
		String strStatus=request.getParameter("status");
		String sessionType=request.getParameter("session_type");
		String sessionYear=request.getParameter("session_year");
		
		if(customParameter!=null){
			String param=request.getParameter("subdepartment");
			 Map<String, String[]> parameters = new HashMap<String, String[]>();
             parameters.put("subdepartment", new String[]{param});
             parameters.put("status", new String[]{strStatus});
             parameters.put("session_type", new String[]{sessionType});
             parameters.put("session_year", new String[]{sessionYear});
             parameters.put("locale", new String[]{locale.toString()});
             List result = Query.findReport("DEPARTMENT_PENDING_ASSEMBLY_DEVICE_COUNT", parameters);
             for(int i=0;i<result.size();i++){
            	 Object[] row = (Object[])result.get(i);
            	 MasterVO departmentPendingAssemblyDeviceCount = new MasterVO();
            	 //device_number
            	 departmentPendingAssemblyDeviceCount.setName(row[0].toString());
            	 //device_type
            	 departmentPendingAssemblyDeviceCount.setType(row[1].toString());
            	 //assignee
            	 departmentPendingAssemblyDeviceCount.setDisplayName(row[2].toString());
            	 //assignment_time
            	 departmentPendingAssemblyDeviceCount.setSessionDate(FormaterUtil.formatStringToDate(row[3].toString(), ApplicationConstants.DB_DATEFORMAT));
            	 //subject
            	 departmentPendingAssemblyDeviceCount.setFormattedOrder(row[4].toString());
       
            	 
            	 assemblyPendingTaskVOs.add(departmentPendingAssemblyDeviceCount);
            }
		}
		return assemblyPendingTaskVOs;
	}
	
	
	@RequestMapping(value="/getDepartmentDeviceCount",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getDepartmentDeviceCountWithFilters(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model){
		String sessionType=request.getParameter("session_type");
		String sessionYear=request.getParameter("session_year");
		String houseType = request.getParameter("house_type");
		String subdepartment =request.getParameter("subdepartment");
		String deviceType = request.getParameter("device_type");
		List<MasterVO> departmentDeviceCounts = new ArrayList<MasterVO>();
   	 	Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put("locale", new String[]{locale.toString()});
        parameters.put("sessionType", new String[]{sessionType.toString()});
        parameters.put("sessionYear", new String[]{sessionYear.toString()});
        parameters.put("houseType", new String[]{houseType.toString()});
        parameters.put("deviceType", new String[]{deviceType.toString()});
        parameters.put("subdepartment", new String[]{subdepartment.toString()});
        List result = Query.findReport("DEPARTMENT_DEVICES_COUNT", parameters);
        for(int i=0;i<result.size();i++){
       	 Object[] row = (Object[])result.get(i);
       	 MasterVO departmentDeviceCount = new MasterVO();
       	 departmentDeviceCount.setName(row[0].toString());
       	 //Pending Count
       	 departmentDeviceCount.setNumber(Integer.parseInt(row[1].toString()));
       	 //Completed Count
       	 departmentDeviceCount.setOrder(Integer.parseInt(row[2].toString()));
       	 //Timeout Count
       	 departmentDeviceCount.setFormattedNumber(row[3].toString());
       	 //Total Count
       	 departmentDeviceCount.setFormattedOrder(row[4].toString());
       	 
       	 departmentDeviceCounts.add(departmentDeviceCount);
       }
		return departmentDeviceCounts;
	}
}
