package org.mkcl.els.controller.mis;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("member/house")
public class MemberHouseRoleController extends BaseController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(@RequestParam(required = false) final String formtype,
            final ModelMap model, final Locale locale,
            final HttpServletRequest request) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        Grid grid = Grid.findByDetailView(urlPattern, locale.toString());
        model.addAttribute("gridId", grid.getId());
        model.addAttribute("urlPattern", urlPattern);
        return "mis/house/list";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newForm(final ModelMap model, final Locale locale,
            final HttpServletRequest request) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        HouseMemberRoleAssociation domain = new HouseMemberRoleAssociation();
        populateNew(model, domain, locale, request);
        model.addAttribute("domain", domain);
        model.addAttribute("urlPattern", urlPattern);
        return "mis/house/new";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{recordIndex}/edit", method = RequestMethod.GET)
    public String edit(final @PathVariable("recordIndex") int recordIndex,
            final ModelMap model, final HttpServletRequest request,
            final @RequestParam("member") Long member, Locale locale) {
        final String urlPattern = request.getServletPath().split("\\/")[1];

        HouseMemberRoleAssociation domain = HouseMemberRoleAssociation
                .findByMemberIdAndId(member, recordIndex);
        populateEdit(model, domain, request, locale.toString());
        model.addAttribute("domain", domain);
        model.addAttribute("urlPattern", urlPattern);
        return "mis/house/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(
            final ModelMap model,
            final HttpServletRequest request,
            final RedirectAttributes redirectAttributes,
            final Locale locale,
            @Valid @ModelAttribute("domain") final HouseMemberRoleAssociation domain,
            final BindingResult result) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        validateCreate(domain, result, request);
        model.addAttribute("domain", domain);
        if (result.hasErrors()) {
            poulateCreateIfErrors(model, domain, request, locale.toString());
            return "mis/house/new";
        }
        populateCreateIfNoErrors(model, domain, request);
        domain.persist();
        request.getSession().setAttribute("refresh", "");
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("msg", "create_success");
        String returnUrl = "redirect:/" + urlPattern + "/"
                + domain.getRecordIndex() + "/edit?member="
                + request.getParameter("member");
        return returnUrl;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(
            final @Valid @ModelAttribute("domain") HouseMemberRoleAssociation domain,
            final BindingResult result, final ModelMap model,
            final RedirectAttributes redirectAttributes,
            final HttpServletRequest request, final Locale locale) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        validateUpdate(domain, result, request);
        model.addAttribute("domain", domain);
        if (result.hasErrors()) {
            poulateUpdateIfErrors(model, domain, request, locale.toString());
            return "mis/house/edit";
        }
        populateUpdateIfNoErrors(model, domain, request);
        domain.merge();
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("msg", "update_success");
        String returnUrl = "redirect:/" + urlPattern + "/"
                + domain.getRecordIndex() + "/edit?member="
                + request.getParameter("member");
        return returnUrl;
    }

    @RequestMapping(value = "/{recordIndex}/delete",
            method = RequestMethod.DELETE)
    public String delete(final @PathVariable("recordIndex") int recordIndex,
            final @RequestParam("member") Long member, final ModelMap model,
            final HttpServletRequest request) {
        HouseMemberRoleAssociation association = HouseMemberRoleAssociation
                .findByMemberIdAndId(member, recordIndex);
        if (association.getMember() != null) {
            association.remove();
        }
        return "info";
    }

    protected void populateNew(final ModelMap model,
            final HouseMemberRoleAssociation domain, final Locale locale,
            final HttpServletRequest request) {
        domain.setIsSitting(true);
        String houseType = this.getCurrentUser().getHouseType();
        HouseType authUserHouseType = HouseType.findByFieldName(
                HouseType.class, "type", houseType, locale.toString());
        model.addAttribute("houseType", authUserHouseType);
        model.addAttribute("houses", House.findAllByFieldName(House.class,
                "type", authUserHouseType, "formationDate",
                ApplicationConstants.DESC, locale.toString()));
        model.addAttribute("roles", MemberRole.findAllByFieldName(
                MemberRole.class, "houseType", authUserHouseType, "name",
                ApplicationConstants.ASC, locale.toString()));
        Long member = Long.parseLong(request.getParameter("member"));
        model.addAttribute("member", member);
        House currentHouse = House.findCurrentHouse(locale.toString());
        if (currentHouse != null && houseType.equals("lowerhouse")) {
            domain.setFromDate(currentHouse.getFirstDate());
            domain.setToDate(currentHouse.getLastDate());
        }
        //algorithm for getting constituencies
        //1.if deafult state is set then get by default state ,house type and locale
        //2.if no default state is set then get list of all constituencies by housetype and locale
        String defaultState=((CustomParameter)CustomParameter.findByName(CustomParameter.class,"DEFAULT_STATE",locale.toString())).getValue();
        model.addAttribute("constituencies",Constituency.findByDefaultStateAndHouseType(defaultState, houseType, locale.toString(), "name",ApplicationConstants.ASC));
        // setting the value of the recordIndex field
        int index = HouseMemberRoleAssociation.findHighestRecordIndex(member);
        domain.setRecordIndex(index + 1);
        domain.setLocale(locale.toString());
    }

    private void populateEdit(final ModelMap model,
            final HouseMemberRoleAssociation domain,
            final HttpServletRequest request, String locale) {
        String houseType = this.getCurrentUser().getHouseType();
        HouseType authUserHouseType = HouseType.findByFieldName(
                HouseType.class, "type", houseType, locale);
        model.addAttribute("houseType", authUserHouseType);
        model.addAttribute("houses", House.findAllByFieldName(House.class,
                "type", authUserHouseType, "formationDate",
                ApplicationConstants.DESC, locale));
        model.addAttribute("roles", MemberRole.findAllByFieldName(
                MemberRole.class, "houseType", authUserHouseType, "name",
                ApplicationConstants.ASC, locale));
        model.addAttribute("member", request.getSession()
                .getAttribute("member"));
        request.getSession().removeAttribute("member");
        String defaultState=((CustomParameter)CustomParameter.findByName(CustomParameter.class,"DEFAULT_STATE",locale.toString())).getValue();
        model.addAttribute("constituencies",Constituency.findByDefaultStateAndHouseType(defaultState, houseType, locale.toString(), "name",ApplicationConstants.ASC));
    }

    private void poulateCreateIfErrors(ModelMap model,
            HouseMemberRoleAssociation domain, HttpServletRequest request,
            String locale) {
        String houseType = this.getCurrentUser().getHouseType();
        HouseType authUserHouseType = HouseType.findByFieldName(
                HouseType.class, "type", houseType, locale.toString());
        model.addAttribute("houseType", authUserHouseType);
        model.addAttribute("houses", House.findAllByFieldName(House.class,
                "type", authUserHouseType, "formationDate",
                ApplicationConstants.DESC, locale.toString()));
        model.addAttribute("roles", MemberRole.findAllByFieldName(
                MemberRole.class, "houseType", authUserHouseType, "name",
                ApplicationConstants.ASC, locale.toString()));
        Long member = Long.parseLong(request.getParameter("member"));
        model.addAttribute("member", member);
        String defaultState=((CustomParameter)CustomParameter.findByName(CustomParameter.class,"DEFAULT_STATE",locale.toString())).getValue();
        model.addAttribute("constituencies",Constituency.findByDefaultStateAndHouseType(defaultState, houseType, locale.toString(), "name",ApplicationConstants.ASC));

    }

    private void poulateUpdateIfErrors(ModelMap model,
            HouseMemberRoleAssociation domain, HttpServletRequest request,
            String locale) {
        String houseType = this.getCurrentUser().getHouseType();
        HouseType authUserHouseType = HouseType.findByFieldName(
                HouseType.class, "type", houseType, locale);
        model.addAttribute("houseType", authUserHouseType);
        model.addAttribute("houses", House.findAllByFieldName(House.class,
                "type", authUserHouseType, "formationDate",
                ApplicationConstants.DESC, locale));
        model.addAttribute("roles", MemberRole.findAllByFieldName(
                MemberRole.class, "houseType", authUserHouseType, "name",
                ApplicationConstants.ASC, locale));
        Long member = Long.parseLong(request.getParameter("member"));
        model.addAttribute("member", member);
        String defaultState=((CustomParameter)CustomParameter.findByName(CustomParameter.class,"DEFAULT_STATE",locale.toString())).getValue();
        model.addAttribute("constituencies",Constituency.findByDefaultStateAndHouseType(defaultState, houseType, locale.toString(), "name",ApplicationConstants.ASC));
    }

    private void validateCreate(HouseMemberRoleAssociation domain,
            Errors errors, HttpServletRequest request) {
        if (domain.isDuplicate()) {
            Object[] params = new Object[4];
            params[0] = domain.getHouse().getName();
            params[1] = domain.getRole().getName();
            params[2] = domain.getFromDate();
            params[3] = domain.getToDate();
            errors.rejectValue("recordIndex", "Duplicate", params,
                    "Entry with House:" + params[0] + ",Role:" + params[1]
                            + "From Date:" + params[2] + ",To Date:"
                            + params[3] + " already exists");
        }        
    }

    private void validateUpdate(HouseMemberRoleAssociation domain,
            BindingResult result, HttpServletRequest request) {
    	if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }

    private void populateCreateIfNoErrors(final ModelMap model,
            final HouseMemberRoleAssociation domain,
            final HttpServletRequest request) {
        request.getSession().setAttribute("member",
                Long.parseLong(request.getParameter("member")));

    }

    private void populateUpdateIfNoErrors(ModelMap model,
            HouseMemberRoleAssociation domain, HttpServletRequest request) {
    	request.getSession().setAttribute("member",
                Long.parseLong(request.getParameter("member")));
    }

    @SuppressWarnings("unused")
    @InitBinder(value = "domain")
    private void initBinder(final WebDataBinder binder) {
        CustomParameter parameter = CustomParameter.findByName(
                CustomParameter.class, "SERVER_DATEFORMAT", "");
        SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue());
        dateFormat.setLenient(true);
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
                dateFormat, true));
        binder.registerCustomEditor(Constituency.class, new BaseEditor(
                new Constituency()));
        binder.registerCustomEditor(House.class, new BaseEditor(new House()));
        binder.registerCustomEditor(MemberRole.class, new BaseEditor(
                new MemberRole()));
        binder.registerCustomEditor(Member.class, new BaseEditor(new Member()));
    }
}
