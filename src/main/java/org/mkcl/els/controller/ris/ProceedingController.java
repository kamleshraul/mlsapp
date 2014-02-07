package org.mkcl.els.controller.ris;

import java.io.File;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.ChildVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ParentVO;
import org.mkcl.els.common.vo.PartDraftVO;
import org.mkcl.els.common.xmlvo.ProceedingXMLVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.Bookmark;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.Constituency;
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
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Part;
import org.mkcl.els.domain.PartDraft;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.Proceeding;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Reporter;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Slot;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
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
		List<Language> languages = new ArrayList<Language>();
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
			
	}

	@Override
	protected void populateEdit(final ModelMap model, final Proceeding domain,
			final HttpServletRequest request) {
		/****Ministries****/
		if(domain.getSlot()!=null){
			Slot slot=domain.getSlot();
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
		}
		/****slot****/
		String strHouseType=this.getCurrentUser().getHouseType();
		HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, domain.getLocale());
		model.addAttribute("slot", domain.getSlot().getId());
		model.addAttribute("slotName",domain.getSlot().getName());
		
		/****Members****/
		List<Member> members=Member.findAll(Member.class, "firstName", "asc", domain.getLocale());
		model.addAttribute("members",members);
		
		/****MemberRoles****/
		List<MemberRole> roles=MemberRole.findAllByFieldName(MemberRole.class, "houseType", houseType, "name", "asc", domain.getLocale());
		model.addAttribute("roles", roles);
		
		/****Designation****/
		List<Designation> designations=Designation.findAll(Designation.class, "name", "asc", domain.getLocale());
		model.addAttribute("designations",designations);
		
		
		/****SubDepartments****/
		List<SubDepartment> subDepartments=SubDepartment.findAll(SubDepartment.class, "name", "asc", domain.getLocale());
		model.addAttribute("subDepartments",subDepartments);
		/****DeviceType****/
		List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name", "desc", domain.getLocale());
		model.addAttribute("deviceTypes", deviceTypes);
		
		/****Parts****/
		List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", domain, "orderNo", "asc", domain.getLocale());
		model.addAttribute("parts", parts);
		if(parts.isEmpty()){
			model.addAttribute("partCount", 0);
		}else{
			model.addAttribute("partCount", domain.getParts().size());
		}
		
		/****Bookmarks****/
		Slot slot=domain.getSlot();
		List<Bookmark> bk=new ArrayList<Bookmark>();
		List<Bookmark> bookmarks=Bookmark.findAllByFieldName(Bookmark.class, "slot", slot, "bookmarkKey", "asc", domain.getLocale());
		//if(bookmarks.isEmpty()){
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
			if(!bookmarks.isEmpty()){
				bk.addAll(bookmarks);
			}
		model.addAttribute("bookmarks", bk);
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
			/****PrimaryMember****/
			String strMember=request.getParameter("primaryMember"+i);
			if(strMember!=null && !strMember.equals("")){
				Member member=Member.findById(Member.class, Long.parseLong(strMember));
				part.setPrimaryMember(member);
			}
			/****Part Id****/
			String id=request.getParameter("partId"+ i);
			if(id!=null){
				if(!id.isEmpty()){
					part.setId(Long.parseLong(id));
				}
			}

			/****OrderNo****/
			String order=request.getParameter("order"+ i);
			if(order!=null){
				if(!order.isEmpty()){
					part.setOrderNo(Integer.parseInt(order));
				}
			}

			/****Part Version****/
			String version=request.getParameter("partVersion"+ i);
			if(version!=null){
				if(!version.isEmpty()){
					part.setVersion(Long.parseLong(version));
				}
			}

			/****Part locale****/
			String locale=request.getParameter("partLocale"+ i);
			if(locale!=null){
				if(!locale.isEmpty()){
					part.setLocale(locale);
				}
			}
			
			/****Main Heading****/
			String mainHeading=request.getParameter("mainHeading"+i);
			if(mainHeading!=null&&!mainHeading.isEmpty()){
				part.setMainHeading(mainHeading);
			}
			
			/****Page Heading****/
			String pageHeading=request.getParameter("pageHeading"+i);
			if(pageHeading!=null && !pageHeading.isEmpty()){
				part.setPageHeading(pageHeading);
			}
			/*String chairPerson=request.getParameter("chairPerson"+i);
			if(chairPerson!=null&& !chairPerson.isEmpty()){
				part.setChairPerson(chairPerson);
			}*/
			/****Member role and Chairperson****/
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
						part.setChairPerson(chairPersonMember.getFullname());
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					part.setChairPersonRole(mr);
				}
			}
			
			/****Proceeding Content****/
			String content=request.getParameter("content"+i);
			if(content!=null && !content.isEmpty()){
				part.setProceedingContent(content);
				part.setRevisedContent(content);
			}
			
			/****Proceeding Id****/
			String strProceeding=request.getParameter("partProceeding"+i);
			if(strProceeding!=null && !strProceeding.isEmpty()){
				Proceeding proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
				part.setProceeding(proceeding);
			}
			
			/****PrimaryMember Ministry****/
			String strPrimaryMemberMinistry=request.getParameter("primaryMemberMinistry"+i);
			if(strPrimaryMemberMinistry!=null && !strPrimaryMemberMinistry.isEmpty()){
				Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strPrimaryMemberMinistry));
				part.setPrimaryMemberMinistry(ministry);
			}
			
			/****PrimaryMember Designation****/
			String strPrimaryMemberDesignation=request.getParameter("primaryMemberDesignation"+i);
			if(strPrimaryMemberDesignation!=null && !strPrimaryMemberDesignation.isEmpty()){
				Designation designation=Designation.findById(Designation.class, Long.parseLong(strPrimaryMemberDesignation));
				part.setPrimaryMemberDesignation(designation);
			}
			
			String strPrimaryMemberSubDepartment=request.getParameter("primaryMemberSubDepartment"+i);
			if(strPrimaryMemberSubDepartment!=null && !strPrimaryMemberSubDepartment.isEmpty()){
				SubDepartment subDepartment=SubDepartment.findById(SubDepartment.class, Long.parseLong(strPrimaryMemberSubDepartment));
				part.setPrimaryMemberSubDepartment(subDepartment);
			}
			
			/****Substitute Member****/
			String strSubstituteMember=request.getParameter("substituteMember"+i);
			if(strSubstituteMember!=null && !strSubstituteMember.isEmpty()){
				Member member=Member.findById(Member.class, Long.parseLong(strSubstituteMember));
				part.setSubstituteMember(member);
			}
			
			/****Substitute Member Ministry****/
			String strSubstituteMemberMinistry=request.getParameter("substituteMemberMinistry"+i);
			if(strSubstituteMemberMinistry!=null && !strSubstituteMemberMinistry.isEmpty()){
				Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strSubstituteMemberMinistry));
				part.setSubstituteMemberMinistry(ministry);
			}
			
			/****Substitute Member Designation****/
			String strSubstituteMemberDesignation=request.getParameter("substituteMemberDesignation"+i);
			if(strSubstituteMemberDesignation!=null && !strSubstituteMemberDesignation.isEmpty()){
				Designation designation=Designation.findById(Designation.class, Long.parseLong(strSubstituteMemberDesignation));
				part.setSubstituteMemberDesignation(designation);
			}
			
			String strSubstituteMemberSubDepartment=request.getParameter("substituteMemberSubDepartment"+i);
			if(strSubstituteMemberSubDepartment!=null && !strSubstituteMemberSubDepartment.isEmpty()){
				SubDepartment subDepartment=SubDepartment.findById(SubDepartment.class, Long.parseLong(strSubstituteMemberSubDepartment));
				part.setSubstituteMemberSubDepartment(subDepartment);
			}
			
			/****Public Representative****/
			String strPublicRepresentative=request.getParameter("publicRepresentative"+i);
			if(strPublicRepresentative!=null && !strPublicRepresentative.isEmpty()){
				part.setPublicRepresentative(strPublicRepresentative);
			}

			/****Public Representative Detail****/
			String strPublicRepresentativeDetail=request.getParameter("publicRepresentativeDetail"+i);
			if(strPublicRepresentativeDetail!=null && !strPublicRepresentativeDetail.isEmpty()){
				part.setPublicRepresentativeDetail(strPublicRepresentativeDetail);
			}
			
			/****Device Type****/
			String strDeviceType=request.getParameter("deviceType"+i);
			if(strDeviceType!=null && !strDeviceType.isEmpty()){
				DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				part.setDeviceType(deviceType);
			}
			
			/****Device Id****/
			String strDeviceId=request.getParameter("deviceId"+i);
			if(strDeviceId!=null && !strDeviceId.isEmpty()){
				part.setDeviceId(Long.parseLong(strDeviceId));
			}
			
			/****The Part Entry Date****/
			part.setEntryDate(new Date());
			
			String strIsConstituencyRequired=request.getParameter("isConstituencyRequired"+i);
			if(strIsConstituencyRequired!=null && !strIsConstituencyRequired.isEmpty()){
				part.setIsConstituencyRequired(true);
			}else{
				part.setIsConstituencyRequired(false);
			}
			
			String strIsInterrupted=request.getParameter("isInterrupted"+i);
			if(strIsInterrupted!=null && !strIsInterrupted.isEmpty()){
				part.setIsInterrupted(true);
			}else{
				part.setIsInterrupted(false);
			}
					
			/****Reporter****/
			part.setReporter(domain.getSlot().getReporter());
			parts.add(part);
		}
		domain.setParts(parts);
	}
	
	@Override
	protected void customValidateUpdate(final Proceeding domain,
			final BindingResult result, final HttpServletRequest request) {

	}

	/****View the Details of the Part in which the Bookmark is added****/
	@RequestMapping(value="/part/viewbookmark",method=RequestMethod.GET)
	public String viewBookmarkDetail(final HttpServletRequest request, final Locale locale,
			final ModelMap model){
		String strBookmarkId=request.getParameter("id");
		
		if(strBookmarkId!=null && !strBookmarkId.isEmpty()){
			/****Bookmark****/
			Bookmark bookmark=Bookmark.findById(Bookmark.class, Long.parseLong(strBookmarkId));
			if(bookmark!=null){
				Part part=bookmark.getMasterPart();
				Proceeding proceeding=part.getProceeding();
				Language language=proceeding.getSlot().findLanguage();
				List result=Part.findAllPartsOfProceeding(proceeding,language,bookmark.getBookmarkKey(),locale.toString());
				model.addAttribute("report", result);
				model.addAttribute("bookmarkKey",bookmark.getBookmarkKey());
				/****Reporter****/
				Reporter reporter=part.getReporter();
				if(reporter!=null){
					User user=reporter.getUser();
					if(user!=null){
						model.addAttribute("reporter",user.findFullName());
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
		Proceeding proceeding=null;
		domain.setLocale(locale.toString());
		String strProceeding= request.getParameter("proceeding");
		if(strProceeding!=null && !strProceeding.isEmpty()){
			/****Proceeding****/
			proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			model.addAttribute("proceeding", proceeding.getId());
			Slot slot=proceeding.getSlot();
			if(slot!=null){
				/****slot****/
				model.addAttribute("slotName", slot.getName());
				model.addAttribute("currentSlot", slot.getId());
				
				/****Reporter****/
				model.addAttribute("reporter", slot.getReporter().getId());
				
				/****session****/
				Roster roster=slot.getRoster();
				Session session=roster.getSession();
				model.addAttribute("session",session.getId());
				
				/****Ministries****/
				List<Ministry> ministries;
				try {
					ministries = Ministry.findMinistriesAssignedToGroups(session.getHouse().getType(), session.getYear(), session.getType(), session.getLocale());
					model.addAttribute("ministries", ministries);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/****Bookmarks****/
				List<Bookmark> bk=new ArrayList<Bookmark>();
				if(proceeding!=null){
					List<Bookmark> bookmarks=Bookmark.findAllByFieldName(Bookmark.class, "slot", slot, "bookmarkKey", "asc", locale.toString());
					List<Bookmark> bookmarks1=Bookmark.findAllByFieldName(Bookmark.class, "language", proceeding.getSlot().findLanguage(), "id", "asc", locale.toString());
					for(Bookmark b:bookmarks1){
						if(b.getSlot()==null){
							String key=b.getBookmarkKey();
							String[] keyArr=key.split("-");
							String[] keyArr1=keyArr[1].split("~");
							String[] keyArry2=keyArr1[1].split("_");
							String[] slotArr=keyArry2[0].split("/");
							for(int i=0;i<slotArr.length;i++){
								if(slotArr[i].equals(proceeding.getSlot().getName())){
									bk.add(b);
								}
							}
						}
					}
					if(!bookmarks.isEmpty()){
						bk.addAll(bookmarks);
					}
					model.addAttribute("bookmarks", bk);
				}
			}
		/****Parts****/
		List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proceeding, "id", "desc", locale.toString());
			if(parts.isEmpty()){
				model.addAttribute("orderNo", "1");
			}else{
				model.addAttribute("orderNo", parts.size()+1);
				model.addAttribute("lastPartId",parts.get(parts.size()-1).getId());
				model.addAttribute("mainHeading",parts.get(0).getMainHeading());
				model.addAttribute("pageHeading",parts.get(0).getPageHeading());
			}

		}
		/****MemberRole****/
		String strHouseType=this.getCurrentUser().getHouseType();
		HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
		List<MemberRole> roles=MemberRole.findAllByFieldName(MemberRole.class, "houseType", houseType, "name", "asc", locale.toString());
		model.addAttribute("roles", roles);
		
		/****Devicetype****/
		List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name", "desc", locale.toString());
		model.addAttribute("deviceTypes", deviceTypes);
		
		/****Domain****/
		model.addAttribute("domain", domain);

		/****Designations****/
		List<Designation> designations=Designation.findAll(Designation.class,"name", "asc", locale.toString());
		model.addAttribute("designations",designations);

		/****Party****/
		List<Party> parties=Party.findActiveParties(locale.toString());
		model.addAttribute("parties", parties);
 
		/****HouseTypes****/
		List<HouseType> houseTypes=HouseType.findAll(HouseType.class, "name", "desc", locale.toString());
		model.addAttribute("houseTypes", houseTypes);
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
		if(domain.getId()!=null){
			((BaseDomain) domain).merge();
		}else{
			((BaseDomain) domain).persist();
		}
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
		String strPrimaryMemberSubDepartment=request.getParameter("primaryMemberSubDepartment");
		String strSubstituteMember=request.getParameter("substituteMember");
		String strSubstituteMinistry=request.getParameter("substituteMemberMinistry");
		String strSubstituteDesignation=request.getParameter("substituteMemberDesignation");
		String strSubstituteSubDepartment=request.getParameter("substituteMemberSubDepartment");
		String strDeviceId=request.getParameter("deviceId");
		String strDeviceType=request.getParameter("deviceType");
		Proceeding proceeding=null;
		/****Proceeding****/
		if(strProceeding!=null && !strProceeding.equals("")){
			proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			domain.setProceeding(proceeding);
		}
		/****Reporter****/
		if(strReporter!=null && !strReporter.equals("")){
			Reporter reporter=Reporter.findById(Reporter.class, Long.parseLong(strReporter));
			domain.setReporter(reporter);
		}
		/****Member****/
		if(strMember!=null && !strMember.equals("")){
			Member member=Member.findById(Member.class, Long.parseLong(strMember));
			domain.setPrimaryMember(member);
		}
		/****MemberRole and ChairPerson****/
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
					domain.setChairPerson(member.getFullname());
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				domain.setChairPersonRole(mr);
				
			}
		}
		/****Revised Content****/
		domain.setRevisedContent(domain.getProceedingContent());
		/****Primary member Designation****/
		if(strPrimaryMemberDesignation!=null && !strPrimaryMemberDesignation.isEmpty()){
			Designation designation=Designation.findById(Designation.class, Long.parseLong(strPrimaryMemberDesignation));
			domain.setPrimaryMemberDesignation(designation);
		}else{
			domain.setPrimaryMemberDesignation(null);
		}
		/****Primary member Ministry****/
		if(strPrimaryMemberMinistry!=null && !strPrimaryMemberMinistry.isEmpty()){
			Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strPrimaryMemberMinistry));
			domain.setPrimaryMemberMinistry(ministry);
		}
		
		if(strPrimaryMemberSubDepartment!=null && !strPrimaryMemberSubDepartment.isEmpty()){
			SubDepartment subDepartment=SubDepartment.findById(SubDepartment.class, Long.parseLong(strPrimaryMemberSubDepartment));
			domain.setPrimaryMemberSubDepartment(subDepartment);
		}

		/****Substitute member ****/
		if(strSubstituteMember!=null && !strSubstituteMember.isEmpty()){
			Member member=Member.findById(Member.class, Long.parseLong(strSubstituteMember));
			domain.setSubstituteMember(member);
		}

		/****Substitute member Designation****/
		if(strSubstituteDesignation!=null && !strSubstituteDesignation.isEmpty()){
			Designation designation=Designation.findById(Designation.class, Long.parseLong(strSubstituteDesignation));
			domain.setSubstituteMemberDesignation(designation);
		}else{
			domain.setSubstituteMemberDesignation(null);
		}

		/****Substitute member ministry****/
		if(strSubstituteMinistry!=null && !strSubstituteMinistry.isEmpty()){
			Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strSubstituteMinistry));
			domain.setSubstituteMemberMinistry(ministry);
		}
		
		if(strSubstituteSubDepartment!=null && !strSubstituteSubDepartment.isEmpty()){
			SubDepartment subDepartment=SubDepartment.findById(SubDepartment.class, Long.parseLong(strSubstituteSubDepartment));
			domain.setSubstituteMemberSubDepartment(subDepartment);
		}
		
		/****DeviceType****/
		if(strDeviceType!=null && !strDeviceType.isEmpty()){
			DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			domain.setDeviceType(deviceType);
		}
		
		/****Device id****/
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
			/****Current Slot****/
			model.addAttribute("currentSlot",slot.getId());
			model.addAttribute("slotName", slot.getName());
			/****Reporter****/
			model.addAttribute("reporter", slot.getReporter().getId());
			
			/****Session****/
			Roster roster=slot.getRoster();
			Session session=roster.getSession();
			model.addAttribute("session",session.getId());
			
			/****Ministries****/
			List<Ministry> ministries;
			try {
				ministries = Ministry.findMinistriesAssignedToGroups(session.getHouse().getType(), session.getYear(), session.getType(), session.getLocale());
				model.addAttribute("ministries", ministries);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			/****Primary member ministry****/
			if(domain.getPrimaryMemberMinistry()!=null){
				model.addAttribute("primaryMemberMinistrySelected", domain.getPrimaryMemberMinistry().getId());
				List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(domain.getPrimaryMemberMinistry(), domain.getLocale());
				model.addAttribute("subDepartments",subDepartments);
			}
			
			/****substitute member Ministry****/
			if(domain.getSubstituteMemberMinistry()!=null){
				model.addAttribute("substituteMemberMinistrySelected", domain.getSubstituteMemberMinistry().getId());
				List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(domain.getSubstituteMemberMinistry(), domain.getLocale());
				model.addAttribute("subDepartments",subDepartments);
			}

			/****Bookmarks****/
			List<Bookmark> bk=new ArrayList<Bookmark>();
			List<Bookmark> bookmarks=Bookmark.findAllByFieldName(Bookmark.class, "slot", slot, "bookmarkKey", "asc", domain.getLocale());
			//if(bookmarks.isEmpty()){
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
				if(!bookmarks.isEmpty()){
					bk.addAll(bookmarks);
				}
				model.addAttribute("bookmarks", bk);

			/*}else{
				model.addAttribute("bookmarks", bookmarks);
			}*/
		}
		
		/****MemberRoles****/
		String strHouseType=this.getCurrentUser().getHouseType();
		HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, domain.getLocale());
		List<MemberRole> roles=MemberRole.findAllByFieldName(MemberRole.class, "houseType", houseType, "name", "asc", domain.getLocale());
		model.addAttribute("roles", roles);
		
		/****Proceeding****/
		model.addAttribute("proceeding",domain.getProceeding().getId());
		
		/****Primary member ****/
		if(domain.getPrimaryMember()!=null){
			model.addAttribute("primaryMember",domain.getPrimaryMember().getId());
			model.addAttribute("formattedPrimaryMember", domain.getPrimaryMember().getFullname());
		}
		
		/****Substitute member ****/
		if(domain.getSubstituteMember()!=null){
			model.addAttribute("substituteMember",domain.getPrimaryMember().getId());
			model.addAttribute("formattedSubstituteMember", domain.getSubstituteMember().getFullname());
		}
		/**********Domain*************/
		model.addAttribute("domain", domain);
		
		/****Designations****/
		List<Designation> designations=Designation.findAll(Designation.class,"name", "asc", domain.getLocale());
		model.addAttribute("designations",designations);
		/****Primary member Designation****/
		if(domain.getPrimaryMemberDesignation()!=null){
			model.addAttribute("primaryMemberDesignationSelected", domain.getPrimaryMemberDesignation().getId());
		}
		/****Substitute member Designation****/
		if(domain.getSubstituteMemberDesignation()!=null){
			model.addAttribute("substituteMemberDesignationSelected", domain.getSubstituteMemberDesignation().getId());
		}
		
		if(domain.getSubstituteMemberSubDepartment()!=null){
			model.addAttribute("substituteMemberSubDepartmentSelected",domain.getSubstituteMemberSubDepartment().getId());
		}
		
		if(domain.getPrimaryMemberSubDepartment()!=null){
			model.addAttribute("primaryMemberSubDepartmentSelected",domain.getPrimaryMemberSubDepartment().getId());
		}
		/**** Part DeviceType****/
		if(domain.getDeviceType()!=null){
			DeviceType deviceType=domain.getDeviceType();
			model.addAttribute("selectedDeviceType",deviceType.getId());
			String device=deviceType.getDevice();
			int deviceNumber=0;
			if(domain.getDeviceId()!=null){
				if(device.equals("Bill")){
					Bill bill=Bill.findById(Bill.class, domain.getDeviceId());
					deviceNumber=bill.getNumber();
				}else if(device.equals("Resolution")){
					Resolution resolution=Resolution.findById(Resolution.class, domain.getDeviceId());
					deviceNumber=resolution.getNumber();
				}else if(device.equals("Question")){
					Question question=Question.findById(Question.class, domain.getDeviceId());
					deviceNumber=question.getNumber();
				}else if(device.equals("Motion")){
					Motion motion=Motion.findById(Motion.class, domain.getDeviceId());
					deviceNumber=motion.getNumber();
				}
			}
			model.addAttribute("deviceNumber", deviceNumber);
		}
		
		
		/****DeviceTypes****/
		List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name", "asc", domain.getLocale());
		model.addAttribute("deviceTypes", deviceTypes);
		
		/****Party****/
		List<Party> parties=Party.findActiveParties(domain.getLocale());
		model.addAttribute("parties", parties);
		
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
		String strPrimaryMemberSubDepartment=request.getParameter("primaryMemberSubDepartment");
		String strSubstituteMember=request.getParameter("substituteMember");
		String strSubstituteMinistry=request.getParameter("substituteMemberMinistry");
		String strSubstituteSubDepartment=request.getParameter("substituteMemberSubDepartment");
		String strSubstituteDesignation=request.getParameter("substituteMemberDesignation");
		String strDeviceType=request.getParameter("deviceType");
		Proceeding proceeding=null;
		/****Proceeding****/
		if(strProceeding!=null && !strProceeding.equals("")){
			proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			domain.setProceeding(proceeding);
		}
		
		/****Reporter****/
		if(strReporter!=null && !strReporter.equals("")){
			Reporter reporter=Reporter.findById(Reporter.class, Long.parseLong(strReporter));
			domain.setReporter(reporter);
		}
		
		/****Primary Member ****/
		if(strMember!=null && !strMember.equals("")){
			Member member=Member.findById(Member.class, Long.parseLong(strMember));
			domain.setPrimaryMember(member);
		}
		
		/****MemberRole and Chairperson****/
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
					
				
			}
		}
		/****Revised Content****/
		domain.setRevisedContent(domain.getProceedingContent());
		
		/****Primary Member Designation****/
		if(strPrimaryMemberDesignation!=null && !strPrimaryMemberDesignation.isEmpty()){
			Designation designation=Designation.findById(Designation.class, Long.parseLong(strPrimaryMemberDesignation));
			domain.setPrimaryMemberDesignation(designation);
		}else{
			domain.setPrimaryMemberDesignation(null);
		}
		/****Primary Member Ministry****/
		if(strPrimaryMemberMinistry!=null && !strPrimaryMemberMinistry.isEmpty()){
			Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strPrimaryMemberMinistry));
			domain.setPrimaryMemberMinistry(ministry);
		}

	
		if(strPrimaryMemberSubDepartment!=null && !strPrimaryMemberSubDepartment.isEmpty()){
			SubDepartment subDepartment=SubDepartment.findById(SubDepartment.class, Long.parseLong(strPrimaryMemberSubDepartment));
			domain.setPrimaryMemberSubDepartment(subDepartment);
		}
		/****Substitute Member ****/
		if(strSubstituteMember!=null && !strSubstituteMember.isEmpty()){
			Member member=Member.findById(Member.class, Long.parseLong(strSubstituteMember));
			domain.setSubstituteMember(member);
		}

		/****Substitute Member Designation****/
		if(strSubstituteDesignation!=null && !strSubstituteDesignation.isEmpty()){
			Designation designation=Designation.findById(Designation.class, Long.parseLong(strSubstituteDesignation));
			domain.setSubstituteMemberDesignation(designation);
		}else{
			domain.setSubstituteMemberDesignation(null);
		}

		/****Substitute Member Ministry****/
		if(strSubstituteMinistry!=null && !strSubstituteMinistry.isEmpty()){
			Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strSubstituteMinistry));
			domain.setSubstituteMemberMinistry(ministry);
		}

		if(strSubstituteSubDepartment!=null && !strSubstituteSubDepartment.isEmpty()){
			SubDepartment subDepartment=SubDepartment.findById(SubDepartment.class, Long.parseLong(strSubstituteSubDepartment));
			domain.setSubstituteMemberSubDepartment(subDepartment);
		}
		/****DeviceType****/
		if(strDeviceType!=null && !strDeviceType.isEmpty()){
			DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			domain.setDeviceType(deviceType);
		}
	}


	protected void populateUpdatePartIfNoErrors(final ModelMap model,
			final Part domain, final HttpServletRequest request) {
		/****Here Updating the Bookmark key with the content to be replaced in Revised Content and not in Proceeding Content****/
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
	
	@RequestMapping(value="/part/save",method=RequestMethod.POST)
	public @ResponseBody MasterVO savePart(final HttpServletRequest request, final Locale locale,final ModelMap model){
		String strProceedingText=request.getParameter("content");
		String strMember=request.getParameter("primaryMember");
		String strProceeding=request.getParameter("proceeding");
		String strMinister=request.getParameter("primaryMemberMinistry");
		String strDesignation=request.getParameter("primaryMemberDesignation");
		String strPublicRepresentative=request.getParameter("publicRepresentative");
		String strPartId=request.getParameter("partId");
		String strOrderNo=request.getParameter("orderNo");
		MasterVO masterVO=new MasterVO();
		if( strProceedingText!=null && !strProceedingText.isEmpty()
			&& strProceeding!=null && !strProceeding.isEmpty()){
			Part part=null;
			if(strPartId!=null && !strPartId.isEmpty()){
				part=Part.findById(Part.class, Long.parseLong(strPartId));
			}else{
				part=new Part();
			}
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			String server=null;
			String strParam=null;
			if(customParameter!=null){
				server=customParameter.getValue();
				if(!strProceedingText.isEmpty()){
					if(server.equals("TOMCAT")){
						try {
							strParam = new String(strProceedingText.getBytes("ISO-8859-1"),"UTF-8");

						}catch (UnsupportedEncodingException e) {
							logger.error("Cannot Encode the Parameter.");
						}
					}
				}
			}
			part.setProceedingContent(strParam);
			if(strMember!=null && !strMember.isEmpty()){
				Member member=Member.findById(Member.class, Long.parseLong(strMember));
				part.setPrimaryMember(member);
			}
			if(strMinister!=null && !strMinister.isEmpty()){
				Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strMinister));
				part.setPrimaryMemberMinistry(ministry);
			}
			if(strDesignation!=null && !strDesignation.isEmpty()){
				Designation designation=Designation.findById(Designation.class, Long.parseLong(strDesignation));
				part.setPrimaryMemberDesignation(designation);
			}
			if(strPublicRepresentative!=null && !strPublicRepresentative.isEmpty()){
				part.setPublicRepresentative(strPublicRepresentative);
			}
			Proceeding proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			part.setProceeding(proceeding);
			part.setOrderNo(Integer.parseInt(strOrderNo));
			part.setLocale(locale.toString());
			if(part.getId()!=null){
				((BaseDomain)part).merge();
			}else{
				((BaseDomain)part).persist();
			}
			masterVO.setId(((BaseDomain)part).getId());
			masterVO.setName(((BaseDomain)part).getVersion().toString());
			return masterVO;
		}
		return null;
		
	}
	
	/****Bookmark Related****/
	@RequestMapping(value="/part/bookmark",method=RequestMethod.GET)
	public String getBookmark(final HttpServletRequest request, final Locale locale,final ModelMap model){
		List<Language> languages=Language.findAll(Language.class, "name", "asc", locale.toString());
		List<Language> mainlanguages=new ArrayList<Language>();
		String strLanguage=request.getParameter("language");
		String strSlot=request.getParameter("currentSlot");
		String strPart=request.getParameter("currentPart");
		String strCount=request.getParameter("count");
		/****Language****/
		Language language=null;
		if(strLanguage!=null && !strLanguage.equals("")){
			language=Language.findById(Language.class, Long.parseLong(strLanguage));
		}
		for(Language l:languages){
			if(l.getId()!=language.getId()){
				mainlanguages.add(l);
			}
		}
		/****Bookmark size****/
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
		//String strPreviousText=request.getParameter("previousText");
		String strBookmarkKey=request.getParameter("bookmarkKey");
		String strLanguage=request.getParameter("language");
		String strPart=request.getParameter("masterPart");
		Bookmark bookmark=new Bookmark();
		bookmark.setLocale(locale.toString());
		/****Master Part****/
		if(strPart!=null && !strPart.isEmpty()){
			Part part=Part.findById(Part.class, Long.parseLong(strPart));
			bookmark.setMasterPart(part);
			bookmark.setPreviousText(part.getProceedingContent());
		}
		
		/****Previous Text****/
	/*	if(strPreviousText!=null && !strPreviousText.equals("")){
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
		}*/
		
		/****Language****/
		if(strLanguage!=null && !strLanguage.equals("")){
			Language language=Language.findByFieldName(Language.class, "type", strLanguage, locale.toString());
			bookmark.setLanguage(language);
		}
		
		/****Bookmark key****/
		if(strBookmarkKey!=null && !strBookmarkKey.equals("")){
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			String server=null;
			String param=null;
			if(customParameter!=null){
				server=customParameter.getValue();
				if(!strBookmarkKey.isEmpty()){
					if(server.equals("TOMCAT")){
						try {
							param = new String(strBookmarkKey.getBytes("ISO-8859-1"),"UTF-8");

						}catch (UnsupportedEncodingException e) {
							logger.error("Cannot Encode the Parameter.");
						}
					}
				}
			}
			bookmark.setBookmarkKey(param);
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
			/****Bookmark****/
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
				/****Slave part****/
				if(strPart!=null && !strPart.isEmpty()){
					Part part=Part.findById(Part.class, Long.parseLong(strPart));
					bookmark.setSlavePart(part);
				}
				/****Slave slot****/
				if(strSlot!=null && !strSlot.isEmpty()){
					Slot slot=Slot.findById(Slot.class, Long.parseLong(strSlot));
					bookmark.setSlot(slot);
				}
				/****Reporter replacing the bookmark****/
				bookmark.setBookmarkReplacedBy(bookmark.getSlot().getReporter());
				
				/****Replacing Date****/
				bookmark.setBookmarkReplacedDate(new Date());
				
				/****Text replaced****/
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

	/****Citations****/
	@RequestMapping(value="part/citations",method=RequestMethod.GET)
	public String getCitations(final HttpServletRequest request, final Locale locale,
			final ModelMap model){
		String strCounter=request.getParameter("counter");
		List<Citation> citations=Citation.findAllByFieldName(Citation.class, "type", ApplicationConstants.REPORTING, "text", "asc", locale.toString());//ProceedingCitation.findAll(ProceedingCitation.class, "id", "asc", locale.toString());
		if(strCounter!=null && !strCounter.isEmpty()){
			model.addAttribute("counter", Integer.parseInt(strCounter));
		}
		model.addAttribute("citations",citations);
		return "proceeding/proceedingcitation";
		
	}

	@RequestMapping(value="/part/getMemberByParty",method=RequestMethod.GET)
	public @ResponseBody List<Member> getMemberByParty(final HttpServletRequest request, final Locale locale,
			final ModelMap model){
		String strPartyId=request.getParameter("partyId");
		String type=this.getCurrentUser().getHouseType();
		HouseType houseType=null;
		if(type!=null && !type.isEmpty()){
			 houseType=HouseType.findByType(type, locale.toString());
		}
		House house=House.find(houseType, new Date(), locale.toString());
		if(strPartyId!=null && !strPartyId.isEmpty()){
			Party party=Party.findById(Party.class, Long.parseLong(strPartyId));
			List<Member> members=Member.findActiveMembersByParty(party,house,locale.toString());
			model.addAttribute("members", members);		
		
		return members;
		}
		return null;
	}
	
	@RequestMapping(value="/part/getMemberByPartyPage",method=RequestMethod.GET)
	public String getMemberByPartyPage(final HttpServletRequest request, final Locale locale,
			final ModelMap model){
		String strPartyId=request.getParameter("partyId");
		String type=this.getCurrentUser().getHouseType();
		HouseType houseType=null;
		if(type!=null && !type.isEmpty()){
			 houseType=HouseType.findByType(type, locale.toString());
		}
		House house=House.find(houseType, new Date(), locale.toString());
		if(strPartyId!=null && !strPartyId.isEmpty()){
			Party party=Party.findById(Party.class, Long.parseLong(strPartyId));
			List<Member> members=Member.findActiveMembersByParty(party,house,locale.toString());
			model.addAttribute("members", members);		
		
		return "proceeding/memberphoto";
		}
		return "";
	}
	
	/****Reports Related****/
	
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
			Slot slot=proceeding.getSlot();
			Roster roster=slot.getRoster();
			Session session=roster.getSession();
			if(strLanguage!=null&& !strLanguage.isEmpty()){
				language=Language.findById(Language.class, Long.parseLong(strLanguage));
			}
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("proceedingId", new String[]{proceeding.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_SLOT_WISE_REPORT, parametersMap);		
			
			model.addAttribute("report", result);
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
							if(memberRole.getType().equals(ApplicationConstants.SPEAKER.toUpperCase())
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_SPEAKER.toUpperCase())
									||memberRole.getType().equals(ApplicationConstants.CHAIRMAN.toUpperCase())
									||memberRole.getType().equals(ApplicationConstants.CHIEF_MINISTER.toUpperCase())
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_CHAIRMAN.toUpperCase())
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER.toUpperCase())){
								row[15]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
			}
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
			model.addAttribute("userName", this.getCurrentUser().getUsername());
			model.addAttribute("undoCount", 0);
			model.addAttribute("redoCount", 0);
			
			
			return "proceeding/part/proceedingwisereport";
		}
		return null;
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
			List<ParentVO> parentVOs=new ArrayList<ParentVO>();
			ParentVO parentVO=new ParentVO();
			parentVO.setId(slot.getId());
			parentVO.setName(slot.getName());
			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
			String startDate=dateFormat.format(slot.getStartTime());
			String startTime=dateFormat1.format(slot.getStartTime());
			parentVO.setStartDate(startDate);
			parentVO.setStartTime(startTime);
			List<ChildVO> childVOs=new ArrayList<ChildVO>();
			List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proceeding, "orderNo", "asc", locale.toString());
			for(Part p:parts){
				ChildVO childVO=new ChildVO();
				childVO.setId(p.getId());
				childVO.setProceedingContent(p.getRevisedContent());
				childVO.setChairperson(p.getChairPerson());
				childVO.setMainHeading(p.getMainHeading());
				childVO.setMemberrole(p.getChairPersonRole().getName());
				childVO.setOrderNo(p.getOrderNo());
				childVO.setPageHeading(p.getPageHeading());
				Member primaryMember=p.getPrimaryMember();
				if(primaryMember!=null){
					List<HouseMemberRoleAssociation> hrma=primaryMember.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								childVO.setPrimaryMember(memberRole.getName());
							}else{
								childVO.setPrimaryMember(primaryMember.getFullname());
							}

						}
					}
									
					if(p.getIsConstituencyRequired()){
						House house=roster.getSession().getHouse();
						MasterVO masterVo=null;
						if(house.getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							 masterVo=Member.findConstituencyByAssemblyId(primaryMember.getId(), house.getId());
							
						}else if(house.getType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							Date currentDate=new Date();
							String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
							masterVo=Member.findConstituencyByCouncilDates(primaryMember.getId(), house.getId(), "DATE", date, date);
						}
						childVO.setConstituency(masterVo.getName());
					}
					if(p.getPrimaryMemberDesignation()!=null){
					childVO.setPrimaryMemberDesignation(p.getPrimaryMemberDesignation().getName());
					}
					if(p.getPrimaryMemberMinistry()!=null){
						childVO.setPrimaryMemberMinistry(p.getPrimaryMemberMinistry().getName());
					}
					if(p.getPrimaryMemberSubDepartment()!=null){
						childVO.setPrimaryMemberSubDepartment(p.getPrimaryMemberSubDepartment().getName());
					}
				}
				Member substituteMember=p.getSubstituteMember();
				if(substituteMember!=null){
					childVO.setSubstituteMember(substituteMember.getFullname());
					if(p.getSubstituteMemberDesignation()!=null){
						childVO.setSubstituteMemberDesignation(p.getSubstituteMemberDesignation().getName());
					}
					if(p.getSubstituteMemberMinistry()!=null){
						childVO.setSubstituteMemberMinistry(p.getSubstituteMemberMinistry().getName());
					}
					if(p.getSubstituteMemberSubDepartment()!=null){
						childVO.setSubstituteMemberSubDepartment(p.getSubstituteMemberSubDepartment().getName());
					}
				}
				if(p.getPublicRepresentative()!=null){
					childVO.setPublicRepresentative(p.getPublicRepresentative());
					if(p.getPublicRepresentativeDetail()!=null){
						childVO.setPublicRepresentativeDetails(p.getPublicRepresentativeDetail());
					}
				}
				childVOs.add(childVO); 
			}
			parentVO.setChildVOs(childVOs);
			parentVOs.add(parentVO);
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word1", reportFormat, "karyavrutt_slotwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_slotwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
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
			List<Object> tempList2=new ArrayList<Object>();
			List<Object> objects=new ArrayList<Object>();
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
			
			for(int i=0;i<result.size();i++){
				Object[] row1=(Object[]) result.get(i);
				Object[] row=new Object[row1.length+2];
				for(int j=0;j<row1.length;j++){
					row[j]=row1[j];
				}
				tempList2.add(row);
			}
			/****Adding the next reporter name to the array****/
			for(int i=0;i<tempList2.size();i++){
				Object[] row=(Object[]) tempList2.get(i);
				for(int j=i+1;j<tempList2.size() && j<i+2;j++){
					Object[] row1=(Object[]) tempList2.get(j);
					if(!row1[6].toString().equals(row[6].toString())){
						row[25]=row1[18];
						i=j+1;
						break;
					}
				}
			}

				/****Adding the previous reporter name ****/
			for(int i=0;i<tempList2.size();i++){
				Object[] row=(Object[]) tempList2.get(i);
				for(int k=i-1;k>=0;k--){
					Object[] row2=(Object[]) tempList2.get(k);
					if(!row2[6].toString().equals(row[6].toString())){
					//	if(row2[10]!=null){
							row[26]=row2[18];
							break;
					//	}
					}
				}
				//objects.add(row);
			}
			model.addAttribute("generalNotice", generalNotice);
			model.addAttribute("mainHeading", mainHeading);
			model.addAttribute("pageHeading",pageHeading);
			model.addAttribute("inplaceOf", inplaceOf);
			model.addAttribute("report", tempList2);
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
			List<ParentVO> parentVOs=new ArrayList<ParentVO>();
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			List<Slot> slots =Slot.findAllByFieldName(Slot.class, "roster", roster, "name", "asc", locale.toString());
			for(Slot s:slots){
				ParentVO parentVO=new ParentVO();
				List<ChildVO> childVOs=new ArrayList<ChildVO>();
				List<Proceeding> proceedings=Proceeding.findAllFilledProceedingBySlot(s);
				for(Proceeding proc:proceedings){
					List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proc, "orderNo", "asc", locale.toString());
					for(Part p:parts){
						ChildVO childVO=new ChildVO();
						childVO.setId(p.getId());
						childVO.setProceedingContent(p.getRevisedContent());
						childVO.setChairperson(p.getChairPerson());
						childVO.setMainHeading(p.getMainHeading());
						childVO.setMemberrole(p.getChairPersonRole().getName());
						childVO.setOrderNo(p.getOrderNo());
						childVO.setPageHeading(p.getPageHeading());
						Member primaryMember=p.getPrimaryMember();
						if(primaryMember!=null){
							List<HouseMemberRoleAssociation> hrma=primaryMember.getHouseMemberRoleAssociations();
							for(HouseMemberRoleAssociation h:hrma){
								if(h.getHouse().equals(session.getHouse())){
									MemberRole memberRole=h.getRole();
									if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
										childVO.setPrimaryMember(memberRole.getName());
									}else{
										childVO.setPrimaryMember(primaryMember.getFullname());
									}
		
								}
							}
						
							
							if(p.getIsConstituencyRequired()){
								House house=null;
								house=House.find(houseType, new Date(), locale.toString());
								MasterVO masterVo=null;
								if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
									 masterVo=Member.findConstituencyByAssemblyId(primaryMember.getId(), house.getId());
									
								}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
									Date currentDate=new Date();
									String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
									masterVo=Member.findConstituencyByCouncilDates(primaryMember.getId(), house.getId(), "DATE", date, date);
								}
								childVO.setConstituency(masterVo.getName());
							}
							if(p.getPrimaryMemberDesignation()!=null){
							childVO.setPrimaryMemberDesignation(p.getPrimaryMemberDesignation().getName());
							}
							if(p.getPrimaryMemberMinistry()!=null){
								childVO.setPrimaryMemberMinistry(p.getPrimaryMemberMinistry().getName());
							}
							if(p.getPrimaryMemberSubDepartment()!=null){
								childVO.setPrimaryMemberSubDepartment(p.getPrimaryMemberSubDepartment().getName());
							}
						}
						Member substituteMember=p.getSubstituteMember();
						if(substituteMember!=null){
							childVO.setSubstituteMember(substituteMember.getFullname());
							if(p.getSubstituteMemberDesignation()!=null){
								childVO.setSubstituteMemberDesignation(p.getSubstituteMemberDesignation().getName());
							}
							if(p.getSubstituteMemberMinistry()!=null){
								childVO.setSubstituteMemberMinistry(p.getSubstituteMemberMinistry().getName());
							}
							if(p.getSubstituteMemberSubDepartment()!=null){
								childVO.setSubstituteMemberSubDepartment(p.getSubstituteMemberSubDepartment().getName());
							}
						}
						if(p.getPublicRepresentative()!=null){
							childVO.setPublicRepresentative(p.getPublicRepresentative());
							if(p.getPublicRepresentativeDetail()!=null){
								childVO.setPublicRepresentativeDetails(p.getPublicRepresentativeDetail());
							}
						}
						childVOs.add(childVO); 
					}
					parentVO.setChildVOs(childVOs);
					parentVO.setId(s.getId());
					parentVO.setName(s.getName());
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
					String startDate=dateFormat.format(s.getStartTime());
					String startTime=dateFormat1.format(s.getStartTime());
					parentVO.setStartDate(startDate);
					parentVO.setStartTime(startTime);
					parentVO.setReporter(s.getReporter().getUser().getFirstName());
					List<User> users=Slot.findDifferentLanguageUsersBySlot(s);
					String languageReporter="";
					for(int i=0;i<users.size();i++){
						languageReporter=languageReporter+users.get(i).getFirstName();
						if(i+1<users.size()){
							languageReporter=languageReporter+"/";
						}
					}
					parentVO.setLanguageReporter(languageReporter);
					parentVOs.add(parentVO);
				}
				
				
			}
			
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word1", reportFormat, "karyavrutt_rosterwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_rosterwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
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
			
			List<Slot> slots=Slot.findSlotsBySessionAndLanguage(session,language);
			List<ParentVO> parentVOs=new ArrayList<ParentVO>();
			for(Slot s:slots){
				ParentVO parentVO=new ParentVO();
				List<ChildVO> childVOs=new ArrayList<ChildVO>();
				List<Proceeding> proceedings=Proceeding.findAllFilledProceedingBySlot(s);
				for(Proceeding proc:proceedings){
					List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proc, "orderNo", "asc", locale.toString());
					for(Part p:parts){
						ChildVO childVO=new ChildVO();
						childVO.setId(p.getId());
						childVO.setProceedingContent(p.getRevisedContent());
						childVO.setChairperson(p.getChairPerson());
						childVO.setMainHeading(p.getMainHeading());
						childVO.setMemberrole(p.getChairPersonRole().getName());
						childVO.setOrderNo(p.getOrderNo());
						childVO.setPageHeading(p.getPageHeading());
						Member primaryMember=p.getPrimaryMember();
						if(primaryMember!=null){
							List<HouseMemberRoleAssociation> hrma=primaryMember.getHouseMemberRoleAssociations();
							for(HouseMemberRoleAssociation h:hrma){
								if(h.getHouse().equals(session.getHouse())){
									MemberRole memberRole=h.getRole();
									if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
										childVO.setPrimaryMember(memberRole.getName());
									}else{
										childVO.setPrimaryMember(primaryMember.getFullname());
									}
		
								}
							}
						
							
							if(p.getIsConstituencyRequired()){
								House house=null;
								house=House.find(houseType, new Date(), locale.toString());
								MasterVO masterVo=null;
								if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
									 masterVo=Member.findConstituencyByAssemblyId(primaryMember.getId(), house.getId());
									
								}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
									Date currentDate=new Date();
									String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
									masterVo=Member.findConstituencyByCouncilDates(primaryMember.getId(), house.getId(), "DATE", date, date);
								}
								childVO.setConstituency(masterVo.getName());
							}
							if(p.getPrimaryMemberDesignation()!=null){
							childVO.setPrimaryMemberDesignation(p.getPrimaryMemberDesignation().getName());
							}
							if(p.getPrimaryMemberMinistry()!=null){
								childVO.setPrimaryMemberMinistry(p.getPrimaryMemberMinistry().getName());
							}
							if(p.getPrimaryMemberSubDepartment()!=null){
								childVO.setPrimaryMemberSubDepartment(p.getPrimaryMemberSubDepartment().getName());
							}
						}
						Member substituteMember=p.getSubstituteMember();
						if(substituteMember!=null){
							childVO.setSubstituteMember(substituteMember.getFullname());
							if(p.getSubstituteMemberDesignation()!=null){
								childVO.setSubstituteMemberDesignation(p.getSubstituteMemberDesignation().getName());
							}
							if(p.getSubstituteMemberMinistry()!=null){
								childVO.setSubstituteMemberMinistry(p.getSubstituteMemberMinistry().getName());
							}
							if(p.getSubstituteMemberSubDepartment()!=null){
								childVO.setSubstituteMemberSubDepartment(p.getSubstituteMemberSubDepartment().getName());
							}
						}
						if(p.getPublicRepresentative()!=null){
							childVO.setPublicRepresentative(p.getPublicRepresentative());
							if(p.getPublicRepresentativeDetail()!=null){
								childVO.setPublicRepresentativeDetails(p.getPublicRepresentativeDetail());
							}
						}
						childVOs.add(childVO); 
					}
					parentVO.setChildVOs(childVOs);
					parentVO.setId(s.getId());
					parentVO.setName(s.getName());
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
					String startDate=dateFormat.format(s.getStartTime());
					String startTime=dateFormat1.format(s.getStartTime());
					parentVO.setStartDate(startDate);
					parentVO.setStartTime(startTime);
					parentVO.setReporter(s.getReporter().getUser().getFirstName());
					List<User> users=Slot.findDifferentLanguageUsersBySlot(s);
					String languageReporter="";
					for(int i=0;i<users.size();i++){
						languageReporter=languageReporter+users.get(i).getFirstName();
						if(i+1<users.size()){
							languageReporter=languageReporter+"/";
						}
					}
					parentVO.setLanguageReporter(languageReporter);
					parentVOs.add(parentVO);
				}
				
				
			}
			
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word1", reportFormat, "karyavrutt_sessionwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_sessionwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
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
			List<Slot> slots=Slot.findSlotsByReporterAndRoster(roster,reporter);
			List<ParentVO> parentVOs=new ArrayList<ParentVO>();
			for(Slot s:slots){
				ParentVO parentVO=new ParentVO();
				List<ChildVO> childVOs=new ArrayList<ChildVO>();
				List<Proceeding> proceedings=Proceeding.findAllFilledProceedingBySlot(s);
				for(Proceeding proc:proceedings){
					List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proc, "orderNo", "asc", locale.toString());
					for(Part p:parts){
						ChildVO childVO=new ChildVO();
						childVO.setId(p.getId());
						childVO.setProceedingContent(p.getRevisedContent());
						childVO.setChairperson(p.getChairPerson());
						childVO.setMainHeading(p.getMainHeading());
						childVO.setMemberrole(p.getChairPersonRole().getName());
						childVO.setOrderNo(p.getOrderNo());
						childVO.setPageHeading(p.getPageHeading());
						Member primaryMember=p.getPrimaryMember();
						if(primaryMember!=null){
							List<HouseMemberRoleAssociation> hrma=primaryMember.getHouseMemberRoleAssociations();
							for(HouseMemberRoleAssociation h:hrma){
								if(h.getHouse().equals(session.getHouse())){
									MemberRole memberRole=h.getRole();
									if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
										childVO.setPrimaryMember(memberRole.getName());
									}else{
										childVO.setPrimaryMember(primaryMember.getFullname());
									}
		
								}
							}
						
							
							if(p.getIsConstituencyRequired()){
								House house=null;
								house=House.find(houseType, new Date(), locale.toString());
								MasterVO masterVo=null;
								if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
									 masterVo=Member.findConstituencyByAssemblyId(primaryMember.getId(), house.getId());
									
								}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
									Date currentDate=new Date();
									String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
									masterVo=Member.findConstituencyByCouncilDates(primaryMember.getId(), house.getId(), "DATE", date, date);
								}
								childVO.setConstituency(masterVo.getName());
							}
							if(p.getPrimaryMemberDesignation()!=null){
							childVO.setPrimaryMemberDesignation(p.getPrimaryMemberDesignation().getName());
							}
							if(p.getPrimaryMemberMinistry()!=null){
								childVO.setPrimaryMemberMinistry(p.getPrimaryMemberMinistry().getName());
							}
							if(p.getPrimaryMemberSubDepartment()!=null){
								childVO.setPrimaryMemberSubDepartment(p.getPrimaryMemberSubDepartment().getName());
							}
						}
						Member substituteMember=p.getSubstituteMember();
						if(substituteMember!=null){
							childVO.setSubstituteMember(substituteMember.getFullname());
							if(p.getSubstituteMemberDesignation()!=null){
								childVO.setSubstituteMemberDesignation(p.getSubstituteMemberDesignation().getName());
							}
							if(p.getSubstituteMemberMinistry()!=null){
								childVO.setSubstituteMemberMinistry(p.getSubstituteMemberMinistry().getName());
							}
							if(p.getSubstituteMemberSubDepartment()!=null){
								childVO.setSubstituteMemberSubDepartment(p.getSubstituteMemberSubDepartment().getName());
							}
						}
						if(p.getPublicRepresentative()!=null){
							childVO.setPublicRepresentative(p.getPublicRepresentative());
							if(p.getPublicRepresentativeDetail()!=null){
								childVO.setPublicRepresentativeDetails(p.getPublicRepresentativeDetail());
							}
						}
						childVOs.add(childVO); 
					}
					parentVO.setChildVOs(childVOs);
					parentVO.setId(s.getId());
					parentVO.setName(s.getName());
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
					String startDate=dateFormat.format(s.getStartTime());
					String startTime=dateFormat1.format(s.getStartTime());
					parentVO.setStartDate(startDate);
					parentVO.setStartTime(startTime);
					parentVO.setReporter(s.getReporter().getUser().getFirstName());
					List<User> users=Slot.findDifferentLanguageUsersBySlot(s);
					String languageReporter="";
					for(int i=0;i<users.size();i++){
						languageReporter=languageReporter+users.get(i).getFirstName();
						if(i+1<users.size()){
							languageReporter=languageReporter+"/";
						}
					}
					parentVO.setLanguageReporter(languageReporter);
					parentVOs.add(parentVO);
				}
				
				
			}
			
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word1", reportFormat, "karyavrutt_reporterwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_reporterwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
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
			for(int i=0;i<result.size();i++){
				Object[] row = (Object[]) result.get(i);
				if(row[10]!=null){
					Member member1=Member.findById(Member.class, Long.parseLong(row[9].toString()));
					List<HouseMemberRoleAssociation> hrma=member1.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[10]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
			}
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
	
	@RequestMapping(value="/memberwisereport1",method=RequestMethod.GET)
	public String getMemberWiseReport1(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){

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
			for(int i=0;i<result.size();i++){
				Object[] row = (Object[]) result.get(i);
				if(row[10]!=null){
					Member member1=Member.findById(Member.class, Long.parseLong(row[9].toString()));
					List<HouseMemberRoleAssociation> hrma=member1.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[10]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
			}
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

			retVal = "proceeding/dailymemberwiseproceeding1";
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
			List<Slot> slots=Slot.findSlotsByMemberAndRoster(roster,member);
			List<ParentVO> parentVOs=new ArrayList<ParentVO>();
			for(Slot s:slots){
				ParentVO parentVO=new ParentVO();
				List<ChildVO> childVOs=new ArrayList<ChildVO>();
				List<Proceeding> proceedings=Proceeding.findAllFilledProceedingBySlot(s);
				for(Proceeding proc:proceedings){
					List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proc, "orderNo", "asc", locale.toString());
					for(Part p:parts){
						ChildVO childVO=new ChildVO();
						childVO.setId(p.getId());
						childVO.setProceedingContent(p.getRevisedContent());
						childVO.setChairperson(p.getChairPerson());
						childVO.setMainHeading(p.getMainHeading());
						childVO.setMemberrole(p.getChairPersonRole().getName());
						childVO.setOrderNo(p.getOrderNo());
						childVO.setPageHeading(p.getPageHeading());
						Member primaryMember=p.getPrimaryMember();
						if(primaryMember!=null){
							List<HouseMemberRoleAssociation> hrma=primaryMember.getHouseMemberRoleAssociations();
							for(HouseMemberRoleAssociation h:hrma){
								if(h.getHouse().equals(session.getHouse())){
									MemberRole memberRole=h.getRole();
									if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
										childVO.setPrimaryMember(memberRole.getName());
									}else{
										childVO.setPrimaryMember(primaryMember.getFullname());
									}
		
								}
							}
						
							
							if(p.getIsConstituencyRequired()){
								House house=null;
								house=House.find(houseType, new Date(), locale.toString());
								MasterVO masterVo=null;
								if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
									 masterVo=Member.findConstituencyByAssemblyId(primaryMember.getId(), house.getId());
									
								}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
									Date currentDate=new Date();
									String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
									masterVo=Member.findConstituencyByCouncilDates(primaryMember.getId(), house.getId(), "DATE", date, date);
								}
								childVO.setConstituency(masterVo.getName());
							}
							if(p.getPrimaryMemberDesignation()!=null){
							childVO.setPrimaryMemberDesignation(p.getPrimaryMemberDesignation().getName());
							}
							if(p.getPrimaryMemberMinistry()!=null){
								childVO.setPrimaryMemberMinistry(p.getPrimaryMemberMinistry().getName());
							}
							if(p.getPrimaryMemberSubDepartment()!=null){
								childVO.setPrimaryMemberSubDepartment(p.getPrimaryMemberSubDepartment().getName());
							}
						}
						Member substituteMember=p.getSubstituteMember();
						if(substituteMember!=null){
							childVO.setSubstituteMember(substituteMember.getFullname());
							if(p.getSubstituteMemberDesignation()!=null){
								childVO.setSubstituteMemberDesignation(p.getSubstituteMemberDesignation().getName());
							}
							if(p.getSubstituteMemberMinistry()!=null){
								childVO.setSubstituteMemberMinistry(p.getSubstituteMemberMinistry().getName());
							}
							if(p.getSubstituteMemberSubDepartment()!=null){
								childVO.setSubstituteMemberSubDepartment(p.getSubstituteMemberSubDepartment().getName());
							}
						}
						if(p.getPublicRepresentative()!=null){
							childVO.setPublicRepresentative(p.getPublicRepresentative());
							if(p.getPublicRepresentativeDetail()!=null){
								childVO.setPublicRepresentativeDetails(p.getPublicRepresentativeDetail());
							}
						}
						childVOs.add(childVO); 
					}
					parentVO.setChildVOs(childVOs);
					parentVO.setId(s.getId());
					parentVO.setName(s.getName());
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
					String startDate=dateFormat.format(s.getStartTime());
					String startTime=dateFormat1.format(s.getStartTime());
					parentVO.setStartDate(startDate);
					parentVO.setStartTime(startTime);
					parentVO.setReporter(s.getReporter().getUser().getFirstName());
					List<User> users=Slot.findDifferentLanguageUsersBySlot(s);
					String languageReporter="";
					for(int i=0;i<users.size();i++){
						languageReporter=languageReporter+users.get(i).getFirstName();
						if(i+1<users.size()){
							languageReporter=languageReporter+"/";
						}
					}
					parentVO.setLanguageReporter(languageReporter);
					parentVOs.add(parentVO);
				}
				
				
			}
			
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word1", reportFormat, "karyavrutt_memberwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_memberwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}
	
	
	
	@RequestMapping(value="/memberwiseproceeding1",method=RequestMethod.GET)
	public @ResponseBody void viewMemberWiseReports(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
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
			List<Slot> slots=Slot.findSlotsByMemberAndRoster(roster,member);
			List<ParentVO> parentVOs=new ArrayList<ParentVO>();
			for(Slot s:slots){
				List<Proceeding> proceedings=Proceeding.findAllFilledProceedingBySlot(s);
				for(Proceeding proc:proceedings){
					List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proc, "orderNo", "asc", locale.toString());
					for(int i=0;i<parts.size();i++){
						Part part=parts.get(i);
						Member primaryMember=part.getPrimaryMember();
						Member substituteMember=part.getSubstituteMember();
						String mainHeading="";
						String pageHeading="";
						if((primaryMember!=null && primaryMember.getId().equals(member.getId()))||(substituteMember!=null && substituteMember.getId().equals(member.getId()))){
							ParentVO parentVO=new ParentVO();
							List<ChildVO> childVOs=new ArrayList<ChildVO>();
							for(int j=i;j<parts.size();j++){
								
								Part part1=parts.get(j);
								if(j==i || (part1.getMainHeading()!=null && part1.getMainHeading().equals(mainHeading))||(part1.getPageHeading()!=null && part1.getPageHeading().equals(pageHeading))){
									ChildVO childVO=new ChildVO();
									childVO.setId(part1.getId());
									childVO.setProceedingContent(part1.getRevisedContent());
									childVO.setChairperson(part1.getChairPerson());
									childVO.setMainHeading(part1.getMainHeading());
									childVO.setMemberrole(part1.getChairPersonRole().getName());
									childVO.setOrderNo(part1.getOrderNo());
									childVO.setPageHeading(part1.getPageHeading());
									Member pMember=part1.getPrimaryMember();
									Member sMember=part1.getSubstituteMember();
									if(pMember!=null){
										List<HouseMemberRoleAssociation> hrma=pMember.getHouseMemberRoleAssociations();
										for(HouseMemberRoleAssociation h:hrma){
											if(h.getHouse().equals(session.getHouse())){
												MemberRole memberRole=h.getRole();
												if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
														||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
														||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
														||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
														||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
														||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
													childVO.setPrimaryMember(memberRole.getName());
												}else{
													childVO.setPrimaryMember(pMember.getFullname());
												}
					
											}
										}
									
										
										if(part1.getIsConstituencyRequired()){
											House house=null;
											house=House.find(houseType, new Date(), locale.toString());
											MasterVO masterVo=null;
											if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
												 masterVo=Member.findConstituencyByAssemblyId(pMember.getId(), house.getId());
												
											}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
												Date currentDate=new Date();
												String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
												masterVo=Member.findConstituencyByCouncilDates(pMember.getId(), house.getId(), "DATE", date, date);
											}
											childVO.setConstituency(masterVo.getName());
										}
										if(part1.getPrimaryMemberDesignation()!=null){
										childVO.setPrimaryMemberDesignation(part1.getPrimaryMemberDesignation().getName());
										}
										if(part1.getPrimaryMemberMinistry()!=null){
											childVO.setPrimaryMemberMinistry(part1.getPrimaryMemberMinistry().getName());
										}
										if(part1.getPrimaryMemberSubDepartment()!=null){
											childVO.setPrimaryMemberSubDepartment(part1.getPrimaryMemberSubDepartment().getName());
										}
									}
									if(sMember!=null){
										childVO.setSubstituteMember(sMember.getFullname());
										if(part1.getSubstituteMemberDesignation()!=null){
											childVO.setSubstituteMemberDesignation(part1.getSubstituteMemberDesignation().getName());
										}
										if(part1.getSubstituteMemberMinistry()!=null){
											childVO.setSubstituteMemberMinistry(part1.getSubstituteMemberMinistry().getName());
										}
										if(part1.getSubstituteMemberSubDepartment()!=null){
											childVO.setSubstituteMemberSubDepartment(part1.getSubstituteMemberSubDepartment().getName());
										}
									}
									if(part1.getPublicRepresentative()!=null){
										childVO.setPublicRepresentative(part1.getPublicRepresentative());
										if(part1.getPublicRepresentativeDetail()!=null){
											childVO.setPublicRepresentativeDetails(part1.getPublicRepresentativeDetail());
										}
									}
									childVOs.add(childVO);
									i=j+1;;
								}
							}
							parentVO.setChildVOs(childVOs);
							parentVO.setId(s.getId());
							parentVO.setName(s.getName());
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
							SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
							String startDate=dateFormat.format(s.getStartTime());
							String startTime=dateFormat1.format(s.getStartTime());
							parentVO.setStartDate(startDate);
							parentVO.setStartTime(startTime);
							parentVO.setReporter(s.getReporter().getUser().getFirstName());
							List<User> users=Slot.findDifferentLanguageUsersBySlot(s);
							String languageReporter="";
							for(int a=0;a<users.size();a++){
								languageReporter=languageReporter+users.get(a).getFirstName();
								if(a+1<users.size()){
									languageReporter=languageReporter+"/";
								}
							}
							parentVO.setLanguageReporter(languageReporter);
							parentVOs.add(parentVO);
							
						}
					}
				}
			}
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word1", reportFormat, "karyavrutt_memberwise1", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_memberwise1", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}
	
	@RequestMapping(value="/memberwisereport2",method=RequestMethod.GET)
	public String getMemberWiseReport2(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){

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
			List result=Query.findReport(ApplicationConstants.RIS_MEMBER_WISE_REPORT2, parametersMap);		
			for(int i=0;i<result.size();i++){
				Object[] row = (Object[]) result.get(i);
				if(row[10]!=null){
					Member member1=Member.findById(Member.class, Long.parseLong(row[9].toString()));
					List<HouseMemberRoleAssociation> hrma=member1.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[10]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
			}
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

			retVal = "proceeding/dailymemberwiseproceeding2";
		}

		return retVal;
	}
	
	@RequestMapping(value="/memberwiseproceeding2",method=RequestMethod.GET)
	public @ResponseBody void viewMemberWiseReport2(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
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
			List<Slot> slots=Slot.findSlotsByMemberAndRoster(roster,member);
			
			List<ParentVO> parentVOs=new ArrayList<ParentVO>();
			for(Slot s:slots){
				ParentVO parentVO=new ParentVO();
				List<ChildVO> childVOs=new ArrayList<ChildVO>();
				List<Proceeding> proceedings=Proceeding.findAllFilledProceedingBySlot(s);
				for(Proceeding proc:proceedings){
					List<Part> parts=Part.findPartsByProceedingAndMember(proc,member);
					for(Part p:parts){
						ChildVO childVO=new ChildVO();
						childVO.setId(p.getId());
						childVO.setProceedingContent(p.getRevisedContent());
						childVO.setChairperson(p.getChairPerson());
						childVO.setMainHeading(p.getMainHeading());
						childVO.setMemberrole(p.getChairPersonRole().getName());
						childVO.setOrderNo(p.getOrderNo());
						childVO.setPageHeading(p.getPageHeading());
						Member primaryMember=p.getPrimaryMember();
						if(primaryMember!=null){
							List<HouseMemberRoleAssociation> hrma=primaryMember.getHouseMemberRoleAssociations();
							for(HouseMemberRoleAssociation h:hrma){
								if(h.getHouse().equals(session.getHouse())){
									MemberRole memberRole=h.getRole();
									if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
										childVO.setPrimaryMember(memberRole.getName());
									}else{
										childVO.setPrimaryMember(primaryMember.getFullname());
									}
		
								}
							}
						
							
							if(p.getIsConstituencyRequired()){
								House house=null;
								house=House.find(houseType, new Date(), locale.toString());
								MasterVO masterVo=null;
								if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
									 masterVo=Member.findConstituencyByAssemblyId(primaryMember.getId(), house.getId());
									
								}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
									Date currentDate=new Date();
									String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
									masterVo=Member.findConstituencyByCouncilDates(primaryMember.getId(), house.getId(), "DATE", date, date);
								}
								childVO.setConstituency(masterVo.getName());
							}
							if(p.getPrimaryMemberDesignation()!=null){
							childVO.setPrimaryMemberDesignation(p.getPrimaryMemberDesignation().getName());
							}
							if(p.getPrimaryMemberMinistry()!=null){
								childVO.setPrimaryMemberMinistry(p.getPrimaryMemberMinistry().getName());
							}
							if(p.getPrimaryMemberSubDepartment()!=null){
								childVO.setPrimaryMemberSubDepartment(p.getPrimaryMemberSubDepartment().getName());
							}
						}
						Member substituteMember=p.getSubstituteMember();
						if(substituteMember!=null){
							childVO.setSubstituteMember(substituteMember.getFullname());
							if(p.getSubstituteMemberDesignation()!=null){
								childVO.setSubstituteMemberDesignation(p.getSubstituteMemberDesignation().getName());
							}
							if(p.getSubstituteMemberMinistry()!=null){
								childVO.setSubstituteMemberMinistry(p.getSubstituteMemberMinistry().getName());
							}
							if(p.getSubstituteMemberSubDepartment()!=null){
								childVO.setSubstituteMemberSubDepartment(p.getSubstituteMemberSubDepartment().getName());
							}
						}
						if(p.getPublicRepresentative()!=null){
							childVO.setPublicRepresentative(p.getPublicRepresentative());
							if(p.getPublicRepresentativeDetail()!=null){
								childVO.setPublicRepresentativeDetails(p.getPublicRepresentativeDetail());
							}
						}
						childVOs.add(childVO); 
					}
					parentVO.setChildVOs(childVOs);
					parentVO.setId(s.getId());
					parentVO.setName(s.getName());
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
					String startDate=dateFormat.format(s.getStartTime());
					String startTime=dateFormat1.format(s.getStartTime());
					parentVO.setStartDate(startDate);
					parentVO.setStartTime(startTime);
					parentVO.setReporter(s.getReporter().getUser().getFirstName());
					List<User> users=Slot.findDifferentLanguageUsersBySlot(s);
					String languageReporter="";
					for(int i=0;i<users.size();i++){
						languageReporter=languageReporter+users.get(i).getFirstName();
						if(i+1<users.size()){
							languageReporter=languageReporter+"/";
						}
					}
					parentVO.setLanguageReporter(languageReporter);
					parentVOs.add(parentVO);
				}
			}
			
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word1", reportFormat, "karyavrutt_memberwise2", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_memberwise2", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}
	
	@Transactional
	@RequestMapping(value="/part/replace",method=RequestMethod.POST)
	public @ResponseBody List doReplace(HttpServletRequest request, HttpServletResponse response, ModelMap model, Locale locale){
		List matchedParts = null;
		try{			
			String strProceeding=request.getParameter("proceedingId");
			String strSearchTerm = request.getParameter("searchTerm");
			String strReplaceTerm = request.getParameter("replaceTerm");
			String strUndoCount = request.getParameter("undoCount");
			String strRedoCount = request.getParameter("redoCount");
			Integer undoCount = Integer.valueOf(strUndoCount);
			Integer redoCount = Integer.valueOf(strRedoCount);
			
			if (strProceeding != null && !strProceeding.equals("")){
				Proceeding proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
				matchedParts = Part.findAllEligibleForReplacement(proceeding, strSearchTerm, strReplaceTerm, locale.toString());
				if(matchedParts != null && !matchedParts.isEmpty()){
					for(int i = 0; i < matchedParts.size(); i++){
						
						Object[] objArr = (Object[])matchedParts.get(i);
						if(!objArr[1].toString().equals(objArr[3].toString())){
							Part partToBeReplaced = Part.findById(Part.class, Long.valueOf(objArr[0].toString()));
							/****Create draft****/
							PartDraft pd = new PartDraft();
							pd.setEditedBy(this.getCurrentUser().getActualUsername());
							pd.setEditedOn(new Date());
							pd.setLocale(locale.toString());
							if(objArr[4]!=null){
								pd.setMainHeading(objArr[4].toString());
							}
							if(objArr[5]!=null ){
								pd.setOriginalMainHeading(objArr[5].toString());
							}
							if(objArr[6]!=null){
								pd.setReplacedMainHeading(objArr[6].toString());
							}
							if(objArr[7]!=null){
								pd.setPageHeading(objArr[7].toString());
							}
							if(objArr[8]!=null){
								pd.setOriginalPageHeading(objArr[8].toString());
							}
							if(objArr[9]!=null){
								pd.setReplacedPageHeading(objArr[9].toString());
							}
							pd.setOriginalText(objArr[2].toString());
							pd.setReplacedText(objArr[3].toString());
							pd.setRevisedContent(objArr[3].toString());
							pd.setUndoCount(undoCount);
							pd.setRedoCount(redoCount);
							pd.setUniqueIdentifierForUndo(UUID.randomUUID().toString());
							pd.setUniqueIdentifierForRedo(UUID.randomUUID().toString());
							pd.setWorkflowCopy(false);
							
							/****Attach undoCount and undoUID in the result list****/
							((Object[])matchedParts.get(i))[10] = partToBeReplaced.getId().toString()+":"+pd.getUndoCount()+":"+pd.getUniqueIdentifierForUndo();
							((Object[])matchedParts.get(i))[11] = partToBeReplaced.getId().toString()+":"+pd.getRedoCount()+":"+pd.getUniqueIdentifierForRedo();
							((Object[])matchedParts.get(i))[12] = "include";
							
							partToBeReplaced.getPartDrafts().add(pd);
							partToBeReplaced.setRevisedContent(objArr[3].toString());
							partToBeReplaced.setMainHeading(objArr[6].toString());
							partToBeReplaced.setPageHeading(objArr[9].toString());
							partToBeReplaced.merge();
						}else{
							((Object[])matchedParts.get(i))[12] = "exclude";
						}
					}
				}
			}		
						
		}catch (Exception e) {
			e.printStackTrace();
		}
		return matchedParts;
	}
	
	/****Replace Functionality in Proceeding wise Report****/
	@Transactional
	@RequestMapping(value="/part/undolastchange/{partid}",method=RequestMethod.POST)
	public @ResponseBody PartDraftVO doUndo(@PathVariable(value="partid") Long id, HttpServletRequest request, HttpServletResponse response, Locale locale){
		PartDraftVO partDraftVO = null;
		try{
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			String strUniqueUndoId = request.getParameter("uniqueIdentifierForUndo");
			String strUndoCount = request.getParameter("undoCount");
			
			if(strUniqueUndoId != null && !strUniqueUndoId.isEmpty()
					&& strUndoCount != null && !strUndoCount.isEmpty()){
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("uniqueIdentifierForUndo", new String[]{strUniqueUndoId});
				parameters.put("undoCount", new String[]{strUndoCount});
				parameters.put("editedOn", new String[]{FormaterUtil.formatDateToString(new Date(), ApplicationConstants.DB_DATEFORMAT)});
				parameters.put("editedBy", new String[]{this.getCurrentUser().getActualUsername()});
				List pds = Query.findReport("RIS_FIND_PART_DRAFTS", parameters);
				if(pds != null){
					for(Object o : pds){
						Object[] pd = (Object[])o;
						partDraftVO = new PartDraftVO();
						partDraftVO.setId(Long.valueOf(pd[0].toString()));
						if(pd[1]!=null){
							partDraftVO.setContent(pd[1].toString());
						}
						if(pd[3]!=null){
							partDraftVO.setPageHeading(pd[3].toString());
						}
						if(pd[5]!=null){
							partDraftVO.setMainHeading(pd[5].toString());
						}
						
					}
					Part pp = Part.findById(Part.class, id);
					pp.setRevisedContent(partDraftVO.getContent());
					pp.setMainHeading(partDraftVO.getMainHeading());
					pp.setPageHeading(partDraftVO.getPageHeading());
					pp.merge();
				}
				
				return partDraftVO;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/***Redo Functionality****/
	@Transactional
	@RequestMapping(value="/part/redolastchange/{partid}",method=RequestMethod.POST)
	public @ResponseBody PartDraftVO doRedo(@PathVariable(value="partid") Long id, HttpServletRequest request, HttpServletResponse response, Locale locale){
		PartDraftVO partDraftVO = null;
		try{
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			String strUrData = request.getParameter("urData");
			if(strUrData != null && !strUrData.isEmpty()){
				String[] data = strUrData.split(":"); 
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("uniqueIdentifierForUndo", new String[]{data[2]});
				parameters.put("undoCount", new String[]{data[1]});
				parameters.put("editedOn", new String[]{FormaterUtil.formatDateToString(new Date(), ApplicationConstants.DB_DATEFORMAT)});
				parameters.put("editedBy", new String[]{this.getCurrentUser().getActualUsername()});
				List pds = Query.findReport("RIS_FIND_PART_DRAFTS", parameters);
				if(pds != null){
					for(Object o : pds){
						Object[] pd = (Object[])o;
						partDraftVO = new PartDraftVO();
						partDraftVO.setId(Long.valueOf(pd[0].toString()));
						if(pd[2]!=null){
							partDraftVO.setContent(pd[2].toString());
						}
						if(pd[4]!=null){
							partDraftVO.setPageHeading(pd[4].toString());
						}
						if(pd[6]!=null){
							partDraftVO.setMainHeading(pd[6].toString());
						}
					}
					Part pp = Part.findById(Part.class, id);
					pp.setRevisedContent(partDraftVO.getContent());
					pp.setMainHeading(partDraftVO.getMainHeading());
					pp.setPageHeading(partDraftVO.getPageHeading());
					pp.merge();
				}
				return partDraftVO;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value="/part/updatePart",method=RequestMethod.POST)
	public @ResponseBody MasterVO updatePart(HttpServletRequest request,ModelMap model,Locale locale){
		String partId=request.getParameter("partId");
		String partField=request.getParameter("partField");
		String partText=request.getParameter("editedContent");
		String editedBy=request.getParameter("editedBy");
		String strUndoCount = request.getParameter("undoCount");
		MasterVO masterVO = null;
		if(partId!=null && !partId.equals("") 
			&& partField!=null && !partId.equals("")
			&& partText!=null && !partText.equals("")){
			Part part=Part.findById(Part.class, Long.parseLong(partId));
			if(part!=null){
				PartDraft partDraft=new PartDraft();
				if(partField.equals("mainHeading")){
					partDraft.setOriginalMainHeading(part.getMainHeading());
					part.setMainHeading(partText);
					partDraft.setMainHeading(partText);
					partDraft.setPageHeading(part.getPageHeading());
					partDraft.setRevisedContent(part.getRevisedContent());
					partDraft.setReplacedMainHeading(partText);
				}else if(partField.equals("pageHeading")){
					partDraft.setOriginalPageHeading(part.getPageHeading());
					part.setPageHeading(partText);
					partDraft.setPageHeading(partText);
					partDraft.setReplacedPageHeading(partText);
					partDraft.setMainHeading(part.getMainHeading());
					partDraft.setRevisedContent(part.getRevisedContent());
				}else{
					partDraft.setOriginalText(part.getRevisedContent());
					part.setRevisedContent(partText);
					partDraft.setRevisedContent(partText);
					partDraft.setReplacedText(partText);
					partDraft.setMainHeading(part.getMainHeading());
					partDraft.setPageHeading(part.getPageHeading());
				}
				partDraft.setEditedBy(editedBy);
				partDraft.setLocale(locale.toString());
				partDraft.setEditedOn(new Date());
				partDraft.setUndoCount(Integer.valueOf(strUndoCount));
				partDraft.setUniqueIdentifierForUndo(UUID.randomUUID().toString());
				partDraft.setWorkflowCopy(false);
				part.getPartDrafts().add(partDraft);
				part.merge();
				masterVO = new MasterVO();
				masterVO.setId(part.getId());
				masterVO.setValue(part.getId()+":"+partDraft.getUndoCount()+":"+partDraft.getUniqueIdentifierForUndo());
			}
		}
		return masterVO;
	}
}
