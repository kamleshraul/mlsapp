package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.chart.Chart;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.domain.Status;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/chart")
public class ChartController extends BaseController{

	@RequestMapping(value="/init", method=RequestMethod.GET)
	public String getChartPage(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		try{
			String strGroup = request.getParameter("group");
			String strDeviceTypeId = request.getParameter("questionType");
			/**** Added By Sandeep Singh ****/
			String strUserGroup = request.getParameter("usergroup");
			String strUserGroupType = request.getParameter("usergroupType");
			String strHouseType = request.getParameter("houseType");
			
    		if(strGroup != null && (!strGroup.isEmpty()) 
					&& strUserGroup != null && (!strUserGroup.isEmpty())
					&& strUserGroupType != null && (!strUserGroupType.isEmpty()) 
					&& strDeviceTypeId != null && (!strDeviceTypeId.isEmpty())
					&& strHouseType != null && (!strHouseType.isEmpty())) {
					
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceTypeId));
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					
				Group group = null;
				if(deviceType != null){
					if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
							&& houseType.getType().equals(ApplicationConstants.LOWER_HOUSE))){
						
						group = Group.findById(Group.class, Long.parseLong(strGroup));
						
						List<MasterVO> masterVOs = new ArrayList<MasterVO>();
						List<QuestionDates> questionDates = group.getQuestionDates();
						for(QuestionDates i:questionDates) {
							MasterVO masterVO = new MasterVO(i.getId(), 
									FormaterUtil.getDateFormatter(
											locale.toString()).format(i.findAnsweringDateForReport()));
							masterVO.setValue(i.getAnsweringDate().toString());
							masterVOs.add(masterVO);
						}
						String maxChartAnsweringDate = FormaterUtil.formatDateToString( questionDates.get(questionDates.size()-1).getAnsweringDate(), ApplicationConstants.DB_DATEFORMAT);
						model.addAttribute("maxChartAnsweringDate", maxChartAnsweringDate);
						model.addAttribute("answeringDates", masterVOs);
					}else{
							model.addAttribute("answeringDates", null);
					}
				}
				model.addAttribute("usergroup", strUserGroup);
				model.addAttribute("usergroupType", strUserGroupType);
				model.addAttribute("deviceType", (deviceType != null)?deviceType.getType():"");			
			}
		}catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
		return "chart/chartinit";
	}

	/**
	 * Return "CREATED" if Chart is created
	 * OR
	 * Return "ALREADY_EXISTS" if Chart already exists
	 * OR
	 * Return "PREVIOUS_CHART_IS_NOT_PROCESSED" if previous Chart is not processed
	 */
	@Transactional
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public @ResponseBody String createChart(final HttpServletRequest request,
			final Locale locale) {
		String retVal = "ERROR";
		try {
			/** Create HouseType */
			String strHouseType = request.getParameter("houseType");
			HouseType houseType =
				HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			
			/** Create SessionType */
			String strSessionTypeId = request.getParameter("sessionType");
			SessionType sessionType =
				SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
			
			/** Create year */
			String strYear = request.getParameter("sessionYear");
			Integer year = Integer.valueOf(strYear);
			
			/** Create Session */
			Session session = 
				Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			
			String strDeviceType=request.getParameter("deviceType");
			DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			/** Create Group*/
			Group group=null;
			QuestionDates questionDates=null;
			Date answeringDate=null;
			if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){				
				/** Create answeringDate */
				String strGroup = request.getParameter("group");
				group = Group.findById(Group.class, Long.parseLong(strGroup));
				
				String strAnsweringDate = request.getParameter("answeringDate");
				questionDates=QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
				answeringDate = questionDates.getAnsweringDate();
			}			
			
			/** Create Chart */
			if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
				Chart foundChart = Chart.find(new Chart(session, group, answeringDate, deviceType, locale.toString()));
				if(foundChart == null) {
					Chart chart = null;
					if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
						chart = new Chart(session, deviceType, locale.toString());
					}else{
						chart = new Chart(session, group, answeringDate, deviceType, locale.toString());
					}
					Chart createdChart = chart.create();
					if(createdChart == null) {
						retVal = "PREVIOUS_CHART_IS_NOT_PROCESSED";
					}
					else {
						retVal = "CREATED";
					}
				}
				else {
					retVal = "ALREADY_EXISTS";
				}
			}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
				Chart foundChart = Chart.find(new Chart(session, deviceType, locale.toString()));
				if(foundChart == null) {
					Chart chart = new Chart(session, deviceType, locale.toString());
					Chart createdChart = chart.create();
					if(createdChart == null) {
						retVal = "PREVIOUS_CHART_IS_NOT_PROCESSED";
					}
					else {
						retVal = "CREATED";
					}
				}
				else {
					retVal = "ALREADY_EXISTS";
				}
			}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_STANDALONE)){
				
				Chart foundChart = Chart.find(new Chart(session, deviceType, locale.toString()));
				
				if(foundChart == null) {
					
					Chart chart = new Chart(session, deviceType, locale.toString());
					Chart createdChart = chart.create();
				
					if(createdChart == null) {
						retVal = "PREVIOUS_CHART_IS_NOT_PROCESSED";
					}
					else {
						retVal = "CREATED";
					}
				}else {
					retVal = "ALREADY_EXISTS";
				}
			}
		}catch(Exception e) {
			logger.error("error", e);
			retVal = "ERROR";
		}
		return retVal;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String viewChart(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String retVal = "chart/error";
		try {
			/** Create HouseType */
			String strHouseType = request.getParameter("houseType");
			HouseType houseType =
				HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			
			/** Create SessionType */
			String strSessionTypeId = request.getParameter("sessionType");
			SessionType sessionType =
				SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
			
			/** Create year */
			String strYear = request.getParameter("sessionYear");
			Integer year = Integer.valueOf(strYear);
			
			/** Create Session */
			Session session = 
				Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			/**Create DeviceType**/
			String strDeviceType=request.getParameter("deviceType");
			DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			model.addAttribute("deviceType", deviceType.getType());
			/** Create Group*/
			Group group=null;
			QuestionDates questionDates=null;
			Date answeringDate=null;
			if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){			
					
				String strGroup = request.getParameter("group");
				group = Group.findById(Group.class, Long.parseLong(strGroup));
				model.addAttribute("deviceType", deviceType.getType());
									
				/** Create answeringDate */
				String strAnsweringDate = request.getParameter("answeringDate");
				questionDates=QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
				answeringDate = questionDates.getAnsweringDate();
					
				/** Add localized answeringDate to model */
				CustomParameter parameter =
					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				String localizedAnsweringDate = FormaterUtil.formatDateToString(answeringDate, 
						parameter.getValue(), locale.toString());
				model.addAttribute("answeringDate", localizedAnsweringDate);
			}
			
			
			List<ChartVO> chartVOs=null;			
			/** View Chart */
			if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				//chartVOs = Chart.getChartVOs(session, deviceType, locale.toString());
				//RESOLUTION_CHART_VIEW
				
				Chart chart = Chart.find(new Chart(session, deviceType, locale.toString()));
				List resolutionNonOfficialChartView = null;
				Map<String, String[]> parametersMap = new HashMap<String, String[]>();
				
				if(chart != null){
					Status rejectionStatus = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REJECTION, locale.toString());
					Status repeatRejectionStatus = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REPEATREJECTION, locale.toString());
					MemberRole memberRole=MemberRole.find(session.getHouse().getType(), ApplicationConstants.MEMBER, locale.toString());
					
					parametersMap.put("locale", new String[]{locale.toString()});
					parametersMap.put("sessionId", new String[]{session.getId().toString()});
					parametersMap.put("houseId", new String[]{session.getHouse().getId().toString()});
					parametersMap.put("roleId", new String[]{memberRole.getId().toString()});
					parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
					parametersMap.put("rejectionStatusId", new String[]{rejectionStatus.getId().toString()});
					parametersMap.put("repeatRejectionStatusId", new String[]{repeatRejectionStatus.getId().toString()});
					parametersMap.put("currentDate", new String[]{FormaterUtil.formatDateToString(new Date(), ApplicationConstants.DB_DATEFORMAT)});
									
					resolutionNonOfficialChartView = org.mkcl.els.domain.Query.findReport(ApplicationConstants.RESOLUTION_CHART_VIEW, parametersMap);						
					
				/*	parametersMap.remove("sessionId");
					parametersMap.remove("deviceTypeId");
					parametersMap.remove("rejectionStatusId");
					parametersMap.remove("repeatRejectionStatusId");
					
					List resolutionChartNonDevice = Query.findReport(ApplicationConstants.RESOLUTION_CHART_WITHOUTDEVICES_VIEW, parametersMap);
					
					resolutionNonOfficialChartView.addAll(resolutionChartNonDevice);
							*/
					for(int i = 0; i < resolutionNonOfficialChartView.size(); i++ ){
						Object[] obj = ((Object[])resolutionNonOfficialChartView.get(i));
						Member member = Member.findById(Member.class, Long.valueOf(obj[0].toString()));
						List<Resolution> rejectedResolutions = Resolution.getRejectedResolution(member, deviceType, session, locale.toString());
								
						((Object[])resolutionNonOfficialChartView.get(i))[12] = Resolution.getRejectedResolutionsAsString(rejectedResolutions); 
						((Object[])resolutionNonOfficialChartView.get(i))[13] = rejectedResolutions.size();
						((Object[])resolutionNonOfficialChartView.get(i))[14] = Resolution.getResolutionWithoutNumber(member,deviceType,session,locale.toString());
						
						obj = null;
						rejectedResolutions = null;
					}
					
					List newList = getSimplifiedChart(resolutionNonOfficialChartView);
					List extraMembers = org.mkcl.els.domain.Query.findReport("RESOLUTION_CHART_VIEW_2", parametersMap);
					newList.addAll(extraMembers);
					model.addAttribute("report", newList);
				}
				
			}else if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)){
				//chartVOs = Chart.getChartVOs(session, group, answeringDate, deviceType, locale.toString());				
				List starredChartView = null;
				Chart chart = Chart.find(new Chart(session, group, answeringDate, deviceType, locale.toString()));
				List extraMembers = null;
				if(chart != null){
					
					MemberRole memberRole=MemberRole.find(session.getHouse().getType(), ApplicationConstants.MEMBER, locale.toString());
					
					Map<String, String[]> parametersMap = new HashMap<String, String[]>();
					parametersMap.put("locale", new String[]{locale.toString()});
					parametersMap.put("sessionId", new String[]{session.getId().toString()});
					parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
					parametersMap.put("groupId", new String[]{group.getId().toString()});
					parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});
										
					// List questionParents = org.mkcl.els.domain.Query.findReport("CHART_QUESTION_ONLY_PARENTS", parametersMap);
					
					starredChartView = org.mkcl.els.domain.Query.findReport("STARRED_CHART_VIEW", parametersMap);					
										
					List starredChartViewCounts = org.mkcl.els.domain.Query.findReport("STARRED_CHART_VIEW_COUNTS", parametersMap);
					if(starredChartViewCounts!=null && !starredChartViewCounts.isEmpty()) {
						if(starredChartViewCounts.get(0)!=null) {
							Object[] counts = (Object[]) starredChartViewCounts.get(0);
							model.addAttribute("processedCount", counts[0]);
							model.addAttribute("clubbedCount", counts[1]);
							model.addAttribute("putupCount", counts[2]);
							model.addAttribute("admitCount", counts[3]);
							model.addAttribute("rejectCount", counts[4]);
							model.addAttribute("unstarredCount", counts[5]);							
						}						
					}
					
					parametersMap.put("houseId", new String[]{session.getHouse().getId().toString()});
					parametersMap.put("roleId", new String[]{memberRole.getId().toString()});					
					parametersMap.put("currentDate", new String[]{FormaterUtil.formatDateToString(new Date(), ApplicationConstants.DB_DATEFORMAT)});
					extraMembers = org.mkcl.els.domain.Query.findReport("STARRED_CHART_VIEW_2", parametersMap);
					
					List simplifiedList = (starredChartView!=null)?getSimplifiedChart(starredChartView):null;
					if(simplifiedList!=null) {
						simplifiedList.addAll(extraMembers);
					} else {
						simplifiedList = new ArrayList<Object[]>();
					}
					model.addAttribute("report", simplifiedList);
					
					//Code for Displaying Departmentwise question count on chart
					List starredChartDepartmentWiseCounts = org.mkcl.els.domain.Query.findReport("STARRED_CHART_DEPARTMENTWISE_COUNT", parametersMap);
					List<MasterVO> departmentCountVOs = new ArrayList<MasterVO>();
					if(starredChartDepartmentWiseCounts!=null && !starredChartDepartmentWiseCounts.isEmpty()) {
						for(int i = 0; i < starredChartDepartmentWiseCounts.size(); i++ ){
							Object[] obj = ((Object[])starredChartDepartmentWiseCounts.get(i));
							MasterVO masterVO = new MasterVO();
							
							masterVO.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(Integer.parseInt(obj[0].toString()), locale.toString()));
							masterVO.setName(obj[1].toString());
							departmentCountVOs.add(masterVO);
						}				
					}
					model.addAttribute("departmentwiseCounts", departmentCountVOs);
				}
			
				
			}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				//chartVOs = Chart.getChartVOs(session, null, answeringDate, deviceType, locale.toString());
				Chart chart = Chart.find(new Chart(session, deviceType, locale.toString()));
				List hdsChartView = null;
				if(chart != null){
					Status rejectionStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_REJECTION, locale.toString());
					Status repeatRejectionStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_REPEATREJECTION, locale.toString());
					MemberRole memberRole=MemberRole.find(session.getHouse().getType(), ApplicationConstants.MEMBER, locale.toString());
					
					Map<String, String[]> parametersMap = new HashMap<String, String[]>();
					parametersMap.put("locale", new String[]{locale.toString()});
					parametersMap.put("sessionId", new String[]{session.getId().toString()});
					parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
					parametersMap.put("rejectionStatusId", new String[]{rejectionStatus.getId().toString()});
					parametersMap.put("repeatRejectionStatusId", new String[]{repeatRejectionStatus.getId().toString()});
					parametersMap.put("houseId", new String[]{session.getHouse().getId().toString()});
					parametersMap.put("roleId", new String[]{memberRole.getId().toString()});
					parametersMap.put("currentDate", new String[]{FormaterUtil.formatDateToString(new Date(), ApplicationConstants.DB_DATEFORMAT)});
					
					hdsChartView = org.mkcl.els.domain.Query.findReport("HDS_CHART_VIEW", parametersMap);						
					
					for(int i = 0; i < hdsChartView.size(); i++ ){
						Object[] obj = ((Object[])hdsChartView.get(i));
						Member member = Member.findById(Member.class, Long.valueOf(obj[0].toString()));
						List<StandaloneMotion> rejectedMotions = StandaloneMotion.findRejectedStandaloneMotions(member, session, deviceType, locale.toString());
								
						((Object[])hdsChartView.get(i))[12] = StandaloneMotion.findRejectedStandaloneMotionsAsString(rejectedMotions, locale.toString()); 
						((Object[])hdsChartView.get(i))[13] = rejectedMotions.size();
						((Object[])hdsChartView.get(i))[14] = StandaloneMotion.findStandaloneMotionWithoutNumber(member, deviceType, session, locale.toString());
						
						obj = null;
						rejectedMotions = null;
					}
					List newList = getSimplifiedChart(hdsChartView);
					List extraMembers = org.mkcl.els.domain.Query.findReport("HDS_CHART_VIEW_2", parametersMap);
					newList.addAll(extraMembers);
					model.addAttribute("report", newList);
				}				
			}
			
			model.addAttribute("chartVOs", chartVOs);
			
			/** Set max Questions on Chart against any member*/
			if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
				Chart chart = new Chart(session, group, answeringDate, deviceType, locale.toString());
				Integer maxQuestionsOnChart = Chart.maxChartedDevices(chart);
				model.addAttribute("maxQns", maxQuestionsOnChart);
			}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
				Chart chart = new Chart(session, deviceType, locale.toString());
				Integer maxResolutionsOnChart = Chart.maxChartedDevices(chart);
				model.addAttribute("maxQns", maxResolutionsOnChart);
			}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_STANDALONE)){
				Chart chart = new Chart(session, deviceType, locale.toString());
				Integer maxResolutionsOnChart = Chart.maxChartedDevices(chart);
				model.addAttribute("maxQns", maxResolutionsOnChart);
			}
			/*if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION) || deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION) || deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				retVal = "chart/starredchart";
			}else{*/
				retVal = "chart/chart";
			//}
			model.addAttribute("error", "");
		}
		catch(Exception e) {
			logger.error("error", e);
			e.printStackTrace();
			model.addAttribute("type", "INSUFFICIENT_PARAMETERS_FOR_VIEWING_CHART");
			retVal = "chart/error";
		}
		return retVal;
	}
	
	
	/**
	 * TO sort the chart in ascending order of the device number
	 * @param chartList
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private List getSimplifiedChart(List chartList){
		
		Map<String, List<Object[]>> chartMap = new HashMap<String, List<Object[]>>();
		Object[] objArr = null;
		if(chartList!=null){
		for(Object o : chartList){
			objArr = (Object[]) o;
			List<Object[]> lo;
			
			lo = chartMap.get(objArr[0].toString());
			if(lo == null){
				lo = new ArrayList<Object[]>();
			}
			
			lo.add(objArr);			
			
			chartMap.put(objArr[0].toString(), lo);
		}
		
		}
		List<Object[]> newList = new ArrayList<Object[]>();
		List<Integer> tempList = new ArrayList<Integer>();
		
		
		for(Map.Entry<String,List<Object[]>> o: chartMap.entrySet()){
			int currSmall = Integer.valueOf(o.getValue().get(0)[4].toString()).intValue();
			tempList.add(currSmall);
		}
		Collections.sort(tempList);
		
		List<List<Object[]>> finalList = new ArrayList<List<Object[]>>();
		for(Integer i : tempList){
			for(Map.Entry<String,List<Object[]>> o: chartMap.entrySet()){
				Integer matched = Integer.valueOf(o.getValue().get(0)[4].toString());
				if(i.equals(matched)){
					finalList.add(o.getValue());
				}
			}
		}
		
		for(List<Object[]> obj : finalList){
			for(Object[] ob : obj){
				newList.add(ob);
			}
		}			
		
		return newList;
	}
}
