/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.BallotRepository.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Ballot;
import org.mkcl.els.domain.BallotEntry;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class BallotRepository
 * 
 * @author amitd
 * @since v1.0.0
 */
@Repository
public class BallotRepository extends BaseRepository<Ballot, Long> {

	public Ballot find(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException{
		/*Search search = new Search();
		search.addFilterEqual("session", session);
		search.addFilterEqual("deviceType", deviceType);
		search.addFilterEqual("answeringDate", answeringDate);
		search.addFilterEqual("locale", locale);
		Ballot ballot = this.searchUnique(search);*/
		//String answerDate = FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT);
		String query = "SELECT b FROM Ballot b" +
				" WHERE b.session.id=:sessionId" + 
				" AND b.deviceType.id=:deviceTypeId" +  
				" AND b.answeringDate=:answeringDate" + 
				" AND b.locale=:locale";
		Ballot ballot = null;
		try{
			TypedQuery<Ballot> jpQuery = em().createQuery(query, Ballot.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("answeringDate", answeringDate);
			jpQuery.setParameter("locale", locale);
		
			ballot = (Ballot)jpQuery.getSingleResult();
		}catch(NoResultException nre){
			nre.printStackTrace();
			logger.error(nre.getMessage());			
			ballot = null;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("BallotRepository_Ballot_find", "No ballot found.");
			throw elsException;	
		}
		
		return ballot;
	}

	public List<Question> findBallotedQuestions(final Member member,
			final Session session, 
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException{
		/**
		 * Commented for performance reason. Uncomment when Caching mechanism is added
		 */
		// CustomParameter parameter = 
		//	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String date = new DateFormater().formatDateToString(answeringDate, parameter.getValue());
		//String date = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		
		String strQuery = "SELECT q FROM Question q WHERE q.id IN(" +
							"  SELECT ds.device FROM Ballot b JOIN b.ballotEntries be JOIN be.deviceSequences ds" +
							" WHERE b.session.id = :sessionId" + 
							" AND b.deviceType.id = :deviceTypeId" + 
							" AND b.answeringDate = :answeringDate" + 
							" AND b.locale = :locale" +
							" AND be.member.id = :memberId" + ")";

		List<Question> questions = new ArrayList<Question>();
		try{
			TypedQuery<Question> jpQuery = this.em().createQuery(strQuery, Question.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("answeringDate", answeringDate);
			jpQuery.setParameter("locale", locale);
						
			List<Question> qX = jpQuery.getResultList();
			if(qX != null){
				questions = qX;
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("BallotRepository_Lis<Question>_findBallotedQuestions", "No balloted questions found.");
			throw elsException;			
		}
		
		return questions;
	}

	public BallotEntry find(final Member member,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException{
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = 
		// CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String date = new DateFormater().formatDateToString(answeringDate, parameter.getValue());

		String strQuery = "SELECT be" +
		" FROM Ballot b JOIN b.ballotEntries be" +
		" WHERE b.session.id = :sessionId" + 
		" AND b.deviceType.id = :deviceTypeId" + 
		" AND b.answeringDate = :answeringDate" + 
		" AND b.locale = :locale" + 
		" AND be.member.id = :memberId";

		BallotEntry ballotEntry = null;
		try{
			TypedQuery<BallotEntry> jpQuery = this.em().createQuery(strQuery, BallotEntry.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("answeringDate", answeringDate);
			jpQuery.setParameter("memberId", member.getId());
			jpQuery.setParameter("locale", locale);
				
			ballotEntry = jpQuery.getSingleResult();
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("BallotRepository_BallotEntry_find", "BallotEntry not found.");
			throw elsException;			
		}
		
		return ballotEntry;
	}

	public List<Member> findMembersEligibleForBallot(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Status internalStatus,
			final Status ballotStatus,
			final String locale) throws ELSException{
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = 
		// CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String date = new DateFormater().formatDateToString(answeringDate, parameter.getValue());
		// String date = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");

		String strQuery = "SELECT m" +
		" FROM Member m" +
		" WHERE m.id IN (" +
		" SELECT DISTINCT(q.primaryMember.id)" +
		" FROM Question q where q.id IN("+
		" SELECT d.id FROM Chart c" +
		" JOIN c.chartEntries ce" +
		" JOIN ce.devices d" +
		" WHERE c.session.id =:sessionId" +
		" AND c.group.id =:groupId" + 
		" AND c.answeringDate <= :answeringDate" + 
		" AND c.deviceType.id=:deviceTypeId" +
		" AND c.locale = :locale" + 
		" AND q.parent =" + null +  
		" AND q.internalStatus.id = :internalStatusId" + 
		" AND (" +
		" q.ballotStatus.id != :ballotStatusId" + 
		" OR" +
		" q.ballotStatus.id =" + null +  
		" )" +
		" ))" +
		" ORDER BY m.lastName ASC, m.firstName ASC";
		
		List<Member> members = new ArrayList<Member>();		
		try{	
			Query jpQuery = this.em().createQuery(strQuery, Member.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("groupId", group.getId());
			jpQuery.setParameter("answeringDate", answeringDate);
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("internalStatusId", internalStatus.getId());
			jpQuery.setParameter("ballotStatusId", ballotStatus.getId());
		
			List<Member> mX = jpQuery.getResultList();
			if(mX != null){
				members = mX;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("BallotRepository_List<Member>_findMembersEligibleForBallot", "No eligible member found.");
			throw elsException;	
		}
		return members;
	}

	public List<Question> findQuestionsEligibleForBallot(final Member member,
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Status internalStatus,
			final Status ballotStatus,
			final Integer noOfRounds,
			final String locale) throws ELSException{
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = 
		//	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String date = new DateFormater().formatDateToString(answeringDate, parameter.getValue());
		//String date = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		
		List<Question> questions = new ArrayList<Question>();
		try{
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("sessionId", new String[]{session.getId().toString()});
			parametersMap.put("groupId", new String[]{group.getId().toString()});
			parametersMap.put("answeringDate", new String[]{answeringDate.toString()});
			parametersMap.put("memberId", new String[]{member.getId().toString()});
			parametersMap.put("internalStatusId", new String[]{internalStatus.getId().toString()});
			parametersMap.put("ballotStatusId", new String[]{ballotStatus.getId().toString()});			
			
			List<Question> qX = org.mkcl.els.domain.Query.findResultListOfGivenClass("QIS_ELIGIBLE_QUESTIONS_FOR_STARRED_ASSEMBLY_BALLOT", parametersMap, Question.class);
			
			if(qX != null && !qX.isEmpty() && noOfRounds!=null && noOfRounds>0){
				for(int i=0; i<qX.size(); i++) {
					if(i==noOfRounds) {
						break;
					}
					questions.add(qX.get(i));			
				}				
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("BallotRepository_List<Question>_findQuestionsEligibleForBallot", "No eligible question found.");
			throw elsException;	
		}
		return questions;
	}

	public void updateBallotQuestions(final Ballot ballot,
			final Status ballotStatus) throws ELSException{
		Session session = ballot.getSession();
		DeviceType deviceType = ballot.getDeviceType();
		Date answeringDate = ballot.getAnsweringDate();
		String locale = ballot.getLocale();

//		CustomParameter parameter =
//			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
//		String date = FormaterUtil.formatDateToString(answeringDate, parameter.getValue());

//		String query = "UPDATE questions" +
//		" SET ballotstatus_id = " + ballotStatus.getId() +
//		" WHERE id IN (" +
//		" SELECT qid FROM (" +
//		" SELECT q.id AS qid" +
//		" FROM ballots AS b JOIN ballots_ballot_entries AS bbe" +
//		" JOIN ballot_entries_question_sequences AS beqs" +
//		" JOIN question_sequences AS qs JOIN questions AS q" +
//		" WHERE b.id = bbe.ballot_id" +
//		" AND bbe.ballot_entry_id = beqs.ballot_entry_id" +
//		" AND beqs.question_sequence_id = qs.id" +
//		" AND qs.question_id = q.id" +
//		" AND b.session_id = " + session.getId() +
//		" AND b.devicetype_id = " + deviceType.getId() +
//		" AND b.answering_date = '" + date + "'" +
//		" AND b.locale = '" + locale + "'" +
//		" ) AS rs" +
//		" )";
		
		/*org.mkcl.els.domain.Query query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "BALLOT_UPDATE_BALLOT_QUESTIONS", locale);
		if(query != null){
			Query executeQuery = this.em().createNativeQuery(query.getQuery());
			executeQuery.setParameter("ballotStatusId", ballotStatus.getId());
			executeQuery.setParameter("sessionId", session.getId());
			executeQuery.setParameter("deviceTypeId", deviceType.getId());
			executeQuery.setParameter("answeringDate", "'"+date+"'");
			executeQuery.setParameter("locale", "'"+locale+"'");
			
			executeQuery.executeUpdate();
			
		}*/
		
		String query = "UPDATE questions" +
				" SET ballotstatus_id =:ballotStatusID" + 
				" WHERE id IN (" +
				" SELECT qid FROM (" +
				" SELECT q.id AS qid FROM questions q WHERE q.id IN (" +
				" SELECT ds.device_id FROM ballots AS b JOIN ballots_ballot_entries AS bbe" +
				" JOIN ballot_entries_device_sequences AS beqs" +
				" JOIN device_sequences AS ds" +
				" WHERE b.id = bbe.ballot_id" +
				" AND bbe.ballot_entry_id = beqs.ballot_entry_id" +
				" AND beqs.device_sequence_id = ds.id" +
				" AND b.session_id =:sessionId" + 
				" AND b.devicetype_id = :deviceTypeId" + 
				" AND b.answering_date =:answeringDate" + 
				" AND b.locale =:locale" +
				" )) AS rs" +
				" )";
		
		try{
			Query jpQuery = this.em().createNativeQuery(query);
			jpQuery.setParameter("ballotStatusID", ballotStatus.getId());
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("answeringDate", answeringDate);
			jpQuery.setParameter("locale", locale);		
		
			jpQuery.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("BallotRepository_void_updateBallotQuestions", "Cann't update the ballot questions.");
			throw elsException;	
		}
	}

	public String createBallot(final Session session,final DeviceType deviceType,
			final Boolean attendance,final String locale) throws ELSException{
		String flag=null;
		try{
			House house=session.getHouse();
			HouseType houseType=house.getType();
			/**** DEVICETYPE_HOUSETYPE_BALLOT****/
			CustomParameter ballotType=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_BALLOT", "");
			if(ballotType!=null){
				if(ballotType.getValue().equals("MEMBER_BALLOT_FROM_PRESENT_MEMBERS")){
					flag=memberBallotFromPresentMembers(session,deviceType,locale);
				}		
			}else{
				flag="CUSTOM_PARAMETER_NOT_SET";
			}
		}catch(Exception ex){
			logger.error("Member Attendance creation failed.",ex);
			flag="DB_EXCEPTION";
		}
		return flag;
	}

	private String memberBallotFromPresentMembers(final Session session,
			final DeviceType deviceType,final String locale) throws ELSException{
		String query="SELECT m FROM MemberBallot m WHERE m.locale=:locale" +
						" AND m.session.id=:sessionId AND m.deviceType.id=:deviceTypeId";
		
		Integer count = null;
		
		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("locale", locale);
		
			count = jpQuery.getResultList().size();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("BallotRepository_String_memberBallotFromPresentMembers", ".");
			throw elsException;	
		}
		if(count>0){
			return "SUCCESS";
		}
		List<Member> members=MemberBallotAttendance.findMembersByAttendance(session, deviceType, true, locale);
		Collections.shuffle(members);
		int order=1;
		Date date=new Date();
		for(Member i:members){
			MemberBallot memberBallot=new MemberBallot();
			memberBallot.setSession(session);
			memberBallot.setDeviceType(deviceType);
			memberBallot.setMember(i);
			memberBallot.setBallotDate(date);
			memberBallot.setPosition(order);
			memberBallot.setAttendance(true);
			memberBallot.setLocale(locale);
			memberBallot.persist();
			order++;
		}
		return "SUCCESS";
	}

	public List<Reference> viewBallot(final Session session,final DeviceType deviceType,
			final Boolean attendance,final String locale) {
		List<Reference> references=new ArrayList<Reference>();
		try{
			House house=session.getHouse();
			HouseType houseType=house.getType();
			/**** DEVICETYPE_HOUSETYPE_BALLOT****/
			CustomParameter ballotType=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_BALLOT", "");
			if(ballotType!=null){
				if(ballotType.getValue().equals("MEMBER_BALLOT_FROM_PRESENT_MEMBERS")){
					references=memberBallotFromPresentMembersRef(session,deviceType,locale);
				}		
			}
		}catch(Exception ex){
			logger.error("Member Attendance creation failed.",ex);
		}
		return references;
	}

	private List<Reference> memberBallotFromPresentMembersRef(final Session session,
			final DeviceType deviceType, final String locale) throws ELSException {
		String query="SELECT m FROM MemberBallot m WHERE m.locale=:locale" +
						" AND m.session.id=:sessionId AND m.deviceType.id=:deviceTypeId";
		
		List<Reference> references=new ArrayList<Reference>();
		try{
			TypedQuery<MemberBallot> jpQuery = this.em().createQuery(query, MemberBallot.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("locale", locale);
			
			List<MemberBallot> ballots = jpQuery.getResultList();
			
			
			for(MemberBallot i:ballots){
				Reference reference=new Reference(String.valueOf(i.getPosition()),i.getMember().getFullname());
				references.add(reference);
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("BallotRepository_List<Reference>_memberBallotFromPresentMembersRef", "NO ballot of present members found.");
			throw elsException;	
		}
		return references;
	}	
}
