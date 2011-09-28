package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.MemberDetails;
import org.mkcl.els.domain.MemberPositionsDetails;
import org.mkcl.els.service.ICustomParameterService;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.IMemberDetailsService;
import org.mkcl.els.service.IMemberPositionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/member_other_details")
public class MemberOtherDetailsController {
	
	@Autowired
	IGridService gridService;
	
	@Autowired
	IMemberDetailsService memberDetailsService;
	
	@Autowired
	IMemberPositionsService memberPositionsService;
	
	@Autowired
	ICustomParameterService customParameterService;
			
	@RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
	public String edit(HttpServletRequest request,@PathVariable Long id, ModelMap model){
		MemberDetails memberOtherDetails = memberDetailsService.findById(id);
		List<MemberPositionsDetails> memberPositionsDetails=memberOtherDetails.getMemberPositions();
		if(!(memberPositionsDetails.isEmpty())){
		model.addAttribute("positions",memberPositionsDetails);
		model.addAttribute("noOfPositions",memberPositionsDetails.size());
		}
		model.addAttribute("memberOtherDetails", memberOtherDetails);
		request.getSession().setAttribute("refresh","");
		return "member_details/other/edit";
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public String edit(@Valid @ModelAttribute("memberOtherDetails") MemberDetails memberOtherDetails,BindingResult result, ModelMap model,HttpServletRequest request,@RequestParam int noOfPositions){
		this.validate(memberOtherDetails,result);
		if(result.hasErrors()){
			List<MemberPositionsDetails> positions=new ArrayList<MemberPositionsDetails>();
			for(int i=1;i<=noOfPositions;i++){
				MemberPositionsDetails position=new MemberPositionsDetails();
				if(request.getParameter("positionPeriod"+i)!=null&&request.getParameter("positionDetail"+i)!=null){
					position.setPeriod(request.getParameter("positionPeriod"+i));
					position.setDetails(request.getParameter("positionDetail"+i));
					positions.add(position);
				}			
			}			
			model.addAttribute("positions",positions);
			model.addAttribute("noOfPositions",positions.size());			
			model.addAttribute("memberOtherDetails", memberOtherDetails);
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
			return "member_details/other/edit";
		}
		for(int i=1;i<=noOfPositions;i++){
			MemberPositionsDetails position=new MemberPositionsDetails();
			if(request.getParameter("positionPeriod"+i)!=null&&request.getParameter("positionDetail"+i)!=null){
				position.setPeriod(request.getParameter("positionPeriod"+i));
				position.setDetails(request.getParameter("positionDetail"+i));
				position.setMember(memberOtherDetails);
				if(request.getParameter("positionId"+i)==null||request.getParameter("positionId"+i).isEmpty()){
					memberPositionsService.create(position);
				}
				else{
					position.setId(Long.parseLong(request.getParameter("positionId"+i)));
					memberPositionsService.update(position);
				}
			}			
		}
		memberDetailsService.updateMemberOtherDetails(memberOtherDetails);
		if(customParameterService.findByName("MIS_PROGRESSIVE_DISPLAY").getValue().equals("PROGRESSIVE")){
			return "redirect:/member_personal_details/"+memberOtherDetails.getId()+"/edit?type=success&msg=update_success";
		}
		else{
		return "redirect:member_other_details/"+memberOtherDetails.getId()+"/edit?type=success&msg=update_success";
		}
	}

	private void validate(MemberDetails memberOtherDetails, Errors errors) {
	
		
	}
}
