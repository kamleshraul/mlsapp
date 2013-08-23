/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.test.TestController.java
 * Created On: Jul 4, 2013
 */



package org.mkcl.els.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberBallotFinalBallotVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseReportVO;
import org.mkcl.els.common.vo.MemberBallotQuestionDistributionVO;
import org.mkcl.els.common.vo.MemberBallotVO;
import org.mkcl.els.common.vo.QuestionDatesVO;
import org.mkcl.els.common.vo.QuestionSequenceVO;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Ballot;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.VDepartment;
import org.mkcl.els.domain.VEmployee;
import org.mkcl.els.domain.VProject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

 
// TODO: Auto-generated Javadoc
/**
 * The Class TestController.
 *
 * @author vikasg
 */
@Controller
@RequestMapping("/test")
public class TestController extends BaseController {
	
	/**
	 * Test page.
	 *
	 * @param request the request
	 * @return the string
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Test page.
	 */
	@RequestMapping(value="/testpage", method=RequestMethod.GET)
    public String testPage(final HttpServletRequest request){
		String returnURL = "";
		Session session = Session.findById(Session.class, new Long(50));
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE, "mr_IN");
		Calendar cldAnsweringDate = Calendar.getInstance();
		cldAnsweringDate.set(2013, 7, 17);
		Date answeringDate = cldAnsweringDate.getTime();
		Ballot ballot = new Ballot(session, deviceType, answeringDate, null, "mr_IN");
		try{
			Ballot.updateBallotQuestions(ballot, Status.findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, "mr_IN"));
		}catch (ELSException e) {
			e.printStackTrace();
		}
		
    	return returnURL;
    }
	
	/**
	 * Find all constituencies by house type.
	 *
	 * @param request the request
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find all constituencies by house type.
	 */
	@RequestMapping(value="/constituenciesall",method=RequestMethod.GET)
	 public @ResponseBody List<MasterVO> findAllConstituenciesByHouseType(final HttpServletRequest request){
		
		List<MasterVO> constituencies = null;
		try{
			constituencies = Constituency.findAllByHouseType("lowerhouse", "mr_IN");
		}catch (ELSException e) {
			e.printStackTrace();
		}
		
    	return constituencies;
    }
	
	/**
	 * Find all by display name.
	 *
	 * @param request the request
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find all by display name.
	 */
	@RequestMapping(value="/constituenciesbydisplayname",method=RequestMethod.GET)
	 public @ResponseBody List<Constituency> findAllByDisplayName(final HttpServletRequest request){
		
		HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
		List<Constituency> constituencies = null;
		
		try{
			constituencies = Constituency.findAllByDisplayName(houseType,"२१-मलकापूर, जिल्हा बुलढाणा", "mr_IN");
		}catch (ELSException e) {
			e.printStackTrace();
		}
		
		return constituencies;
   }
	
	
	/**
	 * Constituenciesby name.
	 *
	 * @param request the request
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Constituenciesby name.
	 */
	@RequestMapping(value = "/constituenciesbyname", method = RequestMethod.GET)
	public @ResponseBody
	List<MasterVO> constituenciesbyName(final HttpServletRequest request) {

		List<MasterVO> constituencies = null;
		try{
			constituencies = Constituency.findVOByDefaultStateAndHouseType("वर्सोवा", "lowerhouse",
						"mr_IN", "name", ApplicationConstants.ASC);
		}catch (ELSException e) {
			e.printStackTrace();
		}

		return constituencies;
	}

	/**
	 * Find answering dates.
	 *
	 * @param request the request
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find answering dates.
	 */
	@RequestMapping(value = "/findansweringdates", method = RequestMethod.GET)
	public @ResponseBody List<String> findAnsweringDates(final HttpServletRequest request, final Locale locale) {

		try {
			return Group.findAnsweringDates(new Long(request.getParameter("qdid")), locale.toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		} catch (ELSException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Find by number house type session type year.
	 *
	 * @param request the request
	 * @return the group
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find by number house type session type year.
	 */
	@RequestMapping(value = "/findByNumberHouseTypeSessionTypeYear", method = RequestMethod.GET)
	public @ResponseBody Group findByNumberHouseTypeSessionTypeYear(final HttpServletRequest request) {

		HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
		SessionType sessionType = SessionType.findById(SessionType.class, new Long(5));
		try {
			return Group.findByNumberHouseTypeSessionTypeYear(new Integer(4), houseType, sessionType, new Integer(2013));
		} catch (ELSException e) {
			e.printStackTrace();
			return null; 
		}
	}
	
	/**
	 * Findhtstmiyear.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the group
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Findhtstmiyear.
	 */
	@RequestMapping(value = "/findhtstmiyear", method = RequestMethod.GET)
	public @ResponseBody Group findhtstmiyear(final HttpServletRequest request, final Locale locale) {

		HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
		SessionType sessionType = SessionType.findById(SessionType.class, new Long(5));
		Ministry ministry = Ministry.findById(Ministry.class, new Long(4));
		try {
			return Group.find(ministry, houseType, new Integer(2013), sessionType, locale.toString());
		} catch (ELSException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Find question date by group.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find question date by group.
	 */
	@RequestMapping(value = "/findQuestionDateByGroup", method = RequestMethod.GET)
	public @ResponseBody
	List<MasterVO> findQuestionDateByGroup(HttpServletRequest request, Locale locale) {

		HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
		SessionType sessionType = SessionType.findById(SessionType.class, new Long(5));
		
		List<MasterVO> qds = null;
		try {
			qds = Group.findQuestionDateByGroup(houseType, sessionType, new Integer(2013), new Integer(2), locale.toString());
		} catch (ELSException e) {
			e.printStackTrace();
		}
		return qds;
	}
	
	/**
	 * Find ministries by name.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find ministries by name.
	 */
	@RequestMapping(value = "/findMinistriesByName", method = RequestMethod.GET)
	public @ResponseBody List<Ministry> findMinistriesByName(final HttpServletRequest request, final Locale locale) {

		try {
			return Group.findMinistriesByName(new Long(151));
		} catch (ELSException e) {
			e.printStackTrace();	
		}
		return null;
	}
	
	
	/**
	 * Findbysessionansdt.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the group
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Findbysessionansdt.
	 */
	@RequestMapping(value = "/findbysessionansdt", method = RequestMethod.GET)
	public @ResponseBody Group findbysessionansdt(final HttpServletRequest request, final Locale locale) {

		Session session = Session.findById(Session.class, new Long(50));
		Calendar cldAnsweringDate = Calendar.getInstance();
		cldAnsweringDate.setTime(new Date());
		cldAnsweringDate.add(Calendar.DAY_OF_MONTH, 21);
		Date answeringDate = cldAnsweringDate.getTime();
		
		try {
			return Group.find(session, answeringDate, locale.toString());
		} catch (ELSException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Find ministries by priority.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find ministries by priority.
	 */
	@RequestMapping(value = "/findMinistriesByPriority", method = RequestMethod.GET)
	public @ResponseBody List<Ministry> findMinistriesByPriority(final HttpServletRequest request, final Locale locale) {
		
		String type = request.getParameter("type");
		try{
			if(type != null){
				if(!type.isEmpty()){
					if(type.equals("number")){
						return Group.findMinistriesByPriority(new Long(2));
					}else if(type.equals("group")){
						Group group = Group.findById(Group.class, new Long(2));
						return Group.findMinistriesByPriority(group);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Find ministries in groups for session excluding given group.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find ministries in groups for session excluding given group.
	 */
	@RequestMapping(value = "/findMinistriesInGroupsForSessionExcludingGivenGroup", method = RequestMethod.GET)
	public @ResponseBody List<Ministry> findMinistriesInGroupsForSessionExcludingGivenGroup(final HttpServletRequest request, final Locale locale) {
		
		HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
		SessionType sessionType = SessionType.findById(SessionType.class, new Long(5));
		try {
			return Group.findMinistriesInGroupsForSessionExcludingGivenGroup(houseType, sessionType, new Integer(2013), new Integer(4), locale.toString());
		} catch (ELSException e) {
			e.printStackTrace();
		}
		return new ArrayList<Ministry>();
	}
	
	/**
	 * Find ministries in groups for session.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find ministries in groups for session.
	 */
	@RequestMapping(value = "/findMinistriesInGroupsForSession", method = RequestMethod.GET)
	public @ResponseBody List<Ministry> findMinistriesInGroupsForSession(final HttpServletRequest request, final Locale locale) {
		
		HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
		SessionType sessionType = SessionType.findById(SessionType.class, new Long(5));
		try {
			return Group.findMinistriesInGroupsForSession(houseType, sessionType, new Integer(2013), locale.toString());
		} catch (ELSException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<Ministry>();
	}
	
	/**
	 * Find group numbers for session excluding given group.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find group numbers for session excluding given group.
	 */
	@RequestMapping(value = "/findGroupNumbersForSessionExcludingGivenGroup", method = RequestMethod.GET)
	public @ResponseBody List<Integer> findGroupNumbersForSessionExcludingGivenGroup(final HttpServletRequest request, final Locale locale) {
		
		HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
		SessionType sessionType = SessionType.findById(SessionType.class, new Long(5));
		try {
			return Group.findGroupNumbersForSessionExcludingGivenGroup(houseType, sessionType, new Integer(2013), new Integer(request.getParameter("groupNumber")), locale.toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ELSException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<Integer>();
	}
	
	/**
	 * Find group numbers for session.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find group numbers for session.
	 */
	@RequestMapping(value = "/findGroupNumbersForSession", method = RequestMethod.GET)
	public @ResponseBody List<Integer> findGroupNumbersForSession(final HttpServletRequest request, final Locale locale) {
		
		HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
		SessionType sessionType = SessionType.findById(SessionType.class, new Long(5));
		try {
			return Group.findGroupNumbersForSession(houseType, sessionType, new Integer(request.getParameter("year")), locale.toString());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ArrayList<Integer>();
	}
	
	/**
	 * Find all group dates formatted.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find all group dates formatted.
	 */
	@RequestMapping(value = "/findAllGroupDatesFormatted", method = RequestMethod.GET)
	public @ResponseBody List<QuestionDatesVO> findAllGroupDatesFormatted(final HttpServletRequest request, final Locale locale) {
		
		HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
		SessionType sessionType = SessionType.findById(SessionType.class, new Long(5));
		return Group.findAllGroupDatesFormatted(houseType, sessionType, new Integer(request.getParameter("year")), locale.toString());
	}
	
	/**
	 * Memberattendfind all.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Memberattendfind all.
	 */
	@RequestMapping(value = "/memberattendfindAll", method = RequestMethod.GET)
	public @ResponseBody List<MemberBallotAttendance> memberattendfindAll(final HttpServletRequest request, final Locale locale) {
		
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		try{
			return MemberBallotAttendance.findAll(session, deviceType, "true", new Integer(1), "", locale.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<MemberBallotAttendance>();
	}
	
	/**
	 * View ballotmember.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * View ballotmember.
	 */
	@RequestMapping(value = "/viewBallotmember", method = RequestMethod.GET)
	public @ResponseBody List<MemberBallotFinalBallotVO> viewBallotmember(final HttpServletRequest request, final Locale locale) {
		
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		String answeringDate = "2013-07-17";
		try{
			return MemberBallot.viewFinalBallot(session, deviceType, answeringDate, locale.toString());
		}catch (Exception e) {
			
		}
		return new ArrayList<MemberBallotFinalBallotVO>();
	}
	
	/**
	 * Find member wise report vo.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the member ballot member wise report vo
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Find member wise report vo.
	 */
	@RequestMapping(value = "/findMemberWiseReportVO", method = RequestMethod.GET)
	public @ResponseBody MemberBallotMemberWiseReportVO findMemberWiseReportVO(final HttpServletRequest request, final Locale locale) {
		
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		Member member = Member.findById(Member.class, new Long(296));
		try{
			return MemberBallot.findMemberWiseReportVO(session, deviceType, member, locale.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * View question distribution.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * View question distribution.
	 */
	@RequestMapping(value = "/viewQuestionDistribution", method = RequestMethod.GET)
	public @ResponseBody List<MemberBallotQuestionDistributionVO> viewQuestionDistribution(final HttpServletRequest request, final Locale locale) {
		
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		//Member member = Member.findById(Member.class, new Long(296));
		try{
			return MemberBallot.viewQuestionDistribution(session, deviceType, locale.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<MemberBallotQuestionDistributionVO>(); 
	}
	
	/**
	 * Gets the member ballot v os.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the member ballot v os
	 */
	@RequestMapping(value = "/getMemberBallotVOs", method = RequestMethod.GET)
	public @ResponseBody List<MemberBallotVO> getMemberBallotVOs(final HttpServletRequest request, final Locale locale) {
		
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		//Member member = Member.findById(Member.class, new Long(296));
		return MemberBallot.getMemberBallotVOs(session.getId(), deviceType.getId(), true, 1, new Long(3), new Long(1), locale.toString());
	}
	
	/*List<MemberBallotVO> getMemberBallotVOs*/
	
	/**
	 * Test rounding.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Test rounding.
	 */
	@RequestMapping(value="/testrounding", method=RequestMethod.GET)
	public @ResponseBody List<Integer> testRounding(final HttpServletRequest request, final Locale locale){
		
		Session session = Session.findById(Session.class, new Long(51));
		String numberOfRoundsStr = session.getParameter(ApplicationConstants.QUESTIONS_STARRED_TOTALROUNDS_FINALBALLOT);
		List<QuestionSequenceVO> questionSequenceVOs = null;
		try{
			questionSequenceVOs = Ballot.findStarredQuestionSequenceVOs(session, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT), locale.toString());
		}catch (ELSException e) {
			e.printStackTrace();
		}
		int numberOfRounds=Integer.parseInt(numberOfRoundsStr);
		int memberIndex=0;			
		List<Integer> questionsInRounds = new ArrayList<Integer>();
		for(int i=1; i<numberOfRounds;i++) {				
			while(questionsInRounds.size()<i) {
				Long memberId = questionSequenceVOs.get(memberIndex).getMemberId();
				boolean toNextMember = true;
				int currentIndex=0;
				for(QuestionSequenceVO qs: questionSequenceVOs) {
					if(currentIndex<=memberIndex) {
						currentIndex++;
						continue;
					}
					else if(qs.getMemberId() == memberId) {
						questionsInRounds.add(currentIndex-memberIndex);
						toNextMember = false;
						break;
					}
					currentIndex++;
				}
				if(toNextMember) {
					memberIndex++;
					currentIndex=0;
				}
			}
			memberIndex = 0;
			for(int j=0; j<i; j++) {
				memberIndex += questionsInRounds.get(j);
			}					
		}			
		int questionsExcludingLastRound = 0;
		for(int i : questionsInRounds) {				
			questionsExcludingLastRound += i;
		}
		questionsInRounds.add(questionSequenceVOs.size() - questionsExcludingLastRound);
		
		return questionsInRounds;
	}
	
	@RequestMapping(value="/saveemp", method=RequestMethod.GET)
	public @ResponseBody VEmployee saveEmployee(HttpServletRequest request){
		VEmployee emp = new VEmployee();
		emp.setDepartmet((VDepartment)VDepartment.findById(VDepartment.class, Long.valueOf(1)));
		
		List<VProject> projects = VProject.findAll(VProject.class, "id", "asc", "mr_IN");		
		emp.setProjects(projects);
		
		emp.setEmpName("V");
		emp.setLocale("mr_IN");
		emp.setVersion(0L);
		
		emp.persist();
		return emp;
	}
	/**
	 * Generator.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Generator.
	 */
	@RequestMapping(value="/generator", method=RequestMethod.GET)
	public @ResponseBody String generator(HttpServletRequest request, Locale locale){
		
		String questionSubjectArray[] ={"ताडाळी, चंद्रपूर येथील मे.धारीवाल कंपनीने शेतक-यांची परवानगी न घेता वीज मनोरे उभारल्‍याबाबत",
				
				"एरंडोल येथ्‍ो 132 KVA विद्युत उपकेंद्र उभारणीबाबत",
				
				"कोळगाव (ता.गेवराई, जि.बीड) येथे वीजेच्‍या तारा अंगावर पडून झालेले मृत्‍यू",
				
				"महाराष्‍ट्रात उभारण्‍यात आलेल्‍या पवनऊर्जा कंपन्‍यांची वीज मुक्‍तप्रवेश धोरणांतर्गत उद्योगांना देण्‍यास राज्‍य वीज नियामक आयोगाने परवानगी दिल्‍याबाबत",
				
				"ताडाळी, चंद्रपूर येथील मे.धारीवाल कंपनीने शेतक-यांची परवानगी न घेता वीज मनोरे उभारल्‍याबाबत"
		};
		
		String questionTextArray[] = {"1) चंद्रपूर येथील सेंट्रल एम.आय.डी.सी. मधील ताडाळी स्थित धारीवाल इन्‍फ्रास्‍ट्रक्‍चर या कंपनीने एका वृत्तपत्रात मे, 2012 या महिन्‍यात जाहिरात प्रसिध्‍द करुन शेतक-यांच्‍या शेतातून विद्युत खांब व लाईन जोडण्‍यासंदर्भात आक्षेप नोंदविण्‍याची सूचना दिली, कंपनीने शेतक-यांना कोणत्‍याही प्रकरची सूचना न देता व वीज वाहिन्‍या जोडण्‍यासाठी कोणताही मोबदला न देता शेतक-यांची दिशाभूल केल्‍याची बाब उघडकीस आली आहे, हे खरे आहे काय ?" +
				"2) असल्‍यास, याप्रकरणी शेतक-यांवर होणारा अन्‍याय दूर करण्‍याच्‍या दृष्‍टीने परिसरातील ग्रामस्‍थांच्‍या एका शिष्‍टमंडळाने निवासी उपजिल्‍हा‍धिकारी, चंद्रपूर यांना 26 मे, 2012 रोजी वा त्‍यासुमारास लेखी निवेदन सादर केले आहे, हे ही खरे आहे काय ? " +
				"3) असल्‍यास, सदर प्रकरणाची सखोल चौकशी करुन शेतक-यांना योग्‍य न्‍याय देण्‍याच्‍या दृ केली आहे वा करण्‍यात येत आहे ? 4) अद्याप याबाबत कार्यवाही झाली नसल्‍यास विलंबाची कारणे काय आहेत ?",
				
				"1) महाराष्‍ट्र शासनाचे प्रत्‍येक तालुक्‍याच्‍या ठिकाणी एक 132 KVA विद्युत उपकेंद्र उभारणीचे धोरण आहे, हे खरे आहे काय ? " +
				"2) असल्‍यास, एरंडोल, जि. जळगांव येथे 132 KVA विद्युत उपकेंद्र उभारणीचा प्रस्‍ताव शासनाच्‍या ऊर्जा विभागाकडे मंजुरीसाठी सादर केला आहे, हे ही खरे आहे काय ? " +
				"3) असल्‍यास, सदर प्रस्‍तावास तातडीने मंजुरी देऊन एरंडोल तालुक्‍यातील वीजेच्‍या समस्‍या सोडविणेसाठी शासन काय कार्यवाही करणार ? व कधी ?",
				
				"1) बीड जिल्‍ह्यातील गेवराई तालुक्‍यातील कोळगाव येथे तुकाराम मदने, रुक्‍मीण मदने व अन्‍य एका व्‍यक्‍तीचा वीजेच्‍या तारा अंगावर पडून दिनांक 10 मे, 2012 रोजी मृत्‍यू झाला, हे खरे आहे काय ? " +
				"2) असल्‍यास, यासंदर्भात चौकशी करण्‍यात आली आहे काय ?  3) असल्‍यास, त्‍यात काय आढळून आले ? 4) मृत व्‍यक्‍तींच्‍या कुटुंबियांना कोणत्‍या प्रकारची मदत करण्‍यात आली वा येत आहे ?",
				
				"1) महाराष्‍ट्रात 8-10 वर्षांपूर्वी उभारण्‍यात आलेल्‍या पवनऊर्जा कंपन्‍यांची राज्‍यातील वीज ग्राहकांच्‍या हक्‍काची वीज मुक्‍तप्रवेश धोरणांतर्गत उद्योगांना देण्‍यास राज्‍य वीज नियामक आयोगाने परवानगी दिली आहे," +
				" त्‍यामुळे कंपन्‍यांना दरवर्षी 250 कोटी रुपयांचा अनाठायी लाभ होणार असल्‍याचा आक्षेप महावितरणने मे, 2012 या महिन्‍यात घेतला आहे, हे खरे आहे काय ? " +
				"2) असल्‍यास, उद्योगांकडून महावितरणच्‍या सामान्‍य ग्राहकांना मिळणा-या क्रॉस सबसिडी कमी होऊन त्‍यांच्‍यावर आर्थिक बोजा पडणार आहे, हे ही खरे आहे काय ? " +
				"3) असल्‍यास, पवनऊर्जा कंपन्‍यांच्‍या वीजेवर पूर्णपणे सामान्‍य जनतेचा असलेला हक्‍क लक्षात घेता केवळ देखभाल व दुरुस्‍तीचा खर्च यापोटी या वीजेचा दर सुमारे 1 रुपये ठेवावा अशी मागणी महावितरणने केली आहे, हे ही खरे आहे काय ? " +
				"4) असल्‍यास, महावितरणच्‍या सर्वसामान्‍य वीज ग्राहकांना न्‍याय मिळण्‍याच्‍या दृष्‍टीने तसेच पवनऊर्जा कंपन्‍यांना होणारा अनाठायी लाभ रोखण्‍याच्‍या दृष्‍टीने राज्‍य शासनाने याबाबत कोणती कार्यवाही केली आहे वा करण्‍यात येत आहे ? " +
				"5) अद्याप याबाबत कार्यवाही नसल्‍यास विलंबाची कारणे काय आहेत ?",
				
				"1) चंद्रपूर येथील सेंट्रल एम.आय.डी.सी. मधील ताडाळी स्थित धारीवाल इन्‍फ्रास्‍ट्रक्‍चर या कंपनीने एका वृत्तपत्रात मे," +
				"2012 या महिन्‍यात जाहिरात प्रसिध्‍द करुन शेतक-यांच्‍या शेतातून विद्युत खांब व लाईन जोडण्‍यासंदर्भात आक्षेप नोंदविण्‍याची सूचना दिली," +
				"कंपनीने शेतक-यांना कोणत्‍याही प्रकरची सूचना न देता व वीज वाहिन्‍या जोडण्‍यासाठी कोणताही मोबदला न देता शेतक-यांची दिशाभूल केल्‍याची बाब उघडकीस आली आहे, हे खरे आहे काय ? " +
				"2) असल्‍यास, याप्रकरणी शेतक-यांवर होणारा अन्‍याय दूर करण्‍याच्‍या दृष्‍टीने परिसरातील ग्रामस्‍थांच्‍या एका शिष्‍टमंडळाने निवासी उपजिल्‍हा‍धिकारी, चंद्रपूर यांना 26 मे, 2012 रोजी वा त्‍यासुमारास लेखी निवेदन सादर केले आहे, हे ही खरे आहे काय ? " +
				"3) असल्‍यास, सदर प्रकरणाची सखोल चौकशी करुन शेतक-यांना योग्‍य न्‍याय देण्‍याच्‍या दृ" +
				"ष्‍टीने राज्‍य शासनाने कोणी कार्यवाही केली आहे वा करण्‍यात येत आहे ? " +
				"4) अद्याप याबाबत कार्यवाही झाली नसल्‍यास विलंबाची कारणे काय आहेत ?"
				
		};
		
		
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		Session session = Session.findById(Session.class, new Long(50));
		List<Group> groups = Group.findAll(Group.class, "id", "ASC", locale.toString());
		List<Member> members = Member.findAll(Member.class, "id", ApplicationConstants.ASC, locale.toString());
		//Ministry ministry = Ministry.findById(Ministry.class, new Long(2));
		//Long[] ansDates = new Long[]{250L, 251L, 252L};
		
		Status status = Status.findByType("question_submit", "mr_IN");
		
		for(Member m : members){
			for(int i = 1; i <= 5; i++){
				Question qs = new Question();
				qs.setPrimaryMember(m);
				qs.setSubject(questionSubjectArray[((int)(Math.random()*1966587))%questionSubjectArray.length]);
				qs.setQuestionText(questionTextArray[((int)(Math.random()*135878344))%questionTextArray.length]);
				Group group = groups.get(((int)(Math.random()*175466587))%groups.size());
				qs.setGroup(group);
				qs.setInternalStatus(status);
				qs.setStatus(status);
				qs.setRecommendationStatus(status);
				qs.setMinistry(group.getMinistries().get(((int)Math.random() * 45) % 5 ));
				qs.setSession(session);
				qs.setType(deviceType);
				qs.setSubmissionDate(new Date());
				qs.setHouseType(session.getHouse().getType());
				
				qs.persist();
			}
		}
		
		return "hello";
	}
	
	/**
	 * Testindiandate.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 * @author vikasg
	 * @since v1.0.0
	 * 
	 * Testindiandate.
	 */
	@RequestMapping(value = "/testindiandate", method = RequestMethod.GET)
	public @ResponseBody String testindiandate(final HttpServletRequest request, final Locale locale) {
		
		/*IndianCaledarDemo demo = new IndianCaledarDemo();
		demo.demonstrate();*/
		System.out.println(System.nanoTime());
		int data = getIntData(); 
		System.out.println(System.nanoTime());
		
		System.out.println(System.nanoTime());
		Integer dataI = getIntegerData(); 
		System.out.println(System.nanoTime());
		
		return String.valueOf(data+dataI);
	}
	
	/**
	 * Gets the integer data.
	 *
	 * @return the integer data
	 */
	private Integer getIntegerData(){
		Integer a = 0;
		for(int i = 0; i < 10; i++){
			a += i;
		}return a;
	}
	
	/**
	 * Gets the int data.
	 *
	 * @return the int data
	 */
	private int getIntData(){
		int a = 0;
		for(int i = 0; i < 10; i++){
			a += i;
		}return a;
	}
	
	
	/**
	 * Scheduler demo.
	 */
	private void schedulerDemo(){
		ScheduledThreadPoolExecutor stpExecutor = new ScheduledThreadPoolExecutor(10, new TestThreadFactory());
		stpExecutor.execute(stpExecutor.getThreadFactory().newThread(new ThreadDemo()));
	}
	
	/**
	 * A factory for creating TestThread objects.
	 */
	class TestThreadFactory implements ThreadFactory{
		
		/* (non-Javadoc)
		 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
		 */
		@Override
		public Thread newThread(Runnable r){
			return new Thread(new ThreadDemo());
		}
	}
	
	/**
	 * The Class ThreadDemo.
	 *
	 * @author vikasg
	 * @since v1.0.0
	 */
	class ThreadDemo implements Runnable{

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			for(int i = 0; i <= 10; i++){
				System.out.println("hello: " + i);
			}			
		}
		
	}	
	
	/*@RequestMapping(value = "/memberstatistics", method = RequestMethod.GET)
	public String getMemberStatistics(final HttpServletRequest request,
			final Model model, final Locale locale) {
		//String strDeviceType = request.getParameter("questionType");
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		
		Member member = Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser()
						.getLastName(), this.getCurrentUser().getBirthDate(),
				locale.toString());
		
		List<MasterVO> data = new ArrayList<MasterVO>();
		
		try{
			//DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.valueOf(strDeviceType));
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType = SessionType.findById(SessionType.class, Long.valueOf(strSessionType));
			
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.valueOf(strSessionYear));
			{
				List<MasterVO> stats = null ;
				stats = Question.getMemberQuestionStatistics(member, session, locale.toString());
				if(!stats.isEmpty()){
					data.addAll(stats);
				}
				
				stats = null ;
				try{
					stats = Resolution.findMemberResolutionStatistics(member, session, locale.toString());
				}catch (Exception e) {
					e.printStackTrace();
				}
				if(!stats.isEmpty()){
					data.addAll(stats);
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("deviceCount", data);
		return "test/memberstatistics";
	}*/
}

