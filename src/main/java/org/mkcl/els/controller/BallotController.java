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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberBallotFinalBallotVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseReportVO;
import org.mkcl.els.common.vo.MemberBallotQuestionDistributionVO;
import org.mkcl.els.common.vo.MemberBallotVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.StarredBallotVO;
import org.mkcl.els.domain.Ballot;
import org.mkcl.els.domain.BallotEntry;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.MemberBallotChoice;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.PreBallot;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionPlace;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
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
					}else if(deviceType.getType().equals(
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
					}else if(deviceType.getType().equals(
							ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
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
							session.getParameter("questions_halfhourdiscussion_standalone_discussionDates");
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
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				QuestionDates questionDates = 
					QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
				answeringDate = questionDates.getAnsweringDate();
			}
			else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
					deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
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

			/** Create DeviceType */
			DeviceType deviceType = null;
			String strDeviceType = request.getParameter("questionType");
			if(strDeviceType == null){
				strDeviceType = request.getParameter("deviceType");
			}

			deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			model.addAttribute("deviceType", deviceType.getType());

			Group group = null;
			if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
					&& houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) 
					&& !deviceType.getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
				String strGroup = request.getParameter("group");
				group = Group.findById(Group.class, Long.valueOf(strGroup));
			}
			
			/** Create answeringDate */
			String strAnsweringDate = request.getParameter("answeringDate");
			Date answeringDate = null;
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				QuestionDates questionDates = 
					QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
				answeringDate = questionDates.getAnsweringDate();
			}
			else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
					deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
				
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
				if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					/*List<StarredBallotVO> ballotVOs = Ballot.findStarredBallotVOs(session, answeringDate, locale.toString());
					model.addAttribute("ballotVOs", ballotVOs);*/
					
					Map<String, String[]> parametersMap = new HashMap<String, String[]>();
					parametersMap.put("locale", new String[]{locale.toString()});
					parametersMap.put("sessionId", new String[]{session.getId().toString()});
					parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
					parametersMap.put("groupId", new String[]{group.getId().toString()});
					parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});
					List ballotVOs = org.mkcl.els.domain.Query.findReport("STARRED_BALLOT_VIEW", parametersMap);
					parametersMap = null;
					model.addAttribute("ballotVOs", ballotVOs);
					CustomParameter serverDateTimeFormat = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
					if(serverDateTimeFormat != null){
						model.addAttribute("formattedCurrentDate", FormaterUtil.formatDateToString((new Date()), serverDateTimeFormat.getValue(), locale.toString()));
					}
					if(ballotVOs != null && !ballotVOs.isEmpty()){
						model.addAttribute("totalMembers", FormaterUtil.formatNumberNoGrouping(Integer.valueOf((((Object[])ballotVOs.get(0))[3]).toString()), locale.toString()));
					}
					retVal = "ballot/ballot";
				}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
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
					
					model.addAttribute("ballotVOs", ballotVOs);
					retVal = "ballot/halfhour_member_ballot";
				}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){

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
					
					List ballotVOs = org.mkcl.els.domain.Query.findReport("HDS_BALLOT_VIEW", parametersMap);
					parametersMap = null;
					List<MasterVO> serialNumber = new ArrayList<MasterVO>(ballotVOs.size());
					for(int i = 0; i < ballotVOs.size(); i++){
						serialNumber.add(new MasterVO((i + 1), FormaterUtil.formatNumberNoGrouping((i + 1), locale.toString())));
					}
					model.addAttribute("serialnumber", serialNumber);
					model.addAttribute("ballotVOs", ballotVOs);
					serialNumber = null;
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
					
					model.addAttribute("ballotVOs", ballotVOs);
					
					retVal = "ballot/halfhour_ballot";
				}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
					/*List<BallotMemberVO> ballotVOs = Ballot.findBallotedMemberVO(session, deviceType, answeringDate, locale.toString());
					model.addAttribute("ballotVOs", ballotVOs);
					retVal = "ballot/nonofficial_member_ballot";*/
					
					Map<String, String[]> parametersMap = new HashMap<String, String[]>();
					parametersMap.put("locale", new String[]{locale.toString()});
					parametersMap.put("sessionId", new String[]{session.getId().toString()});
					parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
					parametersMap.put("groupId", new String[]{group.getId().toString()});
					parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});
					
					List ballotVOs = org.mkcl.els.domain.Query.findReport("HDQ_COUNCIL_BALLOT_VIEW", parametersMap);
					parametersMap = null;
					
					model.addAttribute("ballotVOs", ballotVOs);
					
					retVal = "ballot/halfhour_ballot";
					
				}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){					
					List<BallotMemberVO> ballotVOs = Ballot.findBallotedMemberVO(session, deviceType, answeringDate, locale.toString());
					model.addAttribute("ballotVOs", ballotVOs);
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

					retVal = "ballot/nonofficial_bill_membersubjectcombo_ballot";
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
	/****** Member Ballot For Council Starred Questions-First Batch(Sandeep) ******/
	/****** Member Ballot(Council) Initial Page ****/
	@RequestMapping(value="/memberballot/init",method=RequestMethod.GET)
	public String viewMemberBallotInit(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){		
		String errorpage="ballot/error";
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
					String noOfRounds=session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_UH);
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
		}
		return "ballot/memberballotattendance";	
	}
	/****** Member Ballot(Council) Attendance Update ****/
	@Transactional
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
	/****** Member Ballot(Council) Member Wise Question Report Page ****/
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
	/****** Member Ballot(Council) Question Distribution Report Page ****/
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
	/****** Member Ballot(Council) Member Ballot Choices Initial Page ****/
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
	/****** Member Ballot(Council) Member Ballot List Choices Page ****/
	@RequestMapping(value="/memberballot/listchoices",method=RequestMethod.GET)
	public String listMemberBallotChoice(final HttpServletRequest request,final ModelMap model,final Locale locale){
		try{
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
		}catch (ELSException e) {
			model.addAttribute("type","REQUEST_PARAMETER_NULL");
			return "ballot/error";
		}
		return "ballot/listmemberballotchoice";
	}
	/****** Member Ballot(Council) Member Ballot Choices Update Page ****/
	@Transactional
	@RequestMapping(value="/memberballot/choices",method=RequestMethod.POST)
	public  String updateMemberBallotChoice(final HttpServletRequest request,
			final HttpServletResponse response,
			final ModelMap model,final Locale locale){
		try{
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
		}catch (ELSException e) {
			model.addAttribute("error",e.getParameter());
			return "ballot/error";
		}catch(Exception e){
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
		try{
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
		}catch (ELSException e) {
			logger.error(e.getMessage());
		}
	}
	private void autoFillChoices(String autofillingPattern,Map<String, Integer> noofQuestionsInEachRound,
			int totalRounds, int noOfAdmittedQuestions, Session session,
			DeviceType questionType, Member member, String locale,
			String flag,Map<Long,MemberBallotChoice> manuallySetChoices){
		
		try{
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
		}catch (ELSException e) {
			logger.error(e.getMessage());
		}
	}

	/****** Member Ballot(Council) Member Ballot Update Clubbing Page ****/
	@Transactional
	@RequestMapping(value="/memberballot/updateclubbing",method=RequestMethod.GET)
	public String updateClubbingMemberBallot(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale){
		try{
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
		}catch (ELSException e) {
			logger.error("**** Check request parameters 'session and deviceType' for null values ****");
			model.addAttribute("type", "MEMBERBALLOTUPDATECLUBBING_REQUEST_PARAMETER_NULL");
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
		}catch (ELSException e) {
			logger.error("**** Check request parameter 'session,deviceType,group,answering date' for null values ****");
			model.addAttribute("type", "MEMBERBALLOTFINAL_REQUEST_PARAMETER_NULL");
			return errorpage;
		}
		return "ballot/memberballotfinal";		
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
		try {
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


			/** Create answeringDate */
			String strAnsweringDate = request.getParameter("answeringDate");
			Date answeringDate = null;


			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				QuestionDates questionDates = 
					QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
				answeringDate = questionDates.getAnsweringDate();
			}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
					deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
				CustomParameter dbDateFormat = 
					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION) || deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)){
				CustomParameter dbDateFormat = 
					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
			}
			
			/**** Validate whether pre-ballot can be created for bill ****/
			if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
				boolean isBallotAllowedToCreate = validateBallotCreationForBill(session, deviceType, answeringDate, locale.toString());
				if(isBallotAllowedToCreate==false) {
					model.addAttribute("isBallotAllowedToCreate", isBallotAllowedToCreate);
					return "ballot/nonofficial_bill_preballot";
				}
			}

			/** Route PreBallot creation to appropriate handler method */
			if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {

				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					retVal = this.halfHourPreBallot(model, session, deviceType, answeringDate, locale.toString());

				}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
					retVal = this.halfHourPreBallot(model, session, deviceType, answeringDate, locale.toString());

				}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
					retVal = this.resolutionNonOfficialMemberPreBallot(model, session, deviceType, answeringDate, locale.toString());

				}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
					retVal = this.billNonOfficialPreBallot(model, session, deviceType, answeringDate, locale.toString());
				
				}				
			}else if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {

				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ){
					retVal = this.halfHourPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
					retVal = this.hdsPreBallotAssembly(model, session, deviceType, answeringDate, locale.toString());
				}else if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
					retVal = this.starredPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
					CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					retVal = this.resolutionNonOfficialPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}else if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
					CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					retVal = this.billNonOfficialPreBallot(model, session, deviceType, answeringDate, locale.toString());
				}
			}

		}catch(Exception e) {
			logger.error("error", e);
			model.addAttribute("type", "INSUFFICIENT_PARAMETERS_FOR_PRE_BALLOT_CREATION");
			retVal = "ballot/error";
		}
		return retVal;
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

	private String halfHourPreBallot(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> ballotVOs = new ArrayList<BallotVO>();
		try {
			PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
			if(preBallot != null){
				ballotVOs = PreBallot.getBallotVOFromBallotEntries(preBallot.getBallotEntries(), locale);
			}else{
				ballotVOs = Ballot.findPreBallotVO(session, deviceType, answeringDate, locale);
			}
			
			model.addAttribute("ballotVOs", ballotVOs);
			return "ballot/halfhourq_preballot";
			
		} catch (ELSException e) {			
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
			return "ballot/error"; 
		}
	}

	private String hdsPreBallotAssembly(final ModelMap model,
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

	private String hdsPreBallotCouncil(final ModelMap model,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {

		List<BallotVO> ballotVOs = Ballot.findHDSCouncilPreBallotVO(session, deviceType, answeringDate, locale);
		model.addAttribute("ballotVOs", ballotVOs);
		return "ballot/nonofficial_member_preballot";
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
		List<StarredBallotVO> ballotVOs = 
			Ballot.findStarredPreBallotVOs(session, deviceType, answeringDate, locale);
		model.addAttribute("ballotVOs", ballotVOs);
		return "ballot/starred_preballot";
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
				Status[] internalStatuses = {ADMITTED, REJECTED};

				List<MasterVO> members = new ArrayList<MasterVO>();
				if(ballot != null){
					List<BallotEntry> ballotEntries = ballot.getBallotEntries();
					for(BallotEntry bE : ballotEntries){
						if(bE != null){
							if(bE.getMember() != null){
								if(Resolution.getMemberChoiceCount(session, deviceType, bE.getMember().getId(), answeringDate, internalStatuses, startTime, endTime, locale.toString()) == 0){
									MasterVO masterVO = new MasterVO();
									masterVO.setId(bE.getMember().getId());
									masterVO.setValue(bE.getMember().getFullname());
									members.add(masterVO);
								}
							}
						}
					}
					model.addAttribute("members", members);
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
		try {

			String strResId = request.getParameter("choice");
			/** Create answeringDate */
			String strAnsweringDate = request.getParameter("answeringDate");
			CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			Date answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());


			if(strResId != null){
				if(!strResId.isEmpty()){
					Resolution resolution = Resolution.findById(Resolution.class, new Long(strResId));
					Status statusBalloted = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_PROCESSED_BALLOTED, locale.toString());
					Status statusToBeDiscussed = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_PROCESSED_TOBEDISCUSSED, locale.toString());

					resolution.setBallotStatus(statusBalloted);
					resolution.setDiscussionStatus(statusToBeDiscussed);
					resolution.setDiscussionDate(answeringDate);
					resolution.simpleMerge();
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
		try {
					
			patrakBhagTwoDateFormat = CustomParameter.findByFieldName(CustomParameter.class, "name", "PATRAK_BHAG_TWO_DATE_FORMAT", "");
			dayFormat = CustomParameter.findByFieldName(CustomParameter.class, "name", "DAY_OF_WEEK_FORMAT", "");
			
			String marathiCurrentDay = FormaterUtil.formatDateToString(date, dayFormat.getValue(), locale.toString());
			
			model.addAttribute("formattedCurrentDay", FormaterUtil.getDayInMarathi(marathiCurrentDay, locale.toString()));
			
			/*** To form the patrakbhag don formatted date ***/
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			Integer intDay = calendar.get(Calendar.DAY_OF_MONTH);
			Integer intMonth = calendar.get(Calendar.MONTH);
			Integer intYear = calendar.get(Calendar.YEAR);
			String formattedCurrentDate = FormaterUtil.formatNumberNoGrouping(intDay, locale.toString()) + 
						" " + FormaterUtil.getMonthInMarathi(intMonth, locale.toString()) + 
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
			model.addAttribute("formattedDiscussionDay", FormaterUtil.getDayInMarathi(marathiDiscussionDay, locale.toString()));
					
			/*** To form the patrakbhag don formatted date ***/
			calendar.setTime(answeringDate);
			intDay = calendar.get(Calendar.DAY_OF_MONTH);
			intMonth = calendar.get(Calendar.MONTH);
			intYear = calendar.get(Calendar.YEAR);
			String formattedAnsweringDate = FormaterUtil.formatNumberNoGrouping(intDay, locale.toString()) + 
						" " + FormaterUtil.getMonthInMarathi(intMonth, locale.toString()) + 
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
						allItems=MemberBallotAttendance.findAll(session,deviceType,"false","member",locale.toString());
						selectedItems=MemberBallotAttendance.findAll(session,deviceType,"true","position",locale.toString());
					}else{
						allItems=MemberBallotAttendance.findAll(session,deviceType,"true","member",locale.toString());
						selectedItems=MemberBallotAttendance.findAll(session,deviceType,"false","position",locale.toString());
					}
					List<MemberBallotAttendance> eligibles=MemberBallotAttendance.findAll(session,deviceType,"","member",locale.toString());
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
}
