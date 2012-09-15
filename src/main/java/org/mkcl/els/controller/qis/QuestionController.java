package org.mkcl.els.controller.qis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("question")
public class QuestionController extends GenericController<Question>{

    @Override
    protected void populateModule(final ModelMap model, final HttpServletRequest request,
            final String locale, final AuthUser currentUser) {
        Set<Role> roles=currentUser.getRoles();
        if(roles.isEmpty()){
            model.addAttribute("errorcode", "requiredrolenotfound");
        }else{
            String houseType =null;
            //housetype is obtained from role defined for the authenticated user
            //defining the actors that are allowed to view qis
            CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,ApplicationConstants.QIS_ACTOR_LIST_CUSTOMPARAM_NAME, "");
            if(customParameter!=null){
                String actors=customParameter.getValue();
                //all roles must be of the pattern NAME_HOUSETYPE
                //all uppercase,NAME part shouldnot contain any _
                for(Role i:roles){
                    String temp[]=i.getName().split("_");
                    if(actors.contains(temp[0])){
                        houseType=temp[1].toLowerCase();
                        break;
                    }
                }
                if(houseType!=null){
                    //actual check is performed to prevent sql injection.Only these three are
                    //allowed house type
                    if(houseType.equals(ApplicationConstants.LOWER_HOUSE)||houseType.equals(ApplicationConstants.UPPER_HOUSE)||houseType.equals(ApplicationConstants.BOTH_HOUSE)){
                        model.addAttribute("houseType",houseType);
                    }else{
                        model.addAttribute("errorcode", "undefinedhousetype");
                    }
                }else{
                    model.addAttribute("errorcode", "requiredrolenotfound");
                }
                //question type is obtained when user clicks on device name
                DeviceType questionType=DeviceType.findByFieldName(DeviceType.class, "type",request.getParameter("type"), locale);
                if(questionType!=null){
                    model.addAttribute("questionType",questionType.getId());
                }else{
                    model.addAttribute("errorcode", "questiontypeundefined");
                }
                //populating current year as sessionYear
                Calendar calendar=new GregorianCalendar();
                Integer sessionYear=calendar.get(Calendar.YEAR);
                model.addAttribute("sessionYear",sessionYear);
                //populating current session type
                HouseType hType=HouseType.findByFieldName(HouseType.class,"type",houseType, locale);
                Session currentSession=Session.findLatestSession(hType, sessionYear);
                model.addAttribute("sessionType", currentSession.getType().getId());
            }else{
                model.addAttribute("errorcode", "actorlistnotset");
            }
        }
    }

    @Override
    protected void populateList(final ModelMap model, final HttpServletRequest request,
            final String locale, final AuthUser currentUser) {
        //**********************populating housetype******************
        String houseType = request.getParameter("houseType");
        model.addAttribute("houseType",houseType);
        //*********************populating housetypes**********************
        List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
        model.addAttribute("houseTypes", houseTypes);
        //*********************populating session years************
        String strYear=request.getParameter("sessionYear");
        Integer year=0;
        if(strYear==null){
            year=new GregorianCalendar().get(Calendar.YEAR);
        }else{
            year=Integer.parseInt(strYear);
        }
        model.addAttribute("sessionYear",year);
        CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
        List<Integer> years=new ArrayList<Integer>();
        if(houseFormationYear!=null){
            Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
            for(int i=year;i>=formationYear;i--){
                years.add(i);
            }
        }else{
            model.addAttribute("errorcode", "houseformationyearnotset");
        }
        model.addAttribute("years",years);
        //********************populating session types***************
        HouseType selectedHouseType=HouseType.findByFieldName(HouseType.class,"type",houseType, locale);
        List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
        model.addAttribute("sessionTypes",sessionTypes);
        String strSessionType=request.getParameter("sessionType");
        Session latestSession=null;
        if(strSessionType==null){
            latestSession=Session.findLatestSession(selectedHouseType,year);
            if(latestSession!=null){
                model.addAttribute("sessionType",latestSession.getType().getId());
            }else{
                model.addAttribute("errorcode", "latestsessionnotset");
            }
        }else{
            model.addAttribute("sessionType",Long.parseLong(strSessionType));
        }
        //*********************populating questiontypes**********************
        List<DeviceType> questionTypes = DeviceType.findAll(DeviceType.class, "name", ApplicationConstants.ASC, locale);
        model.addAttribute("questionTypes", questionTypes);
        model.addAttribute("questionType", Long.parseLong(request.getParameter("questionType")));
    }

    @Override
    protected void populateNew(final ModelMap model, final Question domain, final String locale,
            final HttpServletRequest request) {
        domain.setLocale(locale);
        //*************populating housetypes*****************
        List<HouseType> houseTypes=HouseType.findAll(HouseType.class, "type",ApplicationConstants.ASC, locale);
        model.addAttribute("houseTypes",houseTypes);
        //************populating house type in domain*********************
        String selectedHouseType=request.getParameter("houseType");
        HouseType houseType=null;
        if(selectedHouseType!=null){
            if(!selectedHouseType.isEmpty()){
                houseType=HouseType.findByFieldName(HouseType.class,"type",selectedHouseType, locale);
                domain.setHouseType(houseType);
                model.addAttribute("houseType",selectedHouseType);
            }
        }
        //*******************populating year in view****************
        String selectedYear=request.getParameter("sessionYear");
        Integer sessionYear=0;
        if(selectedYear!=null){
            if(!selectedYear.isEmpty()){
                sessionYear=Integer.parseInt(selectedYear);
                model.addAttribute("sessionYear",sessionYear);
            }
        }
        Integer year=new GregorianCalendar().get(Calendar.YEAR);
        CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
        List<Integer> years=new ArrayList<Integer>();
        if(houseFormationYear!=null){
            Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
            for(int i=year;i>=formationYear;i--){
                years.add(i);
            }
        }
        model.addAttribute("years",years);
        //******************populating session types**********************
        List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
        model.addAttribute("sessionTypes",sessionTypes);
        //*****************populating session type in view**********
        String selectedSessionType=request.getParameter("sessionType");
        SessionType sessionType=null;
        if(selectedSessionType!=null){
            if(!selectedSessionType.isEmpty()){
                sessionType=SessionType.findById(SessionType.class,Long.parseLong(selectedSessionType));
                model.addAttribute("sessionType",sessionType.getId());
            }
        }
        //***************populating session*****************************
        Session selectedSession=null;
        House house=null;
        if(houseType!=null&&selectedYear!=null&&sessionType!=null){
            selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
            //domain.setSession(selectedSession);
            if(selectedSession!=null){
                model.addAttribute("session",selectedSession.getId());
                house=selectedSession.getHouse();
            }
        }
        //***********populating question types***************************
        List<DeviceType> questionTypes=DeviceType.findAll(DeviceType.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("questionTypes",questionTypes);
        //*************populating question type in domain*****************
        String selectedQuestionType=request.getParameter("questionType");
        DeviceType questionType=null;
        if(selectedQuestionType!=null){
            if(!selectedQuestionType.isEmpty()){
                questionType=DeviceType.findById(DeviceType.class,Long.parseLong(selectedQuestionType));
                domain.setType(questionType);

            }
        }
        //*****************populating primary members and supporting members**********
        //The same model attribute 'members' will be used to populate both primary and supporting members.
        //It will be populated with list of members who will be/were active during a particular session.
        //This can be found by searching for entries in members_houses_roles table where role.priority=0,
        //member.locale='locale',house='house' and to_date>=session.start_date and to_date>=session.end_date
        List<MasterVO> members=HouseMemberRoleAssociation.findAllActiveMemberVOSInSession(house,selectedSession,locale);
        model.addAttribute("members",members);
        //*********************populating groups*********************************
        List<Group> groups=Group.findByHouseTypeSessionTypeYear(houseType,sessionType,sessionYear);
        model.addAttribute("groups",groups);
        //*********************populating ministries*****************************
        if(!groups.isEmpty()){
            List<Ministry> ministries=groups.get(0).getMinistries();
            model.addAttribute("ministries",ministries);
            //******************populating departments**************************
            if(!ministries.isEmpty()){
                List<Department> departments=MemberMinister.findAssignedDepartments(ministries.get(0),  locale);
                model.addAttribute("departments",departments);
                if(!departments.isEmpty()){
                    //*****************populating sub departments***********************//
                    model.addAttribute("subDepartments",MemberMinister.findAssignedSubDepartments(ministries.get(0), departments.get(0),  locale));
                }
            }
        }
        //********************populating answering dates**************************
        if(!groups.isEmpty()){
            List<QuestionDates> answeringDates=groups.get(0).getQuestionDates();
            model.addAttribute("answeringDates",answeringDates);
        }
        //********************populating priorities******************************
        //This will be populated from custom parameter 'HIGHEST_QUESTION_PRIORITY'
        CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
        if(customParameter!=null){
            model.addAttribute("priority",customParameter.getValue());
            model.addAttribute("selectedPriority", customParameter.getValue());
        }
        //***********populating Languages**********
        List<Language> languages=Language.findAll(Language.class, "name", "desc", domain.getLocale());
        model.addAttribute("languages", languages);
        //populating submission date
        domain.setSubmissionDate(new Date());
    }

    @Override
    protected void populateEdit(final ModelMap model, final Question domain,
            final HttpServletRequest request) {
        //**************Initializing locale************************
        String locale=domain.getLocale();
        //*************populating housetypes*****************
        List<HouseType> houseTypes=HouseType.findAll(HouseType.class, "type",ApplicationConstants.ASC, locale);
        model.addAttribute("houseTypes",houseTypes);
        //************populating house type in domain in order to display appropriate watermark*********************
        HouseType houseType=domain.getHouseType();
        if(houseType!=null){
            model.addAttribute("houseType",houseType.getType());
        }
        //*******************populating year in view****************
        Session selectedSession=domain.getSession();
        Integer sessionYear=0;
        House house=null;
        SessionType sessionType=null;
        model.addAttribute("session",selectedSession.getId());
        sessionYear=selectedSession.getYear();
        model.addAttribute("sessionYear",sessionYear);
        if(selectedSession.getType()!=null){
            sessionType=selectedSession.getType();
            model.addAttribute("sessionType",sessionType.getId());
        }
        if(selectedSession.getHouse()!=null){
            house=selectedSession.getHouse();
        }

        Integer year=new GregorianCalendar().get(Calendar.YEAR);
        CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
        List<Integer> years=new ArrayList<Integer>();
        if(houseFormationYear!=null){
            Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
            for(int i=year;i>=formationYear;i--){
                years.add(i);
            }
        }
        model.addAttribute("years",years);
        //******************populating session types**********************
        List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
        model.addAttribute("sessionTypes",sessionTypes);
        //***********populating question types***************************
        List<DeviceType> questionTypes=DeviceType.findAll(DeviceType.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("questionTypes",questionTypes);
        //*****************populating primary members and supporting members**********
        //The same model attribute 'members' will be used to populate both primary and supporting members.
        //It will be populated with list of members who will be/were active during a particular session.
        //This can be found by searching for entries in members_houses_roles table where role.priority=0,
        //member.locale='locale',house='house' and to_date>=session.start_date and to_date>=session.end_date
        //this is added
        List<MasterVO> members=HouseMemberRoleAssociation.findAllActiveMemberVOSInSession(house,selectedSession,locale);
        model.addAttribute("members",members);
        //*********************populating groups*********************************
        List<Group> groups=Group.findByHouseTypeSessionTypeYear(houseType,sessionType,sessionYear);
        model.addAttribute("groups",groups);
        //*********************populating ministries*****************************
        if(!groups.isEmpty()){
            List<Ministry> ministries=domain.getGroup().getMinistries();
            model.addAttribute("ministries",ministries);
            //******************populating departments**************************
            if(!ministries.isEmpty()){
                model.addAttribute("departments",MemberMinister.findAssignedDepartments(domain.getMinistry(),locale));
                if(domain.getDepartment()!=null){
                    model.addAttribute("subDepartments",MemberMinister.findAssignedSubDepartments(domain.getMinistry(),domain.getDepartment(), locale));
                }
            }
        }
        //********************populating answering dates**************************
        if(!groups.isEmpty()){
            List<QuestionDates> answeringDates=domain.getGroup().getQuestionDates();
            model.addAttribute("answeringDates",answeringDates);
        }
        //********************populating priorities******************************
        //This will be populated from custom parameter 'HIGHEST_QUESTION_PRIORITY'
        CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
        if(customParameter!=null){
            model.addAttribute("priority",customParameter.getValue());
            //populating selected priority
            model.addAttribute("selectedPriority",domain.getPriority());
        }
        //*************populating primary member*****************
        if(domain.getPrimaryMember()!=null){
            model.addAttribute("primaryMember",domain.getPrimaryMember().getId());
            model.addAttribute("primaryMemberName",domain.getPrimaryMember().getFullname());
        }
        //***********populating supporting members**********
        List<Member> supportingMembers=domain.getSupportingMembers();
        model.addAttribute("supportingMembers",supportingMembers);
        if(!supportingMembers.isEmpty()){
            StringBuffer buffer=new StringBuffer();
            for(Member i:supportingMembers){
                buffer.append(i.getFullname()+",");
            }
            buffer.deleteCharAt(buffer.length()-1);
            model.addAttribute("supportingMembersName", buffer.toString());
        }
        //***********populating Languages**********
        List<Language> languages=Language.findAll(Language.class, "name", "desc", domain.getLocale());
        model.addAttribute("languages", languages);
        
        //************************setting answering dates********************
        if(domain.getAnsweringDate()!=null){
            model.addAttribute("selectedAnsweringDate", FormaterUtil.getDateFormatter(ApplicationConstants.SERVER_DATEFORMAT, locale).format(domain.getAnsweringDate()));
        }
        //initializing number to be displayed as title
        if(domain.getNumber()!=null){
        model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
        }
    }

    @Override
    protected void populateCreateIfNoErrors(final ModelMap model, final Question domain,
            final HttpServletRequest request) {
        //request.getSession().setAttribute("houseType",request.getParameter("houseType"));
        //request.getSession().setAttribute("session",request.getParameter("session"));
    	if(domain.getHouseType()!=null && request.getParameter("sessionYear")!="" && request.getParameter("sessionType")!=""
    			&&  domain.getType()!=null && domain.getPrimaryMember()!=null && domain.getMinistry()!=null && 
    			domain.getDepartment()!=null && domain.getSubject()!=""){
    		Status status=Status.findByFieldName(Status.class, "type", "complete", domain.getLocale());
    		domain.setStatus(status);
    	}
    	else{
    		Status status=Status.findByFieldName(Status.class, "type", "incomplete", domain.getLocale());
    		domain.setStatus(status);
    	}

    }

    @Override
    protected void populateUpdateIfNoErrors(final ModelMap model, final Question domain,
            final HttpServletRequest request) {
    	if(domain.getHouseType()!=null && request.getParameter("sessionYear")!="" && request.getParameter("sessionType")!=""
    			&&  domain.getType()!=null && domain.getPrimaryMember()!=null && domain.getMinistry()!=null && 
    			domain.getDepartment()!=null && domain.getSubject()!=""){
    		Status status=Status.findByFieldName(Status.class, "type", "complete", domain.getLocale());
    		domain.setStatus(status);
    	}
    	else{
    		Status status=Status.findByFieldName(Status.class, "type", "incomplete", domain.getLocale());
    		domain.setStatus(status);
    	}
    }

    @Override
    protected void populateCreateIfErrors(final ModelMap model, final Question domain,
            final HttpServletRequest request) {
        populateEdit(model, domain, request);
    }

    @Override
    protected void populateUpdateIfErrors(final ModelMap model, final Question domain,
            final HttpServletRequest request) {
        populateEdit(model, domain, request);
    }

    @Override
    protected void customValidateCreate(final Question domain, final BindingResult result,
            final HttpServletRequest request) {
    }

    @Override
    protected void customValidateUpdate(final Question domain, final BindingResult result,
            final HttpServletRequest request) {
    }


}
