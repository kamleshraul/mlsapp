package org.mkcl.els.controller.mis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Designation;
import org.mkcl.els.domain.MemberDepartment;
import org.mkcl.els.domain.MemberMinister;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("member/minister")
public class MemberMinisterController extends GenericController<MemberMinister> {

	@Override
	protected void populateNew(final ModelMap model, final MemberMinister domain,
            final String locale, final HttpServletRequest request) {
		domain.setLocale(locale);
		populate(model, domain, locale, request);
		model.addAttribute("memberDepartmentCount", 0);
	}
	
	@Override
	protected void populateEdit(final ModelMap model, final MemberMinister domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		populate(model, domain, locale, request);
		List<MemberDepartment>memberDepartments = domain.getMemberDepartments();
		model.addAttribute("memberDepartments", memberDepartments);
		model.addAttribute("memberDepartmentCount", memberDepartments.size());
	}
	
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model,
            final MemberMinister domain, final HttpServletRequest request) {
		request.getSession().setAttribute("member", domain.getMember().getId());
	}
	
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model,
            final MemberMinister domain, final HttpServletRequest request) {
		request.getSession().setAttribute("member", domain.getMember().getId());
	}
	
	@Override
	protected void preValidateCreate(final MemberMinister domain,
            final BindingResult result, final HttpServletRequest request) {
		populateMemberDepartments(domain, result, request);
	}
	
	@Override
	protected void preValidateUpdate(final MemberMinister domain,
            final BindingResult result, final HttpServletRequest request) {
		populateMemberDepartments(domain, result, request);
	}
	
	@Override
    protected void customValidateCreate(final MemberMinister domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }
    @Override
    protected void customValidateUpdate(final MemberMinister domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }
    
	@RequestMapping(value = "/department/{id}/delete", method = RequestMethod.DELETE)
    public String deleteRival(final @PathVariable("id") Long id,
            final ModelMap model, final HttpServletRequest request) {
		MemberDepartment memberDepartment = MemberDepartment.findById(MemberDepartment.class, id);
		memberDepartment.remove();
		return "info";
    }
	
	private void populate(final ModelMap model, final MemberMinister domain,
            final String locale, final HttpServletRequest request){
		Long member = (long) 0;
		if(request.getParameter("member") == null){
			member = (Long) request.getSession().getAttribute("member");
			request.getSession().removeAttribute("member");
		}else{
			member = Long.parseLong(request.getParameter("member"));
		}
		List<Designation> designations = 
			Designation.findAll(Designation.class, "name", ApplicationConstants.ASC, locale);
		List<Department> departments = 
			Department.findAll(Department.class, "name", ApplicationConstants.ASC, locale);
		model.addAttribute("member", member);
		model.addAttribute("designations", designations);
		model.addAttribute("departments", departments);
	}
	
	private void populateMemberDepartments(final MemberMinister domain,
            final BindingResult result, final HttpServletRequest request) {
		List<MemberDepartment> memberDepartments = new ArrayList<MemberDepartment>();
		Integer memberDepartmentCount = 
			Integer.parseInt(request.getParameter("memberDepartmentCount"));
		for(int i = 1; i <= memberDepartmentCount; i++){
			String strDepartment = request.getParameter("memberDepartmentDepartment" + i);
			if(strDepartment != null){
				MemberDepartment memberDepartment = new MemberDepartment();
				
				if(! strDepartment.isEmpty()){
					Department department = Department.findById(Department.class, Long.parseLong(strDepartment));
					memberDepartment.setDepartment(department);
				}
				
				String id = request.getParameter("memberDepartmentId" + i);
				if(id != null){
					if(! id.isEmpty()){
						memberDepartment.setId(Long.parseLong(id));
					}
				}
				
				String version = request.getParameter("memberDepartmentVersion" + i);
				if(version != null){
					if(! version.isEmpty()){
						memberDepartment.setVersion(Long.parseLong(version));
					}
				}
				
				String locale = request.getParameter("memberDepartmentLocale" + i);
				if(locale != null){
					if(! locale.isEmpty()){
						memberDepartment.setLocale(locale);
					}
				}
				
				CustomParameter serverDateFormat = 
					CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
				
				String strFromDate = request.getParameter("memberDepartmentFromDate" + i);
				if(strFromDate != null){
					if(! strFromDate.isEmpty()){
						Date fromDate = null;
						try {
							fromDate = new SimpleDateFormat(serverDateFormat.getValue()).parse(strFromDate);
							memberDepartment.setFromDate(fromDate);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				
				String strToDate = request.getParameter("memberDepartmentToDate" + i);
				if(strToDate != null){
					if(! strToDate.isEmpty()){
						Date toDate = null;
						try {
							toDate = new SimpleDateFormat(serverDateFormat.getValue()).parse(strToDate);
							memberDepartment.setToDate(toDate);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				
				String strIndCharge = request.getParameter("memberDepartmentIsIndependentCharge" + i);
				if(strIndCharge != null){
					if(! strIndCharge.isEmpty()){
						memberDepartment.setIsIndependentCharge(Boolean.parseBoolean(strIndCharge));
					}
				}
				
				memberDepartments.add(memberDepartment);
			}
		}
		domain.setMemberDepartments(memberDepartments);
	}
}
