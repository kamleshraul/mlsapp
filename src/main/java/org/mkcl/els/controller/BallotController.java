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
import org.mkcl.els.common.vo.MemberBallotQuestionDistributionVO;
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

	@RequestMapping(value="/init", method=RequestMethod.GET)
	public String getBallotPage(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String retVal = "ballot/error";
		try {
			/** Create DeviceType */
			String strQuestionType = request.getParameter("questionType");
			DeviceType deviceType = 
				DeviceType.findById(DeviceType.class, Long.parseLong(strQuestionType));
			model.addAttribute("deviceTypeType", deviceType.getType());

			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				/** Create Group*/
				String strGroup = request.getParameter("group");
				Group group = Group.findById(Group.class, Long.parseLong(strGroup));
				
				/** Compute & Add answeringDates to model*/
				List<MasterVO> masterVOs = new ArrayList<MasterVO>();
				List<QuestionDates> questionDates = group.getQuestionDates();
				for(QuestionDates i : questionDates){
					String strAnsweringDate = 
						FormaterUtil.getDateFormatter(locale.toString()).format(i.getAnsweringDate());
					MasterVO masterVO = new MasterVO(i.getId(), strAnsweringDate);
					masterVOs.add(masterVO);
				}
				model.addAttribute("answeringDates", masterVOs);
			}
			else if(deviceType.getType().equals(
					ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
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
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

				/** Compute & Add discussionDates to model*/
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
				model.addAttribute("answeringDates", masterVOs);
			}
			
			retVal = "ballot/ballotinit";
		}
		catch(Exception e) {
			logger.error("error", e);
			retVal = "ballot/error";
		}

		return retVal;
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
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			
			/** Create DeviceType */
			String strQuestionType = request.getParameter("questionType");
			DeviceType deviceType = 
				DeviceType.findById(DeviceType.class, Long.parseLong(strQuestionType));
			
			/** Create answeringDate */
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
			
			/** Create Ballot */
			Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale.toString());
			if(ballot == null) {
				Ballot newBallot = 
					new Ballot(session, deviceType, answeringDate, new Date(), locale.toString());
				newBallot.create();
				retVal = "CREATED";
			}
			else {
				retVal = "ALREADY_EXISTS";
			}
		}
		catch(Exception e) {
			logger.error("error", e);
			retVal = "ERROR";
		}
		return retVal;
	}
	
	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String viewBallot(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String retVal = "ballot/error";
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
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			
			/** Create DeviceType */
			String strQuestionType = request.getParameter("questionType");
			DeviceType deviceType = 
				DeviceType.findById(DeviceType.class, Long.parseLong(strQuestionType));
			
			/** Create answeringDate */
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
			
			/** Add localized answeringDate to model */
			CustomParameter parameter =
				CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			String localizedAnsweringDate =
				FormaterUtil.formatDateToString(answeringDate, parameter.getValue(), locale.toString());
			model.addAttribute("answeringDate", localizedAnsweringDate);
			
			/** DeviceType & HouseType specific Ballot views */
			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					List<StarredBallotVO> ballotVOs = 
						Ballot.findStarredBallotVOs(session, answeringDate, locale.toString());
					model.addAttribute("ballotVOs", ballotVOs);
					retVal = "ballot/ballot";
				}
				else if(deviceType.getType().equals(
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					List<BallotMemberVO> ballotVOs = 
						Ballot.findBallotedMemberVO(session, deviceType, answeringDate, locale.toString());
					model.addAttribute("ballotVOs", ballotVOs);
					retVal = "ballot/halfhour_member_ballot";
				}
			}
			else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				if(deviceType.getType().equals(
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					List<BallotVO> ballotVOs = 
						Ballot.findBallotedVO(session, deviceType, answeringDate, locale.toString());
					model.addAttribute("ballotVOs", ballotVOs);
					retVal = "ballot/halfhour_ballot";
				}
			}
		}
		catch(Exception e) {
			logger.error("error", e);
			model.addAttribute("type", "INSUFFICIENT_PARAMETERS_FOR_VIEWING_BALLOT");
			retVal = "ballot/error";
		}
		return retVal;
	}

	/****** Member Ballot For Council ******/
	@RequestMapping(value="/memberballot/init",method=RequestMethod.GET)
	public String viewMemberBallotInit(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){		
		String errorpage="ballot/error";
		try{
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
							model.addAttribute("type", "NOOFROUNDS_IN_MEMBERBALLOT_NOTSET");
							return errorpage;
						}
					}else{
						logger.error("**** Total No. Of Rounds In Member Ballot Not Set In Session ****");
						model.addAttribute("type", "NOOFROUNDS_IN_MEMBERBALLOT_NOTSET");
						return errorpage;
					}				
				}else{
					logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType,round' for empty values ****");
					model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType,round' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
				return errorpage;
			}	
		}catch(Exception e){
			logger.error("failed",e);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
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
					/**** List of Distinct Members who had submitted questions in First batch ****/
					List<Member> eligibleMembers=MemberBallotAttendance.findEligibleMembers(session, questionType, locale.toString());
					model.addAttribute("eligibleMembers", eligibleMembers);
					model.addAttribute("session",session.getId());
					model.addAttribute("questionType",questionType.getId());
				}else{
					logger.error("**** Check request parameter 'session,questionType' for empty values ****");
					model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'session,questionType' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
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
					/**** First Batch Questions Final Status Distribution member wise ****/
					MemberBallotMemberWiseReportVO reports=MemberBallot.findMemberWiseReportVO(session, questionType, member, locale.toString());
					/**** Populating Group and corresponding Ministries in Model ****/
					List<Group> groups=Group.findByHouseTypeSessionTypeYear(session.getHouse().getType(),session.getType(),session.getYear());
					model.addAttribute("groups",groups);
					model.addAttribute("report",reports);
					model.addAttribute("session",session.getId());
					model.addAttribute("questionType",questionType.getId());
					model.addAttribute("locale",locale.toString());
				}else{
					logger.error("**** Check request parameter 'session,questionType,member' for empty values ****");
					model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'session,questionType,member' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
				return errorpage;
			}						
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "ballot/memberballotmemberwisequestions";
	}


	@RequestMapping(value="/memberballot/questiondistribution",method=RequestMethod.GET)
	public String viewQuestionDistributionReport(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		try{
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			if(strSession!=null&&strQuestionType!=null){
				if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())){
					Session session=Session.findById(Session.class,Long.parseLong(strSession));
					DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
					List<MemberBallotQuestionDistributionVO> questionDistributions=MemberBallot.viewQuestionDistribution(session,questionType,locale.toString());
					model.addAttribute("questionDistributions",questionDistributions);
					model.addAttribute("session",session.getId());
					model.addAttribute("questionType",questionType.getId());
					model.addAttribute("locale",locale.toString());
				}else{
					logger.error("**** Check request parameter 'session,questionType' for empty values ****");
					model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'session,questionType' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
				return errorpage;
			}						
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "ballot/memberballotquestiondistribution";
	}



	@RequestMapping(value="/memberballot/attendance",method=RequestMethod.GET)
	public String markAttendance(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		try {
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
					model.addAttribute("round",round);
				}else{
					logger.error("**** Check request parameter 'session,questionType,round and attendance' for empty values ****");
					model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'session,questionType,round and attendance' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
				return errorpage;
			}
		} catch (NumberFormatException e) {
			logger.error("failed",e);
			model.addAttribute("type","DB_EXCEPTION");
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
						return "locked";
					}
				}
			}		
			String selectedItems=request.getParameter("items");
			String[] items=selectedItems.split(",");
			if(items.length!=0){
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
				/**** Here we want to also ensure once member ballot has been created
				 * i.e attendance has been locked for a round then that configuration
				 * cannot be allowed to be changed in subsequent rounds.May be only its
				 * position can be allowed to be configured through custom parameter.
				 * ************ NEED TO BE DONE AFTER APPROVAL FROM BA**************************************
				 */
				for(String i:items){
					if(!i.isEmpty()){
						MemberBallotAttendance memberBallotAttendance=MemberBallotAttendance.findById(MemberBallotAttendance.class,Long.parseLong(i));
						position++;
						memberBallotAttendance.setPosition(position);
						memberBallotAttendance.setAttendance(attendance);
						memberBallotAttendance.setRound(round);
						memberBallotAttendance.merge();						
					}
				}
				/**** setting attendance of not selected items ****/
				List<MemberBallotAttendance> memberBallotAttendances=MemberBallotAttendance.findAll(session, questionType, "",round, "position", locale.toString());
				for(MemberBallotAttendance i:memberBallotAttendances){
					if(!selectedItems.contains(String.valueOf(i.getId()))){
						i.setAttendance(!attendance);
						i.merge();						
					}
				}
			}else{
				return "nomembers";
			}
		} catch (NumberFormatException e) {
			logger.error("failed",e);
			return "failed";
		}catch (Exception e) {
			logger.error("failed",e);
			return "failed";
		}
		return "success";
	}

	@RequestMapping(value="/memberballot/preballot",method=RequestMethod.GET)
	public String councilStarredPreBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
		try {
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
			model.addAttribute("round",round);
		} catch (Exception e) {
			logger.error("failed",e);
			model.addAttribute("type","DB_EXCEPTION");
			return "ballot/error";
		}
		return "ballot/memberballotpreballot";
	}

	@RequestMapping(value="/preballot",method=RequestMethod.GET)
	public String preBallot(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String retVal = "ballot/error";
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
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			
			/** Create DeviceType */
			String strQuestionType = request.getParameter("questionType");
			DeviceType deviceType = 
				DeviceType.findById(DeviceType.class, Long.parseLong(strQuestionType));
			
			/** Create answeringDate */
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
				
			/** Route PreBallot creation to appropriate handler method */
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
			logger.error("error", e);
			model.addAttribute("type", "INSUFFICIENT_PARAMETERS_FOR_PRE_BALLOT_CREATION");
			retVal = "ballot/error";
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
					model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}

			}else{
				logger.error("**** Check request parameter 'session,questionType,round,attendance,noofrounds' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
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
					model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'session,questionType' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
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
			List<Question> questions=MemberBallotChoice.findFirstBatchQuestions(session, questionType, member,"ALL","number",ApplicationConstants.ASC, locale.toString());
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

		}else{
			model.addAttribute("type","REQUEST_PARAMETER_NULL");
			return "ballot/error";
		}
		return "ballot/listmemberballotchoice";
	}

	@Transactional
	@RequestMapping(value="/memberballot/choices",method=RequestMethod.POST)
	public  String updateMemberBallotChoice(final HttpServletRequest request,
			final HttpServletResponse response,
			final ModelMap model,final Locale locale){
		String strNoOfAdmittedQuestions=request.getParameter("noOfAdmittedQuestions");
		String strQuestionType=request.getParameter("questionType");
		String strSession=request.getParameter("session");
		String strMember=request.getParameter("member");
		String strTotalRounds=request.getParameter("totalRounds");
		if(strNoOfAdmittedQuestions!=null
				&&strQuestionType!=null&&strSession!=null&&strMember!=null
				&&strTotalRounds!=null){
			Session session=Session.findById(Session.class,Long.parseLong(strSession));
			DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
			Member member=Member.findById(Member.class,Long.parseLong(strMember));
			int totalRounds=Integer.parseInt(strTotalRounds);
			int noOfAdmittedQuestions=Integer.parseInt(strNoOfAdmittedQuestions);
			/**** No of question choices allowed in each round ****/
			Map<String,Integer> noofQuestionsInEachRound=new HashMap<String, Integer>();
			for(int i=1;i<=Integer.parseInt(strTotalRounds);i++){
				CustomParameter questionsInEachRound=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_ROUND"+i, "");
				if(questionsInEachRound!=null){
					noofQuestionsInEachRound.put("round"+i,Integer.parseInt(questionsInEachRound.getValue()));
				}else{
					model.addAttribute("type","NO_OF_QUESTIONS_IN_EACH_ROUND_NOT_SET");
					return "ballot/error";
				}
			}	
			fillChoices(noofQuestionsInEachRound,
					totalRounds,noOfAdmittedQuestions,session,
					questionType,member,locale.toString(),
					request);
			/**** Repopulating edit page to display ****/
			/**** Question Choices ****/
			List<Question> questions=MemberBallotChoice.findFirstBatchQuestions(session, questionType, member,"ALL","number",ApplicationConstants.ASC, locale.toString());
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
		}else{
			model.addAttribute("type","REQUEST_PARAMETER_NULL");
			return "ballot/error";
		}		
		model.addAttribute("type","SUCCESS");
		return "ballot/listmemberballotchoice";
	}

	private void fillChoices(Map<String, Integer> noofQuestionsInEachRound,
			int totalRounds, int noOfAdmittedQuestions, Session session,
			DeviceType questionType, Member member, String locale,
			HttpServletRequest request) {
		/**** Here mixing of manual and auto fill can take place depending on the way member has filled
		 * the form ****/
		String strAutoFillingStartsAt=request.getParameter("autofillingstartsat");
		int autoFillingStartsAt=Integer.parseInt(strAutoFillingStartsAt);
		String flag=request.getParameter("flag");
		Map<Long,MemberBallotChoice> manuallySetChoices=new HashMap<Long, MemberBallotChoice>();
		int count=1;
		int choicesClearedForRounds=0;
		for(int i=1;i<=totalRounds;i++){
			if(autoFillingStartsAt==1){
				break;
			}else if((autoFillingStartsAt>1)&&(count>=autoFillingStartsAt)){
				break;
			}else if(count>noOfAdmittedQuestions){
				break;
			}			
			MemberBallot memberBallot=MemberBallot.findByMemberRound(session, questionType, member,i, locale.toString());
			List<MemberBallotChoice> choices=new ArrayList<MemberBallotChoice>();
			if(memberBallot.getQuestionChoices()!=null){
				for(MemberBallotChoice c:memberBallot.getQuestionChoices()){
					choices.add(c);
				}	
				/**** Set member ballot's choices to null and update member ballot****/
				memberBallot.setQuestionChoices(null);
				memberBallot.merge();
				/**** remove all previous entries ****/
				for(MemberBallotChoice c:choices){
					c.remove();
				}					
			}
			choicesClearedForRounds++;
			List<MemberBallotChoice> memberBallotChoices=new ArrayList<MemberBallotChoice>();
			Integer questionsInEachRound=noofQuestionsInEachRound.get("round"+i);
			for(int j=1;j<=questionsInEachRound;j++){
				if((autoFillingStartsAt>1)&&(count>=autoFillingStartsAt)){
					break;
				}else if(count>noOfAdmittedQuestions){
					break;
				}
				String strChoice=request.getParameter("choice"+count);
				String strQuestion=request.getParameter("question"+count);
				String strAnsweringDate=request.getParameter("answeringDate"+count);
				if(strChoice!=null&&strQuestion!=null&&strAnsweringDate!=null){
					MemberBallotChoice memberBallotChoice=null;
//					String strMemberChoice=request.getParameter("memberBallotchoiceId"+count);
//					if(strMemberChoice!=null){
//						memberBallotChoice=MemberBallotChoice.findById(MemberBallotChoice.class,Long.parseLong(strMemberChoice));
//					}else{
						memberBallotChoice=new MemberBallotChoice();
					//}
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
					memberBallotChoice.setAutoFilled(false);
					if(memberBallotChoice.getId()==null){
						memberBallotChoice.persist();
					}else{
						memberBallotChoice.merge();
					}
					memberBallotChoices.add(memberBallotChoice);
					manuallySetChoices.put(memberBallotChoice.getId(),memberBallotChoice);
				}
				count++;
			}
			memberBallot.setQuestionChoices(memberBallotChoices);
			memberBallot.merge();
		}
		/**** Here first we will clear the entries for the rounds which were not updated manually
		 * and might be present in the db.
		 */
		for(int i=choicesClearedForRounds+1;i<=totalRounds;i++){
			MemberBallot memberBallot=MemberBallot.findByMemberRound(session, questionType, member,i, locale.toString());
			List<MemberBallotChoice> choices=new ArrayList<MemberBallotChoice>();
			if(memberBallot.getQuestionChoices()!=null){
				for(MemberBallotChoice c:memberBallot.getQuestionChoices()){
					choices.add(c);
				}	
				/**** Set member ballot's choices to null and update member ballot****/
				memberBallot.setQuestionChoices(null);
				memberBallot.merge();
				/**** remove all previous entries ****/
				for(MemberBallotChoice c:choices){
					c.remove();
				}			
			}
		}
		if(autoFillingStartsAt>=1){
			if(autoFillingStartsAt==1){
				autoFillChoices("ALL",noofQuestionsInEachRound,
						totalRounds,noOfAdmittedQuestions,session,
						questionType,member,locale,
						flag,manuallySetChoices);
			}else{
				autoFillChoices("PARTIAL",noofQuestionsInEachRound,
						totalRounds,noOfAdmittedQuestions,session,
						questionType,member,locale,
						flag,manuallySetChoices);
			}			
		}
	}

	private void autoFillChoices(String autofillingPattern,Map<String, Integer> noofQuestionsInEachRound,
			int totalRounds, int noOfAdmittedQuestions, Session session,
			DeviceType questionType, Member member, String locale,
			String flag,Map<Long,MemberBallotChoice> manuallySetChoices){
		/**** Auto Filling Can Take Place by arranging questions according to lowest number 
		 * or lowest chart answering date ****/	
		/**** Now auto filling can be of all entries or of all entries - n entries ****/
		List<Question> inputQuestions=new ArrayList<Question>();
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "MEMBERBALLOT_AUTOFILL_BY_NUMBER","");
		if(customParameter!=null){
			if(customParameter.getValue().toUpperCase().equals(("NO"))){
				if(autofillingPattern.equals("ALL")){
					inputQuestions=MemberBallotChoice.findFirstBatchQuestions(session, questionType, member,"ALL","chart_answering_date",ApplicationConstants.ASC, locale);
				}else{
					inputQuestions=MemberBallotChoice.findFirstBatchQuestions(session, questionType, member,"PARTIAL","chart_answering_date",ApplicationConstants.ASC, locale);
				}
			}else{
				if(autofillingPattern.equals("ALL")){
					inputQuestions=MemberBallotChoice.findFirstBatchQuestions(session, questionType, member,"ALL","number",ApplicationConstants.ASC, locale);
				}else{
					inputQuestions=MemberBallotChoice.findFirstBatchQuestions(session, questionType, member,"PARTIAL","number",ApplicationConstants.ASC, locale);
				}
			}
		}else{
			if(autofillingPattern.equals("ALL")){
				inputQuestions=MemberBallotChoice.findFirstBatchQuestions(session, questionType, member,"ALL","number",ApplicationConstants.ASC, locale);
			}else{
				inputQuestions=MemberBallotChoice.findFirstBatchQuestions(session, questionType, member,"PARTIAL","number",ApplicationConstants.ASC, locale);
			}
		}	
		if(!inputQuestions.isEmpty()){
			int count=0;
			int size=inputQuestions.size();
			for(int i=1;i<=totalRounds;i++){
				/**** if n(=size) entries have been created then stop ****/
				if(count>=size){
					break;
				}
				List<MemberBallotChoice> memberBallotChoices=new ArrayList<MemberBallotChoice>();
				/**** Getting Member Ballot Entry for a particular round ****/
				MemberBallot memberBallot=MemberBallot.findByMemberRound(session, questionType, member,i, locale.toString());
				/**** Remove existing choices in case of edit ****/
				//if(flag.equals("edit")){
				/**** Obtain current choices ****/
				List<MemberBallotChoice> choices=new ArrayList<MemberBallotChoice>();
				if(memberBallot.getQuestionChoices()!=null){
					for(MemberBallotChoice c:memberBallot.getQuestionChoices()){
						choices.add(c);
					}	
					/**** Set member ballot's choices to null and update member ballot****/
					memberBallot.setQuestionChoices(null);
					memberBallot.merge();
					if(autofillingPattern.toUpperCase().equals("ALL")){
						/**** remove all previous entries ****/
						for(MemberBallotChoice c:choices){
							c.remove();
						}					
					}else{
						/**** we will remove entries other than those which member has manually set ****/
						for(MemberBallotChoice c:choices){
							if(manuallySetChoices.get(c.getId())==null){
								c.remove();
							}else{
								memberBallotChoices.add(c);
							}
						}	
					}
				}
				//}
				/**** In case of new and edit both auto fill will create entries automatically ****/
				Integer questionsInEachRound=noofQuestionsInEachRound.get("round"+i);
				int noOfmanuallyPopulatedChoices=memberBallotChoices.size();
				/**** Iterating for the no of questions allowed in each round times ****/
				for(int j=noOfmanuallyPopulatedChoices+1;j<=questionsInEachRound;j++){
					/**** if n(=size) entries have been created then stop ****/
					if(count>=size){
						break;
					}else if(noOfmanuallyPopulatedChoices==questionsInEachRound){
						break;
					}
					/**** Creating choice entry from the admitted question ****/
					MemberBallotChoice memberBallotChoice=new MemberBallotChoice();
					memberBallotChoice.setLocale(locale.toString());
					memberBallotChoice.setClubbingUpdated(false);
					memberBallotChoice.setProcessed(false);
					Question question=inputQuestions.get(count);
					memberBallotChoice.setChoice(j);
					memberBallotChoice.setQuestion(question);
					memberBallotChoice.setNewAnsweringDate(question.getChartAnsweringDate());
					memberBallotChoice.setAutoFilled(true);
					memberBallotChoice.persist();
					memberBallotChoices.add(memberBallotChoice);
					count++;
				}
				memberBallot.setQuestionChoices(memberBallotChoices);
				memberBallot.merge();
			}
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
