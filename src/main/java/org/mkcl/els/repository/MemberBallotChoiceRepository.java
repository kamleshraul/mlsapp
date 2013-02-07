package org.mkcl.els.repository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallotChoice;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

@Repository
public class MemberBallotChoiceRepository extends BaseRepository<MemberBallotChoice, Serializable>{

    @SuppressWarnings("unchecked")
    public List<MemberBallotChoice> findByMember(final Session session,
            final DeviceType deviceType, final Member member, final String locale) {
        String query="SELECT mbc FROM MemberBallot mb JOIN mb.questionChoices mbc JOIN mb.session s JOIN mb.deviceType dt "+
                     " JOIN mb.member m WHERE s.id="+session.getId()+" AND dt.id="+deviceType.getId()+" AND m.id="+member.getId()+
                     " AND mb.locale='"+locale+"' ORDER BY mb.round,mb.position,mbc.choice";
        return this.em().createQuery(query).getResultList();
    }

	@SuppressWarnings("unchecked")
	public List<Question> findFirstBatchQuestions(final Session session,
			final DeviceType questionType,final Member member,final String pattern,
			final String orderby,final String sortorder,final String locale) {
		/**** Here we are assuming that submission time,start time,end time are all stored in same format in db ****/
		String startTime=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME_UH);
		String endTime=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME_UH);
		List<Question> questions=new ArrayList<Question>();
		if(startTime!=null&&endTime!=null){
			if((!startTime.isEmpty())&&(!endTime.isEmpty())){
					String query="SELECT q FROM Question q JOIN q.primaryMember m JOIN q.session s JOIN q.type qt  "+
					" WHERE m.id="+member.getId()+" AND s.id="+session.getId()+" AND qt.id="+questionType.getId()+
					" AND q.locale='"+locale+"' AND q.internalStatus.type='"+ApplicationConstants.QUESTION_FINAL_ADMISSION+"'  "+
					" AND q.submissionDate>='"+startTime+"' "+
					" AND q.submissionDate<='"+endTime+"' ";
					String questionsToExclude=null;
					if(pattern.toUpperCase().equals("PARTIAL")){
						questionsToExclude=" AND q.id NOT IN (SELECT sq.id FROM MemberBallot mb "+
						"JOIN mb.questionChoices qc JOIN qc.question sq WHERE mb.session.id="+session.getId()+
						" AND mb.deviceType.id="+questionType.getId()+" AND mb.member.id="+member.getId()+
						" AND mb.locale='"+locale+"') ";
					}else{
						questionsToExclude="";
					}
					String order=null;
					if(orderby.equals("chart_answering_date")){
						order="ORDER BY q.chartAnsweringDate.answeringDate "+sortorder+",q.number "+sortorder;
					}else{
						order="ORDER BY q.number "+sortorder;
					}
					String finalQuery=query+questionsToExclude+order;					
					questions=this.em().createQuery(finalQuery).getResultList();				
			}
		}
		return questions;
	}

}
