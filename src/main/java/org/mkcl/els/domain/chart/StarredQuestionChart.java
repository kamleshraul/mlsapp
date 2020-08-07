package org.mkcl.els.domain.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Question.CLUBBING_STATE;
import org.mkcl.els.domain.Question.PROCESSING_MODE;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;

class StarredQuestionChart {
	
	//=================================================
	//
	//=============== VIEW METHODS ====================
	//
	//=================================================
	public static List<ChartVO> getChartVOs(final Session session, 
			final Group group,
			final Date answeringDate, 
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		List<ChartVO> chartVOs = new ArrayList<ChartVO>();
		
		Chart chart = StarredQuestionChart.find(session, 
				group, answeringDate, deviceType, locale);
		if(chart != null) {
			List<ChartVO> chartVOsWithDevices = new ArrayList<ChartVO>();
			List<ChartVO> chartVOsWithoutdevices = new ArrayList<ChartVO>();
			
			List<ChartEntry> chartEntries = chart.getChartEntries();
			for(ChartEntry ce : chartEntries) {
				Long memberId = ce.getMember().getId();
				String memberName = ce.getMember().getFullnameLastNameFirst();
				List<DeviceVO> deviceVOs = 
					StarredQuestionChart.getDeviceVOs(ce.getDevices());
				
				if(deviceVOs.isEmpty()) {
					ChartVO chartVO = new ChartVO(memberId, memberName);
					chartVOsWithoutdevices.add(chartVO);
				}
				else {
					ChartVO chartVO = 
						new ChartVO(memberId, memberName, deviceVOs);
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
	
	/**
	 * Finds the maximum Questions on Chart against any member.
	 * Useful for Council where all the non-charted Questions of
	 * a Member are to be taken on last answeringDate's Chart.
	 *  
	 * Example: 
	 * 	Scenario 1
	 * 		SessionConfig.MAX_QUESTIONS_ON_CHART = 5
	 * 		A has 3 Questions on Chart
	 * 		B has 21 Questions on Chart
	 * 		C has 1 Question on Chart
	 * 		This method will return 21.
	 * 
	 * Scenario 2
	 * 		SessionConfig.MAX_QUESTIONS_ON_CHART = 5
	 * 		A has 3 Questions on Chart
	 * 		B has 2 Questions on Chart
	 * 		C has 1 Question on Chart
	 * 		This method will return 5.
	 * @throws ELSException
	 */
	public static Integer maxChartedQuestions(final Session session, 
			final Group group,
			final Date answeringDate, 
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		HouseType houseType = session.getHouse().getType();
		Integer maxQuestions = 
			StarredQuestionChart.maxChartedQuestions(houseType);
		
		Integer maxChartedQuestions = 
			StarredQuestionChart.findMaxChartedQuestions(session, 
					group, answeringDate, deviceType, locale);
		
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
			
			DeviceVO deviceVO = new DeviceVO(id, number, internalStatusType);
			if(q.getParent() == null) {
				deviceVO.setHasParent(false);
				
				List<ClubbedEntity> clubbedEntities = q.getClubbedEntities();
				String kids = 
					StarredQuestionChart.getClubbingsAsCommaSeparatedString(
							clubbedEntities);
				deviceVO.setKids(kids);
			}
			else {
				deviceVO.setHasParent(true);
				
				Integer parentNumber = q.getParent().getNumber();
				deviceVO.setParent(String.valueOf(parentNumber));
			}
			deviceVOs.add(deviceVO);
		}
		
		return deviceVOs;
	}
	
	private static String getClubbingsAsCommaSeparatedString(
			final List<ClubbedEntity> clubbedEntities) {
		StringBuffer sb = new StringBuffer("");
		
		if(clubbedEntities != null) {
			int n = clubbedEntities.size();
			for(int i = 0; i < n; i++) {
				Question clubbedQuestion = clubbedEntities.get(i).getQuestion();
				sb.append(clubbedQuestion.getNumber());
				if(i < n - 1) {
					sb.append(", ");
				}
			}
		}
		
		return sb.toString();
	}
	
	private static Integer findMaxChartedQuestions(final Session session,
			final Group group, 
			final Date answeringDate,
			final DeviceType deviceType,
			final String locale) throws ELSException {
		return Chart.getChartRepository().findMaxChartedQuestions(session, 
				group, answeringDate, deviceType, locale);
	}

	
	//=================================================
	//
	//=============== DOMAIN METHODS ==================
	//
	//=================================================
	public static Chart create(final Chart chart) throws ELSException {
		Session session = chart.getSession();
		PROCESSING_MODE processingMode = Question.getProcessingMode(session);
		
		try {
			if(processingMode == PROCESSING_MODE.LOWERHOUSE) {
				return StarredQuestionChart.createLH(chart);
			}
			else { // processingMode == PROCESSING_MODE.UPPERHOUSE)
				return StarredQuestionChart.createUH(chart);
			}
		}
		catch(ELSException e) {
			throw e;
		}
	}
	
	/**
	 * A Chart is said to be processed if all the Questions on the
	 * Chart have internalStatus type != 'QUESTION_SYSTEM_TO_BE_PUTUP'.
	 * 
	 * Returns true if a Chart is processed or if a Chart does not exist,
	 * else returns false.
	 * @throws ELSException 
	 */
	public static Boolean isProcessed(final Chart chart) throws ELSException {
		Session session = chart.getSession();
		Group group = chart.getGroup();
		Date answeringDate = chart.getAnsweringDate();
		DeviceType deviceType = chart.getDeviceType();
		String locale= chart.getLocale();
		
		Chart newChart = StarredQuestionChart.find(session, group, 
				answeringDate, deviceType, locale);
		if(newChart != null) {
			String excludeInternalStatusType = 
				ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP;
			return Chart.getChartRepository().isProcessed(chart, 
					excludeInternalStatusType);
		}
		
		return true;
	}
	
	public static Boolean addToChart(
			final Question question) throws ELSException {
		Session session = question.getSession();
		PROCESSING_MODE processingMode = Question.getProcessingMode(session);
		
		try {
			if(processingMode == PROCESSING_MODE.LOWERHOUSE) {
				return StarredQuestionChart.addToChartLH(question);
			}
			else {// processingMode == PROCESSING_MODE.UPPERHOUSE)
				return StarredQuestionChart.addToChartUH(question);
			}
		}
		catch(ELSException e) {
			throw e;
		}
	}
	
	// TODO: Refactor. Reason: Following implementation is copy-paste of 
	// processTargetGroupChart/2.
	public static boolean forcefullyAddToChart(final Chart chart,
			final Question question) throws ELSException {
		/*
		 * 1. Set question.chartAnsweringDate == chart.answeringDate 
		 */
		Group group = chart.getGroup();
		Date answeringDate = chart.getAnsweringDate();
		QuestionDates chartAnsweringDate = 
			group.findQuestionDatesByGroupAndAnsweringDate(answeringDate);
		question.setChartAnsweringDate(chartAnsweringDate);
		question.simpleMerge();
		
		/*
		 * 2. Add @param question to @param chart
		 */
		Member member = question.getPrimaryMember();
		ChartEntry ce = 
			StarredQuestionChart.find(chart.getChartEntries(), member);
		List<Device> devices = StarredQuestionChart.findDevices(ce);//ce.getDevices();
		devices.add(question);
		
		/*
		 * 3. Reorder the devices and then add them to the Chart
		 */
		List<Question> questions = 
			StarredQuestionChart.marshallDevices(devices);
		List<Question> reorderedQuestions = 
			StarredQuestionChart.reorderQuestions(questions, answeringDate);
		List<Device> reorderedDevices =
			StarredQuestionChart.marshallQuestions(reorderedQuestions);
		ce.setDevices(reorderedDevices);
		ce.merge();
		
		return true;
	}
	
	public static void removeFromChart(
			final Question question) throws ELSException {
		Chart chart = Chart.find(question);
		if(chart != null) {
			// Remove question from the Chart.
			Member member = question.getPrimaryMember();
			ChartEntry chartEntry = Chart.find(chart, member);
			
			List<Device> devices = chartEntry.getDevices();
			List<Device> newDevices = new ArrayList<Device>();
			
			for(Device d : devices) {
				Question q = (Question) d;
				if(! q.getId().equals(question.getId())) {
					newDevices.add(q);
				}
			}
			
			chartEntry.setDevices(newDevices);
			chartEntry.persist();
			
			// Reset chart specific attributes in @param question.
			question.setChartAnsweringDate(null);
			question.simpleMerge();
			
			// Update clubbing			
			Question parent = question.getParent();
			if(parent != null) { // question is clubbed to parent
				// Unclub question from parent
				List<ClubbedEntity> clubbedEntities = Question.findClubbedEntitiesByPosition(parent);
				int counter = 0;
				for(ClubbedEntity ce : clubbedEntities) {
					Question q = ce.getQuestion();
					if(q.getId().equals(question.getId())) {
						clubbedEntities.remove(counter);
						ce.remove();
					}
					counter++;
				}
				parent.setClubbedEntities(clubbedEntities);
				parent.simpleMerge();
				
				question.setParent(null);
				question.simpleMerge();
			}
			else { // question may be standalone or may have kids
				List<ClubbedEntity> clubbedEntities = Question.findClubbedEntitiesByPosition(question);
				
				// Immediate clubbed question (if any) will become parent question.
				if(clubbedEntities != null && ! clubbedEntities.isEmpty()) {
					ClubbedEntity immediateClubbedEntity = clubbedEntities.get(0);
					Question newParent = immediateClubbedEntity.getQuestion();
					clubbedEntities.remove(0);
					
					// Reset the positions in Clubbed entities & set newParent as parent of all the clubbings
					int position = 0;
					for(ClubbedEntity ce : clubbedEntities) {
						Question q = ce.getQuestion();
						q.setParent(newParent);
						q.simpleMerge();
						
						ce.setPosition(++position);
						ce.merge();
					}
					
					newParent.setClubbedEntities(clubbedEntities);
					newParent.setParent(null);
					newParent.merge();
				}
			}
		}
	}
	
	public static void simpleRemoveFromChart(
			final Question question) throws ELSException {
		Chart chart = Chart.find(question);
		if(chart != null) {
			// Remove question from the Chart.
			Member member = question.getPrimaryMember();
			ChartEntry chartEntry = Chart.find(chart, member);
			
			List<Device> devices = chartEntry.getDevices();
			List<Device> newDevices = new ArrayList<Device>();
			
			for(Device d : devices) {
				Question q = (Question) d;
				if(! q.getId().equals(question.getId())) {
					newDevices.add(q);
				}
			}
			
			chartEntry.setDevices(newDevices);
			chartEntry.persist();
			
			// Reset chart specific attributes in @param question.
			question.simpleMerge();
		}
	}

	public static void groupChange(final Question question, 
			final Group fromGroup,
			final boolean isForceAddToTargetGroupChart) throws ELSException {
		Session session = question.getSession();
		PROCESSING_MODE processingMode = Question.getProcessingMode(session);
		
		try {
			if(processingMode == PROCESSING_MODE.LOWERHOUSE) {
				StarredQuestionChart.groupChangeLH(question, 
						fromGroup, isForceAddToTargetGroupChart);
			}
			else {// processingMode == PROCESSING_MODE.UPPERHOUSE)
				StarredQuestionChart.groupChangeUH(question, 
						 fromGroup, isForceAddToTargetGroupChart);
			}
		}
		catch(ELSException e) {
			throw e;
		}	
	}

	// FIND CHART
	public static Chart find(final Session session, 
			final Group group, 
			final Date answeringDate,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().find(session, 
				group, answeringDate, deviceType, locale);
	}
	
	public static Chart findLatestChart(final Session session, 
			final Group group,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		List<Date> answeringDates = 
			group.getAnsweringDates(ApplicationConstants.DESC);
		for(Date date : answeringDates) {
			Chart chart = StarredQuestionChart.find(session, group, date, 
					deviceType, locale); 
			if(chart != null) {
				return chart;
			}
		}
		
		return null;
	}
	
	// FIND QUESTIONS
	public static List<Question> findQuestions(final Session session, 
			final Group group,
			final Date answeringDate, 
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().findQuestions(session, group, 
				answeringDate, deviceType, locale);
	}
	
	public static List<Question> findQuestions(final Member member, 
			final Session session,
			final Group group, 
			final Date answeringDate, 
			final DeviceType deviceType,
			final String locale) throws ELSException {
		return Chart.getChartRepository().findQuestions(member, session, group, 
				answeringDate, deviceType, locale);
	}
	
	// FIND MEMBERS
	public static List<Member> findMembers(final Session session, 
			final Group group,
			final Date answeringDate, 
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().findMembers(session, group, 
				answeringDate, deviceType, locale);
	}	
	
	
	//=================================================
	//
	//======= UPPERHOUSE MODE METHODS =================
	//
	//=================================================
	// "CREATE CHART" SUPPORTING METHODS
	/**
	 * Creates a new Chart.
	 * 
	 * IF the Chart exists then return the existing Chart.
	 * ELSE
	 * 		Check the value of the configuration parameter = 
	 * 		"CREATE_CHART_WITHOUT_PROCESSING_PREVIOUS_CHART_<HOUSETYPE>". 
	 * 
	 * 		IF the value is YES:
	 * 			Create a new Chart
	 * 		ELSE:
	 * 			IF a previous dated Chart exists & is unprocessed 
	 * 			then don't create a new Chart and return null.
	 * 			ELSE create a new Chart.
	 * 
	 * Only the Questions submitted for the first batch are to be taken on
	 * the Chart while creating the Chart. Besides if the Chart is being
	 * created for the final answeringDate of a Group then (in a worst
	 * case scenario) it may have Session.numberOfQuestionInFirstBatch
	 * Questions on the Chart.
	 *
	 * @return the chart
	 * @throws ELSException 
	 */
	private static Chart createUH(final Chart chart) throws ELSException {
		Chart newChart = Chart.find(chart);
		
		if(newChart == null) {
			Session session = chart.getSession();
			House house = session.getHouse();
			HouseType houseType = house.getType();
			
			boolean isCreateChartWithoutProcessingPreviousChart =
				StarredQuestionChart
					.isCreateChartWithoutProcessingPreviousChart(houseType);
			if(isCreateChartWithoutProcessingPreviousChart) {
				newChart = StarredQuestionChart.createChartUH(chart);
				StarredQuestionChart.updateChart(newChart);
			}
			else {
				boolean isPreviousChartExists = 
					StarredQuestionChart.isPreviousChartExists(chart);
				boolean isPreviousChartProcessed = 
					StarredQuestionChart.isPreviousChartProcessed(chart);
				
				if(isPreviousChartExists == false
						|| isPreviousChartProcessed == true) {
					newChart = StarredQuestionChart.createChartUH(chart);
					StarredQuestionChart.updateChart(newChart);
				}
				else {
					return null;
				}
			}
		}
		
		return newChart;
	}
	
	private static Chart createChartUH(
			final Chart chart) throws ELSException {
		Session session = chart.getSession();
		Group group = chart.getGroup();
		DeviceType deviceType = chart.getDeviceType();
		Date answeringDate = chart.getAnsweringDate();
		String locale = chart.getLocale();
		
		Date firstBatchSubmissionStartTime =
			StarredQuestionChart.getFirstBatchSubmissionStartTime(session, 
					locale);
		Date firstBatchSubmissionEndTime =
			StarredQuestionChart.getFirstBatchSubmissionEndTime(session, 
					locale);
		
		// Determine the internal statuses which forms the pre-requisite
		// for taking a Question on the Chart.
		Status ASSISTANT_PROCESSED = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, 
				locale);
		Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
		
		// Create chart entries for Members with Questions
		List<ChartEntry> entriesForMembersWithQuestion =
			StarredQuestionChart.chartEntriesForMembersWithQuestionUH(
					session, deviceType, group, answeringDate, 
					firstBatchSubmissionStartTime, firstBatchSubmissionEndTime, 
					internalStatuses, ApplicationConstants.ASC, 
					locale);
		
		// Create chart entries for Members without Questions
		List<ChartEntry> entriesForMembersWithoutQuestion =
			StarredQuestionChart.chartEntriesForMembersWithoutQuestionUH(
					session, deviceType, group, answeringDate, 
					firstBatchSubmissionStartTime, firstBatchSubmissionEndTime, 
					internalStatuses, ApplicationConstants.ASC, 
					locale);
		
		chart.getChartEntries().addAll(entriesForMembersWithQuestion);
		chart.getChartEntries().addAll(entriesForMembersWithoutQuestion);
		
		return (Chart) chart.persist();
	}
	
	private static List<ChartEntry> chartEntriesForMembersWithQuestionUH(
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Date startTime,
			final Date endTime,
			final Status[] internalStatuses,
			final String sortOrder,
			final String locale) throws ELSException {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		Date currentDate = StarredQuestionChart.getCurrentDate();
		List<Member> activeMembersWithQuestions = 
			Question.findActiveMembersWithQuestions(session, currentDate, 
					deviceType, group, internalStatuses, answeringDate, 
					startTime, endTime, sortOrder, locale);
		
		House house = session.getHouse();
		HouseType houseType = house.getType();
		
		boolean isLastAnsweringDate = 
			StarredQuestionChart.isLastAnsweringDate(group, answeringDate);
		boolean isProcessAllRemainingQnsForLastDate =
			StarredQuestionChart
				.isProcessAllRemainingQnsForFirstBatchLastDate(houseType);
		if(isLastAnsweringDate && isProcessAllRemainingQnsForLastDate) {
			for(Member m : activeMembersWithQuestions) {
				ChartEntry chartEntry = StarredQuestionChart.newChartEntryUH(
						session, m, deviceType, group, answeringDate, 
						startTime, endTime, internalStatuses, locale);
				chartEntries.add(chartEntry);
			}
		}
		else {
			Integer maxQuestionsOnChart = 
				StarredQuestionChart.maxChartedQuestions(houseType);
			
			for(Member m : activeMembersWithQuestions) {
				ChartEntry chartEntry = StarredQuestionChart.newChartEntryUH(
						session, m, deviceType, group, answeringDate, 
						startTime, endTime, maxQuestionsOnChart, 
						internalStatuses, locale);
				chartEntries.add(chartEntry);
			}			
		}

		return chartEntries;
	}

	private static List<ChartEntry> chartEntriesForMembersWithoutQuestionUH(
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Date startTime,
			final Date endTime,
			final Status[] internalStatuses,
			final String sortOrder,
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		Date currentDate = StarredQuestionChart.getCurrentDate();
		List<Member> activeMembersWithoutQuestions = 
			Question.findActiveMembersWithoutQuestions(session, currentDate, 
					deviceType, group, internalStatuses, answeringDate, 
					startTime, endTime, sortOrder, locale);
		for(Member m : activeMembersWithoutQuestions) {
			ChartEntry chartEntry = 
				StarredQuestionChart.newEmptyChartEntry(m, locale);
			chartEntries.add(chartEntry);
		}
		
		return chartEntries;
	}
	
	/**
	 * Use this Charting algorithm when creating a ChartEntry (for the first
	 * time).
	 * 
	 * Search for at most @param maxNoOfQuestions according to the following
	 * algorithm. Search only for those Questions which are submitted between
	 * @param startTime and @param endTime (both time inclusive) and are 
	 * verified by the assistant ("ASSISTANT_PROCESSED").
	 * 
	 * 1. Select the Questions which have the answeringDate attribute
	 * explicitly set to the expected answeringDate.
	 * 
	 * 2. Select the Questions which have an answeringDate attribute
	 * explicitly set to a date before the expected answeringDate.
	 * 
	 * 3. Select the Questions which don't have any answeringDate.
	 * 
	 * Returns an empty list if there are no Questions.
	 */
	private static ChartEntry newChartEntryUH(final Session session, 
			final Member member,
			final DeviceType deviceType, 
			final Group group, 
			final Date answeringDate,
			final Date startTime, 
			final Date endTime, 
			final Status[] internalStatuses,
			final String locale) {
		List<Device> candidateQList = new ArrayList<Device>();
		
		// List of Questions having an explicit answeringDate attribute set
		// to current date or previous answering date(s)
		List<Question> datedQuestions = Question.findDatedQuestions(session, 
				member, deviceType, group, answeringDate, 
				startTime, endTime, internalStatuses, locale);
		candidateQList.addAll(datedQuestions);
		
		// List of Questions without any answering date
		List<Question> nonDatedQuestions = Question.findNonAnsweringDate(
				session, member, deviceType, group, 
				startTime, endTime, internalStatuses, ApplicationConstants.ASC, 
				locale);
		candidateQList.addAll(nonDatedQuestions);
		
		ChartEntry chartEntry = new ChartEntry(member, candidateQList, locale);
		return chartEntry;
	}
	
	/**
	 * Use this Charting algorithm when creating a ChartEntry (for the first
	 * time).
	 * 
	 * Search for at most @param maxNoOfQuestions according to the following
	 * algorithm. Search only for those Questions which are submitted between
	 * @param startTime and @param endTime (both time inclusive) and are 
	 * verified by the assistant ("ASSISTANT_PROCESSED")
	 * 
	 * 1. Select the Questions which have the answeringDate attribute
	 * explicitly set to the expected answeringDate.
	 * 
	 * 2. Select the Questions which have an answeringDate attribute
	 * explicitly set to a date before the expected answeringDate.
	 * 
	 * 3. Select the Questions which don't have any answeringDate.
	 * 
	 * Returns an empty list if there are no Questions.
	 */
	private static ChartEntry newChartEntryUH(final Session session, 
			final Member member,
			final DeviceType deviceType, 
			final Group group, 
			final Date answeringDate,
			final Date startTime, 
			final Date endTime, 
			final Integer maxQuestionsOnChart,
			final Status[] internalStatuses, 
			final String locale) {
		List<Device> candidateQList = new ArrayList<Device>();
		int maxQ = maxQuestionsOnChart;
		
		// List of Questions having an explicit answeringDate attribute set
		// to current date or previous answering date(s)
		if(maxQ > 0) {
			List<Question> questions = Question.findDatedQuestions(
					session, member, deviceType, group, answeringDate, 
					startTime, endTime, internalStatuses, maxQ, 
					locale);
			
			candidateQList.addAll(questions);
			maxQ = maxQ - questions.size();
		}
		
		// List of Questions without any answering date
		if(maxQ > 0) {
			List<Question> questions = Question.findNonAnsweringDate(session, 
					member, deviceType, group, startTime, endTime, 
					internalStatuses, maxQ, ApplicationConstants.ASC, 
					locale);
			
			candidateQList.addAll(questions);
		}
		
		ChartEntry chartEntry = new ChartEntry(member, candidateQList, locale);
		return chartEntry;
	}
	
	// "ADD TO CHART" SUPPORTING METHODS
	/**
	 * Algorithm:
	 * 1. Check IF internalStatus of @param question is "ASSISTANT_PROCESSED"?
	 * 
	 * 2. IF the Question is submitted for First Batch do the following:
	 * a> IF @param q does not specify any answeringDate 
	 * (q.answeringDate == null) then begin from first Chart and perform
	 * the action mentioned in Step 2c.
	 * 
	 * b> IF @param q specifies an answeringDate then beginning from Chart 
	 * with answeringDate == q.answeringDate perform the action mentioned 
	 * in Step 2c.
	 * 
	 * c> @param q will either go on an intermediate Chart or last Chart.
	 * IF q goes on last Chart then add it to the Chart.
	 * IF q goes on an intermediate Chart then it's inclusion might
	 * force some existing question out of the Chart. Recursively call
	 * addToChartUH/1 for that question. This will continue until all the
	 * questions are placed on appropriate Charts or last Chart.
	 * 
	 * 3. If the Question is submitted for the Second Batch do the following:
	 * a> If @param q does not specify any answeringDate 
	 * (q.answeringDate == null) then beginning from first Chart for the 
	 * group, find if @param q could fit into the Chart using algorithm 
	 * "ADD TO CHART IF APPLICABLE" as mentioned in addToChartIfApplicable/3. 
	 * Stop when @param q is successfully added to some Chart or when all 
	 * Charts are exhausted.
	 * 
	 * b> If @param q specifies an answeringDate then beginning from Chart 
	 * with answeringDate == q.answeringDate, find if @param q could fit 
	 * into the Chart using algorithm "ADD TO CHART IF APPLICABLE" as 
	 * mentioned in addToChartIfApplicable/3. Stop when @param q is 
	 * successfully added to some Chart or when all Charts are exhausted.
	 *
	 * @param question the Question
	 * @return the Boolean
	 * @throws ELSException
	 */
	private static Boolean addToChartUH(
			final Question question) throws ELSException {
		if(StarredQuestionChart.isAssistantProcessed(question)) {
			if(StarredQuestionChart.isFirstBatchQuestionUH(question)) {
				return StarredQuestionChart.addToChartFirstBatchUH(question);
			}
			else if(StarredQuestionChart.isSecondBatchQuestionUH(question)) {
				return StarredQuestionChart.addToChartSecondBatchUH(question);
			}
		}
		
		return false;
	}

	/**
	 * Checks if @param question is a first batch question.
	 */
	private static Boolean isFirstBatchQuestionUH(final Question question) {
		Session session = question.getSession();
		String locale = question.getLocale();
		
		Date submissionTime = question.getSubmissionDate();
		Date startTime = 
			StarredQuestionChart.getFirstBatchSubmissionStartTime(
					session, locale);
		Date endTime = 
			StarredQuestionChart.getFirstBatchSubmissionEndTime(
					session, locale);
		
		if((submissionTime.compareTo(startTime) >= 0) &&
				(submissionTime.compareTo(endTime) <= 0)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if @param question is a second batch question.
	 */
	private static Boolean isSecondBatchQuestionUH(final Question question) {
		Session session = question.getSession();
		String locale = question.getLocale();
		
		Date submissionTime = question.getSubmissionDate();
		Date startTime = 
			StarredQuestionChart.getSecondBatchSubmissionStartTime(
					session, locale);
		Date endTime = 
			StarredQuestionChart.getSecondBatchSubmissionEndTime(
					session, locale);
		
		if((submissionTime.compareTo(startTime) >= 0) &&
				(submissionTime.compareTo(endTime) <= 0)) {
			return true;
		}
		
		return false;
	}
	
	private static Boolean addToChartFirstBatchUH(
			final Question question) throws ELSException {
		Group group = question.getGroup();
		
		List<Date> answeringDates = new ArrayList<Date>();
		if(question.getAnsweringDate() == null) {
			answeringDates = group.getAnsweringDates(ApplicationConstants.ASC);
		}
		else {
			Date answeringDate = question.getAnsweringDate().getAnsweringDate();
			answeringDates = StarredQuestionChart.getAnsweringDatesGTEQ(group, 
					answeringDate);
		}
		
		Session session = question.getSession();
		DeviceType deviceType = question.getType();
		String locale = question.getLocale();
		
		House house = session.getHouse();
		HouseType houseType = house.getType();
		Integer maxNoOfQuestions = 
			StarredQuestionChart.maxChartedQuestions(houseType);
		
		boolean isDisplacedQn = false;
		Question displacedQn = null;
		
		for(Date d : answeringDates) {
			Chart chart = StarredQuestionChart.find(session, 
					group, d, deviceType, locale);

			if(chart != null) {
				Date answeringDate = chart.getAnsweringDate();
				QuestionDates chartAnsweringDate = 
					group.findQuestionDatesByGroupAndAnsweringDate(
							answeringDate);
				
				boolean isLastAnsweringDate = 
					StarredQuestionChart.isLastAnsweringDate(group, 
							answeringDate);
				boolean isProcessAllRemainingQnsForLastDate =
					StarredQuestionChart
					.isProcessAllRemainingQnsForFirstBatchLastDate(houseType);
				/*
				 * If this Chart is the last Chart and a configuration 
				 * parameter dictates to process all remaining questions 
				 * on last date then directly add the question to the chart.
				 */
				if(isLastAnsweringDate && isProcessAllRemainingQnsForLastDate) {
					/*
					 * @param question can enter this method in 2 cases:
					 * 1. It is a question that has been processed by Assistant.
					 * 2. It is displaced from its Chart by other appropriate 
					 * first batch question. In this case it could be clubbed 
					 * or may have clubbings. If it is a Clubbed question then 
					 * no action is necessary but if it is a parent Question 
					 * then its clubbing should be updated so as not to upset 
					 * the clubbing rule: A Question on (say) 2nd Chart should 
					 * not be a parent of Question on (say) 1st Chart. 
					 */
					Status internalStatus = question.getInternalStatus();
					String internalStatusType = internalStatus.getType();
					
					Status ASSISTANT_PROCESSED = Status.findByType(
							ApplicationConstants
								.QUESTION_SYSTEM_ASSISTANT_PROCESSED, 
							locale);
					Status TO_BE_PUT_UP = Status.findByType(
							ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, 
							locale);
					if(internalStatusType.equals(
							ASSISTANT_PROCESSED.getType())) {
						question.setInternalStatus(TO_BE_PUT_UP);
						question.setRecommendationStatus(TO_BE_PUT_UP);
						question.setChartAnsweringDate(chartAnsweringDate);
						question.simpleMerge();
					}
					else if(internalStatusType.equals(
							TO_BE_PUT_UP.getType())) { // qn could be a parent
						question.setChartAnsweringDate(chartAnsweringDate);
						question.simpleMerge();
						
						/*
						 * If question is a parent then remove it as a parent. 
						 * Select an appropriate kid of the qn as the new 
						 * parent.
						 */
						Question newParent = 
							ClubbedEntity.removeParent(question);
						if(newParent != null){
							newParent.setInternalStatus(TO_BE_PUT_UP);
							newParent.setRecommendationStatus(TO_BE_PUT_UP);
							newParent.simpleMerge();
						}
						
						//Club question with newParent. 
						Question.club(question, newParent, locale);
					}
					
					// Add question to the existing list of charted questions
					Member member = question.getPrimaryMember();
					List<Question> onChartQuestions = 
						StarredQuestionChart.findQuestions(member, session, 
								group, answeringDate, deviceType, locale);
					onChartQuestions.add(question);
					onChartQuestions = 
						StarredQuestionChart.reorderQuestions(onChartQuestions, 
								answeringDate);
					
					// Update the Chart Entry for this Member
					List<Device> devices = new ArrayList<Device>();
					devices.addAll(onChartQuestions);
					ChartEntry ce = StarredQuestionChart.find(
							chart.getChartEntries(), member);
					ce.setDevices(devices);
					ce.merge();

					return true;
				}
				else {
					displacedQn = StarredQuestionChart.addToChartFirstBatchUH(
								chart, question, maxNoOfQuestions);
					if(displacedQn == null) {
						return true;
					}
					else if(! displacedQn.getId().equals(question.getId())) {
						// Break from the loop & addToChartUH the displaced question
						isDisplacedQn = true;
						break;
					}
					else {
						// If displacedQn is same as @param question then @param
						// question is not suitable for this Chart, look for 
						// the next Chart. Hence, do nothing and loop over to
						// the next Chart.
					}
				}
			}
		}
		
		if(isDisplacedQn) {
			StarredQuestionChart.addToChartUH(displacedQn);
		}
		
		return false;
	}

	private static Boolean addToChartSecondBatchUH(
			final Question question) throws ELSException {
		Group group = question.getGroup();
		List<Date> answeringDates = new ArrayList<Date>();
		if(question.getAnsweringDate() == null) {
			answeringDates = group.getAnsweringDates(ApplicationConstants.ASC);
		}
		else {
			Date answeringDate = question.getAnsweringDate().getAnsweringDate();
			answeringDates = StarredQuestionChart.getAnsweringDatesGTEQ(group, 
					answeringDate);
		}
		
		Session session = question.getSession();
		DeviceType deviceType = question.getType();
		String locale = question.getLocale();
		
		House house = session.getHouse();
		HouseType houseType = house.getType();
		Integer maxNoOfQuestions = 
			StarredQuestionChart.maxChartedQuestions(houseType);
		
		for(Date d : answeringDates) {
			Date finalSubmissionDate = group.getFinalSubmissionDate(d);
			Date endTime = StarredQuestionChart.getSecondBatchSubmissionEndTime(
							session, locale);
			int comparisonResult = finalSubmissionDate.compareTo(question.getSubmissionDate());
			int comparisonResult1 = endTime.compareTo(new Date());
			if(comparisonResult>=0 && comparisonResult1>=0){
				Chart chart = StarredQuestionChart.find(session, 
						group, d, deviceType, locale);
				if(chart != null) {
					boolean isAddedToChart = 
						StarredQuestionChart.addToChartIfApplicable(chart, 
								question, maxNoOfQuestions);
					if(isAddedToChart) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * IF @param question is successfully taken on @param chart
	 * and no other question is to be displaced then returns null.
	 * 
	 * IF @param question is successfully taken on @param chart
	 * and a question has to be displaced to other Chart, then 
	 * return the question to be displaced.
	 * 
	 * If @param question cannot be taken on @param chart then 
	 * return @param question.
	 * 
	 * Returns null or question to be displaced.
	 * 
	 * The implementation is similar to addToChartIfApplicable/3.
	 */
	private static Question addToChartFirstBatchUH(
			final Chart chart,
			final Question question,
			final Integer maxNoOfQuestions) throws ELSException {
		Question displacedQn = question;
		
		Member member = question.getPrimaryMember();
		Session session = chart.getSession();
		Group group = chart.getGroup();
		DeviceType deviceType = question.getType();
		Date answeringDate = chart.getAnsweringDate();
		String locale = chart.getLocale();
		
		Status TO_BE_PUT_UP = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, 
				locale);
		Status ASSISTANT_PROCESSED = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, 
				locale);
		QuestionDates chartAnsweringDate = 
			group.findQuestionDatesByGroupAndAnsweringDate(answeringDate);
		
		List<Question> onChartQuestions = 
			StarredQuestionChart.findQuestions(member, session, group,
				answeringDate, deviceType, locale);
		int onChartQuestionsSize = onChartQuestions.size();
		
		if(onChartQuestionsSize == maxNoOfQuestions) {
			List<Question> updatedChartQuestions = new ArrayList<Question>();
			
			// Questions in Workflow will stay unharmed
			List<Question> questionsInWorkflow = 
				StarredQuestionChart.questionsInWorkflow(
						onChartQuestions, locale);
			updatedChartQuestions.addAll(questionsInWorkflow);
			
			//Questions Clubbed will stay unharmed
			List<Question> questionsClubbed = 
					StarredQuestionChart.questionsInClubbingStatus(
							onChartQuestions, locale);
			for(Question q : questionsClubbed){
				if(!questionsInWorkflow.contains(q)){
					updatedChartQuestions.add(q);
				}
			}
			
			// The Questions not in the Workflow will compete for a place 
			// in the Chart
			int requiredQuestions = 
				maxNoOfQuestions - updatedChartQuestions.size();
			List<Question> questionsNotInWorkflow = 
				StarredQuestionChart.questionsNotInWorkflow(
						onChartQuestions, locale);
			questionsNotInWorkflow.add(question);
			
			// The size of candidateQuestions will always be size of 
			// questionsNotInWorkflow + 1 (@param question as provided 
			// in the parameter)
			List<Question> candidateQuestions = 
				StarredQuestionChart.reorderQuestions(questionsNotInWorkflow, 
						answeringDate);
			
			// The nature of candidateQuestions is such that the last 
			// question in the list is the least eligible to be on the
			// Chart and hence, it will leave the Chart.
			int candidateQuestionsSize = candidateQuestions.size();
			for(int i = 0; i < candidateQuestionsSize - 1; i++) {
				Question qn = (Question) candidateQuestions.get(i);
				Status internalStatus = qn.getInternalStatus();
				String internalStatusType = internalStatus.getType();
				
				/*
				 * qn could have been processed by the Assistant OR it
				 * could have been displaced from its Chart by other 
				 * appropriate first batch question. In the 2nd case 
				 * it could be clubbed or may have clubbings. If it is 
				 * a Clubbed question then no action is necessary but 
				 * if it is a parent Question then its clubbing should 
				 * be updated so as not to upset the clubbing rule: A 
				 * Question on (say) 2nd Chart should not be a parent 
				 * of Question on (say) 1st Chart. 
				 */
				if(internalStatusType.equals(ASSISTANT_PROCESSED.getType())) {
					qn.setInternalStatus(TO_BE_PUT_UP);
					qn.setRecommendationStatus(TO_BE_PUT_UP);
					qn.setChartAnsweringDate(chartAnsweringDate);
					qn.simpleMerge();
				}
				else if(internalStatusType.equals(
						TO_BE_PUT_UP.getType())) { // qn could be a parent
					qn.setChartAnsweringDate(chartAnsweringDate);
					qn.simpleMerge();
					
					/*
					 * If qn is a parent then remove it as a parent. 
					 * Select an appropriate kid of the qn as the new 
					 * parent.
					 */
					Question newParent = 
						ClubbedEntity.removeParent(question);
					if(newParent != null){
						newParent.setInternalStatus(TO_BE_PUT_UP);
						newParent.setRecommendationStatus(TO_BE_PUT_UP);
						newParent.simpleMerge();
					}
					
					//Club qn with newParent. 
					if(qn != null && newParent != null){
						Question.club(qn, newParent, locale);
					}
				}
			}
			
			// Update the Chart entry
			if(candidateQuestionsSize >= requiredQuestions) {
				updatedChartQuestions.addAll(
						candidateQuestions.subList(0, requiredQuestions));
			}
			List<Device> devices = new ArrayList<Device>();
			devices.addAll(updatedChartQuestions);
			ChartEntry ce = 
				StarredQuestionChart.find(chart.getChartEntries(), member);
			ce.setDevices(devices);
			ce.merge();
			
			/*
			 * The last Question in the candidateQuestions list is 
			 * leaving the Chart. This is the displaced question.
			 */
			displacedQn = 
				(Question) candidateQuestions.get(candidateQuestionsSize - 1);
			
			/*
			 * Added as a solution to the Ticket Number: 
			 * A question leaving the chart should have status "ASSISTANT_PROCESSED"
			 */
			displacedQn.setChartAnsweringDate(null);
			displacedQn.setInternalStatus(ASSISTANT_PROCESSED);
			displacedQn.setRecommendationStatus(ASSISTANT_PROCESSED);
			displacedQn.simpleMerge();
		}
		else if(onChartQuestionsSize < maxNoOfQuestions) {
			List<Question> updatedChartQuestions = new ArrayList<Question>();
			updatedChartQuestions.addAll(onChartQuestions);
			
			/*
			 * @param questionn could have been processed by the Assistant 
			 * OR it could have been displaced from its Chart by other 
			 * appropriate first batch question. In the 2nd case 
			 * it could be clubbed or may have clubbings. If it is 
			 * a Clubbed question then no action is necessary but 
			 * if it is a parent Question then its clubbing should 
			 * be updated so as not to upset the clubbing rule: A 
			 * Question on (say) 2nd Chart should not be a parent 
			 * of Question on (say) 1st Chart. 
			 */
			Status internalStatus = question.getInternalStatus();
			String internalStatusType = internalStatus.getType();
			if(internalStatusType.equals(ASSISTANT_PROCESSED.getType())) {
				question.setInternalStatus(TO_BE_PUT_UP);
				question.setRecommendationStatus(TO_BE_PUT_UP);
				question.setChartAnsweringDate(chartAnsweringDate);
				question.simpleMerge();
			}
			else if(internalStatusType.equals(
					TO_BE_PUT_UP.getType())) { // question could be a parent
				question.setChartAnsweringDate(chartAnsweringDate);
				question.simpleMerge();
				
				/*
				 * If question is a parent then remove it as a parent. 
				 * Select an appropriate kid of the question as the new 
				 * parent.
				 */
				Question newParent = 
					ClubbedEntity.removeParent(question);
				if(newParent != null){
					newParent.setInternalStatus(TO_BE_PUT_UP);
					newParent.setRecommendationStatus(TO_BE_PUT_UP);
					newParent.simpleMerge();
				}
				
				//Club question with newParent. 
				Question.club(question, newParent, locale);
			}
			
			// Add the @param question to the Chart
			updatedChartQuestions.add(question);
			updatedChartQuestions = 
				StarredQuestionChart.reorderQuestions(updatedChartQuestions, 
						answeringDate);
			
			// Update the Chart Entry.
			List<Device> devices = new ArrayList<Device>();
			devices.addAll(updatedChartQuestions);
			ChartEntry ce = 
				StarredQuestionChart.find(chart.getChartEntries(), member);
			ce.setDevices(devices);
			ce.merge();
			
			//No more questions to displace.
			displacedQn = null;
		}
		
		return displacedQn;
	}
	
	//"GROUP CHANGE" supporting methods
	/**
	 * There are 2 groups involved in this transaction:
	 * 1. SOURCE_GROUP: Group from which @param question has been 
	 * transferred (@param fromGroup)
	 * 
	 * 2. TARGET_GROUP: Group to which @param question has been 
	 * transferred (@param question.group)
	 * 
	 * @param question could be STANDALONE, PARENT, or KID.
	 * 1. IF @param question is KID
	 * 	As per the contract with Question.onGroupChange/2, this 
	 *  case will never arise. Hence, raise an Exception.
	 * 
	 * 2. IF @param question is STANDALONE AND @param question belongs to
	 * First Batch.
	 * 	A. IF @param isForceAddToTargetGroupChart == TRUE then remove 
	 *     @param question from SOURCE_GROUP chart and forcefully 
	 * 	   add it to TARGET_GROUP chart.
	 *     
	 *	B. IF @param isForceAddToTargetGroupChart == FALSE then remove 
	 *     @param question from SOURCE_GROUP chart.
	 *     
	 * 3. IF @param question is STANDALONE AND @param question belongs to
	 * Second Batch.
	 * 	A. IF @param isForceAddToTargetGroupChart == TRUE then remove 
	 *     @param question from SOURCE_GROUP chart and forcefully 
	 * 	   add it to TARGET_GROUP chart. In lieu of @param question 
	 *     leaving SOURCE_GROUP chart, find if another eligible Question 
	 *     could be added to the SOURCE_GROUP Chart.
	 *     
	 *	B. IF @param isForceAddToTargetGroupChart == FALSE then remove 
	 *     @param question from SOURCE_GROUP chart. In lieu of 
	 *     @param question leaving SOURCE_GROUP chart, find if another 
	 *     eligible Question could be added to the SOURCE_GROUP Chart.
	 * 
	 * 4. IF @param question is PARENT AND @param question belongs to
	 * First Batch.
	 * 	A. IF @param isForceAddToTargetGroupChart == TRUE then remove
	 * 	   @param question as well as its kids from their respective
	 * 	   SOURCE_GROUP charts. Forcefully add @param question and all
	 * 	   its kids to the same TARGET_GROUP chart.
	 * 
	 * 	B. IF @param isForceAddToTargetGroupChart == FALSE
	 * 	   	As per the contract with Question.onGroupChange/2, this case 
	 * 	    will never arise. Hence, raise an Exception.
	 * 
	 * 5. IF @param question is PARENT AND @param question belongs to
	 * Second Batch.
	 * 	A. IF @param isForceAddToTargetGroupChart == TRUE then remove
	 * 	   @param question as well as its kids from their respective
	 * 	   SOURCE_GROUP charts. Forcefully add @param question and all
	 * 	   its kids to the same TARGET_GROUP chart. For each question
	 *     leaving SOURCE_GROUP chart, find if another eligible Question 
	 *     could be added to the SOURCE_GROUP Chart.
	 * 
	 * 	B. IF @param isForceAddToTargetGroupChart == FALSE
	 * 	   	As per the contract with Question.onGroupChange/2, this case 
	 * 	    will never arise. Hence, raise an Exception.
	 * 
	 * Handle the Case: 2 week session. 1 Dec to 14 Dec. 1 Dec is Sunday. 
	 * Group 1: 2, 9 December (Monday)
	 * Group 2: 3, 10 December (Tuesday)
	 * Group 3: 4, 11 December (Wednesday)
	 * Group 4: 5, 12 December (Thursday)
	 * Group 5: 6, 13 December (Friday)
	 * A 13th December Question group changes to Group 2. 
	 * 	IF @param isForceAddToTargetGroupChart == TRUE then the
	 * 	Question should forcefully go on 10th December Chart.
	 */
	private static void groupChangeUH(final Question question, 
			final Group fromGroup,
			final boolean isForceAddToTargetGroupChart) throws ELSException {
		Group sourceGroup = fromGroup;
		Group targetGroup = question.getGroup();
		
		CLUBBING_STATE clubbingState = Question.findClubbingState(question);
		if(clubbingState == CLUBBING_STATE.STANDALONE) {
			StarredQuestionChart.groupChangeOfStandaloneQnUH(question, 
					sourceGroup, targetGroup, isForceAddToTargetGroupChart);
		}
		else if(clubbingState == CLUBBING_STATE.PARENT) {
			StarredQuestionChart.groupChangeOfParentQnUH(question, 
					sourceGroup, targetGroup, isForceAddToTargetGroupChart);
		}
		else { // clubbingState == CLUBBING_STATE.CLUBBED
			throw new ELSException("StarredQuestionChart.groupChangeUH/3", 
				"Method invoked for clubbed entity breaking the" +
				" contract between Question & Chart.");
		}
	}
	
	private static void groupChangeOfStandaloneQnUH(final Question question,
			final Group sourceGroup, 
			final Group targetGroup,
			final boolean isForceAddToTargetGroupChart) throws ELSException {
		Chart sourceChart = null;
		
		boolean isFirstBatchQuestion = 
			StarredQuestionChart.isFirstBatchQuestionUH(question);
		if(isFirstBatchQuestion) { // First Batch Question
			sourceChart = 
				StarredQuestionChart.processSourceGroupChartSecondBatchUH(
						question, sourceGroup);
		}
		else { // Second Batch Question
			sourceChart = 
				StarredQuestionChart.processSourceGroupChartSecondBatchUH(question, 
						sourceGroup);
		}
		
		if(sourceChart != null && isForceAddToTargetGroupChart) {
			Chart targetChart = 
				StarredQuestionChart.findTargetChart(sourceChart, targetGroup);
			
			if(targetChart != null) {
				StarredQuestionChart.processTargetGroupChart(question, 
						targetChart);
			}
		}
	}
	
	private static void groupChangeOfParentQnUH(final Question question,
			final Group sourceGroup, 
			final Group targetGroup,
			final boolean isForceAddToTargetGroupChart) throws ELSException {
		if(isForceAddToTargetGroupChart) {
			List<Question> kids = StarredQuestionChart.findClubbings(question);
			
			// Apply source group chart processing to parent
			Chart sourceChart = null;
			boolean isFirstBatchQuestion = 
				StarredQuestionChart.isFirstBatchQuestionUH(question);
			if(isFirstBatchQuestion) { // First Batch Question
				sourceChart = 
					StarredQuestionChart.processSourceGroupChartSecondBatchUH(
							question, sourceGroup);
			}
			else { // Second Batch Question
				sourceChart = 
					StarredQuestionChart.processSourceGroupChartSecondBatchUH(question, 
							sourceGroup);
			}
			
			// Apply source group chart processing to kids
			for(Question kid : kids) {
				boolean isKidFirstBatchQuestion =
					StarredQuestionChart.isFirstBatchQuestionUH(kid);
				if(isKidFirstBatchQuestion) { // First Batch Question
					StarredQuestionChart.processSourceGroupChartSecondBatchUH(
							kid, sourceGroup);
				}
				else { // Second Batch Question
					StarredQuestionChart.processSourceGroupChartSecondBatchUH(kid, 
							sourceGroup);
				}
			}
			
			if(sourceChart != null) {
				// Determine the target chart
				Chart targetChart = 
					StarredQuestionChart.findTargetChart(sourceChart, 
							targetGroup);
				
				// Kids go on the same Chart as parent
				if(targetChart != null) {
					// Apply target group chart processing to parent
					StarredQuestionChart.processTargetGroupChart(question, 
							targetChart);
					
					// Apply target group chart processing to kids
					for(Question kid : kids) {
						StarredQuestionChart.processTargetGroupChart(kid, 
								targetChart);
					}
				}
			}
			
		}
		else {
			throw new ELSException(
					"StarredQuestionChart.groupChangeOfParentQnUH/4", 
					"Method invoked on parent with @param " +
					" isForceAddToTargetGroupChart as false, breaking the" +
					" contract between Question & Chart.");
		}
	}
	
	/**
	 * 1. Remove @param question from its Chart.
	 * 2. Question removed from the Chart has its chartAnsweringDate == null.
	 * 
	 * Returns the processed source group Chart.
	 */
	private static Chart processSourceGroupChartFirstBatchUH(
			final Question question,
			final Group sourceGroup) throws ELSException {
		Chart chart = Chart.find(question);
		
		if(chart != null) {
			Member member = question.getPrimaryMember();
			ChartEntry ce = 
				StarredQuestionChart.find(chart.getChartEntries(), member);
			List<Device> devices = StarredQuestionChart.findDevices(ce);//ce.getDevices();
			
			/*
			 * 1. Remove @param question from the Chart
			 */
			int index = -1;
			for(Device d : devices) {
				++index;
				if(d.getId().equals(question.getId())) {
					break;
				}
			}
			devices.remove(index);

			ce.setDevices(devices);
			ce.merge();
			
			/*
			 * 2. Set @param question's chartAnsweringDate as null
			 */
			question.setChartAnsweringDate(null);
			question.simpleMerge();
		}
		
		return chart;
	}
	
	private static Chart processSourceGroupChartUH(
			final Question question,
			final Group sourceGroup) throws ELSException {
		return StarredQuestionChart.processSourceGroupChartCommon(question, sourceGroup);
	}
	
	private static Chart processSourceGroupChartCommon(
			final Question question,
			final Group sourceGroup) throws ELSException {
		Chart chart = Chart.find(question);
		
		if(chart != null) {
			Member member = question.getPrimaryMember();
			ChartEntry ce = 
				StarredQuestionChart.find(chart.getChartEntries(), member);
			List<Device> devices = StarredQuestionChart.findDevices(ce);//ce.getDevices();
			
			/*
			 * 1. Remove @param question from the Chart
			 */
			int index = -1;
			for(Device d : devices) {
				++index;
				if(d.getId().equals(question.getId())) {
					break;
				}
			}
			devices.remove(index);

			ce.setDevices(devices);
			ce.merge();
			
			/*
			 * 2. Set @param question's chartAnsweringDate as null
			 */
			question.setChartAnsweringDate(null);
			question.simpleMerge();
		}
		
		return chart;
	}
	
	private static void shiftChartQuestionsRecursive(final Question question, final Chart chart, boolean isRemovalFromChartNeeded, final String locale) throws ELSException {
		//Date answeringDate = chart.getAnsweringDate();			
		ChartEntry ce = 
			StarredQuestionChart.find(chart.getChartEntries(), question.getPrimaryMember());
		List<Device> devices = StarredQuestionChart.findDevices(ce);//ce.getDevices();
		
		if(isRemovalFromChartNeeded) {
			/*
			 * 1. Remove @param question from the Chart
			 */
			int index = -1;
			for(Device d : devices) {
				++index;
				if(d.getId().equals(question.getId())) {
					break;
				}
			}
			devices.remove(index);
			
			/*
			 * 2. Set chartAnsweringDate attribute of @param question as null
			 */
			question.setChartAnsweringDate(null);
			question.simpleMerge();
		}
		
		String chartQuestionDetails = Chart.findNextEligibleChartQuestionDetailsOnGroupChange(chart, question.getPrimaryMember());
		if(chartQuestionDetails!=null && !chartQuestionDetails.isEmpty()) {
			Chart eligibleChart = Chart.findById(Chart.class, new Long(chartQuestionDetails.split("~")[0]));
			Question eligibleQuestion = Question.findById(Question.class, new Long(chartQuestionDetails.split("~")[1]));
			ChartEntry eligibleChartEntry = 
					StarredQuestionChart.find(eligibleChart.getChartEntries(), question.getPrimaryMember());
			List<Device> eligibleChartDevices = StarredQuestionChart.findDevices(eligibleChartEntry);//ce.getDevices();
			
			/*
			 * 1. Remove @param question from the Chart
			 */
			int index = -1;
			for(Device d : eligibleChartDevices) {
				++index;
				if(d.getId().equals(eligibleQuestion.getId())) {
					break;
				}
			}
			eligibleChartDevices.remove(index);
			
			/*
			 * 2. Set chartAnsweringDate attribute of @param question as null
			 */
			eligibleQuestion.setChartAnsweringDate(null);
			
			/*
			 * 3. Reorder the devices and then add them to the Chart
			 */
			List<Question> eligibleChartQuestions = 
				StarredQuestionChart.marshallDevices(eligibleChartDevices);
			List<Question> eligibleChartReorderedQuestions = 
				StarredQuestionChart.reorderQuestions(eligibleChartQuestions, eligibleChart.getAnsweringDate());
			List<Device> eligibleChartReorderedDevices =
				StarredQuestionChart.marshallQuestions(eligibleChartReorderedQuestions);
			eligibleChartEntry.setDevices(eligibleChartReorderedDevices);
			eligibleChartEntry.merge();
			
			/*
			 * 4. add the question
			 * to the required previous Chart.
			 */
			// The Questions taken on the Chart should have status 
			// "TO_BE_PUT_UP"
			Status TO_BE_PUT_UP = Status.findByType(
					ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, 
					locale);
			QuestionDates chartAnsweringDate = 
				chart.getGroup().findQuestionDatesByGroupAndAnsweringDate(
						chart.getAnsweringDate());
			
			eligibleQuestion.setInternalStatus(TO_BE_PUT_UP);
			eligibleQuestion.setRecommendationStatus(TO_BE_PUT_UP);
			eligibleQuestion.setChartAnsweringDate(chartAnsweringDate);
			eligibleQuestion.simpleMerge();
			
			devices.add(eligibleQuestion);
			
			/*
			 * 4. Reorder the devices and then add them to the Chart
			 */
			List<Question> questions = 
				StarredQuestionChart.marshallDevices(devices);
			List<Question> reorderedQuestions = 
				StarredQuestionChart.reorderQuestions(questions, chart.getAnsweringDate());
			List<Device> reorderedDevices =
				StarredQuestionChart.marshallQuestions(reorderedQuestions);
			ce.setDevices(reorderedDevices);
			ce.merge();
			
			shiftChartQuestionsRecursive(question, eligibleChart, false, locale);
			
		} else { //add fresh newly assistant processed question if found
			Session session = question.getSession();
			String submissionEndDate = session.getParameter("questions_starred_submissionEndDate");
			Date lastSubmissionDate = FormaterUtil.formatStringToDate(submissionEndDate, ApplicationConstants.DB_DATETIME_FORMAT, locale);
			
			int dateComparator = -1;
			Date finalSubmissionDate = chart.getGroup().getFinalSubmissionDate(chart.getAnsweringDate());
			if(question.getSubmissionDate() != null){
				dateComparator = finalSubmissionDate.compareTo(question.getSubmissionDate());
			}else{
				dateComparator = finalSubmissionDate.compareTo(new Date());
			}			
			if(dateComparator>=0 && lastSubmissionDate.compareTo(new Date())>=0){
				Question q = StarredQuestionChart.onGroupChangeAddQuestionLH(
						session, question.getPrimaryMember(), chart.getGroup(), chart.getAnsweringDate(),
						devices.toArray(new Question[0]), locale);
				if(q != null) {
					// The Questions taken on the Chart should have status 
					// "TO_BE_PUT_UP"
					Status TO_BE_PUT_UP = Status.findByType(
							ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, 
							locale);
					QuestionDates chartAnsweringDate = 
						chart.getGroup().findQuestionDatesByGroupAndAnsweringDate(
								chart.getAnsweringDate());
					
					q.setInternalStatus(TO_BE_PUT_UP);
					q.setRecommendationStatus(TO_BE_PUT_UP);
					q.setChartAnsweringDate(chartAnsweringDate);
					q.simpleMerge();
					
					devices.add(q);
				}
			}
			
			
			/*
			 * Reorder the devices and then add them to the Chart
			 */
			List<Question> questions = 
				StarredQuestionChart.marshallDevices(devices);
			List<Question> reorderedQuestions = 
				StarredQuestionChart.reorderQuestions(questions, chart.getAnsweringDate());
			List<Device> reorderedDevices =
				StarredQuestionChart.marshallQuestions(reorderedQuestions);
			ce.setDevices(reorderedDevices);
			ce.merge();
		}
	}
	
	/**
	 * 1. Remove @param question from its Chart.
	 * 2. Whenever a Question leaves the Chart its chartAnsweringDate 
	 * attribute is set as null.
	 * 3. Since 1 question has left the Chart, find an eligible question
	 * and add it to the Chart.
	 * 4. Reorder the devices in the ChartEntry.
	 * 
	 * Returns the processed source group Chart.
	 */
	private static Chart processSourceGroupChartSecondBatchUH(final Question question,
			final Group sourceGroup) throws ELSException {
		Chart chart = Chart.find(question);
		if(chart != null) {
			String locale = chart.getLocale();
			Session session = question.getSession();
			String submissionEndDate = session.getParameter("questions_starred_submissionEndDate");
			Date lastSubmissionDate = FormaterUtil.formatStringToDate(submissionEndDate, ApplicationConstants.DB_DATETIME_FORMAT, locale);
			// As the shifting of chart questions can be done only till the last submission date, post group change
			// only the device will be removed from the chart without shifting.
			if(lastSubmissionDate.compareTo(new Date())>=0){
				shiftChartQuestionsRecursive(question, chart, true, locale);
			}else{
				processSourceGroupChartUH(question, sourceGroup);
			}
		}
		
		return chart;
	}
	

	//=================================================
	//
	//=========== LOWERHOUSE MODE METHODS =============
	//
	//=================================================
	// "CREATE CHART" SUPPORTING METHODS 
	/**
	 * Creates a new Chart. 
	 * 
	 * If the Chart already exists then returns the existing Chart. 
	 * If a previous dated Chart exists & is unprocessed
	 * then don't create a new Chart and return null.
	 * @throws ELSException
	 */
	private static Chart createLH(Chart chart) throws ELSException {
		Chart newChart = Chart.find(chart);
		
		if(newChart == null) {
			boolean isPreviousChartExists = 
				StarredQuestionChart.isPreviousChartExists(chart);
			boolean isPreviousChartProcessed = 
				StarredQuestionChart.isPreviousChartProcessed(chart);
			
			if(isPreviousChartExists == false
					|| isPreviousChartProcessed == true) {
				newChart = StarredQuestionChart.createChartLH(chart);
				StarredQuestionChart.updateChart(newChart);
			}
			else {
				return null;
			}
		}
		
		return newChart;
	}
	
	private static Chart createChartLH(
			final Chart chart) throws ELSException {
		Session session = chart.getSession();
		Group group = chart.getGroup();
		DeviceType deviceType = chart.getDeviceType();
		Date answeringDate = chart.getAnsweringDate();
		String locale = chart.getLocale();
		
		// Determine maximum questions to be taken on the Chart.
		// At the most these many questions will be taken on the
		// Chart for each member.
		House house = session.getHouse();
		HouseType houseType = house.getType();
		Integer maxQuestionsOnChart = 
			StarredQuestionChart.maxChartedQuestions(houseType);
		
		// Determine submission start time & submission end time for 
		// @param chart. Note that submissionEndTime represents the 
		// time before which a Question must have been submitted in 
		// order to consider it for Chart.
		Date submissionStartTime = 
			StarredQuestionChart.getSubmissionStartTime(session, 
					deviceType, locale);
		Date submissionEndTime = 
			StarredQuestionChart.getSubmissionEndTime(group, answeringDate);
		
		// Determine the internal statuses which forms the pre-requisite
		// for taking a Question on the Chart.
		Status ASSISTANT_PROCESSED = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, 
				locale);
		Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
		
		// Create chart entries for Members with Questions
		List<ChartEntry> entriesForMembersWithQuestion = 
			StarredQuestionChart.chartEntriesForMembersWithQuestionLH(session, 
					deviceType, group, answeringDate, 
					submissionStartTime, submissionEndTime, 
					maxQuestionsOnChart, internalStatuses, 
					ApplicationConstants.ASC, locale);
		
		// Create chart entries for Members without Questions
		List<ChartEntry> entriesForMembersWithoutQuestion = 
			StarredQuestionChart.chartEntriesForMembersWithoutQuestionLH(
					session, deviceType, group, answeringDate, 
					submissionStartTime, submissionEndTime, 
					internalStatuses, ApplicationConstants.ASC, locale);
		
		chart.getChartEntries().addAll(entriesForMembersWithQuestion);
		chart.getChartEntries().addAll(entriesForMembersWithoutQuestion);
		
		return (Chart) chart.persist();
	}
	
	private static List<ChartEntry> chartEntriesForMembersWithQuestionLH(
			final Session session, 
			final DeviceType deviceType, 
			final Group group,
			final Date answeringDate, 
			final Date startTime, 
			final Date endTime,
			final Integer maxQuestionsOnChart,
			final Status[] internalStatuses, 
			final String sortOrder, 
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		Date currentDate = StarredQuestionChart.getCurrentDate();
		List<Member> activeMembersWithQuestions = 
			Question.findActiveMembersWithQuestions(session, 
					currentDate, deviceType, group, internalStatuses, 
					answeringDate,	startTime, endTime, 
					sortOrder, locale);
		for(Member m : activeMembersWithQuestions) {
			ChartEntry chartEntry = 
				StarredQuestionChart.newChartEntryLH(session, m, deviceType, 
					group, answeringDate, endTime, maxQuestionsOnChart, 
					internalStatuses, locale);
			chartEntries.add(chartEntry);
		}
		
		return chartEntries;
	}
	
	private static List<ChartEntry> chartEntriesForMembersWithoutQuestionLH(
			final Session session, 
			final DeviceType deviceType, 
			final Group group,
			final Date answeringDate, 
			final Date startTime, 
			final Date endTime,
			final Status[] internalStatuses, 
			final String sortOrder, 
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		Date currentDate = StarredQuestionChart.getCurrentDate();
		List<Member> activeMembersWithoutQuestions = 
			Question.findActiveMembersWithoutQuestions(session, currentDate, 
					deviceType, group, internalStatuses, 
					answeringDate, startTime, endTime, 
					sortOrder, locale);
		for(Member m : activeMembersWithoutQuestions) {
			ChartEntry chartEntry = 
				StarredQuestionChart.newEmptyChartEntry(m, locale);
			chartEntries.add(chartEntry);
		}
		
		return chartEntries;
	}
	
	/**
	 * Use this Charting algorithm when creating a ChartEntry (for the first
	 * time).
	 * 
	 * Search for at most @param maxQuestionsOnChart according to the following
	 * algorithm. Search only for those Questions which are submitted prior
	 * to the finalSubmissionDate and are verified by the assistant
	 * ("ASSISTANT_PROCESSED")
	 * 
	 * 1. Select the Questions which have the answeringDate attribute
	 * explicitly set to the expected answeringDate.
	 * 
	 * 2. Select the Questions which have an answeringDate attribute
	 * explicitly set to a date before the expected answeringDate.
	 * 
	 * 3. Select the Questions which don't have any answeringDate.
	 * 
	 * Returns an empty list if there are no Questions.
	 */
	private static ChartEntry newChartEntryLH(final Session session, 
			final Member member,
			final DeviceType deviceType,
			final Group group, 
			final Date answeringDate,
			final Date finalSubmissionDate,
			final Integer maxQuestionsOnChart,
			final Status[] internalStatuses,
			final String locale) {
		List<Device> candidateQList = new ArrayList<Device>();
		
		int maxQ = maxQuestionsOnChart;
		
		// List of Questions having an explicit answeringDate attribute set
		// to current date or previous answering date(s)
		if(maxQ > 0) {
			List<Question> questions = 
				Question.findDatedQuestions(session, member, deviceType, 
					group, answeringDate, finalSubmissionDate, 
					internalStatuses, maxQ, locale);
			
			candidateQList.addAll(questions);
			maxQ = maxQ - questions.size();
		}
		
		// List of Questions without any answering date
		if(maxQ > 0) {
			List<Question> questions = 
				Question.findNonAnsweringDate(session, member, deviceType, 
					group, finalSubmissionDate, internalStatuses, maxQ, 
					ApplicationConstants.ASC, locale);
			
			candidateQList.addAll(questions);
		}
		
		ChartEntry chartEntry = new ChartEntry(member, candidateQList, locale);
		return chartEntry;
	}	
	
	// "ADD TO CHART" SUPPORTING METHODS
	/**
	 * Algorithm:
	 * 1. Is Question's internalStatus == "ASSISTANT PROCESSED"?
	 * 2. Is latest chart exists?
	 * 3. Is Question eligible to be added to the Chart?
	 * If answer to all 1, 2, 3 is YES then proceed to Step 4
	 * 4. Use the algorithm "ADD TO CHART IF APPLICABLE" as mentioned
	 * in addToChartIfApplicable/3
	 * @throws ELSException 
	 */
	private static Boolean addToChartLH(
			final Question question) throws ELSException{
		boolean isAssistantProcessed = 
			StarredQuestionChart.isAssistantProcessed(question);
		
		if(isAssistantProcessed) {
			CustomParameter csptAllowQuestionAcrossAllCharts = CustomParameter.findByName(CustomParameter.class, "ALLOW_QUESTION_ACROSS_ALL_CHARTS_LOWERHOUSE", "");
			if(csptAllowQuestionAcrossAllCharts!=null 
					&& csptAllowQuestionAcrossAllCharts.getValue()!=null
					&& csptAllowQuestionAcrossAllCharts.getValue().equalsIgnoreCase("YES")) { //special case when question can be putup on previous chart than latest
				Group group = question.getGroup();
				List<Date> answeringDates = new ArrayList<Date>();
				if(question.getAnsweringDate() == null) {
					answeringDates = group.getAnsweringDates(ApplicationConstants.ASC);
				}
				else {
					Date answeringDate = question.getAnsweringDate().getAnsweringDate();
					answeringDates = StarredQuestionChart.getAnsweringDatesGTEQ(group, 
							answeringDate);
				}
				
				Session session = question.getSession();
				DeviceType deviceType = question.getType();
				String locale = question.getLocale();
				
				House house = session.getHouse();
				HouseType houseType = house.getType();
				Integer maxNoOfQuestions = 
					StarredQuestionChart.maxChartedQuestions(houseType);
				
				for(Date d : answeringDates) {
					Date finalSubmissionDate = group.getFinalSubmissionDate(d);
					Date endTime = StarredQuestionChart.getSubmissionEndTime(
									session, locale);
					int comparisonResult = finalSubmissionDate.compareTo(question.getSubmissionDate());
					int comparisonResult1 = endTime.compareTo(new Date());
					if(comparisonResult>=0 && comparisonResult1>=0){
						Chart chart = StarredQuestionChart.find(session, 
								group, d, deviceType, locale);
						if(chart != null) {
							boolean isAddedToChart = 
								StarredQuestionChart.addToChartIfApplicable(chart, 
										question, maxNoOfQuestions);
							if(isAddedToChart) {
								return true;
							}
						}
					}
				}
			} else {
				Session session = question.getSession();
				Group group = question.getGroup();
				DeviceType deviceType = question.getType();
				String locale = question.getLocale();
				
				Chart latestChart = 
					StarredQuestionChart.findLatestChart(session, group, 
							deviceType, locale);
				
				if(latestChart != null) {
					boolean isEligibleForChart = 
						StarredQuestionChart.isEligibleForChartLH(latestChart, 
								question);
					
					if(isEligibleForChart) {
						House house = session.getHouse();
						HouseType houseType = house.getType();
						
						Integer maxQnsOnChart = 
							StarredQuestionChart.maxChartedQuestions(houseType);
						return StarredQuestionChart.addToChartIfApplicable(
								latestChart, question, maxQnsOnChart);
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 1. Is Question.submissionDate <= chart.finalSubmissionDate?
	 * 2. Is Question.answeringDate == null OR
	 * 	Question.answeringDate == LatestChart.answeringDate OR
	 * 	Question.answeringDate < LatestChart.answeringDate? (Case 
	 *  of Group Change)
	 * 3. If the answer to 1 and 2 is YES then return true
	 */
	private static Boolean isEligibleForChartLH(final Chart chart, 
			final Question q) {
		// Condition 1
		Date questionSubmissionDate = q.getSubmissionDate();
		
		Group group = q.getGroup();
		Date answeringDate = chart.getAnsweringDate();
		Date finalSubmissionDate = group.getFinalSubmissionDate(answeringDate);
		
		int questionSubmittedBeforeFinal = 
			questionSubmissionDate.compareTo(finalSubmissionDate);
		
		// Condition 2
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
		
		Date submissionStartTime = 
			StarredQuestionChart.getSubmissionStartTime(session, 
					deviceType, locale);
		
		int questionSubmittedAfterStartTime = 
			questionSubmissionDate.compareTo(submissionStartTime);
		
		if(questionSubmittedBeforeFinal <= 0 
				&& questionSubmittedAfterStartTime >= 0) {
			QuestionDates questionAnsweringDate = q.getAnsweringDate();
			if(questionAnsweringDate == null) {
				return true;
			}
			else {
				if(questionAnsweringDate.getAnsweringDate().compareTo(
						answeringDate) <= 0) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	//"GROUP CHANGE" supporting methods
	/**
	 * There are 2 groups involved in this transaction:
	 * 1. SOURCE_GROUP: Group from which @param question has been 
	 * transferred (@param fromGroup)
	 * 
	 * 2. TARGET_GROUP: Group to which @param question has been 
	 * transferred (@param question.group)
	 * 
	 * @param question could be STANDALONE, PARENT, or KID.
	 * 1. IF @param question is KID
	 * 	As per the contract with Question.onGroupChange/2, this 
	 *  case will never arise. Hence, raise an Exception.
	 * 
	 * 2. IF @param question is STANDALONE
	 * 	A. IF @param isForceAddToTargetGroupChart == TRUE then remove 
	 *     @param question from SOURCE_GROUP chart and forcefully 
	 * 	   add it to TARGET_GROUP chart. In lieu of @param question 
	 *     leaving SOURCE_GROUP chart, find if another eligible Question 
	 *     could be added to the SOURCE_GROUP Chart.
	 *     
	 *	B. IF @param isForceAddToTargetGroupChart == FALSE then remove 
	 *     @param question from SOURCE_GROUP chart. In lieu of 
	 *     @param question leaving SOURCE_GROUP chart, find if another 
	 *     eligible Question could be added to the SOURCE_GROUP Chart.
	 * 
	 * 3. IF @param question is PARENT
	 * 	A. IF @param isForceAddToTargetGroupChart == TRUE then remove
	 * 	   @param question as well as its kids from their respective
	 * 	   SOURCE_GROUP charts. Forcefully add @param question and all
	 * 	   its kids to the same TARGET_GROUP chart. For each question
	 *     leaving SOURCE_GROUP chart, find if another eligible Question 
	 *     could be added to the SOURCE_GROUP Chart.
	 * 
	 * 	B. IF @param isForceAddToTargetGroupChart == FALSE
	 * 	   	As per the contract with Question.onGroupChange/2, this case 
	 * 	    will never arise. Hence, raise an Exception.
	 * 
	 * Handle the Case: 2 week session. 1 Dec to 14 Dec. 1 Dec is Sunday. 
	 * Group 1: 2, 9 December (Monday)
	 * Group 2: 3, 10 December (Tuesday)
	 * Group 3: 4, 11 December (Wednesday)
	 * Group 4: 5, 12 December (Thursday)
	 * Group 5: 6, 13 December (Friday)
	 * A 13th December Question group changes to Group 2. 
	 * 	IF @param isForceAddToTargetGroupChart == TRUE then the
	 * 	Question should forcefully go on 10th December Chart.
	 */
	private static void groupChangeLH(final Question question, 
			final Group fromGroup,
			final boolean isForceAddToTargetGroupChart) throws ELSException {
		Group sourceGroup = fromGroup;
		Group targetGroup = question.getGroup();
		
		CLUBBING_STATE clubbingState = Question.findClubbingState(question);
		if(clubbingState == CLUBBING_STATE.STANDALONE) {
			StarredQuestionChart.groupChangeOfStandaloneQnLH(question, 
					sourceGroup, targetGroup, isForceAddToTargetGroupChart);
		}
		else if(clubbingState == CLUBBING_STATE.PARENT) {
			StarredQuestionChart.groupChangeOfParentQnLH(question, 
					sourceGroup, targetGroup, isForceAddToTargetGroupChart);
		}
		else { // clubbingState == CLUBBING_STATE.CLUBBED
			throw new ELSException("StarredQuestionChart.groupChangeLH/3", 
				"Method invoked for clubbed entity breaking the" +
				" contract between Question & Chart.");
		}
	}
	
	private static void groupChangeOfStandaloneQnLH(final Question question,
			final Group sourceGroup,
			final Group targetGroup,
			final boolean isForceAddToTargetGroupChart) throws ELSException {
		Chart sourceChart = null;
		CustomParameter csptAllowQuestionAcrossAllCharts = CustomParameter.findByName(CustomParameter.class, "ALLOW_QUESTION_ACROSS_ALL_CHARTS_LOWERHOUSE", "");
		if(csptAllowQuestionAcrossAllCharts!=null 
				&& csptAllowQuestionAcrossAllCharts.getValue()!=null
				&& csptAllowQuestionAcrossAllCharts.getValue().equalsIgnoreCase("YES")) { //special case when question can be putup on previous chart than latest
			sourceChart = StarredQuestionChart.processSourceGroupChart(question, sourceGroup);
		} else {
			sourceChart = StarredQuestionChart.processSourceGroupChartLH(question, sourceGroup);
		}		
		
		if(sourceChart != null && isForceAddToTargetGroupChart) {
			Chart targetChart = 
				StarredQuestionChart.findTargetChart(sourceChart, 
						targetGroup);
			
			if(targetChart != null) {
				StarredQuestionChart.processTargetGroupChart(question, 
						targetChart);
			}
		}
	}

	private static void groupChangeOfParentQnLH(final Question question,
			final Group sourceGroup,
			final Group targetGroup,
			final boolean isForceAddToTargetGroupChart) throws ELSException {
		if(isForceAddToTargetGroupChart) {
			List<Question> kids = StarredQuestionChart.findClubbings(question);
			
			// Apply source group chart processing to parent
			Chart sourceChart = null;
			CustomParameter csptAllowQuestionAcrossAllCharts = CustomParameter.findByName(CustomParameter.class, "ALLOW_QUESTION_ACROSS_ALL_CHARTS_LOWERHOUSE", "");
			if(csptAllowQuestionAcrossAllCharts!=null 
					&& csptAllowQuestionAcrossAllCharts.getValue()!=null
					&& csptAllowQuestionAcrossAllCharts.getValue().equalsIgnoreCase("YES")) { //special case when question can be putup on previous chart than latest
				sourceChart = StarredQuestionChart.processSourceGroupChart(question, sourceGroup);
			} else {
				sourceChart = StarredQuestionChart.processSourceGroupChartLH(question, sourceGroup);
			}
			
			// Apply source group chart processing to kids
			for(Question kid : kids) {
				StarredQuestionChart.processSourceGroupChart(kid, 
						sourceGroup);
			}
			
			if(sourceChart != null) {
				// Determine the target chart
				Chart targetChart = 
					StarredQuestionChart.findTargetChart(sourceChart, 
							targetGroup);
				
				// Kids go on the same Chart as parent
				if(targetChart != null) {
					// Apply target group chart processing to parent
					StarredQuestionChart.processTargetGroupChart(question, 
							targetChart);
					
					// Apply target group chart processing to kids
					for(Question kid : kids) {
						StarredQuestionChart.processTargetGroupChart(kid, 
								targetChart);
					}
				}
			}
		}
		else {
			throw new ELSException(
					"StarredQuestionChart.groupChangeOfParentQnLH/4", 
					"Method invoked on parent with @param " +
					" isForceAddToTargetGroupChart as false, breaking the" +
					" contract between Question & Chart.");
		}
	}
	
	/**
	 * 1. Consider the Questions with status = "ASSISTANT_PROCESSED" for Chart.
	 * 
	 * 2. Select the Questions which have the answeringDate attribute
	 * explicitly set to the expected answeringDate.
	 * 
	 * 3. Select the Questions which have an answeringDate attribute
	 * explicitly set to a date before the expected answeringDate.
	 * 
	 * 4. Select the Questions which don't have any answeringDate.
	 */
	private static Question onGroupChangeAddQuestionLH(final Session session,
			final Member member, 
			final Group group, 
			final Date answeringDate,
			final Question[] excludeQuestions, 
			final String locale) {
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.STARRED_QUESTION, locale);
		Date finalSubmissionDate = group.getFinalSubmissionDate(answeringDate);
		
		Status ASSISTANT_PROCESSED = 
			Status.findByType(ApplicationConstants
					.QUESTION_SYSTEM_ASSISTANT_PROCESSED, locale);
		Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
		
		// Since 1 question has left the group so add 1 question to the chart. 
		// Hence, maxNoOfQuestions = 1
		int maxNoOfQuestions = 1;
		
		List<Question> datedQuestions = Question.find(session, member, 
				deviceType, group, answeringDate, finalSubmissionDate, 
				internalStatuses, excludeQuestions, maxNoOfQuestions, 
				ApplicationConstants.ASC, locale);
		if(datedQuestions.size() == 1) {
			return datedQuestions.get(0);
		}

		List<Question> previousDatedQuestions = 
			Question.findBeforeAnsweringDate(session, member, 
				deviceType, group, answeringDate, finalSubmissionDate, 
				internalStatuses, excludeQuestions, maxNoOfQuestions, 
				ApplicationConstants.ASC, locale);
		if(previousDatedQuestions.size() == 1) {
			return previousDatedQuestions.get(0);
		}

		List<Question> nonDatedQuestions = Question.findNonAnsweringDate(
				session, member, deviceType, group, finalSubmissionDate, 
				internalStatuses, excludeQuestions, maxNoOfQuestions, 
				ApplicationConstants.ASC, locale);
		if(nonDatedQuestions.size() == 1) {
			return nonDatedQuestions.get(0);
		}

		return null;
	}
	
	
	//=================================================
	//
	//=============== COMMON INTERNAL METHODS =========
	//
	//=================================================
	private static Integer maxChartedQuestions(
			final HouseType houseType) throws ELSException {
		String houseTypeType = houseType.getType();
		String upperCaseHouseTypeType = houseTypeType.toUpperCase();
		
		StringBuffer sb = new StringBuffer();
		sb.append("QUESTION_STARRED_NO_OF_QUESTIONS_ON_MEMBER_CHART_");
		sb.append(upperCaseHouseTypeType);
		
		String parameterName = sb.toString();
		CustomParameter noOfQuestionsParameter = 
			CustomParameter.findByName(CustomParameter.class, 
					parameterName, "");
		
		try {
			return Integer.valueOf(noOfQuestionsParameter.getValue());
		}
		catch(Exception e) {
			throw new ELSException("StarredQuestionChart.maxChartedQuestions/1",
					"Custom Parameter " +
					"'QUESTION_STARRED_NO_OF_QUESTIONS_ON_MEMBER_CHART_" +
					upperCaseHouseTypeType + "' is not configured");
		}
	}
	
	private static boolean isPreviousChartExists(
			final Chart chart) throws ELSException {
		Session session = chart.getSession();
		Group group = chart.getGroup();
		DeviceType deviceType = chart.getDeviceType();
		Date answeringDate = chart.getAnsweringDate();
		String locale = chart.getLocale();
		
		Date previousAnsweringDate = 
			StarredQuestionChart.getPreviousAnsweringDate(group, answeringDate);
		if(previousAnsweringDate != null) {
			Chart previousChart = StarredQuestionChart.find(session, group,
					previousAnsweringDate, deviceType, locale);
			if(previousChart != null) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the previous chart is processed.
	 * @throws ELSException 
	 */
	private static Boolean isPreviousChartProcessed(
			final Chart chart) throws ELSException {
		Session session = chart.getSession();
		Group group = chart.getGroup();
		DeviceType deviceType = chart.getDeviceType();
		Date answeringDate = chart.getAnsweringDate();
		String locale = chart.getLocale();
		
		Date previousAnsweringDate = 
			StarredQuestionChart.getPreviousAnsweringDate(group, answeringDate);
		
		if(previousAnsweringDate != null) {
			Chart previousChart = new Chart(session, group, 
					previousAnsweringDate, deviceType, locale);
			return StarredQuestionChart.isProcessed(previousChart);
		}
		
		return true;
	}
	
	private static void updateChart(final Chart chart) throws ELSException {
		Session session = chart.getSession();
		Group group = chart.getGroup();
		DeviceType deviceType = chart.getDeviceType();
		Date answeringDate = chart.getAnsweringDate();
		String locale = chart.getLocale();
		
		Status TO_BE_PUT_UP = 
			Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, 
				locale);
		QuestionDates chartAnsweringDate = 
			group.findQuestionDatesByGroupAndAnsweringDate(answeringDate);
		
		Chart.getChartRepository().updateChartQuestions(session, 
				group, deviceType, answeringDate, 
				chartAnsweringDate, TO_BE_PUT_UP, TO_BE_PUT_UP, 
				locale);
	}
	
	/**
	 * Returns null if @param answeringDate is the first answeringDate
	 * of the @param group, else returns previous answeringDate.
	 */
	private static Date getPreviousAnsweringDate(final Group group,
			final Date answeringDate) {
		List<Date> answeringDates = 
			group.getAnsweringDates(ApplicationConstants.DESC);
		
		for(Date d : answeringDates) {
			if(d.compareTo(answeringDate) < 0) {
				return d;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns @param group answeringDate greater than or 
	 * equal to @param answeringDate.
	 */
	private static Date getAnsweringDateGTEQ(final Group group,
			final Date answeringDate) {
		List<Date> dates = group.getAnsweringDates(ApplicationConstants.ASC);
		for(Date d : dates) {
			if(d.compareTo(answeringDate) >= 0) {
				return d;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns @param group answeringDate lesser than or 
	 * equal to @param answeringDate.
	 */
	private static Date getAnsweringDateLTEQ(final Group group,
			final Date answeringDate) {
		List<Date> dates = group.getAnsweringDates(ApplicationConstants.DESC);
		for(Date d : dates) {
			if(d.compareTo(answeringDate) <= 0) {
				return d;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a list of @param group answeringDates greater than or 
	 * equal to @param answeringDate.
	 */
	private static List<Date> getAnsweringDatesGTEQ(final Group group,
			final Date answeringDate) {
		List<Date> answeringDates = new ArrayList<Date>();
		
		List<Date> dates = group.getAnsweringDates(ApplicationConstants.ASC);
		for(Date d : dates) {
			if(d.compareTo(answeringDate) >= 0) {
				answeringDates.add(d);
			}
		}
		
		return answeringDates;
	}
	
	/**
	 * If @param answeringDate is the last answering date for
	 * @param group the return true, else return false.
	 */
	private static Boolean isLastAnsweringDate(final Group group, 
			final Date answeringDate) {
		List<Date> answeringDates = 
			group.getAnsweringDates(ApplicationConstants.DESC);
		if(answeringDates.size() > 0) {
			Date date = answeringDates.get(0);
			if(date.compareTo(answeringDate) == 0) {
				return true;
			}
		}
		return false;
	}
	
	private static Date getCurrentDate() {
		CustomParameter dbDateFormat =
			CustomParameter.findByName(CustomParameter.class, 
					"DB_DATEFORMAT", "");
		return FormaterUtil.getCurrentDate(dbDateFormat.getValue());
	}
	
	/**
	 * Create an empty ChartEntry for @param member. This ChartEntry wont
	 * have any questions.
	 */
	private static ChartEntry newEmptyChartEntry(final Member member,
			final String locale) {
		ChartEntry chartEntry = new ChartEntry();
		chartEntry.setMember(member);
		chartEntry.setLocale(locale);
		return chartEntry;
	}
	
	private static Boolean isAssistantProcessed(final Question question) {
		String ASSISTANT_PROCESSED = 
			ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED;
		Status internalStatus = question.getInternalStatus();
		if(internalStatus.getType().equals(ASSISTANT_PROCESSED)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Algorithm: "ADD TO CHART IF APPLICABLE"
	 * 1. If a member has less than @param maxNoOfQuestions Questions 
	 * on Chart simply add the Question to the chart, & update the chart. 
	 * Set the status of Question to "TO_BE_PUT_UP"
	 * 
	 * 2. If a member has exactly @param maxNoOfQuestions questions then,
	 * a> The Questions which are in the Workflow (internalStatus != 
	 * "TO_BE_PUT_UP") wont get affected.
	 * 
	 * b> The questions which are not in Workflow (internalStatus == 
	 * "TO_BE_PUT_UP"), will compete with @param question for a slot in 
	 * Chart. At the end of this step the Question which leaves the Chart 
	 * will have internalStatus = "ASSISTANT_PROCESSED". The Question
	 * which comes on the Chart will have internalStatus = 
	 * "TO_BE_PUT_UP". Rest of the Questions will remain unaffected.
	 * 
	 * Constraints:
	 * 1> If @param question is added to the chart, it's internalStatus and 
	 * recommendationStatus should change to "TO_BE_PUT_UP".
	 * 
	 * 2> In lieu of @param question entering the Chart, if some Question 
	 * leaves the Chart then the internalStatus & recommendationStatus 
	 * of that Question should be set to "ASSISTANT_PROCESSED".
	 * 
	 * 3> The internalStatuses of the rest of the Questions on the Chart 
	 * should remain unaffected.
	 * 
	 * Returns true if the @param question is added to the @param chart, 
	 * else returns false
	 */
	private static Boolean addToChartIfApplicable(final Chart chart,
			final Question question, 
			final Integer maxNoOfQuestions) throws ELSException {
		Boolean isAddedToChart = false;
		
		Member member = question.getPrimaryMember();
		Session session = chart.getSession();
		Group group = chart.getGroup();
		DeviceType deviceType = question.getType();
		Date answeringDate = chart.getAnsweringDate();
		String locale = chart.getLocale();
		
		Status TO_BE_PUT_UP = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, 
				locale);
		Status ASSISTANT_PROCESSED = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, 
				locale);
		QuestionDates chartAnsweringDate = 
			group.findQuestionDatesByGroupAndAnsweringDate(answeringDate);
		
		List<Question> onChartQuestions = 
			StarredQuestionChart.findQuestions(member, session, group,
					answeringDate, deviceType, locale);
		int onChartQuestionsSize = onChartQuestions.size();
		
		if(onChartQuestionsSize == maxNoOfQuestions) {
			List<Question> updatedChartQuestions = new ArrayList<Question>();
			
			// Questions in Workflow will stay unharmed
			List<Question> questionsInWorkflow = 
				StarredQuestionChart.questionsInWorkflow(
						onChartQuestions, locale);
			updatedChartQuestions.addAll(questionsInWorkflow);
			
			//Questions Clubbed will stay unharmed
			List<Question> questionsClubbed = 
					StarredQuestionChart.questionsInClubbingStatus(
							onChartQuestions, locale);
			for(Question q : questionsClubbed){
				boolean isParentOnSameChart = false;
				for(Question chartedQuestion: onChartQuestions) {
					if(chartedQuestion.getId()!=q.getId()
							&& chartedQuestion.getId().equals(q.getParent().getId())) {
						isParentOnSameChart = true;
						break;
					} 
				}
				if(!questionsInWorkflow.contains(q) && !isParentOnSameChart){
					updatedChartQuestions.add(q);
				}
			}
				//updatedChartQuestions.addAll(questionsClubbed);
			
			// The Remaining Questions not in the Workflow or children of same chart question will compete for a place 
			// in the Chart
			int requiredQuestions = 
				maxNoOfQuestions - updatedChartQuestions.size();
			
			List<Question> remainingChartedQuestions = new ArrayList<Question>();
			for(Question chartedQuestion: onChartQuestions) {
				boolean isRemainingQuestionForUpdation = true;
				for(Question updatedChartQuestion: updatedChartQuestions) {
					if(chartedQuestion.getId().equals(updatedChartQuestion.getId())) {
						isRemainingQuestionForUpdation = false;
						break;
					}
				}
				if(isRemainingQuestionForUpdation) {
					remainingChartedQuestions.add(chartedQuestion);
				}
			}
			remainingChartedQuestions.add(question);
			
//			List<Question> questionsNotInWorkflow = 
//				StarredQuestionChart.questionsNotInWorkflow(
//						onChartQuestions, locale);
//			questionsNotInWorkflow.add(question);
			
			// The size of candidateQuestions will always be size of 
			// questionsNotInWorkflow + 1 (@param question as provided 
			// in the parameter)
			List<Question> candidateQuestions = 
				StarredQuestionChart.reorderQuestions(remainingChartedQuestions, 
						answeringDate);
			
			// The nature of candidateQuestions is such that the last 
			// question in the list is the least eligible to be on the
			// Chart and hence, it will leave the Chart.
			int candidateQuestionsSize = candidateQuestions.size();
			for(int i = 0; i < candidateQuestionsSize - 1; i++) {
				Question qn = (Question) candidateQuestions.get(i);
				Status internalStatus = qn.getInternalStatus();
				String internalStatusType = internalStatus.getType();
				
				if(internalStatusType.equals(ASSISTANT_PROCESSED.getType())) {
					qn.setInternalStatus(TO_BE_PUT_UP);
					qn.setRecommendationStatus(TO_BE_PUT_UP);
					qn.setChartAnsweringDate(chartAnsweringDate);
					qn.simpleMerge();
					
					if(qn.getId().equals(question.getId())) {
						isAddedToChart = true;
					}
				}
			}
			
			// The last Question qn in the candidateQuestions list 
			// is leaving the Chart.
			Question qn = 
				(Question) candidateQuestions.get(candidateQuestionsSize - 1);
			
			// Update qn's clubbing information
			if(qn.getParent() == null) { // qn could be the parent
				// If qn is a parent then remove it as a parent.
				// Select an appropriate kid of the qn as the
				// new parent.
				Question newParent = ClubbedEntity.removeParent(qn);
				if(newParent != null){
					newParent.setInternalStatus(TO_BE_PUT_UP);
					newParent.setRecommendationStatus(TO_BE_PUT_UP);
					newParent.simpleMerge();
				}
			}
			else { // qn is the kid
				Long parentId = qn.getParent().getId();
				Long kidId = qn.getId();
				ClubbedEntity.unclub(parentId, kidId, locale);
			}
			
			// Update qn's status information.
			qn.setInternalStatus(ASSISTANT_PROCESSED);
			qn.setRecommendationStatus(ASSISTANT_PROCESSED);
			qn.setChartAnsweringDate(null);
			qn = qn.simpleMerge();
			
			// Update the Chart entry
			if(candidateQuestionsSize >= requiredQuestions) {
				updatedChartQuestions.addAll(
						candidateQuestions.subList(0, requiredQuestions));
			}
			List<Device> devices = new ArrayList<Device>();
			devices.addAll(updatedChartQuestions);
			ChartEntry ce = 
				StarredQuestionChart.find(chart.getChartEntries(), member);
			ce.setDevices(devices);
			ce.merge();
			
		}
		else if(onChartQuestionsSize < maxNoOfQuestions) {
			List<Question> updatedChartQuestions = new ArrayList<Question>();
			updatedChartQuestions.addAll(onChartQuestions);
			
			// The Question taken on the Chart should have status 
			// "TO_BE_PUT_UP"
			question.setInternalStatus(TO_BE_PUT_UP);
			question.setRecommendationStatus(TO_BE_PUT_UP);
			question.setChartAnsweringDate(chartAnsweringDate);
			question.simpleMerge();
			
			// Add the Question to the Chart.
			updatedChartQuestions.add(question);
			updatedChartQuestions = 
				StarredQuestionChart.reorderQuestions(updatedChartQuestions, 
						answeringDate);
			
			// Update the Chart Entry.
			List<Device> devices = new ArrayList<Device>();
			devices.addAll(updatedChartQuestions);
			ChartEntry ce = 
				StarredQuestionChart.find(chart.getChartEntries(), member);
			ce.setDevices(devices);
			ce.merge();
			
			isAddedToChart = true;
		}
		
		return isAddedToChart;
	}
	
	/**
	 * All the Questions with status not equal to ("SUBMIT" AND "COMPLETE" 
	 * AND "INCOMPLETE") AND not starting with "question_putup" AND
	 * not starting with "question_system" are not in the workflow.
	 */
	private static List<Question> questionsInWorkflow(
			final List<Question> onChartQuestions,
			final String locale) {
		List<Question> qList = new ArrayList<Question>();
		
		String INCOMPLETE = ApplicationConstants.QUESTION_INCOMPLETE;
		String COMPLETE = ApplicationConstants.QUESTION_COMPLETE;
		String SUBMITTED = ApplicationConstants.QUESTION_SUBMIT;
		
		for(Question q : onChartQuestions) {
			Status internalStatus = q.getInternalStatus();
			String type = internalStatus.getType();
			if(! (type.equals(INCOMPLETE) 
					|| type.equals(COMPLETE) 
					|| type.equals(SUBMITTED) 
					|| type.startsWith("question_putup") 
					|| type.startsWith("question_system"))) {
				qList.add(q);
			}
		}
		
		return qList;
	}
	
	/**
	 * All the Questions with status "SUBMIT" OR "COMPLETE" OR "INCOMPLETE" OR 
	 * starting with "question_putup" OR starting with "question_system" are 
	 * not in the workflow.
	 */
	private static List<Question> questionsNotInWorkflow(
			final List<Question> onChartQuestions,
			final String locale) {
		List<Question> qList = new ArrayList<Question>();
		
		String INCOMPLETE = ApplicationConstants.QUESTION_INCOMPLETE;
		String COMPLETE = ApplicationConstants.QUESTION_COMPLETE;
		String SUBMITTED = ApplicationConstants.QUESTION_SUBMIT;
		
		for(Question q : onChartQuestions) {
			Status internalStatus = q.getInternalStatus();
			String type = internalStatus.getType();
			if(type.equals(INCOMPLETE) 
					|| type.equals(COMPLETE) 
					|| type.equals(SUBMITTED) 
					|| (type.startsWith("question_putup") && q.getParent() == null)
					|| (type.startsWith("question_system") && q.getParent() == null)){
				qList.add(q);
			}
		}
		
		return qList;
	}
	
	/**
	 * All the Questions with status not equal to ("SUBMIT" AND "COMPLETE" 
	 * AND "INCOMPLETE") AND not starting with "question_putup" AND
	 * not starting with "question_system" are not in the workflow.
	 */
	private static List<Question> questionsInClubbingStatus(
			final List<Question> onChartQuestions,
			final String locale) {
		List<Question> qList = new ArrayList<Question>();
		
		String CLUBBED = ApplicationConstants.QUESTION_SYSTEM_CLUBBED;
		String PUTUPCLUBBING = ApplicationConstants.QUESTION_PUTUP_CLUBBING;
		String PUTUPADMITDUETOREVERSECLUBBING = ApplicationConstants.QUESTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING;
		String PUTUPCLUBBINGPOSTADMISSION = ApplicationConstants.QUESTION_PUTUP_CLUBBING_POST_ADMISSION;
		String PUTUPCLUBBINGWITHUNSTARREDQUESTION = ApplicationConstants.QUESTION_PUTUP_CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION;
		String PUTUPNAMECLUBBING = ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING;
		String PUTUPUNCLUBBING = ApplicationConstants.QUESTION_PUTUP_UNCLUBBING;
		for(Question q : onChartQuestions) {
			Status internalStatus = q.getInternalStatus();
			String type = internalStatus.getType();
			String rtype = q.getRecommendationStatus().getType();
			if( (type.equals(CLUBBED) 
					|| type.equals(PUTUPCLUBBING) 
					|| type.equals(PUTUPADMITDUETOREVERSECLUBBING) 
					|| type.equals(PUTUPCLUBBINGPOSTADMISSION) 
					|| type.equals(PUTUPCLUBBINGWITHUNSTARREDQUESTION)
					|| type.equals(PUTUPNAMECLUBBING) 
					|| type.equals(PUTUPUNCLUBBING))) {
				qList.add(q);
			} else if( (rtype.equals(PUTUPADMITDUETOREVERSECLUBBING) 
					|| rtype.equals(PUTUPCLUBBINGPOSTADMISSION) 
					|| rtype.equals(PUTUPCLUBBINGWITHUNSTARREDQUESTION)
					|| rtype.equals(PUTUPUNCLUBBING))) {
				qList.add(q);
			}
		}
		
		return qList;
	}
	
	/**
	 * Orders the @param questions in order defined as follows:
	 * 1. Dated Questions sorted by number in increasing order.
	 * 2. Previously dated Questions sorted by answeringDate in 
	 * increasing order. Break the tie using Question number.
	 * 3. Non dated Questions sorted by number in increasing order.
	 */
	private static List<Question> reorderQuestions(
			final List<Question> onChartQuestions,
			final Date answeringDate) {
		List<Question> datedQList = new ArrayList<Question>();
		List<Question> beforeDatedQList = new ArrayList<Question>();
		List<Question> afterDatedQList = new ArrayList<Question>();
		List<Question> nonDatedQList = new ArrayList<Question>();
		
		for(Question q : onChartQuestions) {
			QuestionDates qd = q.getAnsweringDate();
			if(qd == null) {
				nonDatedQList.add(q);
			}
			else {
				Date questionAnsweringDate = qd.getAnsweringDate();
				if(questionAnsweringDate.compareTo(answeringDate) < 0) {
					beforeDatedQList.add(q);
				}
				else if(questionAnsweringDate.compareTo(answeringDate) > 0) {
					afterDatedQList.add(q);
				}
				else { // questionAnsweringDate.compareTo(answeringDate) == 0
					datedQList.add(q);
				}
			}
		}
		
		datedQList = Question.sortByNumber(datedQList, 
				ApplicationConstants.ASC);
		beforeDatedQList = Question.sortByAnsweringDate(beforeDatedQList, 
				ApplicationConstants.ASC);
		nonDatedQList = Question.sortByNumber(nonDatedQList, 
				ApplicationConstants.ASC);
		afterDatedQList = Question.sortByAnsweringDate(afterDatedQList, 
				ApplicationConstants.ASC);
		
		List<Question> reorderedQList = new ArrayList<Question>();
		reorderedQList.addAll(datedQList);
		reorderedQList.addAll(beforeDatedQList);
		reorderedQList.addAll(nonDatedQList);
		reorderedQList.addAll(afterDatedQList);
	
		// ASSERT: The size of onChartQuestions should be equal to the size
		// 		   of reorderedQList
		return reorderedQList;
	}
	
	/**
	 * Find ChartEntry among @param chartEntries where 
	 * ChartEntry.member == @param member.
	 * 
	 * Returns null if ChartEntry could not be found.
	 */
	private static ChartEntry find(final List<ChartEntry> chartEntries, 
			final Member member) {
		for(ChartEntry ce : chartEntries) {
			Member ceMember = ce.getMember();
			if(ceMember.getId().equals(member.getId())) {
				return ce;
			}
		}
		return null;
	}
	
	private static List<Question> marshallDevices(
			final List<Device> devices) {
		List<Question> questions = new ArrayList<Question>();
		
		for(Device d : devices) {
			Question q = (Question) d;
			questions.add(q);
		}
		
		return questions;
	}

	private static List<Device> marshallQuestions(
			final List<Question> questions) {
		List<Device> devices = new ArrayList<Device>();
		
		for(Question q : questions) {
			Device d = q;
			devices.add(d);
		}
		
		return devices;
	}
	
	/**
	 * Returns the list of kids of @param question.
	 */
	private static List<Question> findClubbings(final Question question) {
		List<Question> questions = new ArrayList<Question>();
		
		List<ClubbedEntity> clubbings = 
			Question.findClubbedEntitiesByPosition(question);
		for(ClubbedEntity ce : clubbings) {
			Question q = ce.getQuestion();
			questions.add(q);
		}
		
		return questions;
	}
	
	/**
	 * 1. Remove @param question from its Chart.
	 * 2. Whenever a Question leaves the Chart its chartAnsweringDate 
	 * attribute is set as null.
	 * 3. Since 1 question has left the Chart, find an eligible question
	 * and add it to the Chart.
	 * 4. Reorder the devices in the ChartEntry.
	 * 
	 * Returns the processed source group Chart.
	 */
	private static Chart processSourceGroupChartLH(final Question question,
			final Group sourceGroup) throws ELSException {
		Chart chart = Chart.find(question);
		if(chart != null) {
			String locale = chart.getLocale();
			Member member = question.getPrimaryMember();
			Session session = question.getSession();
			String submissionEndDate = session.getParameter("questions_starred_submissionEndDate");
			Date lastSubmissionDate = FormaterUtil.formatStringToDate(submissionEndDate, ApplicationConstants.DB_DATETIME_FORMAT, locale);
			Group group = chart.getGroup();
			Date answeringDate = chart.getAnsweringDate();
			Chart latestChart = findLatestChart(session, group, question.getOriginalType(), locale);
			
			Date latestChartAnsweringDate = latestChart.getAnsweringDate();
			ChartEntry ce = 
				StarredQuestionChart.find(chart.getChartEntries(), member);
			List<Device> devices = StarredQuestionChart.findDevices(ce);//ce.getDevices();
			
			/*
			 * 1. Remove @param question from the Chart
			 */
			int index = -1;
			for(Device d : devices) {
				++index;
				if(d.getId().equals(question.getId())) {
					break;
				}
			}
			devices.remove(index);
			
			/*
			 * 2. Set chartAnsweringDate attribute of @param question as null
			 */
			question.setChartAnsweringDate(null);
			question.simpleMerge();
			
			/*
			 * 3. Since 1 question has left the Chart, so add 1 question
			 * to the Chart.
			 */
			int dateComparator = -1;
			if(latestChart != null && latestChart.equals(chart)){
				Date finalSubmissionDate = group.getFinalSubmissionDate(answeringDate);
				if(question.getSubmissionDate() != null){
					dateComparator = finalSubmissionDate.compareTo(question.getSubmissionDate());
				}else{
					dateComparator = finalSubmissionDate.compareTo(new Date());
				}
			}
			
			if(dateComparator>=0 && lastSubmissionDate.compareTo(new Date())>=0){
				Question q = StarredQuestionChart.onGroupChangeAddQuestionLH(
						session, member, sourceGroup, answeringDate,
						devices.toArray(new Question[0]), locale);
				if(q != null) {
					// The Questions taken on the Chart should have status 
					// "TO_BE_PUT_UP"
					Status TO_BE_PUT_UP = Status.findByType(
							ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, 
							locale);
					QuestionDates chartAnsweringDate = 
						group.findQuestionDatesByGroupAndAnsweringDate(
								answeringDate);
					
					q.setInternalStatus(TO_BE_PUT_UP);
					q.setRecommendationStatus(TO_BE_PUT_UP);
					q.setChartAnsweringDate(chartAnsweringDate);
					q.simpleMerge();
					
					devices.add(q);
				}
			}
			
			
			/*
			 * 4. Reorder the devices and then add them to the Chart
			 */
			List<Question> questions = 
				StarredQuestionChart.marshallDevices(devices);
			List<Question> reorderedQuestions = 
				StarredQuestionChart.reorderQuestions(questions, answeringDate);
			List<Device> reorderedDevices =
				StarredQuestionChart.marshallQuestions(reorderedQuestions);
			ce.setDevices(reorderedDevices);
			ce.merge();
		}
		
		return chart;
	}
	
	private static Chart processSourceGroupChart(final Question question,
			final Group sourceGroup) throws ELSException {
		Chart chart = Chart.find(question);
		if(chart != null) {
			String locale = chart.getLocale();
			Session session = question.getSession();
			String submissionEndDate = session.getParameter("questions_starred_submissionEndDate");
			Date lastSubmissionDate = FormaterUtil.formatStringToDate(submissionEndDate, ApplicationConstants.DB_DATETIME_FORMAT, locale);
			// As the shifting of chart questions can be done only till the last submission date, post group change
			// only the device will be removed from the chart without shifting.
			if(lastSubmissionDate.compareTo(new Date())>=0){
				shiftChartQuestionsRecursive(question, chart, true, locale);
			}else{
				processSourceGroupChartCommon(question, sourceGroup);
			}
		}
		
		return chart;
	}
	
	private static List<Device> findDevices(ChartEntry ce) throws ELSException {
		return Chart.getChartRepository().find(ce);
	}

	/**
	 * For @param targetGroup find the smallest answeringDate >= 
	 * @param sourceChart.answeringDate. Let it be called 
	 * targetChartDate.
	 * 
	 * > IF targetChartDate != null then return the Chart for 
	 * @param targetGroup and targetChartDate.
	 * 
	 * > IF targetChartDate == null then it means that @param 
	 * sourceChart.answeringDate is the last week date. In this case, 
	 * for @param targetGroup find the largest answeringDate smaller than 
	 * @param sourceChart.answeringDate. Return the Chart for
	 * @param targetGroup and this date.
	 */
	private static Chart findTargetChart(final Chart sourceChart, 
			final Group targetGroup) throws ELSException {
		Chart chart = null;
		
		Session session = sourceChart.getSession();
		DeviceType deviceType = sourceChart.getDeviceType();
		String locale = sourceChart.getLocale();
		
		Date sourceChartAnsweringDate = sourceChart.getAnsweringDate();
		Date targetChartAnsweringDate = 
			StarredQuestionChart.getAnsweringDateGTEQ(targetGroup, 
					sourceChartAnsweringDate);
		if(targetChartAnsweringDate != null) {
			chart = StarredQuestionChart.find(session, targetGroup, 
					targetChartAnsweringDate, deviceType, locale);
		}
		else {
			Date newTargetChartAnsweringDate =
				StarredQuestionChart.getAnsweringDateLTEQ(targetGroup,
						sourceChartAnsweringDate);
			chart = StarredQuestionChart.find(session, targetGroup, 
					newTargetChartAnsweringDate, deviceType, locale);
		}
		
		return chart;
	}
	
	/**
	 * 1. Set the chartAnsweringDate of @param question as 
	 * @param targetChart.answeringDate.
	 * 2. Add @param question to @param targetChart.
	 * 3. Reorder the devices in the ChartEntry.
	 */
	private static void processTargetGroupChart(final Question question,
			final Chart targetChart) throws ELSException {
		/*
		 * 1. Set question.chartAnsweringDate == targetChart.answeringDate 
		 */
		Group group = targetChart.getGroup();
		Date answeringDate = targetChart.getAnsweringDate();
		QuestionDates chartAnsweringDate = 
			group.findQuestionDatesByGroupAndAnsweringDate(answeringDate);
		question.setChartAnsweringDate(chartAnsweringDate);
		question.simpleMerge();
		
		/*
		 * 2. Add @param question to @param targetChart
		 */
		Member member = question.getPrimaryMember();
		ChartEntry ce = 
			StarredQuestionChart.find(targetChart.getChartEntries(), member);
		List<Device> devices = StarredQuestionChart.findDevices(ce);//ce.getDevices();
		devices.add(question);
		
		/*
		 * 3. Reorder the devices and then add them to the Chart
		 */
		List<Question> questions = 
			StarredQuestionChart.marshallDevices(devices);
		List<Question> reorderedQuestions = 
			StarredQuestionChart.reorderQuestions(questions, answeringDate);
		List<Device> reorderedDevices =
			StarredQuestionChart.marshallQuestions(reorderedQuestions);
		ce.setDevices(reorderedDevices);
		ce.merge();
	}
	
	private static Boolean isProcessAllRemainingQnsForFirstBatchLastDate(
			final HouseType houseType) {
		String houseTypeType = houseType.getType();
		String upperCaseHouseTypeType = houseTypeType.toUpperCase();		
		
		StringBuffer sb = new StringBuffer();
		sb.append(
			"QUESTION_STARRED_PROCESS_ALL_REMAINING_QUESTIONS_FOR_FIRSTBATCH_LASTDATE_");
		sb.append(upperCaseHouseTypeType);
		
		String parameterName = sb.toString();
		
		CustomParameter parameter = 
			CustomParameter.findByName(CustomParameter.class, 
					parameterName, "");
		String value = parameter.getValue();
		
		if(value.equalsIgnoreCase("TRUE")) {
			return true;
		}
		
		return false;
	}
	
	private static boolean isCreateChartWithoutProcessingPreviousChart(
			final HouseType houseType) {
		String houseTypeType = houseType.getType();
		String upperCaseHouseTypeType = houseTypeType.toUpperCase();		
		
		StringBuffer sb = new StringBuffer();
		sb.append(
			"QUESTION_STARRED_CREATE_CHART_WITHOUT_PROCESSING_PREVIOUS_CHART_");
		sb.append(upperCaseHouseTypeType);
		
		String parameterName = sb.toString();
		CustomParameter parameter = 
			CustomParameter.findByName(CustomParameter.class, 
					parameterName, "");
		String value = parameter.getValue();
		
		if(value.equalsIgnoreCase("TRUE")) {
			return true;
		}
		
		return false;
	}
	
	private static Date getSubmissionStartTime(final Session session,
			final DeviceType deviceType, 
			final String locale) {
		StringBuffer key = new StringBuffer();
		key.append(ApplicationConstants.QUESTION_STARRED_SUBMISSION_STARTTIME);
		String value = session.getParameter(key.toString());
		
		return getFormattedTime(value, locale);
	}
	
	private static Date getSubmissionEndTime(final Group group, 
			final Date answeringDate) {
		Date submissionEndTime = group.getFinalSubmissionDate(answeringDate);
		return submissionEndTime;
	}
	
	private static Date getFirstBatchSubmissionStartTime(
			final Session session,
			final String locale) {
		StringBuffer key = new StringBuffer();
		key.append(ApplicationConstants
				.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME);
		String value = session.getParameter(key.toString());
		
		return getFormattedTime(value, locale);
	}
	
	private static Date getFirstBatchSubmissionEndTime(
			final Session session,
			final String locale) {
		StringBuffer key = new StringBuffer();
		key.append(ApplicationConstants
				.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
		String value = session.getParameter(key.toString());
		
		return getFormattedTime(value, locale);
	}
	
	private static Date getSecondBatchSubmissionStartTime(
			final Session session,
			final String locale) {
		StringBuffer key = new StringBuffer();
		key.append(ApplicationConstants
				.QUESTION_STARRED_SECONDBATCH_SUBMISSION_STARTTIME);
		String value = session.getParameter(key.toString());
		
		return getFormattedTime(value, locale);
	}
	
	private static Date getSecondBatchSubmissionEndTime(
			final Session session,
			final String locale) {
		StringBuffer key = new StringBuffer();
		key.append(ApplicationConstants
				.QUESTION_STARRED_SECONDBATCH_SUBMISSION_ENDTIME);
		String value = session.getParameter(key.toString());
		
		return getFormattedTime(value, locale);
	}
	
	private static Date getSubmissionEndTime(
			final Session session,
			final String locale) {
		StringBuffer key = new StringBuffer();
		key.append(ApplicationConstants
				.QUESTION_STARRED_SUBMISSION_ENDTIME);
		String value = session.getParameter(key.toString());
		
		return getFormattedTime(value, locale);
	}
	
	private static Date getFormattedTime(final String strTime,
			final String locale) {
		CustomParameter datePattern = 
			CustomParameter.findByName(CustomParameter.class, 
					"DB_TIMESTAMP", "");
		String datePatternValue = datePattern.getValue();
		
		Date formattedTime = FormaterUtil.formatStringToDate(strTime, 
				datePatternValue, locale);
		return formattedTime;
	}
	
}