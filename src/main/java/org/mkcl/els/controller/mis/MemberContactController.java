package org.mkcl.els.controller.mis;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Address;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Contact;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Tehsil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("member/contact")
public class MemberContactController extends GenericController<Member> {

    @Override
    protected void populateEdit(final ModelMap model, final Member domain,
            final HttpServletRequest request) {
    	model.addAttribute("states", State.findAll(State.class, "name",
                ApplicationConstants.ASC, domain.getLocale()));
        String strDefaultState = ((CustomParameter) CustomParameter.findByName(
                CustomParameter.class, "DEFAULT_STATE", domain.getLocale()))
                .getValue();
        State defaultState = State.findByFieldName(State.class, "name",
                strDefaultState, domain.getLocale());

        List<District> districts = District.findDistrictsByStateId(
                defaultState.getId(), "name", ApplicationConstants.ASC,
                domain.getLocale());
        model.addAttribute("districts", districts);
        //here we will populate tehsils depending on if the district has been set.If it is not then
        //we will populate the tehsils under the district 0.Else we will populate tehsils under the districts
        //in the domain

        District defaultDistrict1 = districts.get(0);
        District defaultDistrict2 = districts.get(0);
        District defaultDistrict3 = districts.get(0);


    	//when bindinG form controls to fields of nested objects it is necessary to instantiate
    	//the nested objects as compiler has no way to initialize a custom object and hence set it to null.
    	//This is necessary only when we are first creating these objects.
        if (domain.getContact() == null) {
            domain.setContact(new Contact());
        }
        if (domain.getPermanentAddress() == null) {
            domain.setPermanentAddress(new Address());
            domain.getPermanentAddress().setState(defaultState);
        }else{
            if(domain.getPermanentAddress().getDistrict()!=null){
                defaultDistrict1=domain.getPermanentAddress().getDistrict();
            }
        }
        if (domain.getPresentAddress() == null) {
            domain.setPresentAddress(new Address());
            domain.getPresentAddress().setState(defaultState);
        }else{
            if(domain.getPresentAddress().getDistrict()!=null){
                defaultDistrict2=domain.getPresentAddress().getDistrict();
            }
        }
        if (domain.getOfficeAddress() == null) {
            domain.setOfficeAddress(new Address());
            domain.getOfficeAddress().setState(defaultState);
        }else{
            if(domain.getOfficeAddress().getDistrict()!=null){
                defaultDistrict3=domain.getOfficeAddress().getDistrict();
            }
        }
        model.addAttribute("tehsils1", Tehsil.findAllByFieldName(Tehsil.class,
                "district", defaultDistrict1, "name", ApplicationConstants.ASC,
                domain.getLocale()));
        model.addAttribute("tehsils2", Tehsil.findAllByFieldName(Tehsil.class,
                "district", defaultDistrict2, "name", ApplicationConstants.ASC,
                domain.getLocale()));
        model.addAttribute("tehsils3", Tehsil.findAllByFieldName(Tehsil.class,
                        "district", defaultDistrict3, "name", ApplicationConstants.ASC,
                        domain.getLocale()));
        domain.getContact().setLocale(domain.getLocale());
        domain.getPermanentAddress().setLocale(domain.getLocale());
        domain.getPresentAddress().setLocale(domain.getLocale());
        domain.getOfficeAddress().setLocale(domain.getLocale());
    }

    @Override
    protected void customValidateUpdate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
    	if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }

    @Override
    protected <E extends BaseDomain> void customInitBinderSuperClass(
            final Class clazz, final WebDataBinder binder) {
        binder.registerCustomEditor(Tehsil.class, new BaseEditor(new Tehsil()));
        binder.registerCustomEditor(District.class, new BaseEditor(
                new District()));
        binder.registerCustomEditor(State.class, new BaseEditor(new State()));
    }

}