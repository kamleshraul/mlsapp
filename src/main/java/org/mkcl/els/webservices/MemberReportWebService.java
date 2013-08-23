/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.webservices.MemberReportWebService.java
 * Created On: Apr 21, 2012
 */
package org.mkcl.els.webservices;

import java.util.List;

import org.mkcl.els.common.vo.MemberAgeWiseReportVO;
import org.mkcl.els.common.vo.MemberChildrenWiseReportVO;
import org.mkcl.els.common.vo.MemberGeneralVO;
import org.mkcl.els.common.vo.MemberPartyDistrictWiseVO;
import org.mkcl.els.common.vo.MemberPartyWiseReportVO;
import org.mkcl.els.common.vo.MemberProfessionWiseReportVO;
import org.mkcl.els.common.vo.MemberQualificationWiseReportVO;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class MemberReportWebService.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("ws/memberreport")
public class MemberReportWebService {

    /**
     * Find members by age.
     *
     * @param locale the locale
     * @return the member age wise report vo
     */
    @RequestMapping(value="/age/{locale}")
    public @ResponseBody MemberAgeWiseReportVO findMembersByAge(@PathVariable("locale") final String locale){
        return Member.findMembersByAge(locale);
    }

    /**
     * Find members by qualification.
     *
     * @param locale the locale
     * @return the member qualification wise report vo
     */
    @RequestMapping(value="/qualification/{locale}")
    public @ResponseBody MemberQualificationWiseReportVO findMembersByQualification(@PathVariable("locale") final String locale){
        return Member.findMembersByQualification(locale);
    }

    /**
     * Find members by profession.
     *
     * @param locale the locale
     * @return the member profession wise report vo
     */
    @RequestMapping(value="/profession/{locale}")
    public @ResponseBody MemberProfessionWiseReportVO findMembersByProfession(@PathVariable("locale") final String locale){
        return Member.findMembersByProfession(locale);
    }

    /**
     * Find members by children.
     *
     * @param locale the locale
     * @return the member children wise report vo
     */
    @RequestMapping(value="/children/{locale}")
    public @ResponseBody MemberChildrenWiseReportVO findMembersByChildren(@PathVariable("locale") final String locale){
        return Member.findMembersByChildren(locale);
    }

    /**
     * Find members by party.
     *
     * @param locale the locale
     * @return the member party wise report vo
     */
    @RequestMapping(value="/party/{locale}")
    public @ResponseBody MemberPartyWiseReportVO findMembersByParty(@PathVariable("locale") final String locale){
        return Member.findMembersByParty(locale);
    }

    /**
     * Find members by party district.
     *
     * @param locale the locale
     * @return the list
     */
    @RequestMapping(value="/partydistrict/{locale}")
    public @ResponseBody List<MemberPartyDistrictWiseVO> findMembersByPartyDistrict(@PathVariable("locale") final String locale){
        return Member.findMembersByPartyDistrict(locale);
    }

    /**
     * Findfemale members.
     *
     * @param locale the locale
     * @return the list
     */
    @RequestMapping(value="/female/{locale}")
    public @ResponseBody List<MemberGeneralVO> findfemaleMembers(@PathVariable("locale") final String locale){
        return Member.findfemaleMembers(locale);
    }

    /**
     * Find members by last name.
     *
     * @param locale the locale
     * @return the list
     */
    @RequestMapping(value="/lastname/{locale}")
    public @ResponseBody List<MemberGeneralVO> findMembersByLastName(@PathVariable("locale") final String locale){
        return Member.findMembersByLastName(locale);
    }

    /**
     * Find members by district.
     *
     * @param locale the locale
     * @return the list
     */
    @RequestMapping(value="/district/{locale}")
    public @ResponseBody List<MemberGeneralVO> findMembersByDistrict(@PathVariable("locale") final String locale){
        return Member.findMembersByDistrict(locale);
    }
}
