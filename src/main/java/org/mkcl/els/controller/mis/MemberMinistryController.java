package org.mkcl.els.controller.mis;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.associations.MemberMinistryAssociation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("member/ministry")
public class MemberMinistryController extends BaseController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(@RequestParam(required = false) final String formtype,
            final ModelMap model, final Locale locale,
            final HttpServletRequest request) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        Grid grid = Grid.findByDetailView(urlPattern, locale.toString());
        model.addAttribute("gridId", grid.getId());
        model.addAttribute("urlPattern", urlPattern);
        return "mis/ministry/list";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newForm(final ModelMap model, final Locale locale,
            final HttpServletRequest request) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        MemberMinistryAssociation domain = new MemberMinistryAssociation();
        populateNew(model, domain, locale, request);
        model.addAttribute("domain", domain);
        model.addAttribute("urlPattern", urlPattern);
        return "mis/ministry/new";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{recordIndex}/edit", method = RequestMethod.GET)
    public String edit(final @PathVariable("recordIndex") int recordIndex,
            final ModelMap model, final HttpServletRequest request,
            final @RequestParam("member") Long member, Locale locale) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        MemberMinistryAssociation domain = MemberMinistryAssociation
                .findByMemberIdAndId(member, recordIndex);
        populateEdit(model, domain, request, locale.toString());
        model.addAttribute("domain", domain);
        model.addAttribute("urlPattern", urlPattern);
        return "mis/ministry/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(
            final ModelMap model,
            final HttpServletRequest request,
            final RedirectAttributes redirectAttributes,
            final Locale locale,
            @Valid @ModelAttribute("domain") final MemberMinistryAssociation domain,
            final BindingResult result) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        validateCreate(domain, result, request);
        model.addAttribute("domain", domain);
        if (result.hasErrors()) {
            poulateCreateIfErrors(model, domain, request, locale.toString());
            return "mis/ministry/new";
        }
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
            final @Valid @ModelAttribute("domain") MemberMinistryAssociation domain,
            final BindingResult result, final ModelMap model,
            final RedirectAttributes redirectAttributes,
            final HttpServletRequest request, final Locale locale) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        validateUpdate(domain, result, request);
        model.addAttribute("domain", domain);
        if (result.hasErrors()) {
            poulateUpdateIfErrors(model, domain, request, locale.toString());
            return "mis/ministry/edit";
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
        MemberMinistryAssociation association = MemberMinistryAssociation
                .findByMemberIdAndId(member, recordIndex);
        if (association.getMember() != null) {
            association.remove();
        }
        return "info";
    }

    private void populateNew(final ModelMap model,
            final MemberMinistryAssociation domain, final Locale locale,
            final HttpServletRequest request) {
    	String houseType=this.getCurrentUser().getHouseType();
    	HouseType authHouseType=HouseType.findByFieldName(HouseType.class,"type",houseType, locale.toString());
        model.addAttribute("ministries", Ministry.findAll(Ministry.class,
                "department", ApplicationConstants.ASC, locale.toString()));
        model.addAttribute("roles", MemberRole.findAllByFieldName(MemberRole.class,"houseType", authHouseType, "name",ApplicationConstants.ASC, locale.toString()));
        Long member = Long.parseLong(request.getParameter("member"));
        model.addAttribute("member", member);
        int index = MemberMinistryAssociation.findHighestRecordIndex(member);
        domain.setRecordIndex(index + 1);
    }

    private void populateEdit(final ModelMap model,
            final MemberMinistryAssociation domain,
            final HttpServletRequest request, final String locale) {
    	String houseType=this.getCurrentUser().getHouseType();
    	HouseType authHouseType=HouseType.findByFieldName(HouseType.class,"type",houseType, locale.toString());
        model.addAttribute("ministries", Ministry.findAll(Ministry.class,
                "department", ApplicationConstants.ASC, locale.toString()));
        model.addAttribute("roles", MemberRole.findAllByFieldName(MemberRole.class,"houseType", authHouseType, "name",ApplicationConstants.ASC, locale.toString()));
        Long member = Long.parseLong(request.getParameter("member"));
        model.addAttribute("member", member);
    }

    private void validateCreate(MemberMinistryAssociation domain,
            Errors errors, HttpServletRequest request) {
        if (domain.isDuplicate()) {
            Object[] params = new Object[3];
            params[0] = domain.getMinistry().getDepartment();
            params[2] = domain.getFromDate();
            params[3] = domain.getToDate();
            errors.rejectValue("recordIndex", "Duplicate", params,
                    "Entry with Department:" + params[0] + "From Date:"
                            + params[1] + ",To Date:" + params[2]
                            + " already exists");
        }        
    }

    @SuppressWarnings("unused")
    private void validateUpdate(MemberMinistryAssociation domain,
            Errors errors, HttpServletRequest request) {
    	if (domain.isVersionMismatch()) {
            errors.rejectValue("VersionMismatch", "version");
        }
    }

    private void poulateCreateIfErrors(ModelMap model,
            MemberMinistryAssociation domain, HttpServletRequest request,
            String locale) {
        model.addAttribute("ministries", Ministry.findAll(Ministry.class,
                "department", ApplicationConstants.ASC, locale.toString()));
        model.addAttribute("roles", MemberRole.findAll(MemberRole.class,
                "name", ApplicationConstants.ASC, locale.toString()));
        Long member = Long.parseLong(request.getParameter("member"));
        model.addAttribute("member", member);

    }

    private void poulateUpdateIfErrors(ModelMap model,
            MemberMinistryAssociation domain, HttpServletRequest request,
            String locale) {
        model.addAttribute("ministries", Ministry.findAll(Ministry.class,
                "department", ApplicationConstants.ASC, locale));
        model.addAttribute("roles", MemberRole.findAll(MemberRole.class,
                "name", ApplicationConstants.ASC, locale));
        model.addAttribute("member", request.getSession()
                .getAttribute("member"));
    }

    private void populateUpdateIfNoErrors(ModelMap model,
            MemberMinistryAssociation domain, HttpServletRequest request) {

    }
}
