package org.mkcl.els.common.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Chart;
import org.mkcl.els.domain.ChartEntry;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;

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
	private String[] group1Chart1 = {"326, 48859, 48861, 48862, 48863, 48864",
			"339, 48898, 48905, 48907, 48908, 48910",
			"347, 48926, 48929, 48930, 48932, 48933",
			"298, 48956, 48958, 48971, 48972",
			"856, 49008, 48985, 48997",
			"855, 49014, 49015, 49016, 49017, 49018",
			"310, 49051, 49053, 49055, 49063, 49064",
			"305, 49103",
			"355, 49107, 49111, 49120, 49121, 49122",
			"904, 49132, 49133, 49134",
			"356, 49173, 49179, 49180",
			"295, 49194, 49196, 49197, 49199, 49200",
			"363, 49225, 49227, 49228, 49239, 49249",
			"358, 49275, 49284",
			"321, 49289, 49306, 49307",
			"349, 49330, 49340, 49344",
			"909, 49361, 49364, 49376, 50269",
			"906, 49379, 49390, 49393, 49398, 49399",
			"852, 49418, 49421, 49431, 49433",
			"296, 49443, 49444, 49451, 49459",
			"907, 49466, 49472, 49474, 49476, 49477",
			"322, 49544, 49545, 49546, 49547, 49548",
			"372, 49555, 49556, 49557, 49558",
			"344, 49589, 49591, 49600, 49610, 49614",
			"307, 49617, 49618, 49620, 49630",
			"364, 49649, 49650, 49651, 49652",
			"851, 49707, 49708, 49709",
			"911, 49710, 49711",
			"325, 49741, 49742, 49743, 49744, 49745",
			"314, 49797, 49798, 49799, 49800",
			"912, 49811, 49812, 49822, 49823, 49826",
			"3002, 49835",
			"900, 49848, 49849, 49850, 49851, 49852",
			"348, 49894, 49909",
			"910, 49919, 49937",
			"902, 49942, 49951, 49959, 49969, 49971",
			"350, 50012, 50014, 50020, 50028, 50029",
			"365, 50034, 50036, 50044, 50050, 50051",
			"345, 50065, 50066, 50067, 50068, 50070",
			"301, 50106, 50110, 50111, 50117",
			"341, 50130, 50131, 50135, 50136, 50138",
			"346, 50188, 50193, 50199, 50200",
			"366, 50225, 50239, 50244",
			"353, 50250, 50262, 50263, 50265, 50266",
			"903, 49977, 49979, 49990, 49997, 49999",
			"338, 49519",
			"299",
			"306",
			"312",
			"313",
			"308",
			"319",
			"323",
			"333",
			"334",
			"342",
			"343",
			"351",
			"354",
			"357",
			"905",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3001"};
	
	private String[] group1Chart2 = {"339, 48911, 48914",
			"347, 48934, 48936, 48943, 48951",
			"855, 49044",
			"310, 49067, 49072, 49073, 49075",
			"355, 49123, 49124, 49126, 49127",
			"295, 49202, 49205, 49208, 49215, 49219",
			"363, 49246, 49252",
			"907, 49478, 49487, 49491, 49494, 49495",
			"372, 49560",
			"364, 49653, 49659, 49661",
			"314, 49802",
			"912, 49829",
			"900, 49853, 49873",
			"350, 50031, 50033",
			"365, 50055, 50058, 50062",
			"345, 50071",
			"341, 50134, 50145, 50149, 50152",
			"346, 50205",
			"322, 49554",
			"344, 49592",
			"356, 49175",
			"903, 50000, 50002",
			"296",
			"298",
			"299",
			"301",
			"305",
			"306",
			"307",
			"312",
			"313",
			"318",
			"319",
			"321",
			"323",
			"325",
			"326",
			"333",
			"334",
			"338",
			"342",
			"343",
			"348",
			"349",
			"351",
			"353",
			"354",
			"357",
			"358",
			"366",
			"851",
			"852",
			"853",
			"856",
			"902",
			"904",
			"905",
			"906",
			"909",
			"910",
			"911",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3001",
			"3002"};
	
	private String[] group1Chart3 = {"346, 50192, 50213",
			"295, 49207",
			"907, 49482, 49496",
			"296",
			"298",
			"299",
			"301",
			"305",
			"306",
			"307",
			"310",
			"312",
			"313",
			"314",
			"318",
			"319",
			"321",
			"322",
			"323",
			"325",
			"326",
			"333",
			"334",
			"338",
			"339",
			"341",
			"342",
			"343",
			"344",
			"345",
			"347",
			"348",
			"349",
			"350",
			"351",
			"353",
			"354",
			"355",
			"356",
			"357",
			"358",
			"363",
			"364",
			"365",
			"366",
			"372",
			"851",
			"852",
			"853",
			"855",
			"856",
			"900",
			"902",
			"903",
			"904",
			"905",
			"906",
			"909",
			"910",
			"911",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3001",
			"3002"};
	
	private String[] group1Chart4 = {"295",
			"296",
			"298",
			"299",
			"301",
			"305",
			"306",
			"307",
			"310",
			"312",
			"313",
			"314",
			"318",
			"319",
			"321",
			"322",
			"323",
			"325",
			"326",
			"333",
			"334",
			"338",
			"339",
			"341",
			"342",
			"343",
			"344",
			"345",
			"346",
			"347",
			"348",
			"349",
			"350",
			"351",
			"353",
			"354",
			"355",
			"356",
			"357",
			"358",
			"363",
			"364",
			"365",
			"366",
			"372",
			"851",
			"852",
			"853",
			"855",
			"856",
			"900",
			"902",
			"903",
			"904",
			"905",
			"906",
			"907",
			"909",
			"910",
			"911",
			"912",
			"2500",
			"2501",
			"2502",
			"2504",
			"3000",
			"3001",
			"3002"};
	
	private String[] group2Chart1 = {"",
			"",
			"",
			""};
	
	private String[] group2Chart2 = {"",
			"",
			"",
			""};
	
	private String[] group2Chart3 = {"",
			"",
			"",
			""};
	
	private String[] group2Chart4 = {"",
			"",
			"",
			""};
	
	private String[] group3Chart1 = {"",
			"",
			"",
			""};
	
	private String[] group3Chart2 = {"",
			"",
			"",
			""};
	
	private String[] group3Chart3 = {"",
			"",
			"",
			""};
	
	private String[] group3Chart4 = {"",
			"",
			"",
			""};
	
	private String[] group4Chart1 = {"",
			"",
			"",
			""};
	
	private String[] group4Chart2 = {"",
			"",
			"",
			""};
	
	private String[] group4Chart3 = {"",
			"",
			"",
			""};
	
	private String[] group4Chart4 = {"",
			"",
			"",
			""};
	
	private String[] group5Chart1 = {"",
			"",
			"",
			""};
	
	private String[] group5Chart2 = {"",
			"",
			"",
			""};
	
	private String[] group5Chart3 = {"",
			"",
			"",
			""};
	
	private String[] group5Chart4 = {"",
			"",
			"",
			""};
	
	// Comma separated list of Question numbers
	// First token is the parent question and subsequent tokens
	// are clid questions in the order specified.
	private String[] clubbings = {"48864, 48907, 49849, 49215",
			"48905, 49544, 49741, 49560",
			"48910, 49852",
			"48951, 50205",
			"49008, 50068",
			"49015, 50244",
			"49016, 50239",
			"49063, 49443",
			"49072, 49207",
			"49180, 50269",
			"49284, 49745",
			"49289, 50031",
			"49306, 49127, 50055",
			"49307, 49344, 49376, 49650, 49126",
			"49330, 50029",
			"49340, 49617, 50020, 49977, 48911",
			"49390, 49433, 50131",
			"49421, 50262",
			"49545, 49919",
			"49557, 49798",
			"49600, 49979",
			"49610, 49829",
			"49649, 49651"};
	
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
		
		List<ClubbedEntity> clubbedQList = new ArrayList<ClubbedEntity>();
		Status CLUBBED = Status.findByType(
				ApplicationConstants.QUESTION_SYSTEM_CLUBBED, 
				ApplicationConstants.DEFAULT_LOCALE);
		// Since token 0 is Parent Question number, hence start from token 1	
		for(int i = 1; i < length; i++) {	
			String strChildQuestionNumber = tokens[i].trim();
			Integer childQuestionNumber = 
				Integer.valueOf(strChildQuestionNumber);
			
			Question childQuestion = Question.find(session, childQuestionNumber);
			childQuestion.setParent(parentQuestion);
			childQuestion.setInternalStatus(CLUBBED);
			childQuestion.setRecommendationStatus(CLUBBED);
			childQuestion.simpleMerge();
			
			ClubbedEntity clubbedQ = new ClubbedEntity();
			clubbedQ.setPosition(i);
			clubbedQ.setDeviceType(deviceType);
			clubbedQ.setQuestion(childQuestion);
			
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
}