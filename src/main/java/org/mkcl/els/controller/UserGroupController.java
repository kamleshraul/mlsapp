package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.UserGroup;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/usergroup")
public class UserGroupController extends GenericController<UserGroup>{

@Override
protected void populateNew(final ModelMap model, final UserGroup domain, final String locale,
        final HttpServletRequest request) {
/*
 * In case of starred question we need following parameters:HOUSETYPE,
 * DEVICETYPE,YEAR,SESSIONTYPE,GROUP,DEPARTMENT,SUBDEPARTMENT.
 */
    List<HouseType> houseTypes=HouseType.findAll(HouseType.class,"type",ApplicationConstants.ASC, locale);
    model.addAttribute("houseTypes",houseTypes);
    List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "type",ApplicationConstants.ASC, locale);
    model.addAttribute("deviceTypes", deviceTypes);
    Integer year=0;
    year=new GregorianCalendar().get(Calendar.YEAR);
    CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
    List<Integer> years=new ArrayList<Integer>();
    if(houseFormationYear!=null){
        Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
        for(int i=year;i>=formationYear;i--){
            years.add(i);
        }
    }
    model.addAttribute("years",years);
    List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
    model.addAttribute("sessionTypes",sessionTypes);
    List<Group> groups=Group.findByHouseTypeSessionTypeYear(houseTypes.get(0), sessionTypes.get(0),years.get(0));
    model.addAttribute("groups",groups);
    List<Ministry> ministries=groups.get(0).getMinistries();
    List<Department> departments=new ArrayList<Department>();
    List<SubDepartment> subDepartments=new ArrayList<SubDepartment>();
}
}
