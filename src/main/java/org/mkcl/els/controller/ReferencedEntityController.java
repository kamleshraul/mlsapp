package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.ReferencedEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/refentity")
public class ReferencedEntityController {

	@RequestMapping(value="/init",method=RequestMethod.GET)
	public String getReferencing(final HttpServletRequest request,final ModelMap model){
		String strquestionId=request.getParameter("id");
		if(strquestionId!=null){
			if(!strquestionId.isEmpty()){
				Question question=Question.findById(Question.class,Long.parseLong(strquestionId));
				if(question.getRevisedSubject()!=null){
					if(!question.getRevisedSubject().isEmpty()){
						model.addAttribute("subject",question.getRevisedSubject());
					}else{
						model.addAttribute("subject",question.getSubject());
					}
				}else{
					model.addAttribute("subject",question.getSubject());
				}
				List<ReferencedEntity> referencedEntities=question.getReferencedEntities();
				List<Reference> references=new ArrayList<Reference>();
				StringBuffer buffer=new StringBuffer();
				if(!referencedEntities.isEmpty()){
					for(ReferencedEntity i:referencedEntities){
						Reference reference=new Reference();
						reference.setId(String.valueOf(i.getId()));
						reference.setName(String.valueOf(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(i.getQuestion().getNumber())));
						buffer.append(i.getId()+",");
						references.add(reference);
					}
					model.addAttribute("referencedQuestions",references);
					model.addAttribute("referencedQuestionsIds",buffer.toString());
				}
				model.addAttribute("id",Long.parseLong(strquestionId));
				model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getNumber()));
			}
		}
		return "referencing/init";
	}
	
	@RequestMapping(value="/search",method=RequestMethod.POST)
    public @ResponseBody List<QuestionSearchVO> searchQuestionForReferencing(final HttpServletRequest request,
            final Locale locale){
		List<QuestionSearchVO> questionSearchVOs=new ArrayList<QuestionSearchVO>();
        String param=request.getParameter("param").trim();
        String questionId=request.getParameter("question");
        String start=request.getParameter("start");
        String noOfRecords=request.getParameter("record");
        if(questionId!=null&&start!=null&&noOfRecords!=null){
                if((!questionId.isEmpty())&&(!start.isEmpty())&&(!noOfRecords.isEmpty())){
                    Question question=Question.findById(Question.class, Long.parseLong(questionId));
                    questionSearchVOs=Question.fullTextSearchReferencing(param,question,Integer.parseInt(start),Integer.parseInt(noOfRecords),locale.toString());
                }
        }       
        return questionSearchVOs;
    }
	
	@Transactional
	@RequestMapping(value="/referencing",method=RequestMethod.POST)
	public @ResponseBody String referencing(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strpId=request.getParameter("pId");
		String strcId=request.getParameter("rId");
		Boolean status=false;
		if(strpId!=null&&strcId!=null){
			if(!strpId.isEmpty()&&!strcId.isEmpty()){
				Long primaryId=Long.parseLong(strpId);
				Long clubbingId=Long.parseLong(strcId);
				status=Question.referencing(primaryId, clubbingId, locale.toString());				
			}
		}
		if(status){
			return "SUCCESS";
		}else{
			return "FAILED";
		}				
	}

	@Transactional
	@RequestMapping(value="/dereferencing",method=RequestMethod.POST)
	public  @ResponseBody String dereferencing(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strpId=request.getParameter("pId");
		String strcId=request.getParameter("rId");
		Boolean status=false;
		if(strpId!=null&&strcId!=null){
			if(!strpId.isEmpty()&&!strcId.isEmpty()){
				Long primaryId=Long.parseLong(strpId);
				Long clubbingId=Long.parseLong(strcId);
				status=Question.deReferencing(primaryId, clubbingId, locale.toString());				
			}
		}
		if(status){
			return "SUCCESS";
		}else{
			return "FAILED";
		}		
	}
}
