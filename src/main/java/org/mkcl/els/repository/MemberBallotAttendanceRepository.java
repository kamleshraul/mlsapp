package org.mkcl.els.repository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberBallotAttendanceRepository extends BaseRepository<MemberBallotAttendance, Serializable>{

	public List<MemberBallotAttendance> findAll(final Session session,
			final DeviceType questionType,
			final String attendance,
			final Integer round, 
			final String sortBy,
			final String locale) throws ELSException {
		/*Search search=new Search();		
		search.addFilterEqual("session.id", session.getId());
		search.addFilterEqual("deviceType.id", questionType.getId());
		search.addFilterEqual("locale", locale);
		search.addFilterEqual("round", round);*/

		/*if(attendance.equals("true")){
			search.addFilterEqual("attendance", true);
		}else if(attendance.equals("false")){
			search.addFilterEqual("attendance", false);
		}
		if(sortBy.equals("member")){
			search.addSort("member.lastName",false);
		}else{
			search.addSort("position",false);
		}
		return this.search(search);*/

		StringBuffer strQuery = new StringBuffer(
				"SELECT m FROM MemberBallotAttendance m" +
						" WHERE m.session.id=:sessionId" +
						" AND m.deviceType.id=:deviceTypeId" +
						" AND m.locale=:locale" +
						" AND m.round=:round"
				);
		if(!attendance.isEmpty()){
			strQuery.append(" AND m.attendance=:attendance");
		}
		strQuery.append(" ORDER BY ");

		if(sortBy.equals("member")){
			strQuery.append(" m.member.lastName ASC");
		}else{
			strQuery.append(" m.position ASC");
		}

		try{
			TypedQuery<MemberBallotAttendance> jpQuery = this.em().createQuery(strQuery.toString(), MemberBallotAttendance.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", questionType.getId());
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("round", round);

			if(attendance.equals("true")){
				jpQuery.setParameter("attendance", true);
			}else if(attendance.equals("false")){
				jpQuery.setParameter("attendance", false);
			}

			return jpQuery.getResultList();

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_List<MemberBallotAttendance>_findAll", "No attendance found.");
			throw elsException;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Member> findMembersByAttendance(final Session session,
			final DeviceType deviceType, 
			final Boolean attendance,
			final Integer round, 
			final String locale) throws ELSException {

		String query="SELECT m FROM MemberBallotAttendance mba" +
				" JOIN mba.member m" +
				" WHERE mba.session.id=:sessionId" +
				" AND mba.deviceType.id=:deviceTypeId" +
				" AND mba.attendance=:attendance" +
				" AND mba.locale=:locale" +
				" AND mba.round=:round" +
				" ORDER BY mba.position";
		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("attendance", attendance);
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("round", round);

			return jpQuery.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_List<Member>_findMembersByAttendance", "No member found.");
			throw elsException;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Member> findEligibleMembers(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		/**** Only those members who have submitted first batch questions will be allowed 
		 * to submit their choices.These are those members whose names appear atleast once in member ballot attendance
		 * either in present or absent list across any rounds.
		 */
		String query="SELECT DISTINCT(m) FROM MemberBallotAttendance mba" +
				" JOIN mba.member m" +
				" WHERE mba.session.id=:sessionId" +
				" AND mba.deviceType.id=:deviceTypeId" +
				" AND mba.locale=:locale ORDER BY m.lastName";

		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("locale", locale);

			return jpQuery.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_List<Member>_findEligibleMembers", "No member found.");
			throw elsException;
		}
	}

	/**
	 * Creates the member ballot attendance.
	 *
	 * @param session the session
	 * @param questionType the question type
	 * @param round 
	 * @param locale the locale
	 * @param locale2 
	 * @param createdAs 
	 * @return the boolean
	 */
	public String createMemberBallotAttendance(
			final Session session, 
			final DeviceType questionType,
			final Integer round, 
			final String createdBy,
			final String createdAs,
			final String locale) {

		String flag=null;
		try{
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP", "");
			/**** here we are making provision for creating attendance manually without any question
			 * submission.****/
			CustomParameter memberBallotWithAllActiveMembers=CustomParameter.findByName(CustomParameter.class,"MEBERBALLOTATTENDANCE_WITH_ALLACTIVEMEMBERS", "");
			if(customParameter!=null){
				if(memberBallotWithAllActiveMembers!=null){			
					if(memberBallotWithAllActiveMembers.getValue().toUpperCase().equals("YES")){
						flag=attendanceWithAllActiveMembers(session,questionType,round,customParameter.getValue(),createdBy,createdAs,locale);
					}else{
						flag=attendanceWithFirstBatchMembers(session,questionType,round,customParameter.getValue(),createdBy,createdAs,locale);
					}
				}else{
					flag=attendanceWithFirstBatchMembers(session,questionType,round,customParameter.getValue(),createdBy,createdAs,locale);
				}
			}else{
				logger.error("**** Custom Parameter 'DB_TIMESTAMP(yyyy-MM-dd HH:mm:ss)' not set ****");
				flag="DB_TIMESTAMP_NOT_SET";
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
	 * @throws ELSException 
	 */
	public Boolean memberBallotCreated(final Session session, 
			final DeviceType questionType,
			final Integer round, 
			final String locale) throws ELSException {
		/**** we are checking if attendance has already been created i.e entries are already
		 * present in MemberBallotAttendance for particular locale,devicetype,session and round****/

		String query="SELECT count(m.id) FROM MemberBallotAttendance m" +
				" WHERE m.session.id=:sessionId" +
				" AND m.deviceType.id=:questionTypeId" +
				" AND m.locale=:locale" +
				" AND m.round=:round";

		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("questionTypeId", questionType.getId());
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("round", round);

			Long count=(Long) jpQuery.getSingleResult();
			if(count>0){
				return true;
			}else{
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_Boolean_memberBallotCreated", "No member ballot found.");
			throw elsException;
		}
	}

	private String attendanceWithAllActiveMembers(final Session session,final DeviceType questionType,
			final Integer round,
			final String dbTimeStampFormat,
			final String createdBy,
			final String createdAs,
			final String locale) throws ELSException {
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
			memberBallotAttendance.setCreatedBy(createdBy);
			memberBallotAttendance.setCreatedAs(createdAs);
			memberBallotAttendance.setEditedBy(createdBy);
			memberBallotAttendance.setEditedAs(createdAs);
			Date date=new Date();
			memberBallotAttendance.setCreatedOn(date);
			memberBallotAttendance.setEditedOn(date);
			memberBallotAttendance.persist();
		}		
		return "ATTENDANCE_CREATION_SUCCESS";
	}


	@SuppressWarnings("unchecked")
	private String attendanceWithFirstBatchMembers(final Session session,
			final DeviceType questionType,
			final Integer round,
			final String dbTimeStampFormat,
			final String createdBy,
			final String createdAs,
			final String locale) throws ELSException {
		/**** If auto creation of ballot attendance is set then only those members who have submitted
		 * questions in first batch will be selected and attendance will be created for them.****/
		SimpleDateFormat format=FormaterUtil.getDateFormatter(dbTimeStampFormat,"en_US");
		Date startTime = FormaterUtil.formatStringToDate(session.getParameter(questionType.getType() +"_submissionFirstBatchStartDate"), dbTimeStampFormat, session.getLocale());
		Date endTime = FormaterUtil.formatStringToDate(session.getParameter(questionType.getType() +"_submissionFirstBatchEndDate"), dbTimeStampFormat, session.getLocale());
		Date currentDate=new Date();
		String role=ApplicationConstants.MEMBER;
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "MEMBERBALLOT_STARRED_COUNCIL_ONLY_ACTIVE_MEMBERS_IN_FIRST_BATCH", "");
		List<Member> members=new ArrayList<Member>();
		if(startTime!=null && endTime!=null && customParameter!=null){
			/*String startTimeStr=format.format(startTime);
			String endTimeStr=format.format(endTime);*/

			/**** Here all members who have submitted atleast one questions 
			 * will appear even though there might be some members whose none of the questions have been admitted
			 * in first batch.These cases of members will be allowed to participate
			 * in the member ballot and their names will appear in attendance list,
			 * pre ballot list and member ballot list.But since they have no admitted
			 * question from first batch so there names will occur in last in final ballot list if
			 * they have submitted question in second batch and some of the
			 * questions have been admitted for the particular answering date.If not then
			 * their names will not appear in final ballot for a particular 
			 * answering date.These will involve members whose all questions
			 * have been either rejected,converted to unstarred and admitted,
			 * have been sent for clarification needed.Also members should be active
			 */
			String query="SELECT DISTINCT m FROM Question q" +
					" JOIN q.primaryMember m" +
					" JOIN m.houseMemberRoleAssociations hmra"+
					" JOIN m.title t" +
					" WHERE q.session.id=:sessionId" +
					" AND q.originalType.id=:questionTypeId" +
					" AND q.submissionDate>=:startTime" +
					" AND q.submissionDate<=:endTime" ;
			if(customParameter.getValue().equals("YES")){
				query=query+ " AND (hmra.toDate>=:currentDate"+
						" OR hmra.toDate IS NULL)"+
						" AND hmra.role.type=:role";
			}
			query=query+" ORDER BY m.lastName " + ApplicationConstants.ASC;							
			try{
				Query jpQuery = this.em().createQuery(query);
				jpQuery.setParameter("sessionId", session.getId());
				jpQuery.setParameter("questionTypeId", questionType.getId());
				jpQuery.setParameter("startTime", startTime);
				jpQuery.setParameter("endTime", endTime);
				if(customParameter.getValue().equals("YES")){
					jpQuery.setParameter("currentDate",currentDate);
					jpQuery.setParameter("role",role);
				}
				members = jpQuery.getResultList();

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
					memberBallotAttendance.setCreatedBy(createdBy);
					memberBallotAttendance.setCreatedAs(createdAs);
					memberBallotAttendance.setEditedBy(createdBy);
					memberBallotAttendance.setEditedAs(createdAs);
					Date date=new Date();
					memberBallotAttendance.setCreatedOn(date);
					memberBallotAttendance.setEditedOn(date);
					memberBallotAttendance.persist();
				}	
			}catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				ELSException elsException = new ELSException();
				elsException.setParameter("MemberBallotAttendanceRepository_String_attendanceWithFirstBatchMembers", "No attendance found.");
				throw elsException;
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
			final DeviceType questionType,
			final Member member,
			final int round,
			final String locale) throws ELSException {
		/*Search search=new Search();
		search.addFilterEqual("session.id", session.getId());
		search.addFilterEqual("deviceType.id", questionType.getId());
		search.addFilterEqual("member.id",member.getId());
		search.addFilterEqual("round", round);
		search.addFilterEqual("locale", locale);
		return this.searchUnique(search);*/

		String strQuery = "SELECT m FROM MemberBallotAttendance m" +
				" WHERE m.session.id=:sessionId" +
				" AND m.deviceType.id=:deviceTypeId" +
				" AND m.member.id=:memberId" +
				" AND m.round=:round" +
				" AND m.locale=:locale";

		try{
			Query jpQuery = this.em().createQuery(strQuery);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", questionType.getId());
			jpQuery.setParameter("memberId", member.getId());
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("locale", locale);

			return (MemberBallotAttendance)jpQuery.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_MemberBallotAttendance_findEntry", "No entry found.");
			throw elsException;
		}
	}

	public Boolean areMembersLocked(final Session session,
			final DeviceType questionType,
			final Integer round,
			final Boolean attendance,
			final String locale) throws ELSException {
		/*Search search=new Search();
		search.addFilterEqual("session.id", session.getId());
		search.addFilterEqual("deviceType.id", questionType.getId());
		search.addFilterEqual("round", round);
		search.addFilterEqual("attendance",attendance);
		search.addFilterEqual("locale", locale);
		int totalCount=this.count(search);*/

		StringBuffer strQuery = new StringBuffer(
				"SELECT COUNT(m.id) FROM MemberBallotAttendance m" +
						" WHERE m.session.id=:sessionId" +
						" AND m.deviceType.id=:deviceTypeId" +
						" AND m.round=:round" +
						" AND m.attendance=:attendance" +
						" AND m.locale=:locale"
				);

		try{
			Query jpQuery = this.em().createQuery(strQuery.toString());
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", questionType.getId());
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("attendance", attendance);
			jpQuery.setParameter("locale", locale);

			int totalCount = ((Long)jpQuery.getSingleResult()).intValue(); 

			strQuery = null;
			jpQuery = null;

			/*Search search1=new Search();
			search1.addFilterEqual("session.id", session.getId());
			search1.addFilterEqual("deviceType.id", questionType.getId());
			search1.addFilterEqual("round", round);
			search1.addFilterEqual("locale", locale);
			search1.addFilterEqual("locked",true);
			search1.addFilterEqual("attendance",attendance);
			int lockedCount=this.count(search1);*/

			strQuery = new StringBuffer(
					"SELECT COUNT(m.id) FROM MemberBallotAttendance m" +
							" WHERE m.session.id=:sessionId" +
							" AND m.deviceType.id=:deviceTypeId" +
							" AND m.round=:round" +
							" AND m.locked=:locked" +
							" AND m.attendance=:attendance" +
							" AND m.locale=:locale"
					);

			jpQuery = this.em().createQuery(strQuery.toString());
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", questionType.getId());
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("locked", true);
			jpQuery.setParameter("attendance", attendance);
			jpQuery.setParameter("locale", locale);

			int lockedCount = ((Long)jpQuery.getSingleResult()).intValue(); 	

			if(totalCount==0&&lockedCount==0&&attendance==false){
				return false;
			}else if(totalCount==0&&lockedCount==0&&attendance==true){
				return false;
			}else if(totalCount==lockedCount){
				return true;
			}else{
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_Boolean_areMembersLocked", "No entry found.");
			throw elsException;
		}
	}

	public Integer findMembersByAttendanceCount(final Session session,
			final DeviceType deviceType, 
			final Boolean attendance, 
			final int round, 
			final String locale) throws ELSException {
		/*Search search=new Search();
		search.addFilterEqual("session.id", session.getId());
		search.addFilterEqual("deviceType.id", deviceType.getId());
		search.addFilterEqual("round", round);
		search.addFilterEqual("attendance", attendance);
		search.addFilterEqual("locale", locale);
		return this.count(search);*/

		StringBuffer strQuery = new StringBuffer(
				"SELECT COUNT(m.id) FROM MemberBallotAttendance m" +
						" WHERE m.session.id=:sessionId" +
						" AND m.deviceType.id=:deviceTypeId" +
						" AND m.round=:round" +
						" AND m.attendance=:attendance" +
						" AND m.locale=:locale"
				);

		try{
			Query jpQuery = this.em().createQuery(strQuery.toString());
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("attendance", attendance);
			jpQuery.setParameter("locale", locale);

			return ((Long)jpQuery.getSingleResult()).intValue(); 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_Integer_findMembersByAttendanceCount", "No attendance count found.");
			throw elsException;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Member> findMembersByAttendance(final Session session,
			final DeviceType deviceType,
			final Boolean attendance,
			final int round,
			final String locale,
			final int startingRecordToFetch,
			final int noOfRecordsToFetch) throws ELSException {

		String query="SELECT m FROM MemberBallotAttendance mba" +
				" JOIN mba.member m" +
				" WHERE mba.session.id=:sessionId" +
				" AND mba.deviceType.id=:deviceTypeId" +
				" AND mba.attendance=:attendance" +
				" AND mba.locale=:locale" +
				" AND mba.round=:round ORDER BY mba.position";

		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("attendance", attendance);
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("round", round);

			return jpQuery.setFirstResult(startingRecordToFetch).setMaxResults(noOfRecordsToFetch).getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_List<Member>_findMembersByAttendance", "No member found.");
			throw elsException;
		}		
	}

	public List<Member> findNewMembers(final Session session,
			final DeviceType deviceType,
			final Boolean attendance,
			final int round,
			final String locale) throws ELSException {
		int previousRound=round-1;
		String query="SELECT m FROM MemberBallotAttendance mba" +
				" JOIN mba.member m" +
				" WHERE mba.session.id=:sessionId_A" +
				" AND mba.deviceType.id=:deviceTypeId_A" +
				" AND mba.attendance=:attendance_A" +
				" AND mba.locale=:locale_A" +
				" AND mba.round=:round" +
				" AND m.id NOT IN(SELECT sm.id FROM MemberBallotAttendance smba JOIN smba.member sm WHERE smba.session.id=:sessionId_B" +
				" AND smba.deviceType.id=:deviceTypeId_B" +
				" AND smba.attendance=:attendance_B AND smba.locale=:locale_B" +
				" AND smba.round=:previousRound) ORDER BY mba.position";

		try{
			TypedQuery<Member> jpQuery = this.em().createQuery(query, Member.class);
			jpQuery.setParameter("sessionId_A", session.getId());
			jpQuery.setParameter("deviceTypeId_A", deviceType.getId());
			jpQuery.setParameter("attendance_A", attendance);
			jpQuery.setParameter("locale_A", locale);
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("sessionId_B", session.getId());
			jpQuery.setParameter("deviceTypeId_B", deviceType.getId());
			jpQuery.setParameter("attendance_B", attendance);
			jpQuery.setParameter("locale_B", locale);
			jpQuery.setParameter("previousRound", previousRound);		

			return jpQuery.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_List<Member>_findNewMembers", "No member found.");
			throw elsException;
		}	
	}

	@SuppressWarnings("unchecked")
	public List<Member> findOldMembers(final Session session,
			final DeviceType deviceType,
			final Boolean attendance,
			final int round,
			final String locale) throws ELSException {
		int previousRound=round-1;
		String query="SELECT m FROM MemberBallotAttendance mba" +
				" JOIN mba.member m" +
				" WHERE mba.session.id=:sessionId_A" +
				" AND mba.deviceType.id=:deviceTypeId_A" +
				" AND mba.attendance=:attendance_A" +
				" AND mba.locale=:locale_A" +
				" AND mba.round=:round" +
				" AND m.id IN(SELECT sm.id FROM MemberBallotAttendance smba" +
				" JOIN smba.member sm" +
				" WHERE smba.session.id=:sessionId_B" +
				" AND smba.deviceType.id=:deviceTypeId_B" +
				" AND smba.attendance=:attendance_B" +
				" AND smba.locale=:locale_B" +
				" AND smba.round=:previousRound) ORDER BY mba.position";

		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId_A", session.getId());
			jpQuery.setParameter("deviceTypeId_A", deviceType.getId());
			jpQuery.setParameter("attendance_A", attendance);
			jpQuery.setParameter("locale_A", locale);
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("sessionId_B", session.getId());
			jpQuery.setParameter("deviceTypeId_B", deviceType.getId());
			jpQuery.setParameter("attendance_B", attendance);
			jpQuery.setParameter("locale_B", locale);
			jpQuery.setParameter("previousRound", previousRound);

			return jpQuery.getResultList();		
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_List<Member>_findOldMembers", "No member found.");
			throw elsException;
		}
	}
	/**** Attendance ****/
	public String createAttendance(final Session session,
			final DeviceType deviceType,
			final String locale) {
		String flag=null;
		try{
			boolean attendanceCreated=checkAttendance(session,deviceType,locale);
			if(attendanceCreated){
				flag="SUCCESS";
			}else{
				House house=session.getHouse();
				HouseType houseType=house.getType();
				/**** Attendance will be created with following members ****/
				/**** DEVICETYPE_HOUSETYPE_ATTENDANCE****/
				CustomParameter attendanceType=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_ATTENDANCE", "");
				if(attendanceType!=null){
					if(attendanceType.getValue().equals("ACTIVE_MEMBERS")){
						flag=attendanceActiveMembers(house,session,deviceType,locale);
					}else if(attendanceType.equals("CALLING_ATTENTION_FIRST_BATCH")){
						flag=attendanceFirstBatchCallingAttention(session,deviceType,locale);
					}			
				}else{
					flag="CUSTOM_PARAMETER_NOT_SET";
				}
				
				CustomParameter includeParties=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_INCLUDE_PARTIES", "");
				if(includeParties!=null){
					if(includeParties.getValue().equalsIgnoreCase("yes")){
						flag = attendanceActiveParties(house, session,deviceType,locale);
					}			
				}else{
					flag="CUSTOM_PARAMETER_NOT_SET";
				}
			}
		}catch(Exception ex){
			logger.error("Member Attendance creation failed.",ex);
			flag="DB_EXCEPTION";
		}
		return flag;
	}

	private boolean checkAttendance(final Session session,
			final DeviceType deviceType,
			final String locale) throws ELSException {

		/*Search search=new Search();
		search.addFilterEqual("session.id", session.getId());
		search.addFilterEqual("deviceType.id", deviceType.getId());
		search.addFilterEqual("locale", locale);
		int count=this.count(search);*/

		String query = "SELECT COUNT(m.id) FROM MemberBallotAttendance m" +
				" WHERE m.session.id=:sessionId" +
				" AND m.deviceType.id=:deviceTypeId" +
				" AND m.locale=:locale"; 

		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("locale", locale);

			int count = ((Long) jpQuery.getSingleResult()).intValue();	

			if(count==0){
				return false;
			}else{
				return true;
			}		
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_boolean_checkAttendance", "No attendance found.");
			throw elsException;
		}	
	}	

	private String attendanceActiveMembers(final House house,
			final Session session,
			final DeviceType deviceType,
			final String locale) {
		List<Member> members=new ArrayList<Member>();
		members=Member.findActiveMembers(house, new Date(), ApplicationConstants.ASC, locale);
		for(Member i:members){
			MemberBallotAttendance memberBallotAttendance = new MemberBallotAttendance(session,deviceType,i,false,locale);			
			memberBallotAttendance.persist();
		}		
		return "SUCCESS";
	}

	private String attendanceFirstBatchCallingAttention(final Session session,
			final DeviceType deviceType,
			final String locale) {
		return null;
	}

	private String attendanceActiveParties(final House house, 
			final Session session,
			final DeviceType deviceType,
			final String locale) {
		
		List<Party> parties = new ArrayList<Party>(); 
		parties = Party.findAll(Party.class, "name", ApplicationConstants.ASC, locale);
		
		for(Party i : parties){
			MemberBallotAttendance memberBallotAttendance = new MemberBallotAttendance(session,deviceType,i,false,locale);			
			memberBallotAttendance.persist();
		}		
		return "SUCCESS";
	}
	
	@SuppressWarnings("unchecked")
	public List<MemberBallotAttendance> findAll(final Session session,
			final DeviceType deviceType,
			final String attendance,
			final String sortOrder,
			final String locale) throws ELSException {

		/*Search search=new Search();
		search.addFilterEqual("session.id", session.getId());
		search.addFilterEqual("deviceType.id", deviceType.getId());
		search.addFilterEqual("locale", locale);

		if(attendance.equals("true")){
			search.addFilterEqual("attendance", true);
		}else if(attendance.equals("false")){
			search.addFilterEqual("attendance", false);
		}

		if(sortOrder.equals("member")){
			search.addSort("member.lastName",false);
		}else{
			search.addSort("position",false);
		}
		return this.search(search);*/


		StringBuffer strQuery = new StringBuffer(
				"SELECT m FROM MemberBallotAttendance m" +
						" WHERE m.session.id=:sessionId" +
						" AND m.deviceType.id=:deviceTypeId" +
						" AND m.locale=:locale"
				);

		if(!attendance.isEmpty()){
			strQuery.append(" AND m.attendance=:attendance");
		}

		if(!sortOrder.isEmpty()){
			strQuery.append(" ORDER BY ");
			
			if(sortOrder.equals("member")){
				strQuery.append(" m.member.lastName ASC");
			}else{
				strQuery.append(" m." + sortOrder + " ASC");
			}
		}
		
		try{
			Query jpQuery = this.em().createQuery(strQuery.toString());
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("locale", locale);

			if(attendance.equals("true")){
				jpQuery.setParameter("attendance", true);
			}else if(attendance.equals("false")){
				jpQuery.setParameter("attendance", false);
			}

			return jpQuery.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_List<MemberBallotAttendance>_findAll", "No attendance found.");
			throw elsException;
		}	
	}

	@SuppressWarnings("unchecked")
	public List<Member> findMembersByAttendance(final Session session,
			final DeviceType deviceType,
			final Boolean attendance,
			final String locale) throws ELSException {

		String query = "SELECT m FROM MemberBallotAttendance mba" +
				" JOIN mba.member m" +
				" WHERE mba.session.id=:sessionId" +
				" AND mba.deviceType.id=:deviceTypeId" +
				" AND mba.attendance=:attendance" +
				" AND mba.locale=:locale ORDER BY mba.position";

		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("attendance", attendance);
			jpQuery.setParameter("locale", locale);

			return jpQuery.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_List<Member>_findMembersByAttendance", "No member found.");
			throw elsException;
		}	
	}
	
	@SuppressWarnings("unchecked")
	public List<Party> findPartiesByAttendance(final Session session,
			final DeviceType deviceType,
			final Boolean attendance,
			final String locale) throws ELSException {

		String query = "SELECT p FROM MemberBallotAttendance mba" +
				" JOIN mba.party p" +
				" WHERE mba.session.id=:sessionId" +
				" AND mba.deviceType.id=:deviceTypeId" +
				" AND mba.attendance=:attendance" +
				" AND mba.locale=:locale ORDER BY mba.position";

		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("attendance", attendance);
			jpQuery.setParameter("locale", locale);

			return jpQuery.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_List<Party>_findMembersByAttendance", "No member found.");
			throw elsException;
		}	
	}

	public int checkPositionForNullValues(final Session session,
			final DeviceType deviceType,
			final String attendance,
			final Integer round,
			final String sortOrder,
			final String locale) throws ELSException {
		/*Search search=new Search();
		search.addFilterEqual("session.id", session.getId());
		search.addFilterEqual("deviceType.id", deviceType.getId());
		search.addFilterEqual("locale", locale);
		if(attendance.equals("true")){
			search.addFilterEqual("attendance", true);
		}else if(attendance.equals("false")){
			search.addFilterEqual("attendance", false);
		}
		search.addFilterEqual("round", round);
		search.addFilterNull("position");
		return this.count(search);*/

		StringBuffer strQuery = new StringBuffer("SELECT COUNT(m.id) FROM MemberBallotAttendance m" +
				" WHERE m.session.id=:sessionId" +
				" AND m.deviceType.id=:deviceTypeId" +
				" AND m.round=:round" +
				" AND m.position=:position" +
				" AND m.locale=:locale"
				); 

		if(!attendance.isEmpty()){
			strQuery.append(" AND m.attendance=:attendance");
		}	

		try{
			Query jpQuery = this.em().createQuery(strQuery.toString());
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("position", null);
			jpQuery.setParameter("locale", locale);

			if(attendance.equals("true")){
				jpQuery.setParameter("attendance", true);
			}else if(attendance.equals("false")){
				jpQuery.setParameter("attendance", false);
			}

			return ((Long)jpQuery.getSingleResult()).intValue();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_int_checkPositionForNullValues", "No position found.");
			throw elsException;
		}
	}

	@SuppressWarnings("unchecked")
	public String updatePositionAbsentMembers(final Session session,
			final DeviceType deviceType,
			final Integer round,
			final boolean attendance,
			final String locale) {
		try {
			//			Search search1=new Search();
			//			search1.addFilterEqual("session.id",session.getId());
			//			search1.addFilterEqual("deviceType.id",deviceType.getId());
			//			search1.addFilterEqual("locale",locale);
			//			search1.addFilterEqual("attendance",false);
			//			search1.addFilterEqual("round",round);
			//			int toalAbsentMembers=this.count(search1);

			/*Search search2=new Search();
			search2.addFilterEqual("session.id",session.getId());
			search2.addFilterEqual("deviceType.id",deviceType.getId());
			search2.addFilterEqual("locale",locale);
			search2.addFilterEqual("attendance",false);
			search2.addFilterEqual("round",round);
			search2.addFilterNull("position");
			List<MemberBallotAttendance> absentMemberAttendance=this.search(search2);*/

			StringBuffer strQuery = new StringBuffer("SELECT m FROM MemberBallotAttendance m" +
					" WHERE m.session.id=:sessionId" +
					" AND m.deviceType.id=:deviceTypeId" +
					" AND m.round=:round" +
					" AND m.attendance=:attendance" +
					" AND m.position=:position" +
					" AND m.locale=:locale"
					); 

			Query jpQuery = this.em().createQuery(strQuery.toString());
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("attendance", false);
			jpQuery.setParameter("position", null);
			jpQuery.setParameter("locale", locale);

			List<MemberBallotAttendance> absentMemberAttendance = jpQuery.getResultList();
			for(MemberBallotAttendance i:absentMemberAttendance){
				MemberBallotAttendance previousEntry=MemberBallotAttendance.find(session,deviceType,i.getMember(), round-1, locale);
				if(previousEntry!=null){
					i.setPosition(previousEntry.getPosition());
					//i.setPositionDiscontious(true);
					i.merge();
				}
			}

			//			Search search3=new Search();
			//			search3.addFilterEqual("session.id",session.getId());
			//			search3.addFilterEqual("deviceType.id",deviceType.getId());
			//			search3.addFilterEqual("locale",locale);
			//			search3.addFilterEqual("attendance",false);
			//			search3.addFilterEqual("round",round);
			//			search2.addFilterEqual("positionDiscontious",true);
			//			int toalPositionDiscontinous=this.count(search3);

			//			if(toalAbsentMembers==toalNullPositions){
			//				if(round==1){
			//					/**** Update position according to members last name ****/
			//					List<MemberBallotAttendance> memberAttendance=findAll(session,deviceType,"false",round,"member",locale.toString());
			//					int count=1;
			//					for(MemberBallotAttendance i:memberAttendance){
			//						i.setPosition(count);
			//						i.setPositionDiscontious(true);
			//						i.merge();
			//						count++;
			//					}
			//				}else{
			//					/**** Update position according to previoud round ****/
			//					List<MemberBallotAttendance> memberAttendance=findAll(session,deviceType,"false",round,"member",locale.toString());
			//					for(MemberBallotAttendance i:memberAttendance){
			//						MemberBallotAttendance previousEntry=MemberBallotAttendance.find(session,deviceType,i.getMember(), round-1, locale);
			//						if(previousEntry!=null){
			//							i.setPosition(previousEntry.getPosition());
			//							i.setPositionDiscontious(true);
			//							i.merge();
			//						}
			//					}
			//				}
			//			}else if(toalAbsentMembers==toalPositionDiscontinous){
			//				if(round!=1){
			//					List<MemberBallotAttendance> memberAttendance=findAll(session,deviceType,"false",round,"member",locale.toString());
			//					for(MemberBallotAttendance i:memberAttendance){
			//						MemberBallotAttendance previousEntry=MemberBallotAttendance.find(session,deviceType,i.getMember(), round-1, locale);
			//						if(previousEntry!=null){
			//							i.setPosition(previousEntry.getPosition());
			//							i.setPositionDiscontious(true);
			//							i.merge();
			//						}
			//					}
			//				}				
			//			}else{
			//				return "NOT_FIRST_TIME";
			//			}
		} catch (Exception e) {
			logger.error("DB_EXCEPTION",e);
			return "FAILED";
		}
		return "FIRST_TIME";
	}

	public Boolean checkPositionDiscontinous(final Session session,
			final DeviceType deviceType,
			final boolean attendance,
			final Integer round,
			final String locale) throws ELSException {
		/*Search search1=new Search();
		search1.addFilterEqual("session.id",session.getId());
		search1.addFilterEqual("deviceType.id",deviceType.getId());
		search1.addFilterEqual("locale",locale);
		search1.addFilterEqual("attendance",false);
		search1.addFilterEqual("round",round);
		int toalAbsentMembers=this.count(search1);*/

		StringBuffer strQuery = new StringBuffer(
				"SELECT COUNT(m.id) FROM MemberBallotAttendance m" +
						" WHERE m.session.id=:sessionId" +
						" AND m.deviceType.id=:deviceTypeId" +
						" AND m.attendance=:attendance" +
						" AND m.round=:round" +
						" AND m.locale=:locale"
				);

		try{
			Query jpQuery = this.em().createQuery(strQuery.toString());
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("attendance", false);
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("locale", locale);

			int toalAbsentMembers = ((Long)jpQuery.getSingleResult()).intValue();

			strQuery = null;
			jpQuery = null;

			/*Search search2=new Search();
			search2.addFilterEqual("session.id",session.getId());
			search2.addFilterEqual("deviceType.id",deviceType.getId());
			search2.addFilterEqual("locale",locale);
			search2.addFilterEqual("attendance",false);
			search2.addFilterEqual("round",round);
			search2.addFilterEqual("positionDiscontious",true);
			int toalDiscontinousPositions=this.count(search2);*/

			strQuery = new StringBuffer(
					"SELECT COUNT(m.id) FROM MemberBallotAttendance m" +
							" WHERE m.session.id=:sessionId" +
							" AND m.deviceType.id=:deviceTypeId" +
							" AND m.attendance=:attendance" +
							" AND m.round=:round" +
							" AND m.positionDiscontious=:positionDiscontious" +
							" AND m.locale=:locale"
					);

			jpQuery = this.em().createQuery(strQuery.toString());
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("attendance", false);
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("positionDiscontious",true);
			jpQuery.setParameter("locale", locale);

			int toalDiscontinousPositions = ((Long)jpQuery.getSingleResult()).intValue();

			if(toalAbsentMembers==toalDiscontinousPositions){
				return true;
			}else{
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotAttendanceRepository_Boolean_checkPositionDiscontinous", "No position found.");
			throw elsException;
		}
	}
}
