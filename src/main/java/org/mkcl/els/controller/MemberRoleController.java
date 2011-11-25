package org.mkcl.els.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.hibernate.mapping.Array;
import org.mkcl.els.common.editors.AssemblyEditor;
import org.mkcl.els.common.editors.AssemblyRoleEditor;
import org.mkcl.els.common.editors.MemberEditor;
import org.mkcl.els.common.vo.AssemblyRolesVo;
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
		MemberDetails memberDetails=memberDetailsService.findById(memberId);
		memberRole.setMember(memberDetails);
		Assembly assembly=assemblyService.findCurrentAssembly(locale.toString());
		memberRole.setAssembly(assembly);
		memberRole.setFromDate(assembly.getAssemblyStartDate());
		if(assembly.getAssemblyDissolvedOn()!=null){
			memberRole.setToDate(!assembly.getAssemblyDissolvedOn().isEmpty()?assembly.getAssemblyDissolvedOn():assembly.getAssemblyEndDate());
		}else{
			memberRole.setToDate(assembly.getAssemblyEndDate());
		}
		memberRole.setLocale(locale.toString());
		model.addAttribute("memberRole",memberRole);
		model.addAttribute("roles",memberRoleService.getUnassignedRoles(memberDetails,assembly,locale.toString()));
		populateModel(model,locale.toString());
		model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()).format(new Date()));
		return "member_mgmt/roles/assignroles/new";
	}
	

	@RequestMapping(value="assignroles/{member_id}/edit",method=RequestMethod.GET)
	public String _editroles(Model model,Locale locale,@PathVariable("member_id")Long memberId,HttpServletRequest request){
		List<MemberRole> memberRoles=memberRoleService.findByMemberId(memberId);		
		model.addAttribute("memberRoles",memberRoles);
		populateModel(model,locale.toString());
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
		List<String> allStatus=new ArrayList<String>();
		allStatus.add(customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue());
		allStatus.add(customParameterService.findByName("MEMBERROLE_UNASSIGNED").getValue());
		model.addAttribute("allStatus",allStatus);
		model.addAttribute("memberRole",memberRole);
		model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()).format(new Date()));
		return "member_mgmt/roles/assignroles/edit_memberrole";
	}	
	
	@RequestMapping(value="assignroles/createMemberRoles",method = RequestMethod.POST)
	public String createRoles(Locale locale,@RequestParam String assignmentDate,@Valid 
			@ModelAttribute("memberRole") MemberRole memberRole, 
			BindingResult result, Model model,HttpServletRequest request,@RequestParam Long member,@RequestParam(required=false) String[] roles,@RequestParam(required=false) String[] roles_check){
		StringBuffer selectedRoles=new StringBuffer();
		/*
		 * The member roles that are already present for a given member,assembly,from date,to date and status assigned.These entries will be updated to assigned.
		 */
		Map<String,MemberRole> unassignedMemberRoles=new HashMap<String, MemberRole>();		
		/*
		 * Check:Selected user is not a member and selected roles is null then role not null is displayed.
		 */
		if(!memberRoleService.isMember(memberRole.getMember(),memberRole.getAssembly(),memberRole.getFromDate(),memberRole.getToDate())){
			/*
			 * The selected user is not a member and is tried to assign a role for a given assembly and period.
			 */
			int count=0;
			if(roles_check!=null){
				for(String i:roles_check){
					if(i.equals(customParameterService.findByName("DEFAULT_MEMBERROLE").getValue())){
						count++;
					}
				}
				if(count==0){
					result.rejectValue("role","NotMember");
			}			
				for(String i:roles){				
					memberRole.setRole(assemblyRoleService.findById(Long.parseLong(i)));
					MemberRole duplicateMemberRole = memberRoleService.checkForDuplicateMemberRole(memberRole);
					if(duplicateMemberRole.getId()!=null){
						this.validate(memberRole, result,assignmentDate,duplicateMemberRole);									
					}else{
						this.validate(memberRole, result, assignmentDate);
					}
					selectedRoles.append(i+",");						
				}
			}else{
				result.rejectValue("role","NotNull");
			}			
		}
		/*
		 * Check:Selected user is a member and selected roles is null then role not null is displayed.		 
		 */		
		else{
			if(roles!=null){
				for(String i:roles){				
					memberRole.setRole(assemblyRoleService.findById(Long.parseLong(i)));
					MemberRole duplicateMemberRole = memberRoleService.checkForDuplicateMemberRole(memberRole);
					if(duplicateMemberRole.getId()!=null){						
						this.validate(memberRole, result,assignmentDate,duplicateMemberRole);									
					}else{
						this.validate(memberRole, result, assignmentDate);
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
			populateModel(model,locale.toString());
			model.addAttribute("roles",memberRoleService.getUnassignedRoles(memberRole.getMember(),memberRole.getAssembly(),locale.toString()));
			model.addAttribute("assignmentDate",assignmentDate);
			return "member_mgmt/roles/assignroles/new";
		}
		/*
		 * A member role is updated only if an entry already exists for the given assembly,member,from date,to date
		 * but has been previously unassigned.
		 * A new member role is created if there is no entry for the given assembly,member,from date and to date.
		 */
		SimpleDateFormat format=new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue());
		for(String i:roles){	
				MemberRole memberRoleToInsert=MemberRole.newInstance(memberRole);	
				
				try {
					if(format.parse(memberRole.getToDate()).before(format.parse(assignmentDate))){
						memberRoleToInsert.setStatus(customParameterService.findByName("MEMBERROLE_UNASSIGNED").getValue());
					}else{
						memberRoleToInsert.setStatus(customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue());
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				memberRoleService.create(memberRoleToInsert);					
		}
		return "redirect:/member_role/assignroles/"+memberRole.getMember().getId()+"/edit?type=success&msg=create_success";

	}
	@RequestMapping(value="assignroles/unassignMemberRoles",method=RequestMethod.POST)
	public String unassignMemberRoles(@RequestParam String memberRolesToUnassign,@RequestParam Long memberId){
		String temp[]=memberRolesToUnassign.split(",");
		String assignmentDate=new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()).format(new Date());
		for(String i:temp){
			if(!i.equals("")){
				MemberRole memberRole=memberRoleService.findById(Long.parseLong(i));
				memberRole.setToDate(assignmentDate);
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
			return memberRoleService.getAssignedUnassignedRoles(memberId, rows, page, sidx, order, filter.toSQl(), locale);
		}
		else{
			return memberRoleService.getAssignedUnassignedRoles(memberId, rows, page, sidx, order, locale);
		}
	}	
	@RequestMapping(value="unassignedroles/{member}/{assembly}")
	public @ResponseBody AssemblyRolesVo getUnassignedRoles(@PathVariable("member")Long member,@PathVariable("assembly")Long assembly,Locale locale){
		AssemblyRolesVo assemblyRolesVo=new AssemblyRolesVo();
		assemblyRolesVo.setRoles(memberRoleService.getUnassignedRoles(memberDetailsService.findById(member),assemblyService.findById(assembly), locale.toString()));
		Assembly tempAssembly=assemblyService.findById(assembly);
		assemblyRolesVo.setFromDate(tempAssembly.getAssemblyStartDate());
		if(tempAssembly.getAssemblyDissolvedOn()!=null){
			if(!tempAssembly.getAssemblyDissolvedOn().isEmpty()){
				assemblyRolesVo.setToDate(tempAssembly.getAssemblyDissolvedOn());
			}else{
				assemblyRolesVo.setToDate(tempAssembly.getAssemblyEndDate());
			}
		}else{
			assemblyRolesVo.setToDate(tempAssembly.getAssemblyEndDate());
		}
		return assemblyRolesVo;
	}
	private void populateModel(Model model,String locale){
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
		//model.addAttribute("roles", assemblyRoleService.findAllSorted(locale.toString()));
	}	
	/*
	 * ###########################################################################################
	 * ASSIGN MEMBERS MODULE
	 * ###########################################################################################
	 */

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
		if(assembly.getAssemblyDissolvedOn()!=null){
			memberRole.setToDate(!assembly.getAssemblyDissolvedOn().isEmpty()?assembly.getAssemblyDissolvedOn():assembly.getAssemblyEndDate());
		}else{
			memberRole.setToDate(assembly.getAssemblyEndDate());
		}
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
		List<String> allStatus=new ArrayList<String>();
		allStatus.add(customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue());
		allStatus.add(customParameterService.findByName("MEMBERROLE_UNASSIGNED").getValue());
		model.addAttribute("allStatus",allStatus);
		model.addAttribute("memberRole",memberRole);
		model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()).format(new Date()));
		return "member_mgmt/roles/assignmembers/edit_memberrole";
	}
	@RequestMapping(value="assignmembers/createMemberRoles",method=RequestMethod.POST)
	public String createMemberRoles(HttpServletRequest request,@RequestParam String membersToAssign,@Valid @ModelAttribute("memberRole")MemberRole memberRole,BindingResult result,Model model,@RequestParam String assignmentDate,Locale locale){
		this.validate(memberRole, result,assignmentDate);
		/*
		 * Check:Atleast one member is selected
		 */
		if(membersToAssign.isEmpty()){
			result.rejectValue("member","NotNull");
		}				
		if(result.hasErrors()){
			model.addAttribute("memberRole",memberRole);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			model.addAttribute("membersToAssign",membersToAssign);
			model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()).format(new Date()));
			populateModelMemberNew(model,locale.toString());			
			return "member_mgmt/roles/assignmembers/new";
		}	
		String temp[]=membersToAssign.split(",");
		for(String i:temp){
				memberRole.setMember(memberDetailsService.findById(Long.parseLong(i)));
				MemberRole memberRoleToUpdate=memberRoleService.checkForDuplicateMemberRole(memberRole);
				String assigned=customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue();
				if(memberRoleToUpdate!=null){
					memberRoleToUpdate.setStatus(assigned);
					memberRoleService.update(memberRoleToUpdate);
				}else{
					MemberRole memberRoleToInsert=MemberRole.newInstance(memberRole);
					memberRoleToInsert.setStatus(assigned);
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
			return memberRoleService.getAssignedUnassignedMembers(roleId, rows, page, sidx, order, filter.toSQl(), locale);
		}
		else{
			return memberRoleService.getAssignedUnassignedMembers(roleId, rows, page, sidx, order, locale);
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
	
	private void populateModelMemberNew(Model model, String locale) {
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
		model.addAttribute("roles",assemblyRoleService.findAllSorted(locale));
	}
	private void populateModelMemberEdit(Model model, String locale,Long roleId) {
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
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
	private void validate(MemberRole memberRole, Errors errors, String assignmentDate){
		try {
			if(memberRole.getAssembly()!=null){
				SimpleDateFormat format=new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue());
				Date assemblyStartDate=null;
				Date assemblyEndDate=null;
				Date assemblyDissolveDate=null;
				Date roleIsAssignedOn=null;
				Date fromDate=null;
				Date toDate=null;				
				String strAssemblyStartDate=memberRole.getAssembly().getAssemblyStartDate();
				String strAssemblyEndDate=memberRole.getAssembly().getAssemblyEndDate();
				String strAssemblyDissolveDate=memberRole.getAssembly().getAssemblyDissolvedOn();
				String strFromDate=memberRole.getFromDate();
				String strToDate=memberRole.getToDate();				
				String[] errorArgs=new String[6];				
				if(strAssemblyStartDate!=null){
					if(!strAssemblyStartDate.isEmpty()){
						errorArgs[0]=strAssemblyStartDate;
						assemblyStartDate=format.parse(strAssemblyStartDate);
					}
				}
				if(strAssemblyEndDate!=null){
					if(!strAssemblyEndDate.isEmpty()){
						errorArgs[1]=strAssemblyEndDate;
						assemblyEndDate=format.parse(strAssemblyEndDate);
					}
				}
				if(strAssemblyDissolveDate!=null){
					if(!strAssemblyDissolveDate.isEmpty()){
						errorArgs[2]=strAssemblyDissolveDate;
						assemblyDissolveDate=format.parse(strAssemblyDissolveDate);
					}					
				}
				if(assignmentDate!=null){
					if(!assignmentDate.isEmpty()){
						errorArgs[3]=assignmentDate;
						roleIsAssignedOn=format.parse(assignmentDate);
					}
				}
				if(strFromDate!=null){
					if(!strFromDate.isEmpty()){
						errorArgs[4]=strFromDate;
						fromDate=format.parse(strFromDate);
					}
				}
				if(strToDate!=null){
					if(!strToDate.isEmpty()){
						errorArgs[5]=strToDate;
						toDate=format.parse(strToDate);
					}
				}
				
				/*
				 * Check:For current assembly member roles cannot be assigned after assembly has ended or 
				 * dissolved.
				 */
				if(memberRole.getAssembly().isCurrentAssembly()){
					if(assemblyEndDate!=null){						
							if(roleIsAssignedOn.after(assemblyEndDate)){
								errors.rejectValue("assembly","RoleAssignment_AfterAssemblyEnded",errorArgs,"Role cannot be assigned after assembly end date:{1}");
							}						
					}
					if(assemblyDissolveDate!=null){
						if(roleIsAssignedOn.after(assemblyDissolveDate)){
							errors.rejectValue("assembly","RoleAssignment_AfterAssemblyDissolved",errorArgs,"Role cannot be assigned after assembly dissolution date:{2}");
						}							
					}					
				}
					/*
					 * Check:from date and to date must be between assembly start date and end date or start date and dissolved date
					 * if assembly dissolved date is not null.
					 */	
					if(fromDate!=null&&assemblyStartDate!=null&assemblyEndDate!=null){	
						if(assemblyDissolveDate!=null){
							if(fromDate.before(assemblyStartDate)||fromDate.after(assemblyDissolveDate)){
								errors.rejectValue("fromDate","FromDate_NotBetween_AssemblyStartDissolveDate",errorArgs,"From Date ({4}) must be between assembly start date ({0}) and dissolution date ({2})");
							}
						}else if(fromDate.before(assemblyStartDate)||fromDate.after(assemblyEndDate)){
							errors.rejectValue("fromDate","FromDate_NotBetween_AssemblyStartEndDate",errorArgs,"From Date ({4}) must be between assembly start date ({0}) and end date ({1})");
						}
					}
					if(toDate!=null&&assemblyStartDate!=null&assemblyEndDate!=null){
						if(assemblyDissolveDate!=null){
							if(toDate.before(assemblyStartDate)||toDate.after(assemblyDissolveDate)){
								errors.rejectValue("toDate","ToDate_NotBetween_AssemblyStartDissolveDate",errorArgs,"To Date ({5}) must be between assembly start date ({0}) and dissolution date ({2})");
							}
						}else if(toDate.before(assemblyStartDate)||toDate.after(assemblyEndDate)){
							errors.rejectValue("toDate","ToDate_NotBetween_AssemblyStartEndDate",errorArgs,"To Date ({5}) must be between assembly start date ({0}) and end date ({1})");
						}
					}	
					
					if(toDate!=null&&fromDate!=null){
						if(toDate.before(fromDate)){
							errors.rejectValue("toDate","ToDate_LTFromDate",errorArgs,"To Date must be greater than from date({4})");
						}
						if(fromDate.after(toDate)){
							errors.rejectValue("fromDate","FromDate_GTToDate",errorArgs,"From Date must be less than to date({5})");
						}
					}
				}						
							

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void validate(MemberRole memberRole, Errors errors,String assignmentDate,MemberRole duplicateMemberRole){
		try {
			if(memberRole.getAssembly()!=null){
				SimpleDateFormat format=new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue());
				Date assemblyStartDate=null;
				Date assemblyEndDate=null;
				Date assemblyDissolveDate=null;
				Date roleIsAssignedOn=null;
				Date fromDate=null;
				Date toDate=null;	
				Date duplicateFromDate=null;
				Date duplicateToDate=null;
				String strAssemblyStartDate=memberRole.getAssembly().getAssemblyStartDate();
				String strAssemblyEndDate=memberRole.getAssembly().getAssemblyEndDate();
				String strAssemblyDissolveDate=memberRole.getAssembly().getAssemblyDissolvedOn();
				String strFromDate=memberRole.getFromDate();
				String strToDate=memberRole.getToDate();
				String strDuplicateFromDate=duplicateMemberRole.getFromDate();
				String strDuplicateToDate=duplicateMemberRole.getToDate();
				String[] errorArgs=new String[11];				
				if(strAssemblyStartDate!=null){
					if(!strAssemblyStartDate.isEmpty()){
						errorArgs[0]=strAssemblyStartDate;
						assemblyStartDate=format.parse(strAssemblyStartDate);
					}
				}
				if(strAssemblyEndDate!=null){
					if(!strAssemblyEndDate.isEmpty()){
						errorArgs[1]=strAssemblyEndDate;
						assemblyEndDate=format.parse(strAssemblyEndDate);
					}
				}
				if(strAssemblyDissolveDate!=null){
					if(!strAssemblyDissolveDate.isEmpty()){
						errorArgs[2]=strAssemblyDissolveDate;
						assemblyDissolveDate=format.parse(strAssemblyDissolveDate);
					}					
				}
				if(assignmentDate!=null){
					if(!assignmentDate.isEmpty()){
						errorArgs[3]=assignmentDate;
						roleIsAssignedOn=format.parse(assignmentDate);
					}
				}
				if(strFromDate!=null){
					if(!strFromDate.isEmpty()){
						errorArgs[4]=strFromDate;
						fromDate=format.parse(strFromDate);
					}
				}
				if(strToDate!=null){
					if(!strToDate.isEmpty()){
						errorArgs[5]=strToDate;
						toDate=format.parse(strToDate);
					}
				}
				if(strDuplicateFromDate!=null){
					if(!strDuplicateFromDate.isEmpty()){
						errorArgs[6]=strDuplicateFromDate;
						duplicateFromDate=format.parse(strDuplicateFromDate);
					}
				}
				if(strDuplicateToDate!=null){
					if(!strDuplicateToDate.isEmpty()){
						errorArgs[7]=strDuplicateToDate;
						duplicateToDate=format.parse(strDuplicateToDate);
					}
				}
				errorArgs[8]=memberRole.getMember().getFullName();
				errorArgs[9]=memberRole.getAssembly().getAssembly();
				errorArgs[10]=memberRole.getRole().getName();	
				
				/*
				 * Check:For current assembly member roles cannot be assigned after assembly has ended or 
				 * dissolved.
				 */
				if(memberRole.getAssembly().isCurrentAssembly()){
					if(assemblyEndDate!=null){						
							if(roleIsAssignedOn.after(assemblyEndDate)){
								errors.rejectValue("assembly","RoleAssignment_AfterAssemblyEnded",errorArgs,"Role cannot be assigned after assembly end date:{1}");
							}						
					}
					if(assemblyDissolveDate!=null){
						if(roleIsAssignedOn.after(assemblyDissolveDate)){
							errors.rejectValue("assembly","RoleAssignment_AfterAssemblyDissolved",errorArgs,"Role cannot be assigned after assembly dissolution date:{2}");
						}							
					}					
				}
					/*
					 * Check:from date and to date must be between assembly start date and end date or start date and dissolved date
					 * if assembly dissolved date is not null.
					 */	
					if(fromDate!=null&&assemblyStartDate!=null&assemblyEndDate!=null){	
						if(assemblyDissolveDate!=null){
							if(fromDate.before(assemblyStartDate)||fromDate.after(assemblyDissolveDate)){
								errors.rejectValue("fromDate","FromDate_NotBetween_AssemblyStartDissolveDate",errorArgs,"From Date ({4}) must be between assembly start date ({0}) and dissolution date ({2})");
							}
						}else if(fromDate.before(assemblyStartDate)||fromDate.after(assemblyEndDate)){
							errors.rejectValue("fromDate","FromDate_NotBetween_AssemblyStartEndDate",errorArgs,"From Date ({4}) must be between assembly start date ({0}) and end date ({1})");
						}
					}
					if(toDate!=null&&assemblyStartDate!=null&assemblyEndDate!=null){
						if(assemblyDissolveDate!=null){
							if(toDate.before(assemblyStartDate)||toDate.after(assemblyDissolveDate)){
								errors.rejectValue("toDate","ToDate_NotBetween_AssemblyStartDissolveDate",errorArgs,"To Date ({5}) must be between assembly start date ({0}) and dissolution date ({2})");
							}
						}else if(toDate.before(assemblyStartDate)||toDate.after(assemblyEndDate)){
							errors.rejectValue("toDate","ToDate_NotBetween_AssemblyStartEndDate",errorArgs,"To Date ({5}) must be between assembly start date ({0}) and end date ({1})");
						}
					}	
					
					if(toDate!=null&&fromDate!=null){
						if(toDate.before(fromDate)){
							errors.rejectValue("toDate","ToDate_LTFromDate",errorArgs,"To Date must be greater than from date({4})");
						}
						if(fromDate.after(toDate)){
							errors.rejectValue("fromDate","FromDate_GTToDate",errorArgs,"From Date must be less than to date({5})");
						}
						/*
						 * From date and To date is same as that of duplicate entry.
						 * From date and To date is between the from date and to date of duplicate entry.
						 * From date is between the from date and to date of duplicate entry and to date > to date of
						 * duplicate entry.
						 * To date is between the from and to date of duplicate entry and from date < from date of 
						 * duplicate entry. 
						 */
						if(duplicateFromDate!=null&duplicateToDate!=null){							

							if(fromDate.equals(duplicateFromDate)&&toDate.equals(duplicateToDate)){
								errors.rejectValue("assembly","DuplicateMemberRoleEntry",errorArgs,"Entry already exists for member({8}) having role ({10}),assembly({9}) between periods ({6} and {7})");
							}

							if((fromDate.after(duplicateFromDate)&&toDate.before(duplicateToDate))||(fromDate.equals(duplicateFromDate)&&toDate.before(duplicateToDate))||(fromDate.after(duplicateFromDate)&&toDate.equals(duplicateToDate))){
								errors.rejectValue("assembly","DuplicateMemberRoleEntryFromToBetween",errorArgs,"Entry already exists for member({8}) having role ({10}),assembly({9}) between periods ({6} and {7}).Kindly change from and to dates");
							}

							if(((fromDate.after(duplicateFromDate)&&fromDate.before(duplicateToDate))||(fromDate.equals(duplicateFromDate))||(fromDate.equals(duplicateToDate)))&&toDate.after(duplicateToDate)){
								errors.rejectValue("assembly","DuplicateMemberRoleEntryFromBetween",errorArgs,"Entry already exists for member({8}) having role ({10}),assembly({9}) between periods ({6} and {7}).Kindly change from date to something greater than {7}");
							}

							if(((toDate.after(duplicateFromDate)&&toDate.before(duplicateToDate))||(toDate.equals(duplicateFromDate))||(toDate.equals(duplicateToDate)))&&fromDate.before(duplicateFromDate)){
								errors.rejectValue("assembly","DuplicateMemberRoleEntryToBetween",errorArgs,"Entry already exists for member({8}) having role ({10}),assembly({9}) between periods ({6} and {7}).Kindly change to date to something less than {6}");
							}	
							if(toDate.after(duplicateToDate)&&fromDate.before(duplicateFromDate)){
								errors.rejectValue("assembly","DuplicateMemberRoleEntryToFromNotBetween",errorArgs,"Entry already exists for member({8}) having role ({10}),assembly({9}) between periods ({6} and {7}).Kindly change both from and to date to less than {6} or greater than {7}");
							}	
						}
					}
				}						
							

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}	
}
