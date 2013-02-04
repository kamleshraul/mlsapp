/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.BallotController.java
 * Created On: Jan 11, 2013
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BallotMemberVO;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberBallotFinalBallotVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseReportVO;
import org.mkcl.els.common.vo.MemberBallotVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.StarredBallotVO;
import org.mkcl.els.domain.Ballot;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.MemberBallotChoice;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class BallotController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/ballot")
public class BallotController extends BaseController{

	@RequestMapping(value="/init",method=RequestMethod.GET)
	public String getBallotPage(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		try {
			String strDeviceType = request.getParameter("questionType");
			DeviceType deviceType = 
				DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));

			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				String strGroup = request.getParameter("group");
				Group group = Group.findById(Group.class, Long.parseLong(strGroup));
				List<MasterVO> masterVOs = new ArrayList<MasterVO>();
				List<QuestionDates> questionDates = group.getQuestionDates();
				for(QuestionDates i : questionDates){
					String strAnsweringDate = 
						FormaterUtil.getDateFormatter(locale.toString()).format(i.getAnsweringDate());
					MasterVO masterVO = new MasterVO(i.getId(), strAnsweringDate);
					masterVOs.add(masterVO);
				}
				model.addAttribute("deviceTypeType", deviceType.getType());
				model.addAttribute("answeringDates", masterVOs);
			}
			else if(deviceType.getType().equals(
					ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				String strLocale = locale.toString();
				String strHouseType = request.getParameter("houseType");
				String strYear = request.getParameter("sessionYear");
				String strSessionTypeId = request.getParameter("sessionType");

				HouseType houseType =
					HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
				SessionType sessionType =
					SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
				Integer year = Integer.valueOf(strYear);

				Session session = 
					Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

				CustomParameter dbDateFormat = 
					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				CustomParameter serverDateFormat = 
					CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
				String strDiscussionDates = 
					session.getParameter("questions_halfhourdiscussion_from_question_discussionDates");
				String[] strDiscussionDatesArr = strDiscussionDates.split("#");
				List<MasterVO> masterVOs = new ArrayList<MasterVO>();
				for(String strDiscussionDate : strDiscussionDatesArr) {
					Date discussionDate = 
						FormaterUtil.formatStringToDate(strDiscussionDate, dbDateFormat.getValue());
					String localizedDate = FormaterUtil.formatDateToString(discussionDate, 
							serverDateFormat.getValue(), locale.toString());
					MasterVO masterVO = new MasterVO();
					masterVO.setName(localizedDate);
					masterVO.setValue(strDiscussionDate);

					masterVOs.add(masterVO);
				}

				model.addAttribute("deviceTypeType", deviceType.getType());
				model.addAttribute("answeringDates", masterVOs);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return "ballot/ballotinit";
	}

	/**
	 * Return "CREATED" if Ballot is created
	 * OR
	 * Return "ALREADY_EXISTS" if Ballot already exists
	 */
	@Transactional
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public @ResponseBody String createBallot(final HttpServletRequest request,
			final Locale locale) {
		String retVal = "ALREADY_EXISTS";

		String strLocale = locale.toString();
		String strHouseType = request.getParameter("houseType");
		String strYear = request.getParameter("sessionYear");
		String strSessionTypeId = request.getParameter("sessionType");

		HouseType houseType =
			HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
		SessionType sessionType =
			SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
		Integer year = Integer.valueOf(strYear);

		Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

		String strDeviceType = request.getParameter("questionType");
		DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));

		String strAnsweringDate = request.getParameter("answeringDate");
		Date answeringDate = null;
		if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
			QuestionDates questionDates = 
				QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
			answeringDate = questionDates.getAnsweringDate();
		}
		else if(deviceType.getType().equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			CustomParameter dbDateFormat = 
				CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
		}

		if(answeringDate != null) {
			Ballot ballot = Ballot.find(session, deviceType, answeringDate, strLocale);
			if(ballot == null) {
				Ballot newBallot = 
					new Ballot(session, deviceType, answeringDate, new Date(), strLocale);
				newBallot.create();
				retVal = "CREATED";
			} 
		}

		return retVal;	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String viewBallot(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String retVal = "question/error";

		String strLocale = locale.toString();
		String strHouseType = request.getParameter("houseType");
		String strYear = request.getParameter("sessionYear");
		String strSessionTypeId = request.getParameter("sessionType");

		HouseType houseType =
			HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
		SessionType sessionType =
			SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
		Integer year = Integer.valueOf(strYear);

		Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

		String strDeviceType = request.getParameter("questionType");
		DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));

		String strAnsweringDate = request.getParameter("answeringDate");
		Date answeringDate = null;
		if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
			QuestionDates questionDates = 
				QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
			answeringDate = questionDates.getAnsweringDate();
		}
		else if(deviceType.getType().equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			CustomParameter dbDateFormat = 
				CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
		}

		if(answeringDate != null) {
			CustomParameter parameter =
				CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			String localizedAnsweringDate =
				FormaterUtil.formatDateToString(answeringDate, parameter.getValue(), strLocale);
			model.addAttribute("answeringDate", localizedAnsweringDate);

			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					List<StarredBallotVO> ballotVOs = 
						Ballot.findStarredBallotVOs(session, answeringDate, strLocale);
					model.addAttribute("ballotVOs", ballotVOs);
					retVal = "ballot/ballot";
				}
				else if(deviceType.getType().equals(
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					List<BallotMemberVO> ballotVOs = 
						Ballot.findBallotedMemberVO(session, deviceType, answeringDate, strLocale);
					model.addAttribute("ballotVOs", ballotVOs);
					retVal = "ballot/halfhour_member_ballot";
				}
			}
			else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				if(deviceType.getType().equals(
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					List<BallotVO> ballotVOs = 
						Ballot.findBallotedVO(session, deviceType, answeringDate, strLocale);
					model.addAttribute("ballotVOs", ballotVOs);
					retVal = "ballot/halfhour_ballot";
				}
			}
		}
		else {
			model.addAttribute("errorcode", "answeringDateNotSelected");
		}

		return retVal;
	}

	/****** Member Ballot For Council ******/

	@RequestMapping(value="/memberballot/init",method=RequestMethod.GET)
	public String viewMemberBallotInit(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){		
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strQuestionType=request.getParameter("questionType");
		String strRound=request.getParameter("round");
		if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null&&strRound!=null){
			if((!strHouseType.isEmpty())
					&&(!strSessionType.isEmpty())
					&&(!strSessionYear.isEmpty())
					&&(!strQuestionType.isEmpty())
					&&(!strRound.isEmpty())){
				HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
				SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
				Integer sessionYear=Integer.parseInt(strSessionYear);
				Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				String noOfRounds=session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_UH);
				if(noOfRounds!=null){
					if(!noOfRounds.isEmpty()){
						DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
						Integer round=Integer.parseInt(strRound);
						/**** Populating No. Of Rounds ****/
						List<Reference> rounds=new ArrayList<Reference>();
						for(int i=1;i<=Integer.parseInt(noOfRounds);i++){
							Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
							rounds.add(reference);
						}
						/**** Populating Groups ****/
						List<Group> groups=Group.findByHouseTypeSessionTypeYear(houseType,sessionType,sessionYear);
						List<MasterVO> masterVOs=new ArrayList<MasterVO>();
						for(Group i:groups){
							MasterVO masterVO=new MasterVO(i.getId(),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i.getNumber()));
							masterVOs.add(masterVO);
						}
						/**** Populating Model ****/
						model.addAttribute("groups",masterVOs);
						model.addAttribute("rounds",rounds);
						model.addAttribute("selectedRound",round);
						model.addAttribute("session",session.getId());
						model.addAttribute("questionType", questionType.getId());
						model.addAttribute("noOfRounds",noOfRounds);
					}else{
						logger.error("**** Total No. Of Rounds In Member Ballot Not Set In Session ****");
						model.addAttribute("type", "NOOFROUNDS_MEMBERBALLOT_NOTSET");
						return "ballot/error";
					}
				}else{
					logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType' for empty values ****");
					model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
					return "ballot/error";
				}				
			}else{
				logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType' for empty values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
				return "ballot/error";
			}
		}else{
			logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType' for null values ****");
			model.addAttribute("type", "REQUEST_PARAMETER_NULL");
			return "ballot/error";
		}		
		return "ballot/memberballotinit";
	}

	@RequestMapping(value="/memberballot/memberwise",method=RequestMethod.GET)
	public String viewMemberWiseReport(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		try{
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			if(strSession!=null&&strQuestionType!=null){
				if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())){
					Session session=Session.findById(Session.class,Long.parseLong(strSession));
					DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
					List<Member> eligibleMembers=MemberBallotAttendance.findEligibleMembers(session, questionType, locale.toString());
					model.addAttribute("eligibleMembers", eligibleMembers);
					model.addAttribute("session",session.getId());
					model.addAttribute("questionType",questionType.getId());
				}else{
					logger.error("**** Check request parameter 'session,questionType' for empty values ****");
					model.addAttribute("type", "MEMBERBALLOTCHOICE_REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'session,questionType' for null values ****");
				model.addAttribute("type", "MEMBERBALLOTCHOICE_REQUEST_PARAMETER_NULL");
				return errorpage;
			}						
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "ballot/memberballotmemberwise";
	}

	@RequestMapping(value="/memberballot/member/questions",method=RequestMethod.GET)
	public String viewMemberQuestionsReport(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		try{
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			String strMember=request.getParameter("member");
			if(strSession!=null&&strQuestionType!=null){
				if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())){
					Session session=Session.findById(Session.class,Long.parseLong(strSession));
					DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
					Member member=Member.findById(Member.class,Long.parseLong(strMember));
					MemberBallotMemberWiseReportVO reports=Question.findMemberWiseReportVO(session, questionType, member, locale.toString());
					model.addAttribute("report",reports);
					model.addAttribute("session",session.getId());
					model.addAttribute("questionType",questionType.getId());
				}else{
					logger.error("**** Check request parameter 'session,questionType' for empty values ****");
					model.addAttribute("type", "MEMBERBALLOTCHOICE_REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'session,questionType' for null values ****");
				model.addAttribute("type", "MEMBERBALLOTCHOICE_REQUEST_PARAMETER_NULL");
				return errorpage;
			}						
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "ballot/memberballotmemberwisequestions";
	}


	@RequestMapping(value="/memberballot/attendance",method=RequestMethod.GET)
	public String markAttendance(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		String strSession=request.getParameter("session");
		String strQuestionType=request.getParameter("questionType");
		String strRound=request.getParameter("round");
		String strAttendance=request.getParameter("attendance");
		if(strSession!=null&&strQuestionType!=null&&strRound!=null&&strAttendance!=null){
			if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())
					&&(!strRound.isEmpty())&&(!strAttendance.isEmpty())){
				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
				Integer round=Integer.parseInt(strRound);
				Boolean attendance=Boolean.parseBoolean(strAttendance);
				/**** Attendance for round n is allowed to be created only if attendance
				 * for previous round has been locked ****/
				Boolean attendanceCanBeCreated=true;
				if(round!=1){
					attendanceCanBeCreated=MemberBallotAttendance.areMembersLocked(session, questionType, round-1, attendance, locale.toString());					
				}
				if(!attendanceCanBeCreated){
					model.addAttribute("type", "MEMBER_BALLOT_NOT_CREATED_FOR_PREVIOUS_ROUND");
					return errorpage;
				}				
				/**** Checking if attendance already created ****/
				Boolean attendanceCreatedAlready=MemberBallotAttendance.memberBallotCreated(session,questionType,round,locale.toString());
				if(!attendanceCreatedAlready){
					String flag=MemberBallotAttendance.createMemberBallotAttendance(session,questionType,round,locale.toString());
					if(!flag.contains("SUCCESS")){
						model.addAttribute("type", flag);
						return errorpage;
					}
				}
				/**** populating model ****/
				List<MemberBallotAttendance> allItems=null;
				List<MemberBallotAttendance> selectedItems=null;
				if(attendance){
					allItems=MemberBallotAttendance.findAll(session,questionType,"false",round,"member",locale.toString());
					selectedItems=MemberBallotAttendance.findAll(session,questionType,"true",round,"position",locale.toString());
				}else{
					allItems=MemberBallotAttendance.findAll(session,questionType,"true",round,"member",locale.toString());
					selectedItems=MemberBallotAttendance.findAll(session,questionType,"false",round,"position",locale.toString());
				}
				List<MemberBallotAttendance> eligibles=MemberBallotAttendance.findAll(session,questionType,"",round,"member",locale.toString());
				model.addAttribute("allItems",allItems);
				model.addAttribute("allItemsCount",allItems.size());
				model.addAttribute("selectedItems",selectedItems);
				model.addAttribute("selectedItemsCount",selectedItems.size());
				model.addAttribute("eligibles",eligibles);
				Boolean locked=MemberBallotAttendance.areMembersLocked(session, questionType, round, attendance, locale.toString());
				model.addAttribute("locked",locked);
			}else{
				logger.error("**** Check request parameter 'session,questionType,round and attendance' for empty values ****");
				model.addAttribute("type", "ATTENDANCE_REQUEST_PARAMETER_EMPTY");
				return errorpage;
			}
		}else{
			logger.error("**** Check request parameter 'session,questionType,round and attendance' for null values ****");
			model.addAttribute("type", "ATTENDANCE_REQUEST_PARAMETER_NULL");
			return errorpage;
		}
		return "ballot/memberballotattendance";	
	}

	@RequestMapping(value="/memberballot/attendance",method=RequestMethod.PUT)
	public @ResponseBody String updateAttendance(final HttpServletRequest request,final ModelMap model,final Locale locale){
		try {
			String strLocked=request.getParameter("locked");
			if(strLocked!=null){
				if(!strLocked.isEmpty()){
					Boolean locked=Boolean.parseBoolean(strLocked);
					if(locked){
						model.addAttribute("type","ATTENDANCE_LOCKED");					
						return "locked";
					}
				}
			}		
			String selectedItems=request.getParameter("items");
			String[] items=selectedItems.split(",");
			String strAttendance=request.getParameter("attendance");
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			String strRound=request.getParameter("round");
			Session session=Session.findById(Session.class,Long.parseLong(strSession));
			DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
			Boolean attendance=Boolean.parseBoolean(strAttendance);
			Integer round=Integer.parseInt(strRound);			
			int position=0;
			/**** setting position,attendance and round of selected member ballot attendance entries****/
			for(String i:items){
				MemberBallotAttendance memberBallotAttendance=MemberBallotAttendance.findById(MemberBallotAttendance.class,Long.parseLong(i));
				position++;
				memberBallotAttendance.setPosition(position);
				memberBallotAttendance.setAttendance(attendance);
				memberBallotAttendance.setRound(round);
				memberBallotAttendance.merge();
			}
			/**** setting attendance of not selected items ****/
			List<MemberBallotAttendance> memberBallotAttendances=MemberBallotAttendance.findAll(session, questionType, "",round, "position", locale.toString());
			for(MemberBallotAttendance i:memberBallotAttendances){
				if(!selectedItems.contains(String.valueOf(i.getId()))){
					i.setAttendance(!attendance);
					i.merge();
				}
			}
		} catch (NumberFormatException e) {
			logger.error("Failed",e);
			return "failed";
		}
		return "success";
	}

	@RequestMapping(value="/memberballot/preballot",method=RequestMethod.GET)
	public String councilStarredPreBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strAttendance=request.getParameter("attendance");
		String strQuestionType=request.getParameter("questionType");
		String strSession=request.getParameter("session");
		String strRound=request.getParameter("round");
		Session session=Session.findById(Session.class,Long.parseLong(strSession));
		DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
		Boolean attendance=Boolean.parseBoolean(strAttendance);
		Integer round=Integer.parseInt(strRound);
		List<MemberBallotAttendance> memberBallotAttendances=null;
		if(attendance){
			memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"true",round,"position",locale.toString());
		}else{
			memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"false",round,"position",locale.toString());
		}
		model.addAttribute("selectedItems",memberBallotAttendances);
		model.addAttribute("attendance",attendance);			
		return "ballot/memberballotpreballot";
	}

	@RequestMapping(value="/preballot",method=RequestMethod.GET)
	public String preBallot(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String retVal = "question/error";
		try {
			String strHouseType = request.getParameter("houseType");
			HouseType houseType =
				HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
				
			String strSessionTypeId = request.getParameter("sessionType");
			SessionType sessionType =
				SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
			
			String strYear = request.getParameter("sessionYear");
			Integer year = Integer.valueOf(strYear);
			
			String strQuestionType = request.getParameter("questionType");
			DeviceType deviceType = 
				DeviceType.findById(DeviceType.class, Long.parseLong(strQuestionType));

			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			
			String strAnsweringDate = request.getParameter("answeringDate");
			Date answeringDate = null;
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				QuestionDates questionDates = 
					QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
				answeringDate = questionDates.getAnsweringDate();
			}
			else if(deviceType.getType().equals(
					ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				CustomParameter dbDateFormat = 
					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}
				
			if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				if(deviceType.getType().equals(
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					retVal = this.halfHourPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}
			}
			else if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				if(deviceType.getType().equals(
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					retVal = this.halfHourPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}
				else if(deviceType.getType().equals(
						ApplicationConstants.STARRED_QUESTION)) {
					retVal = this.starredPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}
			}
		}
		catch(Exception e) {
			model.addAttribute("errorcode", "insufficientParametersForBallotCreation");
			e.printStackTrace();
		}
		return retVal;
	}

	private String halfHourPreBallot(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> ballotVOs = 
			Ballot.findPreBallotVO(session, deviceType, answeringDate, locale);
		model.addAttribute("ballotVOs", ballotVOs);
		return "ballot/halfhour_ballot";
	}

	private String starredPreBallot(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		List<StarredBallotVO> ballotVOs = 
			Ballot.findStarredPreBallotVOs(session, answeringDate, locale);
		model.addAttribute("ballotVOs", ballotVOs);
		return "ballot/starred_preballot";
	}

	@Transactional
	@RequestMapping(value="/memberballot/view",method=RequestMethod.GET)
	public String createMemberBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		try{
			String strAttendance=request.getParameter("attendance");
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			String strRound=request.getParameter("round");
			String strGroup=request.getParameter("group");
			String strAnsweringDate=request.getParameter("answeringDate");
			if(strSession!=null&&strQuestionType!=null
					&&strAttendance!=null&&strRound!=null){
				if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())
						&&(!strAttendance.isEmpty())&&(!strRound.isEmpty())){
					Session session=Session.findById(Session.class,Long.parseLong(strSession));
					DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
					Boolean attendance=Boolean.parseBoolean(strAttendance);
					Integer round=Integer.parseInt(strRound);
					String flag=MemberBallot.createMemberBallot(session,questionType,attendance,round,locale.toString());
					if(flag.contains("SUCCESS")){
						List<MemberBallotVO> memberBallots=null;
						if(strGroup==null&&strAnsweringDate==null){
							memberBallots=MemberBallot.viewMemberBallotVO(session,questionType,attendance,round,locale.toString());
						}else if(strGroup.equals("-")&&strAnsweringDate.equals("-")){
							memberBallots=MemberBallot.viewMemberBallotVO(session,questionType,attendance,round,locale.toString());
						}else if((!strGroup.equals("-"))&&strAnsweringDate.equals("-")){
							Group group=Group.findById(Group.class,Long.parseLong(strGroup));
							memberBallots=MemberBallot.viewMemberBallotVO(session,questionType,attendance,round,group,locale.toString());
						}else if((!strGroup.equals("-"))&&(!strAnsweringDate.equals("-"))){
							QuestionDates answeringDate=Question.findById(QuestionDates.class,Long.parseLong(strAnsweringDate));
							Group group=Group.findById(Group.class,Long.parseLong(strGroup));
							memberBallots=MemberBallot.viewMemberBallotVO(session,questionType,attendance,round,group,answeringDate,locale.toString());
						}
						model.addAttribute("memberBallots",memberBallots);
					}else{
						model.addAttribute("type",flag);
						return errorpage;
					}
				}else{
					logger.error("**** Check request parameter 'session,questionType,round,attendance,noofrounds' for empty values ****");
					model.addAttribute("type", "MEMBERBALLOT_REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}

			}else{
				logger.error("**** Check request parameter 'session,questionType,round,attendance,noofrounds' for null values ****");
				model.addAttribute("type", "MEMBERBALLOT_REQUEST_PARAMETER_NULL");
				return errorpage;
			}						
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "ballot/memberballot";		
	}

	@RequestMapping(value="/memberballot/choices",method=RequestMethod.GET)
	public String viewMemberBallotChoice(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		try{
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			if(strSession!=null&&strQuestionType!=null){
				if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())){
					Session session=Session.findById(Session.class,Long.parseLong(strSession));
					DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
					List<Member> eligibleMembers=MemberBallotAttendance.findEligibleMembers(session, questionType, locale.toString());
					model.addAttribute("eligibleMembers", eligibleMembers);
					model.addAttribute("session",session.getId());
					model.addAttribute("questionType",questionType.getId());
				}else{
					logger.error("**** Check request parameter 'session,questionType' for empty values ****");
					model.addAttribute("type", "MEMBERBALLOTCHOICE_REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'session,questionType' for null values ****");
				model.addAttribute("type", "MEMBERBALLOTCHOICE_REQUEST_PARAMETER_NULL");
				return errorpage;
			}						
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "ballot/memberballotchoice";
	}

	@RequestMapping(value="/memberballot/listchoices",method=RequestMethod.GET)
	public String listMemberBallotChoice(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strQuestionType=request.getParameter("questionType");
		String strSession=request.getParameter("session");
		String strMember=request.getParameter("member");
		if(strQuestionType!=null&&strSession!=null&&strMember!=null){
			Session session=Session.findById(Session.class,Long.parseLong(strSession));
			DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
			Member member=Member.findById(Member.class,Long.parseLong(strMember));
			List<Question> questions=Question.findAdmittedStarredQuestionsUH(session,questionType,member,locale.toString());
			model.addAttribute("admittedQuestions",questions);
			model.addAttribute("noOfAdmittedQuestions",questions.size());
			List<MemberBallot> memberBallots=MemberBallot.findByMember(session, questionType, member, locale.toString());
			model.addAttribute("memberBallots",memberBallots);
			if(memberBallots!=null){
				if(!memberBallots.isEmpty()){
					Boolean autoFilled=memberBallots.get(0).getChoicesAutoFilled();
					model.addAttribute("autofill",autoFilled);
				}
			}
			int rounds=memberBallots.size();
			model.addAttribute("totalRounds", rounds);
			List<MemberBallotChoice> memberBallotChoices=MemberBallotChoice.findByMember(session,questionType, member, locale.toString());
			if(memberBallotChoices.isEmpty()){
				model.addAttribute("flag","new");
				request.setAttribute("totalRounds", rounds);
				for(int i=1;i<=rounds;i++){
					CustomParameter questionsInEachRound=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_ROUND"+i, "");
					if(questionsInEachRound!=null){
						request.setAttribute("round"+i, Integer.parseInt(questionsInEachRound.getValue()));
					}else{
						model.addAttribute("type","NO_OF_QUESTIONS_IN_EACH_ROUND_NOT_SET");
						return "ballot/error";
					}
				}
			}else{
				model.addAttribute("flag","edit");
			}

		}else{
			model.addAttribute("type","MEMBER_BALLOT_CHOICE_GET_REQUEST_PARAMETER_NULL");
			return "ballot/error";
		}
		return "ballot/listmemberballotchoice";
	}

	@Transactional
	@RequestMapping(value="/memberballot/choices",method=RequestMethod.POST)
	public  String updateMemberBallotChoice(final HttpServletRequest request,
			final HttpServletResponse response,
			final ModelMap model,final Locale locale){
		String strAutoFill=request.getParameter("autofill");	
		String strNoOfAdmittedQuestions=request.getParameter("noOfAdmittedQuestions");
		String strQuestionType=request.getParameter("questionType");
		String strSession=request.getParameter("session");
		String strMember=request.getParameter("member");
		String strTotalRounds=request.getParameter("totalRounds");
		Boolean autoFill=null;
		if(strNoOfAdmittedQuestions!=null
				&&strQuestionType!=null&&strSession!=null&&strMember!=null
				&&strTotalRounds!=null){
			if(strAutoFill!=null){
				autoFill=Boolean.parseBoolean(strAutoFill);
				if(!autoFill){
					Boolean allChoicesFilled=checkAllChoicesFilled(request,strNoOfAdmittedQuestions);
					if(allChoicesFilled==false){
						model.addAttribute("type","NO_OF_CHOICES_IS_LESS_THAN_NO_OF_ADMITTED_QUESTIONS");
						return "ballot/error";
					}
				}
			}
		}else{
			model.addAttribute("type","MEMBER_BALLOT_CHOICE_POST_REQUEST_PARAMETER_NULL");
			return "ballot/error";
		}		
		Session session=Session.findById(Session.class,Long.parseLong(strSession));
		DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
		Member member=Member.findById(Member.class,Long.parseLong(strMember));
		int totalRounds=Integer.parseInt(strTotalRounds);
		int noOfAdmittedQuestions=Integer.parseInt(strNoOfAdmittedQuestions);
		/**** No of question choices allowed in each round ****/
		Map<String,Integer> noofQuestionsInEachRound=new HashMap<String, Integer>();
		for(int i=1;i<=Integer.parseInt(strTotalRounds);i++){
			CustomParameter questionsInEachRound=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_ROUND"+i, "");
			noofQuestionsInEachRound.put("round"+i,Integer.parseInt(questionsInEachRound.getValue()));
		}			
		List<Question> questions=Question.findAdmittedStarredQuestionsUH(session,questionType,member,locale.toString());
		if(autoFill!=null){
			/**** Auto Fill Choices****/
			if(autoFill){
				String flag=request.getParameter("flag");
				autoFillChoices(questions,noofQuestionsInEachRound,totalRounds,noOfAdmittedQuestions,session,questionType,member,locale.toString(),flag);
				model.addAttribute("autofill",true);
			}
		}else{
			/**** Member Has Specified Choices ****/
			fillChoices(noofQuestionsInEachRound,totalRounds,noOfAdmittedQuestions,session,questionType,member,locale.toString(),request);
		}
		/**** Question Choices ****/
		model.addAttribute("admittedQuestions",questions);
		model.addAttribute("noOfAdmittedQuestions",questions.size());
		List<MemberBallot> memberBallots=MemberBallot.findByMember(session, questionType, member, locale.toString());
		model.addAttribute("memberBallots",memberBallots);
		int rounds=memberBallots.size();
		model.addAttribute("totalRounds", rounds);
		List<MemberBallotChoice> memberBallotChoices=MemberBallotChoice.findByMember(session,questionType, member, locale.toString());
		if(memberBallotChoices.isEmpty()){
			model.addAttribute("flag","new");
			request.setAttribute("totalRounds", rounds);
			for(int i=1;i<=rounds;i++){
				CustomParameter questionsInEachRound=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_ROUND"+i, "");
				if(questionsInEachRound!=null){
					request.setAttribute("round"+i, Integer.parseInt(questionsInEachRound.getValue()));
				}else{
					model.addAttribute("type","NO_OF_QUESTIONS_IN_EACH_ROUND_NOT_SET");
					return "ballot/error";
				}
			}
		}else{
			model.addAttribute("flag","edit");
		}
		model.addAttribute("type","SUCCESS");
		return "ballot/listmemberballotchoice";
	}

	private Boolean checkAllChoicesFilled(HttpServletRequest request, String strNoOfAdmittedQuestions) {
		int noOfChoicesFilled=1;
		int nofAdmittedQuestions=Integer.parseInt(strNoOfAdmittedQuestions);
		for(int i=0;i<nofAdmittedQuestions;i++){
			String question=request.getParameter("question"+i);
			if(question!=null){
				if((!question.isEmpty())&&(!question.equals("-"))){
					noOfChoicesFilled++;
				}
			}
		}
		if(nofAdmittedQuestions==noOfChoicesFilled){
			return true;
		}else{
			return false;
		}
	}

	private void fillChoices(Map<String, Integer> noofQuestionsInEachRound,
			int totalRounds, int noOfAdmittedQuestions, Session session,
			DeviceType questionType, Member member, String locale,
			HttpServletRequest request) {
		int count=1;
		for(int i=1;i<=totalRounds;i++){
			if(count>noOfAdmittedQuestions){
				break;
			}
			MemberBallot memberBallot=MemberBallot.findByMemberRound(session, questionType, member,i, locale.toString());
			memberBallot.setChoicesAutoFilled(false);
			List<MemberBallotChoice> memberBallotChoices=new ArrayList<MemberBallotChoice>();
			Integer questionsInEachRound=noofQuestionsInEachRound.get("round"+i);
			for(int j=1;j<=questionsInEachRound;j++){
				if(count>noOfAdmittedQuestions){					
					break;
				}
				String strChoice=request.getParameter("choice"+count);
				String strQuestion=request.getParameter("question"+count);
				String strAnsweringDate=request.getParameter("answeringDate"+count);
				if(strChoice!=null&&strQuestion!=null&&strAnsweringDate!=null){
					MemberBallotChoice memberBallotChoice=null;
					String strMemberChoice=request.getParameter("memberBallotchoiceId"+count);
					if(strMemberChoice!=null){
						memberBallotChoice=MemberBallotChoice.findById(MemberBallotChoice.class,Long.parseLong(strMemberChoice));
					}else{
						memberBallotChoice=new MemberBallotChoice();
					}
					if(!strChoice.isEmpty()){
						memberBallotChoice.setChoice(Integer.parseInt(strChoice));
					}
					if(!strQuestion.equals("-")){
						Question question=Question.findById(Question.class,Long.parseLong(strQuestion));
						if(!strAnsweringDate.equals("-")){
							QuestionDates questionDates=QuestionDates.findById(QuestionDates.class,Long.parseLong(strAnsweringDate));
							memberBallotChoice.setNewAnsweringDate(questionDates);
							question.setAnsweringDate(questionDates);
							question.simpleMerge();
						}else{
							memberBallotChoice.setNewAnsweringDate(question.getChartAnsweringDate());
						}
						memberBallotChoice.setQuestion(question);
					}
					memberBallotChoice.setLocale(locale);
					memberBallotChoice.setClubbingUpdated(false);
					memberBallotChoice.setProcessed(false);
					if(memberBallotChoice.getId()==null){
						memberBallotChoice.persist();
					}else{
						memberBallotChoice.merge();
					}
					memberBallotChoices.add(memberBallotChoice);
				}
				count++;
			}
			memberBallot.setQuestionChoices(memberBallotChoices);
			memberBallot.merge();
		}

	}

	private void autoFillChoices(List<Question> questions, Map<String, Integer> noofQuestionsInEachRound, int totalRounds, int noOfAdmittedQuestions, Session session, DeviceType questionType, Member member, String locale, String flag){
			int count=0;			
			for(int i=1;i<=totalRounds;i++){
				/**** if choice has been created for all the admitted questions ****/
				if(count>=noOfAdmittedQuestions){
					break;
				}
				/**** Getting Member Ballot Entry for a particular round ****/
				MemberBallot memberBallot=MemberBallot.findByMemberRound(session, questionType, member,i, locale.toString());
				/**** In case of edit page if auto fill option is selected then first we will remove
				 * all the existing entries and then again auto fill.
				 */
				if(flag.equals("edit")){
					List<MemberBallotChoice> choices=new ArrayList<MemberBallotChoice>();					
					for(MemberBallotChoice c:memberBallot.getQuestionChoices()){
						choices.add(c);
					}	
					memberBallot.setQuestionChoices(null);
					memberBallot.merge();
					for(MemberBallotChoice c:choices){
						c.remove();
					}					
				}
				/**** In case of new and edit both auto fill will create entries automatically ****/
				memberBallot.setChoicesAutoFilled(true);
				List<MemberBallotChoice> memberBallotChoices=new ArrayList<MemberBallotChoice>();
				Integer questionsInEachRound=noofQuestionsInEachRound.get("round"+i);
				/**** Iterating for the no of questions allowed in each round times ****/
				for(int j=1;j<=questionsInEachRound;j++){
					/**** if choice has been created for all the admitted questions ****/
					if(count>=noOfAdmittedQuestions){
						break;
					}
					/**** Creating choice entry from the admitted question ****/
					MemberBallotChoice memberBallotChoice=new MemberBallotChoice();
					memberBallotChoice.setLocale(locale.toString());
					memberBallotChoice.setClubbingUpdated(false);
					memberBallotChoice.setProcessed(false);
					Question question=questions.get(count);
					memberBallotChoice.setChoice(j);
					memberBallotChoice.setQuestion(question);
					memberBallotChoice.setNewAnsweringDate(question.getChartAnsweringDate());
					memberBallotChoice.persist();
					memberBallotChoices.add(memberBallotChoice);
					count++;
				}
				memberBallot.setQuestionChoices(memberBallotChoices);
				memberBallot.merge();
			}
		}
	

	@Transactional
	@RequestMapping(value="/memberballot/updateclubbing",method=RequestMethod.GET)
	public String updateClubbingMemberBallot(final ModelMap model,
			final HttpServletRequest request,final Locale locale){
		Boolean status=false;
		String strSession=request.getParameter("session");
		String strDeviceType=request.getParameter("questionType");
		if(strSession!=null&&strDeviceType!=null){
			Session session=Session.findById(Session.class,Long.parseLong(strSession));
			DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
			Integer primaryInMBCount=MemberBallot.findPrimaryCount(session,deviceType,locale.toString());
			int start=0;
			int size=50;
			for(int i=start;i<primaryInMBCount;i=i+size){
				status=MemberBallot.updateClubbing(session,deviceType,i,i+size,locale.toString());
			}
			if(status){
				status=MemberBallot.deleteTempEntries();
			}
			if(!status){
				model.addAttribute("type", "MEMBERBALLOTUPDATECLUBBING_FAILED");
				return "ballot/error";
			}
		}else{
			logger.error("**** Check request parameters 'session and deviceType' for null values ****");
			model.addAttribute("type", "MEMBERBALLOTUPDATECLUBBING_REQUEST_PARAMETER_NULL");
			return "ballot/error";
		}
		model.addAttribute("type", "success");
		return "ballot/memberballotupdateclubbing";
	}

	@Transactional
	@RequestMapping(value="/memberballot/final",method=RequestMethod.GET)
	public String createFinalMemberBallot(final HttpServletRequest request,
			final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		String strSession=request.getParameter("session");
		String strDeviceType=request.getParameter("questionType");
		String strGroup=request.getParameter("group");
		String strAnsweringDate=request.getParameter("answeringDate");
		Boolean status=false;
		List<MemberBallotFinalBallotVO> ballots=new ArrayList<MemberBallotFinalBallotVO>();
		if(strSession!=null&&strDeviceType!=null&&strGroup!=null&&strAnsweringDate!=null){
			if((!strSession.isEmpty())&&(!strDeviceType.isEmpty())&&(!strGroup.isEmpty())&&(!strAnsweringDate.isEmpty())){
				Session session=Session.findById(Session.class, Long.parseLong(strSession));
				String firstBatchSubmissionDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME_UH);
				if(firstBatchSubmissionDate!=null){
					if(!firstBatchSubmissionDate.isEmpty()){
						QuestionDates questionDates=QuestionDates.findById(QuestionDates.class,Long.parseLong(strAnsweringDate));
						String ansDate=null;
						CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_DATEFORMAT", "");
						if(customParameter!=null){
							ansDate=FormaterUtil.getDateFormatter(customParameter.getValue(), "en_US").format(questionDates.getAnsweringDate());;
						}else{
							model.addAttribute("type","DB_DATEFORMAT_NOTSET");
							return errorpage;	
						}
						DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
						Group group=Group.findById(Group.class,Long.parseLong(strGroup));
						String totalRounds=session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_BALLOT_UH);
						if(totalRounds==null){
							model.addAttribute("type","TOTAL_ROUNDS_IN_BALLOT_UH_NOTSET");
							return errorpage;	
						}else if(totalRounds.isEmpty()){
							model.addAttribute("type","TOTAL_ROUNDS_IN_BALLOT_UH_NOTSET");
							return errorpage;	
						}
						status=MemberBallot.createFinalBallot(session, deviceType,group,ansDate, locale.toString(),firstBatchSubmissionDate,Integer.parseInt(totalRounds));
						if(status){
							ballots=MemberBallot.viewFinalBallot(session, deviceType,ansDate, locale.toString());
							model.addAttribute("ballots",ballots);
							model.addAttribute("answeringDate",FormaterUtil.getDateFormatter(locale.toString()).format(questionDates.getAnsweringDate()));
						}else{
							model.addAttribute("type","FINALBALLOT_FAILED");
							return errorpage;	
						}
					}else{
						model.addAttribute("type","FIRSTBATCH_SUBMISSIONDATE_NOTSET");
						return errorpage;
					}
				}else{
					model.addAttribute("type","FIRSTBATCH_SUBMISSIONDATE_NOTSET");
					return errorpage;
				}				
			}else{
				logger.error("**** Check request parameter 'session,deviceType,group,answering date' for null values ****");
				model.addAttribute("type", "MEMBERBALLOTFINAL_REQUEST_PARAMETER_EMPTY");
				return errorpage;
			}	
		}else{
			logger.error("**** Check request parameter 'session,deviceType,group,answering date' for null values ****");
			model.addAttribute("type", "MEMBERBALLOTFINAL_REQUEST_PARAMETER_NULL");
			return errorpage;
		}
		return "ballot/memberballotfinal";		
	}
}
