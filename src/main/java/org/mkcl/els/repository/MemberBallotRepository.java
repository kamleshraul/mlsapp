package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
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
					return "'MEMBERBALLOT_DELETE_EXISTING_ROUND_NOTSET";
				}
				if(freshBallotAllowed){
					return memberBallot(session,deviceType,attendance,round,locale);
				}else{
					return "MEMBERBALLOT_FOR_CURRENT_ROUND_ALREADY_EXISTS";
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
			int previousRound=round-1;
			String query=null;
			if(previousRound!=0){
				query="UPDATE MemberBallotAttendance m SET locked=true WHERE m.session.id="+session.getId()+
				" AND m.deviceType.id="+deviceType.getId()+" AND attendance="+attendance+
				" AND m.round="+previousRound+" AND m.locale='"+locale+"'";
				this.em().createQuery(query).executeUpdate();
			}	
			return "SUCCESS";
		}
		return flag;
	}

	private String isMemberBallotAllowed(Session session, DeviceType deviceType,
			Boolean attendance, int round, String locale) {
		if(attendance){
			if(round!=1){
				Boolean status=MemberBallotAttendance.areMembersLocked(session, deviceType, round, attendance,locale);
				if(status){
					return "ALLOWED";
				}else{
					return "PREVIOUSROUND_PRESENTLIST_NOT_LOCKED";
				}
			}else{
				return "ALLOWED";
			}
		}else{
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"MEMBERBALLOT_ABSENTBALLOT_AFTER_ALL_PRESENTBALLOT", locale);
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
									return "PRESENT MEMBERS FOR ALL ROUNDS NOT LOCKED";
								}
							}
							if(presentStatus){
								presentStatus=MemberBallotAttendance.areMembersLocked(session,deviceType,round-1, attendance, locale);
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

	@SuppressWarnings("unchecked")
	public List<MemberBallot> viewMemberBallot(final Session session, final DeviceType deviceType,final Boolean attendanceType,
			final int round, final String locale) {
		String query="SELECT mb FROM MemberBallot mb JOIN mb.member m WHERE mb.session.id="+session.getId()+"  "+
		" AND mb.deviceType.id="+deviceType.getId()+" AND mb.round="+round+" AND mb.attendance="+attendanceType+" AND mb.locale='"+locale+"' ORDER BY mb.position "+ApplicationConstants.ASC;
		List<MemberBallot> memberBallots=new ArrayList<MemberBallot>();
		memberBallots=this.em().createQuery(query).getResultList();
		return memberBallots;
	}

	@SuppressWarnings("unchecked")
	public List<MemberBallot> viewMemberBallot(final Session session,
			final DeviceType deviceType, final boolean attendance, final int round, final Group group,
			final String locale) {
		String query="SELECT mb FROM MemberBallot mb JOIN mb.member m JOIN mb.questionChoices qc JOIN qc.question q JOIN q.group g WHERE mb.session.id="+session.getId()+"  "+
		" AND mb.deviceType.id="+deviceType.getId()+" AND mb.round="+round+" AND mb.attendance="+attendance+" AND g.id="+group.getId()+" AND mb.locale='"+locale+"' ORDER BY mb.position "+ApplicationConstants.ASC;
		List<MemberBallot> memberBallots=new ArrayList<MemberBallot>();
		memberBallots=this.em().createQuery(query).getResultList();
		return memberBallots;
	}

	@SuppressWarnings("unchecked")
	public List<MemberBallot> viewMemberBallot(final Session session,
			final DeviceType deviceType, final boolean attendance, final int round,
			final QuestionDates answeringDate, final String locale) {
		String query="SELECT mb FROM MemberBallot mb JOIN mb.member m JOIN mb.questionChoices qc WHERE mb.session.id="+session.getId()+"  "+
		" AND mb.deviceType.id="+deviceType.getId()+" AND mb.round="+round+" AND mb.attendance="+attendance+" AND qc.newAnsweringDate.id="+answeringDate.getId()+" AND mb.locale='"+locale+"' ORDER BY mb.position "+ApplicationConstants.ASC;
		List<MemberBallot> memberBallots=new ArrayList<MemberBallot>();
		memberBallots=this.em().createQuery(query).getResultList();
		return memberBallots;
	}

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
