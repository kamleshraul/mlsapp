package org.mkcl.els.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BallotEntryVO;
import org.mkcl.els.common.vo.FinalBallotVO;
import org.mkcl.els.common.vo.MemberBallotFinalBallotQuestionVO;
import org.mkcl.els.common.vo.MemberBallotFinalBallotVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseCountVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseQuestionVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseReportVO;
import org.mkcl.els.common.vo.MemberBallotQuestionDistributionVO;
import org.mkcl.els.common.vo.MemberBallotQuestionVO;
import org.mkcl.els.common.vo.MemberBallotVO;
import org.mkcl.els.comparator.BallotEntryAttRoundPosComparator;
import org.mkcl.els.comparator.BallotEntryNumberComparator;
import org.mkcl.els.comparator.BallotEntryVOFirstBatchComparator;
import org.mkcl.els.comparator.BallotEntryVOSecondBatchComparator;
import org.mkcl.els.domain.ballot.Ballot;
import org.mkcl.els.domain.ballot.BallotEntry;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.ballot.DeviceSequence;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Repository;

@Repository
public class MemberBallotRepository extends BaseRepository<MemberBallot, Serializable>{

	/*********************************** Member Ballot 
	 * @param locale2 
	 * @param createdAs 
	 * @param totalRounds ************************************************/
	public String createMemberBallot(final Session session,
			final DeviceType deviceType,
			final Boolean attendance,
			final Integer round,
			final String createdBy,
			final String createdAs,
			final String locale,
			final Integer totalRounds){
		try {

			StringBuffer strQuery = new StringBuffer("SELECT COUNT(m.id) FROM MemberBallot m" +
					" WHERE m.session.id=:sessionId" +
					" AND m.deviceType.id=:deviceTypeId" +
					" AND m.round=:round" +
					" AND m.attendance=:attendance" +
					" AND m.locale=:locale"
					);

			Query jpQuery = this.em().createQuery(strQuery.toString());
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("attendance", attendance);
			jpQuery.setParameter("locale", locale);

			int count = ((Long)jpQuery.getSingleResult()).intValue();
			if(count>0){
				jpQuery = null;
				/**** Then we will decide if to delete the existing entries and do fresh balloting 
				 * or to not allow  fresh balloting ****/
				CustomParameter operationOnExistingEntries=CustomParameter.findByName(CustomParameter.class,"MEMBERBALLOT_DELETE_EXISTING_MEMBERBALLOT"+round, "");
				Boolean freshBallotAllowed=false;
				if(operationOnExistingEntries!=null){
					String operation=operationOnExistingEntries.getValue();
					if(operation.toUpperCase().equals("DELETE")){
						String deleteMemberBallots="DELETE FROM MemberBallot m" +
								" WHERE m.session.id=:sessionId" +
								" AND m.deviceType.id=:deviceTypeId" +
								" AND m.locale=:locale" +
								" AND m.round=:round" +
								" AND m.attendance=:attendance";

						jpQuery = this.em().createQuery(deleteMemberBallots);
						jpQuery.setParameter("sessionId", session.getId());
						jpQuery.setParameter("deviceTypeId", deviceType.getId());
						jpQuery.setParameter("round", round);
						jpQuery.setParameter("attendance", attendance);
						jpQuery.setParameter("locale", locale);						

						jpQuery.executeUpdate();
						freshBallotAllowed=true;
					}
				}
				if(freshBallotAllowed){
					return memberBallot(session,deviceType,attendance,round,createdBy,createdAs,locale,totalRounds);
				}else{
					return "SUCCESS";
				}
			}else{
				return memberBallot(session,deviceType,attendance,round,createdBy,createdAs,locale,totalRounds);
			}
		} catch (Exception e) {
			logger.error("FAILED",e);
			return "FAILED";
		}
	}

	public String memberBallot(final Session session,
			final DeviceType deviceType,
			final Boolean attendance,
			final int round,
			final String createdBy, 
			final String createdAs, 
			final String locale,
			final Integer totalRounds) throws ELSException{
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
					if(randomizeInput.getValue().toUpperCase().equals("YES_WITH_SLICING")){
						input=customRandomization(session,deviceType,attendance,round,locale);
					}else if(randomizeInput.getValue().toUpperCase().equals("YES_WITHOUT_SLICING")){
						input=customRandomizationWithoutSlicing(session,deviceType,attendance,round,locale,totalRounds);
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
			int count=1;
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
				memberBallot.setCreatedAs(createdAs);
				memberBallot.setCreatedBy(createdBy);
				memberBallot.setPseudoPosition(count);
				memberBallot.persist();
				order++;
				count++;
			}

			String query="UPDATE MemberBallotAttendance m" +
					" SET locked=true WHERE m.session.id=:sessionId" +
					" AND m.deviceType.id=:deviceTypeId" +
					" AND m.attendance=:attendance" +
					" AND m.round=:round" +
					" AND m.locale=:locale";
			try{
				Query jpQuery = this.em().createQuery(query);
				jpQuery.setParameter("sessionId", session.getId());
				jpQuery.setParameter("deviceTypeId", deviceType.getId());
				jpQuery.setParameter("attendance", attendance);
				jpQuery.setParameter("round", round);
				jpQuery.setParameter("locale", locale);

				jpQuery.executeUpdate();

				return "SUCCESS";
			}catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		return flag;
	}

	private List<Member> customRandomizationWithoutSlicing(final Session session,
			final DeviceType deviceType,
			final Boolean attendance,
			final int round,
			final String locale, 
			final Integer totalRounds) throws ELSException {
		List<Member> finalMembers=new ArrayList<Member>();
		List<Member> uniqueMembers=new ArrayList<Member>();
		List<Member> nonUniqueMembers=new ArrayList<Member>();
		if(round!=1){
			List<Member> inputMembers=MemberBallotAttendance.findMembersByAttendance(session, deviceType, attendance, round, locale);
			Collections.shuffle(inputMembers);
			/**** Check How Many Positions Cannot Be Same ****/
			int noOfUniquePositions=0;
			if(inputMembers.size()>=round && attendance==true){
				noOfUniquePositions=(int) Math.floor(inputMembers.size()/round);
			}else{
				noOfUniquePositions=(int) Math.floor(inputMembers.size()/round);
			}
			for(Member i:inputMembers){
				Boolean unique=membersNotPrsentAtPositionX(session,deviceType,attendance,round,i,noOfUniquePositions,locale);
				if(unique&&uniqueMembers.size()<=noOfUniquePositions){					
					uniqueMembers.add(i);
				}else{
					nonUniqueMembers.add(i);
				}
			}		
			finalMembers.addAll(uniqueMembers);
			finalMembers.addAll(nonUniqueMembers);
		}
		return finalMembers;
	}

	private List<Member> customRandomization(final Session session,
			final DeviceType deviceType,
			final Boolean attendance,
			final int round,
			final String locale) {
		//		List<Member> finalMembers=new ArrayList<Member>();
		//		List<Member> uniqueMembers=new ArrayList<Member>();
		//		List<Member> nonUniqueMembers=new ArrayList<Member>();
		//		if(round!=1){
		//			Integer noOfMembersPreviousRound=MemberBallotAttendance.findMembersByAttendanceCount(session,deviceType,attendance,round-1,locale);
		//			int firstHalvesSize=(int) Math.ceil((noOfMembersPreviousRound/2));
		//			int secondHalvesSize=noOfMembersPreviousRound-firstHalvesSize;
		//			List<Member> firstHalfMembers=findMembersByPosition(session,deviceType,attendance,round-1,locale,0,firstHalvesSize);
		//			List<Member> secondHalfMembers=findMembersByPosition(session,deviceType,attendance,round-1,locale,firstHalvesSize,secondHalvesSize);
		//			List<Member> newMembers=MemberBallotAttendance.findNewMembers(session,deviceType,attendance,round,locale);
		//			if(secondHalfMembers!=null){
		//				if(newMembers!=null){
		//					if(!newMembers.isEmpty()){
		//						secondHalfMembers.addAll(newMembers);
		//					}
		//				}
		//				Collections.shuffle(secondHalfMembers);
		//				/**** Check How Many Positions Cannot Be Same ****/
		//				int noOfUniquePositions=2;
		//				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"MEMBERBALLOT_NOOFUNIQUESPOSITIONS", "");
		//				if(customParameter!=null){
		//					try {
		//						noOfUniquePositions=Integer.parseInt(customParameter.getValue());
		//					} catch (NumberFormatException e) {
		//						logger.error("Invalid Value Of Custom Parameter 'MEMBERBALLOT_NOOFUNIQUESPOSITIONS'",e);
		//					}					
		//				}				
		//				for(Member i:secondHalfMembers){
		//					Boolean unique=membersNotPrsentAtPositionX(session,deviceType,attendance,round,i,noOfUniquePositions,locale);
		//					if(!unique){
		//						nonUniqueMembers.add(i);
		//					}else{
		//						uniqueMembers.add(i);
		//					}
		//				}
		//
		//			}
		//			Collections.shuffle(firstHalfMembers);
		//			finalMembers.addAll(uniqueMembers);
		//			finalMembers.addAll(nonUniqueMembers);
		//			finalMembers.addAll(firstHalfMembers);			
		//		}
		//		return finalMembers;
		return null;
	}

	private Boolean membersNotPrsentAtPositionX(final Session session,
			final DeviceType deviceType,
			final Boolean attendance,
			final int round,
			final Member membersAtPositionX,
			final int noOfUniquePositions,
			final String locale) throws ELSException {
		String query="SELECT COUNT(mb.id) FROM MemberBallot mb" +
				" WHERE mb.session.id=:sessionId" +
				" AND mb.deviceType.id=:deviceTypeId" +
				" AND mb.attendance=:attendance" +
				" AND mb.locale=:locale" +
				" AND mb.member.id=:membersAtPositionXId";

		try {
			StringBuffer roundQuery=new StringBuffer();
			for(int i=1;i<round;i++){
				roundQuery.append("mb.round="+i+" OR ");
			}
			roundQuery.delete(roundQuery.length()-3,roundQuery.length()-1);
			StringBuffer positionQuery=new StringBuffer();			
			for(int i=1;i<=noOfUniquePositions;i++){
				positionQuery.append("mb.pseudoPosition="+i+" OR ");
			}
			String finalQuery=null;
			if(positionQuery!=null&&!positionQuery.toString().isEmpty()){
				positionQuery.delete(positionQuery.length()-3,positionQuery.length()-1);		
				finalQuery=query+" AND ("+roundQuery.toString()+") AND ("+positionQuery.toString()+")";

				Query jpQuery = this.em().createQuery(finalQuery);
				jpQuery.setParameter("sessionId", session.getId());
				jpQuery.setParameter("deviceTypeId", deviceType.getId());
				jpQuery.setParameter("attendance", attendance);
				jpQuery.setParameter("membersAtPositionXId", membersAtPositionX.getId());
				jpQuery.setParameter("locale", locale);

				Long count=(Long) jpQuery.getSingleResult();
				if(count==0){
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotRepository_Boolean_membersNotPrsentAtPositionX", "No member position found.");
			throw elsException;
		}	
	}

	private int noOfRecordsInMemberBallot(final Session session,
			final DeviceType deviceType, 
			final boolean attendance, 
			final int round, 
			final String locale) throws ELSException {

		try {
			String strQuery = "SELECT COUNT(m.id) FROM MemberBallot m" +
					" WHERE m.session.id=:sessionId" +
					" AND m.deviceType.id=:deviceTypeId" +
					" AND m.attendance=:attendance" +
					" AND m.round=:round" +
					" AND m.locale=:locale";
			Query jpQuery = this.em().createQuery(strQuery);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("attendance", attendance);
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("locale", locale);

			return ((Long)jpQuery.getSingleResult()).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotRepository_int_noOfRecordsInMemberBallot", "No record in member ballot found.");
			throw elsException;
		}
	}

	private String isMemberBallotAllowed(final Session session, 
			final DeviceType deviceType,
			final Boolean attendance, 
			final int round, 
			final String locale) throws ELSException {
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
					String strNoOfRounds=session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT);
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

	@SuppressWarnings("unchecked")
	public List<MemberBallot> findByMember(final Session session,
			final DeviceType deviceType, 
			final Member member, 
			final String locale) throws ELSException {		

		String strQuery = "SELECT m FROM MemberBallot m" +
				" WHERE m.session.id=:sessionId" +
				" AND m.deviceType.id=:deviceTypeId" +
				" AND m.member.id=:memberId" +
				" AND m.locale=:locale ORDER BY m.round ASC, m.position ASC";

		try{
			Query jpQuery = this.em().createQuery(strQuery);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("memberId", member.getId());
			jpQuery.setParameter("locale", locale);


			return jpQuery.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotRepository_List<MemberBallot>_findByMember", "No ballot found.");
			throw elsException;
		}
	}

	public MemberBallot findByMemberRound(final Session session,
			final DeviceType questionType, 
			final Member member,
			final int round, 
			final String locale) throws ELSException {

		try {
			String strQuery = "SELECT m FROM MemberBallot m" +
					" WHERE m.session.id=:sessionId" +
					" AND m.deviceType.id=:deviceTypeId" +
					" AND m.member.id=:memberId" +
					" AND m.round=:round" +
					" AND m.locale=:locale";

			TypedQuery<MemberBallot> jpQuery = this.em().createQuery(strQuery, MemberBallot.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", questionType.getId());
			jpQuery.setParameter("memberId", member.getId());
			jpQuery.setParameter("round", round);
			jpQuery.setParameter("locale", locale);

			MemberBallot memberBallot = null;
			try{
				memberBallot = jpQuery.getSingleResult();
			}catch (NoResultException nrfe) {
				logger.error("NO MEMBER BALLOT FOOUND FOR ROUND");
			}catch (Exception e) {
				e.printStackTrace();
			}
			return memberBallot;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotRepository_MemberBallot_findByMemberRound", "No ballot found.");
			throw elsException;
		}

	}


	public List<MemberBallotVO> viewMemberBallotVO(final Session session, 
			final DeviceType deviceType,
			final Boolean attendance,
			final int round, 
			final String locale) {
		return getMemberBallotVOs(session.getId(), deviceType.getId(), attendance, round, new Long(0),
				new Long(0), locale);
	}

	public List<MemberBallotVO> viewMemberBallotVO(final Session session, 
			final DeviceType deviceType,
			final Boolean attendance,
			final int round,
			final Group group, 
			final String locale) {
		return getMemberBallotVOs(session.getId(), deviceType.getId(), attendance, round, group.getId(),
				new Long(0), locale);
	}

	public List<MemberBallotVO> viewMemberBallotVO(final Session session, 
			final DeviceType deviceType,
			final Boolean attendance,
			final int round,
			final Group group,
			final QuestionDates answeringDate, 
			final String locale) {
		return getMemberBallotVOs(session.getId(), deviceType.getId(), attendance, round,group.getId(),
				answeringDate.getId(), locale);
	}

	/**** Unused: Overtaken by findReport() of Query for reporting ****/
	@SuppressWarnings("rawtypes")
	public List<MemberBallotVO> getMemberBallotVOs(final Long session,
			final Long deviceType, 
			final boolean attendance, 
			final int round,
			final Long group,
			final Long answeringDate, 
			final String locale){
		List<MemberBallotVO> ballots=new ArrayList<MemberBallotVO>();
		try {
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_DATEFORMAT", "");
			NumberFormat numberFormat=FormaterUtil.getNumberFormatterNoGrouping(locale);
			SimpleDateFormat dbFormat=FormaterUtil.getDateFormatter(customParameter.getValue(), locale);
			SimpleDateFormat format=FormaterUtil.getDateFormatter(locale);

			/**** Member Ballot Entries ****/
			org.mkcl.els.domain.Query elsQueryMemberBallotInner = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_GET_MEMBER_BALLOT_VO_MEMBER_BALLOT_INNER_QUERY", locale);
			org.mkcl.els.domain.Query elsQueryMemberBallotFinal = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_GET_MEMBER_BALLOT_VO_MEMBER_BALLOT_FINAL_QUERY", locale);
			Query jpQuery = null;
			if(elsQueryMemberBallotInner != null){
				if(elsQueryMemberBallotFinal != null){

					jpQuery = this.em().createNativeQuery(elsQueryMemberBallotFinal.getQuery().replaceAll("INNER_QUERY", elsQueryMemberBallotInner.getQuery()));
					jpQuery.setParameter("sessionId", session);
					jpQuery.setParameter("deviceTypeId", deviceType);
					jpQuery.setParameter("round", round);
					jpQuery.setParameter("attendance", attendance);
					jpQuery.setParameter("locale", locale);					

					List memberBallots = jpQuery.getResultList();

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

					jpQuery = null;
					org.mkcl.els.domain.Query elsQueryMemberChoice = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_GET_MEMBER_BALLOT_VO_MEMBER_CHOICE_CREATED", locale);
					/**** Checking If Question Choices Have Been Filled ****/
					String memberChoiceCreatedQuery=null;
					if(elsQueryMemberChoice != null){
						if(!buffer.toString().isEmpty()){
							buffer.deleteCharAt(buffer.length()-1);
							memberChoiceCreatedQuery=elsQueryMemberChoice.getQuery().replaceAll("INNER_QUERY",buffer.toString());
						}else{
							return ballots;
						}			
						jpQuery = this.em().createNativeQuery(memberChoiceCreatedQuery);
						BigInteger count=(BigInteger)jpQuery.getSingleResult();
						if(count.equals(0)){
							return ballots;
						}
					}
				}
			}
			/*
			 	MEMBERBALLOT_GET_MEMBER_BALLOT_VO_QUESTION_CHOICE_PLAIN
			  	MEMBERBALLOT_GET_MEMBER_BALLOT_VO_QUESTION_CHOICE_WITH_QUESTIONDATE
			  	MEMBERBALLOT_GET_MEMBER_BALLOT_VO_QUESTION_CHOICE_WITH_GROUP
			  	MEMBERBALLOT_GET_MEMBER_BALLOT_VO_QUESTION_CHOICE_WITH_QUESTIONDATE_GROUP

			 */
			/**** Populating Question Choices ****/
			for(MemberBallotVO i:ballots){
				List<MemberBallotQuestionVO> questionVOs = new ArrayList<MemberBallotQuestionVO>();
				/*String questionChoiceQuery="SELECT q.id as questionid,q.number as questionnumber,g.number as groupnumber"+
				" ,p.id as parentid,p.number as parentnumber,qd.answering_date as answeringdate"+
				" FROM memberballot_choice as mbc"+
				" JOIN questions as q JOIN question_dates as qd"+
				" LEFT JOIN questions as p ON (p.id=q.parent)"+
				" JOIN groups as g JOIN memberballot_choice_association as mbca"+
				" WHERE mbca.memberballot_choice_id=mbc.id"+
				" AND mbc.question=q.id AND mbc.new_answering_date=qd.id"+
				" AND q.group_id=g.id"+
				" AND mbca.memberballot_id="+i.getId();*/
				org.mkcl.els.domain.Query elsQueryInner = null;

				if (answeringDate == 0 && group == 0) {
					elsQueryInner = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class,
							"keyField","MEMBERBALLOT_GET_MEMBER_BALLOT_VO_QUESTION_CHOICE_PLAIN", locale);
				} else if (answeringDate != 0 && group != 0) {
					elsQueryInner = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class,
							"keyField","MEMBERBALLOT_GET_MEMBER_BALLOT_VO_QUESTION_CHOICE_WITH_QUESTIONDATE_GROUP",locale);
				} else if (answeringDate != 0) {
					elsQueryInner = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class,
							"keyField","MEMBERBALLOT_GET_MEMBER_BALLOT_VO_QUESTION_CHOICE_WITH_QUESTIONDATE",locale);
				} else if (group != 0) {
					elsQueryInner = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class,
							"keyField","MEMBERBALLOT_GET_MEMBER_BALLOT_VO_QUESTION_CHOICE_WITH_GROUP",locale);
				}

				if(elsQueryInner != null){

					org.mkcl.els.domain.Query elsQueryFinal = null;
					elsQueryFinal = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, 
							"keyField", "MEMBERBALLOT_GET_MEMBER_BALLOT_VO_QUESTION_CHOICE_FINAL_QUERY", locale);

					if(elsQueryFinal != null){

						String finalQuery = elsQueryFinal.getQuery().replaceAll("INNER_QUERY", elsQueryInner.getQuery());
						jpQuery = this.em().createNativeQuery(finalQuery);
						jpQuery.setParameter("memberBallotId", i.getId());

						if(answeringDate != 0){
							jpQuery.setParameter("qdId", answeringDate);
						}

						if(group != 0){
							jpQuery.setParameter("groupId", group);
						}

						List result = jpQuery.getResultList();

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
					}
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

	public Integer findPrimaryCount(final Session session, 
			final DeviceType deviceType,
			final String locale) throws ELSException {

		String query="SELECT q FROM MemberBallot mb" +
				" JOIN mb.questionChoices qc" +
				" JOIN qc.question q" +
				" WHERE mb.session.id=:sessionId" +
				" AND mb.deviceType.id=:deviceTypeId" +
				" AND mb.locale=:locale" +
				" AND q.parent IS NULL";
		// " AND q.parent=:parent";
		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("locale", locale);
			// jpQuery.setParameter("parent", null);

			return jpQuery.getResultList().size();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotRepository_Integer_findPrimaryCount", "No count found.");
			throw elsException;
		}
	}

	/***************************** Member Ballot Update Clubbing ********************************/
	@SuppressWarnings("unused")
	public Boolean updateClubbing(final Session session, 
			final DeviceType deviceType,
			final int start, 
			final int size, 
			final String locale) {
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
	/****************************** Member Final Ballot UH @throws ELSException **********************************/	
	public Boolean createFinalBallotUH(final Session session,
			final DeviceType deviceType,
			final Group group,
			final String strAnsweringDate,
			final Date answeringDate,
			final String locale,
			final String firstBatchSubmissionEndDate,
			final int totalRounds) throws ELSException {
		/**** Ballot for a particular answering date can be created only once ****/
		//Boolean status=ballotAlreadyCreated(session,deviceType,group,strAnsweringDate,locale);
		Boolean status = true;
		Ballot prevBallot = Ballot.find(session, deviceType, answeringDate, locale);
		if(prevBallot != null){
			CustomParameter csptBallotRecreate = CustomParameter.
					findByName(CustomParameter.class, deviceType.getType().toUpperCase() + "_" + session.getHouse().getType().getType().toUpperCase() +"_BALLOT_RECREATE_IF_EXISTS", "");
			if(csptBallotRecreate != null){
				String ballotRecreate = csptBallotRecreate.getValue();
				if(ballotRecreate != null && !ballotRecreate.isEmpty() && ballotRecreate.equals("YES")){
					prevBallot.removeBallotUH();
					status = false;
				}						
			}
		}else{
			status = false;
		}
		
		if(!status){
			try {
				Map<String,String[]> requestMap=new HashMap<String, String[]>();
				requestMap.put("locale",new String[]{locale});
				requestMap.put("sessionid",new String[]{String.valueOf(session.getId())});
				requestMap.put("deviceType",new String[]{String.valueOf(deviceType.getId())});
				requestMap.put("groupid",new String[]{String.valueOf(group.getId())});
				requestMap.put("rounds",new String[]{String.valueOf(totalRounds)});
				requestMap.put("answeringDate",new String[]{strAnsweringDate});
				requestMap.put("firstBatchSubmissionEndDate",new String[]{firstBatchSubmissionEndDate});	
				House house=session.getHouse();
				requestMap.put("house", new String[]{String.valueOf(house.getId())});
				Date currentDate=new Date();
				String strCurrentDate=FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATEFORMAT, "en_US").format(currentDate);
				requestMap.put("currentDate", new String[]{strCurrentDate});
				/**** memberPositionMap contains distinct members and their position as they are being read during various stages ****/
				Map<Long,Integer> memberPositionMap=new LinkedHashMap<Long, Integer>();
				/**** memberRoundBallotEntryVOMap contains members and the various questions that become eligible during final ballot for that
				 * member ****/
				Map<Long,Map<Integer,BallotEntryVO>> memberRoundBallotEntryVOMap=new LinkedHashMap<Long, Map<Integer,BallotEntryVO>>();
				/**** Position starts with 1000 so that cases like that of Aashish Selar can be adjusted easily ****/
				int position=1000;				

				Date firstBatchSubmissionEndDateDate=FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT, "en_US").parse(firstBatchSubmissionEndDate);
				int totalRoundsInMemberBallot=Integer.parseInt(session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT));
				/**** These fields will be used to determine the position of an inactive member question when the
				 * supporting member or clubbed entity primary member or clubbed entity supporting member of that question has
				 * neither given question in first batch or second batch for given answering date(e.g Aashish Selar case) ****/
				List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundPresent=new ArrayList<BallotEntryVO>();
				List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundAbsent=new ArrayList<BallotEntryVO>();
				List<BallotEntryVO> membersWithQuestionsOnlyInSecondBatch=new ArrayList<BallotEntryVO>();
				Long lastMemberFirstBatch=new Long(0);
				Long lastMemberSecondBatch=new Long(0);

				/**** Horizontal Scanning=first batch current date,first batch previous date,second batch current date,second batch previous date ***/
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"FINAL_BALLOT_COUNCIL_SCANNING","");
				if(customParameter!=null && customParameter.getValue().toLowerCase().equals("horizontal")){
					position=firstBatchCurrentAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					lastMemberFirstBatch=lastMember(memberPositionMap);
					position=firstBatchPreviousAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					membersWithQuestionsOnlyInLastRoundPresent=membersWithFIrstBatchLastRoundQuestionsPresent(session, deviceType, requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap, membersWithQuestionsOnlyInLastRoundPresent,
							membersWithQuestionsOnlyInLastRoundAbsent, lastMemberFirstBatch, totalRoundsInMemberBallot, locale);
					membersWithQuestionsOnlyInLastRoundAbsent=membersWithFIrstBatchLastRoundQuestionsAbsent(session, deviceType, requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap, membersWithQuestionsOnlyInLastRoundPresent,
							membersWithQuestionsOnlyInLastRoundAbsent, lastMemberFirstBatch, totalRoundsInMemberBallot, locale);
					position=secondBatchCurrentAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					lastMemberSecondBatch=lastMember(memberPositionMap);
					// position=secondBatchPreviousAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					membersWithQuestionsOnlyInSecondBatch=membersWithSecondBatchQuestionsOnly(session, deviceType, requestMap, position, totalRoundsInMemberBallot, 
							memberPositionMap, memberRoundBallotEntryVOMap, membersWithQuestionsOnlyInSecondBatch, lastMemberSecondBatch, locale);
				}else{
					position=firstBatchCurrentAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					lastMemberFirstBatch=lastMember(memberPositionMap);
					position=secondBatchCurrentAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					lastMemberSecondBatch=lastMember(memberPositionMap);
					position=firstBatchPreviousAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					membersWithQuestionsOnlyInLastRoundPresent=membersWithFIrstBatchLastRoundQuestionsPresent(session, deviceType, requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap, membersWithQuestionsOnlyInLastRoundPresent,
							membersWithQuestionsOnlyInLastRoundAbsent, lastMemberFirstBatch, totalRoundsInMemberBallot, locale);
					membersWithQuestionsOnlyInLastRoundAbsent=membersWithFIrstBatchLastRoundQuestionsAbsent(session, deviceType, requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap, membersWithQuestionsOnlyInLastRoundPresent,
							membersWithQuestionsOnlyInLastRoundAbsent, lastMemberFirstBatch, totalRoundsInMemberBallot, locale);
					// position=secondBatchPreviousAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					membersWithQuestionsOnlyInSecondBatch=membersWithSecondBatchQuestionsOnly(session, deviceType, requestMap, position, totalRoundsInMemberBallot, 
							memberPositionMap, memberRoundBallotEntryVOMap, membersWithQuestionsOnlyInSecondBatch, lastMemberSecondBatch, locale);
				}				
				inactiveMemberFirstBatchCurrentAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
						session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
						,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
						,lastMemberFirstBatch,lastMemberSecondBatch);

				inactiveMemberFirstBatchPreviousAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
						session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
						,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
						,lastMemberFirstBatch,lastMemberSecondBatch);

				inactiveMemberSecondBatchCurrentAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
						session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
						,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
						,lastMemberFirstBatch,lastMemberSecondBatch);

				inactiveMemberSecondBatchPreviousAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
						session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
						,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
						,lastMemberFirstBatch,lastMemberSecondBatch);


				//Setting Position
				Map<Integer,Long> sortedMemberPositionMap=new TreeMap<Integer, Long>();
				List<BallotEntry> ballotEntries=new ArrayList<BallotEntry>();
				for(java.util.Map.Entry<Long, Integer> i:memberPositionMap.entrySet()){
					sortedMemberPositionMap.put(i.getValue(),i.getKey());					
				}
				//Setting Sequence and creating device sequences,ballot entries and ballot
				int sequence=1;
				int actualPosition=1;
				Status ballotStatus=Status.findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);
				CustomParameter eligibleQuestionsAreSorted=CustomParameter.findByName(CustomParameter.class, "FINAL_BALLOT_COUNCIL_ELIGIBLE_QUESTIONS_SORTED", "");
				if(eligibleQuestionsAreSorted!=null && eligibleQuestionsAreSorted.getValue().toLowerCase().equals("yes")){
					for(java.util.Map.Entry<Long,Map<Integer, BallotEntryVO>> i:memberRoundBallotEntryVOMap.entrySet()){
						List<BallotEntryVO> firstBatchQuestions=new ArrayList<BallotEntryVO>();
						List<BallotEntryVO> secondBatchQuestions=new ArrayList<BallotEntryVO>();
						List<BallotEntryVO> inactiveMemberQuestions=new ArrayList<BallotEntryVO>();
						CustomParameter inactiveMembersQuestionsSorted=CustomParameter.findByName(CustomParameter.class,"FINAL_BALLOT_COUNCIL_INACTIVEMEMBERQUESTIONS_SORTED", "");
						if(inactiveMembersQuestionsSorted!=null && inactiveMembersQuestionsSorted.getValue().toLowerCase().equals("yes")){
							for(java.util.Map.Entry<Integer, BallotEntryVO> j:i.getValue().entrySet()){
								if(j.getValue().getSubmissionDate()!=null){
									if((j.getValue().getSubmissionDate().before(firstBatchSubmissionEndDateDate)
											||j.getValue().getSubmissionDate().equals(firstBatchSubmissionEndDateDate))
											&& j.getValue().getRound()!=null 
											&& j.getValue().getPosition()!=null
											){
										firstBatchQuestions.add(j.getValue());
									}else{
										secondBatchQuestions.add(j.getValue());
									}
								}
							}
						}else{
							for(java.util.Map.Entry<Integer, BallotEntryVO> j:i.getValue().entrySet()){
								if(j.getValue().getSubmissionDate()!=null){
									if((j.getValue().getSubmissionDate().before(firstBatchSubmissionEndDateDate)
											||j.getValue().getSubmissionDate().equals(firstBatchSubmissionEndDateDate))
											&& j.getValue().getRound()!=null 
											&& j.getValue().getPosition()!=null
											){
										firstBatchQuestions.add(j.getValue());
									}else if((j.getValue().getSubmissionDate().before(firstBatchSubmissionEndDateDate)
											||j.getValue().getSubmissionDate().equals(firstBatchSubmissionEndDateDate))
											&& j.getValue().getRound()==null 
											&& j.getValue().getPosition()==null
											){
										inactiveMemberQuestions.add(j.getValue());
									}							
									else{
										secondBatchQuestions.add(j.getValue());
									}
								}
							}
						}
						
						Map<Integer, BallotEntryVO> tempMap=new HashMap<Integer, BallotEntryVO>();
						boolean filled=false;
						int count=0;
						if(!firstBatchQuestions.isEmpty()){
							Collections.sort(firstBatchQuestions,new BallotEntryVOFirstBatchComparator());
							for(BallotEntryVO k:firstBatchQuestions){
								if(count==totalRounds){
									break;
								}
								tempMap.put(count+1,k);
								count++;
							}
							if(tempMap.size()==totalRounds){
								filled=true;
							}
						}
						if(!filled && !secondBatchQuestions.isEmpty()){
							Collections.sort(secondBatchQuestions,new BallotEntryVOSecondBatchComparator());
							for(BallotEntryVO k:secondBatchQuestions){
								if(count==totalRounds){
									break;
								}
								tempMap.put(count+1,k);
								count++;
							}
							if(tempMap.size()==totalRounds){
								filled=true;
							}
						}
						if(!filled && !inactiveMemberQuestions.isEmpty()){
							for(BallotEntryVO k:inactiveMemberQuestions){
								if(count==totalRounds){
									break;
								}
								tempMap.put(count+1,k);
								count++;
							}
							if(tempMap.size()==totalRounds){
								filled=true;
							}
						}
						memberRoundBallotEntryVOMap.put(i.getKey(),tempMap);
					}
				}
				
				for(int round=1;round<=totalRounds;round++){
					for(java.util.Map.Entry<Integer, Long> i:sortedMemberPositionMap.entrySet()){
						BallotEntryVO ballotEntryVO=memberRoundBallotEntryVOMap.get(i.getValue()).get(round);
						if(ballotEntryVO!=null){
							ballotEntryVO.setSequence(sequence);
							sequence++;
							memberRoundBallotEntryVOMap.get(i.getValue()).put(round,ballotEntryVO);
						}
					}
				}

				for(java.util.Map.Entry<Integer,Long> i:sortedMemberPositionMap.entrySet()){					
					BallotEntry ballotEntry=new BallotEntry();
					ballotEntry.setLocale(locale);
					Member member=Member.findById(Member.class,i.getValue());
					ballotEntry.setMember(member);
					ballotEntry.setPosition(actualPosition);
					actualPosition++;
					List<DeviceSequence> deviceSequences=new ArrayList<DeviceSequence>();
					if(memberRoundBallotEntryVOMap.get(i.getValue())!=null){
						for(java.util.Map.Entry<Integer, BallotEntryVO> j:memberRoundBallotEntryVOMap.get(i.getValue()).entrySet()){
							DeviceSequence deviceSequence=new DeviceSequence();
							deviceSequence.setRound(j.getKey());
							deviceSequence.setLocale(locale);
							deviceSequence.setSequenceNo(j.getValue().getSequence());
							Question question=Question.findById(Question.class, j.getValue().getDeviceId());
							//update question ballot status 
							question.setBallotStatus(ballotStatus);
							question.simpleMerge();
							deviceSequence.setDevice(question);
							deviceSequence.persist();
							deviceSequences.add(deviceSequence);
						}
					}
					ballotEntry.setDeviceSequences(deviceSequences);
					ballotEntry.persist();
					ballotEntries.add(ballotEntry);
				}
				//create ballot
				Ballot ballot=new Ballot();
				ballot.setBallotDate(new Date());
				ballot.setDeviceType(deviceType);
				ballot.setGroup(group);
				ballot.setLocale(locale);
				ballot.setSession(session);
				ballot.setAnsweringDate(answeringDate);	
				ballot.setBallotEntries(ballotEntries);
				ballot.persist();				
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

	@SuppressWarnings("rawtypes")
	private Integer firstBatchCurrentAnsweringDate(final Map<String,String[]> requestMap,int position,final int totalRounds,
			final Map<Long,Integer> memberPositionMap,final Map<Long,Map<Integer,BallotEntryVO>> memberRoundBallotEntryVOMap
			) throws ParseException{
		//list of first batch questions belonging to particular answering date and sorted according to attendance,round,position,choice
		//and taken from member ballot choice
		List queryFirstBatchCurrentAnsweringDate=org.mkcl.els.domain.Query.findReport("FINAL_BALLOT_UH_FIRSTBATCHCURRENTANSWERINGDATE", requestMap);
		for(Object i:queryFirstBatchCurrentAnsweringDate){
			Object[] o=(Object[]) i;
			if(o!=null && o.length > 0){
				Long memberid=new Long(0);
				if(o[0]!=null){
					memberid=Long.parseLong(o[0].toString());
				}
				//if member is not added already then add it to memberPositionMap and increase position by 1000
				if(memberPositionMap.get(memberid)==null){
					memberPositionMap.put(memberid, position);
					position=position+1000;					
				}
				//if member is not added already then add it to memberRoundBallotEntryVOMap 
				Map<Integer,BallotEntryVO> roundBallotEntryMap=null;
				if(memberRoundBallotEntryVOMap.get(memberid)==null){
					roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
					memberRoundBallotEntryVOMap.put(memberid, roundBallotEntryMap);
				}
				//question will be added only if size of roundBallotEntryMap < total rounds
				roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberid);
				int roundBallotEntryMapSize=roundBallotEntryMap.size();
				if(roundBallotEntryMapSize <totalRounds){
					BallotEntryVO ballotEntryVO=new BallotEntryVO();
					ballotEntryVO.setMemberId(memberid);
					if(o[1]!=null){
						ballotEntryVO.setAttendance(Boolean.parseBoolean(o[1].toString()));
					}
					if(o[2]!=null){
						ballotEntryVO.setRound(Integer.parseInt(o[2].toString()));
					}
					if(o[3]!=null){
						ballotEntryVO.setPosition(Integer.parseInt(o[3].toString()));
					}
					if(o[4]!=null){
						ballotEntryVO.setChoice(Integer.parseInt(o[4].toString()));
					}
					if(o[5]!=null){
						ballotEntryVO.setDeviceId(Long.parseLong(o[5].toString()));
					}
					if(o[6]!=null){
						ballotEntryVO.setDeviceNumber(Integer.parseInt(o[6].toString()));
					}
					if(o[7]!=null){
						ballotEntryVO.setPriority(Integer.parseInt(o[7].toString()));
					}
					if(o[8]!=null){
						ballotEntryVO.setSubmissionDate(FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT, "en_US").parse(o[8].toString()));
					}
					if(o[9]!=null){
						ballotEntryVO.setChartAnsweringDate(FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATEFORMAT, "en_US").parse(o[9].toString()));
					}
					roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
				}
				memberRoundBallotEntryVOMap.put(memberid, roundBallotEntryMap);						
			}
		}	
		return position;
	}

	@SuppressWarnings("rawtypes")
	private Integer firstBatchPreviousAnsweringDate(final Map<String,String[]> requestMap,int position,final int totalRounds,
			final Map<Long,Integer> memberPositionMap,final Map<Long,Map<Integer,BallotEntryVO>> memberRoundBallotEntryVOMap
			) throws ParseException{
		//list of first batch questions belonging to previous answering date and sorted according to answering date,attendance,round,position,choice
		//and taken from member ballot choice
		List queryFirstBatchPreviousAnsweringDate=org.mkcl.els.domain.Query.findReport("FINAL_BALLOT_UH_FIRSTBATCHPREVIOUSANSWERINGDATE", requestMap);
		for(Object i:queryFirstBatchPreviousAnsweringDate){
			Object[] o=(Object[]) i;
			if(o!=null && o.length > 0){
				Long memberid=new Long(0);
				if(o[0]!=null){
					memberid=Long.parseLong(o[0].toString());
				}
				//if member is not added already then add it to memberPositionMap and increase position by 1000
				if(memberPositionMap.get(memberid)==null){
					memberPositionMap.put(memberid, position);
					position=position+1000;
				}
				//if member is not added already then add it to memberRoundBallotEntryVOMap 
				Map<Integer,BallotEntryVO> roundBallotEntryMap=null;
				if(memberRoundBallotEntryVOMap.get(memberid)==null){
					roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
					memberRoundBallotEntryVOMap.put(memberid, roundBallotEntryMap);
				}
				//question will be added only if size of roundBallotEntryMap < total rounds
				roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberid);
				int roundBallotEntryMapSize=roundBallotEntryMap.size();
				if(roundBallotEntryMapSize <totalRounds){
					BallotEntryVO ballotEntryVO=new BallotEntryVO();
					ballotEntryVO.setMemberId(memberid);
					if(o[1]!=null){
						ballotEntryVO.setAttendance(Boolean.parseBoolean(o[1].toString()));
					}
					if(o[2]!=null){
						ballotEntryVO.setRound(Integer.parseInt(o[2].toString()));
					}
					if(o[3]!=null){
						ballotEntryVO.setPosition(Integer.parseInt(o[3].toString()));
					}
					if(o[4]!=null){
						ballotEntryVO.setChoice(Integer.parseInt(o[4].toString()));
					}
					if(o[5]!=null){
						ballotEntryVO.setDeviceId(Long.parseLong(o[5].toString()));
					}
					if(o[6]!=null){
						ballotEntryVO.setDeviceNumber(Integer.parseInt(o[6].toString()));
					}
					if(o[7]!=null){
						ballotEntryVO.setPriority(Integer.parseInt(o[7].toString()));
					}
					if(o[8]!=null){
						ballotEntryVO.setSubmissionDate(FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT, "en_US").parse(o[8].toString()));
					}
					if(o[9]!=null){
						ballotEntryVO.setChartAnsweringDate(FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATEFORMAT, "en_US").parse(o[9].toString()));
					}
					roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
				}
				memberRoundBallotEntryVOMap.put(memberid, roundBallotEntryMap);						
			}
		}	
		return position;
	}

//	@SuppressWarnings("rawtypes")
//	private Integer secondBatchCurrentAnsweringDate(final Map<String,String[]> requestMap,int position,final int totalRounds,
//			final Map<Long,Integer> memberPositionMap,final Map<Long,Map<Integer,BallotEntryVO>> memberRoundBallotEntryVOMap			
//			) throws ParseException{
//		//list of second batch questions belonging to particular answering date and sorted according to number
//		//and taken from questions
//		List querySecondBatchCurrentAnsweringDate=org.mkcl.els.domain.Query.findReport("FINAL_BALLOT_UH_SECONDBATCHCURRENTANSWERINGDATE", requestMap);
//		for(Object i:querySecondBatchCurrentAnsweringDate){
//			Object[] o=(Object[]) i;
//			if(o!=null && o.length > 0){
//				Long memberid=new Long(0);
//				if(o[0]!=null){
//					memberid=Long.parseLong(o[0].toString());
//				}
//				//if member is not added already then add it to memberPositionMap and increase position by 1000
//				if(memberPositionMap.get(memberid)==null){
//					memberPositionMap.put(memberid, position);					
//					position=position+1000;
//				}
//				//if member is not added already then add it to memberRoundBallotEntryVOMap 
//				Map<Integer,BallotEntryVO> roundBallotEntryMap=null;
//				if(memberRoundBallotEntryVOMap.get(memberid)==null){
//					roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
//					memberRoundBallotEntryVOMap.put(memberid, roundBallotEntryMap);
//				}
//				//question will be added only if size of roundBallotEntryMap < total rounds
//				roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberid);
//				int roundBallotEntryMapSize=roundBallotEntryMap.size();
//				if(roundBallotEntryMapSize <totalRounds){
//					BallotEntryVO ballotEntryVO=new BallotEntryVO();
//					ballotEntryVO.setMemberId(memberid);
//					if(o[1]!=null){
//						ballotEntryVO.setDeviceId(Long.parseLong(o[1].toString()));
//					}
//					if(o[2]!=null){
//						ballotEntryVO.setDeviceNumber(Integer.parseInt(o[2].toString()));
//					}
//					if(o[3]!=null){
//						ballotEntryVO.setPriority(Integer.parseInt(o[3].toString()));
//					}
//					if(o[4]!=null){
//						ballotEntryVO.setSubmissionDate(FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT, "en_US").parse(o[4].toString()));
//					}
//					if(o[5]!=null){
//						ballotEntryVO.setChartAnsweringDate(FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATEFORMAT, "en_US").parse(o[5].toString()));
//					}
//					roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
//				}
//				memberRoundBallotEntryVOMap.put(memberid, roundBallotEntryMap);						
//			}
//		}		
//		return position;
//	}
	
	// NEWLY ADDED
	@SuppressWarnings("rawtypes")
	private List<BallotEntryVO> deserialize(final List secondBatchQuestions) {
		List<BallotEntryVO> ballotEntryVOs = new ArrayList<BallotEntryVO>();
		
		for(Object i : secondBatchQuestions) {
			BallotEntryVO ballotEntryVO = new BallotEntryVO();
			
			Object[] o = (Object[]) i;
			if(o != null && o.length > 0) {
				if(o[0] != null) {
					Long memberId = Long.parseLong(o[0].toString());
					ballotEntryVO.setMemberId(memberId);
				}
				
				if(o[1] != null) {
					Long deviceId = Long.parseLong(o[1].toString());
					ballotEntryVO.setDeviceId(deviceId);
				}
				
				if(o[2] != null) {
					Integer deviceNumber = Integer.parseInt(o[2].toString());
					ballotEntryVO.setDeviceNumber(deviceNumber);
				}
				
				if(o[3] != null) {
					Integer priority = Integer.parseInt(o[3].toString());
					ballotEntryVO.setPriority(priority);
				}
				
				if(o[4] != null) {
					String strSubmissionDate = o[4].toString();
					Date submissionDate = 
							FormaterUtil.formatStringToDate(strSubmissionDate, ApplicationConstants.DB_DATETIME_FORMAT);
					ballotEntryVO.setSubmissionDate(submissionDate);
				}
				
				if(o[5] != null) {
					String strChartAnsweringDate = o[5].toString();
					Date chartAnsweringDate = 
							FormaterUtil.formatStringToDate(strChartAnsweringDate, ApplicationConstants.DB_DATEFORMAT);
					ballotEntryVO.setChartAnsweringDate(chartAnsweringDate);
				}
			}
			
			ballotEntryVOs.add(ballotEntryVO);
		}
		
		return ballotEntryVOs;
 	}
	
	// NEWLY ADDED
	private List<FinalBallotVO> computeFinalBallotVO(final List<BallotEntryVO> ballotEntryVOs) {
		List<FinalBallotVO> finalBallotVOs = new ArrayList<FinalBallotVO>();
		
		int size = ballotEntryVOs.size();
		int counter = 0;
		FinalBallotVO finalBallotVO = new FinalBallotVO();
		for(BallotEntryVO be : ballotEntryVOs) {
			++counter;
			if(finalBallotVO.getMemberId() == null) {
				finalBallotVO.setMemberId(be.getMemberId());
				finalBallotVO.setFinalBallotEntryVOs(new ArrayList<BallotEntryVO>());
				finalBallotVO.getBallotEntryVOs().add(be);
			}
			else if(finalBallotVO.getMemberId().equals(be.getMemberId())) {
				finalBallotVO.getBallotEntryVOs().add(be);
			}
			else { // ! finalBallotVO.getMemberId().equals(be.getMemberId())
				finalBallotVOs.add(finalBallotVO);
				finalBallotVO = new FinalBallotVO();
				finalBallotVO.setMemberId(be.getMemberId());
				finalBallotVO.setFinalBallotEntryVOs(new ArrayList<BallotEntryVO>());
				finalBallotVO.getBallotEntryVOs().add(be);
			}
			
			if(counter == size) {
				finalBallotVOs.add(finalBallotVO);
			}
		}
		
		return sortByFirstRoundQuestions(finalBallotVOs);
	}
	
	// NEWLY ADDED
	private List<FinalBallotVO> sortByFirstRoundQuestions(final List<FinalBallotVO> finalBallotVOs) {
		List<FinalBallotVO> finalBallotVOList = new ArrayList<FinalBallotVO>();
		finalBallotVOList.addAll(finalBallotVOs);
		
		Comparator<FinalBallotVO> c = new Comparator<FinalBallotVO>() {

			@Override
			public int compare(FinalBallotVO fb1, FinalBallotVO fb2) {
				BallotEntryVO ballotEntryVO1 = fb1.getBallotEntryVOs().get(0);
				Integer device1 = ballotEntryVO1.getDeviceNumber();
				
				BallotEntryVO ballotEntryVO2 = fb2.getBallotEntryVOs().get(0);
				Integer device2 = ballotEntryVO2.getDeviceNumber();
				
				return device1.compareTo(device2);
			}
			
		};
		Collections.sort(finalBallotVOList, c);
		
		return finalBallotVOList;
	}
	
	// NEWLY ADDED
	@SuppressWarnings("rawtypes")
	private List serialize(final List<FinalBallotVO> finalBallotVOs) {
		List<Object[]> objArrList = new ArrayList<Object[]>();
		
		for(FinalBallotVO finalBallotVO : finalBallotVOs) {
			List<BallotEntryVO> ballotEntryVOs = finalBallotVO.getBallotEntryVOs();
			for(BallotEntryVO be : ballotEntryVOs) {
				List<Object> objList = new ArrayList<Object>();
				objList.add(be.getMemberId().toString());
				objList.add(be.getDeviceId().toString());
				objList.add(be.getDeviceNumber().toString());
				objList.add(be.getPriority().toString());
				
				Date submissionDate = be.getSubmissionDate();
				String strSubmissionDate = 
						FormaterUtil.formatDateToString(submissionDate, ApplicationConstants.DB_DATETIME_FORMAT);
				objList.add(strSubmissionDate);
				
				Date chartAnsweringDate = be.getChartAnsweringDate();
				String strChartAnsweringDate = 
						FormaterUtil.formatDateToString(chartAnsweringDate, ApplicationConstants.DB_DATEFORMAT);
				objList.add(strChartAnsweringDate);
				
				objArrList.add(objList.toArray());
			}
		}
		
		return objArrList;
	}
	
	@SuppressWarnings("rawtypes")
	private Integer secondBatchCurrentAnsweringDate(final Map<String,String[]> requestMap, 
			int position, 
			final int totalRounds,
			final Map<Long,Integer> memberPositionMap, 
			final Map<Long,Map<Integer,BallotEntryVO>> memberRoundBallotEntryVOMap) throws ParseException{
		//list of second batch questions belonging to particular answering date and sorted according to number
		//and taken from questions
		List querySecondBatchCurrentAnsweringDate=org.mkcl.els.domain.Query.findReport("FINAL_BALLOT_UH_SECONDBATCHCURRENTANSWERINGDATE", requestMap);
		
		// Newly Added START======================
		List<BallotEntryVO> ballotEntryVOs = deserialize(querySecondBatchCurrentAnsweringDate);
		List<FinalBallotVO> finalBallotVOs = computeFinalBallotVO(ballotEntryVOs);
		List secondBatchQuestions = serialize(finalBallotVOs);
		// Newly Added END========================
		
		for(Object i:secondBatchQuestions){
			Object[] o=(Object[]) i;
			if(o!=null && o.length > 0){
				Long memberid=new Long(0);
				if(o[0]!=null){
					memberid=Long.parseLong(o[0].toString());
				}
				//if member is not added already then add it to memberPositionMap and increase position by 1000
				if(memberPositionMap.get(memberid)==null){
					memberPositionMap.put(memberid, position);					
					position=position+1000;
				}
				//if member is not added already then add it to memberRoundBallotEntryVOMap 
				Map<Integer,BallotEntryVO> roundBallotEntryMap=null;
				if(memberRoundBallotEntryVOMap.get(memberid)==null){
					roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
					memberRoundBallotEntryVOMap.put(memberid, roundBallotEntryMap);
				}
				//question will be added only if size of roundBallotEntryMap < total rounds
				roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberid);
				int roundBallotEntryMapSize=roundBallotEntryMap.size();
				if(roundBallotEntryMapSize <totalRounds){
					BallotEntryVO ballotEntryVO=new BallotEntryVO();
					ballotEntryVO.setMemberId(memberid);
					if(o[1]!=null){
						ballotEntryVO.setDeviceId(Long.parseLong(o[1].toString()));
					}
					if(o[2]!=null){
						ballotEntryVO.setDeviceNumber(Integer.parseInt(o[2].toString()));
					}
					if(o[3]!=null){
						ballotEntryVO.setPriority(Integer.parseInt(o[3].toString()));
					}
					if(o[4]!=null){
						ballotEntryVO.setSubmissionDate(FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT, "en_US").parse(o[4].toString()));
					}
					if(o[5]!=null){
						ballotEntryVO.setChartAnsweringDate(FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATEFORMAT, "en_US").parse(o[5].toString()));
					}
					roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
				}
				memberRoundBallotEntryVOMap.put(memberid, roundBallotEntryMap);						
			}
		}		
		return position;
	}

//	@SuppressWarnings("rawtypes")
//	private Integer secondBatchPreviousAnsweringDate(final Map<String,String[]> requestMap,int position,final int totalRounds,
//			final Map<Long,Integer> memberPositionMap,final Map<Long,Map<Integer,BallotEntryVO>> memberRoundBallotEntryVOMap
//			) throws ParseException{
//		//list of second batch questions belonging to particular answering date and sorted according to answering date and number
//		//and taken from questions
//		List querySecondBatchPreviousAnsweringDate=org.mkcl.els.domain.Query.findReport("FINAL_BALLOT_UH_SECONDBATCHPREVIOUSANSWERINGDATE", requestMap);
//		for(Object i:querySecondBatchPreviousAnsweringDate){
//			Object[] o=(Object[]) i;
//			if(o!=null && o.length > 0){
//				Long memberid=new Long(0);
//				if(o[0]!=null){
//					memberid=Long.parseLong(o[0].toString());
//				}
//				//if member is not added already then add it to memberPositionMap and increase position by 1000
//				if(memberPositionMap.get(memberid)==null){
//					memberPositionMap.put(memberid, position);
//					position=position+1000;
//				}
//				//if member is not added already then add it to memberRoundBallotEntryVOMap 
//				Map<Integer,BallotEntryVO> roundBallotEntryMap=null;
//				if(memberRoundBallotEntryVOMap.get(memberid)==null){
//					roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
//					memberRoundBallotEntryVOMap.put(memberid, roundBallotEntryMap);
//				}
//				//question will be added only if size of roundBallotEntryMap < total rounds
//				roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberid);
//				int roundBallotEntryMapSize=roundBallotEntryMap.size();
//				if(roundBallotEntryMapSize <totalRounds){
//					BallotEntryVO ballotEntryVO=new BallotEntryVO();
//					ballotEntryVO.setMemberId(memberid);
//					if(o[1]!=null){
//						ballotEntryVO.setDeviceId(Long.parseLong(o[1].toString()));
//					}
//					if(o[2]!=null){
//						ballotEntryVO.setDeviceNumber(Integer.parseInt(o[2].toString()));
//					}
//					if(o[3]!=null){
//						ballotEntryVO.setPriority(Integer.parseInt(o[3].toString()));
//					}
//					if(o[4]!=null){
//						ballotEntryVO.setSubmissionDate(FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT, "en_US").parse(o[4].toString()));
//					}
//					if(o[5]!=null){
//						ballotEntryVO.setChartAnsweringDate(FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATEFORMAT, "en_US").parse(o[5].toString()));
//					}
//					roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
//				}
//				memberRoundBallotEntryVOMap.put(memberid, roundBallotEntryMap);						
//			}
//		}	
//		return position;
//	}

	private List<BallotEntryVO> membersWithFIrstBatchLastRoundQuestionsPresent(final Session session,
			final DeviceType deviceType,final Map<String, String[]> requestMap,
			int position,final int totalRounds,
			final Map<Long, Integer> memberPositionMap,
			final Map<Long, Map<Integer, BallotEntryVO>> memberRoundBallotEntryVOMap,
			List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundPresent,
			List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundAbsent,
			Long lastMemberFirstBatch,
			final int totalRoundsInMemberBallot,
			final String locale) throws ELSException {
		/****Members who have given questions in first batch last round only ****/
		for(java.util.Map.Entry<Long, Map<Integer, BallotEntryVO>> i:memberRoundBallotEntryVOMap.entrySet()){
			boolean containsQuestionsFromLesserRoundPresent=false;
			boolean containsQuestionsFromAbsent=false;
			for(java.util.Map.Entry<Integer, BallotEntryVO> j:i.getValue().entrySet()){
				if(j.getValue().isAttendance() 
						&& (j.getValue().getRound().compareTo(totalRoundsInMemberBallot) < 0)){
					containsQuestionsFromLesserRoundPresent=true;
					break;
				}else if(!j.getValue().isAttendance()){
					containsQuestionsFromAbsent=true;
				}
			}			
			if(!containsQuestionsFromLesserRoundPresent && !containsQuestionsFromAbsent){
				Member member=Member.findById(Member.class, i.getKey());
				MemberBallot memberBallot=MemberBallot.findByMemberRound(session, deviceType, member, totalRoundsInMemberBallot, locale);
				BallotEntryVO ballotEntryVO=new BallotEntryVO();
				ballotEntryVO.setMemberId(i.getKey());
				ballotEntryVO.setRound(memberBallot.getRound());
				ballotEntryVO.setPosition(memberBallot.getPosition());
				ballotEntryVO.setAttendance(memberBallot.getAttendance());
				membersWithQuestionsOnlyInLastRoundPresent.add(ballotEntryVO);
			}
		}
		return membersWithQuestionsOnlyInLastRoundPresent;
	}

	private List<BallotEntryVO> membersWithFIrstBatchLastRoundQuestionsAbsent(final Session session,
			final DeviceType deviceType,final Map<String, String[]> requestMap,
			int position,final int totalRounds,
			final Map<Long, Integer> memberPositionMap,
			final Map<Long, Map<Integer, BallotEntryVO>> memberRoundBallotEntryVOMap,
			List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundPresent,
			List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundAbsent,
			Long lastMemberFirstBatch,
			final int totalRoundsInMemberBallot,
			final String locale) throws ELSException {
		/****Members who have given questions in first batch last round only ****/
		for(java.util.Map.Entry<Long, Map<Integer, BallotEntryVO>> i:memberRoundBallotEntryVOMap.entrySet()){
			boolean containsQuestionsFromLesserRoundAbsent=false;	
			boolean containsQuestionsFromPresent=true;
			for(java.util.Map.Entry<Integer, BallotEntryVO> j:i.getValue().entrySet()){
				if(!j.getValue().isAttendance() 
						&& (j.getValue().getRound().compareTo(totalRoundsInMemberBallot) < 0)){
					containsQuestionsFromLesserRoundAbsent=true;
					break;
				}else if(j.getValue().isAttendance()){
					containsQuestionsFromPresent=true;
					break;
				}
			}					
			if(!containsQuestionsFromLesserRoundAbsent && !containsQuestionsFromPresent){
				Member member=Member.findById(Member.class, i.getKey());
				MemberBallot memberBallot=MemberBallot.findByMemberRound(session, deviceType, member, totalRoundsInMemberBallot, locale);
				BallotEntryVO ballotEntryVO=new BallotEntryVO();
				ballotEntryVO.setMemberId(i.getKey());
				ballotEntryVO.setRound(memberBallot.getRound());
				ballotEntryVO.setPosition(memberBallot.getPosition());
				ballotEntryVO.setAttendance(memberBallot.getAttendance());
				membersWithQuestionsOnlyInLastRoundAbsent.add(ballotEntryVO);
			}
		}
		return membersWithQuestionsOnlyInLastRoundAbsent;
	}

	private Long lastMember(final Map<Long, Integer> memberPositionMap){
		Long lastMember=new Long(0);
		if(memberPositionMap!=null && memberPositionMap.size()>0){
			int size=memberPositionMap.size();
			int count=1;
			for(java.util.Map.Entry<Long, Integer> i:memberPositionMap.entrySet()){
				if(size==count){
					lastMember=i.getKey();
				}
				count++;
			}
		}
		return lastMember;
	}

	private List<BallotEntryVO> membersWithSecondBatchQuestionsOnly(final Session session,
			final DeviceType deviceType,final Map<String, String[]> requestMap,
			int position,final int totalRounds,
			final Map<Long, Integer> memberPositionMap,
			final Map<Long, Map<Integer, BallotEntryVO>> memberRoundBallotEntryVOMap,
			List<BallotEntryVO> membersWithQuestionsOnlyInSecondBatch,			
			Long lastMemberSecondBatch,
			final String locale) {		
		/****Members who have given questions in second batch only ****/
		for(java.util.Map.Entry<Long, Map<Integer, BallotEntryVO>> i:memberRoundBallotEntryVOMap.entrySet()){
			int count=1;
			int number=0;
			boolean containsQuestionsFromFirstBatch=false;
			for(java.util.Map.Entry<Integer, BallotEntryVO> j:i.getValue().entrySet()){
				if(count==1){
					number=j.getValue().getDeviceNumber();
				}
				count++;
				if(j.getValue().getRound()!=null 
						&& j.getValue().getRound() !=null){
					containsQuestionsFromFirstBatch=true;					
					break;
				}					
			}
			if(!containsQuestionsFromFirstBatch){
				BallotEntryVO ballotEntryVO=new BallotEntryVO();
				ballotEntryVO.setMemberId(i.getKey());
				ballotEntryVO.setDeviceNumber(number);
				membersWithQuestionsOnlyInSecondBatch.add(ballotEntryVO);
			}				
		}
		return membersWithQuestionsOnlyInSecondBatch;
	}

	@SuppressWarnings("rawtypes")
	private void inactiveMemberFirstBatchCurrentAnsweringDate(
			final Map<String, String[]> requestMap,
			int position,
			final int totalRounds,
			final Map<Long, Integer> memberPositionMap,
			final Map<Long, Map<Integer, BallotEntryVO>> memberRoundBallotEntryVOMap,
			final Session session,
			final DeviceType deviceType,
			final String locale,
			final int totalRoundsInMemberBallot,
			final Date firstBatchSubmissionEndDateDate,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundPresent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundAbsent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInSecondBatch,
			final Long lastMemberFirstBatch,
			final Long lastMemberSecondBatch) throws ELSException {
		//list of inactive members who gave question in first batch on current answering date
		List queryInactiveMemberCurrentAnsweringDate=org.mkcl.els.domain.Query.findReport("FINAL_BALLOT_UH_INACTIVEMEMBERFIRSTBATCHCURRENTANSWERINGDATE", requestMap);
		for(Object i:queryInactiveMemberCurrentAnsweringDate){
			Object[] o=(Object[]) i;
			if(o!=null){
				Question question=Question.findById(Question.class,Long.parseLong(o[1].toString()));	
				boolean allocated=inactiveMemberQuestionSupportingMember(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
						session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
						,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
						,lastMemberFirstBatch,lastMemberSecondBatch,question);
				if(!allocated){
					allocated=inactiveMemberQuestionClubbedMember(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
							session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
							,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
							,lastMemberFirstBatch,lastMemberSecondBatch,question);
				}
			}
		}
	}	

	@SuppressWarnings("rawtypes")
	private void inactiveMemberFirstBatchPreviousAnsweringDate(
			final Map<String, String[]> requestMap,
			int position,
			final int totalRounds,
			final Map<Long, Integer> memberPositionMap,
			final Map<Long, Map<Integer, BallotEntryVO>> memberRoundBallotEntryVOMap,
			final Session session,
			final DeviceType deviceType,
			final String locale,
			final int totalRoundsInMemberBallot,
			final Date firstBatchSubmissionEndDateDate,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundPresent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundAbsent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInSecondBatch,
			final Long lastMemberFirstBatch,
			final Long lastMemberSecondBatch) throws ELSException {
		//list of inactive members who gave question in first batch on previous answering date
		List queryInactiveMemberPreviousAnsweringDate=org.mkcl.els.domain.Query.findReport("FINAL_BALLOT_UH_INACTIVEMEMBERFIRSTBATCHPREVIOUSANSWERINGDATE", requestMap);
		for(Object i:queryInactiveMemberPreviousAnsweringDate){
			Object[] o=(Object[]) i;
			if(o!=null){
				Question question=Question.findById(Question.class,Long.parseLong(o[1].toString()));	
				boolean allocated=inactiveMemberQuestionSupportingMember(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
						session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
						,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
						,lastMemberFirstBatch,lastMemberSecondBatch,question);
				if(!allocated){
					allocated=inactiveMemberQuestionClubbedMember(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
							session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
							,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
							,lastMemberFirstBatch,lastMemberSecondBatch,question);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void inactiveMemberSecondBatchCurrentAnsweringDate(
			final Map<String, String[]> requestMap,
			int position,
			final int totalRounds,
			final Map<Long, Integer> memberPositionMap,
			final Map<Long, Map<Integer, BallotEntryVO>> memberRoundBallotEntryVOMap,
			final Session session,
			final DeviceType deviceType,
			final String locale,
			final int totalRoundsInMemberBallot,
			final Date firstBatchSubmissionEndDateDate,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundPresent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundAbsent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInSecondBatch,
			final Long lastMemberFirstBatch,
			final Long lastMemberSecondBatch) throws ELSException {
		//list of inactive members who gave question in first batch on current answering date
		List queryInactiveMemberCurrentAnsweringDate=org.mkcl.els.domain.Query.findReport("FINAL_BALLOT_UH_INACTIVEMEMBERSECONDBATCHCURRENTANSWERINGDATE", requestMap);
		for(Object i:queryInactiveMemberCurrentAnsweringDate){
			Object[] o=(Object[]) i;
			if(o!=null){
				Question question=Question.findById(Question.class,Long.parseLong(o[1].toString()));	
				boolean allocated=inactiveMemberQuestionSupportingMember(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
						session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
						,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
						,lastMemberFirstBatch,lastMemberSecondBatch,question);
				if(!allocated){
					allocated=inactiveMemberQuestionClubbedMember(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
							session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
							,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
							,lastMemberFirstBatch,lastMemberSecondBatch,question);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void inactiveMemberSecondBatchPreviousAnsweringDate(
			final Map<String, String[]> requestMap,
			int position,
			final int totalRounds,
			final Map<Long, Integer> memberPositionMap,
			final Map<Long, Map<Integer, BallotEntryVO>> memberRoundBallotEntryVOMap,
			final Session session,
			final DeviceType deviceType,
			final String locale,
			final int totalRoundsInMemberBallot,
			final Date firstBatchSubmissionEndDateDate,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundPresent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundAbsent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInSecondBatch,
			final Long lastMemberFirstBatch,
			final Long lastMemberSecondBatch) throws ELSException {
		//list of inactive members who gave question in first batch on current answering date
		List queryInactiveMemberCurrentAnsweringDate=org.mkcl.els.domain.Query.findReport("FINAL_BALLOT_UH_INACTIVEMEMBERSECONDBATCHPREVIOUSANSWERINGDATE", requestMap);
		for(Object i:queryInactiveMemberCurrentAnsweringDate){
			Object[] o=(Object[]) i;
			if(o!=null){
				Question question=Question.findById(Question.class,Long.parseLong(o[1].toString()));	
				boolean allocated=inactiveMemberQuestionSupportingMember(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
						session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
						,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
						,lastMemberFirstBatch,lastMemberSecondBatch,question);
				if(!allocated){
					allocated=inactiveMemberQuestionClubbedMember(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
							session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
							,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
							,lastMemberFirstBatch,lastMemberSecondBatch,question);
				}
			}
		}
	}

	private boolean inactiveMemberQuestionSupportingMember(
			final Map<String, String[]> requestMap,
			int position,
			final int totalRounds,
			final Map<Long, Integer> memberPositionMap,
			final Map<Long, Map<Integer, BallotEntryVO>> memberRoundBallotEntryVOMap,
			final Session session,
			final DeviceType deviceType,
			final String locale,
			final int totalRoundsInMemberBallot,
			final Date firstBatchSubmissionEndDateDate,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundPresent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundAbsent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInSecondBatch,
			final Long lastMemberFirstBatch,
			final Long lastMemberSecondBatch,
			final Question question) throws ELSException {
		List<SupportingMember> supportingMembers=question.getSupportingMembers();
		boolean allocated=false;		
		Date currentDate=new Date();
		if(supportingMembers!=null && !supportingMembers.isEmpty()){
			for(SupportingMember s:supportingMembers){
				Member member=s.getMember();
				boolean allowed=false;
//				if(!member.isPresentInMemberBallotAttendanceUH(session,deviceType,locale)
//						&& member.isActiveMemberOn(currentDate, locale)
//						&& question.containsClubbingFromSecondBatch(session,member,locale)
//						){
//					allowed=true;
//				}else if( member.isPresentInMemberBallotAttendanceUH(session,deviceType,locale)
//						&& member.isActiveMemberOn(currentDate, locale)
//						&& !question.containsClubbingFromSecondBatch(session,member,locale)){		
//					allowed=true;
//				}else if( member.isPresentInMemberBallotAttendanceUH(session,deviceType,locale)
//						&& member.isActiveMemberOn(currentDate, locale)
//						&& question.containsClubbingFromSecondBatch(session,member,locale)){		
//					allowed=true;
//				}
				MemberRole memberRole=MemberRole.find(session.getHouse().getType(), ApplicationConstants.MEMBER, locale);
				HouseMemberRoleAssociation hmra=Member.find(member,memberRole,currentDate,locale);
				if(hmra!=null){
					boolean isMemberAllowed=isMemberAllowed(hmra,question);
					if(isMemberAllowed){
						boolean isActivePresidingOfficer=member.isActiveMemberInAnyOfGivenRolesOn(ApplicationConstants.NON_MEMBER_ROLES.split(","),currentDate, locale);
						if(!isActivePresidingOfficer){
							boolean isMinister=member.isActiveMinisterOn(currentDate, locale);
							if(!isMinister){
								allowed=true;
							}
						}
					}
				}
				
			
				if(allowed){
					allocated=allocateQuestionToMember(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
							session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
							,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
							,lastMemberFirstBatch,lastMemberSecondBatch,question,member);
					if(allocated){
						return allocated;
					}
				}			
			}
		}
		return allocated;
	}	
	
	private Boolean isMemberAllowed(HouseMemberRoleAssociation hmra,Question question){
		if(hmra!=null && question!=null){
			if(hmra.getFromDate().before(question.getSubmissionDate())
				&& hmra.getToDate().after(question.getSubmissionDate())){
				return true;
			}
		}
		return false;
	}
	
	
	private boolean inactiveMemberQuestionClubbedMember(
			final Map<String, String[]> requestMap,
			int position,
			final int totalRounds,
			final Map<Long, Integer> memberPositionMap,
			final Map<Long, Map<Integer, BallotEntryVO>> memberRoundBallotEntryVOMap,
			final Session session,
			final DeviceType deviceType,
			final String locale,
			final int totalRoundsInMemberBallot,
			final Date firstBatchSubmissionEndDateDate,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundPresent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundAbsent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInSecondBatch,
			final Long lastMemberFirstBatch,
			final Long lastMemberSecondBatch,
			final Question question) throws ELSException {
		List<ClubbedEntity> clubbedEntities=question.getClubbedEntities();
		boolean allocated=false;		
		Date currentDate=new Date();
		if(clubbedEntities!=null && !clubbedEntities.isEmpty()){
			for(ClubbedEntity i:clubbedEntities){
				Member member=i.getQuestion().getPrimaryMember();
				boolean allowed=false;
//				if(!member.isPresentInMemberBallotAttendanceUH(session,deviceType,locale)
//						&& member.isActiveMemberOn(currentDate, locale)
//						&& question.containsClubbingFromSecondBatch(session,member,locale)
//						){
//					allowed=true;
//				}else if( member.isPresentInMemberBallotAttendanceUH(session,deviceType,locale)
//						&& member.isActiveMemberOn(currentDate, locale)
//						&& !question.containsClubbingFromSecondBatch(session,member,locale)){		
//					allowed=true;
//				}else if( member.isPresentInMemberBallotAttendanceUH(session,deviceType,locale)
//						&& member.isActiveMemberOn(currentDate, locale)
//						&& question.containsClubbingFromSecondBatch(session,member,locale)){		
//					allowed=true;
//				}	
				MemberRole memberRole=MemberRole.find(session.getHouse().getType(), ApplicationConstants.MEMBER, locale);
				HouseMemberRoleAssociation hmra=Member.find(member,memberRole,currentDate,locale);
				if(hmra!=null){
					boolean isMemberAllowed=isMemberAllowed(hmra,question);
					if(isMemberAllowed){
						boolean isActivePresidingOfficer=member.isActiveMemberInAnyOfGivenRolesOn(ApplicationConstants.NON_MEMBER_ROLES.split(","), currentDate, locale);
						if(!isActivePresidingOfficer){
							boolean isMinister=member.isActiveMinisterOn(currentDate, locale);
							if(!isMinister){
								allowed=true;
							}
						}
					}
				}
				
				
				if(allowed){
					allocated=allocateQuestionToMember(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
							session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
							,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
							,lastMemberFirstBatch,lastMemberSecondBatch,question,member);
				}				
				if(allocated){
					return allocated;
				}else{
					List<SupportingMember> supportingMembers=i.getQuestion().getSupportingMembers();
					if(supportingMembers!=null && !supportingMembers.isEmpty()){
						for(SupportingMember s:supportingMembers){
							Member supportingMember=s.getMember();
							boolean supportingAllowed=false;
//							if(!supportingMember.isPresentInMemberBallotAttendanceUH(session,deviceType,locale)
//									&& supportingMember.isActiveMemberOn(currentDate, locale)
//									&& question.containsClubbingFromSecondBatch(session,supportingMember,locale)
//									){
//								supportingAllowed=true;
//							}else if( supportingMember.isPresentInMemberBallotAttendanceUH(session,deviceType,locale)
//									&& supportingMember.isActiveMemberOn(currentDate, locale)
//									&& !question.containsClubbingFromSecondBatch(session,supportingMember,locale)){		
//								supportingAllowed=true;
//							}else if( supportingMember.isPresentInMemberBallotAttendanceUH(session,deviceType,locale)
//									&& supportingMember.isActiveMemberOn(currentDate, locale)
//									&& question.containsClubbingFromSecondBatch(session,supportingMember,locale)){		
//								supportingAllowed=true;
//							}	
							//MemberRole memberRole=MemberRole.find(session.getHouse().getType(), ApplicationConstants.MEMBER, locale);
							HouseMemberRoleAssociation supportingHmra=Member.find(supportingMember,memberRole,currentDate,locale);
							if(hmra!=null){
								boolean isSupportingMemberAllowed=isMemberAllowed(supportingHmra,question);
								if(isSupportingMemberAllowed){
									boolean isActivePresidingOfficer=supportingMember.isActiveMemberInAnyOfGivenRolesOn(ApplicationConstants.NON_MEMBER_ROLES.split(","),currentDate, locale);
									if(!isActivePresidingOfficer){
										boolean isMinister=supportingMember.isActiveMinisterOn(currentDate, locale);
										if(!isMinister){
											supportingAllowed=true;
										}
									}
								}
							}
							if(supportingAllowed){
								allocated=allocateQuestionToMember(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
										session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
										,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
										,lastMemberFirstBatch,lastMemberSecondBatch,question,supportingMember);
								if(allocated){
									return allocated;
								}
							}
						}
					}
				}
			}
		}
		return allocated;
	}	

	private boolean allocateQuestionToMember(
			final Map<String, String[]> requestMap,
			int position,
			final int totalRounds,
			final Map<Long, Integer> memberPositionMap,
			final Map<Long, Map<Integer, BallotEntryVO>> memberRoundBallotEntryVOMap,
			final Session session,
			final DeviceType deviceType,
			final String locale,
			final int totalRoundsInMemberBallot,
			final Date firstBatchSubmissionEndDateDate,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundPresent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundAbsent,
			final List<BallotEntryVO> membersWithQuestionsOnlyInSecondBatch,
			final Long lastMemberFirstBatch,
			final Long lastMemberSecondBatch,
			final Question question,
			final Member member) throws ELSException {
		Long memberId=member.getId();
		Integer questionNumber=question.getNumber();
		boolean allocated=false;
		/**** if member is either present in first batch or second batch and it has an empty slot ****/
		if(memberPositionMap.get(memberId)!=null 
				&& memberRoundBallotEntryVOMap.get(memberId)!=null 
				&& memberRoundBallotEntryVOMap.get(memberId).size() < totalRounds){
			Map<Integer,BallotEntryVO> roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberId);
			int roundBallotEntryMapSize=0;
			if(roundBallotEntryMap!=null){
				roundBallotEntryMapSize=roundBallotEntryMap.size();
			}else{
				roundBallotEntryMapSize=0;
				roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
			}
			BallotEntryVO ballotEntryVO=new BallotEntryVO();
			ballotEntryVO.setMemberId(memberId);					
			ballotEntryVO.setDeviceId(question.getId());					
			ballotEntryVO.setDeviceNumber(question.getNumber());				
			ballotEntryVO.setPriority(question.getPriority());
			ballotEntryVO.setSubmissionDate(question.getSubmissionDate());
			ballotEntryVO.setChartAnsweringDate(question.getChartAnsweringDate().getAnsweringDate());
			roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
			memberRoundBallotEntryVOMap.put(memberId, roundBallotEntryMap);	
			allocated= true;
		}
		/**** if supporting member is not found then it can be added in the memberPositionMap and memberRoundBallotEntryVOMap ****/
		else if(memberPositionMap.get(memberId)==null 
				&& memberRoundBallotEntryVOMap.get(memberId)==null){
			MemberBallot memberBallot=MemberBallot.findByMemberRound(session, deviceType, member, totalRoundsInMemberBallot, locale);
			/**** supporting member gave questions in first batch i.e it has entry in member ballot fifth round(present/absent) ****/
			if(memberBallot!=null){
				Long oldMemberId=null;
				/**** Present Member(5th round) ****/
				if(memberBallot.getAttendance()){
					BallotEntryVO ballotEntryVO=new BallotEntryVO();
					ballotEntryVO.setMemberId(memberId);									
					ballotEntryVO.setDeviceId(question.getId());									
					ballotEntryVO.setDeviceNumber(question.getNumber());									
					ballotEntryVO.setPriority(question.getPriority());	
					ballotEntryVO.setSubmissionDate(question.getSubmissionDate());
					ballotEntryVO.setChartAnsweringDate(question.getChartAnsweringDate().getAnsweringDate());
					ballotEntryVO.setAttendance(memberBallot.getAttendance());
					ballotEntryVO.setRound(memberBallot.getRound());
					ballotEntryVO.setPosition(memberBallot.getPosition());
					if(membersWithQuestionsOnlyInLastRoundPresent!=null && !membersWithQuestionsOnlyInLastRoundPresent.isEmpty()){
						membersWithQuestionsOnlyInLastRoundPresent.add(ballotEntryVO);
						Collections.sort(membersWithQuestionsOnlyInLastRoundPresent,new BallotEntryAttRoundPosComparator());
						for(int i=0;i<membersWithQuestionsOnlyInLastRoundPresent.size();i++){
							if(membersWithQuestionsOnlyInLastRoundPresent.get(i).getMemberId().compareTo(memberId)==0){
								if(i==0){
									Map<Integer,BallotEntryVO> roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberId);											
									int roundBallotEntryMapSize=0;
									if(roundBallotEntryMap!=null){
										roundBallotEntryMapSize=roundBallotEntryMap.size();
									}else{
										roundBallotEntryMapSize=0;
										roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
									}
									roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
									memberRoundBallotEntryVOMap.put(memberId, roundBallotEntryMap);	
									int oldPosition=0;
									if(lastMemberFirstBatch!=null){
										oldPosition=memberPositionMap.get(lastMemberFirstBatch);
									}	
									memberPositionMap.put(memberId, oldPosition+1);
									allocated= true;
								}else{
									Map<Integer,BallotEntryVO> roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberId);											
									int roundBallotEntryMapSize=0;
									if(roundBallotEntryMap!=null){
										roundBallotEntryMapSize=roundBallotEntryMap.size();
									}else{
										roundBallotEntryMapSize=0;
										roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
									}
									roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
									memberRoundBallotEntryVOMap.put(memberId, roundBallotEntryMap);	
									int oldPosition=memberPositionMap.get(oldMemberId);
									memberPositionMap.put(memberId, oldPosition+1);
									allocated= true;
								}
							}
							oldMemberId=membersWithQuestionsOnlyInLastRoundPresent.get(i).getMemberId();
							if(allocated){
								return true;
							}
						}
					}else{
						Map<Integer,BallotEntryVO> roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberId);											
						int roundBallotEntryMapSize=0;
						if(roundBallotEntryMap!=null){
							roundBallotEntryMapSize=roundBallotEntryMap.size();
						}else{
							roundBallotEntryMapSize=0;
							roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
						}
						roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
						memberRoundBallotEntryVOMap.put(memberId, roundBallotEntryMap);	
						int oldPosition=0;
						if(lastMemberFirstBatch!=null){
							oldPosition=memberPositionMap.get(lastMemberFirstBatch);
						}	
						memberPositionMap.put(memberId, oldPosition+1);
						return true;
					}									
				}//present member ends
				/**** Absent Member ****/
				else{
					BallotEntryVO ballotEntryVO=new BallotEntryVO();
					ballotEntryVO.setMemberId(memberId);									
					ballotEntryVO.setDeviceId(question.getId());									
					ballotEntryVO.setDeviceNumber(question.getNumber());									
					ballotEntryVO.setPriority(question.getPriority());	
					ballotEntryVO.setSubmissionDate(question.getSubmissionDate());
					ballotEntryVO.setChartAnsweringDate(question.getChartAnsweringDate().getAnsweringDate());
					ballotEntryVO.setAttendance(memberBallot.getAttendance());
					ballotEntryVO.setRound(memberBallot.getRound());
					ballotEntryVO.setPosition(memberBallot.getPosition());
					if(membersWithQuestionsOnlyInLastRoundAbsent!=null && !membersWithQuestionsOnlyInLastRoundAbsent.isEmpty()){
						membersWithQuestionsOnlyInLastRoundAbsent.add(ballotEntryVO);
						Collections.sort(membersWithQuestionsOnlyInLastRoundAbsent,new BallotEntryAttRoundPosComparator());
						for(int i=0;i<membersWithQuestionsOnlyInLastRoundAbsent.size();i++){
							if(membersWithQuestionsOnlyInLastRoundAbsent.get(i).getMemberId().compareTo(memberId)==0){
								if(i==0){
									Map<Integer,BallotEntryVO> roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberId);											
									int roundBallotEntryMapSize=0;
									if(roundBallotEntryMap!=null){
										roundBallotEntryMapSize=roundBallotEntryMap.size();
									}else{
										roundBallotEntryMapSize=0;
										roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
									}
									roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
									memberRoundBallotEntryVOMap.put(memberId, roundBallotEntryMap);	
									int oldPosition=0;
									if(lastMemberFirstBatch!=null){
										oldPosition=memberPositionMap.get(lastMemberFirstBatch);
									}		
									memberPositionMap.put(memberId, oldPosition+1);
									allocated= true;
								}else{
									Map<Integer,BallotEntryVO> roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberId);											
									int roundBallotEntryMapSize=0;
									if(roundBallotEntryMap!=null){
										roundBallotEntryMapSize=roundBallotEntryMap.size();
									}else{
										roundBallotEntryMapSize=0;
										roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
									}
									roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
									memberRoundBallotEntryVOMap.put(memberId, roundBallotEntryMap);	
									int oldPosition=memberPositionMap.get(oldMemberId);
									memberPositionMap.put(memberId, oldPosition+1);
									allocated= true;
								}
							}
							oldMemberId=membersWithQuestionsOnlyInLastRoundAbsent.get(i).getMemberId();
							if(allocated){
								return true;
							}
						}
					}else{
						Map<Integer,BallotEntryVO> roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberId);											
						int roundBallotEntryMapSize=0;
						if(roundBallotEntryMap!=null){
							roundBallotEntryMapSize=roundBallotEntryMap.size();
						}else{
							roundBallotEntryMapSize=0;
							roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
						}
						roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
						memberRoundBallotEntryVOMap.put(memberId, roundBallotEntryMap);	
						int oldPosition=0;
						if(lastMemberFirstBatch!=null){
							oldPosition=memberPositionMap.get(lastMemberFirstBatch);
						}		
						memberPositionMap.put(memberId, oldPosition+1);
						return true;
					}
				}							
			}//absent member ends
			/**** Gave questions in second batch i.e member ballot is null ****/
			else{
				Long oldMemberId=null;
				BallotEntryVO ballotEntryVO=new BallotEntryVO();
				ballotEntryVO.setMemberId(memberId);									
				ballotEntryVO.setDeviceId(question.getId());									
				ballotEntryVO.setDeviceNumber(question.getNumber());									
				ballotEntryVO.setPriority(question.getPriority());	
				ballotEntryVO.setSubmissionDate(question.getSubmissionDate());
				ballotEntryVO.setChartAnsweringDate(question.getChartAnsweringDate().getAnsweringDate());
				if(membersWithQuestionsOnlyInSecondBatch!=null && !membersWithQuestionsOnlyInSecondBatch.isEmpty()){
					membersWithQuestionsOnlyInSecondBatch.add(ballotEntryVO);
					Collections.sort(membersWithQuestionsOnlyInSecondBatch,new BallotEntryNumberComparator());
					for(int i=0;i<membersWithQuestionsOnlyInSecondBatch.size();i++){
						if(membersWithQuestionsOnlyInSecondBatch.get(i).getDeviceNumber().compareTo(questionNumber)==0){
							if(i==0){
								Map<Integer,BallotEntryVO> roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberId);											
								int roundBallotEntryMapSize=0;
								if(roundBallotEntryMap!=null){
									roundBallotEntryMapSize=roundBallotEntryMap.size();
								}else{
									roundBallotEntryMapSize=0;
									roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
								}
								roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
								memberRoundBallotEntryVOMap.put(memberId, roundBallotEntryMap);	
								int oldPosition=0;
								if(lastMemberSecondBatch!=null){
									oldPosition=memberPositionMap.get(lastMemberSecondBatch);
								}							
								memberPositionMap.put(memberId, oldPosition+1);
								allocated= true;
							}else{
								Map<Integer,BallotEntryVO> roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberId);											
								int roundBallotEntryMapSize=0;
								if(roundBallotEntryMap!=null){
									roundBallotEntryMapSize=roundBallotEntryMap.size();
								}else{
									roundBallotEntryMapSize=0;
									roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
								}
								roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
								memberRoundBallotEntryVOMap.put(memberId, roundBallotEntryMap);	
								int oldPosition=memberPositionMap.get(oldMemberId);
								memberPositionMap.put(memberId, oldPosition+1);
								allocated= true;
							}
						}
						oldMemberId=membersWithQuestionsOnlyInSecondBatch.get(i).getMemberId();
						if(allocated){
							return true;
						}
					}
				}else{
					Map<Integer,BallotEntryVO> roundBallotEntryMap=memberRoundBallotEntryVOMap.get(memberId);											
					int roundBallotEntryMapSize=0;
					if(roundBallotEntryMap!=null){
						roundBallotEntryMapSize=roundBallotEntryMap.size();
					}else{
						roundBallotEntryMapSize=0;
						roundBallotEntryMap=new LinkedHashMap<Integer, BallotEntryVO>();
					}
					roundBallotEntryMap.put(roundBallotEntryMapSize+1, ballotEntryVO);
					memberRoundBallotEntryVOMap.put(memberId, roundBallotEntryMap);	
					int oldPosition=0;
					if(lastMemberSecondBatch!=null){
						oldPosition=memberPositionMap.get(lastMemberSecondBatch);
					}	
					memberPositionMap.put(memberId, oldPosition+1);
					return true;
				}
			}//second batch member ends
		}//new supporting member which was not present in the list of questions for particular answering date
		return allocated;
	}
	/****************************** Member Final Ballot UH ENDS **********************************/	

	private Boolean ballotAlreadyCreated(final Session session,
			final DeviceType deviceType,
			final Group group,
			final String answeringDate,
			final String locale) throws ELSException {

		try{
			String query="SELECT COUNT(b.id) FROM Ballot b" +
					" WHERE b.session.id=:sessionId" +
					" AND b.deviceType.id=:deviceTypeId" +
					" AND b.group.id=:groupId" +
					" AND b.answeringDate=:answeringDate" +
					" AND locale=:locale";
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("groupId", group.getId());
			jpQuery.setParameter("answeringDate", FormaterUtil.formatStringToDate(answeringDate, ApplicationConstants.DB_DATEFORMAT));		
			jpQuery.setParameter("locale", locale);
			Long count= ((Long)jpQuery.getSingleResult()).longValue();
			if(count.compareTo(Long.valueOf(0))==0){
				return false;
			}else{
				return true;
			}		
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotRepository_Boolean_ballotAlreadyCreated", "No ballot found.");
			throw elsException;
		}
	}

	@SuppressWarnings("rawtypes")
	public List<MemberBallotFinalBallotVO> viewBallot(final Session session,
			final DeviceType deviceType,
			final String answeringDate,
			final String locale) throws ELSException{

		try{
			org.mkcl.els.domain.Query queryInner = null;
			org.mkcl.els.domain.Query queryWrapper = null;
			String ballotEntryQuery = null;
			Query jpQuery = null;
			List ballotEntries = null;

			List<MemberBallotFinalBallotVO> ballots = new ArrayList<MemberBallotFinalBallotVO>();
			NumberFormat numberFormat=FormaterUtil.getNumberFormatterNoGrouping(locale);		

			queryInner = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_VIEW_BALLOT_IF_EMPTY_INNER_QUERY", locale);
			queryWrapper = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_VIEW_BALLOT_IF_EMPTY_WRAPPER", locale);
			if(queryInner != null){
				if(queryWrapper != null){
					ballotEntryQuery = queryWrapper.getQuery().replaceAll("INNER_QUERY", queryInner.getQuery());				
					jpQuery = this.em().createNativeQuery(ballotEntryQuery);
					jpQuery.setParameter("sessionId", session.getId());
					jpQuery.setParameter("deviceTypeId", deviceType.getId());
					jpQuery.setParameter("answeringDate",answeringDate);
					jpQuery.setParameter("locale",locale);
				}
			}

			ballotEntries = jpQuery.getResultList();

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

			queryInner = null;
			queryWrapper = null;
			jpQuery = null;

			if(!ballots.isEmpty()){
				for(MemberBallotFinalBallotVO i:ballots){
					queryInner = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_VIEW_BALLOT_IF_NOT_EMPTY_INNER_QUERY", locale);
					queryWrapper = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_VIEW_BALLOT_IF_NOT_EMPTY_WRAPPER", locale);

					List questionEntries= null;
					String finalQuestionQuery = null;

					if(queryInner != null){
						if(queryWrapper != null){
							finalQuestionQuery = queryWrapper.getQuery().replaceAll("INNER_QUERY", queryInner.getQuery());				
							jpQuery = this.em().createNativeQuery(finalQuestionQuery);		
							jpQuery.setParameter("ballotEntryId", i.getBallotEntryId());
						}
					}

					questionEntries = jpQuery.getResultList();

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
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotRepository_List<MemberBallotFinalBallotVO>_viewBallot", "No ballot found.");
			throw elsException;
		}
	}

	/******************** Member Ballot Member Wise Report  
	 * @throws ELSException *****************************************/
	@SuppressWarnings({ "rawtypes"})
	public MemberBallotMemberWiseReportVO findMemberWiseReportVO(final Session session, 
			final DeviceType questionType, 
			final Member member,
			final String locale) throws ELSException {
		try {
			String startDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME);
			String endDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
			MemberBallotMemberWiseReportVO memberBallotMemberWiseReportVO=new MemberBallotMemberWiseReportVO();
			if(startDate!=null&&endDate!=null){
				if((!startDate.isEmpty())&&(!endDate.isEmpty())){
					/**** Count of questions ****/
					org.mkcl.els.domain.Query queryCounter = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_MEMBER_WISE_REPORT_COUNT_QUERY", locale);
					if(queryCounter != null){

						String countQuery=queryCounter.getQuery();

						Query jpQuery = this.em().createNativeQuery(countQuery);
						jpQuery.setParameter("sessionId", session.getId());
						jpQuery.setParameter("memberId", member.getId());
						jpQuery.setParameter("locale", locale);
						jpQuery.setParameter("startDate", FormaterUtil.formatStringToDate(startDate, ApplicationConstants.DB_DATETIME_FORMAT));
						jpQuery.setParameter("endDate", FormaterUtil.formatStringToDate(endDate, ApplicationConstants.DB_DATETIME_FORMAT));
						jpQuery.setParameter("questionTypeId", questionType.getId());

						List countResults=jpQuery.getResultList();
						List<MemberBallotMemberWiseCountVO> countVOs=new ArrayList<MemberBallotMemberWiseCountVO>();
						NumberFormat numberFormat=FormaterUtil.getNumberFormatterNoGrouping(locale);
						for(Object i:countResults){
							Object[] o=(Object[]) i;
							MemberBallotMemberWiseCountVO memberBallotMemberWiseCountVO=new MemberBallotMemberWiseCountVO();
							if(o[0]!=null&&o[1]!=null&&o[2]!=null&&o[3]!=null){
								if((!o[0].toString().isEmpty())&&(!o[1].toString().isEmpty())&&(!o[2].toString().isEmpty())){
									memberBallotMemberWiseCountVO.setCount(numberFormat.format(Integer.parseInt(o[0].toString())));
									memberBallotMemberWiseCountVO.setStatusType(o[1].toString());
									memberBallotMemberWiseCountVO.setStatusTypeType(o[2].toString());
									memberBallotMemberWiseCountVO.setCurrentDeviceType(o[3].toString());
									countVOs.add(memberBallotMemberWiseCountVO);
								}					
							}
						}
						memberBallotMemberWiseReportVO.setMemberBallotMemberWiseCountVOs(countVOs);
						/**** Member Full Name ****/
						memberBallotMemberWiseReportVO.setMember(member.getFullname());	

						jpQuery = null;
						queryCounter = null;

						org.mkcl.els.domain.Query questionQueryInner = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_MEMBER_WISE_REPORT_QUESTION_QUERY_INNER", locale);
						org.mkcl.els.domain.Query questionQueryFinal = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_MEMBER_WISE_REPORT_QUESTION_QUERY_FINAL", locale);
						if(questionQueryInner != null){
							if(questionQueryFinal != null){

								/**** Questions ****/
								List<MemberBallotMemberWiseQuestionVO> questionVOs=new ArrayList<MemberBallotMemberWiseQuestionVO>();

								String query = questionQueryFinal.getQuery().replaceAll("INNER_QUERY", questionQueryInner.getQuery());

								jpQuery = this.em().createNativeQuery(query);
								jpQuery.setParameter("sessionId", session.getId());
								jpQuery.setParameter("memberId", member.getId());
								jpQuery.setParameter("locale", locale);
								jpQuery.setParameter("startDate", FormaterUtil.formatStringToDate(startDate, ApplicationConstants.DB_DATETIME_FORMAT));
								jpQuery.setParameter("endDate", FormaterUtil.formatStringToDate(endDate, ApplicationConstants.DB_DATETIME_FORMAT));
								jpQuery.setParameter("questionTypeId", questionType.getId());

								List questionResults=jpQuery.getResultList();
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
									if(o[6]!=null){
										questionVO.setOriginalDeviceType(o[6].toString());
									}
									if(o[7]!=null){
										questionVO.setCurrentDeviceType(o[7].toString());
									}
									if(o[8]!=null){
										questionVO.setClubbingInformation(o[8].toString());
									} else {
										questionVO.setClubbingInformation("");
									}
									questionVOs.add(questionVO);
									position++;
								}
								memberBallotMemberWiseReportVO.setMemberBallotMemberWiseQuestionVOs(questionVOs);
							}
						}
					}
				}
			}		
			return memberBallotMemberWiseReportVO;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotRepository_MemberBallotMemberWiseReportVO_findMemberWiseReportVO", "No member ballot found.");
			throw elsException;
		}
	}

	/**************************** Member Ballot Question Distributions 
	 * @throws ELSException ******************************/
	@SuppressWarnings("rawtypes")
	public List<MemberBallotQuestionDistributionVO> viewQuestionDistribution(
			final Session session, 
			final DeviceType questionType, 
			final String locale) throws ELSException {
		try {
			List<MemberBallotQuestionDistributionVO> distributions=new ArrayList<MemberBallotQuestionDistributionVO>();
			String startDateParameter=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME);
			String endDateParameter=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
			if(startDateParameter!=null&&endDateParameter!=null){
				if((!startDateParameter.isEmpty())&&(!endDateParameter.isEmpty())){					
					Date startDate = FormaterUtil.formatStringToDate(startDateParameter, ApplicationConstants.DB_DATETIME_FORMAT);
					Date endDate = FormaterUtil.formatStringToDate(endDateParameter, ApplicationConstants.DB_DATETIME_FORMAT);
					org.mkcl.els.domain.Query elsQuery = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_VIEW_QUESTION_DISTRIBUTION_MEMBER", locale);
					if(elsQuery != null){
						Query jpQuery = this.em().createNativeQuery(elsQuery.getQuery());
						jpQuery.setParameter("sessionId", session.getId());
						jpQuery.setParameter("questionTypeId", questionType.getId());
						jpQuery.setParameter("locale", locale);
						jpQuery.setParameter("startDate", startDate);
						jpQuery.setParameter("endDate", endDate);

						List members = jpQuery.getResultList();
						int count=1;
						NumberFormat numberFormat = FormaterUtil.getNumberFormatterNoGrouping(locale);
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
							if(o[2]!=null){
								distribution.setHouseType(o[2].toString());
							}
							if(o[3]!=null){
								distribution.setHouseTypeName(o[3].toString());
							}
							if(o[4]!=null){
								distribution.setSessionTypeName(o[4].toString());
							}
							if(o[5]!=null){
								distribution.setSessionYear(o[5].toString());
							}
							if(o[6]!=null){
								distribution.setSessionCountName(o[6].toString());
							}
							distribution.setQuestionSubmissionStartTime(startDate);	
							distribution.setQuestionSubmissionEndTime(endDate);	
							distribution.setsNo(numberFormat.format(count));
							count++;
							distributions.add(distribution);
						}

						elsQuery = null;
						jpQuery = null;

						elsQuery =org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_VIEW_QUESTION_DISTRIBUTION_COUNT", locale);

						if(elsQuery != null){

							jpQuery = this.em().createNativeQuery(elsQuery.getQuery());

							for(MemberBallotQuestionDistributionVO i:distributions){

								jpQuery.setParameter("sessionId", session.getId());
								jpQuery.setParameter("memberId", i.getMemberId());
								jpQuery.setParameter("locale", locale);
								jpQuery.setParameter("startDate", FormaterUtil.formatStringToDate(startDateParameter, ApplicationConstants.DB_DATETIME_FORMAT));
								jpQuery.setParameter("endDate", FormaterUtil.formatStringToDate(endDateParameter, ApplicationConstants.DB_DATETIME_FORMAT));
								jpQuery.setParameter("questionTypeId", questionType.getId());

								List countResults = jpQuery.getResultList();
								List<MemberBallotMemberWiseCountVO> countVOs=new ArrayList<MemberBallotMemberWiseCountVO>();
								int clarificationCount=0;
								int totalCount=0;
								for(Object j:countResults){
									Object[] o=(Object[]) j;
									MemberBallotMemberWiseCountVO memberBallotMemberWiseCountVO=new MemberBallotMemberWiseCountVO();
									if(o[0]!=null&&o[1]!=null&&o[2]!=null){
										if((!o[0].toString().isEmpty())&&(!o[1].toString().isEmpty())&&(!o[2].toString().isEmpty())){
											if(o[2].toString().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													||o[2].toString().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_GOVT)
													||o[2].toString().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													||o[2].toString().equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)){
												clarificationCount=clarificationCount+Integer.parseInt(o[0].toString());								
											}else{
												memberBallotMemberWiseCountVO.setCount(numberFormat.format(Integer.parseInt(o[0].toString())));
												memberBallotMemberWiseCountVO.setStatusType(o[1].toString());
												memberBallotMemberWiseCountVO.setStatusTypeType(o[2].toString());
												memberBallotMemberWiseCountVO.setCurrentDeviceType(o[3].toString());
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
				}
			}		
			return distributions;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotRepository_List<MemberBallotQuestionDistributionVO> viewQuestionDistribution", "No ballot found.");
			throw elsException;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Member> findMembersByPosition(final Session session,
			final DeviceType deviceType,
			final Boolean attendance,
			final int round,
			final String locale,
			final int startingRecordToFetch,
			final int noOfRecordsToFetch) throws ELSException {

		try {
			String query="SELECT m FROM MemberBallot mb" +
					" JOIN mb.member m WHERE"+
					" mb.session.id=:sessionId" +
					" AND mb.deviceType.id=:deviceTypeId" +
					" AND mb.attendance=:attendance" +
					" AND mb.locale=:locale" +
					" AND mb.round=:round ORDER BY mb.position";

			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("attendance", attendance);
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("round", round);

			return jpQuery.setFirstResult(startingRecordToFetch).setMaxResults(noOfRecordsToFetch).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotRepository_List<Member>_findMembersByPosition", "No member in ballot found.");
			throw elsException;
		}		
	}

	public int findEntryCount(final Session session,
			final DeviceType deviceType,
			final int round,
			final boolean attendance,
			final String locale) throws ELSException {
		try {
			String query="SELECT COUNT(mb.id) FROM MemberBallot mb" +
					" WHERE mb.session.id=:sessionId" +
					" AND mb.deviceType.id=:deviceTypeId" +
					" AND mb.attendance=:attendance" +
					" AND mb.locale=:locale" +
					" AND mb.round=:round";

			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("attendance", attendance);
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("round", round);

			return ((Long)jpQuery.getSingleResult()).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotRepository_int_findEntryCount", "No coount found.");
			throw elsException;
		}		
	}

	@SuppressWarnings("rawtypes")
	public boolean updateClubbing(final Session session,final DeviceType deviceType,
			final Map<String,String[]> requestMap,final String locale) throws ELSException {		
		try {
			List result=org.mkcl.els.domain.Query.findReport("COUNCIL_UPDATE_CLUBBING", requestMap);
			if(result!=null && !result.isEmpty()){
				for(Object i:result){
					Object[] o=(Object[]) i;
					String oldPrimary=null;
					String newPrimary=null;
					if(o[0]!=null){
						oldPrimary=o[0].toString();
					}
					if(o[1]!=null){
						newPrimary=o[1].toString();
					}
					if(oldPrimary!=null && newPrimary!=null){
						Question oldPrimaryQuestion=Question.findById(Question.class,Long.parseLong(oldPrimary));
						Question newPrimaryQuestion=Question.findById(Question.class,Long.parseLong(newPrimary));
						//ClubbedEntity newPrimaryClubbedEntity=ClubbedEntity.findByFieldName(ClubbedEntity.class,"question.id",newPrimaryQuestion.getId(), locale);
						ClubbedEntity newPrimaryClubbedEntity=ClubbedEntity.findByQuestion(newPrimaryQuestion, locale);
						/**** Clubbed Entities of New Primary Question and Old primary question ****/
						ClubbedEntity oldPrimaryClubbedEntity=new ClubbedEntity();
						oldPrimaryClubbedEntity.setDeviceType(deviceType);
						oldPrimaryClubbedEntity.setLocale(locale);
						oldPrimaryClubbedEntity.setQuestion(oldPrimaryQuestion);
						oldPrimaryClubbedEntity.setPosition(newPrimaryClubbedEntity.getPosition());
						oldPrimaryClubbedEntity.persist();					
						List<ClubbedEntity> newPrimaryclubbedEntities=new ArrayList<ClubbedEntity>();					
						List<ClubbedEntity> oldPrimaryClubbedEntities=oldPrimaryQuestion.getClubbedEntities();
						List<Question> questions = new ArrayList<Question>();
						for(ClubbedEntity j:oldPrimaryClubbedEntities){
							if(!j.getId().equals(newPrimaryClubbedEntity.getId())){
								questions.add(j.getQuestion());
//								Question question=Question.findById(Question.class, j.getQuestion().getId());
//								question.setParent(newPrimaryQuestion);
//								question.mergeWithDraftOnly();
								newPrimaryclubbedEntities.add(j);
							}
						}
						
						for(Question q: questions){
							q.setParent(newPrimaryQuestion);
							q.mergeWithDraftOnly();
						}
						newPrimaryclubbedEntities.add(oldPrimaryClubbedEntity);
						
						oldPrimaryQuestion.setParent(newPrimaryQuestion);
						oldPrimaryQuestion.setClubbedEntities(null);
						oldPrimaryQuestion.mergeWithDraftOnly();
						Status oldPrimaryQuestionInternalStaus = oldPrimaryQuestion.getInternalStatus();
						String oldPrimaryQuestionLevel = oldPrimaryQuestion.getLevel();
						String actor = oldPrimaryQuestion.getActor();
						UserGroupType usergroupType = null;
						if(actor!= null && !actor.isEmpty()){
							String  strUsergroupType = actor.split("#")[1];
							usergroupType = UserGroupType.findByType(strUsergroupType, locale);
						}
						Question.endDeviceWorkflow(oldPrimaryQuestion.getType().getDevice(), oldPrimaryQuestion.getId(), oldPrimaryQuestion.getHouseType().getType(), locale);
						
						newPrimaryQuestion.setParent(null);
						newPrimaryQuestion.setRevisedQuestionText(oldPrimaryQuestion.getRevisedQuestionText());
						newPrimaryQuestion.setRevisedSubject(oldPrimaryQuestion.getRevisedSubject());
						newPrimaryQuestion.setClubbedEntities(newPrimaryclubbedEntities);
						newPrimaryQuestion.mergeWithDraftOnly();
						
						Question.startDeviceWorkflow(newPrimaryQuestion.getType().getDevice(), newPrimaryQuestion.getId(), oldPrimaryQuestionInternalStaus, usergroupType, Integer.parseInt(oldPrimaryQuestionLevel), oldPrimaryQuestion.getHouseType().getType(), false, locale);
						
						
						//newPrimaryClubbedEntity.remove();

						/**** Update Position of New Primary Question Clubbed Entities ****/
						List<ClubbedEntity> newPrimaryClubbedEntitiesSorted=Question.findClubbedEntitiesByChartAnsDateNumber(newPrimaryQuestion,locale);
						int position=1;
						for(ClubbedEntity l:newPrimaryClubbedEntitiesSorted){
							l.setPosition(position);
							l.merge();
							position++;
						}
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberBallotRepository_exception_updateclubbing", "Eexception occureed during update clubbing");
			throw elsException;
		}			
	}

	public List<MemberBallot> findBySessionDeviceType(final Session session,
			final DeviceType deviceType, 
			final String locale) {
		StringBuffer strQuery = new StringBuffer("SELECT m FROM MemberBallot m" + 
								" WHERE m.session.id=:sessionId" + 
								" AND m.deviceType.id=:deviceTypeId" + 
								" AND m.locale=:locale ORDER BY m.position ASC");
		
		TypedQuery<MemberBallot> tQuery = this.em().createQuery(strQuery.toString(), MemberBallot.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("locale", locale);
		return tQuery.getResultList(); 		
	}
	
	public List<MemberBallotFinalBallotVO> previewFinalBallotUH(final Session session,
			final DeviceType deviceType,
			final Group group,
			final String strAnsweringDate,
			final Date answeringDate,
			final String locale,
			final String firstBatchSubmissionEndDate,
			final int totalRounds) throws ELSException {
		try {
				List<MemberBallotFinalBallotVO> ballots=new ArrayList<MemberBallotFinalBallotVO>();
				Map<String,String[]> requestMap=new HashMap<String, String[]>();
				requestMap.put("locale",new String[]{locale});
				requestMap.put("sessionid",new String[]{String.valueOf(session.getId())});
				requestMap.put("deviceType",new String[]{String.valueOf(deviceType.getId())});
				requestMap.put("groupid",new String[]{String.valueOf(group.getId())});
				requestMap.put("rounds",new String[]{String.valueOf(totalRounds)});
				requestMap.put("answeringDate",new String[]{strAnsweringDate});
				requestMap.put("firstBatchSubmissionEndDate",new String[]{firstBatchSubmissionEndDate});	
				House house=session.getHouse();
				requestMap.put("house", new String[]{String.valueOf(house.getId())});
				Date currentDate=new Date();
				String strCurrentDate=FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATEFORMAT, "en_US").format(currentDate);
				requestMap.put("currentDate", new String[]{strCurrentDate});
				/**** memberPositionMap contains distinct members and their position as they are being read during various stages ****/
				Map<Long,Integer> memberPositionMap=new LinkedHashMap<Long, Integer>();
				/**** memberRoundBallotEntryVOMap contains members and the various questions that become eligible during final ballot for that
				 * member ****/
				Map<Long,Map<Integer,BallotEntryVO>> memberRoundBallotEntryVOMap=new LinkedHashMap<Long, Map<Integer,BallotEntryVO>>();
				/**** Position starts with 1000 so that cases like that of Aashish Selar can be adjusted easily ****/
				int position=1000;				

				Date firstBatchSubmissionEndDateDate=FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT, "en_US").parse(firstBatchSubmissionEndDate);
				int totalRoundsInMemberBallot=Integer.parseInt(session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT));
				/**** These fields will be used to determine the position of an inactive member question when the
				 * supporting member or clubbed entity primary member or clubbed entity supporting member of that question has
				 * neither given question in first batch or second batch for given answering date(e.g Aashish Selar case) ****/
				List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundPresent=new ArrayList<BallotEntryVO>();
				List<BallotEntryVO> membersWithQuestionsOnlyInLastRoundAbsent=new ArrayList<BallotEntryVO>();
				List<BallotEntryVO> membersWithQuestionsOnlyInSecondBatch=new ArrayList<BallotEntryVO>();
				Long lastMemberFirstBatch=new Long(0);
				Long lastMemberSecondBatch=new Long(0);

				/**** Horizontal Scanning=first batch current date,first batch previous date,second batch current date,second batch previous date ***/
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"FINAL_BALLOT_COUNCIL_SCANNING","");
				if(customParameter!=null && customParameter.getValue().toLowerCase().equals("horizontal")){
					position=firstBatchCurrentAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					lastMemberFirstBatch=lastMember(memberPositionMap);
					position=firstBatchPreviousAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					membersWithQuestionsOnlyInLastRoundPresent=membersWithFIrstBatchLastRoundQuestionsPresent(session, deviceType, requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap, membersWithQuestionsOnlyInLastRoundPresent,
							membersWithQuestionsOnlyInLastRoundAbsent, lastMemberFirstBatch, totalRoundsInMemberBallot, locale);
					membersWithQuestionsOnlyInLastRoundAbsent=membersWithFIrstBatchLastRoundQuestionsAbsent(session, deviceType, requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap, membersWithQuestionsOnlyInLastRoundPresent,
							membersWithQuestionsOnlyInLastRoundAbsent, lastMemberFirstBatch, totalRoundsInMemberBallot, locale);
					position=secondBatchCurrentAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					lastMemberSecondBatch=lastMember(memberPositionMap);
					// position=secondBatchPreviousAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					membersWithQuestionsOnlyInSecondBatch=membersWithSecondBatchQuestionsOnly(session, deviceType, requestMap, position, totalRoundsInMemberBallot, 
							memberPositionMap, memberRoundBallotEntryVOMap, membersWithQuestionsOnlyInSecondBatch, lastMemberSecondBatch, locale);
				}else{
					position=firstBatchCurrentAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					lastMemberFirstBatch=lastMember(memberPositionMap);
					position=secondBatchCurrentAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					lastMemberSecondBatch=lastMember(memberPositionMap);
					position=firstBatchPreviousAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					membersWithQuestionsOnlyInLastRoundPresent=membersWithFIrstBatchLastRoundQuestionsPresent(session, deviceType, requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap, membersWithQuestionsOnlyInLastRoundPresent,
							membersWithQuestionsOnlyInLastRoundAbsent, lastMemberFirstBatch, totalRoundsInMemberBallot, locale);
					membersWithQuestionsOnlyInLastRoundAbsent=membersWithFIrstBatchLastRoundQuestionsAbsent(session, deviceType, requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap, membersWithQuestionsOnlyInLastRoundPresent,
							membersWithQuestionsOnlyInLastRoundAbsent, lastMemberFirstBatch, totalRoundsInMemberBallot, locale);
					// position=secondBatchPreviousAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap);
					membersWithQuestionsOnlyInSecondBatch=membersWithSecondBatchQuestionsOnly(session, deviceType, requestMap, position, totalRoundsInMemberBallot, 
							memberPositionMap, memberRoundBallotEntryVOMap, membersWithQuestionsOnlyInSecondBatch, lastMemberSecondBatch, locale);
				}				
				inactiveMemberFirstBatchCurrentAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
						session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
						,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
						,lastMemberFirstBatch,lastMemberSecondBatch);

				inactiveMemberFirstBatchPreviousAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
						session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
						,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
						,lastMemberFirstBatch,lastMemberSecondBatch);

				inactiveMemberSecondBatchCurrentAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
						session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
						,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
						,lastMemberFirstBatch,lastMemberSecondBatch);

				inactiveMemberSecondBatchPreviousAnsweringDate(requestMap, position, totalRounds, memberPositionMap, memberRoundBallotEntryVOMap,
						session,deviceType,locale,totalRoundsInMemberBallot,firstBatchSubmissionEndDateDate
						,membersWithQuestionsOnlyInLastRoundPresent,membersWithQuestionsOnlyInLastRoundAbsent,membersWithQuestionsOnlyInSecondBatch
						,lastMemberFirstBatch,lastMemberSecondBatch);


				//Setting Position
				Map<Integer,Long> sortedMemberPositionMap=new TreeMap<Integer, Long>();
				for(java.util.Map.Entry<Long, Integer> i:memberPositionMap.entrySet()){
					sortedMemberPositionMap.put(i.getValue(),i.getKey());					
				}
				//Setting Sequence and creating device sequences,ballot entries and ballot
				int sequence=1;
				int actualPosition=1;
				CustomParameter eligibleQuestionsAreSorted=CustomParameter.findByName(CustomParameter.class, "FINAL_BALLOT_COUNCIL_ELIGIBLE_QUESTIONS_SORTED", "");
				if(eligibleQuestionsAreSorted!=null && eligibleQuestionsAreSorted.getValue().toLowerCase().equals("yes")){
					for(java.util.Map.Entry<Long,Map<Integer, BallotEntryVO>> i:memberRoundBallotEntryVOMap.entrySet()){
						List<BallotEntryVO> firstBatchQuestions=new ArrayList<BallotEntryVO>();
						List<BallotEntryVO> secondBatchQuestions=new ArrayList<BallotEntryVO>();
						List<BallotEntryVO> inactiveMemberQuestions=new ArrayList<BallotEntryVO>();
						CustomParameter inactiveMembersQuestionsSorted=CustomParameter.findByName(CustomParameter.class,"FINAL_BALLOT_COUNCIL_INACTIVEMEMBERQUESTIONS_SORTED", "");
						if(inactiveMembersQuestionsSorted!=null && inactiveMembersQuestionsSorted.getValue().toLowerCase().equals("yes")){
							for(java.util.Map.Entry<Integer, BallotEntryVO> j:i.getValue().entrySet()){
								if(j.getValue().getSubmissionDate()!=null){
									if((j.getValue().getSubmissionDate().before(firstBatchSubmissionEndDateDate)
											||j.getValue().getSubmissionDate().equals(firstBatchSubmissionEndDateDate))
											&& j.getValue().getRound()!=null 
											&& j.getValue().getPosition()!=null
											){
										firstBatchQuestions.add(j.getValue());
									}else{
										secondBatchQuestions.add(j.getValue());
									}
								}
							}
						}else{
							for(java.util.Map.Entry<Integer, BallotEntryVO> j:i.getValue().entrySet()){
								if(j.getValue().getSubmissionDate()!=null){
									if((j.getValue().getSubmissionDate().before(firstBatchSubmissionEndDateDate)
											||j.getValue().getSubmissionDate().equals(firstBatchSubmissionEndDateDate))
											&& j.getValue().getRound()!=null 
											&& j.getValue().getPosition()!=null
											){
										firstBatchQuestions.add(j.getValue());
									}else if((j.getValue().getSubmissionDate().before(firstBatchSubmissionEndDateDate)
											||j.getValue().getSubmissionDate().equals(firstBatchSubmissionEndDateDate))
											&& j.getValue().getRound()==null 
											&& j.getValue().getPosition()==null
											){
										inactiveMemberQuestions.add(j.getValue());
									}							
									else{
										secondBatchQuestions.add(j.getValue());
									}
								}
							}
						}
						
						Map<Integer, BallotEntryVO> tempMap=new HashMap<Integer, BallotEntryVO>();
						boolean filled=false;
						int count=0;
						if(!firstBatchQuestions.isEmpty()){
							Collections.sort(firstBatchQuestions,new BallotEntryVOFirstBatchComparator());
							for(BallotEntryVO k:firstBatchQuestions){
								if(count==totalRounds){
									break;
								}
								tempMap.put(count+1,k);
								count++;
							}
							if(tempMap.size()==totalRounds){
								filled=true;
							}
						}
						if(!filled && !secondBatchQuestions.isEmpty()){
							Collections.sort(secondBatchQuestions,new BallotEntryVOSecondBatchComparator());
							for(BallotEntryVO k:secondBatchQuestions){
								if(count==totalRounds){
									break;
								}
								tempMap.put(count+1,k);
								count++;
							}
							if(tempMap.size()==totalRounds){
								filled=true;
							}
						}
						if(!filled && !inactiveMemberQuestions.isEmpty()){
							for(BallotEntryVO k:inactiveMemberQuestions){
								if(count==totalRounds){
									break;
								}
								tempMap.put(count+1,k);
								count++;
							}
							if(tempMap.size()==totalRounds){
								filled=true;
							}
						}
						memberRoundBallotEntryVOMap.put(i.getKey(),tempMap);
					}
				}
				
				for(int round=1;round<=totalRounds;round++){
					for(java.util.Map.Entry<Integer, Long> i:sortedMemberPositionMap.entrySet()){
						BallotEntryVO ballotEntryVO=memberRoundBallotEntryVOMap.get(i.getValue()).get(round);
						if(ballotEntryVO!=null){
							ballotEntryVO.setSequence(sequence);
							sequence++;
							memberRoundBallotEntryVOMap.get(i.getValue()).put(round,ballotEntryVO);
						}
					}
				}
				
				
				Integer count = 1;
				for(Entry<Long, Map<Integer, BallotEntryVO>> i : memberRoundBallotEntryVOMap.entrySet()){
					Long memberId = i.getKey();
					Member member = Member.findById(Member.class, memberId);
					MemberBallotFinalBallotVO mVO = new MemberBallotFinalBallotVO();
					mVO.setMember(member.findFirstLastName());
					mVO.setBallotSno(count.toString());
					List<MemberBallotFinalBallotQuestionVO> vos = new ArrayList<MemberBallotFinalBallotQuestionVO>();
					for(Entry<Integer, BallotEntryVO> j : i.getValue().entrySet()){
						MemberBallotFinalBallotQuestionVO vo = new MemberBallotFinalBallotQuestionVO();
						BallotEntryVO bvo = j.getValue();
						vo.setQuestionId(bvo.getDeviceId().toString());
						vo.setQuestionNumber(FormaterUtil.formatNumberNoGrouping(bvo.getDeviceNumber(), locale.toString()));
						vos.add(vo);							
					}
					mVO.setQuestions(vos);
					ballots.add(mVO);
					count++;
				}
				
				Comparator<MemberBallotFinalBallotVO> compareByMember = new Comparator<MemberBallotFinalBallotVO>() {
				    @Override
				    public int compare(MemberBallotFinalBallotVO o1, MemberBallotFinalBallotVO o2) {
				        return o1.getMember().compareTo(o2.getMember());
				    }
				};
				
										 
				Collections.sort(ballots, compareByMember);
				return ballots;
			}
			catch (Exception e) {
				logger.error("FINAL BALLOT FAILED",e);
				return null;
			}
	}
	
}
