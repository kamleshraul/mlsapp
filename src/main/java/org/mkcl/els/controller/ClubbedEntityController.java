package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.common.vo.DeviceSearchVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MotionSearchVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.BillAmendmentMotion;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.DiscussionMotion;
import org.mkcl.els.domain.EventMotion;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.RulesSuspensionMotion;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SpecialMentionNotice;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/clubentity")
public class ClubbedEntityController extends BaseController{

	@RequestMapping(value="/init",method=RequestMethod.GET)
	public String getClubbing(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		
		String retVal = "clubbing/error";
		String strFacility = request.getParameter("searchfacility");
		String strUseForFiling = request.getParameter("useforfiling");
		
		if(strUseForFiling != null && !strUseForFiling.isEmpty()){
			model.addAttribute("useforfiling", strUseForFiling);
		}
		
		if(strFacility != null && strFacility.equals("yes")){
			
			/**** Advanced Search Filters****/
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			
			HouseType houseType = null;
			Integer sessionYear = null;
			SessionType sessionType = null;
			Session session = null; 
			try{
				houseType = HouseType.findByType(strHouseType, locale.toString());
				sessionYear = new Integer(strSessionYear);
				sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				
				model.addAttribute("houseType", houseType.getType());
			}catch(Exception e){
				logger.error("error", e);
			}
			
			int year = sessionYear;
			CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
			List<Reference> years = new ArrayList<Reference>();
			if(houseFormationYear != null){
				Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
				for(int i = year; i >= formationYear; i--){
					Reference reference = new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
					years.add(reference);
				}
			}else{
				model.addAttribute("flag", "houseformationyearnotset");
				return "clubbing/error";
			}
			model.addAttribute("years", years);
			model.addAttribute("sessionYear", year);
			List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType", ApplicationConstants.ASC, locale.toString());
			model.addAttribute("sessionTypes", sessionTypes);
			model.addAttribute("sessionType", session.getType().getId());
			
			CustomParameter csptSearchByFacility = CustomParameter.findByName(CustomParameter.class, "SEARCHFACILITY_SEARCH_BY", "");
			if(csptSearchByFacility != null && csptSearchByFacility.getValue() != null && ! csptSearchByFacility.getValue().isEmpty()){
				List<MasterVO> searchByData = new ArrayList<MasterVO>();
				for(String sf : csptSearchByFacility.getValue().split(";")){
					String[] data = sf.split(":");
					MasterVO newVO = new MasterVO();
					newVO.setValue(data[0]);
					newVO.setName(data[1]);
					searchByData.add(newVO);
				}
				model.addAttribute("searchBy", searchByData);
			}
			
			DeviceType deviceType = null;
			try{
				String strDevice = request.getParameter("deviceType");
				if(strDevice != null && !strDevice.isEmpty()){
					deviceType = DeviceType.findById(DeviceType.class, new Long(strDevice));
				} else {
					strDevice = request.getParameter("questionType");
					if(strDevice != null && !strDevice.isEmpty()){
						deviceType = DeviceType.findById(DeviceType.class, new Long(strDevice));
					}
				}
			}catch(Exception e){
				logger.error("error", e);
			}
			
			if(deviceType != null){
				model.addAttribute("deviceType", deviceType.getId());
			} else {
				logger.error("error", "request parameter of deviceType/questionType is missing in ClubbedEntityController/getClubbing");
				model.addAttribute("flag", "devicetypenotset");
				return "clubbing/error";
			}			
			if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)) {
				try{
					model.addAttribute("deviceTypes", DeviceType.findDeviceTypesStartingWith("questions_", locale.toString()));
				}catch (ELSException e) {
					model.addAttribute("ClubbedEntityController","Request can not be completed at the moment.");
				}				
				
				List<Group> allgroups = null;
				try{
					allgroups = Group.findByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				}catch (ELSException e) {
					model.addAttribute("ClubbedEntityController","Request can not be completed at the moment.");
				}
				
				List<MasterVO> masterVOs = new ArrayList<MasterVO>();
				for(Group i:allgroups){
					MasterVO masterVO = new MasterVO(i.getId(),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i.getNumber()));
					masterVOs.add(masterVO);
				}
				model.addAttribute("groups",masterVOs);
				
				model.addAttribute("whichDevice", "questions_");
				
			} else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_CUTMOTIONS)) {
				try{
					model.addAttribute("deviceTypes", DeviceType.findDeviceTypesStartingWith("motions_cutmotion_", locale.toString()));
				}catch (ELSException e) {
					model.addAttribute("ClubbedEntityController","Request can not be completed at the moment.");
				}
				
				model.addAttribute("whichDevice", "motions_cutmotion_");
			}			

			retVal = "searchfacility/init";
			
		}else{
			String deviceForClubbing = "";
			String strquestionId=request.getParameter("id");
			if(strquestionId!=null){
				if(!strquestionId.isEmpty()){
					deviceForClubbing = ApplicationConstants.DEVICE_QUESTIONS;
					Question question=Question.findById(Question.class,Long.parseLong(strquestionId));
					/**** Advanced Search Options ****/
					/**** First we will check if clubbing is allowed on the given question ****/
					Boolean isClubbingAllowed=isClubbingAllowed(question,request);
					if(isClubbingAllowed){
						/**** Question subject will be visible on the clubbing page ****/
						if(question.getRevisedSubject()!=null){
							if(!question.getRevisedSubject().isEmpty()){
								model.addAttribute("subject",question.getRevisedSubject());
							}else{
								model.addAttribute("subject",question.getSubject());
							}
						}else{
							model.addAttribute("subject",question.getSubject());
						}
						if(question.getRevisedQuestionText() != null && !question.getRevisedQuestionText().isEmpty()){
							model.addAttribute("questionText",question.getRevisedQuestionText());
						}else{
							model.addAttribute("questionText",question.getQuestionText());
						}
						/**** Question number will also be visible ****/
						model.addAttribute("id",Long.parseLong(strquestionId));
						model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getNumber()));
						/**** Member Name will also be visible ****/
						model.addAttribute("memberName",question.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						/**** Advanced Search Filters****/
						String deviceType=question.getType().getType();
						model.addAttribute("deviceType",deviceType);
						model.addAttribute("houseType",question.getHouseType().getType());
						try{
							model.addAttribute("deviceTypes",DeviceType.findAllowedTypesInStarredClubbing(locale.toString()));
						}catch (ELSException e) {
							model.addAttribute("ClubbedEntityController","Request can not be completed at the moment.");
						}
						List<Group> allgroups = null;
						try{
							allgroups = Group.findByHouseTypeSessionTypeYear(question.getHouseType(),question.getSession().getType(),question.getSession().getYear());
						}catch (ELSException e) {
							model.addAttribute("ClubbedEntityController","Request can not be completed at the moment.");
						}
						List<MasterVO> masterVOs=new ArrayList<MasterVO>();
						for(Group i:allgroups){
							MasterVO masterVO=new MasterVO(i.getId(),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i.getNumber()));
							masterVOs.add(masterVO);
						}
						model.addAttribute("groups",masterVOs);
						int year=question.getSession().getYear();
						CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
						List<Reference> years=new ArrayList<Reference>();
						if(houseFormationYear!=null){
							Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
							for(int i=year;i>=formationYear;i--){
								Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
								years.add(reference);
							}
						}else{
							model.addAttribute("flag", "houseformationyearnotset");
							return "clubbing/error";
						}
						model.addAttribute("years",years);
						model.addAttribute("sessionYear",year);
						List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType",ApplicationConstants.ASC, locale.toString());
						model.addAttribute("sessionTypes",sessionTypes);
						model.addAttribute("sessionType",question.getSession().getType().getId());
						model.addAttribute("whichDevice", "questions_");
					}else{
						/**** if question is already clubbed ****/
						if(question.getParent()!=null){
							model.addAttribute("flag","ALREADY_CLUBBED");
							return "clubbing/error";
						}else{
							model.addAttribute("flag","CLUBBING_NOT_ALLOWED");
							return "clubbing/error";
						}
					}				
				}else{
					logger.error("**** Check request parameter 'id' for no value");
					model.addAttribute("flag","REQUEST_PARAMETER_ISEMPTY");
					return "clubbing/error";
				}
			}else{
				
				String strbillId=request.getParameter("billId");
				String strMotionId = request.getParameter("motionId");
				String strStandaloneMotionId = request.getParameter("standaloneMotionId");
				String strResolutionId = request.getParameter("resolutionId");
				String strCutMotionId = request.getParameter("cutMotionId");
				String strEventMotionId = request.getParameter("eventMotionId");
				String strDiscussionMotionId = request.getParameter("discussionMotionId");
				String strBillAmendmentMotionId = request.getParameter("billAmendmentMotionId");
				String strAdjournmentMotionId = request.getParameter("adjournmentMotionId");
				String strSpecialMentionNoticeId = request.getParameter("specialMentionNoticeId");
				String strRulesSuspensionMotionId = request.getParameter("rulesSuspensionMotionId");
				
				if(strbillId!=null) {
					if(!strbillId.isEmpty()) {
						deviceForClubbing = ApplicationConstants.DEVICE_BILLS;
						Bill bill=Bill.findById(Bill.class,Long.parseLong(strbillId));
						/**** Advanced Search Options ****/
						/**** First we will check if clubbing is allowed on the given bill ****/
						Boolean isClubbingAllowed=isClubbingAllowed(bill,request);
						if(isClubbingAllowed){
							/**** Bill title will be visible on the clubbing page ****/
							model.addAttribute("title",bill.getDefaultTitle());						
							/**** If exists, Bill number will also be visible ****/
							model.addAttribute("id",Long.parseLong(strbillId));
							if(bill.getNumber()!=null) {
								model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(bill.getLocale()).format(bill.getNumber()));
							}		
							/**** Member Name will also be visible ****/
							model.addAttribute("memberName",bill.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
							/**** Advanced Search Filters****/
							String deviceType=bill.getType().getType();
							model.addAttribute("deviceType",deviceType);
							model.addAttribute("houseType",bill.getHouseType().getType());
							String languagesAllowedInSession = bill.getSession().getParameter(deviceType + "_languagesAllowed");
							if(languagesAllowedInSession != null && !languagesAllowedInSession.isEmpty()) {
								List<Language> languagesAllowedForBill = new ArrayList<Language>();
								for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
									Language languageAllowed = Language.findByFieldName(Language.class, "type", languageAllowedInSession, bill.getLocale());
									languagesAllowedForBill.add(languageAllowed);
								}
								model.addAttribute("languagesAllowedForBill", languagesAllowedForBill);
							}
							String defaultTitleLanguage = bill.getSession().getParameter(deviceType+"_defaultTitleLanguage");
					    	if(defaultTitleLanguage!=null&&!defaultTitleLanguage.isEmpty()) {
					    		model.addAttribute("defaultTitleLanguage", defaultTitleLanguage);
					    	}
	//						model.addAttribute("deviceTypes",DeviceType.getAllowedTypesInBillClubbing(locale.toString()));
	//						int year=bill.getSession().getYear();
	//						CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
	//						List<Reference> years=new ArrayList<Reference>();
	//						if(houseFormationYear!=null){
	//							Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
	//							for(int i=year;i>=formationYear;i--){
	//								Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
	//								years.add(reference);
	//							}
	//						}else{
	//							model.addAttribute("flag", "houseformationyearnotset");
	//							return "clubbing/error";
	//						}
	//						model.addAttribute("years",years);
	//						model.addAttribute("sessionYear",year);
	//						List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType",ApplicationConstants.ASC, locale.toString());
	//						model.addAttribute("sessionTypes",sessionTypes);
	//						model.addAttribute("sessionType",bill.getSession().getType().getId());	
							model.addAttribute("whichDevice", "bills_");
						}else{
							/**** if bill is already clubbed ****/
							if(bill.getParent()!=null){
								model.addAttribute("flag","ALREADY_CLUBBED");
								return "clubbing/error";
							}else{
								model.addAttribute("flag","CLUBBING_NOT_ALLOWED");
								return "clubbing/error";
							}
						}
					}				
				} else if(strMotionId != null && !strMotionId.isEmpty()){
					deviceForClubbing = ApplicationConstants.DEVICE_MOTIONS_CALLING;
					Motion motion = Motion.findById(Motion.class, new Long(strMotionId));
					/**** Advanced Search Options ****/
					/**** First we will check if clubbing is allowed on the given bill ****/
					Boolean isClubbingAllowed = isClubbingAllowed(motion, request);
					if(isClubbingAllowed){
						/**** Question subject will be visible on the clubbing page ****/
						if(motion.getRevisedSubject()!=null){
							if(!motion.getRevisedSubject().isEmpty()){
								model.addAttribute("subject",motion.getRevisedSubject());
							}else{
								model.addAttribute("subject",motion.getSubject());
							}
						}else{
							model.addAttribute("subject",motion.getSubject());
						}
						/**** Question number will also be visible ****/
						model.addAttribute("id",Long.parseLong(strMotionId));
						model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
						/**** Member Name will also be visible ****/
						model.addAttribute("memberName",motion.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						/**** Advanced Search Filters****/
						String deviceType = motion.getType().getType();
						model.addAttribute("deviceType", deviceType);
						model.addAttribute("houseType", motion.getHouseType().getType());
						try{
							model.addAttribute("deviceTypes", DeviceType.findAllowedTypesInMotionClubbing(motion.getLocale()));
						}catch (ELSException e) {
							model.addAttribute("ClubbedEntityController","Request can not be completed at the moment.");
						}
											
						int year = motion.getSession().getYear();
						CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
						List<Reference> years = new ArrayList<Reference>();
						if(houseFormationYear != null){
							Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
							for(int i = year; i >= formationYear; i--){
								Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
								years.add(reference);
							}
						}else{
							model.addAttribute("flag", "houseformationyearnotset");
							return "clubbing/error";
						}
						model.addAttribute("years", years);
						model.addAttribute("sessionYear", year);
						List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale.toString());
						model.addAttribute("sessionTypes", sessionTypes);
						model.addAttribute("sessionType", motion.getSession().getType().getId());
						model.addAttribute("whichDevice", "motions_");
					}else{
						model.addAttribute("flag","CLUBBING_NOT_ALLOWED");
						return "clubbing/error";
					}
				} else if(strStandaloneMotionId != null && !strStandaloneMotionId.isEmpty()){
					deviceForClubbing = ApplicationConstants.DEVICE_STANDALONEMOTIONS;
					StandaloneMotion motion = StandaloneMotion.findById(StandaloneMotion.class, new Long(strStandaloneMotionId));
					Boolean isClubbingAllowed = false;
					if(strUseForFiling != null && !strUseForFiling.isEmpty() && strUseForFiling.equals("yes")){
						isClubbingAllowed = true;
					}else{
						/**** Advanced Search Options ****/					
						/**** First we will check if clubbing is allowed on the given bill ****/
						isClubbingAllowed = isClubbingAllowed(motion, request);
					}
					
					if(isClubbingAllowed){
						/**** StandaloneMotion subject will be visible on the clubbing page ****/
						if(motion.getRevisedSubject()!=null){
							if(!motion.getRevisedSubject().isEmpty()){
								model.addAttribute("subject",motion.getRevisedSubject());
							}else{
								model.addAttribute("subject",motion.getSubject());
							}
						}else{
							model.addAttribute("subject",motion.getSubject());
						}
						/**** StandaloneMotion number will also be visible ****/
						model.addAttribute("id",Long.parseLong(strStandaloneMotionId));
						model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
						/**** Member Name will also be visible ****/
						model.addAttribute("memberName",motion.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						/**** Advanced Search Filters****/
						String deviceType = motion.getType().getType();
						model.addAttribute("deviceType", deviceType);
						model.addAttribute("houseType", motion.getHouseType().getType());
						try{
							model.addAttribute("deviceTypes", DeviceType.findAllowedTypesInMotionClubbing(motion.getLocale()));
						}catch (ELSException e) {
							model.addAttribute("ClubbedEntityController","Request can not be completed at the moment.");
						}
						
						if(motion.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							List<Group> allgroups = null;
							try{
								allgroups = Group.findByHouseTypeSessionTypeYear(motion.getHouseType(), motion.getSession().getType(), motion.getSession().getYear());
							}catch (ELSException e) {
								model.addAttribute("ClubbedEntityController","Request can not be completed at the moment.");
							}
							List<MasterVO> masterVOs = new ArrayList<MasterVO>();
							for(Group i : allgroups){
								MasterVO masterVO = new MasterVO(i.getId(),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i.getNumber()));
								masterVOs.add(masterVO);
							}
							model.addAttribute("groups",masterVOs);
						}
						
						int year = motion.getSession().getYear();
						CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
						List<Reference> years = new ArrayList<Reference>();
						if(houseFormationYear != null){
							Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
							for(int i = year; i >= formationYear; i--){
								Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
								years.add(reference);
							}
						}else{
							model.addAttribute("flag", "houseformationyearnotset");
							return "clubbing/error";
						}
						model.addAttribute("years", years);
						model.addAttribute("sessionYear", year);
						List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale.toString());
						model.addAttribute("sessionTypes", sessionTypes);
						model.addAttribute("sessionType", motion.getSession().getType().getId());
						model.addAttribute("whichDevice", "motions_standalonemotion_");
					}else{
						model.addAttribute("flag","CLUBBING_NOT_ALLOWED");
						return "clubbing/error";
					}
				}else if(strCutMotionId != null && !strCutMotionId.isEmpty()){
					deviceForClubbing = ApplicationConstants.DEVICE_CUTMOTIONS;
					CutMotion motion = CutMotion.findById(CutMotion.class, new Long(strCutMotionId));
					/**** Advanced Search Options ****/
					/**** First we will check if clubbing is allowed on the given bill ****/
					Boolean isClubbingAllowed = isClubbingAllowed(motion, request);
					if(isClubbingAllowed){
						/**** Question subject will be visible on the clubbing page ****/
						if(motion.getRevisedMainTitle()!=null){
							if(!motion.getRevisedMainTitle().isEmpty()){
								model.addAttribute("subject",motion.getRevisedMainTitle());
							}else{
								model.addAttribute("subject",motion.getMainTitle());
							}
						}else{
							model.addAttribute("subject",motion.getMainTitle());
						}
						/**** Question number will also be visible ****/
						model.addAttribute("id",Long.parseLong(strCutMotionId));
						model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
						/**** Member Name will also be visible ****/
						model.addAttribute("memberName",motion.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						/**** Advanced Search Filters****/
						String deviceType = motion.getDeviceType().getType();
						model.addAttribute("deviceType", deviceType);
						model.addAttribute("houseType", motion.getHouseType().getType());
						
						int year = motion.getSession().getYear();
						CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
						List<Reference> years = new ArrayList<Reference>();
						if(houseFormationYear != null){
							Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
							for(int i = year; i >= formationYear; i--){
								Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
								years.add(reference);
							}
						}else{
							model.addAttribute("flag", "houseformationyearnotset");
							return "clubbing/error";
						}
						model.addAttribute("years", years);
						model.addAttribute("sessionYear", year);
						List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale.toString());
						model.addAttribute("sessionTypes", sessionTypes);
						model.addAttribute("sessionType", motion.getSession().getType().getId());
						model.addAttribute("whichDevice", "motions_cutmotion_");
					}else{
						model.addAttribute("flag","CLUBBING_NOT_ALLOWED");
						return "clubbing/error";
					}
				}else if(strEventMotionId != null && !strEventMotionId.isEmpty()){
					deviceForClubbing = ApplicationConstants.DEVICE_EVENTMOTIONS;
					EventMotion motion = EventMotion.findById(EventMotion.class, new Long(strEventMotionId));
					/**** Advanced Search Options ****/
					/**** First we will check if clubbing is allowed on the given bill ****/
					Boolean isClubbingAllowed = isClubbingAllowed(motion, request);
					if(isClubbingAllowed){
						/**** StandaloneMotion subject will be visible on the clubbing page ****/
						if(motion.getRevisedEventTitle()!=null){
							if(!motion.getRevisedEventTitle().isEmpty()){
								model.addAttribute("subject",motion.getRevisedEventTitle());
							}else{
								model.addAttribute("subject",motion.getEventTitle());
							}
						}else{
							model.addAttribute("subject",motion.getEventTitle());
						}
						/**** StandaloneMotion number will also be visible ****/
						model.addAttribute("id",Long.parseLong(strEventMotionId));
						model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
						/**** Member Name will also be visible ****/
						model.addAttribute("memberName",motion.getMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						/**** Advanced Search Filters****/
						String deviceType = motion.getDeviceType().getType();
						model.addAttribute("deviceType", deviceType);
						model.addAttribute("houseType", motion.getHouseType().getType());							
						
						int year = motion.getSession().getYear();
						CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
						List<Reference> years = new ArrayList<Reference>();
						if(houseFormationYear != null){
							Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
							for(int i = year; i >= formationYear; i--){
								Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
								years.add(reference);
							}
						}else{
							model.addAttribute("flag", "houseformationyearnotset");
							return "clubbing/error";
						}
						model.addAttribute("years", years);
						model.addAttribute("sessionYear", year);
						List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale.toString());
						model.addAttribute("sessionTypes", sessionTypes);
						model.addAttribute("sessionType", motion.getSession().getType().getId());
						model.addAttribute("whichDevice", "motions_eventmotion_");
					}else{
						model.addAttribute("flag","CLUBBING_NOT_ALLOWED");
						return "clubbing/error";
					}
				}else if(strDiscussionMotionId != null && !strDiscussionMotionId.isEmpty()){
					deviceForClubbing = ApplicationConstants.DEVICE_DISCUSSIONMOTIONS;
					DiscussionMotion motion = DiscussionMotion.findById(DiscussionMotion.class, new Long(strDiscussionMotionId));
					/**** Advanced Search Options ****/
					/**** First we will check if clubbing is allowed on the given bill ****/
					Boolean isClubbingAllowed = isClubbingAllowed(motion, request);
					if(isClubbingAllowed){
						/**** StandaloneMotion subject will be visible on the clubbing page ****/
						if(motion.getRevisedSubject()!=null){
							if(!motion.getRevisedSubject().isEmpty()){
								model.addAttribute("subject",motion.getRevisedSubject());
							}else{
								model.addAttribute("subject",motion.getSubject());
							}
						}else{
							model.addAttribute("subject",motion.getSubject());
						}
						/**** StandaloneMotion number will also be visible ****/
						model.addAttribute("id",Long.parseLong(strDiscussionMotionId));
						model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
						/**** Member Name will also be visible ****/
						model.addAttribute("memberName",motion.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						/**** Advanced Search Filters****/
						String deviceType = motion.getType().getType();
						model.addAttribute("deviceType", deviceType);
						model.addAttribute("houseType", motion.getHouseType().getType());
											
						int year = motion.getSession().getYear();
						CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
						List<Reference> years = new ArrayList<Reference>();
						if(houseFormationYear != null){
							Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
							for(int i = year; i >= formationYear; i--){
								Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
								years.add(reference);
							}
						}else{
							model.addAttribute("flag", "houseformationyearnotset");
							return "clubbing/error";
						}
						model.addAttribute("years", years);
						model.addAttribute("sessionYear", year);
						List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale.toString());
						model.addAttribute("sessionTypes", sessionTypes);
						model.addAttribute("sessionType", motion.getSession().getType().getId());
						model.addAttribute("whichDevice", "motions_discussion_");
					}else{
						model.addAttribute("flag","CLUBBING_NOT_ALLOWED");
						return "clubbing/error";
					}
				} else if(strAdjournmentMotionId!=null && !strAdjournmentMotionId.isEmpty()) {
					deviceForClubbing = ApplicationConstants.DEVICE_ADJOURNMENTMOTIONS;
					AdjournmentMotion adjournmentMotion = AdjournmentMotion.findById(AdjournmentMotion.class, new Long(strAdjournmentMotionId));
					/**** Advanced Search Options ****/
					/**** First we will check if clubbing is allowed on the given motion ****/
					Boolean isClubbingAllowed = isClubbingAllowed(adjournmentMotion, request);
					if(isClubbingAllowed){
						/**** Adjournment Motion subject will be visible on the clubbing page ****/
						if(adjournmentMotion.getRevisedSubject()!=null){
							if(!adjournmentMotion.getRevisedSubject().isEmpty()){
								model.addAttribute("subject",adjournmentMotion.getRevisedSubject());
							}else{
								model.addAttribute("subject",adjournmentMotion.getSubject());
							}
						}else{
							model.addAttribute("subject",adjournmentMotion.getSubject());
						}
						/**** Adjournment Motion number will also be visible ****/
						model.addAttribute("id",Long.parseLong(strAdjournmentMotionId));
						model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(adjournmentMotion.getLocale()).format(adjournmentMotion.getNumber()));
						/**** Member Name will also be visible ****/
						model.addAttribute("memberName",adjournmentMotion.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						/**** Advanced Search Filters****/
						String deviceType = adjournmentMotion.getType().getType();
						model.addAttribute("deviceType", deviceType);
						model.addAttribute("houseType", adjournmentMotion.getHouseType().getType());
											
						int year = adjournmentMotion.getSession().getYear();
						CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
						List<Reference> years = new ArrayList<Reference>();
						if(houseFormationYear != null){
							Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
							for(int i = year; i >= formationYear; i--){
								Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
								years.add(reference);
							}
						}else{
							model.addAttribute("flag", "houseformationyearnotset");
							return "clubbing/error";
						}
						model.addAttribute("years", years);
						model.addAttribute("sessionYear", year);
						List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale.toString());
						model.addAttribute("sessionTypes", sessionTypes);
						model.addAttribute("sessionType", adjournmentMotion.getSession().getType().getId());
						model.addAttribute("whichDevice", "motions_adjournment_");
					}else{
						model.addAttribute("flag","CLUBBING_NOT_ALLOWED");
						return "clubbing/error";
					}
				}else if(strSpecialMentionNoticeId!=null && !strSpecialMentionNoticeId.isEmpty()) {
					deviceForClubbing = ApplicationConstants.DEVICE_SPECIALMENTIONNOTICES;
					SpecialMentionNotice specialMentionNotice = SpecialMentionNotice.findById(SpecialMentionNotice.class, new Long(strSpecialMentionNoticeId));
					/**** Advanced Search Options ****/
					/**** First we will check if clubbing is allowed on the given motion ****/
					Boolean isClubbingAllowed = isClubbingAllowed(specialMentionNotice, request);
					if(isClubbingAllowed){
						/**** Adjournment Motion subject will be visible on the clubbing page ****/
						if(specialMentionNotice.getRevisedSubject()!=null){
							if(!specialMentionNotice.getRevisedSubject().isEmpty()){
								model.addAttribute("subject",specialMentionNotice.getRevisedSubject());
							}else{
								model.addAttribute("subject",specialMentionNotice.getSubject());
							}
						}else{
							model.addAttribute("subject",specialMentionNotice.getSubject());
						}
						/**** Adjournment Motion number will also be visible ****/
						model.addAttribute("id",Long.parseLong(strSpecialMentionNoticeId));
						model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(specialMentionNotice.getLocale()).format(specialMentionNotice.getNumber()));
						/**** Member Name will also be visible ****/
						model.addAttribute("memberName",specialMentionNotice.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						/**** Advanced Search Filters****/
						String deviceType = specialMentionNotice.getType().getType();
						model.addAttribute("deviceType", deviceType);
						model.addAttribute("houseType", specialMentionNotice.getHouseType().getType());
											
						int year = specialMentionNotice.getSession().getYear();
						CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
						List<Reference> years = new ArrayList<Reference>();
						if(houseFormationYear != null){
							Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
							for(int i = year; i >= formationYear; i--){
								Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
								years.add(reference);
							}
						}else{
							model.addAttribute("flag", "houseformationyearnotset");
							return "clubbing/error";
						}
						model.addAttribute("years", years);
						model.addAttribute("sessionYear", year);
						List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale.toString());
						model.addAttribute("sessionTypes", sessionTypes);
						model.addAttribute("sessionType", specialMentionNotice.getSession().getType().getId());
						model.addAttribute("whichDevice", "notices_specialmention_");
					}else{
						model.addAttribute("flag","CLUBBING_NOT_ALLOWED");
						return "clubbing/error";
					}
				} else if(strBillAmendmentMotionId!=null && !strBillAmendmentMotionId.isEmpty()) {
					deviceForClubbing = ApplicationConstants.DEVICE_BILLAMENDMENTMOTIONS;
					BillAmendmentMotion billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, new Long(strBillAmendmentMotionId));
					/**** Advanced Search Options ****/
					/**** First we will check if clubbing is allowed on the given motion ****/
					Boolean isClubbingAllowed = isClubbingAllowed(billAmendmentMotion, request);
					if(isClubbingAllowed){
						/**** Bill Amendment Motion subject line will be visible on the clubbing page ****/
						String amendedBillInfo = billAmendmentMotion.getAmendedBillInfo();
						if(amendedBillInfo!=null && !amendedBillInfo.isEmpty()) {
							amendedBillInfo = amendedBillInfo.replace("#", "~");
						} else {
							model.addAttribute("flag","amendedBillInfo_notfound");
							return "clubbing/error";
						}
						model.addAttribute("amendedBillInfo", amendedBillInfo);
//						if(billAmendmentMotion.getRevisedSubject()!=null){
//							if(!billAmendmentMotion.getRevisedSubject().isEmpty()){
//								model.addAttribute("subject",adjournmentMotion.getRevisedSubject());
//							}else{
//								model.addAttribute("subject",adjournmentMotion.getSubject());
//							}
//						}else{
//							model.addAttribute("subject",adjournmentMotion.getSubject());
//						}
						/**** Bill Amendment Motion number will also be visible ****/
						model.addAttribute("id",Long.parseLong(strBillAmendmentMotionId));
						model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(billAmendmentMotion.getLocale()).format(billAmendmentMotion.getNumber()));
						/**** Member Name will also be visible ****/
						model.addAttribute("memberName",billAmendmentMotion.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						/**** Advanced Search Filters****/
						/**** Amended Bill Languages ****/		
						Bill amendedBill = billAmendmentMotion.getAmendedBill();
						String amendedBillLanguages = amendedBill.findLanguagesOfContentDrafts();
						if(amendedBillLanguages != null && !amendedBillLanguages.isEmpty()) {
							List<Language> languagesAllowedForMotion = new ArrayList<Language>();
							for(String amendedBillLanguage: amendedBillLanguages.split("#")) {
								Language languageAllowed = Language.findByFieldName(Language.class, "type", amendedBillLanguage, amendedBill.getLocale());
								languagesAllowedForMotion.add(languageAllowed);
							}
							model.addAttribute("languagesAllowedForMotion", languagesAllowedForMotion);
						}
						/**** Default Bill Language ****/
						String defaultAmendedBillLanguage = amendedBill.getSession().getParameter(amendedBill.getType().getType()+"_defaultTitleLanguage");
						model.addAttribute("defaultAmendedBillLanguage", defaultAmendedBillLanguage);
						String deviceType = billAmendmentMotion.getType().getType();
						model.addAttribute("deviceType", deviceType);
						model.addAttribute("houseType", billAmendmentMotion.getHouseType().getType());
											
						int year = billAmendmentMotion.getSession().getYear();
						CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
						List<Reference> years = new ArrayList<Reference>();
						if(houseFormationYear != null){
							Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
							for(int i = year; i >= formationYear; i--){
								Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
								years.add(reference);
							}
						}else{
							model.addAttribute("flag", "houseformationyearnotset");
							return "clubbing/error";
						}
						model.addAttribute("years", years);
						model.addAttribute("sessionYear", year);
						List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale.toString());
						model.addAttribute("sessionTypes", sessionTypes);
						model.addAttribute("sessionType", billAmendmentMotion.getSession().getType().getId());
						model.addAttribute("whichDevice", "motions_billamendment_");
					}else{
						model.addAttribute("flag","CLUBBING_NOT_ALLOWED");
						return "clubbing/error";
					}
				}else if(strResolutionId != null && !strResolutionId.isEmpty()){
					deviceForClubbing = ApplicationConstants.DEVICE_RESOLUTIONS;
					Resolution r = Resolution.findById(Resolution.class, new Long(strResolutionId));
					Boolean isClubbingAllowed = false;
					if(strUseForFiling != null && !strUseForFiling.isEmpty() && strUseForFiling.equals("yes")){
						isClubbingAllowed = true;
					}else{
						/**** Advanced Search Options ****/					
						/**** First we will check if clubbing is allowed on the given bill ****/
						//isClubbingAllowed = isClubbingAllowed(motion, request);
					}
					
					if(isClubbingAllowed){
						/**** StandaloneMotion subject will be visible on the clubbing page ****/
						if(r.getRevisedSubject()!=null){
							if(!r.getRevisedSubject().isEmpty()){
								model.addAttribute("subject",r.getRevisedSubject());
							}else{
								model.addAttribute("subject",r.getSubject());
							}
						}else{
							model.addAttribute("subject",r.getSubject());
						}
						/**** StandaloneMotion number will also be visible ****/
						model.addAttribute("id",Long.parseLong(strResolutionId));
						model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(r.getLocale()).format(r.getNumber()));
						/**** Member Name will also be visible ****/
						model.addAttribute("memberName",r.getMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						/**** Advanced Search Filters****/
						String deviceType = r.getType().getType();
						model.addAttribute("deviceType", deviceType);
						model.addAttribute("houseType", r.getHouseType().getType());
						try{
							model.addAttribute("deviceTypes", DeviceType.findAllowedTypesInMotionClubbing(r.getLocale()));
						}catch (ELSException e) {
							model.addAttribute("ClubbedEntityController","Request can not be completed at the moment.");
						}
						
						
						int year = r.getSession().getYear();
						CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
						List<Reference> years = new ArrayList<Reference>();
						if(houseFormationYear != null){
							Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
							for(int i = year; i >= formationYear; i--){
								Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
								years.add(reference);
							}
						}else{
							model.addAttribute("flag", "houseformationyearnotset");
							return "clubbing/error";
						}
						model.addAttribute("years", years);
						model.addAttribute("sessionYear", year);
						List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale.toString());
						model.addAttribute("sessionTypes", sessionTypes);
						model.addAttribute("sessionType", r.getSession().getType().getId());
						model.addAttribute("whichDevice", "resolutions_");
					}else{
						model.addAttribute("flag","CLUBBING_NOT_ALLOWED");
						return "clubbing/error";
					}
				}else if(strRulesSuspensionMotionId !=null && !strRulesSuspensionMotionId.isEmpty()) {
					deviceForClubbing = ApplicationConstants.DEVICE_RULESUSPENSIONMOTIONS;
					RulesSuspensionMotion rulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, new Long(strRulesSuspensionMotionId));
					/**** Advanced Search Options ****/
					/**** First we will check if clubbing is allowed on the given motion ****/
					Boolean isClubbingAllowed = isClubbingAllowed(rulesSuspensionMotion, request);
					if(isClubbingAllowed){
						/**** Adjournment Motion subject will be visible on the clubbing page ****/
						if(rulesSuspensionMotion.getRevisedSubject()!=null){
							if(!rulesSuspensionMotion.getRevisedSubject().isEmpty()){
								model.addAttribute("subject",rulesSuspensionMotion.getRevisedSubject());
							}else{
								model.addAttribute("subject",rulesSuspensionMotion.getSubject());
							}
						}else{
							model.addAttribute("subject",rulesSuspensionMotion.getSubject());
						}
						/**** Adjournment Motion number will also be visible ****/
						model.addAttribute("id",Long.parseLong(strRulesSuspensionMotionId));
						model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(rulesSuspensionMotion.getLocale()).format(rulesSuspensionMotion.getNumber()));
						/**** Member Name will also be visible ****/
						model.addAttribute("memberName",rulesSuspensionMotion.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						/**** Advanced Search Filters****/
						String deviceType = rulesSuspensionMotion.getType().getType();
						model.addAttribute("deviceType", deviceType);
						model.addAttribute("houseType", rulesSuspensionMotion.getHouseType().getType());
											
						int year = rulesSuspensionMotion.getSession().getYear();
						CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
						List<Reference> years = new ArrayList<Reference>();
						if(houseFormationYear != null){
							Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
							for(int i = year; i >= formationYear; i--){
								Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
								years.add(reference);
							}
						}else{
							model.addAttribute("flag", "houseformationyearnotset");
							return "clubbing/error";
						}
						model.addAttribute("years", years);
						model.addAttribute("sessionYear", year);
						List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale.toString());
						model.addAttribute("sessionTypes", sessionTypes);
						model.addAttribute("sessionType", rulesSuspensionMotion.getSession().getType().getId());
						model.addAttribute("whichDevice", "motions_rules_suspension_");
					}else{
						model.addAttribute("flag","CLUBBING_NOT_ALLOWED");
						return "clubbing/error";
					}
				} else{ 
					logger.error("**** Check request parameter for 'id of given device' for null value");
					model.addAttribute("flag","REQUEST_PARAMETER_NULL");
					return "clubbing/error";
				}		
			}
			
			CustomParameter csptDevicesAllowedForBulkClubbing = CustomParameter.findByName(CustomParameter.class, "DEVICES_ALLOWED_FOR_BULK_CLUBBING", "");
			if(csptDevicesAllowedForBulkClubbing!=null && csptDevicesAllowedForBulkClubbing.getValue()!=null) {
				for(String deviceAllowedForBulkClubbing: csptDevicesAllowedForBulkClubbing.getValue().split("~")) {
					if(deviceForClubbing.equalsIgnoreCase(deviceAllowedForBulkClubbing)) {
						int devicesCountLimitAllowedForBulkClubbing = 5;
						CustomParameter csptLimitOfDevicesCountAllowedForBulkClubbing = CustomParameter.findByName(CustomParameter.class, "DEVICES_COUNT_LIMIT_ALLOWED_FOR_BULK_CLUBBING", "");
						if(csptLimitOfDevicesCountAllowedForBulkClubbing!=null && csptLimitOfDevicesCountAllowedForBulkClubbing.getValue()!=null) {
							devicesCountLimitAllowedForBulkClubbing = Integer.parseInt(csptLimitOfDevicesCountAllowedForBulkClubbing.getValue());
						}
						model.addAttribute("devicesCountLimitAllowedForBulkClubbing", devicesCountLimitAllowedForBulkClubbing);
						return "clubbing/bulk_init";
					}
				}
			}
			
			retVal = "clubbing/init";
		}
		return retVal;
	}
	
	

	private Boolean isClubbingAllowed(RulesSuspensionMotion rulesSuspensionMotion, HttpServletRequest request) {
		/**** Clubbing is allowed only if following requirement is met ****/
		String usergroupType = request.getParameter("usergroupType");	
		/****To enable the userGroups who can do clubbing ****/
		CustomParameter clubbingAllowedUserGroups = CustomParameter.findByName(CustomParameter.class, "RSMOIS_ALLOWED_USERGROUP_TO_DO_CLUBBING", "");
		if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
			return true;
		}		
		return false;
	}



	private Boolean isClubbingAllowed(final Question question,final HttpServletRequest request) {
		/**** Clubbing is allowed only if following requirement is met ****/
		String usergroupType=request.getParameter("usergroupType");	
		/****To enable the userGroups who can do clubbing ****/
		CustomParameter clubbingAllowedUserGroups = CustomParameter.findByName(CustomParameter.class, "QIS_ALLOWED_USERGROUP_TO_DO_CLUBBING", "");
		if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
			return true;
		}		
		return false;
	}
	
	private Boolean isClubbingAllowed(final Bill bill,final HttpServletRequest request) {
		/**** Clubbing is allowed only if one of the below requirement is met ****/
		String internalStatusType=bill.getInternalStatus().getType();
		String recommendationStatusType=bill.getRecommendationStatus().getType();
		String deviceType=bill.getType().getType();		
		WorkflowDetails workflowDetails=WorkflowDetails.findCurrentWorkflowDetail(bill,ApplicationConstants.APPROVAL_WORKFLOW);
		String usergroupType=request.getParameter("usergroupType");		
		/**** if deviceType=nonofficial_bill ****/
		if(deviceType.equals(ApplicationConstants.NONOFFICIAL_BILL)) {
			/**** if internal status=assistant_processed,  
			 * workflow has not started and this is assistant's login ****/
			if(((internalStatusType.equals(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED))
					&& workflowDetails.getId()==null&&usergroupType!=null)){
				if(usergroupType.equals("assistant") || usergroupType.equals("clerk")){
					return true;
				}
			} 		
			else {
				/**** if recommendation status=discuss||send back,workflow has started,bill is currently at assistant's login
				 *    if internal status=admitted,workflow has started and bill is currently at assistant's login (facility to be confirmed) ****/
				if((recommendationStatusType.equals(ApplicationConstants.BILL_RECOMMEND_DISCUSS)
						||recommendationStatusType.equals(ApplicationConstants.BILL_RECOMMEND_SENDBACK)
						||internalStatusType.equals(ApplicationConstants.BILL_FINAL_ADMISSION))
						&&workflowDetails.getId()!=null){
					if(workflowDetails.getAssigneeUserGroupType().equals("assistant")
							&&usergroupType!=null){
						if(usergroupType.equals("assistant") || usergroupType.equals("clerk")){
							return true;
						}
					}
				}
			}
		}		
		return false;
	}

	private Boolean isClubbingAllowed(final Motion motion, final HttpServletRequest request) {
		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, "MOTION_CLUBBING_MODE", "");
		
		if(csptClubbingMode == null){
			return false;
		}else{
			if(csptClubbingMode.getValue() == null){
				return false;
			}else{
				if(csptClubbingMode.getValue().isEmpty()){
					return false;
				}
			}
		}
		
		if(csptClubbingMode.getValue().equals("normal")){
			return true;
		}else{
			/**** Clubbing is allowed only if one of the below requirement is met ****/
			String internalStatusType = motion.getInternalStatus().getType();
			String recommendationStatusType = motion.getRecommendationStatus().getType();
			String deviceType = motion.getType().getType();		
			WorkflowDetails workflowDetails;
			try {
				workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(motion);
			
				String usergroupType = request.getParameter("usergroupType");	
				/****To enable the userGroups who can do clubbing ****/
				CustomParameter clubbingAllowedUserGroups = CustomParameter.findByName(CustomParameter.class, "MOIS_ALLOWED_USERGROUP_TO_DO_CLUBBING", "");
				/**** if deviceType=unstarred||half-hour||short notice,internal status=assistant_processed 
				 * ,workflow has not started and this is assistant's login****/
				if(internalStatusType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
						&& workflowDetails == null && usergroupType != null){
					
					if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
						return true;
					}
				}else{ 
					/**** if deviceType=starred,internalStatusType=to_be_put_up,workflow has not started 
					 * and this is assitant's login ****/
					if(internalStatusType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP)
							&& workflowDetails==null&&usergroupType!=null){
						if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
							return true;
						}
					}else{
						// TODO: [HACK 17May2014 Amit] Remove this if condition once the Monsoon session 2014 is over.
						// This condition is added because the first batch questions of
						// Council were manually updated and didn't go in the workflow.
						// Hence, workflowDetails is going to be null.
						if(recommendationStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)
								|| recommendationStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
								|| internalStatusType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)){
							if(usergroupType != null) {
								if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
									return true;
								}
							}
						}
						
						/**** if recommendation status=discuss||send back,workflow has started,question is currently at assistant's login
						 *    if internal status=admitted,workflow has started and question is currently at assistant's login ****/
						if((recommendationStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)
								||recommendationStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK))
								&&workflowDetails.getId() != null){
							if(workflowDetails.getAssigneeUserGroupType().equals("assistant")
									&&usergroupType != null){
								if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
									return true;
								}
							}
						}
					}
				}
			}catch (Exception e) {
			}
			return false;
		}
	}
	
	private Boolean isClubbingAllowed(final StandaloneMotion motion, final HttpServletRequest request) {
		/**** Clubbing is allowed only if one of the below requirement is met ****/
		String internalStatusType = motion.getInternalStatus().getType();
		String recommendationStatusType = motion.getRecommendationStatus().getType();
		String deviceType = motion.getType().getType();		
		WorkflowDetails workflowDetails;
		try {
			workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(motion);
		
			String usergroupType = request.getParameter("usergroupType");	
			/****To enable the userGroups who can do clubbing ****/
			CustomParameter clubbingAllowedUserGroups = CustomParameter.findByName(CustomParameter.class, "SMOIS_ALLOWED_USERGROUP_TO_DO_CLUBBING", "");
			/**** if deviceType=unstarred||half-hour||short notice,internal status=assistant_processed 
			 * ,workflow has not started and this is assistant's login****/
			if(internalStatusType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
					&& workflowDetails == null && usergroupType != null){
				
				if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
					return true;
				}
			}else{ 
				/**** if deviceType=starred,internalStatusType=to_be_put_up,workflow has not started 
				 * and this is assitant's login ****/
				if(internalStatusType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP)
						&& workflowDetails == null && usergroupType != null){
					if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
						return true;
					}
				}else{
					// TODO: [HACK 17May2014 Amit] Remove this if condition once the Monsoon session 2014 is over.
					// This condition is added because the first batch questions of
					// Council were manually updated and didn't go in the workflow.
					// Hence, workflowDetails is going to be null.
					if(recommendationStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS)
							|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
							|| internalStatusType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)){
						if(usergroupType != null) {
							if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
								return true;
							}
						}
					}
					
					/**** if recommendation status=discuss||send back,workflow has started,device is currently at assistant's login
					 *    if internal status=admitted,workflow has started and question is currently at assistant's login ****/
					if((recommendationStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS)
							|| recommendationStatusType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK))
							&& workflowDetails.getId() != null){
						if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.ASSISTANT)
								&&usergroupType != null){
							if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
								return true;
							}
						}
					}
				}
			}
		}catch (Exception e) {
		}
		return false;
	}
	
	private Boolean isClubbingAllowed(final CutMotion motion, final HttpServletRequest request) {
		/**** Clubbing is allowed only if one of the below requirement is met ****/
		String internalStatusType = motion.getInternalStatus().getType();
		String recommendationStatusType = motion.getRecommendationStatus().getType();
		String deviceType = motion.getDeviceType().getType();		
		WorkflowDetails workflowDetails;
		try {
			workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(motion);
		
			String usergroupType = request.getParameter("usergroupType");	
			/****To enable the userGroups who can do clubbing ****/
			CustomParameter clubbingAllowedUserGroups = CustomParameter.findByName(CustomParameter.class, "CMOIS_ALLOWED_USERGROUP_TO_DO_CLUBBING", "");
			/**** if deviceType=unstarred||half-hour||short notice,internal status=assistant_processed 
			 * ,workflow has not started and this is assistant's login****/
			if(internalStatusType.equals(ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED)
					&& workflowDetails == null && usergroupType != null){
				
				if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
					return true;
				}
			}else{ 
				/**** if deviceType=starred,internalStatusType=to_be_put_up,workflow has not started 
				 * and this is assitant's login ****/
				if(internalStatusType.equals(ApplicationConstants.CUTMOTION_SYSTEM_PUTUP)
						&& workflowDetails==null&&usergroupType!=null){
					if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
						return true;
					}
				}else{
					// TODO: [HACK 17May2014 Amit] Remove this if condition once the Monsoon session 2014 is over.
					// This condition is added because the first batch questions of
					// Council were manually updated and didn't go in the workflow.
					// Hence, workflowDetails is going to be null.
					if(recommendationStatusType.equals(ApplicationConstants.CUTMOTION_RECOMMEND_DISCUSS)
							|| recommendationStatusType.equals(ApplicationConstants.CUTMOTION_RECOMMEND_SENDBACK)
							|| internalStatusType.equals(ApplicationConstants.CUTMOTION_FINAL_ADMISSION)){
						if(usergroupType != null) {
							if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
								return true;
							}
						}
					}
					
					/**** if recommendation status=discuss||send back,workflow has started,question is currently at assistant's login
					 *    if internal status=admitted,workflow has started and question is currently at assistant's login ****/
					if((recommendationStatusType.equals(ApplicationConstants.CUTMOTION_RECOMMEND_DISCUSS)
							||recommendationStatusType.equals(ApplicationConstants.CUTMOTION_RECOMMEND_SENDBACK))
							&&workflowDetails.getId() != null){
						if(workflowDetails.getAssigneeUserGroupType().equals("assistant")
								&&usergroupType != null){
							if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
								return true;
							}
						}
					}
				}
			}
		}catch (Exception e) {
		}
		return false;
	}
	
	private Boolean isClubbingAllowed(final EventMotion motion, final HttpServletRequest request) {
		/**** Clubbing is allowed only if one of the below requirement is met ****/
		String internalStatusType = motion.getInternalStatus().getType();
		String recommendationStatusType = motion.getRecommendationStatus().getType();
		String deviceType = motion.getDeviceType().getType();		
		WorkflowDetails workflowDetails;
		try {
			workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(motion);
		
			String usergroupType = request.getParameter("usergroupType");	
			/****To enable the userGroups who can do clubbing ****/
			CustomParameter clubbingAllowedUserGroups = CustomParameter.findByName(CustomParameter.class, "EMOIS_ALLOWED_USERGROUP_TO_DO_CLUBBING", "");
			/**** if deviceType=unstarred||half-hour||short notice,internal status=assistant_processed 
			 * ,workflow has not started and this is assistant's login****/
			if(internalStatusType.equals(ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED)
					&& workflowDetails == null && usergroupType != null){
				
				if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
					return true;
				}
			}else{ 
				/**** if deviceType=starred,internalStatusType=to_be_put_up,workflow has not started 
				 * and this is assitant's login ****/
				if(internalStatusType.equals(ApplicationConstants.EVENTMOTION_SYSTEM_PUTUP)
						&& workflowDetails==null&&usergroupType!=null){
					if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
						return true;
					}
				}else{
					// TODO: [HACK 17May2014 Amit] Remove this if condition once the Monsoon session 2014 is over.
					// This condition is added because the first batch questions of
					// Council were manually updated and didn't go in the workflow.
					// Hence, workflowDetails is going to be null.
					if(recommendationStatusType.equals(ApplicationConstants.EVENTMOTION_RECOMMEND_DISCUSS)
							|| recommendationStatusType.equals(ApplicationConstants.EVENTMOTION_RECOMMEND_SENDBACK)
							|| internalStatusType.equals(ApplicationConstants.EVENTMOTION_FINAL_ADMISSION)){
						if(usergroupType != null) {
							if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
								return true;
							}
						}
					}
					
					/**** if recommendation status=discuss||send back,workflow has started,question is currently at assistant's login
					 *    if internal status=admitted,workflow has started and question is currently at assistant's login ****/
					if((recommendationStatusType.equals(ApplicationConstants.EVENTMOTION_RECOMMEND_DISCUSS)
							||recommendationStatusType.equals(ApplicationConstants.EVENTMOTION_RECOMMEND_SENDBACK))
							&&workflowDetails.getId() != null){
						if(workflowDetails.getAssigneeUserGroupType().equals("assistant")
								&&usergroupType != null){
							if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
								return true;
							}
						}
					}
				}
			}
		}catch (Exception e) {
		}
		return false;
	}
	
	private Boolean isClubbingAllowed(final DiscussionMotion motion, final HttpServletRequest request) {
		/**** Clubbing is allowed only if one of the below requirement is met ****/
		String internalStatusType = motion.getInternalStatus().getType();
		String recommendationStatusType = motion.getRecommendationStatus().getType();
		String deviceType = motion.getType().getType();		
		WorkflowDetails workflowDetails;
		try {
			workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(motion);
		
			String usergroupType = request.getParameter("usergroupType");	
			/****To enable the userGroups who can do clubbing ****/
			CustomParameter clubbingAllowedUserGroups = CustomParameter.findByName(CustomParameter.class, "DMOIS_ALLOWED_USERGROUP_TO_DO_CLUBBING", "");
			/**** if deviceType=unstarred||half-hour||short notice,internal status=assistant_processed 
			 * ,workflow has not started and this is assistant's login****/
			if(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED)
					&& workflowDetails == null && usergroupType != null){
				
				if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
					return true;
				}
			}else{ 
				/**** if deviceType=starred,internalStatusType=to_be_put_up,workflow has not started 
				 * and this is assitant's login ****/
				if(internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_SYSTEM_PUTUP)
						&& workflowDetails==null&&usergroupType!=null){
					if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
						return true;
					}
				}else{
					// TODO: [HACK 17May2014 Amit] Remove this if condition once the Monsoon session 2014 is over.
					// This condition is added because the first batch questions of
					// Council were manually updated and didn't go in the workflow.
					// Hence, workflowDetails is going to be null.
					if(recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_DISCUSS)
							|| recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_SENDBACK)
							|| internalStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION)){
						if(usergroupType != null) {
							if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
								return true;
							}
						}
					}
					
					/**** if recommendation status=discuss||send back,workflow has started,question is currently at assistant's login
					 *    if internal status=admitted,workflow has started and question is currently at assistant's login ****/
					if((recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_DISCUSS)
							||recommendationStatusType.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_SENDBACK))
							&&workflowDetails.getId() != null){
						if(workflowDetails.getAssigneeUserGroupType().equals("assistant")
								&&usergroupType != null){
							if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
								return true;
							}
						}
					}
				}
			}
		}catch (Exception e) {
		}
		return false;
	}
	
	private Boolean isClubbingAllowed(final AdjournmentMotion adjournmentMotion,final HttpServletRequest request) {
		/**** Clubbing is allowed only if following requirement is met ****/
		String usergroupType=request.getParameter("usergroupType");	
//		String internalStatusType = adjournmentMotion.getInternalStatus().getType();
		/****To enable the userGroups who can do clubbing ****/
		CustomParameter clubbingAllowedUserGroups = CustomParameter.findByName(CustomParameter.class, "AMOIS_ALLOWED_USERGROUP_TO_DO_CLUBBING", "");
		if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
//			if(!internalStatusType.equals(ApplicationConstants.ADJOURNMENTMOTION_SUBMIT)) {
//				return true;
//			} else {
//				return false;
//			}	
			return true;
		}		
		return false;
	}
	
	private Boolean isClubbingAllowed(final SpecialMentionNotice specialMentionNotice,final HttpServletRequest request) {
		/**** Clubbing is allowed only if following requirement is met ****/
		String usergroupType=request.getParameter("usergroupType");	
//		String internalStatusType = adjournmentMotion.getInternalStatus().getType();
		/****To enable the userGroups who can do clubbing ****/
		CustomParameter clubbingAllowedUserGroups = CustomParameter.findByName(CustomParameter.class, "SMIS_ALLOWED_USERGROUP_TO_DO_CLUBBING", "");
		if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
			return true;
		}		
		return false;
	}
	
	private Boolean isClubbingAllowed(final BillAmendmentMotion billAmendmentMotion,final HttpServletRequest request) {
		/**** Clubbing is allowed only if following requirement is met ****/
		String usergroupType=request.getParameter("usergroupType");	
		String internalStatusType = billAmendmentMotion.getInternalStatus().getType();
		/****To enable the userGroups who can do clubbing ****/
		CustomParameter clubbingAllowedUserGroups = CustomParameter.findByName(CustomParameter.class, "BAMOIS_ALLOWED_USERGROUP_TO_DO_CLUBBING", "");
		if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
			if(!internalStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_SUBMIT)) {
				return true;
			} else {
				return false;
			}			
		}		
		return false;
	}
	
	@RequestMapping(value="/advancedsearch",method=RequestMethod.GET)
	public String advancedSearch(final HttpServletRequest request,
			final ModelMap model,final Locale locale){
		/**** The processed question id ****/
		String strId=request.getParameter("id");
		if(strId!=null){
			if(!strId.isEmpty()){
				Question question=Question.findById(Question.class,Long.parseLong(strId));
				/**** Advanced Search Filters will depend on the device type of processed question ****/
				String deviceType=question.getType().getType();
				model.addAttribute("deviceType",deviceType);
				model.addAttribute("houseType",question.getHouseType().getType());
				/**** Starred Filters ****/
				if(deviceType.equals(ApplicationConstants.STARRED_QUESTION)){
					try {
						model.addAttribute("deviceTypes",DeviceType.findAllowedTypesInStarredClubbing(locale.toString()));
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<Group> allgroups = new ArrayList<Group>();
					try {
						allgroups = Group.findByHouseTypeSessionTypeYear(question.getHouseType(),question.getSession().getType(),question.getSession().getYear());
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<MasterVO> masterVOs=new ArrayList<MasterVO>();
					for(Group i:allgroups){
						MasterVO masterVO=new MasterVO(i.getId(),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i.getNumber()));
						masterVOs.add(masterVO);
					}
					model.addAttribute("groups",masterVOs);
					int year=question.getSession().getYear();
					CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
					List<Reference> years=new ArrayList<Reference>();
					if(houseFormationYear!=null){
						Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
						for(int i=year;i>=formationYear;i--){
							Reference reference=new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
							years.add(reference);
						}
					}else{
						model.addAttribute("flag", "houseformationyearnotset");
						return "clubbing/error";
					}
					model.addAttribute("years",years);
					model.addAttribute("sessionYear",year);
					List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType",ApplicationConstants.ASC, locale.toString());
					model.addAttribute("sessionTypes",sessionTypes);
					model.addAttribute("sessionType",question.getSession().getType().getId());
				}
			}else{
				logger.error("**** Check request parameter 'id' for no value");
				model.addAttribute("flag","REQUEST_PARAMETER_ISEMPTY");
				return "clubbing/error";
			}
		}else{
			logger.error("**** Check request parameter 'id' for null value");
			model.addAttribute("flag","REQUEST_PARAMETER_NULL");
			return "clubbing/error";
		}

		return "clubbing/advancedsearch";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/search",method=RequestMethod.POST)
	public @ResponseBody List<QuestionSearchVO> searchQuestionForClubbing(final HttpServletRequest request,
			final Locale locale) throws NumberFormatException, ELSException{
		List<QuestionSearchVO> questionSearchVOs=new ArrayList<QuestionSearchVO>();
		String param=request.getParameter("param").trim();
		String questionId=request.getParameter("question");
		String start=request.getParameter("start");
		String noOfRecords=request.getParameter("record");
		Map<String,String[]> requestMap=request.getParameterMap();
		if(questionId!=null&&start!=null&&noOfRecords!=null){
			if((!questionId.isEmpty())&&(!start.isEmpty())&&(!noOfRecords.isEmpty())){
				Question question=Question.findById(Question.class, Long.parseLong(questionId));
				questionSearchVOs=ClubbedEntity.fullTextSearchClubbing(param,question,Integer.parseInt(start),
						Integer.parseInt(noOfRecords),locale.toString(),requestMap);
			}
		}       
		return questionSearchVOs;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchfacility",method=RequestMethod.POST)
	public @ResponseBody List<DeviceSearchVO> searchDeviceForSearchFacility(final HttpServletRequest request,
			final Locale locale){
		List<DeviceSearchVO> deviceSearchVOs = new ArrayList<DeviceSearchVO>();
		try{
			String param = request.getParameter("param").trim();
			String whichDevice = request.getParameter("whichDevice");
			String strDeviceType = request.getParameter("deviceType");
			String strSession = request.getParameter("session");
			String start = request.getParameter("start");
			String noOfRecords = request.getParameter("record");
			
			
			String strHouseType = request.getParameter("houseType");
 			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			
			DeviceType deviceType = null;
			Session session = null;
			HouseType houseType = null;
			Integer sessionYear = null;
			SessionType sessionType = null;
			
			if(strHouseType != null && !strHouseType.isEmpty() && !strHouseType.equals("-")){
				houseType = HouseType.findByType(strHouseType, locale.toString());
			}
			
			if(strSessionYear != null && !strSessionYear.isEmpty() && !strSessionYear.equals("-")){
				sessionYear = new Integer(strSessionYear);
			}
			
			if(strSessionType != null && !strSessionType.isEmpty() && !strSessionType.equals("-")){
				sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
			}
			
			if(houseType != null && sessionYear != null && sessionType != null){
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			}
			
			if(strDeviceType != null && !strDeviceType.isEmpty()){
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
			}
			
			if(session == null){
				if(strSession != null && !strSession.isEmpty()){
					session = Session.findById(Session.class, new Long(strSession));
				}
			}
						
			Map<String,String[]> requestMap = request.getParameterMap();
			if(start != null && noOfRecords != null){
				if((!start.isEmpty()) && (!noOfRecords.isEmpty())){
					deviceSearchVOs = ClubbedEntity.fullTextSearchForSearching(whichDevice, param, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
				}
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		return deviceSearchVOs;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchbill",method=RequestMethod.POST)
	public @ResponseBody List<BillSearchVO> searchBillForClubbing(final HttpServletRequest request,
			final Locale locale){
		List<BillSearchVO> billSearchVOs=new ArrayList<BillSearchVO>();		
		String param=request.getParameter("param").trim();
		String billId=request.getParameter("billId");
		String start=request.getParameter("start");
		String noOfRecords=request.getParameter("record");
		Map<String,String[]> requestMap=request.getParameterMap();
		if(param!=null&&billId!=null&&start!=null&&noOfRecords!=null){
			if((!param.isEmpty()&&!billId.isEmpty())&&(!start.isEmpty())&&(!noOfRecords.isEmpty())){
				Bill bill=Bill.findById(Bill.class, Long.parseLong(billId));
				billSearchVOs=ClubbedEntity.fullTextSearchClubbing(param,bill,Integer.parseInt(start),
						Integer.parseInt(noOfRecords),locale.toString(),requestMap);
			}
		}       
		return billSearchVOs;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchmotion",method=RequestMethod.POST)
	public @ResponseBody List<MotionSearchVO> searchMotionForClubbing(final HttpServletRequest request, final Locale locale){
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		String param = request.getParameter("param").trim();
		String motionId = request.getParameter("motion");
		String start = request.getParameter("start");
		String noOfRecords = request.getParameter("record");
		String strUseForFiling = request.getParameter("filing");
		
		Map<String,String[]> requestMap = request.getParameterMap();
		if(motionId != null && start != null && noOfRecords != null){
			if((!motionId.isEmpty()) && (!start.isEmpty()) && (!noOfRecords.isEmpty())){
				Motion motion = Motion.findById(Motion.class, Long.parseLong(motionId));
				if(strUseForFiling != null && !strUseForFiling.isEmpty() && strUseForFiling.equals("yes")){
					motionSearchVOs = ClubbedEntity.fullTextSearchFiling(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
				}else{
					motionSearchVOs = ClubbedEntity.fullTextSearchClubbing(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
				}
				//motionSearchVOs = ClubbedEntity.fullTextSearchClubbing(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
			}
		}       
		return motionSearchVOs;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchstandalone",method=RequestMethod.POST)
	public @ResponseBody List<QuestionSearchVO> searchStandaloneForClubbing(final HttpServletRequest request, final Locale locale){
		List<QuestionSearchVO> motionSearchVOs = new ArrayList<QuestionSearchVO>();
		String param = request.getParameter("param").trim();
		String motionId = request.getParameter("motion");
		String start = request.getParameter("start");
		String noOfRecords = request.getParameter("record");
		String strUseForFiling = request.getParameter("filing");
		
		Map<String,String[]> requestMap = request.getParameterMap();
		if(motionId != null && start != null && noOfRecords != null){
			if((!motionId.isEmpty()) && (!start.isEmpty()) && (!noOfRecords.isEmpty())){
				StandaloneMotion motion = StandaloneMotion.findById(StandaloneMotion.class, Long.parseLong(motionId));
				if(strUseForFiling != null && !strUseForFiling.isEmpty() && strUseForFiling.equals("yes")){
					motionSearchVOs = ClubbedEntity.fullTextSearchFiling(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
				}else{
					motionSearchVOs = ClubbedEntity.fullTextSearchClubbing(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
				}
			}
		}       
		return motionSearchVOs;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchresolution",method=RequestMethod.POST)
	public @ResponseBody List<QuestionSearchVO> searchResolutionForClubbing(final HttpServletRequest request, final Locale locale){
		List<QuestionSearchVO> resolutionSearchVOs = new ArrayList<QuestionSearchVO>();
		String param = request.getParameter("param").trim();
		String motionId = request.getParameter("resolutionId");
		String start = request.getParameter("start");
		String noOfRecords = request.getParameter("record");
		String strUseForFiling = request.getParameter("filing");
		
		Map<String,String[]> requestMap = request.getParameterMap();
		if(motionId != null && start != null && noOfRecords != null){
			if((!motionId.isEmpty()) && (!start.isEmpty()) && (!noOfRecords.isEmpty())){
				Resolution motion = Resolution.findById(Resolution.class, Long.parseLong(motionId));
				if(strUseForFiling != null && !strUseForFiling.isEmpty() && strUseForFiling.equals("yes")){
					resolutionSearchVOs = ClubbedEntity.fullTextSearchFiling(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
				}else{
					//resolutionSearchVOs = ClubbedEntity.fullTextSearchClubbing(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
				}
			}
		}       
		return resolutionSearchVOs;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchcutmotion",method=RequestMethod.POST)
	public @ResponseBody List<MotionSearchVO> searchCutMotionForClubbing(final HttpServletRequest request, final Locale locale){
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		String param = request.getParameter("param").trim();
		String cutMotionId = request.getParameter("motion");
		String start = request.getParameter("start");
		String noOfRecords = request.getParameter("record");
		Map<String,String[]> requestMap = request.getParameterMap();
		if(cutMotionId != null && start != null && noOfRecords != null){
			if((!cutMotionId.isEmpty()) && (!start.isEmpty()) && (!noOfRecords.isEmpty())){
				CutMotion motion = CutMotion.findById(CutMotion.class, Long.parseLong(cutMotionId));
				motionSearchVOs = ClubbedEntity.fullTextSearchClubbing(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
			}
		}       
		return motionSearchVOs;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searcheventmotion",method=RequestMethod.POST)
	public @ResponseBody List<MotionSearchVO> searchEventMotionForClubbing(final HttpServletRequest request, final Locale locale){
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		String param = request.getParameter("param").trim();
		String eventMotionId = request.getParameter("motion");
		String start = request.getParameter("start");
		String noOfRecords = request.getParameter("record");
		Map<String,String[]> requestMap = request.getParameterMap();
		if(eventMotionId != null && start != null && noOfRecords != null){
			if((!eventMotionId.isEmpty()) && (!start.isEmpty()) && (!noOfRecords.isEmpty())){
				EventMotion motion = EventMotion.findById(EventMotion.class, Long.parseLong(eventMotionId));
				motionSearchVOs = ClubbedEntity.fullTextSearchClubbing(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
			}
		}       
		return motionSearchVOs;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchdiscussionmotion",method=RequestMethod.POST)
	public @ResponseBody List<MotionSearchVO> searchDiscussionMotionForClubbing(final HttpServletRequest request, final Locale locale){
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		String param = request.getParameter("param").trim();
		String discussionMotionId = request.getParameter("motion");
		String start = request.getParameter("start");
		String noOfRecords = request.getParameter("record");
		Map<String,String[]> requestMap = request.getParameterMap();
		if(discussionMotionId != null && start != null && noOfRecords != null){
			if((!discussionMotionId.isEmpty()) && (!start.isEmpty()) && (!noOfRecords.isEmpty())){
				DiscussionMotion motion = DiscussionMotion.findById(DiscussionMotion.class, Long.parseLong(discussionMotionId));
				motionSearchVOs = ClubbedEntity.fullTextSearchClubbing(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
			}
		}       
		return motionSearchVOs;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchadjournmentmotion",method=RequestMethod.POST)
	public @ResponseBody List<MotionSearchVO> searchAdjournmentMotionForClubbing(final HttpServletRequest request, final Locale locale){
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		String param = request.getParameter("param").trim();
		String adjournmentMotionId = request.getParameter("motion");
		String start = request.getParameter("start");
		String noOfRecords = request.getParameter("record");
		Map<String,String[]> requestMap = request.getParameterMap();
		if(adjournmentMotionId != null && start != null && noOfRecords != null){
			if((!adjournmentMotionId.isEmpty()) && (!start.isEmpty()) && (!noOfRecords.isEmpty())){
				AdjournmentMotion motion = AdjournmentMotion.findById(AdjournmentMotion.class, Long.parseLong(adjournmentMotionId));
				motionSearchVOs = ClubbedEntity.fullTextSearchClubbing(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
			}
		}       
		return motionSearchVOs;
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchrulessuspensionmotion",method=RequestMethod.POST)
	public @ResponseBody List<MotionSearchVO> searchRulesSuspensionMotionForClubbing(final HttpServletRequest request, final Locale locale){
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		String param = request.getParameter("param").trim();
		String rulesSuspensionMotionId = request.getParameter("motion");
		String start = request.getParameter("start");
		String noOfRecords = request.getParameter("record");
		Map<String,String[]> requestMap = request.getParameterMap();
		if(rulesSuspensionMotionId != null && start != null && noOfRecords != null){
			if((!rulesSuspensionMotionId.isEmpty()) && (!start.isEmpty()) && (!noOfRecords.isEmpty())){
				RulesSuspensionMotion motion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, Long.parseLong(rulesSuspensionMotionId));
				motionSearchVOs = ClubbedEntity.fullTextSearchClubbing(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
			}
		}       
		return motionSearchVOs;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchspecialmentionnotice",method=RequestMethod.POST)
	public @ResponseBody List<MotionSearchVO> searchSpecialMentionNoticeForClubbing(final HttpServletRequest request, final Locale locale){
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		String param = request.getParameter("param").trim();
		String specialMentionNoticeId = request.getParameter("motion");
		String start = request.getParameter("start");
		String noOfRecords = request.getParameter("record");
		Map<String,String[]> requestMap = request.getParameterMap();
		if(specialMentionNoticeId != null && start != null && noOfRecords != null){
			if((!specialMentionNoticeId.isEmpty()) && (!start.isEmpty()) && (!noOfRecords.isEmpty())){
				SpecialMentionNotice notice = SpecialMentionNotice.findById(SpecialMentionNotice.class, Long.parseLong(specialMentionNoticeId));
				motionSearchVOs = ClubbedEntity.fullTextSearchClubbing(param, notice, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
			}
		}       
		return motionSearchVOs;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchbillamendmentmotion",method=RequestMethod.POST)
	public @ResponseBody List<MotionSearchVO> searchBillAmendmentMotionForClubbing(final HttpServletRequest request,
			final Locale locale){
		List<MotionSearchVO> motionSearchVOs=new ArrayList<MotionSearchVO>();		
		String param=request.getParameter("param").trim();
		String billAmendmentMotionId=request.getParameter("billAmendmentMotionId");
		String start=request.getParameter("start");
		String noOfRecords=request.getParameter("record");
		Map<String,String[]> requestMap=request.getParameterMap();
		if(param!=null&&billAmendmentMotionId!=null&&start!=null&&noOfRecords!=null){
			if((!param.isEmpty()&&!billAmendmentMotionId.isEmpty())&&(!start.isEmpty())&&(!noOfRecords.isEmpty())){
				BillAmendmentMotion billAmendmentMotion=BillAmendmentMotion.findById(BillAmendmentMotion.class, Long.parseLong(billAmendmentMotionId));
				motionSearchVOs=ClubbedEntity.fullTextSearchClubbing(param,billAmendmentMotion,Integer.parseInt(start),
						Integer.parseInt(noOfRecords),locale.toString(),requestMap);
			}
		}       
		return motionSearchVOs;
	}
	
	
	@Transactional
	@RequestMapping(value="/clubbing",method=RequestMethod.POST)
	public @ResponseBody String clubbing(final HttpServletRequest request,final ModelMap model,final Locale locale) throws ELSException{
		String strpId=request.getParameter("pId");
		String strcId=request.getParameter("cId");
		String whichDevice=request.getParameter("whichDevice");		
		String status=null;
		if(strpId!=null&&strcId!=null&&whichDevice!=null){
			if(!strpId.isEmpty()&&!strcId.isEmpty()&&!whichDevice.isEmpty()){
				/**** current user's usergrouptype (needed for clubbing draft updation of edited as) ****/
				String editedAs = null;
				String strUserGroupType = request.getParameter("usergroupType");
				if(strUserGroupType != null && !strUserGroupType.isEmpty()){
					UserGroupType userGroupType = UserGroupType.findByType(strUserGroupType,locale.toString());
					if(userGroupType==null) {
						logger.error("request parameter 'usergroupType' not found");
						throw new ELSException("ClubbedEntityController/clubbing/3", "request parameter 'usergroupType' not found");
					}
					editedAs = userGroupType.getName();
				}
				/**** current user's username (needed for clubbing draft updation of edited by) ****/
				String editedBy = this.getCurrentUser().getActualUsername();
				/**** devices for clubbing ****/
				Long primaryId=Long.parseLong(strpId);
				Long clubbingId=Long.parseLong(strcId);
				if(whichDevice.equals("questions_")) {					
					Question primaryQuestion = Question.findById(Question.class, Long.parseLong(strpId));
					Question clubbingQuestion = Question.findById(Question.class, Long.parseLong(strcId));
					boolean clubResult = false;
					try {
						primaryQuestion.setEditedAs(editedAs);
						primaryQuestion.setEditedBy(editedBy);
						primaryQuestion.setEditedOn(new Date());
						clubbingQuestion.setEditedAs(editedAs);
						clubbingQuestion.setEditedBy(editedBy);
						clubbingQuestion.setEditedOn(new Date());
						clubResult = Question.club(primaryQuestion, clubbingQuestion, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "CLUBBING_SUCCESS";
					} else {
						status = "CLUBBING_FAILURE";
					}
				} else if(whichDevice.equals("bills_")) {
					status=ClubbedEntity.clubBill(primaryId, clubbingId, locale.toString());
				} else if(whichDevice.equals("motions_")){
					//status = Motion.clubMotion(primaryId, clubbingId, locale.toString());
					boolean clubResult = false;
					try {
						clubResult = Motion.club(primaryId, clubbingId, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "CLUBBING_SUCCESS";
					} else {
						status = "CLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_standalonemotion_")){
					//status = ClubbedEntity.clubStandalone(primaryId, clubbingId, locale.toString());
					boolean clubResult = false;
					try {
						clubResult = StandaloneMotion.club(primaryId, clubbingId, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "CLUBBING_SUCCESS";
					} else {
						status = "CLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_eventmotion_")){
					//status = ClubbedEntity.clubStandalone(primaryId, clubbingId, locale.toString());
					boolean clubResult = false;
					try {
						clubResult = EventMotion.club(primaryId, clubbingId, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "CLUBBING_SUCCESS";
					} else {
						status = "CLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_cutmotion_")){
					//status = ClubbedEntity.clubStandalone(primaryId, clubbingId, locale.toString());
					boolean clubResult = false;
					try {
						clubResult = CutMotion.club(primaryId, clubbingId, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "CLUBBING_SUCCESS";
					} else {
						status = "CLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_discussion_")){
					//status = ClubbedEntity.clubStandalone(primaryId, clubbingId, locale.toString());
					boolean clubResult = false;
					try {
						clubResult = DiscussionMotion.club(primaryId, clubbingId, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "CLUBBING_SUCCESS";
					} else {
						status = "CLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_adjournment_")) {					
					AdjournmentMotion primaryMotion = AdjournmentMotion.findById(AdjournmentMotion.class, Long.parseLong(strpId));
					AdjournmentMotion clubbingMotion = AdjournmentMotion.findById(AdjournmentMotion.class, Long.parseLong(strcId));
					boolean clubResult = false;
					try {
						primaryMotion.setEditedAs(editedAs);
						primaryMotion.setEditedBy(editedBy);
						primaryMotion.setEditedOn(new Date());
						clubbingMotion.setEditedAs(editedAs);
						clubbingMotion.setEditedBy(editedBy);
						clubbingMotion.setEditedOn(new Date());
						clubResult = AdjournmentMotion.club(primaryMotion, clubbingMotion, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "CLUBBING_SUCCESS";
					} else {
						status = "CLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("notices_specialmention_")) {					
					SpecialMentionNotice primaryMotion = SpecialMentionNotice.findById(SpecialMentionNotice.class, Long.parseLong(strpId));
					SpecialMentionNotice clubbingMotion = SpecialMentionNotice.findById(SpecialMentionNotice.class, Long.parseLong(strcId));
					boolean clubResult = false;
					try {
						primaryMotion.setEditedAs(editedAs);
						primaryMotion.setEditedBy(editedBy);
						primaryMotion.setEditedOn(new Date());
						clubbingMotion.setEditedAs(editedAs);
						clubbingMotion.setEditedBy(editedBy);
						clubbingMotion.setEditedOn(new Date());
						clubResult = SpecialMentionNotice.club(primaryMotion, clubbingMotion, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "CLUBBING_SUCCESS";
					} else {
						status = "CLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_billamendment_")) {					
					BillAmendmentMotion primaryMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, Long.parseLong(strpId));
					BillAmendmentMotion clubbingMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, Long.parseLong(strcId));
					boolean clubResult = false;
					try {
						primaryMotion.setEditedAs(editedAs);
						primaryMotion.setEditedBy(editedBy);
						primaryMotion.setEditedOn(new Date());
						clubbingMotion.setEditedAs(editedAs);
						clubbingMotion.setEditedBy(editedBy);
						clubbingMotion.setEditedOn(new Date());
						clubResult = BillAmendmentMotion.club(primaryMotion, clubbingMotion, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "CLUBBING_SUCCESS";
					} else {
						status = "CLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_rules_suspension_")) {					
					RulesSuspensionMotion primaryMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, Long.parseLong(strpId));
					RulesSuspensionMotion clubbingMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, Long.parseLong(strcId));
					boolean clubResult = false;
					try {
						primaryMotion.setEditedAs(editedAs);
						primaryMotion.setEditedBy(editedBy);
						primaryMotion.setEditedOn(new Date());
						clubbingMotion.setEditedAs(editedAs);
						clubbingMotion.setEditedBy(editedBy);
						clubbingMotion.setEditedOn(new Date());
						clubResult = RulesSuspensionMotion.club(primaryMotion, clubbingMotion, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "CLUBBING_SUCCESS";
					} else {
						status = "CLUBBING_FAILURE";
					}
				}
			}
		}
		return status;
	}
	
	@Transactional
	@RequestMapping(value="/reverse_clubbing",method=RequestMethod.POST)
	public  @ResponseBody String reverseClubbing(final HttpServletRequest request,final ModelMap model,final Locale locale) throws ELSException{
		String whichDevice=request.getParameter("whichDevice");
		String deviceId=request.getParameter("deviceId");
		String status = "REVERSE_CLUBBING_FAILURE";
		if(whichDevice!=null && !whichDevice.isEmpty()
				&& deviceId!=null && !deviceId.isEmpty()){			
			if(whichDevice.equals(ApplicationConstants.DEVICE_MOTIONS_CALLING)) {
				boolean reverseClubbingResult = false;
				try {
					Motion motion = Motion.findById(Motion.class, Long.parseLong(deviceId));
					reverseClubbingResult = Motion.reverseClub(motion);
				} catch (Exception e) {
					e.printStackTrace();				
				} finally {
					if(reverseClubbingResult) {
						status = "REVERSE_CLUBBING_SUCCESS";
					}
				}				
			}
		}
		return status;
	}

	@Transactional
	@RequestMapping(value="/unclubbing",method=RequestMethod.POST)
	public  @ResponseBody String unclubbing(final HttpServletRequest request,final ModelMap model,final Locale locale) throws ELSException{
		String strpId=request.getParameter("pId");
		String strcId=request.getParameter("cId");
		String whichDevice=request.getParameter("whichDevice");
		String status=null;
		if(strpId!=null&&strcId!=null&&whichDevice!=null){
			if(!strpId.isEmpty()&&!strcId.isEmpty()&&!whichDevice.isEmpty()){
				/**** current user's usergrouptype (needed for clubbing draft updation of edited as) ****/
				String editedAs = null;
				String strUserGroupType = request.getParameter("usergroupType");
				if(strUserGroupType != null && !strUserGroupType.isEmpty()){
					UserGroupType userGroupType = UserGroupType.findByType(strUserGroupType,locale.toString());
					if(userGroupType==null) {
						logger.error("request parameter 'usergroupType' not found");
						throw new ELSException("ClubbedEntityController/clubbing/3", "request parameter 'usergroupType' not found");
					}
					editedAs = userGroupType.getName();
				}
				/**** current user's username (needed for clubbing draft updation of edited by) ****/
				String editedBy = this.getCurrentUser().getActualUsername();
				/**** devices for unclubbing ****/
				Long primaryId=Long.parseLong(strpId);
				Long clubbingId=Long.parseLong(strcId);
				if(whichDevice.equals("questions_")) {
					Question primaryQuestion = Question.findById(Question.class, Long.parseLong(strpId));
					Question clubbingQuestion = Question.findById(Question.class, Long.parseLong(strcId));
					primaryQuestion.setEditedAs(editedAs);
					primaryQuestion.setEditedBy(editedBy);
					primaryQuestion.setEditedOn(new Date());
					clubbingQuestion.setEditedAs(editedAs);
					clubbingQuestion.setEditedBy(editedBy);
					clubbingQuestion.setEditedOn(new Date());
					boolean clubResult = false;
					try {
						clubResult = Question.unclub(primaryQuestion, clubbingQuestion, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "UNCLUBBING_SUCCESS";
					} else {
						status = "UNCLUBBING_FAILURE";
					}
				} else if(whichDevice.equals("bills_")) {
					status=ClubbedEntity.unclubBill(primaryId, clubbingId, locale.toString());
				}else if(whichDevice.equals("motions_")) {
					//status=ClubbedEntity.unclubMotion(primaryId, clubbingId, locale.toString());
					boolean clubResult = false;
					try {
						clubResult = Motion.unclub(primaryId, clubbingId, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "UNCLUBBING_SUCCESS";
					} else {
						status = "UNCLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_standalonemotion_")) {
					//status=ClubbedEntity.unclubStandalone(primaryId, clubbingId, locale.toString());
					boolean clubResult = false;
					try {
						clubResult = StandaloneMotion.unclub(primaryId, clubbingId, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "UNCLUBBING_SUCCESS";
					} else {
						status = "UNCLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_eventmotion_")) {
					//status=ClubbedEntity.unclubStandalone(primaryId, clubbingId, locale.toString());
					boolean clubResult = false;
					try {
						clubResult = EventMotion.unclub(primaryId, clubbingId, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "UNCLUBBING_SUCCESS";
					} else {
						status = "UNCLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_cutmotion_")) {
					//status=ClubbedEntity.unclubStandalone(primaryId, clubbingId, locale.toString());
					boolean clubResult = false;
					try {
						clubResult = CutMotion.unclub(primaryId, clubbingId, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "UNCLUBBING_SUCCESS";
					} else {
						status = "UNCLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_discussion_")) {
					//status=ClubbedEntity.unclubStandalone(primaryId, clubbingId, locale.toString());
					boolean clubResult = false;
					try {
						clubResult = DiscussionMotion.unclub(primaryId, clubbingId, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "UNCLUBBING_SUCCESS";
					} else {
						status = "UNCLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_adjournment_")) {					
					AdjournmentMotion primaryMotion = AdjournmentMotion.findById(AdjournmentMotion.class, Long.parseLong(strpId));
					AdjournmentMotion clubbingMotion = AdjournmentMotion.findById(AdjournmentMotion.class, Long.parseLong(strcId));
					boolean clubResult = false;
					try {
						primaryMotion.setEditedAs(editedAs);
						primaryMotion.setEditedBy(editedBy);
						primaryMotion.setEditedOn(new Date());
						clubbingMotion.setEditedAs(editedAs);
						clubbingMotion.setEditedBy(editedBy);
						clubbingMotion.setEditedOn(new Date());
						clubResult = AdjournmentMotion.unclub(primaryMotion, clubbingMotion, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "UNCLUBBING_SUCCESS";
					} else {
						status = "UNCLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("notices_specialmention_")) {					
					SpecialMentionNotice primaryMotion = SpecialMentionNotice.findById(SpecialMentionNotice.class, Long.parseLong(strpId));
					SpecialMentionNotice clubbingMotion = SpecialMentionNotice.findById(SpecialMentionNotice.class, Long.parseLong(strcId));
					boolean clubResult = false;
					try {
						primaryMotion.setEditedAs(editedAs);
						primaryMotion.setEditedBy(editedBy);
						primaryMotion.setEditedOn(new Date());
						clubbingMotion.setEditedAs(editedAs);
						clubbingMotion.setEditedBy(editedBy);
						clubbingMotion.setEditedOn(new Date());
						clubResult = SpecialMentionNotice.unclub(primaryMotion, clubbingMotion, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "UNCLUBBING_SUCCESS";
					} else {
						status = "UNCLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_billamendment_")) {					
					BillAmendmentMotion primaryMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, Long.parseLong(strpId));
					BillAmendmentMotion clubbingMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, Long.parseLong(strcId));
					boolean clubResult = false;
					try {
						primaryMotion.setEditedAs(editedAs);
						primaryMotion.setEditedBy(editedBy);
						primaryMotion.setEditedOn(new Date());
						clubbingMotion.setEditedAs(editedAs);
						clubbingMotion.setEditedBy(editedBy);
						clubbingMotion.setEditedOn(new Date());
						clubResult = BillAmendmentMotion.unclub(primaryMotion, clubbingMotion, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "UNCLUBBING_SUCCESS";
					} else {
						status = "UNCLUBBING_FAILURE";
					}
				}else if(whichDevice.equals("motions_rules_suspension_")) {					
					RulesSuspensionMotion primaryMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, Long.parseLong(strpId));
					RulesSuspensionMotion clubbingMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, Long.parseLong(strcId));
					boolean clubResult = false;
					try {
						primaryMotion.setEditedAs(editedAs);
						primaryMotion.setEditedBy(editedBy);
						primaryMotion.setEditedOn(new Date());
						clubbingMotion.setEditedAs(editedAs);
						clubbingMotion.setEditedBy(editedBy);
						clubbingMotion.setEditedOn(new Date());
						clubResult = RulesSuspensionMotion.unclub(clubbingMotion, locale.toString());
					} catch (ELSException e) {
						status = e.getParameter("error");
						if(status!=null) {
							return status;
						} else {
							e.printStackTrace();
						}
					}
					if(clubResult) {
						status = "UNCLUBBING_SUCCESS";
					} else {
						status = "UNCLUBBING_FAILURE";
					}
				}									
			}
		}
		return status;
	}
}
