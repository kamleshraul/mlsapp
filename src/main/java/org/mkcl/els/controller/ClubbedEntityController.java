package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Question;
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
				/**** if question is already clubbed ****/
				if(question.getParent()!=null){
					model.addAttribute("parent",question.getParent().getNumber());
					return "clubbing/noclubbing";
				}
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
				/**** List of already clubbed entities ****/
				List<ClubbedEntity> clubbedEntities=question.getClubbedEntities();
				List<Reference> references=new ArrayList<Reference>();
				StringBuffer buffer=new StringBuffer();
				if(!clubbedEntities.isEmpty()){
					for(ClubbedEntity i:clubbedEntities){
						Reference reference=new Reference();
						reference.setId(String.valueOf(i.getId()));
						reference.setName(String.valueOf(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(i.getQuestion().getNumber())));
						buffer.append(i.getId()+",");
						references.add(reference);
					}
					model.addAttribute("clubbedQuestions",references);
					model.addAttribute("clubbedQuestionsIds",buffer.toString());
				}
				model.addAttribute("id",Long.parseLong(strquestionId));
				model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getNumber()));
			}else{
				logger.error("**** Check request parameter 'id' for no value");
			}
		}else{
			logger.error("**** Check request parameter 'id' for null value");
		}
		return "clubbing/init";
	}
	
	@RequestMapping(value="/search",method=RequestMethod.POST)
    public @ResponseBody List<QuestionSearchVO> searchQuestionForClubbing(final HttpServletRequest request,
            final Locale locale){
        List<QuestionSearchVO> questionSearchVOs=new ArrayList<QuestionSearchVO>();
        String param=request.getParameter("param").trim();
        String questionId=request.getParameter("question");
        String start=request.getParameter("start");
        String noOfRecords=request.getParameter("record");
        if(questionId!=null&&start!=null&&noOfRecords!=null){
                if((!questionId.isEmpty())&&(!start.isEmpty())&&(!noOfRecords.isEmpty())){
                    Question question=Question.findById(Question.class, Long.parseLong(questionId));
                    questionSearchVOs=Question.fullTextSearchClubbing(param,question,Integer.parseInt(start),Integer.parseInt(noOfRecords),locale.toString());
                }
        }       
        return questionSearchVOs;
    }
	
	@Transactional
	@RequestMapping(value="/clubbing",method=RequestMethod.POST)
	public  String clubbing(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strpId=request.getParameter("pId");
		String strcId=request.getParameter("cId");
		Boolean status=false;
		if(strpId!=null&&strcId!=null){
			if(!strpId.isEmpty()&&!strcId.isEmpty()){
				Long primaryId=Long.parseLong(strpId);
				Long clubbingId=Long.parseLong(strcId);
				try{
					status=Question.club(primaryId, clubbingId, locale.toString());
				}catch(Exception ex){
					ex.printStackTrace();
					status=false;
				}
			}
		}
		model.addAttribute("status",status);
		return "clubbing/clubbingresult";
	}

	@Transactional
	@RequestMapping(value="/unclubbing",method=RequestMethod.POST)
	public  String unclubbing(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strpId=request.getParameter("pId");
		String strcId=request.getParameter("cId");
		Boolean status=false;
		if(strpId!=null&&strcId!=null){
			if(!strpId.isEmpty()&&!strcId.isEmpty()){
				Long primaryId=Long.parseLong(strpId);
				Long clubbingId=Long.parseLong(strcId);
				try{
					status=Question.unclub(primaryId, clubbingId, locale.toString());
				}catch(Exception ex){
					ex.printStackTrace();
					status=false;
				}
			}
		}
		model.addAttribute("unclubbingstatus",status);
		return "clubbing/clubbingresult";
	}

}
