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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BallotMemberVO;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.common.vo.MasterVO;
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
				DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
				Integer round=Integer.parseInt(strRound);				
				String noOfRounds=session.getParameter("questions_starred_totalRoundsMemberBallot");
				List<Reference> rounds=new ArrayList<Reference>();
				for(int i=1;i<=Integer.parseInt(noOfRounds);i++){
					Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
					rounds.add(reference);
				}
				List<Group> groups=Group.findByHouseTypeSessionTypeYear(houseType,sessionType,sessionYear);
				List<MasterVO> masterVOs=new ArrayList<MasterVO>();
				for(Group i:groups){
					MasterVO masterVO=new MasterVO(i.getId(),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i.getNumber()));
					masterVOs.add(masterVO);
				}
				model.addAttribute("groups",masterVOs);
				model.addAttribute("rounds",rounds);
				model.addAttribute("selectedRound",round);
				model.addAttribute("session",session.getId());
				model.addAttribute("questionType", questionType.getId());
				model.addAttribute("noOfRounds",noOfRounds);
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
		
	@RequestMapping(value="/memberballot/attendance",method=RequestMethod.GET)
	public String markAttendance(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		String strSession=request.getParameter("session");
		String strQuestionType=request.getParameter("questionType");
		String strRound=request.getParameter("round");
		String strAttendance=request.getParameter("attendance");
		String strNoOfRounds=request.getParameter("noofrounds");
		if(strSession!=null&&strQuestionType!=null&&strRound!=null&&strAttendance!=null&&strNoOfRounds!=null){
			if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())&&(!strRound.isEmpty())&&(!strAttendance.isEmpty())&&(!strNoOfRounds.isEmpty())){
				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
				Integer round=Integer.parseInt(strRound);
				Boolean attendance=Boolean.parseBoolean(strAttendance);
				Integer noOfRounds=Integer.parseInt(strNoOfRounds);
				Boolean previousRoundPresenteesLocked=true;
				Boolean previousRoundAbsenteesLocked=true;
				if(round!=1){
					previousRoundPresenteesLocked=MemberBallotAttendance.areMembersLocked(session,questionType,round-1,true,noOfRounds,locale.toString());
					previousRoundAbsenteesLocked=MemberBallotAttendance.areMembersLocked(session,questionType,round-1,false,noOfRounds,locale.toString());
				}
				if(previousRoundPresenteesLocked&&previousRoundAbsenteesLocked){
					Boolean attendanceCreatedAlready=MemberBallotAttendance.memberBallotCreated(session,questionType,round,locale.toString());
					if(!attendanceCreatedAlready){
						String flag=MemberBallotAttendance.createMemberBallotAttendance(session,questionType,round,locale.toString());
						if(!flag.contains("SUCCESS")){
							model.addAttribute("type", flag);
							return errorpage;
						}
					}
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
					if(attendance){
						Boolean currentRoundPresenteesLocked=MemberBallotAttendance.areMembersLocked(session,questionType,round,true,noOfRounds,locale.toString());
						model.addAttribute("membersLocked",currentRoundPresenteesLocked);
					}else{
						Boolean currentRoundAbsenteesLocked=MemberBallotAttendance.areMembersLocked(session,questionType,round,false,noOfRounds,locale.toString());
						model.addAttribute("membersLocked",currentRoundAbsenteesLocked);
					}
				}else if(previousRoundPresenteesLocked==false&&previousRoundAbsenteesLocked==false){
					model.addAttribute("type","PREVIOUS_ROUND_NOT_LOCKED");
					return errorpage;
				}else if(previousRoundAbsenteesLocked==false){
					model.addAttribute("type","PREVIOUS_ROUND_ABSENTEES_NOT_LOCKED");
					return errorpage;
				}else if(previousRoundPresenteesLocked==false){
					model.addAttribute("type","PREVIOUS_ROUND_PRESENTEES_NOT_LOCKED");
					return errorpage;
				}
			}else{
				logger.error("**** Check request parameter 'session,questionType,round and attendance' for empty values ****");
				model.addAttribute("type", "ATTENDANCE_REQUEST_PARAMETER_EMPTY");
				return "ballot/error";
			}
		}else{
			logger.error("**** Check request parameter 'session,questionType,round and attendance' for null values ****");
			model.addAttribute("type", "ATTENDANCE_REQUEST_PARAMETER_NULL");
			return "ballot/error";
		}			
		return "ballot/memberballotattendance";		
	}
	
	@RequestMapping(value="/memberballot/attendance",method=RequestMethod.PUT)
	public @ResponseBody String updateAttendance(final HttpServletRequest request,final ModelMap model,final Locale locale){
		try{
			String selectedItems=request.getParameter("items");
			String[] items=selectedItems.split(",");
			String strAttendance=request.getParameter("attendance");
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			String strRound=request.getParameter("round");
			String strLocked=request.getParameter("locked");
			String strNoOfRounds=request.getParameter("noofrounds");
			Session session=Session.findById(Session.class,Long.parseLong(strSession));
			DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
			Boolean attendance=Boolean.parseBoolean(strAttendance);
			Integer round=Integer.parseInt(strRound);
			Boolean locked=Boolean.parseBoolean(strLocked);
			Integer noofRounds=Integer.parseInt(strNoOfRounds);
			Boolean currentRoundPresenteesLocked=MemberBallotAttendance.areMembersLocked(session,questionType,round,true,noofRounds,locale.toString());
			Boolean currentRoundAbsenteesLocked=MemberBallotAttendance.areMembersLocked(session,questionType,round,false,noofRounds,locale.toString());
			if(selectedItems.isEmpty()){
				if(currentRoundAbsenteesLocked==true&&currentRoundPresenteesLocked==true){
					return "locked";
				}else{
					return "success";
				}
			}
			if(currentRoundPresenteesLocked==false||currentRoundAbsenteesLocked==false){		
				int position=0;
				/**** setting position,attendance and round of selected member ballot attendance entries****/
				for(String i:items){
					MemberBallotAttendance memberBallotAttendance=MemberBallotAttendance.findById(MemberBallotAttendance.class,Long.parseLong(i));
					position++;
					memberBallotAttendance.setPosition(position);
					memberBallotAttendance.setAttendance(attendance);
					memberBallotAttendance.setRound(round);
					memberBallotAttendance.setLocked(locked);
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
				return "success";
			}else{
				return "locked";
			}

		}catch(Exception ex){
			logger.error("failed",ex);
			return "failed";
		}
	}
	
	@RequestMapping(value="/memberballot/preballot",method=RequestMethod.GET)
	public String councilStarredPreBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		try{
			String strAttendance=request.getParameter("attendance");
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			String strRound=request.getParameter("round");
			String strNoOfRounds=request.getParameter("noofrounds");
			Session session=Session.findById(Session.class,Long.parseLong(strSession));
			DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
			Boolean attendance=Boolean.parseBoolean(strAttendance);
			Integer round=Integer.parseInt(strRound);
			Integer noofRounds=Integer.parseInt(strNoOfRounds);
			Boolean previousRoundPresenteesLocked=true;
			Boolean previousRoundAbsenteesLocked=true;
			if(round!=1){
				previousRoundPresenteesLocked=MemberBallotAttendance.areMembersLocked(session,questionType,round-1,true,noofRounds,locale.toString());
				previousRoundAbsenteesLocked=MemberBallotAttendance.areMembersLocked(session,questionType,round-1,false,noofRounds,locale.toString());
			}
			if(previousRoundPresenteesLocked&&previousRoundAbsenteesLocked){
				List<MemberBallotAttendance> memberBallotAttendances=null;
				if(attendance){
					memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"true",round,"position",locale.toString());
				}else{
					memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"false",round,"position",locale.toString());
				}
				model.addAttribute("selectedItems",memberBallotAttendances);
				model.addAttribute("attendance",attendance);
			}else if(previousRoundAbsenteesLocked==false&&previousRoundPresenteesLocked==false){
				model.addAttribute("type","PREVIOUS_ROUND_NOT_LOCKED");
				return errorpage;
			}else if(previousRoundAbsenteesLocked==false){
				model.addAttribute("type","PREVIOUS_ROUND_ABSENTEES_NOT_LOCKED");
				return errorpage;
			}else if(previousRoundPresenteesLocked==false){
				model.addAttribute("type","PREVIOUS_ROUND_PRESENTEES_NOT_LOCKED");
				return errorpage;
			}		

		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "ballot/memberballotpreballot";
	}

	@RequestMapping(value="/preballot",method=RequestMethod.GET)
	public String preBallot(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String retVal = "question/error";
		try {
			String houseType = request.getParameter("houseType");
			
			String strQuestionType = request.getParameter("questionType");
			DeviceType deviceType = 
				DeviceType.findById(DeviceType.class, Long.parseLong(strQuestionType));
			
			if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
				if(deviceType.getType().equals(
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					retVal = this.halfHourPreBallot(request, model, deviceType, locale);
				}
			}
			else if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
				if(deviceType.getType().equals(
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					retVal = this.halfHourPreBallot(request, model, deviceType, locale);
				}
			}
		}
		catch(Exception e) {
			model.addAttribute("errorcode", "insufficientParametersForBallotCreation");
			e.printStackTrace();
		}
		return retVal;
	}
		
	private String halfHourPreBallot(final HttpServletRequest request,
			final ModelMap model,
			final DeviceType deviceType,
			final Locale locale) {
		String strLocale = locale.toString();
		
		String strHouseType = request.getParameter("houseType");
		HouseType houseType =
            HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
		
		String strSessionTypeId = request.getParameter("sessionType");
		SessionType sessionType =
            SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
		
		String strYear = request.getParameter("sessionYear");
		Integer year = Integer.valueOf(strYear);
		
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
        
		List<BallotVO> ballotVOs = 
			Ballot.findPreBallotVO(session, deviceType, answeringDate, strLocale);
		
		model.addAttribute("ballotVOs", ballotVOs);
		return "ballot/halfhour_ballot";
	}
	
	@RequestMapping(value="/memberballot/view",method=RequestMethod.GET)
	public String createMemberBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		try{
			String strAttendance=request.getParameter("attendance");
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			String strRound=request.getParameter("round");
			String strNoOfRounds=request.getParameter("noofrounds");
			String strGroup=request.getParameter("group");
			String strAnsweringDate=request.getParameter("answeringDate");

			if(strSession!=null&&strQuestionType!=null
					&&strAttendance!=null&&strRound!=null&&strNoOfRounds!=null
			){
				if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())
						&&(!strAttendance.isEmpty())&&(!strRound.isEmpty())
						&&(!strNoOfRounds.isEmpty())){
					Session session=Session.findById(Session.class,Long.parseLong(strSession));
					DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
					Boolean attendance=Boolean.parseBoolean(strAttendance);
					Integer round=Integer.parseInt(strRound);
					Integer noofRounds=Integer.parseInt(strNoOfRounds);

					Boolean previousRoundPresenteesLocked=true;
					Boolean previousRoundAbsenteesLocked=true;
					if(round!=1){
						previousRoundPresenteesLocked=MemberBallotAttendance.areMembersLocked(session,questionType,round-1,true,noofRounds,locale.toString());
						previousRoundAbsenteesLocked=MemberBallotAttendance.areMembersLocked(session,questionType,round-1,false,noofRounds,locale.toString());
					}
					if(previousRoundPresenteesLocked&&previousRoundAbsenteesLocked){
						MemberBallot.createMemberBallot(session,questionType,attendance,round,locale.toString());
						List<MemberBallot> memberBallots=null;
						if(strGroup==null&&strAnsweringDate==null){
							memberBallots=MemberBallot.viewMemberBallot(session,questionType,attendance,round,locale.toString());
						}else if(strGroup.equals("-")&&strAnsweringDate.equals("-")){
							memberBallots=MemberBallot.viewMemberBallot(session,questionType,attendance,round,locale.toString());
						}else if((!strGroup.equals("-"))&&strAnsweringDate.equals("-")){
							Group group=Group.findById(Group.class,Long.parseLong(strGroup));
							memberBallots=MemberBallot.viewMemberBallot(session,questionType,attendance,round,group,locale.toString());
						}else if((!strGroup.equals("-"))&&(!strAnsweringDate.equals("-"))){
							QuestionDates answeringDate=Question.findById(QuestionDates.class,Long.parseLong(strAnsweringDate));
							memberBallots=MemberBallot.viewMemberBallot(session,questionType,attendance,round,answeringDate,locale.toString());
						}
						model.addAttribute("memberBallots",memberBallots);								
					}else if(previousRoundAbsenteesLocked==false&&previousRoundPresenteesLocked==false){
						model.addAttribute("type","PREVIOUS_ROUND_NOT_LOCKED");
						return errorpage;
					}else if(previousRoundAbsenteesLocked==false){
						model.addAttribute("type","PREVIOUS_ROUND_ABSENTEES_NOT_LOCKED");
						return errorpage;
					}else if(previousRoundPresenteesLocked==false){
						model.addAttribute("type","PREVIOUS_ROUND_PRESENTEES_NOT_LOCKED");
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
		String strQuestionType=request.getParameter("deviceType");
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
						logger.error("**** Custom Parameter 'STARRED_MEMBERBALLOTCOUNCIL_ROUND'"+i+" not set");
					}
				}
			}else{
				model.addAttribute("flag","edit");
			}

		}else{
			logger.error("**** Check request parameter 'session,deviceType and member' for null values ****");
		}
		return "ballot/listmemberballotchoice";
	}

	@RequestMapping(value="/memberballot/choices",method=RequestMethod.POST)
	public @ResponseBody String updateMemberBallotChoice(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strQuestionType=request.getParameter("deviceType");
		String strSession=request.getParameter("session");
		String strMember=request.getParameter("member");
		String strTotalRounds=request.getParameter("totalRounds");
		String strNoOfAdmittedQuestions=request.getParameter("noOfAdmittedQuestions");
		int count=1;
		int noOfAdmittedQuestions=Integer.parseInt(strNoOfAdmittedQuestions);
		if(strQuestionType!=null&&strSession!=null&&strMember!=null&&strTotalRounds!=null&&strNoOfAdmittedQuestions!=null){
			Session session=Session.findById(Session.class,Long.parseLong(strSession));
			DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
			Member member=Member.findById(Member.class,Long.parseLong(strMember));
			Boolean status=true;
			for(int i=1;i<=Integer.parseInt(strTotalRounds);i++){
				if(count>noOfAdmittedQuestions){
					break;
				}
				if(status){
					MemberBallot memberBallot=MemberBallot.findByMemberRound(session, questionType, member,i, locale.toString());
					List<MemberBallotChoice> memberBallotChoices=new ArrayList<MemberBallotChoice>();
					CustomParameter questionsInEachRound=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_ROUND"+i, "");
					for(int j=1;j<=Integer.parseInt(questionsInEachRound.getValue());j++){
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
								}
								memberBallotChoice.setQuestion(question);
								question.merge();
							}
							memberBallotChoice.setLocale(locale.toString());
							memberBallotChoices.add(memberBallotChoice);
						}else{
							status=false;
							break;
						}
						count++;
					}
					if(status){
						memberBallot.setQuestionChoices(memberBallotChoices);
						memberBallot.merge();
					}else{
						break;
					}
				}
			}
		}else{
			logger.error("**** Check request parameter 'session,deviceType,member,totalRounds and noOfAdmittedQuestions' for null values ****");
			return "failure";
		}
		return "success";
	}

	@Transactional
	@RequestMapping(value="/memberballot/updateclubbing",method=RequestMethod.PUT)
	public Boolean updateClubbingMemberBallot(final HttpServletRequest request,final Locale locale){
		Boolean status=false;
		String strSession=request.getParameter("session");
		String strDeviceType=request.getParameter("deviceType");
		if(strSession!=null&&strDeviceType!=null){
			Session session=Session.findById(Session.class,Long.parseLong(strSession));
			DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
			Integer primaryInMBCount=MemberBallot.findPrimaryCount(session,deviceType,locale.toString());
			int start=0;
			int size=50;
			for(int i=start;i<primaryInMBCount;i=i+size){
				status=MemberBallot.updateClubbing(session,deviceType,i,i+size,locale.toString());
			}
		}else{
			logger.error("**** Check request parameters 'session and deviceType' for null values ****");
		}
		return status;
	}

	@RequestMapping(value="/memberballot/final",method=RequestMethod.POST)
	public String createFinalMemberBallot(final HttpServletRequest request,
			final ModelMap model,final Locale locale){
		return null;
	}

}
