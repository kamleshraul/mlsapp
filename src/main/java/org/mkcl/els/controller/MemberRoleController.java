package org.mkcl.els.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.AssemblyEditor;
import org.mkcl.els.common.editors.AssemblyRoleEditor;
import org.mkcl.els.common.editors.MemberEditor;
import org.mkcl.els.common.vo.Filter;
import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.AssemblyRole;
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
import org.springframework.ui.Model;
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
public class MemberRoleController extends BaseController{
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
	public String indexMembers(Model model){
		Grid grid = gridService.findByName("MMS_ASSIGNROLE");
		model.addAttribute("gridId", grid.getId());
		return "member_mgmt/roles/assignroles/list";
	}

	@RequestMapping(value="assignroles/new",method=RequestMethod.GET)
	public String _noMembers(Model model,Locale locale){
		return "member_mgmt/roles/assignroles/nomembers";
	}

	@RequestMapping(value="assignroles/{member_id}/new",method=RequestMethod.GET)
	public String _assignroles(Model model,Locale locale,@PathVariable("member_id")Long memberId){
		MemberRole memberRole=new MemberRole();
		memberRole.setMember(memberDetailsService.findById(memberId));
		Assembly assembly=assemblyService.findCurrentAssembly(locale.toString());
		memberRole.setAssembly(assembly);
		memberRole.setFromDate(assembly.getAssemblyStartDate());
		memberRole.setToDate(assembly.getAssemblyEndDate());
		memberRole.setLocale(locale.toString());
		model.addAttribute("memberRole",memberRole);
		populateModelRoleNew(model,locale.toString(),memberId);
		model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()).format(new Date()));
		return "member_mgmt/roles/assignroles/new";
	}

	@RequestMapping(value="assignroles/{member_id}/edit",method=RequestMethod.GET)
	public String _editroles(Model model,Locale locale,@PathVariable("member_id")Long memberId,HttpServletRequest request){
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
	
	@RequestMapping(value="assignroles/memberrole/{memberrole_id}/edit",method=RequestMethod.GET)
	public String _editMemberRole(Model model,Locale locale,@PathVariable("memberrole_id")Long memberRoleId,HttpServletRequest request){
		MemberRole memberRole=memberRoleService.findById(memberRoleId);		
		model.addAttribute("memberRole",memberRole);
		model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()).format(new Date()));
		return "member_mgmt/roles/assignroles/edit_memberrole";
	}	
	
	@RequestMapping(value="assignroles/createMemberRoles",method = RequestMethod.POST)
	public String createRoles(Locale locale,@RequestParam String assignmentDate,@Valid 
			@ModelAttribute("memberRole") MemberRole memberRole, 
			BindingResult result, Model model,HttpServletRequest request,@RequestParam Long memberId,@RequestParam(required=false) String[] roles,@RequestParam(required=false) String[] roles_check){
		memberRole.setMember(memberDetailsService.findById(memberId));
		
		/*
		 * Check:role can be assigned only before an assembly ends or is dissolved.
		 */
		this.validate(memberRole, result,assignmentDate);
		
		/*
		 *The roles the user selected.
		 */
		StringBuffer selectedRoles=new StringBuffer();
		/*
		 * The member roles that are already present for a given member,assembly,from date,to date and status assigned.These entries will be updated to assigned.
		 */
		Map<String,MemberRole> unassignedMemberRoles=new HashMap<String, MemberRole>();		
		/*
		 * Check:Selected user is not a member and selected roles is null then role not null is displayed.
		 * Check:Selected user is not a member and one of the selected role is not 'Member' then NotMember is displayed.
		 * Check:Selected user is a member and an entry already exists for a given role,member,assembly,from date,to date and status as 'Assigned' then NonUnique error is displayed.
		 */
		if(!memberRoleService.isMember(memberRole.getMember(),memberRole.getAssembly(),memberRole.getFromDate(),memberRole.getToDate())){
			int count=0;
			if(roles_check!=null){
				for(String i:roles_check){
					if(i.equals(customParameterService.findByName("DEFAULT_MEMBERROLE").getValue())){
						count++;
					}
				}
				for(String i:roles){				
					memberRole.setRole(assemblyRoleService.findById(Long.parseLong(i)));
					MemberRole duplicateMemberRole = memberRoleService.checkForDuplicateMemberRole(memberRole);
					if(duplicateMemberRole!=null){
						if(duplicateMemberRole.getStatus().equals(customParameterService.findByName("MEMBERROLE_UNASSIGNED").getValue())){
							unassignedMemberRoles.put(i,duplicateMemberRole);
						}else{
							String[] errorArgs = new String[1];
							errorArgs[0]=memberRole.getRole().getName();				
							result.rejectValue("assembly","NonUnique", errorArgs, "Kindly unselect role{0} as an entry already exists for this member having role:{0} between selected period and assembly.");	
						}			
					}						
					selectedRoles.append(i+",");						
				}
			}else{
				result.rejectValue("role","NotNull");
			}			
			if(count==0){
				result.rejectValue("role","NotMember");
			}
			
		}
		/*
		 * Check:Selected user is a member and selected roles is null then role not null is displayed.
		 * Check:Selected user is a member and an entry already exists for a given role,member,assembly,from date,to date and status as 'Assigned' then NonUnique error is displayed.
		 */		
		else{
			if(roles!=null){
				for(String i:roles){				
					memberRole.setRole(assemblyRoleService.findById(Long.parseLong(i)));
					MemberRole duplicateMemberRole = memberRoleService.checkForDuplicateMemberRole(memberRole);
					if(duplicateMemberRole!=null){
						if(duplicateMemberRole.getStatus().equals(customParameterService.findByName("MEMBERROLE_UNASSIGNED"))){
							unassignedMemberRoles.put(i,duplicateMemberRole);
						}else{
							String[] errorArgs = new String[1];
							errorArgs[0]=memberRole.getRole().getName();				
							result.rejectValue("assembly","NonUnique", errorArgs, "An entry already exists for this member having role:{0} between selected period and assembly");	
						}			
					}						
					selectedRoles.append(i+",");						
				}
			}				
			else{
				result.rejectValue("role","NotNull");
			}
		}		
		if(result.hasErrors()){
			model.addAttribute("memberRole",memberRole);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");		
			model.addAttribute("selectedroles",selectedRoles.toString());
			populateModelRoleNew(model,locale.toString(),memberId);
			model.addAttribute("assignmentDate",assignmentDate);
			return "member_mgmt/roles/assignroles/new";
		}
		/*
		 * A member role is updated only if an entry already exists for the given assembly,member,from date,to date
		 * but has been previously unassigned.
		 * A new member role is created if there is no entry for the given assembly,member,from date and to date.
		 */
		for(String i:roles){
			if(unassignedMemberRoles.containsKey(i)){
				MemberRole memberRoleToUpdate=unassignedMemberRoles.get(i);
				memberRoleToUpdate.setStatus(customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue());
				memberRoleService.update(memberRoleToUpdate);
			}else{
				MemberRole memberRoleToInsert=new MemberRole();
				memberRoleToInsert.setAssembly(memberRole.getAssembly());
				memberRoleToInsert.setFromDate(memberRole.getFromDate());
				memberRoleToInsert.setLocale(memberRole.getLocale());
				memberRoleToInsert.setMember(memberRole.getMember());
				memberRoleToInsert.setRemarks(memberRole.getRemarks());
				memberRoleToInsert.setRole(assemblyRoleService.findById(Long.parseLong(i)));
				memberRoleToInsert.setToDate(memberRole.getToDate());
				memberRoleToInsert.setVersion(memberRole.getVersion());
				memberRoleToInsert.setStatus(customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue());
				memberRoleService.create(memberRoleToInsert);
			}			
		}
		return "redirect:/member_role/assignroles/"+memberRole.getMember().getId()+"/edit?type=success&msg=create_success";

	}

	@RequestMapping(value="assignroles/unassignMemberRoles",method=RequestMethod.POST)
	public String unassignMemberRoles(@RequestParam String memberRolesToUnassign,@RequestParam Long memberId){
		String temp[]=memberRolesToUnassign.split(",");
		for(String i:temp){
			if(!i.equals("")){
				MemberRole memberRole=memberRoleService.findById(Long.parseLong(i));
				memberRole.setStatus(customParameterService.findByName("MEMBERROLE_UNASSIGNED").getValue());
				memberRoleService.update(memberRole);
			}
		}
		return "redirect:/member_role/assignroles/"+memberId+"/edit?type=success&msg=update_success";		
	}
	
	@RequestMapping(value="assignroles/deleteMemberRoles",method=RequestMethod.POST)
	public String deleteMemberRoles(@RequestParam String memberRolesToDelete,@RequestParam String memberId){
		String temp[]=memberRolesToDelete.split(",");		
		for(String i:temp){
			if(!i.equals("")){
				memberRoleService.removeById(Long.parseLong(i));
			}
		}
		
		return "redirect:/member_role/assignroles/"+memberId+"/edit?type=success&msg=delete_success";				
	}
	
	
	@RequestMapping(value="assignroles/updateMemberRole",method = RequestMethod.POST)
	public String updateRoles(Model model,HttpServletRequest request,@RequestParam Long member,@RequestParam String assignmentDate,@Valid@ModelAttribute("memberRole") MemberRole memberRole,BindingResult result){
		this.validate(memberRole, result, assignmentDate);
		if(result.hasErrors()){
			model.addAttribute("memberRole",memberRole);
			model.addAttribute("assignmentDate",assignmentDate);
			model.addAttribute("type","error");
			model.addAttribute("msg","update_failed");	
			return "member_mgmt/roles/assignroles/edit_memberrole";
		}
		memberRoleService.update(memberRole);							
		return "redirect:/member_role/assignroles/"+member+"/edit?type=success&msg=update_success";	
	}

	@RequestMapping(value="assignmembers/list",method = RequestMethod.GET)
	public String indexRoles(Model model){
		Grid grid = gridService.findByName("MMS_ASSIGNMEMBER");
		model.addAttribute("gridId", grid.getId());
		return "member_mgmt/roles/assignmembers/list";
	}

	@RequestMapping(value="assignmembers/new",method=RequestMethod.GET)
	public String _assignmembers(Model model,Locale locale){
		return "member_mgmt/roles/assignmembers/noroles";
	}

	@RequestMapping(value="assignmembers/{role_id}/new",method=RequestMethod.GET)
	public String _assignmembers(Model model,Locale locale,@PathVariable("role_id")Long roleId){
		MemberRole memberRole=new MemberRole();
		Assembly assembly=assemblyService.findCurrentAssembly(locale.toString());
		memberRole.setAssembly(assembly);
		memberRole.setFromDate(assembly.getAssemblyStartDate());
		memberRole.setToDate(assembly.getAssemblyEndDate());
		memberRole.setRole(assemblyRoleService.findById(roleId));
		memberRole.setLocale(locale.toString());
		model.addAttribute("memberRole",memberRole);
		model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()).format(new Date()));
		populateModelMemberNew(model,locale.toString());
		return "member_mgmt/roles/assignmembers/new";
	}

	@RequestMapping(value="assignmembers/{role_id}/edit",method=RequestMethod.GET)
	public String _updatemembers(Model model,Locale locale,@PathVariable("role_id")Long roleId,HttpServletRequest request){
		List<MemberRole> memberRoles=memberRoleService.findByRoleId(roleId);		
		model.addAttribute("memberRoles",memberRoles);
		model.addAttribute("noOfRecords",memberRoles.size());
		request.getSession().setAttribute("refresh","");
		model.addAttribute("role",assemblyRoleService.findById(roleId));
		populateModelMemberEdit(model,locale.toString(),roleId);
		return "member_mgmt/roles/assignmembers/edit";
	}
	
	@RequestMapping(value="assignmembers/memberrole/{memberrole_id}/edit",method=RequestMethod.GET)
	public String _editMemberRoleMembers(Model model,Locale locale,@PathVariable("memberrole_id")Long memberRoleId,HttpServletRequest request){
		MemberRole memberRole=memberRoleService.findById(memberRoleId);		
		model.addAttribute("memberRole",memberRole);
		model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()).format(new Date()));
		return "member_mgmt/roles/assignmembers/edit_memberrole";
	}

	@RequestMapping(value="assignmembers/createMemberRoles",method=RequestMethod.POST)
	public String createMemberRoles(HttpServletRequest request,@RequestParam String membersToAssign,@Valid @ModelAttribute("memberRole")MemberRole memberRole,BindingResult result,Model model,@RequestParam String assignmentDate,Locale locale){
		this.validate(memberRole, result,assignmentDate);
		Map<String,MemberRole> unassignedMemberRoles=new HashMap<String, MemberRole>();			
		if(membersToAssign.isEmpty()){
			result.rejectValue("member","NotNull");
		}else{
			String[] members=membersToAssign.split(",");
			for(String i:members){
				MemberDetails member=memberDetailsService.findById(Long.parseLong(i));
				memberRole.setMember(member);
				MemberRole duplicateMemberRole = memberRoleService.checkForDuplicateMemberRole(memberRole);
				if(duplicateMemberRole!=null){
					if(duplicateMemberRole.getStatus().equals(customParameterService.findByName("MEMBERROLE_UNASSIGNED").getValue())){
						unassignedMemberRoles.put(i,duplicateMemberRole);
					}else{
						String[] errorArgs = new String[1];
						errorArgs[0]=memberRole.getMember().getFirstName();				
						result.rejectValue("member","NonUnique", errorArgs, "Kindly unselect member {0} as an entry already exists for this role having member {0} between selected period and assembly");	
					}			
				}		
			}
		}		
		if(result.hasErrors()){
			model.addAttribute("memberRole",memberRole);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()).format(new Date()));
			populateModelMemberNew(model,locale.toString());			
			return "member_mgmt/roles/assignmembers/new";
		}		
		String temp[]=membersToAssign.split(",");
		for(String i:temp){
			if(unassignedMemberRoles.containsKey(i)){
				MemberRole memberRoleToUpdate=unassignedMemberRoles.get(i);
				memberRoleToUpdate.setStatus(customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue());
				memberRoleService.update(memberRoleToUpdate);
			}else{
				MemberRole memberRoleToInsert=new MemberRole();
				memberRoleToInsert.setAssembly(memberRole.getAssembly());
				memberRoleToInsert.setFromDate(memberRole.getFromDate());
				memberRoleToInsert.setLocale(memberRole.getLocale());
				memberRoleToInsert.setMember(memberDetailsService.findById(Long.parseLong(i)));
				memberRoleToInsert.setRemarks(memberRole.getRemarks());
				memberRoleToInsert.setRole(memberRole.getRole());
				memberRoleToInsert.setToDate(memberRole.getToDate());
				memberRoleToInsert.setVersion(memberRole.getVersion());
				memberRoleToInsert.setStatus(customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue());
				memberRoleService.create(memberRoleToInsert);
			}
		}
		return "redirect:/member_role/assignmembers/"+memberRole.getRole().getId()+"/edit?type=success&msg=update_success";		
	}

	@RequestMapping(value="assignmembers/updateMemberRole",method = RequestMethod.POST)
	public String updateMember(Model model,HttpServletRequest request,@RequestParam Long role,@RequestParam String assignmentDate,@Valid@ModelAttribute("memberRole") MemberRole memberRole,BindingResult result){
		this.validate(memberRole, result, assignmentDate);
		if(result.hasErrors()){
			model.addAttribute("memberRole",memberRole);
			model.addAttribute("assignmentDate",assignmentDate);
			model.addAttribute("type","error");
			model.addAttribute("msg","update_failed");	
			return "member_mgmt/roles/assignmembers/edit_memberrole";
		}
		memberRoleService.update(memberRole);							
		return "redirect:/member_role/assignmembers/"+role+"/edit?type=success&msg=update_success";	
	}
	
	@RequestMapping(value="assignmembers/unassignMemberRoles",method=RequestMethod.POST)
	public String unassignMemberRolesMember(@RequestParam String memberRolesToUnassign,@RequestParam Long roleId){
		String temp[]=memberRolesToUnassign.split(",");
		for(String i:temp){
			if(!i.equals("")){
				MemberRole memberRole=memberRoleService.findById(Long.parseLong(i));
				memberRole.setStatus(customParameterService.findByName("MEMBERROLE_UNASSIGNED").getValue());
				memberRoleService.update(memberRole);
			}
		}
		return "redirect:/member_role/assignmembers/"+roleId+"/edit?type=success&msg=update_success";		
	}
	
	@RequestMapping(value="assignmembers/deleteMemberRoles",method=RequestMethod.POST)
	public String deleteMemberRolesMember(@RequestParam String memberRolesToDelete,@RequestParam String roleId){
		String temp[]=memberRolesToDelete.split(",");		
		for(String i:temp){
			if(!i.equals("")){
				memberRoleService.removeById(Long.parseLong(i));
			}
		}
		
		return "redirect:/member_role/assignmembers/"+roleId+"/edit?type=success&msg=delete_success";				
	}
	
	@RequestMapping(value = "/assignroles/assigned/{memberId}", method = RequestMethod.GET)
	public  @ResponseBody GridData getAssignedRoles(
			@PathVariable Long memberId,
			@RequestParam(value = "page", required = false) Integer page ,
			@RequestParam(value = "rows", required = false) Integer rows,
			@RequestParam(value = "sidx", required = false) String sidx,
			@RequestParam(value = "sord", required = false) String order,
			@RequestParam(value = "_search", required = false) Boolean search,
			@RequestParam(value = "searchField", required = false) String searchField,
			@RequestParam(value = "searchString", required = false) String searchString,
			@RequestParam(value = "searchOper", required = false) String searchOper,
			@RequestParam(value = "filters", required = false) String filtersData,
			@RequestParam(value = "baseFilters", required = false) String baseFilters,
			Model model, HttpServletRequest request, Locale locale) throws ClassNotFoundException {

		Filter filter = Filter.create(filtersData);
		if(search){
			return memberRoleService.getAssignedRoles(memberId, rows, page, sidx, order, filter.toSQl(), locale);
		}
		else{
			return memberRoleService.getAssignedRoles(memberId, rows, page, sidx, order, locale);
		}
	}

	@RequestMapping(value = "/assignmembers/unassigned/{memberId}", method = RequestMethod.GET)
	public  @ResponseBody GridData getUnAssignedRoles(
			@PathVariable Long memberId,
			@RequestParam(value = "page", required = false) Integer page ,
			@RequestParam(value = "rows", required = false) Integer rows,
			@RequestParam(value = "sidx", required = false) String sidx,
			@RequestParam(value = "sord", required = false) String order,
			@RequestParam(value = "_search", required = false) Boolean search,
			@RequestParam(value = "searchField", required = false) String searchField,
			@RequestParam(value = "searchString", required = false) String searchString,
			@RequestParam(value = "searchOper", required = false) String searchOper,
			@RequestParam(value = "filters", required = false) String filtersData,
			@RequestParam(value = "baseFilters", required = false) String baseFilters,
			Model model, HttpServletRequest request, Locale locale) throws ClassNotFoundException {

		Filter filter = Filter.create(filtersData);
		if(search){
			return memberRoleService.getUnAssignedMembers(memberId, rows, page, sidx, order, filter.toSQl(), locale);
		}
		else{
			return memberRoleService.getUnAssignedMembers(memberId, rows, page, sidx, order, locale);
		}
	}
	

	@RequestMapping(value = "/assignmembers/assigned/{roleId}", method = RequestMethod.GET)
	public  @ResponseBody GridData getAssignedMembers(
			@PathVariable Long roleId,
			@RequestParam(value = "page", required = false) Integer page ,
			@RequestParam(value = "rows", required = false) Integer rows,
			@RequestParam(value = "sidx", required = false) String sidx,
			@RequestParam(value = "sord", required = false) String order,
			@RequestParam(value = "_search", required = false) Boolean search,
			@RequestParam(value = "searchField", required = false) String searchField,
			@RequestParam(value = "searchString", required = false) String searchString,
			@RequestParam(value = "searchOper", required = false) String searchOper,
			@RequestParam(value = "filters", required = false) String filtersData,
			@RequestParam(value = "baseFilters", required = false) String baseFilters,
			Model model, HttpServletRequest request, Locale locale) throws ClassNotFoundException {

		Filter filter = Filter.create(filtersData);
		if(search){
			return memberRoleService.getAssignedMembers(roleId, rows, page, sidx, order, filter.toSQl(), locale);
		}
		else{
			return memberRoleService.getAssignedMembers(roleId, rows, page, sidx, order, locale);
		}
	}

	@InitBinder 
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()); 
		dateFormat.setLenient(true); 
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Assembly.class, new AssemblyEditor(assemblyService)); 
		binder.registerCustomEditor(AssemblyRole.class, new AssemblyRoleEditor(assemblyRoleService)); 
		binder.registerCustomEditor(MemberDetails.class, new MemberEditor(memberDetailsService)); 
	}

	private void populateModelMemberNew(Model model, String locale
	) {
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
		model.addAttribute("roles",assemblyRoleService.findAllSorted(locale));

	}

	private void populateModelMemberEdit(Model model, String locale,
			Long roleId) {
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
	}

	private void populateModelRoleNew(Model model,String locale,Long memberId){
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
		model.addAttribute("rolesmaster", assemblyRoleService.findAllSorted(locale.toString()));
	}
	private void populateModelRoleEdit(Model model, String locale, Long memberId) {
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
		model.addAttribute("roles",assemblyRoleService.findAllSorted(locale));		
	}


	private void validate(MemberRole memberRole, Errors errors, String assignmentDate){
		try {
			if(memberRole.getAssembly()!=null){
				if(memberRole.getAssembly().isCurrentAssembly()){
					SimpleDateFormat format=new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue());
					if(memberRole.getAssembly().getAssemblyEndDate()!=null){
						if(!memberRole.getAssembly().getAssemblyEndDate().isEmpty()){
							if(format.parse(assignmentDate).after(format.parse(memberRole.getAssembly().getAssemblyEndDate()))){
								errors.rejectValue("assembly","RoleAssignment_AfterAssemblyEnded");
							}
						}
					}
					if(memberRole.getAssembly().getAssemblyDissolvedOn()!=null){
						if(!memberRole.getAssembly().getAssemblyDissolvedOn().isEmpty()){
							if(format.parse(assignmentDate).after(format.parse(memberRole.getAssembly().getAssemblyDissolvedOn()))){
								errors.rejectValue("assembly","RoleAssignment_AfterAssemblyDissolved");
							}
						}
					}	
				}
			}						

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
