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

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MemberBallotQuestionVO;
import org.mkcl.els.common.vo.MemberBallotVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberBallotRepository extends BaseRepository<MemberBallot, Serializable>{

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
				CustomParameter operationOnExistingEntries=CustomParameter.findByName(CustomParameter.class,"MEMBER_BALLOT_DELETE_EXISTING_ROUND"+round, "");
				Boolean freshBallotAllowed=false;
				if(operationOnExistingEntries!=null){
					String operation=operationOnExistingEntries.getValue();
					if(operation.toUpperCase().equals(ApplicationConstants.MEMBERBALLOT_DELETE_EXISTING)){
						String deleteMemberBallots="DELETE FROM MemberBallot m WHERE m.session.id="+session.getId()+
						" AND m.deviceType.id="+deviceType.getId()+
						" AND m.locale='"+locale+"'"+
						" AND m.round="+round+
						" AND m.attendance="+attendance;
						this.em().createQuery(deleteMemberBallots).executeUpdate();
						freshBallotAllowed=true;
					}
				}else{
					logger.error("Custom parameter 'MEMBERBALLOT_DELETE_EXISTING_ROUND"+round+"' not set");
					return "MEMBERBALLOT_DELETE_EXISTING_ROUND_NOTSET";
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
		}
		return "FAILED";
	}

	public String memberBallot(Session session,DeviceType deviceType,Boolean attendance,int round,String locale){
		String flag=isMemberBallotAllowed(session,deviceType,attendance,round,locale);
		if(flag.equals("ALLOWED")){
			List<Member> input=MemberBallotAttendance.findMembersByAttendance(session,deviceType,attendance,round,locale);
			int order=1;
			Collections.shuffle(input);
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
							return "QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_UH_NOTSET";
						}
					}else{
						return "QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_UH_NOTSET";
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
			" FROM memberballot as mb JOIN members as m JOIN titles as t WHERE"+
			" mb.member=m.id AND m.title_id=t.id"+
			" AND mb.session="+session+" AND mb.device_type="+deviceType+
			" AND mb.round="+round+" AND mb.attendance="+attendance+" AND mb.locale='"+locale+"'"+
			" ORDER BY mb.position";	
			String query="SELECT rs.memberballotid,rs.position,rs.memberid,rs.membername FROM("+memberBallotQuery+") as rs";
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
				ballots.add(memberBallotVO);
			}	
			/**** Checking If Question Choices Have Been Filled ****/
			buffer.deleteCharAt(buffer.length()-1);
			String memberChoiceCreatedQuery="SELECT COUNT(*) FROM memberballot_choice_association as mbc"+
			" WHERE memberballot_id IN("+buffer.toString()+")";
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

//	@SuppressWarnings("unchecked")
//	public List<MemberBallot> viewMemberBallot(final Session session,
//			final DeviceType deviceType, final boolean attendance, final int round, final Group group,
//			final String locale) {
//		String query="SELECT mb FROM MemberBallot mb JOIN mb.member m JOIN mb.questionChoices qc JOIN qc.question q JOIN q.group g WHERE mb.session.id="+session.getId()+"  "+
//		" AND mb.deviceType.id="+deviceType.getId()+" AND mb.round="+round+" AND mb.attendance="+attendance+" AND g.id="+group.getId()+" AND mb.locale='"+locale+"' ORDER BY mb.position "+ApplicationConstants.ASC;
//		List<MemberBallot> memberBallots=new ArrayList<MemberBallot>();
//		memberBallots=this.em().createQuery(query).getResultList();
//		return memberBallots;
//	}

//	@SuppressWarnings("unchecked")
//	public List<MemberBallotVO> viewMemberBallotVO(final Session session,
//			final DeviceType deviceType, final boolean attendance, final int round, final Group group,
//			final String locale) {
//		List<MemberBallotVO> ballots=new ArrayList<MemberBallotVO>();
//		String query="SELECT mb.id FROM memberballot as mb WHERE"+
//		" mb.session="+session.getId()+" AND mb.device_type="+deviceType.getId()+
//		" AND mb.round="+round+" AND mb.attendance="+attendance+" AND mb.locale='"+locale+"'"+
//		" ORDER BY mb.position";		
//
//
//		return ballots;
//	}

//	@SuppressWarnings("unchecked")
//	public List<MemberBallot> viewMemberBallot(final Session session,
//			final DeviceType deviceType, final boolean attendance, final int round,
//			final QuestionDates answeringDate, final String locale) {
//		String query="SELECT mb FROM MemberBallot mb JOIN mb.member m JOIN mb.questionChoices qc WHERE mb.session.id="+session.getId()+"  "+
//		" AND mb.deviceType.id="+deviceType.getId()+" AND mb.round="+round+" AND mb.attendance="+attendance+" AND qc.newAnsweringDate.id="+answeringDate.getId()+" AND mb.locale='"+locale+"' ORDER BY mb.position "+ApplicationConstants.ASC;
//		List<MemberBallot> memberBallots=new ArrayList<MemberBallot>();
//		memberBallots=this.em().createQuery(query).getResultList();
//		return memberBallots;
//	}

//	@SuppressWarnings("unchecked")
//	public List<MemberBallotVO> viewMemberBallotVO(final Session session,
//			final DeviceType deviceType, final boolean attendance, final int round,
//			final QuestionDates answeringDate, final String locale) {
//		List<MemberBallotVO> ballots=new ArrayList<MemberBallotVO>();
//		String query="SELECT mb.id FROM memberballot as mb WHERE"+
//		" mb.session="+session.getId()+" AND mb.device_type="+deviceType.getId()+
//		" AND mb.round="+round+" AND mb.attendance="+attendance+" AND mb.locale='"+locale+"'"+
//		" ORDER BY mb.position";		
//		return ballots;
//	}

	public Integer findPrimaryCount(final Session session, final DeviceType deviceType,
			final String locale) {
		String query="SELECT q FROM MemberBallot mb JOIN mb.questionChoices qc JOIN qc.question q"+
		" WHERE mb.session.id="+session.getId()+" AND mb.deviceType.id="+deviceType.getId()+" "+
		" AND mb.locale='"+locale+"' AND q.parent=null";
		Integer count=this.em().createQuery(query).getResultList().size();
		return count;
	}

	public Boolean updateClubbing(final Session session, final DeviceType deviceType,
			final int start, final int size, final String locale) {
		try {
			this.em().createNativeQuery("call "+ApplicationConstants.CLUBBING_UPDATE_PROCEDURE+"(?,?,?,?,?)").setParameter(1,session.getId()).setParameter(2,deviceType.getId()).setParameter(3,start).setParameter(4,size).setParameter(5,locale).executeUpdate();
			return true;
		}
		catch (Exception e) {
			logger.error("CLUBBING UPDATE FAILED",e);
			return false;
		}
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

	public Boolean createFinalBallot(Session session, DeviceType deviceType,
			Group group, QuestionDates answeringDate, String locale,
			String firstBatchSubmissionDate) {
		try {
			this.em().createNativeQuery("call "+ApplicationConstants.FINAL_BALLOT_PROCEDURE+"(?,?,?,?,?,?)").setParameter(1,session.getId()).setParameter(2,deviceType.getId()).setParameter(3,group.getId()).setParameter(4,answeringDate.getId()).setParameter(5,locale).setParameter(6,firstBatchSubmissionDate).executeUpdate();
			this.em().createNativeQuery("call "+ApplicationConstants.FINAL_BALLOT_UPDATE_SEQUENCE_PROCEDURE+"(?,?,?,?,?,?)").setParameter(1,session.getId()).setParameter(2,deviceType.getId()).setParameter(3,group.getId()).setParameter(4,answeringDate.getId()).setParameter(5,locale).setParameter(6,firstBatchSubmissionDate).executeUpdate();
			return true;
		}
		catch (Exception e) {
			logger.error("FINAL BALLOT FAILED",e);
			return false;
		}
	}

	

}
