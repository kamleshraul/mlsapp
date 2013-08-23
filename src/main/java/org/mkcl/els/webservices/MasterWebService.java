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

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Gender;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MaritalStatus;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.State;
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
        List<Reference> districts = new ArrayList<Reference>();
        try{
        	districts = District.findDistrictsRefByStateId(state.getId(), "name", ApplicationConstants.ASC, locale);
        }catch (ELSException e) {
			e.printStackTrace();
		}
        return districts;
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

    @RequestMapping(value = "/house/{houseType}/{locale}")
    public @ResponseBody
    List<Reference> getHouse(@PathVariable final String locale,@PathVariable final String houseType) {
        List<House> houses = null;
		try {
			houses = House.findByHouseType(houseType, locale);
		} catch (ELSException e) {
			e.printStackTrace();
		}
        List<Reference> houseVOs = new ArrayList<Reference>();
        for(House i:houses){
            Reference reference=new Reference(String.valueOf(i.getId()),i.getDisplayName());
            houseVOs.add(reference);
        }
        return houseVOs;
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
}
