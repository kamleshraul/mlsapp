package org.mkcl.els.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.DateFormater;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.QuestionRevisionVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class QuestionRepository extends BaseRepository<Question, Long>{


    public Integer findLastStarredUnstarredShortNoticeQuestionNo(final House house,final Session currentSession){
        String query="SELECT q.number FROM questions AS q JOIN sessions AS s JOIN houses AS h "+
        "JOIN devicetypes AS dt WHERE q.session_id=s.id AND s.house_id=h.id "+
        "AND h.id="+house.getId()+" AND s.id="+currentSession.getId()+" AND dt.id=q.devicetype_id AND dt.type!='halfhourdiscussion' ORDER BY q.id DESC LIMIT 0,1";
        List result=this.em().createNativeQuery(query).getResultList();
        Integer lastNumber=0;
        if(!result.isEmpty()){
            Object i=result.get(0);
            lastNumber=Integer.parseInt(i.toString());
        }
        return lastNumber;
    }

    public Integer findLastHalfHourDiscussionQuestionNo(final House house,final Session currentSession){
        String query="SELECT q.number FROM questions AS q JOIN sessions AS s JOIN houses AS h "+
        "JOIN devicetypes AS dt WHERE q.session_id=s.id AND s.house_id=h.id "+
        "AND h.id="+house.getId()+" AND s.id="+currentSession.getId()+" AND dt.id=q.devicetype_id AND dt.type=='halfhourdiscussion' ORDER BY q.id DESC LIMIT 0,1";
        List result=this.em().createNativeQuery(query).getResultList();
        Integer lastNumber=0;
        if(!result.isEmpty()){
            Object i=result.get(0);
            lastNumber=Integer.parseInt(i.toString());
        }
        return lastNumber;
    }

    public Integer assignQuestionNo(final HouseType houseType, final Session session,
            final DeviceType questionType,final String locale) {
        String strHouseType=houseType.getType();
        String strQuestionType=questionType.getType();
        Long house=session.getHouse().getId();
        String query=null;
        if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
            if(strQuestionType.equals("questions_starred")||strQuestionType.equals("questions_unstarred")||strQuestionType.equals("questions_shortnotice")){
                query="SELECT q FROM Question q JOIN q.session JOIN s.house h JOIN q.type dt WHERE "+
                " h.id="+house+"  AND (dt.type='questions_shortnotice' OR dt.type='questions_starred' OR dt.type='questions_unstarred') ORDER BY q.number "+ApplicationConstants.DESC;
            }else if(strQuestionType.equals("questions_halfhourdiscussion")){
                query="SELECT q FROM Question q JOIN q.session JOIN s.house h JOIN q.type dt WHERE "+
                " h.id="+house+"  AND (dt.type='questions_halfhourdiscussion') ORDER BY q.number "+ApplicationConstants.DESC;
            }
        }else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
            Session lowerHouseSession=Session.find(session.getYear(),session.getType().getType(),ApplicationConstants.LOWER_HOUSE);
            House lowerHouse=lowerHouseSession.getHouse();
            CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"DB_DATETIMEFORMAT", "");
            SimpleDateFormat simpleDateFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(),"en_US");
            String lowerHouseFormationDate=simpleDateFormat.format(lowerHouse.getFormationDate());
            if(strQuestionType.equals("questions_starred")||strQuestionType.equals("questions_unstarred")||strQuestionType.equals("questions_shortnotice")){
                query="SELECT q FROM Question q JOIN q.type dt JOIN q.houseType ht WHERE "+
                " ht.type='"+ApplicationConstants.UPPER_HOUSE+"' AND q.submissionDate>='"+lowerHouseFormationDate+"' "+
                " AND (dt.type='questions_shortnotice' OR dt.type='questions_starred' OR dt.type='questions_unstarred') ORDER BY q.number "+ApplicationConstants.DESC;
            }else if(strQuestionType.equals("questions_halfhourdiscussion")){
                query="SELECT q FROM Question q JOIN q.type dt JOIN q.houseType ht WHERE "+
                " ht.type='"+ApplicationConstants.UPPER_HOUSE+"' AND q.submissionDate>='"+lowerHouseFormationDate+"' "+
                " AND (dt.type='questions_halfhourdiscussion') ORDER BY q.number "+ApplicationConstants.DESC;
            }
        }
        try{
            List<Question> questions=this.em().createQuery(query).setFirstResult(0).setMaxResults(1).getResultList();
            if(questions==null){
                return 0;
            }else if(questions.isEmpty()){
                return 0;
            }else{
                if(questions.get(0).getNumber()==null){
                    return 0;
                }else{
                    return questions.get(0).getNumber();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @SuppressWarnings("rawtypes")
    public List<QuestionRevisionVO> getRevisions(final Long questionId,final String locale) {
        String query="SELECT rs.usergroup,rs.fullname,rs.editedon,rs.status,rs.question,rs.subject,rs.remark FROM ("+
        "SELECT ugt.name as usergroup,concat(u.title,' ',u.first_name,' ',u.middle_name,' ',u.last_name) as fullname,qd.edited_on as editedon,"+
        "s.name as status,qd.question_text as question,qd.subject as subject,qd.remarks as remark FROM questions as q JOIN questions_drafts_association as qda "+
        " JOIN question_drafts as qd LEFT JOIN usergroups_types as ugt ON qd.editedastype_id=ugt.id JOIN users as u JOIN "+
        " status as s WHERE q.id=qda.question_id AND qda.question_draft_id=qd.id AND "+
        "  qd.editedby_id=u.id AND qd.recommendationstatus_id=s.id "+
        " AND q.id="+questionId +" ORDER BY qd.edited_on desc ) as rs";
        List results=this.em().createNativeQuery(query).getResultList();
        List<QuestionRevisionVO> questionRevisionVOs=new ArrayList<QuestionRevisionVO>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            QuestionRevisionVO questionRevisionVO=new QuestionRevisionVO();
            if(o[0]!=null){
                questionRevisionVO.setEditedAs(o[0].toString());
            }else{
                UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type","member", locale);
                questionRevisionVO.setEditedAs(userGroupType.getName());
            }
            questionRevisionVO.setEditedBY(o[1].toString());
            questionRevisionVO.setEditedOn(o[2].toString());
            questionRevisionVO.setStatus(o[3].toString());
            questionRevisionVO.setQuestion(o[4].toString());
            questionRevisionVO.setSubject(o[5].toString());
            if(o[6]!=null){
                questionRevisionVO.setRemarks(o[6].toString());
            }
            questionRevisionVOs.add(questionRevisionVO);
        }
        return questionRevisionVOs;
    }

    /**
     * Returns null if there is no result, else returns a List
     * of Questions.
     */
    public List<Question> find(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        String strAnsweringDate = this.answeringDateAsString(answeringDate);
        String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);

        StringBuffer query = new StringBuffer(
                " SELECT q" +
                " FROM Question q" +
                " WHERE q.session.id = " + session.getId() +
                " AND q.primaryMember.id = " + member.getId() +
                " AND q.type.id = " + deviceType.getId() +
                " AND q.group.id = " + group.getId() +
                " AND q.answeringDate.answeringDate = '" + strAnsweringDate + "'" +
                " AND q.submissionDate <= '" + strFinalSubmissionDate + "'" +
                " AND q.locale = '" + locale + "'"
        );
        query.append(this.getStatusFilters(internalStatuses));
        if(sortOrder.equals(ApplicationConstants.ASC)) {
            query.append(" ORDER BY q.number ASC");
        }
        else if(sortOrder.equals(ApplicationConstants.DESC)) {
            query.append(" ORDER BY q.number DESC");
        }

        TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
        tQuery.setMaxResults(maxNoOfQuestions);
        List<Question> questions = tQuery.getResultList();
        return questions;
    }

    /**
     * Returns null if there is no result, else returns a List
     * of Questions.
     */
    public List<Question> findBeforeAnsweringDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        String strAnsweringDate = this.answeringDateAsString(answeringDate);
        String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);

        StringBuffer query = new StringBuffer(
                " SELECT q" +
                " FROM Question q" +
                " WHERE q.session.id = " + session.getId() +
                " AND q.primaryMember.id = " + member.getId() +
                " AND q.type.id = " + deviceType.getId() +
                " AND q.group.id = " + group.getId() +
                " AND q.answeringDate.answeringDate < '" + strAnsweringDate + "'" +
                " AND q.submissionDate <= '" + strFinalSubmissionDate + "'" +
                " AND q.locale = '" + locale + "'"
        );
        query.append(this.getStatusFilters(internalStatuses));

        TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
        List<Question> questions = tQuery.getResultList();
        if(questions != null) {
            questions = Question.sortByAnsweringDate(questions, sortOrder);
            if(questions.size() >= maxNoOfQuestions) {
                return questions.subList(0, maxNoOfQuestions);
            }
        }
        return questions;
    }

    /**
     * Returns null if there is no result, else returns a List
     * of Questions.
     */
    public List<Question> findNonAnsweringDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);

        StringBuffer query = new StringBuffer(
                " SELECT q" +
                " FROM Question q" +
                " WHERE q.session.id = " + session.getId() +
                " AND q.primaryMember.id = " + member.getId() +
                " AND q.type.id = " + deviceType.getId() +
                " AND q.group.id = " + group.getId() +
                " AND q.answeringDate = " + null +
                " AND q.submissionDate <= '" + strFinalSubmissionDate + "'" +
                " AND q.locale = '" + locale + "'"
        );
        query.append(this.getStatusFilters(internalStatuses));
        if(sortOrder.equals(ApplicationConstants.ASC)) {
            query.append(" ORDER BY q.number ASC");
        }
        else if(sortOrder.equals(ApplicationConstants.DESC)) {
            query.append(" ORDER BY q.number DESC");
        }

        TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
        tQuery.setMaxResults(maxNoOfQuestions);
        List<Question> questions = tQuery.getResultList();
        return questions;
    }

    public List<Question> findDatedQuestions(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Integer maxNoOfQuestions,
            final String locale) {
        String strAnsweringDate = this.answeringDateAsString(answeringDate);
        String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);

        StringBuffer query = new StringBuffer(
                " SELECT q" +
                " FROM Question q" +
                " WHERE q.session.id = " + session.getId() +
                " AND q.primaryMember.id = " + member.getId() +
                " AND q.type.id = " + deviceType.getId() +
                " AND q.group.id = " + group.getId() +
                " AND q.answeringDate.answeringDate <= '" + strAnsweringDate + "'" +
                " AND q.submissionDate <= '" + strFinalSubmissionDate + "'" +
                " AND q.locale = '" + locale + "'"
        );
        query.append(this.getStatusFilters(internalStatuses));
        query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");

        TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
        tQuery.setMaxResults(maxNoOfQuestions);
        List<Question> questions = tQuery.getResultList();
        return questions;
    }

    public Question find(final Session session, final Integer number) {
        Search search = new Search();
        search.addFilterEqual("session", session);
        search.addFilterEqual("number", number);
        return this.searchUnique(search);
    }

    private String getStatusFilters(final Status[] internalStatuses) {
        StringBuffer sb = new StringBuffer();
        sb.append(" AND(");
        int n = internalStatuses.length;
        for(int i = 0; i < n; i++) {
            sb.append(" q.internalStatus.id = " + internalStatuses[i].getId());
            if(i < n - 1) {
                sb.append(" OR");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @SuppressWarnings("rawtypes")
    public List<QuestionSearchVO> fullTextSearchClubbing(final String param,final Long sessionId,final Long groupId, final Long currentChartId, final Long questionId, final String locale) {
        /*
         * data to fetch and from where.
         */
        String initialQuery="SELECT q.id as id,q.number as number,q.subject as subject "+
        ",q.question_text as questionText,q.revised_subject as revisedSubject "+
        ",q.revised_question_text as revisedQuestionText,t.name as title "+
        ",m.first_name as firstName,m.middle_name as middleName,m.last_name as lastName "+
        ",mi.name as ministry,d.name as department,sd.name as subdepartment,c.answering_date as answeringdate,g.number as groupnumber "+
        ",st.name as statusname "+
        "FROM questions as q "+
        "left join members as m on(q.member_id=m.id ) "+
        "left join titles as t on(t.id=m.title_id) "+
        "left join ministries as mi on(q.ministry_id=mi.id) "+
        "left join departments as d on(q.department_id=d.id) "+
        "left join subdepartments as sd on(q.subdepartment_id=sd.id) "+
        "join charts as c "+
        "join charts_chart_entries as cce "+
        "join chart_entries_questions as ceq "+
        "left join groups as g on(q.group_id=g.id) "+
        "join sessions as s join status as st "+
        "WHERE c.group_id=q.group_id and "+
        "c.session_id=s.id and cce.chart_id=c.id and ceq.chart_entry_id=cce.chart_entry_id "+
        "and ceq.question_id=q.id and st.id=q.internalstatus_id and "+
        "g.id="+groupId+" and s.id="+sessionId+" and c.id<="+currentChartId+" and q.parent is null "+
        "and q.id<>"+questionId;
        /*
         * fulltext query
         */
        String searchQuery=null;
        if(!param.contains("+")&&!param.contains("-")){
            searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
            "against('"+param+"' in natural language mode)";
        }else if(param.contains("+")&&!param.contains("-")){
            String[] parameters=param.split("\\+");
            StringBuffer buffer=new StringBuffer();
            for(String i:parameters){
                buffer.append("+"+i+" ");
            }
            searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
            "against('"+buffer.toString()+"' in boolean  mode)";
        }else if(!param.contains("+")&&param.contains("-")){
            String[] parameters=param.split("-");
            StringBuffer buffer=new StringBuffer();
            for(String i:parameters){
                buffer.append(i+" "+"-");
            }
            buffer.deleteCharAt(buffer.length()-1);
            searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
            "against('"+buffer.toString()+"' in boolean  mode)";
        }else if(param.contains("+")||param.contains("-")){
            searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
            "against('"+param+"' in boolean  mode)";
        }
        /*
         * order by query.It is arranged first by answering date descending and then group number descending
         */
        String orderByQuery=" ORDER BY c.answering_date desc,q.number "+ApplicationConstants.DESC;
        String query=initialQuery+searchQuery+orderByQuery;
        String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.questionText,rs.revisedSubject"+
        ",rs.revisedQuestionText,rs.title,rs.firstName,rs.middleName,rs.lastName"+
        ",rs.ministry,rs.department,rs.subdepartment,rs.answeringdate,rs.groupnumber,rs.statusname FROM ("+query+") as rs";
        List results=this.em().createNativeQuery(finalQuery).getResultList();
        List<QuestionSearchVO> questionSearchVOs=new ArrayList<QuestionSearchVO>();
        SimpleDateFormat format=FormaterUtil.getDateFormatter(locale);
        CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_DATEFORMAT","");
        SimpleDateFormat dbFormat=FormaterUtil.getDateFormatter(customParameter.getValue(), locale);
        if(results!=null){
            for(Object i:results){
                Object[] o=(Object[]) i;
                QuestionSearchVO questionSearchVO=new QuestionSearchVO();
                if(o[0]!=null){
                    questionSearchVO.setId(Long.parseLong(o[0].toString()));
                }
                if(o[1]!=null){
                    questionSearchVO.setNumber(Integer.parseInt(o[1].toString()));
                }
                if(o[2]!=null){
                    questionSearchVO.setSubject(o[2].toString());
                }
                if(o[3]!=null){
                    questionSearchVO.setQuestionText(o[3].toString());
                }
                if(o[4]!=null){
                    questionSearchVO.setRevisedSubject(o[4].toString());
                }
                if(o[5]!=null){
                    questionSearchVO.setRevisedQuestionText(o[5].toString());
                }
                if(o[6]!=null){
                    if(o[8]!=null){
                        questionSearchVO.setPrimaryMember(o[6].toString()+" "+o[7].toString()+" "+o[8].toString()+" "+o[9].toString());
                    }else{
                        questionSearchVO.setPrimaryMember(o[6].toString()+" "+o[7].toString()+" "+o[9].toString());
                    }
                }else{
                    questionSearchVO.setPrimaryMember(o[7].toString()+" "+o[8].toString()+" "+o[9].toString());
                }
                if(o[10]!=null){
                    questionSearchVO.setMinistry(o[10].toString());
                }
                if(o[11]!=null){
                    questionSearchVO.setDepartment(o[11].toString());
                }
                if(o[12]!=null){
                    questionSearchVO.setSubDepartment(o[12].toString());
                }
                if(o[13]!=null){
                    Date dbDate;
                    try {
                        dbDate = dbFormat.parse(o[13].toString());
                        questionSearchVO.setAnsweringDate(format.format(dbDate));
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(o[14]!=null){
                    questionSearchVO.setGroup(o[14].toString());
                }
                if(o[15]!=null){
                    questionSearchVO.setStatus(o[15].toString());
                }
                questionSearchVOs.add(questionSearchVO);
            }
        }
        return questionSearchVOs;
    }

    @SuppressWarnings("unchecked")
    public List<Question> findAll(final Member currentMember, final Session session,
            final DeviceType deviceType, final Status internalStatus) {
        List<Question> questions=new ArrayList<Question>();
        String query="SELECT q FROM Question q WHERE q.primaryMember.id="+currentMember.getId()+" "+
        " AND q.session.id="+session.getId()+" AND q.type.id="+deviceType.getId()+" "+
        " AND q.internalStatus.id="+internalStatus.getId()+" ORDER BY q.number "+ApplicationConstants.ASC;
        questions=this.em().createQuery(query).getResultList();
        return questions;
    }

    @SuppressWarnings("unchecked")
    public List<Question> findAllFirstBatch(final Member currentMember, final Session session,
            final DeviceType deviceType, final Status internalStatus) {
        List<Question> questions=new ArrayList<Question>();
        //        Date firstBatchDate=session.getQuestionSubmissionFirstBatchDate();
        //        Date firstBatchStartTime=session.getQuestionSubmissionFirstBatchStartTimeUH();
        //        Date firstBatchEndTime=session.getQuestionSubmissionFirstBatchEndTimeUH();
        //        if(firstBatchDate!=null&&firstBatchStartTime!=null&&firstBatchEndTime!=null){
        //            Calendar calendar1=new GregorianCalendar();
        //            calendar1.setTime(firstBatchDate);
        //
        //            Calendar startTime=new GregorianCalendar();
        //            startTime.setTime(firstBatchStartTime);
        //            startTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
        //            startTime.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));
        //            startTime.set(Calendar.DATE,calendar1.get(Calendar.DATE));
        //
        //            Calendar endTime=new GregorianCalendar();
        //            endTime.setTime(firstBatchEndTime);
        //            endTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
        //            endTime.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));
        //            endTime.set(Calendar.DATE,calendar1.get(Calendar.DATE));
        //
        //            CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DB_DATETIMEFORMAT", "");
        //            if(customParameter!=null){
        //                SimpleDateFormat format= FormaterUtil.getDateFormatter(customParameter.getValue(), "en_US");
        //                String query="SELECT q FROM Question q WHERE q.primaryMember.id="+currentMember.getId()+" "+
        //                " AND q.session.id="+session.getId()+" AND q.type.id="+deviceType.getId()+" "+
        //                " AND q.internalStatus.id="+internalStatus.getId()+" AND q.submissionDate>='"+format.format(startTime.getTime())+"' AND q.submissionDate>='"+format.format(endTime.getTime())+"' ORDER BY q.number "+ApplicationConstants.ASC;
        //                questions=this.em().createQuery(query).getResultList();
        //            }
        //        }
        return questions;
    }

    @SuppressWarnings("unchecked")
    public List<Question> findAllSecondBatch(final Member currentMember, final Session session,
            final DeviceType deviceType, final Status internalStatus) {
        List<Question> questions=new ArrayList<Question>();
        //        Date secondBatchDate=session.getQuestionSubmissionSecondBatchDateUH();
        //        Date secondBatchStartTime=session.getQuestionSubmissionSecondBatchStartTimeUH();
        //        Date secondBatchEndTime=session.getQuestionSubmissionSecondBatchEndTimeUH();
        //        if(secondBatchDate!=null&&secondBatchStartTime!=null&&secondBatchEndTime!=null){
        //            Calendar calendar1=new GregorianCalendar();
        //            calendar1.setTime(secondBatchDate);
        //
        //            Calendar startTime=new GregorianCalendar();
        //            startTime.setTime(secondBatchStartTime);
        //            startTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
        //            startTime.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));
        //            startTime.set(Calendar.DATE,calendar1.get(Calendar.DATE));
        //
        //            Calendar endTime=new GregorianCalendar();
        //            endTime.setTime(secondBatchEndTime);
        //            endTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
        //            endTime.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));
        //            endTime.set(Calendar.DATE,calendar1.get(Calendar.DATE));
        //
        //            CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DB_DATETIMEFORMAT", "");
        //            if(customParameter!=null){
        //                SimpleDateFormat format= FormaterUtil.getDateFormatter(customParameter.getValue(), "en_US");
        //                String query="SELECT q FROM Question q WHERE q.primaryMember.id="+currentMember.getId()+" "+
        //                " AND q.session.id="+session.getId()+" AND q.type.id="+deviceType.getId()+" "+
        //                " AND q.internalStatus.id="+internalStatus.getId()+" AND q.submissionDate>='"+format.format(startTime.getTime())+"' AND q.submissionDate>='"+format.format(endTime.getTime())+"' ORDER BY q.number "+ApplicationConstants.ASC;
        //                questions=this.em().createQuery(query).getResultList();
        //            }
        //        }
        return questions;
    }

    public Boolean club(final Long questionBeingProcessed,
            final Long questionBeingClubbed,final String locale) {
        Boolean status=true;
        Question beingProcessedQuestion=Question.findById(Question.class,questionBeingProcessed);
        Question beingClubbedQuestion=Question.findById(Question.class,questionBeingClubbed);
        List<Question> beingProcessedClubbing=beingProcessedQuestion.getClubbings();
        if(beingProcessedClubbing==null){
            beingProcessedClubbing=new ArrayList<Question>();
        }
        List<Question> beingClubbedClubbing=beingClubbedQuestion.getClubbings();
        if(beingClubbedClubbing==null){
            beingClubbedClubbing=new ArrayList<Question>();
        }
        Status oldPQStatus=beingProcessedQuestion.getInternalStatus();
        Status oldCQStatus=beingClubbedQuestion.getInternalStatus();
        Status newPQStatus=beingProcessedQuestion.getInternalStatus();
        Status newCQStatus=beingClubbedQuestion.getInternalStatus();
        String processedType=oldPQStatus.getType();
        String clubbedType=oldCQStatus.getType();
        if(beingProcessedQuestion.getNumber()<beingClubbedQuestion.getNumber()){
            if(processedType.equals("question_before_workflow_tobeputup")
                    &&clubbedType.equals("question_before_workflow_tobeputup")){
                List<Question> cClubbings=beingClubbedQuestion.getClubbings();
                beingClubbedQuestion.setClubbings(null);
                beingProcessedClubbing.add(beingClubbedQuestion);
                if(cClubbings!=null){
                    if(!cClubbings.isEmpty()){
                        for(Question k:cClubbings){
                            k.setParent(beingProcessedQuestion);
                            k.merge();
                        }
                        for(Question k:cClubbings){
                            beingProcessedClubbing.add(k);
                        }
                    }
                }
                beingClubbedQuestion.setParent(beingProcessedQuestion);
                newPQStatus=Status.findByType("question_before_workflow_tobeputup", locale);
                newCQStatus=Status.findByType("question_before_workflow_clubbed", locale);
                beingProcessedQuestion.setInternalStatus(newPQStatus);
                beingProcessedQuestion.setRecommendationStatus(newPQStatus);
                beingClubbedQuestion.setInternalStatus(newCQStatus);
                beingClubbedQuestion.setRecommendationStatus(newCQStatus);
                beingProcessedQuestion.merge();
            }else if(processedType.equals("question_before_workflow_tobeputup")
                    &&(clubbedType.equals("question_workflow_approving_admission")
                            ||clubbedType.equals("question_workflow_approving_rejection")
                            ||clubbedType.equals("question_workflow_approving_converttounstarred"))){
                newPQStatus=Status.findByType("question_before_workflow_nameclub", locale);
                beingProcessedQuestion.setInternalStatus(newPQStatus);
                beingProcessedQuestion.setRecommendationStatus(newPQStatus);
                beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##NAMECLUB~"+beingClubbedQuestion.getId()+"##");
            }else if(processedType.equals("question_before_workflow_tobeputup")
                    &&clubbedType.equals("question_workflow_approving_clarificationneeded")){
                newPQStatus=Status.findByType("question_before_workflow_putonhold", locale);
                beingProcessedQuestion.setInternalStatus(newPQStatus);
                beingProcessedQuestion.setRecommendationStatus(newPQStatus);
                beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##PUTONHOLD~"+beingClubbedQuestion.getId()+"##");
            }else if(processedType.equals("question_before_workflow_tobeputup")
                    &&(clubbedType.equals("question_workflow_decisionstatus_admission")
                            ||clubbedType.equals("question_workflow_decisionstatus_rejection")
                            ||clubbedType.equals("question_workflow_decisionstatus_converttounstarred")
                            ||clubbedType.equals("question_workflow_decisionstatus_onhold")
                            ||clubbedType.equals("question_workflow_decisionstatus_discuss")
                            ||clubbedType.equals("question_workflow_decisionstatus_clarificationneeded")
                            ||clubbedType.equals("question_workflow_decisionstatus_nameclubbing")
                            ||clubbedType.equals("question_workflow_decisionstatus_sendback")
                            ||clubbedType.equals("question_workflow_decisionstatus_groupchanged")
                            ||clubbedType.equals("question_workflow_decisionstatus_clarificationreceived")
                            ||clubbedType.equals("question_workflow_decisionstatus_clarificationnotreceived")
                    )){
                newPQStatus=Status.findByType("question_before_workflow_clubwithpending", locale);
                beingProcessedQuestion.setInternalStatus(newPQStatus);
                beingProcessedQuestion.setRecommendationStatus(newPQStatus);
                beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##CLUBWITHPENDING~"+beingClubbedQuestion.getId()+"##");
            }else if(clubbedType.equals("question_before_workflow_tobeputup")
                    &&(processedType.equals("question_workflow_approving_admission")
                            ||processedType.equals("question_workflow_approving_rejection")
                            ||processedType.equals("question_workflow_approving_converttounstarred"))){
                newCQStatus=Status.findByType("question_before_workflow_nameclub", locale);
                beingClubbedQuestion.setInternalStatus(newCQStatus);
                beingClubbedQuestion.setRecommendationStatus(newCQStatus);
                beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##NAMECLUB~"+beingProcessedQuestion.getId()+"##");
            }else if(clubbedType.equals("question_before_workflow_tobeputup")
                    &&processedType.equals("question_workflow_approving_clarificationneeded")){
                newCQStatus=Status.findByType("question_before_workflow_putonhold", locale);
                beingClubbedQuestion.setInternalStatus(newCQStatus);
                beingClubbedQuestion.setRecommendationStatus(newCQStatus);
                beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##PUTONHOLD~"+beingProcessedQuestion.getId()+"##");
            }else if(clubbedType.equals("question_before_workflow_tobeputup")
                    &&(processedType.equals("question_workflow_decisionstatus_admission")
                            ||processedType.equals("question_workflow_decisionstatus_rejection")
                            ||processedType.equals("question_workflow_decisionstatus_converttounstarred")
                            ||processedType.equals("question_workflow_decisionstatus_onhold")
                            ||processedType.equals("question_workflow_decisionstatus_discuss")
                            ||processedType.equals("question_workflow_decisionstatus_clarificationneeded")
                            ||processedType.equals("question_workflow_decisionstatus_nameclubbing")
                            ||processedType.equals("question_workflow_decisionstatus_sendback")
                            ||processedType.equals("question_workflow_decisionstatus_groupchanged")
                            ||processedType.equals("question_workflow_decisionstatus_clarificationreceived")
                            ||processedType.equals("question_workflow_decisionstatus_clarificationnotreceived")
                    )){
                newCQStatus=Status.findByType("question_before_workflow_clubwithpending", locale);
                beingClubbedQuestion.setInternalStatus(newCQStatus);
                beingClubbedQuestion.setRecommendationStatus(newCQStatus);
                beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##CLUBWITHPENDING~"+beingProcessedQuestion.getId()+"##");
            }
        }else if(beingProcessedQuestion.getNumber()>beingClubbedQuestion.getNumber()){
            if(processedType.equals("question_before_workflow_tobeputup")
                    &&clubbedType.equals("question_before_workflow_tobeputup")){
                List<Question> pClubbings=beingProcessedQuestion.getClubbings();
                beingProcessedQuestion.setClubbings(null);
                beingClubbedClubbing.add(beingProcessedQuestion);
                if(pClubbings!=null){
                    if(!pClubbings.isEmpty()){
                        for(Question k:pClubbings){
                            k.setParent(beingClubbedQuestion);
                            k.merge();
                        }
                        for(Question k:pClubbings){
                            beingClubbedClubbing.add(k);
                        }
                    }
                }
                beingProcessedQuestion.setParent(beingClubbedQuestion);
                newPQStatus=Status.findByType("question_before_workflow_clubbed", locale);
                newCQStatus=Status.findByType("question_before_workflow_tobeputup", locale);
                beingProcessedQuestion.setInternalStatus(newPQStatus);
                beingProcessedQuestion.setRecommendationStatus(newPQStatus);
                beingClubbedQuestion.setInternalStatus(newCQStatus);
                beingClubbedQuestion.setRecommendationStatus(newCQStatus);
            }else if(processedType.equals("question_before_workflow_tobeputup")
                    &&(clubbedType.equals("question_workflow_approving_admission")
                            ||clubbedType.equals("question_workflow_approving_rejection")
                            ||clubbedType.equals("question_workflow_approving_converttounstarred"))){
                newPQStatus=Status.findByType("question_before_workflow_nameclub", locale);
                beingProcessedQuestion.setInternalStatus(newPQStatus);
                beingProcessedQuestion.setRecommendationStatus(newPQStatus);
                beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##NAMECLUB~"+beingClubbedQuestion.getId()+"##");
            }else if(processedType.equals("question_before_workflow_tobeputup")
                    &&clubbedType.equals("question_workflow_approving_clarificationneeded")){
                newPQStatus=Status.findByType("question_before_workflow_putonhold", locale);
                beingProcessedQuestion.setInternalStatus(newPQStatus);
                beingProcessedQuestion.setRecommendationStatus(newPQStatus);
                beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##PUTONHOLD~"+beingClubbedQuestion.getId()+"##");
            }else if(processedType.equals("question_before_workflow_tobeputup")
                    &&(clubbedType.equals("question_workflow_decisionstatus_admission")
                            ||clubbedType.equals("question_workflow_decisionstatus_rejection")
                            ||clubbedType.equals("question_workflow_decisionstatus_converttounstarred")
                            ||clubbedType.equals("question_workflow_decisionstatus_onhold")
                            ||clubbedType.equals("question_workflow_decisionstatus_discuss")
                            ||clubbedType.equals("question_workflow_decisionstatus_clarificationneeded")
                            ||clubbedType.equals("question_workflow_decisionstatus_nameclubbing")
                            ||clubbedType.equals("question_workflow_decisionstatus_sendback")
                            ||clubbedType.equals("question_workflow_decisionstatus_groupchanged")
                            ||clubbedType.equals("question_workflow_decisionstatus_clarificationreceived")
                            ||clubbedType.equals("question_workflow_decisionstatus_clarificationnotreceived")
                    )){
                newPQStatus=Status.findByType("question_before_workflow_clubwithpending", locale);
                beingProcessedQuestion.setInternalStatus(newPQStatus);
                beingProcessedQuestion.setRecommendationStatus(newPQStatus);
                beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##CLUBWITHPENDING~"+beingClubbedQuestion.getId()+"##");
            }else if(clubbedType.equals("question_before_workflow_tobeputup")
                    &&(processedType.equals("question_workflow_approving_admission")
                            ||processedType.equals("question_workflow_approving_rejection")
                            ||processedType.equals("question_workflow_approving_converttounstarred"))){
                newCQStatus=Status.findByType("question_before_workflow_nameclub", locale);
                beingClubbedQuestion.setInternalStatus(newCQStatus);
                beingClubbedQuestion.setRecommendationStatus(newCQStatus);
                beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##NAMECLUB~"+beingProcessedQuestion.getId()+"##");
            }else if(clubbedType.equals("question_before_workflow_tobeputup")
                    &&processedType.equals("question_workflow_approving_clarificationneeded")){
                newCQStatus=Status.findByType("question_before_workflow_putonhold", locale);
                beingClubbedQuestion.setInternalStatus(newCQStatus);
                beingClubbedQuestion.setRecommendationStatus(newCQStatus);
                beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##PUTONHOLD~"+beingProcessedQuestion.getId()+"##");
            }else if(clubbedType.equals("question_before_workflow_tobeputup")
                    &&(processedType.equals("question_workflow_decisionstatus_admission")
                            ||processedType.equals("question_workflow_decisionstatus_rejection")
                            ||processedType.equals("question_workflow_decisionstatus_converttounstarred")
                            ||processedType.equals("question_workflow_decisionstatus_onhold")
                            ||processedType.equals("question_workflow_decisionstatus_discuss")
                            ||processedType.equals("question_workflow_decisionstatus_clarificationneeded")
                            ||processedType.equals("question_workflow_decisionstatus_nameclubbing")
                            ||processedType.equals("question_workflow_decisionstatus_sendback")
                            ||processedType.equals("question_workflow_decisionstatus_groupchanged")
                            ||processedType.equals("question_workflow_decisionstatus_clarificationreceived")
                            ||processedType.equals("question_workflow_decisionstatus_clarificationnotreceived")
                    )){
                newCQStatus=Status.findByType("question_before_workflow_clubwithpending", locale);
                beingClubbedQuestion.setInternalStatus(newCQStatus);
                beingClubbedQuestion.setRecommendationStatus(newCQStatus);
                beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##CLUBWITHPENDING~"+beingProcessedQuestion.getId()+"##");
            }
        }else{
            status= false;
        }
        beingProcessedQuestion.merge();
        beingClubbedQuestion.merge();
        return status;
    }

    public Boolean unclub(final Long questionBeingProcessed,
            final Long questionBeingClubbed, final String locale) {
        Boolean status=true;
        Question beingProcessedQuestion=Question.findById(Question.class,questionBeingProcessed);
        Question beingClubbedQuestion=Question.findById(Question.class,questionBeingClubbed);
        if(beingProcessedQuestion.getNumber()<beingClubbedQuestion.getNumber()){
            List<Question> oldClubbedQuestions=beingProcessedQuestion.getClubbings();
            List<Question> newClubbedQuestions=new ArrayList<Question>();
            for(Question i:oldClubbedQuestions){
                if(i.getId()!=beingClubbedQuestion.getId()){
                    newClubbedQuestions.add(i);
                }
            }
            beingProcessedQuestion.setClubbings(newClubbedQuestions);
            beingClubbedQuestion.setParent(null);
        }else if(beingProcessedQuestion.getNumber()>beingClubbedQuestion.getNumber()){
            List<Question> oldClubbedQuestions=beingClubbedQuestion.getClubbings();
            List<Question> newClubbedQuestions=new ArrayList<Question>();
            for(Question i:oldClubbedQuestions){
                if(i.getId()!=beingProcessedQuestion.getId()){
                    newClubbedQuestions.add(i);
                }
            }
            beingClubbedQuestion.setClubbings(newClubbedQuestions);
            beingProcessedQuestion.setParent(null);
        }else{
            status= false;
        }
        beingProcessedQuestion.merge();
        beingClubbedQuestion.merge();
        return status;
    }


    //=========== ADD FOLLOWING METHODS ==========================
    /**
     * Returns null if there is no result, else returns a List
     * of Questions.
     */
    public List<Question> find(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Question[] excludeQuestions,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        String strAnsweringDate = this.answeringDateAsString(answeringDate);
        String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);

        StringBuffer query = new StringBuffer(
                " SELECT q" +
                " FROM Question q" +
                " WHERE q.session.id = " + session.getId() +
                " AND q.primaryMember.id = " + member.getId() +
                " AND q.type.id = " + deviceType.getId() +
                " AND q.group.id = " + group.getId() +
                " AND q.answeringDate.answeringDate = '" + strAnsweringDate + "'" +
                " AND q.submissionDate <= '" + strFinalSubmissionDate + "'" +
                " AND q.locale = '" + locale + "'"
        );
        query.append(this.getStatusFilters(internalStatuses));
        query.append(this.getQuestionFilters(excludeQuestions));
        if(sortOrder.equals(ApplicationConstants.ASC)) {
            query.append(" ORDER BY q.number ASC");
        }
        else if(sortOrder.equals(ApplicationConstants.DESC)) {
            query.append(" ORDER BY q.number DESC");
        }

        TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
        tQuery.setMaxResults(maxNoOfQuestions);
        List<Question> questions = tQuery.getResultList();
        return questions;
    }

    /**
     * Returns null if there is no result, else returns a List
     * of Questions.
     */
    public List<Question> findBeforeAnsweringDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Question[] excludeQuestions,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        String strAnsweringDate = this.answeringDateAsString(answeringDate);
        String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);

        StringBuffer query = new StringBuffer(
                " SELECT q" +
                " FROM Question q" +
                " WHERE q.session.id = " + session.getId() +
                " AND q.primaryMember.id = " + member.getId() +
                " AND q.type.id = " + deviceType.getId() +
                " AND q.group.id = " + group.getId() +
                " AND q.answeringDate.answeringDate < '" + strAnsweringDate + "'" +
                " AND q.submissionDate <= '" + strFinalSubmissionDate + "'" +
                " AND q.locale = '" + locale + "'"
        );
        query.append(this.getStatusFilters(internalStatuses));
        query.append(this.getQuestionFilters(excludeQuestions));

        TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
        List<Question> questions = tQuery.getResultList();
        if(questions != null) {
            questions = Question.sortByAnsweringDate(questions, sortOrder);
            if(questions.size() >= maxNoOfQuestions) {
                return questions.subList(0, maxNoOfQuestions);
            }
        }
        return questions;
    }

    /**
     * Returns null if there is no result, else returns a List
     * of Questions.
     */
    public List<Question> findNonAnsweringDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date finalSubmissionDate,
            final Status[] internalStatuses,
            final Question[] excludeQuestions,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);

        StringBuffer query = new StringBuffer(
                " SELECT q" +
                " FROM Question q" +
                " WHERE q.session.id = " + session.getId() +
                " AND q.primaryMember.id = " + member.getId() +
                " AND q.type.id = " + deviceType.getId() +
                " AND q.group.id = " + group.getId() +
                " AND q.answeringDate = " + null +
                " AND q.submissionDate <= '" + strFinalSubmissionDate + "'" +
                " AND q.locale = '" + locale + "'"
        );
        query.append(this.getStatusFilters(internalStatuses));
        query.append(this.getQuestionFilters(excludeQuestions));
        if(sortOrder.equals(ApplicationConstants.ASC)) {
            query.append(" ORDER BY q.number ASC");
        }
        else if(sortOrder.equals(ApplicationConstants.DESC)) {
            query.append(" ORDER BY q.number DESC");
        }

        TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
        tQuery.setMaxResults(maxNoOfQuestions);
        List<Question> questions = tQuery.getResultList();
        return questions;
    }

    private String submissionDateAsString(final Date date) {
        // Removed for performance reason. Uncomment when Caching mechanism is added
        // CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
        //		"DB_TIMESTAMP", "");
        // String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
        String strDate = new DateFormater().formatDateToString(date, "yyyy-MM-dd HH:mm:ss");
        String str = strDate.replaceFirst("00:00:00", "23:59:59");
        return str;
    }

    private String answeringDateAsString(final Date date) {
        // Removed for performance reason. Uncomment when Caching mechanism is added
        // CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
        //		"DB_DATEFORMAT", "");
        // String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
        String strDate = new DateFormater().formatDateToString(date, "yyyy-MM-dd");
        String str = strDate.replaceFirst("00:00:00", "23:59:59");
        return str;
    }

    private String getQuestionFilters(final Question[] excludeQuestions) {
        StringBuffer sb = new StringBuffer();
        sb.append(" AND(");
        int n = excludeQuestions.length;
        for(int i = 0; i < n; i++) {
            sb.append(" q.id != " + excludeQuestions[i].getId());
            if(i < n - 1) {
                sb.append(" AND");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public List<Question> findDatedQuestions(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date startTime,
            final Date endTime,
            final Status[] internalStatuses,
            final Integer maxNoOfQuestions,
            final String locale) {
        String strAnsweringDate = this.answeringDateAsString(answeringDate);

        // Removed for performance reason. Uncomment when Caching mechanism is added
        // CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
        //      "DB_TIMESTAMP", "");
        // String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
        String strStartTime = new DateFormater().formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
        String strEndTime = new DateFormater().formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

        StringBuffer query = new StringBuffer(
                " SELECT q" +
                " FROM Question q" +
                " WHERE q.session.id = " + session.getId() +
                " AND q.primaryMember.id = " + member.getId() +
                " AND q.type.id = " + deviceType.getId() +
                " AND q.group.id = " + group.getId() +
                " AND q.answeringDate.answeringDate <= '" + strAnsweringDate + "'" +
                " AND q.submissionDate >= '" + strStartTime + "'" +
                " AND q.submissionDate <= '" + strEndTime + "'" +
                " AND q.locale = '" + locale + "'"
        );
        query.append(this.getStatusFilters(internalStatuses));
        query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");

        TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
        tQuery.setMaxResults(maxNoOfQuestions);
        List<Question> questions = tQuery.getResultList();
        return questions;
    }

    /**
     * Returns null if there is no result, else returns a List
     * of Questions.
     */
    public List<Question> findNonAnsweringDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date startTime,
            final Date endTime,
            final Status[] internalStatuses,
            final Integer maxNoOfQuestions,
            final String sortOrder,
            final String locale) {
        // Removed for performance reason. Uncomment when Caching mechanism is added
        // CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
        //      "DB_TIMESTAMP", "");
        // String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
        String strStartTime = new DateFormater().formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
        String strEndTime = new DateFormater().formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

        StringBuffer query = new StringBuffer(
                " SELECT q" +
                " FROM Question q" +
                " WHERE q.session.id = " + session.getId() +
                " AND q.primaryMember.id = " + member.getId() +
                " AND q.type.id = " + deviceType.getId() +
                " AND q.group.id = " + group.getId() +
                " AND q.answeringDate = " + null +
                " AND q.submissionDate >= '" + strStartTime + "'" +
                " AND q.submissionDate <= '" + strEndTime + "'" +
                " AND q.locale = '" + locale + "'"
        );
        query.append(this.getStatusFilters(internalStatuses));
        if(sortOrder.equals(ApplicationConstants.ASC)) {
            query.append(" ORDER BY q.number ASC");
        }
        else if(sortOrder.equals(ApplicationConstants.DESC)) {
            query.append(" ORDER BY q.number DESC");
        }

        TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
        tQuery.setMaxResults(maxNoOfQuestions);
        List<Question> questions = tQuery.getResultList();
        return questions;
    }

    public List<Question> findDatedQuestions(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date answeringDate,
            final Date startTime,
            final Date endTime,
            final Status[] internalStatuses,
            final String locale) {
        String strAnsweringDate = this.answeringDateAsString(answeringDate);

        // Removed for performance reason. Uncomment when Caching mechanism is added
        // CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
        //      "DB_TIMESTAMP", "");
        // String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
        String strStartTime = new DateFormater().formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
        String strEndTime = new DateFormater().formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

        StringBuffer query = new StringBuffer(
                " SELECT q" +
                " FROM Question q" +
                " WHERE q.session.id = " + session.getId() +
                " AND q.primaryMember.id = " + member.getId() +
                " AND q.type.id = " + deviceType.getId() +
                " AND q.group.id = " + group.getId() +
                " AND q.answeringDate.answeringDate <= '" + strAnsweringDate + "'" +
                " AND q.submissionDate >= '" + strStartTime + "'" +
                " AND q.submissionDate <= '" + strEndTime + "'" +
                " AND q.locale = '" + locale + "'"
        );
        query.append(this.getStatusFilters(internalStatuses));
        query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");

        TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
        List<Question> questions = tQuery.getResultList();
        return questions;
    }

    public List<Question> findNonAnsweringDate(final Session session,
            final Member member,
            final DeviceType deviceType,
            final Group group,
            final Date startTime,
            final Date endTime,
            final Status[] internalStatuses,
            final String sortOrder,
            final String locale) {
        // Removed for performance reason. Uncomment when Caching mechanism is added
        // CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
        //      "DB_TIMESTAMP", "");
        // String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
        String strStartTime = new DateFormater().formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
        String strEndTime = new DateFormater().formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

        StringBuffer query = new StringBuffer(
                " SELECT q" +
                " FROM Question q" +
                " WHERE q.session.id = " + session.getId() +
                " AND q.primaryMember.id = " + member.getId() +
                " AND q.type.id = " + deviceType.getId() +
                " AND q.group.id = " + group.getId() +
                " AND q.answeringDate = " + null +
                " AND q.submissionDate >= '" + strStartTime + "'" +
                " AND q.submissionDate <= '" + strEndTime + "'" +
                " AND q.locale = '" + locale + "'"
        );
        query.append(this.getStatusFilters(internalStatuses));
        if(sortOrder.equals(ApplicationConstants.ASC)) {
            query.append(" ORDER BY q.number ASC");
        }
        else if(sortOrder.equals(ApplicationConstants.DESC)) {
            query.append(" ORDER BY q.number DESC");
        }

        TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
        List<Question> questions = tQuery.getResultList();
        return questions;
    }

    @SuppressWarnings("unchecked")
    public Boolean createMemberBallotAttendance(
            final Session session, final DeviceType questionType, final String locale) {
        /*
         * first we will check if attendance has already been created for particular
         * session ,device type and locale
         */
        Boolean status=memberBallotCreated(session,questionType,locale);
        Boolean operationStatus=false;
        List<MemberBallotAttendance> memberBallotAttendances=new ArrayList<MemberBallotAttendance>();
        if(!status){
            List<Member> members=new ArrayList<Member>();
            CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP", "");
            if(customParameter!=null){
                SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
                if(session.getQuestionSubmissionFirstBatchStartDateUH()!=null&&session.getQuestionSubmissionFirstBatchEndDateUH()!=null){
                    String startTime=format.format(session.getQuestionSubmissionFirstBatchStartDateUH());
                    String endTime=format.format(session.getQuestionSubmissionFirstBatchEndDateUH());
                    String query="SELECT DISTINCT m FROM Question q JOIN q.primaryMember m JOIN m.title t WHERE q.session.id="+session.getId()+
                    " AND q.type.id="+questionType.getId()+" AND q.submissionDate>='"+startTime+"' AND q.submissionDate<='"+endTime+"'"+
                    " ORDER BY m.lastName "+ApplicationConstants.ASC;
                    members=this.em().createQuery(query).getResultList();
                    for(Member i:members){
                        MemberBallotAttendance memberBallotAttendance=new MemberBallotAttendance(session,questionType,i,false,locale);
                        memberBallotAttendance.persist();
                        memberBallotAttendances.add(memberBallotAttendance);
                    }
                    operationStatus=true;
                }else if(session.getQuestionSubmissionFirstBatchStartDateUH()==null){
                    logger.error("**** First Batch Submission Start Date not set ****");
                }else if(session.getQuestionSubmissionFirstBatchEndDateUH()==null){
                    logger.error("**** First Batch Submission End Date not set ****");
                }
            }else{
                logger.error("**** Custom Parameter 'DB_TIMESTAMP(yyyy-MM-dd HH:mm:ss)' not set ****");
            }
        }
        return operationStatus;
    }

    private Boolean memberBallotCreated(final Session session, final DeviceType questionType,
            final String locale) {
        String query="SELECT m FROM MemberBallotAttendance m WHERE m.session.id="+session.getId()+
        " AND m.deviceType="+questionType.getId()+" AND m.locale='"+locale+"'";
        Integer count=this.em().createQuery(query).getResultList().size();
        if(count>0){
            return true;
        }else{
            return false;
        }
    }
}
