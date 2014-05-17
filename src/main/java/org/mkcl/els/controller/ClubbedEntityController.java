package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.SessionType;
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
		String strquestionId=request.getParameter("id");
		if(strquestionId!=null){
			if(!strquestionId.isEmpty()){
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
					/**** Question number will also be visible ****/
					model.addAttribute("id",Long.parseLong(strquestionId));
					model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getNumber()));
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
			if(strbillId!=null) {
				if(!strbillId.isEmpty()) {
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
			} else {
				logger.error("**** Check request parameter for 'id of given device' for null value");
				model.addAttribute("flag","REQUEST_PARAMETER_NULL");
				return "clubbing/error";
			}			
		}
		return "clubbing/init";
	}

	private Boolean isClubbingAllowed(final Question question,final HttpServletRequest request) {
		/**** Clubbing is allowed only if one of the below requirement is met ****/
		String internalStatusType=question.getInternalStatus().getType();
		String recommendationStatusType=question.getRecommendationStatus().getType();
		String deviceType=question.getType().getType();		
		WorkflowDetails workflowDetails;
		try {
			workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(question);
		
			String usergroupType=request.getParameter("usergroupType");	
			/****To enable the userGroups who can do clubbing ****/
			CustomParameter clubbingAllowedUserGroups = CustomParameter.findByName(CustomParameter.class, "QIS_ALLOWED_USERGROUP_TO_DO_CLUBBING", "");
			/**** if deviceType=unstarred||half-hour||short notice,internal status=assistant_processed 
			 * ,workflow has not started and this is assistant's login****/
			if((deviceType.equals(ApplicationConstants.UNSTARRED_QUESTION)
					||deviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)
					||deviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
					||deviceType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION))
					&&(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED))
					&& workflowDetails==null&&usergroupType!=null){
				
				if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
					return true;
				}
			}else{ 
				/**** if deviceType=starred,internalStatusType=to_be_put_up,workflow has not started 
				 * and this is assitant's login ****/
				if(deviceType.equals(ApplicationConstants.STARRED_QUESTION)
						&&internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
						&& workflowDetails==null&&usergroupType!=null){
					if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
						return true;
					}
				}else{
					// TODO: [HACK 17May2014 Amit] Remove this if condition once the Monsoon session 2014 is over.
					// This condition is added because the first batch questions of
					// Council were manually updated and didn't go in the workflow.
					// Hence, workflowDetails is going to be null.
					if(recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)
							|| recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
							|| internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
						if(usergroupType != null 
								&& (usergroupType.equals("assistant")
										|| usergroupType.equals("clerk"))) {
							if(clubbingAllowedUserGroups != null && clubbingAllowedUserGroups.getValue().contains(usergroupType)){
								return true;
							}
						}
					}
					
					/**** if recommendation status=discuss||send back,workflow has started,question is currently at assistant's login
					 *    if internal status=admitted,workflow has started and question is currently at assistant's login ****/
					if((recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)
							||recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK))
							&&workflowDetails.getId()!=null){
						if(workflowDetails.getAssigneeUserGroupType().equals("assistant")
								&&usergroupType!=null){
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
				if(usergroupType.equals("assistant")){
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
						if(usergroupType.equals("assistant")){
							return true;
						}
					}
				}
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
			final Locale locale){
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

	@Transactional
	@RequestMapping(value="/clubbing",method=RequestMethod.POST)
	public @ResponseBody String clubbing(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strpId=request.getParameter("pId");
		String strcId=request.getParameter("cId");
		String whichDevice=request.getParameter("whichDevice");
		String status=null;
		if(strpId!=null&&strcId!=null&&whichDevice!=null){
			if(!strpId.isEmpty()&&!strcId.isEmpty()&&!whichDevice.isEmpty()){
				Long primaryId=Long.parseLong(strpId);
				Long clubbingId=Long.parseLong(strcId);
				if(whichDevice.equals("questions_")) {
					status=ClubbedEntity.club(primaryId, clubbingId, locale.toString());
				} else if(whichDevice.equals("bills_")) {
					status=ClubbedEntity.clubBill(primaryId, clubbingId, locale.toString());
				}				
			}
		}
		return status;
	}

	@Transactional
	@RequestMapping(value="/unclubbing",method=RequestMethod.POST)
	public  @ResponseBody String unclubbing(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strpId=request.getParameter("pId");
		String strcId=request.getParameter("cId");
		String whichDevice=request.getParameter("whichDevice");
		String status=null;
		if(strpId!=null&&strcId!=null&&whichDevice!=null){
			if(!strpId.isEmpty()&&!strcId.isEmpty()&&!whichDevice.isEmpty()){
				Long primaryId=Long.parseLong(strpId);
				Long clubbingId=Long.parseLong(strcId);
				if(whichDevice.equals("questions_")) {
					status=ClubbedEntity.unclub(primaryId, clubbingId, locale.toString());
				} else if(whichDevice.equals("bills_")) {
					status=ClubbedEntity.unclubBill(primaryId, clubbingId, locale.toString());
				}				
			}
		}
		return status;
	}
}
