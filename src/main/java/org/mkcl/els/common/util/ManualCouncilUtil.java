package org.mkcl.els.common.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.chart.Chart;
import org.mkcl.els.domain.chart.ChartEntry;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.springframework.http.HttpRequest;

public class ManualCouncilUtil {
	
	/**
	 * STEP 1
	 */
	// groupNo: 1, 2, 3, 4, or 5
	// chartNo: 1, 2, 3, 4, or 5
	public void createChart(final Integer groupNo, final Integer chartNo) {
		if(groupNo.equals(new Integer(1))) {
			if(chartNo.equals(new Integer(1))) {
				createChartHelper(group1, 
						group1Chart1, group1Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				createChartHelper(group1, 
						group1Chart2, group1Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				createChartHelper(group1, 
						group1Chart3, group1Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				createChartHelper(group1, 
						group1Chart4, group1Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(2))) {
			if(chartNo.equals(new Integer(1))) {
				createChartHelper(group2, 
						group2Chart1, group2Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				createChartHelper(group2, 
						group2Chart2, group2Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				createChartHelper(group2, 
						group2Chart3, group2Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				createChartHelper(group2, 
						group2Chart4, group2Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(3))) {
			if(chartNo.equals(new Integer(1))) {
				createChartHelper(group3, 
						group3Chart1, group3Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				createChartHelper(group3, 
						group3Chart2, group3Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				createChartHelper(group3, 
						group3Chart3, group3Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				createChartHelper(group3, 
						group3Chart4, group3Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(4))) {
			if(chartNo.equals(new Integer(1))) {
				createChartHelper(group4, 
						group4Chart1, group4Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				createChartHelper(group4, 
						group4Chart2, group4Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				createChartHelper(group4, 
						group4Chart3, group4Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				createChartHelper(group4, 
						group4Chart4, group4Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(5))) {
			if(chartNo.equals(new Integer(1))) {
				createChartHelper(group5, 
						group5Chart1, group5Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				createChartHelper(group5, 
						group5Chart2, group5Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				createChartHelper(group5, 
						group5Chart3, group5Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				createChartHelper(group5, 
						group5Chart4, group5Chart4AnsweringDate);
			}
		}
	}
	
	/**
	 * STEP 2
	 */
	public void clubQuestions() {
		Session session = Session.findById(Session.class, sessionId);
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.STARRED_QUESTION, 
				ApplicationConstants.DEFAULT_LOCALE);
		int length = clubbings.length;
		for(int i = 0; i < length; i++) {
			clubEach(session, deviceType, clubbings[i]);
		}
	}
	
	/**
	 * STEP 3
	 */
	// groupNo: 1, 2, 3, 4, or 5
	// chartNo: 1, 2, 3, 4, or 5
	public void updateChart(final Integer groupNo, final Integer chartNo) 
		throws ELSException {
		if(groupNo.equals(new Integer(1))) {
			if(chartNo.equals(new Integer(1))) {
				updateChartHelper(group1, group1Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				updateChartHelper(group1, group1Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				updateChartHelper(group1, group1Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				updateChartHelper(group1, group1Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(2))) {
			if(chartNo.equals(new Integer(1))) {
				updateChartHelper(group2, group2Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				updateChartHelper(group2, group2Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				updateChartHelper(group2, group2Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				updateChartHelper(group2, group2Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(3))) {
			if(chartNo.equals(new Integer(1))) {
				updateChartHelper(group3, group3Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				updateChartHelper(group3, group3Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				updateChartHelper(group3, group3Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				updateChartHelper(group3, group3Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(4))) {
			if(chartNo.equals(new Integer(1))) {
				updateChartHelper(group4, group4Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				updateChartHelper(group4, group4Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				updateChartHelper(group4, group4Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				updateChartHelper(group4, group4Chart4AnsweringDate);
			}
		}
		else if(groupNo.equals(new Integer(5))) {
			if(chartNo.equals(new Integer(1))) {
				updateChartHelper(group5, group5Chart1AnsweringDate);
			}
			else if(chartNo.equals(new Integer(2))) {
				updateChartHelper(group5, group5Chart2AnsweringDate);
			}
			else if(chartNo.equals(new Integer(3))) {
				updateChartHelper(group5, group5Chart3AnsweringDate);
			}
			else if(chartNo.equals(new Integer(4))) {
				updateChartHelper(group5, group5Chart4AnsweringDate);
			}
		}
	}
	
	//========== FIELDS TO BE POPULATED ====
	// Each entry in the array is a comma separated String.
	// The first token refers to Member id and the remaining
	// tokens refer to Question numbers.
	// If a Member does not have any Question, then there will
	// be only one token in the String, that of Member id.
	private String[] group1Chart1 = {};
	
	private String[] group1Chart2 = {};
	
	private String[] group1Chart3 = {};
	
	private String[] group1Chart4 = {};
	
	private String[] group2Chart1 = {};
	
	private String[] group2Chart2 = {};
	
	private String[] group2Chart3 = {};
	
	private String[] group2Chart4 = {};
	
	private String[] group3Chart1 = {};
	
	private String[] group3Chart2 = {};
	
	private String[] group3Chart3 = {};
	
	private String[] group3Chart4 = {};
	
	private String[] group4Chart1 = {};
	
	private String[] group4Chart2 = {};
	
	private String[] group4Chart3 = {};
	
	private String[] group4Chart4 = {};
	
	private String[] group5Chart1 = {};
	
	private String[] group5Chart2 = {};
	
	private String[] group5Chart3 = {};
	
	private String[] group5Chart4 = {};
	
	// Comma separated list of Question numbers
	// First token is the parent question and subsequent tokens
	// are child questions in the order specified.
	private String[] clubbings = {"48889, 49866",
			"48961, 49980, 50226, 49453",
			"48959, 49257, 50270, 49541, 49957",
			"48865, 49131, 49245, 49262, 48999",
			"49221, 49228, 49682",
			"49235, 49584 ",
			"49271, 49644, 49674",
			"49273, 49542",
			"49308, 49346",
			"49550, 50021",
			"49614, 49340",
			"49078, 49704",
			"49290, 49321",
			"49593,49864",
			"48899, 49513",
			"48978, 49058",
			"48989, 49243",
			"49287, 49973",
			"49386, 49391,49414",
			"49438, 48981",
			"49441, 49502, 49747, 49750, 50100,50139, 50214,49151,48980,49765",
			"49748, 50228",
			"49807, 49176",
			"49869, 49925 ",
			"49871, 49397",
			"50052, 49068, 49312",
			"50157, 49517",
			"50257, 49752, 50224"};
	
	//========== INTERNAL METHODS ==========
	private void createChartHelper(final Long groupId, 
			final String[] groupChart,
			final String groupChartAnsweringDate) {
		Session session = Session.findById(Session.class, sessionId);
		Group group = Group.findById(Group.class, groupId);
		Date answeringDate = FormaterUtil.formatStringToDate(
				groupChartAnsweringDate, formatType);;
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.STARRED_QUESTION, 
				ApplicationConstants.DEFAULT_LOCALE);
		
		QuestionDates chartAnsweringDate = 
			group.findQuestionDatesByGroupAndAnsweringDate(answeringDate);
		
		Chart chart = new Chart(session, group, answeringDate, deviceType, 
				ApplicationConstants.DEFAULT_LOCALE);
		
		List<ChartEntry> memberChartEntries = 
			this.memberChartEntries(session, chartAnsweringDate, groupChart);		
		chart.getChartEntries().addAll(memberChartEntries);		
		chart.persist();
	}
	
	private List<ChartEntry> memberChartEntries(final Session session,
			final QuestionDates chartAnsweringDate,
			final String[] chart) {
		List<ChartEntry> chartEntries = new ArrayList<ChartEntry>();
		
		int length = chart.length;
		for(int i = 0; i < length; i++) {
			String entry = chart[i];
			ChartEntry chartEntry = 
				this.chartEntry(session, chartAnsweringDate, entry);
			chartEntries.add(chartEntry);
		}
		
		return chartEntries;
	}
	
	private ChartEntry chartEntry(final Session session, 
			final QuestionDates chartAnsweringDate,
			final String entry) {
		String[] tokens = entry.split(",");
		
		// An entry will always have atleast 1 token
		String strMemberId = tokens[0].trim();
		Long memberId = Long.valueOf(strMemberId);
		Member member = Member.findById(Member.class, memberId);
		
		int length = tokens.length;		
		// Member has no Questions
		if(length == 1) {
			ChartEntry chartEntry = new ChartEntry();
			chartEntry.setLocale(ApplicationConstants.DEFAULT_LOCALE);
			chartEntry.setMember(member);
			return chartEntry;
		}
		// Member has Questions
		else {
			List<Device> memberQList = new ArrayList<Device>();
			
			// Since token 0 is Member id, hence start from token 1
			for(int i = 1; i < length; i++) {
				String strQuestionNumber = tokens[i].trim();
				Integer questionNumber = Integer.valueOf(strQuestionNumber);
				Question q = Question.find(session, questionNumber);
				q.setChartAnsweringDate(chartAnsweringDate);
				q.simpleMerge();
				
				memberQList.add(q);
			}
			
			ChartEntry chartEntry = new ChartEntry(member, memberQList, 
					ApplicationConstants.DEFAULT_LOCALE);
			return chartEntry;
		}
	}
	
	private void clubEach(final Session session, 
			final DeviceType deviceType,
			final String entry) {
		String[] tokens = entry.split(",");
		int length = tokens.length;
		
		// First token is the parent question
		String strParentQuestionNumber = tokens[0].trim();
		Integer parentQuestionNumber = Integer.valueOf(strParentQuestionNumber);
		Question parentQuestion = Question.find(session, parentQuestionNumber);
		
		// Statuses of Parent Question
		Status internalStatus = parentQuestion.getInternalStatus();
		Status recommendationStatus = parentQuestion.getRecommendationStatus();
		Status status = parentQuestion.getStatus();
		
		List<ClubbedEntity> clubbedQList = new ArrayList<ClubbedEntity>();
		List<ClubbedEntity> alreadyClubbedQList = 
				Question.findClubbedEntitiesByPosition(parentQuestion);
		Integer position = new Integer(0);
		if(alreadyClubbedQList != null && ! alreadyClubbedQList.isEmpty()) {
			clubbedQList.addAll(alreadyClubbedQList);
			position = clubbedQList.size();
		}
		
		// Since token 0 is Parent Question number, hence start from token 1	
		for(int i = 1; i < length; i++) {	
			String strChildQuestionNumber = tokens[i].trim();
			Integer childQuestionNumber = 
				Integer.valueOf(strChildQuestionNumber);
			
			Question childQuestion = Question.find(session, childQuestionNumber);
			childQuestion.setParent(parentQuestion);
			childQuestion.setInternalStatus(internalStatus);
			childQuestion.setRecommendationStatus(recommendationStatus);
			childQuestion.setStatus(status);
			childQuestion.simpleMerge();
			
			ClubbedEntity clubbedQ = new ClubbedEntity();
			clubbedQ.setPosition(position + i);
			clubbedQ.setDeviceType(deviceType);
			clubbedQ.setQuestion(childQuestion);
			clubbedQ.persist();
			
			clubbedQList.add(clubbedQ);
		}
		
		// Add the clubbedQList to Parent Question and merge
		parentQuestion.setClubbedEntities(clubbedQList);
		parentQuestion.simpleMerge();
	}
	
	private void updateChartHelper(final Long groupId,
			final String groupChartAnsweringDate) throws ELSException {
		Session session = Session.findById(Session.class, sessionId);
		Group group = Group.findById(Group.class, groupId);
		Date answeringDate = FormaterUtil.formatStringToDate(
				groupChartAnsweringDate, formatType);;
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.STARRED_QUESTION, 
				ApplicationConstants.DEFAULT_LOCALE);
		
		Status TO_BE_PUT_UP = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, 
				ApplicationConstants.DEFAULT_LOCALE);
		int toBePutUpPriority = TO_BE_PUT_UP.getPriority();
		
		Status CLUBBED = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_CLUBBED, 
				ApplicationConstants.DEFAULT_LOCALE);
		
		List<Question> chartedQuestions = 
			Chart.findQuestions(session, group, answeringDate, deviceType, 
					ApplicationConstants.DEFAULT_LOCALE);
		for(Question q : chartedQuestions) {
			Status internalStatus = q.getInternalStatus();
			int internalStatusPriority = internalStatus.getPriority();
			if(internalStatusPriority < toBePutUpPriority
					&& ! (internalStatus.getType().equals(CLUBBED.getType()))) {
				q.setInternalStatus(TO_BE_PUT_UP);
				q.setRecommendationStatus(TO_BE_PUT_UP);
				q.simpleMerge();
			}
		}
	}

	// Id of the current Council session
	private Long sessionId = new Long(500);
	
	// Ids of Council groups as stored in the database.
	private Long group1 = new Long(2500);
	private Long group2 = new Long(2501);
	private Long group3 = new Long(2502);
	private Long group4 = new Long(2503);
	private Long group5 = new Long(2504);
	
	// AnsweringDate for each Chart of each Group
	private String group1Chart1AnsweringDate = "02/06/2014";
	private String group1Chart2AnsweringDate = "09/06/2014";
	private String group1Chart3AnsweringDate = "16/06/2014";
	private String group1Chart4AnsweringDate = "23/06/2014";
	
	private String group2Chart1AnsweringDate = "03/06/2014";
	private String group2Chart2AnsweringDate = "10/06/2014";
	private String group2Chart3AnsweringDate = "17/06/2014";
	private String group2Chart4AnsweringDate = "24/06/2014";
	
	private String group3Chart1AnsweringDate = "04/06/2014";
	private String group3Chart2AnsweringDate = "11/06/2014";
	private String group3Chart3AnsweringDate = "18/06/2014";
	private String group3Chart4AnsweringDate = "25/06/2014";
	
	private String group4Chart1AnsweringDate = "05/06/2014";
	private String group4Chart2AnsweringDate = "12/06/2014";
	private String group4Chart3AnsweringDate = "19/06/2014";
	private String group4Chart4AnsweringDate = "26/06/2014";
	
	private String group5Chart1AnsweringDate = "06/06/2014";
	private String group5Chart2AnsweringDate = "13/06/2014";
	private String group5Chart3AnsweringDate = "20/06/2014";
	private String group5Chart4AnsweringDate = "27/06/2014";
		
	// Date format type
	private String formatType = "dd/MM/yyyy";
	
	
	public String addToChart(){
		String strQuestionIds[]={"7663",
				"7673",
				"7714",
				"7717",
				"7731",
				"7737",
				"7739",
				"7754",
				"7782",
				"7783",
				"7792",
				"7888",
				"7948",
				"7965",
				"7980",
				"8004",
				"8012",
				"8052",
				"8150",
				"8184",
				"8187",
				"8188",
				"8211",
				"8243",
				"8261",
				"8300",
				"8393",
				"8415",
				"8430",
				"8433",
				"8494",
				"10027",
				"10033",
				"10056",
				"10059",
				"10109",
				"10114",
				"10118",
				"10119",
				"10120",
				"10121",
				"10132",
				"10213",
				"10257",
				"10265",
				"10274",
				"10275",
				"10353",
				"10503",
				"10524",
				"10550",
				"10551",
				"14415",
				"14503",
				"14837",
				"16338",
				"17654",
				"17658",
				"18087",
				"19646"};
		for(int i=0;i<strQuestionIds.length;i++){
			Question q=Question.findById(Question.class, Long.parseLong(strQuestionIds[i]));
			Chart chart1=new Chart(q.getSession(), q.getGroup(), q.getChartAnsweringDate().getAnsweringDate(), q.getOriginalType(), q.getLocale());
			try {
				Chart chart=Chart.find(chart1);
				ChartEntry chartEntry=Chart.find(chart,q.getPrimaryMember());
				List<Device> chartEntryDevices=chartEntry.getDevices();
				List<Question> onChartQuestions=new ArrayList<Question>();
				for(Device d:chartEntryDevices){
					Question q1=(Question) d;
					onChartQuestions.add(q1);
				}
				onChartQuestions.add(q);
				
				List<Question> questions=reorderQuestions(onChartQuestions, q.getType(), q.getChartAnsweringDate().getAnsweringDate());
				List<Device> onChartDevices=new ArrayList<Device>();
				for(Question q2:questions){
					Device d=q2;
					onChartDevices.add(d);
				}
				chartEntry.setDevices(onChartDevices);
				chartEntry.merge();
				
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "FAIL";
			}
		}
		return "SUCCESS";
	}
	
	
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
			nonDatedQList = Question.sortByNumber(nonDatedQList, ApplicationConstants.ASC);
			afterDatedQList = Question.sortByAnsweringDate(afterDatedQList, ApplicationConstants.ASC);
			
			List<Question> candidateQList = new ArrayList<Question>();
			candidateQList.addAll(datedQList);
			candidateQList.addAll(beforeDatedQList);
			candidateQList.addAll(nonDatedQList);
			candidateQList.addAll(afterDatedQList);
		
			// ASSERT: The size of onChartQuestions should be equal to the size
			// 		   of candidateQList
			return candidateQList;
		}
	}
	
	public static void performActionOnConvertToUnstarredAndAdmit(Question domain) {		
		DeviceType newDeviceType=DeviceType.findByType(ApplicationConstants.UNSTARRED_QUESTION,domain.getLocale());
		DeviceType originalDeviceType=DeviceType.findByType(ApplicationConstants.STARRED_QUESTION,domain.getLocale());
		domain.setType(newDeviceType);
		domain.setOriginalType(originalDeviceType);
		Status finalStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		domain.setInternalStatus(finalStatus);
		if(domain.getRevisedSubject()==null){			
			domain.setRevisedSubject(domain.getSubject());			
		}else if(domain.getRevisedSubject().isEmpty()){
			domain.setRevisedSubject(domain.getSubject());
		}
		if(domain.getRevisedQuestionText()==null){			
			domain.setRevisedQuestionText(domain.getQuestionText());			
		}else if(domain.getRevisedQuestionText().isEmpty()){
			domain.setRevisedQuestionText(domain.getQuestionText());
		}
		if(domain.getRevisedReason()==null){
			domain.setRevisedReason(domain.getReason());
		}else if(domain.getRevisedReason().isEmpty()){
			domain.setRevisedReason(domain.getReason());
		}
		if(domain.getRevisedBriefExplanation()==null){
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());
		}else if(domain.getRevisedBriefExplanation().isEmpty()){
			domain.setRevisedBriefExplanation(domain.getBriefExplanation());
		}

		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		if(clubbedEntities!=null){
			String subject=null;
			String questionText=null;
			String reasonText=null;
			String briefExplainationText=null;
			if(domain.getRevisedSubject()!=null && !domain.getRevisedSubject().isEmpty()){
				subject=domain.getRevisedSubject();				
			}else{
				subject=domain.getSubject();
			}
			if(domain.getRevisedQuestionText()!=null && !domain.getRevisedQuestionText().isEmpty()){
				questionText=domain.getRevisedQuestionText();
			}else{
				questionText=domain.getQuestionText();
			}
			if(domain.getRevisedReason()!=null && !domain.getRevisedReason().isEmpty()){
				reasonText=domain.getRevisedQuestionText();
			}else{
				reasonText=domain.getReason();
			}
			if(domain.getRevisedBriefExplanation()!=null && !domain.getRevisedBriefExplanation().isEmpty()){
				briefExplainationText=domain.getRevisedQuestionText();
			}else{
				briefExplainationText=domain.getBriefExplanation();
			}
			Status nameClubbing=Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, domain.getLocale());
			for(ClubbedEntity i:clubbedEntities){
				Question question=i.getQuestion();
				if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)){
					question.setRevisedSubject(subject);
					question.setRevisedQuestionText(questionText);
					question.setRevisedReason(reasonText);
					question.setRevisedBriefExplanation(briefExplainationText);					
					question.setType(newDeviceType);
					question.setOriginalType(originalDeviceType);
					question.setStatus(domain.getStatus());
					question.setInternalStatus(domain.getInternalStatus());
					question.setInternalStatus(domain.getRecommendationStatus());
				}else if(question.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING)){
					question.setInternalStatus(nameClubbing);
				}			
				question.simpleMerge();
			}
		}
		
		if(domain.getParent() != null) {
			ClubbedEntity.updateClubbing(domain);

			// Hack (07May2014): Commenting the following line results in 
			// OptimisticLockException.
			domain.setVersion(domain.getVersion() + 1);
		}
	}
}