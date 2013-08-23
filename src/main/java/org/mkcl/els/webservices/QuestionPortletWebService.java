package org.mkcl.els.webservices;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.common.vo.MemberInfo;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SupportingMember;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/ws/vservices")
public class QuestionPortletWebService {

	/**
	 * Gets the biography.
	 *
	 * @param id the id
	 * @param locale the locale
	 * @return the biography
	 */
	@RequestMapping(value = "/biography/{id}/{locale}")
	public @ResponseBody MemberBiographyVO getBiography(@PathVariable("id") final long id ,
	    @PathVariable("locale") final String locale,
	    final HttpServletRequest request){
	String constituency = null;
	String party=null;
	String gender=null;
	String maritalstatus=null;
	//for tomcat
	CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
	if(customParameter!=null){
	if(customParameter.getValue().equals("TOMCAT")){
	try {
		constituency = new String(request.getParameter("constituency").getBytes("ISO-8859-1"),"UTF-8");
		party = new String(request.getParameter("party").getBytes("ISO-8859-1"),"UTF-8");
		gender = new String(request.getParameter("gender").getBytes("ISO-8859-1"),"UTF-8");
		maritalstatus = new String(request.getParameter("maritalstatus").getBytes("ISO-8859-1"),"UTF-8");
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	}else{
	//for glassfish
	constituency = request.getParameter("constituency");
	party = request.getParameter("party");
	gender = request.getParameter("gender");
	maritalstatus = request.getParameter("maritalstatus");
		}
		}
		String[] data={constituency,party,gender,maritalstatus};
	    return Member.findBiography(id , locale,data);
	}
	
	@RequestMapping(value = "/getproceeds/{id}")
	public @ResponseBody List<MasterVO> getAllQuestions(@PathVariable("id") final Long id, HttpServletRequest request){
		
		List<MasterVO> masterVOs = null;
		List<Question> questions = null;
		Status status = null;
		DeviceType deviceType = null;
		
		try{
			status = Status.findByType("question_final_admission", "mr_IN");
			deviceType = DeviceType.findById(DeviceType.class, id);
			
			if(deviceType != null){
				if(status != null){
					questions  = Question.findByDeviceAndStatus(deviceType, status);
					if(questions != null){
						if(questions.size() > 0){
							masterVOs = new ArrayList<MasterVO>();
							for(Question q : questions){
								MasterVO masterVO = new MasterVO();
								
								masterVO.setId(q.getId());
								masterVO.setName(q.getNumber().toString());
								masterVO.setName(q.getSubject());
								masterVO.setValue(q.getQuestionText());
								
								masterVOs.add(masterVO);
								masterVO = null;
							}
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return masterVOs;
	}
	
	@RequestMapping(value = "/getallproceeds")
	public @ResponseBody List<MasterVO> getQuestions(HttpServletRequest request){
		
		List<MasterVO> masterVOs = null;
		List<Question> questions = null;
		Status status = null;
		
		try{
			status = Status.findByType("question_final_admission", "mr_IN");
			
			if(status != null){
				questions  = Question.findAllByFieldName(Question.class, "status", status, "type", "ASC", "mr_IN");
				if(questions != null){
					if(questions.size() > 0){
						masterVOs = new ArrayList<MasterVO>();
						for(Question q : questions){
							MasterVO masterVO = new MasterVO();
							
							masterVO.setId(q.getId());
							masterVO.setNumber(q.getNumber());
							masterVO.setName(q.getSubject());
							masterVO.setValue(q.getQuestionText());
							
							masterVOs.add(masterVO);
							masterVO = null;
						}
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return masterVOs;
	}
	
	@RequestMapping(value = "/getalldevices",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getDevices(HttpServletRequest request){
		
		List<MasterVO> masterVOs = null;
		List<DeviceType> devices = null;
		Status status = null;
		
		try{
			status = Status.findByType("question_final_admission", "mr_IN");
			
			if(status != null){
				devices  = DeviceType.findAll(DeviceType.class, "id", "ASC", "mr_IN");
				if(devices != null){
					if(devices.size() > 0){
						masterVOs = new ArrayList<MasterVO>();
						for(DeviceType d : devices){
							MasterVO masterVO = new MasterVO();
							
							masterVO.setId(d.getId());
							masterVO.setName(d.getType());
							masterVO.setValue(d.getName());
							
							masterVOs.add(masterVO);
							masterVO = null;
						}
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return masterVOs;
	}
	
	@RequestMapping(value = "/viewquestion/{id}",method=RequestMethod.GET)
	public @ResponseBody QuestionSearchVO viewQuestion(@PathVariable("id") final Long id, HttpServletRequest request){
		
		QuestionSearchVO qSearchVO = null;
		Question question = null;
		
		try{
			 question = Question.findById(Question.class, id);
			 
			 if(question != null){
				 qSearchVO = new QuestionSearchVO();
				 
				 qSearchVO.setId(question.getId());
				 qSearchVO.setNumber(question.getNumber().toString());
				 qSearchVO.setSubject(question.getSubject());
				 qSearchVO.setQuestionText(question.getQuestionText());
				 qSearchVO.setStatus(question.getStatus().getName());
				 qSearchVO.setStatusType(question.getStatus().getType());
				 qSearchVO.setDeviceType(question.getType().getName());
				 qSearchVO.setSessionType(question.getSession().getType().getSessionType());
				 qSearchVO.setSessionYear(question.getSession().getYear().toString());
				 qSearchVO.setMinistry(question.getMinistry().getName());
				 if(question.getDepartment() != null){
					 qSearchVO.setDepartment(question.getDepartment().getName());
				 }
				 if(question.getSubDepartment() != null){
					 qSearchVO.setSubDepartment(question.getSubDepartment().getName()); 
				 }
				 qSearchVO.setPrimaryMember(question.getPrimaryMember().getId().toString());
				 qSearchVO.setFormattedPrimaryMember(question.getPrimaryMember().getFirstName()+" "+ question.getPrimaryMember().getMiddleName()+" "+question.getPrimaryMember().getLastName());
				 String[] supportingMembers = null;
				 String[] formattedSupportingMembers = null;
				 if(question.getSupportingMembers().size() > 0){
					 supportingMembers = new String[question.getSupportingMembers().size()];
					 formattedSupportingMembers = new String[question.getSupportingMembers().size()];
				 }
				 int i = 0;
				 for(SupportingMember sm: question.getSupportingMembers()){
					 Member m = sm.getMember();
					 
					 supportingMembers[i] = m.getId().toString();
					 formattedSupportingMembers[i] = m.getFirstName() + " " + m.getMiddleName() + " " + m.getLastName();
				 }
				 qSearchVO.setSupportingMembers(supportingMembers);
				 qSearchVO.setFormattedSupportingMembers(formattedSupportingMembers);
			 }
			 
		}catch (Exception e) {
			e.printStackTrace();
		}
		return qSearchVO;
	}
	
	
	@RequestMapping(value = "/member/{id}",method=RequestMethod.GET)
	public @ResponseBody MemberInfo getMember(@PathVariable("id") final Long id, HttpServletRequest request){
		
		MemberInfo memberInfo = null;
		Member member = null;
		
		try{
			 member = Member.findById(Member.class, id);
			 
			 if(member != null){
				 memberInfo = new MemberInfo();
				 
				 memberInfo.setId(member.getId());
				 if(member.getElectionResults() != null){
					 if(member.getElectionResults().size() > 0){
						 memberInfo.setConstituency(member.getElectionResults().get(0).getConstituency().getName());
					 }
				 }
				 
				 memberInfo.setMaritalStatus(member.getMaritalStatus().getName());
				 memberInfo.setGender(member.getGender().getName());
				 
				 if(member.getMemberPartyAssociations() != null){
					 if(member.getMemberPartyAssociations().size() > 0){
						 memberInfo.setParty(member.getMemberPartyAssociations().get(0).getParty().getName());
					 }
				 }				 
			 }
			 
		}catch (Exception e) {
			e.printStackTrace();
		}
		return memberInfo;
	}
}

