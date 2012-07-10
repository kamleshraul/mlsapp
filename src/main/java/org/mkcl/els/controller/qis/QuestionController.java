package org.mkcl.els.controller.qis;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.QuestionType;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("question")
public class QuestionController extends GenericController<Question>{

    @Override
    protected void populateModule(final ModelMap model, final HttpServletRequest request,
            final String locale, final AuthUser currentUser) {
        //Lets assume that housetype now we are reading from role by splitting
        //role name at _.
        Set<Role> roles=currentUser.getRoles();
        //Now we will check if the user is an actor of starred question module.
        //starred question module has following axtors:MEMBER,MEMBERPA,DEO,ASSISTANT
        String houseType =null;
        for(Role i:roles){
            if(i.getName().startsWith("MEMBER")||i.getName().startsWith("QUESTION_DATA_ENTRY_OPERATOR")
                    ||i.getName().startsWith("MEMBERPA")||i.getName().startsWith("QUESTIONASSISTANT")){
                String temp[]=i.getName().split("_");
                houseType=temp[temp.length-1].toLowerCase();
            }
        }
        //now if the user has allowed roles then only they will be permitted to view
        //the module.otherwise error message will be visible.
        if(houseType!=null){
            //to handle both house cases
            model.addAttribute("houseType",houseType);
        }
        //adding question type to model.This will be obtained from request and will vary
        //according to the link clicked by user
        QuestionType questionType=QuestionType.findByFieldName(QuestionType.class, "type",request.getParameter("type"), locale);
        if(questionType!=null){
            model.addAttribute("questionType",questionType.getId());
        }
    }

    @Override
    protected void populateList(final ModelMap model, final HttpServletRequest request,
            final String locale, final AuthUser currentUser) {
        //**********************populating housetype******************
        String houseType = request.getParameter("houseType");
        model.addAttribute("houseType",houseType);
        //*********************populating housetypes**********************
        //housetypes are arranged in increasing order of their type
        List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
        model.addAttribute("houseTypes", houseTypes);
        //*********************populating houses**********************
        //houses are arranged in decreasing order of their formation date
        List<House> houses = House.findByHouseType(houseType, locale);
        model.addAttribute("houses", houses);
        House currentHouse = null;
        if(! houses.isEmpty()){
            currentHouse = houses.get(0);
        }
        //*****************populating sessions***************************
        //Current house is the first entry of houses
        //latest session is find by ararnging current house session in decreasing order
        //of their start date
        //sessions will be looked according to the year and house id
        Integer year=new GregorianCalendar().get(Calendar.YEAR);
        model.addAttribute("sessionYear",year);
        //**********************populating session type and latest session type********************
        List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
        model.addAttribute("sessionTypes",sessionTypes);
        //List<Session> sessions = new ArrayList<Session>();
        if(currentHouse != null){
            model.addAttribute("house", currentHouse.getId());
            Session latestSession=Session.findLatestSession(currentHouse);
            if(latestSession.getId()!=null){
                SessionType sessionType=latestSession.getType();
                if(sessionType!=null){
                    model.addAttribute("sessionType",sessionType.getId());
                }
            }
        }
        //model.addAttribute("sessions", sessions);
        //*********************populating questiontypes**********************
        //questiontypes are arranged in increasing order of their name
        List<QuestionType> questionTypes = QuestionType.findAll(QuestionType.class, "name", ApplicationConstants.ASC, locale);
        model.addAttribute("questionTypes", questionTypes);
        QuestionType questionType=QuestionType.findById(QuestionType.class, Long.parseLong(request.getParameter("questionType")));
        if(questionType!=null){
            model.addAttribute("questionType",questionType.getId());
        }
    }

    @Override
    protected void populateNew(final ModelMap model, final Question domain, final String locale,
            final HttpServletRequest request) {
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
        if(houseType!=null&&selectedYear!=null&&sessionType!=null){
        	selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
        	domain.setSession(selectedSession);
        	model.addAttribute("session",selectedSession.getId());
        }
        //***********populating question types***************************
        List<QuestionType> questionTypes=QuestionType.findAll(QuestionType.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("questionTypes",questionTypes);
        //*************populating question type in domain*****************
        String selectedQuestionType=request.getParameter("questionType");
        QuestionType questionType=null;
        if(selectedQuestionType!=null){
            if(!selectedQuestionType.isEmpty()){
                questionType=QuestionType.findById(QuestionType.class,Long.parseLong(selectedQuestionType));
                domain.setType(questionType);

            }
        }
        //******************populating submission date in domain to current date*****************************
        domain.setSubmissionDate(new Date());
        //*****************populating primary members and supporting members**********
        //The same model attribute 'members' will be used to populate both primary and supporting members.
        //It will be populated with list of members who will be/were active during a particular session.
        //This can be found by searching for entries in members_houses_roles table where role.priority=0,
        //member.locale='locale',house='house' and to_date>=session.start_date and to_date>=session.end_date
        String selectedHouse=request.getParameter("house");
        House house=null;
        if(selectedHouse!=null){
        	if(!selectedHouse.isEmpty()){
        		house=House.findById(House.class, Long.parseLong(selectedHouse));
        	}
        }
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
        		//model.addAttribute("departments",ministries.get(0).getMemberDepartments());
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
        }
    }
}
