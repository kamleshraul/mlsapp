package org.mkcl.els.domain.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;

class NonOfficialResolutionChart {

	//=================================================
	//
	//=============== VIEW METHODS ====================
	//
	//=================================================
	public static List<ChartVO> getChartVOs(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		List<ChartVO> chartVOs = new ArrayList<ChartVO>();
		
		Chart chart = NonOfficialResolutionChart.find(session, 
				deviceType, locale);
		if(chart != null) {
			List<ChartVO> chartVOsWithDevices = new ArrayList<ChartVO>();
			List<ChartVO> chartVOsWithoutdevices = new ArrayList<ChartVO>();
			
			List<ChartEntry> chartEntries = chart.getChartEntries();
			for(ChartEntry ce : chartEntries) {
				Member member = ce.getMember();
				Long memberId = member.getId();
				String memberName = member.getFullnameLastNameFirst();
				List<DeviceVO> deviceVOs = 
					NonOfficialResolutionChart.getDeviceVOs(ce.getDevices());
				
				if(deviceVOs.isEmpty()) {
					ChartVO chartVO = new ChartVO(memberId, memberName);
					chartVOsWithoutdevices.add(chartVO);
				}
				else {
					ChartVO chartVO = 
						new ChartVO(memberId, memberName, deviceVOs);
					
					int extraCount = Resolution.getResolutionWithoutNumber(
							member, deviceType, session, locale);
					chartVO.setExtraCount(extraCount);
					
					String rejectedNotices = "";
					List<Resolution> rejectedResolutions = 
						Resolution.getRejectedResolution(member, 
								deviceType, session, locale);
					for(Resolution r : rejectedResolutions){
						if(rejectedResolutions.get(0).equals(r)) {
							rejectedNotices = r.getNumber().toString();
						}
						else {
							rejectedNotices = rejectedNotices + "," + 
								r.getNumber().toString();
						}
					}
					chartVO.setRejectedNotices(rejectedNotices);
					chartVO.setRejectedCount(rejectedResolutions.size());
					
					chartVOsWithDevices.add(chartVO);
				}
			}
			
			chartVOsWithDevices = ChartVO.sort(chartVOsWithDevices, 
					ApplicationConstants.ASC, deviceType.getType());
			chartVOsWithoutdevices = ChartVO.sort(chartVOsWithoutdevices, 
					ApplicationConstants.ASC, deviceType.getType());
			
			chartVOs.addAll(chartVOsWithDevices);
			chartVOs.addAll(chartVOsWithoutdevices);
		}
		else {
			chartVOs = null;
		}
		
		return chartVOs;
	}

	public static List<ChartVO> getAdmittedChartVOs(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		List<ChartVO> chartVOs = new ArrayList<ChartVO>();		
		
		Chart chart = 
			NonOfficialResolutionChart.find(session, deviceType, locale);
		if(chart != null) {
			List<ChartEntry> chartEntries = chart.getChartEntries();
			for(ChartEntry ce : chartEntries) {
				Member member = ce.getMember();
				Long memberId = member.getId();
				String memberName = member.getFullname();
				List<DeviceVO> deviceVOs = 
					NonOfficialResolutionChart.getAdmittedDeviceVOs(
							ce.getDevices());
				if(deviceVOs != null && !deviceVOs.isEmpty()){
					ChartVO chartVO = new ChartVO(memberId, memberName, deviceVOs);
					chartVOs.add(chartVO);
				}
			}			
			chartVOs = ChartVO.sort(chartVOs, 
					ApplicationConstants.ASC, deviceType.getType());
		}
		else {
			chartVOs = null;
		}
		
		return chartVOs;
	}
	
	public static Integer maxChartedResolutions(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		HouseType houseType = session.getHouse().getType();
		Integer maxResolutions = 
			NonOfficialResolutionChart.maxChartedResolutions(
					houseType);
		
		Integer maxChartedResolutions = 
			NonOfficialResolutionChart.findMaxChartedResolutions(session, locale);
		if(maxChartedResolutions > maxResolutions) {
			maxResolutions = maxChartedResolutions;;
		}

		return maxResolutions;
	}
	
	private static List<DeviceVO> getDeviceVOs(final List<Device> devices) {
		List<DeviceVO> deviceVOs = new ArrayList<DeviceVO>();
		
		for(Device d : devices) {
			Resolution r = (Resolution) d;
			
			Long id = r.getId();
			Integer number = r.getNumber();
			
			// Factual position
			Boolean isFactualRecieved = false;
			String factualPosition = r.getFactualPosition();
			if(factualPosition != null && ! factualPosition.isEmpty()) {
				isFactualRecieved = true;
			}
			
			Status internalStatus = r.getInternalStatusLowerHouse();
			String internalStatusType = internalStatus.getType();
			String internalStatusName = internalStatus.getName();
			
			HouseType houseType = r.getHouseType();
			String houseTypeType = houseType.getType();			
			if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
				internalStatus = r.getInternalStatusUpperHouse();
				internalStatusType = internalStatus.getType();
				internalStatusName = internalStatus.getName();
			}
			
			if((! internalStatusType.equals(
					ApplicationConstants.RESOLUTION_FINAL_REJECTION)) 
				&& 
				(! internalStatusType.equals(
						ApplicationConstants.RESOLUTION_FINAL_REPEATREJECTION))) {
				DeviceVO deviceVO = new DeviceVO(id, number, 
						internalStatusType, internalStatusName, 
						isFactualRecieved);
				deviceVOs.add(deviceVO);
			}
		}
		
		return deviceVOs;
	}
	
	private static List<DeviceVO> getAdmittedDeviceVOs(
			final List<Device> devices) {
		List<DeviceVO> deviceVOs = new ArrayList<DeviceVO>();
		
		for(Device d : devices) {
			Resolution r = (Resolution) d;
			
			Status internalStatus = null;
			HouseType houseType = r.getHouseType();
			String houseTypeType = houseType.getType();
			if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
				internalStatus = r.getInternalStatusLowerHouse();						
			}
			else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)){
				internalStatus = r.getInternalStatusUpperHouse();						
			}
			
			String internalStatusType = internalStatus.getType();
			if(internalStatusType.equals(
					ApplicationConstants.RESOLUTION_FINAL_ADMISSION)
					|| internalStatusType.equals(ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION)) {	
				Integer number = r.getNumber();
				String formatNumber = r.formatNumber();
				String revisedNoticeContent = r.getRevisedNoticeContent();
				
				DeviceVO deviceVO = new DeviceVO(formatNumber, 
							revisedNoticeContent);
				deviceVO.setNumber(number);
				deviceVOs.add(deviceVO);						
			}
		}
		
		return deviceVOs;
	}
	
	private static Integer findMaxChartedResolutions(final Session session, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().findMaxChartedResolution(session, 
				locale);
	}
 
	
	//=================================================
	//
	//=============== DOMAIN METHODS ==================
	//
	//=================================================
	public static Chart create(final Chart chart) throws ELSException {
		HouseType houseType = chart.getSession().getHouse().getType();
		
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			return NonOfficialResolutionChart.createLH(chart);
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			return NonOfficialResolutionChart.createUH(chart);
		}
		
		return chart;
	}

	public static Boolean isProcessed(final Chart chart) throws ELSException {
		Chart newChart = 
			NonOfficialResolutionChart.find(chart.getSession(), 
					chart.getDeviceType(), 
					chart.getLocale());
		
		if(newChart != null) {
			String excludeInternalStatus = 
				ApplicationConstants.RESOLUTION_SYSTEM_TO_BE_PUTUP;
			return Chart.getChartRepository().isProcessed(chart, 
					excludeInternalStatus);
		}
		
		return true;
	}

	public static Boolean addToChart(
			final Resolution resolution) throws ELSException {
		Session session = resolution.getSession();
		HouseType houseType = session.getHouse().getType();
		
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			return NonOfficialResolutionChart.addToChartLH(resolution);
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			return NonOfficialResolutionChart.addToChartUH(resolution);
		}
		
		return false;
	}
	
	// TODO: Implement
	public static boolean forcefullyAddToChart(final Chart chart,
			final Resolution resolution) throws ELSException {
		return false;
	}
	
	// TODO: Test
	public static void removeFromChart(final Resolution resolution) throws ELSException {
		Chart chart = Chart.find(resolution);
		if(chart != null) {
			// Remove resolution from the Chart
			Member member = resolution.getMember();
			ChartEntry chartEntry = Chart.find(chart, member);
			
			List<Device> devices = chartEntry.getDevices();
			List<Device> newDevices = new ArrayList<Device>();
			
			for(Device d : devices) {
				Question q = (Question) d;
				if(! q.getId().equals(resolution.getId())) {
					newDevices.add(q);
				}
			}
			
			chartEntry.setDevices(newDevices);
			chartEntry.persist();
		}	
	}	
	
	// FIND CHART
	public static Chart find(final Session session, 
			final DeviceType deviceType,
			final String locale) throws ELSException {
		return Chart.getChartRepository().find(session, deviceType, locale);
	}
	
	// FIND RESOLUTIONS
	public static List<Resolution> findResolutions(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().findResolutions(session, 
				deviceType, locale);
	}
	
	public static List<Resolution> findResolutions(final Member member,
			final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().findResolutions(member, session, 
				deviceType, locale);
	}
	
	// FIND MEMBERS
	public static List<Member> findMembers(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().findMembers(session, 
				deviceType, locale);
	}
	
	
	//=================================================
	//
	//=============== COUNCIL METHODS =================
	//
	//=================================================
	private static Chart createUH(final Chart chart) throws ELSException {
		Chart newChart = Chart.find(chart);
		
		if(newChart == null) {
			newChart = NonOfficialResolutionChart.persistChartUH(chart);
			NonOfficialResolutionChart.updateChart(newChart);
		}
		return newChart;
	}
	
	private static Boolean addToChartUH(
			final Resolution resolution) throws ELSException {
		if(NonOfficialResolutionChart.isAssistantProcessed(resolution)) {
			Chart chart = NonOfficialResolutionChart.find(
					resolution.getSession(), 
					resolution.getType(), 
					resolution.getLocale());
			if(chart != null) {
				if(NonOfficialResolutionChart.isEligibleForChart(chart, 
						resolution)) {
					House house = resolution.getSession().getHouse();
					HouseType houseType = house.getType();
					Integer maxNoOfResolutions = 
						NonOfficialResolutionChart.maxChartedResolutions(
								houseType);
					return NonOfficialResolutionChart.addToChartIfApplicable(
							chart, resolution, maxNoOfResolutions);
				}
			}
		}
		
		return false;
	}
	
	private static Chart persistChartUH(Chart chart) {
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		Date startTime = FormaterUtil.formatStringToDate(chart.getSession().getParameter(
				ApplicationConstants.RESOLUTION_NONOFFICIAL_SUBMISSION_STARTDATE), 
				datePattern.getValue(), chart.getLocale());
		Date endTime = FormaterUtil.formatStringToDate(chart.getSession().getParameter(
				ApplicationConstants.RESOLUTION_NONOFFICIAL_SUBMISSION_ENDDATE), 
				datePattern.getValue(), chart.getLocale());

		Status ASSISTANT_PROCESSED = Status.findByType(
				ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED, chart.getLocale());
		Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };

		List<ChartEntry> entriesForMembersWithResolution =
			NonOfficialResolutionChart.chartEntriesForMembersWithResolutionUH(chart.getSession(), 
					chart.getDeviceType(), startTime, endTime, internalStatuses, 
					ApplicationConstants.ASC, chart.getLocale());
		
		List<ChartEntry> entriesForMembersWithoutResolution =
			NonOfficialResolutionChart.chartEntriesForMembersWithoutResolutionUH(chart.getSession(), 
					chart.getDeviceType(), startTime, endTime, internalStatuses, 
					ApplicationConstants.ASC, chart.getLocale());
		
		chart.getChartEntries().addAll(entriesForMembersWithResolution);
		chart.getChartEntries().addAll(entriesForMembersWithoutResolution);
		
		return (Chart) chart.persist();
	}
	
	private static List<ChartEntry> chartEntriesForMembersWithResolutionUH(final Session session, 
			final DeviceType deviceType, 
			final Date startTime,
			final Date endTime, 
			final Status[] internalStatuses, 
			final String sortOrder, 
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		House house = session.getHouse();
		HouseType houseType = house.getType();
		Integer maxResolutionsOnChart = NonOfficialResolutionChart.maxChartedResolutions(houseType);
		Date currentDate = NonOfficialResolutionChart.getCurrentDate();
		List<Member> activeMembersWithResolutions = null;;
		try{
			activeMembersWithResolutions = Resolution.findActiveMembersWithResolutions(session, currentDate, deviceType, 
					internalStatuses, startTime, endTime, sortOrder, locale);
			for(Member m : activeMembersWithResolutions) {
				ChartEntry chartEntry = NonOfficialResolutionChart.newChartEntryUH(session, m, deviceType, 
						startTime, endTime, maxResolutionsOnChart, internalStatuses, 
						locale);
				chartEntries.add(chartEntry);
			}
		}catch (ELSException e) {
			e.printStackTrace();
		}
		
		return chartEntries;
	}
	
	private static List<ChartEntry> chartEntriesForMembersWithoutResolutionUH(final Session session, 
			final DeviceType deviceType, 
			final Date startTime,
			final Date endTime, 
			final Status[] internalStatuses, 
			final String sortOrder, 
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		Date currentDate = NonOfficialResolutionChart.getCurrentDate();
		List<Member> activeMembersWithoutResolutions = null;
		try{
			activeMembersWithoutResolutions = Resolution.findActiveMembersWithoutResolutions(session, currentDate, deviceType,
					internalStatuses, startTime, endTime, sortOrder, locale);
		for(Member m : activeMembersWithoutResolutions) {
			ChartEntry chartEntry = NonOfficialResolutionChart.newEmptyChartEntry(m, locale);
			chartEntries.add(chartEntry);
		}
		}catch (ELSException e) {
			e.printStackTrace();
		}
		
		return chartEntries;
	}
	
	private static ChartEntry newChartEntryUH(final Session session, 
			final Member member,
			final DeviceType deviceType, 
			final Date startTime, 
			final Date endTime,
			final Integer maxResolutionsOnChart, 
			final Status[] internalStatuses,
			final String locale) {
		List<Device> candidateRList = new ArrayList<Device>();
		int maxR = maxResolutionsOnChart;
		if(maxR > 0) {
			List<Resolution> resolutions = null;
			try{
				resolutions = Resolution.findNonAnsweringDate(session, member, 
					deviceType, startTime, endTime, internalStatuses, maxR, 
					ApplicationConstants.ASC, locale);
				candidateRList.addAll(resolutions);
			}catch (ELSException e) {
				e.printStackTrace();
			}
		}
		ChartEntry chartEntry = new ChartEntry(member, candidateRList, locale);
		return chartEntry;
	}
	
	
	//=================================================
	//
	//=============== ASSEMBLY METHODS ================
	//
	//=================================================
	private static Chart createLH(final Chart chart) throws ELSException {
		Chart newChart = Chart.find(chart);
		
		if(newChart == null) {
			newChart = NonOfficialResolutionChart.persistChartLH(chart);
			NonOfficialResolutionChart.updateChart(newChart);
		}
		return newChart;
	}
	
	private static Boolean addToChartLH(Resolution resolution) throws ELSException {
		if(NonOfficialResolutionChart.isAssistantProcessed(resolution)) {
			Chart chart = NonOfficialResolutionChart.find(resolution.getSession(), 
					resolution.getType(), resolution.getLocale());
			if(chart != null) {
				if(NonOfficialResolutionChart.isEligibleForChart(chart, resolution)) {
					House house = chart.getSession().getHouse();
					HouseType houseType = house.getType(); 
					Integer maxNoOfResolutions = NonOfficialResolutionChart.maxChartedResolutions(houseType);
					return NonOfficialResolutionChart.addToChartIfApplicable(chart, 
							resolution, maxNoOfResolutions);
				}
			}
		}
		
		return false;
	}
	
	private static Chart persistChartLH(Chart chart) {
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		Date startTime = FormaterUtil.formatStringToDate(chart.getSession().getParameter(
				ApplicationConstants.RESOLUTION_NONOFFICIAL_SUBMISSION_STARTDATE), 
				datePattern.getValue(), chart.getLocale());
		Date endTime = FormaterUtil.formatStringToDate(chart.getSession().getParameter(
				ApplicationConstants.RESOLUTION_NONOFFICIAL_SUBMISSION_ENDDATE), 
				datePattern.getValue(), chart.getLocale());

		Status ASSISTANT_PROCESSED = Status.findByType(
				ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED, chart.getLocale());
		Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
		
		List<ChartEntry> entriesForMembersWithResolution =
			NonOfficialResolutionChart.chartEntriesForMembersWithResolutionLH(chart.getSession(), 
					chart.getDeviceType(), startTime, endTime, internalStatuses, 
					ApplicationConstants.ASC, chart.getLocale());
		
		List<ChartEntry> entriesForMembersWithoutResolution =
			NonOfficialResolutionChart.chartEntriesForMembersWithoutResolutionLH(chart.getSession(), 
					chart.getDeviceType(), startTime, endTime, internalStatuses, 
					ApplicationConstants.ASC, chart.getLocale());
		
		chart.getChartEntries().addAll(entriesForMembersWithResolution);
		chart.getChartEntries().addAll(entriesForMembersWithoutResolution);
		
		return (Chart) chart.persist();
	}
	
	private static List<ChartEntry> chartEntriesForMembersWithResolutionLH(final Session session, 
			final DeviceType deviceType, 
			final Date startTime,
			final Date endTime, 
			final Status[] internalStatuses, 
			final String sortOrder, 
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		House house = session.getHouse();
		HouseType houseType = house.getType();
		Integer maxResolutionsOnChart = NonOfficialResolutionChart.maxChartedResolutions(houseType);
		Date currentDate = NonOfficialResolutionChart.getCurrentDate();
		List<Member> activeMembersWithResolutions = null;
		try{
			activeMembersWithResolutions = Resolution.findActiveMembersWithResolutions(session, currentDate, deviceType, 
						internalStatuses,startTime, endTime, sortOrder, locale);
			for(Member m : activeMembersWithResolutions) {
				ChartEntry chartEntry = NonOfficialResolutionChart.newChartEntryLH(session, m, deviceType,
						endTime, maxResolutionsOnChart, internalStatuses, locale);
				chartEntries.add(chartEntry);
			}
		}catch (ELSException e) {
			e.printStackTrace();
		}
		
		return chartEntries;
	}
	
	private static List<ChartEntry> chartEntriesForMembersWithoutResolutionLH(final Session session, 
			final DeviceType deviceType, 
			final Date startTime,
			final Date endTime, 
			final Status[] internalStatuses, 
			final String sortOrder, 
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		Date currentDate = NonOfficialResolutionChart.getCurrentDate();
		List<Member> activeMembersWithoutResolutions = null;
		try{
			activeMembersWithoutResolutions = Resolution.findActiveMembersWithoutResolutions(session, currentDate, deviceType, 
					internalStatuses, startTime, endTime, sortOrder, locale);
		for(Member m : activeMembersWithoutResolutions) {
			ChartEntry chartEntry = NonOfficialResolutionChart.newEmptyChartEntry(m, locale);
			chartEntries.add(chartEntry);
		}
		}catch (ELSException e) {
			e.printStackTrace();
		}
		
		return chartEntries;
	}
	
	private static ChartEntry newChartEntryLH(final Session session, 
			final Member member,
			final DeviceType deviceType, 
			final Date endTime, 
			final Integer maxResolutionsOnChart,
			final Status[] internalStatuses, 
			final String locale) {
		List<Device> candidateRList = new ArrayList<Device>();
		int maxR = maxResolutionsOnChart;
		if(maxR > 0) {
			List<Resolution> resolutions = null;
			try{
				resolutions = Resolution.findNonAnsweringDate(session, member,
					deviceType, endTime, internalStatuses, maxR, ApplicationConstants.ASC, 
					locale);
				candidateRList.addAll(resolutions);
			}catch (ELSException e) {
				e.printStackTrace();
			}
		}
		ChartEntry chartEntry = new ChartEntry(member, candidateRList, locale);
		return chartEntry;
	}
	
	//=================================================
	//
	//=============== COMMON INTERNAL METHODS =========
	//
	//=================================================
	private static Integer maxChartedResolutions(
			final HouseType houseType) {
		CustomParameter noOfResolutionsParameter = null;
		
		String houseTypeType = houseType.getType();
		if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
			noOfResolutionsParameter = 
				CustomParameter.findByFieldName(CustomParameter.class, 
						"name", "NO_OF_RESOLUTIONS_ON_MEMBER_CHART_UH", "");
		}
		else if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
			noOfResolutionsParameter = 
				CustomParameter.findByFieldName(CustomParameter.class, 
						"name", "NO_OF_RESOLUTIONS_ON_MEMBER_CHART_LH", "");
		}
		
		if(noOfResolutionsParameter != null) {
			return Integer.valueOf(noOfResolutionsParameter.getValue());
		}
		return 0;
	}
	
	private static Date getCurrentDate() {
		CustomParameter dbDateFormat =
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		return FormaterUtil.getCurrentDate(dbDateFormat.getValue());
	}
	
	private static void updateChart(final Chart chart) throws ELSException {
		Status TO_BE_PUT_UP = Status.findByType(
				ApplicationConstants.RESOLUTION_SYSTEM_TO_BE_PUTUP, chart.getLocale());
		Chart.getChartRepository().updateChartResolutions(chart.getSession(), TO_BE_PUT_UP, 
				TO_BE_PUT_UP, chart.getLocale());
	}
	
	/**
	 * Create empty ChartEntry for @param member. This ChartEntry wont
	 * have any resolutions.
	 */
	private static ChartEntry newEmptyChartEntry(final Member member,
			final String locale) {
		ChartEntry chartEntry = new ChartEntry();
		chartEntry.setLocale(locale);
		chartEntry.setMember(member);
		return chartEntry;
	}
	
	private static Boolean isAssistantProcessed(final Resolution resolution) {
		Session session = resolution.getSession();
		HouseType houseType = session.getHouse().getType();
		
		Status internalStatus = null;
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			internalStatus = resolution.getInternalStatusLowerHouse();
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			internalStatus = resolution.getInternalStatusUpperHouse();
		}

		String ASSISTANT_PROCESSED = ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED;
		if(internalStatus != null && internalStatus.getType().equals(ASSISTANT_PROCESSED)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Algorithm: "ADD TO CHART IF APPLICABLE"
	 * maxResolutions = Maximum Resolutions for a Member that can be taken on a Chart.
	 * memberResolutions = Number of Member Resolutions.
	 * rejected = number of Member Resolutions with internalStatus = "REJECTED"
	 * 
	 * if(memberResolutions - rejected < maxResolutions) then add the Resolution to the chart, and 
	 * update the chart. Set the status of Resolution to "TO_BE_PUT_UP".
	 *
	 * Returns true if the @param resolution is added to the @param chart, else returns
	 * false
	 * @throws ELSException 
	 */
	private static Boolean addToChartIfApplicable(final Chart chart,
			final Resolution resolution, 
			final Integer maxNoOfResolutions) throws ELSException {
		Member member = resolution.getMember();
		
		Integer memberResolutions = Chart.getChartRepository().findResolutionsCount(member, 
				chart.getSession(), chart.getDeviceType(), new Status[]{}, chart.getLocale());
		Status REPEATREJECTED=Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REPEATREJECTION,chart.getLocale());
		Status REJECTED = Status.findByType(
				ApplicationConstants.RESOLUTION_FINAL_REJECTION, chart.getLocale());
		Integer rejected = Chart.getChartRepository().findResolutionsCount(member, 
				chart.getSession(), chart.getDeviceType(), new Status[]{ REJECTED,REPEATREJECTED }, 
				chart.getLocale());
		
		if(memberResolutions - rejected <= maxNoOfResolutions) {
			// The Resolutions taken on the Chart should have status "TO_BE_PUT_UP"
			Status TO_BE_PUT_UP = 
				Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_TO_BE_PUTUP, chart.getLocale());
			if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				resolution.setInternalStatusLowerHouse(TO_BE_PUT_UP);
				resolution.setRecommendationStatusLowerHouse(TO_BE_PUT_UP);	
			}
			else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				resolution.setInternalStatusUpperHouse(TO_BE_PUT_UP);
				resolution.setRecommendationStatusUpperHouse(TO_BE_PUT_UP);
			}
			resolution.simpleMerge();
			
			// Add the Resolution to the Chart.
			ChartEntry chartEntry = NonOfficialResolutionChart.find(chart.getChartEntries(), member);			
			List<Device> devices = Chart.getChartRepository().findDevicesWithChartEntry(chartEntry, ApplicationConstants.RESOLUTION);
			devices.add(resolution);
			devices = NonOfficialResolutionChart.reorderResolutions(devices);
			chartEntry.setDevices(devices);
			chartEntry.merge();
			
			return true;
		}
		
		return false;
	}

	private static Boolean isEligibleForChart(final Chart chart,
			final Resolution resolution) {
		Date resolutionSubmissionDate = resolution.getSubmissionDate();
		
		String strSubmissionEndDate = resolution.getSession().getParameter(
				ApplicationConstants.RESOLUTION_NONOFFICIAL_SUBMISSION_ENDDATE);
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_DATETIMEFORMAT", "");
		Date finalSubmissionDate =FormaterUtil.formatStringToDate(strSubmissionEndDate, 
				datePattern.getValue());
		
		int flag = resolutionSubmissionDate.compareTo(finalSubmissionDate);
		if(flag <= 0) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Find ChartEntry among @param chartEntries where ChartEntry.member == @param member.
	 * Returns null if ChartEntry could not be found.
	 */
	private static ChartEntry find(final List<ChartEntry> chartEntries, final Member member) {
		for(ChartEntry ce : chartEntries) {
			if(ce.getMember().getId().equals(member.getId())) {
				return ce;
			}
		}
		return null;
	}
	
	private static List<Device> reorderResolutions(final List<Device> devices) {
		return Resolution.sortByNumber(devices, ApplicationConstants.ASC);
	
	}
}
