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
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/usergroup")
public class UserGroupController extends GenericController<UserGroup>{

    @Override
    protected void populateList(final ModelMap model, final HttpServletRequest request,
            final String locale, final AuthUser currentUser) {
        /*
         * setting credential
         */
        String strUser=request.getParameter("user");
        model.addAttribute("user",strUser);
        Long userId=Long.parseLong(strUser);
        User user=User.findById(User.class, userId);
        Credential credential=user.getCredential();
        if(credential!=null){
            model.addAttribute("credential",credential.getId());
        }
    }

    @Override
    protected void populateNew(final ModelMap model, final UserGroup domain, final String locale,
            final HttpServletRequest request) {
        /*
         * setting credential
         */
        String strCredential=request.getParameter("credential");
        Credential credential=Credential.findById(Credential.class,Long.parseLong(strCredential));
        domain.setCredential(credential);
        /*
         * setting user group types
         */
        List<UserGroupType> userGroupTypes=UserGroupType.findAll(UserGroupType.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("userGroupTypes",userGroupTypes);
        /*
         * In case of starred question we need following parameters:HOUSETYPE,
         * DEVICETYPE,YEAR,SESSIONTYPE,GROUP,DEPARTMENT,SUBDEPARTMENT.
         */
        /*
         * setting house types and selected house type
         */
        List<HouseType> houseTypes=HouseType.findAllNoExclude("name",ApplicationConstants.ASC, locale);
        model.addAttribute("housetypes",houseTypes);
        if(!houseTypes.isEmpty()){
            model.addAttribute("selectedHouseType",houseTypes.get(0).getName());
        }
        /*
         * setting years and selected year
         */
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
        model.addAttribute("selectedYear",year);
        /*
         * setting session types amd selected session type
         */
        List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
        model.addAttribute("sessionTypes",sessionTypes);
        Session currentSession=null;
        if(!houseTypes.isEmpty()){
            currentSession=Session.findLatestSession(houseTypes.get(0),year);
            if(currentSession!=null){
                if(currentSession.getId()!=null){
                    model.addAttribute("selectedSessionType",currentSession.getType().getSessionType());
                    /*
                     * setting groups and selected group
                     */
                    List<Group> groups=Group.findByHouseTypeSessionTypeYear(houseTypes.get(0), sessionTypes.get(0),years.get(0));
                    model.addAttribute("groups",groups);
                    model.addAttribute("selectedgroup",groups.get(0).getNumber());
                    /*
                     * setting departments,sub-departments,selected departments and selected sub departments
                     */
                    List<MasterVO> departments=new ArrayList<MasterVO>();
                    List<MasterVO> subdepartments=new ArrayList<MasterVO>();
                    if(!groups.isEmpty()){
                        departments=MemberMinister.findAssignedDepartmentsVO(groups.get(0), locale);
                        subdepartments=MemberMinister.findAssignedSubDepartmentsVO(groups.get(0), locale);

                    }
                    model.addAttribute("departments",departments);
                    model.addAttribute("subdepartments", subdepartments);
                }
            }
        }
        /*
         * setting device types and there is no initial selected device type
         */
        List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "type",ApplicationConstants.ASC, locale);
        model.addAttribute("deviceTypes", deviceTypes);
        /*
         * setting locale
         */
        domain.setLocale(locale);
        model.addAttribute("locale",locale);
        /*
         * setting current date
         */
        String currentDate=FormaterUtil.getDateFormatter(locale).format(new Date());
        model.addAttribute("currentdate", currentDate);
    }

    @Override
    protected void populateEdit(final ModelMap model, final UserGroup domain,
            final HttpServletRequest request) {
        /*
         * setting locale
         */
        String locale=domain.getLocale();
        model.addAttribute("locale",locale);
        /*
         * In case of starred question we need following parameters:HOUSETYPE,
         * DEVICETYPE,YEAR,SESSIONTYPE,GROUP,DEPARTMENT,SUBDEPARTMENT.
         */
        /*
         * setting house types and selected house type
         */
        List<HouseType> houseTypes=HouseType.findAllNoExclude("name",ApplicationConstants.ASC, locale);
        model.addAttribute("housetypes",houseTypes);
        String strHouseType=domain.getParameterValue("HOUSETYPE_"+locale).trim();
        HouseType houseType=null;
        if(!houseTypes.isEmpty()){
            if(!strHouseType.isEmpty()){
                model.addAttribute("selectedHouseType",strHouseType);
                houseType=HouseType.findByName(HouseType.class,strHouseType, locale);
            }
        }
        /*
         * setting years and selected year
         */
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
        Integer selectedYear=0;
        if(!domain.getParameterValue("YEAR_"+locale).isEmpty()){
            selectedYear=Integer.parseInt(domain.getParameterValue("YEAR_"+locale).trim());
            model.addAttribute("selectedYear",selectedYear);
        }
        /*
         * setting session types and selected session type
         */
        List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
        model.addAttribute("sessionTypes",sessionTypes);
        String strSessionType=domain.getParameterValue("SESSIONTYPE_"+locale);
        SessionType sessionType=null;
        if(!strSessionType.isEmpty()){
            model.addAttribute("selectedSessionType",strSessionType);
            sessionType=SessionType.findByFieldName(SessionType.class,"sessionType",strSessionType.trim(), locale);
        }
        /*
         * setting device types and selected device types
         */
        List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "type",ApplicationConstants.ASC, locale);
        model.addAttribute("deviceTypes", deviceTypes);
        model.addAttribute("selectedDeviceType",domain.getParameterValue("DEVICETYPE_"+locale).trim());
        /*
         * setting groups and selected groups
         */
        List<Group> groups=new ArrayList<Group>();
        if(houseType!=null&&sessionType!=null&&selectedYear!=0){
            groups=Group.findByHouseTypeSessionTypeYear(houseType,sessionType,selectedYear);
        }
        String strGroup=domain.getParameterValue("GROUP_"+locale).trim();
        Integer[] selectedGroupNumbers=null;
        if(!strGroup.isEmpty()){
            String[] strGroups=strGroup.split("##");
            selectedGroupNumbers=new Integer[strGroups.length];
            for(int i=0;i<strGroups.length;i++){
                selectedGroupNumbers[i]=Integer.parseInt(strGroups[i]);
            }
            model.addAttribute("selectedGroup",domain.getParameterValue("GROUP_"+locale).trim());
        }
        model.addAttribute("groups",groups);
        /*
         * setting departments,sub-departments,selected department and selected sub department
         */
        List<MasterVO> departments=new ArrayList<MasterVO>();
        List<MasterVO> subdepartments=new ArrayList<MasterVO>();
        if(selectedGroupNumbers!=null&&houseType!=null&&sessionType!=null&&selectedYear!=0){
            departments=MemberMinister.findAssignedDepartmentsVO(selectedGroupNumbers, houseType, sessionType, selectedYear, locale);
            subdepartments=MemberMinister.findAssignedSubDepartmentsVO(selectedGroupNumbers, houseType, sessionType, selectedYear, locale);

        }
        model.addAttribute("departments",departments);
        model.addAttribute("subdepartments", subdepartments);
        model.addAttribute("selectedDepartment",domain.getParameterValue("DEPARTMENT_"+locale).trim());
        model.addAttribute("selectedSubDepartment",domain.getParameterValue("SUBDEPARTMENT_"+locale).trim());
        /*
         * setting current date
         */

        String currentDate=FormaterUtil.getDateFormatter(locale).format(new Date());
        model.addAttribute("currentdate", currentDate);
        /*
         * setting user group types
         */
        List<UserGroupType> userGroupTypes=UserGroupType.findAll(UserGroupType.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("userGroupTypes",userGroupTypes);
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

    @SuppressWarnings("unchecked")
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

    @Override
    protected void customValidateCreate(final UserGroup domain, final BindingResult result,
            final HttpServletRequest request) {
        // Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }

    @Override
    protected void customValidateUpdate(final UserGroup domain, final BindingResult result,
            final HttpServletRequest request) {
        // Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }
}
