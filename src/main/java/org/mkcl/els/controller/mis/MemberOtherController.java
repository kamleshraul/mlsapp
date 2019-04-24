/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.mis.MemberOtherController.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.controller.mis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberSupportingMember;
import org.mkcl.els.domain.PositionHeld;
import org.mkcl.els.domain.Session;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class MemberOtherController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("member/other")
public class MemberOtherController extends GenericController<Member>{

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#preValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void preValidateUpdate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
		populate(domain, result, request);
    }

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
    protected void populateEdit(final ModelMap model, final Member domain,
            final HttpServletRequest request) {
		if(!domain.getPositionsHeld().isEmpty()){
	        model.addAttribute("positionCount", domain.getPositionsHeld().size());
	        model.addAttribute("positions", domain.getPositionsHeld());
		}else{
	        model.addAttribute("positionCount", 0);
		}
		//will be sued to load appropriate background image
        //this is set in session in case of post and put to display the image
        if(request.getSession().getAttribute("houseType")==null){
        model.addAttribute("houseType",request.getParameter("houseType"));
        }else{
            model.addAttribute("houseType",request.getSession().getAttribute("houseType"));
            request.getSession().removeAttribute("houseType");
        }    }

	/**
	 * Populate.
	 *
	 * @param domain the domain
	 * @param result the result
	 * @param request the request
	 */
	private void populate(final Member domain,
            final BindingResult result, final HttpServletRequest request){
			List<PositionHeld> positions = new ArrayList<PositionHeld>();
			 Integer positionCount = Integer.parseInt(request
			 .getParameter("positionCount"));
			 for (int i = 1; i <= positionCount; i++) {
			  PositionHeld positionHeld = new PositionHeld();
			 String fromDate=request.getParameter("positionFromDate" + i);
			 if(fromDate!=null){
			 if(!fromDate.isEmpty()){
			 positionHeld.setFromDate(fromDate);
			 }
			 }

			 String toDate=request.getParameter("positionToDate" + i);
			 if(toDate!=null){
			 positionHeld.setToDate(toDate);
			 }

			 String position=request.getParameter("positionPosition" + i);
			 if(position!=null){
			 positionHeld.setPosition(position);
			 }

			String id=request.getParameter("positionId"+ i);
	        if(id!=null){
	        	if(!id.isEmpty()){
	        	positionHeld.setId(Long.parseLong(id));
	        	}
	        }

	        String version=request.getParameter("positionVersion"+ i);
	        if(version!=null){
	        	if(!version.isEmpty()){
	        	positionHeld.setVersion(Long.parseLong(version));
	        	}
	        }

	        String locale=request.getParameter("positionLocale"+ i);
	        if(locale!=null){
	        	if(!locale.isEmpty()){
	        	positionHeld.setLocale(locale);
	        }
	        }
			 positions.add(positionHeld);
		 }
			 domain.setPositionsHeld(positions);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
    protected void customValidateUpdate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customInitBinderSuperClass(java.lang.Class, org.springframework.web.bind.WebDataBinder)
	 */
	@Override
    @SuppressWarnings("rawtypes")
	protected <E extends BaseDomain> void customInitBinderSuperClass(
            final Class clazz, final WebDataBinder binder) {
		// Set Date Editor
        CustomParameter parameter = CustomParameter.findByName(
                CustomParameter.class, "SERVER_DATEFORMAT", "");
        SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue());
        dateFormat.setLenient(true);
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
                dateFormat, true));
	}


    /**
     * Delete position.
     *
     * @param id the id
     * @param model the model
     * @param request the request
     * @return the string
     */
    @RequestMapping(value = "/position/{id}/delete", method = RequestMethod.DELETE)
    public String deletePosition(final @PathVariable("id") Long id,
            final ModelMap model, final HttpServletRequest request) {
	    PositionHeld positionHeld=PositionHeld.findById(PositionHeld.class, id);
	    positionHeld.remove();
        return "info";
    }

    @Override
    protected void populateAfterUpdate(final ModelMap model, final Member domain,
            final HttpServletRequest request) {
        request.getSession().setAttribute("houseType",request.getParameter("houseType"));
    }
    
	@Transactional
	@RequestMapping(value="/saveSupportingMembers",method=RequestMethod.POST)
	public @ResponseBody String saveSupportingMembers(final ModelMap model, final HttpServletRequest request, final HttpServletResponse response, final Locale locale) throws ELSException, ParseException {
	{
		//Populate UserGroup
		String usergroupType = request.getParameter("usergroupType");
		if(usergroupType != null && !usergroupType.isEmpty()){
			model.addAttribute("usergroupType", usergroupType);
		}else{
			usergroupType = (String) request.getSession().getAttribute("usergroupType");
			if(usergroupType != null && !usergroupType.isEmpty()){
				model.addAttribute("usergroupType", usergroupType);
				request.getSession().removeAttribute("usergroupType");
			}
			else{
				throw new ELSException("StarredQuestionController.populatenew/5", 
						"UserGroupType is Not set");
			}
		}
		
		//Populate usergroup
		String usergroup = request.getParameter("usergroup");
		if(usergroup != null && !usergroup.isEmpty()){
			model.addAttribute("usergroup",usergroup);
		}else{
			usergroup = (String) request.getSession().getAttribute("usergroup");
			if(usergroup != null && !usergroup.isEmpty()){
				model.addAttribute("usergroup", usergroup);
				request.getSession().removeAttribute("usergroup");
			}
			else{
				throw new ELSException("StarredQuestionController.populatenew/5", 
						"UserGroup is Not set");
			}
		}
    	String strSessionId=request.getParameter("session");
		Long sessionId = Long.parseLong(strSessionId);
		Session session = Session.findById(Session.class, sessionId);
		
		DeviceType deviceType = null;

		String strDeviceType = request.getParameter("deviceType");
		deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
	/*		String server = null;
		CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			server = customParameter.getValue();			
	 if(!strDeviceType.isEmpty()){
			if(server.equals("TOMCAT")){
				try {
					String param = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
					deviceType = DeviceType.findByName(DeviceType.class, param, locale.toString());
				}catch (UnsupportedEncodingException e) {
					logger.error("Cannot Encode the Parameter.");
				}
			}else{
				deviceType = DeviceType.findByName(DeviceType.class, strDeviceType, locale.toString());
			}
		}*/
		  
		Long primaryMemberId = null;
		Member primaryMemId=null;
		if(request.getParameter("primaryMember") != null){
			if(!request.getParameter("primaryMember").isEmpty()){
				primaryMemberId = Long.parseLong(request.getParameter("primaryMember"));
				primaryMemId = Member.findById(Member.class, primaryMemberId);
			}
		}
		
		List<MemberSupportingMember> supportingMembers = new ArrayList<MemberSupportingMember>();
		List<MemberSupportingMember> members=new ArrayList<MemberSupportingMember>();
		if(primaryMemberId!=null){
			Member member=Member.findById(Member.class,primaryMemberId);
			String msg= MemberSupportingMember.deleteMemberSupportingMember(deviceType, member, session, locale.toString());
		}
		String[] strSupportingMemberIds = request.getParameterValues("selectedSupportingMembers");
		if(strSupportingMemberIds != null && strSupportingMemberIds.length > 0) {
		
			for(String strSupportingMemberId : strSupportingMemberIds) {
				MemberSupportingMember supportingMember=null;
				Long supportingMemberId = Long.parseLong(strSupportingMemberId);
				Member member = Member.findById(Member.class, supportingMemberId);
				
			/*	for(MemberSupportingMember j : members){
					if(j.getSupportingMember().getId() == member.getId()){
						supportingMember = j;
						break;
					}
				}
				*/
				if(supportingMember == null){
					supportingMember = new MemberSupportingMember();
					supportingMember.setMember(primaryMemId);
					supportingMember.setLocale(locale.toString());
					supportingMember.setDeviceType(deviceType);
					supportingMember.setSupportingMember(member);
					supportingMember.setSession(session);
					
					
				supportingMembers.add(supportingMember);
			}
		}
			primaryMemId.setMemberSupportingMember(supportingMembers);
			primaryMemId.merge();
		}
	
		String supportingMemberNames = MemberOtherController.getDelimitedMemberSupportingMembers(deviceType, primaryMemId, session, locale.toString(), usergroupType);
		model.addAttribute("supportingMembersName", supportingMemberNames);
	
		return "success";
	}
	}
    
   
    
    public static String getDelimitedMemberSupportingMembers(final DeviceType deviceType,
			final Member member,
			final Session session,
			final String locale,
			String usergroupType) {
		String memberNames = "";
		MemberSupportingMember memberSupportingMember=null;
		List<MemberSupportingMember> selectedSupportingMembers = memberSupportingMember.getMemberSupportingMemberRepository().findMemberSupportingMember(deviceType, member, session, locale);
		if(selectedSupportingMembers != null){
			if(!selectedSupportingMembers.isEmpty()){
				StringBuffer bufferFirstNamesFirst = new StringBuffer();
				for(MemberSupportingMember i:selectedSupportingMembers){
					if(usergroupType != null && !usergroupType.isEmpty() && (usergroupType.equals("member") || usergroupType.equals("typist"))){
						Member m = i.getSupportingMember();
						if(m.isActiveMemberOn(new Date(), locale)){
							bufferFirstNamesFirst.append(m.getFullname() + ",");
						}
					}
				}
				if(bufferFirstNamesFirst.length()>0){
					bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length()-1);
					memberNames = bufferFirstNamesFirst.toString();
				}
			}
		}
		return memberNames;
	}
}


