package org.mkcl.els.repository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberBallotAttendanceRepository extends BaseRepository<MemberBallotAttendance, Serializable>{

    public List<MemberBallotAttendance> findAll(final Session session,
            final DeviceType questionType,final String attendance,
            final Integer round, final String sortBy,
            final String locale) {
        Search search=new Search();
        search.addFilterEqual("session.id", session.getId());
        search.addFilterEqual("deviceType.id", questionType.getId());
        search.addFilterEqual("locale", locale);
        search.addFilterEqual("round", round);
        if(attendance.equals("true")){
            search.addFilterEqual("attendance", true);
        }else if(attendance.equals("false")){
            search.addFilterEqual("attendance", false);
        }
        if(sortBy.equals("member")){
            search.addSort("member.lastName",false);
        }else{
            search.addSort("position",false);
        }
        return this.search(search);
    }

    @SuppressWarnings("unchecked")
    public List<Member> findMembersByAttendance(final Session session,
            final DeviceType deviceType, final Boolean attendance,final Integer round, final String locale) {
        String query="SELECT m FROM MemberBallotAttendance mba JOIN mba.member m WHERE "+
        " mba.session.id="+session.getId()+" AND mba.deviceType.id="+deviceType.getId()+" "+
        " AND mba.attendance="+attendance+" AND mba.locale='"+locale+"' AND mba.round="+round+" ORDER BY mba.position";
        return this.em().createQuery(query).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Member> findEligibleMembers(final Session session,
            final DeviceType deviceType, final String locale) {
        String query="SELECT DISTINCT(m) FROM MemberBallotAttendance mba JOIN mba.member m WHERE "+
        " mba.session.id="+session.getId()+" AND mba.deviceType.id="+deviceType.getId()+" "+
        " AND mba.locale='"+locale+"' ORDER BY m.lastName";
        return this.em().createQuery(query).getResultList(); 
        }
    
	/**
	 * Creates the member ballot attendance.
	 *
	 * @param session the session
	 * @param questionType the question type
	 * @param round 
	 * @param locale the locale
	 * @return the boolean
	 */
	public String createMemberBallotAttendance(
			final Session session, final DeviceType questionType,final Integer round, final String locale) {
		String flag=null;
		try{
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP", "");
				/**** here we are making provision for creating attendance manually without any question
				 * submission.****/
				CustomParameter manuallyCreateMemberBallot=CustomParameter.findByName(CustomParameter.class,"CREATE_MEMBER_ATTENDANCE_MANUALLY", "");
				if(manuallyCreateMemberBallot!=null){
					if(customParameter!=null){
						if(manuallyCreateMemberBallot.getValue().toLowerCase().equals("yes")){
							flag=attendanceWithAllActiveMembers(session,questionType,round,customParameter.getValue(),locale);
						}else{
							flag=attendanceWithFirstBatchMembers(session,questionType,round,customParameter.getValue(),locale);
						}
					}else{
						logger.error("**** Custom Parameter 'DB_TIMESTAMP(yyyy-MM-dd HH:mm:ss)' not set ****");
						flag="DB_TIMESTAMP_NOT_SET";
					}
				}else{
					logger.error("**** Custom parameter 'CREATE_MEMBER_ATTENDANCE_MANUALLY' not set ****");
					flag="CREATE_MEMBER_ATTENDANCE_MANUALLY_NOT_SET";
				}		
		}catch(Exception ex){
			logger.error("Member Attendance creation failed.",ex);
			flag="DB_EXCEPTION";
		}
		return flag;
	}
	
	/**
	 * Member ballot created.
	 *
	 * @param session the session
	 * @param questionType the question type
	 * @param round 
	 * @param locale the locale
	 * @return the boolean
	 */
	public Boolean memberBallotCreated(final Session session, final DeviceType questionType,
			final Integer round, final String locale) {
		/**** we are checking if attendance has already been created i.e entries are already
		 * present in MemberBallotAttendance for particular locale,devicetype,session and round****/
		String query="SELECT count(m.id) FROM MemberBallotAttendance m WHERE m.session.id="+session.getId()+
		" AND m.deviceType="+questionType.getId()+" AND m.locale='"+locale+"' AND m.round="+round;
		Long count=(Long) this.em().createQuery(query).getSingleResult();
		if(count>0){
			return true;
		}else{
			return false;
		}
	}

	private String attendanceWithAllActiveMembers(final Session session,final DeviceType questionType,
			final Integer round,final String dbTimeStampFormat,
			final String locale) {
		/**** If manual creation is set in that case member attendance will be created 
		 * for all the active memebrs on a particular date ****/
		List<Member> members=new ArrayList<Member>();
		House house=session.getHouse();
		members=Member.findActiveMembers(house, new Date(), ApplicationConstants.ASC, locale);
		for(Member i:members){
			MemberBallotAttendance memberBallotAttendance=null;
			if(round==1){
				memberBallotAttendance=new MemberBallotAttendance(session,questionType,i,false,round,false,locale);
			}else{
				MemberBallotAttendance previousRoundEntry=MemberBallotAttendance.find(session,questionType,i,round-1,locale);
				memberBallotAttendance=new MemberBallotAttendance(session,questionType,i,previousRoundEntry.getAttendance(),
						round,false,locale);
				memberBallotAttendance.setPosition(previousRoundEntry.getPosition());
			}			
			memberBallotAttendance.persist();
		}		
		return "ATTENDANCE_CREATION_SUCCESS";
	}

	
	@SuppressWarnings("unchecked")
	private String attendanceWithFirstBatchMembers(final Session session,
			final DeviceType questionType,
			final Integer round,final String dbTimeStampFormat,
			final String locale) {
		/**** If auto creation of ballot attendance is set then only those members who have submitted
		 * questions in first batch will be selected and attendance will be created for them.****/
		SimpleDateFormat format=FormaterUtil.getDateFormatter(dbTimeStampFormat,"en_US");
		Date startTime = FormaterUtil.formatStringToDate(session.getParameter(questionType.getType() +"_submissionFirstBatchStartDate"), dbTimeStampFormat, session.getLocale());
		Date endTime = FormaterUtil.formatStringToDate(session.getParameter(questionType.getType() +"_submissionFirstBatchEndDate"), dbTimeStampFormat, session.getLocale());
		List<Member> members=new ArrayList<Member>();
		if(startTime!=null && endTime!=null){
			String startTimeStr=format.format(startTime);
			String endTimeStr=format.format(endTime);
			String query="SELECT DISTINCT m FROM Question q JOIN q.primaryMember m JOIN m.title t WHERE q.session.id="+session.getId()+
			" AND q.type.id="+questionType.getId()+" AND q.submissionDate>='"+startTimeStr+"' AND q.submissionDate<='"+endTimeStr+"'"+
			" ORDER BY m.lastName "+ApplicationConstants.ASC;
			members=this.em().createQuery(query).getResultList();
			for(Member i:members){
				MemberBallotAttendance memberBallotAttendance=null;
				if(round==1){
					memberBallotAttendance=new MemberBallotAttendance(session,questionType,i,false,round,false,locale);
				}else{
					MemberBallotAttendance previousRoundEntry=MemberBallotAttendance.find(session,questionType,i,round-1,locale);
					memberBallotAttendance=new MemberBallotAttendance(session,questionType,i,previousRoundEntry.getAttendance(),
							round,false,locale);
					memberBallotAttendance.setPosition(previousRoundEntry.getPosition());
				}			
				memberBallotAttendance.persist();
			}		
		}else if(startTime==null){
			logger.error("**** First Batch Submission Start Date not set ****");
			return "FIRST_BATCH_SUBMISSION_START_DATE_NOT_SET";
		}else if(endTime==null){
			logger.error("**** First Batch Submission End Date not set ****");
			return "FIRST_BATCH_SUBMISSION_END_DATE_NOT_SET";
		}
		return "ATTENDANCE_CREATION_SUCCESS";
	}

	public MemberBallotAttendance findEntry(final Session session,
			final DeviceType questionType,final Member member,final int round,final String locale) {
		Search search=new Search();
        search.addFilterEqual("session.id", session.getId());
        search.addFilterEqual("deviceType.id", questionType.getId());
        search.addFilterEqual("member.id",member.getId());
        search.addFilterEqual("round", round);
        search.addFilterEqual("locale", locale);
        return this.searchUnique(search);
	}
	
	public Boolean areMembersLocked(final Session session,final DeviceType questionType,
			final Integer round,final Boolean attendance,
			final Integer noofRounds,final String locale) {
		Search search=new Search();
        search.addFilterEqual("session.id", session.getId());
        search.addFilterEqual("deviceType.id", questionType.getId());
        search.addFilterEqual("round", round);
        search.addFilterEqual("attendance",attendance);
        search.addFilterEqual("locale", locale);
        int totalCount=this.count(search);
        
        Search search1=new Search();
        search1.addFilterEqual("session.id", session.getId());
        search1.addFilterEqual("deviceType.id", questionType.getId());
        search1.addFilterEqual("round", round);
        search1.addFilterEqual("locale", locale);
        search1.addFilterEqual("locked",true);
        search1.addFilterEqual("attendance",attendance);
        int lockedCount=this.count(search1);
        if(totalCount==0&&lockedCount==0&&attendance==false&&round==noofRounds){
        	return true;
        }else if(totalCount==0&&lockedCount==0){
        	return false;
        }else if(totalCount==lockedCount){
        	return true;
        }else{
        	return false;
        }	
	}	
}
