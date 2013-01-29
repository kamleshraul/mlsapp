package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
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
	public String getClubbing(final HttpServletRequest request,final ModelMap model){
		String strquestionId=request.getParameter("id");
		if(strquestionId!=null){
			if(!strquestionId.isEmpty()){
				Question question=Question.findById(Question.class,Long.parseLong(strquestionId));
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
			logger.error("**** Check request parameter 'id' for null value");
			model.addAttribute("flag","REQUEST_PARAMETER_NULL");
			return "clubbing/error";
		}
		return "clubbing/init";
	}

	private Boolean isClubbingAllowed(final Question question,final HttpServletRequest request) {
		/**** Clubbing is allowed only if one of the below requirement is met ****/
		String internalStatusType=question.getInternalStatus().getType();
		String recommendationStatusType=question.getRecommendationStatus().getType();
		String deviceType=question.getType().getType();		
		WorkflowDetails workflowDetails=WorkflowDetails.findCurrentWorkflowDetail(question);
		String usergroupType=request.getParameter("usergroupType");		
		/**** if deviceType=unstarred||half-hour||short notice,internal status=assistant_processed 
		 * ,workflow has not started and this is assistant's login****/
		if((deviceType.equals(ApplicationConstants.UNSTARRED_QUESTION)
				||deviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)
				||deviceType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION))
				&&(internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED))
				&& workflowDetails.getId()==null&&usergroupType!=null){
			if(usergroupType.equals("assistant")){
				return true;
			}
		}else 
			/**** if deviceType=starred,internalStatusType=to_be_put_up,workflow has not started 
			 * and this is assitant's login ****/
			if(deviceType.equals(ApplicationConstants.STARRED_QUESTION)
					&&internalStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
					&& workflowDetails.getId()==null&&usergroupType!=null){
				if(usergroupType.equals("assistant")){
					return true;
				}
			}else
				/**** if recommendation status=discuss||send back,workflow has started,question is currently at assistant's login
				 *    if internal status=admitted,workflow has started and question is currently at assistant's login ****/
				if((recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)
						||recommendationStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
						||internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION))
						&&workflowDetails.getId()!=null){
					if(workflowDetails.getAssigneeUserGroup().equals("assistant")
							&&usergroupType!=null){
						if(usergroupType.equals("assistant")){
							return true;
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
					model.addAttribute("deviceTypes",DeviceType.getAllowedTypesInStarredClubbing(locale.toString()));
					List<Group> allgroups=Group.findByHouseTypeSessionTypeYear(question.getHouseType(),question.getSession().getType(),question.getSession().getYear());
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

	@Transactional
	@RequestMapping(value="/clubbing",method=RequestMethod.POST)
	public @ResponseBody String clubbing(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strpId=request.getParameter("pId");
		String strcId=request.getParameter("cId");
		String status=null;
		if(strpId!=null&&strcId!=null){
			if(!strpId.isEmpty()&&!strcId.isEmpty()){
				Long primaryId=Long.parseLong(strpId);
				Long clubbingId=Long.parseLong(strcId);
				status=ClubbedEntity.club(primaryId, clubbingId, locale.toString());
			}
		}
		return status;
	}

	@Transactional
	@RequestMapping(value="/unclubbing",method=RequestMethod.POST)
	public  @ResponseBody String unclubbing(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strpId=request.getParameter("pId");
		String strcId=request.getParameter("cId");
		String status=null;
		if(strpId!=null&&strcId!=null){
			if(!strpId.isEmpty()&&!strcId.isEmpty()){
				Long primaryId=Long.parseLong(strpId);
				Long clubbingId=Long.parseLong(strcId);
				status=ClubbedEntity.unclub(primaryId, clubbingId, locale.toString());
			}
		}
		return status;
	}
}
