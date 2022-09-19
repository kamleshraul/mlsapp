/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.mis.MemberMinisterController.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.controller.mis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.DateUtil;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Designation;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberDepartment;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * The Class MemberMinisterController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("member/minister")
public class MemberMinisterController extends GenericController<MemberMinister> {
	
	@Override
	protected void populateNew(final ModelMap model, MemberMinister domain,
			final String locale, final HttpServletRequest request) {
		domain.setLocale(locale);
		populate(model, domain, locale, request);
		model.addAttribute("memberDepartmentCount", 0);
		model.addAttribute("houseType", request.getParameter("houseType"));
	}
	
	@Override
	protected void preValidateCreate(MemberMinister domain,
			final BindingResult result, final HttpServletRequest request) {
		this.populateMemberDepartments(domain, result, request);
	}
	
	@Override
    protected void customValidateCreate(final MemberMinister domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }
	
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model,
            final MemberMinister domain, final HttpServletRequest request) {
		try {
			updateMinisterRole(domain);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
		request.getSession().setAttribute("member", domain.getMember().getId());
		request.getSession().setAttribute("houseType", request.getParameter("houseType"));
	}
	
	@Override
	protected void populateEdit(final ModelMap model, MemberMinister domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		populate(model, domain, locale, request);
		populateEditDepartments(model, domain, locale, request);
		List<MemberDepartment>memberDepartments = domain.getMemberDepartments();
		model.addAttribute("memberDepartments", memberDepartments);
		model.addAttribute("memberDepartmentCount", memberDepartments.size());
		String houseType = request.getParameter("houseType");
		if(houseType == null){
		    houseType = (String) request.getSession().getAttribute("houseType");
		    request.getSession().removeAttribute("houseType");
		}
		model.addAttribute("houseType",houseType);
	}
	
	@Override
	protected void preValidateUpdate(final MemberMinister domain,
            final BindingResult result, final HttpServletRequest request) {
		this.populateMemberDepartments(domain, result, request);
	}
	
	@Override
    protected void customValidateUpdate(final MemberMinister domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }
	
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model,
            final MemberMinister domain, final HttpServletRequest request) {
		try {
			updateMinisterRole(domain);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
		request.getSession().setAttribute("member", domain.getMember().getId());
		request.getSession().setAttribute("houseType", request.getParameter("houseType"));
	}
	
	
	@RequestMapping(value="/department/{id}/delete", method=RequestMethod.POST)
	public String deleteMemberDepartment(final @PathVariable("id") Long id,
	        final ModelMap model,
	        final HttpServletRequest request) {
	    MemberDepartment memberDepartment = MemberDepartment.findById(MemberDepartment.class, id);
	    memberDepartment.remove();
	    return "info";

	}
	
	//==================== INTERNAL METHODS ====================
	
	private void populate(final ModelMap model, final MemberMinister domain,
            final String locale, final HttpServletRequest request){
		List<Designation> designations =
	        Designation.findAll(Designation.class, "priority", ApplicationConstants.ASC, locale);
	    model.addAttribute("designations", designations);
		
		this.populateMinistries(model, domain, locale, request);
		this.populateDeptsAndSubDepts(model, domain, locale, request);
		
		Long memberId = (long) 0;
		if(request.getParameter("member") == null) {
			memberId = (Long) request.getSession().getAttribute("member");
			request.getSession().removeAttribute("member");
		} else {
			memberId = Long.parseLong(request.getParameter("member"));
		}
		
		Member member = Member.findById(Member.class, memberId);
		model.addAttribute("member", memberId);
		model.addAttribute("fullname", member.getFullname());
		model.addAttribute("isMinisterEmpty",true);
		
		CustomParameter parameter = 
			CustomParameter.findByFieldName(CustomParameter.class, "name", "NON_PORTFOLIO_BASED_DESIGNATIONS", locale);
		model.addAttribute("nonPortfolioDesignations", parameter.getValue());
	}
	
	private void populateMinistries(final ModelMap model, final MemberMinister domain,
            final String locale, final HttpServletRequest request) {
		// In case of New, always show the list of unassigned ministries for the user to 
		// select. In case of edit or update, however, show the current selection
		// of the ministry in addition to the list of unassigned ministries.
		List<Ministry> unassignedMinistries = Ministry.findUnassignedMinistries(locale);
		List<Ministry> ministries = new ArrayList<Ministry>();
		// Case of populateEdit
		if(domain.getMinistry() != null){
			ministries.add(domain.getMinistry());
		} 
		ministries.addAll(unassignedMinistries);
		model.addAttribute("ministries", ministries);
	}
	
	// TODO
	private void populateDeptsAndSubDepts(final ModelMap model, final MemberMinister domain,
            final String locale, final HttpServletRequest request) {
		List<Department> departments =
			Department.findAll(Department.class, "name", ApplicationConstants.ASC, locale);
		model.addAttribute("departments", departments);
		
		if(departments != null){
			if(departments.size() > 0){
				Department department = departments.get(0);
				List<SubDepartment> subdepartments = 
					SubDepartment.findAllByFieldName(SubDepartment.class, 
							"department", department, "name", ApplicationConstants.ASC, locale);
				model.addAttribute("subdepartments", subdepartments);
			}
		}
	}
	
	private void populateEditDepartments(final ModelMap model, final MemberMinister domain,
            final String locale, final HttpServletRequest request){
		Map<Long, List<SubDepartment>> mapOfSubDepartmentList =
			new HashMap<Long, List<SubDepartment>>();
		List<MemberDepartment> memberDepartments = domain.getMemberDepartments();
		int size = memberDepartments.size();
		for(int i = 0; i < size; i++) {
			Department department = memberDepartments.get(i).getDepartment();
			List<SubDepartment> subDepartments = 
				SubDepartment.findAllByFieldName(SubDepartment.class, 
						"department", department, "name", ApplicationConstants.ASC, locale);
			// In the JSPs, the count is initialized to 1. Hence, the
			// key must start with 1.
			mapOfSubDepartmentList.put((long) (i + 1), subDepartments);
		}
		model.addAttribute("mapOfSubDepartmentList", mapOfSubDepartmentList);
	}
	
	private void populateMemberDepartments(final MemberMinister domain,
            final BindingResult result, final HttpServletRequest request) {
		List<MemberDepartment> memberDepartments = new ArrayList<MemberDepartment>();
		Integer memberDepartmentCount =
			Integer.parseInt(request.getParameter("memberDepartmentCount"));
		for(int i = 1; i <= memberDepartmentCount; i++) {
			String strDepartment = request.getParameter("memberDepartmentDepartment" + i);
			if(strDepartment != null) {
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

				String locale = request.getParameter("memberDepartmentLocale" + i);
				if(locale != null){
					if(! locale.isEmpty()){
						memberDepartment.setLocale(locale);
					}
				}
				
				String version = request.getParameter("memberDepartmentVersion" + i);
				if(version != null){
					if(! version.isEmpty()){
						memberDepartment.setVersion(Long.parseLong(version));
					}
				}
				 
				// The multiselect field "memberDepartmentSubDepartment" + i returns a String
				// array of subdepartments selected by the user.
				String[] strSubDepartments = request.getParameterValues("memberDepartmentSubDepartment" + i);
				if(strSubDepartments != null){
					int noOfSubDepartments = strSubDepartments.length;
					for(int j = 0; j < noOfSubDepartments; j++){
						SubDepartment subDepartment = 
							SubDepartment.findById(SubDepartment.class, Long.parseLong(strSubDepartments[j]));
						memberDepartment.getSubDepartments().add(subDepartment);
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
	
	private void updateMinisterRole(final MemberMinister domain) throws ELSException {
		if(domain.getMinistryToDate()==null || DateUtil.compareDatePartOnly(domain.getMinistryToDate(), new Date()) >= 0) {
			Role role = Role.findByFieldName(Role.class, "type", "MINISTER", domain.getLocale());
			User user = User.find(domain.getMember());
			Credential credential = user.getCredential();			
			credential.getRoles().add(role);
		} else {
			List<MemberMinister> memberMinisters = MemberMinister.findAllByFieldName(MemberMinister.class, "member", domain.getMember(), "ministryToDate", ApplicationConstants.DESC, domain.getLocale());
			boolean shouldRoleBeRemoved = true;
			for(MemberMinister i : memberMinisters) {
				if(i.getId()!=domain.getId() && i.getMinistryToDate() == null) {
					shouldRoleBeRemoved = false;
					break;
				} else if(i.getId()!=domain.getId() && DateUtil.compareDatePartOnly(i.getMinistryToDate(), new Date()) >= 0)	{
					shouldRoleBeRemoved = false;
					break;
				}
			}
			if(shouldRoleBeRemoved == true) {
				Role role = Role.findByFieldName(Role.class, "type", "MINISTER", domain.getLocale());
				User user = User.find(domain.getMember());
				Credential credential = user.getCredential();
				credential.getRoles().remove(role);
			}		
		}		
	}
}