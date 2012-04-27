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

import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Gender;
import org.mkcl.els.domain.MaritalStatus;
import org.mkcl.els.domain.Party;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
        String defaultstate=((CustomParameter)CustomParameter.findByName(CustomParameter.class, "DEFAULT_STATE", locale)).getValue();
    	return Constituency.findVOByDefaultStateAndHouseType(defaultstate, housetype, locale,"name",ApplicationConstants.ASC);
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
        	MasterVO masterVO=new MasterVO(i.getId(),i.getName());
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
    	List<Gender> genders=Gender.findAll(Gender.class,"name",ApplicationConstants.ALL_LOCALE, locale);
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

//    @RequestMapping(value = "/terms/{housetype}/{locale}")
//    public @ResponseBody
//    Integer getNoOfTerms(@PathVariable final String housetype,@PathVariable final String locale) {
//        return Member.maxNoOfTerms(housetype,locale);
//    }
}
