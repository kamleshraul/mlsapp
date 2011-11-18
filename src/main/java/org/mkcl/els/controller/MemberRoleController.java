package org.mkcl.els.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.AssemblyEditor;
import org.mkcl.els.common.editors.AssemblyRoleEditor;
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
	
	@RequestMapping(value="assignroles/{member_id}/new",method=RequestMethod.GET)
	public String _assignroles(Model model,Locale locale,@PathVariable("member_id")Long memberId){
		MemberRole memberRole=new MemberRole();
		memberRole.setMember(memberDetailsService.findById(memberId));
		memberRole.setLocale(locale.toString());
		model.addAttribute("memberRole",memberRole);
		populateModelRoleNew(model,locale.toString(),memberId);
		model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("DB_DATEFORMAT").getValue()).format(new Date()));
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


	@RequestMapping(value="assignroles/createMemberRoles",method = RequestMethod.POST)
	public String createRoles(Locale locale,@RequestParam String assignmentDate,@Valid 
			@ModelAttribute("memberRole") MemberRole memberRole, 
			BindingResult result, Model model,HttpServletRequest request,@RequestParam Long memberId,@RequestParam(required=false) String[] roles){
		this.validate(memberRole, result,assignmentDate);
		memberRole.setMember(memberDetailsService.findById(memberId));
		if(result.hasErrors()){
			model.addAttribute("memberRole",memberRole);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			populateModelRoleNew(model,locale.toString(),memberId);
			model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("DB_DATEFORMAT").getValue()).format(new Date()));
			return "member_mgmt/roles/assignroles/new";
		}	
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
	@RequestMapping(value="assignroles/updateMemberRoles",method = RequestMethod.POST)
	public String updateRoles(Model model,HttpServletRequest request,@RequestParam Long memberId,@RequestParam Integer noOfRecords){
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
	public String indexRoles(Model model){
		Grid grid = gridService.findByName("MMS_ASSIGNMEMBER");
		model.addAttribute("gridId", grid.getId());
		return "member_mgmt/roles/assignmembers/list";
	}
	@RequestMapping(value="assignmembers/{role_id}/new",method=RequestMethod.GET)
	public String _assignmembers(Model model,Locale locale,@PathVariable("role_id")Long roleId){
		MemberRole memberRole=new MemberRole();
		memberRole.setRole(assemblyRoleService.findById(roleId));
		memberRole.setLocale(locale.toString());
		model.addAttribute("memberRole",memberRole);
		model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("DB_DATEFORMAT").getValue()).format(new Date()));
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
	
	@RequestMapping(value="assignmembers/createMemberRoles",method=RequestMethod.POST)
	public String createMemberRoles(HttpServletRequest request,@RequestParam String membersToAssign,@Valid @ModelAttribute("memberRole")MemberRole memberRole,BindingResult result,Model model,@RequestParam String assignmentDate,Locale locale){
		this.validate(memberRole, result,assignmentDate);		
		if(result.hasErrors()){
			model.addAttribute("memberRole",memberRole);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			model.addAttribute("assignmentDate",new SimpleDateFormat(customParameterService.findByName("DB_DATEFORMAT").getValue()).format(new Date()));
			populateModelMemberNew(model,locale.toString());			
			return "member_mgmt/roles/assignmembers/new";
		}		
		String temp[]=membersToAssign.split(",");
		for(String i:temp){
			if(!i.equals("")){
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
				memberRoleToInsert.setAssignedBy(this.getCurrentUser().getFullName());
				memberRoleToInsert.setAssignedOn(new Date());
				memberRoleService.update(memberRoleToInsert);
			}
		}
		return "redirect:/member_role/assignmembers/"+memberRole.getRole().getId()+"/edit?type=success&msg=update_success";		
	}
	
	@RequestMapping(value="assignmembers/updateMemberRoles",method=RequestMethod.POST)
	public String updateMemberRoles(@RequestParam String memberRolesToUnassign,@RequestParam Long roleId){
		String temp[]=memberRolesToUnassign.split(",");
		for(String i:temp){
			if(!i.equals("")){
				MemberRole memberRole=memberRoleService.findById(Long.parseLong(i));
				memberRole.setStatus(customParameterService.findByName("MEMBERROLE_UNASSIGNED").getValue());
				memberRole.setUnassignedBy(this.getCurrentUser().getFullName());
				memberRole.setUnassignedOn(new Date());
				memberRoleService.update(memberRole);
			}
		}
		return "redirect:/member_role/assignmembers/"+roleId+"/edit?type=success&msg=update_success";		
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
	
	@RequestMapping(value = "/assignmembers/unassigned/{roleId}", method = RequestMethod.GET)
	public  @ResponseBody GridData getUnAssignedmembers(
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
			return memberRoleService.getUnAssignedMembers(roleId, rows, page, sidx, order, filter.toSQl(), locale);
		}
		else{
			return memberRoleService.getUnAssignedMembers(roleId, rows, page, sidx, order, locale);
		}
	}
	
	
	@InitBinder 
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()); 
		dateFormat.setLenient(true); 
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Assembly.class, new AssemblyEditor(assemblyService)); 
		binder.registerCustomEditor(AssemblyRole.class, new AssemblyRoleEditor(assemblyRoleService)); 

	}
	
	private void populateModelMemberNew(Model model, String locale
			) {
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
		//model.addAttribute("members",memberRoleService.findUnassignedMembers(roleId));
		model.addAttribute("roles",assemblyRoleService.findAllSorted(locale));

	}
	
	private void populateModelMemberEdit(Model model, String locale,
			Long roleId) {
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
	}
	
	private void populateModelRoleNew(Model model,String locale,Long memberId){
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
		model.addAttribute("roles",assemblyRoleService.findUnassignedRoles(locale,memberId));
	}
	private void populateModelRoleEdit(Model model, String locale, Long memberId) {
		model.addAttribute("assemblies",assemblyService.findAllSorted(locale));		
		model.addAttribute("roles",assemblyRoleService.findAllSorted(locale));		
	}
	
	private void validate(MemberRole memberRole, Errors errors, String assignmentDate){
		try {
			if(memberRole.getAssembly()!=null){
				if(memberRole.getAssembly().getAssemblyEndDate()!=null){
					if(new SimpleDateFormat(customParameterService.findByName("DB_DATEFORMAT").getValue()).parse(assignmentDate).after(memberRole.getAssembly().getAssemblyEndDate())){
						errors.rejectValue("assembly","RoleAssignment_AfterAssemblyEnded");
					}
				}
			if(memberRole.getAssembly().getAssemblyDissolvedOn()!=null){
				if(new SimpleDateFormat(customParameterService.findByName("DB_DATEFORMAT").getValue()).parse(assignmentDate).after(memberRole.getAssembly().getAssemblyDissolvedOn())){
					errors.rejectValue("assembly","RoleAssignment_AfterAssemblyDissolved");
				}
			}			
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
