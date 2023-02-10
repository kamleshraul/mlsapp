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
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.domain.Status;

class HalfHourDiscussionChart {

	//=================================================
	//
	//=============== VIEW METHODS ====================
	//
	//=================================================
	public static List<ChartVO> getChartVOs(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		List<ChartVO> chartVOs = new ArrayList<ChartVO>();
		
		Chart chart = HalfHourDiscussionChart.find(session, deviceType, locale);
		if(chart != null) {
			List<ChartVO> chartVOsWithDevices = new ArrayList<ChartVO>();
			List<ChartVO> chartVOsWithoutdevices = new ArrayList<ChartVO>();
			
			List<ChartEntry> chartEntries = chart.getChartEntries();
			for(ChartEntry ce : chartEntries) {
				Member member = ce.getMember();
				Long memberId = member.getId();
				String memberName = member.getFullnameLastNameFirst();
				List<DeviceVO> deviceVOs = 
					HalfHourDiscussionChart.getDeviceVOs(ce.getDevices());
				
				if(deviceVOs.isEmpty()) {
					ChartVO chartVO = new ChartVO(memberId, memberName);
					chartVOsWithoutdevices.add(chartVO);
				}
				else {
					ChartVO chartVO = 
						new ChartVO(memberId, memberName, deviceVOs);
					
					int extraCount = Question.getQuestionWithoutNumber(
							member, deviceType, session, locale);
					chartVO.setExtraCount(extraCount);
					
					String rejectedHDSes = "";
					List<Question> rejectedQuestions = 
						Question.getRejectedQuestions(member, 
								session, deviceType, locale);
					for(Question q : rejectedQuestions) {
						if(rejectedQuestions.get(0).equals(q)) {
							rejectedHDSes = q.getNumber().toString();
						}
						else {
							rejectedHDSes = rejectedHDSes + "," + 
								q.getNumber().toString();
						}
					}
					chartVO.setRejectedNotices(rejectedHDSes);
					
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
	
	public static Integer maxChartedDiscussions(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		HouseType houseType = session.getHouse().getType();
		Integer maxQuestions = 
			HalfHourDiscussionChart.maxChartedDiscussions(houseType);
		
		Integer maxChartedQuestions = 
			HalfHourDiscussionChart.findMaxChartedQuestions(session, 
					deviceType, locale);
		
		if(maxChartedQuestions > maxQuestions) {
			maxQuestions = maxChartedQuestions;
		}
		
		return maxQuestions;
	}

	private static List<DeviceVO> getDeviceVOs(final List<Device> devices) {
		List<DeviceVO> deviceVOs = new ArrayList<DeviceVO>();
		
		for(Device d : devices) {
			Question q = (Question) d;
			
			Long id = q.getId();
			Integer number = q.getNumber();
			Status internalStatus = q.getInternalStatus();
			String internalStatusType = internalStatus.getType();
			String internalStatusName = internalStatus.getName();
			
			if((! internalStatusType.equals(
					ApplicationConstants.STANDALONE_FINAL_REJECTION))
				&& 
				(! internalStatusType.equals(
						ApplicationConstants.STANDALONE_FINAL_REPEATREJECTION))) {
				DeviceVO deviceVO = new DeviceVO(id, number, 
						internalStatusType, internalStatusName);
				deviceVOs.add(deviceVO);
			}
		}
		
		return deviceVOs;
	}
	
	private static Integer findMaxChartedQuestions(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		// TODO: REFACTOR - Instead of passing nulls to ChartRepository,
		//		create a separate method in ChartRepository.
		return Chart.getChartRepository().findMaxChartedQuestions(session, null, 
				null, deviceType, locale);
	}


	//=================================================
	//
	//=============== DOMAIN METHODS ==================
	//
	//=================================================
	public static Chart create(final Chart chart) throws ELSException {
		HouseType houseType = chart.getSession().getHouse().getType();
		
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			return HalfHourDiscussionChart.createLH(chart);
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			throw new ELSException("StarredQuestionChart.create/1", 
				"Method invoked for inappropriate house type..");
		}
		
		return chart;
	}

	/**
	 * A Chart is said to be processed if all the Questions on the
	 * Chart have internalStatus type != 'question_before_workflow_tobeputup'.
	 * 
	 * Returns true if a Chart is processed or if a Chart does not exist,
	 * else returns false.
	 * @throws ELSException 
	 */
	public static Boolean isProcessed(final Chart chart)  throws ELSException {
		Chart newChart = HalfHourDiscussionChart.find(chart.getSession(),
				chart.getDeviceType(), 
				chart.getLocale());
		
		if(newChart != null) {
			String excludeInternalStatus = 
				ApplicationConstants
					.STANDALONE_SYSTEM_TO_BE_PUTUP;
			return Chart.getChartRepository().isProcessed(chart, 
					excludeInternalStatus);
		}
		
		return true;
	}

	public static Boolean addToChart(
			final StandaloneMotion question) throws ELSException {
		Session session = question.getSession();
		HouseType houseType = session.getHouse().getType();
		
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			return HalfHourDiscussionChart.addToChartLH(question);
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			throw new ELSException("StandaloneMotionChart.addToChart/1", 
				"Method invoked for inappropriate house type..");
		}
		
		return false;
	}
	
	// TODO: Implement
	public static boolean forcefullyAddToChart(final Chart chart,
			final StandaloneMotion motion) throws ELSException {
		return false;
	}
	
	// TODO: Test. Handle clubbing case. Make the immediate clubbed entity as the parent.
	// Refer StarredQuestionChart.removeFromChart/1.
	public static void removeFromChart(final StandaloneMotion standaloneMotion) throws ELSException {
		Chart chart = Chart.find(standaloneMotion);
		if(chart != null) {
			// Remove standalone motion from the Chart
			Member member = standaloneMotion.getPrimaryMember();
			ChartEntry chartEntry = Chart.find(chart, member);
			
			List<Device> devices = chartEntry.getDevices();
			List<Device> newDevices = new ArrayList<Device>();
			
			for(Device d : devices) {
				Question q = (Question) d;
				if(! q.getId().equals(standaloneMotion.getId())) {
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
	
	// FIND QUESTIONS
	public static List<Question> findQuestions(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		// TODO: REFACTOR - Instead of passing nulls to ChartRepository,
		//		create a separate method in ChartRepository.
		return Chart.getChartRepository().findQuestions(session, 
				null, null, deviceType, locale);
	}
	
	public static List<Question> findQuestions(final Member member, 
			final Session session,
			final DeviceType deviceType,
			final String locale) throws ELSException {
		// TODO: REFACTOR - Instead of passing nulls to ChartRepository,
		//		create a separate method in ChartRepository.
		return Chart.getChartRepository().findQuestions(member, session, null, 
				null, deviceType, locale);
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
	//=============== ASSEMBLY METHODS ================
	//
	//=================================================
	// CREATE CHART
	private static Chart createLH(final Chart chart) throws ELSException {
		Chart newChart = Chart.find(chart);
		if(newChart == null) {
			newChart = HalfHourDiscussionChart.persistChartLH(chart);
			HalfHourDiscussionChart.updateChart(newChart);
		}
		return newChart;
	}
	
	private static Chart persistChartLH(final Chart chart) {
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
		
		Date startTime = HalfHourDiscussionChart.getSubmissionStartTime(
				session, deviceType, locale);
		Date endTime = HalfHourDiscussionChart.getSubmissionEndTime(
				session, deviceType, locale);
			
		Status ASSISTANT_PROCESSED = Status.findByType(
				ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED, 
				locale);
		Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
		
		House house = session.getHouse();
		HouseType houseType = house.getType();
		Integer maxQuestionsOnChart = 
			HalfHourDiscussionChart.maxChartedDiscussions(houseType);
		
		List<ChartEntry> entriesForMembersWithQuestion =
			HalfHourDiscussionChart.chartEntriesForMembersWithQuestionLH(session, 
					deviceType, startTime, endTime, maxQuestionsOnChart, 
					internalStatuses, ApplicationConstants.ASC, locale);
		
		List<ChartEntry> entriesForMembersWithoutQuestion =
			HalfHourDiscussionChart.chartEntriesForMembersWithoutQuestionLH(
					session, deviceType, startTime, endTime, 
					internalStatuses, ApplicationConstants.ASC, 
					locale);
		
		chart.getChartEntries().addAll(entriesForMembersWithQuestion);
		chart.getChartEntries().addAll(entriesForMembersWithoutQuestion);
		
		return (Chart) chart.persist();
	}
	
	private static List<ChartEntry> chartEntriesForMembersWithQuestionLH(
			final Session session, 
			final DeviceType deviceType, 
			final Date startTime, 
			final Date finalSubmissionTime,
			final Integer maxQuestionsOnChart,
			final Status[] internalStatuses, 
			final String sortOrder, 
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		Date currentDate = HalfHourDiscussionChart.getCurrentDate();
		List<Member> activeMembersWithQuestions = 
			StandaloneMotion.findActiveMembersWithStandaloneMotions(session, currentDate, 
					deviceType, null, internalStatuses, null, startTime, finalSubmissionTime, sortOrder, locale);

		for(Member m : activeMembersWithQuestions) {
			ChartEntry chartEntry = HalfHourDiscussionChart.newChartEntryLH(
					session, m, deviceType, finalSubmissionTime, 
					maxQuestionsOnChart, internalStatuses, locale);
			chartEntries.add(chartEntry);
		}
		
		return chartEntries;
	}
	
	private static List<ChartEntry> chartEntriesForMembersWithoutQuestionLH(
			final Session session, 
			final DeviceType deviceType, 
			final Date startTime, 
			final Date finalSubmissionTime,
			final Status[] internalStatuses, 
			final String sortOrder, 
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		Date currentDate = HalfHourDiscussionChart.getCurrentDate();
		List<Member> activeMembersWithoutQuestions = 
			StandaloneMotion.findActiveMembersWithoutStandalolneMotions(session, currentDate, 
					deviceType, null, internalStatuses, null, 
					startTime, finalSubmissionTime, sortOrder, locale);
		for(Member m : activeMembersWithoutQuestions) {
			ChartEntry chartEntry = 
				HalfHourDiscussionChart.newEmptyChartEntry(m, locale);
			chartEntries.add(chartEntry);
		}
		
		return chartEntries;
	}
	
	private static ChartEntry newChartEntryLH(final Session session, 
			final Member member,
			final DeviceType deviceType,
			final Date finalSubmissionDate,
			final Integer maxQuestionsOnChart,
			final Status[] internalStatuses,
			final String locale) {
		List<Device> candidateList = new ArrayList<Device>();
		
		int maxQ = maxQuestionsOnChart;
		if(maxQ > 0) {
			List<StandaloneMotion> motions = StandaloneMotion.findNonDiscussionDate(
					session, member, deviceType, null, finalSubmissionDate, 
					internalStatuses, maxQ, ApplicationConstants.ASC, 
					locale);
			
			candidateList.addAll(motions);
		}
		
		ChartEntry chartEntry = new ChartEntry(member, candidateList, locale);
		return chartEntry;
	}
	
	// ADD TO CHART
	private static Boolean addToChartLH(
			final StandaloneMotion motion) throws ELSException {
		if(HalfHourDiscussionChart.isAssistantProcessed(motion)) {
			Session session = motion.getSession();
			DeviceType deviceType = motion.getType();
			String locale = motion.getLocale();
			
			Chart chart = HalfHourDiscussionChart.find(session, 
					deviceType, locale);
			if(chart != null) {
				House house = session.getHouse();
				HouseType houseType = house.getType();
				Integer maxNoOfQuestions = 
					HalfHourDiscussionChart.maxChartedDiscussions(houseType);
				return HalfHourDiscussionChart.addToChartIfApplicable(
						chart, motion, maxNoOfQuestions);
			}
		}
		
		return false;
	}
	
	
	//=================================================
	//
	//=============== INTERNAL METHODS ================
	//
	//=================================================
	private static Integer maxChartedDiscussions(
			final HouseType houseType) {
		CustomParameter noOfDiscussionsParameter = null;
		
		String houseTypeType = houseType.getType();
		if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
			noOfDiscussionsParameter = 
				CustomParameter.findByFieldName(CustomParameter.class, 
						"name", 
						"NO_OF_HALFHOURDISCUSSIONSTANDALONE_ON_CHART_COUNT_LH", 
						"");
		}
		
		if(noOfDiscussionsParameter != null) {
			return Integer.valueOf(noOfDiscussionsParameter.getValue());
		}
		return 0;
	}
	
	private static Date getSubmissionStartTime(final Session session,
			final DeviceType deviceType, 
			final String locale) {
		StringBuffer key = new StringBuffer();
		key.append(deviceType.getType());
		key.append("_submissionStartDate");
		String value = session.getParameter(key.toString());
		
		CustomParameter datePattern = 
			CustomParameter.findByName(CustomParameter.class, 
					"DB_TIMESTAMP", "");
		String datePatternValue = datePattern.getValue();
		
		Date submissionStartTime = FormaterUtil.formatStringToDate(value, 
				datePatternValue, locale);
		return submissionStartTime;
	}
	
	private static Date getSubmissionEndTime(final Session session,
			final DeviceType deviceType, 
			final String locale) {
		StringBuffer key = new StringBuffer();
		key.append(deviceType.getType());
		key.append("_submissionEndDate");
		String value = session.getParameter(key.toString());
		
		CustomParameter datePattern = 
			CustomParameter.findByName(CustomParameter.class, 
					"DB_TIMESTAMP", "");
		String datePatternValue = datePattern.getValue();
		
		Date submissionEndTime = FormaterUtil.formatStringToDate(value, 
				datePatternValue, locale);
		return submissionEndTime;
	}
	
	private static void updateChart(final Chart chart) throws ELSException {
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
		Status TO_BE_PUT_UP = Status.findByType(
				ApplicationConstants
					.STANDALONE_SYSTEM_TO_BE_PUTUP, 
				locale);
			
		Chart.getChartRepository().updateChartStandalones(session, 
				null, deviceType, null, TO_BE_PUT_UP, TO_BE_PUT_UP, 
				locale);
	}
	
	private static Date getCurrentDate() {
		CustomParameter dbDateFormat =
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		return FormaterUtil.getCurrentDate(dbDateFormat.getValue());
	}
	
	/**
	 * Create empty ChartEntry for @param member. This ChartEntry wont
	 * have any questions.
	 */
	private static ChartEntry newEmptyChartEntry(final Member member,
			final String locale) {
		ChartEntry chartEntry = new ChartEntry();
		chartEntry.setLocale(locale);
		chartEntry.setMember(member);
		return chartEntry;
	}
	
	private static Boolean isAssistantProcessed(final StandaloneMotion motion) {
		String ASSISTANT_PROCESSED = 
			ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED;
		
		Status internalStatus = motion.getInternalStatus();
		if(internalStatus.getType().equals(ASSISTANT_PROCESSED)) {
			return true;
		}
		return false;
	}
	
	private static Boolean addToChartIfApplicable(final Chart chart,
			final StandaloneMotion motion, 
			final Integer maxNoOfQuestions) throws ELSException {
		Boolean isAddedToChart = false;
		
		Member member = motion.getPrimaryMember();
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
		
		Integer memberQuestions = Chart.getChartRepository().findStandalonesCount(
				member, session, deviceType, new Status[]{}, locale);
		
		Status REJECTED = Status.findByType(
				ApplicationConstants.STANDALONE_FINAL_REJECTION, 
				locale);
		Integer rejected = Chart.getChartRepository().findStandalonesCount(member, 
				session, deviceType, new Status[]{ REJECTED }, 
				locale);
		
		Status REPEAT_REJECTED = Status.findByType(
				ApplicationConstants.STANDALONE_FINAL_REPEATREJECTION, 
				locale);
		Integer repeat_rejected = Chart.getChartRepository().findStandalonesCount(member, 
				session, deviceType, new Status[]{ REPEAT_REJECTED }, 
				locale);
		
		if(memberQuestions - rejected - repeat_rejected < maxNoOfQuestions) {
			// The HDS taken on the Chart should have status "TO_BE_PUT_UP"
			Status TO_BE_PUT_UP = 
				Status.findByType(
						ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP, 
						locale);
			
			motion.setInternalStatus(TO_BE_PUT_UP);
			motion.setRecommendationStatus(TO_BE_PUT_UP);	
			motion.simpleMerge();
			
			// Add the HDS to the Chart.
			ChartEntry chartEntry = HalfHourDiscussionChart.find(
					chart.getChartEntries(), member);
			List<Device> devices = Chart.getChartRepository().findDevicesWithChartEntry(chartEntry, ApplicationConstants.STANDALONE_MOTION);
			devices.add(motion);
			devices = HalfHourDiscussionChart.reorderDevices(devices, motion.getClass().getSimpleName());
			chartEntry.setDevices(devices);
			chartEntry.merge();
			
			isAddedToChart = true;
		}
		
		return isAddedToChart;
	}
	
	/**
	 * Find ChartEntry among @param chartEntries where ChartEntry.member == @param member.
	 * Returns null if ChartEntry could not be found.
	 */
	private static ChartEntry find(final List<ChartEntry> chartEntries, 
			final Member member) {
		for(ChartEntry ce : chartEntries) {
			if(ce.getMember().getId().equals(member.getId())) {
				return ce;
			}
		}
		return null;
	}
	
	private static List<Device> reorderDevices(final List<Device> devices, String className) {
		
		List<Device> retDevices = new ArrayList<Device>();
		
		if(className.equals("StandaloneMotion")){
			List<StandaloneMotion> qList = new ArrayList<StandaloneMotion>();
			for(Device d : devices){
				StandaloneMotion q = ((StandaloneMotion)d);
				qList.add(q);
			}
			List<StandaloneMotion> newQList = 
					StandaloneMotion.sortByNumber(qList, ApplicationConstants.ASC);
			
			for(StandaloneMotion tq : newQList){
				retDevices.add(tq);
			}
		}
		return retDevices;
	}
}
