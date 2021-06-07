package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MemberBallotChoiceRevisionVO;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotChoice;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

@Repository
public class MemberBallotChoiceRepository extends BaseRepository<MemberBallotChoice, Serializable>{

    public List<MemberBallotChoice> findByMember(final Session session,
            final DeviceType deviceType, 
            final Member member, 
            final String locale) throws ELSException {
        String query="SELECT mbc FROM MemberBallot mb" +
        			" JOIN mb.questionChoices mbc" +
        			" JOIN mb.session s" +
        			" JOIN mb.deviceType dt " +
                    " JOIN mb.member m" +
                    " WHERE s.id=:sessionId" +
                    " AND dt.id=:deviceTypeId" +
                    " AND m.id=:memberId" +
                    " AND mb.locale=:locale ORDER BY mb.round,mb.position,mbc.choice";
        
        try{
	        TypedQuery<MemberBallotChoice> jpQuery = this.em().createQuery(query, MemberBallotChoice.class);
	        jpQuery.setParameter("sessionId", session.getId());
	        jpQuery.setParameter("deviceTypeId", deviceType.getId());
	        jpQuery.setParameter("memberId", member.getId());
	        jpQuery.setParameter("locale", locale);
	        
	        return jpQuery.getResultList();
        }catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotChoiceRepository_List<MemberBallotChoice>_findByMember", "No choice found.");
			throw elsException;
		}
    }

	@SuppressWarnings("unchecked")
	public List<Question> findFirstBatchQuestions(final Session session,
			final DeviceType questionType,
			final Member member,
			final String pattern,
			final String orderby,
			final String sortorder,
			final String locale) throws ELSException {
		
		try{
			/**** Here we are assuming that submission time,start time,end time are all stored in same format in db ****/
			String startTime=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME);
			String endTime=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
			
			List<Question> questions=new ArrayList<Question>();
			
			if(startTime!=null&&endTime!=null){
				if((!startTime.isEmpty())&&(!endTime.isEmpty())){
			
					String query="SELECT q FROM Question q" +
							" JOIN q.primaryMember m" +
							" JOIN q.session s" +
							" JOIN q.type qt  " +
							" WHERE m.id=:memberId" +
							" AND s.id=:sessionId" +
							" AND qt.id=:deviceTypeId" +
							" AND q.locale=:locale" +
							" AND q.internalStatus.type=:internalStatusType" +
							" AND q.submissionDate>=:startTime" +
							" AND q.submissionDate<=:endTime";
					
					    /**** PARTIAL is used in case of auto filling of question choices to get those questions which
					     * have not been entered as choice by member****/
					
						String questionsToExclude=null;
						if(pattern.toUpperCase().equals("PARTIAL")){
							questionsToExclude=" AND q.id NOT IN (SELECT sq.id FROM MemberBallot mb " +
							" JOIN mb.questionChoices qc" +
							" JOIN qc.question sq" +
							" WHERE mb.session.id=:sessionId" +
							" AND mb.deviceType.id=:deviceTypeId" +
							" AND mb.member.id=:memberId" +
							" AND mb.locale=:locale)";
						}else{
							questionsToExclude="";
						}
						
						String order=null;
						if(orderby.equals("chart_answering_date")){
							order=" ORDER BY q.chartAnsweringDate.answeringDate "+sortorder+",q.number "+sortorder;
						}else{
							order=" ORDER BY q.number "+sortorder;
						}
						
						String finalQuery=query+questionsToExclude+order;
						
						Query jpQuery = this.em().createQuery(finalQuery);
						jpQuery.setParameter("memberId", member.getId());
						jpQuery.setParameter("sessionId", session.getId());
						jpQuery.setParameter("deviceTypeId", questionType.getId());
						jpQuery.setParameter("locale", locale);
						jpQuery.setParameter("internalStatusType", ApplicationConstants.QUESTION_FINAL_ADMISSION);
						jpQuery.setParameter("startTime", FormaterUtil.formatStringToDate(startTime, ApplicationConstants.DB_DATETIME_FORMAT));
						jpQuery.setParameter("endTime", FormaterUtil.formatStringToDate(endTime, ApplicationConstants.DB_DATETIME_FORMAT));
						
						/*Set<Parameter<?>> parameters = jpQuery.getParameters();
						for(Parameter<?> p : parameters){
						
							if(p.getName().equals("sessionId_B")){
								jpQuery.setParameter("sessionId_B", session.getId());
							}else if(parameters.contains("deviceTypeId_B")){
								jpQuery.setParameter("deviceTypeId_B", questionType.getId());
							}else if(parameters.contains("memberId_B")){
								jpQuery.setParameter("memberId_B", member.getId());
							}else if(parameters.contains("locale_B")){
								jpQuery.setParameter("locale_B", locale);
							}
						}*/
						
						questions = jpQuery.getResultList();				
				}
			}
			return questions;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotChoiceRepository_List<Question>_findFirstBatchQuestions", "No question found.");
			throw elsException;
		}
	}

	@SuppressWarnings("unchecked")
	public MemberBallotChoice findMemberBallotChoice(final Session session,
			final DeviceType deviceType,final Member member,final int round,final int choice) {
		String strQuery="SELECT mbc FROM MemberBallot mb JOIN mb.questionChoices mbc "
					 +" WHERE mb.session.id=:session AND mb.deviceType.id=:deviceType AND mb.member.id=:member "
					 +" AND mb.round=:round AND mbc.choice=:choice";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("session", session.getId());
		query.setParameter("deviceType", deviceType.getId());
		query.setParameter("member", member.getId());
		query.setParameter("round", round);
		query.setParameter("choice",choice);
		List<MemberBallotChoice> results=query.getResultList();
		if(results!=null && ! results.isEmpty()){
			return results.get(0);
		}else{
			return null;
		}
	}

	public Boolean isQuestiongivenForChoice(Question question) {
		Boolean choiceFlag = false;
		String strQuery="SELECT mbc FROM MemberBallot mb "
				+ " JOIN mb.questionChoices mbc "
				+ " WHERE mb.session.id=:sessionId"
				+ " AND mbc.question.id=:questionId";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("sessionId", question.getSession().getId());
		query.setParameter("questionId", question.getId());
		List<MemberBallotChoice> results=query.getResultList();
		if(results!=null && ! results.isEmpty()){
			choiceFlag = true;
		}
		return choiceFlag;
	}
	
	@SuppressWarnings("unchecked")
	public MemberBallot findCorrespondingMemberBallot(final MemberBallotChoice choice) {
		String strQuery="SELECT mb FROM MemberBallot mb JOIN mb.questionChoices mbc "
					 +" WHERE mbc.id=:choiceId";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("choiceId",choice.getId());
		List<MemberBallot> results=query.getResultList();
		if(results!=null && ! results.isEmpty()){
			return results.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * Gets the revisions.
	 *
	 * @param memberId the member id
	 * @param sessionId the session id
	 * @param locale the locale
	 * @return the revisions
	 */
	@SuppressWarnings("rawtypes")
	public List<MemberBallotChoiceRevisionVO> getRevisions(final Long memberId, final Long sessionId, final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOTCHOICE_GET_REVISION", "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("memberId", memberId);
		query.setParameter("sessionId", sessionId);
		query.setParameter("locale", locale);
		List results = query.getResultList();
		List<MemberBallotChoiceRevisionVO> choiceRevisionVOs = new ArrayList<MemberBallotChoiceRevisionVO>();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			MemberBallotChoiceRevisionVO choiceRevisionVO = new MemberBallotChoiceRevisionVO();
			if(o[0]!=null) {
				choiceRevisionVO.setQuestionNumber(o[0].toString());
			} else {
				choiceRevisionVO.setQuestionNumber("");
			}
			if(o[1]!=null) {
				choiceRevisionVO.setRevisionsCount(Integer.parseInt(o[1].toString()));
			} else {
				choiceRevisionVO.setRevisionsCount(1);
			}
			if(o[2]!=null) {
				choiceRevisionVO.setEditedOn(o[2].toString());
			} else {
				choiceRevisionVO.setEditedOn("");
			}
			if(o[3]!=null) {
				choiceRevisionVO.setEditedAs(o[3].toString());
			} else {
				choiceRevisionVO.setEditedAs("");
			}
			if(o[4]!=null) {
				choiceRevisionVO.setEditedBY(o[4].toString());
			} else {
				choiceRevisionVO.setEditedBY("");
			}
			if(o[5]!=null) {
				choiceRevisionVO.setRound(o[5].toString());
			} else {
				choiceRevisionVO.setRound("");
			}
			if(o[6]!=null) {
				choiceRevisionVO.setChoice(o[6].toString());
			} else {
				choiceRevisionVO.setChoice("");
			}
			if(o[7]!=null) {
				choiceRevisionVO.setAnsweringDate(o[7].toString());
			} else {
				choiceRevisionVO.setAnsweringDate("");
			}
			if(o[8]!=null) {
				choiceRevisionVO.setIsAutoFilled(o[8].toString());
			} else {
				choiceRevisionVO.setIsAutoFilled("");
			}
			if(o[9]!=null) {
				choiceRevisionVO.setReasonForChoiceUpdate(o[9].toString());
			} else {
				choiceRevisionVO.setReasonForChoiceUpdate("");
			}
		}
		return choiceRevisionVOs;
	}

}