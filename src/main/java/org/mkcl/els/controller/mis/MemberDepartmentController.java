package org.mkcl.els.controller.mis;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.associations.MemberDepartmentAssociation;
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
@RequestMapping("member/department")
public class MemberDepartmentController extends BaseController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(@RequestParam(required = false) final String formtype,
            final ModelMap model, final Locale locale,
            final HttpServletRequest request) {
    	final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String urlPattern=servletPath.split("\\/list")[0];
        Grid grid = Grid.findByDetailView(urlPattern, locale.toString());
        model.addAttribute("gridId", grid.getId());
        model.addAttribute("urlPattern", urlPattern);
        return "member/department/list";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newForm(final ModelMap model, final Locale locale,
            final HttpServletRequest request) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        MemberDepartmentAssociation domain = new MemberDepartmentAssociation();
        populateNew(model, domain, locale, request);
        model.addAttribute("domain", domain);
        model.addAttribute("urlPattern", urlPattern);
        return "member/department/new";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{recordIndex}/edit", method = RequestMethod.GET)
    public String edit(final @PathVariable("recordIndex") int recordIndex,
            final ModelMap model, final HttpServletRequest request,
            final @RequestParam("member") Long member, final Locale locale) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        MemberDepartmentAssociation domain = MemberDepartmentAssociation
                .findByMemberIdAndId(member, recordIndex);
        populateEdit(model, domain, request, locale.toString());
        model.addAttribute("domain", domain);
        model.addAttribute("urlPattern", urlPattern);
        return "member/department/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(
            final ModelMap model,
            final HttpServletRequest request,
            final RedirectAttributes redirectAttributes,
            final Locale locale,
            @Valid @ModelAttribute("domain") final MemberDepartmentAssociation domain,
            final BindingResult result) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        validateCreate(domain, result, request);
        model.addAttribute("domain", domain);
        if (result.hasErrors()) {
            poulateCreateIfErrors(model, domain, request, locale.toString());
            return "member/department/new";
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
            final @Valid @ModelAttribute("domain") MemberDepartmentAssociation domain,
            final BindingResult result, final ModelMap model,
            final RedirectAttributes redirectAttributes,
            final HttpServletRequest request, final Locale locale) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        validateUpdate(domain, result, request);
        model.addAttribute("domain", domain);
        if (result.hasErrors()) {
            poulateUpdateIfErrors(model, domain, request, locale.toString());
            return "member/department/edit";
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
        MemberDepartmentAssociation association = MemberDepartmentAssociation
                .findByMemberIdAndId(member, recordIndex);
        if (association.getMember() != null) {
            association.remove();
        }
        return "info";
    }

    private void populateNew(final ModelMap model,
            final MemberDepartmentAssociation domain, final Locale locale,
            final HttpServletRequest request) {
    	String houseType=this.getCurrentUser().getHouseType();
    	HouseType authHouseType=HouseType.findByFieldName(HouseType.class,"type",houseType, locale.toString());
        model.addAttribute("departments", Department.findAll(Department.class,
                "name", ApplicationConstants.ASC, locale.toString()));
        model.addAttribute("roles", MemberRole.findAllByFieldName(MemberRole.class,"houseType", authHouseType, "name",ApplicationConstants.ASC, locale.toString()));
        Long member = Long.parseLong(request.getParameter("member"));
        model.addAttribute("member", member);
        int index = MemberDepartmentAssociation.findHighestRecordIndex(member);
        domain.setRecordIndex(index + 1);
    }

    private void populateEdit(final ModelMap model,
            final MemberDepartmentAssociation domain,
            final HttpServletRequest request, final String locale) {
    	String houseType=this.getCurrentUser().getHouseType();
    	HouseType authHouseType=HouseType.findByFieldName(HouseType.class,"type",houseType, locale.toString());
        model.addAttribute("departments", Department.findAll(Department.class,
                "name", ApplicationConstants.ASC, locale.toString()));
        model.addAttribute("roles", MemberRole.findAllByFieldName(MemberRole.class,"houseType", authHouseType, "name",ApplicationConstants.ASC, locale.toString()));
        Long member = Long.parseLong(request.getParameter("member"));
        model.addAttribute("member", member);
    }

    private void validateCreate(final MemberDepartmentAssociation domain,
            final Errors errors, final HttpServletRequest request) {
        if (domain.isDuplicate()) {
            Object[] params = new Object[3];
            params[0] = domain.getDepartment().getName();
            params[2] = domain.getFromDate();
            params[3] = domain.getToDate();
            errors.rejectValue("recordIndex", "Duplicate", params,
                    "Entry with Department:" + params[0] + "From Date:"
                            + params[1] + ",To Date:" + params[2]
                            + " already exists");
        }
    }

    @SuppressWarnings("unused")
    private void validateUpdate(final MemberDepartmentAssociation domain,
            final Errors errors, final HttpServletRequest request) {
    	if (domain.isVersionMismatch()) {
            errors.rejectValue("VersionMismatch", "version");
        }
    }

    private void poulateCreateIfErrors(final ModelMap model,
            final MemberDepartmentAssociation domain, final HttpServletRequest request,
            final String locale) {
        model.addAttribute("departments", Department.findAll(Department.class,
                "name", ApplicationConstants.ASC, locale.toString()));
        model.addAttribute("roles", MemberRole.findAll(MemberRole.class,
                "name", ApplicationConstants.ASC, locale.toString()));
        Long member = Long.parseLong(request.getParameter("member"));
        model.addAttribute("member", member);

    }

    private void poulateUpdateIfErrors(final ModelMap model,
            final MemberDepartmentAssociation domain, final HttpServletRequest request,
            final String locale) {
        model.addAttribute("departments", Department.findAll(Department.class,
                "name", ApplicationConstants.ASC, locale.toString()));
        model.addAttribute("roles", MemberRole.findAll(MemberRole.class,
                "name", ApplicationConstants.ASC, locale));
        model.addAttribute("member", request.getSession()
                .getAttribute("member"));
    }

    private void populateUpdateIfNoErrors(final ModelMap model,
            final MemberDepartmentAssociation domain, final HttpServletRequest request) {

    }
}
