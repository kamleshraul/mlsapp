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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.BallotMemberVO;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.common.vo.BillBallotVO;
import org.mkcl.els.common.vo.GroupVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberBallotChoiceRevisionVO;
import org.mkcl.els.common.vo.MemberBallotFinalBallotVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseCountVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseQuestionVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseReportVO;
import org.mkcl.els.common.vo.MemberBallotQuestionDistributionVO;
import org.mkcl.els.common.vo.MemberBallotVO;
import org.mkcl.els.common.vo.MemberwiseQuestionsVO;
import org.mkcl.els.common.vo.QuestionSequenceVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.StarredBallotVO;
import org.mkcl.els.common.xmlvo.CumulativeMemberwiseQuestionsXmlVO;
import org.mkcl.els.common.xmlvo.MemberBallotTotalQuestionReportXmlVO;
import org.mkcl.els.common.xmlvo.MemberwiseQuestionsXmlVO;
import org.mkcl.els.domain.ActivityLog;
import org.mkcl.els.domain.Question.PROCESSING_MODE;
import org.mkcl.els.domain.ballot.Ballot;
import org.mkcl.els.domain.ballot.BallotEntry;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.MemberBallotChoice;
import org.mkcl.els.domain.MemberBallotChoiceAudit;
import org.mkcl.els.domain.MemberBallotChoiceDraft;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.ballot.PreBallot;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionPlace;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibm.icu.util.Calendar;

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
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		
		try {
			
			String category = request.getParameter("category");

			if(category != null){
				if(category.equals("question")){
					model.addAttribute("category", "question");
					/** Create DeviceType */
					String strQuestionType = request.getParameter("questionType");			
					DeviceType deviceType = 
							DeviceType.findById(DeviceType.class, Long.parseLong(strQuestionType));
					model.addAttribute("deviceTypeType", deviceType.getType());

					if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						/** Create Group*/
						String strGroup = request.getParameter("group");
						Group group = Group.findById(Group.class, Long.parseLong(strGroup));
						String strHouseType = request.getParameter("houseType");
						HouseType houseType =
								HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());

						model.addAttribute("balHouseType", houseType.getType());
						/** Compute & Add answeringDates to model*/
						List<MasterVO> masterVOs = new ArrayList<MasterVO>();
						List<QuestionDates> questionDates = group.getQuestionDates();
						for(QuestionDates i : questionDates){
							String strAnsweringDate = 
									FormaterUtil.getDateFormatter(locale.toString()).format(i.getAnsweringDate());
							MasterVO masterVO = new MasterVO(i.getId(), strAnsweringDate);
							masterVO.setValue(FormaterUtil.formatDateToString(i.getAnsweringDate(), ApplicationConstants.DB_DATEFORMAT));
							masterVOs.add(masterVO);
						}
						model.addAttribute("answeringDates", masterVOs);
					}else if(deviceType.getType().equals(
							ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
						/** Create HouseType */
						String strHouseType = request.getParameter("houseType");
						HouseType houseType =
								HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());

						model.addAttribute("balHouseType", houseType.getType());
						
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
				}else if(category.equals("resolution")){

					model.addAttribute("category", "resolution");
					String strDeviceType = request.getParameter("deviceType");			
					DeviceType deviceType = 
							DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
					model.addAttribute("deviceTypeType", deviceType.getType());

					if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {

						String strHouseType = request.getParameter("houseType");
						model.addAttribute("houseType", strHouseType);
						String strSessionYear = request.getParameter("sessionYear");
						String strSessionType = request.getParameter("sessionType");
						List<MasterVO> masterVOs = new ArrayList<MasterVO>();

						if(strHouseType != null && strSessionYear != null && strSessionType != null){
							if((!strHouseType.isEmpty()) && (!strSessionYear.isEmpty()) && (!strSessionType.isEmpty())){
								HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
								model.addAttribute("houseType", strHouseType);
								Integer sessionYear  = Integer.parseInt(strSessionYear);
								SessionType sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
								Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
								//findSessionByHouseSessionTypeYear(house, sessionType, sessionYear);

								String strDiscussionDates = session.getParameter(deviceType.getType()+"_discussionDates");

								String[] strDates = strDiscussionDates.split("#");

								//tester(new Long(4), new Long(10));

								for(String i: strDates){

									Date date = FormaterUtil.formatStringToDate(i, ApplicationConstants.DB_DATEFORMAT);
									MasterVO masterVO = new MasterVO(i.hashCode(), FormaterUtil.getDateFormatter(locale.toString()).format(date));
									masterVO.setValue(i);
									masterVOs.add(masterVO);
								}
							}						
						}
						model.addAttribute("answeringDates", masterVOs);
					}
				}else if(category.equals("bill")){

					model.addAttribute("category", "bill");
					String strDeviceType = request.getParameter("deviceType");			
					DeviceType deviceType = 
							DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
					model.addAttribute("deviceTypeType", deviceType.getType());

					if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {

						String strHouseType = request.getParameter("houseType");						
						String strSessionYear = request.getParameter("sessionYear");
						String strSessionType = request.getParameter("sessionType");
						List<MasterVO> masterVOs = new ArrayList<MasterVO>();

						if(strHouseType != null && strSessionYear != null && strSessionType != null){
							if((!strHouseType.isEmpty()) && (!strSessionYear.isEmpty()) && (!strSessionType.isEmpty())){
								HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
								model.addAttribute("houseType", strHouseType);
								Integer sessionYear  = Integer.parseInt(strSessionYear);
								SessionType sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
								Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
								//findSessionByHouseSessionTypeYear(house, sessionType, sessionYear);

								String strDiscussionDates = session.getParameter(deviceType.getType()+"_discussionDates");

								String[] strDates = strDiscussionDates.split("#");

								//tester(new Long(4), new Long(10));

								for(String i: strDates){

									Date date = FormaterUtil.formatStringToDate(i, ApplicationConstants.DB_DATEFORMAT);
									MasterVO masterVO = new MasterVO(i.hashCode(), FormaterUtil.getDateFormatter(locale.toString()).format(date));
									masterVO.setValue(i);
									masterVOs.add(masterVO);
								}
							}						
						}
						model.addAttribute("answeringDates", masterVOs);
					}
				}else if(category.equals("motion")){
					String strDeviceType = request.getParameter("questionType");			
					DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
					model.addAttribute("deviceTypeType", deviceType.getType());
					model.addAttribute("category", "motion");
					
					if(deviceType.getType().equals(
							ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
						/** Create HouseType */
						String strHouseType = request.getParameter("houseType");
						HouseType houseType =
								HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
						model.addAttribute("houseType",houseType.getType());

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
								session.getParameter("motions_standalonemotion_halfhourdiscussion_discussionDates");
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
				}

				List<MasterVO> outputFormats = new ArrayList<MasterVO>();
				MasterVO pdfFormat = new MasterVO();
				pdfFormat.setName("PDF");
				pdfFormat.setValue("PDF");
				outputFormats.add(pdfFormat);
				MasterVO wordFormat = new MasterVO();
				wordFormat.setName("WORD");
				wordFormat.setValue("WORD");
				outputFormats.add(wordFormat);									
				model.addAttribute("outputFormats", outputFormats);
				
				CustomParameter csptHighSecurityPasswordEnabledFlag = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.HIGH_SECURITY_PASSWORD_ENABLED_FLAG, "");
				if(csptHighSecurityPasswordEnabledFlag!=null
						&& csptHighSecurityPasswordEnabledFlag.getValue()!=null
						&& !csptHighSecurityPasswordEnabledFlag.getValue().isEmpty()) {
					model.addAttribute("highSecurityPasswordEnabled", csptHighSecurityPasswordEnabledFlag.getValue());
				} else {
					model.addAttribute("highSecurityPasswordEnabled", "yes");
				}

				retVal = "ballot/ballotinit";
			}

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
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
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
			DeviceType deviceType = null;
			String strQuestionType = request.getParameter("questionType");
			if(strQuestionType != null){
				if(!strQuestionType.isEmpty()){
					deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strQuestionType));
				}
			}else{
				String strDeviceType = request.getParameter("deviceType");
				if(strDeviceType != null){
					if(!strDeviceType.isEmpty()){
						deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
					}
				}
			}

			/** Create answeringDate */
			String strAnsweringDate = request.getParameter("answeringDate");
			Date answeringDate = null;
			QuestionDates questionDates = null;
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				questionDates = QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
				answeringDate = questionDates.getAnsweringDate();
			}
			else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
					deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
				CustomParameter dbDateFormat = 
						CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION) || deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)){
				CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}

			/**** Validate whether ballot can be created for bill ****/
			if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
				boolean isBallotAllowedToCreate = validateBallotCreationForBill(session, deviceType, answeringDate, locale.toString());
				if(isBallotAllowedToCreate==false) {
					retVal = "NOT_ALLOWED";
					return retVal;
				}
			}

			/** Create Ballot */
			Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale.toString());
			if(ballot == null) {
				Ballot newBallot = new Ballot(session, deviceType, answeringDate, new Date(), locale.toString());
				newBallot.create();	
				//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
//				String groupNumber = "";
//				if(questionDates!=null) {					
//					Group group = Group.findByAnsweringDateInHouseType(answeringDate, houseType);	
//					groupNumber = group.getNumber().toString();
//				}
//				String ballotUserName = this.getCurrentUser().getActualUsername();
//				CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_BALLOT_NOTIFICATION_DISABLED", "");
//				if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
//						|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
//					NotificationController.sendBallotCreationNotification(deviceType, houseType, answeringDate, groupNumber, ballotUserName, locale.toString());
//				}
				retVal = "CREATED";
			}
			else {
				retVal = "ALREADY_EXISTS";
			}
		}
		catch(ELSException elsex) {
			retVal = "PRE_BALLOT_NOT_CREATED";
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
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try {
			
			/**** To save the log of activity, id of the class ****/
			String classId = null;
			
			model.addAttribute("formater", new FormaterUtil());
			model.addAttribute("locale", locale.toString());
			
			/** Create HouseType */
			String strHouseType = request.getParameter("houseType");
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			model.addAttribute("houseType", houseType.getType());

			/** Create SessionType */
			String strSessionTypeId = request.getParameter("sessionType");
			SessionType sessionType = SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
			model.addAttribute("sessionType", sessionType.getType());

			/** Create year */
			String strYear = request.getParameter("sessionYear");
			Integer year = Integer.valueOf(strYear);
			model.addAttribute("sessionYear", year);

			/** Create Session */
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			model.addAttribute("session", session.getId());

			/** Create DeviceType */
			DeviceType deviceType = null;
			String strDeviceType = request.getParameter("questionType");
			if(strDeviceType == null){
				strDeviceType = request.getParameter("deviceType");
			}
			deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			model.addAttribute("deviceType", deviceType.getType());
			model.addAttribute("deviceName", deviceType.getName());

			Group group = null;
			if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
					&& houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) 
					&& !deviceType.getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)
					&& !deviceType.getType().startsWith(ApplicationConstants.DEVICE_BILLS)){
				String strGroup = request.getParameter("group");
				group = Group.findById(Group.class, Long.valueOf(strGroup));
			}
			
			/** Populate User Details */
			List<UserGroup> userGroups = UserGroup.findActiveUserGroupsOfGivenUser(this.getCurrentUser().getActualUsername(), houseType.getName(), deviceType.getName(), locale.toString());
			StringBuffer designation = new StringBuffer("");
			for(UserGroup ug : userGroups){
				if(ug != null){
					designation.append(ug.getUserGroupType().getName());
					break;
				}
			}
			model.addAttribute("currentDesignation", designation.toString());
			model.addAttribute("currentUser", (this.getCurrentUser().getTitle()+" "+this.getCurrentUser().getFirstName()+" " + this.getCurrentUser().getMiddleName() + " " +this.getCurrentUser().getLastName()));

			/** Create answeringDate */
			String strAnsweringDate = request.getParameter("answeringDate");
			Date answeringDate = null;
			QuestionDates questionDates = null;
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				questionDates = QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
				answeringDate = questionDates.getAnsweringDate();
			}
			else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
					deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {

				CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION) || deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)){
				CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}

			/** Add localized answeringDate to model */
			CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT_HYPHEN", "");
			String localizedAnsweringDate = FormaterUtil.formatDateToString(answeringDate, parameter.getValue(), locale.toString());
			model.addAttribute("answeringDate", localizedAnsweringDate);

			/** DeviceType & HouseType specific Ballot views */
			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					/*List<BallotMemberVO> ballotVOs = Ballot.findBallotedMemberVO(session, deviceType, answeringDate, locale.toString());
					model.addAttribute("ballotVOs", ballotVOs);*/

					Map<String, String[]> parametersMap = new HashMap<String, String[]>();
					parametersMap.put("locale", new String[]{locale.toString()});
					parametersMap.put("sessionId", new String[]{session.getId().toString()});
					parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
					parametersMap.put("groupId", new String[]{group.getId().toString()});
					parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});

					List ballotVOs = org.mkcl.els.domain.Query.findReport("HDQ_ASSEMBLY_BALLOT_VIEW", parametersMap);
					parametersMap = null;
					
					if(ballotVOs != null && !ballotVOs.isEmpty()){
						Object[] objs = (Object[])ballotVOs.get(0);
						if(objs[6] != null){
							classId = objs[6].toString();
						}
					}
					model.addAttribute("ballotVOs", ballotVOs);
					//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
					String groupNumber = "";
					if(questionDates!=null) {					
						group = Group.findByAnsweringDateInHouseType(answeringDate, houseType);
						groupNumber = group.getNumber().toString();
					}
					//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
					String ballotUserName = this.getCurrentUser().getActualUsername();
					CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_BALLOT_NOTIFICATION_DISABLED", "");
					if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
							|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
						NotificationController.sendBallotCreationNotification(deviceType, houseType, answeringDate, groupNumber, ballotUserName, locale.toString());
					}					
					retVal = "ballot/halfhour_member_ballot";
				}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){

					/*List<DeviceBallotVO> ballotVOs = Ballot.findHDSBallotVO(session, deviceType, answeringDate, locale.toString());
					StringBuilder voIds = new StringBuilder();
					for(int i = 0; i < ballotVOs.size(); i++){
						voIds.append(ballotVOs.get(i).getId().toString()+";"+ballotVOs.get(i).getSelected());
						if(i < (ballotVOs.size() - 1)){
							voIds.append("#");
						}
					}
					model.addAttribute("ids",voIds.toString());*/

					Map<String, String[]> parametersMap = new HashMap<String, String[]>();
					parametersMap.put("locale", new String[]{locale.toString()});
					parametersMap.put("sessionId", new String[]{session.getId().toString()});
					parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
					parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});

					List ballotVOs = org.mkcl.els.domain.Query.findReport("HDS_ASSEMBLY_BALLOT_VIEW", parametersMap);
					parametersMap = null;
					List<MasterVO> serialNumber = new ArrayList<MasterVO>(ballotVOs.size());
					for(int i = 0; i < ballotVOs.size(); i++){
						serialNumber.add(new MasterVO((i + 1), FormaterUtil.formatNumberNoGrouping((i + 1), locale.toString())));
					}
					model.addAttribute("serialnumber", serialNumber);
					model.addAttribute("ballotVOs", ballotVOs);
					
					if(ballotVOs != null && !ballotVOs.isEmpty()){
						Object[] objs = (Object[])ballotVOs.get(0);
						if(objs[1] != null){
							classId = objs[1].toString();
						}
					}
					
					serialNumber = null;
					//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
					String groupNumber = "";
					if(questionDates!=null) {					
						group = Group.findByAnsweringDateInHouseType(answeringDate, houseType);
						groupNumber = group.getNumber().toString();
					}
					String ballotUserName = this.getCurrentUser().getActualUsername();
					CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_BALLOT_NOTIFICATION_DISABLED", "");
					if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
							|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
						NotificationController.sendBallotCreationNotification(deviceType, houseType, answeringDate, groupNumber, ballotUserName, locale.toString());
					}
					retVal = "ballot/hds_membersubjectcombo_ballot";
				}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
					if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
						/*List<ResolutionBallotVO> ballotVOs = Ballot.findResolutionMemberSubjectBallotVO(session, deviceType, answeringDate, locale.toString());
						StringBuilder voIds = new StringBuilder();
						for(int i = 0; i < ballotVOs.size(); i++){
							voIds.append(ballotVOs.get(i).getId().toString()+";"+ballotVOs.get(i).getChecked());
							if(i < (ballotVOs.size() - 1)){
								voIds.append("#");
							}
						}
						model.addAttribute("ids",voIds.toString());
						model.addAttribute("ballotVOs", ballotVOs);*/
						Map<String, String[]> parametersMap = new HashMap<String, String[]>();
						parametersMap.put("locale", new String[]{locale.toString()});
						parametersMap.put("sessionId", new String[]{session.getId().toString()});
						parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
						parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});

						List report = org.mkcl.els.domain.Query.findReport("RESOLUTION_NONOFFICIAL_ASSEMBLY_BALLOT_VIEW", parametersMap);
						List<MasterVO> serialNumber = new ArrayList<MasterVO>(report.size());
						parametersMap = null;				

						
						
						StringBuilder voIds = new StringBuilder();
						for(int i = 0; i < report.size(); i++){
							serialNumber.add(new MasterVO((i + 1), FormaterUtil.formatNumberNoGrouping((i + 1), locale.toString())));
							Object[] obj = (Object[])report.get(i);
							voIds.append(obj[0].toString()+";"+obj[6].toString());
							if(i < (report.size() - 1)){
								voIds.append("#");
							}

						}

						model.addAttribute("serialnumber", serialNumber);

						model.addAttribute("ids",voIds.toString());
						model.addAttribute("ballotVOs", report);
						//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
						String groupNumber = "";
						if(questionDates!=null) {					
							group = Group.findByAnsweringDateInHouseType(answeringDate, houseType);
							groupNumber = group.getNumber().toString();
						}
						String ballotUserName = this.getCurrentUser().getActualUsername();
						CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_BALLOT_NOTIFICATION_DISABLED", "");
						if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
								|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
							NotificationController.sendBallotCreationNotification(deviceType, houseType, answeringDate, groupNumber, ballotUserName, locale.toString());
						}
						retVal = "ballot/nonofficial_membersubjectcombo_ballot";
					}
				}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
					List<BillBallotVO> ballotVOs = Ballot.findBillMemberSubjectBallotVO(session, deviceType, answeringDate, locale.toString());
					StringBuilder voIds = new StringBuilder();
					for(int i = 0; i < ballotVOs.size(); i++){
						voIds.append(ballotVOs.get(i).getId().toString()+";"+ballotVOs.get(i).getChecked());
						if(i < (ballotVOs.size() - 1)){
							voIds.append("#");
						}
					}
					model.addAttribute("ids",voIds.toString());
					model.addAttribute("ballotVOs", ballotVOs);

					//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
					String groupNumber = "";
					if(questionDates!=null) {					
						group = Group.findByAnsweringDateInHouseType(answeringDate, houseType);
						groupNumber = group.getNumber().toString();
					}
					String ballotUserName = this.getCurrentUser().getActualUsername();
					CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_BALLOT_NOTIFICATION_DISABLED", "");
					if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
							|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
						NotificationController.sendBallotCreationNotification(deviceType, houseType, answeringDate, groupNumber, ballotUserName, locale.toString());
					}
					retVal = "ballot/nonofficial_bill_membersubjectcombo_ballot";
				}
			}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					/*List<BallotVO> ballotVOs = 
						Ballot.findBallotedVO(session, deviceType, answeringDate, locale.toString());
					model.addAttribute("ballotVOs", ballotVOs);*/

					Map<String, String[]> parametersMap = new HashMap<String, String[]>();
					parametersMap.put("locale", new String[]{locale.toString()});
					parametersMap.put("sessionId", new String[]{session.getId().toString()});
					parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
					parametersMap.put("groupId", new String[]{group.getId().toString()});
					parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});

					List ballotVOs = org.mkcl.els.domain.Query.findReport("HDQ_COUNCIL_BALLOT_VIEW", parametersMap);
					parametersMap = null;

					if(ballotVOs != null && !ballotVOs.isEmpty()){
						Object[] objs = (Object[])ballotVOs.get(0);
						if(objs[8] != null){
							classId = objs[8].toString();
						}
					}
					
					model.addAttribute("ballotVOs", ballotVOs);

					//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
					String groupNumber = "";
					if(questionDates!=null) {					
						group = Group.findByAnsweringDateInHouseType(answeringDate, houseType);
						groupNumber = group.getNumber().toString();
					}
					String ballotUserName = this.getCurrentUser().getActualUsername();
					CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_BALLOT_NOTIFICATION_DISABLED", "");
					if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
							|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
						NotificationController.sendBallotCreationNotification(deviceType, houseType, answeringDate, groupNumber, ballotUserName, locale.toString());
					}
					retVal = "ballot/halfhour_ballot";
				}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
					/*List<BallotMemberVO> ballotVOs = Ballot.findBallotedMemberVO(session, deviceType, answeringDate, locale.toString());
					model.addAttribute("ballotVOs", ballotVOs);
					retVal = "ballot/nonofficial_member_ballot";*/

					Map<String, String[]> parametersMap = new HashMap<String, String[]>();
					parametersMap.put("locale", new String[]{locale.toString()});
					parametersMap.put("sessionId", new String[]{session.getId().toString()});
					parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
					parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});

					List ballotVOs = org.mkcl.els.domain.Query.findReport("HDS_COUNCIL_BALLOT_VIEW", parametersMap);
					parametersMap = null;
					if(ballotVOs != null && !ballotVOs.isEmpty()){
						Object[] objs = (Object[])ballotVOs.get(0);
						if(objs[8] != null){
							classId = objs[8].toString();
						}
					}
					model.addAttribute("ballotVOs", ballotVOs);

					//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
					String groupNumber = "";
					if(questionDates!=null) {					
						group = Group.findByAnsweringDateInHouseType(answeringDate, houseType);
						groupNumber = group.getNumber().toString();
					}
					String ballotUserName = this.getCurrentUser().getActualUsername();
					CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_BALLOT_NOTIFICATION_DISABLED", "");
					if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
							|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
						NotificationController.sendBallotCreationNotification(deviceType, houseType, answeringDate, groupNumber, ballotUserName, locale.toString());
					}
					retVal = "ballot/halfhour_ballot";

				}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){					
					List<BallotMemberVO> ballotVOs = Ballot.findBallotedMemberVO(session, deviceType, answeringDate, locale.toString());
					model.addAttribute("ballotVOs", ballotVOs);
					//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
					String groupNumber = "";
					if(questionDates!=null) {					
						group = Group.findByAnsweringDateInHouseType(answeringDate, houseType);
						groupNumber = group.getNumber().toString();
					}
					String ballotUserName = this.getCurrentUser().getActualUsername();
					CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_BALLOT_NOTIFICATION_DISABLED", "");
					if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
							|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
						NotificationController.sendBallotCreationNotification(deviceType, houseType, answeringDate, groupNumber, ballotUserName, locale.toString());
					}
					retVal = "ballot/nonofficial_member_ballot";
				}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
					List<BillBallotVO> ballotVOs = Ballot.findBillMemberSubjectBallotVO(session, deviceType, answeringDate, locale.toString());
					StringBuilder voIds = new StringBuilder();
					for(int i = 0; i < ballotVOs.size(); i++){
						voIds.append(ballotVOs.get(i).getId().toString()+";"+ballotVOs.get(i).getChecked());
						if(i < (ballotVOs.size() - 1)){
							voIds.append("#");
						}
					}
					model.addAttribute("ids",voIds.toString());
					model.addAttribute("ballotVOs", ballotVOs);

					//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
					String groupNumber = "";
					if(questionDates!=null) {					
						group = Group.findByAnsweringDateInHouseType(answeringDate, houseType);
						groupNumber = group.getNumber().toString();
					}
					String ballotUserName = this.getCurrentUser().getActualUsername();
					CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_BALLOT_NOTIFICATION_DISABLED", "");
					if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
							|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
						NotificationController.sendBallotCreationNotification(deviceType, houseType, answeringDate, groupNumber, ballotUserName, locale.toString());
					}
					retVal = "ballot/nonofficial_bill_membersubjectcombo_ballot";
				}
			}
			
			/** Processing mode specific Ballot views */
			PROCESSING_MODE processingMode = Question.getProcessingMode(session);
			// If retVal is not already set in the above conditional execution, only then use the following logic.
			if(retVal.equals("ballot/error") && processingMode == PROCESSING_MODE.LOWERHOUSE) {
				Map<String, String[]> parametersMap = new HashMap<String, String[]>();
				parametersMap.put("locale", new String[]{locale.toString()});
				parametersMap.put("sessionId", new String[]{session.getId().toString()});
				parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parametersMap.put("groupId", new String[]{group.getId().toString()});
				parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});
				List ballotVOs = org.mkcl.els.domain.Query.findReport("STARRED_BALLOT_VIEW", parametersMap);
				parametersMap = null;
				
				/**** Log the details of the viewer ****/					
				if(ballotVOs != null && !ballotVOs.isEmpty()){
					Object[] objs = (Object[])ballotVOs.get(0);
					if(objs[4] != null){
						classId = objs[4].toString();
					}
				}
				/**** Log the details of the viewer ****/
				
				model.addAttribute("ballotVOs", ballotVOs);
				
				/** Add number of rounds to model */
				String houseTypeType = houseType.getType();
				String upperCaseHouseTypeType = houseTypeType.toUpperCase();
				
				StringBuffer sb = new StringBuffer();
				sb.append("QUESTION_STARRED_BALLOT_NO_OF_ROUNDS_");
				sb.append(upperCaseHouseTypeType);
				
				String parameterName = sb.toString();
				CustomParameter noOfRoundsParameter = CustomParameter.findByName(CustomParameter.class, parameterName, "");
				if(noOfRoundsParameter!=null && noOfRoundsParameter.getValue()!=null && !noOfRoundsParameter.getValue().isEmpty()) {
					model.addAttribute("noOfRounds", noOfRoundsParameter.getValue());
				} else {
					model.addAttribute("noOfRounds", ApplicationConstants.OUESTION_BALLOT_NO_OF_ROUNDS);
				}
				
				// Show the ballot date instead of the current date
				String strBallotDate = null;
				if(ballotVOs != null && ! ballotVOs.isEmpty()) {
					Object[] objs = (Object[])ballotVOs.get(0);
					if(objs[5] != null) {
						strBallotDate = objs[5].toString();
					}
				}
				CustomParameter serverDateTimeFormat = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
				if(serverDateTimeFormat != null){
					Date ballotDate = new Date();
					if(strBallotDate != null) {
						CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
								"DB_DATETIMEFORMAT", "");
						ballotDate = FormaterUtil.formatStringToDate(strBallotDate, datePattern.getValue(), locale.toString());
					}
					
					model.addAttribute("formattedCurrentDate", FormaterUtil.formatDateToString(ballotDate, serverDateTimeFormat.getValue(), locale.toString()));
				}
				
				// Set total members & total questions
				if(ballotVOs != null && !ballotVOs.isEmpty()){
					model.addAttribute("totalMembers", FormaterUtil.formatNumberNoGrouping(Integer.valueOf((((Object[])ballotVOs.get(0))[3]).toString()), locale.toString()));
					
					int size = ballotVOs.size();
					model.addAttribute("totalNoOfQuestions", FormaterUtil.formatNumberNoGrouping(size, locale.toString()));
				}
				
				Group qnGroup = Group.find(session, answeringDate, locale.toString());
				model.addAttribute("groupNo", 
						FormaterUtil.formatNumberNoGrouping(qnGroup.getNumber(), locale.toString()));
				
				//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
				String groupNumber = "";
				if(questionDates!=null) {					
					group = Group.findByAnsweringDateInHouseType(answeringDate, houseType);
					groupNumber = group.getNumber().toString();
				}
				String ballotUserName = this.getCurrentUser().getActualUsername();
				CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_BALLOT_NOTIFICATION_DISABLED", "");
				if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
						|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
					NotificationController.sendBallotCreationNotification(deviceType, houseType, answeringDate, groupNumber, ballotUserName, locale.toString());
				}
				retVal = "ballot/ballot";
			}
			
			if(classId != null){
				logActivity(classId, request, locale.toString());
			}
			
		}
		catch(Exception e) {
			logger.error("error", e);
			model.addAttribute("type", "INSUFFICIENT_PARAMETERS_FOR_VIEWING_BALLOT");
			retVal = "ballot/error";
		}
		return retVal;
	}
	
	@RequestMapping(value = "/viewlog", method = RequestMethod.GET)
	public String viewBallotViewLig(Model model, HttpServletRequest request, Locale locale){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String retVal = "ballot/error";
		
		try{			
			String strHouseType = request.getParameter("houseType");
			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			String strQuestionType = request.getParameter("questionType");
			String strGroup = request.getParameter("group");
			String strStatus = request.getParameter("status");
			String strRole = request.getParameter("role"); 
			String strAnsweringDate = request.getParameter("answeringDate");
			String strCategory = request.getParameter("category");
			
			HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
			Integer sessionYear = new Integer(strSessionYear);
			SessionType sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strQuestionType));
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			Date answeringDate = null;
			
			if(deviceType != null && deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)){
				QuestionDates qdAnsweringDate = QuestionDates.findById(QuestionDates.class, new Long(strAnsweringDate));
				answeringDate = qdAnsweringDate.getAnsweringDate();
			}else{
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, ApplicationConstants.DB_DATEFORMAT); 
			}
			
			Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale.toString());
			List<ActivityLog> loggers = ActivityLog.findAllByFieldName(ActivityLog.class, "classId", ballot.getId().toString(), "id", ApplicationConstants.ASC, locale.toString());
			List<MasterVO> data = new ArrayList<MasterVO>();
			for(ActivityLog al : loggers){
				MasterVO mv = new MasterVO();
				Credential cr = al.getCredetial();
				if(cr != null){
					User user = User.findByUserName(cr.getUsername(), locale.toString());
					if(user != null){
						mv.setName(user.getTitle()+" " +user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
						mv.setValue(FormaterUtil.formatDateToString(al.getTimeOfAction(), ApplicationConstants.SERVER_DATETIMEFORMAT, locale.toString()));
					}
				}
				data.add(mv);
			}
			model.addAttribute("data", data);
			
			retVal = "ballot/showlogger";
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
	}
	
	/****** Member Ballot For Council Starred Questions-First Batch(Sandeep) ******/
	/****** Member Ballot(Council) Initial Page ****/
	@RequestMapping(value="/memberballot/init",method=RequestMethod.GET)
	public String viewMemberBallotInit(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){		
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			
			String strHouseType=request.getParameter("houseType");
			String strSessionType=request.getParameter("sessionType");
			String strSessionYear=request.getParameter("sessionYear");
			String strDeviceType=request.getParameter("questionType");
			if(strDeviceType == null){
				strDeviceType = request.getParameter("deviceType");
			}
			String strRound=request.getParameter("round");
			if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strDeviceType!=null&&strRound!=null){
				if((!strHouseType.isEmpty())
						&&(!strSessionType.isEmpty())
						&&(!strSessionYear.isEmpty())
						&&(!strDeviceType.isEmpty())
						&&(!strRound.isEmpty())){
					HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
					SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
					Integer sessionYear=Integer.parseInt(strSessionYear);
					Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
					String noOfRounds=session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT);
					if(noOfRounds!=null){
						if(!noOfRounds.isEmpty()){
							DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
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
							/**** Populating Output Formats For Report/s ****/
							List<MasterVO> outputFormats = new ArrayList<MasterVO>();
							MasterVO pdfFormat = new MasterVO();
							pdfFormat.setName("PDF");
							pdfFormat.setValue("PDF");
							outputFormats.add(pdfFormat);
							MasterVO wordFormat = new MasterVO();
							wordFormat.setName("WORD");
							wordFormat.setValue("WORD");
							outputFormats.add(wordFormat);									
							model.addAttribute("outputFormats", outputFormats);
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
	/****** Member Ballot(Council) Attendance Page ****/
	@RequestMapping(value="/memberballot/attendance",method=RequestMethod.GET)
	public String markAttendance(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
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
					/**** Lock checking is not needed ****/
					Boolean attendanceCanBeCreated=true;
					if(round!=1){
						attendanceCanBeCreated=MemberBallotAttendance.areMembersLocked(session, questionType, round-1, attendance, locale.toString());					
					}
					if(!attendanceCanBeCreated){
						model.addAttribute("type", "MEMBER_BALLOT_NOT_CREATED_FOR_PREVIOUS_ROUND");
						return errorpage;
					}				
					/**** Checking if attendance already created.Attendance is already created
					 * if there are entries in member ballot attendance for a particular round ****/
					Boolean attendanceCreatedAlready=MemberBallotAttendance.memberBallotCreated(session,questionType,round,locale.toString());
					/**** This code is appropriate in case when attendance is being created for the very
					 * first time as in case of present member attendance.so for present members
					 * position from previous rounds are preserved.But in case of absent members
					 * positions from previous rounds are not preseved as flow will never enter
					 * this block as attendance has already been created.****/
					if(!attendanceCreatedAlready){
						/**** Under Signed(Created By and Created As) ****/
						Set<Role> roles=this.getCurrentUser().getRoles();
						String createdAs="";
						String createdBy="";
						for(Role i:roles){
							if(i.getName().startsWith("QIS_")){
								model.addAttribute("role",i.getLocalizedName());
								createdAs=i.getLocalizedName();
								break;
							}
						}
						createdBy=this.getCurrentUser().getActualUsername();
						String flag=MemberBallotAttendance.createMemberBallotAttendance(session,questionType,round,createdBy,createdAs,locale.toString());
						if(!flag.contains("SUCCESS")){
							model.addAttribute("type", flag);
							return errorpage;
						}
					}
					/**** Here for attendance=false we will check if all the positions are null.
					 * if it is the case then for round=1,we will give postion to all absent members
					 * according to their last name.For other rounds we will see the position of previous round
					 * and give it to it.
					 */
					if(attendance==false&&round!=1){
						MemberBallotAttendance.updatePositionAbsentMembers(session,questionType,round,false,locale.toString());
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
					model.addAttribute("attendance",attendance);
					/**** Controlling re-ordering of members ****/
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"MEMBERBALLOT_REORDERING_PATTERN", "");
					if(customParameter!=null){
						model.addAttribute("reorderingPattern",customParameter.getValue());
					}else{
						model.addAttribute("type", "MEMBERBALLOT_REORDERING_PATTERN_NOTSET");
						return errorpage;
					}
					/**** In case of absent members reordering should be allowed as discussed with BA due to
					 * PAs of important members asking for re-ordering.
					 */
					if(round!=1&&attendance==true){
						List<Member> oldMembers=MemberBallotAttendance.findOldMembers(session, questionType, attendance, round, locale.toString());
						StringBuffer buffer=new StringBuffer();
						for(Member i:oldMembers){
							buffer.append(i.findFirstLastName()+",");
						}
						buffer.deleteCharAt(buffer.length()-1);
						model.addAttribute("oldMembers",buffer.toString());
					}else{
						model.addAttribute("oldMembers","-");
					}
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
		}catch (ELSException e) {
			logger.error("failed",e);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}catch(Exception e){
			logger.error("failed",e);
			model.addAttribute("type","GEN_EXCEPTION");
			return errorpage;
		}
		return "ballot/memberballotattendance";	
	}
	/****** Member Ballot(Council) Attendance Update ****/
	@Transactional
	@RequestMapping(value="/memberballot/attendance",method=RequestMethod.PUT)
	public @ResponseBody String updateAttendance(final HttpServletRequest request,final ModelMap model,final Locale locale){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
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
				/**** Lock checking is not needed ****/
				Boolean attendanceCanBeCreated=true;
				if(round!=1){
					attendanceCanBeCreated=MemberBallotAttendance.areMembersLocked(session, questionType, round-1, attendance, locale.toString());					
				}
				if(!attendanceCanBeCreated){
					return "ballotnotcreated";
				}	
				if(attendance==false&&round==1){
					int presentcount=MemberBallotAttendance.findMembersByAttendanceCount(session, questionType,true, round, locale.toString());
					if(presentcount==0){
						return "ballotnotcreated";
					}
					int presentMemberBallotEntries=MemberBallot.findEntryCount(session,questionType, round, true, locale.toString());
					if(presentMemberBallotEntries==0){
						return "ballotnotcreated";
					}
					String totalRounds=request.getParameter("totalrounds");
					if(totalRounds!=null&&!totalRounds.isEmpty()){
						for(int i=2;i<=Integer.parseInt(totalRounds);i++){
							attendanceCanBeCreated=MemberBallotAttendance.areMembersLocked(session, questionType, round, true, locale.toString());					
							if(!attendanceCanBeCreated){
								return "ballotnotcreated";
							}	
						}
					}					
				}
				/**** For present members initialize position to 0 ****/
				/**** For absent members initialize position to the no of present members in particular 
				 * round.
				 */
				int position=0;
				if(attendance==false){
					position=MemberBallotAttendance.findMembersByAttendanceCount(session, questionType, true, round, locale.toString());
				}				
				/**** Under Signed(Created By and Created As) ****/
				Set<Role> roles=this.getCurrentUser().getRoles();
				String createdAs="";
				String createdBy="";
				for(Role i:roles){
					if(i.getName().startsWith("QIS_")){
						model.addAttribute("role",i.getLocalizedName());
						createdAs=i.getLocalizedName();
						break;
					}
				}
				createdBy=this.getCurrentUser().getActualUsername();
				Date date=new Date();
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
						memberBallotAttendance.setEditedAs(createdAs);
						memberBallotAttendance.setEditedBy(createdBy);
						memberBallotAttendance.setEditedOn(date);
						if(attendance==false){
							memberBallotAttendance.setPositionDiscontious(false);
						}
						memberBallotAttendance.merge();						
					}
				}
				/**** setting attendance of not selected items ****/
				List<MemberBallotAttendance> memberBallotAttendances=MemberBallotAttendance.findAll(session, questionType, "",round, "position", locale.toString());
				for(MemberBallotAttendance i:memberBallotAttendances){
					if(!selectedItems.contains(String.valueOf(i.getId()))){
						i.setAttendance(!attendance);
						i.setEditedAs(createdAs);
						i.setEditedBy(createdBy);
						i.setEditedOn(date);
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
	/****** Member Ballot(Council) Pre Ballot report Page ****/
	@RequestMapping(value="/memberballot/preballot",method=RequestMethod.GET)
	public String councilStarredPreBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try {
			String strAttendance=request.getParameter("attendance");
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			String strRound=request.getParameter("round");
			Session session=Session.findById(Session.class,Long.parseLong(strSession));
			DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
			Boolean attendance=Boolean.parseBoolean(strAttendance);
			Integer round=Integer.parseInt(strRound);
			/**** Lock checking is not needed ****/
			Boolean attendanceCanBeCreated=true;
			if(round!=1){
				attendanceCanBeCreated=MemberBallotAttendance.areMembersLocked(session, questionType, round-1, attendance, locale.toString());					
			}
			if(!attendanceCanBeCreated){
				model.addAttribute("type", "MEMBER_BALLOT_NOT_CREATED_FOR_PREVIOUS_ROUND");
				return "ballot/error";
			}	
			/**** Before Pre-Ballot we must first check if atleast once submit button has been 
			 * clicked or not.Because if submit button is not clicked for a particular round and
			 * attendance type then position is not preserved and position will be null in that case.
			 */
			if(attendance){
				int memberBallotAttendanceCount=MemberBallotAttendance.findMembersByAttendanceCount(session,questionType,attendance,round,locale.toString());
				if(memberBallotAttendanceCount == 0){
					model.addAttribute("type","SUBMIT_BUTTON_NOT_CLICKED");
					return "ballot/error";
				}
			}else{
				/**** If positions are null ****/
				int noOfPositionsNull=MemberBallotAttendance.checkPositionForNullValues(session,questionType,"false",round,"position",locale.toString());
				if(noOfPositionsNull>0){
					model.addAttribute("type","SUBMIT_BUTTON_NOT_CLICKED");
					return "ballot/error";
				}
				/**** if positions are discontinous ****/
				//				Boolean positionsDiscontinous=MemberBallotAttendance.checkPositionDiscontinous(session,questionType,false,round,locale.toString());
				//				if(positionsDiscontinous){
				//					model.addAttribute("type","SUBMIT_BUTTON_NOT_CLICKED");
				//					return "ballot/error";
				//				}				
			}	

			List<MemberBallotAttendance> memberBallotAttendances=null;
			if(attendance){
				memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"true",round,"position",locale.toString());
			}else{
				memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"false",round,"position",locale.toString());
			}
			model.addAttribute("selectedItems",memberBallotAttendances);
			model.addAttribute("attendance",attendance);	
			model.addAttribute("round",round);
			/**** Current Date ****/
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"SERVER_DATEFORMAT_HYPHEN","");
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(), locale.toString());
				model.addAttribute("currentdate",format.format(new Date()));
			}else{
				model.addAttribute("type","SERVER_DATEFORMAT_HYPHEN_NOTSET");
				return "ballot/error";
			}
			/**** Under Signed ****/
			Set<Role> roles=this.getCurrentUser().getRoles();
			for(Role i:roles){
				if(i.getName().startsWith("QIS_")){
					model.addAttribute("role",i.getLocalizedName());
					break;
				}
			}
		} catch (Exception e) {
			logger.error("failed",e);
			model.addAttribute("type","DB_EXCEPTION");
			return "ballot/error";
		}
		return "ballot/memberballotpreballot";
	}
	/****** Member Ballot(Council) Member Ballot report Page ****/
	@Transactional
	@RequestMapping(value="/memberballot/view",method=RequestMethod.GET)
	public String createMemberBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			String strAttendance=request.getParameter("attendance");
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			String strRound=request.getParameter("round");
			String strTotalRound=request.getParameter("noofrounds");
			String strGroup=request.getParameter("group");
			String strAnsweringDate=request.getParameter("answeringDate");
			if(strSession!=null&&strQuestionType!=null
					&&strAttendance!=null&&strRound!=null&&strTotalRound!=null){
				if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())
						&&(!strAttendance.isEmpty())&&(!strRound.isEmpty())
						&&(!strTotalRound.isEmpty())){
					/**** Current Date ****/
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"SERVER_DATEFORMAT_HYPHEN","");
					if(customParameter!=null){
						SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(), locale.toString());
						model.addAttribute("currentdate",format.format(new Date()));
					}else{
						model.addAttribute("type","SERVER_DATEFORMAT_HYPHEN_NOTSET");
						return "ballot/error";
					}
					Session session=Session.findById(Session.class,Long.parseLong(strSession));
					DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
					Boolean attendance=Boolean.parseBoolean(strAttendance);
					Integer round=Integer.parseInt(strRound);
					Integer totalRounds=Integer.parseInt(strTotalRound);
					/*Member Ballot can be created for present members only when:
					Round 1:round=1,attendance=present,attendance link is clicked,
					if needed order is set and save button is clicked(must-as for round 1 
					member ballot attendance entries are initially all set to attendance=false,
					position=null)

					For Round 2,3,4,5:round=2,3,4,5,attendance=present,attendance link is clicked
					(save link needs to be clicked only if order is changed-member ballot entries 
					contain n entries from previous round whose attendance=true
					and position is same as that from previous round.These entries positions 
					cannot be changed.Only newly added present members positions can be changed).
					 */
					if(attendance){
						int memberBallotAttendanceCount=MemberBallotAttendance.findMembersByAttendanceCount(session,questionType,attendance,round,locale.toString());
						if(memberBallotAttendanceCount == 0){
							model.addAttribute("type","SUBMIT_BUTTON_NOT_CLICKED");
							return "ballot/error";
						}
					}else{
						/**** If positions are null ****/
						int noOfPositionsNull=MemberBallotAttendance.checkPositionForNullValues(session,questionType,"false",round,"position",locale.toString());
						if(noOfPositionsNull>0){
							model.addAttribute("type","SUBMIT_BUTTON_NOT_CLICKED");
							return "ballot/error";
						}
						/**** if positions are discontinous ****/
						//						Boolean positionsDiscontinous=MemberBallotAttendance.checkPositionDiscontinous(session,questionType,false,round,locale.toString());
						//						if(positionsDiscontinous){
						//							model.addAttribute("type","SUBMIT_BUTTON_NOT_CLICKED");
						//							return "ballot/error";
						//						}				
					}						
					/**** Under Signed(Created By and Created As) ****/
					Set<Role> roles=this.getCurrentUser().getRoles();
					String createdAs="";
					String createdBy="";
					for(Role i:roles){
						if(i.getName().startsWith("QIS_")){
							model.addAttribute("role",i.getLocalizedName());
							createdAs=i.getLocalizedName();
							break;
						}
					}
					createdBy=this.getCurrentUser().getActualUsername();
					String flag=MemberBallot.createMemberBallot(session,questionType,attendance,round,createdBy,createdAs,locale.toString(),totalRounds);
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
						model.addAttribute("attendance",attendance);
						model.addAttribute("round",round);
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
	@RequestMapping(value="/memberballot/memberballotstatus",method=RequestMethod.GET)
	public String viewMemberBallotStatus(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			String strNoOfRounds=request.getParameter("noofrounds");
			List<Reference> presentMemberBallot=new ArrayList<Reference>();
			List<Reference> absentMemberBallot=new ArrayList<Reference>();
			if(strQuestionType!=null&&!strQuestionType.isEmpty()
					&&strNoOfRounds!=null&&!strNoOfRounds.isEmpty()
					&&strSession!=null&&!strSession.isEmpty()){
				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
				Integer noOfRounds=Integer.parseInt(strNoOfRounds);
				for(int i=1;i<=noOfRounds;i++){
					int count=MemberBallot.findEntryCount(session,questionType,i,true,locale.toString());
					Reference reference=new Reference();
					reference.setId(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
					reference.setNumber(String.valueOf(i));
					if(count>0){						
						reference.setName("COMPLETE");
					}else{
						reference.setName("INCOMPLETE");
					}
					presentMemberBallot.add(reference);
				}
				for(int i=1;i<=noOfRounds;i++){
					int count=MemberBallot.findEntryCount(session,questionType,i,false,locale.toString());
					Reference reference=new Reference();
					reference.setId(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
					reference.setNumber(String.valueOf(i));
					if(count>0){						
						reference.setName("COMPLETE");
					}else{
						reference.setName("INCOMPLETE");
					}
					absentMemberBallot.add(reference);
				}
				model.addAttribute("presentBallot",presentMemberBallot);
				model.addAttribute("absentBallot",absentMemberBallot);
			}
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "ballot/memberballotstatus";
	}

	@RequestMapping(value="/memberballot/memberballotstatusround",method=RequestMethod.GET)
	public @ResponseBody String viewMemberBallotStatusOfRound(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		String status="INCOMPLETE";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			String strRound=request.getParameter("round");
			String strAttendance=request.getParameter("attendance");
			if(strQuestionType!=null&&!strQuestionType.isEmpty()
					&&strRound!=null&&!strRound.isEmpty()
					&&strSession!=null&&!strSession.isEmpty()
					&&strAttendance!=null&&!strAttendance.isEmpty()){
				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
				Integer round=Integer.parseInt(strRound);
				Boolean attendance=Boolean.parseBoolean(strAttendance);
				/**** Lock checking is not needed ****/
				Boolean attendanceCanBeCreated=true;
				if(round!=1){
					attendanceCanBeCreated=MemberBallotAttendance.areMembersLocked(session, questionType, round-1, attendance, locale.toString());					
				}
				if(!attendanceCanBeCreated){
					return "COMPLETE";
				}	
				if(attendance){
					int memberBallotAttendanceCount=MemberBallotAttendance.findMembersByAttendanceCount(session,questionType,attendance,round,locale.toString());
					if(memberBallotAttendanceCount == 0){
						return "COMPLETE";
					}
				}else{
					/**** If positions are null ****/
					int noOfPositionsNull=MemberBallotAttendance.checkPositionForNullValues(session,questionType,"false",round,"position",locale.toString());
					if(noOfPositionsNull>0){
						return "COMPLETE";
					}
					/**** if positions are discontinous ****/
					//					Boolean positionsDiscontinous=MemberBallotAttendance.checkPositionDiscontinous(session,questionType,false,round,locale.toString());
					//					if(positionsDiscontinous){
					//					return "COMPLETE";
					//					}				
				}					
				int count=MemberBallot.findEntryCount(session,questionType,round,attendance,locale.toString());
				if(count>0){
					return "COMPLETE";
				}
			}
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return "FAILURE";
		}
		return status;
	}
	/****** Member Ballot(Council) Member Wise Question Report Initial Page ****/
	@RequestMapping(value="/memberballot/memberwise",method=RequestMethod.GET)
	public String viewMemberWiseReport(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";

		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			if(strSession!=null&&strQuestionType!=null){
				if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())){
					Session session=Session.findById(Session.class,Long.parseLong(strSession));
					DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
					/**** List of Distinct Members who had submitted questions in First batch ****/
					List<Member> eligibleMembers=Question.findMembersHavingQuestionSubmittedInFirstBatch(session, questionType, locale.toString());
					model.addAttribute("eligibleMembers", eligibleMembers);
					List<MasterVO> eligibleMemberCounts = new ArrayList<MasterVO>();
					if(eligibleMembers!=null && !eligibleMembers.isEmpty()) {
						for(int i=1; i<=eligibleMembers.size(); i++) {
							MasterVO eligibleMemberCount = new MasterVO();
							eligibleMemberCount.setNumber(i);
							eligibleMemberCount.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
							eligibleMemberCounts.add(eligibleMemberCount);
						}
					}
					model.addAttribute("eligibleMemberCounts", eligibleMemberCounts);
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
	/****** Member Ballot(Council) Member Wise Question Report Page ****/
	@RequestMapping(value="/memberballot/member/questions",method=RequestMethod.GET)
	public String viewMemberQuestionsReport(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
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
					MemberBallotMemberWiseReportVO reports = MemberBallot.findMemberWiseReportVO(session, questionType, member, locale.toString());
					List<MemberBallotMemberWiseQuestionVO> newMemberBallotMemberWiseQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>();
					List<MemberBallotMemberWiseQuestionVO> memberBallotMemberWiseQuestionVOs = reports.getMemberBallotMemberWiseQuestionVOs();
					List<MemberBallotMemberWiseQuestionVO> starredQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>();
					List<MemberBallotMemberWiseQuestionVO> unstarredQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>();
					List<MemberBallotMemberWiseQuestionVO> clarificationQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>();
					List<MemberBallotMemberWiseQuestionVO> rejectedQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>(); 
					MemberBallotMemberWiseReportVO memberBallotMemberWiseReportVO = new MemberBallotMemberWiseReportVO();
					for(MemberBallotMemberWiseQuestionVO i : memberBallotMemberWiseQuestionVOs){
						if(i.getCurrentDeviceType().equals(ApplicationConstants.STARRED_QUESTION) 
								&& (i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION) || i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION))) {
							starredQuestionVOs.add(i);											
						} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
								|| i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT)) {
							unstarredQuestionVOs.add(i);											
						} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
								||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
								||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
								||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_GOVT)
								||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_GOVT)
								||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
								||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)) {
							clarificationQuestionVOs.add(i);											
						} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)
								|| i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_REJECTION)) {
							rejectedQuestionVOs.add(i);											
						}
					}
					
					List<Group> groups=Group.findByHouseTypeSessionTypeYear(session.getHouse().getType(),session.getType(),session.getYear());
					
					NumberFormat numberFormat = FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
					int count = 0;
					for(Group g : groups){
						String strGroupNumber = g.getNumber().toString();
						for(MemberBallotMemberWiseQuestionVO questionVO : starredQuestionVOs) {
							if(strGroupNumber.equals(questionVO.getGroupNumber())){
								String serialNo = numberFormat.format(++count);
								questionVO.setSno(serialNo);
							}
						}
						
						for(MemberBallotMemberWiseQuestionVO questionVO : unstarredQuestionVOs) {
							if(strGroupNumber.equals(questionVO.getGroupNumber())){
								String serialNo = numberFormat.format(++count);
								questionVO.setSno(serialNo);
							}
						}
						
						for(MemberBallotMemberWiseQuestionVO questionVO : rejectedQuestionVOs) {
							if(strGroupNumber.equals(questionVO.getGroupNumber())){
								String serialNo = numberFormat.format(++count);
								questionVO.setSno(serialNo);
							}
						}
						
						for(MemberBallotMemberWiseQuestionVO questionVO : clarificationQuestionVOs) {
							if(strGroupNumber.equals(questionVO.getGroupNumber())){
								String serialNo = numberFormat.format(++count);
								questionVO.setSno(serialNo);
							}
						}
					}
					
					
					newMemberBallotMemberWiseQuestionVOs.addAll(starredQuestionVOs);
					newMemberBallotMemberWiseQuestionVOs.addAll(unstarredQuestionVOs);
					newMemberBallotMemberWiseQuestionVOs.addAll(rejectedQuestionVOs);
					newMemberBallotMemberWiseQuestionVOs.addAll(clarificationQuestionVOs);
					
					memberBallotMemberWiseReportVO.setMemberBallotMemberWiseQuestionVOs(newMemberBallotMemberWiseQuestionVOs);
					memberBallotMemberWiseReportVO.setMemberBallotMemberWiseCountVOs(reports.getMemberBallotMemberWiseCountVOs());
					memberBallotMemberWiseReportVO.setMember(reports.getMember());
					/**** Populating Group and corresponding Ministries in Model ****/
					
					model.addAttribute("groups",groups);
					model.addAttribute("report",memberBallotMemberWiseReportVO);
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
	/****** Member Ballot(Council) Member Wise Question Report Page ****/
	@RequestMapping(value="/memberballot/member/questionsreport",method=RequestMethod.GET)
	public @ResponseBody void generateMemberQuestionsReportUsingFOP(final HttpServletRequest request,final HttpServletResponse response, final ModelMap model,final Locale locale){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String strSession = request.getParameter("session");
		String strQuestionType = request.getParameter("questionType");
		String strMember = request.getParameter("member");
		String outputFormat = request.getParameter("outputFormat");

		if(strSession!=null&&strQuestionType!=null&&strMember!=null&&outputFormat!=null){
			if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())
					&&(!strMember.isEmpty())&&(!outputFormat.isEmpty())){
				try {
					Session session=Session.findById(Session.class,Long.parseLong(strSession));
					DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
					Member member=Member.findById(Member.class,Long.parseLong(strMember));
					if(session!=null&&questionType!=null&&member!=null) {
						MemberwiseQuestionsXmlVO memberwiseQuestionsXmlVO = new MemberwiseQuestionsXmlVO();
						/**** First Batch Questions Final Status Distribution member wise ****/
						MemberBallotMemberWiseReportVO memberBallotMemberWiseReportVO=MemberBallot.findMemberWiseReportVO(session, questionType, member, locale.toString());
						//memberwiseQuestionsXmlVO.setMemberBallotMemberWiseReportVO(memberBallotMemberWiseReportVO);
						memberwiseQuestionsXmlVO.setMember(memberBallotMemberWiseReportVO.getMember());
						String houseType = session.findHouseType();
						if(houseType==null) {
							houseType = "";
						}
						memberwiseQuestionsXmlVO.setHouseType(houseType);
						System.out.println(memberwiseQuestionsXmlVO.getHouseType());
						/** question submission date formatting **/
						String startDateParameter=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME);
						if(startDateParameter!=null){
							Date startDate = FormaterUtil.formatStringToDate(startDateParameter, ApplicationConstants.DB_DATETIME_FORMAT);
							SimpleDateFormat dbFormat = null;
							CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
							if(dbDateFormat!=null){
								dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
							}
							String[] strQuestionSubmissionDate=dbFormat.format(startDate).split(",");
							String[] strAnsweringMonth=strQuestionSubmissionDate[1].split(" ");
							String answeringMonth=FormaterUtil.getMonthInLocaleLanguage(strAnsweringMonth[1], locale.toString());
							MessageResource mrDate = MessageResource.findByFieldName(MessageResource.class, "code", "generic.date", locale.toString());
							String genericDateLabel  = (mrDate!=null)? mrDate.getValue():"";
							model.addAttribute("questionSubmissionDate",genericDateLabel + " " +strAnsweringMonth[0]+" "+ answeringMonth +","+strQuestionSubmissionDate[2]);
							memberwiseQuestionsXmlVO.setSubmissionDate(genericDateLabel + " " +strAnsweringMonth[0]+" "+ answeringMonth +","+strQuestionSubmissionDate[2]);
						}												
						memberwiseQuestionsXmlVO.setMemberBallotMemberWiseCountVOs(memberBallotMemberWiseReportVO.getMemberBallotMemberWiseCountVOs());
						memberwiseQuestionsXmlVO.setMemberBallotMemberWiseQuestionVOs(memberBallotMemberWiseReportVO.getMemberBallotMemberWiseQuestionVOs());
						Integer clarificationCount = 0;
						Integer admittedCount = 0;
						Integer convertedToUnstarredAndAdmittedCount = 0;
						Integer rejectedCount = 0;
						if(memberBallotMemberWiseReportVO!=null) {
							for(MemberBallotMemberWiseCountVO i: memberBallotMemberWiseReportVO.getMemberBallotMemberWiseCountVOs()) {
								if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_GOVT)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_GOVT)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)) {

									clarificationCount += Integer.parseInt(i.getCount());
								} else if(i.getCurrentDeviceType().equals(ApplicationConstants.STARRED_QUESTION) 
										&& (i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION) || i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION))) {
									admittedCount = Integer.parseInt(i.getCount());
								} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
										|| i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT)) {
									convertedToUnstarredAndAdmittedCount = Integer.parseInt(i.getCount());
								} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)
										|| i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_REJECTION)) {
									rejectedCount = Integer.parseInt(i.getCount());
								}
							}
						}
						memberwiseQuestionsXmlVO.setAdmittedQuestionCount(FormaterUtil.formatNumberNoGrouping(admittedCount, locale.toString()));
						memberwiseQuestionsXmlVO.setConvertedToUnstarredAndAdmittedQuestionCount(FormaterUtil.formatNumberNoGrouping(convertedToUnstarredAndAdmittedCount, locale.toString()));
						memberwiseQuestionsXmlVO.setRejectedQuestionCount(FormaterUtil.formatNumberNoGrouping(rejectedCount, locale.toString()));
						memberwiseQuestionsXmlVO.setClarificationQuestionCount(FormaterUtil.formatNumberNoGrouping(clarificationCount, locale.toString()));
						/**** Populating Groups, Corresponding Ministries & Answering Dates ****/
						List<Group> groups=Group.findByHouseTypeSessionTypeYear(session.getHouse().getType(),session.getType(),session.getYear());
						List<GroupVO> groupVOs = new ArrayList<GroupVO>();						
						int count = 0;
						for(Group g: groups) {
							GroupVO groupVO = new GroupVO();
							List<MemberBallotMemberWiseQuestionVO> starredQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>();
							List<MemberBallotMemberWiseQuestionVO> unstarredQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>();
							List<MemberBallotMemberWiseQuestionVO> clarificationQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>();
							List<MemberBallotMemberWiseQuestionVO> rejectedQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>();
							groupVO.setNumber(g.getNumber());
							groupVO.setFormattedNumber(g.formatNumber());
							List<Ministry> ministries = g.findMinistriesByPriority();	
							List<MasterVO> ministryMasterVOs = new ArrayList<MasterVO>();
							int ministryCount = 1;
							for(Ministry m: ministries) {
								MasterVO ministryMasterVO = new MasterVO();
								ministryMasterVO.setFormattedNumber("(" + FormaterUtil.formatNumberNoGrouping(ministryCount, locale.toString())+ ")");
								ministryMasterVO.setName(m.getDisplayName());
								ministryMasterVOs.add(ministryMasterVO);
								ministryCount++;
							}
							groupVO.setMinistries(ministryMasterVOs);							
							groupVO.setAnsweringDates(g.findQuestionDateReferenceVOByGroup());
							boolean hasQuestionsForGivenMember = false;
							String currentGroupNumber = groupVO.getFormattedNumber();
							if(memberBallotMemberWiseReportVO!=null) {
								for(MemberBallotMemberWiseQuestionVO i: memberBallotMemberWiseReportVO.getMemberBallotMemberWiseQuestionVOs()) {
									if(i.getGroupFormattedNumber().equals(currentGroupNumber)) {
										if(i.getCurrentDeviceType().equals(ApplicationConstants.STARRED_QUESTION) 
												&& (i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION) || i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION))) {
											starredQuestionVOs.add(i);											
										} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
												|| i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT)) {
											unstarredQuestionVOs.add(i);											
										} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_GOVT)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_GOVT)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)) {
											clarificationQuestionVOs.add(i);											
										} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)
												|| i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_REJECTION)) {
											rejectedQuestionVOs.add(i);											
										}
									}
								}								
								if(!starredQuestionVOs.isEmpty() || !unstarredQuestionVOs.isEmpty()
										|| !clarificationQuestionVOs.isEmpty() || !rejectedQuestionVOs.isEmpty()) {
									hasQuestionsForGivenMember = true;
								}
								NumberFormat numberFormat = FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
								for(MemberBallotMemberWiseQuestionVO questionVO : starredQuestionVOs) {
									String serialNo = numberFormat.format(++count);
									questionVO.setSno(serialNo);
								}
								
								for(MemberBallotMemberWiseQuestionVO questionVO : unstarredQuestionVOs) {
									String serialNo = numberFormat.format(++count);
									questionVO.setSno(serialNo);
								}
								
								for(MemberBallotMemberWiseQuestionVO questionVO : rejectedQuestionVOs) {
									String serialNo = numberFormat.format(++count);
									questionVO.setSno(serialNo);
								}
								
								for(MemberBallotMemberWiseQuestionVO questionVO : clarificationQuestionVOs) {
									String serialNo = numberFormat.format(++count);
									questionVO.setSno(serialNo);
								}
								
								groupVO.setHasQuestionsForGivenMember(hasQuestionsForGivenMember);
								groupVO.setStarredQuestionVOs(starredQuestionVOs);
								groupVO.setUnstarredQuestionVOs(unstarredQuestionVOs);
								groupVO.setClarificationQuestionVOs(clarificationQuestionVOs);
								groupVO.setRejectedQuestionVOs(rejectedQuestionVOs);
							}
							groupVOs.add(groupVO);
						}
						memberwiseQuestionsXmlVO.setGroupVOs(groupVOs);
						/**** generate report ****/
						reportFile = generateReportUsingFOP(memberwiseQuestionsXmlVO, "memberballot_memberwise_questions", outputFormat, "memberballot_memberwise_questions_report", locale.toString());
						if(reportFile!=null) {
							System.out.println("Memberballot Memberwise Questions Report generated successfully in " + outputFormat + " format!");
							openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
						}
					}					
				} catch(Exception e) {
					isError = true;
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "REQUEST_PARAMETER_NULL", locale.toString());
				}
			}else{
				logger.error("**** Check request parameter 'session,questionType,member' for empty values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
				//return errorpage;
			}
		}else{
			logger.error("**** Check request parameter 'session,questionType,member' for null values ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "REQUEST_PARAMETER_NULL", locale.toString());
		}
		if(isError) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
				if(errorMessage != null) {
					if(!errorMessage.getValue().isEmpty()) {
						response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + errorMessage.getValue() + "</h3></body></html>");
					} else {
						response.getWriter().println("<h3>Some Error In Letter Generation. Please Contact Administrator.</h3>");
					}
				} else {
					response.getWriter().println("<h3>Some Error In Letter Generation. Please Contact Administrator.</h3>");
				}

				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}
	}
	/****** Member Ballot(Council) Cumulative Member Wise Question Report Page ****/
	@RequestMapping(value="/memberballot/member/cumulative/questionsreport",method=RequestMethod.GET)
	public @ResponseBody void generateCumulativeMemberQuestionsReportUsingFOP(final HttpServletRequest request,final HttpServletResponse response, final ModelMap model,final Locale locale){
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = null;

		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String strSession = request.getParameter("session");
		String strQuestionType = request.getParameter("questionType");
		String strMembers = request.getParameter("allMembers");
		String outputFormat = request.getParameter("outputFormat");

		if(strSession!=null&&strQuestionType!=null&&strMembers!=null&&outputFormat!=null){
			if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())
					&&(!strMembers.isEmpty())&&(!outputFormat.isEmpty())){
				try {
					Session session=Session.findById(Session.class,Long.parseLong(strSession));
					DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
					if(session==null || questionType==null) {
						throw new ELSException("BallotController_generateCumulativeMemberQuestionsReportUsingFOP_incorrectParameters", "Please check correctness of request paramters");
					}
					String houseType = session.findHouseType();
					if(houseType==null) {
						houseType = "";
					}
					/** question submission date formatting **/
					String questionSubmissionStartDate = "";
					String startDateParameter=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME);
					if(startDateParameter!=null){
						Date startDate = FormaterUtil.formatStringToDate(startDateParameter, ApplicationConstants.DB_DATETIME_FORMAT);
						SimpleDateFormat dbFormat = null;
						CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
						if(dbDateFormat!=null){
							dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
						}
						String[] strQuestionSubmissionDate=dbFormat.format(startDate).split(",");
						String[] strAnsweringMonth=strQuestionSubmissionDate[1].split(" ");
						String answeringMonth=FormaterUtil.getMonthInLocaleLanguage(strAnsweringMonth[1], locale.toString());
						MessageResource mrDate = MessageResource.findByFieldName(MessageResource.class, "code", "generic.date", locale.toString());
						String genericDateLabel  = (mrDate!=null)? mrDate.getValue():"";
						model.addAttribute("questionSubmissionDate",genericDateLabel + " " +strAnsweringMonth[0]+" "+ answeringMonth +","+strQuestionSubmissionDate[2]);
						questionSubmissionStartDate = genericDateLabel + " " +strAnsweringMonth[0]+" "+ answeringMonth +","+strQuestionSubmissionDate[2];
					}
					List<Member> allMembers = Member.findAllHavingIdIn(Member.class, strMembers.split(","));
					CumulativeMemberwiseQuestionsXmlVO cumulativeMemberwiseQuestionsXmlVO = new CumulativeMemberwiseQuestionsXmlVO();
					List<MemberwiseQuestionsVO> memberwiseQuestionsVOs = new ArrayList<MemberwiseQuestionsVO>();
					for(Member member: allMembers) {
						MemberwiseQuestionsVO memberwiseQuestionsVO = new MemberwiseQuestionsVO();
						/**** First Batch Questions Final Status Distribution member wise ****/
						MemberBallotMemberWiseReportVO memberBallotMemberWiseReportVO=MemberBallot.findMemberWiseReportVO(session, questionType, member, locale.toString());
						//memberwiseQuestionsXmlVO.setMemberBallotMemberWiseReportVO(memberBallotMemberWiseReportVO);
						memberwiseQuestionsVO.setMember(memberBallotMemberWiseReportVO.getMember());
						
						memberwiseQuestionsVO.setHouseType(houseType);
						memberwiseQuestionsVO.setSubmissionDate(questionSubmissionStartDate);
																		
						memberwiseQuestionsVO.setMemberBallotMemberWiseCountVOs(memberBallotMemberWiseReportVO.getMemberBallotMemberWiseCountVOs());
						memberwiseQuestionsVO.setMemberBallotMemberWiseQuestionVOs(memberBallotMemberWiseReportVO.getMemberBallotMemberWiseQuestionVOs());
						Integer clarificationCount = 0;
						Integer admittedCount = 0;
						Integer convertedToUnstarredAndAdmittedCount = 0;
						Integer rejectedCount = 0;
						if(memberBallotMemberWiseReportVO!=null) {
							for(MemberBallotMemberWiseCountVO i: memberBallotMemberWiseReportVO.getMemberBallotMemberWiseCountVOs()) {
								if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_GOVT)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_GOVT)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
										||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)) {

									clarificationCount += Integer.parseInt(i.getCount());
								} else if(i.getCurrentDeviceType().equals(ApplicationConstants.STARRED_QUESTION) 
										&& (i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION) || i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION))) {
									admittedCount = Integer.parseInt(i.getCount());
								} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
										|| i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT)) {
									convertedToUnstarredAndAdmittedCount = Integer.parseInt(i.getCount());
								} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)
										|| i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_REJECTION)) {
									rejectedCount = Integer.parseInt(i.getCount());
								}
							}
						}
						memberwiseQuestionsVO.setAdmittedQuestionCount(FormaterUtil.formatNumberNoGrouping(admittedCount, locale.toString()));
						memberwiseQuestionsVO.setConvertedToUnstarredAndAdmittedQuestionCount(FormaterUtil.formatNumberNoGrouping(convertedToUnstarredAndAdmittedCount, locale.toString()));
						memberwiseQuestionsVO.setRejectedQuestionCount(FormaterUtil.formatNumberNoGrouping(rejectedCount, locale.toString()));
						memberwiseQuestionsVO.setClarificationQuestionCount(FormaterUtil.formatNumberNoGrouping(clarificationCount, locale.toString()));
						/**** Populating Groups, Corresponding Ministries & Answering Dates ****/
						List<Group> groups=Group.findByHouseTypeSessionTypeYear(session.getHouse().getType(),session.getType(),session.getYear());
						List<GroupVO> groupVOs = new ArrayList<GroupVO>();	
						int count = 0;
						for(Group g: groups) {
							GroupVO groupVO = new GroupVO();
							List<MemberBallotMemberWiseQuestionVO> starredQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>();
							List<MemberBallotMemberWiseQuestionVO> unstarredQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>();
							List<MemberBallotMemberWiseQuestionVO> clarificationQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>();
							List<MemberBallotMemberWiseQuestionVO> rejectedQuestionVOs = new ArrayList<MemberBallotMemberWiseQuestionVO>();
							groupVO.setNumber(g.getNumber());
							groupVO.setFormattedNumber(g.formatNumber());
							List<Ministry> ministries = g.findMinistriesByPriority();	
							List<MasterVO> ministryMasterVOs = new ArrayList<MasterVO>();
							int ministryCount = 1;
							for(Ministry m: ministries) {
								MasterVO ministryMasterVO = new MasterVO();
								ministryMasterVO.setFormattedNumber("(" + FormaterUtil.formatNumberNoGrouping(ministryCount, locale.toString())+ ")");
								ministryMasterVO.setName(m.getName());
								ministryMasterVOs.add(ministryMasterVO);
								ministryCount++;
							}
							groupVO.setMinistries(ministryMasterVOs);							
							groupVO.setAnsweringDates(g.findQuestionDateReferenceVOByGroup());
							boolean hasQuestionsForGivenMember = false;
							String currentGroupNumber = groupVO.getFormattedNumber();
							if(memberBallotMemberWiseReportVO!=null) {
								for(MemberBallotMemberWiseQuestionVO i: memberBallotMemberWiseReportVO.getMemberBallotMemberWiseQuestionVOs()) {
									if(i.getGroupFormattedNumber().equals(currentGroupNumber)) {
										if(i.getCurrentDeviceType().equals(ApplicationConstants.STARRED_QUESTION) 
												&& (i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_ADMISSION) || i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION))) {
											starredQuestionVOs.add(i);											
										} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION)
												|| i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED_AND_ADMIT)) {
											unstarredQuestionVOs.add(i);											
										} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_GOVT)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_GOVT)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
												||i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)) {
											clarificationQuestionVOs.add(i);											
										} else if(i.getStatusTypeType().equals(ApplicationConstants.QUESTION_RECOMMEND_REJECTION)
												|| i.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_REJECTION)) {
											rejectedQuestionVOs.add(i);											
										}
									}
								}								
								if(!starredQuestionVOs.isEmpty() || !unstarredQuestionVOs.isEmpty()
										|| !clarificationQuestionVOs.isEmpty() || !rejectedQuestionVOs.isEmpty()) {
									hasQuestionsForGivenMember = true;
								}								
								groupVO.setHasQuestionsForGivenMember(hasQuestionsForGivenMember);
								
								/*
								 * Reset the serial number as per requested on the UI. The order
								 * is as follows: Starred Admit, Unstarred Admit, Rejected,
								 * Clarification.
								 */
								NumberFormat numberFormat = FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
								
								for(MemberBallotMemberWiseQuestionVO questionVO : starredQuestionVOs) {
									String serialNo = numberFormat.format(++count);
									questionVO.setSno(serialNo);
								}
								
								for(MemberBallotMemberWiseQuestionVO questionVO : unstarredQuestionVOs) {
									String serialNo = numberFormat.format(++count);
									questionVO.setSno(serialNo);
								}
								
								for(MemberBallotMemberWiseQuestionVO questionVO : rejectedQuestionVOs) {
									String serialNo = numberFormat.format(++count);
									questionVO.setSno(serialNo);
								}
								
								for(MemberBallotMemberWiseQuestionVO questionVO : clarificationQuestionVOs) {
									String serialNo = numberFormat.format(++count);
									questionVO.setSno(serialNo);
								}
								
								groupVO.setStarredQuestionVOs(starredQuestionVOs);
								groupVO.setUnstarredQuestionVOs(unstarredQuestionVOs);
								groupVO.setRejectedQuestionVOs(rejectedQuestionVOs);
								groupVO.setClarificationQuestionVOs(clarificationQuestionVOs);
							}
							groupVOs.add(groupVO);
						}
						memberwiseQuestionsVO.setGroupVOs(groupVOs);
						memberwiseQuestionsVOs.add(memberwiseQuestionsVO);
					}			
					cumulativeMemberwiseQuestionsXmlVO.setMemberwiseQuestionDataList(memberwiseQuestionsVOs);
					/**** generate report ****/
					reportFile = generateReportUsingFOP(cumulativeMemberwiseQuestionsXmlVO, "memberballot_cumulative_memberwise_questions", outputFormat, "memberballot_cumulative_memberwise_questions_report", locale.toString());
					if(reportFile!=null) {
						System.out.println("Memberballot Memberwise Questions Report generated successfully in " + outputFormat + " format!");
						openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
					}					
				} catch(Exception e) {
					isError = true;
					errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "REQUEST_PARAMETER_NULL", locale.toString());
				}
			}else{
				logger.error("**** Check request parameter 'session,questionType,member' for empty values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_EMPTY");
				//return errorpage;
			}
		}else{
			logger.error("**** Check request parameter 'session,questionType,member' for null values ****");
			isError = true;
			errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "REQUEST_PARAMETER_NULL", locale.toString());
		}
		if(isError) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
				if(errorMessage != null) {
					if(!errorMessage.getValue().isEmpty()) {
						response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + errorMessage.getValue() + "</h3></body></html>");
					} else {
						response.getWriter().println("<h3>Some Error In Letter Generation. Please Contact Administrator.</h3>");
					}
				} else {
					response.getWriter().println("<h3>Some Error In Letter Generation. Please Contact Administrator.</h3>");
				}

				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}
	}
	/****** Member Ballot(Council) Question Distribution Report Page ****/
	@RequestMapping(value="/memberballot/questiondistribution",method=RequestMethod.GET)
	public String viewQuestionDistributionReport(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
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
					if(questionDistributions!=null && !questionDistributions.isEmpty()) {
						MemberBallotQuestionDistributionVO memberBallotQuestionDistributionVO = questionDistributions.get(0);
						/** question submission date formatting **/
						SimpleDateFormat dbFormat = null;
						CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
						if(dbDateFormat!=null){
							dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
						}
						String[] strQuestionSubmissionDate=dbFormat.format(memberBallotQuestionDistributionVO.getQuestionSubmissionStartTime()).split(",");
						String[] strAnsweringMonth=strQuestionSubmissionDate[1].split(" ");
						String answeringMonth=FormaterUtil.getMonthInLocaleLanguage(strAnsweringMonth[1], locale.toString());
						MessageResource mrDate = MessageResource.findByFieldName(MessageResource.class, "code", "generic.date", locale.toString());
						String genericDateLabel  = (mrDate!=null)? mrDate.getValue():"";
						model.addAttribute("questionSubmissionDate",genericDateLabel + " " +strAnsweringMonth[0]+" "+ answeringMonth +","+strQuestionSubmissionDate[2]);
						/** question submission start time **/
						java.util.Calendar calendar = java.util.Calendar.getInstance();
						calendar.setTime(memberBallotQuestionDistributionVO.getQuestionSubmissionStartTime());
						int hours = calendar.get(Calendar.HOUR);
						int minutes = calendar.get(Calendar.MINUTE);
						model.addAttribute("questionSubmissionStartTime", FormaterUtil.formatNumberNoGrouping(hours, locale.toString())+"."+FormaterUtil.formatNumberNoGrouping(minutes, locale.toString()));
						Integer dayTime = calendar.get(java.util.Calendar.AM_PM);	
						model.addAttribute("dayTime",dayTime);
						/** question submission end time **/
						calendar.setTime(memberBallotQuestionDistributionVO.getQuestionSubmissionEndTime());
						hours = calendar.get(Calendar.HOUR);
						minutes = calendar.get(Calendar.MINUTE);
						model.addAttribute("questionSubmissionEndTime", FormaterUtil.formatNumberNoGrouping(hours, locale.toString())+"."+FormaterUtil.formatNumberNoGrouping(minutes, locale.toString()));

						model.addAttribute("houseType",memberBallotQuestionDistributionVO.getHouseType());
						model.addAttribute("houseTypeName",memberBallotQuestionDistributionVO.getHouseTypeName());
						model.addAttribute("sessionTypeName",memberBallotQuestionDistributionVO.getSessionTypeName());
						model.addAttribute("sessionYear",memberBallotQuestionDistributionVO.getSessionYear());
						model.addAttribute("sessionCountName",memberBallotQuestionDistributionVO.getSessionCountName());

						model.addAttribute("questionType",questionType.getId());
						model.addAttribute("questionTypeName",questionType.getName());
						model.addAttribute("locale",locale.toString());
					}		            
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
	/****** Member Ballot(Council) Question Distribution Report Header Page ****/
	@RequestMapping(value="/memberballot/questiondistribution/header",method=RequestMethod.GET)
	public String generateQuestionDistributionReportHeader(HttpServletRequest request, ModelMap model, Locale locale) {
		String returnPath = "ballot/templates/error";

		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String houseType = request.getParameter("houseType");
		String houseTypeName = request.getParameter("houseTypeName");
		String sessionTypeName = request.getParameter("sessionTypeName");
		String sessionYear = request.getParameter("sessionYear");
		String sessionCountName = request.getParameter("sessionCountName");
		String questionSubmissionStartTime = request.getParameter("questionSubmissionStartTime");
		String questionSubmissionEndTime = request.getParameter("questionSubmissionEndTime");
		String questionSubmissionDate = request.getParameter("questionSubmissionDate");
		String dayTime = request.getParameter("dayTime");
		String questionTypeName = request.getParameter("questionTypeName");

		if(houseType!=null&&!houseType.isEmpty() && houseTypeName!=null&&!houseTypeName.isEmpty()
				&&sessionTypeName!=null&&!sessionTypeName.isEmpty() && sessionYear!=null&&!sessionYear.isEmpty()
				&&sessionCountName!=null&&!sessionCountName.isEmpty() && questionSubmissionStartTime!=null&&!questionSubmissionStartTime.isEmpty()
				&&questionSubmissionEndTime!=null&&!questionSubmissionEndTime.isEmpty() && questionSubmissionDate!=null&&!questionSubmissionDate.isEmpty()
				&&dayTime!=null&&!dayTime.isEmpty() && questionTypeName!=null&&!questionTypeName.isEmpty()) {

			/**** Server encoding request parameter ****/
			CustomParameter deploymentServerParameter = CustomParameter.findByFieldName(CustomParameter.class, "name", "DEPLOYMENT_SERVER", "");
			if(deploymentServerParameter!=null) {
				if(deploymentServerParameter.getValue()!=null) {
					if(deploymentServerParameter.getValue().equals("TOMCAT")) {
						try {
							houseTypeName = new String(houseTypeName.getBytes("ISO-8859-1"),"UTF-8");
							sessionTypeName = new String(sessionTypeName.getBytes("ISO-8859-1"),"UTF-8");
							sessionYear = new String(sessionYear.getBytes("ISO-8859-1"),"UTF-8");
							sessionCountName = new String(sessionCountName.getBytes("ISO-8859-1"),"UTF-8");
							questionSubmissionStartTime = new String(questionSubmissionStartTime.getBytes("ISO-8859-1"),"UTF-8");
							questionSubmissionEndTime = new String(questionSubmissionEndTime.getBytes("ISO-8859-1"),"UTF-8");
							questionSubmissionDate = new String(questionSubmissionDate.getBytes("ISO-8859-1"),"UTF-8");
							dayTime = new String(dayTime.getBytes("ISO-8859-1"),"UTF-8");
							questionTypeName = new String(questionTypeName.getBytes("ISO-8859-1"),"UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();									
						}
					}
				}
			}

			model.addAttribute("houseType", houseType);
			model.addAttribute("houseTypeName", houseTypeName);
			model.addAttribute("sessionTypeName", sessionTypeName);
			model.addAttribute("sessionYear", sessionYear);
			model.addAttribute("sessionCountName", sessionCountName);
			model.addAttribute("questionSubmissionStartTime", questionSubmissionStartTime);
			model.addAttribute("questionSubmissionEndTime", questionSubmissionEndTime);
			model.addAttribute("questionSubmissionDate", questionSubmissionDate);
			model.addAttribute("dayTime", dayTime);
			model.addAttribute("questionTypeName", questionTypeName);

			returnPath = "ballot/templates/memberballotquestiondistribution_header";

		} else {
			model.addAttribute("errorMessage", "Request Parameter/s are either null or empty.");
		}

		return returnPath;
	}
	@RequestMapping(value="/memberballot/questiondistribution/report",method=RequestMethod.GET)
	public @ResponseBody void generateQuestionDistributionReportUsingFOP(final HttpServletRequest request,final HttpServletResponse response, final ModelMap model,final Locale locale) {
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.errorMessage", locale.toString());

		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String strQuestionType=request.getParameter("questionType");
		String strSession=request.getParameter("session");
		String outputFormat = request.getParameter("outputFormat");

		if(strQuestionType!=null&&strSession!=null&&outputFormat!=null){
			if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())&&!outputFormat.isEmpty()){
				try {
					/**** Generate & Process Report Data ****/
					Session session=Session.findById(Session.class,Long.parseLong(strSession));
					DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
					List<MemberBallotQuestionDistributionVO> questionDistributions=MemberBallot.viewQuestionDistribution(session,questionType,locale.toString());
					if(questionDistributions!=null && !questionDistributions.isEmpty()) {
						MemberBallotTotalQuestionReportXmlVO xmlVO = new MemberBallotTotalQuestionReportXmlVO();
						xmlVO.setQuestionDistributionVOs(questionDistributions);
						int totalAdmittedQuestions = 0;
						int totalConvertToUnstarredAndAdmitQuestions = 0;
						int totalRejectedQuestions = 0;
						int totalClarificationQuestions = 0;
						int totalQuestions = 0;
						for(MemberBallotQuestionDistributionVO i: questionDistributions) {
							for(MemberBallotMemberWiseCountVO j: i.getDistributions()) {
								if(j.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION) && j.getCurrentDeviceType().equals(ApplicationConstants.STARRED_QUESTION)) {
									totalAdmittedQuestions += Integer.parseInt(j.getCount());																		
								} else if(j.getStatusTypeType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION) && j.getCurrentDeviceType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
									totalConvertToUnstarredAndAdmitQuestions += Integer.parseInt(j.getCount());
								} else if(j.getStatusTypeType().equals(ApplicationConstants.QUESTION_FINAL_REJECTION) && j.getCurrentDeviceType().equals(ApplicationConstants.STARRED_QUESTION)) {
									totalRejectedQuestions += Integer.parseInt(j.getCount());
								} else if(j.getStatusTypeType().equals("clarification")) {
									totalClarificationQuestions += Integer.parseInt(j.getCount());
								}
							}
							totalQuestions += Integer.parseInt(i.getTotalCount());							
						}
						xmlVO.setTotalAdmittedQuestions(FormaterUtil.formatNumberNoGrouping(totalAdmittedQuestions, locale.toString()));
						xmlVO.setTotalConvertToUnstarredAndAdmitQuestions(FormaterUtil.formatNumberNoGrouping(totalConvertToUnstarredAndAdmitQuestions, locale.toString()));
						xmlVO.setTotalRejectedQuestions(FormaterUtil.formatNumberNoGrouping(totalRejectedQuestions, locale.toString()));
						xmlVO.setTotalClarificationQuestions(FormaterUtil.formatNumberNoGrouping(totalClarificationQuestions, locale.toString()));
						xmlVO.setTotalQuestions(FormaterUtil.formatNumberNoGrouping(totalQuestions, locale.toString()));

						String percentTotalAdmittedQuestions = FormaterUtil.getDecimalFormatterWithNoGrouping(2, locale.toString()).format((double)totalAdmittedQuestions/(double)totalQuestions*100);
						xmlVO.setPercentTotalAdmittedQuestions(percentTotalAdmittedQuestions);
						String percentTotalConvertToUnstarredAndAdmitQuestions = FormaterUtil.getDecimalFormatterWithNoGrouping(2, locale.toString()).format((double)totalConvertToUnstarredAndAdmitQuestions/(double)totalQuestions*100);
						xmlVO.setPercentTotalConvertToUnstarredAndAdmitQuestions(percentTotalConvertToUnstarredAndAdmitQuestions);
						String percentTotalRejectedQuestions = FormaterUtil.getDecimalFormatterWithNoGrouping(2, locale.toString()).format((double)totalRejectedQuestions/(double)totalQuestions*100);
						xmlVO.setPercentTotalRejectedQuestions(percentTotalRejectedQuestions);
						String percentTotalClarificationQuestions = FormaterUtil.getDecimalFormatterWithNoGrouping(2, locale.toString()).format((double)totalClarificationQuestions/(double)totalQuestions*100);
						xmlVO.setPercentTotalClarificationQuestions(percentTotalClarificationQuestions);

						MemberBallotQuestionDistributionVO memberBallotQuestionDistributionVO = questionDistributions.get(0);
						/** question submission date formatting **/
						SimpleDateFormat dbFormat = null;
						CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
						if(dbDateFormat!=null){
							dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
						}
						String[] strQuestionSubmissionDate=dbFormat.format(memberBallotQuestionDistributionVO.getQuestionSubmissionStartTime()).split(",");
						String[] strAnsweringMonth=strQuestionSubmissionDate[1].split(" ");
						String answeringMonth=FormaterUtil.getMonthInLocaleLanguage(strAnsweringMonth[1], locale.toString());
						MessageResource mrDate = MessageResource.findByFieldName(MessageResource.class, "code", "generic.date", locale.toString());
						String genericDateLabel  = (mrDate!=null)? mrDate.getValue():"";
						xmlVO.setQuestionSubmissionDate(genericDateLabel + " " +strAnsweringMonth[0]+" "+ answeringMonth +","+strQuestionSubmissionDate[2]);
						/** question submission start time **/
						java.util.Calendar calendar = java.util.Calendar.getInstance();
						calendar.setTime(memberBallotQuestionDistributionVO.getQuestionSubmissionStartTime());
						int hours = calendar.get(Calendar.HOUR);
						int minutes = calendar.get(Calendar.MINUTE);
						xmlVO.setQuestionSubmissionStartTime(FormaterUtil.formatNumberNoGrouping(hours, locale.toString())+"."+FormaterUtil.formatNumberNoGrouping(minutes, locale.toString()));
						xmlVO.setDayTime(calendar.get(java.util.Calendar.AM_PM));
						/** question submission end time **/
						calendar.setTime(memberBallotQuestionDistributionVO.getQuestionSubmissionEndTime());
						hours = calendar.get(Calendar.HOUR);
						minutes = calendar.get(Calendar.MINUTE);
						xmlVO.setQuestionSubmissionEndTime(FormaterUtil.formatNumberNoGrouping(hours, locale.toString())+"."+FormaterUtil.formatNumberNoGrouping(minutes, locale.toString()));

						xmlVO.setHouseType(memberBallotQuestionDistributionVO.getHouseType());
						xmlVO.setHouseTypeName(memberBallotQuestionDistributionVO.getHouseTypeName());
						xmlVO.setSessionTypeName(memberBallotQuestionDistributionVO.getSessionTypeName());
						xmlVO.setSessionYear(memberBallotQuestionDistributionVO.getSessionYear());
						xmlVO.setSessionCountName(memberBallotQuestionDistributionVO.getSessionCountName());
						xmlVO.setQuestionTypeName(questionType.getName());						

						/**** generate report ****/						
						reportFile = generateReportUsingFOP(xmlVO, "total_questions_distribution", outputFormat, "total_questions_distribution_report", locale.toString());
						if(reportFile!=null) {
							System.out.println("Report generated successfully in " + outputFormat + " format!");
							openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
						}
					} else {
						logger.error("**** question distributions not found  ****");				
						isError = true;
					}
				} catch(Exception e) {
					logger.error("**** Some Runtime Exception Occurred ****");
					e.printStackTrace();
					isError = true;
				}
			} else{
				logger.error("**** Check request parameter 'strRequestParameter,outputFormat' for null values ****");
				isError = true;				
			}		
		} else{
			logger.error("**** Check request parameter 'strRequestParameter,outputFormat' for null values ****");
			isError = true;				
		}

		if(isError) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
				if(errorMessage != null) {
					if(!errorMessage.getValue().isEmpty()) {
						response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + errorMessage.getValue() + "</h3></body></html>");
					} else {
						response.getWriter().println("<h3>Some Error In Report Generation. Please Contact Administrator.</h3>");
					}
				} else {
					response.getWriter().println("<h3>Some Error In Report Generation. Please Contact Administrator.</h3>");
				}

				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}
	}	
	/****** Member Ballot(Council) Member Ballot Choices Initial Page ****/
	@RequestMapping(value="/memberballot/choices",method=RequestMethod.GET)
	public String viewMemberBallotChoice(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
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
	/****** Member Ballot(Council) Member Ballot List Choices Page ****/
	@RequestMapping(value="/memberballot/listchoices",method=RequestMethod.GET)
	public String listMemberBallotChoice(final HttpServletRequest request,final ModelMap model,final Locale locale){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		boolean isMemberFillingQuestionChoices = false;
		
		try{
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			String strMember=request.getParameter("member");

			if(strQuestionType!=null&&strSession!=null){
				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));

				/**** Populating New page ****/
				/**** Member whose choice is being filled ****/
				Member member=null;
				if(strMember==null) {
					String roleFillingQuestionChoices = request.getParameter("role_filling_questionchoices");
					if(roleFillingQuestionChoices!=null && !roleFillingQuestionChoices.isEmpty() && roleFillingQuestionChoices.split("_")[0].equalsIgnoreCase(ApplicationConstants.MEMBER)) {
						AuthUser authUser = this.getCurrentUser();
						member = Member.findMember(authUser.getFirstName(), authUser.getMiddleName(),
								authUser.getLastName(), authUser.getBirthDate(), locale.toString());
						if(member==null) {
							model.addAttribute("type","MEMBER_NOT_FOUND");
							return "ballot/error";
						} else {
							isMemberFillingQuestionChoices = true;
							model.addAttribute("session",session.getId());
							model.addAttribute("questionType",questionType.getId());
							model.addAttribute("role_filling_questionchoices",roleFillingQuestionChoices);
							model.addAttribute("member_name",member.getFullname());
							model.addAttribute("isMemberFillingQuestionChoices", "YES");
						}
					} else{
						model.addAttribute("type","REQUEST_PARAMETER_NULL");
						return "ballot/error";
					}
				} else {
					member=Member.findById(Member.class,Long.parseLong(strMember));
				}				
				model.addAttribute("member",member);

				/**** Total admitted questions of member and list of admitted questions ****/
				List<Question> questions=MemberBallotChoice.findFirstBatchQuestions(session, questionType, member,"ALL","number",ApplicationConstants.ASC, locale.toString());
				model.addAttribute("admittedQuestions",questions);
				model.addAttribute("noOfAdmittedQuestions",questions.size());

				/**** Total rounds and list of member ballots ****/
				List<MemberBallot> memberBallots=MemberBallot.findByMember(session, questionType, member, locale.toString());
				model.addAttribute("memberBallots",memberBallots);
				int rounds=memberBallots.size();
				model.addAttribute("totalRounds", rounds);

				/**** Blank Form(default-no) and in case of blank form submitted auto filling to start at ****/
				model.addAttribute("blankForm","no");
				CustomParameter customParameter1=CustomParameter.findByName(CustomParameter.class, "MEMBERBALLOT_CHOICE_BLANK_FORM_FILL_FROM_ROUND", "");
				int blankFormAutoFillingStartsAt=5;
				if(customParameter1!=null){
					blankFormAutoFillingStartsAt = Integer.parseInt(customParameter1.getValue());			
				}
				model.addAttribute("blankFormAutoFillingStartsAt",blankFormAutoFillingStartsAt);

				/**** Map of Member Ballot Choices and Question In Each Rounds ****/
				Map<Integer,Map<Integer,MemberBallotChoice>> memberBallotChoicesMap=new HashMap<Integer, Map<Integer,MemberBallotChoice>>();
				Map<Integer,Integer> questionsInEachRoundMap=new HashMap<Integer, Integer>();
				int noOfMemberBallotChoices=0;
				int noOfMemberBallotChoicesExceptLast=0;
				for(int i=1;i<=rounds;i++){
					CustomParameter questionsInEachRound=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_ROUND"+i, "");
					if(questionsInEachRound!=null){
						int noOfQuestions=Integer.parseInt(questionsInEachRound.getValue());
						questionsInEachRoundMap.put(i,noOfQuestions);
						if(i!=rounds){
							noOfMemberBallotChoicesExceptLast+=noOfQuestions;
						}
						Map<Integer,MemberBallotChoice> choicesMap=new HashMap<Integer, MemberBallotChoice>();
						for(int j=1;j<=Integer.parseInt(questionsInEachRound.getValue());j++){
							MemberBallotChoice memberBallotChoice=MemberBallotChoice.findMemberBallotChoice(session, questionType, member, i, j);
							if(memberBallotChoice!=null){
								noOfMemberBallotChoices=noOfMemberBallotChoices+1;
							}
							choicesMap.put(j,memberBallotChoice);
						}
						memberBallotChoicesMap.put(i,choicesMap);
					}
				}	
				model.addAttribute("questionsInEachRoundMap",questionsInEachRoundMap);
				model.addAttribute("memberBallotChoicesMap",memberBallotChoicesMap);
				model.addAttribute("noOfMemberBallotChoices",noOfMemberBallotChoices);
				model.addAttribute("noOfMemberBallotChoicesExceptLast",noOfMemberBallotChoicesExceptLast);

				/**** Questions In Last Round By Default ****/
				CustomParameter questionsInLastRoundByDefault=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_LASTROUND_BYDEFAULT", "");
				if(questionsInLastRoundByDefault!=null){
					model.addAttribute("questionsInLastRoundByDefault", Integer.parseInt(questionsInLastRoundByDefault.getValue()));
				}else{
					model.addAttribute("questionsInLastRoundByDefault",20);
				}		

			}else{
				model.addAttribute("type","REQUEST_PARAMETER_NULL");
				return "ballot/error";
			}
		}catch (ELSException e) {
			model.addAttribute("type","REQUEST_PARAMETER_NULL");
			return "ballot/error";
		}
		
		if(isMemberFillingQuestionChoices) {
			return "ballot/memberballotchoicebymember";
		} else {
			return "ballot/listmemberballotchoice";
		}
	}

	/****** Member Ballot(Council) Member Ballot Choices Update Page ****/
	@Transactional
	@RequestMapping(value="/memberballot/choices",method=RequestMethod.POST)
	public  String updateMemberBallotChoice(final HttpServletRequest request,
			final HttpServletResponse response,
			final ModelMap model,final Locale locale){
		boolean fillStatus=false;
		
		boolean isMemberFillingQuestionChoices = false;
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			String strNoOfAdmittedQuestions=request.getParameter("noOfAdmittedQuestions");
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");
			String strMember=request.getParameter("member");
			String strTotalRounds=request.getParameter("totalRounds");
			if(strNoOfAdmittedQuestions!=null
					&&strQuestionType!=null
					&&strSession!=null
					&&strTotalRounds!=null){
				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
				
				Member member=null;
				if(strMember==null) {
					String roleFillingQuestionChoices = request.getParameter("role_filling_questionchoices");
					if(roleFillingQuestionChoices!=null && !roleFillingQuestionChoices.isEmpty() && roleFillingQuestionChoices.split("_")[0].equalsIgnoreCase(ApplicationConstants.MEMBER)) {
						AuthUser authUser = this.getCurrentUser();
						member = Member.findMember(authUser.getFirstName(), authUser.getMiddleName(),
								authUser.getLastName(), authUser.getBirthDate(), locale.toString());
						if(member==null) {
							model.addAttribute("type","MEMBER_NOT_FOUND");
							return "ballot/error";
						} else {
							isMemberFillingQuestionChoices = true;
							model.addAttribute("isMemberFillingQuestionChoices","YES");
						}
					} else{
						model.addAttribute("type","REQUEST_PARAMETER_NULL");
						return "ballot/error";
					}
				} else {
					member=Member.findById(Member.class,Long.parseLong(strMember));
				}
				
				String reasonForChoicesUpdate = request.getParameter("reasonForChoicesUpdate");
				if(!isMemberFillingQuestionChoices
						&& (reasonForChoicesUpdate==null || reasonForChoicesUpdate.isEmpty())) {
					model.addAttribute("type","REQUEST_PARAMETER_NULL");
					return "ballot/error";
				}
				
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
				fillStatus=fillChoices(noofQuestionsInEachRound,
						totalRounds,noOfAdmittedQuestions,session,
						questionType,member,locale.toString(),
						request);
				
				MemberBallotChoiceAudit memberBallotChoiceAudit = new MemberBallotChoiceAudit();
				memberBallotChoiceAudit.setLocale(locale.toString());
				memberBallotChoiceAudit.setSession(session);
				memberBallotChoiceAudit.setDeviceType(questionType);
				memberBallotChoiceAudit.setMember(member);
				
				List<MemberBallotChoiceDraft> choiceEntries=new ArrayList<MemberBallotChoiceDraft>();
				List<MemberBallotChoice> choices=MemberBallotChoice.findByMember(session, questionType, member, locale.toString());
				if(choices!=null && !choices.isEmpty()){
					for(MemberBallotChoice c: choices){
						MemberBallotChoiceDraft choiceEntry = new MemberBallotChoiceDraft();
						choiceEntry.setAutoFilled(c.getAutoFilled());
						choiceEntry.setBlankFormAutoFilled(c.getBlankFormAutoFilled());
						choiceEntry.setChoice(c.getChoice());
						choiceEntry.setClubbingUpdated(c.getClubbingUpdated());
						choiceEntry.setNewAnsweringDate(c.getNewAnsweringDate());
						choiceEntry.setProcessed(c.getProcessed());
						choiceEntry.setLocale(c.getLocale());
						choiceEntry.setQuestion(c.getQuestion());
						choiceEntry.setMemberballotChoiceId(c.getId());
						choiceEntry.setMemberballotId(c.findCorrespondingMemberBallot().getId());
						choiceEntry.setEditedBy(c.getEditedBy());
						choiceEntry.setEditedOn(c.getEditedOn());
						choiceEntry.setEditedAs(c.getEditedAs());
						choiceEntry.persist();
						choiceEntries.add(choiceEntry);
					}
					memberBallotChoiceAudit.setChoiceEntries(choiceEntries);
				}				
				
				String username = this.getCurrentUser().getActualUsername();
				Credential credential = Credential.findByFieldName(Credential.class, "username", username, null);
				memberBallotChoiceAudit.setEditedBy(username);
				memberBallotChoiceAudit.setEditedOn(new Date());
				UserGroup usergroup =  UserGroup.findActive(credential, new Date(), locale.toString());
				if(usergroup!= null){
					memberBallotChoiceAudit.setEditedAs(usergroup.getUserGroupType());
				}
				
				if(isMemberFillingQuestionChoices) {
					memberBallotChoiceAudit.setIsFilledByMember(true);
					//member filled choices notification
					CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, "MEMBERBALLOT_QUESTIONS_CHOICES_FILLED_BY_MEMBER_NOTIFICATION_DISABLED", "");
					if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
							|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
						NotificationController.sendMemberBallotQuestionChoicesFilledByMemberNotification(questionType, session, member.getFullname(), locale.toString());
					}
				} else {
					memberBallotChoiceAudit.setIsFilledByMember(false);
					memberBallotChoiceAudit.setReasonForChoicesUpdate(reasonForChoicesUpdate);					
					//branch filled updated choices notification
					CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, "MEMBERBALLOT_QUESTIONS_CHOICES_FILLED_BY_BRANCH_NOTIFICATION_DISABLED", "");
					if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
							|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
						NotificationController.sendMemberBallotQuestionChoicesFilledByBranchNotification(questionType, session, member.getFullname(), this.getCurrentUser().getActualUsername(), locale.toString());
					}
				}
				
				memberBallotChoiceAudit.persist();

				/**** Populating Edit page ****/
				/**** Member whose choice is being filled ****/
				model.addAttribute("member",member);

				/**** Total admitted questions of member and list of admitted questions ****/
				List<Question> questions=MemberBallotChoice.findFirstBatchQuestions(session, questionType, member,"ALL","number",ApplicationConstants.ASC, locale.toString());
				model.addAttribute("admittedQuestions",questions);
				model.addAttribute("noOfAdmittedQuestions",questions.size());

				/**** Total rounds and list of member ballots ****/
				List<MemberBallot> memberBallots=MemberBallot.findByMember(session, questionType, member, locale.toString());
				model.addAttribute("memberBallots",memberBallots);
				int rounds=memberBallots.size();
				model.addAttribute("totalRounds", rounds);

				/**** Blank Form(default-no) and in case of blank form submitted auto filling to start at ****/
				model.addAttribute("blankForm","no");
				CustomParameter customParameter1=CustomParameter.findByName(CustomParameter.class, "MEMBERBALLOT_CHOICE_BLANK_FORM_FILL_FROM_ROUND", "");
				int blankFormAutoFillingStartsAt=5;
				if(customParameter1!=null){
					blankFormAutoFillingStartsAt = Integer.parseInt(customParameter1.getValue());			
				}
				model.addAttribute("blankFormAutoFillingStartsAt",blankFormAutoFillingStartsAt);

				/**** Map of Member Ballot Choices and Question In Each Rounds ****/
				Map<Integer,Map<Integer,MemberBallotChoice>> memberBallotChoicesMap=new HashMap<Integer, Map<Integer,MemberBallotChoice>>();
				Map<Integer,Integer> questionsInEachRoundMap=new HashMap<Integer, Integer>();
				int noOfMemberBallotChoices=0;
				int noOfMemberBallotChoicesExceptLast=0;
				for(int i=1;i<=rounds;i++){
					CustomParameter questionsInEachRound=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_ROUND"+i, "");
					if(questionsInEachRound!=null){
						int noOfQuestions=Integer.parseInt(questionsInEachRound.getValue());
						questionsInEachRoundMap.put(i,noOfQuestions);
						if(i!=rounds){
							noOfMemberBallotChoicesExceptLast+=noOfQuestions;
						}
						Map<Integer,MemberBallotChoice> choicesMap=new HashMap<Integer, MemberBallotChoice>();
						for(int j=1;j<=Integer.parseInt(questionsInEachRound.getValue());j++){
							MemberBallotChoice memberBallotChoice=MemberBallotChoice.findMemberBallotChoice(session, questionType, member, i, j);
							if(memberBallotChoice!=null){
								noOfMemberBallotChoices=noOfMemberBallotChoices+1;
							}
							choicesMap.put(j,memberBallotChoice);
						}
						memberBallotChoicesMap.put(i,choicesMap);
					}
				}	
				model.addAttribute("questionsInEachRoundMap",questionsInEachRoundMap);
				model.addAttribute("memberBallotChoicesMap",memberBallotChoicesMap);
				model.addAttribute("noOfMemberBallotChoices",noOfMemberBallotChoices);
				model.addAttribute("noOfMemberBallotChoicesExceptLast",noOfMemberBallotChoicesExceptLast);

				/**** Questions In Last Round By Default ****/
				CustomParameter questionsInLastRoundByDefault=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_LASTROUND_BYDEFAULT", "");
				if(questionsInLastRoundByDefault!=null){
					model.addAttribute("questionsInLastRoundByDefault", Integer.parseInt(questionsInLastRoundByDefault.getValue()));
				}else{
					model.addAttribute("questionsInLastRoundByDefault",20);
				}

			}else{
				model.addAttribute("type","REQUEST_PARAMETER_NULL");
				return "ballot/error";
			}
		}catch (ELSException e) {
			model.addAttribute("error",e.getParameter());
			return "ballot/error";
		}catch(Exception e){
			model.addAttribute("type","REQUEST_PARAMETER_NULL");
			return "ballot/error";
		}
		if(fillStatus){
			model.addAttribute("type","SUCCESS");
		}else{
			model.addAttribute("type","FAILED");
		}
		return "ballot/listmemberballotchoice";
	}

	private boolean fillChoices(Map<String, Integer> noofQuestionsInEachRound,
			int totalRounds, int noOfAdmittedQuestions, Session session,
			DeviceType questionType, Member member, String locale,
			HttpServletRequest request) {
		try{
			/**** Total manually set choices ****/
			Map<Long,MemberBallotChoice> manuallySetChoices=new HashMap<Long, MemberBallotChoice>();
			/**** Total Blank Choices kept deliberately blank ****/			
			Map<Integer,Map<Integer,Integer>> manuallySetBlankChoices=new HashMap<Integer, Map<Integer,Integer>>();

			/**** Obtain value of 'autofillingstartsat' ****/
			String strAutoFillingStartsAt=request.getParameter("autofillingstartsat");
			int autoFillingStartsAt=Integer.parseInt(strAutoFillingStartsAt);

			/**** Blank form is submitted without clicking on auto fill.
			 * All admitted questions will be taken from last round ****/
			//			String strBlankForm=request.getParameter("blankForm");				
			//			if(strBlankForm!=null&&!strBlankForm.isEmpty()&&strBlankForm.equals("yes")&&autoFillingStartsAt==0){
			//				return autoFillChoices("ALL",noofQuestionsInEachRound,
			//						totalRounds,noOfAdmittedQuestions,session,
			//						questionType,member,locale,
			//						manuallySetChoices,"BLANK_FORM_NO_AUTOFILL_CLICKED",manuallySetBlankChoices);	
			//			}	

			/**** Blank form is submitted and auto fill clicked at round 5 ****/
			//			String strblankFormAutoFillFromLast=request.getParameter("blankFormAutoFillingStartsFromLast");
			//			if(strblankFormAutoFillFromLast!=null && !strblankFormAutoFillFromLast.isEmpty()&& strblankFormAutoFillFromLast.equals("yes")){
			//				return autoFillChoices("ALL",noofQuestionsInEachRound,
			//						totalRounds,noOfAdmittedQuestions,session,
			//						questionType,member,locale,
			//						manuallySetChoices,"BLANK_FORM_AUTOFILL_CLICKED_AUTOFILL_AT_LASTROUND",manuallySetBlankChoices);
			//			}		

			/**** count=no of manually filled choices(except choices kept deliberately blank) ****/
			int count=1;
			/**** choicesClearedForRounds=clear choices which were not manually filled but might have been there in db ****/
			int choicesClearedForRounds=0;
			/**** How many times loop has been travelled ****/
			int loopCount=0;
			/**** Manual Filling of Choices ****/
			if(autoFillingStartsAt > 1 || autoFillingStartsAt==0){
				for(int i=1;i<=totalRounds;i++){					
					if((autoFillingStartsAt>1)&&(loopCount>=autoFillingStartsAt-1)){
						/**** some questions have been filled manually and some will be auto filled ****/
						break;
					}else if(count>noOfAdmittedQuestions){
						/**** All questions have been manually filled ****/					
						return true;
					}		
					/**** Clear existing member ballot choice association entries and member ballot choices entries ****/
					MemberBallot memberBallot=MemberBallot.findByMemberRound(session, questionType, member,i, locale.toString());
					List<MemberBallotChoice> choices=new ArrayList<MemberBallotChoice>();			
					if(memberBallot.getQuestionChoices()!=null && !memberBallot.getQuestionChoices().isEmpty()){
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
					/**** Blank choices in each round  ****/
					Map<Integer,Integer> blankChoices=new HashMap<Integer, Integer>();
					Integer questionsInEachRound=noofQuestionsInEachRound.get("round"+i);
					for(int j=1;j<=questionsInEachRound;j++){
						if((autoFillingStartsAt>1)&&(loopCount>=autoFillingStartsAt-1)){
							break;
						}else if(count>noOfAdmittedQuestions){
							break;
						}
						String strChoice=request.getParameter("choice"+(loopCount+1));
						String strQuestion=request.getParameter("question"+(loopCount+1));
						String strAnsweringDate=request.getParameter("answeringDate"+(loopCount+1));
						/**** Manual filling 
						 * a.both question and answering date has been filled
						 * b.question has been filled but not answering date
						 * c.both have been left empty ****/
						if(strChoice!=null&&strQuestion!=null&&strAnsweringDate!=null&&!strQuestion.equals("-")){
							MemberBallotChoice memberBallotChoice=null;					
							memberBallotChoice=new MemberBallotChoice();
							if(!strChoice.isEmpty()){
								memberBallotChoice.setChoice(Integer.parseInt(strChoice));
							}
							if(!strQuestion.equals("-")){
								Question question=Question.findById(Question.class,Long.parseLong(strQuestion));
								if(!strAnsweringDate.equals("-")){
									QuestionDates questionDates=QuestionDates.findById(QuestionDates.class,Long.parseLong(strAnsweringDate));
									memberBallotChoice.setNewAnsweringDate(questionDates);
									question.setAnsweringDate(questionDates);
									question.merge();
									Map<String, String> parameters = new HashMap<String, String>();
									parameters.put("deviceId", question.getId().toString());
									parameters.put("status", ApplicationConstants.MYTASK_PENDING);
									List<WorkflowDetails> workflowTasksOfQuestion =  WorkflowDetails.findAllByFieldNames(WorkflowDetails.class, parameters, "id", ApplicationConstants.ASC, locale);
									if(workflowTasksOfQuestion!=null && !workflowTasksOfQuestion.isEmpty()) {
										for(WorkflowDetails wd: workflowTasksOfQuestion) {
											wd.setAnsweringDate(question.getAnsweringDate().getAnsweringDate());
											wd.merge();
										}
									}
								}else{
									memberBallotChoice.setNewAnsweringDate(question.getChartAnsweringDate());
									question.setAnsweringDate(question.getChartAnsweringDate());
									question.merge();
									Map<String, String> parameters = new HashMap<String, String>();
									parameters.put("deviceId", question.getId().toString());
									parameters.put("status", ApplicationConstants.MYTASK_PENDING);
									List<WorkflowDetails> workflowTasksOfQuestion =  WorkflowDetails.findAllByFieldNames(WorkflowDetails.class, parameters, "id", ApplicationConstants.ASC, locale);
									if(workflowTasksOfQuestion!=null && !workflowTasksOfQuestion.isEmpty()) {
										for(WorkflowDetails wd: workflowTasksOfQuestion) {
											wd.setAnsweringDate(question.getAnsweringDate().getAnsweringDate());
											wd.merge();
										}
									}
								}
								memberBallotChoice.setQuestion(question);
							}
							memberBallotChoice.setLocale(locale);
							memberBallotChoice.setClubbingUpdated(false);
							memberBallotChoice.setProcessed(false);
							memberBallotChoice.setAutoFilled(false);
							memberBallotChoice.setBlankFormAutoFilled(false);
							
							String username = this.getCurrentUser().getActualUsername();
							Credential credential = Credential.findByFieldName(Credential.class, "username", username, null);
							memberBallotChoice.setEditedBy(username);
							memberBallotChoice.setEditedOn(new Date());
							UserGroup usergroup =  UserGroup.findActive(credential, new Date(), locale);
							if(usergroup!= null){
								memberBallotChoice.setEditedAs(usergroup.getUserGroupType().getType());
							}
							
							if(memberBallotChoice.getId()==null){
								memberBallotChoice.persist();
							}else{
								memberBallotChoice.merge();
							}
							memberBallotChoices.add(memberBallotChoice);
							manuallySetChoices.put(memberBallotChoice.getId(),memberBallotChoice);
							count++;
							loopCount++;
						}else{
							/**** The choices in each round which are deliberately kept blank ****/
							blankChoices.put(j,j);
							loopCount++;
						}
					}
					/**** if no choices have been filled for a round its choices will be set to null ****/
					if(memberBallotChoices.isEmpty()){
						memberBallotChoices=null;
					}
					memberBallot.setQuestionChoices(memberBallotChoices);
					memberBallot.merge();
					/**** Blank Choices In each round ****/
					manuallySetBlankChoices.put(i,blankChoices);
				}
			}
			/**** Here first we will clear the entries for the rounds which were not updated manually and might be present in the db.
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
			if(count> noOfAdmittedQuestions){
				return true;
			}
			/**** Partially filled choices(manually-blank+properly filled choices) and no auto filling clicked 
			 * No auto filling will take place */
			//			if(autoFillingStartsAt==0 && count< noOfAdmittedQuestions){
			//				return true;
			//			}else 
			if(autoFillingStartsAt>=1){
				if(autoFillingStartsAt==1){
					return autoFillChoices("ALL",noofQuestionsInEachRound,
							totalRounds,noOfAdmittedQuestions,session,
							questionType,member,locale,
							manuallySetChoices,"BLANKFORM_AUTOFILL_CLICKED_AT_BEGINING",manuallySetBlankChoices);
				}else{
					/**** Partially filled choices and want rest questions to be auto filled from last round by clicking auto
					 * fill at last round ****/
					return autoFillChoices("PARTIAL",noofQuestionsInEachRound,
							totalRounds,noOfAdmittedQuestions,session,
							questionType,member,locale,
							manuallySetChoices,"PARTIALLYFILLED_CHOICES_AUTOFILL_CLICKED",manuallySetBlankChoices);
				}			
			}
		}catch (ELSException e) {
			logger.error(e.getMessage());
		}
		return false;
	}
	private boolean autoFillChoices(String autofillingPattern,Map<String, Integer> noofQuestionsInEachRound,
			int totalRounds, int noOfAdmittedQuestions, Session session,
			DeviceType questionType, Member member, String locale,
			Map<Long,MemberBallotChoice> manuallySetChoices,String autoFillingCase,
			Map<Integer,Map<Integer,Integer>> manuallySetBlankChoices){

		try{			
			List<Question> inputQuestions=new ArrayList<Question>();
			CustomParameter customParameter1=CustomParameter.findByName(CustomParameter.class, "MEMBERBALLOT_CHOICE_BLANK_FORM_FILL_FROM_ROUND", "");
			int fillingStartsFromRound=5;
			if(customParameter1!=null){
				fillingStartsFromRound = Integer.parseInt(customParameter1.getValue());			
			}
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
				//int startingRound=1;
				//				if(autoFillingCase!=null && !autoFillingCase.isEmpty() && 
				//						(autoFillingCase.equals("BLANK_FORM_NO_AUTOFILL_CLICKED")
				//								||autoFillingCase.equals("BLANK_FORM_AUTOFILL_CLICKED_AUTOFILL_AT_LASTROUND")
				//								||autoFillingCase.equals("PARTIALLYFILLED_NO_AUTOFILL_CLICKED_AUTOFILL_AT_LAST")
				//								||autoFillingCase.equals("PARTIALLYFILLED_CHOICES_AUTOFILL_CLICKED_AT_LAST"))){					
				//					startingRound=fillingStartsFromRound;
				//				}
				for(int i=1;i<=totalRounds;i++){
					if(count>=size){
						break;
					}
					/**** Manually set choices in each round ****/
					List<MemberBallotChoice> memberBallotChoices=new ArrayList<MemberBallotChoice>();
					/**** Member ballot of current round ****/
					MemberBallot memberBallot=MemberBallot.findByMemberRound(session, questionType, member,i, locale.toString());
					/**** Existing choices in each round ****/
					List<MemberBallotChoice> choices=new ArrayList<MemberBallotChoice>();

					/**** set member ballot of current round choices to null and update
					 * remove all existing choices if pattern is ALL
					 * remove those entries which have not been set manually
					 * collect existing manually set entries in memberBallotChoices ****/
					if(memberBallot.getQuestionChoices()!=null){
						for(MemberBallotChoice c:memberBallot.getQuestionChoices()){
							choices.add(c);
						}	
						memberBallot.setQuestionChoices(null);
						memberBallot.merge();
						if(autofillingPattern.toUpperCase().equals("ALL")){
							for(MemberBallotChoice c:choices){
								c.remove();
							}					
						}else{
							for(MemberBallotChoice c:choices){
								if(manuallySetChoices.get(c.getId())==null){
									c.remove();
								}else{
									memberBallotChoices.add(c);
								}
							}	
						}
					}
					Integer questionsInEachRound=noofQuestionsInEachRound.get("round"+i);
					/**** Choices which are manually filled in each round ****/
					Map<Integer,Long> choicesManuallySetEachRound=new HashMap<Integer, Long>();
					if(memberBallotChoices!=null && !memberBallotChoices.isEmpty()){
						for(MemberBallotChoice mb:memberBallotChoices){
							if(mb.getChoice()!=null){
								choicesManuallySetEachRound.put(mb.getChoice(), mb.getId());
							}
						}						
					}
					/**** size of existing manually set choices in each round-blank choices ****/
					//	if((startingRound!=1 && i==startingRound)||(startingRound==1 && i<=totalRounds)){
					for(int j=1;j<=questionsInEachRound;j++){
						if(count>=size){
							break;
						}
						if(!choicesManuallySetEachRound.isEmpty()&& choicesManuallySetEachRound.get(j)!=null){

						}else if(manuallySetBlankChoices.get(i)!=null && manuallySetBlankChoices.get(i).get(j)!=null){

						}else{
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
							
							String username = this.getCurrentUser().getActualUsername();
							Credential credential = Credential.findByFieldName(Credential.class, "username", username, null);
							memberBallotChoice.setEditedBy(username);
							memberBallotChoice.setEditedOn(new Date());
							UserGroup usergroup =  UserGroup.findActive(credential, new Date(), locale);
							if(usergroup!= null){
								memberBallotChoice.setEditedAs(usergroup.getUserGroupType().getType());
							}
							
							memberBallotChoice.persist();
							memberBallotChoices.add(memberBallotChoice);
							count++;
							question.setAnsweringDate(question.getChartAnsweringDate());
							question.merge();
							Map<String, String> parameters = new HashMap<String, String>();
							parameters.put("deviceId", question.getId().toString());
							parameters.put("status", ApplicationConstants.MYTASK_PENDING);
							List<WorkflowDetails> workflowTasksOfQuestion =  WorkflowDetails.findAllByFieldNames(WorkflowDetails.class, parameters, "id", ApplicationConstants.ASC, locale);
							if(workflowTasksOfQuestion!=null && !workflowTasksOfQuestion.isEmpty()) {
								for(WorkflowDetails wd: workflowTasksOfQuestion) {
									wd.setAnsweringDate(question.getChartAnsweringDate().getAnsweringDate());
									wd.merge();
								}
							}
						}
					}
					//}					
					memberBallot.setQuestionChoices(memberBallotChoices);
					memberBallot.merge();
				}
			}	
			return true;
		}catch (ELSException e) {
			logger.error(e.getMessage());
		}
		return false;
	}
	
	@RequestMapping(value="/memberballot/choices_status",method=RequestMethod.GET)
	public String viewMemberBallotChoicesStatus(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			String strQuestionType=request.getParameter("questionType");
			String strSession=request.getParameter("session");			
			if(strQuestionType!=null && !strQuestionType.isEmpty()
					&& strSession!=null && !strSession.isEmpty()) {
				
				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				model.addAttribute("session",session.getId());
				
				DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
				model.addAttribute("questionType",questionType.getId());
				
				Map<String, String[]> queryParameters = new HashMap<String, String[]>();
				queryParameters.put("deviceTypeId", new String[]{questionType.getId().toString()});
				queryParameters.put("sessionId", new String[]{session.getId().toString()});
				queryParameters.put("locale", new String[]{locale.toString()});
				
				@SuppressWarnings("rawtypes")
				List report = Query.findReport("MEMBERBALLOT_CHOICES_STATUS_REPORT", queryParameters);
				if(report != null && !report.isEmpty()){
					Object[] obj = (Object[])report.get(0);
					if(obj != null){
						
						model.addAttribute("colHeaders", obj[0].toString().split(";"));
						model.addAttribute("statusMessages", obj[5].toString().split(";"));
					}
					List<String> serialNumbers = populateSerialNumbers(report, locale);
					model.addAttribute("serialNumbers", serialNumbers);
				}
				model.addAttribute("formater", new FormaterUtil());
				model.addAttribute("locale", locale.toString());
				model.addAttribute("report", report);
				
			} else{
				logger.error("**** Check request parameter 'session,questionType' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
				return errorpage;
			}
		}catch(Exception ex){
			logger.error("failed",ex);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "ballot/memberballotchoice_status";
	}

	@RequestMapping(value="/memberballotchoice/revisions/{memberId}/session/{sessionId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,
							@PathVariable("memberId")  final Long memberId,
							@PathVariable("sessionId")  final Long sessionId,
							final ModelMap model){
		
		List<MemberBallotChoiceRevisionVO> revisions = MemberBallotChoice.getRevisions(memberId, sessionId, locale.toString());
		model.addAttribute("revisions", revisions);
		
		return "ballot/memberballotchoice_revisions";
	}

	/****** Member Ballot(Council) Member Ballot Update Clubbing Page ****/
	@Transactional
	@RequestMapping(value="/memberballot/updateclubbing",method=RequestMethod.GET)
	public String updateClubbingMemberBallot(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			String strSession=request.getParameter("session");
			String strDeviceType=request.getParameter("questionType");
			if(strSession!=null&&strDeviceType!=null){
				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
				String startTime=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME);
				String endTime=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
				
				Map<String,String[]> requestMap=new HashMap<String, String[]>();
				requestMap.put("locale",new String[]{locale.toString()});
				requestMap.put("sessionid",new String[]{String.valueOf(session.getId())});
				requestMap.put("deviceTypeId",new String[]{String.valueOf(deviceType.getId())});
				requestMap.put("startDate",new String[]{startTime});
				requestMap.put("endDate",new String[]{endTime});
				requestMap.put("statusType",new String[]{ApplicationConstants.QUESTION_FINAL_ADMISSION});
				
				boolean updateClubbingStatus=MemberBallot.updateClubbing(session, deviceType, requestMap, locale.toString());	
				if(!updateClubbingStatus){
					model.addAttribute("type", "MEMBERBALLOTUPDATECLUBBING_FAILED");
					return "ballot/error";
				}
			}else{
				logger.error("**** Check request parameters 'session and deviceType' for null values ****");
				model.addAttribute("type", "REQUEST_PARAMETER_NULL");
				return "ballot/error";
			}
		}catch (ELSException e) {
			if(e.getParameter("MemberBallotRepository_exception_updateclubbing")!=null) {
				logger.error(e.getParameter("MemberBallotRepository_exception_updateclubbing"));
			}
			model.addAttribute("type", "DB_EXCEPTION");
			return "ballot/error";
		}
		model.addAttribute("type", "success");
		return "ballot/memberballotupdateclubbing";
	}	

	/****** Member Ballot(Council) Member Ballot Final Ballot Page ****/
	@Transactional
	@RequestMapping(value="/memberballot/final",method=RequestMethod.GET)
	public String createFinalMemberBallot(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			String strSession=request.getParameter("session");
			String strDeviceType=request.getParameter("questionType");
			String strGroup=request.getParameter("group");
			String strAnsweringDate=request.getParameter("answeringDate");
			Boolean status=false;
			List<MemberBallotFinalBallotVO> ballots=new ArrayList<MemberBallotFinalBallotVO>();
			if(strSession!=null&&strDeviceType!=null&&strGroup!=null&&strAnsweringDate!=null){
				if((!strSession.isEmpty())&&(!strDeviceType.isEmpty())&&(!strGroup.isEmpty())&&(!strAnsweringDate.isEmpty())){
					Session session=Session.findById(Session.class, Long.parseLong(strSession));
					String firstBatchSubmissionDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
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
							String totalRounds=session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_FINAL);
							if(totalRounds==null){
								model.addAttribute("type","TOTAL_ROUNDS_IN_BALLOT_UH_NOTSET");
								return errorpage;	
							}else if(totalRounds.isEmpty()){
								model.addAttribute("type","TOTAL_ROUNDS_IN_BALLOT_UH_NOTSET");
								return errorpage;	
							}
							status=MemberBallot.createFinalBallotUH(session, deviceType,group,ansDate,questionDates.getAnsweringDate(), locale.toString(),firstBatchSubmissionDate,Integer.parseInt(totalRounds));
							if(status){
								ballots=MemberBallot.viewFinalBallot(session, deviceType,ansDate, locale.toString());
								model.addAttribute("ballots",ballots);
								model.addAttribute("answeringDate",FormaterUtil.getDateFormatter(locale.toString()).format(questionDates.getAnsweringDate()));
								//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
								String ballotUserName = this.getCurrentUser().getActualUsername();
								CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+session.getHouse().getType().getType().toUpperCase()+"_BALLOT_NOTIFICATION_DISABLED", "");
								if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
										|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
									NotificationController.sendBallotCreationNotification(deviceType, session.getHouse().getType(), questionDates.getAnsweringDate(), group.getNumber().toString(), ballotUserName, locale.toString());
								}
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
		}catch (ELSException e) {
			logger.error("**** Check request parameter 'session,deviceType,group,answering date' for null values ****");
			model.addAttribute("type", "MEMBERBALLOTFINAL_REQUEST_PARAMETER_NULL");
			return errorpage;
		}
		return "ballot/memberballotfinal";		
	}
	
	@Transactional
	@RequestMapping(value="/memberballot/viewfinalballot",method=RequestMethod.GET)
	public String viewFinalMemberBallot(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			String strSession=request.getParameter("session");
			String strDeviceType=request.getParameter("questionType");
			String strGroup=request.getParameter("group");
			String strAnsweringDate=request.getParameter("answeringDate");
			Boolean status=false;
			List<MemberBallotFinalBallotVO> ballots=new ArrayList<MemberBallotFinalBallotVO>();
			if(strSession!=null&&strDeviceType!=null&&strGroup!=null&&strAnsweringDate!=null){
				if((!strSession.isEmpty())&&(!strDeviceType.isEmpty())&&(!strGroup.isEmpty())&&(!strAnsweringDate.isEmpty())){
					Session session=Session.findById(Session.class, Long.parseLong(strSession));
					String firstBatchSubmissionDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
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
							String totalRounds=session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_FINAL);
							if(totalRounds==null){
								model.addAttribute("type","TOTAL_ROUNDS_IN_BALLOT_UH_NOTSET");
								return errorpage;	
							}else if(totalRounds.isEmpty()){
								model.addAttribute("type","TOTAL_ROUNDS_IN_BALLOT_UH_NOTSET");
								return errorpage;	
							}
							Ballot ballot = Ballot.find(session, deviceType, questionDates.getAnsweringDate(), locale.toString());
							//status=MemberBallot.createFinalBallotUH(session, deviceType,group,ansDate,questionDates.getAnsweringDate(), locale.toString(),firstBatchSubmissionDate,Integer.parseInt(totalRounds));
							if(ballot!=null){
								ballots=MemberBallot.viewFinalBallot(session, deviceType,ansDate, locale.toString());
								model.addAttribute("ballots",ballots);
								model.addAttribute("answeringDate",FormaterUtil.getDateFormatter(locale.toString()).format(questionDates.getAnsweringDate()));
								//SEND NOTIFICATION OF SUCCESSFUL BALLOT CREATION
								String ballotUserName = this.getCurrentUser().getActualUsername();
								CustomParameter csptBallotNotificationDisabled = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_"+session.getHouse().getType().getType().toUpperCase()+"_BALLOT_NOTIFICATION_DISABLED", "");
								if(csptBallotNotificationDisabled==null || csptBallotNotificationDisabled.getValue()==null
										|| (!csptBallotNotificationDisabled.getValue().equals("YES"))) {
									NotificationController.sendBallotCreationNotification(deviceType, session.getHouse().getType(), questionDates.getAnsweringDate(), group.getNumber().toString(), ballotUserName, locale.toString());
								}
								
							}else{
								model.addAttribute("type","FINAL_BALLOT_NOT_CREATED");
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
		}catch (ELSException e) {
			logger.error("**** Check request parameter 'session,deviceType,group,answering date' for null values ****");
			model.addAttribute("type", "MEMBERBALLOTFINAL_REQUEST_PARAMETER_NULL");
			return errorpage;
		}
		return "ballot/memberballotfinal";		
	}
	
	
	/****** Member Ballot(Council) Member Ballot Final Ballot Page ****/
	@Transactional
	@RequestMapping(value="/memberballot/previewfinal",method=RequestMethod.GET)
	public String previewFinalMemberBallot(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			String strSession=request.getParameter("session");
			String strDeviceType=request.getParameter("questionType");
			String strGroup=request.getParameter("group");
			String strAnsweringDate=request.getParameter("answeringDate");
			Boolean status=false;
			List<MemberBallotFinalBallotVO> ballots=new ArrayList<MemberBallotFinalBallotVO>();
			
			if(strSession!=null&&strDeviceType!=null&&strGroup!=null&&strAnsweringDate!=null){
				if((!strSession.isEmpty())&&(!strDeviceType.isEmpty())&&(!strGroup.isEmpty())&&(!strAnsweringDate.isEmpty())){
					Session session=Session.findById(Session.class, Long.parseLong(strSession));
					String firstBatchSubmissionDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
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
							String totalRounds=session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_FINAL);
							if(totalRounds==null){
								model.addAttribute("type","TOTAL_ROUNDS_IN_BALLOT_UH_NOTSET");
								return errorpage;	
							}else if(totalRounds.isEmpty()){
								model.addAttribute("type","TOTAL_ROUNDS_IN_BALLOT_UH_NOTSET");
								return errorpage;	
							}

							ballots = MemberBallot.previewFinalBallotUH(session, deviceType, group, ansDate, questionDates.getAnsweringDate(), locale.toString(), firstBatchSubmissionDate, Integer.parseInt(totalRounds));
							model.addAttribute("ballots",ballots);
							model.addAttribute("answeringDate",FormaterUtil.getDateFormatter(locale.toString()).format(questionDates.getAnsweringDate()));
							

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
		}catch (ELSException e) {
			logger.error("**** Check request parameter 'session,deviceType,group,answering date' for null values ****");
			model.addAttribute("type", "MEMBERBALLOTFINAL_REQUEST_PARAMETER_NULL");
			return errorpage;
		}
		return "ballot/previewballotfinal";		
	}

	/****** Member Ballot(Council) Member Ballot report in PDF or WORD using FOP ****//*
	@RequestMapping(value="/memberballot/report",method=RequestMethod.GET)
	public @ResponseBody void createMemberBallotReport(final HttpServletRequest request,
			final HttpServletResponse response,
			final ModelMap model,
			final Locale locale){		
		File reportFile = null;

		String strAttendance=request.getParameter("attendance");
		String strQuestionType=request.getParameter("questionType");
		String strSession=request.getParameter("session");
		String strRound=request.getParameter("round");
		String strGroup=request.getParameter("group");
		String strAnsweringDate=request.getParameter("answeringDate");
		String reportFormat=request.getParameter("outputFormat");
		if(strSession!=null&&strQuestionType!=null
				&&strAttendance!=null&&strRound!=null&&reportFormat!=null){
			if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())
					&&(!strAttendance.isEmpty())&&(!strRound.isEmpty())&&(!reportFormat.isEmpty())){
				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
				Boolean attendance=Boolean.parseBoolean(strAttendance);
				Integer round=Integer.parseInt(strRound);
				try {
	 *//**** check whether member ballot is done or not for given round  ****//*
					int count=MemberBallot.findEntryCount(session,questionType,round,attendance,locale.toString());
					if(count>0){
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

		            	StarredQuestionCouncilBallotXmlVO data = new StarredQuestionCouncilBallotXmlVO();
		            	data.setRound(FormaterUtil.formatNumberNoGrouping(round, locale.toString()));
		            	data.setMemberBallots(memberBallots);
		            	data.setAttendance(strAttendance);
		            	data.setSessionPlace(session.getPlace().getPlace());		            	
		            	data.setDate(FormaterUtil.formatDateToString(new Date(), ApplicationConstants.BALLOT_REPORT_DATE_FORMAT, locale.toString()));

		            	//generate report
		            	if(reportFormat.equals("WORD")) {
		            		reportFile = generateReportUsingFOP(data, "template_starredquestion_council_ballot_report_word", reportFormat, "memberballot_r" + round, locale.toString());
		            	} else {
		            		reportFile = generateReportUsingFOP(data, "template_starredquestion_council_ballot_report", reportFormat, "memberballot_r" + round, locale.toString());
		            	}        		
		        		System.out.println("Member Ballot Report for Round " + round + " generated successfully in " + reportFormat + " format!");

		        		openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);					
					} else {
						//response.sendError(404, "Report cannot be generated at this stage.");
	            		MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "ballot.memberpreballot.incomplete", locale.toString());
	            		if(message != null) {
	            			if(!message.getValue().isEmpty()) {
	            				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
	            			} else {
	            				response.getWriter().println("<h3>member ballot for this round is not completed.</h3>");
	            			}
	            		} else {
	            			response.getWriter().println("<h3>member ballot for this round is not completed.</h3>");
	            		}
	            		return;
					}
				} catch (IOException e) {						
					e.printStackTrace();
				}catch (ELSException e) {
					logger.error(e.getMessage());
				}							
			}else{
				logger.error("**** Check request parameters 'attendance,questionType,session,round,outputFormat' for empty values ****");
				try {
					response.getWriter().println("<h3>Check request parameters 'attendance,questionType,session,round,outputFormat' for empty values</h3>");
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}

		}else{
			logger.error("**** Check request parameters 'attendance,questionType,session,round,outputFormat' for null values ****");
			try {
				response.getWriter().println("<h3>Check request parameters 'attendance,questionType,session,round,outputFormat' for null values</h3>");
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}					
	}

	  *//****** Member Ballot(Council) Pre Ballot report in PDF or WORD using FOP ****//*
	@RequestMapping(value="/memberballot/preballotreport",method=RequestMethod.GET)
	public @ResponseBody void councilStarredPreBallotReport(final HttpServletRequest request,
			final HttpServletResponse response,
			final ModelMap model,
			final Locale locale){
		File reportFile = null;		

		String strAttendance=request.getParameter("attendance");
		String strQuestionType=request.getParameter("questionType");
		String strSession=request.getParameter("session");
		String strRound=request.getParameter("round");
		String reportFormat=request.getParameter("outputFormat");
		if(strAttendance!=null&&strQuestionType!=null&&strSession!=null&&strRound!=null&&reportFormat!=null) {
			if(!strAttendance.isEmpty()&&!strQuestionType.isEmpty()&&!strSession.isEmpty()&&!strRound.isEmpty()&&!reportFormat.isEmpty()) {
				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
				Boolean attendance=Boolean.parseBoolean(strAttendance);
				Integer round=Integer.parseInt(strRound);		
				try {
	   *//**** Before Pre-Ballot we must first check if atleast once submit button has been 
	   * clicked or not.Because if submit button is not clicked for a particular round and
	   * attendance type then position is not preserved and position will be null in that case.
	   *//*
					if(attendance){
						int memberBallotAttendanceCount=MemberBallotAttendance.findMembersByAttendanceCount(session,questionType,attendance,round,locale.toString());
						if(memberBallotAttendanceCount == 0){
							//response.sendError(404, "Report cannot be generated at this stage.");
		            		MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "ballot.memberpreballot.incomplete", locale.toString());
		            		if(message != null) {
		            			if(!message.getValue().isEmpty()) {
		            				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
		            			} else {
		            				response.getWriter().println("<h3>preballot for this round is not completed.</h3>");
		            			}
		            		} else {
		            			response.getWriter().println("<h3>preballot for this round is not completed.</h3>");
		            		}
		            		return;
						}
					}else{
						int noOfPositionsNull=MemberBallotAttendance.checkPositionForNullValues(session,questionType,"false",round,"position",locale.toString());
						if(noOfPositionsNull>0){
							//response.sendError(404, "Report cannot be generated at this stage.");
		            		MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "ballot.memberpreballot.incomplete", locale.toString());
		            		if(message != null) {
		            			if(!message.getValue().isEmpty()) {
		            				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
		            			} else {
		            				response.getWriter().println("<h3>preballot for this round is not completed.</h3>");
		            			}
		            		} else {
		            			response.getWriter().println("<h3>preballot for this round is not completed.</h3>");
		            		}
		            		return;
						}
					}
					List<MemberBallotAttendance> memberBallotAttendances=null;
					if(attendance){
						memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"true",round,"position",locale.toString());
					}else{
						memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"false",round,"position",locale.toString());
					}				
	            	List<MemberBallotVO> memberPreBallots = new ArrayList<MemberBallotVO>();
	    			for(MemberBallotAttendance i : memberBallotAttendances) {
	    				MemberBallotVO memberPreBallot = new MemberBallotVO();	    				
	    				memberPreBallot.setPosition(FormaterUtil.formatNumberNoGrouping(i.getPosition(), locale.toString()));
	    				memberPreBallot.setMember(i.getMember().findFirstLastName());	
	    				memberPreBallots.add(memberPreBallot);
	    			}

	            	StarredQuestionCouncilBallotXmlVO data = new StarredQuestionCouncilBallotXmlVO();
	            	data.setRound(FormaterUtil.formatNumberNoGrouping(round, locale.toString()));
	            	data.setMemberBallots(memberPreBallots);
	            	data.setAttendance(strAttendance);
	            	data.setSessionPlace(session.getPlace().getPlace());		            	
	            	data.setDate(FormaterUtil.formatDateToString(new Date(), ApplicationConstants.BALLOT_REPORT_DATE_FORMAT, locale.toString()));

	            	//generate report
	            	if(reportFormat.equals("WORD")) {
	            		reportFile = generateReportUsingFOP(data, "template_starredquestion_council_preballot_report_word", reportFormat, "preballot_r" + round, locale.toString());
	            	} else {
	            		reportFile = generateReportUsingFOP(data, "template_starredquestion_council_preballot_report", reportFormat, "preballot_r" + round, locale.toString());
	            	}        		
	        		System.out.println("Pre-Ballot Report for Round " + round + " generated successfully in " + reportFormat + " format!");

	        		openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
				} catch (IOException e) {						
					e.printStackTrace();
				}catch (ELSException e) {
					logger.error(e.getMessage());					
				}
			} else{
				logger.error("**** Check request parameters 'attendance,questionType,session,round,outputFormat' for empty values ****");
				try {
					response.getWriter().println("<h3>Check request parameters 'attendance,questionType,session,round,outputFormat' for empty values</h3>");
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		} else{
			logger.error("**** Check request parameters 'attendance,questionType,session,round,outputFormat' for null values ****");
			try {
				response.getWriter().println("<h3>Check request parameters 'attendance,questionType,session,round,outputFormat' for null values</h3>");
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}							
	}*/

	/****** Member Ballot(Council) Member Ballot report in PDF or WORD using FOP ****/
	@RequestMapping(value="/memberballot/report",method=RequestMethod.GET)
	public void createMemberBallotReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){		
		File reportFile = null;

		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String strAttendance=request.getParameter("attendance");
		String strQuestionType=request.getParameter("questionType");
		String strSession=request.getParameter("session");
		String strRound=request.getParameter("round");
		String strGroup=request.getParameter("group");
		String strAnsweringDate=request.getParameter("answeringDate");
		String reportFormat=request.getParameter("outputFormat");

		if(strSession!=null&&strQuestionType!=null &&strAttendance!=null&&strRound!=null&&reportFormat!=null){

			if((!strSession.isEmpty())&&(!strQuestionType.isEmpty())&&(!strAttendance.isEmpty())&&(!strRound.isEmpty())&&(!reportFormat.isEmpty())){

				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
				Boolean attendance=Boolean.parseBoolean(strAttendance);
				Integer round=Integer.parseInt(strRound);

				try {
					/**** check whether member ballot is done or not for given round  ****/
					int count=MemberBallot.findEntryCount(session,questionType,round,attendance,locale.toString());
					Map<String, String[]> parametersMap = new HashMap<String, String[]>();
					if(count>0){

						parametersMap.put("locale", new String[]{locale.toString()});
						parametersMap.put("sessionId", new String[]{session.getId().toString()});
						parametersMap.put("deviceTypeId", new String[]{questionType.getId().toString()});
						parametersMap.put("round", new String[]{round.toString()});
						parametersMap.put("attendance", new String[]{attendance.toString()});
						List memberBallotsReport = null;

						if(strGroup==null&&strAnsweringDate==null){
							parametersMap.put("locale", new String[]{locale.toString()});
							memberBallotsReport = org.mkcl.els.domain.Query.findReport("MEMBERBALLOT_FINAL_REPORT_BALLOT_WITH_CHOICE_PLAIN_QUERY", parametersMap);


						}else if(strGroup.equals("-")&&strAnsweringDate.equals("-")){

							parametersMap.put("locale", new String[]{locale.toString()});
							memberBallotsReport = org.mkcl.els.domain.Query.findReport("MEMBERBALLOT_FINAL_REPORT_BALLOT_WITH_CHOICE_PLAIN_QUERY", parametersMap);


						}else if((!strGroup.equals("-"))&&(!strAnsweringDate.equals("-"))){

							QuestionDates answeringDate=Question.findById(QuestionDates.class,Long.parseLong(strAnsweringDate));
							Group group=Group.findById(Group.class,Long.parseLong(strGroup));

							parametersMap.put("locale", new String[]{locale.toString()});
							parametersMap.put("qdId", new String[]{answeringDate.getId().toString()});
							parametersMap.put("groupId", new String[]{group.getId().toString()});
							memberBallotsReport = org.mkcl.els.domain.Query.findReport("MEMBERBALLOT_FINAL_REPORT_BALLOT_WITH_CHOICE_ANSWERINGDATE_GROUP_QUERY", parametersMap);


						}else if((!strGroup.equals("-"))&&strAnsweringDate.equals("-")){

							Group group=Group.findById(Group.class,Long.parseLong(strGroup));

							parametersMap.put("locale", new String[]{locale.toString()});
							parametersMap.put("groupId", new String[]{group.getId().toString()});
							memberBallotsReport = org.mkcl.els.domain.Query.findReport("MEMBERBALLOT_FINAL_REPORT_BALLOT_WITH_CHOICE_GROUP_QUERY", parametersMap);


						}else if((strGroup.equals("-"))&&!strAnsweringDate.equals("-")){
							QuestionDates answeringDate=Question.findById(QuestionDates.class,Long.parseLong(strAnsweringDate));

							parametersMap.put("locale", new String[]{locale.toString()});
							parametersMap.put("qdId", new String[]{answeringDate.getId().toString()});
							memberBallotsReport = org.mkcl.els.domain.Query.findReport("MEMBERBALLOT_FINAL_REPORT_BALLOT_WITH_CHOICE_ANSWERINGDATE_QUERY", parametersMap);		
						}

						Object[] xmlData = new Object[]{FormaterUtil.formatNumberNoGrouping(round, locale.toString()), strAttendance, session.getPlace().getPlace(), FormaterUtil.formatDateToString(new Date(), ApplicationConstants.BALLOT_REPORT_DATE_FORMAT, locale.toString()), memberBallotsReport};

						//generate report
						if(reportFormat.equals("WORD")) {
							reportFile = generateReportUsingFOP(xmlData, "template_starredquestion_council_ballot_report_word", reportFormat, "memberballot_r" + round, locale.toString());
						} else {
							reportFile = generateReportUsingFOP(xmlData, "template_starredquestion_council_ballot_report", reportFormat, "memberballot_r" + round, locale.toString());
						}        		
						System.out.println("Member Ballot Report for Round " + round + " generated successfully in " + reportFormat + " format!");
						xmlData = null;
						openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);					
					} else {
						//response.sendError(404, "Report cannot be generated at this stage.");
						MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "ballot.memberpreballot.incomplete", locale.toString());
						if(message != null) {
							if(!message.getValue().isEmpty()) {
								response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
							} else {
								response.getWriter().println("<h3>member ballot for this round is not completed.</h3>");
							}
						} else {
							response.getWriter().println("<h3>member ballot for this round is not completed.</h3>");
						}
						return ;
					}
				} catch (IOException e) {						
					String message = e.getMessage();

					if(message == null){
						message = "There is some problem, request may not complete successfully.";
					}
					model.addAttribute("error", message);
					e.printStackTrace();

				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
				} catch (Exception e) {
					String message = e.getMessage();

					if(message == null){
						message = "There is some problem, request may not complete successfully.";
					}
					model.addAttribute("error", message);
					e.printStackTrace();

					try {
						response.getWriter().println("<h3>Check request parameters 'attendance,questionType,session,round,outputFormat' for empty values</h3>");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}								
			}else{
				logger.error("**** Check request parameters 'attendance,questionType,session,round,outputFormat' for empty values ****");
				try {
					response.getWriter().println("<h3>Check request parameters 'attendance,questionType,session,round,outputFormat' for empty values</h3>");
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}

		}else{
			logger.error("**** Check request parameters 'attendance,questionType,session,round,outputFormat' for null values ****");
			try {
				response.getWriter().println("<h3>Check request parameters 'attendance,questionType,session,round,outputFormat' for null values</h3>");
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}					
	}

	/****** Member Ballot(Council) Pre Ballot report in PDF or WORD using FOP ****/
	@RequestMapping(value="/memberballot/preballotreport",method=RequestMethod.GET)
	public @ResponseBody void councilStarredPreBallotReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		File reportFile = null;		

		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		String strAttendance=request.getParameter("attendance");
		String strQuestionType=request.getParameter("questionType");
		String strSession=request.getParameter("session");
		String strRound=request.getParameter("round");
		String reportFormat=request.getParameter("outputFormat");
		if(strAttendance!=null&&strQuestionType!=null&&strSession!=null&&strRound!=null&&reportFormat!=null) {
			if(!strAttendance.isEmpty()&&!strQuestionType.isEmpty()&&!strSession.isEmpty()&&!strRound.isEmpty()&&!reportFormat.isEmpty()) {
				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
				Boolean attendance=Boolean.parseBoolean(strAttendance);
				Integer round=Integer.parseInt(strRound);		
				try {
					/**** Before Pre-Ballot we must first check if atleast once submit button has been 
					 * clicked or not.Because if submit button is not clicked for a particular round and
					 * attendance type then position is not preserved and position will be null in that case.
					 */
					if(attendance){
						int memberBallotAttendanceCount=MemberBallotAttendance.findMembersByAttendanceCount(session,questionType,attendance,round,locale.toString());
						if(memberBallotAttendanceCount == 0){
							//response.sendError(404, "Report cannot be generated at this stage.");
							MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "ballot.memberpreballot.incomplete", locale.toString());
							if(message != null) {
								if(!message.getValue().isEmpty()) {
									response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
								} else {
									response.getWriter().println("<h3>preballot for this round is not completed.</h3>");
								}
							} else {
								response.getWriter().println("<h3>preballot for this round is not completed.</h3>");
							}
							return;
						}
					}else{
						int noOfPositionsNull=MemberBallotAttendance.checkPositionForNullValues(session,questionType,"false",round,"position",locale.toString());
						if(noOfPositionsNull>0){
							//response.sendError(404, "Report cannot be generated at this stage.");
							MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "ballot.memberpreballot.incomplete", locale.toString());
							if(message != null) {
								if(!message.getValue().isEmpty()) {
									response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
								} else {
									response.getWriter().println("<h3>preballot for this round is not completed.</h3>");
								}
							} else {
								response.getWriter().println("<h3>preballot for this round is not completed.</h3>");
							}
							return;
						}
					}

					Map<String, String[]> parametersMap = new HashMap<String, String[]>();
					parametersMap.put("locale", new String[]{locale.toString()});
					parametersMap.put("sessionId", new String[]{session.getId().toString()});
					parametersMap.put("deviceTypeId", new String[]{questionType.getId().toString()});
					parametersMap.put("round", new String[]{round.toString()});
					parametersMap.put("attendance", new String[]{((attendance==true)? "true":"false")});					

					List memberBallotAttendances = org.mkcl.els.domain.Query.findReport("MEMBERBALLOT_PREBALLOT_REPORT_QUERY", parametersMap);

					/*if(attendance){
						memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"true",round,"position",locale.toString());
					}else{
						memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"false",round,"position",locale.toString());
					}*/

					List<MemberBallotVO> memberPreBallots = new ArrayList<MemberBallotVO>();
					for(Object i : memberBallotAttendances) {

						/*MemberBallotVO memberPreBallot = new MemberBallotVO();	    				
	    				memberPreBallot.setPosition(FormaterUtil.formatNumberNoGrouping(i.getPosition(), locale.toString()));
	    				memberPreBallot.setMember(i.getMember().findFirstLastName());	
	    				memberPreBallots.add(memberPreBallot);*/
					}

					Object[] xmlData = new Object[]{FormaterUtil.formatNumberNoGrouping(round, locale.toString()), strAttendance, session.getPlace().getPlace(), FormaterUtil.formatDateToString(new Date(), ApplicationConstants.BALLOT_REPORT_DATE_FORMAT, locale.toString()), memberBallotAttendances};	            	

					//generate report
					if(reportFormat.equals("WORD")) {
						//reportFile = generateReportUsingFOP(data, "template_starredquestion_council_preballot_report_word", reportFormat, "preballot_r" + round, locale.toString());
						reportFile = generateReportUsingFOP(xmlData, "template_starredquestion_council_preballot_report_word", reportFormat, "preballot_r" + round, locale.toString());

					} else {
						//reportFile = generateReportUsingFOP(data, "template_starredquestion_council_preballot_report", reportFormat, "preballot_r" + round, locale.toString());
						reportFile = generateReportUsingFOP(xmlData, "template_starredquestion_council_preballot_report", reportFormat, "preballot_r" + round, locale.toString());
					}        		
					System.out.println("Pre-Ballot Report for Round " + round + " generated successfully in " + reportFormat + " format!");

					openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
				} catch (IOException e) {						
					String message = e.getMessage();

					if(message == null){
						message = "There is some problem, request may not complete successfully.";
					}
					model.addAttribute("error", message);

				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
				} catch (Exception e) {
					String message = e.getMessage();

					if(message == null){
						message = "There is some problem, request may not complete successfully.";
					}
					model.addAttribute("error", message);
					e.printStackTrace();
				}
			} else{
				logger.error("**** Check request parameters 'attendance,questionType,session,round,outputFormat' for empty values ****");
				try {
					response.getWriter().println("<h3>Check request parameters 'attendance,questionType,session,round,outputFormat' for empty values</h3>");
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		} else{
			logger.error("**** Check request parameters 'attendance,questionType,session,round,outputFormat' for null values ****");
			try {
				response.getWriter().println("<h3>Check request parameters 'attendance,questionType,session,round,outputFormat' for null values</h3>");
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}							
	}

	/****** Member Ballot For Council Ends ******/
	/********************************************/
	@RequestMapping(value="/preballot",method=RequestMethod.GET)
	public String preBallot(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String retVal = "ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try {
			model.addAttribute("formater", new FormaterUtil());
			model.addAttribute("locale", locale.toString());
			model.addAttribute("preballot", "yes");
			
			/** Create HouseType */
			String strHouseType = request.getParameter("houseType");
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			model.addAttribute("houseType", houseType.getType());
			
			/** Create SessionType */
			String strSessionTypeId = request.getParameter("sessionType");
			SessionType sessionType = SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));

			/** Create year */
			String strYear = request.getParameter("sessionYear");
			Integer year = Integer.valueOf(strYear);

			/** Create Session */
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			model.addAttribute("sessionId", session.getId());
			
			/** Create DeviceType */
			DeviceType deviceType = null;
			String strDeviceType = request.getParameter("questionType");
			if(strDeviceType == null){
				strDeviceType = request.getParameter("deviceType");				
			}	
			deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			model.addAttribute("deviceType", deviceType.getType());
			model.addAttribute("deviceName", deviceType.getName());
			model.addAttribute("deviceId", deviceType.getId());
			
			/** Create Group */
			String strGroup = request.getParameter("group");
			Group  group=null;
			if(strGroup!=null && !strGroup.isEmpty()){
				group = Group.findById(Group.class, new Long(strGroup));
			}
			
			/** Create answeringDate */
			String strAnsweringDate = request.getParameter("answeringDate");
			Date answeringDate = null;
			CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				QuestionDates questionDates = QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
				answeringDate = questionDates.getAnsweringDate();
			}
			else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
					deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}
			else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION) || deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)){
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}
			
			model.addAttribute("strAnsweringDate", strAnsweringDate);
			if(answeringDate!=null) {
				model.addAttribute("answeringDate", FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.REPORT_DATEFORMAT, locale.toString()));
			}

			/**** Validate whether pre-ballot can be created for bill ****/
			if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
				boolean isBallotAllowedToCreate = validateBallotCreationForBill(session, deviceType, answeringDate, locale.toString());
				if(isBallotAllowedToCreate==false) {
					model.addAttribute("isBallotAllowedToCreate", isBallotAllowedToCreate);
					return "ballot/nonofficial_bill_preballot";
				}
			}

			/** Route PreBallot creation to appropriate handler method - based on house type*/
			if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					retVal = this.preBallotHDQCouncil(model, session, deviceType, group, answeringDate, locale.toString());
				}
				else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
					retVal = this.preBallotHDSCouncil(model, session, deviceType, group, answeringDate, locale.toString());
				}
				else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
					retVal = this.resolutionNonOfficialMemberPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}
				else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
					retVal = this.billNonOfficialPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}				
			}
			else if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ){
					retVal = this.preBallotHDQAssembly(model, session, deviceType, group, answeringDate, locale.toString());
				}
				else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
					retVal = this.preBallotHDSAssembly(model, session, deviceType, answeringDate, locale.toString());	
				}
				else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					retVal = this.resolutionNonOfficialPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}
				else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					retVal = this.billNonOfficialPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}
			}
			
			/** Route PreBallot creation to appropriate handler method - based on processing mode*/
			PROCESSING_MODE processingMode = Question.getProcessingMode(session);
			// If retVal is not already set in the above conditional execution, only then use the following logic.
			if(retVal.equals("ballot/error") && processingMode == PROCESSING_MODE.LOWERHOUSE) {
				String houseTypeType = houseType.getType();
				String upperCaseHouseTypeType = houseTypeType.toUpperCase();
				
				StringBuffer sb = new StringBuffer();
				sb.append("QUESTION_STARRED_BALLOT_NO_OF_ROUNDS_");
				sb.append(upperCaseHouseTypeType);
				
				String parameterName = sb.toString();
				
				/** Add number of rounds to model */
				CustomParameter noOfRoundsParameter = CustomParameter.findByName(CustomParameter.class, parameterName, "");
				if(noOfRoundsParameter!=null && noOfRoundsParameter.getValue()!=null && !noOfRoundsParameter.getValue().isEmpty()) {
					model.addAttribute("noOfRounds", noOfRoundsParameter.getValue());
				}
				else {
					model.addAttribute("noOfRounds", ApplicationConstants.OUESTION_BALLOT_NO_OF_ROUNDS);
				}
				
				/** Add localized answeringDate to model */
				String localizedAnsweringDate = null;
				CustomParameter answeringDateFormatParameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT_HYPHEN", "");
				if(answeringDateFormatParameter!=null && answeringDateFormatParameter.getValue()!=null && !answeringDateFormatParameter.getValue().isEmpty()) {
					localizedAnsweringDate = FormaterUtil.formatDateToString(answeringDate, answeringDateFormatParameter.getValue(), locale.toString());
				}
				else {
					localizedAnsweringDate = FormaterUtil.formatDateToString(answeringDate, "dd-MM-yyyy", locale.toString());
				}
				model.addAttribute("answeringDate", localizedAnsweringDate);
				
				CustomParameter serverDateTimeFormat = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
				if(serverDateTimeFormat != null){
					model.addAttribute("formattedCurrentDate", FormaterUtil.formatDateToString((new Date()), serverDateTimeFormat.getValue(), locale.toString()));
				}
				else {
					model.addAttribute("formattedCurrentDate", FormaterUtil.formatDateToString((new Date()), "dd/MM/yyyy HH:mm:ss", locale.toString()));
				}
				retVal = this.starredPreBallot(model, session, deviceType, answeringDate, locale.toString());
			}
		}
		catch(Exception e) {
			logger.error("error", e);
			model.addAttribute("type", "INSUFFICIENT_PARAMETERS_FOR_PRE_BALLOT_CREATION");
			retVal = "ballot/error";
		}
		return retVal;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/ballotfooter", method=RequestMethod.GET)
	public @ResponseBody List getBallotFooter(HttpServletRequest request, HttpServletResponse response, Locale locale){
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("locale", new String[]{locale.toString()});
		
		try{
			params.put("sessionId", new String[]{request.getParameter("session")});
			params.put("deviceTypeId", new String[]{request.getParameter("device")});
			params.put("answeringDate", new String[]{request.getParameter("answerDate")});
			List report = Query.findReport(request.getParameter("report"), params);
			
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			StringBuffer designation = new StringBuffer("");
			for(UserGroup ug : userGroups){
				if(ug != null){
					designation.append(ug.getUserGroupType().getName());
					break;
				}
			}
			
			((Object[])report.get(0))[2] = designation.toString() + ", " + (this.getCurrentUser().getTitle()+" "+this.getCurrentUser().getFirstName()+" " + this.getCurrentUser().getMiddleName() + " " +this.getCurrentUser().getLastName());
			
			return report;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}	
	
	private boolean validateBallotCreationForBill(Session session,
			DeviceType deviceType, Date answeringDate, String locale) {
		boolean isAllowedToCreateBallot = true;
		List<Bill> bills;
		try {
			bills = Bill.findPendingBillsBeforeBalloting(session, deviceType, answeringDate, locale);
			if(!bills.isEmpty()) {
				isAllowedToCreateBallot = false;
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isAllowedToCreateBallot;
	}

	private String preBallotHDQCouncil(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> ballotVOs = new ArrayList<BallotVO>();
		try {
			PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
			if(preBallot != null){
				CustomParameter csptPreBallotRecreate = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase() + "_" + session.getHouse().getType().getType().toUpperCase() +"_PREBALLOT_RECREATE_IF_EXISTS", "");
				if(csptPreBallotRecreate == null || csptPreBallotRecreate.getValue().equals("YES")){
					Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
					if(ballot == null){
						preBallot.remove();
						ballotVOs = Ballot.findPreBallotVO(session, deviceType, answeringDate, locale);
					}else{
						if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
							
							Map<String, String[]> parameters = new HashMap<String, String[]>();
							parameters.put("preBallotId", new String[]{preBallot.getId().toString()});
							parameters.put("locale", new String[]{locale.toString()});
							
							List data = Query.findReport("HDQ_COUNCIL_PREBALLOT_VIEW", parameters);
							
							if(data != null && !data.isEmpty()){
								for(Object o : data){
									Object[] objArr = (Object[]) o;
									BallotVO vo = new BallotVO();
									if(objArr[0] != null){
										vo.setQuestionNumber(new Integer(objArr[0].toString()));
									}
									if(objArr[1] != null){
										vo.setMemberName(objArr[1].toString());
									}
									if(objArr[2] != null){
										vo.setQuestionSubject(objArr[2].toString());
									}
									ballotVOs.add(vo);
								}
							}
						}
					}
				}else{
					if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
						
						Map<String, String[]> parameters = new HashMap<String, String[]>();
						parameters.put("preBallotId", new String[]{session.getId().toString()});
						parameters.put("locale", new String[]{locale.toString()});
						
						List data = Query.findReport("HDQ_COUNCIL_PREBALLOT_VIEW", parameters);
						
						if(data != null && !data.isEmpty()){
							for(Object o : data){
								Object[] objArr = (Object[]) o;
								BallotVO vo = new BallotVO();
								if(objArr[0] != null){
									vo.setQuestionNumber(new Integer(objArr[0].toString()));
								}
								if(objArr[1] != null){
									vo.setMemberName(objArr[1].toString());
								}
								if(objArr[2] != null){
									vo.setQuestionSubject(objArr[2].toString());
								}
								ballotVOs.add(vo);
							}
						}
					}
				}
			}else{
				ballotVOs = Ballot.findPreBallotVO(session, deviceType, answeringDate, locale);
			}
			
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/hdqPreBallotCouncil";//"ballot/halfhourq_preballot";

		} catch (ELSException e) {			
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error"; 
		}
	}
	
	private String preBallotHDSCouncil(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> ballotVOs = new ArrayList<BallotVO>();
		try {
			PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
			if(preBallot != null){
				CustomParameter csptPreBallotRecreate = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase() + "_" + session.getHouse().getType().getType().toUpperCase() +"_PREBALLOT_RECREATE_IF_EXISTS", "");
				if(csptPreBallotRecreate == null || csptPreBallotRecreate.getValue().equals("YES")){
					Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
					if(ballot == null){
						preBallot.remove();
						ballotVOs = Ballot.findPreBallotVO(session, deviceType, answeringDate, locale);
					}else{
						if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
							Map<String, String[]> parameters = new HashMap<String, String[]>();
							parameters.put("preBallotId", new String[]{preBallot.getId().toString()});
							parameters.put("locale", new String[]{locale.toString()});
							
							List data = Query.findReport("HDS_COUNCIL_PREBALLOT_VIEW", parameters);
							
							if(data != null && !data.isEmpty()){
								for(Object o : data){
									Object[] objArr = (Object[]) o;
									BallotVO vo = new BallotVO();
									if(objArr[0] != null){
										vo.setQuestionNumber(new Integer(objArr[0].toString()));
									}
									if(objArr[1] != null){
										vo.setMemberName(objArr[1].toString());
									}
									if(objArr[2] != null){
										vo.setQuestionSubject(objArr[2].toString());
									}
									ballotVOs.add(vo);
								}
							}
						}
					}
				}else{
					if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
						Map<String, String[]> parameters = new HashMap<String, String[]>();
						parameters.put("preBallotId", new String[]{session.getId().toString()});
						parameters.put("locale", new String[]{locale.toString()});
						
						List data = Query.findReport("HDS_COUNCIL_PREBALLOT_VIEW", parameters);
						
						if(data != null && !data.isEmpty()){
							for(Object o : data){
								Object[] objArr = (Object[]) o;
								BallotVO vo = new BallotVO();
								if(objArr[0] != null){
									vo.setQuestionNumber(new Integer(objArr[0].toString()));
								}
								if(objArr[1] != null){
									vo.setMemberName(objArr[1].toString());
								}
								if(objArr[2] != null){
									vo.setQuestionSubject(objArr[2].toString());
								}
								ballotVOs.add(vo);
							}
						}
					}
				}
			}else{
				ballotVOs = Ballot.findPreBallotVO(session, deviceType, answeringDate, locale);
			}
			
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/hdsPreBallotCouncil";//"ballot/halfhourq_preballot";

		} catch (ELSException e) {			
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error"; 
		}
	}

	private String preBallotHDSAssembly(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> ballotVOs;
		try {
			ballotVOs = Ballot.findPreBallotVO(session, deviceType, answeringDate, locale);
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/hds_preballot_assembly";
		} catch (ELSException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error";
		}
	}
	
	private String preBallotHDQAssembly(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) {
		List<BallotMemberVO> ballotVOs;
		try {
			ballotVOs = Ballot.findPreBallotHDQAssembly(session, deviceType, group, answeringDate, locale);
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/hdq_preballot_assembly";
		} catch (ELSException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error";
		}
	}
	

	private String resolutionNonOfficialMemberPreBallot(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {

		List<BallotVO> ballotVOs;
		try {
			ballotVOs = Ballot.findResolutionCouncilPreBallotVO(session, deviceType, answeringDate, locale);
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/nonofficial_member_preballot";
		} catch (ELSException e) {

			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error";
		}
	}

	private String starredPreBallot(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
			
		
		List<StarredBallotVO> ballotVOs = Ballot.findStarredPreBallotVOs(session, deviceType, answeringDate, locale);
		ballotVOs = sortStarredPreBallotVOs(ballotVOs);
		Group group = Group.find(session, answeringDate, locale);
		model.addAttribute("groupNo", 
				FormaterUtil.formatNumberNoGrouping(group.getNumber(), locale));
		
		/**** total number of members ****/
		if(ballotVOs!=null) {
			model.addAttribute("totalMembers", FormaterUtil.formatNumberNoGrouping(ballotVOs.size(), locale));
			
			Integer serialNo = 0;
			Integer noOfQuestions = 0;
			for(StarredBallotVO ballotVO : ballotVOs) {
				++serialNo;
				ballotVO.setSerialNo(FormaterUtil.formatNumberNoGrouping(serialNo, locale));
				
				List<QuestionSequenceVO> sequenceVOs = ballotVO.getQuestionSequenceVOs();
				int size = sequenceVOs.size();
				noOfQuestions = noOfQuestions + size;
			}			
			model.addAttribute("totalNoOfQuestions", 
					FormaterUtil.formatNumberNoGrouping(noOfQuestions, locale));
		}
		
		model.addAttribute("ballotVOs", ballotVOs);
		return "ballot/starred_preballot";
	}
	
	private static List<StarredBallotVO> sortStarredPreBallotVOs(final List<StarredBallotVO> ballotVOs) {
		List<StarredBallotVO> newBallotVOs = new ArrayList<StarredBallotVO>();
		newBallotVOs.addAll(ballotVOs);
		
		Comparator<StarredBallotVO> c = new Comparator<StarredBallotVO>() {

			@Override
			public int compare(StarredBallotVO vo1, StarredBallotVO vo2) {
				List<QuestionSequenceVO> qsVOs1 = vo1.getQuestionSequenceVOs();
				List<QuestionSequenceVO> qsVOs2 = vo2.getQuestionSequenceVOs();
				
				QuestionSequenceVO qsVO1 = qsVOs1.get(0);
				QuestionSequenceVO qsVO2 = qsVOs2.get(0);
				
				Integer number1 = qsVO1.getNumber();
				Integer number2 = qsVO2.getNumber();
				
				return number1.compareTo(number2);
			}
			
		};
		
		Collections.sort(newBallotVOs, c);
		return newBallotVOs;
	}

	private String resolutionNonOfficialPreBallot(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> ballotVOs;
		try {
			ballotVOs = Ballot.findPreBallotVO(session, deviceType, answeringDate, locale);
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/nonofficial_assembly_preballot";
		} catch (ELSException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error";
		}
	}

	private Boolean isResolutionMemberValidForCurrentDateChoiceSubmission(final Session session,
			final DeviceType deviceType,
			final Member member,
			final Date discussionDate, 
			final String locale){

		return (Resolution.findChoiceCountForGivenDiscussionDateOfMember(session, deviceType, member, discussionDate, locale) > 0)? true : false;
	}

	private void fillResolutionChoices(final ModelMap model, 
			final HttpServletRequest request, 
			final Locale locale) throws Exception{

		StringBuffer sb = new StringBuffer();
		/** Create HouseType */
		String strHouseType = request.getParameter("houseType");
		HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
		model.addAttribute("houseType", houseType.getType());
		sb.append("houseType="+strHouseType+"&");

		/** Create SessionType */
		String strSessionTypeId = request.getParameter("sessionType");
		SessionType sessionType = SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
		model.addAttribute("sessionType", sessionType.getType());
		sb.append("sessionType="+strSessionTypeId+"&");

		/** Create year */
		String strYear = request.getParameter("sessionYear");
		Integer year = Integer.valueOf(strYear);
		model.addAttribute("sessionYear", year);
		sb.append("sessionYear="+strYear+"&");

		/** Create Session */
		Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
		model.addAttribute("session", session.getId());

		/** Create DeviceType */
		DeviceType deviceType = null;
		String strDeviceType = request.getParameter("questionType");
		if(strDeviceType == null){
			strDeviceType = request.getParameter("deviceType");
		}

		deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
		model.addAttribute("deviceType", deviceType.getType());
		sb.append("deviceType="+strDeviceType+"&");

		/** Create answeringDate */
		String strAnsweringDate = request.getParameter("answeringDate");
		sb.append("answeringDate="+strAnsweringDate);
		Date answeringDate = null;
		if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
			CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
		}

		/** Add localized answeringDate to model */
		CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		String localizedAnsweringDate = FormaterUtil.formatDateToString(answeringDate, parameter.getValue(), locale.toString());
		model.addAttribute("answeringDate", localizedAnsweringDate);

		/** DeviceType & HouseType specific Ballot views */
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
			}
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale.toString());

				CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");

				Date startTime = FormaterUtil.formatStringToDate(session.getParameter(deviceType.getType() + "_submissionStartDate"),datePattern.getValue(), locale.toString());
				Date endTime = FormaterUtil.formatStringToDate(session.getParameter(deviceType.getType() + "_submissionEndDate"),datePattern.getValue(), locale.toString());

				Status ADMITTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_FINAL_ADMISSION, locale.toString());
				Status REJECTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_FINAL_REJECTION, locale.toString());
				Status REPEATADMITTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION, locale.toString());
				Status REPEATREJECTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_FINAL_REPEATREJECTION, locale.toString());
				Status[] internalStatuses = {ADMITTED,REPEATADMITTED, REJECTED, REPEATREJECTED};

				List<MasterVO> members = new ArrayList<MasterVO>();
				if(ballot != null){
					/**** Statuses ****/
					Status ballotStatus = Status.findByType(ApplicationConstants.RESOLUTION_PROCESSED_BALLOTED, locale.toString());
					Status discussionStatus = Status.findByType(ApplicationConstants.RESOLUTION_PROCESSED_TOBEDISCUSSED, locale.toString());
					List<Resolution> chosenResolutions = Resolution.findChosenResolutionsForGivenDate(session, deviceType, ballotStatus, discussionStatus, answeringDate, locale.toString());
					List<BallotEntry> ballotEntries = ballot.getBallotEntries();

					if(chosenResolutions.size() < ballotEntries.size()){

						for(BallotEntry bE : ballotEntries){
							if(bE != null){
								if(bE.getMember() != null){
									if(Resolution.getMemberChoiceCount(session, deviceType, bE.getMember().getId(), answeringDate, internalStatuses, startTime, endTime, locale.toString()) == 0){
										MasterVO masterVO = new MasterVO();
										masterVO.setId(bE.getMember().getId());
										masterVO.setName(bE.getMember().getFullname());
										masterVO.setValue(this.isResolutionMemberValidForCurrentDateChoiceSubmission(session, deviceType, bE.getMember(), answeringDate, locale.toString())? "0":"1");
										members.add(masterVO);
									}
								}
							}
						}

						Map<Long, List<MasterVO>> memberResolutions = new HashMap<Long, List<MasterVO>>();
						Map<Long, List<MasterVO>> memberChosenResolutions = new HashMap<Long, List<MasterVO>>();

						if(members != null && !members.isEmpty()){
							internalStatuses = null;
							Status[] tempInternalStatuses = {ADMITTED,REPEATADMITTED};
							for(MasterVO mv : members){
								List<MasterVO> resos = new ArrayList<MasterVO>();
								List<Resolution> resosList = Resolution.find(session, deviceType, mv.getId(), answeringDate, tempInternalStatuses, startTime, endTime,ApplicationConstants.ASC, locale.toString());
								for(Resolution r : resosList){
									MasterVO resMV = new MasterVO();
									resMV.setId(r.getId());
									resMV.setName(FormaterUtil.formatNumberNoGrouping(r.getNumber(), locale.toString()));
									resMV.setValue(r.getNoticeContent());
									resos.add(resMV);
									resMV = null;
								}
								memberResolutions.put(mv.getId(), resos);
								resos = null;

								List<MasterVO> resosChosen = new ArrayList<MasterVO>();
								List<Resolution> resosChosenList = Resolution.findResolutionsByDiscussionDateAndMember(session, deviceType, mv.getId(), answeringDate, tempInternalStatuses, startTime, endTime, ApplicationConstants.ASC, locale.toString());
								for(Resolution rc : resosChosenList){
									MasterVO resCMV = new MasterVO();
									resCMV.setId(rc.getId());
									resCMV.setName(FormaterUtil.formatNumberNoGrouping(rc.getNumber(), locale.toString()));
									resCMV.setValue(rc.getNoticeContent());
									resosChosen.add(resCMV);
									resCMV = null;
								}
								memberChosenResolutions.put(mv.getId(), resosChosen);
								resosChosen = null;

							}
						}

						model.addAttribute("members", members);
						model.addAttribute("memberRes", memberResolutions);
						model.addAttribute("memberChosenRes", memberChosenResolutions);
						model.addAttribute("choicedone", "no");
					}else{
						model.addAttribute("choicedone", "yes");
					}
				}
			}
		}
	}

	//Using for filling the choice of a resolution by the assistant
	//once he receives the choice of resolution from the member
	@RequestMapping(value="/fillresolutionchoices", method=RequestMethod.GET)
	public String getFillChoices(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String retVal = "ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try {			
			fillResolutionChoices(model, request, locale);
			retVal = "ballot/nonofficial_memberballot_choice";
		}
		catch(Exception e) {
			logger.error("error", e);
			model.addAttribute("type", "INSUFFICIENT_PARAMETERS_FOR_VIEWING_CHOICE_PAGE");
			retVal = "ballot/error";
		}
		return retVal;
	}

	@Transactional
	@RequestMapping(value="/fillresolutionchoice", method=RequestMethod.POST)
	public String postFillChoice(final ModelMap model, 
			final HttpServletRequest request, 
			final HttpServletResponse response, 
			final Locale locale){
		String retVal = "ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try {

			String[] strResIds = request.getParameterValues("choice");
			/** Create answeringDate */
			String strAnsweringDate = request.getParameter("answeringDate");
			CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			Date answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());


			if(strResIds != null){
				for(String resId : strResIds){
					if(!resId.isEmpty()){
						Resolution resolution = Resolution.findById(Resolution.class, new Long(resId));
						Status statusBalloted = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_PROCESSED_BALLOTED, locale.toString());
						Status statusToBeDiscussed = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_PROCESSED_TOBEDISCUSSED, locale.toString());
						Status recommendationStatus=Status.findByType(ApplicationConstants.RESOLUTION_PROCESSED_UNDERCONSIDERATION, locale.toString());
						resolution.setBallotStatus(statusBalloted);
						if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							resolution.setRecommendationStatusLowerHouse(recommendationStatus);
						}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							resolution.setRecommendationStatusUpperHouse(recommendationStatus);
						}
						resolution.setDiscussionStatus(statusToBeDiscussed);
						resolution.setDiscussionDate(answeringDate);
						resolution.simpleMerge();
					}
				}
			}
			retVal = "ballot/nonofficial_memberballot_choice";
		}
		catch(Exception e) {
			logger.error("error", e);
			model.addAttribute("type", "INSUFFICIENT_PARAMETERS_TO_SUBMIT_CHOICE");
			retVal = "ballot/error";
		}

		return retVal;
	}

	@RequestMapping(value="/showpatrakbhagdon", method=RequestMethod.GET)
	public String showpatrakbhagdon(final ModelMap model, 
			final HttpServletRequest request, 
			final Locale locale){
		Date date = new Date();//FormaterUtil.formatStringToDate(strAnsweringDate, ApplicationConstants.DB_DATEFORMAT, locale.toString());
		CustomParameter patrakBhagTwoDateFormat;
		CustomParameter dayFormat;

		String retVal = "ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try {

			patrakBhagTwoDateFormat = CustomParameter.findByFieldName(CustomParameter.class, "name", "PATRAK_BHAG_TWO_DATE_FORMAT", "");
			dayFormat = CustomParameter.findByFieldName(CustomParameter.class, "name", "DAY_OF_WEEK_FORMAT", "");

			String marathiCurrentDay = FormaterUtil.formatDateToString(date, dayFormat.getValue(), locale.toString());

			model.addAttribute("formattedCurrentDay", FormaterUtil.getDayInLocaleLanguage(marathiCurrentDay, locale.toString()));

			/*** To form the patrakbhag don formatted date ***/
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			Integer intDay = calendar.get(Calendar.DAY_OF_MONTH);
			Integer intMonth = calendar.get(Calendar.MONTH);
			Integer intYear = calendar.get(Calendar.YEAR);
			String formattedCurrentDate = FormaterUtil.formatNumberNoGrouping(intDay, locale.toString()) + 
					" " + FormaterUtil.getMonthInLocaleLanguage(intMonth, locale.toString()) + 
					", " + FormaterUtil.formatNumberNoGrouping(intYear, locale.toString());

			model.addAttribute("formattedCurrentDate", formattedCurrentDate);
			model.addAttribute("patrakbhag2indianDateFormat", FormaterUtil.getIndianDate(date, locale));

			/** Create HouseType */
			String strHouseType = request.getParameter("houseType");
			HouseType houseType =
					HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			model.addAttribute("houseType", houseType.getType());

			/** Create SessionType */
			String strSessionTypeId = request.getParameter("sessionType");
			SessionType sessionType =
					SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
			model.addAttribute("sessionType", sessionType.getType());

			/** Create year */
			String strYear = request.getParameter("sessionYear");
			Integer year = Integer.valueOf(strYear);
			model.addAttribute("sessionYear", year);

			/** Create Session */
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			model.addAttribute("session", session.getId());

			SessionPlace sessionPlace = session.getPlace();
			if(sessionPlace != null){
				model.addAttribute("sessionPlace", sessionPlace.getPlace());
			}

			AuthUser currentUser = getCurrentUser();
			String user = currentUser.getTitle() +" " + currentUser.getFirstName() + " " + currentUser.getLastName();
			model.addAttribute("authorityName", user);
			String userRole = null;
			for(Role r : currentUser.getRoles()){
				if(r != null){
					if(r.getType().startsWith("ROIS")){
						userRole = r.getLocalizedName();
					}
				}
			}

			model.addAttribute("userRole", userRole);

			/** Create DeviceType */
			DeviceType deviceType = null;
			String strDeviceType = request.getParameter("deviceType");
			deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			model.addAttribute("deviceType", deviceType.getType());

			model.addAttribute("footer", session.getParameter(deviceType.getType() + "_patrakbhag2footer"));

			/** Create answeringDate */
			Date answeringDate = null;
			String strAnsweringDate = request.getParameter("answeringDate");
			if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}

			String marathiDiscussionDay = FormaterUtil.formatDateToString(answeringDate, dayFormat.getValue(), locale.toString());
			model.addAttribute("formattedDiscussionDay", FormaterUtil.getDayInLocaleLanguage(marathiDiscussionDay, locale.toString()));

			/*** To form the patrakbhag don formatted date ***/
			calendar.setTime(answeringDate);
			intDay = calendar.get(Calendar.DAY_OF_MONTH);
			intMonth = calendar.get(Calendar.MONTH);
			intYear = calendar.get(Calendar.YEAR);
			String formattedAnsweringDate = FormaterUtil.formatNumberNoGrouping(intDay, locale.toString()) + 
					" " + FormaterUtil.getMonthInLocaleLanguage(intMonth, locale.toString()) + 
					", " + FormaterUtil.formatNumberNoGrouping(intYear, locale.toString());
			model.addAttribute("formattedDiscussionDate", formattedAnsweringDate);

			if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){		
				Map<String, String[]> parametersMap = new HashMap<String, String[]>();
				parametersMap.put("locale", new String[]{locale.toString()});
				parametersMap.put("sessionId", new String[]{session.getId().toString()});
				parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});

				List report = org.mkcl.els.domain.Query.findReport("RESOLUTION_PATRAKBHAGDON_VIEW", parametersMap);

				model.addAttribute("report", report);
				List<MasterVO> masterVOs = new ArrayList<MasterVO>();
				if(report != null){
					int size = report.size();							
					for(int i = 0; i <= size; i++){
						if(i < size){
							Object[] obj = (Object[]) report.get(i);
							((Object[])report.get(i))[2] = FormaterUtil.formatNumberNoGrouping(obj[2], locale.toString());
							obj = null;
						}
						MasterVO masterVO = new MasterVO();
						masterVO.setValue(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
						masterVOs.add(masterVO);
						masterVO = null;
					}
					model.addAttribute("counter",masterVOs);
				}

				retVal = "resolution/reports/patrakbhagtwo";
			}
		}
		catch(Exception e) {
			logger.error("error", e);
			model.addAttribute("type", "INSUFFICIENT_PARAMETERS_FOR_VIEWING_PATRAK_BHAG_TWO");
			retVal = "ballot/error";
		}

		return retVal;
	}

	/**** Motion Ballot Begins(Sandeep)****/
	@Transactional
	@RequestMapping(value="/motion/init",method=RequestMethod.GET)
	public String initMotionBallot(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){		
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}		
		
		try{
			/**** Session paramters ****/
			String strHouseType=request.getParameter("houseType");
			String strSessionType=request.getParameter("sessionType");
			String strSessionYear=request.getParameter("sessionYear");
			/**** Device Type parameters ****/
			String strDeviceType=request.getParameter("deviceType");			
			if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strDeviceType!=null){
				if((!strHouseType.isEmpty())
						&&(!strSessionType.isEmpty())
						&&(!strSessionYear.isEmpty())
						&&(!strDeviceType.isEmpty())){

					/**** Session ****/
					HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
					SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
					Integer sessionYear=Integer.parseInt(strSessionYear);
					Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);

					/**** Device Type ****/
					DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
					/**** Populating Model ****/
					model.addAttribute("session",session.getId());
					model.addAttribute("deviceType", deviceType.getId());
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
		return "ballot/motionballotinit";
	}

	/**** Attendance-View and Update****/
	@Transactional
	@RequestMapping(value="/attendance",method=RequestMethod.GET)
	public String markAttendanceMotion(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try {
			/**** Request parameters ****/
			String strSession=request.getParameter("session");
			String strDeviceType=request.getParameter("deviceType");
			String strAttendance=request.getParameter("attendance");
			if(strSession!=null&&strDeviceType!=null&&strAttendance!=null){
				if((!strSession.isEmpty())&&(!strDeviceType.isEmpty())
						&&(!strAttendance.isEmpty())){					
					/**** Session ****/
					Session session=Session.findById(Session.class,Long.parseLong(strSession));					
					/**** Device Type ****/
					DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));					
					/**** Attendance ****/
					Boolean attendance=Boolean.parseBoolean(strAttendance);						
					/**** Attendance Creation ****/
					String flag=MemberBallotAttendance.createAttendance(session,deviceType,locale.toString());
					if(!flag.contains("SUCCESS")){
						model.addAttribute("type", flag);
						return errorpage;
					}					
					/**** populating model ****/
					List<MemberBallotAttendance> allItems=null;
					List<MemberBallotAttendance> selectedItems=null;
					if(attendance){
						allItems=MemberBallotAttendance.findAll(session,deviceType,"false","id",locale.toString());
						selectedItems=MemberBallotAttendance.findAll(session,deviceType,"true","position",locale.toString());
					}else{
						allItems=MemberBallotAttendance.findAll(session,deviceType,"true","id",locale.toString());
						selectedItems=MemberBallotAttendance.findAll(session,deviceType,"false","position",locale.toString());
					}
					List<MemberBallotAttendance> eligibles=MemberBallotAttendance.findAll(session,deviceType,"","id",locale.toString());
					model.addAttribute("allItems",allItems);
					model.addAttribute("allItemsCount",allItems.size());
					model.addAttribute("selectedItems",selectedItems);
					model.addAttribute("selectedItemsCount",selectedItems.size());
					model.addAttribute("eligibles",eligibles);
					model.addAttribute("attendance",attendance);					
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
		}catch (ELSException e) {
			logger.error("failed",e);
			model.addAttribute("type","DB_EXCEPTION");
			return errorpage;
		}
		return "ballot/attendance";	
	}

	@Transactional
	@RequestMapping(value="/attendance",method=RequestMethod.PUT)
	public @ResponseBody String updateAttendanceMotion(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try {			
			String selectedItems=request.getParameter("items");
			String[] items=selectedItems.split(",");
			if(items.length!=0){
				String strAttendance=request.getParameter("attendance");
				String strDeviceType=request.getParameter("deviceType");
				String strSession=request.getParameter("session");
				Session session=Session.findById(Session.class,Long.parseLong(strSession));
				DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
				Boolean attendance=Boolean.parseBoolean(strAttendance);
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
						memberBallotAttendance.merge();						
					}
				}
				/**** setting attendance of not selected items ****/
				List<MemberBallotAttendance> memberBallotAttendances=MemberBallotAttendance.findAll(session, deviceType, "", "position", locale.toString());
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

	/**** Ballot Type - Create and View ****/
	@Transactional
	@RequestMapping(value="/create/view",method=RequestMethod.GET)
	public String createBallotAndView(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String errorpage="ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			String strAttendance=request.getParameter("attendance");
			String strDeviceType=request.getParameter("deviceType");
			String strSession=request.getParameter("session");
			if(strSession!=null&&strDeviceType!=null
					&&strAttendance!=null){
				if((!strSession.isEmpty())&&(!strDeviceType.isEmpty())
						&&(!strAttendance.isEmpty())){
					Session session=Session.findById(Session.class,Long.parseLong(strSession));
					DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
					Boolean attendance=Boolean.parseBoolean(strAttendance);
					String flag=Ballot.createBallot(session,deviceType,attendance,locale.toString());
					if(flag.contains("SUCCESS")){
						List<Reference> ballots=Ballot.viewBallot(session,deviceType,attendance,locale.toString());						
						model.addAttribute("ballots",ballots);
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
		return "ballot/viewballot";		
	}
	
	@Transactional
	@RequestMapping(value="/yaadi/updatebyyaadi", method=RequestMethod.GET)
	public @ResponseBody String updateByYaadi(HttpServletRequest request, ModelMap model, Locale locale){
		String retVal = "FAILURE";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try{
			/**** Session paramters ****/
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			
			/**** Device Type parameters ****/
			String strDeviceType = request.getParameter("deviceType");
			
			if(strHouseType != null && strSessionType != null && strSessionYear != null && strDeviceType != null){
				if((!strHouseType.isEmpty()) &&(!strSessionType.isEmpty()) &&(!strSessionYear.isEmpty()) && (!strDeviceType.isEmpty())){

					/**** Session ****/
					HouseType houseType = HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
					SessionType sessionType = SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
					Integer sessionYear = Integer.parseInt(strSessionYear);
					Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);

					/**** Device Type ****/
					DeviceType deviceType = DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
					
					/**** Populating Model ****/
					model.addAttribute("session",session.getId());
					model.addAttribute("deviceType", deviceType.getId());
					
					if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)){
						/**** Answering Date ****/
						String strAnsweringDate = request.getParameter("answeringDate");
						QuestionDates questionDates = QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
						Date answeringDate = questionDates.getAnsweringDate();
						
						Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale.toString());
						
						if(ballot != null){
							Status yaadiLaid = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_YAADILAID, locale.toString());
							String editedBy = this.getCurrentUser().getActualUsername();
							Date editedOn = new Date();
							List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
							String editedAs = null;
							for(UserGroup ug : userGroups){
								if(ug.getUserGroupType() != null){
									editedAs = ug.getUserGroupType().getName();
									break;
								}
							}
							int done = Ballot.updateByYaadi(ballot, yaadiLaid, editedAs, editedBy, editedOn);
							if(done == 0){
								retVal = "NO UPDATION";
							}else{
								retVal = "SUCCESS";
							}
						}
						
					}else if(deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
						
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			retVal = "INSUFFICIENT PARAMETERS FOR UPDATING BALLOTED QUESTIONS.";
		}
		
		return retVal;
	}
	
	/**** Motion Ballot Ends ****/
	/****************************/

	/**** Bill Ballot Handler Methods ****/
	private String billNonOfficialPreBallot(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> ballotVOs;
		try {
			ballotVOs = Ballot.findPreBallotVO(session, deviceType, answeringDate, locale);
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/nonofficial_bill_preballot";
		} catch (ELSException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error";
		}
	}
	
	/**** Log the details of the viewer ****/
	@Transactional
	private void logActivity(final String id, final HttpServletRequest request, final String locale){
		/**** Log the details of the viewer ****/
		ActivityLog activityLogger = new ActivityLog();
		activityLogger.setLocale(locale);
		Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), null);
		activityLogger.setEventClass("Ballot");
		activityLogger.setClassId(id);
		activityLogger.setCredetial(credential);
		activityLogger.setTimeOfAction(new Date());
		activityLogger.setLinkClicked(request.getServletPath());
		activityLogger.persist();
		/**** Log the details of the viewer ****/			
	}
	/***For Viewing of Preballot without recreating it**/
	
	@RequestMapping(value="/viewpreballot",method=RequestMethod.GET)
	public String viewPreBallot(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String retVal = "ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		
		try {
			model.addAttribute("formater", new FormaterUtil());
			model.addAttribute("locale", locale.toString());
			model.addAttribute("preballot", "yes");
			
			/** Create HouseType */
			String strHouseType = request.getParameter("houseType");
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			model.addAttribute("houseType", houseType.getType());
			
			/** Create SessionType */
			String strSessionTypeId = request.getParameter("sessionType");
			SessionType sessionType = SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));

			/** Create year */
			String strYear = request.getParameter("sessionYear");
			Integer year = Integer.valueOf(strYear);

			/** Create Session */
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			model.addAttribute("sessionId", session.getId());
			
			/** Create DeviceType */
			DeviceType deviceType = null;
			String strDeviceType = request.getParameter("questionType");
			if(strDeviceType == null){
				strDeviceType = request.getParameter("deviceType");				
			}	
			deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			model.addAttribute("deviceType", deviceType.getType());
			model.addAttribute("deviceName", deviceType.getName());
			model.addAttribute("deviceId", deviceType.getId());
			
			/** Create Group */
			String strGroup = request.getParameter("group");
			Group  group=null;
			if(strGroup!=null && !strGroup.isEmpty()){
				group = Group.findById(Group.class, new Long(strGroup));
			}
			
			/** Create answeringDate */
			String strAnsweringDate = request.getParameter("answeringDate");
			Date answeringDate = null;
			CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				QuestionDates questionDates = QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
				answeringDate = questionDates.getAnsweringDate();
			}
			else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
					deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}
			else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION) || deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)){
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}
			
			model.addAttribute("strAnsweringDate", strAnsweringDate);
			if(answeringDate!=null) {
				model.addAttribute("answeringDate", FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.REPORT_DATEFORMAT, locale.toString()));
			}

			/**** Validate whether pre-ballot can be created for bill ****/
			if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
				boolean isBallotAllowedToCreate = validateBallotCreationForBill(session, deviceType, answeringDate, locale.toString());
				if(isBallotAllowedToCreate==false) {
					model.addAttribute("isBallotAllowedToCreate", isBallotAllowedToCreate);
					return "ballot/nonofficial_bill_preballot";
				}
			}

			/** Route PreBallot creation to appropriate handler method - based on house type*/
			if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					retVal = this.viewPreBallotHDQCouncil(model, session, deviceType, group, answeringDate, locale.toString());
				}
				else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
					retVal = this.viewPreBallotHDSCouncil(model, session, deviceType, group, answeringDate, locale.toString());
				}
				// TODO
				else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
					retVal = this.resolutionNonOfficialMemberPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}
				else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
					retVal = this.billNonOfficialPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}				
			}
			else if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ){
					retVal = this.viewPreBallotHDQAssembly(model, session, deviceType, group, answeringDate, locale.toString());
				}
				
				//TODO
				else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
					retVal = this.preBallotHDSAssembly(model, session, deviceType, answeringDate, locale.toString());	
				}
				else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					retVal = this.resolutionNonOfficialPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}
				else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					retVal = this.billNonOfficialPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}
			}
			
			/** Route PreBallot creation to appropriate handler method - based on processing mode*/
			PROCESSING_MODE processingMode = Question.getProcessingMode(session);
			// If retVal is not already set in the above conditional execution, only then use the following logic.
			if(retVal.equals("ballot/error") && processingMode == PROCESSING_MODE.LOWERHOUSE) {
				String houseTypeType = houseType.getType();
				String upperCaseHouseTypeType = houseTypeType.toUpperCase();
				
				StringBuffer sb = new StringBuffer();
				sb.append("QUESTION_STARRED_BALLOT_NO_OF_ROUNDS_");
				sb.append(upperCaseHouseTypeType);
				
				String parameterName = sb.toString();
				
				/** Add number of rounds to model */
				CustomParameter noOfRoundsParameter = CustomParameter.findByName(CustomParameter.class, parameterName, "");
				if(noOfRoundsParameter!=null && noOfRoundsParameter.getValue()!=null && !noOfRoundsParameter.getValue().isEmpty()) {
					model.addAttribute("noOfRounds", noOfRoundsParameter.getValue());
				}
				else {
					model.addAttribute("noOfRounds", ApplicationConstants.OUESTION_BALLOT_NO_OF_ROUNDS);
				}
				
				/** Add localized answeringDate to model */
				String localizedAnsweringDate = null;
				CustomParameter answeringDateFormatParameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT_HYPHEN", "");
				if(answeringDateFormatParameter!=null && answeringDateFormatParameter.getValue()!=null && !answeringDateFormatParameter.getValue().isEmpty()) {
					localizedAnsweringDate = FormaterUtil.formatDateToString(answeringDate, answeringDateFormatParameter.getValue(), locale.toString());
				}
				else {
					localizedAnsweringDate = FormaterUtil.formatDateToString(answeringDate, "dd-MM-yyyy", locale.toString());
				}
				model.addAttribute("answeringDate", localizedAnsweringDate);
				
				CustomParameter serverDateTimeFormat = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
				if(serverDateTimeFormat != null){
					model.addAttribute("formattedCurrentDate", FormaterUtil.formatDateToString((new Date()), serverDateTimeFormat.getValue(), locale.toString()));
				}
				else {
					model.addAttribute("formattedCurrentDate", FormaterUtil.formatDateToString((new Date()), "dd/MM/yyyy HH:mm:ss", locale.toString()));
				}
				retVal = this.viewStarredPreBallot(model, session, deviceType, answeringDate, locale.toString());
			}
		}
		catch(Exception e) {
			logger.error("error", e);
			model.addAttribute("type", "INSUFFICIENT_PARAMETERS_FOR_PRE_BALLOT_CREATION");
			retVal = "ballot/error";
		}
		return retVal;
	}

	private String viewPreBallotHDQAssembly(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) {
		List<BallotMemberVO> ballotVOs;
		try {
			ballotVOs = Ballot.getPreBallotHDQAssembly(session, deviceType, group, answeringDate, locale);
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/hdq_preballot_assembly";
		} catch (ELSException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error";
		}
	}

	private String viewPreBallotHDSCouncil(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> ballotVOs = new ArrayList<BallotVO>();
		try {
			PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
			if(preBallot != null){
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("preBallotId", new String[]{preBallot.getId().toString()});
				parameters.put("locale", new String[]{locale.toString()});
				List data = Query.findReport("HDS_COUNCIL_PREBALLOT_VIEW", parameters);
				if(data != null && !data.isEmpty()){
					for(Object o : data){
						Object[] objArr = (Object[]) o;
						BallotVO vo = new BallotVO();
						if(objArr[0] != null){
							vo.setQuestionNumber(new Integer(objArr[0].toString()));
						}
						if(objArr[1] != null){
							vo.setMemberName(objArr[1].toString());
						}
						if(objArr[2] != null){
							vo.setQuestionSubject(objArr[2].toString());
						}
						ballotVOs.add(vo);
					}
				}
			}
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/hdsPreBallotCouncil";//"ballot/halfhourq_preballot";
		} catch (ELSException e) {			
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error"; 
		}

	}

	private String viewPreBallotHDQCouncil(final ModelMap model,final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> ballotVOs = new ArrayList<BallotVO>();
		try {
			PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
			if(preBallot != null){
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("preBallotId", new String[]{preBallot.getId().toString()});
				parameters.put("locale", new String[]{locale.toString()});
				List data = Query.findReport("HDQ_COUNCIL_PREBALLOT_VIEW", parameters);
				if(data != null && !data.isEmpty()){
					for(Object o : data){
						Object[] objArr = (Object[]) o;
						BallotVO vo = new BallotVO();
						if(objArr[0] != null){
							vo.setQuestionNumber(new Integer(objArr[0].toString()));
						}
						if(objArr[1] != null){
							vo.setMemberName(objArr[1].toString());
						}
						if(objArr[2] != null){
							vo.setQuestionSubject(objArr[2].toString());
						}
						ballotVOs.add(vo);
					}
				}
			}
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/hdqPreBallotCouncil";//"ballot/halfhourq_preballot";
		} catch (ELSException e) {			
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error"; 
		}
	}

	private String viewStarredPreBallot(final ModelMap model, 
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<StarredBallotVO> ballotVOs = Ballot.getPreBallotVOs(session, deviceType, answeringDate, locale);
		ballotVOs = sortStarredPreBallotVOs(ballotVOs);
		
		Group group = Group.find(session, answeringDate, locale);
		model.addAttribute("groupNo", 
				FormaterUtil.formatNumberNoGrouping(group.getNumber(), locale));
		PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
		if(preBallot != null){
			model.addAttribute("formattedCurrentDate", 
					FormaterUtil.formatDateToString((preBallot.getPreBallotDate()), "dd/MM/yyyy HH:mm:ss", locale.toString()));
		}
		/**** total number of members ****/
		if(ballotVOs!=null) {
			model.addAttribute("totalMembers", FormaterUtil.formatNumberNoGrouping(ballotVOs.size(), locale));
			
			Integer serialNo = 0;
			Integer noOfQuestions = 0;
			for(StarredBallotVO ballotVO : ballotVOs) {
				++serialNo;
				ballotVO.setSerialNo(FormaterUtil.formatNumberNoGrouping(serialNo, locale));
				
				List<QuestionSequenceVO> sequenceVOs = ballotVO.getQuestionSequenceVOs();
				int size = sequenceVOs.size();
				noOfQuestions = noOfQuestions + size;
			}			
			model.addAttribute("totalNoOfQuestions", 
					FormaterUtil.formatNumberNoGrouping(noOfQuestions, locale));
		}
		
		model.addAttribute("ballotVOs", ballotVOs);
		return "ballot/starred_preballot";

	}
	
	@RequestMapping(value="/previewpreballot", method=RequestMethod.GET)
	public String previewPreBallot(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String retVal = "ballot/error";
		
		try{
			/**** Log the activity ****/
			ActivityLog.logActivity(request, locale.toString());
		}catch(Exception e){
			logger.error("error", e);
		}
		try {
			model.addAttribute("formater", new FormaterUtil());
			model.addAttribute("locale", locale.toString());
				
			/** Create HouseType */
			String strHouseType = request.getParameter("houseType");
			HouseType houseType = HouseType.
					findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			model.addAttribute("houseType", houseType.getType());
			
			/** Create SessionType */
			String strSessionTypeId = request.getParameter("sessionType");
			SessionType sessionType = SessionType.
					findById(SessionType.class, Long.valueOf(strSessionTypeId));

			/** Create year */
			String strYear = request.getParameter("sessionYear");
			Integer year = Integer.valueOf(strYear);

			/** Create Session */
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			model.addAttribute("sessionId", session.getId());
			
			/** Create DeviceType */
			DeviceType deviceType = null;
			String strDeviceType = request.getParameter("questionType");
			if(strDeviceType == null){
				strDeviceType = request.getParameter("deviceType");				
			}	
			deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			model.addAttribute("deviceType", deviceType.getType());
			model.addAttribute("deviceName", deviceType.getName());
			model.addAttribute("deviceId", deviceType.getId());
			
			/** Create Group */
			String strGroup = request.getParameter("group");
			Group  group=null;
			if(strGroup!=null && !strGroup.isEmpty()){
				group = Group.findById(Group.class, new Long(strGroup));
			}
			
			/** Create answeringDate */
			String strAnsweringDate = request.getParameter("answeringDate");
			Date answeringDate = null;
			CustomParameter dbDateFormat = CustomParameter.
					findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				QuestionDates questionDates = QuestionDates.
						findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
				answeringDate = questionDates.getAnsweringDate();
			}
			else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
					deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}
			else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION) || deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)){
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}
						
			model.addAttribute("strAnsweringDate", strAnsweringDate);
			if(answeringDate!=null) {
				model.addAttribute("answeringDate", FormaterUtil.
						formatDateToString(answeringDate, ApplicationConstants.REPORT_DATEFORMAT, locale.toString()));
			}
		
			
			/** Route PreBallot creation to appropriate handler method - based on processing mode*/
			PROCESSING_MODE processingMode = Question.getProcessingMode(session);
			// If retVal is not already set in the above conditional execution, only then use the following logic.
			if(retVal.equals("ballot/error") && processingMode == PROCESSING_MODE.LOWERHOUSE && deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				String houseTypeType = houseType.getType();
				String upperCaseHouseTypeType = houseTypeType.toUpperCase();
				StringBuffer sb = new StringBuffer();
				sb.append("QUESTION_STARRED_BALLOT_NO_OF_ROUNDS_");
				sb.append(upperCaseHouseTypeType);
				
				String parameterName = sb.toString();
				
				/** Add number of rounds to model */
				CustomParameter noOfRoundsParameter = CustomParameter.
						findByName(CustomParameter.class, parameterName, "");
				if(noOfRoundsParameter!=null && noOfRoundsParameter.getValue()!=null 
						&& !noOfRoundsParameter.getValue().isEmpty()) {
					model.addAttribute("noOfRounds", noOfRoundsParameter.getValue());
				}
				else {
					model.addAttribute("noOfRounds", ApplicationConstants.OUESTION_BALLOT_NO_OF_ROUNDS);
				}
				
				/** Add localized answeringDate to model */
				String localizedAnsweringDate = null;
				CustomParameter answeringDateFormatParameter = CustomParameter.
						findByName(CustomParameter.class, "SERVER_DATEFORMAT_HYPHEN", "");
				if(answeringDateFormatParameter!=null && answeringDateFormatParameter.getValue()!=null 
						&& !answeringDateFormatParameter.getValue().isEmpty()) {
					localizedAnsweringDate = FormaterUtil.
							formatDateToString(answeringDate, answeringDateFormatParameter.getValue(), locale.toString());
				}
				else {
					localizedAnsweringDate = FormaterUtil.
							formatDateToString(answeringDate, "dd-MM-yyyy", locale.toString());
				}
				model.addAttribute("answeringDate", localizedAnsweringDate);
				
				CustomParameter serverDateTimeFormat = CustomParameter.
						findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
				if(serverDateTimeFormat != null){
					model.addAttribute("formattedCurrentDate", FormaterUtil.
							formatDateToString((new Date()), serverDateTimeFormat.getValue(), locale.toString()));
				}
				else {
					model.addAttribute("formattedCurrentDate", FormaterUtil.
							formatDateToString((new Date()), "dd/MM/yyyy HH:mm:ss", locale.toString()));
				}
				retVal = this.previewStarredPreBallot(model, session, deviceType, answeringDate, locale.toString());
			}else{
				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
					if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
						retVal = this.previewHDQPreBallotAssembly(model, session, deviceType, group, answeringDate, locale.toString());
					}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
						retVal = this.previewHDQPreBallotCouncil(model, session, deviceType, answeringDate, locale.toString());
					}
				}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
					if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
						retVal = this.previewHDSPreBallotCouncil(model, session, deviceType, answeringDate, locale.toString());
					}
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
	
	private String previewHDSPreBallotCouncil(ModelMap model, Session session,
			DeviceType deviceType, Date answeringDate, String locale) {
		List<BallotVO> ballotVOs = new ArrayList<BallotVO>();
		try {
			PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
			if(preBallot != null){
				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("preBallotId", new String[]{session.getId().toString()});
					parameters.put("locale", new String[]{locale.toString()});
					List data = Query.findReport("HDS_COUNCIL_PREBALLOT_VIEW", parameters);
					if(data != null && !data.isEmpty()){
						for(Object o : data){
							Object[] objArr = (Object[]) o;
							BallotVO vo = new BallotVO();
							if(objArr[0] != null){
								vo.setQuestionNumber(new Integer(objArr[0].toString()));
							}
							if(objArr[1] != null){
								vo.setMemberName(objArr[1].toString());
							}
							if(objArr[2] != null){
								vo.setQuestionSubject(objArr[2].toString());
							}
							ballotVOs.add(vo);
						}
					}
				}
			}else{
				ballotVOs = Ballot.findPreviewPreBallotVOHDSCouncil(session, deviceType, answeringDate, locale);
			}
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/hdsPreBallotCouncil";
		} catch (ELSException e) {			
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error"; 
		}
	}

	private String previewHDQPreBallotCouncil(ModelMap model, Session session,
			DeviceType deviceType, Date answeringDate, String locale) {
		List<BallotVO> ballotVOs = new ArrayList<BallotVO>();
		try {
			PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
			if(preBallot != null){
				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("preBallotId", new String[]{session.getId().toString()});
					parameters.put("locale", new String[]{locale.toString()});
					
					List data = Query.findReport("HDQ_COUNCIL_PREBALLOT_VIEW", parameters);
					
					if(data != null && !data.isEmpty()){
						for(Object o : data){
							Object[] objArr = (Object[]) o;
							BallotVO vo = new BallotVO();
							if(objArr[0] != null){
								vo.setQuestionNumber(new Integer(objArr[0].toString()));
							}
							if(objArr[1] != null){
								vo.setMemberName(objArr[1].toString());
							}
							if(objArr[2] != null){
								vo.setQuestionSubject(objArr[2].toString());
							}
							ballotVOs.add(vo);
						}
					}
				}
			}else{
				ballotVOs = Ballot.previewPreBallotVOHDQCouncil(session, deviceType, answeringDate, locale);
			}
			
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/hdqPreBallotCouncil";//"ballot/halfhourq_preballot";

		} catch (ELSException e) {			
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error"; 
		}
	}

	private String previewHDQPreBallotAssembly(ModelMap model, Session session,
			DeviceType deviceType, Group group, Date answeringDate, String locale) {
		List<BallotMemberVO> ballotVOs;
		try {
			ballotVOs = Ballot.previewPreBallotHDQAssembly(session, deviceType, group, answeringDate, locale);
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/hdq_preballot_assembly";
		} catch (ELSException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error";
		}
	}

	private String previewStarredPreBallot(final ModelMap model, 
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<StarredBallotVO> ballotVOs = Ballot.
				previewPreBallotVOs(session, deviceType, answeringDate, locale);
		ballotVOs = sortStarredPreBallotVOs(ballotVOs);
		
		Group group = Group.find(session, answeringDate, locale);
		model.addAttribute("groupNo", 
				FormaterUtil.formatNumberNoGrouping(group.getNumber(), locale));
		
		/**** total number of members ****/
		if(ballotVOs!=null) {
			model.addAttribute("totalMembers", FormaterUtil.formatNumberNoGrouping(ballotVOs.size(), locale));
			
			Integer serialNo = 0;
			Integer noOfQuestions = 0;
			for(StarredBallotVO ballotVO : ballotVOs) {
				++serialNo;
				ballotVO.setSerialNo(FormaterUtil.formatNumberNoGrouping(serialNo, locale));
				
				List<QuestionSequenceVO> sequenceVOs = ballotVO.getQuestionSequenceVOs();
				int size = sequenceVOs.size();
				noOfQuestions = noOfQuestions + size;
			}			
			model.addAttribute("totalNoOfQuestions", 
					FormaterUtil.formatNumberNoGrouping(noOfQuestions, locale));
		}
		
		model.addAttribute("ballotVOs", ballotVOs);
		return "ballot/preview_starred_preballot";

	}
	
	@RequestMapping(value = "/updateHDQ", method = RequestMethod.GET)
	public String getUpdateHDQ(Model model, HttpServletRequest request, Locale locale){
		String retVal = "ballot/error";
		try{
			/** Create HouseType */
			String strHouseType = request.getParameter("houseType");
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());

			/** Create SessionType */
			String strSessionTypeId = request.getParameter("sessionType");
			SessionType sessionType = SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));

			/** Create year */
			String strYear = request.getParameter("sessionYear");
			Integer year = Integer.valueOf(strYear);

			/** Create Session */
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

			/** Create DeviceType */
			DeviceType deviceType = null;
			String strDeviceType = request.getParameter("questionType");
			if(strDeviceType == null){
				strDeviceType = request.getParameter("deviceType");
			}
			deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			
			CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			String strAnsweringDate = request.getParameter("answeringDate");
			Date answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			
			/**Find ballot**/
			Ballot balHDQ = Ballot.find(session, deviceType, answeringDate, locale.toString());
			if(balHDQ == null){
				/* Show error if ballot is not created since it is 
				 * pointless to update balloted hdqs if ballot is not
				 * done  
				 */
				model.addAttribute("type", "ballot_not_created");
				return "ballot/error";
			}
			Map<String, List<MasterVO>> data = new LinkedHashMap<String, List<MasterVO>>();
			
			for(BallotEntry be : balHDQ.getBallotEntries()){
				
				Status internalStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION, locale.toString());
				List<Question> hdqs = Question.findAll(be.getMember(), session, deviceType, internalStatus);
				List<MasterVO> questions = new LinkedList<MasterVO>();
				
				for(Question q : hdqs){
					
					MasterVO qvo = new MasterVO();
					qvo.setValue(q.getPrimaryMember().getId().toString());
					qvo.setName(be.getMember().getFullnameLastNameFirst());
					
					qvo.setId(q.getId());
					qvo.setNumber(q.getNumber());
					qvo.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(q.getNumber(), locale.toString()));
					questions.add(qvo);					
				}		
				
				data.put(be.getMember().getId().toString(), questions);
			}
			
			model.addAttribute("data", data);
			
			model.addAttribute("hdqdeviceType", deviceType.getType());
			model.addAttribute("hdqSession", session.getId());
			model.addAttribute("hdqHouseType", houseType.getType());
			
			retVal = "ballot/updateBallotedHDQ";
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
	}
	
	@RequestMapping(value = "/updateHDS", method = RequestMethod.GET)
	public String getUpdateHDS(Model model, HttpServletRequest request, Locale locale){
		String retVal = "ballot/error";
		try{
			/** Create HouseType */
			String strHouseType = request.getParameter("houseType");
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());

			/** Create SessionType */
			String strSessionTypeId = request.getParameter("sessionType");
			SessionType sessionType = SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));

			/** Create year */
			String strYear = request.getParameter("sessionYear");
			Integer year = Integer.valueOf(strYear);

			/** Create Session */
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

			/** Create DeviceType */
			DeviceType deviceType = null;
			String strDeviceType = request.getParameter("questionType");
			if(strDeviceType == null){
				strDeviceType = request.getParameter("deviceType");
			}
			deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			
			CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			String strAnsweringDate = request.getParameter("answeringDate");
			Date answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			
			/**Find ballot**/
			Ballot balHDQ = Ballot.find(session, deviceType, answeringDate, locale.toString());
			if(balHDQ == null){
				/* Show error if ballot is not created since it is 
				 * pointless to update balloted hdqs if ballot is not
				 * done  
				 */
				model.addAttribute("type", "ballot_not_created");
				return "ballot/error";
			}
			Map<String, List<MasterVO>> data = new LinkedHashMap<String, List<MasterVO>>();
			
			for(BallotEntry be : balHDQ.getBallotEntries()){
				
				Status internalStatus = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION, locale.toString());
				List<Question> hdqs = Question.findAll(be.getMember(), session, deviceType, internalStatus);
				List<MasterVO> questions = new LinkedList<MasterVO>();
				
				for(Question q : hdqs){
					
					MasterVO qvo = new MasterVO();
					qvo.setValue(q.getPrimaryMember().getId().toString());
					qvo.setName(be.getMember().getFullnameLastNameFirst());
					
					qvo.setId(q.getId());
					qvo.setNumber(q.getNumber());
					qvo.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(q.getNumber(), locale.toString()));
					questions.add(qvo);					
				}		
				
				data.put(be.getMember().getId().toString(), questions);
			}
			
			model.addAttribute("data", data);
			
			model.addAttribute("hdqdeviceType", deviceType.getType());
			model.addAttribute("hdqSession", session.getId());
			model.addAttribute("hdqHouseType", houseType.getType());
			
			retVal = "ballot/updateBallotedHDS";
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
	}
	
	@Transactional
	@RequestMapping(value = "/updateHDQ", method = RequestMethod.POST)
	public String postUpdateHDQ(Model model, HttpServletRequest request, Locale locale){
		String retVal = "ballot/error";
		try{
			String strAnsweringDate = request.getParameter("answeringDate");
			String strIds = request.getParameter("ids");
			if(strIds != null && !strIds.isEmpty() 
					&& strAnsweringDate != null && !strAnsweringDate.isEmpty()){
				
				CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				Date answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
				String[] ids = strIds.split(":");
				
				
				for(String id : ids){
					Question q = Question.findById(Question.class, new Long(id));
					Status balloted = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED, locale.toString());
					//update members question so as to remove any previously balloted hdqs of the same date since it can be done
					//many times
					Question.updateUnBallot(q.getPrimaryMember(), q.getSession(), q.getType(), q.getInternalStatus(), answeringDate);
					
					q.setBallotStatus(balloted);
					q.setDiscussionDate(answeringDate);
					q.simpleMerge();
				}
				
				model.addAttribute("type", "ballot_updated");
				retVal = "ballot/info";
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		return retVal;
	}
	
	@Transactional
	@RequestMapping(value = "/updateHDS", method = RequestMethod.POST)
	public String postUpdateHDS(Model model, HttpServletRequest request, Locale locale){
		String retVal = "ballot/error";
		try{
			String strAnsweringDate = request.getParameter("answeringDate");
			String strIds = request.getParameter("ids");
			if(strIds != null && !strIds.isEmpty() 
					&& strAnsweringDate != null && !strAnsweringDate.isEmpty()){
				
				CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				Date answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
				String[] ids = strIds.split(":");
				
				
				for(String id : ids){
					StandaloneMotion q = StandaloneMotion.findById(StandaloneMotion.class, new Long(id));
					Status balloted = Status.findByType(ApplicationConstants.STANDALONE_PROCESSED_BALLOTED, locale.toString());
					//update members motion so as to remove any previously balloted hdqs of the same date since it can be done
					//many times
					Question.updateUnBallot(q.getPrimaryMember(), q.getSession(), q.getType(), q.getInternalStatus(), answeringDate);
					
					q.setBallotStatus(balloted);
					q.setDiscussionDate(answeringDate);
					q.simpleMerge();
				}
				
				model.addAttribute("type", "ballot_updated");
				retVal = "ballot/info";
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		return retVal;
	}
}
