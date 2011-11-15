package org.mkcl.els.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.AssemblyEditor;
import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.common.vo.MemberInRoleVO;
import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.MemberDetails;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.service.IAssemblyRoleService;
import org.mkcl.els.service.IAssemblyService;
import org.mkcl.els.service.ICustomParameterService;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.IMemberDetailsService;
import org.mkcl.els.service.IMemberRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/member_role")
public class MemberRoleController {
	@Autowired
	IGridService gridService;
	
	@Autowired
	IAssemblyService assemblyService;
	
	@Autowired
	IAssemblyRoleService assemblyRoleService;
	
	@Autowired
	ICustomParameterService customParameterService;
	
	@Autowired
	IMemberDetailsService memberDetailsService;
	
	@Autowired
	IMemberRoleService memberRoleService;
	
	@RequestMapping(value="assignroles/list",method = RequestMethod.GET)
	public String indexMembers(ModelMap model){
		Grid grid = gridService.findByName("MMS_ASSIGNROLE");
		model.addAttribute("gridId", grid.getId());
		return "member_mgmt/roles/assignroles/list";
	}
	
	@RequestMapping(value="assignroles/{member_id}/new",method=RequestMethod.GET)
	public String _assignroles(ModelMap model,Locale locale,@PathVariable("member_id")Long memberId){
		MemberRole memberRole=new MemberRole();
		memberRole.setMember(memberDetailsService.findById(memberId));
		memberRole.setLocale(locale.toString());
		model.addAttribute("memberRole",memberRole);
		model.addAttribute("member",memberId);
		populateModelRoleNew(model,locale.toString(),memberId);
		return "member_mgmt/roles/assignroles/new";
	}
	
	@RequestMapping(value="assignroles/{member_id}/edit",method=RequestMethod.GET)
	public String _editroles(ModelMap model,Locale locale,@PathVariable("member_id")Long memberId,HttpServletRequest request){
		List<MemberRole> memberRoles=memberRoleService.findByMemberId(memberId);		
		model.addAttribute("memberRoles",memberRoles);
		populateModelRoleEdit(model,locale.toString(),memberId);
		model.addAttribute("memberId",memberId);
		MemberDetails memberDetails=memberDetailsService.findById(memberId);
		model.addAttribute("memberName",memberDetails.getFirstName()+" "+memberDetails.getMiddleName()+" "+memberDetails.getLastName());
		model.addAttribute("noOfRecords",memberRoles.size());
		request.getSession().setAttribute("refresh","");
		return "member_mgmt/roles/assignroles/edit";
	}	


	@RequestMapping(value="assignroles",method = RequestMethod.POST)
	public String createRoles(@Valid 
			@ModelAttribute("memberRole") MemberRole memberRole, 
			BindingResult result, ModelMap model,HttpServletRequest request,@RequestParam Long memberId,@RequestParam String[] roles){
		this.validate(memberRole, result);		
		if(result.hasErrors()){
			model.addAttribute("memberRole",memberRole);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "member_mgmt/roles/assignroles/new";
		}	
		memberRole.setMember(memberDetailsService.findById(memberId));
		for(String i:roles){
			MemberRole memberRoleToInsert=new MemberRole();
			memberRoleToInsert.setAssembly(memberRole.getAssembly());
			memberRoleToInsert.setFromDate(memberRole.getFromDate());
			memberRoleToInsert.setLocale(memberRole.getLocale());
			memberRoleToInsert.setMember(memberRole.getMember());
			memberRoleToInsert.setRemarks(memberRole.getRemarks());
			memberRoleToInsert.setRole(assemblyRoleService.findById(Long.parseLong(i)));
			memberRoleToInsert.setToDate(memberRole.getToDate());
			memberRoleToInsert.setVersion(memberRole.getVersion());
			memberRoleService.create(memberRoleToInsert);
		}
		return "redirect:/member_role/assignroles/"+memberRole.getMember().getId()+"/edit?type=success&msg=create_success";
	
	}
	@RequestMapping(value="assignroles/update",method = RequestMethod.POST)
	public String updateRoles(ModelMap model,HttpServletRequest request,@RequestParam Long memberId,@RequestParam Integer noOfRecords){
		SimpleDateFormat formatServer=new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue());
		if(noOfRecords>=1){
			try {
				for(int i=1;i<=noOfRecords;i++){
					MemberRole memberRole=new MemberRole();
					memberRole.setAssembly(assemblyService.findById(Long.parseLong(request.getParameter("assembly"+i))));
					String fromDate=request.getParameter("fromDate"+i);
					String toDate=request.getParameter("toDate"+i);
					if(fromDate!=null){
						if(!fromDate.equals("")){
							memberRole.setFromDate(formatServer.parse(fromDate));
						}
					}
					if(toDate!=null){
						if(!toDate.equals("")){
							memberRole.setToDate(formatServer.parse(toDate));
						}
					}
					memberRole.setRemarks(request.getParameter("remarks"+i));
					memberRole.setRole(assemblyRoleService.findById(Long.parseLong(request.getParameter("role"+i))));
					memberRole.setMember(memberDetailsService.findById(Long.parseLong(request.getParameter("memberId"))));
					memberRole.setVersion(Long.parseLong(request.getParameter("version"+i)));
					memberRole.setId(Long.parseLong(request.getParameter("id"+i)));
					memberRole.setLocale(request.getParameter("locale"+i));
					memberRoleService.update(memberRole);
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		return "redirect:/member_role/assignroles/"+memberId+"/edit?type=success&msg=create_success";
	
	}
	
	
	@RequestMapping(value="assignmembers/list",method = RequestMethod.GET)
	public String indexRoles(ModelMap model){
		Grid grid = gridService.findByName("MMS_ASSIGNMEMBER");
		model.addAttribute("gridId", grid.getId());
		return "member_mgmt/roles/assignmembers/list";
	}
	@RequestMapping(value="assignmembers/{role_id}/new",method=RequestMethod.GET)
	public String _assignmembers(ModelMap model,Locale locale,@PathVariable("role_id")Long roleId){
		MemberRole memberRole=new MemberRole();
		memberRole.setRole(assemblyRoleService.findById(roleId));
		memberRole.setLocale(locale.toString());
		model.addAttribute("memberRole",memberRole);
		populateModelMemberNew(model,locale.toString(),roleId);
		return "member_mgmt/roles/assignmembers/new";
	}
	
	@RequestMapping(value="assignmembers/{role_id}/edit",method=RequestMethod.GET)
	public String _updatemembers(ModelMap model,Locale locale,@PathVariable("role_id")Long roleId,HttpServletRequest request){
		List<MemberRole> memberRoles=memberRoleService.findByRoleId(roleId);		
		model.addAttribute("memberRoles",memberRoles);
		model.addAttribute("noOfRecords",memberRoles.size());
		request.getSession().setAttribute("refresh","");
		model.addAttribute("role",assemblyRoleService.findById(roleId));
		populateModelMemberEdit(model,locale.toString(),roleId);
		return "member_mgmt/roles/assignmembers/edit";
	}
	
	
	@InitBinder 
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()); 
		dateFormat.setLenient(true); 
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Assembly.class, new AssemblyEditor(assemblyService)); 

	}
	
	private void populateModelMemberNew(ModelMap model, String locale,
			Long roleId) {
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
		model.addAttribute("members",memberRoleService.findUnassignedMembers(roleId));
	}
	
	private void populateModelMemberEdit(ModelMap model, String locale,
			Long roleId) {
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
	}
	
	private void populateModelRoleNew(ModelMap model,String locale,Long memberId){
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
		model.addAttribute("roles",assemblyRoleService.findUnassignedRoles(locale,memberId));
	}
	private void populateModelRoleEdit(ModelMap model, String locale, Long memberId) {
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
		model.addAttribute("roles",assemblyRoleService.findAllSorted(locale));		
	}
	
	private void validate(MemberRole memberRole, Errors errors){
		
	}
}
