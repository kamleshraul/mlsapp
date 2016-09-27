package org.mkcl.els.controller.cis;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CommitteeMeeting;
import org.mkcl.els.domain.CommitteeMeetingType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.NonCommiteeMember;
import org.mkcl.els.domain.NonCommiteeMemberInformation;
import org.mkcl.els.domain.NonCommitteeMemberType;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("committeemeeting/noncommitteemembers")
public class NonCommiteeMembersController extends GenericController<NonCommiteeMember> {

	
	@Autowired
	private IProcessService processService;
	
	@Override
	protected void populateList(final ModelMap model, final HttpServletRequest request,
            final String locale, final AuthUser currentUser) {
		super.populateList(model, request, locale, currentUser);
		
		String committeeMeetingId = request.getParameter("committeeMeetingId");
		model.addAttribute("committeeMeetingId", committeeMeetingId);
	}
	
	@Override
	protected void populateNew(final ModelMap model, 
			final NonCommiteeMember domain,
			final String locale, 
			final HttpServletRequest request) {
		domain.setLocale(locale);
		this.populateNonCommitteeMemberTypes(model, locale);
		this.commonPopulateEdit(model, domain, request);	
		String committeeMeetingId = request.getParameter("committeeMeetingId");
		model.addAttribute("committeeMeetingId", committeeMeetingId);
	}

	@Override
	protected void preValidateCreate(final NonCommiteeMember domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		
		this.setCommitteeMeeting(domain, request);
		this.setNonCommitteeMemberType(domain, request);
		this.setNonCommiteeMemberInfos(domain, request);
		this.valEmptyAndNull(domain, result);
		this.preValidate(domain, result, request);
	}

	@Override
	protected void customValidateCreate(final NonCommiteeMember domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		
		
	}
	//=============== VALIDATIONS =========
	private void valEmptyAndNull(final NonCommiteeMember domain, 
			final BindingResult result) {
		
		if(domain.getDepartmentName() == null || domain.getDepartmentName() == "-") {
			result.rejectValue("departmentName", "NotEmpty",
			"Department Name should not be empty");
		}

		
		if(domain.getNonCommitteeMemberType() == null) {
			result.rejectValue("nonCommitteeMemberType", "NotEmpty",
			"Non CommitteeMemberType should not be empty");
		}

		
		if(domain.getNoncommiteememberinformation() == null) {
			result.rejectValue("noncommiteememberinformation", "NotEmpty",
			"Non commiteemember information should not be empty");
		}
	}
	@Override
	protected void populateEdit(final ModelMap model, 
			final NonCommiteeMember domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		this.commonPopulateEdit(model, domain, request);
		this.populateNonCommitteeMemberTypes(model, locale);

		this.populateNonCommitteeMemberType(model,
				domain.getNonCommitteeMemberType());
		String committeeMeetingId = request.getParameter("committeeMeetingId");
		model.addAttribute("committeeMeetingId", committeeMeetingId);
		
	}
	
	@Override
	protected void preValidateUpdate(final NonCommiteeMember domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		this.setCommitteeMeeting(domain, request);
		this.setNonCommitteeMemberType(domain, request);
		this.setNonCommiteeMemberInfos(domain, request);
		this.preValidate(domain, result, request);
	}
	
	private void preValidate(final NonCommiteeMember domain,
			final BindingResult result, 
			final HttpServletRequest request){
	}
	@Override
	protected void customValidateUpdate(final NonCommiteeMember domain,
			final BindingResult result, 
			final HttpServletRequest request) {
		
	}
	
	@Override
	protected void populateAfterUpdate(final ModelMap model, 
			final NonCommiteeMember domain,
			final HttpServletRequest request) {		
			
	}	
	
	@RequestMapping(value="{id}/view", method=RequestMethod.GET)
	public String view(final ModelMap model, 
			@PathVariable("id") final Long id,
			final Locale localeObj) {
		String locale = localeObj.toString();
		
		NonCommiteeMember nonCommiteeMember = NonCommiteeMember.findById(NonCommiteeMember.class, id);
		model.addAttribute("id", nonCommiteeMember.getId());
		model.addAttribute("departmentName", nonCommiteeMember.getDepartmentName());
		model.addAttribute("noncommiteememberinformations", nonCommiteeMember.getNoncommiteememberinformation());
		
		return "committeemeeting/noncommitteemembers/view";
	}

	
	@RequestMapping(value="/info/{id}/delete", method=RequestMethod.DELETE)
	public String deleteTourItinerary(final @PathVariable("id") Long id) {
		NonCommiteeMemberInformation nmInfo = NonCommiteeMemberInformation.findById(NonCommiteeMemberInformation.class, id);
		nmInfo.remove();
	    return "info";
	}
	
	private void setNonCommiteeMemberInfos(final NonCommiteeMember domain,
			final HttpServletRequest request) {
		
				
		
		List<NonCommiteeMemberInformation> nonCommiteeMemberInformation = new ArrayList<NonCommiteeMemberInformation>();
		
		Integer noncommitteememberSize =
			Integer.parseInt(request.getParameter("noncommitteememberSize"));
		for(int i = 1; i <= noncommitteememberSize; i++) {
			String strnoncommitteemember = request.getParameter("noncommitteemember" + i);
			
			
			if(strnoncommitteemember != null && ! strnoncommitteemember.isEmpty()) {
				NonCommiteeMemberInformation qAns = new NonCommiteeMemberInformation();

				qAns.setName(strnoncommitteemember);				 
				
				
				String strId = request.getParameter("prasInfoId" + i);
				if(strId != null && ! strId.isEmpty()){
					Long id = Long.parseLong(strId);
					qAns.setId(id);
				}

				String locale = request.getParameter("prasInfoLocale" + i);
				if(locale != null && ! locale.isEmpty()){
					qAns.setLocale(locale);
				}
				
				String strVersion = 
					request.getParameter("prasInfoVersion" + i);
				if(strVersion != null && ! strVersion.isEmpty()){
					Long version = Long.parseLong(strVersion);
					qAns.setVersion(version);
				}
				
				nonCommiteeMemberInformation.add(qAns);
			}
		}
		
			domain.setNoncommiteememberinformation(nonCommiteeMemberInformation);
	}
	
	
	//==========================================
	// Common Internal Methods
	//==========================================
	
	private void setCommitteeMeeting(final NonCommiteeMember domain,
			final HttpServletRequest request) {
		String strcommitteeMeetingId = request.getParameter("committeeMeetingId");
		if (strcommitteeMeetingId != null && !strcommitteeMeetingId.isEmpty()) {
			Long committeeMeetingId = Long.parseLong(strcommitteeMeetingId);
			CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(
					CommitteeMeeting.class, committeeMeetingId);
			domain.setCommitteeMeeting(committeeMeeting);
		}
	}

	private void setNonCommitteeMemberType(final NonCommiteeMember domain,
			final HttpServletRequest request) {
		String strnonCommitteeMemberTypes = request
				.getParameter("nonCommitteeMemberTypes");
		if (strnonCommitteeMemberTypes != null
				&& !strnonCommitteeMemberTypes.isEmpty()) {
			Long nonCommitteeMemberTypeId = Long
					.parseLong(strnonCommitteeMemberTypes);
			NonCommitteeMemberType nonCommitteeMemberType = NonCommitteeMemberType
					.findById(NonCommitteeMemberType.class,
							nonCommitteeMemberTypeId);
			domain.setNonCommitteeMemberType(nonCommitteeMemberType);
		}
	}
	
	private List<NonCommitteeMemberType> populateNonCommitteeMemberTypes(final ModelMap model,
			final String locale) {
		List<NonCommitteeMemberType> nonCommitteeMemberTypes = NonCommitteeMemberType.findAll(
				NonCommitteeMemberType.class, "name", ApplicationConstants.ASC, locale);
		model.addAttribute("nonCommitteeMemberTypes", nonCommitteeMemberTypes);
		return nonCommitteeMemberTypes;
	}
	
	private void populateNonCommitteeMemberType(final ModelMap model,
			final NonCommitteeMemberType nonCommitteeMemberType) {

		model.addAttribute("nonCommitteeMemberType", nonCommitteeMemberType);
	}
	
		private void commonPopulateEdit(final ModelMap model, 
			final NonCommiteeMember domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		
		if(domain.getNoncommiteememberinformation() != null){
			model.addAttribute("noncommitteememberSize", domain.getNoncommiteememberinformation().size());
		}else{
			model.addAttribute("noncommitteememberSize", 0);
		}
		model.addAttribute("noncommitteemembers", domain.getNoncommiteememberinformation());
		HouseType hsType = null;
		String strHouseType = this.getCurrentUser().getHouseType();
		if(strHouseType != null && !strHouseType.isEmpty()){
			try{
				hsType = HouseType.findByType(strHouseType, locale);
			}catch(Exception e){
				logger.error("error", e);
			}
			
			if(hsType == null){
				hsType = HouseType.findById(HouseType.class, new Long(strHouseType));
			}
		}
		
		
			}
	
	
	
}