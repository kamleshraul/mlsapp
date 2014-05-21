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

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.repository.ChartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Chart.
 * 
 * @author amitd
 * @author anandk
 * @author vikasg
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="charts")
public class Chart extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 292896040851837645L;

	//=============== ATTRIBUTES ====================
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="group_id")
	private Group group;
	
	@Temporal(TemporalType.DATE)
	private Date answeringDate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private DeviceType deviceType;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="charts_chart_entries",
			joinColumns={ @JoinColumn(name="chart_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="chart_entry_id", referencedColumnName="id") })
	private List<ChartEntry> chartEntries;
	
	@Autowired
	private transient ChartRepository repository;
	
	
	//=============== CONSTRUCTORS ==================
	/**
	 * Not to be used. Kept here because JPA needs an 
	 * Entity to have a default public Constructor.
	 */
	public Chart() {
		super();
	}

	/**
	 * Use this constructor to construct a new Starred Question instance.
	 */
	public Chart(final Session session,
			final Group group,
			final Date answeringDate,
			final DeviceType deviceType,
			final String locale) {
		super(locale);
		this.setSession(session);
		this.setGroup(group);
		this.setDeviceType(deviceType);
		this.setAnsweringDate(answeringDate);
		this.setChartEntries(new ArrayList<ChartEntry>());
	}
	
	/**
	 * Use this constructor to construct a new Non Official Resolution instance.
	 */
	public Chart(final Session session, 
			final DeviceType deviceType,
			final String locale) {
		super(locale);
		this.setSession(session);
		this.setDeviceType(deviceType);
		this.setChartEntries(new ArrayList<ChartEntry>());
	}

	
	//=============== VIEW METHODS ==================
	/**
	 * Returns null if Chart does not exist for the specified parameters
	 * OR
	 * Returns an empty list if there are no Questions asked by any Member
	 * OR
	 * Returns a list of ChartVOs.
	 * @throws ELSException 
	 */
	public static List<ChartVO> getChartVOs(final Session session,
						final Group group,
						final Date answeringDate,
						final DeviceType deviceType,
						final String locale) throws ELSException {		
		if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
			return QuestionChart.getChartVOs(session, group, answeringDate, deviceType, locale);
		}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			return QuestionChart.getChartVOs(session, group, answeringDate, deviceType, locale);
		}
		
		return null;
	}
	
	/**
	 * Returns null if Chart does not exist for the specified parameters
	 * OR
	 * Returns an empty list if there are no Questions asked by any Member
	 * OR
	 * Returns a list of ChartVOs.
	 * @throws ELSException 
	 */
	public static List<ChartVO> getChartVOs(final Session session,
			final DeviceType deviceType,
			final String locale) throws ELSException {		
		if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return ResolutionChart.getChartVOs(session, deviceType, locale);
		}
		
		return null;
	}
	
	public static List<ChartVO> getAdmittedChartVOs(final Session session,
			final DeviceType deviceType,
			final String locale) throws ELSException {		
		if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return ResolutionChart.getAdmittedChartVOs(session, deviceType, locale);
		} else {
			return null;
		}		
	}
	
	/**
	 * Finds the maximum Questions on Chart against any member.
	 * Useful for Council where all the un-charted Questions of
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
		return QuestionChart.maxChartedQuestions(session, group, answeringDate, deviceType, locale);
	}
	
	public static Integer maxChartedResolutions(final Session session,
			final String locale) throws ELSException {
		return ResolutionChart.maxChartedResolutions(session, locale);
	}
	
	
	//=============== DOMAIN METHODS ================
	public static ChartRepository getChartRepository() {
		ChartRepository repository = new Chart().repository;
		
		if(repository == null) {
			throw new IllegalStateException(
				"ChartRepository has not been injected in Chart Domain");
		}
		
		return repository;
	}
	
	/**
	 * Creates a new Chart. If a chart already exists then returns the
	 * existing Chart. If a previous dated Chart exists & is unprocessed
	 * then don't create a new Chart and return null.
	 * @throws ELSException 
	 */
	public Chart create() throws ELSException {
		String strDeviceType = this.getDeviceType().getType();
		
		if(strDeviceType.equals(ApplicationConstants.STARRED_QUESTION)) {
			return QuestionChart.create(this);
		}else if(strDeviceType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return ResolutionChart.create(this);
		}else if(strDeviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			return QuestionChart.create(this);
		}			
		
		return null;
	}
	
	public Boolean isProcessed() throws ELSException {
		String strDeviceType = this.getDeviceType().getType();
		
		if(strDeviceType.equals(ApplicationConstants.STARRED_QUESTION)) {
			return QuestionChart.isProcessed(this);
		}
		else if(strDeviceType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return ResolutionChart.isProcessed(this);
		}
		
		return false;
	}
	
	/**
	 * Returns true if @param device is added to Chart, else returns false.
	 * @throws ELSException 
	 */
	public static Boolean addToChart(final Device device) throws ELSException {
		if(device instanceof Question) {
			Question question = (Question) device;
			return QuestionChart.addToChart(question); 
		}
		else if(device instanceof Resolution) {
			Resolution resolution = (Resolution) device;
			return ResolutionChart.addToChart(resolution);
		}
		
		return false;
	}
	
	public static void groupChange(final Device device, 
			final Group affectedGroup) throws ELSException {
		if(device instanceof Question) {
			Question question = (Question) device;
			QuestionChart.groupChange(question, affectedGroup);
		}
	}
	
	/**
	 * Find the Chart to which @param device belongs.
	 * Returns null if this device does not belong to any Chart.
	 * @throws ELSException 
	 */
	public static Chart find(final Device device) throws ELSException {
		return Chart.getChartRepository().find(device);
	}
	
	/**
	 * Returns null if there is no Chart for the specified parameters.
	 * @throws ELSException 
	 */
	public static Chart find(final Chart chart) throws ELSException {
		String strDeviceType = chart.getDeviceType().getType();
		
		if(strDeviceType.equals(ApplicationConstants.STARRED_QUESTION)) {
			return QuestionChart.find(chart.getSession(), chart.getGroup(), 
					chart.getAnsweringDate(), chart.getDeviceType(), chart.getLocale());
		}else if(strDeviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			return QuestionChart.find(chart.getSession(), chart.getDeviceType(), chart.getLocale());
		}else if(strDeviceType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return ResolutionChart.find(chart.getSession(), chart.getDeviceType(), 
					chart.getLocale());
		}
		
		return null;
	}	
	
	/**
	 * For @param group, check for existence of a Chart for a given
	 * answeringDate in the descending order of the answering dates.
	 * 
	 * Returns null if there is no Chart for the specified parameters.
	 * @throws ELSException 
	 */
	public static Chart findLatestQuestionChart(final Session session,
			final Group group,
			final DeviceType deviceType,
			final String locale) throws ELSException {
		return QuestionChart.findLatestChart(session, group, deviceType, locale);
	}
	
	public static List<Device> findDevices(final Chart chart) throws ELSException {
		return Chart.getChartRepository().findDevices(chart);
	}
	
	public static List<Device> findDevices(final Member member,
			final Chart chart) throws ELSException {
		return Chart.getChartRepository().findDevices(member, chart);
	}
	
	/**
	 * Returns an unsorted list of Questions.
	 * OR
	 * Returns an empty list if there are no Questions.
	 * @throws ELSException 
	 */
	public static List<Question> findQuestions(final Session session, 
			final Group group, 
			final Date answeringDate,
			final DeviceType deviceType,
			final String locale) throws ELSException {
		return QuestionChart.findQuestions(session, group, answeringDate, deviceType, locale);
	}
	
	/**
	 * Returns the list of Questions of @param member taken on a Chart
	 * for the particular @param answeringDate.
	 * 
	 * Returns an empty list if there are no Questions for member.
	 * @throws ELSException 
	 */
	public static List<Question> findQuestions(final Member member,
			final Session session, 
			final Group group, 
			final Date answeringDate,
			final DeviceType deviceType,
			final String locale) throws ELSException {
		return QuestionChart.findQuestions(member, session, group, answeringDate, 
				deviceType, locale);
	}
	
	public static List<Resolution> findResolutions(final Session session,
			final DeviceType deviceType,
			final String locale) throws ELSException {
		return ResolutionChart.findResolutions(session, deviceType, locale);
	}
	
	public static List<Resolution> findResolutions(final Member member,
			final Session session,
			final DeviceType deviceType,
			final String locale) throws ELSException {
		return ResolutionChart.findResolutions(member, session, deviceType, locale);
	}
	
	/**
	 * Returns a list of Members on Chart.
	 * OR
	 * Returns an empty list if there are no Members.
	 * @throws ELSException 
	 */
	public static List<Member> findMembers(final Chart chart) throws ELSException {
		List<Member> members = new ArrayList<Member>();
		
		String strDeviceType = chart.getDeviceType().getType();
		if(strDeviceType.equals(ApplicationConstants.STARRED_QUESTION)) {
			members = QuestionChart.findMembers(chart.getSession(), chart.getGroup(),
					chart.getAnsweringDate(), chart.getDeviceType(), chart.getLocale());
		}
		else if(strDeviceType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			members = ResolutionChart.findMembers(chart.getSession(), chart.getDeviceType(), 
					chart.getLocale());
		}
		
		return members;
	}
	
	// All the List returning methods having sortOrder as a parameter
	
	
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

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(final DeviceType deviceType) {
		this.deviceType = deviceType;
	}
	
}


class QuestionChart {

	//=============== VIEW METHODS ==================
	public static List<ChartVO> getChartVOs(final Session session, 
			final Group group,
			final Date answeringDate, 
			final DeviceType deviceType,
			final String locale) throws ELSException {
		List<ChartVO> chartVOs = new ArrayList<ChartVO>();
		
		
		Chart chart = null;
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			chart = QuestionChart.find(session, deviceType, locale);
		}else{
			chart = QuestionChart.find(session, group, answeringDate, deviceType, locale);
		}
		if(chart != null) {
			List<ChartVO> chartVOsWithDevices = new ArrayList<ChartVO>();
			List<ChartVO> chartVOsWithoutdevices = new ArrayList<ChartVO>();
			
			List<ChartEntry> chartEntries = chart.getChartEntries();
			for(ChartEntry ce : chartEntries) {
				Long memberId = ce.getMember().getId();
				String memberName = ce.getMember().getFullnameLastNameFirst();
				List<DeviceVO> deviceVOs = QuestionChart.getDeviceVOs(ce.getDevices());
				
				if(deviceVOs.isEmpty()) {
					ChartVO chartVO = new ChartVO(memberId, memberName);
					chartVOsWithoutdevices.add(chartVO);
				}else {
					
					String rejectedHDSes="";
					int extraCount=0;
					List<Question> rejectedQuestions = Question.getRejectedQuestions(ce.getMember(), session, deviceType, locale);
					extraCount=Question.getQuestionWithoutNumber(ce.getMember(),deviceType,session,locale);
					if(deviceVOs.isEmpty()) {
						ChartVO chartVO = new ChartVO(memberId, memberName);
						chartVOsWithoutdevices.add(chartVO);
					}
					
					for(Question q: rejectedQuestions){
						if(rejectedQuestions.get(0).equals(q)){
							rejectedHDSes=q.getNumber().toString();
						}else{
							rejectedHDSes=rejectedHDSes+","+q.getNumber().toString();
						}
					}
					ChartVO chartVO = new ChartVO(memberId, memberName, deviceVOs);
					chartVO.setRejectedNotices(rejectedHDSes);
					chartVO.setExtraCount(extraCount);
					chartVOsWithDevices.add(chartVO);
				}
			}
			
			chartVOsWithDevices = ChartVO.sort(chartVOsWithDevices, ApplicationConstants.ASC, deviceType.getType());
			chartVOsWithoutdevices = ChartVO.sort(chartVOsWithoutdevices, ApplicationConstants.ASC, deviceType.getType());
			
			chartVOs.addAll(chartVOsWithDevices);
			chartVOs.addAll(chartVOsWithoutdevices);
		}
		else {
			chartVOs = null;
		}
		
		return chartVOs;
	}

	public static Integer maxChartedQuestions(final Session session, 
			final Group group,
			final Date answeringDate, 
			final DeviceType deviceType,
			final String locale) throws ELSException {
		Integer maxQuestions = 0;
		
		HouseType houseType = session.getHouse().getType();
		if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			maxQuestions = QuestionChart.maxQuestionsOnChartUH(deviceType);
		}
		else if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			maxQuestions = QuestionChart.maxQuestionsOnChartLH(deviceType);
		}
		
		Integer maxChartedQuestions = QuestionChart.findMaxChartedQuestions(session, group, answeringDate, deviceType, locale);
		if(maxChartedQuestions > maxQuestions) {
			maxQuestions = maxChartedQuestions;
		}
		
		return maxQuestions;
	}
	
	private static List<DeviceVO> getDeviceVOs(final List<Device> devices) {
		List<DeviceVO> deviceVOs = new ArrayList<DeviceVO>();
		
		for(Device d : devices) {
			Question q = (Question) d;
			if(q.getType() != null){
				if(q.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
					if(q.getInternalStatus() != null){
						if((!q.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_REJECTION)) && (!q.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_REPEATREJECTION))){
							String localisedStatusType=q.getInternalStatus().getName();
							DeviceVO deviceVO = new DeviceVO(q.getId(), 
									q.getNumber(),q.getInternalStatus().getType(),localisedStatusType);
							
							deviceVOs.add(deviceVO);
						}
					}
				}else{
					DeviceVO deviceVO = new DeviceVO(q.getId(),q.getNumber(),q.getInternalStatus().getType());
					if(q.getParent() == null) {
						deviceVO.setHasParent(false);
						String kids = QuestionChart.getClubbingsAsCommaSeparatedString(q.getClubbedEntities());
						deviceVO.setKids(kids);
					}else {
						deviceVO.setHasParent(true);
						deviceVO.setParent(String.valueOf(q.getParent().getNumber()));
					}
				
					deviceVOs.add(deviceVO);
				}
			}
		}
		
		return deviceVOs;
	}

	private static String getClubbingsAsCommaSeparatedString(
			final List<ClubbedEntity> clubbedEntities) {
		StringBuffer sb = new StringBuffer("");
		
		if(clubbedEntities != null) {
			int n = clubbedEntities.size();
			for(int i = 0; i < n; i++) {
				sb.append(clubbedEntities.get(i).getQuestion().getNumber());
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
		return Chart.getChartRepository().findMaxChartedQuestions(session, group, 
				answeringDate, deviceType, locale);
	}

	
	//=============== DOMAIN METHODS ================
	public static Chart create(final Chart chart) throws ELSException {
		HouseType houseType = chart.getSession().getHouse().getType();
		
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			return QuestionChart.createLH(chart);
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			return QuestionChart.createUH(chart);
		}
		
		return chart;
	}

	public static List<Member> findMembers(final Session session, 
			final Group group,
			final Date answeringDate, 
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().findMembers(session, group, answeringDate, 
				deviceType, locale);
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

	public static List<Question> findQuestions(final Session session, 
			final Group group,
			final Date answeringDate, 
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().findQuestions(session, group, answeringDate, 
				deviceType, locale);
	}

	public static void groupChange(final Question question, final Group affectedGroup) throws ELSException {
		Session session = question.getSession();
		HouseType houseType = session.getHouse().getType();
		
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			QuestionChart.groupChangeLH(question, affectedGroup);
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			QuestionChart.groupChangeUH(question, affectedGroup);
		}
	}

	public static Boolean addToChart(final Question question) throws ELSException {
		Session session = question.getSession();
		HouseType houseType = session.getHouse().getType();
		
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			return QuestionChart.addToChartLH(question);
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			return QuestionChart.addToChartUH(question);
		}
		
		return false;
	}

	public static Chart find(final Session session, 
			final Group group, 
			final Date answeringDate,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().find(session, group, answeringDate, deviceType, locale);
	}
	
	public static Chart find(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().find(session, deviceType, locale);
	}

	public static Chart findLatestChart(final Session session, 
			final Group group,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			Chart chart = QuestionChart.find(session, deviceType, locale); 
			if(chart != null) {
				return chart;
			}
		}else{
			List<Date> answeringDates = group.getAnsweringDates(ApplicationConstants.DESC);
		
			for(Date date : answeringDates) {
				Chart chart = QuestionChart.find(session, group, date, deviceType, locale); 
				if(chart != null) {
					return chart;
				}
			}
		}
		return null;
	}

	/**
	 * A Chart is said to be processed if all the Questions on the
	 * Chart have internalStatus type != 'question_before_workflow_tobeputup'.
	 * 
	 * Returns true if a Chart is processed or if a Chart does not exist,
	 * else returns false.
	 * @throws ELSException 
	 */
	public static Boolean isProcessed(final Chart chart) throws ELSException {
		Chart newChart = QuestionChart.find(chart.getSession(), chart.getGroup(), 
				chart.getAnsweringDate(), chart.getDeviceType(), chart.getLocale());
		
		if(newChart != null) {
			String excludeInternalStatus = ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP;
			return Chart.getChartRepository().isProcessed(chart, excludeInternalStatus);
		}
		
		return true;
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
	 *
	 * @return the chart
	 * @throws ELSException 
	 */
	private static Chart createUH(final Chart chart) throws ELSException {
		
		if(chart.getDeviceType() != null){
			if(chart.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				Chart newChart = Chart.find(chart);
				if(newChart == null) {
					newChart = QuestionChart.persistChartUH(chart);
					QuestionChart.updateChart(newChart);
				}
				return newChart;
			}else{
				Boolean isFirstAnsweringDate = QuestionChart.isFirstAnsweringDate(chart);
				Boolean isPreviousChartExists = QuestionChart.isPreviousChartExists(chart);
				Boolean isPreviousChartProcessed = QuestionChart.isPreviousChartProcessed(chart);
				
				if(isFirstAnsweringDate == true ||
						(isPreviousChartExists == true && isPreviousChartProcessed == true)) {
					Chart newChart = Chart.find(chart);
					if(newChart == null) {
						newChart = QuestionChart.persistChartUH(chart);
						QuestionChart.updateChart(newChart);
					}
					return newChart;
				}
			}
		}
		
		return null;
	}

	/**
	 * Algorithm:
	 * 1. Check if internalStatus of @param q is "ASSISTANT_PROCESSED"?
	 * 
	 * 2. If the Question is submitted for First Batch do the following:
	 * a> Find latest Chart.
	 * b> If chart.answeringDate is the last answeringDate for
	 * chart.group then simply add the @param q to the Chart.
	 * c> If chart.answeringDate is not the last answeringDate for
	 * chart.group then use the algorithm "ADD TO CHART IF APPLICABLE"
	 * as mentioned in addToChartIfApplicable/3.
	 * 
	 * 3. If the Question is submitted for the Second Batch do the following:
	 * a> If @param q does not specify any answeringDate (q.answeringDate == null)
	 * then beginning from first Chart for the group, find if @param q
	 * could fit into the Chart using algorithm "ADD TO CHART IF APPLICABLE" as
	 * mentioned in addToChartIfApplicable/3. Stop when @param q is successfully
	 * added to some Chart or when all Charts are exhausted.
	 * 
	 * b> If @param q specifies an answeringDate then beginning from Chart with
	 * answeringDate == q.answeringDate, find if @param q could fit into the
	 * Chart using algorithm "ADD TO CHART IF APPLICABLE" as mentioned in
	 * addToChartIfApplicable/3. Stop when @param q is successfully added to some
	 * Chart or when all Charts are exhausted.
	 *
	 * @param question the Question
	 * @return the Boolean
	 * @throws ELSException 
	 */
	private static Boolean addToChartUH(final Question question) throws ELSException {
		if(QuestionChart.isAssistantProcessed(question)) {
			if(QuestionChart.isFirstBatchQuestionUH(question)) {
				return QuestionChart.addToChartFirstBatchUH(question);
			}
			else if(QuestionChart.isSecondBatchQuestionUH(question)) {
				return QuestionChart.addToChartSecondBatchUH(question);
			}
		}
		
		return false;
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
	 * and recommendationStatus to "TO_BE_PUT_UP"
	 *
	 * @param question the question
	 * @param affectedGroup the group from which this question was removed
	 * @throws ELSException
	 * 
	 * TODO: What if the @param question which should leave the Chart
	 * is a clubbed question or has any clubbings? 
	 */
	private static void groupChangeUH(final Question question, 
			final Group affectedGroup) throws ELSException {
		Session session = question.getSession();
		Member member = question.getPrimaryMember();
		String locale = question.getLocale();
		
		Chart chart = Chart.find(question);
		if(chart != null) {
			ChartEntry ce = QuestionChart.find(chart.getChartEntries(), member);
			List<Device> devices = ce.getDevices();
			int index = -1;
			for(Device d : devices) {
				++index;
				if(d.getId().equals(question.getId())) {
					break;
				}
			}
			devices.remove(index);
			
			if(QuestionChart.isSecondBatchQuestionUH(question)) {
				// Since 1 question has left the group so add 1 question to
				// the chart. Hence maxNoOfQuestions = 1
				Question q = QuestionChart.onGroupChangeAddQuestion(session, member, affectedGroup,
						chart.getAnsweringDate(),
						devices.toArray(new Question[0]), locale);
				
				if(q != null) {
					// The Questions taken on the Chart should have status "TO_BE_PUT_UP"
					Status TO_BE_PUT_UP = Status.findByType(
							ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale);
					QuestionDates chartAnsweringDate = chart.getGroup().
						findQuestionDatesByGroupAndAnsweringDate(chart.getAnsweringDate());
					
					q.setInternalStatus(TO_BE_PUT_UP);
					q.setRecommendationStatus(TO_BE_PUT_UP);
					q.setChartAnsweringDate(chartAnsweringDate);
					if(q.getFile()==null){
						/**** Add Question to file ****/
						Reference reference=Question.findCurrentFile(q);
						q.setFile(Integer.parseInt(reference.getId()));
						q.setFileIndex(Integer.parseInt(reference.getName()));
						q.setFileSent(false);
					}
					q.simpleMerge();
			
					devices.add(q);
				}
			}
			
			ce.setDevices(devices);
			ce.merge();
		}
	}
	
	private static Chart persistChartUH(final Chart chart) {
		CustomParameter datePattern = 
			CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
		Date startTime = FormaterUtil.formatStringToDate(chart.getSession().getParameter(
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME_UH), 
				datePattern.getValue(), chart.getLocale());
		Date endTime = FormaterUtil.formatStringToDate(chart.getSession().getParameter(
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME_UH), 
				datePattern.getValue(), chart.getLocale());
			
		Status ASSISTANT_PROCESSED = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, chart.getLocale());
		Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
		
		
		List<ChartEntry> entriesForMembersWithQuestion =
			QuestionChart.chartEntriesForMembersWithQuestionUH(chart.getSession(), 
					chart.getDeviceType(), chart.getGroup(), chart.getAnsweringDate(), 
					startTime, endTime, internalStatuses, ApplicationConstants.ASC, 
					chart.getLocale());
		
		List<ChartEntry> entriesForMembersWithoutQuestion =
			QuestionChart.chartEntriesForMembersWithoutQuestionUH(chart.getSession(), 
					chart.getDeviceType(), chart.getGroup(), chart.getAnsweringDate(), 
					startTime, endTime, internalStatuses, ApplicationConstants.ASC, 
					chart.getLocale());
		
		chart.getChartEntries().addAll(entriesForMembersWithQuestion);
		chart.getChartEntries().addAll(entriesForMembersWithoutQuestion);
		
		return (Chart) chart.persist();
	}
	
	private static List<ChartEntry> chartEntriesForMembersWithQuestionUH(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Date startTime,
			final Date endTime,
			final Status[] internalStatuses,
			final String sortOrder,
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		Date currentDate = QuestionChart.getCurrentDate();
		List<Member> activeMembersWithQuestions = 
			Question.findActiveMembersWithQuestions(session, currentDate, deviceType, group, 
					internalStatuses, answeringDate, startTime, endTime, sortOrder, 
					locale);
		Boolean isLastAnsweringDate = null;
		if(!deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			isLastAnsweringDate = QuestionChart.isLastAnsweringDate(group, answeringDate);
		}
		Boolean isProcessAllRemainingQuestionsForLastDate = 
			QuestionChart.processAllRemainingQnsForLastDateUH().equalsIgnoreCase("TRUE");
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			
			Integer maxQuestionsOnChart = QuestionChart.maxQuestionsOnChartUH(deviceType);
			for(Member m : activeMembersWithQuestions) {
				ChartEntry chartEntry = QuestionChart.newChartEntryUH(session, m, 
						deviceType, group, answeringDate, startTime, endTime, 
						maxQuestionsOnChart, internalStatuses, locale);
				chartEntries.add(chartEntry);
			}
			
		}else{
			if(isLastAnsweringDate && isProcessAllRemainingQuestionsForLastDate) {
		
				for(Member m : activeMembersWithQuestions) {
					ChartEntry chartEntry = QuestionChart.newChartEntryUH(session, m, 
							deviceType, group, answeringDate, startTime, endTime, 
							internalStatuses, locale);
					chartEntries.add(chartEntry);
				}
			}else{
				Integer maxQuestionsOnChart = QuestionChart.maxQuestionsOnChartUH(deviceType);
				for(Member m : activeMembersWithQuestions) {
					ChartEntry chartEntry = QuestionChart.newChartEntryUH(session, m, 
							deviceType, group, answeringDate, startTime, endTime, 
							maxQuestionsOnChart, internalStatuses, locale);
					chartEntries.add(chartEntry);
				}
			}
		}
		
		return chartEntries;
	}

	private static List<ChartEntry> chartEntriesForMembersWithoutQuestionUH(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Date startTime,
			final Date endTime,
			final Status[] internalStatuses,
			final String sortOrder,
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		Date currentDate = QuestionChart.getCurrentDate();
		List<Member> activeMembersWithoutQuestions = 
			Question.findActiveMembersWithoutQuestions(session, currentDate, deviceType, group,
					internalStatuses, answeringDate, startTime, endTime, sortOrder, 
					locale);
		for(Member m : activeMembersWithoutQuestions) {
			ChartEntry chartEntry = QuestionChart.newEmptyChartEntry(m, locale);
			chartEntries.add(chartEntry);
		}
		
		return chartEntries;
	}
	
	private static String processAllRemainingQnsForLastDateUH() {
		CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, 
				"COUNCIL_PROCESS_ALL_REMAINING_QNS_FOR_LASTDATE", "");
		return parameter.getValue();
	}
	
	private static Integer maxQuestionsOnChartUH(final DeviceType deviceType) {
		CustomParameter noOfQuestionsParameter = null;
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			noOfQuestionsParameter = CustomParameter.findByFieldName(CustomParameter.class, "name", "NO_OF_HALFHOURDISCUSSIONSTANDALONE_ON_CHART_COUNT_UH", "");
		}else{
			noOfQuestionsParameter = CustomParameter.findByFieldName(CustomParameter.class, "name", "NO_OF_QUESTIONS_ON_MEMBER_CHART_UH", "");
		}
		return Integer.valueOf(noOfQuestionsParameter.getValue());
	}
	
	/**
	 * Use this Charting algorithm when creating a ChartEntry (for the first
	 * time).
	 * 
	 * Search for at most @param maxNoOfQuestions according to the following
	 * algorithm. Search only for those Questions which are submitted between
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
	 * 
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param answeringDate the answering date
	 * @param startTime and @param endTime (both time inclusive) and are verified
	 * by the assistant ("ASSISTANT_PROCESSED")
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
	 * 
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param answeringDate the answering date
	 * @param startTime and @param endTime (both time inclusive) and are verified
	 * by the assistant ("ASSISTANT_PROCESSED")
	 * @param endTime the end time
	 * @param maxQuestionsOnChart the max questions on chart
	 * @param locale the locale
	 * @return the chart entry
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
		if(!deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			if(maxQ > 0) {
				List<Question> questions = Question.findDatedQuestions(session, member, deviceType, 
						group, answeringDate, startTime, endTime, internalStatuses, maxQ, 
						locale);
				
				candidateQList.addAll(questions);
				maxQ = maxQ - questions.size();
			}
		}
		
		// List of Questions without any answering date
		if(maxQ > 0) {
			List<Question> questions = Question.findNonAnsweringDate(session, member, deviceType, 
					group, startTime, endTime, internalStatuses, maxQ, ApplicationConstants.ASC, 
					locale);
			
			candidateQList.addAll(questions);
		}
		try{
			for(Device d:candidateQList){
				Question q=(Question) d;
				if(q.getFile()==null){
					Reference reference=Question.findCurrentFile(q);
					q.setFile(Integer.parseInt(reference.getId()));
					q.setFileIndex(Integer.parseInt(reference.getName()));
					q.setFileSent(false);
				}
			}
		}catch (ELSException e) {
			e.printStackTrace();
		}
		ChartEntry chartEntry = new ChartEntry(member, candidateQList, locale);
		return chartEntry;
	}
	
	/**
	 * Checks if @param question is a first batch question.
	 */
	private static Boolean isFirstBatchQuestionUH(final Question question) {
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		
		Session session = question.getSession();
		Date startTime = FormaterUtil.formatStringToDate(session.getParameter(
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME_UH), 
				datePattern.getValue(), question.getLocale());
		Date endTime = FormaterUtil.formatStringToDate(session.getParameter(
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME_UH), 
				datePattern.getValue(), question.getLocale());
		
		Date submissionTime = question.getSubmissionDate();
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
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		
		Session session = question.getSession();
		Date startTime = FormaterUtil.formatStringToDate(session.getParameter(
				ApplicationConstants.QUESTION_STARRED_SECONDBATCH_SUBMISSION_STARTTIME_UH), 
				datePattern.getValue(), question.getLocale());
		Date endTime = FormaterUtil.formatStringToDate(session.getParameter(
				ApplicationConstants.QUESTION_STARRED_SECONDBATCH_SUBMISSION_ENDTIME_UH), 
				datePattern.getValue(), question.getLocale());
		
		Date submissionTime = question.getSubmissionDate();
		if((submissionTime.compareTo(startTime) >= 0) &&
				(submissionTime.compareTo(endTime) <= 0)) {
			return true;
		}
		return false;
	}
	
	private static Boolean addToChartFirstBatchUH(final Question question) throws ELSException {
		Chart chart = QuestionChart.findLatestChart(question.getSession(), question.getGroup(), 
				question.getType(), question.getLocale());
		if(chart != null) {
			if(question.getType() != null){
				
				if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
					Integer maxNoOfQuestions = QuestionChart.maxQuestionsOnChartUH(question.getType());
					return QuestionChart.addToChartIfApplicable(chart, question, maxNoOfQuestions);
				}else{
					Date answeringDate = chart.getAnsweringDate();
					
					Boolean isLastAnsweringDate = 
						QuestionChart.isLastAnsweringDate(question.getGroup(), answeringDate); 
					if(isLastAnsweringDate) {
						// The Question taken on the Chart should have status "TO_BE_PUT_UP"
						Status TO_BE_PUT_UP = Status.findByType(
								ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, question.getLocale());
						QuestionDates chartAnsweringDate = chart.getGroup().
							findQuestionDatesByGroupAndAnsweringDate(chart.getAnsweringDate());
						question.setInternalStatus(TO_BE_PUT_UP);
						question.setRecommendationStatus(TO_BE_PUT_UP);
						question.setChartAnsweringDate(chartAnsweringDate);
						if(question.getFile()==null){
							/**** Add Question to file ****/
							Reference reference=Question.findCurrentFile(question);
							question.setFile(Integer.parseInt(reference.getId()));
							question.setFileIndex(Integer.parseInt(reference.getName()));
							question.setFileSent(false);
						}
						question.simpleMerge();
		
						// Add question to the existing list of charted questions
						List<Question> onChartQuestions = 
							QuestionChart.findQuestions(question.getPrimaryMember(), question.getSession(), 
								question.getGroup(), answeringDate, question.getType(), 
								question.getLocale());
						onChartQuestions.add(question);
						onChartQuestions = QuestionChart.reorderQuestions(onChartQuestions, question.getType(), answeringDate);
		
						// Update the Chart Entry for this Member
						List<Device> devices = new ArrayList<Device>();
						devices.addAll(onChartQuestions);
						ChartEntry ce = QuestionChart.find(chart.getChartEntries(), 
								question.getPrimaryMember());
						ce.setDevices(devices);
						ce.merge();
		
						return true;
					}
					else {
						Boolean isEligibleForChart = isEligibleForChartUH(chart, question);
						if(isEligibleForChart) {
							Integer maxNoOfQuestions = QuestionChart.maxQuestionsOnChartUH(question.getType());
							return QuestionChart.addToChartIfApplicable(chart, question, maxNoOfQuestions);
						}
					}
				}
			}
			
		}
		
		return false;
	}
	
	private static Boolean addToChartSecondBatchUH(Question question) throws ELSException {
		Boolean isAddedToChart = false;
		
		List<Date> answeringDates = new ArrayList<Date>();
		if(question.getAnsweringDate() == null) {
			answeringDates = question.getGroup().getAnsweringDates(ApplicationConstants.ASC);
		}
		else {
			answeringDates = QuestionChart.getAnsweringDatesGTEQ(question.getGroup(), 
					question.getAnsweringDate().getAnsweringDate());
		}
		
		Integer maxNoOfQuestions = QuestionChart.maxQuestionsOnChartUH(question.getType());
		for(Date d : answeringDates) {
			Chart chart = QuestionChart.find(question.getSession(), question.getGroup(), d, 
					question.getType(), question.getLocale());
			if(chart != null) {
				if(QuestionChart.addToChartIfApplicable(chart, question, maxNoOfQuestions)) {
					isAddedToChart = true;
					break;
				}
			}
		}
		
		return isAddedToChart;
	}
	
	/**
	 * 1. Check if Question.submissionDate <= chart.finalSubmissionDate?
	 * 2. Check if Question.answeringDate == null OR
	 * Question.answeringDate == LatestChart.answeringDate OR
	 * Question.answeringDate < LatestChart.answeringDate? (Case of Group Change)
	 * 3. If the answer to 1 and 2 is YES then return true
	 */
	private static Boolean isEligibleForChartUH(final Chart chart, final Question q) {
		if(chart.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
			return true;
		} 
		else {
			Date chartAnsweringDate = chart.getAnsweringDate(); 
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

	//=============== ASSEMBLY METHODS ================
	/**
	 * Creates a new Chart. If a chart already exists then returns the
	 * existing Chart. If a previous dated Chart exists & is unprocessed
	 * then don't create a new Chart and return null.
	 * @throws ELSException 
	 */
	private static Chart createLH(final Chart chart) throws ELSException {
		if(chart.getDeviceType() != null){
			if(chart.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				
				Chart newChart = Chart.find(chart);
				if(newChart == null) {
					newChart = QuestionChart.persistChartLH(chart);
					QuestionChart.updateChart(newChart);
				}
				return newChart;
			}else{
				Boolean isFirstAnsweringDate = QuestionChart.isFirstAnsweringDate(chart);
				Boolean isPreviousChartExists = QuestionChart.isPreviousChartExists(chart);
				Boolean isPreviousChartProcessed = QuestionChart.isPreviousChartProcessed(chart);
				
				if(isFirstAnsweringDate == true ||
						(isPreviousChartExists == true && isPreviousChartProcessed == true)) {
					Chart newChart = Chart.find(chart);
					if(newChart == null) {
						newChart = QuestionChart.persistChartLH(chart);
						QuestionChart.updateChart(newChart);
					}
					return newChart;
				}
			}
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
	 * in addToChartIfApplicable/3
	 * @throws ELSException 
	 */
	private static Boolean addToChartLH(final Question question) throws ELSException {
		if(QuestionChart.isAssistantProcessed(question)) {
			Session session = question.getSession();
			Group group = question.getGroup();
			DeviceType deviceType = question.getType();
			String locale = question.getLocale();
			
			Chart chart = null;
			if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				chart = QuestionChart.find(session, deviceType, locale);
			}else{
				chart = QuestionChart.findLatestChart(session, group, deviceType, locale);
			}
			if(chart != null) {
				if(isEligibleForChartLH(chart, question)) {
					Integer maxNoOfQuestions = QuestionChart.maxQuestionsOnChartLH(question.getType());
					return QuestionChart.addToChartIfApplicable(chart, question, maxNoOfQuestions);
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
	 * and recommendationStatus to "TO_BE_PUT_UP"
	 *
	 * @param question the question
	 * @param affectedGroup the group from which this question was removed
	 * @throws ELSException
	 * 
	 *  TODO: What if the @param question which should leave the Chart
	 * is a clubbed question or has any clubbings? 
	 */
	private static void groupChangeLH(final Question question, 
			final Group affectedGroup) throws ELSException {
		Session session = question.getSession();
		Member member = question.getPrimaryMember();
		String locale = question.getLocale();
		
		Chart chart = Chart.find(question);
		if(chart != null) {
			ChartEntry ce = QuestionChart.find(chart.getChartEntries(), member);
			List<Device> devices = ce.getDevices();
			int index = -1;
			for(Device d : devices) {
				++index;
				if(d.getId().equals(question.getId())) {
					break;
				}
			}
			devices.remove(index);
			
			// Since 1 question has left the group so add 1 question to the chart. Hence
			// maxNoOfQuestions = 1
			Question q = QuestionChart.onGroupChangeAddQuestion(session, member, affectedGroup,
					chart.getAnsweringDate(),
					devices.toArray(new Question[0]), locale);
			if(q != null) {
				// The Questions taken on the Chart should have status "TO_BE_PUT_UP"
				Status TO_BE_PUT_UP = 
					Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale);
				QuestionDates chartAnsweringDate = chart.getGroup().
					findQuestionDatesByGroupAndAnsweringDate(chart.getAnsweringDate());
				q.setInternalStatus(TO_BE_PUT_UP);
				q.setRecommendationStatus(TO_BE_PUT_UP);
				q.setChartAnsweringDate(chartAnsweringDate);
				if(q.getFile()==null){
					/**** Add Question to file ****/
					Reference reference=Question.findCurrentFile(q);
					q.setFile(Integer.parseInt(reference.getId()));
					q.setFileIndex(Integer.parseInt(reference.getName()));
					q.setFileSent(false);
				}
				q.simpleMerge();
				
				devices.add(q);
			}
			
			ce.setDevices(devices);
			ce.merge();
		}
	}
	
	private static Chart persistChartLH(final Chart chart) {
		Integer maxQuestionsOnChart = QuestionChart.maxQuestionsOnChartLH(chart.getDeviceType());
		
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
		Date startTime = FormaterUtil.formatStringToDate(chart.getSession().getParameter(
				chart.getDeviceType().getType() + "_submissionStartDate"), 
				datePattern.getValue(), chart.getLocale());
		Date finalSubmissionTime = null;
		if(chart.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			finalSubmissionTime = FormaterUtil.formatStringToDate(chart.getSession().getParameter(
					chart.getDeviceType().getType() + "_submissionEndDate"), 
					datePattern.getValue(), chart.getLocale());
		}else{
			finalSubmissionTime = chart.getGroup().getFinalSubmissionDate(chart.getAnsweringDate());
		}

		Status ASSISTANT_PROCESSED = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, chart.getLocale());
		Status[] internalStatuses = new Status[] { ASSISTANT_PROCESSED };
		
		List<ChartEntry> entriesForMembersWithQuestion =
			QuestionChart.chartEntriesForMembersWithQuestionLH(chart.getSession(), 
					chart.getDeviceType(), chart.getGroup(), chart.getAnsweringDate(), 
					startTime, finalSubmissionTime, maxQuestionsOnChart, internalStatuses, 
					ApplicationConstants.ASC, chart.getLocale());
		
		List<ChartEntry> entriesForMembersWithoutQuestion =
			QuestionChart.chartEntriesForMembersWithoutQuestionLH(chart.getSession(), 
					chart.getDeviceType(), chart.getGroup(), chart.getAnsweringDate(), 
					startTime, finalSubmissionTime, internalStatuses, ApplicationConstants.ASC, 
					chart.getLocale());
		
		chart.getChartEntries().addAll(entriesForMembersWithQuestion);
		chart.getChartEntries().addAll(entriesForMembersWithoutQuestion);
		
		return (Chart) chart.persist();
	}
	
	private static Integer maxQuestionsOnChartLH(final DeviceType deviceType) {
		CustomParameter noOfQuestionsParameter = null;
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			noOfQuestionsParameter = CustomParameter.findByFieldName(CustomParameter.class, "name", "NO_OF_HALFHOURDISCUSSIONSTANDALONE_ON_CHART_COUNT_LH", "");
		}else{
			noOfQuestionsParameter = CustomParameter.findByFieldName(CustomParameter.class, "name", "NO_OF_QUESTIONS_ON_MEMBER_CHART_LH", "");
		}
		return Integer.valueOf(noOfQuestionsParameter.getValue());
	}
	
	private static List<ChartEntry> chartEntriesForMembersWithQuestionLH(final Session session, 
			final DeviceType deviceType, 
			final Group group,
			final Date answeringDate, 
			final Date startTime, 
			final Date finalSubmissionTime,
			final Integer maxQuestionsOnChart,
			final Status[] internalStatuses, 
			final String sortOrder, 
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		Date currentDate = QuestionChart.getCurrentDate();
		List<Member> activeMembersWithQuestions = 
			Question.findActiveMembersWithQuestions(session, currentDate, deviceType, 
					group, internalStatuses, answeringDate,	startTime, finalSubmissionTime, 
					sortOrder, locale);
		for(Member m : activeMembersWithQuestions) {
			ChartEntry chartEntry = QuestionChart.newChartEntryLH(session, m, deviceType, 
					group, answeringDate, finalSubmissionTime, maxQuestionsOnChart, 
					internalStatuses, locale);
			chartEntries.add(chartEntry);
		}
		
		return chartEntries;
	}
	
	private static List<ChartEntry> chartEntriesForMembersWithoutQuestionLH(final Session session, 
			final DeviceType deviceType, 
			final Group group,
			final Date answeringDate, 
			final Date startTime, 
			final Date finalSubmissionTime,
			final Status[] internalStatuses, 
			final String sortOrder, 
			final String locale) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		Date currentDate = QuestionChart.getCurrentDate();
		List<Member> activeMembersWithoutQuestions = 
			Question.findActiveMembersWithoutQuestions(session, currentDate, deviceType, 
					group, internalStatuses, answeringDate, startTime, finalSubmissionTime, 
					sortOrder, locale);
		for(Member m : activeMembersWithoutQuestions) {
			ChartEntry chartEntry = QuestionChart.newEmptyChartEntry(m, locale);
			chartEntries.add(chartEntry);
		}
		
		return chartEntries;
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
			final Status[] internalStatuses,
			final String locale) {
		List<Device> candidateQList = new ArrayList<Device>();
		
		int maxQ = maxQuestionsOnChart;
		
		// List of Questions having an explicit answeringDate attribute set
		// to current date or previous answering date(s)
		
		if(!deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			if(maxQ > 0) {
				List<Question> questions = Question.findDatedQuestions(session, member, deviceType, 
						group, answeringDate, finalSubmissionDate, internalStatuses, maxQ, 
						locale);
				
				candidateQList.addAll(questions);
				maxQ = maxQ - questions.size();
			}
		}
		// List of Questions without any answering date
		if(maxQ > 0) {
			List<Question> questions = Question.findNonAnsweringDate(session, member, deviceType, 
					group, finalSubmissionDate, internalStatuses, maxQ, ApplicationConstants.ASC, 
					locale);
			
			candidateQList.addAll(questions);
		}
		try{
			for(Device d1:candidateQList){
				Question q=(Question) d1;
				if(q.getFile()==null){
					/**** Add Question to file ****/
					Reference reference=Question.findCurrentFile(q);
					q.setFile(Integer.parseInt(reference.getId()));
					q.setFileIndex(Integer.parseInt(reference.getName()));
					q.setFileSent(false);
				}
			}
		}catch (ELSException e) {
			e.printStackTrace();
		}
		ChartEntry chartEntry = new ChartEntry(member, candidateQList, locale);
		return chartEntry;
	}
	
	/**
	 * 1. Check if Question.submissionDate <= chart.finalSubmissionDate?
	 * 2. Check if Question.answeringDate == null OR
	 * Question.answeringDate == LatestChart.answeringDate OR
	 * Question.answeringDate < LatestChart.answeringDate? (Case of Group Change)
	 * 3. If the answer to 1 and 2 is YES then return true
	 */
	private static Boolean isEligibleForChartLH(final Chart chart, final Question q) {
		if(chart.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			return true;
		}else{
			Date chartAnsweringDate = chart.getAnsweringDate();
			Date questionSubmissionDate = q.getSubmissionDate();
			Date finalSubmissionDate = q.getGroup().getFinalSubmissionDate(chartAnsweringDate);
			int questionSubmittedBeforeFinal = questionSubmissionDate.compareTo(finalSubmissionDate);
			CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
			Date initialSubmissionDate = FormaterUtil.formatStringToDate(chart.getSession().getParameter(
					chart.getDeviceType().getType() + "_submissionStartDate"), 
					datePattern.getValue(), chart.getLocale());
			int questionSubmittedAfterInitial=questionSubmissionDate.compareTo(initialSubmissionDate);
			if(questionSubmittedBeforeFinal <= 0 && questionSubmittedAfterInitial >= 0) { 
				QuestionDates questionAnsweringDate = q.getAnsweringDate();
				if(questionAnsweringDate == null) {
					return true;
				}
				else if(questionAnsweringDate.getAnsweringDate().compareTo(chartAnsweringDate) <= 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	//=============== COMMON INTERNAL METHODS =========
	/**
	 * Checks if the previous chart is processed.
	 * @throws ELSException 
	 */
	private static Boolean isPreviousChartProcessed(final Chart chart) throws ELSException {
		
		if(!chart.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			Date previousAnsweringDate = QuestionChart.getPreviousAnsweringDate(chart.getGroup(), chart.getAnsweringDate());
			
			if(previousAnsweringDate != null) {
				Chart previousChart = 
						new Chart(chart.getSession(), chart.getGroup(), previousAnsweringDate, chart.getDeviceType(), chart.getLocale());
				return QuestionChart.isProcessed(previousChart);
			}
		}
		
		return true;
	}
	
	private static void updateChart(final Chart chart) throws ELSException {
		Status TO_BE_PUT_UP = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, 
				chart.getLocale());
		Group group = chart.getGroup();
		QuestionDates chartAnsweringDate = null;
		if(chart.getDeviceType() != null){
			if(!chart.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				chartAnsweringDate = group.findQuestionDatesByGroupAndAnsweringDate(chart.getAnsweringDate());
			}
			
			Chart.getChartRepository().updateChartQuestions(chart.getSession(), group, chart.getDeviceType(), 
					chart.getAnsweringDate(), chartAnsweringDate, TO_BE_PUT_UP, TO_BE_PUT_UP, 
					chart.getLocale());
		}
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
	
	/**
	 * If the @param answeringDate is the last answering date for the.
	 *
	 * @param group then return true, else return false.
	 * @param answeringDate the answering date
	 * @return true, if is last answering date
	 */
	private static Boolean isLastAnsweringDate(final Group group, 
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
	
	private static Boolean isAssistantProcessed(final Question question) {
		String ASSISTANT_PROCESSED = ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED;
		Status internalStatus = question.getInternalStatus();
		if(internalStatus.getType().equals(ASSISTANT_PROCESSED)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns a list of @param group answeringDates greater than or equal to.
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
	 * Algorithm: "ADD TO CHART IF APPLICABLE"
	 * 1. If a member has less than 5 Questions on Chart simply add the Question
	 * to the chart, & update the chart. Set the status of Question to
	 * "TO_BE_PUT_UP"
	 * 
	 * 2. If a member has exactly 5 questions then,
	 * a> The Questions which are in the Workflow (internalStatus != "TO_BE_PUT_UP")
	 * wont get affected.
	 * 
	 * b> The questions which are not in Workflow (internalStatus = "TO_BE_PUT_UP"),
	 * will compete with @param q for a slot in Chart. At the end of this step all
	 * the Questions on the Chart which are not in the Workflow will have
	 * internalStatus = "TO_BE_PUT_UP". The Question which leaves the Chart will
	 * have internalStatus = "ASSISTANT_PROCESSED".
	 * 
	 * Constraints:
	 * 1> If this question is added to the chart, it's internalStatus and 
	 * recommendationStatus should change to "TO_BE_PUT_UP".
	 * 
	 * 2> In lieu of this question entering the Chart, if some Question leaves
	 * the Chart then the internalStatus & recommendationStatus of that Question 
	 * should be set to "ASSISTANT_PROCESSED".
	 * 
	 * 3> The internalStatuses of the rest of the Questions on the Chart should
	 * remain unaffected.
	 * 
	 * Returns true if the @param q is added to the @param chart, else returns
	 * false
	 *
	 * @param chart the chart
	 * @param q the q
	 * @param maxNoOfQuestions the max no of questions
	 * @return true, if successful
	 * @throws ELSException 
	 */
	private static Boolean addToChartIfApplicable(final Chart chart,
			final Question question, 
			final Integer maxNoOfQuestions) throws ELSException {
		Boolean isAddedToChart = false;
		
		if(chart.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			
			Member member = question.getPrimaryMember();
			
			Integer memberQuestions = Chart.getChartRepository().findQuestionsCount(member, 
					chart.getSession(), chart.getDeviceType(), new Status[]{}, chart.getLocale());
			
			Status REJECTED = Status.findByType(
					ApplicationConstants.QUESTION_FINAL_REJECTION, chart.getLocale());
			Integer rejected = Chart.getChartRepository().findQuestionsCount(member, 
					chart.getSession(), chart.getDeviceType(), new Status[]{ REJECTED }, 
					chart.getLocale());
			
			if(memberQuestions - rejected <= maxNoOfQuestions) {
				// The HDS taken on the Chart should have status "TO_BE_PUT_UP"
				Status TO_BE_PUT_UP = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, chart.getLocale());
				
				question.setInternalStatus(TO_BE_PUT_UP);
				question.setRecommendationStatus(TO_BE_PUT_UP);	
				if(question.getFile()==null){
					/**** Add Question to file ****/
					Reference reference=Question.findCurrentFile(question);
					question.setFile(Integer.parseInt(reference.getId()));
					question.setFileIndex(Integer.parseInt(reference.getName()));
					question.setFileSent(false);
				}
				question.simpleMerge();
				
				// Add the HDS to the Chart.
				ChartEntry chartEntry = QuestionChart.find(chart.getChartEntries(), member);
				List<Device> devices = chartEntry.getDevices();
				devices.add(question);
				devices = QuestionChart.reorderQuestions(devices);
				chartEntry.setDevices(devices);
				chartEntry.merge();
				
				isAddedToChart = true;
			}
		}else{
			Member member = question.getPrimaryMember();
			Session session = chart.getSession();
			Group group = chart.getGroup();
			String locale = chart.getLocale();
			
			Status TO_BE_PUT_UP = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale);
			Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, locale);
			QuestionDates chartAnsweringDate = group.findQuestionDatesByGroupAndAnsweringDate(chart.getAnsweringDate());
			
			List<Question> onChartQuestions = QuestionChart.findQuestions(member, session, group,chart.getAnsweringDate(), question.getType(), locale);
			
			if(onChartQuestions.size() == maxNoOfQuestions) {
				List<Question> updatedChartQuestions = new ArrayList<Question>();
				
				// Questions in Workflow will stay unharmed
				List<Question> questionsInWorkflow = 
					QuestionChart.questionsInWorkflow(onChartQuestions, locale);
				updatedChartQuestions.addAll(questionsInWorkflow);
				
				// The Questions not in the Workflow will compete for a place in the Chart
				int requiredQuestions = maxNoOfQuestions - updatedChartQuestions.size();
				List<Question> questionsNotInWorkflow = 
					QuestionChart.questionsNotInWorkflow(onChartQuestions, locale);
				questionsNotInWorkflow.add(question);
				
				// The size of candidateQuestions will always be size of questionsNotInWorkflow
				// + 1 (Question question as provided in the parameter)
				List<Question> candidateQuestions = 
					QuestionChart.reorderQuestions(questionsNotInWorkflow, question.getType(), chart.getAnsweringDate());
				
				// The Questions taken on the Chart should have status "TO_BE_PUT_UP". The nature
				// of candidateQuestions is such that the last question in that list will 
				// always leave the Chart.
				int size = candidateQuestions.size();
				for(int i = 0; i < size - 1; i++) {
					Question qn = (Question) candidateQuestions.get(i);
					if(qn.getInternalStatus().getType().equals(ASSISTANT_PROCESSED.getType())) {
						qn.setInternalStatus(TO_BE_PUT_UP);
						qn.setRecommendationStatus(TO_BE_PUT_UP);
						qn.setChartAnsweringDate(chartAnsweringDate);
						
						if(qn.getFile()==null){
							/**** Add Question to file ****/
							Reference reference=Question.findCurrentFile(qn);
							qn.setFile(Integer.parseInt(reference.getId()));
							qn.setFileIndex(Integer.parseInt(reference.getName()));
							qn.setFileSent(false);
						}
						qn.simpleMerge();
						
						if(qn.getId().equals(question.getId())) {
							isAddedToChart = true;
						}
					}	
				}
				
				// Question qn is leaving the Chart. Update it's status and clubbing information
				Question qn = (Question) candidateQuestions.get(size - 1);
				qn.setInternalStatus(ASSISTANT_PROCESSED);
				qn.setRecommendationStatus(ASSISTANT_PROCESSED);
				qn.setChartAnsweringDate(null);
				if(qn.getFile()==null){
					qn.setFile(null);
					qn.setFileIndex(null);
					qn.setFileSent(false);
				}
				
				qn = qn.simpleMerge();
				if(qn.getParent() == null) {
					Question newParent = ClubbedEntity.removeParent(qn);
					if(newParent!=null){
						newParent.setInternalStatus(TO_BE_PUT_UP);
						newParent.setRecommendationStatus(TO_BE_PUT_UP);
						if(newParent.getFile()==null){
							/**** Add Question to file ****/
							Reference reference=Question.findCurrentFile(newParent);
							newParent.setFile(Integer.parseInt(reference.getId()));
							newParent.setFileIndex(Integer.parseInt(reference.getName()));
							newParent.setFileSent(false);
						}
						newParent.simpleMerge();
					}
				}
				else {
					ClubbedEntity.unclub(qn.getParent().getId(), qn.getId(), locale);
				}
				
				// Update the Chart Entry.
				if(candidateQuestions.size() >= requiredQuestions) {
					updatedChartQuestions.addAll(candidateQuestions.subList(0, requiredQuestions));
				}
				List<Device> devices = new ArrayList<Device>();
				devices.addAll(updatedChartQuestions);
				ChartEntry ce = QuestionChart.find(chart.getChartEntries(), member);
				ce.setDevices(devices);
				ce.merge();
			}
			else if(onChartQuestions.size() < maxNoOfQuestions) {
				List<Question> updatedChartQuestions = new ArrayList<Question>();
				updatedChartQuestions.addAll(onChartQuestions);
				
				 // The Question taken on the Chart should have status "TO_BE_PUT_UP"
				question.setInternalStatus(TO_BE_PUT_UP);
				question.setRecommendationStatus(TO_BE_PUT_UP);
				question.setChartAnsweringDate(chartAnsweringDate);
				if(question.getFile()==null){
					/**** Add Question to file ****/
					Reference reference=Question.findCurrentFile(question);
					question.setFile(Integer.parseInt(reference.getId()));
					question.setFileIndex(Integer.parseInt(reference.getName()));
					question.setFileSent(false);
				}
				question.simpleMerge();
				
				// Add the Question to the Chart.
				updatedChartQuestions.add(question);
				updatedChartQuestions = QuestionChart.reorderQuestions(updatedChartQuestions, question.getType(), 
						chart.getAnsweringDate());
				
				// Update the Chart Entry.
				List<Device> devices = new ArrayList<Device>();
				devices.addAll(updatedChartQuestions);
				ChartEntry ce = QuestionChart.find(chart.getChartEntries(), member);
				ce.setDevices(devices);
				ce.merge();
				
				isAddedToChart = true;
			}
		}
		return isAddedToChart;
	}
	
	/**
	 * Orders the @param questions in order defined as follows:
	 * 1. Dated Questions sorted by number in increasing order
	 * 2. Previously dated Questions sorted by answeringDate in increasing order.
	 * Break the tie using Question number
	 * 3. Non dated Questions sorted by number in increasing order
	 */
	private static List<Question> reorderQuestions(final List<Question> onChartQuestions,
			final DeviceType deviceType, final Date answeringDate) {
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			
			List<Question> qList = Question.sortByNumber(onChartQuestions, ApplicationConstants.ASC);
			
			List<Question> candidateQList = new ArrayList<Question>();
			candidateQList.addAll(qList);
			
			return candidateQList;
		}else{
			List<Question> datedQList = new ArrayList<Question>();
			List<Question> beforeDatedQList = new ArrayList<Question>();
			List<Question> afterDatedQList = new ArrayList<Question>();
			List<Question> nonDatedQList = new ArrayList<Question>();
			
			for(Question q : onChartQuestions) {
				if(q.getAnsweringDate() == null) {
					nonDatedQList.add(q);
				}
				else {
					if(q.getAnsweringDate().getAnsweringDate().compareTo(answeringDate) < 0) {
						beforeDatedQList.add(q);
					}
					else if(q.getAnsweringDate().getAnsweringDate().compareTo(answeringDate) > 0) {
						afterDatedQList.add(q);
					}
					else {// q.getAnsweringDate().getAnsweringDate().compareTo(answeringDate) == 0
						datedQList.add(q);
					}
					
				}
			}
			
			datedQList = Question.sortByNumber(datedQList, ApplicationConstants.ASC);
			beforeDatedQList = Question.sortByAnsweringDate(beforeDatedQList, ApplicationConstants.ASC);
			afterDatedQList = Question.sortByAnsweringDate(afterDatedQList, ApplicationConstants.ASC);
			nonDatedQList = Question.sortByNumber(nonDatedQList, ApplicationConstants.ASC);
			
			List<Question> candidateQList = new ArrayList<Question>();
			candidateQList.addAll(datedQList);
			candidateQList.addAll(beforeDatedQList);
			candidateQList.addAll(afterDatedQList);
			candidateQList.addAll(nonDatedQList);
		
			// ASSERT: The size of onChartQuestions should be equal to the size
			// 		   of candidateQList
			return candidateQList;
		}
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
	
	/**
	 * All the Questions with status not equal to "SUBMIT" AND "COMPLETE" 
	 * AND "INCOMPLETE" AND not starting with "question_putup" AND
	 * not starting with "question_system" are not in the workflow.
	 */
	private static List<Question> questionsInWorkflow(final List<Question> onChartQuestions,
			final String locale) {
		List<Question> qList = new ArrayList<Question>();
		
		String INCOMPLETE = ApplicationConstants.QUESTION_INCOMPLETE;
		String COMPLETE = ApplicationConstants.QUESTION_COMPLETE;
		String SUBMITTED = ApplicationConstants.QUESTION_SUBMIT;
		
		for(Question q : onChartQuestions) {
			String type = q.getInternalStatus().getType();
			if(! (type.equals(INCOMPLETE) || type.equals(COMPLETE) || 
					type.equals(SUBMITTED) || type.startsWith("question_putup") ||
					type.startsWith("question_system"))) {
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
	private static List<Question> questionsNotInWorkflow(final List<Question> onChartQuestions,
			final String locale) {
		List<Question> qList = new ArrayList<Question>();
		
		String INCOMPLETE = ApplicationConstants.QUESTION_INCOMPLETE;
		String COMPLETE = ApplicationConstants.QUESTION_COMPLETE;
		String SUBMITTED = ApplicationConstants.QUESTION_SUBMIT;
		
		for(Question q : onChartQuestions) {
			String type = q.getInternalStatus().getType();
			if(type.equals(INCOMPLETE) || type.equals(COMPLETE) || 
					type.equals(SUBMITTED) || type.startsWith("question_putup") ||
					type.startsWith("question_system")) {
				qList.add(q);
			}
		}
		
		return qList;
	}
	
	private static Question onGroupChangeAddQuestion(final Session session,
			final Member member, 
			final Group group, 
			final Date answeringDate,
			final Question[] excludeQuestions, 
			final String locale) {
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
		Date finalSubmissionDate = group.getFinalSubmissionDate(answeringDate);
		
		Status ASSISTANT_PROCESSED = 
			Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, locale);
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
	
	private static List<Device> reorderQuestions(final List<Device> devices) {
		List<Question> qList = new ArrayList<Question>();
		for(Device d : devices){
			Question q = ((Question)d);
			qList.add(q);
		}
		List<Question> newQList = Question.sortByNumber(qList, ApplicationConstants.ASC);
		
		List<Device> retDevices = new ArrayList<Device>();
		for(Question tq : newQList){
			retDevices.add(tq);
		}
		return retDevices;
	}
	
	private static Boolean isFirstAnsweringDate(final Chart chart) {
		Date previousAnsweringDate = QuestionChart.getPreviousAnsweringDate(chart.getGroup(), chart.getAnsweringDate());
		if(previousAnsweringDate == null) {
			return true;
		}
		return false;
	}

	private static Boolean isPreviousChartExists(final Chart chart) throws ELSException {
		if(!chart.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			Date previousAnsweringDate = QuestionChart.getPreviousAnsweringDate(chart.getGroup(), chart.getAnsweringDate());
			
			if(previousAnsweringDate != null) {
				Chart previousChart = QuestionChart.find(chart.getSession(), chart.getGroup(), 
						previousAnsweringDate, chart.getDeviceType(), chart.getLocale());
				if(previousChart != null) {
					return true;
				}
			}
		}
		
		return false;
	}
}


class ResolutionChart {

	//=============== VIEW METHODS ==================
	public static List<ChartVO> getChartVOs(final Session session, 
			final DeviceType deviceType,
			final String locale) throws ELSException {
		List<ChartVO> chartVOs = new ArrayList<ChartVO>();
		
		Chart chart = ResolutionChart.find(session, deviceType, locale);
		if(chart != null) {
			List<ChartVO> chartVOsWithDevices = new ArrayList<ChartVO>();
			List<ChartVO> chartVOsWithoutdevices = new ArrayList<ChartVO>();
			
			List<ChartEntry> chartEntries = chart.getChartEntries();
			for(ChartEntry ce : chartEntries) {
				int extraCount=0;
				Long memberId = ce.getMember().getId();
				String memberName = ce.getMember().getFullnameLastNameFirst();
				Member member=Member.findById(Member.class, memberId);
				List<DeviceVO> deviceVOs = ResolutionChart.getDeviceVOs(ce.getDevices());
				extraCount=Resolution.getResolutionWithoutNumber(member,deviceType,session,locale);
				List<Resolution> rejectedResolution=Resolution.getRejectedResolution(member,deviceType,session,locale);
				
				if(deviceVOs.isEmpty()) {
					ChartVO chartVO = new ChartVO(memberId, memberName);
					chartVOsWithoutdevices.add(chartVO);
				}
				else {
					String rejectedNotices="";
					for(Resolution r: rejectedResolution){
						if(rejectedResolution.get(0).equals(r)){
							rejectedNotices=r.getNumber().toString();
						}else{
							rejectedNotices=rejectedNotices+","+r.getNumber().toString();
						}
					}
					ChartVO chartVO = new ChartVO(memberId, memberName, deviceVOs);
					chartVO.setRejectedNotices(rejectedNotices);
					chartVO.setExtraCount(extraCount);
					chartVO.setRejectedCount(rejectedResolution.size());
					chartVOsWithDevices.add(chartVO);
				}
			}
			
			chartVOsWithDevices = ChartVO.sort(chartVOsWithDevices, ApplicationConstants.ASC,deviceType.getType());
			chartVOsWithoutdevices = ChartVO.sort(chartVOsWithoutdevices, ApplicationConstants.ASC,deviceType.getType());
			
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
		Chart chart = ResolutionChart.find(session, deviceType, locale);
		if(chart != null) {
			List<ChartVO> chartVOsWithDevices = new ArrayList<ChartVO>();
			List<ChartEntry> chartEntries = chart.getChartEntries();
			for(ChartEntry ce : chartEntries) {
				Long memberId = ce.getMember().getId();
				String memberName = ce.getMember().getFullname();
				
				List<DeviceVO> deviceVOs = new ArrayList<DeviceVO>();				
				for(Device d : ce.getDevices()) {
					Resolution r = (Resolution) d;
					String internalStatusType = null;
					if(r.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						internalStatusType = r.getInternalStatusLowerHouse().getType();						
					}
					else if(r.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						internalStatusType = r.getInternalStatusUpperHouse().getType();						
					}
					if(internalStatusType.equals(ApplicationConstants.RESOLUTION_FINAL_ADMISSION)){						
						DeviceVO deviceVO = new DeviceVO(r.formatNumber(), r.getRevisedNoticeContent());
						deviceVO.setNumber(r.getNumber());
						deviceVOs.add(deviceVO);						
					}
				}				
				if(!deviceVOs.isEmpty()) {
					ChartVO chartVO = new ChartVO(memberId, memberName, deviceVOs);
					
					chartVOsWithDevices.add(chartVO);
				}				
			}			
			chartVOsWithDevices = ChartVO.sort(chartVOsWithDevices, ApplicationConstants.ASC,deviceType.getType());
			chartVOs.addAll(chartVOsWithDevices);			
		}
		else {
			chartVOs = null;
		}		
		return chartVOs;
	}

	public static Integer maxChartedResolutions(final Session session, final String locale) throws ELSException {
		Integer maxResolutions = 0;
		
		HouseType houseType = session.getHouse().getType();
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			maxResolutions = ResolutionChart.maxResolutionsOnChartLH();
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			maxResolutions = ResolutionChart.maxResolutionsOnChartUH();
		}
		
		Integer maxChartedResolutions = ResolutionChart.findMaxChartedResolutions(session, locale);
		if(maxChartedResolutions > maxResolutions) {
			maxResolutions = maxChartedResolutions;;
		}

		return maxResolutions;
	}
	
	private static List<DeviceVO> getDeviceVOs(final List<Device> devices) {
		List<DeviceVO> deviceVOs = new ArrayList<DeviceVO>();
		
		for(Device d : devices) {
			Resolution r = (Resolution) d;
			Boolean isFactualRecieved=false;
			if(r.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				String internalStatusType = r.getInternalStatusLowerHouse().getType();
				String localisedStatusType=r.getInternalStatusLowerHouse().getName();
				if(!internalStatusType.equals(ApplicationConstants.RESOLUTION_FINAL_REJECTION)&&
					!internalStatusType.equals(ApplicationConstants.RESOLUTION_FINAL_REPEATREJECTION)){
					if(r.getFactualPosition()!=null){
						if(!r.getFactualPosition().isEmpty()){
							isFactualRecieved=true;
						}
					}
					DeviceVO deviceVO = new DeviceVO(r.getId(), r.getNumber(), internalStatusType,localisedStatusType,isFactualRecieved);
					deviceVOs.add(deviceVO);
				}
			}
			else if(r.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
				String internalStatusType = r.getInternalStatusUpperHouse().getType();
				String localisedStatusType=r.getInternalStatusUpperHouse().getName();
				if(!internalStatusType.equals(ApplicationConstants.RESOLUTION_FINAL_REJECTION)
					&&!internalStatusType.equals(ApplicationConstants.RESOLUTION_FINAL_REPEATREJECTION)){
					if(r.getFactualPosition()!=null){
						if(!r.getFactualPosition().isEmpty()){
							isFactualRecieved=true;
						}
					}
					DeviceVO deviceVO = new DeviceVO(r.getId(), r.getNumber(), internalStatusType,localisedStatusType,isFactualRecieved);
					deviceVOs.add(deviceVO);
				}
			}
		}
		
		return deviceVOs;
	}
	
	private static Integer findMaxChartedResolutions(final Session session, final String locale) throws ELSException {
		return Chart.getChartRepository().findMaxChartedResolution(session, locale);
	}
 
	
	//=============== DOMAIN METHODS ================
	public static Chart create(final Chart chart) throws ELSException {
		HouseType houseType = chart.getSession().getHouse().getType();
		
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			return ResolutionChart.createLH(chart);
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			return ResolutionChart.createUH(chart);
		}
		
		return chart;
	}

	public static List<Member> findMembers(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().findMembers(session, deviceType, locale);
	}

	public static List<Resolution> findResolutions(final Member member,
			final Session session, 
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().findResolutions(member, session, deviceType, locale);
	}

	public static List<Resolution> findResolutions(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		return Chart.getChartRepository().findResolutions(session, deviceType, locale);
	}

	public static Boolean addToChart(final Resolution resolution) throws ELSException {
		Session session = resolution.getSession();
		HouseType houseType = session.getHouse().getType();
		
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			return ResolutionChart.addToChartLH(resolution);
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			return ResolutionChart.addToChartUH(resolution);
		}
		
		return false;
	}

	public static Chart find(final Session session, 
			final DeviceType deviceType,
			final String locale) throws ELSException {
		return Chart.getChartRepository().find(session, deviceType, locale);
	}

	public static Boolean isProcessed(final Chart chart) throws ELSException {
		Chart newChart = 
			ResolutionChart.find(chart.getSession(), chart.getDeviceType(), chart.getLocale());
		
		if(newChart != null) {
			String excludeInternalStatus = ApplicationConstants.RESOLUTION_SYSTEM_TO_BE_PUTUP;
			return Chart.getChartRepository().isProcessed(chart, excludeInternalStatus);
		}
		
		return true;
	}
	
	
	//=============== COUNCIL METHODS =================
	private static Chart createUH(final Chart chart) throws ELSException {
		Chart newChart = Chart.find(chart);
		
		if(newChart == null) {
			newChart = ResolutionChart.persistChartUH(chart);
			ResolutionChart.updateChart(newChart);
		}
		return newChart;
	}
	
	private static Boolean addToChartUH(final Resolution resolution) throws ELSException {
		if(ResolutionChart.isAssistantProcessed(resolution)) {
			Chart chart = ResolutionChart.find(resolution.getSession(), 
					resolution.getType(), resolution.getLocale());
			if(chart != null) {
				if(ResolutionChart.isEligibleForChart(chart, resolution)) {
					Integer maxNoOfResolutions = ResolutionChart.maxResolutionsOnChartUH();
					return ResolutionChart.addToChartIfApplicable(chart, 
							resolution, maxNoOfResolutions);
				}
			}
		}
		
		return false;
	}

	private static Chart persistChartUH(final Chart chart) {
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
			ResolutionChart.chartEntriesForMembersWithResolutionUH(chart.getSession(), 
					chart.getDeviceType(), startTime, endTime, internalStatuses, 
					ApplicationConstants.ASC, chart.getLocale());
		
		List<ChartEntry> entriesForMembersWithoutResolution =
			ResolutionChart.chartEntriesForMembersWithoutResolutionUH(chart.getSession(), 
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
		
		Integer maxResolutionsOnChart = ResolutionChart.maxResolutionsOnChartUH();
		Date currentDate = ResolutionChart.getCurrentDate();
		List<Member> activeMembersWithResolutions = null;;
		try{
			activeMembersWithResolutions = Resolution.findActiveMembersWithResolutions(session, currentDate, deviceType, 
					internalStatuses, startTime, endTime, sortOrder, locale);
			for(Member m : activeMembersWithResolutions) {
				ChartEntry chartEntry = ResolutionChart.newChartEntryUH(session, m, deviceType, 
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
		
		Date currentDate = ResolutionChart.getCurrentDate();
		List<Member> activeMembersWithoutResolutions = null;
		try{
			activeMembersWithoutResolutions = Resolution.findActiveMembersWithoutResolutions(session, currentDate, deviceType,
					internalStatuses, startTime, endTime, sortOrder, locale);
		for(Member m : activeMembersWithoutResolutions) {
			ChartEntry chartEntry = ResolutionChart.newEmptyChartEntry(m, locale);
			chartEntries.add(chartEntry);
		}
		}catch (ELSException e) {
			e.printStackTrace();
		}
		
		return chartEntries;
	}
	
	private static Integer maxResolutionsOnChartUH() {
		CustomParameter noOfResolutions = CustomParameter.
				findByName(CustomParameter.class, "NO_OF_RESOLUTIONS_ON_MEMBER_CHART_LH", "");
		return Integer.valueOf(noOfResolutions.getValue());
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
		for(Device d:candidateRList){
			Resolution r=(Resolution) d;
			if(r.getFileUpperHouse()==null){
				Reference reference = null;
				
				try {
					reference = Resolution.findCurrentFile(r,r.getHouseType());
					r.setFileUpperHouse(Integer.parseInt(reference.getId()));
					r.setFileIndexUpperHouse(Integer.parseInt(reference.getName()));
					r.setFileSentUpperHouse(false);
					
				} catch (ELSException e) {					
					e.printStackTrace();
				}
			}
		}
		ChartEntry chartEntry = new ChartEntry(member, candidateRList, locale);
		return chartEntry;
	}
	
	
	//=============== ASSEMBLY METHODS ================
	private static Chart createLH(final Chart chart) throws ELSException {
		Chart newChart = Chart.find(chart);
		
		if(newChart == null) {
			newChart = ResolutionChart.persistChartLH(chart);
			ResolutionChart.updateChart(newChart);
		}
		return newChart;
	}
	
	private static Boolean addToChartLH(Resolution resolution) throws ELSException {
		if(ResolutionChart.isAssistantProcessed(resolution)) {
			Chart chart = ResolutionChart.find(resolution.getSession(), 
					resolution.getType(), resolution.getLocale());
			if(chart != null) {
				if(ResolutionChart.isEligibleForChart(chart, resolution)) {
					Integer maxNoOfResolutions = ResolutionChart.maxResolutionsOnChartLH();
					return ResolutionChart.addToChartIfApplicable(chart, 
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
			ResolutionChart.chartEntriesForMembersWithResolutionLH(chart.getSession(), 
					chart.getDeviceType(), startTime, endTime, internalStatuses, 
					ApplicationConstants.ASC, chart.getLocale());
		
		List<ChartEntry> entriesForMembersWithoutResolution =
			ResolutionChart.chartEntriesForMembersWithoutResolutionLH(chart.getSession(), 
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
		
		Integer maxResolutionsOnChart = ResolutionChart.maxResolutionsOnChartLH();
		Date currentDate = ResolutionChart.getCurrentDate();
		List<Member> activeMembersWithResolutions = null;
		try{
			activeMembersWithResolutions = Resolution.findActiveMembersWithResolutions(session, currentDate, deviceType, 
						internalStatuses,startTime, endTime, sortOrder, locale);
			for(Member m : activeMembersWithResolutions) {
				ChartEntry chartEntry = ResolutionChart.newChartEntryLH(session, m, deviceType,
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
		
		Date currentDate = ResolutionChart.getCurrentDate();
		List<Member> activeMembersWithoutResolutions = null;
		try{
			activeMembersWithoutResolutions = Resolution.findActiveMembersWithoutResolutions(session, currentDate, deviceType, 
					internalStatuses, startTime, endTime, sortOrder, locale);
		for(Member m : activeMembersWithoutResolutions) {
			ChartEntry chartEntry = ResolutionChart.newEmptyChartEntry(m, locale);
			chartEntries.add(chartEntry);
		}
		}catch (ELSException e) {
			e.printStackTrace();
		}
		
		return chartEntries;
	}
	
	private static Integer maxResolutionsOnChartLH() {
		CustomParameter noOfDevicesParameter = CustomParameter.findByName(CustomParameter.class, 
				"NO_OF_RESOLUTIONS_ON_MEMBER_CHART_LH", "");
		return Integer.valueOf(noOfDevicesParameter.getValue());
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
		for(Device d:candidateRList){
			Resolution r=(Resolution) d;
			if(r.getFileLowerHouse()==null){
				try {
					Reference reference=Resolution.findCurrentFile(r,r.getHouseType());
					r.setFileLowerHouse(Integer.parseInt(reference.getId()));
					r.setFileIndexLowerHouse(Integer.parseInt(reference.getName()));
					r.setFileSentLowerHouse(false);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ELSException e) {
					e.printStackTrace();
				}
			}

		}
		ChartEntry chartEntry = new ChartEntry(member, candidateRList, locale);
		return chartEntry;
	}
	
	
	//=============== COMMON INTERNAL METHODS =========
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
				if(resolution.getFileLowerHouse()==null){
					Reference reference=Resolution.findCurrentFile(resolution,resolution.getHouseType());
					resolution.setFileLowerHouse(Integer.parseInt(reference.getId()));
					resolution.setFileIndexLowerHouse(Integer.parseInt(reference.getName()));
					resolution.setFileSentLowerHouse(false);
				}
				
			}
			else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				resolution.setInternalStatusUpperHouse(TO_BE_PUT_UP);
				resolution.setRecommendationStatusUpperHouse(TO_BE_PUT_UP);
				if(resolution.getFileUpperHouse()==null){
					Reference reference=Resolution.findCurrentFile(resolution,resolution.getHouseType());
					resolution.setFileUpperHouse(Integer.parseInt(reference.getId()));
					resolution.setFileIndexUpperHouse(Integer.parseInt(reference.getName()));
					resolution.setFileSentUpperHouse(false);
				}
			}
			resolution.simpleMerge();
			
			// Add the Resolution to the Chart.
			ChartEntry chartEntry = ResolutionChart.find(chart.getChartEntries(), member);
			List<Device> devices = chartEntry.getDevices();
			devices.add(resolution);
			devices = ResolutionChart.reorderResolutions(devices);
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