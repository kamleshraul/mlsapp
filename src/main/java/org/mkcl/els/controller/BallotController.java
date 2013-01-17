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

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.HalfHourBallotMemberVO;
import org.mkcl.els.common.vo.HalfHourBallotVO;
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
import org.mkcl.els.domain.UserGroup;
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
	public String getBallotPage(final HttpServletRequest request,final ModelMap model,
			final Locale locale){
		String strGroup=request.getParameter("group");
		if(strGroup!=null){
			Group group=Group.findById(Group.class,Long.parseLong(strGroup));
			List<MasterVO> masterVOs=new ArrayList<MasterVO>();
			List<QuestionDates> questionDates=group.getQuestionDates();
			for(QuestionDates i:questionDates){
				MasterVO masterVO=new MasterVO(i.getId(),FormaterUtil.getDateFormatter(locale.toString()).format(i.getAnsweringDate()));
				masterVOs.add(masterVO);
			}
			model.addAttribute("answeringDates",masterVOs);
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

        Group group = null;
        List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
        if(userGroups != null){
            if(! userGroups.isEmpty()){
                for(UserGroup i : userGroups) {
                    UserGroup j = UserGroup.findById(UserGroup.class,i.getId());
                    String strType = j.getUserGroupType().getType();
                    if(strType.equals("assistant")) {
                        String groupNumber = j.getParameterValue("GROUP_" + strLocale);
                        if(sessionType != null){
                            group = Group.findByNumberHouseTypeSessionTypeYear(
                                    Integer.parseInt(groupNumber), houseType, sessionType, year);
                        }
                    }
                }
            }
        }

        String strTempDate = request.getParameter("answeringDate");
        QuestionDates questionDates = QuestionDates.findById(QuestionDates.class, Long.parseLong(strTempDate));
        Date answeringDate = questionDates.getAnsweringDate();
        
        String strDeviceType = request.getParameter("questionType");
        DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
        
        if(answeringDate != null) {
        	if(deviceType.getType().equals("questions_starred")) {
        		Ballot ballot = Ballot.find(session, deviceType, group, answeringDate, strLocale);
                if(ballot == null) {
                    Ballot newBallot = 
                    	new Ballot(session, deviceType, group, answeringDate, new Date(), strLocale);
                    newBallot.create();
                    retVal = "CREATED";
                }
        	}
        	else if(deviceType.getType().equals("questions_halfhourdiscussion_from_question")) {
        		Ballot ballot = Ballot.find(session, deviceType, answeringDate, strLocale);
                if(ballot == null) {
                    Ballot newBallot = 
                    	new Ballot(session, deviceType, answeringDate, new Date(), strLocale);
                    newBallot.create();
                    retVal = "CREATED";
                }
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

        Group group = null;
        List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
        if(userGroups != null){
            if(! userGroups.isEmpty()){
                for(UserGroup i : userGroups) {
                    UserGroup j = UserGroup.findById(UserGroup.class,i.getId());
                    String strType = j.getUserGroupType().getType();
                    if(strType.equals("assistant")) {
                        String groupNumber = j.getParameterValue("GROUP_" + strLocale);
                        if(sessionType != null){
                            group = Group.findByNumberHouseTypeSessionTypeYear(
                                    Integer.parseInt(groupNumber), houseType, sessionType, year);
                        }
                    }
                }
            }
        }

        String strDeviceType = request.getParameter("questionType");
        DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
        
        String strTempDate=request.getParameter("answeringDate");
        QuestionDates questionDates=QuestionDates.findById(QuestionDates.class,Long.parseLong(strTempDate));
        Date answeringDate = questionDates.getAnsweringDate();

        if(answeringDate != null) {
        	CustomParameter parameter =
        		CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
        	String strAnsweringDate =
        		FormaterUtil.formatDateToString(answeringDate, parameter.getValue());
        	model.addAttribute("answeringDate", strAnsweringDate);
        		
        	if(houseType.getType().equals("lowerhouse")) {
            	if(deviceType.getType().equals("questions_starred")) {
            		List<StarredBallotVO> ballotVOs = 
                    	Ballot.findStarredBallotVOs(session, group, answeringDate, strLocale);
                    model.addAttribute("ballotVOs", ballotVOs);
                    retVal = "ballot/ballot";
    			}
    			else if(deviceType.getType().equals("questions_halfhourdiscussion_from_question")) {
    				List<HalfHourBallotMemberVO> ballotVOs = 
    					Ballot.findHalfHourBallotedMemberVOs(session, answeringDate, strLocale);
    				model.addAttribute("ballotVOs", ballotVOs);
    				retVal = "ballot/halfhour_member_ballot";
    			}
            }
            else if(houseType.getType().equals("upperhouse")) {
            	if(deviceType.getType().equals("questions_halfhourdiscussion_from_question")) {
            		List<HalfHourBallotVO> ballotVOs = 
            			Ballot.findHalfHourBallotedVOs(session, answeringDate, strLocale);
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

    @RequestMapping(value="/memberballot/init",method=RequestMethod.GET)
    public String viewMemberBallot(final HttpServletRequest request,
            final Locale locale){
        return "ballot/memberballotinit";
    }
    /**
     * Creates the final member ballot.
     *
     * @param request the request
     * @param locale the locale
     * @return the string
     */
    @RequestMapping(value="/memberballot/final",method=RequestMethod.POST)
    public String createFinalMemberBallot(final HttpServletRequest request,
            final ModelMap model,final Locale locale){
        return null;
    }
    
    @RequestMapping(value="/attendance",method=RequestMethod.GET)
	public String markAttendance(final HttpServletRequest request,final ModelMap model,final Locale locale){
		/*
		 * here we want to populate all those members in council who have submitted
		 * atleast 1 question or a maximum of 31 questions in first round of
		 * question submission.
		 *
		 */
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strQuestionType=request.getParameter("questionType");
		String strOperation=request.getParameter("operation");
		if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null){
			HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
			Integer sessionYear=Integer.parseInt(strSessionYear);
			Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
			if(session!=null){
				Boolean created=Question.createMemberBallotAttendance(session,questionType,locale.toString());
				if(created){
					logger.info("Member Ballot Attendance Entries Created");
				}else{
					logger.info("Member Ballot Attendance Entries Already Created");
				}
				List<MemberBallotAttendance> allItems=null;
				List<MemberBallotAttendance> selectedItems=null;
				if(strOperation.equals("presentees")){
					allItems=MemberBallotAttendance.findAll(session,questionType,"false","member",locale.toString());
					selectedItems=MemberBallotAttendance.findAll(session,questionType,"true","position",locale.toString());
				}else{
					allItems=MemberBallotAttendance.findAll(session,questionType,"true","member",locale.toString());
					selectedItems=MemberBallotAttendance.findAll(session,questionType,"false","position",locale.toString());
				}
				List<MemberBallotAttendance> eligibles=MemberBallotAttendance.findAll(session,questionType,"","member",locale.toString());
				model.addAttribute("allItems",allItems);
				model.addAttribute("selectedItems",selectedItems);
				model.addAttribute("eligibles",eligibles);
				model.addAttribute("session",session.getId());
				model.addAttribute("questionType",strQuestionType);
				model.addAttribute("houseType",strHouseType);
				model.addAttribute("sessionType",strSessionType);
				model.addAttribute("sessionYear",strSessionYear);
			}else{
				logger.error("**** Session not defined for selected houseType,sessionType and sessionYear ****");
			}
		}else{
			logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType' for null values ****");
		}
		return "ballot/attendance";
	}

	@RequestMapping(value="/attendance",method=RequestMethod.PUT)
	public @ResponseBody String updateAttendance(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String selectedItems=request.getParameter("items");
		String[] items=selectedItems.split(",");
		String strAttendance=request.getParameter("attendance");
		String strQuestionType=request.getParameter("questionType");
		String strSession=request.getParameter("session");
		Session session=Session.findById(Session.class,Long.parseLong(strSession));
		DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
		Boolean attendance=Boolean.parseBoolean(strAttendance);
		int position=0;
		for(String i:items){
			MemberBallotAttendance memberBallotAttendance=MemberBallotAttendance.findById(MemberBallotAttendance.class,Long.parseLong(i));
			position++;
			memberBallotAttendance.setPosition(position);
			memberBallotAttendance.setAttendance(attendance);
			memberBallotAttendance.merge();
		}
		List<MemberBallotAttendance> memberBallotAttendances=MemberBallotAttendance.findAll(session, questionType, "", "position", locale.toString());
		for(MemberBallotAttendance i:memberBallotAttendances){
			if(!selectedItems.contains(String.valueOf(i.getId()))){
				i.setAttendance(!attendance);
				i.merge();
			}
		}
		return "success";
	}

	@RequestMapping(value="/preballot",method=RequestMethod.GET)
	public String preBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strQuestionType=request.getParameter("questionType");
		String strAttendance=request.getParameter("attendance");
		model.addAttribute("attendance",strAttendance);
		if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null){
			HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
			Integer sessionYear=Integer.parseInt(strSessionYear);
			Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
			if(session!=null){
				List<MemberBallotAttendance> memberBallotAttendances=null;
				if(strAttendance.equals("present")){
					memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"true","position",locale.toString());
				}else{
					memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"false","position",locale.toString());
				}
				model.addAttribute("selectedItems",memberBallotAttendances);
			}else{
				logger.error("**** Session not defined for selected houseType,sessionType and sessionYear ****");
			}
		}else{
			logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType' for null values ****");
		}
		return "ballot/preballot";
	}

	@RequestMapping(value="/memberballot/create",method=RequestMethod.POST)
	public @ResponseBody String createMemberBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strQuestionType=request.getParameter("questionType");
		String strAttendance=request.getParameter("attendance");
		String strRound=request.getParameter("round");
		if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null&&strAttendance!=null&&strRound!=null){
			HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
			Integer sessionYear=Integer.parseInt(strSessionYear);
			Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
			if(session!=null){
				Boolean status=MemberBallot.createMemberBallot(session,questionType,Boolean.parseBoolean(strAttendance),Integer.parseInt(strRound),locale.toString());
				if(status){
					return "success";
				}else{
					return "alreadycreated";
				}
			}else{
				logger.error("**** Session not defined for selected houseType,sessionType and sessionYear ****");
				return "failed";
			}
		}else{
			logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType,attendance and round' for null values ****");
			return "failed";
		}
	}

	@RequestMapping(value="/memberballot/view",method=RequestMethod.GET)
	public String viewMemberBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strQuestionType=request.getParameter("questionType");
		model.addAttribute("houseType",strHouseType);
		model.addAttribute("sessionType",strSessionType);
		model.addAttribute("sessionYear",strSessionYear);
		model.addAttribute("questionType",strQuestionType);
		if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null){
			HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
			Integer sessionYear=Integer.parseInt(strSessionYear);
			Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			if(session!=null){
				List<Group> groups=Group.findByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				model.addAttribute("groups",groups);
				CustomParameter totalRoundsCouncilBallot=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_TOTALROUNDS", "");
				if(totalRoundsCouncilBallot!=null){
					int rounds=Integer.parseInt(totalRoundsCouncilBallot.getValue());
					List<Reference> references=new ArrayList<Reference>();
					NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
					for(int i=1;i<=rounds;i++){
						references.add(new Reference(String.valueOf(i),String.valueOf(format.format(i))));
					}
					model.addAttribute("rounds",references);
				}else{
					logger.error("**** Custom Parameter 'STARRED_MEMBERBALLOTCOUNCIL_TOTALROUNDS' not set");
				}
			}else{
				logger.error("**** Session not defined for selected houseType,sessionType and sessionYear ****");
			}
		}else{
			logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType,attendance and round' for null values ****");
		}
		return "ballot/memberballot";
	}


	@RequestMapping(value="/memberballotresult",method=RequestMethod.GET)
	public String viewMemberBallotResult(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strQuestionType=request.getParameter("questionType");
		String strAttendance=request.getParameter("attendance");
		String strRound=request.getParameter("round");
		if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null&&strAttendance!=null&&strRound!=null){
			HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
			Integer sessionYear=Integer.parseInt(strSessionYear);
			Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
			if(session!=null){
				String strGroup=request.getParameter("group");
				String strAnsweringDate=request.getParameter("answeringDate");
				List<MemberBallot> memberBallots=null;
				if(strGroup==null&&strAnsweringDate==null){
					memberBallots=MemberBallot.viewMemberBallot(session,questionType,Boolean.parseBoolean(strAttendance),Integer.parseInt(strRound),locale.toString());
				}else if(strGroup.equals("-")&&strAnsweringDate.equals("-")){
					memberBallots=MemberBallot.viewMemberBallot(session,questionType,Boolean.parseBoolean(strAttendance),Integer.parseInt(strRound),locale.toString());
				}else if((!strGroup.equals("-"))&&strAnsweringDate.equals("-")){
					Group group=Group.findById(Group.class,Long.parseLong(strGroup));
					memberBallots=MemberBallot.viewMemberBallot(session,questionType,Boolean.parseBoolean(strAttendance),Integer.parseInt(strRound),group,locale.toString());
				}else if((!strGroup.equals("-"))&&(!strAnsweringDate.equals("-"))){
					QuestionDates answeringDate=Question.findById(QuestionDates.class,Long.parseLong(strAnsweringDate));
					memberBallots=MemberBallot.viewMemberBallot(session,questionType,Boolean.parseBoolean(strAttendance),Integer.parseInt(strRound),answeringDate,locale.toString());
				}
				model.addAttribute("memberBallots",memberBallots);
			}else{
				logger.error("**** Session not defined for selected houseType,sessionType and sessionYear ****");
			}
		}else{
			logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType,attendance and round' for null values ****");
		}
		return "ballot/memberballotresult";
	}

	@RequestMapping(value="/memberballotchoice",method=RequestMethod.GET)
	public String viewMemberBallotChoice(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strQuestionType=request.getParameter("questionType");
		if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null){
			HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
			Integer sessionYear=Integer.parseInt(strSessionYear);
			Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
			if(session!=null){
				model.addAttribute("session",session.getId());
				model.addAttribute("questionType",questionType.getId());
				List<Member> eligibleMembers=MemberBallotAttendance.findEligibleMembers(session, questionType, locale.toString());
				model.addAttribute("eligibleMembers", eligibleMembers);
			}else{
				logger.error("**** Session not defined for selected houseType,sessionType and sessionYear ****");
			}
		}else{
			logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType' for null values ****");
		}
		return "ballot/memberballotchoice";
	}

	@RequestMapping(value="/listmemberballotchoice",method=RequestMethod.GET)
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

	@RequestMapping(value="/memberballotchoice",method=RequestMethod.POST)
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

}
