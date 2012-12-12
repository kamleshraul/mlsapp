package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.DateUtil;
import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.QuestionVO;
import org.mkcl.els.repository.ChartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="charts")
public class Chart extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 2139509586805589388L;
	
	
	//=============== ATTRIBUTES ====================
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="group_id")
	private Group group;
	
	@Temporal(TemporalType.DATE)
	private Date answeringDate;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="charts_chart_entries",
			joinColumns={ @JoinColumn(name="chart_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="chart_entry_id", referencedColumnName="id") })
	private List<ChartEntry> chartEntries;
	
	@Autowired
	private transient ChartRepository repository;
	
	
	//=============== CONSTRUCTORS ==================
	public Chart() {
		super();
	}

	public Chart(final Session session,
			final Group group,
			final Date answeringDate,
			final String locale) {
		super(locale);
		this.setSession(session);
		this.setGroup(group);
		this.setAnsweringDate(answeringDate);
		this.setChartEntries(new ArrayList<ChartEntry>());
	}
	
	
	//=============== VIEW METHODS ==================
	/**
	 * Returns null if Chart does not exist for the specified parameters
	 * OR
	 * Returns an empty list if there are no Questions asked by any Member
	 * OR
	 * Returns a list of ChartVOs
	 */
	public static List<ChartVO> getChartVOs(final Session session,
			final Group group,
			final Date answeringDate,
			final String locale) {
		List<ChartVO> chartVOs = new ArrayList<ChartVO>();
		Chart chart = Chart.find(session, group, answeringDate, locale);
		
		if(chart != null) {
			List<ChartVO> chartVOsWithQuestions = new ArrayList<ChartVO>();
			List<ChartVO> chartVOsWithoutQuestions = new ArrayList<ChartVO>();
			
			List<ChartEntry> chartEntries = chart.getChartEntries();
			for(ChartEntry ce : chartEntries) {
				Long memberId = ce.getMember().getId();
				String memberName = ce.getMember().getFullnameLastNameFirst();
				List<QuestionVO> questionVOs = Chart.getQuestionVOs(ce.getQuestions());
				
				if(questionVOs.isEmpty()) {
					ChartVO chartVO = new ChartVO(memberId, memberName);
					chartVOsWithoutQuestions.add(chartVO);
				}
				else {
					ChartVO chartVO = new ChartVO(memberId, memberName, questionVOs);
					chartVOsWithQuestions.add(chartVO);
				}
			}
			chartVOsWithQuestions = ChartVO.sort(chartVOsWithQuestions, ApplicationConstants.ASC);
			
			chartVOsWithoutQuestions = 
				ChartVO.sort(chartVOsWithoutQuestions, ApplicationConstants.ASC);
			
			chartVOs.addAll(chartVOsWithQuestions);
			chartVOs.addAll(chartVOsWithoutQuestions);
		} 
		else {
			chartVOs = null;
		}
		
		return chartVOs;
	}
	
	private static List<QuestionVO> getQuestionVOs(List<Question> questions) {
		List<QuestionVO> questionVOs = new ArrayList<QuestionVO>();
		for(Question q : questions) {
			QuestionVO questionVO = new QuestionVO(q.getId(), 
					q.getNumber(), 
					q.getInternalStatus().getType());
			
			questionVOs.add(questionVO);
		}
		return questionVOs;
	}
	
	
	//=============== DOMAIN METHODS ================
	public Chart create() {
		Chart chart = null;
		HouseType houseType = this.getSession().getHouse().getType();
		
		if(houseType.getType().equals("lowerhouse")) {
			chart = createLH();
		}
		else if(houseType.getType().equals("upperhouse")) {
			chart = createUH();
		}
		return chart;
	}
	
	/**
	 * Returns true if @param q is added to Chart, else returns false.
	 */
	public static Boolean addToChart(final Question q) {
		Session session = q.getSession();
		HouseType houseType = session.getHouse().getType();
		
		if(houseType.getType().equals("lowerhouse")) {
			return addToChartLH(q);
		}
		else if(houseType.getType().equals("upperhouse")) {
			return addToChartUH(q);
		}
		return false;
	}
	
	public static void groupChange(final Question question, final Group affectedGroup) {
		Session session = question.getSession();
		HouseType houseType = session.getHouse().getType();
		
		if(houseType.getType().equals("lowerhouse")) {
			groupChangeLH(question, affectedGroup);
		}
		else if(houseType.getType().equals("upperhouse")) {
			groupChangeUH(question, affectedGroup);
		}
	}
	
	/**
	 * A Chart is said to be processed if all the Questions on the
	 * Chart have internalStatus != 'TO_BE_PUT_UP' and internalStatus
	 * does not begin with "question_before_workflow".
	 * 
	 * Returns true if a Chart is processed or if a Chart does not exist, 
	 * else returns false.
	 */
	public static Boolean isProcessed(final Session session, 
			final Group group, 
			final Date answeringDate,
			final String locale) {
		Chart chart = Chart.find(session, group, answeringDate, locale);
		if(chart != null) {
			String excludeInternalStatus = "question_before_workflow";
			return Chart.getChartRepository().isProcessed(session, group, answeringDate, 
					excludeInternalStatus, locale);
		}
		return true;
	}
	
	public static Chart find(final Question question) {
	    return getChartRepository().find(question);
	}
	
	/**
	 * Returns null if there is no Chart for the specified parameters.
	 */
	public static Chart find(final Session session, 
			final Group group, 
			final Date answeringDate,
			final String locale) {
		return Chart.getChartRepository().find(session, group, answeringDate, locale);
	}
	
	/**
	 * For @param group, check for existence of a Chart for a given 
	 * answeringDate in the descending order of the answering dates.
	 * 
	 * Returns null if there is no Chart for the specified parameters.
	 */
	public static Chart findLatestChart(final Session session,
			final Group group, 
			final String locale) {
		List<Date> answeringDates = group.getAnsweringDates(ApplicationConstants.DESC);
		for(Date date : answeringDates) {
			Chart chart = Chart.find(session, group, date, locale); 
			if(chart != null) {
				return chart;
			}
		}
		return null;
	}
	
	/**
	 * Returns the list of Questions of @param member taken on a Chart
	 * for the particular @param answeringDate.
	 * 
	 * Returns an empty list if there are no Questions for member.
	 */
	public static List<Question> findQuestions(final Member member,
			final Session session, 
			final Group group, 
			final Date answeringDate,
			final String locale) {
		return Chart.getChartRepository().findQuestions(member, session, group, 
				answeringDate, locale);
	}
	
	/**
	 * Returns an unsorted list of Questions.
	 * OR
	 * Returns an empty list if there are no Questions.
	 */
	public static List<Question> findQuestions(final Session session, 
			final Group group, 
			final Date answeringDate,
			final String locale) {
		return Chart.getChartRepository().findQuestions(session, group, answeringDate, locale);
	}
	
	/**
	 * Returns a list of Questions sorted on Question number according 
	 * to @param sortOrder.
	 * OR
	 * Returns an empty list if there are no Questions.
	 */
	public static List<Question> findQuestions(final Session session, 
			final Group group, 
			final Date answeringDate,
			final String sortOrder,
			final String locale) {
		return Chart.getChartRepository().findQuestions(session, group, answeringDate, 
				sortOrder, locale);
	}
	
	/**
	 * Returns a list of Members on Chart.
	 * OR
	 * Returns an empty list if there are no Members.
	 */
	public static List<Member> findMembers(final Session session, 
			final Group group, 
			final Date answeringDate,
			final String locale) {
		return Chart.getChartRepository().findMembers(session, group, answeringDate, locale);
	}
	
	
	//=============== COUNCIL METHODS =================
	/**
	 * Creates a new Chart. If a chart already exists then returns the
	 * existing Chart. If a previous dated Chart exists & is unprocessed
	 * then don't create a new Chart and return null.
	 * 
	 * The Questions submitted only for first batch are to be taken on 
	 * the Chart while creating the Chart. Besides if the Chart is being
	 * created for the final answeringDate of a Group then (in a worst
	 * case scenario) it may have Session.numberOfQuestionInFirstBatchUH
	 * Questions on the Chart.
	 */
	private Chart createUH() {
		boolean isPreviousChartProcessed = Chart.isPreviousChartProcessed(this.getSession(), 
				this.getGroup(), this.getAnsweringDate(), this.getLocale());
		
		if(isPreviousChartProcessed == true) {
			Chart chart = Chart.find(this.getSession(), this.getGroup(), this.getAnsweringDate(), 
					this.getLocale());
			
			if(chart == null) {
				Date currentDate = Chart.getCurrentDate();
				List<Member> activeMembers = Member.findActiveMembers(this.getSession().getHouse(), 
						currentDate, ApplicationConstants.ASC, this.getLocale());
				
				Date startTime = this.getSession().getQuestionSubmissionFirstBatchStartDateUH();
				Date endTime = this.getSession().getQuestionSubmissionFirstBatchEndDateUH();
				DeviceType deviceType = DeviceType.findByType("questions_starred", this.getLocale());
				Status ASSISTANT_PROCESSED = 
					Status.findByType("question_assistantprocessed", this.getLocale());
				
				if((Chart.isLastAnsweringDate(this.getGroup(), this.getAnsweringDate()) == true) && 
						(Chart.processAllRemainingQnsForLastDateUH().equalsIgnoreCase("TRUE"))) {
					for(Member m : activeMembers) {
						ChartEntry chartEntry = Chart.newChartEntryUH(this.getSession(), m, 
								deviceType, this.getGroup(), this.getAnsweringDate(), startTime, 
								endTime, ASSISTANT_PROCESSED, this.getLocale());
						this.getChartEntries().add(chartEntry);
					}
				}
				else {
					Integer maxQuestionsOnChart = Chart.maxQuestionsOnChartUH();
					for(Member m : activeMembers) {
						ChartEntry chartEntry = Chart.newChartEntryUH(this.getSession(), m, 
								deviceType, this.getGroup(), this.getAnsweringDate(), startTime, 
								endTime, maxQuestionsOnChart, ASSISTANT_PROCESSED, 
								this.getLocale());
						this.getChartEntries().add(chartEntry);
					}
				}
				chart = (Chart) this.persist();
				
				Status TO_BE_PUT_UP = 
					Status.findByType("question_before_workflow_tobeputup", this.getLocale());
				// List<Question> questions = Chart.findQuestions(this.getSession(), this.getGroup(), 
				//	this.getAnsweringDate(), this.getLocale());
				// for(Question q : questions) {
				//	q.setInternalStatus(TO_BE_PUT_UP);
				//	q.setRecommendationStatus(TO_BE_PUT_UP);
				//	q.simpleMerge();
				// }
				Chart.getChartRepository().updateChartQuestions(chart, TO_BE_PUT_UP);
			}
			
			return chart;
		}
		
		return null;
	}
	
	/**
	 * Algorithm:
	 * 1. Check if internalStatus of @param q is "ASSISTANT_PROCESSED"?
	 * 
	 * 2. If the Question is submitted for First Batch do the following:
	 * 	a> Find latest Chart.
	 * 	b> If chart.answeringDate is the last answeringDate for 
	 *     chart.group then simply add the @param q to the Chart.
	 * 	c> If chart.answeringDate is not the last answeringDate for
	 *     chart.group then use the algorithm "ADD TO CHART IF APPLICABLE" 
	 *     as mentioned in addToChartIfApplicable/3.
	 * 
	 * 3. If the Question is submitted for the Second Batch do the following:
	 * 	a> If @param q does not specify any answeringDate (q.answeringDate == null)
	 *     then beginning from first Chart for the group, find if @param q 
	 *     could fit into the Chart using algorithm "ADD TO CHART IF APPLICABLE" as 
	 *     mentioned in addToChartIfApplicable/3. Stop when @param q is successfully 
	 *     added to some Chart or when all Charts are exhausted.
	 * 	   
	 * 	b> If @param q specifies an answeringDate then beginning from Chart with
	 * 	   answeringDate == q.answeringDate, find if @param q could fit into the 
	 *     Chart using algorithm "ADD TO CHART IF APPLICABLE" as mentioned in 
	 *     addToChartIfApplicable/3. Stop when @param q is successfully added to some 
	 *     Chart or when all Charts are exhausted.
	 */
	private static Boolean addToChartUH(final Question q) {
		Boolean isAddedToChart = false;
		
		if(Chart.isAssistantProcessed(q)) {
			Session session = q.getSession();
			Group group = q.getGroup();
			Member member = q.getPrimaryMember();
			String locale = q.getLocale();
			
			if(Chart.isFirstBatchQuestionUH(q)) {
				Chart chart = Chart.findLatestChart(session, group, locale);
				if(chart != null) {
					Date chartAnsweringDate = chart.getAnsweringDate();
					
					if(Chart.isLastAnsweringDate(group, chartAnsweringDate)) {
						List<Question> onChartQuestions = Chart.findQuestions(member, session, 
								group, chartAnsweringDate, locale);
						
						// The Questions taken on the Chart should have status "TO_BE_PUT_UP"
						Status TO_BE_PUT_UP = 
							Status.findByType("question_before_workflow_tobeputup", locale);
						q.setInternalStatus(TO_BE_PUT_UP);
						q.setRecommendationStatus(TO_BE_PUT_UP);
						q.simpleMerge();
						
						onChartQuestions.add(q);
						onChartQuestions = Chart.updateCandidateQuestions(onChartQuestions, 
								chartAnsweringDate);
						
						ChartEntry ce = Chart.find(chart.getChartEntries(), member);
						ce.setQuestions(onChartQuestions);
						ce.merge();
						
						isAddedToChart = true;
					}
					else {
						Integer maxNoOfQuestions = Chart.maxQuestionsOnChartUH();
						isAddedToChart = Chart.addToChartIfApplicable(chart, q, maxNoOfQuestions);
					}
				}
			}
			else if(Chart.isSecondBatchQuestionUH(q)) {
				Integer maxNoOfQuestions = Chart.maxQuestionsOnChartUH();
				List<Date> answeringDates = new ArrayList<Date>();
				if(q.getAnsweringDate() == null) {
					answeringDates = group.getAnsweringDates(ApplicationConstants.ASC);
				}
				else {
					answeringDates = Chart.getAnsweringDatesGTEQ(group, 
							q.getAnsweringDate().getAnsweringDate());
				}
				for(Date answeringDate : answeringDates) {
					Chart chart = Chart.find(session, group, answeringDate, locale);
					if(chart != null) {
						if(Chart.addToChartIfApplicable(chart, q, maxNoOfQuestions)) {
							isAddedToChart = true;
							break;
						}
					}
				}
			}
		}
		
		return isAddedToChart;
	}
	
	/**
	 * If the @param question is a First batch Question then remove the Question
	 * from the "affectedGroup" Chart (if it is at all taken on that Chart).
	 * 
	 * If the @param question is a Second batch Question then remove the Question 
	 * from the "affectedGroup" Chart (if it is at all taken on that Chart). Since
	 * 1 Question has left the Chart, find if there is another eligible Question 
	 * which could be added to the Chart. Following is the algorithm:
	 * 1. Consider the Questions with status = "ASSISTANT_PROCESSED" for Chart.
	 * 
	 * 2. Select the Questions which have the answeringDate attribute 
	 * explicitly set to the expected answeringDate.
	 * 
	 * 3. Select the Questions which have an answeringDate attribute 
	 * explicitly set to a date before the expected answeringDate.
	 * 
	 * 4. Select the Questions which don't have any answeringDate.
	 * 
	 * 5. If any Question is selected for the Chart then set its internalStatus
	 * to "TO_BE_PUT_UP"
	 * 
	 * @param question
	 * @param affectedGroup the group from which this question was removed
	 */
	private static void groupChangeUH(final Question question, final Group affectedGroup) {
		Session session = question.getSession();
		Member member = question.getPrimaryMember();
		String locale = question.getLocale();
		
		// Find the Chart to which this Question belongs. 
		// Returns null if this Question does not belong to any Chart.
		Chart chart = Chart.find(question);
		if(chart != null) {
			ChartEntry ce = Chart.find(chart.getChartEntries(), member);
			List<Question> questions = ce.getQuestions();
			int index = -1;
			for(Question q : questions) {
				++index;
				if(q.getId().equals(question.getId())) {
					break;
				}
			}
			questions.remove(index);
			
			if(Chart.isSecondBatchQuestionUH(question) == true) {
				// Since 1 question has left the group so add 1 question to 
				// the chart. Hence maxNoOfQuestions = 1
				Question q = Chart.onGroupChangeAddQuestion(session, member, affectedGroup, 
						chart.getAnsweringDate(), 
						questions.toArray(new Question[0]), locale);
				
				if(q != null) {
					// The Questions taken on the Chart should have status "TO_BE_PUT_UP"
					Status TO_BE_PUT_UP = 
						Status.findByType("question_before_workflow_tobeputup", locale);
					q.setInternalStatus(TO_BE_PUT_UP);
					q.setRecommendationStatus(TO_BE_PUT_UP);
					q.simpleMerge();
					
					questions.add(q);
				}
			}
			
			ce.setQuestions(questions);
			ce.merge();
		}
	}
	
	private static String processAllRemainingQnsForLastDateUH() {
		CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, 
				"COUNCIL_PROCESS_ALL_REMAINING_QNS_FOR_LASTDATE", "");
		return parameter.getValue();
	}
	
	private static Integer maxQuestionsOnChartUH() {
		CustomParameter noOfQuestionsParameter = CustomParameter.
			findByFieldName(CustomParameter.class, "name", "NO_OF_QUESTIONS_ON_MEMBER_CHART_UH", "");
		return Integer.valueOf(noOfQuestionsParameter.getValue());
	}
	
	/**
	 * Use this Charting algorithm when creating a ChartEntry (for the first
	 * time).
	 * 
	 * Search for at most @param maxNoOfQuestions according to the following
	 * algorithm. Search only for those Questions which are submitted between
	 * @param startTime and @param endTime (both time inclusive) and are verified 
	 * by the assistant ("ASSISTANT_PROCESSED")
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
			final Status ASSISTANT_PROCESSED,
			final String locale) {
		List<Question> candidateQList = new ArrayList<Question>();
		
		Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
		
		// List of Questions having an explicit answeringDate attribute set
		// to current date or previous answering date(s)
		List<Question> datedQuestions = Question.findDatedQuestions(session, member, deviceType, 
				group, answeringDate, startTime, endTime, internalStatuses, locale);
		candidateQList.addAll(datedQuestions);
		
		// List of Questions without any answering date
		List<Question> nonDatedQuestions = Question.findNonAnsweringDate(session, member, 
				deviceType, group, startTime, endTime, internalStatuses, ApplicationConstants.ASC, 
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
	 * @param startTime and @param endTime (both time inclusive) and are verified 
	 * by the assistant ("ASSISTANT_PROCESSED")
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
			final Status ASSISTANT_PROCESSED,
			final String locale) {
		List<Question> candidateQList = new ArrayList<Question>();
		int maxQ = maxQuestionsOnChart;
		
		// List of Questions having an explicit answeringDate attribute set
		// to current date or previous answering date(s)
		if(maxQ > 0) {
			Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
			
			List<Question> questions = Question.findDatedQuestions(session, member, deviceType, 
					group, answeringDate, startTime, endTime, internalStatuses, maxQ, 
					locale);
			
			candidateQList.addAll(questions);
			maxQ = maxQ - questions.size();
		}
		
		// List of Questions without any answering date
		if(maxQ > 0) {
			Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
			
			List<Question> questions = Question.findNonAnsweringDate(session, member, deviceType, 
					group, startTime, endTime, internalStatuses, maxQ, ApplicationConstants.ASC, 
					locale);
			
			candidateQList.addAll(questions);
		}
		
		ChartEntry chartEntry = new ChartEntry(member, candidateQList, locale);
		return chartEntry;
	}
	
	private static boolean isFirstBatchQuestionUH(final Question question) {
		Session session = question.getSession();
		Date startTime = session.getQuestionSubmissionFirstBatchStartDateUH();
		Date endTime = session.getQuestionSubmissionFirstBatchEndDateUH();
		Date submissionTime = question.getSubmissionDate();
		if((submissionTime.compareTo(startTime) >= 0) &&
				(submissionTime.compareTo(endTime) <= 0)) {
			return true;
		}
		return false;
	}
	
	private static boolean isSecondBatchQuestionUH(final Question question) {
		Session session = question.getSession();
		Date startTime = session.getQuestionSubmissionSecondBatchStartDateUH();
		Date endTime = session.getQuestionSubmissionSecondBatchEndDateUH();
		Date submissionTime = question.getSubmissionDate();
		if((submissionTime.compareTo(startTime) >= 0) &&
				(submissionTime.compareTo(endTime) <= 0)) {
			return true;
		}
		return false;
	}
	
	
	//=============== ASSEMBLY METHODS ================
	/**
	 * Creates a new Chart. If a chart already exists then returns the
	 * existing Chart. If a previous dated Chart exists & is unprocessed
	 * then don't create a new Chart and return null.
	 */
	private Chart createLH() {
		boolean isPreviousChartProcessed = Chart.isPreviousChartProcessed(this.getSession(), 
				this.getGroup(), this.getAnsweringDate(), this.getLocale());
		
		if(isPreviousChartProcessed == true) {
			Chart chart = Chart.find(this.getSession(), this.getGroup(), this.getAnsweringDate(), 
					this.getLocale());
			
			if(chart == null) {
				Date currentDate = Chart.getCurrentDate();
				List<Member> activeMembers = Member.findActiveMembers(this.getSession().getHouse(), 
						currentDate, ApplicationConstants.ASC, this.getLocale());
				
				Integer maxQuestionsOnChart = Chart.maxQuestionsOnChartLH();
				DeviceType deviceType = DeviceType.findByType("questions_starred", this.getLocale());
				Date finalSubmissionDate = this.getGroup().
					getFinalSubmissionDate(this.getAnsweringDate());
				Status ASSISTANT_PROCESSED = 
					Status.findByType("question_assistantprocessed", this.getLocale());
				
				for(Member m : activeMembers) {
					ChartEntry chartEntry = Chart.newChartEntryLH(this.getSession(), m, 
							deviceType, this.getGroup(), this.getAnsweringDate(), 
							finalSubmissionDate, maxQuestionsOnChart, ASSISTANT_PROCESSED, 
							this.getLocale());
					this.getChartEntries().add(chartEntry);
				}
				chart = (Chart) this.persist();
				
				Status TO_BE_PUT_UP = 
					Status.findByType("question_before_workflow_tobeputup", this.getLocale());
				// List<Question> questions = Chart.findQuestions(this.getSession(), this.getGroup(), 
				//		this.getAnsweringDate(), this.getLocale());
				// for(Question q : questions) {
				//	q.setInternalStatus(TO_BE_PUT_UP);
				//	q.setRecommendationStatus(TO_BE_PUT_UP);
				//	q.simpleMerge();
				// }
				Chart.getChartRepository().updateChartQuestions(chart, TO_BE_PUT_UP);
			}
			
			return chart;
		}
		
		return null;
	}	
	
	/**
	 * Algorithm:   
	 * 1. Check if Question internalStatus is "ASSISTANT PROCESSED"?
	 * 2. Check if a latest chart exists?
	 * 3. Check if Question is eligible to be added to the Chart?
	 * If answer to all 1, 2, 3 is YES then proceed to Step 4
	 * 4. Use the algorithm "ADD TO CHART IF APPLICABLE" as mentioned
	 *    in addToChartIfApplicable/3
	 */
	private static Boolean addToChartLH(final Question q) {
		if(Chart.isAssistantProcessed(q)) {
			Session session = q.getSession();
			Group group = q.getGroup();
			String locale = q.getLocale();
			
			Chart chart = Chart.findLatestChart(session, group, locale);
			if(chart != null) {
				if(isEligibleForChartLH(chart, q) == true) {
					Integer maxNoOfQuestions = Chart.maxQuestionsOnChartLH();
					return Chart.addToChartIfApplicable(chart, q, maxNoOfQuestions);
				}
			}
		}
		return false;
	}
	
	/**
	 * Removes the Question from the "affectedGroup" Chart (if it is at all taken on that
	 * Chart). Since 1 Question has left the Chart, find if there is another eligible 
	 * Question which could be added to the Chart. Following is the algorithm:
	 * 
	 * 1. Consider the Questions with status = "ASSISTANT_PROCESSED" for Chart.
	 * 
	 * 2. Select the Questions which have the answeringDate attribute 
	 * explicitly set to the expected answeringDate.
	 * 
	 * 3. Select the Questions which have an answeringDate attribute 
	 * explicitly set to a date before the expected answeringDate.
	 * 
	 * 4. Select the Questions which don't have any answeringDate.
	 * 
	 * 5. If any Question is selected for the Chart then set its internalStatus
	 * to "TO_BE_PUT_UP"
	 * 
	 * @param question
	 * @param affectedGroup the group from which this question was removed
	 */
	private static void groupChangeLH(final Question question, final Group affectedGroup) {
		Session session = question.getSession();
		Member member = question.getPrimaryMember();
		String locale = question.getLocale();
		
		// Find the Chart to which this Question belongs. 
		// Returns null if this Question does not belong to any Chart.
		Chart chart = Chart.find(question);
		if(chart != null) {
			ChartEntry ce = Chart.find(chart.getChartEntries(), member);
			List<Question> questions = ce.getQuestions();
			int index = -1;
			for(Question q : questions) {
				++index;
				if(q.getId().equals(question.getId())) {
					break;
				}
			}
			questions.remove(index);
			
			// Since 1 question has left the group so add 1 question to the chart. Hence 
			// maxNoOfQuestions = 1
			Question q = Chart.onGroupChangeAddQuestion(session, member, affectedGroup,
					chart.getAnsweringDate(),
					questions.toArray(new Question[0]), locale);
			if(q != null) {
				// The Questions taken on the Chart should have status "TO_BE_PUT_UP"
				Status TO_BE_PUT_UP = 
					Status.findByType("question_before_workflow_tobeputup", locale);
				q.setInternalStatus(TO_BE_PUT_UP);
				q.setRecommendationStatus(TO_BE_PUT_UP);
				q.simpleMerge();
				
				questions.add(q);
			}
			
			ce.setQuestions(questions);
			ce.merge();
		}
	}
	
	private static Integer maxQuestionsOnChartLH() {
		CustomParameter noOfQuestionsParameter = CustomParameter.
			findByFieldName(CustomParameter.class, "name", "NO_OF_QUESTIONS_ON_MEMBER_CHART_LH", "");
		return Integer.valueOf(noOfQuestionsParameter.getValue());
	}
	
	/**
	 * Use this Charting algorithm when creating a ChartEntry (for the first
	 * time).
	 * 
	 * Search for at most @param maxNoOfQuestions according to the following
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
			final Status ASSISTANT_PROCESSED,
			final String locale) {
		List<Question> candidateQList = new ArrayList<Question>();
		int maxQ = maxQuestionsOnChart;
		
		// List of Questions having an explicit answeringDate attribute set
		// to current date or previous answering date(s)
		if(maxQ > 0) {
			Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
			
			List<Question> questions = Question.findDatedQuestions(session, member, deviceType, 
					group, answeringDate, finalSubmissionDate, internalStatuses, maxQ, 
					locale);
			
			candidateQList.addAll(questions);
			maxQ = maxQ - questions.size();
		}
		
		// List of Questions without any answering date
		if(maxQ > 0) {
			Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
			
			List<Question> questions = Question.findNonAnsweringDate(session, member, deviceType, 
					group, finalSubmissionDate, internalStatuses, maxQ, ApplicationConstants.ASC, 
					locale);
			
			candidateQList.addAll(questions);
		}
		
		ChartEntry chartEntry = new ChartEntry(member, candidateQList, locale);
		return chartEntry;
	}
	
	
	
	/**
	 * 1. Check if Question.submissionDate <= chart.finalSubmissionDate?
	 * 2. Check if Question.answeringDate == null OR
	 * 		Question.answeringDate == LatestChart.answeringDate OR
	 * 		Question.answeringDate < LatestChart.answeringDate? (Case of Group Change)
	 * 3. If the answer to 1 and 2 is YES then return true
	 */
	private static boolean isEligibleForChartLH(final Chart chart, final Question q) {
		Date chartAnsweringDate = chart.getAnsweringDate();
		Date questionSubmissionDate = q.getSubmissionDate();
		Date finalSubmissionDate = q.getGroup().getFinalSubmissionDate(chartAnsweringDate);
		int flag = questionSubmissionDate.compareTo(finalSubmissionDate);
		if(flag <= 0) { 
			QuestionDates questionAnsweringDate = q.getAnsweringDate();
			if(questionAnsweringDate == null) {
				return true;
			}
			else if(questionAnsweringDate.getAnsweringDate().compareTo(chartAnsweringDate) <= 0) {
				return true;
			}
		}
		return false;
	}

	
	//=============== COMMON INTERNAL METHODS =======
	private static ChartRepository getChartRepository() {
		ChartRepository repository = new Chart().repository;
		if(repository == null) {
			throw new IllegalStateException(
				"ChartRepository has not been injected in Chart Domain");
		}
		return repository;
	}
	
	/**
	 * Algorithm: "ADD TO CHART IF APPLICABLE"
	 * 1. If a member has less than 5 Questions on Chart simply add the Question
	 *    to the chart, & update the chart. Set the status of Question to 
	 *    "TO_BE_PUT_UP"
	 * 
	 * 2. If a member has exactly 5 questions then, 
	 * 	a> The Questions which are in the Workflow (internalStatus != "TO_BE_PUT_UP") 
	 *     wont get affected.
	 *    
	 *  b> The questions which are not in Workflow (internalStatus = "TO_BE_PUT_UP"),
	 *     will compete with @param q for a slot in Chart. At the end of this step all
	 *     the Questions on the Chart which are not in the Workflow will have 
	 *     internalStatus = "TO_BE_PUT_UP". The Question which leaves the Chart will
	 *     have internalStatus = "ASSISTANT_PROCESSED".
	 *     
	 * Constraints: 
	 * 1> If this question is added to the chart, it's internalStatus should 
	 * change to "TO_BE_PUT_UP". 
	 * 
	 * 2> In lieu of this question entering the Chart, if some Question leaves 
	 * the Chart then the internalStatus of that Question should be set to 
	 * "ASSISTANT_PROCESSED". 
	 * 
	 * 3> The internalStatuses of the rest of the Questions on the Chart should 
	 * remain unaffected.
	 * 
	 * Returns true if the @param q is added to the @param chart, else returns
	 * false
	 */
	private static boolean addToChartIfApplicable(final Chart chart, 
			final Question q,
			final Integer maxNoOfQuestions) {
		boolean isAddedToChart = false;
		
		Member member = q.getPrimaryMember();
		Session session = q.getSession();
		Group group = q.getGroup();
		Date chartAnsweringDate = chart.getAnsweringDate();
		String locale = q.getLocale();
		
		Status TO_BE_PUT_UP = Status.findByType("question_before_workflow_tobeputup", locale);
		Status ASSISTANT_PROCESSED = Status.findByType("question_assistantprocessed", locale);
		
		List<Question> onChartQuestions = 
			Chart.findQuestions(member, session, group, chartAnsweringDate, locale);
		
		if(onChartQuestions.size() == maxNoOfQuestions) {
			List<Question> updateChartQuestions = new ArrayList<Question>();
			List<Question> questionsInWorkflow = Chart.questionsInWorkflow(onChartQuestions, locale);
			updateChartQuestions.addAll(questionsInWorkflow);
			
			int requiredQuestions = maxNoOfQuestions - updateChartQuestions.size();
			List<Question> questionsNotInWorkflow = 
				Chart.questionsNotInWorkflow(onChartQuestions, locale);
			questionsNotInWorkflow.add(q);
			
			// The size of candidateQuestions will always be size of questionsNotInWorkflow
			// + 1 (Question q as provided in the parameter)
			List<Question> candidateQuestions = 
				Chart.updateCandidateQuestions(questionsNotInWorkflow, chartAnsweringDate);
			
			// The Questions taken on the Chart should have status "TO_BE_PUT_UP". The nature
			// of candidateQuestions is such that the last question in that list will 
			// always leave the Chart.
			int size = candidateQuestions.size();
			for(int i = 0; i < size - 1; i++) {
				Question qn = candidateQuestions.get(i);
				qn.setInternalStatus(TO_BE_PUT_UP);
				qn.setRecommendationStatus(TO_BE_PUT_UP);
				qn.simpleMerge();
				
				if(qn.getId().equals(q.getId())) {
					isAddedToChart = true;
				}
			}
			Question qn = candidateQuestions.get(size - 1);
			qn.setInternalStatus(ASSISTANT_PROCESSED);
			qn.setRecommendationStatus(ASSISTANT_PROCESSED);
			qn.simpleMerge();
			
			if(candidateQuestions.size() >= requiredQuestions) {
				updateChartQuestions.addAll(candidateQuestions.subList(0, requiredQuestions));
			}
			
			ChartEntry ce = Chart.find(chart.getChartEntries(), member);
			ce.setQuestions(updateChartQuestions);
			ce.merge();
		}
		else if(onChartQuestions.size() < maxNoOfQuestions) {
			List<Question> updateChartQuestions = new ArrayList<Question>();
			updateChartQuestions.addAll(onChartQuestions);
			
			// The Questions taken on the Chart should have status "TO_BE_PUT_UP"
			q.setInternalStatus(TO_BE_PUT_UP);
			q.setRecommendationStatus(TO_BE_PUT_UP);
			q.simpleMerge();
			
			updateChartQuestions.add(q);
			
			updateChartQuestions = 
				Chart.updateCandidateQuestions(updateChartQuestions, chartAnsweringDate);
			
			ChartEntry ce = Chart.find(chart.getChartEntries(), member);
			ce.setQuestions(updateChartQuestions);
			ce.merge();
			isAddedToChart = true;
		}
		
		return isAddedToChart;
	}
	
	private static Question onGroupChangeAddQuestion(final Session session,
			final Member member,
			final Group group,
			final Date answeringDate,
			final Question[] excludeQuestions,
			final String locale) {
		DeviceType deviceType = DeviceType.findByType("questions_starred", locale);
		Date finalSubmissionDate = group.getFinalSubmissionDate(answeringDate);
		Status ASSISTANT_PROCESSED = Status.findByType("question_assistantprocessed", locale);
		Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
		
		// Since 1 question has left the group so add 1 question to the chart. Hence 
		// maxNoOfQuestions = 1
		int maxNoOfQuestions = 1;
		
		List<Question> datedQuestions = Question.find(session, member, deviceType, group, 
				answeringDate, finalSubmissionDate, internalStatuses, excludeQuestions, 
				maxNoOfQuestions, ApplicationConstants.ASC, locale);
			
		if(datedQuestions.size() == 1) {
			return datedQuestions.get(0);
		}
		
		List<Question> previousDatedQuestions = Question.findBeforeAnsweringDate(session, member, 
				deviceType, group, answeringDate, finalSubmissionDate, internalStatuses, 
				excludeQuestions, maxNoOfQuestions, ApplicationConstants.ASC, locale);
			
		if(previousDatedQuestions.size() == 1) {
			return previousDatedQuestions.get(0);
		}
		
		List<Question> nonDatedQuestions = Question.findNonAnsweringDate(session, member, 
				deviceType, group, finalSubmissionDate, internalStatuses, excludeQuestions, 
				maxNoOfQuestions, ApplicationConstants.ASC, locale);
			
		if(nonDatedQuestions.size() == 1) {
			return nonDatedQuestions.get(0);
		}
		
		return null;
	}
	
	/**
	 * Orders the @param questions in order defined as follows:
	 * 1. Dated Questions sorted by number in increasing order
	 * 2. Previously dated Questions sorted by answeringDate in increasing order. 
	 *    Break the tie using Question number
	 * 3. Non dated Questions sorted by number in increasing order
	 */
	private static List<Question> updateCandidateQuestions(final List<Question> questions,
			final Date answeringDate) {
		List<Question> datedQList = new ArrayList<Question>();
		List<Question> beforeDatedQList = new ArrayList<Question>();
		List<Question> nonDatedQList = new ArrayList<Question>();
		
		for(Question q : questions) {
			if(q.getAnsweringDate() == null) {
				nonDatedQList.add(q);
			}
			else {
				if(q.getAnsweringDate().getAnsweringDate().compareTo(answeringDate) == 0) {
					datedQList.add(q);
				}
				else if(q.getAnsweringDate().getAnsweringDate().compareTo(answeringDate) < 0) {
					beforeDatedQList.add(q);
				}
			}
		}
		
		datedQList = Question.sortByNumber(datedQList, ApplicationConstants.ASC);
		beforeDatedQList = Question.sortByAnsweringDate(beforeDatedQList, ApplicationConstants.ASC);
		nonDatedQList = Question.sortByNumber(nonDatedQList, ApplicationConstants.ASC);
		
		List<Question> candidateQList = new ArrayList<Question>();
		candidateQList.addAll(datedQList);
		candidateQList.addAll(beforeDatedQList);
		candidateQList.addAll(nonDatedQList);
		return candidateQList;
	}
	
	private static Boolean isPreviousChartProcessed(final Session session, 
			final Group group, 
			final Date answeringDate,
			final String locale) {
		Date previousAnsweringdate = Chart.getPreviousAnsweringDate(group, answeringDate);
		if(previousAnsweringdate != null) {
			return Chart.isProcessed(session, group, previousAnsweringdate, locale);
		}
		return true;
	}
	
	/**
	 * The Questions on Chart have status "TO_BE_PUT_UP". Hence, only those 
	 * Questions on Chart with internalStatus != "TO_BE_PUT_UP" and
	 * internalStatus not beginning with "question_before_workflow" are in
	 * the Workflow.
	 */
	private static List<Question> questionsInWorkflow(final List<Question> questions,
			final String locale) {
		List<Question> qList = new ArrayList<Question>();
		String TO_BE_PUT_UP = "question_before_workflow_tobeputup";
		for(Question q : questions) {
			String type = q.getInternalStatus().getType();
			if(! (type.equals(TO_BE_PUT_UP) || type.startsWith("question_before_workflow"))) {
				qList.add(q);
			}
		}
		return qList;
	}
	
	/**
	 * The Questions on Chart have status "TO_BE_PUT_UP". Hence, only those 
	 * Questions on Chart with internalStatus != "TO_BE_PUT_UP" and
	 * internalStatus not beginning with "question_before_workflow" are in
	 * the Workflow.
	 */
	private static List<Question> questionsNotInWorkflow(final List<Question> questions,
			final String locale) {
		List<Question> qList = new ArrayList<Question>();
		String TO_BE_PUT_UP = "question_before_workflow_tobeputup";
		for(Question q : questions) {
			String type = q.getInternalStatus().getType();
			if(type.equals(TO_BE_PUT_UP) || type.startsWith("question_before_workflow")) {
				qList.add(q);
			}
		}
		return qList;
	}

	/**
	 * Returns null if @param answeringDate is the first answeringDate 
	 * of the @param group, else returns previous answeringDate.
	 */
	private static Date getPreviousAnsweringDate(final Group group,
			final Date answeringDate) {
		List<Date> answeringDates = group.getAnsweringDates(ApplicationConstants.DESC);
		for(Date d : answeringDates) {
			if(d.compareTo(answeringDate) < 0) {
				return d;
			}
		}
		return null;
	}
	
	/**
	 * If the @param answeringDate is the last answering date for the
	 * @param group then return true, else return false.
	 */
	private static boolean isLastAnsweringDate(final Group group, 
			final Date answeringDate) {
		List<Date> answeringDates = group.getAnsweringDates(ApplicationConstants.DESC);
		if(answeringDates.size() > 0) {
			Date date = answeringDates.get(0);
			if(date.compareTo(answeringDate) == 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a list of @param group answeringDates greater than or equal to 
	 * @param answeringDate.
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
	
	private static boolean isAssistantProcessed(Question q) {
		String ASSISTANT_PROCESSED = "question_assistantprocessed";
		Status internalStatus = q.getInternalStatus();
		if(internalStatus.getType().equals(ASSISTANT_PROCESSED)) {
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
	
	private static Date getCurrentDate() {
		CustomParameter dbDateFormat =
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		return DateUtil.getCurrentDate(dbDateFormat.getValue());
	}	

	
	//=============== GETTERS/SETTERS ===============
	public Session getSession() {
		return session;
	}

	private void setSession(final Session session) {
		this.session = session;
	}

	public Group getGroup() {
		return group;
	}

	private void setGroup(final Group group) {
		this.group = group;
	}

	public Date getAnsweringDate() {
		return answeringDate;
	}

	private void setAnsweringDate(final Date answeringDate) {
		this.answeringDate = answeringDate;
	}

	public List<ChartEntry> getChartEntries() {
		return chartEntries;
	}

	public void setChartEntries(final List<ChartEntry> chartEntries) {
		this.chartEntries = chartEntries;
	}
	
}