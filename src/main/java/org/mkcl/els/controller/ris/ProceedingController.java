package org.mkcl.els.controller.ris;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.codehaus.groovy.antlr.AntlrParserPlugin;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Bookmark;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Designation;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Part;
import org.mkcl.els.domain.PartialUpdate;
import org.mkcl.els.domain.Proceeding;
import org.mkcl.els.domain.ProceedingCitation;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Reporter;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Slot;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proceeding")
public class ProceedingController extends GenericController<Proceeding>{

	/*****************Proceeding Related**************/
	@Override
	protected void populateModule(final ModelMap model,final HttpServletRequest request,
			final String locale,final AuthUser currentUser) {	

		/**** House Types ****/
		List<HouseType> houseTypes = new ArrayList<HouseType>();
		String houseType=this.getCurrentUser().getHouseType();
		if(houseType.equals("lowerhouse")){
			houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
		}else if(houseType.equals("upperhouse")){
			houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
		}else if(houseType.equals("bothhouse")){
			houseTypes=HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
		}
		model.addAttribute("houseTypes", houseTypes);
		if(houseType.equals("bothhouse")){
			houseType="lowerhouse";
		}
		model.addAttribute("houseType",houseType);

		/**** Session Types ****/
		List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
		HouseType authUserHouseType=HouseType.findByFieldName(HouseType.class, "type",houseType, locale);
		Session lastSessionCreated = null;
		try {
			lastSessionCreated = Session.findLatestSession(authUserHouseType);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Integer year=new GregorianCalendar().get(Calendar.YEAR);
		if(lastSessionCreated.getId()!=null){
			year=lastSessionCreated.getYear();
			model.addAttribute("sessionType",lastSessionCreated.getType().getId());
		}else{
			model.addAttribute("errorcode","nosessionentriesfound");
		}
		model.addAttribute("sessionTypes",sessionTypes);

		/**** Years ****/
		CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
		List<Integer> years=new ArrayList<Integer>();
		if(houseFormationYear!=null){
			Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
			for(int i=year;i>=formationYear;i--){
				years.add(i);
			}
		}else{
			model.addAttribute("errorcode", "houseformationyearnotset");
		}
		model.addAttribute("years",years);
		model.addAttribute("sessionYear",year);	

		/**** Language ****/
		List<Language> languages = null;
		try {
			languages = Language.findAllLanguagesByModule("RIS",locale);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("languages",languages);

		/******Reporter*********/
		User user = null;
		try {
			user = User.findByUserName(this.getCurrentUser().getUsername(),locale);
			model.addAttribute("ugparam",user.getId());
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//List<Reporter> reporter=Reporter.findAllByFieldName(Reporter.class, "user", user, "id", "desc", locale);
		
	}

	@Override
	protected void populateEdit(final ModelMap model, final Proceeding domain,
			final HttpServletRequest request) {
		if(domain.getSlot()!=null){
			Slot slot=domain.getSlot();
			Roster roster=slot.getRoster();
			Session session=roster.getSession();
			List<Ministry> ministries;
			try {
				ministries = Ministry.findMinistriesAssignedToGroups(session.getHouse().getType(), session.getYear(), session.getType(), session.getLocale());
				model.addAttribute("ministries", ministries);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		String strHouseType=this.getCurrentUser().getHouseType();
		HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, domain.getLocale());
		model.addAttribute("slot", domain.getSlot().getId());
		model.addAttribute("slotName",domain.getSlot().getName());
		List<Member> members=Member.findAll(Member.class, "firstName", "asc", domain.getLocale());
		model.addAttribute("members",members);
		List<MemberRole> roles=MemberRole.findAllByFieldName(MemberRole.class, "houseType", houseType, "name", "asc", domain.getLocale());
		model.addAttribute("roles", roles);
		List<Designation> designations=Designation.findAll(Designation.class, "name", "asc", domain.getLocale());
		model.addAttribute("designations",designations);
		List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name", "desc", domain.getLocale());
		model.addAttribute("deviceTypes", deviceTypes);
		List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", domain, "orderNo", "asc", domain.getLocale());
		model.addAttribute("parts", parts);
		if(parts.isEmpty()){
			model.addAttribute("partCount", 0);
		}else{
			model.addAttribute("partCount", domain.getParts().size());
		}
		Roster roster=domain.getSlot().getRoster();
		Session session=roster.getSession();
		model.addAttribute("session",session.getId());
		Slot slot=domain.getSlot();
		List<Bookmark> bk=new ArrayList<Bookmark>();
		List<Bookmark> bookmarks=Bookmark.findAllByFieldName(Bookmark.class, "slot", slot, "bookmarkKey", "asc", domain.getLocale());
		if(bookmarks.isEmpty()){
			List<Bookmark> bookmarks1=Bookmark.findAllByFieldName(Bookmark.class, "language", domain.getSlot().findLanguage(), "id", "asc", domain.getLocale());
			for(Bookmark b:bookmarks1){
				if(b.getSlot()==null){
					String key=b.getBookmarkKey();
					String[] keyArr=key.split("-");
					String[] keyArr1=keyArr[1].split("~");
					String[] keyArry2=keyArr1[1].split("_");
					String[] slotArr=keyArry2[0].split("/");
					for(int i=0;i<slotArr.length;i++){
						if(slotArr[i].equals(domain.getSlot().getName())){
							bk.add(b);
						}
					}
				}

			}
			model.addAttribute("bookmarks", bk);

		}else{
			model.addAttribute("bookmarks", bookmarks);
		}

		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, ApplicationConstants.PROCEEDING_SEARCHOPTION, "");
		if(customParameter!=null){
			String param=customParameter.getValue();
			//String[] searchOptions=param.split("##");
			model.addAttribute("searchOptions", param);
		}
	}

	@Override
	protected void preValidateUpdate(final Proceeding domain,
			final BindingResult result, final HttpServletRequest request) {
		populateParts(domain, request, result);

	}

	private void populateParts(Proceeding domain, HttpServletRequest request,
			BindingResult result) {
		List<Part> parts = new ArrayList<Part>();
		Integer partCount = Integer.parseInt(request
				.getParameter("partCount"));
		for (int i = 1; i <= partCount; i++) {
			Part part=new Part();
			String strMember=request.getParameter("primaryMember"+i);
			if(strMember!=null && !strMember.equals("")){
				Member member=Member.findById(Member.class, Long.parseLong(strMember));
				part.setPrimaryMember(member);
			}
			String id=request.getParameter("partId"+ i);
			if(id!=null){
				if(!id.isEmpty()){
					part.setId(Long.parseLong(id));
				}
			}

			String order=request.getParameter("order"+ i);
			if(order!=null){
				if(!order.isEmpty()){
					part.setOrderNo(Integer.parseInt(order));
				}
			}

			String version=request.getParameter("partVersion"+ i);
			if(version!=null){
				if(!version.isEmpty()){
					part.setVersion(Long.parseLong(version));
				}
			}

			String locale=request.getParameter("partLocale"+ i);
			if(locale!=null){
				if(!locale.isEmpty()){
					part.setLocale(locale);
				}
			}
			String mainHeading=request.getParameter("mainHeading"+i);
			if(mainHeading!=null&&!mainHeading.isEmpty()){
				part.setMainHeading(mainHeading);
			}
			String pageHeading=request.getParameter("pageHeading"+i);
			if(pageHeading!=null && !pageHeading.isEmpty()){
				part.setPageHeading(pageHeading);
			}
			/*String chairPerson=request.getParameter("chairPerson"+i);
			if(chairPerson!=null&& !chairPerson.isEmpty()){
				part.setChairPerson(chairPerson);
			}*/
			String strRole=request.getParameter("chairPersonRole"+i);
			if(strRole!=null && !strRole.isEmpty()){
				MemberRole mr=MemberRole.findById(MemberRole.class, Long.parseLong(strRole));
				Member chairPersonMember=null;
				if(mr!=null){
					Slot slot=domain.getSlot();
					Roster roster=slot.getRoster();
					Session session=roster.getSession();
					House house=session.getHouse();
					List<HouseMemberRoleAssociation> hmras;
					try {
						hmras = HouseMemberRoleAssociation.findActiveHouseMemberRoles(house, mr, new Date(), domain.getLocale());
						for(HouseMemberRoleAssociation h:hmras){
							if(h.getRole().equals(mr)){
								chairPersonMember=h.getMember();
								break;
							}
						}
						part.setChairPersonRole(mr);
						part.setChairPerson(chairPersonMember.getFullname());
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
			String content=request.getParameter("content"+i);
			if(content!=null && !content.isEmpty()){
				part.setProceedingContent(content);
				part.setRevisedContent(content);
			}
			String strProceeding=request.getParameter("partProceeding"+i);
			if(strProceeding!=null && !strProceeding.isEmpty()){
				Proceeding proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
				part.setProceeding(proceeding);
			}
			String strPrimaryMemberMinistry=request.getParameter("primaryMemberMinistry"+i);
			if(strPrimaryMemberMinistry!=null && !strPrimaryMemberMinistry.isEmpty()){
				Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strPrimaryMemberMinistry));
				part.setPrimaryMemberMinistry(ministry);
			}
			String strPrimaryMemberDesignation=request.getParameter("primaryMemberDesignation"+i);
			if(strPrimaryMemberDesignation!=null && !strPrimaryMemberDesignation.isEmpty()){
				Designation designation=Designation.findById(Designation.class, Long.parseLong(strPrimaryMemberDesignation));
				part.setPrimaryMemberDesignation(designation);
			}
			String strSubstituteMember=request.getParameter("substituteMember"+i);
			if(strSubstituteMember!=null && !strSubstituteMember.isEmpty()){
				Member member=Member.findById(Member.class, Long.parseLong(strSubstituteMember));
				part.setSubstituteMember(member);
			}
			String strSubstituteMemberMinistry=request.getParameter("substituteMemberMinistry"+i);
			if(strSubstituteMemberMinistry!=null && !strSubstituteMemberMinistry.isEmpty()){
				Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strSubstituteMemberMinistry));
				part.setSubstituteMemberMinistry(ministry);
			}
			String strSubstituteMemberDesignation=request.getParameter("substituteMemberDesignation"+i);
			if(strSubstituteMemberDesignation!=null && !strSubstituteMemberDesignation.isEmpty()){
				Designation designation=Designation.findById(Designation.class, Long.parseLong(strSubstituteMemberDesignation));
				part.setSubstituteMemberDesignation(designation);
			}
			String strPublicRepresentative=request.getParameter("publicRepresentative"+i);
			if(strPublicRepresentative!=null && !strPublicRepresentative.isEmpty()){
				part.setPublicRepresentative(strPublicRepresentative);
			}

			String strPublicRepresentativeDetail=request.getParameter("publicRepresentativeDetail"+i);
			if(strPublicRepresentativeDetail!=null && !strPublicRepresentativeDetail.isEmpty()){
				part.setPublicRepresentativeDetail(strPublicRepresentativeDetail);
			}
			String strDeviceType=request.getParameter("deviceType"+i);
			if(strDeviceType!=null && !strDeviceType.isEmpty()){
				DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				part.setDeviceType(deviceType);
			}
			String strDeviceId=request.getParameter("deviceId"+i);
			if(strDeviceId!=null && !strDeviceId.isEmpty()){
				part.setDeviceId(Long.parseLong(strDeviceId));
			}
			part.setEntryDate(new Date());
			part.setReporter(domain.getSlot().getReporter());
			parts.add(part);
		}
		domain.setParts(parts);

	}
	@Override
	protected void customValidateUpdate(final Proceeding domain,
			final BindingResult result, final HttpServletRequest request) {

	}

	


	@RequestMapping(value="/part/viewbookmark",method=RequestMethod.GET)
	public String viewBookmarkDetail(final HttpServletRequest request, final Locale locale,
			final ModelMap model){
		String strBookmarkId=request.getParameter("id");
		if(strBookmarkId!=null && !strBookmarkId.isEmpty()){
			Bookmark bookmark=Bookmark.findById(Bookmark.class, Long.parseLong(strBookmarkId));
			if(bookmark!=null){
				model.addAttribute("previousText", bookmark.getPreviousText());
				model.addAttribute("bookmarkKey",bookmark.getBookmarkKey());
				Part part=bookmark.getMasterPart();
				if(part!=null){
					Member member=part.getPrimaryMember();
					if(member!=null){
						model.addAttribute("memberName", member.getFullname());
					}
					Reporter reporter=part.getReporter();
					if(reporter!=null){
						User user=reporter.getUser();
						if(user!=null){
							model.addAttribute("reporter",user.findFullName());
						}
					}
				}
			}

		}
		return "proceeding/bookmarkreadonly";
	}

	@Transactional
	@RequestMapping(value = "/{proceedingId}/{partId}/delete", method = RequestMethod.DELETE)
	public @ResponseBody String deletePart(final @PathVariable("proceedingId") Long proceedingId,
			final @PathVariable("partId") Long partId,
			final ModelMap model, final HttpServletRequest request) {
		Proceeding proceeding =Proceeding.findById(Proceeding.class,proceedingId);
		Boolean status=false;
		status=Proceeding.removePart(proceeding,partId);
		if(status){
			return "SUCCESS";
		}else{
			return "FAILED";
		}
	}


	/**************************Parts Related ***********************************/
	@RequestMapping(value = "/part/module", method = RequestMethod.GET)
	public String indexPart(final ModelMap model, final HttpServletRequest request,
			final @RequestParam(required = false) String formtype,
			final Locale locale) {
		final String servletPath = request.getServletPath().replaceFirst("\\/","");
		/******* Hook **********/
		populateModule(model, request, locale.toString(), this.getCurrentUser());
		/***********************/
		if (formtype != null) {
			if (formtype.equals("g")) {
				String urlPattern=servletPath.split("\\/module")[0];
				String messagePattern=urlPattern.replaceAll("\\/",".");
				model.addAttribute("messagePattern", messagePattern);
				model.addAttribute("urlPattern", urlPattern);
				return "generic/module";
			}
		}
		//here making provisions for displaying error pages
		if(model.containsAttribute("errorcode")){
			return servletPath.replace("module","error");
		}else{
			return servletPath;
		}
	}

	@RequestMapping(value = "/part/list", method = RequestMethod.GET)
	public String listPart(@RequestParam(required = false) final String formtype,
			final ModelMap model, final Locale locale,
			final HttpServletRequest request) {
		final String servletPath = request.getServletPath().replaceFirst("\\/","");
		String urlPattern=servletPath.split("\\/list")[0];
		String messagePattern=urlPattern.replaceAll("\\/",".");
		String newurlPattern=modifyURLPattern(urlPattern,request,model,locale.toString());
		Grid grid;
		try {
			grid = Grid.findByDetailView(newurlPattern, locale.toString());
			model.addAttribute("gridId", grid.getId());
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/******* Hook **********/
		populateList(model, request, locale.toString(), this.getCurrentUser());
		/***********************/
		if (formtype != null) {
			if (formtype.equals("g")) {
				model.addAttribute("messagePattern", messagePattern);
				model.addAttribute("urlPattern", urlPattern);
				return "generic/list";
			}
		}
		//here making provisions for displaying error pages
		if(model.containsAttribute("errorcode")){
			return servletPath.replace("list","error");
		}else{
			return servletPath;
		}
	}

	@RequestMapping(value = "/part/new", method = RequestMethod.GET)
	public String newPart(final ModelMap model, final Locale locale,
			final HttpServletRequest request) {
		final String servletPath = request.getServletPath().replaceFirst("\\/","");
		String urlPattern=servletPath.split("\\/new")[0];
		String messagePattern=urlPattern.replaceAll("\\/",".");
		model.addAttribute("messagePattern", messagePattern);
		model.addAttribute("urlPattern", urlPattern);
		//THIS IS USED TO REMOVE THE BUG WHERE IN RECORD UPDATED MESSAGE
		//APPEARS WHEN CLICKED ON NEW REOCRD
		model.addAttribute("type", "");
		Part domain=new Part();
		domain.setLocale(locale.toString());
		String strProceeding= request.getParameter("proceeding");
		if(strProceeding!=null && !strProceeding.isEmpty()){
			Proceeding proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			model.addAttribute("proceeding", proceeding.getId());
			Slot slot=proceeding.getSlot();
			if(slot!=null){
				model.addAttribute("slotName", slot.getName());
				model.addAttribute("session",slot.getRoster().getSession().getId());
				model.addAttribute("reporter", slot.getReporter().getId());
				Roster roster=slot.getRoster();
				Session session=roster.getSession();
				model.addAttribute("session",session.getId());
				List<Ministry> ministries;
				try {
					ministries = Ministry.findMinistriesAssignedToGroups(session.getHouse().getType(), session.getYear(), session.getType(), session.getLocale());
					model.addAttribute("ministries", ministries);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				List<Bookmark> bk=new ArrayList<Bookmark>();
				List<Bookmark> bookmarks=Bookmark.findAllByFieldName(Bookmark.class, "slot", slot, "bookmarkKey", "asc", domain.getLocale());
				if(bookmarks.isEmpty()){
					if(domain.getProceeding()!=null){
						List<Bookmark> bookmarks1=Bookmark.findAllByFieldName(Bookmark.class, "language", domain.getProceeding().getSlot().findLanguage(), "id", "asc", domain.getLocale());
						for(Bookmark b:bookmarks1){
							if(b.getSlot()==null){
								String key=b.getBookmarkKey();
								String[] keyArr=key.split("-");
								String[] keyArr1=keyArr[1].split("~");
								String[] keyArry2=keyArr1[1].split("_");
								String[] slotArr=keyArry2[0].split("/");
								for(int i=0;i<slotArr.length;i++){
									if(slotArr[i].equals(domain.getProceeding().getSlot().getName())){
										bk.add(b);
									}
								}
							}

						}
						model.addAttribute("bookmarks", bk);
					}
				}else{
					model.addAttribute("bookmarks", bookmarks);
				}
			}

			List<Part> parts=proceeding.getParts();
			if(parts.isEmpty()){
				model.addAttribute("orderNo", "1");
			}else{
				model.addAttribute("orderNo", parts.size()+1);
				model.addAttribute("lastPartId",parts.get(parts.size()-1).getId());
			}

		}
		String strHouseType=this.getCurrentUser().getHouseType();
		HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
		List<MemberRole> roles=MemberRole.findAllByFieldName(MemberRole.class, "houseType", houseType, "name", "asc", locale.toString());
		model.addAttribute("roles", roles);
		List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name", "desc", locale.toString());
		model.addAttribute("deviceTypes", deviceTypes);
		model.addAttribute("domain", domain);

		List<Designation> designations=Designation.findAll(Designation.class,"name", "asc", locale.toString());
		model.addAttribute("designations",designations);


		//here making provisions for displaying error pages
		if(model.containsAttribute("errorcode")){
			return servletPath.replace("new","error");
		}else{
			String modifiedNewUrlPattern=modifyNewUrlPattern(servletPath,request,model,locale.toString());
			return modifiedNewUrlPattern;
		}
	}




	@RequestMapping(value = "/part",method = RequestMethod.POST)
	public String createPart(final ModelMap model,
			final HttpServletRequest request,
			final RedirectAttributes redirectAttributes,
			final Locale locale,
			@Valid @ModelAttribute("domain") final Part domain,
			final BindingResult result) {
		model.addAttribute("domain", domain);
		/*****Hook*************/
		validateCreatePart(domain, result, request);
		/*****Hook*************/
		populateCreatePartIfNoErrors(model, domain, request);
		/**********************/
		((BaseDomain) domain).persist();
		redirectAttributes.addFlashAttribute("type", "success");
		//this is done so as to remove the bug due to which update message appears even though there
		//is a fresh new/edit request i.e after creating/updating records if we click on
		//new /edit then success message appears
		request.getSession().setAttribute("type","success");
		redirectAttributes.addFlashAttribute("msg", "create_success");
		model.addAttribute("currentId",domain.getId());
		String returnUrl = "redirect:/proceeding/part/new?proceeding="+domain.getProceeding().getId();
		return returnUrl;
	}


	protected void populateCreatePartIfNoErrors(final ModelMap model,
			final Part domain, final HttpServletRequest request) {
		domain.setEntryDate(new Date());
		String strMember=request.getParameter("primaryMember");
		String strProceeding=request.getParameter("proceeding");
		String strReporter=request.getParameter("reporter");
		String strRole=request.getParameter("chairPersonRole");
		String strPrimaryMemberMinistry=request.getParameter("primaryMemberMinistry");
		String strPrimaryMemberDesignation=request.getParameter("primaryMemberDesignation");
		String strSubstituteMember=request.getParameter("substituteMember");
		String strSubstituteMinistry=request.getParameter("substituteMemberMinistry");
		String strSubstituteDesignation=request.getParameter("substituteMemberDesignation");
		String strDeviceId=request.getParameter("deviceId");
		String strDeviceType=request.getParameter("deviceType");
		Proceeding proceeding=null;
		if(strProceeding!=null && !strProceeding.equals("")){
			proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			domain.setProceeding(proceeding);
		}
		if(strReporter!=null && !strReporter.equals("")){
			Reporter reporter=Reporter.findById(Reporter.class, Long.parseLong(strReporter));
			domain.setReporter(reporter);
		}
		if(strMember!=null && !strMember.equals("")){
			Member member=Member.findById(Member.class, Long.parseLong(strMember));
			domain.setPrimaryMember(member);
		}
		if(strRole!=null && !strRole.equals("")){
			MemberRole mr=MemberRole.findById(MemberRole.class, Long.parseLong(strRole));
			Member member=null;
			if(mr!=null){
				Slot slot=proceeding.getSlot();
				Roster roster=slot.getRoster();
				Session session=roster.getSession();
				House house=session.getHouse();
				List<HouseMemberRoleAssociation> hmras;
				try {
					hmras = HouseMemberRoleAssociation.findActiveHouseMemberRoles(house, mr, new Date(), domain.getLocale());
					for(HouseMemberRoleAssociation h:hmras){
						if(h.getRole().equals(mr)){
							member=h.getMember();
							break;
						}
					}
					domain.setChairPersonRole(mr);
					domain.setChairPerson(member.getFullname());	
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				domain.setRevisedContent(domain.getProceedingContent());
			}
		}
		if(strPrimaryMemberDesignation!=null && !strPrimaryMemberDesignation.isEmpty()){
			Designation designation=Designation.findById(Designation.class, Long.parseLong(strPrimaryMemberDesignation));
			domain.setPrimaryMemberDesignation(designation);
		}else{
			domain.setPrimaryMemberDesignation(null);
		}
		if(strPrimaryMemberMinistry!=null && !strPrimaryMemberMinistry.isEmpty()){
			Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strPrimaryMemberMinistry));
			domain.setPrimaryMemberMinistry(ministry);
		}

		if(strSubstituteMember!=null && !strSubstituteMember.isEmpty()){
			Member member=Member.findById(Member.class, Long.parseLong(strSubstituteMember));
			domain.setSubstituteMember(member);
		}

		if(strSubstituteDesignation!=null && !strSubstituteDesignation.isEmpty()){
			Designation designation=Designation.findById(Designation.class, Long.parseLong(strSubstituteDesignation));
			domain.setSubstituteMemberDesignation(designation);
		}else{
			domain.setSubstituteMemberDesignation(null);
		}

		if(strSubstituteMinistry!=null && !strSubstituteMinistry.isEmpty()){
			Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strSubstituteMinistry));
			domain.setSubstituteMemberMinistry(ministry);
		}
		if(strDeviceType!=null && !strDeviceType.isEmpty()){
			DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			domain.setDeviceType(deviceType);
		}
		if(strDeviceId!=null && !strDeviceId.isEmpty()){
			domain.setDeviceId(Long.parseLong(strDeviceId));
		}
	}

	private void validateCreatePart(final Part domain, final BindingResult result,
			final HttpServletRequest request) {

	}

	@RequestMapping(value = "/part/{id}/edit", method = RequestMethod.GET)
	public String editPart(final @PathVariable("id") Long id, final ModelMap model,
			final HttpServletRequest request) {
		final String servletPath = request.getServletPath().replaceFirst("\\/","");
		String urlPattern=servletPath.split("\\/edit")[0].replace("/"+id,"");
		String messagePattern=urlPattern.replaceAll("\\/",".");
		model.addAttribute("messagePattern", messagePattern);
		model.addAttribute("urlPattern", urlPattern);
		Part domain = Part.findById(Part.class, id);
		Slot slot=domain.getProceeding().getSlot();
		
		if(slot!=null){
			model.addAttribute("currentSlot",slot.getId());
			model.addAttribute("slotName", slot.getName());
			model.addAttribute("session",slot.getRoster().getSession().getId());
			model.addAttribute("reporter", slot.getReporter().getId());
			Roster roster=slot.getRoster();
			Session session=roster.getSession();
			model.addAttribute("session",session.getId());
			List<Ministry> ministries;
			try {
				ministries = Ministry.findMinistriesAssignedToGroups(session.getHouse().getType(), session.getYear(), session.getType(), session.getLocale());
				model.addAttribute("ministries", ministries);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(domain.getPrimaryMemberMinistry()!=null){
				model.addAttribute("primaryMemberMinistrySelected", domain.getPrimaryMemberMinistry().getId());
			}
			if(domain.getSubstituteMemberMinistry()!=null){
				model.addAttribute("substituteMemberMinistrySelected", domain.getSubstituteMemberMinistry().getId());
			}

			List<Bookmark> bk=new ArrayList<Bookmark>();
			List<Bookmark> bookmarks=Bookmark.findAllByFieldName(Bookmark.class, "slot", slot, "bookmarkKey", "asc", domain.getLocale());
			if(bookmarks.isEmpty()){
				List<Bookmark> bookmarks1=Bookmark.findAllByFieldName(Bookmark.class, "language", domain.getProceeding().getSlot().findLanguage(), "id", "asc", domain.getLocale());
				for(Bookmark b:bookmarks1){
					if(b.getSlot()==null){
						String key=b.getBookmarkKey();
						String[] keyArr=key.split("-");
						String[] keyArr1=keyArr[1].split("~");
						String[] keyArry2=keyArr1[1].split("_");
						String[] slotArr=keyArry2[0].split("/");
						for(int i=0;i<slotArr.length;i++){
							if(slotArr[i].equals(domain.getProceeding().getSlot().getName())){
								bk.add(b);
							}
						}
					}

				}
				model.addAttribute("bookmarks", bk);

			}else{
				model.addAttribute("bookmarks", bookmarks);
			}
		}
		String strHouseType=this.getCurrentUser().getHouseType();
		HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, domain.getLocale());
		List<MemberRole> roles=MemberRole.findAllByFieldName(MemberRole.class, "houseType", houseType, "name", "asc", domain.getLocale());
		model.addAttribute("roles", roles);
		model.addAttribute("proceeding",domain.getProceeding().getId());
		if(domain.getPrimaryMember()!=null){
			model.addAttribute("primaryMember",domain.getPrimaryMember().getId());
			model.addAttribute("formattedPrimaryMember", domain.getPrimaryMember().getFullname());
		}
		if(domain.getSubstituteMember()!=null){
			model.addAttribute("substituteMember",domain.getPrimaryMember().getId());
			model.addAttribute("formattedSubstituteMember", domain.getSubstituteMember().getFullname());
		}
		/***********************/
		model.addAttribute("domain", domain);
		List<Designation> designations=Designation.findAll(Designation.class,"name", "asc", domain.getLocale());
		model.addAttribute("designations",designations);
		if(domain.getPrimaryMemberDesignation()!=null){
			model.addAttribute("primaryMemberDesignationSelected", domain.getPrimaryMemberDesignation().getId());
		}
		if(domain.getSubstituteMemberDesignation()!=null){
			model.addAttribute("substituteMemberDesignationSelected", domain.getSubstituteMemberDesignation().getId());
		}
		
		if(domain.getDeviceType()!=null){
			DeviceType deviceType=domain.getDeviceType();
			model.addAttribute("selectedDeviceType",deviceType.getId());
		}
		List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name", "asc", domain.getLocale());
		model.addAttribute("deviceTypes", deviceTypes);
		//this is done so as to remove the bug due to which update message appears even though there
		//is a fresh new/edit request i.e after creating/updating records if we click on
		//new /edit then success message appears
		if(request.getSession().getAttribute("type")==null){
			model.addAttribute("type","");
		}else{
			model.addAttribute("type",request.getSession().getAttribute("type"));
			request.getSession().removeAttribute("type");
		}
		//here making provisions for displaying error pages
		if(model.containsAttribute("errorcode")){
			return urlPattern+"/"+"error";
		}else{
			String newUrlPattern=urlPattern+"/edit";
			String modifiedEditUrlPattern=modifyEditUrlPattern(newUrlPattern,request,model,domain.getLocale());
			return modifiedEditUrlPattern;
		}
	}


	@Transactional
	@RequestMapping(value = "/part",method = RequestMethod.PUT)
	public String updatePart(final @Valid @ModelAttribute("domain") Part domain,
			final BindingResult result, final ModelMap model,
			final RedirectAttributes redirectAttributes,
			final HttpServletRequest request) {
		final String servletPath = request.getServletPath().replaceFirst("\\/","");
		String messagePattern=servletPath.replaceAll("\\/",".");
		model.addAttribute("messagePattern", messagePattern);
		model.addAttribute("urlPattern", servletPath);
		domain.setEntryDate(new Date());
		preValidateUpdatePart(domain, result, request);
		validateUpdatePart(domain, result, request);
		model.addAttribute("domain", domain);
		//	        if (result.hasErrors()) {
		//	            /*****Hook*************/
		//	            populateUpdatePartIfErrors(model, domain, request);
		//	            String newUrlPattern=servletPath+"/edit";
		//	            String modifiedEditUrlPattern=modifyEditUrlPattern(newUrlPattern,request,model,domain.getLocale());
		//	            return modifiedEditUrlPattern;            
		//	        }
		/*****Hook*************/
		populateUpdatePartIfNoErrors(model, domain, request);
		/**********************/
		((BaseDomain) domain).merge();
		redirectAttributes.addFlashAttribute("type", "success");
		//this is done so as to remove the bug due to which update message appears even though there
		//is a fresh new/edit request i.e after creating/updating records if we click on
		//new /edit then success message appears
		request.getSession().setAttribute("type","success");
		redirectAttributes.addFlashAttribute("msg", "update_success");
		String returnUrl = "redirect:/" + servletPath + "/"
				+ ((BaseDomain) domain).getId() + "/edit";
		return returnUrl;
	}


	private void preValidateUpdatePart(Part domain, BindingResult result,
			HttpServletRequest request) {
		String strMember=request.getParameter("primaryMember");
		String strProceeding=request.getParameter("proceeding");
		String strReporter=request.getParameter("reporter");
		String strRole=request.getParameter("chairPersonRole");
		String strPrimaryMemberMinistry=request.getParameter("primaryMemberMinistry");
		String strPrimaryMemberDesignation=request.getParameter("primaryMemberDesignation");
		String strSubstituteMember=request.getParameter("substituteMember");
		String strSubstituteMinistry=request.getParameter("substituteMemberMinistry");
		String strSubstituteDesignation=request.getParameter("substituteMemberDesignation");
		String strDeviceType=request.getParameter("deviceType");
		Proceeding proceeding=null;
		if(strProceeding!=null && !strProceeding.equals("")){
			proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			domain.setProceeding(proceeding);
		}
		if(strReporter!=null && !strReporter.equals("")){
			Reporter reporter=Reporter.findById(Reporter.class, Long.parseLong(strReporter));
			domain.setReporter(reporter);
		}
		if(strMember!=null && !strMember.equals("")){
			Member member=Member.findById(Member.class, Long.parseLong(strMember));
			domain.setPrimaryMember(member);
		}
		if(strRole!=null && !strRole.equals("")){
			MemberRole mr=MemberRole.findById(MemberRole.class, Long.parseLong(strRole));
			domain.setChairPersonRole(mr);
			Member member=null;
			if(mr!=null){
				Slot slot=proceeding.getSlot();
				Roster roster=slot.getRoster();
				Session session=roster.getSession();
				House house=session.getHouse();
				List<HouseMemberRoleAssociation> hmras;
				try {
					hmras = HouseMemberRoleAssociation.findActiveHouseMemberRoles(house, mr, new Date(), domain.getLocale());
					for(HouseMemberRoleAssociation h:hmras){
						if(h.getRole().equals(mr)){
							member=h.getMember();
							break;
						}
					}
					domain.setChairPerson(member.getFullname());	
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				domain.setRevisedContent(domain.getProceedingContent());
			}
		}

		if(strPrimaryMemberDesignation!=null && !strPrimaryMemberDesignation.isEmpty()){
			Designation designation=Designation.findById(Designation.class, Long.parseLong(strPrimaryMemberDesignation));
			domain.setPrimaryMemberDesignation(designation);
		}else{
			domain.setPrimaryMemberDesignation(null);
		}
		if(strPrimaryMemberMinistry!=null && !strPrimaryMemberMinistry.isEmpty()){
			Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strPrimaryMemberMinistry));
			domain.setPrimaryMemberMinistry(ministry);
		}

		if(strSubstituteMember!=null && !strSubstituteMember.isEmpty()){
			Member member=Member.findById(Member.class, Long.parseLong(strSubstituteMember));
			domain.setSubstituteMember(member);
		}

		if(strSubstituteDesignation!=null && !strSubstituteDesignation.isEmpty()){
			Designation designation=Designation.findById(Designation.class, Long.parseLong(strSubstituteDesignation));
			domain.setSubstituteMemberDesignation(designation);
		}else{
			domain.setSubstituteMemberDesignation(null);
		}

		if(strSubstituteMinistry!=null && !strSubstituteMinistry.isEmpty()){
			Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strSubstituteMinistry));
			domain.setSubstituteMemberMinistry(ministry);
		}

		if(strDeviceType!=null && !strDeviceType.isEmpty()){
			DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			domain.setDeviceType(deviceType);
		}
	}


	protected void populateUpdatePartIfNoErrors(final ModelMap model,
			final Part domain, final HttpServletRequest request) {

		List<Bookmark> bookmarks=Bookmark.findAllByFieldName(Bookmark.class, "masterPart", domain, "id", "asc", domain.getLocale());
		if(!bookmarks.isEmpty()){
			for(Bookmark b:bookmarks){
				if(domain.getProceedingContent().contains(b.getBookmarkKey())){
					String content=domain.getProceedingContent();
					if(b.getTextToBeReplaced()!=null && !b.getTextToBeReplaced().equals("")){
						String revisedContent=content.replaceAll(b.getBookmarkKey(), b.getTextToBeReplaced());
						domain.setRevisedContent(revisedContent);
					}
				}
			}
		}

	}

	private void validateUpdatePart(final Part domain, final BindingResult result,
			final HttpServletRequest request) {

	}


	@RequestMapping(value="/part/bookmark",method=RequestMethod.GET)
	public String getBookmark(final HttpServletRequest request, final Locale locale,final ModelMap model){
		List<Language> languages=Language.findAll(Language.class, "name", "asc", locale.toString());
		List<Language> mainlanguages=new ArrayList<Language>();
		String strLanguage=request.getParameter("language");
		String strSlot=request.getParameter("currentSlot");
		String strPart=request.getParameter("currentPart");
		String strCount=request.getParameter("count");
		Language language=null;
		if(strLanguage!=null && !strLanguage.equals("")){
			language=Language.findById(Language.class, Long.parseLong(strLanguage));
		}
		for(Language l:languages){
			if(l.getId()!=language.getId()){
				mainlanguages.add(l);
			}
		}
		if(strPart!=null&&!strPart.isEmpty()){
			Part part=Part.findById(Part.class, Long.parseLong(strPart));
			List<Bookmark> bookmarks=Bookmark.findAllByFieldName(Bookmark.class, "masterPart", part, "id", "asc", locale.toString());
			model.addAttribute("bookmarkSize",bookmarks.size());
		}
		model.addAttribute("count", strCount);
		model.addAttribute("languages", languages);
		model.addAttribute("currentSlot", strSlot);		
		return "proceeding/bookmark";
	}


	@RequestMapping(value="/part/bookmark",method=RequestMethod.POST)
	public  @ResponseBody void addBookmark(final HttpServletRequest request, final Locale locale,final ModelMap model){
		String strPreviousText=request.getParameter("previousText");
		String strBookmarkKey=request.getParameter("bookmarkKey");
		String strLanguage=request.getParameter("language");
		String strPart=request.getParameter("masterPart");
		Bookmark bookmark=new Bookmark();
		bookmark.setLocale(locale.toString());
		if(strPart!=null && !strPart.isEmpty()){
			Part part=Part.findById(Part.class, Long.parseLong(strPart));
			bookmark.setMasterPart(part);
		}
		if(strPreviousText!=null && !strPreviousText.equals("")){
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			String server=null;
			String param=null;
			if(customParameter!=null){
				server=customParameter.getValue();
				if(!strPreviousText.isEmpty()){
					if(server.equals("TOMCAT")){
						try {
							param = new String(strPreviousText.getBytes("ISO-8859-1"),"UTF-8");

						}catch (UnsupportedEncodingException e) {
							logger.error("Cannot Encode the Parameter.");
						}
					}
				}
			}
			bookmark.setPreviousText(param);
		}
		if(strLanguage!=null && !strLanguage.equals("")){
			Language language=Language.findByFieldName(Language.class, "type", strLanguage, locale.toString());
			bookmark.setLanguage(language);
		}
		if(strBookmarkKey!=null && !strBookmarkKey.equals("")){
			bookmark.setBookmarkKey(strBookmarkKey);
		}
		bookmark.persist();
	}

	@RequestMapping(value="/part/updatetext",method=RequestMethod.POST)
	public @ResponseBody String updateBookmark(final HttpServletRequest request, final Locale locale,final ModelMap model){
		String strBookmark=request.getParameter("bookmark");
		String strText=request.getParameter("textToBeAdded");
		String strPart=request.getParameter("part");
		String strSlot=request.getParameter("currentSlot");
		if(strBookmark!=null && !strBookmark.equals("")){
			Bookmark bookmark=Bookmark.findById(Bookmark.class, Long.parseLong(strBookmark));
			if(strText!=null && !strText.equals("")){
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				String server=null;
				String param=null;
				if(customParameter!=null){
					server=customParameter.getValue();
					if(!strText.isEmpty()){
						if(server.equals("TOMCAT")){
							try {
								param = new String(strText.getBytes("ISO-8859-1"),"UTF-8");

							}catch (UnsupportedEncodingException e) {
								logger.error("Cannot Encode the Parameter.");
							}
						}
					}
				}
				if(strPart!=null && !strPart.isEmpty()){
					Part part=Part.findById(Part.class, Long.parseLong(strPart));
					bookmark.setSlavePart(part);
				}
				if(strSlot!=null && !strSlot.isEmpty()){
					Slot slot=Slot.findById(Slot.class, Long.parseLong(strSlot));
					bookmark.setSlot(slot);
				}
				bookmark.setBookmarkReplacedBy(bookmark.getSlot().getReporter());
				bookmark.setBookmarkReplacedDate(new Date());
				bookmark.setTextToBeReplaced(param);
				Part masterPart=bookmark.getMasterPart();
				String masterContent=masterPart.getRevisedContent();
				masterContent=masterContent.replace(bookmark.getBookmarkKey(), param);
				masterPart.setRevisedContent(masterContent);
				masterPart.merge();
				bookmark.merge();
				return "success";
			}
		}
		return null;

	}

	@RequestMapping(value="part/citations",method=RequestMethod.GET)
	public String getCitations(final HttpServletRequest request, final Locale locale,
			final ModelMap model){
		String strCounter=request.getParameter("counter");
		List<ProceedingCitation> citations=ProceedingCitation.findAll(ProceedingCitation.class, "id", "asc", locale.toString());
		if(strCounter!=null && !strCounter.isEmpty()){
			model.addAttribute("counter", Integer.parseInt(strCounter));
		}
		model.addAttribute("citations",citations);
		return "proceeding/proceedingcitation";
	}


	@RequestMapping(value="/part/proceedingwiseReport",method=RequestMethod.GET)
	public String getProceedingWiseReport(final HttpServletRequest request, final Locale locale,
			final ModelMap model){
		String pageHeading="पृ शी";
		String mainHeading="मु शी";
		String reporterString="प्रथम";
		String inplaceOf="यांच्या करिता";
		String generalNotice = "(असुधारित प्रत/ प्रसिद्धीसाठी नाही)";
		Language language=null;
		String strProceeding=request.getParameter("proceeding");
		String strLanguage=request.getParameter("language");
		if(strProceeding!=null && !strProceeding.isEmpty()){
			Proceeding proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			if(strLanguage!=null&& !strLanguage.isEmpty()){
				language=Language.findById(Language.class, Long.parseLong(strLanguage));
			}
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("proceedingId", new String[]{proceeding.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_SLOT_WISE_REPORT, parametersMap);		
			model.addAttribute("report", result);
			List<MasterVO> outputFormats = new ArrayList<MasterVO>();
			MasterVO pdfFormat = new MasterVO();
			pdfFormat.setName("PDF");
			pdfFormat.setValue("PDF");
			outputFormats.add(pdfFormat);
			MasterVO wordFormat = new MasterVO();
			wordFormat.setName("WORD");
			wordFormat.setValue("WORD");
			outputFormats.add(wordFormat);	
			model.addAttribute("language",language.getId());
			model.addAttribute("proceeding",proceeding.getId());
			model.addAttribute("outputFormats", outputFormats);
			model.addAttribute("chairpersonText", "अध्याक्षथानी");
			model.addAttribute("mainHeading", mainHeading);
			model.addAttribute("pageHeading",pageHeading);
			model.addAttribute("reporterString",reporterString);
			model.addAttribute("inplaceOf", inplaceOf);
			model.addAttribute("generalNotice", generalNotice);
			return "proceeding/part/proceedingwisereport";
		}
		return null;
	}



	/**********Report Related******************/
	@RequestMapping(value="/rosterwisereport",method=RequestMethod.GET)
	public String mergeProceedingContentReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){

		String retVal= "proceeding/error";
		String generalNotice = "(असुधारित प्रत/ प्रसिद्धीसाठी नाही)";
		String pageHeading="पृ शी";
		String mainHeading="मु शी";
		String inplaceOf="यांच्या करिता";
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("rosterId", new String[]{roster.getId().toString()});
			List result=Query.findReport(ApplicationConstants.PROCEEDING_CONTENT_MERGE_REPORT, parametersMap);		
			for(int i=0;i<result.size();i++){
				Object[] row = (Object[]) result.get(i);
				if(row[14]!=null){
					Member member=Member.findById(Member.class, Long.parseLong(row[14].toString()));
					List<HouseMemberRoleAssociation> hrma=member.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[15]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
			}
			model.addAttribute("generalNotice", generalNotice);
			model.addAttribute("mainHeading", mainHeading);
			model.addAttribute("pageHeading",pageHeading);
			model.addAttribute("inplaceOf", inplaceOf);
			model.addAttribute("report", result);
			List<MasterVO> outputFormats = new ArrayList<MasterVO>();
			MasterVO pdfFormat = new MasterVO();
			pdfFormat.setName("PDF");
			pdfFormat.setValue("PDF");
			outputFormats.add(pdfFormat);
			MasterVO wordFormat = new MasterVO();
			wordFormat.setName("WORD");
			wordFormat.setValue("WORD");
			outputFormats.add(wordFormat);									
			model.addAttribute("outputFormats", outputFormats);

			retVal = "proceeding/dailyproceedingreport";
		}

		return retVal;
	}

	@RequestMapping(value="/viewreport",method=RequestMethod.GET)
	public @ResponseBody void viewProceedingReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String reportFormat=request.getParameter("outputFormat");
		File reportFile = null;

		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("rosterId", new String[]{roster.getId().toString()});
			List result=Query.findReport(ApplicationConstants.PROCEEDING_CONTENT_MERGE_REPORT, parametersMap);		
			List<Object> objects=new ArrayList<Object>();
			List<Object> tempList1=new ArrayList<Object>();
			List<Object> tempList2=new ArrayList<Object>();
			List<Object> tempList3=new ArrayList<Object>();
			for(int a=0;a<result.size();a++){
				Object[] row = (Object[]) result.get(a);
				/****If the Member who is speaking is Speaker/Chairman/Depy Speaker/Chief Minister The Membername is replaced by 
				 their memberrole*****/
				if(row[14]!=null){
					Member member=Member.findById(Member.class, Long.parseLong(row[14].toString()));
					List<HouseMemberRoleAssociation> hrma=member.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[15]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
				/***If same person is chairperson for each part then no need to display the chairperson on each part...only display on first part*****/
				for(int b=a+1;b<result.size();b++){

					Object[] row1 = (Object[]) result.get(b);
					if(row1[3]!=null &&row[3]!=null){
						if(row[3].equals(row1[3])){
							row1[3]=null;
							row1[9]=null;
						}
					}

				}
				tempList1.add(row);
			}

			/****If the parts are of same slot then concat the paragraphs of same member under the member name instead of having membername in each part****/
			for(int i=0; i<tempList1.size(); i++){
				Object[] row = (Object[]) tempList1.get(i);
				if(row[14]!=null){
					row[0]="<b>"+row[15]+" : </b>"+row[0];
				}
				int j=i+1;
				for(;j<tempList1.size();j++){
					Object[] row1 = (Object[]) tempList1.get(j);
					if(row[6].equals(row1[6])){
						if(row[1]!=row1[1] && row[2]!=row1[2]){
							break;
						}else{
							if(row1[14]!=null){
								if(row1[10]!=null){
									if(row1[11]!=null){
										if(row1[16]!=null){
											if(row1[12]!=null){
												if(row1[13]!=null){
													row[0]=row[0].toString()+"<p><b>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"
															+" ,"+row1[17]+" ( "+row1[13]+" "+row1[12]+" ) "+"यांच्या करिता"+": </b>"+row1[0].toString()+"</p>";
												}else{
													row[0]=row[0].toString()+"<p><b>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"
															+" ,"+row1[17]+" ( "+row1[12]+" ) "+"यांच्या करिता"+":</b> "+row1[0].toString()+"</p>";
												}
											}else{
												row[0]=row[0].toString()+"<p><b>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"
														+" ,"+row1[17]+" यांच्या करिता"+":</b> "+row1[0].toString()+"</p>";
											}
										}else{
											row[0]=row[0].toString()+"<p><b>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"+
													":</b> "+row1[0].toString()+"</p>";
										}
									}else{

										row[0]=row[0].toString()+"<p><b>"+row1[15].toString()+" ( "+row1[10]+")"+
												":</b> "+row1[0].toString()+"</p>";
									}
								}else{
									if(row1[16]!=null){
										if(row1[12]!=null){
											if(row1[13]!=null){
												row[0]=row[0].toString()+"<p><b>"+row1[15].toString()+" ,"+row1[17]+" ( "+row1[13]+" "+row1[12]+" ) "+"यांच्या करिता"+":</b> "+row1[0].toString()+"</p>";
											}else{
												row[0]=row[0].toString()+"<p><b>"+row1[15].toString()
														+" ,"+row1[17]+" ( "+row1[12]+" ) "+"यांच्या करिता"+":</b> "+row1[0].toString()+"</p>";
											}
										}else{
											row[0]=row[0].toString()+"<p><b>"+row1[15].toString()
													+" ,"+row1[17]+" यांच्या करिता"+": </b>"+row1[0].toString()+"</p>";
										}
									}else{
										row[0]=row[0].toString()+"<p><b>"+row1[15].toString()+	" :</b> "+row1[0].toString()+"</p>";
									}

								}
							}else{
								if(row1[4]!=null){
									row[0]=row[0].toString()+"<p><b>"+row1[4]+" ( "+row1[5]+" ) :</b>"+row1[0]+"</p>";
								}else{
									row[0]=row[0].toString()+"<p>"+row1[0]+"</p>";
								}
							}
							i=i+1;
						}

					}else{
						break;
					}
				}
				tempList2.add(row);
			}
			/****inserting the array of objects with more capacity to another list inorder to add the previous reporter name 
			 * and next reporter name ****/

			for(int i=0;i<tempList2.size();i++){
				Object[] row1=(Object[]) tempList2.get(i);
				Object[] row=new Object[row1.length+2];
				for(int j=0;j<row1.length;j++){
					row[j]=row1[j];
				}
				tempList3.add(row);
			}

			/****Adding the next reporter name to the array****/
			for(int i=0;i<tempList3.size();i++){
				Object[] row=(Object[]) tempList3.get(i);
				for(int j=i+1;j<tempList3.size();j++){
					Object[] row1=(Object[]) tempList3.get(j);
					if(row1[6]!=row[6]){
						row[20]=row1[18];
						break;
					}
				}

				/****Adding the previous reporter name ****/
				for(int k=i-1;k>=0;k--){
					Object[] row2=(Object[]) tempList3.get(k);
					if(row2[6]!=row[6]){
						if(row2[10]!=null){
							row[21]=row2[18];
							break;
						}
					}
				}
				objects.add(row);
			}
			Object[] xmlData=new Object[]{objects};

			if(!result.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(xmlData, "template_ris_proceeding_content_merge_report_word", reportFormat, "karyavrutt" , locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(xmlData, "template_ris_proceeding_content_merge_report_word", reportFormat, "karyavrutt", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				xmlData = null;
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}

	@RequestMapping(value="/sessionwisereport",method=RequestMethod.GET)
	public @ResponseBody void viewProceedingSessionwiseReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String reportFormat=request.getParameter("outputFormat");
		File reportFile = null;

		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("sessionId", new String[]{session.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_SESSION_WISE_REPORT, parametersMap);		
			List<Object> objects=new ArrayList<Object>();
			List<Object> tempList1=new ArrayList<Object>();
			List<Object> tempList2=new ArrayList<Object>();
			List<Object> tempList3=new ArrayList<Object>();
			for(int a=0;a<result.size();a++){
				Object[] row = (Object[]) result.get(a);
				/****If the Member who is speaking is Speaker/Chairman/Depy Speaker/Chief Minister The Membername is replaced by 
				 their memberrole*****/
				if(row[14]!=null){
					Member member=Member.findById(Member.class, Long.parseLong(row[14].toString()));
					List<HouseMemberRoleAssociation> hrma=member.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[15]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
				/***If same person is chairperson for each part then no need to display the chairperson on each part...only display on first part*****/
				for(int b=a+1;b<result.size();b++){

					Object[] row1 = (Object[]) result.get(b);
					if(row1[3]!=null &&row[3]!=null){
						if(row[3].equals(row1[3])){
							row1[3]=null;
							row1[9]=null;
						}
					}

				}
				tempList1.add(row);
			}

			/****If the parts are of same slot then concat the paragraphs of same member under the member name instead of having membername in each part****/
			for(int i=0; i<tempList1.size(); i++){
				Object[] row = (Object[]) tempList1.get(i);
				int j=i+1;
				for(;j<tempList1.size();j++){
					Object[] row1 = (Object[]) tempList1.get(j);
					if(row[6].equals(row1[6])){
						if(row[1]!=row1[1] && row[2]!=row1[2]){
							break;
						}else{
							if(row1[14]!=null){
								if(row1[10]!=null){
									if(row1[11]!=null){
										if(row1[16]!=null){
											if(row1[12]!=null){
												if(row1[13]!=null){
													row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"
															+" ,"+row1[17]+" ( "+row1[13]+" "+row1[12]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
												}else{
													row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"
															+" ,"+row1[17]+" ( "+row1[12]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
												}
											}else{
												row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"
														+" ,"+row1[17]+" यांच्या करिता"+": "+row1[0].toString()+"</p>";
											}
										}else{
											row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"+
													": "+row1[0].toString()+"</p>";
										}
									}else{

										row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[10]+")"+
												": "+row1[0].toString()+"</p>";
									}
								}else{
									if(row1[16]!=null){
										if(row1[12]!=null){
											if(row1[13]!=null){
												row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ,"+row1[17]+" ( "+row1[13]+" "+row1[12]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
											}else{
												row[0]=row[0].toString()+"<p>"+row1[15].toString()
														+" ,"+row1[17]+" ( "+row1[12]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
											}
										}else{
											row[0]=row[0].toString()+"<p>"+row1[15].toString()
													+" ,"+row1[17]+" यांच्या करिता"+": "+row1[0].toString()+"</p>";
										}
									}else{
										row[0]=row[0].toString()+"<p>"+row1[15].toString()+	" : "+row1[0].toString()+"</p>";
									}

								}
							}else{
								if(row1[4]!=null){
									row[0]=row[0].toString()+"<p>"+row1[4]+" ( "+row1[5]+" )"+row1[0]+"</p>";
								}else{
									row[0]=row[0].toString()+"<p>"+row1[0]+"</p>";
								}
							}
							i=i+1;
						}

					}else{
						break;
					}
				}
				tempList2.add(row);
			}
			/****inserting the array of objects with more capacity to another list inorder to add the previous reporter name 
			 * and next reporter name ****/

			for(int i=0;i<tempList2.size();i++){
				Object[] row1=(Object[]) tempList2.get(i);
				Object[] row=new Object[row1.length+2];
				for(int j=0;j<row1.length;j++){
					row[j]=row1[j];
				}
				tempList3.add(row);
			}

			/****Adding the next reporter name to the array****/
			for(int i=0;i<tempList3.size();i++){
				Object[] row=(Object[]) tempList3.get(i);
				for(int j=i+1;j<tempList3.size();j++){
					Object[] row1=(Object[]) tempList3.get(j);
					if(row1[6]!=row[6]){
						row[21]=row1[18];
						break;
					}
				}

				/****Adding the previous reporter name ****/
				for(int k=i-1;k>=0;k--){
					Object[] row2=(Object[]) tempList3.get(k);
					if(row2[6]!=row[6]){
						if(row2[10]!=null){
							row[22]=row2[18];
							break;
						}
					}
				}
				objects.add(row);
			}
			Object[] xmlData=new Object[]{objects};

			if(!result.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(xmlData, "template_ris_sessionwise_report", reportFormat, "karyavrutt" , locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(xmlData, "template_ris_sessionwise_report", reportFormat, "karyavrutt", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				xmlData = null;
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}


	@RequestMapping(value="/reporterwisereport",method=RequestMethod.GET)
	public String reporterwiseProceedingContent(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){

		String retVal= "proceeding/error";
		String generalNotice = "(असुधारित प्रत/ प्रसिद्धीसाठी नाही)";
		String pageHeading="पृ शी";
		String mainHeading="मु शी";
		String inplaceOf="यांच्या करिता";
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String strUser=request.getParameter("user");
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("")&&
				strUser!=null && !strUser.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			User user=User.findById(User.class, Long.parseLong(strUser));
			Reporter reporter= Roster.findByUser(roster, user);
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("rosterId", new String[]{roster.getId().toString()});
			parametersMap.put("reporterId",new String[]{reporter.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_REPORTER_WISE_REPORT, parametersMap);		
			model.addAttribute("generalNotice", generalNotice);
			model.addAttribute("mainHeading", mainHeading);
			model.addAttribute("pageHeading",pageHeading);
			model.addAttribute("inplaceOf", inplaceOf);
			model.addAttribute("report", result);
			List<MasterVO> outputFormats = new ArrayList<MasterVO>();
			MasterVO pdfFormat = new MasterVO();
			pdfFormat.setName("PDF");
			pdfFormat.setValue("PDF");
			outputFormats.add(pdfFormat);
			MasterVO wordFormat = new MasterVO();
			wordFormat.setName("WORD");
			wordFormat.setValue("WORD");
			outputFormats.add(wordFormat);									
			model.addAttribute("outputFormats", outputFormats);

			retVal = "proceeding/dailyreporterwiseproceeding";
		}

		return retVal;
	}

	@RequestMapping(value="/reporterwiseproceeding",method=RequestMethod.GET)
	public @ResponseBody void getReporterwiseProceedingReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String reportFormat=request.getParameter("outputFormat");
		File reportFile = null;

		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			User user=User.findById(User.class, this.getCurrentUser().getUserId());
			Reporter reporter=Roster.findByUser(roster, user);
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("rosterId", new String[]{roster.getId().toString()});
			parametersMap.put("reporterId",new String[]{reporter.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_REPORTER_WISE_REPORT, parametersMap);		
			List<Object> objects=new ArrayList<Object>();
			List<Object> tempList1=new ArrayList<Object>();
			List<Object> tempList2=new ArrayList<Object>();
			List<Object> tempList3=new ArrayList<Object>();
			for(int a=0;a<result.size();a++){
				Object[] row = (Object[]) result.get(a);
				/****If the Member who is speaking is Speaker/Chairman/Depy Speaker/Chief Minister The Membername is replaced by 
				 their memberrole*****/
				if(row[14]!=null){
					Member member=Member.findById(Member.class, Long.parseLong(row[14].toString()));
					List<HouseMemberRoleAssociation> hrma=member.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[15]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
				/***If same person is chairperson for each part then no need to display the chairperson on each part...only display on first part*****/
				for(int b=a+1;b<result.size();b++){

					Object[] row1 = (Object[]) result.get(b);
					if(row1[3]!=null &&row[3]!=null){
						if(row[3].equals(row1[3])){
							row1[3]=null;
							row1[9]=null;
						}
					}

				}
				tempList1.add(row);
			}

			/****If the parts are of same slot then concat the paragraphs of same member under the member name instead of having membername in each part****/
			for(int i=0; i<tempList1.size(); i++){
				Object[] row = (Object[]) tempList1.get(i);
				int j=i+1;
				for(;j<tempList1.size();j++){
					Object[] row1 = (Object[]) tempList1.get(j);
					if(row[6].equals(row1[6])){
						if(row[1]!=row1[1] && row[2]!=row1[2]){
							break;
						}else{
							if(row1[14]!=null){
								if(row1[10]!=null){
									if(row1[11]!=null){
										if(row1[16]!=null){
											if(row1[12]!=null){
												if(row1[13]!=null){
													row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"
															+" ,"+row1[17]+" ( "+row1[13]+" "+row1[12]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
												}else{
													row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"
															+" ,"+row1[17]+" ( "+row1[12]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
												}
											}else{
												row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"
														+" ,"+row1[17]+" यांच्या करिता"+": "+row1[0].toString()+"</p>";
											}
										}else{
											row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"+
													": "+row1[0].toString()+"</p>";
										}
									}else{

										row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[10]+")"+
												": "+row1[0].toString()+"</p>";
									}
								}else{
									if(row1[16]!=null){
										if(row1[12]!=null){
											if(row1[13]!=null){
												row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ,"+row1[17]+" ( "+row1[13]+" "+row1[12]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
											}else{
												row[0]=row[0].toString()+"<p>"+row1[15].toString()
														+" ,"+row1[17]+" ( "+row1[12]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
											}
										}else{
											row[0]=row[0].toString()+"<p>"+row1[15].toString()
													+" ,"+row1[17]+" यांच्या करिता"+": "+row1[0].toString()+"</p>";
										}
									}else{
										row[0]=row[0].toString()+"<p>"+row1[15].toString()+	" : "+row1[0].toString()+"</p>";
									}

								}
							}else{
								if(row1[4]!=null){
									row[0]=row[0].toString()+"<p>"+row1[4]+" ( "+row1[5]+" )"+row1[0]+"</p>";
								}else{
									row[0]=row[0].toString()+"<p>"+row1[0]+"</p>";
								}
							}
							i=i+1;
						}

					}else{
						break;
					}
				}
				tempList2.add(row);
			}
			/****inserting the array of objects with more capacity to another list inorder to add the previous reporter name 
			 * and next reporter name ****/

			for(int i=0;i<tempList2.size();i++){
				Object[] row1=(Object[]) tempList2.get(i);
				Object[] row=new Object[row1.length+2];
				for(int j=0;j<row1.length;j++){
					row[j]=row1[j];
				}
				tempList3.add(row);
			}

			/****Adding the next reporter name to the array****/
			for(int i=0;i<tempList3.size();i++){
				Object[] row=(Object[]) tempList3.get(i);
				for(int j=i+1;j<tempList3.size();j++){
					Object[] row1=(Object[]) tempList3.get(j);
					if(row1[6]!=row[6]){
						row[20]=row1[18];
						break;
					}
				}

				/****Adding the previous reporter name ****/
				for(int k=i-1;k>=0;k--){
					Object[] row2=(Object[]) tempList3.get(k);
					if(row2[6]!=row[6]){
						if(row2[10]!=null){
							row[21]=row2[18];
							break;
						}
					}
				}
				objects.add(row);
			}
			Object[] xmlData=new Object[]{objects};

			if(!result.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(xmlData, "template_ris_proceeding_content_merge_report_word", reportFormat, "karyavrutt" , locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(xmlData, "template_ris_proceeding_content_merge_report_word", reportFormat, "karyavrutt", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				xmlData = null;
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}


	@RequestMapping(value="/proceedingwisereport",method=RequestMethod.GET)
	public @ResponseBody void viewProceedingWiseReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		String strProceeding=request.getParameter("proceeding");
		String reportFormat=request.getParameter("outputFormat");
		String strLanguage=request.getParameter("language");

		File reportFile = null;

		if(strProceeding!=null && !strProceeding.isEmpty()
				&& strLanguage!=null && !strLanguage.isEmpty()){
			Proceeding proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			Slot slot=proceeding.getSlot();
			Roster roster=slot.getRoster();
			Session session=roster.getSession();
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("proceedingId", new String[]{proceeding.getId().toString()});
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_SLOT_WISE_REPORT, parametersMap);		
			List<Object> objects=new ArrayList<Object>();
			List<Object> tempList1=new ArrayList<Object>();
			List<Object> tempList2=new ArrayList<Object>();
			List<Object> tempList3=new ArrayList<Object>();
			for(int a=0;a<result.size();a++){
				Object[] row = (Object[]) result.get(a);
				/****If the Member who is speaking is Speaker/Chairman/Depy Speaker/Chief Minister The Membername is replaced by 
				 their memberrole*****/
				if(row[14]!=null){
					Member member=Member.findById(Member.class, Long.parseLong(row[14].toString()));
					List<HouseMemberRoleAssociation> hrma=member.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[15]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
				/***If same person is chairperson for each part then no need to display the chairperson on each part...only display on first part*****/
				for(int b=a+1;b<result.size();b++){

					Object[] row1 = (Object[]) result.get(b);
					if(row1[3]!=null &&row[3]!=null){
						if(row[3].equals(row1[3])){
							row1[3]=null;
							row1[9]=null;
						}
					}

				}
				tempList1.add(row);
			}

			/****If the parts are of same slot then concat the paragraphs of same member under the member name instead of having membername in each part****/
			for(int i=0; i<tempList1.size(); i++){
				Object[] row = (Object[]) tempList1.get(i);
				int j=i+1;
				for(;j<tempList1.size();j++){
					Object[] row1 = (Object[]) tempList1.get(j);
					if(row[6].equals(row1[6])){
						if(row[1]!=row1[1] && row[2]!=row1[2]){
							break;
						}else{
							if(row1[14]!=null){
								if(row1[10]!=null){
									if(row1[11]!=null){
										if(row1[16]!=null){
											if(row1[12]!=null){
												if(row1[13]!=null){
													row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"
															+" ,"+row1[17]+" ( "+row1[13]+" "+row1[12]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
												}else{
													row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"
															+" ,"+row1[17]+" ( "+row1[12]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
												}
											}else{
												row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"
														+" ,"+row1[17]+" यांच्या करिता"+": "+row1[0].toString()+"</p>";
											}
										}else{
											row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[11]+""+row1[10]+")"+
													": "+row1[0].toString()+"</p>";
										}
									}else{

										row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ( "+row1[10]+")"+
												": "+row1[0].toString()+"</p>";
									}
								}else{
									if(row1[16]!=null){
										if(row1[12]!=null){
											if(row1[13]!=null){
												row[0]=row[0].toString()+"<p>"+row1[15].toString()+" ,"+row1[17]+" ( "+row1[13]+" "+row1[12]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
											}else{
												row[0]=row[0].toString()+"<p>"+row1[15].toString()
														+" ,"+row1[17]+" ( "+row1[12]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
											}
										}else{
											row[0]=row[0].toString()+"<p>"+row1[15].toString()
													+" ,"+row1[17]+" यांच्या करिता"+": "+row1[0].toString()+"</p>";
										}
									}else{
										row[0]=row[0].toString()+"<p>"+row1[15].toString()+	" : "+row1[0].toString()+"</p>";
									}

								}
							}else{
								if(row1[4]!=null){
									row[0]=row[0].toString()+"<p>"+row1[4]+" ( "+row1[5]+" )"+row1[0]+"</p>";
								}else{
									row[0]=row[0].toString()+"<p>"+row1[0]+"</p>";
								}
							}
							i=i+1;
						}

					}else{
						break;
					}
				}
				tempList2.add(row);
			}
			/****inserting the array of objects with more capacity to another list inorder to add the previous reporter name 
			 * and next reporter name ****/

			for(int i=0;i<tempList2.size();i++){
				Object[] row1=(Object[]) tempList2.get(i);
				Object[] row=new Object[row1.length+2];
				for(int j=0;j<row1.length;j++){
					row[j]=row1[j];
				}
				tempList3.add(row);
			}

			/****Adding the next reporter name to the array****/
			for(int i=0;i<tempList3.size();i++){
				Object[] row=(Object[]) tempList3.get(i);
				for(int j=i+1;j<tempList3.size();j++){
					Object[] row1=(Object[]) tempList3.get(j);
					if(row1[6]!=row[6]){
						row[20]=row1[18];
						break;
					}
				}

				/****Adding the previous reporter name ****/
				for(int k=i-1;k>=0;k--){
					Object[] row2=(Object[]) tempList3.get(k);
					if(row2[6]!=row[6]){
						if(row2[10]!=null){
							row[21]=row2[18];
							break;
						}
					}
				}
				objects.add(row);
			}
			Object[] xmlData=new Object[]{objects};

			if(!result.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(xmlData, "template_ris_proceeding_content_merge_report_word", reportFormat, "karyavrutt" , locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(xmlData, "template_ris_proceeding_content_merge_report_word", reportFormat, "karyavrutt", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				xmlData = null;
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}


	@RequestMapping(value="/memberwisereport",method=RequestMethod.GET)
	public String getMemberWiseReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){

		String retVal= "proceeding/error";
		String generalNotice = "(असुधारित प्रत/ प्रसिद्धीसाठी नाही)";
		String pageHeading="पृ शी";
		String mainHeading="मु शी";
		String inplaceOf="यांच्या करिता";
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String strMember=request.getParameter("member");
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("") &&
				strMember!=null && !strMember.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			Member member=Member.findById(Member.class, Long.parseLong(strMember));
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("rosterId", new String[]{roster.getId().toString()});
			parametersMap.put("memberId", new String[]{member.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_MEMBER_WISE_REPORT, parametersMap);		
			model.addAttribute("generalNotice", generalNotice);
			model.addAttribute("mainHeading", mainHeading);
			model.addAttribute("pageHeading",pageHeading);
			model.addAttribute("inplaceOf", inplaceOf);
			model.addAttribute("member",member.getId());
			model.addAttribute("report", result);
			List<MasterVO> outputFormats = new ArrayList<MasterVO>();
			MasterVO pdfFormat = new MasterVO();
			pdfFormat.setName("PDF");
			pdfFormat.setValue("PDF");
			outputFormats.add(pdfFormat);
			MasterVO wordFormat = new MasterVO();
			wordFormat.setName("WORD");
			wordFormat.setValue("WORD");
			outputFormats.add(wordFormat);									
			model.addAttribute("outputFormats", outputFormats);

			retVal = "proceeding/dailymemberwiseproceeding";
		}

		return retVal;
	}


	@RequestMapping(value="/memberwiseproceeding",method=RequestMethod.GET)
	public @ResponseBody void viewMemberWiseReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		String reportFormat=request.getParameter("outputFormat");
		File reportFile = null;

		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String strMember=request.getParameter("member");
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("") &&
				strMember!=null && !strMember.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			Member member=Member.findById(Member.class, Long.parseLong(strMember));
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("rosterId", new String[]{roster.getId().toString()});
			parametersMap.put("memberId", new String[]{member.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_MEMBER_WISE_REPORT, parametersMap);	
			List<Object> tempList2=new ArrayList<Object>();
			for(int i=0; i<result.size(); i++){
				Object[] row = (Object[]) result.get(i);
				if(row[10]!=null){
					row[0]="<b>"+row[10]+"</b>"+row[0];
				}
				
				int j=i+1;
				for(;j<result.size();j++){
					Object[] row1 = (Object[]) result.get(j);
					if(row[1]!=row1[1] && row[2]!=row1[2]){
						break;
					}else{
					if(row1[9]!=null){
						if(row1[5]!=null){
							if(row1[6]!=null){
								if(row1[11]!=null){
									if(row1[7]!=null){
										if(row1[8]!=null){
											row[0]=row[0].toString()+"<p>"+row1[10].toString()+" ( "+row1[6]+""+row1[5]+")"
													+" ,"+row1[12]+" ( "+row1[8]+" "+row1[7]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
										}else{
											row[0]=row[0].toString()+"<p>"+row1[10].toString()+" ( "+row1[6]+""+row1[5]+")"
													+" ,"+row1[12]+" ( "+row1[7]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
										}
									}else{
										row[0]=row[0].toString()+"<p>"+row1[10].toString()+" ( "+row1[6]+""+row1[5]+")"
												+" ,"+row1[12]+" यांच्या करिता"+": "+row1[0].toString()+"</p>";
									}
								}else{
									row[0]=row[0].toString()+"<p>"+row1[10].toString()+" ( "+row1[6]+""+row1[5]+")"+
											": "+row1[0].toString()+"</p>";
								}
							}else{

								row[0]=row[0].toString()+"<p>"+row1[10].toString()+" ( "+row1[5]+")"+
										": "+row1[0].toString()+"</p>";
							}
						}else{
							if(row1[11]!=null){
								if(row1[7]!=null){
									if(row1[8]!=null){
										row[0]=row[0].toString()+"<p>"+row1[10].toString()+" ,"+row1[12]+" ( "+row1[8]+" "+row1[7]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
									}else{
										row[0]=row[0].toString()+"<p>"+row1[10].toString()
												+" ,"+row1[12]+" ( "+row1[7]+" ) "+"यांच्या करिता"+": "+row1[0].toString()+"</p>";
									}
								}else{
									row[0]=row[0].toString()+"<p>"+row1[10].toString()
											+" ,"+row1[12]+" यांच्या करिता"+": "+row1[0].toString()+"</p>";
								}
							}else{
								row[0]=row[0].toString()+"<p>"+row1[10].toString()+	" : "+row1[0].toString()+"</p>";
							}
						}
					}
					i=i+1;
				}
				}
				tempList2.add(row);
			}
			
			Object[] xmlData=new Object[]{tempList2};

			if(!result.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(xmlData, "ris_proceeding_content_memberwise_report", reportFormat, "karyavrutt" , locale.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(xmlData, "ris_proceeding_content_memberwise_report", reportFormat, "karyavrutt", locale.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				xmlData = null;
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}
}
