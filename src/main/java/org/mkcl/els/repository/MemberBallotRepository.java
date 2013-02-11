package org.mkcl.els.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MemberBallotFinalBallotQuestionVO;
import org.mkcl.els.common.vo.MemberBallotFinalBallotVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseCountVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseQuestionVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseReportVO;
import org.mkcl.els.common.vo.MemberBallotQuestionDistributionVO;
import org.mkcl.els.common.vo.MemberBallotQuestionVO;
import org.mkcl.els.common.vo.MemberBallotVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.MemberBallotChoice;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberBallotRepository extends BaseRepository<MemberBallot, Serializable>{

	/*********************************** Member Ballot ************************************************/
	public String createMemberBallot(final Session session,final DeviceType deviceType,final Boolean attendance,final Integer round,final String locale){
		try {
			Search search=new Search();
			search.addFilterEqual("session.id",session.getId());
			search.addFilterEqual("deviceType.id",deviceType.getId());
			search.addFilterEqual("round",round);
			search.addFilterEqual("attendance",attendance);
			search.addFilterEqual("locale",locale);
			int count=this.count(search);
			if(count>0){
				/**** Then we will decide if to delete the existing entries and do fresh balloting 
				 * or to not allow  fresh balloting ****/
				CustomParameter operationOnExistingEntries=CustomParameter.findByName(CustomParameter.class,"MEMBERBALLOT_DELETE_EXISTING_MEMBERBALLOT"+round, "");
				Boolean freshBallotAllowed=false;
				if(operationOnExistingEntries!=null){
					String operation=operationOnExistingEntries.getValue();
					if(operation.toUpperCase().equals("DELETE")){
						String deleteMemberBallots="DELETE FROM MemberBallot m WHERE m.session.id="+session.getId()+
						" AND m.deviceType.id="+deviceType.getId()+
						" AND m.locale='"+locale+"'"+
						" AND m.round="+round+
						" AND m.attendance="+attendance;
						this.em().createQuery(deleteMemberBallots).executeUpdate();
						freshBallotAllowed=true;
					}
				}
				if(freshBallotAllowed){
					return memberBallot(session,deviceType,attendance,round,locale);
				}else{
					return "SUCCESS";
				}
			}else{
				return memberBallot(session,deviceType,attendance,round,locale);
			}
		} catch (Exception e) {
			logger.error("FAILED",e);
			return "FAILED";
		}
	}

	public String memberBallot(Session session,DeviceType deviceType,Boolean attendance,int round,String locale){
		String flag=isMemberBallotAllowed(session,deviceType,attendance,round,locale);
		List<Member> input=new ArrayList<Member>();
		if(flag.equals("ALLOWED")){
			int order=1;
			if(!attendance){
				/**** Controlling how to set the positions of absent members ****/
				CustomParameter absentMemberPosition=CustomParameter.findByName(CustomParameter.class,"MEMBERBALLOT_ABSENTMEMBERS_POSITION_STARTAT", "");
				if(absentMemberPosition!=null){
					if(absentMemberPosition.getValue().toUpperCase().equals("BEGINING")){
						order=1;
					}else{
						order=noOfRecordsInMemberBallot(session,deviceType,true,round,locale)+1;
					}
				}else{
					order=noOfRecordsInMemberBallot(session,deviceType,true,round,locale)+1;
				}
			}	
			/*****  Controlling how to randomize the input *******/
			if(round==1){
				input=MemberBallotAttendance.findMembersByAttendance(session,deviceType,attendance,round,locale);
				Collections.shuffle(input);
			}else{
				CustomParameter randomizeInput=CustomParameter.findByName(CustomParameter.class,"MEMBERBALLOT_RANDOMIZE_CUSTOMALGORITHM", "");
				if(randomizeInput!=null){
					if(randomizeInput.getValue().toUpperCase().equals("YES")){
						input=customRandomization(session,deviceType,attendance,round,locale);
					}else{
						input=MemberBallotAttendance.findMembersByAttendance(session,deviceType,attendance,round,locale);
						Collections.shuffle(input);
					}					
				}else{
					input=MemberBallotAttendance.findMembersByAttendance(session,deviceType,attendance,round,locale);
					Collections.shuffle(input);
				}
			}
			Date date=new Date();
			for(Member i:input){
				MemberBallot memberBallot=new MemberBallot();
				memberBallot.setSession(session);
				memberBallot.setDeviceType(deviceType);
				memberBallot.setMember(i);
				memberBallot.setBallotDate(date);
				memberBallot.setRound(round);
				memberBallot.setPosition(order);
				memberBallot.setAttendance(attendance);
				memberBallot.setLocale(locale);
				memberBallot.persist();
				order++;
			}
			String query="UPDATE MemberBallotAttendance m SET locked=true WHERE m.session.id="+session.getId()+
			" AND m.deviceType.id="+deviceType.getId()+" AND m.attendance="+attendance+
			" AND m.round="+round+" AND m.locale='"+locale+"'";
			this.em().createQuery(query).executeUpdate();

			return "SUCCESS";
		}
		return flag;
	}

	private List<Member> customRandomization(Session session,
			DeviceType deviceType, Boolean attendance, int round, String locale) {
		List<Member> finalMembers=new ArrayList<Member>();
		List<Member> uniqueMembers=new ArrayList<Member>();
		List<Member> nonUniqueMembers=new ArrayList<Member>();
		if(round!=1){
			Integer noOfMembersPreviousRound=MemberBallotAttendance.findMembersByAttendanceCount(session,deviceType,attendance,round-1,locale);
			int firstHalvesSize=(int) Math.ceil((noOfMembersPreviousRound/2));
			int secondHalvesSize=noOfMembersPreviousRound-firstHalvesSize;
			List<Member> firstHalfMembers=findMembersByPosition(session,deviceType,attendance,round-1,locale,0,firstHalvesSize);
			List<Member> secondHalfMembers=findMembersByPosition(session,deviceType,attendance,round-1,locale,firstHalvesSize,secondHalvesSize);
			List<Member> newMembers=MemberBallotAttendance.findNewMembers(session,deviceType,attendance,round,locale);
			if(secondHalfMembers!=null){
				if(newMembers!=null){
					if(!newMembers.isEmpty()){
					secondHalfMembers.addAll(newMembers);
					}
				}
				Collections.shuffle(secondHalfMembers);
				/**** Check How Many Positions Cannot Be Same ****/
				int noOfUniquePositions=2;
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"MEMBERBALLOT_NOOFUNIQUESPOSITIONS", "");
				if(customParameter!=null){
					try {
						noOfUniquePositions=Integer.parseInt(customParameter.getValue());
					} catch (NumberFormatException e) {
						logger.error("Invalid Value Of Custom Parameter 'MEMBERBALLOT_NOOFUNIQUESPOSITIONS'",e);
					}					
				}				
				for(Member i:secondHalfMembers){
					Boolean unique=membersNotPrsentAtPositionX(session,deviceType,attendance,round,i,noOfUniquePositions,locale);
					if(!unique){
						nonUniqueMembers.add(i);
					}else{
						uniqueMembers.add(i);
					}
				}
				
			}
			Collections.shuffle(firstHalfMembers);
			finalMembers.addAll(uniqueMembers);
			finalMembers.addAll(nonUniqueMembers);
			finalMembers.addAll(firstHalfMembers);			
		}
		return finalMembers;
	}

	private Boolean membersNotPrsentAtPositionX(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final Member membersAtPositionX,final int noOfUniquePositions,
			final String locale) {
		String query="SELECT COUNT(mb.id) FROM MemberBallot mb"+
		" WHERE mb.session.id="+session.getId()+" AND mb.deviceType.id="+deviceType.getId()+
		" AND mb.attendance="+attendance+" AND mb.locale='"+locale+"' AND mb.member.id="+membersAtPositionX.getId();
		StringBuffer roundQuery=new StringBuffer();
		for(int i=1;i<round;i++){
			roundQuery.append("mb.round="+i+" OR ");
		}
		roundQuery.delete(roundQuery.length()-3,roundQuery.length()-1);
		StringBuffer positionQuery=new StringBuffer();
		for(int i=1;i<=noOfUniquePositions;i++){
			positionQuery.append("mb.position="+i+" OR ");
		}
		positionQuery.delete(positionQuery.length()-3,positionQuery.length()-1);		
		String finalQuery=query+" AND ("+roundQuery.toString()+") AND ("+positionQuery.toString()+")";
		Long count=(Long) this.em().createQuery(finalQuery).getSingleResult();
		if(count==0){
			return true;
		}else{
			return false;
		}	
	}

	private int noOfRecordsInMemberBallot(Session session,
			DeviceType deviceType, boolean attendance, int round, String locale) {
		Search search=new Search();
		search.addFilterEqual("session.id",session.getId());
		search.addFilterEqual("deviceType.id",deviceType.getId());
		search.addFilterEqual("locale",locale);
		search.addFilterEqual("attendance",attendance);
		search.addFilterEqual("round",round);
		return this.count(search);
	}

	private String isMemberBallotAllowed(Session session, DeviceType deviceType,
			Boolean attendance, int round, String locale) {
		if(attendance){
			if(round!=1){
				Boolean status=MemberBallotAttendance.areMembersLocked(session, deviceType, round-1, attendance,locale);
				if(status){
					return "ALLOWED";
				}else{
					return "PREVIOUSROUND_PRESENTLIST_NOT_LOCKED";
				}
			}else{
				return "ALLOWED";
			}
		}else{
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"MEMBERBALLOT_ABSENTBALLOT_AFTER_ALL_PRESENTBALLOT", "");
			if(customParameter!=null){
				if(customParameter.getValue().toUpperCase().equals(ApplicationConstants.MEMBERBALLOT_ABSENTBALLOT_AFTER_ALL_PRESENTBALLOT)){
					String strNoOfRounds=session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_UH);
					if(strNoOfRounds!=null){
						if(!strNoOfRounds.isEmpty()){
							int noOfRounds=Integer.parseInt(strNoOfRounds);
							Boolean presentStatus=true;
							for(int i=1;i<=noOfRounds;i++){
								presentStatus=MemberBallotAttendance.areMembersLocked(session,deviceType,i, true, locale);
								if(!presentStatus){
									return "PRESENT_MEMBERS_FOR_ALL_ROUNDS_NOT_LOCKED";
								}
							}
							if(presentStatus){
								if(round!=1){
									presentStatus=MemberBallotAttendance.areMembersLocked(session,deviceType,round-1, attendance, locale);
								}
							}
							if(presentStatus){
								return "ALLOWED";
							}else{
								return "ABSENT MEMBER FOR PREVIOUS ROUND NOT LOCKED";
							}
						}else{
							return "NOOFROUNDS_IN_MEMBERBALLOT_NOTSET";
						}
					}else{
						return "NOOFROUNDS_IN_MEMBERBALLOT_NOTSET";
					}
				}
			}else{
				return "MEMBERBALLOT_ABSENTBALLOT_AFTER_ALL_PRESENTBALLOT_NOTSET";
			}
		}
		return "NOT ALLOWED";
	}

	public List<MemberBallot> findByMember(final Session session,
			final DeviceType deviceType, final Member member, final String locale) {
		Search search=new Search();
		search.addFilterEqual("session.id",session.getId());
		search.addFilterEqual("deviceType.id",deviceType.getId());
		search.addFilterEqual("member.id",member.getId());
		search.addFilterEqual("locale",locale);
		search.addSort("round",false);
		search.addSort("position",false);
		return this.search(search);
	}

	public MemberBallot findByMemberRound(final Session session,
			final DeviceType questionType, final Member member, final int round, final String locale) {
		Search search=new Search();
		search.addFilterEqual("session.id",session.getId());
		search.addFilterEqual("deviceType.id",questionType.getId());
		search.addFilterEqual("member.id",member.getId());
		search.addFilterEqual("round",round);
		search.addFilterEqual("locale",locale);
		return this.searchUnique(search);
	}


	public List<MemberBallotVO> viewMemberBallotVO(final Session session, final DeviceType deviceType,final Boolean attendance,
			final int round, final String locale) {
		return getMemberBallotVOs(session.getId(), deviceType.getId(), attendance, round, new Long(0),
				new Long(0), locale);
	}

	public List<MemberBallotVO> viewMemberBallotVO(final Session session, final DeviceType deviceType,final Boolean attendance,
			final int round,final Group group, final String locale) {
		return getMemberBallotVOs(session.getId(), deviceType.getId(), attendance, round, group.getId(),
				new Long(0), locale);
	}

	public List<MemberBallotVO> viewMemberBallotVO(final Session session, final DeviceType deviceType,final Boolean attendance,
			final int round,final Group group,final QuestionDates answeringDate, final String locale) {
		return getMemberBallotVOs(session.getId(), deviceType.getId(), attendance, round,group.getId(),
				answeringDate.getId(), locale);
	}

	@SuppressWarnings("rawtypes")
	public List<MemberBallotVO> getMemberBallotVOs(final Long session,
			final Long deviceType, final boolean attendance, final int round,
			final Long group,final Long answeringDate, final String locale){
		List<MemberBallotVO> ballots=new ArrayList<MemberBallotVO>();
		try {
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_DATEFORMAT", "");
			NumberFormat numberFormat=FormaterUtil.getNumberFormatterNoGrouping(locale);
			SimpleDateFormat dbFormat=FormaterUtil.getDateFormatter(customParameter.getValue(), locale);
			SimpleDateFormat format=FormaterUtil.getDateFormatter(locale);
			/**** Member Ballot Entries ****/
			String memberBallotQuery="SELECT mb.id as memberballotid,mb.position as position,m.id as memberid"+
			",concat(t.name,' ',m.first_name,' ',m.middle_name,' ',m.last_name) as membername"+
			",mb.attendance as attendance,mb.round as round"+
			" FROM memberballot as mb JOIN members as m JOIN titles as t WHERE"+
			" mb.member=m.id AND m.title_id=t.id"+
			" AND mb.session="+session+" AND mb.device_type="+deviceType+
			" AND mb.round="+round+" AND mb.attendance="+attendance+" AND mb.locale='"+locale+"'"+
			" ORDER BY mb.position";	
			String query="SELECT rs.memberballotid,rs.position,rs.memberid,rs.membername,rs.attendance,rs.round FROM("+memberBallotQuery+") as rs";
			List memberBallots=this.em().createNativeQuery(query).getResultList();
			StringBuffer buffer=new StringBuffer();
			for(Object i:memberBallots){
				Object[] o=(Object[]) i;
				MemberBallotVO memberBallotVO=new MemberBallotVO();
				if(o[0]!=null){
					memberBallotVO.setId(o[0].toString());	
					buffer.append(o[0].toString()+",");
				}
				if(o[1]!=null){
					if(!o[1].toString().isEmpty()){
						memberBallotVO.setPosition(numberFormat.format(Integer.parseInt(o[1].toString())));
					}
				}
				if(o[2]!=null){
					memberBallotVO.setMemberId(o[2].toString());
				}
				if(o[3]!=null){
					memberBallotVO.setMember(o[3].toString());
				}
				if(o[4]!=null){
					memberBallotVO.setAttendance(o[4].toString());
				}
				if(o[5]!=null){
					memberBallotVO.setRound(o[5].toString());
				}
				ballots.add(memberBallotVO);
			}	
			/**** Checking If Question Choices Have Been Filled ****/
			String memberChoiceCreatedQuery=null;
			if(!buffer.toString().isEmpty()){
				buffer.deleteCharAt(buffer.length()-1);
				memberChoiceCreatedQuery="SELECT COUNT(*) FROM memberballot_choice_association as mbc"+
				" WHERE memberballot_id IN("+buffer.toString()+")";
			}else{
				return ballots;
			}			
			BigInteger count=(BigInteger) this.em().createNativeQuery(memberChoiceCreatedQuery).getSingleResult();
			if(count.equals(0)){
				return ballots;
			}
			/**** Populating Question Choices ****/
			for(MemberBallotVO i:ballots){
				List<MemberBallotQuestionVO> questionVOs=new ArrayList<MemberBallotQuestionVO>();
				String questionChoiceQuery="SELECT q.id as questionid,q.number as questionnumber,g.number as groupnumber"+
				" ,p.id as parentid,p.number as parentnumber,qd.answering_date as answeringdate"+
				" FROM memberballot_choice as mbc"+
				" JOIN questions as q JOIN question_dates as qd"+
				" LEFT JOIN questions as p ON (p.id=q.parent)"+
				" JOIN groups as g JOIN memberballot_choice_association as mbca"+
				" WHERE mbca.memberballot_choice_id=mbc.id"+
				" AND mbc.question=q.id AND mbc.new_answering_date=qd.id"+
				" AND q.group_id=g.id"+
				" AND mbca.memberballot_id="+i.getId();
				String strAnsDateQuery="";
				if(answeringDate!=0){
					strAnsDateQuery=" AND qd.id="+answeringDate;
				}
				String strGroupQuery="";
				if(group!=0){
					strGroupQuery=" AND g.id="+group;
				}
				String orderBy=" ORDER BY mbc.choice";
				String innerQuery=questionChoiceQuery+strGroupQuery+strAnsDateQuery+orderBy;
				String finalQuery="SELECT rs.questionid,rs.questionnumber,rs.groupnumber,rs.parentid,rs.parentnumber,"+
				"rs.answeringdate FROM ("+innerQuery+") as rs";
				List result=this.em().createNativeQuery(finalQuery).getResultList();
				for(Object j:result){
					MemberBallotQuestionVO questionVO=new MemberBallotQuestionVO();
					Object[] o=(Object[]) j;
					if(o[0]!=null){
						questionVO.setId(o[0].toString());
					}
					if(o[1]!=null){
						if(!o[1].toString().isEmpty()){
							questionVO.setNumber(numberFormat.format(Integer.parseInt(o[1].toString())));
						}
					}
					if(o[2]!=null){
						if(!o[2].toString().isEmpty()){
							questionVO.setGroup(numberFormat.format(Integer.parseInt(o[2].toString())));
						}
					}
					if(o[3]!=null){
						questionVO.setParentId(o[3].toString());
					}
					if(o[4]!=null){
						questionVO.setParentNumber(numberFormat.format(Integer.parseInt(o[4].toString())));
					}
					if(o[5]!=null){
						if(!o[5].toString().isEmpty()){
							Date dbDate=dbFormat.parse(o[5].toString());						
							questionVO.setAnsweringDate(format.format(dbDate));
						}
					}		
					questionVOs.add(questionVO);
				}
				i.setQuestions(questionVOs);
			}			
		} catch (NumberFormatException e) {
			logger.error("Number Format Exception",e);
			return ballots;
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return ballots;
		}
		return ballots;
	}	

	public Integer findPrimaryCount(final Session session, final DeviceType deviceType,
			final String locale) {
		String query="SELECT q FROM MemberBallot mb JOIN mb.questionChoices qc JOIN qc.question q"+
		" WHERE mb.session.id="+session.getId()+" AND mb.deviceType.id="+deviceType.getId()+" "+
		" AND mb.locale='"+locale+"' AND q.parent=null";
		Integer count=this.em().createQuery(query).getResultList().size();
		return count;
	}

	/***************************** Member Ballot Update Clubbing ********************************/
	@SuppressWarnings("unused")
	public Boolean updateClubbing(final Session session, final DeviceType deviceType,
			final int start, final int size, final String locale) {
		int count=this.em().createNativeQuery("call "+ApplicationConstants.CLUBBING_UPDATE_PROCEDURE+"(?,?,?,?,?)").setParameter(1,session.getId()).setParameter(2,deviceType.getId()).setParameter(3,start).setParameter(4,size).setParameter(5,locale).executeUpdate();
		return true;
	}

	public Boolean deleteTempEntries() {
		try {
			this.em().createNativeQuery("call "+ApplicationConstants.DELETE_TEMP_PROCEDURE).executeUpdate();
			return true;
		}
		catch (Exception e) {
			logger.error("TEMP ENTRIES DELETE FAILED",e);
			return false;
		}
	}

	/****************************** Member Ballot Final Ballot **********************************/
	public Boolean createFinalBallot(final Session session,final DeviceType deviceType,final Group group,
			final String answeringDate,final String locale,
			final String firstBatchSubmissionEndDate,final int totalRounds) {
		/**** Ballot for a particular answering date can be created only once ****/
		Boolean status=ballotAlreadyCreated(session,deviceType,group,answeringDate,locale);
		if(!status){
			try {
				/**** This controls the horizontal/vertical scanning of previous answering dates 
				 * questions ****/
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"MEMBERBALLOT_FINALBALLOT_HORIZONTALSCAN", "");
				if(customParameter!=null){
					if(customParameter.getValue().equals("NO")){
						this.em().createNativeQuery("call "+ApplicationConstants.MEMBERBALLOT_FINALBALLOT_VERTICAL_PROCEDURE+"(?,?,?,?,?,?,?)")
						.setParameter(1,session.getId()).setParameter(2,deviceType.getId())
						.setParameter(3,group.getId()).setParameter(4,answeringDate)
						.setParameter(5,locale).setParameter(6,firstBatchSubmissionEndDate)
						.setParameter(7,totalRounds).executeUpdate();
					}else{
						this.em().createNativeQuery("call "+ApplicationConstants.MEMBERBALLOT_FINALBALLOT_HORIZONTAL_PROCEDURE+"(?,?,?,?,?,?,?)")
						.setParameter(1,session.getId()).setParameter(2,deviceType.getId())
						.setParameter(3,group.getId()).setParameter(4,answeringDate)
						.setParameter(5,locale).setParameter(6,firstBatchSubmissionEndDate)
						.setParameter(7,totalRounds).executeUpdate();
					}
				}else{
					this.em().createNativeQuery("call "+ApplicationConstants.MEMBERBALLOT_FINALBALLOT_HORIZONTAL_PROCEDURE+"(?,?,?,?,?,?,?)")
					.setParameter(1,session.getId()).setParameter(2,deviceType.getId())
					.setParameter(3,group.getId()).setParameter(4,answeringDate)
					.setParameter(5,locale).setParameter(6,firstBatchSubmissionEndDate)
					.setParameter(7,totalRounds).executeUpdate();

				}
				/**** Updating the sequence no of recently created ballot entries ****/
				this.em().createNativeQuery("call "+ApplicationConstants.FINAL_BALLOT_UPDATE_SEQUENCE_PROCEDURE+"(?,?,?,?,?,?)").
				setParameter(1,session.getId()).setParameter(2,deviceType.getId()).
				setParameter(3,group.getId()).setParameter(4,answeringDate).
				setParameter(5,locale).setParameter(6,firstBatchSubmissionEndDate).executeUpdate();
				return true;
			}
			catch (Exception e) {
				logger.error("FINAL BALLOT FAILED",e);
				return false;
			}
		}else{
			return true;
		}		
	}

	private Boolean ballotAlreadyCreated(final Session session,final DeviceType deviceType,
			final Group group,final String answeringDate,final String locale) {
		String query="SELECT COUNT(id) FROM ballots"+
		" WHERE session_id="+session.getId()+
		" AND devicetype_id="+deviceType.getId()+
		" AND group_id="+group.getId()+
		" AND answering_date='"+answeringDate+"'"+
		" AND locale='"+locale+"'";
		BigInteger count=(BigInteger) this.em().createNativeQuery(query).getSingleResult();
		if(count.compareTo(BigInteger.valueOf(0))==0){
			return false;
		}else{
			return true;
		}		
	}

	@SuppressWarnings("rawtypes")
	public List<MemberBallotFinalBallotVO> viewBallot(final Session session,
			final DeviceType deviceType,final String answeringDate,final String locale){
		List<MemberBallotFinalBallotVO> ballots=new ArrayList<MemberBallotFinalBallotVO>();
		NumberFormat numberFormat=FormaterUtil.getNumberFormatterNoGrouping(locale);
		String query="SELECT be.position as position,concat(t.name,' ',m.first_name,' ',m.last_name) as member"+
		",be.id as ballotentryid"+
		" FROM ballots as b"+
		" JOIN ballots_ballot_entries as bbe"+
		" JOIN ballot_entries as be"+
		" JOIN members as m"+
		" JOIN titles as t"+
		" WHERE bbe.ballot_id=b.id"+
		" AND bbe.ballot_entry_id=be.id"+
		" AND be.member_id=m.id"+
		" AND m.title_id=t.id"+
		" AND b.session_id="+session.getId()+
		" AND b.devicetype_id="+deviceType.getId()+
		" AND b.answering_date='"+answeringDate+"'"+
		" AND b.locale='"+locale+"'"+
		" ORDER BY be.position";
		String ballotEntryQuery="SELECT rs.position,rs.member,rs.ballotentryid FROM ("+query+") as rs";
		List ballotEntries=this.em().createNativeQuery(ballotEntryQuery).getResultList();
		for(Object i:ballotEntries){
			Object[] o=(Object[]) i;
			MemberBallotFinalBallotVO ballot=new MemberBallotFinalBallotVO();
			if(o[0]!=null){
				if(!o[0].toString().isEmpty()){
					ballot.setBallotSno(numberFormat.format(Integer.parseInt(o[0].toString())));
				}
			}
			if(o[1]!=null){
				ballot.setMember(o[1].toString());
			}
			if(o[2]!=null){
				ballot.setBallotEntryId(o[2].toString());
			}
			ballots.add(ballot);
		}
		if(!ballots.isEmpty()){
			for(MemberBallotFinalBallotVO i:ballots){
				String questionQuery="SELECT q.id as questionid,q.number as questionnumber"+
				",qs.sequence_no as sequenceno"+
				",mb.round as round,mb.attendance as attendance FROM"+
				" question_sequences as qs"+
				" LEFT JOIN ballot_entries_question_sequences as beqs ON(beqs.question_sequence_id=qs.id)"+
				" LEFT JOIN ballot_entries as be ON(beqs.ballot_entry_id=be.id)"+
				" LEFT JOIN memberballot_choice as mc ON(qs.question_id=mc.question)"+
				" LEFT JOIN memberballot_choice_association as mca ON(mca.memberballot_choice_id=mc.id)"+
				" LEFT JOIN memberballot as mb ON(mca.memberballot_id=mb.id)"+
				" LEFT JOIN questions as q ON(qs.question_id=q.id)"+
				" WHERE be.id="+i.getBallotEntryId()+
				" ORDER BY qs.sequence_no";
				String finalQuestionQuery="SELECT rs.questionid,rs.questionnumber,"+
				"rs.sequenceno,rs.round,rs.attendance FROM("+questionQuery+") as rs";
				List questionEntries=this.em().createNativeQuery(finalQuestionQuery).getResultList();
				List<MemberBallotFinalBallotQuestionVO> questions=new ArrayList<MemberBallotFinalBallotQuestionVO>();
				for(Object j:questionEntries){
					Object[] o=(Object[]) j;
					MemberBallotFinalBallotQuestionVO question=new MemberBallotFinalBallotQuestionVO();
					if(o[0]!=null){
						question.setQuestionId(o[0].toString());
					}
					if(o[1]!=null){
						if(!o[1].toString().isEmpty()){
							question.setQuestionNumber(numberFormat.format(Integer.parseInt(o[1].toString())));
						}
					}
					if(o[2]!=null){
						if(!o[2].toString().isEmpty()){
							question.setQuestionPosition(numberFormat.format(Integer.parseInt(o[2].toString())));
						}
					}
					if(o[3]!=null){
						question.setQuestionRound(o[3].toString());
					}
					if(o[4]!=null){
						question.setQuestionAttendance(o[4].toString());
					}
					questions.add(question);
				}
				i.setQuestions(questions);
			}				
		}		
		return ballots;
	}



	/******************** Member Ballot Member Wise Report  *****************************************/
	@SuppressWarnings({ "rawtypes"})
	public MemberBallotMemberWiseReportVO findMemberWiseReportVO(
			Session session, DeviceType questionType, Member member,
			String locale) {
		String startDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME_UH);
		String endDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME_UH);
		MemberBallotMemberWiseReportVO memberBallotMemberWiseReportVO=new MemberBallotMemberWiseReportVO();
		if(startDate!=null&&endDate!=null){
			if((!startDate.isEmpty())&&(!endDate.isEmpty())){
				/**** Count of questions ****/
				String countQuery="SELECT COUNT(q.id),s.name,s.type FROM questions as q"+
				" JOIN devicetypes as dt JOIN status as s WHERE q.session_id="+session.getId()+
				" AND q.member_id="+member.getId()+" AND q.locale='"+locale+"'"+
				" AND q.submission_date>='"+startDate+"' AND q.submission_date<='"+endDate+"'"+
				" AND q.originaldevicetype_id="+questionType.getId()+
				" AND q.devicetype_id=dt.id AND q.internalstatus_id=s.id"+
				" AND (s.type='"+ApplicationConstants.QUESTION_FINAL_ADMISSION+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_FINAL_REJECTION+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT+"') "+
				" GROUP BY q.internalstatus_id ORDER BY s.type";
				List countResults=this.em().createNativeQuery(countQuery).getResultList();
				List<MemberBallotMemberWiseCountVO> countVOs=new ArrayList<MemberBallotMemberWiseCountVO>();
				NumberFormat numberFormat=FormaterUtil.getNumberFormatterNoGrouping(locale);
				for(Object i:countResults){
					Object[] o=(Object[]) i;
					MemberBallotMemberWiseCountVO memberBallotMemberWiseCountVO=new MemberBallotMemberWiseCountVO();
					if(o[0]!=null&&o[1]!=null&&o[2]!=null){
						if((!o[0].toString().isEmpty())&&(!o[1].toString().isEmpty())&&(!o[2].toString().isEmpty())){
							memberBallotMemberWiseCountVO.setCount(numberFormat.format(Integer.parseInt(o[0].toString())));
							memberBallotMemberWiseCountVO.setStatusType(o[1].toString());
							memberBallotMemberWiseCountVO.setStatusTypeType(o[2].toString());
							countVOs.add(memberBallotMemberWiseCountVO);
						}					
					}
				}
				memberBallotMemberWiseReportVO.setMemberBallotMemberWiseCountVOs(countVOs);
				/**** Member Full Name ****/
				memberBallotMemberWiseReportVO.setMember(member.getFullname());				
				/**** Questions ****/
				List<MemberBallotMemberWiseQuestionVO> questionVOs=new ArrayList<MemberBallotMemberWiseQuestionVO>();
				String questionQuery="SELECT q.number as questionnumber,q.subject as subject,q.rejection_reason as reason,s.name as status,g.number as groupnumber,s.type as statustype FROM questions as q"+
				" JOIN devicetypes as dt JOIN status as s "+
				" JOIN groups as g"+
				" WHERE q.session_id="+session.getId()+
				" AND q.member_id="+member.getId()+" AND q.locale='"+locale+"'"+
				" AND q.submission_date>='"+startDate+"' AND q.submission_date<='"+endDate+"'"+
				" AND q.originaldevicetype_id="+questionType.getId()+
				" AND q.devicetype_id=dt.id AND q.internalstatus_id=s.id"+
				" AND q.group_id=g.id"+
				" AND (s.type='"+ApplicationConstants.QUESTION_FINAL_ADMISSION+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_FINAL_REJECTION+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT+"'"+
				" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT+"')"+
				" ORDER BY g.number,s.type,q.number";
				String query="SELECT rs.questionnumber,rs.subject,rs.reason,rs.status,rs.groupnumber,rs.statustype FROM ("+questionQuery+") as rs";
				List questionResults=this.em().createNativeQuery(query).getResultList();
				int position=1;
				for(Object i:questionResults){
					Object[] o=(Object[]) i;
					MemberBallotMemberWiseQuestionVO questionVO=new MemberBallotMemberWiseQuestionVO();
					questionVO.setSno(numberFormat.format(position));
					if(o[0]!=null){
						if(!o[0].toString().isEmpty()){
							questionVO.setQuestionNumber(numberFormat.format(Integer.parseInt(o[0].toString())));							
						}
					}
					if(o[1]!=null){
						questionVO.setQuestionSubject(o[1].toString());
					}
					if(o[2]!=null){
						questionVO.setQuestionReason(o[2].toString());
					}
					if(o[3]!=null){
						questionVO.setStatusType(o[3].toString());
					}
					if(o[4]!=null){
						questionVO.setGroupNumber(o[4].toString());
						if(!o[4].toString().isEmpty()){
							questionVO.setGroupFormattedNumber(numberFormat.format(Integer.parseInt(o[4].toString())));
						}
					}
					if(o[5]!=null){
						questionVO.setStatusTypeType(o[5].toString());
					}
					questionVOs.add(questionVO);
					position++;
				}
				memberBallotMemberWiseReportVO.setMemberBallotMemberWiseQuestionVOs(questionVOs);
			}
		}		
		return memberBallotMemberWiseReportVO;
	}

	/**************************** Member Ballot Question Distributions ******************************/
	@SuppressWarnings("rawtypes")
	public List<MemberBallotQuestionDistributionVO> viewQuestionDistribution(
			Session session, DeviceType questionType, String locale) {
		List<MemberBallotQuestionDistributionVO> distributions=new ArrayList<MemberBallotQuestionDistributionVO>();
		String startDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME_UH);
		String endDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME_UH);
		if(startDate!=null&&endDate!=null){
			if((!startDate.isEmpty())&&(!endDate.isEmpty())){
				String query=" SELECT distinct(m.id),concat(t.name,' ',m.first_name,' ',m.middle_name,' ',m.last_name) FROM questions as q"+
				" JOIN members as m"+
				" JOIN titles as t"+
				" WHERE q.member_id=m.id AND m.title_id=t.id AND q.session_id="+session.getId()+
				" AND q.originaldevicetype_id="+questionType.getId()+" AND q.locale='"+locale+"'"+
				" AND q.submission_date>='"+startDate+"' AND q.submission_date<='"+endDate+"'"+
				" ORDER BY q.submission_date";
				List members=this.em().createNativeQuery(query).getResultList();
				int count=1;
				NumberFormat numberFormat=FormaterUtil.getNumberFormatterNoGrouping(locale);
				for(Object i:members){
					Object[] o=(Object[]) i;
					MemberBallotQuestionDistributionVO distribution=new MemberBallotQuestionDistributionVO();
					if(o[0]!=null){
						if(!o[0].toString().isEmpty()){
							distribution.setMemberId(o[0].toString());
						}
					}
					if(o[1]!=null){
						distribution.setMember(o[1].toString());
					}
					distribution.setsNo(numberFormat.format(count));
					count++;
					distributions.add(distribution);
				}
				for(MemberBallotQuestionDistributionVO i:distributions){
					String countQuery="SELECT COUNT(q.id),s.name,s.type FROM questions as q"+
					" JOIN devicetypes as dt JOIN status as s WHERE q.session_id="+session.getId()+
					" AND q.member_id="+i.getMemberId()+" AND q.locale='"+locale+"'"+
					" AND q.submission_date>='"+startDate+"' AND q.submission_date<='"+endDate+"'"+
					" AND q.originaldevicetype_id="+questionType.getId()+
					" AND q.devicetype_id=dt.id AND q.internalstatus_id=s.id"+
					" AND (s.type='"+ApplicationConstants.QUESTION_FINAL_ADMISSION+"'"+
					" OR s.type='"+ApplicationConstants.QUESTION_FINAL_REJECTION+"'"+
					" OR s.type='"+ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT+"'"+
					" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT+"'"+
					" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER+"'"+
					" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT+"'"+
					" OR s.type='"+ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT+"') "+
					" GROUP BY q.internalstatus_id ORDER BY s.priority "+ApplicationConstants.DESC+",s.name";
					List countResults=this.em().createNativeQuery(countQuery).getResultList();
					List<MemberBallotMemberWiseCountVO> countVOs=new ArrayList<MemberBallotMemberWiseCountVO>();
					int clarificationCount=0;
					int totalCount=0;
					for(Object j:countResults){
						Object[] o=(Object[]) j;
						MemberBallotMemberWiseCountVO memberBallotMemberWiseCountVO=new MemberBallotMemberWiseCountVO();
						if(o[0]!=null&&o[1]!=null&&o[2]!=null){
							if((!o[0].toString().isEmpty())&&(!o[1].toString().isEmpty())&&(!o[2].toString().isEmpty())){
								if(o[2].toString().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
										||o[2].toString().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT)
										||o[2].toString().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
										||o[2].toString().equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)){
									clarificationCount=clarificationCount+Integer.parseInt(o[0].toString());								
								}else{
									memberBallotMemberWiseCountVO.setCount(numberFormat.format(Integer.parseInt(o[0].toString())));
									memberBallotMemberWiseCountVO.setStatusType(o[1].toString());
									memberBallotMemberWiseCountVO.setStatusTypeType(o[2].toString());
									countVOs.add(memberBallotMemberWiseCountVO);
								}
								totalCount=totalCount+Integer.parseInt(o[0].toString());
							}					
						}						
					}
					if(clarificationCount>0){
						MemberBallotMemberWiseCountVO memberBallotClarificationCount=new MemberBallotMemberWiseCountVO();
						memberBallotClarificationCount.setCount(numberFormat.format(clarificationCount));
						memberBallotClarificationCount.setStatusType("clarification");
						memberBallotClarificationCount.setStatusTypeType("clarification");	
						countVOs.add(memberBallotClarificationCount);
					}
					i.setDistributions(countVOs);	
					i.setTotalCount(numberFormat.format(totalCount));
				}
			}
		}		
		return distributions;
	}

	@SuppressWarnings("unchecked")
	public List<Member> findMembersByPosition(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale,final int startingRecordToFetch,final int noOfRecordsToFetch) {
		String query="SELECT m FROM MemberBallot mb JOIN mb.member m WHERE "+
		" mb.session.id="+session.getId()+" AND mb.deviceType.id="+deviceType.getId()+" "+
		" AND mb.attendance="+attendance+" AND mb.locale='"+locale+"' AND mb.round="+round+" ORDER BY mb.position";
		return this.em().createQuery(query).setFirstResult(startingRecordToFetch).setMaxResults(noOfRecordsToFetch).getResultList();		
	}
}
