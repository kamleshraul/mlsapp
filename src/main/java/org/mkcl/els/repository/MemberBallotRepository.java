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

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
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
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
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

	/****************************** Member Ballot Final Ballot 
	 * @throws ELSException **********************************/
	public Boolean createFinalBallot(final Session session,
			final DeviceType deviceType,
			final Group group,
			final String answeringDate,
			final String locale,
			final String firstBatchSubmissionEndDate,
			final int totalRounds) throws ELSException {
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
			
			queryInner = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_VIEW_BALLOT_IF_EMPTY_INNER_QUERY", null);
			queryWrapper = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_VIEW_BALLOT_IF_EMPTY_WRAPPER", null);
			if(queryInner != null){
				if(queryWrapper != null){
					ballotEntryQuery = queryWrapper.getQuery().replaceAll("INNER_QUERY", queryInner.getQuery());				
					jpQuery = this.em().createNativeQuery(ballotEntryQuery);
					jpQuery.setParameter("sessionId", session.getId());
					jpQuery.setParameter("deviceTypeId", deviceType.getId());
					jpQuery.setParameter("answeringDate", "'" + answeringDate + "'");
					jpQuery.setParameter("locale", "'" + locale + "'");
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
					queryInner = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_VIEW_BALLOT_IF_NOT_EMPTY_INNER_QUERY", null);
					queryWrapper = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_VIEW_BALLOT_IF_NOT_EMPTY_WRAPPER", null);
									
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
			String startDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME_UH);
			String endDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME_UH);
			MemberBallotMemberWiseReportVO memberBallotMemberWiseReportVO=new MemberBallotMemberWiseReportVO();
			if(startDate!=null&&endDate!=null){
				if((!startDate.isEmpty())&&(!endDate.isEmpty())){
					/**** Count of questions ****/
					org.mkcl.els.domain.Query queryCounter = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_MEMBER_WISE_REPORT_COUNT_QUERY", "");
					if(queryCounter != null){
						
						String countQuery=queryCounter.getQuery();
											
						Query jpQuery = this.em().createNativeQuery(countQuery);
						jpQuery.setParameter("sessionId", session.getId());
						jpQuery.setParameter("memberId", member.getId());
						jpQuery.setParameter("locale", locale);
						jpQuery.setParameter("startDate", FormaterUtil.formatStringToDate(startDate, ApplicationConstants.DB_DATEFORMAT));
						jpQuery.setParameter("endDate", FormaterUtil.formatStringToDate(endDate, ApplicationConstants.DB_DATEFORMAT));
						jpQuery.setParameter("questionTypeId", questionType.getId());
						
						List countResults=jpQuery.getResultList();
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
						
						jpQuery = null;
						queryCounter = null;
						
						org.mkcl.els.domain.Query questionQueryInner = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_MEMBER_WISE_REPORT_QUESTION_QUERY_INNER", "");
						org.mkcl.els.domain.Query questionQueryFinal = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_MEMBER_WISE_REPORT_QUESTION_QUERY_FINAL", "");
						if(questionQueryInner != null){
							if(questionQueryFinal != null){
								
								/**** Questions ****/
								List<MemberBallotMemberWiseQuestionVO> questionVOs=new ArrayList<MemberBallotMemberWiseQuestionVO>();
															
								String query = questionQueryFinal.getQuery().replaceAll("INNER_QUERY", questionQueryInner.getQuery());
								
								jpQuery = this.em().createNativeQuery(query);
								jpQuery.setParameter("sessionId", session.getId());
								jpQuery.setParameter("memberId", member.getId());
								jpQuery.setParameter("locale", locale);
								jpQuery.setParameter("startDate", FormaterUtil.formatStringToDate(startDate, ApplicationConstants.DB_DATEFORMAT));
								jpQuery.setParameter("endDate", FormaterUtil.formatStringToDate(endDate, ApplicationConstants.DB_DATEFORMAT));
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
			String startDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME_UH);
			String endDate=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME_UH);
			if(startDate!=null&&endDate!=null){
				if((!startDate.isEmpty())&&(!endDate.isEmpty())){
					
					org.mkcl.els.domain.Query elsQuery = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_VIEW_QUESTION_DISTRIBUTION_MEMBER", "");
					if(elsQuery != null){
						Query jpQuery = this.em().createNativeQuery(elsQuery.getQuery());
						jpQuery.setParameter("sessionId", session.getId());
						jpQuery.setParameter("questionTypeId", questionType.getId());
						jpQuery.setParameter("locale", locale);
						jpQuery.setParameter("startDate", FormaterUtil.formatStringToDate(startDate, ApplicationConstants.DB_DATEFORMAT));
						jpQuery.setParameter("endDate", FormaterUtil.formatStringToDate(endDate, ApplicationConstants.DB_DATEFORMAT));
						
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
							distribution.setsNo(numberFormat.format(count));
							count++;
							distributions.add(distribution);
						}
						
						elsQuery = null;
						jpQuery = null;
						
						elsQuery =org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "MEMBERBALLOT_VIEW_QUESTION_DISTRIBUTION_COUNT", "");
						
						if(elsQuery != null){
							
							jpQuery = this.em().createNativeQuery(elsQuery.getQuery());
							
							for(MemberBallotQuestionDistributionVO i:distributions){
															
								jpQuery.setParameter("sessionId", session.getId());
								jpQuery.setParameter("memberId", i.getMemberId());
								jpQuery.setParameter("locale", locale);
								jpQuery.setParameter("startDate", FormaterUtil.formatStringToDate(startDate, ApplicationConstants.DB_DATEFORMAT));
								jpQuery.setParameter("endDate", FormaterUtil.formatStringToDate(endDate, ApplicationConstants.DB_DATEFORMAT));
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
}
