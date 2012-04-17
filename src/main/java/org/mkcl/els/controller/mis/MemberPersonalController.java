package org.mkcl.els.controller.mis;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Address;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Contact;
import org.mkcl.els.domain.Degree;
import org.mkcl.els.domain.FamilyMember;
import org.mkcl.els.domain.Gender;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.MaritalStatus;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Nationality;
import org.mkcl.els.domain.Profession;
import org.mkcl.els.domain.Qualification;
import org.mkcl.els.domain.Relation;
import org.mkcl.els.domain.Religion;
import org.mkcl.els.domain.Reservation;
import org.mkcl.els.domain.Title;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/member/personal")
public class MemberPersonalController extends GenericController<Member> {

    //adding housetype
    @Override
    protected void populateModule(final ModelMap model,
            final HttpServletRequest request, final String locale,
            final AuthUser currentUser) {
        model.addAttribute("housetype", currentUser.getHouseType());
    }

    //init binders
    @SuppressWarnings("rawtypes")
	@Override
    protected <E extends BaseDomain> void customInitBinderSuperClass(
            final Class clazz, final WebDataBinder binder) {
        binder.registerCustomEditor(Gender.class, new BaseEditor(new Gender()));
        binder.registerCustomEditor(MaritalStatus.class, new BaseEditor(
                new MaritalStatus()));
        binder.registerCustomEditor(List.class, "professions",
                new CustomCollectionEditor(List.class) {

                    @Override
                    protected Object convertElement(final Object element) {
                        String id = null;

                        if (element instanceof String) {
                            id = (String) element;
                        }

                        return id != null ? BaseDomain.findById(
                                Profession.class, Long.valueOf(id)) : null;
                    }
                });
        binder.registerCustomEditor(Nationality.class, new BaseEditor(
                new Nationality()));
        binder.registerCustomEditor(Address.class,
                new BaseEditor(new Address()));
        binder.registerCustomEditor(Contact.class,
                new BaseEditor(new Contact()));
        binder.registerCustomEditor(Title.class, new BaseEditor(new Title()));
    }
    //logic for populating domain and model during new and edit request
    private void populate(final ModelMap model, final Member domain,
             final HttpServletRequest request){
        String locale=domain.getLocale();
        model.addAttribute("titles", Title.findAll(Title.class, "name",
                ApplicationConstants.ASC, locale));
        model.addAttribute("nationalities", Nationality.findAll(
                Nationality.class, "name", ApplicationConstants.ASC,
                locale));
        model.addAttribute("genders", Gender.findAll(Gender.class, "name",
                ApplicationConstants.ASC, locale));
        model.addAttribute("religions", Religion.findAll(Religion.class,
                "name", ApplicationConstants.ASC, locale));
        model.addAttribute("reservations", Reservation.findAll(
                Reservation.class, "name", ApplicationConstants.ASC,
                locale));
        model.addAttribute("relations", Relation.findAll(Relation.class,
                "name", ApplicationConstants.ASC, locale));
        model.addAttribute("degrees", Degree.findAll(Degree.class, "name",
                ApplicationConstants.ASC, locale));
        model.addAttribute("languages", Language.findAll(Language.class,
                "name", ApplicationConstants.ASC, locale));
        model.addAttribute("professions", Profession.findAll(Profession.class,
                "name", ApplicationConstants.ASC, locale));
        model.addAttribute("maritalStatuses", MaritalStatus.findAll(
                MaritalStatus.class, "name", ApplicationConstants.ASC,
                locale));
    }

    @Override
    protected void populateNew(final ModelMap model, final Member domain,
            final String locale, final HttpServletRequest request) {
        domain.setLocale(locale.toString());
        populate(model, domain,request);
        model.addAttribute("familyCount",0);
        model.addAttribute("qualificationCount",0);
    }

    @Override
    protected void populateEdit(final ModelMap model, final Member domain,
            final HttpServletRequest request) {
        populate(model, domain,request);
        model.addAttribute("familyMembers", domain.getFamilyMembers());
        model.addAttribute("familyCount", domain.getFamilyMembers().size());
        model.addAttribute("qualifications", domain.getQualifications());
        model.addAttribute("qualificationCount", domain.getQualifications()
                .size());
    }
    //private utility method for populating domain with family and qualifications
    private void populateFamilyQualification(final Member domain, final HttpServletRequest request,final BindingResult result){
        List<FamilyMember> familyMembers = new ArrayList<FamilyMember>();
        Integer familyCount = Integer.parseInt(request
                .getParameter("familyCount"));
        for (int i = 1; i <= familyCount; i++) {
        	String relation=request.getParameter("familyMemberRelation"+ i);
        	if(relation!=null){
        	FamilyMember familyMember=new FamilyMember();

        	String name=request.getParameter("familyMemberName"+i);
        	if(name!=null){
        		if(!name.isEmpty()){
                	familyMember.setName(request.getParameter("familyMemberName"+ i));
        		}//else{
        			//result.rejectValue("familyMembers","NotEmpty");
        		//}
        	}

        	if(!relation.isEmpty()){
        		familyMember.setRelation((Relation) Relation.findById(Relation.class,Long.parseLong(relation)));
        	}

        	String id=request.getParameter("familyMemberId"+ i);
        	if(id!=null){
        		if(!id.isEmpty()){
        		familyMember.setId(Long.parseLong(id));
        		}
        	}

        	String version=request.getParameter("familyMemberVersion"+ i);
        	if(version!=null){
        		if(!version.isEmpty()){
        		familyMember.setVersion(Long.parseLong(version));
        		}
        	}

        	String locale=request.getParameter("familyMemberLocale"+ i);
 	        if(locale!=null){
 	        	if(!locale.isEmpty()){
 	        	familyMember.setLocale(locale);
 	        }
 	        }
 	        familyMembers.add(familyMember);
        }
        }
        domain.setFamilyMembers(familyMembers);

        List<Qualification> qualifications = new ArrayList<Qualification>();
        Integer qualificationCount = Integer.parseInt(request
                .getParameter("qualificationCount"));
        for (int i = 1; i <= qualificationCount; i++) {
            String degree=request.getParameter("qualificationDegree"+ i);
        	if(degree!=null){
            Qualification qualification = new Qualification();

            String detail=request.getParameter("qualificationDetail" + i);
            if(detail!=null){
            qualification.setDetails(detail);
            }

        	if(!degree.isEmpty()){
        		qualification.setDegree((Degree) Degree.findById(Degree.class,Long.parseLong(degree)));
        	}

            qualification.setLocale(domain.getLocale());

            String id=request.getParameter("qualificationId"+ i);
        	if(id!=null){
        		if(!id.isEmpty()){
        			qualification.setId(Long.parseLong(id));
        		}
        	}

        	String version=request.getParameter("qualificationVersion"+ i);
        	if(version!=null){
        		if(!version.isEmpty()){
        			qualification.setVersion(Long.parseLong(version));
        		}
        	}
        	String locale=request.getParameter("qualificationLocale"+ i);
 	        if(locale!=null){
 	        	if(!locale.isEmpty()){
 	        	qualification.setLocale(locale);
 	        }
 	        }
            qualifications.add(qualification);
         }
        }
        domain.setQualifications(qualifications);
    }
    //as we enter post and put we will populate domain with family members and qualifications
    @Override
    protected void preValidateCreate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
        populateFamilyQualification(domain,request,result);
    }
    @Override
    protected void preValidateUpdate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
        populateFamilyQualification(domain,request,result);
    }
    //in case of errors we need to re populate domain with populate edit logic
    @Override
    protected void poulateCreateIfErrors(final ModelMap model,
            final Member domain,
            final HttpServletRequest request) {
        populateEdit(model, domain, request);
        model.addAttribute("type", "error");
        model.addAttribute("msg", "create_failed");
    }
    @Override
    protected void poulateUpdateIfErrors(final ModelMap model, final Member domain,
            final HttpServletRequest request) {
        populateEdit(model, domain, request);
        model.addAttribute("type", "error");
        model.addAttribute("msg", "update_failed");
    }
    //here we are just checking for version mis match in validation.there is no check for duplicate entries
    @Override
    protected void customValidateCreate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }
    @Override
    protected void customValidateUpdate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }

    @RequestMapping(value = "/family/{id}/delete", method = RequestMethod.DELETE)
    public String deleteFamily(final @PathVariable("id") Long id,
            final ModelMap model, final HttpServletRequest request) {
        FamilyMember familyMember=FamilyMember.findById(FamilyMember.class, id);
        familyMember.remove();
        return "info";
    }

    @RequestMapping(value = "/qualification/{id}/delete", method = RequestMethod.DELETE)
    public String deleteQualification(final @PathVariable("id") Long id,
            final ModelMap model, final HttpServletRequest request) {
        Qualification qualification=Qualification.findById(Qualification.class, id);
        qualification.remove();
        return "info";
    }
}
