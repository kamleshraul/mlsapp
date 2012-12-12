package org.mkcl.els.controller.qis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallotCouncil;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroup;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("ballot")
public class BallotController extends GenericController<MemberBallotCouncil>{

    @Override
    protected void populateModule(final ModelMap model, final HttpServletRequest request,
            final String locale, final AuthUser currentUser) {
        /*
         * adding housetype of authenticated user and list of house types to model
         */
        List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
        String houseType=this.getCurrentUser().getHouseType();
        model.addAttribute("houseTypes", houseTypes);
        model.addAttribute("houseType",houseType);
        /*
         * adding session types and session type of the most recent session entry in session
         */
        List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
        HouseType authUserHouseType=HouseType.findByFieldName(HouseType.class, "type",houseType, locale);
        Session lastSessionCreated=Session.findLatestSession(authUserHouseType);
        Integer year=new GregorianCalendar().get(Calendar.YEAR);
        if(lastSessionCreated.getId()!=null){
            year=lastSessionCreated.getYear();
            model.addAttribute("sessionType",lastSessionCreated.getType().getId());
        }else{
            model.addAttribute("errorcode","nosessionentriesfound");
        }
        model.addAttribute("sessionTypes",sessionTypes);
        /*
         * adding years and session year
         */
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
        model.addAttribute("sessionYear",year);
        /*
         * adding list of device types which are eligible for ballot
         */
        List<DeviceType> deviceTypes = DeviceType.findAll(DeviceType.class,"name",ApplicationConstants.ASC, locale);
        List<DeviceType> deviceTypesBallotNeeded=new ArrayList<DeviceType>();
        String ballotEnabledFor=lastSessionCreated.getDeviceTypesNeedBallot();
        if(ballotEnabledFor!=null){
            if(!ballotEnabledFor.isEmpty()){
                for(DeviceType i:deviceTypes){
                    if(ballotEnabledFor.contains(i.getType())){
                        deviceTypesBallotNeeded.add(i);
                    }
                }
            }
        }
        model.addAttribute("deviceTypes", deviceTypesBallotNeeded);
        /*
         * ballot will be displayed according to a particular group answeirng dates
         */
        List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
        if(userGroups!=null){
            if(!userGroups.isEmpty()){
                for(UserGroup i:userGroups){
                    UserGroup j=UserGroup.findById(UserGroup.class,i.getId());
                    String strType=j.getUserGroupType().getType();
                    if(strType.equals("deputy_secretary")
                            ||strType.equals("officer_special_duty")
                            ||strType.equals("joint_secretary")
                            ||strType.equals("secretary")
                            ||strType.equals("principal_secretary")
                            ||strType.equals("deputy_speaker")
                            ||strType.equals("speaker")
                            ||strType.equals("deputy_chairman")
                            ||strType.equals("chairman")
                            ||strType.equals("assistant")
                            ||strType.equals("under_secretary")
                            ||strType.equals("section_officer"
                            )
                    ){
                        String groupNumber=j.getParameterValue("GROUP_"+locale);
                        String[] groups=groupNumber.split("##");
                        if(lastSessionCreated!=null){
                            if(groups.length>0){
                                model.addAttribute("groups",groups.length);
                                Group group=Group.findByNumberHouseTypeSessionTypeYear(Integer.parseInt(groups[0]), authUserHouseType, lastSessionCreated.getType(), year);
                                if(group!=null){
                                    model.addAttribute("answeringDates",group.getAnsweringDates(ApplicationConstants.ASC));
                                }
                                model.addAttribute("usergroup",strType);
                                break;
                            }
                        }
                    }else if(strType.equals("member")){
                        model.addAttribute("usergroup",strType);
                    }
                }
            }
        }
        /*
         * if housetype is upperhouse then there will be member balloting.Member balloting is done for n rounds.we obtain the number of rounds
         * from custom parameter MEMBER_BALLOT_NO_OF_ROUNDS
         */
        CustomParameter customParameter=CustomParameter.findByFieldName(CustomParameter.class,"name", "MEMBER_BALLOT_NO_OF_ROUNDS", "");
        if(customParameter!=null){
            if(houseType.equals("upperhouse")){
                model.addAttribute("noOfRounds",customParameter.getValue());
            }
        }
    }

    @Transactional
    @RequestMapping(value="/memberballot",method=RequestMethod.GET)
    public String viewMemberBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String strHouseType=request.getParameter("houseType");
        String strSessionYear=request.getParameter("sessionYear");
        String strSessionType=request.getParameter("sessionType");
        String strDeviceType=request.getParameter("deviceType");
        String strRound=request.getParameter("round");
        model.addAttribute("round",strRound);
        List<Member> memberBallots=new ArrayList<Member>();
        if(strHouseType!=null&&strSessionYear!=null&&strSessionType!=null&&strDeviceType!=null&&strRound!=null){
            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
            Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
            model.addAttribute("place",session.getPlace().getPlace());
            Date date=session.getFirstBallotDate();
            if(date!=null){
                String formattedDate=FormaterUtil.getDateFormatter("dd-MM-yyyy", locale.toString()).format(date);
                model.addAttribute("date",formattedDate);
            }
            DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
            memberBallots=MemberBallotCouncil.viewMemberBallot(session, deviceType, Integer.parseInt(strRound), locale.toString());
        }
        model.addAttribute("memberBallots",memberBallots);
        return "ballot/memberballot";
    }

    @Transactional
    @RequestMapping(value="/prioritylist",method=RequestMethod.GET)
    public String viewPriorityList(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String strHouseType=request.getParameter("houseType");
        String strSessionYear=request.getParameter("sessionYear");
        String strSessionType=request.getParameter("sessionType");
        String strDeviceType=request.getParameter("deviceType");
        if(strHouseType!=null&&strSessionYear!=null&&strSessionType!=null&&strDeviceType!=null){
            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
            DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
            Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
            if(session!=null){
                AuthUser authUser=this.getCurrentUser();
                Member currentMember=Member.findMember(authUser.getFirstName(),authUser.getMiddleName(),authUser.getLastName(),authUser.getBirthDate(), locale.toString());
                if(currentMember.getId()!=null){
                    Status internalStatus=Status.findByType("question_workflow_approving_admission", locale.toString());
                    List<Question> questions=Question.findAllFirstBatch(currentMember,session,deviceType,internalStatus);
                    model.addAttribute("questions",questions);
                    model.addAttribute("noOfAdmittedQuestions",questions.size());
                    model.addAttribute("deviceTypeName",deviceType.getName());
                    model.addAttribute("deviceType",deviceType.getId());
                    model.addAttribute("session", session.getId());
                    model.addAttribute("member",currentMember.getId());
                }
            }
        }
        return "ballot/prioritylist";
    }

    @Transactional
    @RequestMapping(value="/prioritylist",method=RequestMethod.POST)
    public String createPriorityList(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String strHouseType=request.getParameter("houseType");
        String strSessionYear=request.getParameter("sessionYear");
        String strSessionType=request.getParameter("sessionType");
        String strDeviceType=request.getParameter("deviceType");
        if(strHouseType!=null&&strSessionYear!=null&&strSessionType!=null&&strDeviceType!=null){
            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
            DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
            Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
            if(session!=null){
                AuthUser authUser=this.getCurrentUser();
                Member currentMember=Member.findMember(authUser.getFirstName(),authUser.getMiddleName(),authUser.getLastName(),authUser.getBirthDate(), locale.toString());
                if(currentMember.getId()!=null){
                    Status internalStatus=Status.findByType("question_workflow_approving_admission", locale.toString());
                    List<Question> questions=Question.findAllFirstBatch(currentMember,session,deviceType,internalStatus);
                    model.addAttribute("questions",questions);
                    model.addAttribute("noOfAdmitted",questions.size());
                    model.addAttribute("noOfQuestions",session.getNumberOfQuestionInFirstBatchUH());
                    model.addAttribute("deviceTypeName",deviceType.getName());
                }
            }
        }
        return "ballot/prioritylist";
    }

    @Transactional
    @RequestMapping(value="/memberballot",method=RequestMethod.POST)
    public @ResponseBody String createMemberBallot(final HttpServletRequest request,final Locale locale){
        String strHouseType=request.getParameter("houseType");
        String strSessionYear=request.getParameter("sessionYear");
        String strSessionType=request.getParameter("sessionType");
        String strDeviceType=request.getParameter("deviceType");
        String strRound=request.getParameter("round");
        Boolean status=false;
        if(strHouseType!=null&&strSessionYear!=null&&strSessionType!=null&&strDeviceType!=null&&strRound!=null){
            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
            Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
            DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
            status=MemberBallotCouncil.createMemberBallot(session, deviceType, Integer.parseInt(strRound), locale.toString());
        }
        if(status){
            return "success";
        }else{
            return "failure";
        }
    }
}
