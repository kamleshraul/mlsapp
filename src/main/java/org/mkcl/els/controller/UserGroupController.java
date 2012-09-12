package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
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
        model.addAttribute("housetypes",houseTypes);
        if(!houseTypes.isEmpty()){
        model.addAttribute("selectedhousetype",houseTypes.get(0).getName());
        }

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
        model.addAttribute("selectedyear",year);

        List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
        model.addAttribute("sessionTypes",sessionTypes);
        Session currentSession=null;
        if(!houseTypes.isEmpty()){
        currentSession=Session.findLatestSession(houseTypes.get(0),year);
        if(currentSession!=null){
            model.addAttribute("selectedsessiontype",currentSession.getType().getSessionType());
        }
        }


        List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "type",ApplicationConstants.ASC, locale);
        model.addAttribute("deviceTypes", deviceTypes);


        List<Group> groups=Group.findByHouseTypeSessionTypeYear(houseTypes.get(0), sessionTypes.get(0),years.get(0));
        model.addAttribute("groups",groups);
        List<MasterVO> departments=new ArrayList<MasterVO>();
        List<MasterVO> subdepartments=new ArrayList<MasterVO>();
        if(!groups.isEmpty()){
            departments=MemberMinister.findAssignedDepartmentsVO(groups.get(0), locale);
            subdepartments=MemberMinister.findAssignedSubDepartmentsVO(groups.get(0), locale);

        }
        model.addAttribute("groups",groups);
        model.addAttribute("selectedgroup",groups.get(0).getNumber());
        model.addAttribute("departments",departments);
        model.addAttribute("subdepartments", subdepartments);

        model.addAttribute("locale",locale);

        String currentDate=FormaterUtil.getDateFormatter(locale).format(new Date());
        model.addAttribute("currentdate", currentDate);
        domain.setLocale(locale);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void populateCreateIfNoErrors(final ModelMap model, final UserGroup domain,
            final HttpServletRequest request) {
        /*
         * Here we are collecting all the request parameters that begins with 'param_'
         * and storing them as key/value pair.key is obtained by splitting request
         * parameters that begins with param_ and storing index[1]
         */
        Map<String,String[]> params=request.getParameterMap();
        Map<String,String> deviceTypeParams=new HashMap<String, String>();
        for(Entry<String,String[]> i:params.entrySet()){
            String key=i.getKey();
            if(key.startsWith("param_")){
                String[] values=params.get(key);
                if(values.length==1){
                deviceTypeParams.put(key.split("param_")[1],values[0]);
                }else{
                    StringBuffer buffer=new StringBuffer();
                    for(String j:values){
                        buffer.append(j+"##");
                    }
                    deviceTypeParams.put(key.split("param_")[1],buffer.toString());
                }
            }
        }
        domain.setParameters(deviceTypeParams);
    }

    @Override
    protected void populateUpdateIfNoErrors(final ModelMap model, final UserGroup domain,
            final HttpServletRequest request) {
        /*
         * Here we are collecting all the request parameters that begins with 'param_'
         * and storing them as key/value pair.key is obtained by splitting request
         * parameters that begins with param_ and storing index[1]
         */
        Map<String,String[]> params=request.getParameterMap();
        Map<String,String> deviceTypeParams=new HashMap<String, String>();
        for(Entry<String,String[]> i:params.entrySet()){
            String key=i.getKey();
            if(key.startsWith("param_")){
                String[] values=params.get(key);
                if(values.length==1){
                deviceTypeParams.put(key.split("param_")[1],values[0]);
                }else{
                    StringBuffer buffer=new StringBuffer();
                    for(String j:values){
                        buffer.append(j+"##");
                    }
                    deviceTypeParams.put(key.split("param_")[1],buffer.toString());
                }
            }
        }
        domain.setParameters(deviceTypeParams);
    }

}
