/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.webservices.MasterWebService.java
 * Created On: Apr 17, 2012
 */

package org.mkcl.els.webservices;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.HouseTypeVO;
import org.mkcl.els.common.vo.HouseVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProceedingVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.Citizen;
import org.mkcl.els.domain.CitizenQuestion;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Gender;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.MaritalStatus;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Part;
//import org.mkcl.els.domain.PartDraft;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.SubDepartment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class MasterWebService.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/ws/masters")
public class MasterWebService {

    /**
     * Gets the constituencies.
     *
     * @param locale the locale
     * @param housetype the housetype
     * @return the constituencies
     */
    @RequestMapping(value = "/constituencies/{housetype}/{locale}")
    public @ResponseBody
    List<MasterVO> getConstituencies(@PathVariable("locale") final String locale,@PathVariable("housetype") final String housetype) {
        //if(housetype.equals(ApplicationConstants.LOWER_HOUSE)){
        //String defaultstate=((CustomParameter)CustomParameter.findByName(CustomParameter.class, "DEFAULT_STATE", locale)).getValue();
        //return Constituency.findVOByDefaultStateAndHouseType(defaultstate, housetype, locale,"name",ApplicationConstants.ASC);
        //}else {
        HouseType houseType2=HouseType.findByFieldName(HouseType.class, "type", housetype, locale);
        List<Constituency> constituencies= Constituency.findAllByFieldName(Constituency.class, "houseType", houseType2, "name", ApplicationConstants.ASC, locale);
        List<MasterVO> constituenciesVO=new ArrayList<MasterVO>();
        for(Constituency i:constituencies){
            MasterVO masterVO=new MasterVO(i.getId(),i.getDisplayName());
            constituenciesVO.add(masterVO);
        }
        return constituenciesVO;
        //}
    }

    @RequestMapping(value = "/districts/{housetype}/{locale}")
    public @ResponseBody
    List<Reference> getDistricts(@PathVariable("locale") final String locale,@PathVariable("housetype") final String housetype) {
        String defaultstate=((CustomParameter)CustomParameter.findByName(CustomParameter.class, "DEFAULT_STATE", locale)).getValue();
        State state=State.findByName(State.class,defaultstate, locale);
        try {
			return District.findDistrictsRefByStateId(state.getId(), "name", ApplicationConstants.ASC, locale);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }

    /**
     * Gets the parties.
     *
     * @param locale the locale
     * @return the parties
     */
    @RequestMapping(value = "/parties/{locale}")
    public @ResponseBody
    List<MasterVO> getParties(@PathVariable final String locale) {
        List<Party> parties= Party.findAll(Party.class,"name",ApplicationConstants.ASC, locale);
        List<MasterVO> partiesVO=new ArrayList<MasterVO>();
        for(Party i:parties){
        	String partySymbolPhoto = i.findCurrentPartySymbolPhoto();
        	MasterVO masterVO=new MasterVO(i.getId(),i.getName(),partySymbolPhoto);
        	partiesVO.add(masterVO);
        }
        return partiesVO;
    }

    /**
     * Gets the genders.
     *
     * @param locale the locale
     * @return the genders
     */
    @RequestMapping(value = "/genders/{locale}")
    public @ResponseBody
    List<MasterVO> getGenders(@PathVariable final String locale) {
    	List<Gender> genders=Gender.findAll(Gender.class,"name",ApplicationConstants.ASC, locale);
    	List<MasterVO> gendersVOs=new ArrayList<MasterVO>();
        for(Gender i:genders){
        	MasterVO masterVO=new MasterVO(i.getId(),i.getName());
        	gendersVOs.add(masterVO);
        }
        return gendersVOs;
    }

    /**
     * Gets the marital status.
     *
     * @param locale the locale
     * @return the marital status
     */
    @RequestMapping(value = "/maritalstatus/{locale}")
    public @ResponseBody
    List<MasterVO> getMaritalStatus(@PathVariable final String locale) {
        List<MaritalStatus> maritalStatus= MaritalStatus.findAll(MaritalStatus.class,"name",ApplicationConstants.ASC, locale);
        List<MasterVO> maritalStatusVOs=new ArrayList<MasterVO>();
        for(MaritalStatus i:maritalStatus){
        	MasterVO masterVO=new MasterVO(i.getId(),i.getName());
        	maritalStatusVOs.add(masterVO);
        }
        return maritalStatusVOs;
    }
    
    @RequestMapping(value = "/memberroles/{houseType}/{locale}")
    public @ResponseBody
    List<MasterVO> getMemberRoles(@PathVariable final String houseType,@PathVariable final String locale) {
        List<MemberRole> memberRoles=MemberRole.findByHouseType(houseType, locale);       	
        List<MasterVO> masterVOs=new ArrayList<MasterVO>();
        for(MemberRole i:memberRoles){
        	MasterVO masterVO=new MasterVO(i.getId(),i.getName());
        	masterVOs.add(masterVO);
        }
        return masterVOs;
    }
    
    @RequestMapping(value = "/member/{house}/{memberrole}/{locale}")
    public @ResponseBody
    Long getMemberByMemberRole(@PathVariable final Long house,
    		@PathVariable final Long memberrole,
    		@PathVariable final String locale) {
        Long memberid=null;
        List<Member> members=Member.findByMemberRole(house,memberrole,locale);
        if(members!=null&&!members.isEmpty()){
        	return members.get(0).getId();
        }
        return memberid;
    }

    @RequestMapping(value = "/months/{locale}")
    public @ResponseBody
    List<MasterVO> getMonths(@PathVariable final String locale) {
    	   return FormaterUtil.getMonths(locale);
    }

    @RequestMapping(value = "/houseType/{locale}")
    public @ResponseBody
    List<Reference> getHouseType(@PathVariable final String locale) {
        List<HouseType> houseTypes= HouseType.findAll(HouseType.class,"name",ApplicationConstants.ASC, locale);
        List<Reference> houseTypeVOs=new ArrayList<Reference>();
        for(HouseType i:houseTypes){
            Reference reference=new Reference(i.getType(),i.getName());
            houseTypeVOs.add(reference);
        }
        return houseTypeVOs;
    }
    
    @RequestMapping(value = "/houseTypes/{locale}")
    public @ResponseBody
    List<HouseTypeVO> getHouseTypes(@PathVariable final String locale) {
        List<HouseType> houseTypes= HouseType.findAll(HouseType.class,"name",ApplicationConstants.ASC, locale);
        List<HouseTypeVO> houseTypeVOs=new ArrayList<HouseTypeVO>();
        for(HouseType i:houseTypes){
        	HouseTypeVO houseTypeVO=new HouseTypeVO(i.getType(), i.getName());
            houseTypeVOs.add(houseTypeVO);
        }
        return houseTypeVOs;
    }

    @RequestMapping(value = "/house/{houseType}/{locale}")
    public @ResponseBody
    List<Reference> getHouse(@PathVariable final String locale,@PathVariable final String houseType) {
        List<House> houses;
		try {
			houses = House.findByHouseType(houseType, locale);
			List<Reference> houseVOs=new ArrayList<Reference>();
			for(House i:houses){
			    Reference reference=new Reference(String.valueOf(i.getId()),i.getDisplayName());
			    houseVOs.add(reference);
			}
			return houseVOs;  
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		      
    }
    
    @RequestMapping(value = "/houses/{houseType}/{locale}")
    public @ResponseBody
    List<HouseVO> getHouses(@PathVariable final String locale,@PathVariable final String houseType) {
        List<House> houses;
		try {
			houses = House.findByHouseType(houseType, locale);
			List<HouseVO> houseVOs=new ArrayList<HouseVO>();
			for(House i:houses){
				HouseVO houseVO=new HouseVO(i.getId(), i.getDisplayName());
			    houseVOs.add(houseVO);
			}
			return houseVOs;  
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		      
    }
    
    @RequestMapping(value = "/{house}/headingcount/{locale}")
    public @ResponseBody
    Reference getHouseHeadingCount(@PathVariable final String locale,@PathVariable("house") final Long houseId) {
        House house= House.findById(House.class, houseId);
        Reference reference=new Reference();
        reference.setName(house.getDisplayName());
        reference.setNumber(String.valueOf(house.getTotalMembers()));
        return reference;
    }
    //Methods for Android App
    
    @RequestMapping(value = "/locale")
    public @ResponseBody
    List<MasterVO> getLocale() throws ELSException {
        List<ApplicationLocale> locales= ApplicationLocale.findAllLocale();
        
      
        List<MasterVO> localesVOs=new ArrayList<MasterVO>();
        for(ApplicationLocale i:locales){
        	MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setValue(i.getLocale());
			masterVO.setName(i.getDisplayName());
			localesVOs.add(masterVO);
        }
        return localesVOs;
    }
    
   /* @RequestMapping(value = "/member/{houseType}/{locale}")
    public @ResponseBody
    List<MasterVO> getMembersByHouseType(@PathVariable("houseType") final String houseType, @PathVariable("locale") final String locale) throws ELSException {
        List<Member> members= Member.findMembersWithHousetype(houseType, locale);
        
      
        List<MasterVO> membersVOs=new ArrayList<MasterVO>();
        for(Member i:members){
        	MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setValue(i.getLocale());
			masterVO.setName(i.getFullname());
			masterVO.setDisplayName(i.getAlias());
			membersVOs.add(masterVO);
        }
        return membersVOs;
    }*/
    
    @RequestMapping(value = "/member/{houseType}/{constituency}")
    public @ResponseBody
    List<MasterVO> getMembersByconstituency(@PathVariable("houseType") final String houseType,@PathVariable("constituency") final Long constituency) throws ELSException {
        List<Member> members= Member.findMembersWithconstituency(houseType,constituency);
        
      
        List<MasterVO> membersVOs=new ArrayList<MasterVO>();
        for(Member i:members){
        	MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setValue(i.getLocale());
			masterVO.setName(i.getFullname());
			masterVO.setDisplayName(i.getAlias());
			membersVOs.add(masterVO);
        }
        return membersVOs;
    }
    
    @RequestMapping(value = "/subdepartments/{locale}")
    public @ResponseBody
    List<MasterVO> getSubdepartments(@PathVariable("locale") final String locale) throws ELSException {
        List<SubDepartment> subdepartments= SubDepartment.findAll(SubDepartment.class, "name",ApplicationConstants.ASC, locale);
        
      
        List<MasterVO> subdepartmentsVOs=new ArrayList<MasterVO>();
        for(SubDepartment i:subdepartments){
        	MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setName(i.getName());
			masterVO.setIsSelected(i.getIsExpired());
			subdepartmentsVOs.add(masterVO);
        }
        return subdepartmentsVOs;
    }
    
    @RequestMapping(value = "/Citizen/{mobile}/{locale}")
    public @ResponseBody
    List<MasterVO> findCitizensBy(@PathVariable("mobile") final String mobile,@PathVariable("locale") final String locale) throws ELSException {
        List<Citizen> citizens= Citizen.findAllByFieldName(Citizen.class, "mobile", mobile,"mobile",ApplicationConstants.ASC, locale);
        
      
        List<MasterVO> citizensVOs=new ArrayList<MasterVO>();
        for(Citizen i:citizens){
        	MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setName(i.getName());
			masterVO.setFormattedNumber(i.getMobile());
			masterVO.setValue(i.getEmail());
			citizensVOs.add(masterVO);
        }
        return citizensVOs;
    }
    
    @RequestMapping(value = "/AddCitizen", method = RequestMethod.POST)
    public @ResponseBody
    Reference addCitizens(  HttpServletRequest request) throws ELSException {
    String name = request.getParameter("name");
    String mobile = request.getParameter("mobile");
    String email = request.getParameter("email");
    String locale = request.getParameter("locale");
    
    
       String c= Citizen.AddCitizen(name,mobile,email,locale);
       String status="failed";
       if(c!="ERROR")
       {
       	status="success";
       }
       
      
       Reference reference=new Reference();
       if (status=="success")
       {
       reference.setNumber(c);
       reference.setState(status);
       }else
       {
    	   reference.setState(status);
    	   reference.setRemark(c);
       }
       return reference; 
                  
    }
    
    @RequestMapping(value = "/AddCitizenQuestion",method = RequestMethod.POST)
    public @ResponseBody
    Reference addCitizenQuestion(HttpServletRequest request) throws ELSException {
    	String citizenID = request.getParameter("citizenID");
        String memberID = request.getParameter("memberID");
        String districtID = request.getParameter("districtID");
        String constituencyID = request.getParameter("constituencyID");
        String departmentID = request.getParameter("departmentID");
        String questionText = request.getParameter("questionText");
        String locale = request.getParameter("locale");
        
        byte[] by_new = questionText.getBytes();
        
        String c= CitizenQuestion.AddCitizenQuestion(citizenID,districtID,constituencyID,departmentID,questionText,memberID,locale);
        String status="failed";
        if(c!="ERROR")
        {
        	status="success";
        }
        
        
        Reference reference=new Reference();
        if (status=="success")
        {
        reference.setNumber(c);
        reference.setState(status);
        }else
        {
        	reference.setState(status);
        	reference.setRemark(c);
        }
        
        return reference; 
    }
    
    @RequestMapping(value = "/districts/{locale}")
    public @ResponseBody
    List<Reference> getDistricts(@PathVariable("locale") final String locale) {
        String defaultstate=((CustomParameter)CustomParameter.findByName(CustomParameter.class, "DEFAULT_STATE", locale)).getValue();
        State state=State.findByName(State.class,defaultstate, locale);
        try {
			return District.findDistrictsRefByStateId(state.getId(), "name", ApplicationConstants.ASC, locale);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    @RequestMapping(value = "/constituenciesByDistrict/{district}/{houseType}/{locale}")
    public @ResponseBody
    List<MasterVO> getConstituenciesByDistrict(@PathVariable("district") final String district,@PathVariable("houseType") final String houseType,@PathVariable("locale") final String locale) {
        
        try {
        	 List<Constituency> constituencies= Constituency.findConstituenciesByDistrictId(Long.parseLong(district),houseType, "name", ApplicationConstants.ASC, locale);
			
			 
	        List<MasterVO> constituenciesVOs=new ArrayList<MasterVO>();
	        for(Constituency i:constituencies){
	        	MasterVO masterVO=new MasterVO();
				masterVO.setId(i.getId());
				masterVO.setName(i.getName());
				masterVO.setValue(i.getDisplayName());
				constituenciesVOs.add(masterVO);
	        }
	        return constituenciesVOs;
	        
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    @RequestMapping(value = "/AddPart",method = RequestMethod.POST)
    public @ResponseBody
    Reference addPart(HttpServletRequest request) throws ELSException {
    	//Long partID = Long.parseLong("20600");
    	Long partID = Long.parseLong(request.getParameter("partID"));
   
        String byteArray=request.getParameter("byteArray");
        
        String xml = null;
        byte[] xmlData = new byte[request.getContentLength()];
        try {
        //Start reading XML Request as a Stream of Bytes
        
        	
        	
        InputStream sis = request.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(sis);

        //bis.read(xmlData, 0, xmlData.length);
        
        int index = 0;
        while (index < xmlData.length)
        {
            int bytesRead = bis.read(xmlData, index, xmlData.length - index);
            if (bytesRead == 0)
            {
                break;
            }
            index += bytesRead;
        }
        
        if (request.getCharacterEncoding() != null) {
                xml = new String(xmlData, "UTF-8");
             
        } else {
                xml = new String(xmlData);
                
        }
        } catch (IOException ioe) {
            
         }
        Part part=Part.findById(Part.class,partID );
       	if(part.getProceedingContent() != null && !part.getProceedingContent().isEmpty())
        {
       	 part.setRevisedContent(xml);
        }else
        {
        	part.setProceedingContent(xml);
        }
       	if(part.getEntryDate() == null){
       		part.setEntryDate(new Date());
       	}
        if(part.getOrderNo() == null){
        	part.setOrderNo(1);
        }
           
/*        if(part.getId() != null){
        	List<PartDraft> partDrafts = part.getPartDrafts();
        	PartDraft partDraft = new PartDraft();
        	partDraft.setEditedOn(new Date());
        	partDraft.setLocale(part.getLocale());
        	partDraft.setEditedBy(part.getReporter().getUser().findFirstLastName());
        	partDraft.setOriginalText(part.getProceedingContent());
        	partDraft.setRevisedContent(part.getRevisedContent());
        	if(partDrafts != null){
        		partDrafts.add(partDraft);
        	}else{
        		partDrafts = new ArrayList<PartDraft>();
        		partDrafts.add(partDraft);
        	}
        	part.setPartDrafts(partDrafts);
        }*/
        part.merge();
        String status="failed";
               
        Reference reference=new Reference();
        if (status=="success")
        {
        reference.setNumber("1");
        reference.setState(status);
        }else
        {
        	reference.setState(status);
        	reference.setRemark("2");
        }
        
        return reference; 
    }
    
    @RequestMapping(value = "/getPart/{partId}/{locale}" ,method = RequestMethod.GET, 
    		produces = "text/plain; charset=utf-8")
    public @ResponseBody
   String getPart(@PathVariable final String locale,@PathVariable final Long partId,HttpServletResponse response) {
    	Part part=Part.findById(Part.class,partId );
		   
        String str="";
       if (part.getRevisedContent()!=null && !part.getRevisedContent().isEmpty())
        	str=part.getRevisedContent();
       else
    	   str=part.getProceedingContent();
       
        
        return str;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "/getRoster/{rosterId}/{locale}" ,method = RequestMethod.GET)
    public @ResponseBody
    List<ProceedingVO> getRoster(@PathVariable final String locale,@PathVariable final Long rosterId,HttpServletResponse response) {
    	
    	Roster roster = null;
    	roster=Roster.findById(Roster.class,rosterId );
    	//roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
    	
    	Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{locale.toString()});
		parametersMap.put("languageId",new String[]{"1"});
		parametersMap.put("rosterId", new String[]{roster.getId().toString()});
		List result=Query.findReport(ApplicationConstants.PROCEEDING_CONTENT_MERGE_REPORT4, parametersMap);
		 List<ProceedingVO> proceedingVOs=new ArrayList<ProceedingVO>();
		for(Object i:result){
			Object[] o=(Object[]) i;
			ProceedingVO proceedingVO=new ProceedingVO();
			proceedingVO.setSlotName(o[0].toString());
			proceedingVO.setCurrentSlotStartDate(o[1].toString());
			proceedingVO.setCurrentSlotStartTime(o[2].toString());
			proceedingVO.setLanguageReporter(o[3].toString());
			proceedingVO.setGeneralNotice(o[4].toString());
			proceedingVOs.add(proceedingVO);
		}
		
        
        return proceedingVOs;
    }
    
    @RequestMapping(value="/rosterdays",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getRosterDaysFromSession(final HttpServletRequest request, final Locale locale){
		String strhouseType=request.getParameter("houseType");
		String stryear=request.getParameter("sessionYear");
		String strsessionType=request.getParameter("sessionType");
		String strlanguage=request.getParameter("language");
		
		//List<Integer> rosterDays=new ArrayList<Integer>();
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		if(strhouseType!=null&&stryear!=null&&strsessionType!=null){
			HouseType selectedHouseType=HouseType.findByFieldName(HouseType.class,"type",strhouseType,locale.toString());
			SessionType selectedSessionType=SessionType.findById(SessionType.class, Long.parseLong(strsessionType));
			Integer year=Integer.parseInt(stryear);
			Language language=Language.findById(Language.class, Long.parseLong(strlanguage));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(selectedHouseType, selectedSessionType, year);
			} catch (ELSException e) {
				e.printStackTrace();
			}
			List<Roster> rosters=Roster.findAllRosterBySessionAndLanguage(session,language, locale.toString());
			for(Roster r:rosters){
				MasterVO masterVO = new MasterVO();
				masterVO.setNumber(r.getDay());
				masterVO.setValue(r.getId().toString());
				masterVO.setName(FormaterUtil.formatDateToString(r.getStartTime(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
				masterVOs.add(masterVO);
				//rosterDays.add(r.getDay());
			}
		}
		return masterVOs;
	}
    
   

  
    
}
