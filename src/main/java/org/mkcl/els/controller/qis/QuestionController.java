package org.mkcl.els.controller.qis;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberContactVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.QuestionRevisionVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Ballot;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Chart;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.ClarificationNeededFrom;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.MemberBallotChoice;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.QuestionDraft;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("question")
public class QuestionController extends GenericController<Question>{

    @Autowired
    private IProcessService processService;

    @Override
    protected void populateModule(final ModelMap model, final HttpServletRequest request,
            final String locale, final AuthUser currentUser) {
        /*
         * populating selected device type which user selected by clicking device type menu links.
         * if a device is not ready it will show work under progress error message.If the device is
         * ready then selected device type,house type of authenticated user
         * ,session year and session type of the most recent session is added to the model.These will be passed as
         * quesry string to /list request
         */
        DeviceType deviceType=DeviceType.findByFieldName(DeviceType.class, "type",request.getParameter("type"), locale);
        if(deviceType!=null){
            /*
             * adding selected question type and list of question types to model
             */
            List<DeviceType> deviceTypes = DeviceType.findDeviceTypesStartingWith("questions", locale);
            model.addAttribute("questionTypes", deviceTypes);
            model.addAttribute("questionType",deviceType.getId());
            model.addAttribute("questionTypeType",deviceType.getType());

            /*
             * adding housetype of authenticated user and list of house types to model
             */
            List<HouseType> houseTypes = new ArrayList<HouseType>();
            String houseType=this.getCurrentUser().getHouseType();
            if(houseType.equals("lowerhouse")){
                houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
            }else if(houseType.equals("upperhouse")){
                houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
            }else if(houseType.equals("bothhouse")){
                houseTypes=HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
            }
            model.addAttribute("houseTypes", houseTypes);
            if(houseType.equals("bothhouse")){
                houseType="lowerhouse";
            }
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
            List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
            if(userGroups!=null){
                if(!userGroups.isEmpty()){
                    for(UserGroup i:userGroups){
                        UserGroup j=UserGroup.findById(UserGroup.class,i.getId());
                        String strType=j.getUserGroupType().getType();
                        model.addAttribute("usergroupType",strType);
                        model.addAttribute("usergroup",j.getId());
                        if(strType.equals("assistant")
                                ||strType.equals("under_secretary")
                                ||strType.equals("deputy_secretary")
                                ||strType.equals("officer_special_duty")
                                ||strType.equals("joint_secretary")
                                ||strType.equals("secretary")
                                ||strType.equals("principal_secretary")
                                ||strType.equals("deputy_speaker")
                                ||strType.equals("speaker")
                                ||strType.equals("deputy_chairman")
                                ||strType.equals("chairman")
                                ||strType.equals("section_officer")
                                ||strType.equals("member")){
                            String groupNumber=j.getParameterValue("GROUP_"+locale);
                            model.addAttribute("groupsAllowed",groupNumber);
                            if(groupNumber!=null){
                                if(!groupNumber.isEmpty()){
                                    String[] strgroups=groupNumber.split("##");
                                    List<Group> groups=new ArrayList<Group>();
                                    if(lastSessionCreated!=null){
                                        for(String k:strgroups){
                                            Group group=Group.findByNumberHouseTypeSessionTypeYear(Integer.parseInt(k),  authUserHouseType, lastSessionCreated.getType(), year);
                                            if(group!=null){
                                                groups.add(group);
                                            }
                                        }
                                        model.addAttribute("groups",groups);
                                        if(!groups.isEmpty()){
                                            List<QuestionDates> questionDates=groups.get(0).getQuestionDates();
                                            List<MasterVO> masterVOs=new ArrayList<MasterVO>();
                                            if(questionDates!=null){
                                                if(!questionDates.isEmpty()){
                                                    for(QuestionDates qd:questionDates){
                                                        MasterVO masterVO=new MasterVO();
                                                        masterVO.setId(qd.getId());
                                                        masterVO.setName(FormaterUtil.getDateFormatter(locale).format(qd.getAnsweringDate()));
                                                        masterVOs.add(masterVO);
                                                    }
                                                }
                                            }
                                            model.addAttribute("answeringDates",masterVOs);
                                        }
                                        break;
                                    }
                                }
                            }

                        }
                    }
                }
            }
            //in case of clerk there are no user groups
            Set<Role> roles=this.getCurrentUser().getRoles();
            for(Role i:roles){
                if(i.getType().equals("CLERK")){
                    model.addAttribute("userrole",i.getType());
                    break;
                }
            }
        }else{
            model.addAttribute("errorcode","workunderprogress");
        }
        CustomParameter totalRoundsCouncilBallot=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_TOTALROUNDS", "");
        if(totalRoundsCouncilBallot!=null){
            int rounds=Integer.parseInt(totalRoundsCouncilBallot.getValue());
            List<Reference> references=new ArrayList<Reference>();
            NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(locale);
            for(int i=1;i<=rounds;i++){
                references.add(new Reference(String.valueOf(i),String.valueOf(format.format(i))));
            }
            model.addAttribute("rounds",references);
        }else{
            logger.error("**** Custom Parameter 'STARRED_MEMBERBALLOTCOUNCIL_TOTALROUNDS' not set");
        }
    }

    @Override
    protected void populateList(final ModelMap model, final HttpServletRequest request,
            final String locale, final AuthUser currentUser) {

    }
    @Override
    protected String modifyURLPattern(final String urlPattern,final HttpServletRequest request,final ModelMap model,final String locale) {
        String newUrlPattern=urlPattern;
        String usergroup=request.getParameter("usergroup");
        if(usergroup!=null){
            if(!usergroup.isEmpty()){
                UserGroup j=UserGroup.findById(UserGroup.class,Long.parseLong(usergroup));
                String strType=j.getUserGroupType().getType();
                if(strType.equals("assistant")
                        ||strType.equals("under_secretary")
                        ||strType.equals("deputy_secretary")
                        ||strType.equals("officer_special_duty")
                        ||strType.equals("joint_secretary")
                        ||strType.equals("secretary")
                        ||strType.equals("principal_secretary")
                        ||strType.equals("deputy_speaker")
                        ||strType.equals("speaker")
                        ||strType.equals("deputy_chairman")
                        ||strType.equals("chairman")
                        ||strType.equals("section_officer")){
                    newUrlPattern=urlPattern+"?usergroup=assistant";
                    String groupNumber=j.getParameterValue("GROUP_"+locale);
                    String[] strgroups=groupNumber.split("##");
                    model.addAttribute("ugparam",strgroups[0]);
                }else if(strType.equals("member")){
                    model.addAttribute("ugparam",this.getCurrentUser().getActualUsername());
                }
            }else{
                model.addAttribute("ugparam",this.getCurrentUser().getActualUsername());
            }
        }else{
            model.addAttribute("ugparam",this.getCurrentUser().getActualUsername());
        }
        return newUrlPattern;
    }

    @Override
    protected String modifyNewUrlPattern(final String servletPath,
            final HttpServletRequest request, final ModelMap model, final String string) {
        String userrole=request.getParameter("userrole");
        String strusergroup=request.getParameter("usergroup");
        if(userrole!=null){
            if(!userrole.isEmpty()){
                if(userrole.equals("CLERK")){
                    return servletPath.replace("new","newclerk");
                }
            }
        }
        if(strusergroup!=null){
            if(!strusergroup.isEmpty()){
                UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strusergroup));
                if(userGroup.getUserGroupType().getType().equals("member")){
                    return servletPath;
                }
            }
        }
        model.addAttribute("errorcode","permissiondenied");
        return servletPath.replace("new","error");
    }
    @Override
    protected void populateNew(final ModelMap model, final Question domain, final String locale,
            final HttpServletRequest request) {
        /*
         * ading locale in domain
         */
        domain.setLocale(locale);

        /*
         * adding list of available house types and selected house type in model.
         */
        String selectedHouseType=request.getParameter("houseType");
        HouseType houseType=null;
        if(selectedHouseType!=null){
            if(!selectedHouseType.isEmpty()){
                houseType=HouseType.findByFieldName(HouseType.class,"type",selectedHouseType, locale);
                domain.setHouseType(houseType);
                model.addAttribute("houseType",selectedHouseType);
                model.addAttribute("houseTypeName",houseType.getName());
                model.addAttribute("houseTypeId",houseType.getId());
            }
        }

        /*
         * adding list of available years and selected session year in model.
         */
        String selectedYear=request.getParameter("sessionYear");
        Integer sessionYear=0;
        if(selectedYear!=null){
            if(!selectedYear.isEmpty()){
                sessionYear=Integer.parseInt(selectedYear);
                model.addAttribute("sessionYearSelected",sessionYear);
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

        /*
         * adding list of available session types and selected session type in model.
         */
        List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
        model.addAttribute("sessionTypes",sessionTypes);
        String selectedSessionType=request.getParameter("sessionType");
        SessionType sessionType=null;
        if(selectedSessionType!=null){
            if(!selectedSessionType.isEmpty()){
                sessionType=SessionType.findById(SessionType.class,Long.parseLong(selectedSessionType));
                model.addAttribute("sessionTypeSelected",sessionType.getId());
            }
        }

        /*
         * adding list of available question types and selected question type in model
         */
        List<DeviceType> questionTypes = DeviceType.findDeviceTypesStartingWith("questions", locale);
        model.addAttribute("questionTypes",questionTypes);
        String selectedQuestionType=request.getParameter("questionType");
        DeviceType questionType=null;
        if(selectedQuestionType!=null){
            if(!selectedQuestionType.isEmpty()){
                questionType=DeviceType.findById(DeviceType.class,Long.parseLong(selectedQuestionType));
                domain.setType(questionType);
            }
        }

        /*
         * adding primary member to model
         */
        Member member=Member.findMember(this.getCurrentUser().getFirstName(),this.getCurrentUser().getMiddleName(),this.getCurrentUser().getLastName(),this.getCurrentUser().getBirthDate(),locale);
        if(member.getId()!=null){
            domain.setPrimaryMember(member);
            model.addAttribute("primaryMember",member.getId());
            model.addAttribute("primaryMemberName",member.getFullname());
        }

        /*
         * adding session to model.Now depending on the question type selected we will also be adding
         * ministries,groups,departments,subdepartments,answering dates after rotation order has been published in case
         * of starred question.
         */
        Session selectedSession=null;
        if(houseType!=null&&selectedYear!=null&&sessionType!=null){
            selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
            if(selectedSession!=null){
                model.addAttribute("session",selectedSession.getId());
                domain.setSession(selectedSession);
                /*
                 *adding constituency of primary member
                 */
                Long houseId=selectedSession.getHouse().getId();
                MasterVO constituency=null;
                if(selectedHouseType.equals("lowerhouse")){
                    constituency=Member.findConstituencyByAssemblyId(member.getId(), houseId);
                    model.addAttribute("primaryMemberConstituency",constituency.getName());
                }else if(selectedHouseType.equals("upperhouse")){
                    Date currentDate=new Date();
                    String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
                    constituency=Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
                    model.addAttribute("primaryMemberConstituency",constituency.getName());
                }

                /*
                 *In case of starred questions minsitry,groups,departments,subdepartments,answering dates will
                 *be visible only after rotation publishing date
                 */
                if(questionType.getType().trim().equals("questions_starred")){
                    Date rotationOrderPubDate=null;
                    CustomParameter serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
                    if(houseType.getType().equals("lowerhouse")){
                        String strRotationOrderPubDate = selectedSession.getParamater("questions_starred_rotationOrderPublishingDate");
                        rotationOrderPubDate = FormaterUtil.formatStringToDate(strRotationOrderPubDate, serverDateFormat.getValue());
                    }else if(houseType.getType().equals("upperhouse")){
                        String strRotationOrderPubDate = selectedSession.getParamater("questions_starred_rotationOrderPublishingDate");
                        rotationOrderPubDate = FormaterUtil.formatStringToDate(strRotationOrderPubDate, serverDateFormat.getValue());
                    }   CustomParameter rotationOrderDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");

                    if(rotationOrderDateFormat!=null){
                        if(rotationOrderPubDate!=null){
                            /*
                             * adding rotation order publishing date
                             */
                            String tempDate=FormaterUtil.getDateFormatter(rotationOrderDateFormat.getValue(), locale).format(rotationOrderPubDate);
                            String[] temp=tempDate.split(",");
                            String formattedDay=FormaterUtil.getDayInMarathi(temp[0], locale);
                            String formattedDate=temp[1].split(" ")[0];
                            String formattedMonth=FormaterUtil.getMonthInMarathi(temp[1].split(" ")[1], locale);
                            String formattedYear=temp[2];
                            model.addAttribute("rotationOrderPublishDate", formattedDay+","+formattedDate+" "+formattedMonth+","+formattedYear);

                            Date currentDate=new Date();
                            if(currentDate.equals(rotationOrderPubDate)||currentDate.after(rotationOrderPubDate)){
                                /*
                                 * adding ministries
                                 */
                                List<Ministry> ministries=Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
                                model.addAttribute("ministries",ministries);
                            }
                        }else{
                            model.addAttribute("errorcode","rotationorderpublishingdatenotset");
                        }
                    }else{
                        model.addAttribute("errorcode","rotationorderdateformatnotset");
                    }
                }else{
                    /*
                     * adding ministries
                     */
                    List<Ministry> ministries=Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
                    model.addAttribute("ministries",ministries);
                }
            }else{
                model.addAttribute("errorcode","nosessionentriesfound");
            }
        }

        /*
         * adding list of available languages to model
         */
        List<Language> languages=Language.findAll(Language.class, "priority", ApplicationConstants.ASC, domain.getLocale());
        model.addAttribute("languages", languages);

        /*
         * adding list of available priorities to model
         */
        CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
        if(customParameter!=null){
            model.addAttribute("priority",customParameter.getValue());
        }else{
            model.addAttribute("errorcode","highestquestionprioritynotset");
        }
    }

    @Override
    protected String modifyEditUrlPattern(final String newUrlPattern,
            final HttpServletRequest request, final ModelMap model, final String locale) {
        String clubbedQuestionDetail=request.getParameter("clubbedQuestionDetail");
        if(clubbedQuestionDetail!=null){
            return newUrlPattern.replace("edit","editreadonly");
        }
        List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
        if(userGroups!=null){
            if(!userGroups.isEmpty()){
                /*
                 *first we will check if the user is an assistant
                 */
                for(UserGroup i:userGroups){
                    UserGroup userGroup=UserGroup.findById(UserGroup.class, i.getId());
                    String strType=userGroup.getUserGroupType().getType();
                    if(strType.equals("under_secretary")
                            ||strType.equals("deputy_secretary")
                            ||strType.equals("officer_special_duty")
                            ||strType.equals("joint_secretary")
                            ||strType.equals("secretary")
                            ||strType.equals("principal_secretary")
                            ||strType.equals("deputy_speaker")
                            ||strType.equals("speaker")
                            ||strType.equals("deputy_chairman")
                            ||strType.equals("chairman")
                            ||strType.equals("section_officer")){
                        return newUrlPattern.replace("edit","editreadonly");
                    }else if(strType.equals("member")){
                        return newUrlPattern;
                    }else if(strType.equals("assistant")){
                        return newUrlPattern.replace("edit","assistant");
                    }
                }
            }
        }
        model.addAttribute("errorcode","permissiondenied");
        return "questions/error";
    }


    @Override
    protected void populateEdit(final ModelMap model, final Question domain,
            final HttpServletRequest request) {
        String locale=domain.getLocale();

        /*
         * adding housetypes and selected house type
         */
        HouseType houseType=domain.getHouseType();
        model.addAttribute("houseType",houseType.getType());
        model.addAttribute("houseTypeName",houseType.getName());
        model.addAttribute("houseTypeId",houseType.getId());


        /*
         * adding session
         */
        Session selectedSession=domain.getSession();
        model.addAttribute("session",selectedSession.getId());

        /*
         * adding years and selected session year
         */
        Integer sessionYear=0;
        sessionYear=selectedSession.getYear();
        model.addAttribute("sessionYearSelected",sessionYear);
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


        /*
         * adding session types and selected session types
         */
        List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
        model.addAttribute("sessionTypes",sessionTypes);
        SessionType sessionType=null;
        if(selectedSession.getType()!=null){
            sessionType=selectedSession.getType();
            model.addAttribute("sessionTypeSelected",sessionType.getId());
        }


        /*
         * adding list of available question types and selected question type
         */
        List<DeviceType> questionTypes=DeviceType.findAll(DeviceType.class,"name",ApplicationConstants.ASC, locale);
        model.addAttribute("questionTypes",questionTypes);
        DeviceType questionType=domain.getType();
        model.addAttribute("deviceTypeSelected",questionType.getId());
        model.addAttribute("deviceType",questionType.getName());



        /*
         * adding ministries,groups,departments,sub-departments,answering dates
         */
        if(questionType.getType().trim().equals("questions_starred")){
            Date rotationOrderPubDate=null;
            CustomParameter serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
            if(houseType.getType().equals("lowerhouse")){
                String strRotationOrderPubDate = selectedSession.getParamater("questions_starred_rotationOrderPublishingDate");
                rotationOrderPubDate = FormaterUtil.formatStringToDate(strRotationOrderPubDate, serverDateFormat.getValue());
            }else if(houseType.getType().equals("upperhouse")){
                String strRotationOrderPubDate = selectedSession.getParamater("questions_starred_rotationOrderPublishingDate");
                rotationOrderPubDate = FormaterUtil.formatStringToDate(strRotationOrderPubDate, serverDateFormat.getValue());
            }
            CustomParameter rotationOrderDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
            if(rotationOrderDateFormat!=null){
                if(rotationOrderPubDate!=null){

                    /*
                     * adding rotation order publishing date
                     */
                    String tempDate=FormaterUtil.getDateFormatter(rotationOrderDateFormat.getValue(), locale).format(rotationOrderPubDate);
                    String[] temp=tempDate.split(",");
                    String formattedDay=FormaterUtil.getDayInMarathi(temp[0], locale);
                    String formattedDate=temp[1].split(" ")[0];
                    String formattedMonth=FormaterUtil.getMonthInMarathi(temp[1].split(" ")[1], locale);
                    String formattedYear=temp[2];
                    model.addAttribute("rotationOrderPublishDate", formattedDay+","+formattedDate+" "+formattedMonth+","+formattedYear);
                    Date currentDate=new Date();
                    if(currentDate.equals(rotationOrderPubDate)||currentDate.after(rotationOrderPubDate)){

                        /*
                         * adding ministries
                         */
                        List<Ministry> ministries=Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
                        model.addAttribute("ministries",ministries);
                        if(domain.getMinistry()!=null){
                            model.addAttribute("ministrySelected",domain.getMinistry().getId());
                        }

                        /*
                         * adding group
                         */
                        Ministry ministry=domain.getMinistry();
                        if(ministry!=null){
                            Group group=domain.getGroup();
                            model.addAttribute("group",domain.getGroup());

                            /*
                             * adding deparments
                             */
                            List<Department> departments=MemberMinister.findAssignedDepartments(ministry, locale);
                            model.addAttribute("departments",departments);
                            Department department=domain.getDepartment();
                            if(department!=null){
                                model.addAttribute("departmentSelected",department.getId());
                                /*
                                 * adding sub-departments
                                 */
                                List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry,department, locale);
                                model.addAttribute("subDepartments",subDepartments);
                                if(domain.getSubDepartment()!=null){
                                    model.addAttribute("subDepartmentSelected",domain.getSubDepartment().getId());
                                }
                            }
                            /*
                             * adding answering dates in case of starred questions
                             */
                            if(group!=null){
                                List<QuestionDates> answeringDates=group.getQuestionDates();
                                List<MasterVO> masterVOs=new ArrayList<MasterVO>();
                                for(QuestionDates i:answeringDates){
                                    MasterVO masterVO=new MasterVO();
                                    masterVO.setId(i.getId());
                                    masterVO.setName(FormaterUtil.getDateFormatter(locale).format(i.getAnsweringDate()));
                                    masterVOs.add(masterVO);
                                }
                                model.addAttribute("answeringDates",masterVOs);
                                if(domain.getAnsweringDate()!=null){
                                    model.addAttribute("answeringDateSelected", domain.getAnsweringDate().getId());
                                    model.addAttribute("answeringDate",FormaterUtil.getDateFormatter(locale).format(domain.getAnsweringDate().getAnsweringDate()));
                                }
                            }
                        }

                    }
                }
            }
        }else{
            /*
             * adding ministries
             */
            List<Ministry> ministries=Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
            model.addAttribute("ministries",ministries);
            if(domain.getMinistry()!=null){
                model.addAttribute("ministrySelected",domain.getMinistry().getId());
            }

            /*
             * adding group
             */
            Ministry ministry=domain.getMinistry();
            if(ministry!=null){
                Group group=domain.getGroup();
                model.addAttribute("group",group);

                /*
                 * adding deparments
                 */
                List<Department> departments=MemberMinister.findAssignedDepartments(ministry, locale);
                model.addAttribute("departments",departments);
                Department department=domain.getDepartment();
                if(department!=null){
                    model.addAttribute("departmentSelected",department.getId());
                    /*
                     * adding sub-departments
                     */
                    List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry,department, locale);
                    model.addAttribute("subDepartments",subDepartments);
                    if(domain.getSubDepartment()!=null){
                        model.addAttribute("subDepartmentSelected",domain.getSubDepartment().getId());
                    }
                }
            }
        }

        /*
         * adding primary member id and name to model.in case of member screen primary member name will
         * be displayed with last name first whereas in case of assistant it will be displayed with first name
         * first.
         */
        String memberNames=null;
        String primaryMemberName=null;
        Member member=domain.getPrimaryMember();
        if(member!=null){
            model.addAttribute("primaryMember",member.getId());
            primaryMemberName=member.getFullname();
            memberNames=primaryMemberName;
            model.addAttribute("primaryMemberName",primaryMemberName);
        }
        /*
         *adding constituency of primary member
         */
        Long houseId=selectedSession.getHouse().getId();
        MasterVO constituency=null;
        if(houseType.getType().equals("lowerhouse")){
            constituency=Member.findConstituencyByAssemblyId(member.getId(), houseId);
            model.addAttribute("primaryMemberConstituency",constituency.getName());
        }else if(houseType.getType().equals("upperhouse")){
            Date currentDate=new Date();
            String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
            constituency=Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
            model.addAttribute("primaryMemberConstituency",constituency.getName());
        }

        /*
         * adding list of supporting members to model.
         */
        List<SupportingMember> selectedSupportingMembers=domain.getSupportingMembers();
        List<Member> supportingMembers=new ArrayList<Member>();
        if(selectedSupportingMembers!=null){
            if(!selectedSupportingMembers.isEmpty()){
                StringBuffer bufferFirstNamesFirst=new StringBuffer();
                for(SupportingMember i:selectedSupportingMembers){
                    Member m=i.getMember();
                    bufferFirstNamesFirst.append(m.getFullname()+",");
                    supportingMembers.add(m);
                }
                bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length()-1);
                model.addAttribute("supportingMembersName", bufferFirstNamesFirst.toString());
                model.addAttribute("supportingMembers",supportingMembers);
                memberNames=primaryMemberName+","+bufferFirstNamesFirst.toString();
                model.addAttribute("memberNames",memberNames);
            }else{
                model.addAttribute("memberNames",memberNames);
            }
        }else{
            model.addAttribute("memberNames",memberNames);
        }
        /*
         * adding list of available languages to model
         */
        List<Language> languages=Language.findAll(Language.class, "priority", ApplicationConstants.ASC, domain.getLocale());
        model.addAttribute("languages", languages);
        if(domain.getLanguage()!=null){
            model.addAttribute("languageSelected",domain.getLanguage().getId());
        }

        /*
         * adding list of available priorities to model
         */
        CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "HIGHEST_QUESTION_PRIORITY", "");
        if(customParameter!=null){
            model.addAttribute("priority",customParameter.getValue());
            model.addAttribute("prioritySelected",domain.getPriority());
        }else{
            model.addAttribute("errorcode","highestquestionprioritynotset");
        }
        /*
         * adding creation date and submission date
         */
        CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
        if(dateTimeFormat!=null){
            if(domain.getCreationDate()!=null){
                model.addAttribute("creationDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getCreationDate()));
            }
            if(domain.getSubmissionDate()!=null){
                model.addAttribute("submissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getSubmissionDate()));
            }
        }
        /*
         * adding status
         */
        if(domain.getStatus()!=null){
            model.addAttribute("status",domain.getStatus().getId());
            model.addAttribute("statusType",domain.getStatus().getType());
        }
        /*
         * in case of assistant
         *
         */
        String clubbedQuestionDetail=request.getParameter("clubbedQuestionDetail");
        List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
        if(userGroups!=null){
            if(!userGroups.isEmpty()){
                /*
                 *first we will check if the user is an assistant
                 */
                for(UserGroup i:userGroups){
                    UserGroup userGroup=UserGroup.findById(UserGroup.class, i.getId());
                    String strType=userGroup.getUserGroupType().getType();
                    model.addAttribute("usergroup",strType);
                    if(strType.equals("assistant")
                            ||clubbedQuestionDetail!=null){
                        /*
                         * adding decision status in model and clarification needed from               *
                         */
                        List<Status> internalStatus=Status.findStartingWith("question_workflow_decisionstatus", "name", ApplicationConstants.ASC, domain.getLocale());
                        model.addAttribute("internalStatus",internalStatus);
                        if(domain.getInternalStatus()!=null){
                            model.addAttribute("internalStatusSelected",domain.getInternalStatus().getId());
                        }
                        /*
                         * adding clarification needed from
                         */
                        List<ClarificationNeededFrom> clarificationNeededFroms=ClarificationNeededFrom.findAll(ClarificationNeededFrom.class, "name",ApplicationConstants.ASC,domain.getLocale());
                        model.addAttribute("clarificationsNeededFrom",clarificationNeededFroms);
                        if(domain.getClarificationNeededFrom()!=null){
                            model.addAttribute("clarificationsNeededSelected",domain.getClarificationNeededFrom().getId());
                        }
                        /*
                         *List of actors
                         */
                        Long sessionId=selectedSession.getId();
                        model.addAttribute("sessionId", sessionId);
                        Long deviceTypeId=questionType.getId();
                        model.addAttribute("deviceTypeId", deviceTypeId);
                        String workflowType=internalStatus.get(0).getType();
                        model.addAttribute("workflowType", workflowType);
                        Integer groupNumber=domain.getGroup().getNumber();
                        model.addAttribute("groupNumber",groupNumber);
                        WorkflowConfig workflowConfig=WorkflowConfig.findLatest(sessionId,deviceTypeId,workflowType);
                        model.addAttribute("workflowConfigId",workflowConfig.getId());
                        model.addAttribute("level",1);
                        List<Reference> actorsIncreasingOrder=WorkflowConfig.findActors(sessionId,deviceTypeId,workflowType,groupNumber,workflowConfig.getId(),1,ApplicationConstants.ASC);
                        model.addAttribute("actors",actorsIncreasingOrder);

                        // added by amitd
                        String internalStatusType = domain.getInternalStatus().getType();
                        model.addAttribute("internalStatusType", internalStatusType);

                        //add clubbing
                        List<Reference> references=new ArrayList<Reference>();
                        List<Question> clubbedQuestions=domain.getClubbings();
                        StringBuffer buffer1=new StringBuffer();
                        for(Question q:clubbedQuestions){
                            Reference reference=new Reference();
                            reference.setId(String.valueOf(q.getId()));
                            reference.setName(String.valueOf(q.getNumber()));
                            references.add(reference);
                            buffer1.append(q.getPrimaryMember().getFullname()+",");
                            List<SupportingMember> clubbedSupportingMember=q.getSupportingMembers();
                            if(clubbedSupportingMember!=null){
                                if(!clubbedSupportingMember.isEmpty()){
                                    for(SupportingMember l:clubbedSupportingMember){
                                        buffer1.append(l.getMember().getFullname()+",");
                                    }
                                }
                            }

                        }
                        if(!buffer1.toString().isEmpty()){
                            buffer1.deleteCharAt(buffer1.length()-1);
                        }
                        String allMembersNames=memberNames+","+buffer1.toString();
                        model.addAttribute("memberNames",allMembersNames);
                        if(!references.isEmpty()){
                            model.addAttribute("clubbedQuestions",references);
                        }else{
                            if(domain.getParent()!=null){
                                model.addAttribute("parentNumber",domain.getParent().getNumber());
                                model.addAttribute("parent",domain.getParent().getId());
                            }
                        }
                        String edit=request.getParameter("edit");
                        if(edit!=null){
                            if(!edit.isEmpty()){
                                model.addAttribute("edit", edit);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }


    @Override
    protected void customValidateCreate(final Question domain, final BindingResult result,
            final HttpServletRequest request) {
        /*
         * populating supporting members in domain if any present
         */
        populateSupportingMembers(domain,request);

        // Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
        /*
         * There are two additional operations that a member can perform.
         * a.send request to supporting members for approval
         * b.submit a question for answering
         */
        String operation=request.getParameter("operation");
        if(operation!=null){
            if(!operation.isEmpty()){
                /*
                 * For approval cycle:we first check if operation=approval
                 */
                if(operation.equals("approval")){
                    /*
                     * a.First check if these fields have been filled atleast and they are
                     * primary member,subject,question and supporting members                     *
                     */
                    if(domain.getHouseType()==null){
                        result.rejectValue("houseType","HousetypeEmpty");
                    }
                    if(domain.getType()==null){
                        result.rejectValue("type","QuestionTypeEmpty");
                    }
                    if(domain.getSession()==null){
                        result.rejectValue("session","SessionEmpty");
                    }
                    if(domain.getPrimaryMember()==null){
                        result.rejectValue("primaryMember","PrimaryMemberEmpty");
                    }
                    if(domain.getSubject().isEmpty()){
                        result.rejectValue("subject","SubjectEmpty");
                    }
                    if(domain.getQuestionText().isEmpty()){
                        result.rejectValue("questionText","QuestionTextEmpty");
                    }
                    if(domain.getSupportingMembers()==null){
                        result.rejectValue("supportingMembers","SupportingMembersEmpty");
                    }else{
                        if(domain.getSupportingMembers().isEmpty()){
                            result.rejectValue("supportingMembers","SupportingMembersEmpty");
                        }
                    }
                    if(domain.getSupportingMembers()!=null){
                        int count=0;
                        for(SupportingMember i:domain.getSupportingMembers()){
                            if(i.getDecisionStatus().getType().equals("supportingmember_assigned")){
                                count++;
                            }
                        }
                        if(count==0){
                            result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent");
                        }
                    }
                }else
                    /*
                     * For submit cycle:we first check if operation=submit
                     */
                    if(operation.equals("submit")){
                        /*
                         * a.First check if these fields have been filled atleast and they are
                         * session,primary member,subject,question,ministry,group                     *
                         */
                        if(domain.getHouseType()==null){
                            result.rejectValue("houseType","HousetypeEmpty");
                        }
                        if(domain.getType()==null){
                            result.rejectValue("type","QuestionTypeEmpty");
                        }
                        if(domain.getSession()==null){
                            result.rejectValue("session","SessionEmpty");
                        }
                        if(domain.getPrimaryMember()==null){
                            result.rejectValue("primaryMember","PrimaryMemberEmpty");
                        }
                        if(domain.getSubject().isEmpty()){
                            result.rejectValue("subject","SubjectEmpty");
                        }
                        if(domain.getQuestionText().isEmpty()){
                            result.rejectValue("questionText","QuestionTextEmpty");
                        }
                        if(domain.getMinistry()==null){
                            result.rejectValue("ministry","MinistryEmpty");
                        }
                    }
            }
        }else{
            if(domain.getHouseType()==null){
                result.rejectValue("houseType","HousetypeEmpty");
            }
            if(domain.getType()==null){
                result.rejectValue("type","QuestionTypeEmpty");
            }
            if(domain.getSession()==null){
                result.rejectValue("session","SessionEmpty");
            }
            if(domain.getPrimaryMember()==null){
                result.rejectValue("primaryMember","PrimaryMemberEmpty");
            }
            if(domain.getSubject().isEmpty()){
                result.rejectValue("subject","SubjectEmpty");
            }
            if(domain.getQuestionText().isEmpty()){
                result.rejectValue("questionText","QuestionTextEmpty");
            }
        }
    }

    @Override
    protected void populateCreateIfNoErrors(final ModelMap model, final Question domain,
            final HttpServletRequest request) {
        /*
         * setting status of question.if all the mandatory fields have been filled it will be complete
         * else it will be incomplete
         */
        if(domain.getHouseType()!=null && domain.getSession()!=null
                &&  domain.getType()!=null && domain.getPrimaryMember()!=null && domain.getMinistry()!=null &&
                domain.getGroup()!=null && (!domain.getSubject().isEmpty())
                &&(!domain.getQuestionText().isEmpty())){
            Status status=Status.findByFieldName(Status.class, "type", "questions_complete", domain.getLocale());
            domain.setStatus(status);
            domain.setInternalStatus(status);
            /*
             * Here we will further check if this is a submission of question request.If it is then only supporting members
             * should be updated,submission time should be updated and status will be set.
             */
            String operation=request.getParameter("operation");
            if(operation!=null){
                if(!operation.isEmpty()){
                    if(operation.trim().equals("submit")){
                        /*
                         * submission date is set
                         */
                        if(domain.getSubmissionDate()==null){
                            domain.setSubmissionDate(new Date());
                        }
                        /*
                         * only those supporting memebrs will be included who have approved the requests
                         */
                        List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
                        if(domain.getSupportingMembers()!=null){
                            if(!domain.getSupportingMembers().isEmpty()){
                                for(SupportingMember i:domain.getSupportingMembers()){
                                    if(i.getDecisionStatus().getType().trim().equals("supportingmember_approved")){
                                        supportingMembers.add(i);
                                    }
                                }
                                domain.setSupportingMembers(supportingMembers);
                            }
                        }
                        /*
                         *set status
                         */
                        Status newstatus=Status.findByFieldName(Status.class, "type", "questions_submit", domain.getLocale());
                        domain.setStatus(newstatus);
                        domain.setInternalStatus(newstatus);
                    }
                }
            }
        }
        else{
            Status status=Status.findByFieldName(Status.class, "type", "questions_incomplete", domain.getLocale());
            domain.setStatus(status);
            domain.setInternalStatus(status);
        }

        /*
         * add creation date and created by
         */
        domain.setCreationDate(new Date());
        domain.setCreatedBy(this.getCurrentUser().getActualUsername());
        domain.setEditedOn(new Date());
        User user=User.findByUserName(this.getCurrentUser().getActualUsername(), domain.getLocale());
        domain.setEditedBy(user);
        List<UserGroup> userGroupsTemp=this.getCurrentUser().getUserGroups();
        if(userGroupsTemp!=null){
            if(!userGroupsTemp.isEmpty()){
                for(UserGroup i:userGroupsTemp){
                    UserGroup j=UserGroup.findById(UserGroup.class,i.getId());
                    String strType=j.getUserGroupType().getType();
                    if(strType.equals("under_secretary")
                            ||strType.equals("deputy_secretary")
                            ||strType.equals("officer_special_duty")
                            ||strType.equals("joint_secretary")
                            ||strType.equals("secretary")
                            ||strType.equals("principal_secretary")
                            ||strType.equals("deputy_speaker")
                            ||strType.equals("speaker")
                            ||strType.equals("deputy_chairman")
                            ||strType.equals("chairman")
                            ||strType.equals("assistant")){
                        domain.setEditedAs(i.getUserGroupType());
                    }
                }
            }
        }

    }

    @Override
    protected void populateAfterCreate(final ModelMap model, final Question domain,
            final HttpServletRequest request) {
        /*
         * send for approval:here we will start an activity process with process definition as
         */
        String operation=request.getParameter("operation");
        request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
        request.getSession().setAttribute("userrole",request.getParameter("userrole"));
        if(operation!=null){
            if(!operation.isEmpty()){
                if(operation.equals("approval")){
                    ProcessDefinition processDefinition=processService.findProcessDefinitionByKey("Supporting_Members_Approval_Process");
                    Map<String,String> properties=new HashMap<String, String>();
                    properties.put("pv_locale",domain.getLocale());
                    //now for displaying in my task grid
                    properties.put("pv_deviceId",String.valueOf(domain.getId()));
                    properties.put("pv_deviceType",domain.getType().getName());
                    properties.put("pv_primaryMemberFullName",this.getCurrentUser().getTitle()+" "+this.getCurrentUser().getFirstName()+" "+this.getCurrentUser().getMiddleName()+" "+this.getCurrentUser().getLastName());
                    properties.put("pv_subject",domain.getSubject());
                    processService.createProcessInstance(processDefinition, properties);
                    Question question=Question.findById(Question.class,domain.getId());
                    List<SupportingMember> supportingMembers=question.getSupportingMembers();
                    Status status=Status.findByFieldName(Status.class,"type","supportingmember_pending",domain.getLocale());
                    for(SupportingMember i:supportingMembers){
                        //we want to send only those members whose status is not supportingmember_pending
                        if(i.getDecisionStatus().getType().equals("supportingmember_assigned")){
                            i.setDecisionStatus(status);
                            i.merge();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void customValidateUpdate(final Question domain, final BindingResult result,
            final HttpServletRequest request) {
        /*
         * populating supporting members in domain if any present
         */
        populateSupportingMembers(domain,request);

        // Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
        /*
         * There are two additional operations that a member can perform.
         * a.send request to supporting members for approval
         * b.submit a question for answering
         */
        String operation=request.getParameter("operation");
        if(operation!=null){
            if(!operation.isEmpty()){
                /*
                 * For approval cycle:we first check if operation=approval
                 */
                if(operation.equals("approval")){
                    /*
                     * a.First check if these fields have been filled atleast and they are
                     * primary member,subject,question and supporting members                     *
                     */
                    if(domain.getHouseType()==null){
                        result.rejectValue("houseType","HousetypeEmpty");
                    }
                    if(domain.getType()==null){
                        result.rejectValue("type","QuestionTypeEmpty");
                    }
                    if(domain.getSession()==null){
                        result.rejectValue("session","SessionEmpty");
                    }
                    if(domain.getPrimaryMember()==null){
                        result.rejectValue("primaryMember","PrimaryMemberEmpty");
                    }
                    if(domain.getSubject().isEmpty()){
                        result.rejectValue("subject","SubjectEmpty");
                    }
                    if(domain.getQuestionText().isEmpty()){
                        result.rejectValue("questionText","QuestionTextEmpty");
                    }
                    if(domain.getSupportingMembers()==null){
                        result.rejectValue("supportingMembers","SupportingMembersEmpty");
                    }else{
                        if(domain.getSupportingMembers().isEmpty()){
                            result.rejectValue("supportingMembers","SupportingMembersEmpty");
                        }
                    }
                    if(domain.getSupportingMembers()!=null){
                        int count=0;
                        for(SupportingMember i:domain.getSupportingMembers()){
                            if(i.getDecisionStatus().getType().equals("supportingmember_assigned")){
                                count++;
                            }
                        }
                        if(count==0){
                            result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent");
                        }
                    }
                }else
                    /*
                     * For submit cycle:we first check if operation=submit
                     */
                    if(operation.equals("submit")){
                        /*
                         * a.First check if these fields have been filled atleast and they are
                         * session,primary member,subject,question,ministry,group                     *
                         */
                        if(domain.getHouseType()==null){
                            result.rejectValue("houseType","HousetypeEmpty");
                        }
                        if(domain.getType()==null){
                            result.rejectValue("type","QuestionTypeEmpty");
                        }
                        if(domain.getSession()==null){
                            result.rejectValue("session","SessionEmpty");
                        }
                        if(domain.getPrimaryMember()==null){
                            result.rejectValue("primaryMember","PrimaryMemberEmpty");
                        }
                        if(domain.getSubject().isEmpty()){
                            result.rejectValue("subject","SubjectEmpty");
                        }
                        if(domain.getQuestionText().isEmpty()){
                            result.rejectValue("questionText","QuestionTextEmpty");
                        }
                        if(domain.getMinistry()==null){
                            result.rejectValue("ministry","MinistryEmpty");
                        }

                    }
            }
        }else{
            if(domain.getHouseType()==null){
                result.rejectValue("houseType","HousetypeEmpty");
            }
            if(domain.getType()==null){
                result.rejectValue("type","QuestionTypeEmpty");
            }
            if(domain.getSession()==null){
                result.rejectValue("session","SessionEmpty");
            }
            if(domain.getPrimaryMember()==null){
                result.rejectValue("primaryMember","PrimaryMemberEmpty");
            }
            if(domain.getSubject().isEmpty()){
                result.rejectValue("subject","SubjectEmpty");
            }
            if(domain.getQuestionText().isEmpty()){
                result.rejectValue("questionText","QuestionTextEmpty");
            }
        }
    }

    @Override
    protected void populateUpdateIfNoErrors(final ModelMap model, final Question domain,
            final HttpServletRequest request) {
        /*
         * setting status of question.if all the mandatory fields have been filled it will be complete
         * else it will be incomplete
         */
        if(domain.getHouseType()!=null||domain.getType()!=null||domain.getSession()!=null
                ||  domain.getPrimaryMember()!=null && domain.getMinistry()!=null &&
                domain.getGroup()!=null && (!domain.getSubject().isEmpty())
                &&(!domain.getQuestionText().isEmpty())){
            Status status=Status.findByFieldName(Status.class, "type", "questions_complete", domain.getLocale());
            if(!domain.getStatus().getType().equals("questions_submit")){
                domain.setStatus(status);
                domain.setInternalStatus(status);
                domain.setRecommendationStatus(status);
            }
            /*
             * Here we will further check if this is a submission of question request.If it is then only supporting members
             * should be updated,submission time should be updated and status will be set.
             */
            String operation=request.getParameter("operation");
            if(operation!=null){
                if(!operation.isEmpty()){
                    if(operation.trim().equals("submit")){
                        /*
                         * submission date is set
                         */
                        if(domain.getSubmissionDate()==null){
                            domain.setSubmissionDate(new Date());
                        }
                        /*
                         * only those supporting memebrs will be included who have approved the requests
                         */
                        List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
                        if(domain.getSupportingMembers()!=null){
                            if(!domain.getSupportingMembers().isEmpty()){
                                for(SupportingMember i:domain.getSupportingMembers()){
                                    if(i.getDecisionStatus().getType().trim().equals("supportingmember_decisionstatus_approve")){
                                        supportingMembers.add(i);
                                    }
                                }
                                domain.setSupportingMembers(supportingMembers);
                            }
                        }
                        /*
                         *set status
                         */
                        Status newstatus=Status.findByFieldName(Status.class, "type", "questions_submit", domain.getLocale());
                        domain.setStatus(newstatus);
                        domain.setInternalStatus(newstatus);
                        domain.setRecommendationStatus(newstatus);
                    }
                }
            }
        }
        else{
            Status status=Status.findByFieldName(Status.class, "type", "questions_incomplete", domain.getLocale());
            domain.setStatus(status);
            domain.setInternalStatus(status);
        }
        domain.setEditedOn(new Date());
        User user=User.findByUserName(this.getCurrentUser().getActualUsername(), domain.getLocale());
        domain.setEditedBy(user);
        List<UserGroup> userGroupsTemp=this.getCurrentUser().getUserGroups();
        if(userGroupsTemp!=null){
            if(!userGroupsTemp.isEmpty()){
                for(UserGroup i:userGroupsTemp){
                    UserGroup j=UserGroup.findById(UserGroup.class,i.getId());
                    String strType=j.getUserGroupType().getType();
                    if(strType.equals("under_secretary")
                            ||strType.equals("deputy_secretary")
                            ||strType.equals("officer_special_duty")
                            ||strType.equals("joint_secretary")
                            ||strType.equals("secretary")
                            ||strType.equals("principal_secretary")
                            ||strType.equals("deputy_speaker")
                            ||strType.equals("speaker")
                            ||strType.equals("deputy_chairman")
                            ||strType.equals("chairman")
                            ||strType.equals("assistant")){
                        domain.setEditedAs(i.getUserGroupType());
                    }

                    // added by amitd
                    //here if the status of question is questions_submit i.e it has not been approved till then
                    //assistant can do processing and change the content of chart.for this we have taken a flag which if true will give
                    //question a status of assistant_processed.
                    if(strType.equals("assistant")) {
                        Long id = domain.getId();
                        Question question = Question.findById(Question.class, id);
                        String internalStatus = question.getInternalStatus().getType();
                        if(internalStatus.equals("questions_submit")&&domain.getMinistry()!=null&&domain.getGroup()!=null&&domain.getDepartment()!=null) {
                            Status ASSISTANT_PROCESSED = Status.findByType("question_assistantprocessed", domain.getLocale());
                            domain.setInternalStatus(ASSISTANT_PROCESSED);
                            domain.setRecommendationStatus(ASSISTANT_PROCESSED);
                        }
                    }
                }
            }
        }
        /*
         * updating submission date and creation date
         */
        String strCreationDate=request.getParameter("setCreationDate");
        String strSubmissionDate=request.getParameter("setSubmissionDate");
        CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
        if(dateTimeFormat!=null){
            SimpleDateFormat format=FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),domain.getLocale());
            try {
                if(strSubmissionDate!=null){
                    domain.setSubmissionDate(format.parse(strSubmissionDate));
                }
                if(strCreationDate!=null){
                    domain.setCreationDate(format.parse(strCreationDate));
                }
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }



    @Override
    protected void populateAfterUpdate(final ModelMap model, final Question domain,
            final HttpServletRequest request) {

        /*
         * send for approval:here we will start an activity process with process definition as
         */
        String operation=request.getParameter("operation");
        if(operation!=null){
            if(!operation.isEmpty()){
                if(operation.equals("approval")){
                    ProcessDefinition processDefinition=processService.findProcessDefinitionByKey("Supporting_Members_Approval_Process");
                    Map<String,String> properties=new HashMap<String, String>();
                    properties.put("pv_locale",domain.getLocale());
                    //now for displaying in my task grid
                    properties.put("pv_deviceId",String.valueOf(domain.getId()));
                    properties.put("pv_deviceType",domain.getType().getName());
                    properties.put("pv_primaryMemberFullName",this.getCurrentUser().getTitle()+" "+this.getCurrentUser().getFirstName()+" "+this.getCurrentUser().getMiddleName()+" "+this.getCurrentUser().getLastName());
                    properties.put("pv_subject",domain.getSubject());
                    processService.createProcessInstance(processDefinition, properties);
                    Question question=Question.findById(Question.class,domain.getId());
                    List<SupportingMember> supportingMembers=question.getSupportingMembers();
                    Status status=Status.findByFieldName(Status.class,"type","supportingmember_pending",domain.getLocale());
                    for(SupportingMember i:supportingMembers){
                        if(i.getDecisionStatus().getType().equals("supportingmember_assigned")){
                            i.setDecisionStatus(status);
                            i.merge();
                        }
                    }
                }else if(operation.equals("startworkflow")){
                    ProcessDefinition processDefinition=processService.findProcessDefinitionByKey("STARRED_QUESTION_PROCESS");
                    Map<String,String> properties=new HashMap<String, String>();
                    properties.put("pv_locale",domain.getLocale());
                    //now for displaying in my task grid
                    properties.put("pv_deviceId",String.valueOf(domain.getId()));
                    properties.put("pv_deviceType",domain.getType().getName());
                    properties.put("pv_primaryMemberFullName",domain.getPrimaryMember().getFullname());
                    properties.put("pv_subject",domain.getSubject());
                    //variables needed for finding next actors
                    properties.put("pv_sessionId",request.getParameter("sessionId"));
                    properties.put("pv_deviceTypeId",request.getParameter("deviceTypeId"));
                    properties.put("pv_workflowType",request.getParameter("workflowType"));
                    properties.put("pv_groupNumber",request.getParameter("groupNumber"));
                    String workflowConfigId=request.getParameter("workflowConfigId");
                    properties.put("pv_workflowConfigId",workflowConfigId );
                    //another way to end workflow set pv_endflag='end'
                    properties.put("pv_endflag", "continue");
                    //for traversing the workflow we need two variables pv_nextactor and pv_nextuser
                    String actor=request.getParameter("actor");
                    Integer level=WorkflowConfig.getLevel(Long.parseLong(workflowConfigId), actor);
                    properties.put("pv_level",String.valueOf(level));
                    if(actor!=null){
                        properties.put("pv_nextactor",actor);
                        String nextuser=findNextUser(domain, actor, domain.getLocale());
                        properties.put("pv_nextuser",nextuser);
                    }
                    processService.createProcessInstance(processDefinition, properties);
                }
            }
        }

        Status internalStatus = domain.getInternalStatus();
        // If the internalStatus of the Question is ASSISTANT_PROCESSED then
        // add to Chart if applicable
        if(internalStatus.getType().equals("question_assistantprocessed")) {
            Question question = Question.findById(Question.class, domain.getId());
            Chart.addToChart(question);
        }
        // If the internalStatus of the Question is GROUP_CHANGED then do the
        // following
        else if(internalStatus.getType().
                equals("question_workflow_decisionstatus_groupchanged")) {
            Question question = Question.findById(Question.class, domain.getId());
            QuestionDraft draft = question.findPreviousDraft();
            Group affectedGroup = draft.getGroup();
            Chart.groupChange(question, affectedGroup);
        }
    }

    private String findNextUser(final Question domain,final String actor,final String locale){
        UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class, "type",actor, domain.getLocale());
        List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType", userGroupType,"activeFrom",ApplicationConstants.DESC, domain.getLocale());
        Credential credential=null;
        int noOfComparisons=0;
        int noOfSuccess=0;
        if(userGroups!=null){
            if(!userGroups.isEmpty()){
                for(UserGroup i:userGroups){
                    if(i.getActiveFrom().before(new Date())||i.getActiveFrom().equals(new Date())){
                        String userType=i.getUserGroupType().getType();
                        if(userType.equals("member")){
                            return i.getCredential().getUsername();
                        }
                        Map<String,String> map=i.getParameters();
                        if(map.get("DEPARTMENT_"+locale)!=null&&domain.getDepartment()!=null){
                            noOfComparisons++;
                            if(map.get("DEPARTMENT_"+locale).contains(domain.getDepartment().getName())){
                                noOfSuccess++;
                            }
                        }
                        if(map.get("DEVICETYPE_"+locale)!=null&&domain.getType()!=null){
                            noOfComparisons++;
                            if(map.get("DEVICETYPE_"+locale).contains(domain.getType().getName())){
                                noOfSuccess++;
                            }
                        }
                        if(map.get("GROUP_"+locale)!=null&&domain.getGroup()!=null){
                            noOfComparisons++;
                            if(map.get("GROUP_"+locale).contains(String.valueOf(domain.getGroup().getNumber()))){
                                noOfSuccess++;
                            }
                        }
                        if(map.get("HOUSETYPE_"+locale)!=null&&domain.getHouseType()!=null){
                            noOfComparisons++;
                            if(map.get("HOUSETYPE_"+locale).equals("Both House")&&userType.equals("principal_secretary")){
                                noOfSuccess++;
                            }else if(map.get("HOUSETYPE_"+locale).equals(domain.getHouseType().getName())){
                                noOfSuccess++;
                            }
                        }
                        if(map.get("SESSIONTYPE_"+locale)!=null&&domain.getSession()!=null){
                            noOfComparisons++;
                            if(map.get("SESSIONTYPE_"+locale).equals(domain.getSession().getType().getSessionType())){
                                noOfSuccess++;
                            }
                        }
                        if(map.get("YEAR_"+locale)!=null&&domain.getSession()!=null){
                            noOfComparisons++;
                            if(map.get("YEAR_"+locale).equals(String.valueOf(domain.getSession().getYear()))){
                                noOfSuccess++;
                            }
                        }
                        if(map.get("SUBDEPARTMENT_"+locale)!=null&&domain.getSubDepartment()!=null){
                            noOfComparisons++;
                            if(map.get("SUBDEPARTMENT_"+locale).contains(domain.getSubDepartment().getName())){
                                noOfSuccess++;
                            }
                        }
                        if(noOfComparisons!=0&&noOfSuccess!=0&&noOfComparisons==noOfSuccess){
                            credential=i.getCredential();
                            return credential.getUsername();
                        }
                    }
                }
            }
        }
        return "";
    }


    private void populateSupportingMembers(final Question domain,final HttpServletRequest request){
        /*
         * here we are obtaining the supporting members id from the jsp
         * This method will be called from create,send for approval and submit.status that need to be set is
         */
        String[] selectedSupportingMembers=request.getParameterValues("selectedSupportingMembers");
        List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
        if(selectedSupportingMembers!=null){
            if(selectedSupportingMembers.length>0){
                List<SupportingMember> members=new ArrayList<SupportingMember>();
                if(domain.getId()!=null){
                    Question question=Question.findById(Question.class,domain.getId());
                    members=question.getSupportingMembers();
                }
                for(String i:selectedSupportingMembers){
                    SupportingMember supportingMember=null;
                    Member member=Member.findById(Member.class, Long.parseLong(i));
                    /*
                     * first we are iterating over the already present supporting members of domain to find out
                     * if the supporting members already exists.if yes then we add this supporting member to the list without modifing it.
                     */
                    for(SupportingMember j:members){
                        if(j.getMember().getId()==member.getId()){
                            supportingMember=j;
                            break;
                        }
                    }

                    /*
                     * if the supporting member is a new supporting member.In that case we will set its member,locale,and status.
                     * Status will be set to assigned when question is first created,will be pending when it is send for approval,
                     * will be approved,rejected when set through my task of supporting members
                     */
                    if(supportingMember==null){
                        supportingMember=new SupportingMember();
                        supportingMember.setMember(member);
                        supportingMember.setLocale(domain.getLocale());
                        /*
                         * Initially when create is clicked status will be assigned.Also when send for approval is clicked and
                         * the supporting member doesn't exists then its status will be assigned.Now tasks will be created for all
                         * the supporting members whose status is assigned.And once task has been created the status will change to pending.
                         */
                        supportingMember.setDecisionStatus((Status) Status.findByFieldName(Status.class, "type","supportingmember_assigned", domain.getLocale()));
                    }
                    supportingMembers.add(supportingMember);
                }
                domain.setSupportingMembers(supportingMembers);
            }
        }
    }




    //    @Override
    //    protected void preValidateCreate(final Question domain, final BindingResult result,
    //            final HttpServletRequest request) {
    //        if(domain.getSession()!=null){
    //            Date rotationOrderPubDate=domain.getSession().getRotationOrderPublishingDate();
    //            Date currentDate=new Date();
    //            if(currentDate.equals(rotationOrderPubDate)||currentDate.after(rotationOrderPubDate)){
    //                //if rotation order is  published then for creating questions some basic information needs
    //                //to be filled.Status here can be .And an error will be generated
    //                //indicating that mandatory fields are not entered.
    //                if(domain.getHouseType()==null || domain.getSession()==null
    //                        ||  domain.getType()==null || domain.getPrimaryMember()==null ||
    //                        domain.getSubject().isEmpty()
    //                        ||domain.getQuestionText().isEmpty() || domain.getLanguage()==null){
    //                    Status status=Status.findByFieldName(Status.class, "type", "question_init_incomplete", domain.getLocale());
    //                    domain.setStatus(status);
    //                    result.rejectValue("version","mandatory");
    //                }else{
    //                    Status status=Status.findByFieldName(Status.class, "type", "question_init_incomplete", domain.getLocale());
    //                    domain.setStatus(status);
    //                }
    //
    //            }else{
    //                //if rotation order is not published then for creating questions some basic information needs
    //                //to be filled.Status here will be incomplete.And an error will be generated
    //                //indicating that mandatory fields are not entered.
    //                if(domain.getHouseType()==null || domain.getSession()==null
    //                        ||  domain.getType()==null || domain.getPrimaryMember()==null ||
    //                        domain.getSubject().isEmpty()
    //                        ||domain.getQuestionText().isEmpty() || domain.getLanguage()==null){
    //                    Status status=Status.findByFieldName(Status.class, "type", "question_init_incomplete", domain.getLocale());
    //                    domain.setStatus(status);
    //                    result.rejectValue("version","mandatory");
    //                }
    //            }
    //        }
    //    }


    /*
     * This method is used to view the approval status of a question from the supporting members
     */
    @RequestMapping(value="/status/{question}",method=RequestMethod.GET)
    public String getSupportingMemberStatus(final HttpServletRequest request,final ModelMap model,@PathVariable("question") final String question){
        Question questionTemp=Question.findById(Question.class,Long.parseLong(question));
        List<SupportingMember> supportingMembers=questionTemp.getSupportingMembers();
        model.addAttribute("supportingMembers",supportingMembers);
        return "question/supportingmember";
    }

    @RequestMapping(value="/citations/{deviceType}",method=RequestMethod.GET)
    public String getCitations(final HttpServletRequest request, final Locale locale,@PathVariable("deviceType")  final Long type,
            final ModelMap model){
        DeviceType deviceType=DeviceType.findById(DeviceType.class,type);
        List<Citation> deviceTypeBasedcitations=Citation.findAllByFieldName(Citation.class,"deviceType",deviceType, "text",ApplicationConstants.ASC, locale.toString());
        Status status=null;
        if(request.getParameter("status")!=null){
              status=Status.findById(Status.class, Long.parseLong(request.getParameter("status"))); 
        }
        List<Citation> citations=new ArrayList<Citation>();
        if(status!=null){
             for(Citation i:deviceTypeBasedcitations){
                if(i.getStatus()!=null){
                    if(i.getStatus().equals(status.getType())){
                        citations.add(i);
                    }
                }
            }
         }
        
        model.addAttribute("citations",citations);
        return "question/citation";
    }

    @RequestMapping(value="/revisions/{questionId}",method=RequestMethod.GET)
    public String getDrafts(final Locale locale,@PathVariable("questionId")  final Long questionId,
            final ModelMap model){
        List<QuestionRevisionVO> drafts=Question.getRevisions(questionId,locale.toString());
        model.addAttribute("drafts",drafts);
        return "question/revisions";
    }

    @RequestMapping(value="/members/contacts",method=RequestMethod.GET)
    public String getMemberContacts(final Locale locale,
            final ModelMap model,final HttpServletRequest request){
        String strMembers=request.getParameter("members");
        String[] members=strMembers.split(",");
        List<MemberContactVO> memberContactVOs=Member.getContactDetails(members);
        model.addAttribute("membersContact",memberContactVOs);
        return "question/contacts";
    }

    /**
     * Return "CREATED" if Chart is created
     * OR
     * Return "ALREADY_EXISTS" if Chart already exists
     * OR
     * Return "PREVIOUS_CHART_IS_NOT_PROCESSED" if previous Chart is not processed
     */
    @Transactional
    @RequestMapping(value="chart/create", method=RequestMethod.GET)
    public @ResponseBody String createChart(final HttpServletRequest request,
            final Locale locale) {
        String retVal = "ALREADY_EXISTS";

        String strLocale = locale.toString();
        String strHouseType = request.getParameter("houseType");
        String strYear = request.getParameter("sessionYear");
        String strSessionTypeId = request.getParameter("sessionType");
        String strTempDate=request.getParameter("answeringDate");
        QuestionDates questionDates=QuestionDates.findById(QuestionDates.class,Long.parseLong(strTempDate));

        HouseType houseType =
            HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
        SessionType sessionType =
            SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
        Integer year = Integer.valueOf(strYear);

        Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

        Group group = null;
        List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
        if(userGroups != null){
            if(! userGroups.isEmpty()){
                for(UserGroup i : userGroups) {
                    UserGroup j = UserGroup.findById(UserGroup.class,i.getId());
                    String strType = j.getUserGroupType().getType();
                    if(strType.equals("assistant")) {
                        String groupNumber = j.getParameterValue("GROUP_" + strLocale);
                        if(sessionType != null){
                            group = Group.findByNumberHouseTypeSessionTypeYear(
                                    Integer.parseInt(groupNumber), houseType, sessionType, year);
                        }
                    }
                }
            }
        }

        Date answeringDate = questionDates.getAnsweringDate();
        if(answeringDate != null) {
            Chart foundChart = Chart.find(session, group, answeringDate, strLocale);
            if(foundChart == null) {
                Chart chart = new Chart(session, group, answeringDate, strLocale);
                Chart createdChart = chart.create();
                if(createdChart == null) {
                    retVal = "PREVIOUS_CHART_IS_NOT_PROCESSED";
                }
                else {
                    retVal = "CREATED";
                }
            }
        }

        return retVal;
    }

    @RequestMapping(value="chart/view", method=RequestMethod.GET)
    public String viewChart(final ModelMap model,
            final HttpServletRequest request,
            final Locale locale) {
        String strLocale = locale.toString();
        String strHouseType = request.getParameter("houseType");
        String strYear = request.getParameter("sessionYear");
        String strSessionTypeId = request.getParameter("sessionType");
        String strTempDate=request.getParameter("answeringDate");
        QuestionDates questionDates=QuestionDates.findById(QuestionDates.class,Long.parseLong(strTempDate));
        //String strAnsweringDate = request.getParameter("answeringDate");

        HouseType houseType =
            HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
        SessionType sessionType =
            SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
        Integer year = Integer.valueOf(strYear);

        Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

        Group group = null;
        List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
        if(userGroups != null){
            if(! userGroups.isEmpty()){
                for(UserGroup i : userGroups) {
                    UserGroup j = UserGroup.findById(UserGroup.class,i.getId());
                    String strType = j.getUserGroupType().getType();
                    if(strType.equals("assistant")) {
                        String groupNumber = j.getParameterValue("GROUP_" + strLocale);
                        if(sessionType != null){
                            group = Group.findByNumberHouseTypeSessionTypeYear(
                                    Integer.parseInt(groupNumber), houseType, sessionType, year);
                        }
                    }
                }
            }
        }

        Date answeringDate = questionDates.getAnsweringDate();
        //			if(strAnsweringDate != null) {
        //				CustomParameter parameter =
        //					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
        //				answeringDate = new DateFormater().formatStringToDate(strAnsweringDate,
        //						parameter.getValue());
        //			}

        if(answeringDate != null) {
            List<ChartVO> chartVOs = Chart.getChartVOs(session, group, answeringDate, strLocale);
            model.addAttribute("chartVOs", chartVOs);

            CustomParameter parameter =
                CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
            String strAnsweringDate =
                FormaterUtil.formatDateToString(answeringDate, parameter.getValue());
            model.addAttribute("answeringDate", strAnsweringDate);
        }
        else {
            model.addAttribute("errorcode", "answeringDateNotSelected");
        }

        return "question/chart";
    }

    @RequestMapping(value="/clubbing",method=RequestMethod.GET)
    public String getClubbing(final HttpServletRequest request,final ModelMap model){
        String strquestionId=request.getParameter("id");
        if(strquestionId!=null){
            if(!strquestionId.isEmpty()){
                Question question=Question.findById(Question.class,Long.parseLong(strquestionId));
                if(question.getParent()!=null){
                    model.addAttribute("parent",question.getParent().getNumber());
                    return "question/noclubbing";
                }
                if(question.getRevisedSubject()!=null){
                    if(!question.getRevisedSubject().isEmpty()){
                        model.addAttribute("subject",question.getRevisedSubject());
                    }else{
                        model.addAttribute("subject",question.getSubject());
                    }
                }else{
                    model.addAttribute("subject",question.getSubject());
                }
                List<Question> clubbings=question.getClubbings();
                List<Reference> references=new ArrayList<Reference>();
                StringBuffer buffer=new StringBuffer();
                if(!clubbings.isEmpty()){
                    for(Question i:clubbings){
                        Reference reference=new Reference();
                        reference.setId(String.valueOf(i.getId()));
                        reference.setName(String.valueOf(i.getNumber()));
                        buffer.append(i.getId()+",");
                        references.add(reference);
                    }
                    model.addAttribute("clubbedQuestions",references);
                    model.addAttribute("clubbedQuestionsIds",buffer.toString());
                }
                model.addAttribute("id",Long.parseLong(strquestionId));
                model.addAttribute("number",question.getNumber());
            }
        }
        return "question/clubbing";
    }

    @Transactional
    @RequestMapping(value="/clubbing",method=RequestMethod.POST)
    public  String clubbing(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String strpId=request.getParameter("pId");
        String strcId=request.getParameter("cId");
        Boolean status=false;
        if(strpId!=null&&strcId!=null){
            if(!strpId.isEmpty()&&!strcId.isEmpty()){
                Long primaryId=Long.parseLong(strpId);
                Long clubbingId=Long.parseLong(strcId);
                try{
                    status=Question.club(primaryId, clubbingId, locale.toString());
                }catch(Exception ex){
                    ex.printStackTrace();
                    status=false;
                }
            }
        }
        model.addAttribute("status",status);
        return "question/clubbingresult";
    }

    @Transactional
    @RequestMapping(value="/unclubbing",method=RequestMethod.POST)
    public  String unclubbing(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String strpId=request.getParameter("pId");
        String strcId=request.getParameter("cId");
        Boolean status=false;
        if(strpId!=null&&strcId!=null){
            if(!strpId.isEmpty()&&!strcId.isEmpty()){
                Long primaryId=Long.parseLong(strpId);
                Long clubbingId=Long.parseLong(strcId);
                try{
                    status=Question.club(primaryId, clubbingId, locale.toString());
                }catch(Exception ex){
                    ex.printStackTrace();
                    status=false;
                }
            }
        }
        model.addAttribute("unclubbingstatus",status);
        return "question/clubbingresult";
    }


    @RequestMapping(value="/referencing",method=RequestMethod.GET)
    public String getReferencing(final HttpServletRequest request,final ModelMap model){
        String strquestionId=request.getParameter("id");
        if(strquestionId!=null){
            if(!strquestionId.isEmpty()){
                model.addAttribute("question",Long.parseLong(strquestionId));
            }
        }
        return "question/referencing";
    }

    @RequestMapping(value="/subject/{id}",method=RequestMethod.GET)
    public @ResponseBody MasterVO getSubject(final HttpServletRequest request,final ModelMap model,
            final @PathVariable("id")Long id){
        Question question=Question.findById(Question.class, id);
        MasterVO masterVO=new MasterVO();
        masterVO.setId(question.getId());
        if(question.getRevisedSubject()!=null){
            masterVO.setName(question.getRevisedSubject());
        }else{
            masterVO.setName(question.getSubject());
        }
        return masterVO;
    }

    /**
     * Return "CREATED" if Ballot is created
     * OR
     * Return "ALREADY_EXISTS" if Ballot already exists
     */
    @Transactional
    @RequestMapping(value="ballot/create", method=RequestMethod.GET)
    public @ResponseBody String createBallot(final HttpServletRequest request,
            final Locale locale) {
        String retVal = "ALREADY_EXISTS";

        String strLocale = locale.toString();
        String strHouseType = request.getParameter("houseType");
        String strYear = request.getParameter("sessionYear");
        String strSessionTypeId = request.getParameter("sessionType");
        String strTempDate=request.getParameter("answeringDate");
        QuestionDates questionDates=QuestionDates.findById(QuestionDates.class,Long.parseLong(strTempDate));
        //String strAnsweringDate = request.getParameter("answeringDate");

        HouseType houseType =
            HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
        SessionType sessionType =
            SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
        Integer year = Integer.valueOf(strYear);

        Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

        Group group = null;
        List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
        if(userGroups != null){
            if(! userGroups.isEmpty()){
                for(UserGroup i : userGroups) {
                    UserGroup j = UserGroup.findById(UserGroup.class,i.getId());
                    String strType = j.getUserGroupType().getType();
                    if(strType.equals("assistant")) {
                        String groupNumber = j.getParameterValue("GROUP_" + strLocale);
                        if(sessionType != null){
                            group = Group.findByNumberHouseTypeSessionTypeYear(
                                    Integer.parseInt(groupNumber), houseType, sessionType, year);
                        }
                    }
                }
            }
        }

        Date answeringDate = questionDates.getAnsweringDate();
        //			if(strAnsweringDate != null) {
        //				CustomParameter parameter =
        //					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
        //				answeringDate = new DateFormater().formatStringToDate(strAnsweringDate,
        //						parameter.getValue());
        //			}

        if(answeringDate != null) {
            Ballot ballot = Ballot.find(session, group, answeringDate, strLocale);
            if(ballot == null) {
                Ballot newBallot = new Ballot(session, group, answeringDate, new Date(), strLocale);
                newBallot.create();
                retVal = "CREATED";
            }
        }

        return retVal;
    }

    @RequestMapping(value="ballot/view", method=RequestMethod.GET)
    public String viewBallot(final ModelMap model,
            final HttpServletRequest request,
            final Locale locale) {
        String strLocale = locale.toString();
        String strHouseType = request.getParameter("houseType");
        String strYear = request.getParameter("sessionYear");
        String strSessionTypeId = request.getParameter("sessionType");
        String strTempDate=request.getParameter("answeringDate");
        QuestionDates questionDates=QuestionDates.findById(QuestionDates.class,Long.parseLong(strTempDate));
        //String strAnsweringDate = request.getParameter("answeringDate");

        HouseType houseType =
            HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
        SessionType sessionType =
            SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
        Integer year = Integer.valueOf(strYear);

        Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

        Group group = null;
        List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
        if(userGroups != null){
            if(! userGroups.isEmpty()){
                for(UserGroup i : userGroups) {
                    UserGroup j = UserGroup.findById(UserGroup.class,i.getId());
                    String strType = j.getUserGroupType().getType();
                    if(strType.equals("assistant")) {
                        String groupNumber = j.getParameterValue("GROUP_" + strLocale);
                        if(sessionType != null){
                            group = Group.findByNumberHouseTypeSessionTypeYear(
                                    Integer.parseInt(groupNumber), houseType, sessionType, year);
                        }
                    }
                }
            }
        }

        Date answeringDate = questionDates.getAnsweringDate();
        //			if(strAnsweringDate != null) {
        //				CustomParameter parameter =
        //					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
        //				answeringDate = new DateFormater().formatStringToDate(strAnsweringDate,
        //						parameter.getValue());
        //			}

        if(answeringDate != null) {
            List<BallotVO> ballotVOs = Ballot.getBallotVOs(session, group, answeringDate, strLocale);
            model.addAttribute("ballotVOs", ballotVOs);

            CustomParameter parameter =
                CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
            String strAnsweringDate =
                FormaterUtil.formatDateToString(answeringDate, parameter.getValue());
            model.addAttribute("answeringDate", strAnsweringDate);
        }
        else {
            model.addAttribute("errorcode", "answeringDateNotSelected");
        }

        return "question/ballot";
    }

    @Transactional
    @Override
    protected Boolean preDelete(final ModelMap model, final BaseDomain domain,
            final HttpServletRequest request,final Long id) {
        Question question=Question.findById(Question.class, id);
        if(question!=null){
            Status status=question.getStatus();
            if(status.getType().equals("questions_incomplete")||status.getType().equals("questions_complete")){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @RequestMapping(value="/attendance",method=RequestMethod.GET)
    public String markAttendance(final HttpServletRequest request,final ModelMap model,final Locale locale){
        /*
         * here we want to populate all those members in council who have submitted
         * atleast 1 question or a maximum of 31 questions in first round of
         * question submission.
         *
         */
        String strHouseType=request.getParameter("houseType");
        String strSessionType=request.getParameter("sessionType");
        String strSessionYear=request.getParameter("sessionYear");
        String strQuestionType=request.getParameter("questionType");
        String strOperation=request.getParameter("operation");
        if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null){
            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
            Integer sessionYear=Integer.parseInt(strSessionYear);
            Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
            DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
            if(session!=null){
                Boolean created=Question.createMemberBallotAttendance(session,questionType,locale.toString());
                if(created){
                    logger.info("Member Ballot Attendance Entries Created");
                }else{
                    logger.info("Member Ballot Attendance Entries Already Created");
                }
                List<MemberBallotAttendance> allItems=null;
                List<MemberBallotAttendance> selectedItems=null;
                if(strOperation.equals("presentees")){
                    allItems=MemberBallotAttendance.findAll(session,questionType,"false","member",locale.toString());
                    selectedItems=MemberBallotAttendance.findAll(session,questionType,"true","position",locale.toString());
                }else{
                    allItems=MemberBallotAttendance.findAll(session,questionType,"true","member",locale.toString());
                    selectedItems=MemberBallotAttendance.findAll(session,questionType,"false","position",locale.toString());
                }
                List<MemberBallotAttendance> eligibles=MemberBallotAttendance.findAll(session,questionType,"","member",locale.toString());
                model.addAttribute("allItems",allItems);
                model.addAttribute("selectedItems",selectedItems);
                model.addAttribute("eligibles",eligibles);
                model.addAttribute("session",session.getId());
                model.addAttribute("questionType",strQuestionType);
                model.addAttribute("houseType",strHouseType);
                model.addAttribute("sessionType",strSessionType);
                model.addAttribute("sessionYear",strSessionYear);
            }else{
                logger.error("**** Session not defined for selected houseType,sessionType and sessionYear ****");
            }
        }else{
            logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType' for null values ****");
        }
        return "question/attendance";
    }

    @RequestMapping(value="/attendance",method=RequestMethod.PUT)
    public @ResponseBody String updateAttendance(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String selectedItems=request.getParameter("items");
        String[] items=selectedItems.split(",");
        String strAttendance=request.getParameter("attendance");
        String strQuestionType=request.getParameter("questionType");
        String strSession=request.getParameter("session");
        Session session=Session.findById(Session.class,Long.parseLong(strSession));
        DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
        Boolean attendance=Boolean.parseBoolean(strAttendance);
        int position=0;
        for(String i:items){
            MemberBallotAttendance memberBallotAttendance=MemberBallotAttendance.findById(MemberBallotAttendance.class,Long.parseLong(i));
            position++;
            memberBallotAttendance.setPosition(position);
            memberBallotAttendance.setAttendance(attendance);
            memberBallotAttendance.merge();
        }
        List<MemberBallotAttendance> memberBallotAttendances=MemberBallotAttendance.findAll(session, questionType, "", "position", locale.toString());
        for(MemberBallotAttendance i:memberBallotAttendances){
            if(!selectedItems.contains(String.valueOf(i.getId()))){
                i.setAttendance(!attendance);
                i.merge();
            }
        }
        return "success";
    }

    @RequestMapping(value="/preballot",method=RequestMethod.GET)
    public String preBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String strHouseType=request.getParameter("houseType");
        String strSessionType=request.getParameter("sessionType");
        String strSessionYear=request.getParameter("sessionYear");
        String strQuestionType=request.getParameter("questionType");
        String strAttendance=request.getParameter("attendance");
        model.addAttribute("attendance",strAttendance);
        if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null){
            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
            Integer sessionYear=Integer.parseInt(strSessionYear);
            Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
            DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
            if(session!=null){
                List<MemberBallotAttendance> memberBallotAttendances=null;
                if(strAttendance.equals("present")){
                    memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"true","position",locale.toString());
                }else{
                    memberBallotAttendances=MemberBallotAttendance.findAll(session,questionType,"false","position",locale.toString());
                }
                model.addAttribute("selectedItems",memberBallotAttendances);
            }else{
                logger.error("**** Session not defined for selected houseType,sessionType and sessionYear ****");
            }
        }else{
            logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType' for null values ****");
        }
        return "question/preballot";
    }

    @RequestMapping(value="/memberballot",method=RequestMethod.POST)
    public @ResponseBody String createMemberBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String strHouseType=request.getParameter("houseType");
        String strSessionType=request.getParameter("sessionType");
        String strSessionYear=request.getParameter("sessionYear");
        String strQuestionType=request.getParameter("questionType");
        String strAttendance=request.getParameter("attendance");
        String strRound=request.getParameter("round");
        if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null&&strAttendance!=null&&strRound!=null){
            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
            Integer sessionYear=Integer.parseInt(strSessionYear);
            Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
            DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
            if(session!=null){
                Boolean status=MemberBallot.createMemberBallot(session,questionType,Boolean.parseBoolean(strAttendance),Integer.parseInt(strRound),locale.toString());
                if(status){
                    return "success";
                }else{
                    return "alreadycreated";
                }
            }else{
                logger.error("**** Session not defined for selected houseType,sessionType and sessionYear ****");
                return "failed";
            }
        }else{
            logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType,attendance and round' for null values ****");
            return "failed";
        }
    }

    @RequestMapping(value="/memberballot",method=RequestMethod.GET)
    public String viewMemberBallot(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String strHouseType=request.getParameter("houseType");
        String strSessionType=request.getParameter("sessionType");
        String strSessionYear=request.getParameter("sessionYear");
        String strQuestionType=request.getParameter("questionType");
        model.addAttribute("houseType",strHouseType);
        model.addAttribute("sessionType",strSessionType);
        model.addAttribute("sessionYear",strSessionYear);
        model.addAttribute("questionType",strQuestionType);
        if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null){
            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
            Integer sessionYear=Integer.parseInt(strSessionYear);
            Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
            if(session!=null){
                List<Group> groups=Group.findByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
                model.addAttribute("groups",groups);
                CustomParameter totalRoundsCouncilBallot=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_TOTALROUNDS", "");
                if(totalRoundsCouncilBallot!=null){
                    int rounds=Integer.parseInt(totalRoundsCouncilBallot.getValue());
                    List<Reference> references=new ArrayList<Reference>();
                    NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
                    for(int i=1;i<=rounds;i++){
                        references.add(new Reference(String.valueOf(i),String.valueOf(format.format(i))));
                    }
                    model.addAttribute("rounds",references);
                }else{
                    logger.error("**** Custom Parameter 'STARRED_MEMBERBALLOTCOUNCIL_TOTALROUNDS' not set");
                }
            }else{
                logger.error("**** Session not defined for selected houseType,sessionType and sessionYear ****");
            }
        }else{
            logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType,attendance and round' for null values ****");
        }
        return "question/memberballot";
    }


    @RequestMapping(value="/memberballotresult",method=RequestMethod.GET)
    public String viewMemberBallotResult(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String strHouseType=request.getParameter("houseType");
        String strSessionType=request.getParameter("sessionType");
        String strSessionYear=request.getParameter("sessionYear");
        String strQuestionType=request.getParameter("questionType");
        String strAttendance=request.getParameter("attendance");
        String strRound=request.getParameter("round");
        if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null&&strAttendance!=null&&strRound!=null){
            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
            Integer sessionYear=Integer.parseInt(strSessionYear);
            Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
            DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
            if(session!=null){
                String strGroup=request.getParameter("group");
                String strAnsweringDate=request.getParameter("answeringDate");
                List<MemberBallot> memberBallots=null;
                if(strGroup==null&&strAnsweringDate==null){
                    memberBallots=MemberBallot.viewMemberBallot(session,questionType,Boolean.parseBoolean(strAttendance),Integer.parseInt(strRound),locale.toString());
                }else if(strGroup.equals("-")&&strAnsweringDate.equals("-")){
                    memberBallots=MemberBallot.viewMemberBallot(session,questionType,Boolean.parseBoolean(strAttendance),Integer.parseInt(strRound),locale.toString());
                }else if((!strGroup.equals("-"))&&strAnsweringDate.equals("-")){
                    Group group=Group.findById(Group.class,Long.parseLong(strGroup));
                    memberBallots=MemberBallot.viewMemberBallot(session,questionType,Boolean.parseBoolean(strAttendance),Integer.parseInt(strRound),group,locale.toString());
                }else if((!strGroup.equals("-"))&&(!strAnsweringDate.equals("-"))){
                    QuestionDates answeringDate=Question.findById(QuestionDates.class,Long.parseLong(strAnsweringDate));
                    memberBallots=MemberBallot.viewMemberBallot(session,questionType,Boolean.parseBoolean(strAttendance),Integer.parseInt(strRound),answeringDate,locale.toString());
                }
                model.addAttribute("memberBallots",memberBallots);
            }else{
                logger.error("**** Session not defined for selected houseType,sessionType and sessionYear ****");
            }
        }else{
            logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType,attendance and round' for null values ****");
        }
        return "question/memberballotresult";
    }

    @RequestMapping(value="/memberballotchoice",method=RequestMethod.GET)
    public String viewMemberBallotChoice(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String strHouseType=request.getParameter("houseType");
        String strSessionType=request.getParameter("sessionType");
        String strSessionYear=request.getParameter("sessionYear");
        String strQuestionType=request.getParameter("questionType");
        if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null&&strQuestionType!=null){
            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
            Integer sessionYear=Integer.parseInt(strSessionYear);
            Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
            DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
            if(session!=null){
                model.addAttribute("session",session.getId());
                model.addAttribute("questionType",questionType.getId());
                List<Member> eligibleMembers=MemberBallotAttendance.findEligibleMembers(session, questionType, locale.toString());
                model.addAttribute("eligibleMembers", eligibleMembers);
            }else{
                logger.error("**** Session not defined for selected houseType,sessionType and sessionYear ****");
            }
        }else{
            logger.error("**** Check request parameter 'houseType,sessionType,sessionYear,questionType' for null values ****");
        }
        return "question/memberballotchoice";
    }

    @RequestMapping(value="/listmemberballotchoice",method=RequestMethod.GET)
    public String listMemberBallotChoice(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String strQuestionType=request.getParameter("deviceType");
        String strSession=request.getParameter("session");
        String strMember=request.getParameter("member");
        if(strQuestionType!=null&&strSession!=null&&strMember!=null){
            Session session=Session.findById(Session.class,Long.parseLong(strSession));
            DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
            Member member=Member.findById(Member.class,Long.parseLong(strMember));
            List<Question> questions=Question.findAdmittedStarredQuestionsUH(session,questionType,member,locale.toString());
            model.addAttribute("admittedQuestions",questions);
            model.addAttribute("noOfAdmittedQuestions",questions.size());
            List<MemberBallot> memberBallots=MemberBallot.findByMember(session, questionType, member, locale.toString());
            model.addAttribute("memberBallots",memberBallots);
            int rounds=memberBallots.size();
            model.addAttribute("totalRounds", rounds);
            List<MemberBallotChoice> memberBallotChoices=MemberBallotChoice.findByMember(session,questionType, member, locale.toString());
            if(memberBallotChoices.isEmpty()){
                model.addAttribute("flag","new");
                request.setAttribute("totalRounds", rounds);
                for(int i=1;i<=rounds;i++){
                    CustomParameter questionsInEachRound=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_ROUND"+i, "");
                    if(questionsInEachRound!=null){
                        request.setAttribute("round"+i, Integer.parseInt(questionsInEachRound.getValue()));
                    }else{
                        logger.error("**** Custom Parameter 'STARRED_MEMBERBALLOTCOUNCIL_ROUND'"+i+" not set");
                    }
                }
            }else{
                model.addAttribute("flag","edit");
            }

        }else{
            logger.error("**** Check request parameter 'session,deviceType and member' for null values ****");
        }
        return "question/listmemberballotchoice";
    }

    @RequestMapping(value="/memberballotchoice",method=RequestMethod.POST)
    public @ResponseBody String updateMemberBallotChoice(final HttpServletRequest request,final ModelMap model,final Locale locale){
        String strQuestionType=request.getParameter("deviceType");
        String strSession=request.getParameter("session");
        String strMember=request.getParameter("member");
        String strTotalRounds=request.getParameter("totalRounds");
        String strNoOfAdmittedQuestions=request.getParameter("noOfAdmittedQuestions");
        int count=1;
        int noOfAdmittedQuestions=Integer.parseInt(strNoOfAdmittedQuestions);
        if(strQuestionType!=null&&strSession!=null&&strMember!=null&&strTotalRounds!=null&&strNoOfAdmittedQuestions!=null){
            Session session=Session.findById(Session.class,Long.parseLong(strSession));
            DeviceType questionType=DeviceType.findById(DeviceType.class,Long.parseLong(strQuestionType));
            Member member=Member.findById(Member.class,Long.parseLong(strMember));
            Boolean status=true;
            for(int i=1;i<=Integer.parseInt(strTotalRounds);i++){
                if(count>noOfAdmittedQuestions){
                    break;
                }
                if(status){
                    MemberBallot memberBallot=MemberBallot.findByMemberRound(session, questionType, member,i, locale.toString());
                    List<MemberBallotChoice> memberBallotChoices=new ArrayList<MemberBallotChoice>();
                    CustomParameter questionsInEachRound=CustomParameter.findByName(CustomParameter.class,"STARRED_MEMBERBALLOTCOUNCIL_ROUND"+i, "");
                    for(int j=1;j<=Integer.parseInt(questionsInEachRound.getValue());j++){
                        String strChoice=request.getParameter("choice"+count);
                        String strQuestion=request.getParameter("question"+count);
                        String strAnsweringDate=request.getParameter("answeringDate"+count);
                        if(strChoice!=null&&strQuestion!=null&&strAnsweringDate!=null){
                            MemberBallotChoice memberBallotChoice=null;
                            String strMemberChoice=request.getParameter("memberBallotchoiceId"+count);
                            if(strMemberChoice!=null){
                                memberBallotChoice=MemberBallotChoice.findById(MemberBallotChoice.class,Long.parseLong(strMemberChoice));
                            }else{
                                memberBallotChoice=new MemberBallotChoice();
                            }
                            if(!strChoice.isEmpty()){
                                memberBallotChoice.setChoice(Integer.parseInt(strChoice));
                            }
                            if(!strQuestion.equals("-")){
                                Question question=Question.findById(Question.class,Long.parseLong(strQuestion));
                                if(!strAnsweringDate.equals("-")){
                                    QuestionDates questionDates=QuestionDates.findById(QuestionDates.class,Long.parseLong(strAnsweringDate));
                                    memberBallotChoice.setNewAnsweringDate(questionDates);
                                    question.setAnsweringDate(questionDates);
                                }
                                memberBallotChoice.setQuestion(question);
                                question.merge();
                            }
                            memberBallotChoice.setLocale(locale.toString());
                            memberBallotChoices.add(memberBallotChoice);
                        }else{
                            status=false;
                            break;
                        }
                        count++;
                    }
                    if(status){
                        memberBallot.setQuestionChoices(memberBallotChoices);
                        memberBallot.merge();
                    }else{
                        break;
                    }
                }
            }
        }else{
            logger.error("**** Check request parameter 'session,deviceType,member,totalRounds and noOfAdmittedQuestions' for null values ****");
            return "failure";
        }
        return "success";
    }

    @Transactional
    @RequestMapping(value="/memberballot/updateclubbing",method=RequestMethod.PUT)
    public Boolean updateClubbingMemberBallot(final HttpServletRequest request,final Locale locale){
        Boolean status=false;
        String strSession=request.getParameter("session");
        String strDeviceType=request.getParameter("deviceType");
        if(strSession!=null&&strDeviceType!=null){
            Session session=Session.findById(Session.class,Long.parseLong(strSession));
            DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
            Integer primaryInMBCount=MemberBallot.findPrimaryCount(session,deviceType,locale.toString());
            int start=0;
            int size=50;
            for(int i=start;i<primaryInMBCount;i=i+size){
                status=MemberBallot.updateClubbing(session,deviceType,i,i+size,locale.toString());
            }
        }else{
            logger.error("**** Check request parameters 'session and deviceType' for null values ****");
        }
        return status;
    }
}

