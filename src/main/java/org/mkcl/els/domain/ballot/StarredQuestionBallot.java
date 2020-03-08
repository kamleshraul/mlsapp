package org.mkcl.els.domain.ballot;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.QuestionSequenceVO;
import org.mkcl.els.common.vo.RoundVO;
import org.mkcl.els.common.vo.StarredBallotVO;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.domain.ballot.Ballot;
import org.mkcl.els.domain.ballot.BallotEntry;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Question.PROCESSING_MODE;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.Title;

class StarredQuestionBallot {

	//=================================================
	//
	//=============== VIEW METHODS ====================
	//
	//=================================================
	public static List<StarredBallotVO> findStarredBallotVOs(final Session session,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<StarredBallotVO> ballotVOs = new ArrayList<StarredBallotVO>();
		
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> ballotEntries = ballot.getBallotEntries();
			for(BallotEntry be : ballotEntries) {
				Long memberId = be.getMember().getId();
				String memberName = be.getMember().getFullnameLastNameFirst();
				List<QuestionSequenceVO> questionSequenceVOs =
					StarredQuestionBallot.getQuestionSequenceVOs(be.getDeviceSequences());

				StarredBallotVO ballotVO = new StarredBallotVO(memberId, 
						memberName, questionSequenceVOs);
				ballotVOs.add(ballotVO);
			}
		}
		else {
			ballotVOs = null;
		}
		
		return ballotVOs;
	}
	
	public static List<QuestionSequenceVO> findStarredQuestionSequenceVOs(final Session session,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<QuestionSequenceVO> ballotVOs = new ArrayList<QuestionSequenceVO>();
		
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> ballotEntries = ballot.getBallotEntries();
			for(BallotEntry be : ballotEntries) {
				List<QuestionSequenceVO> questionSequenceVOs = 
						StarredQuestionBallot.getQuestionSequenceVOs(be.getDeviceSequences());
				for(QuestionSequenceVO i: questionSequenceVOs) {
					i.setMemberId(be.getMember().getId());
				}
				ballotVOs.addAll(questionSequenceVOs);
			}
		}
		else {
			ballotVOs = null;
		}
		
		return ballotVOs;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<StarredBallotVO> findStarredPreBallotVOs(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<StarredBallotVO> ballotVOs = new ArrayList<StarredBallotVO>();
		
		Integer noOfRounds = StarredQuestionBallot.getNoOfRounds(session.getHouse().getType());
		Group group = Group.find(session, answeringDate, locale);
		 
		//find the preballot 
		PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
		List<BallotEntry> ballotEntries = null;
		
		Map<String, String[]> queryParameters = new HashMap<String, String[]>();
		
		if(preBallot == null){
			//if does not exists create a pre ballot and save it
			ballotEntries = StarredQuestionBallot.compute(session, group, answeringDate, noOfRounds, locale);
			ballotEntries = 
				StarredQuestionBallot.inactiveMembersQuestionHandover(session, group, deviceType, 
						ballotEntries, answeringDate, noOfRounds, locale);			
			if(!ballotEntries.isEmpty()){
				preBallot = new PreBallot(session, deviceType, answeringDate, new Date(System.currentTimeMillis()), locale);
				preBallot.setBallotEntries(ballotEntries);
				preBallot.persist();
				queryParameters.put("preballotId", new String[]{preBallot.getId().toString()});
			}
		}
		else {
			CustomParameter cp = CustomParameter.findByName(CustomParameter.class, "QIS_STARRED_LOWERHOUSE_PREBALLOT_RECREATE_IF_EXISTS", "");
			if(cp == null || cp.getValue().equals("YES")) {
				// Delete the existing PreBallot ONLY IF its corresponding Ballot
				// is not created
				Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
				if(ballot == null) {
					// preBallot.remove();
					preBallot.optimizedRemove();
					
					ballotEntries = StarredQuestionBallot.compute(session, group, answeringDate, noOfRounds, locale);
					ballotEntries = 
						StarredQuestionBallot.inactiveMembersQuestionHandover(session, group, deviceType, 
								ballotEntries, answeringDate, noOfRounds, locale);			
					if(!ballotEntries.isEmpty()){
						PreBallot newPreBallot = new PreBallot(session, deviceType, answeringDate, new Date(System.currentTimeMillis()), locale);
						newPreBallot.setBallotEntries(ballotEntries);
						newPreBallot.persist();
						queryParameters.put("preballotId", new String[]{newPreBallot.getId().toString()});
					}
				}
				else {
					// Return the existing PreBallot
					ballotEntries = preBallot.getBallotEntries();
					queryParameters.put("preballotId", new String[]{preBallot.getId().toString()});
				}
			}
			else {
				// Return the existing PreBallot
				ballotEntries = preBallot.getBallotEntries();
				queryParameters.put("preballotId", new String[]{preBallot.getId().toString()});
			}
		}
		queryParameters.put("locale", new String[]{preBallot.getLocale()});
		
		for(BallotEntry be : ballotEntries) {
			StarredBallotVO ballotVO = new StarredBallotVO();
			Member currentMember = be.getMember();
			ballotVO.setMemberId(currentMember.getId());
			CustomParameter memberNameFormatParameter = null;
			
			if(session.findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
				memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_PREBALLOT_MEMBERNAMEFORMAT_LOWERHOUSE", "");
			} 
			else if(session.findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
				memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_PREBALLOT_MEMBERNAMEFORMAT_LOWERHOUSE", "");
			}
			
			if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
				ballotVO.setMemberName(currentMember.findNameInGivenFormat(memberNameFormatParameter.getValue()));
			} 
			else {
				ballotVO.setMemberName(currentMember.findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
			}			
			
			queryParameters.put("memberId", new String[]{currentMember.getId().toString()});
			List deviceSequences = Query.findReport("QIS_LOWERHOUSE_PREBALLOT_MEMBER_DEVICESEQUENCES", queryParameters);
			if(deviceSequences!=null && !deviceSequences.isEmpty()) {
				List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
				for(Object obj: deviceSequences) {
					if(obj!=null) {
						Object[] deviceSequence = (Object[]) obj;
						if(deviceSequence!=null && deviceSequence.length>=3) {
							QuestionSequenceVO questionSequenceVO = new QuestionSequenceVO();
							if(deviceSequence[1]!=null && deviceSequence[2]!=null) {
								questionSequenceVO.setQuestionId(Long.parseLong(deviceSequence[1].toString()));						
								questionSequenceVO.setNumber(Integer.parseInt(deviceSequence[2].toString()));
								questionSequenceVO.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(questionSequenceVO.getNumber(), locale));
								questionSequenceVOs.add(questionSequenceVO);
							}
						}													
					}
				}
				ballotVO.setQuestionSequenceVOs(questionSequenceVOs);
			}			
			ballotVOs.add(ballotVO);
		}
		
		return ballotVOs;
	}
	
	
	public static List<StarredBallotVO> getStarredPreBallotVOs(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<StarredBallotVO> ballotVOs = new ArrayList<StarredBallotVO>();
		//find the preballot 
		PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
		List<BallotEntry> ballotEntries = null;
		if(preBallot != null){
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
			// Return the existing PreBallot
			ballotEntries = preBallot.getBallotEntries();
			queryParameters.put("preballotId", new String[]{preBallot.getId().toString()});
			queryParameters.put("locale", new String[]{preBallot.getLocale()});
			
			for(BallotEntry be : ballotEntries) {
				StarredBallotVO ballotVO = new StarredBallotVO();
				Member currentMember = be.getMember();
				ballotVO.setMemberId(currentMember.getId());
				CustomParameter memberNameFormatParameter = null;
				
				if(session.findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_PREBALLOT_MEMBERNAMEFORMAT_LOWERHOUSE", "");
				} 
				else if(session.findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_PREBALLOT_MEMBERNAMEFORMAT_LOWERHOUSE", "");
				}
				
				if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
					ballotVO.setMemberName(currentMember.findNameInGivenFormat(memberNameFormatParameter.getValue()));
				} 
				else {
					ballotVO.setMemberName(currentMember.findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
				}			
				
				queryParameters.put("memberId", new String[]{currentMember.getId().toString()});
				List deviceSequences = Query.findReport("QIS_LOWERHOUSE_PREBALLOT_MEMBER_DEVICESEQUENCES", queryParameters);
				if(deviceSequences!=null && !deviceSequences.isEmpty()) {
					List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
					for(Object obj: deviceSequences) {
						if(obj!=null) {
							Object[] deviceSequence = (Object[]) obj;
							if(deviceSequence!=null && deviceSequence.length>=3) {
								QuestionSequenceVO questionSequenceVO = new QuestionSequenceVO();
								if(deviceSequence[1]!=null && deviceSequence[2]!=null) {
									questionSequenceVO.setQuestionId(Long.parseLong(deviceSequence[1].toString()));						
									questionSequenceVO.setNumber(Integer.parseInt(deviceSequence[2].toString()));
									questionSequenceVO.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(questionSequenceVO.getNumber(), locale));
									questionSequenceVOs.add(questionSequenceVO);
								}
							}													
						}
					}
					ballotVO.setQuestionSequenceVOs(questionSequenceVOs);
				}			
				ballotVOs.add(ballotVO);
			}
		}
		return ballotVOs;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<DeviceVO> findBallotedQuestionVOs(final Session session, final DeviceType deviceType, final Group group, final Date answeringDate,
			final String locale) throws ELSException {			
		List<DeviceVO> deviceVOs = new ArrayList<DeviceVO>();		
		
		Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{locale.toString()});
		parametersMap.put("sessionId", new String[]{session.getId().toString()});
		parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
		if(group!=null && group.getId()!=null) {
			parametersMap.put("groupId", new String[]{group.getId().toString()});
		} else {
			parametersMap.put("groupId", new String[]{"0"});
		}				
		parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});
		List ballotVOs = org.mkcl.els.domain.Query.findReport("YADI_BALLOT_VIEW", parametersMap);
		parametersMap = null;
		
		if(ballotVOs!=null && !ballotVOs.isEmpty()) {
			List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
			for(Object i: ballotVOs) {
				Object[] ballotVO = (Object[])i;
				QuestionSequenceVO questionSequenceVO = new QuestionSequenceVO();
				if(ballotVO[0]!=null && !ballotVO[0].toString().isEmpty()) {
					questionSequenceVO.setMemberId(Long.parseLong(ballotVO[0].toString()));
				}
				if(ballotVO[1]!=null && !ballotVO[1].toString().isEmpty()) {
					questionSequenceVO.setQuestionId(Long.parseLong(ballotVO[1].toString()));
				}
				if(ballotVO[2]!=null && !ballotVO[2].toString().isEmpty()) {
					questionSequenceVO.setNumber(Integer.parseInt(ballotVO[2].toString()));
				}
				if(ballotVO[3]!=null && !ballotVO[3].toString().isEmpty()) {
					questionSequenceVO.setSequenceNo(Integer.parseInt(ballotVO[3].toString()));
				}
				if(ballotVO.length>=5) {
					if(ballotVO[4]!=null && !ballotVO[4].toString().isEmpty()) {
						questionSequenceVO.setQuestionreferenceText(ballotVO[4].toString());
					}					
				}
				questionSequenceVOs.add(questionSequenceVO);
			}
			QuestionSequenceVO.sortBySequenceNumber(questionSequenceVOs);
			int count=0;
			for(QuestionSequenceVO questionSequenceVO: questionSequenceVOs) {
				System.out.println(questionSequenceVO.getNumber() + ": " + questionSequenceVO.getSequenceNo());
				DeviceVO deviceVO = new DeviceVO();
				count++;
				deviceVO.setSerialNumber(FormaterUtil.formatNumberNoGrouping(count, locale));
				deviceVO.setId(questionSequenceVO.getQuestionId());
				deviceVO.setNumber(questionSequenceVO.getNumber());
				deviceVO.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(questionSequenceVO.getNumber(), locale));
				deviceVO.setQuestionReferenceText(questionSequenceVO.getQuestionreferenceText());
				Question q = Question.findById(Question.class, questionSequenceVO.getQuestionId());
				/**** Member Names ****/
				Member ballotEntryMember = Member.findById(Member.class, questionSequenceVO.getMemberId());
				String houseType = session.findHouseType();
				String allMemberNames = "";	
				String ballotEntryMemberName = "";
				String questionMemberNames = "";
				CustomParameter memberNameFormatParameter = null;
				String memberNameFormat = null;
				if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_YADI_MEMBERNAMEFORMAT_LOWERHOUSE", "");
					if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
						memberNameFormat = memberNameFormatParameter.getValue();						
					} else {
						memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;						
					}
					if(ballotEntryMember.isSupportingOrClubbedMemberToBeAddedForDevice(q)) {
						ballotEntryMemberName = ballotEntryMember.findNameWithConstituencyInGivenFormat(q.getSession().getHouse(), memberNameFormat);
					} else {
						ballotEntryMemberName = "";
					}
					questionMemberNames = q.findAllMemberNamesWithConstituencies(memberNameFormat);
				} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
					
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_YADI_MEMBERNAMEFORMAT_UPPERHOUSE", "");
					if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
						memberNameFormat = memberNameFormatParameter.getValue();						
					} else {
						memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;						
					}
					
					// Added by AmitD on 12 Dec 2014
//					if(!ballotEntryMember.isActiveMemberInAnyOfGivenRolesOn(ApplicationConstants.NON_MEMBER_ROLES.split(","), new Date(), locale)
//							&& !ballotEntryMember.isActiveMinisterOn(new Date(), locale)) {
//						ballotEntryMemberName = ballotEntryMember.findNameInGivenFormat(memberNameFormat);
//					} else {
//						ballotEntryMemberName = "";
//					}
					if(ballotEntryMember.isSupportingOrClubbedMemberToBeAddedForDevice(q)) {
						ballotEntryMemberName = ballotEntryMember.findNameInGivenFormat(memberNameFormat);
					} else {
						ballotEntryMemberName = "";
					}
					
					questionMemberNames = q.findAllMemberNames(memberNameFormat);										
				}
				if(!ballotEntryMemberName.isEmpty()) {
					String[] questionMemberNamesArr = questionMemberNames.split(",");
					StringBuffer revisedQuestionMemberNames = new StringBuffer();
					for(int k=0; k<questionMemberNamesArr.length; k++) {
						if(k==0) {
							if(!questionMemberNamesArr[k].trim().equals(ballotEntryMemberName)) {
								if(q.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
									if(q.getPrimaryMember().isSupportingOrClubbedMemberToBeAddedForDevice(q)) {
										revisedQuestionMemberNames.append("");
									} else {
										revisedQuestionMemberNames.append(questionMemberNamesArr[k]);
									}
								} else {
									revisedQuestionMemberNames.append(questionMemberNamesArr[k]);
								}									
							}							
						} else {
							if(!questionMemberNamesArr[k].trim().equals(ballotEntryMemberName)) {
								revisedQuestionMemberNames.append("," + questionMemberNamesArr[k]);
							}							
						}
					}
					questionMemberNames = revisedQuestionMemberNames.toString();									
				}				
				if(!questionMemberNames.isEmpty()) {
					if(questionMemberNames.startsWith(", ")) {
						allMemberNames = ballotEntryMemberName + questionMemberNames;						
					} else {
						if(!ballotEntryMemberName.isEmpty()) {
							allMemberNames = ballotEntryMemberName + ", " + questionMemberNames;
						} else {
							allMemberNames = questionMemberNames;
						}
					}					
				} else {
					allMemberNames = ballotEntryMemberName;
				}
				List<Title> titles = Title.findAll(Title.class, "name", ApplicationConstants.ASC, locale);
				if(titles!=null && !titles.isEmpty()) {
					for(Title t: titles) {
						if(t.getName().trim().endsWith(".")) {
							allMemberNames = allMemberNames.replace(t.getName().trim()+" ", t.getName().trim());
						}
					}
				}
				deviceVO.setMemberNames(allMemberNames);
				//=============================================================================
				if(q.getRevisedSubject()!=null && !q.getRevisedSubject().isEmpty()) {
					deviceVO.setSubject(q.getRevisedSubject());
				} else if(q.getSubject()!=null && !q.getSubject().isEmpty()) {
					deviceVO.setSubject(q.getSubject());
				}
				String content = q.getRevisedQuestionText();
				if(content!=null && !content.isEmpty()) {
					if(content.endsWith("<br><p></p>")) {
						content = content.substring(0, content.length()-11);						
					} else if(content.endsWith("<p></p>")) {
						content = content.substring(0, content.length()-7);					
					}
					//content = FormaterUtil.formatNumbersInGivenText(content, locale);
					deviceVO.setContent(content);
				} else {
					content = q.getQuestionText();
					if(content!=null && !content.isEmpty()) {
						if(content.endsWith("<br><p></p>")) {
							content = content.substring(0, content.length()-11);							
						} else if(content.endsWith("<p></p>")) {
							content = content.substring(0, content.length()-7);					
						}
						//content = FormaterUtil.formatNumbersInGivenText(content, locale);
						deviceVO.setContent(content);
					}
				}						
				String answer = q.getAnswer();
				if(answer != null) {
					if(answer.endsWith("<br><p></p>")) {
						answer = answer.substring(0, answer.length()-11);						
					} else if(answer.endsWith("<p></p>")) {
						answer = answer.substring(0, answer.length()-7);					
					}
					//answer = FormaterUtil.formatNumbersInGivenText(answer, locale);
				}				
				deviceVO.setAnswer(answer);				
				Member answeringMember = MemberMinister.findMemberHavingMinistryInSession(session, q.getMinistry());
				if(answeringMember != null){
					deviceVO.setAnsweredBy(answeringMember.findNameInGivenFormat(memberNameFormat));
				}
				/** Ministry name as per subdepartment name **/
				if(q.getSubDepartment().getMinistryDisplayName()!=null 
						&& !q.getSubDepartment().getMinistryDisplayName().isEmpty()) {
					deviceVO.setMinistryName(q.getSubDepartment().getMinistryDisplayName());
				} else {
					deviceVO.setMinistryName(q.getSubDepartment().getName());
				}
				try {
					MemberMinister memberMinister = Question.findMemberMinisterIfExists(q);
					if(memberMinister!=null) {
						deviceVO.setPrimaryMemberDesignation(memberMinister.getDesignation().getName());
					} else {
						deviceVO.setPrimaryMemberDesignation("");
					}
				} catch(ELSException ex) {
					deviceVO.setPrimaryMemberDesignation("");
				}
				/** referenced question details (later should come through referenced entities) **/
				if(deviceVO.getQuestionReferenceText()==null || deviceVO.getQuestionReferenceText().isEmpty()) {
					String questionReferenceText = q.getQuestionreferenceText();
					if(questionReferenceText!=null) {
						if(questionReferenceText.endsWith("<br><p></p>")) {
							questionReferenceText = questionReferenceText.substring(0, questionReferenceText.length()-11);						
						} else if(questionReferenceText.endsWith("<p></p>")) {
							questionReferenceText = questionReferenceText.substring(0, questionReferenceText.length()-7);					
						}
						//questionReferenceText = FormaterUtil.formatNumbersInGivenText(questionReferenceText, locale);
						deviceVO.setQuestionReferenceText(questionReferenceText);
					} else {
						deviceVO.setQuestionReferenceText("");
					}
				}				
				deviceVOs.add(deviceVO);
			}
		} else {
			deviceVOs = null;
		}
				
		return deviceVOs;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<RoundVO> findBallotedRoundVOsForSuchi(final Session session, final DeviceType deviceType, final String processingMode,
			final Group group, final Date answeringDate, final String locale) throws ELSException {
		//first we find balloted questions in sequence order
		List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
		
		Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{locale.toString()});
		parametersMap.put("sessionId", new String[]{session.getId().toString()});
		parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
		if(group!=null && group.getId()!=null) {
			parametersMap.put("groupId", new String[]{group.getId().toString()});
		} else {
			parametersMap.put("groupId", new String[]{"0"});
		}		
		parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});
		List ballotVOs = org.mkcl.els.domain.Query.findReport("YADI_BALLOT_VIEW", parametersMap);
		parametersMap = null;
		
		if(ballotVOs!=null && !ballotVOs.isEmpty()) {
			for(Object i: ballotVOs) {
				Object[] ballotVO = (Object[])i;
				QuestionSequenceVO questionSequenceVO = new QuestionSequenceVO();
				if(ballotVO[0]!=null && !ballotVO[0].toString().isEmpty()) {
					questionSequenceVO.setMemberId(Long.parseLong(ballotVO[0].toString()));
				}
				if(ballotVO[1]!=null && !ballotVO[1].toString().isEmpty()) {
					questionSequenceVO.setQuestionId(Long.parseLong(ballotVO[1].toString()));
				}
				if(ballotVO[2]!=null && !ballotVO[2].toString().isEmpty()) {
					questionSequenceVO.setNumber(Integer.parseInt(ballotVO[2].toString()));
				}
				if(ballotVO[3]!=null && !ballotVO[3].toString().isEmpty()) {
					questionSequenceVO.setSequenceNo(Integer.parseInt(ballotVO[3].toString()));
				}
				if(ballotVO.length>=5) {
					if(ballotVO[4]!=null && !ballotVO[4].toString().isEmpty()) {
						questionSequenceVO.setQuestionreferenceText(ballotVO[4].toString());
					}					
				}
				questionSequenceVOs.add(questionSequenceVO);
			}
			QuestionSequenceVO.sortBySequenceNumber(questionSequenceVOs);
		}
		for(QuestionSequenceVO questionSequenceVO: questionSequenceVOs) {
			System.out.println(questionSequenceVO.getMemberId()+": "+questionSequenceVO.getQuestionId()
					+": "+questionSequenceVO.getNumber()+": "+questionSequenceVO.getSequenceNo());
		}
		//now we arrange them in roundwise order.
		List<RoundVO> roundVOs = new ArrayList<RoundVO>();		
		String numberOfRoundsStr="0";
		if((processingMode==null && session.findHouseType().equals(ApplicationConstants.LOWER_HOUSE))
				|| (processingMode!=null && processingMode.equals(ApplicationConstants.LOWER_HOUSE))) {
			
			numberOfRoundsStr = session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_BALLOT);				
		
		} else if((processingMode==null && session.findHouseType().equals(ApplicationConstants.UPPER_HOUSE))
				|| (processingMode!=null && processingMode.equals(ApplicationConstants.UPPER_HOUSE))) {
			
			numberOfRoundsStr = session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_FINAL);				
		}		
		//crucial time.. find number of questions in each round
		int numberOfRounds=Integer.parseInt(numberOfRoundsStr);
		int memberIndex=0;			
		List<Integer> questionsInRounds = new ArrayList<Integer>();
		for(int i=1; i<numberOfRounds;i++) {				
			while(questionsInRounds.size()<i) {
				Long memberId = null;
				try {
					memberId = questionSequenceVOs.get(memberIndex).getMemberId();
					boolean toNextMember = true;
					int currentIndex=0;
					for(QuestionSequenceVO qs: questionSequenceVOs) {					
						if(currentIndex<=memberIndex) {
							currentIndex++;
							continue;
						}
						else if(qs.getMemberId().equals(memberId)) {
							int questionsExcludingLastRound = 0;
							for(int k : questionsInRounds) {				
								questionsExcludingLastRound += k;
							}
							questionsInRounds.add(currentIndex - questionsExcludingLastRound);
							//questionsInRounds.add(currentIndex-memberIndex);
							toNextMember = false;
							break;
						}
						currentIndex++;
					}
					if(toNextMember) {					
						memberIndex++;
						if(memberIndex<questionSequenceVOs.size()) {
							currentIndex=0;
						} else {
							int questionsExcludingLastRound = 0;
							for(int l : questionsInRounds) {				
								questionsExcludingLastRound += l;
							}
							questionsInRounds.add(questionSequenceVOs.size() - questionsExcludingLastRound);
						}					
					}
				} catch(IndexOutOfBoundsException e) {
					//in case when no questions are available for next round
					questionsInRounds.add(0);
					break;
				}				
			}
			memberIndex = 0;
			for(int j=0; j<i; j++) {
				memberIndex += questionsInRounds.get(j);
			}					
		}			
		int questionsExcludingLastRound = 0;
		for(int i : questionsInRounds) {				
			questionsExcludingLastRound += i;
		}
		questionsInRounds.add(questionSequenceVOs.size() - questionsExcludingLastRound);		
		//now gather all details of each round		
		int questionsTillGivenRound = 0;
		int count=0;
		for(int i : questionsInRounds) {
			if(i>0) {
				String formattedNumberOfQuestionsInGivenRound = FormaterUtil.formatNumberNoGrouping(i, locale);
				String firstElementInGivenRound = FormaterUtil.formatNumberNoGrouping(questionsTillGivenRound+1, locale);
				String lastElementInGivenRound = FormaterUtil.formatNumberNoGrouping(questionsTillGivenRound+i, locale);
				RoundVO roundVO = new RoundVO();
				roundVO.setNumberOfQuestionsInGivenRound(i);
				roundVO.setFormattedNumberOfQuestionsInGivenRound(formattedNumberOfQuestionsInGivenRound);
				roundVO.setFirstElementInGivenRound(firstElementInGivenRound);
				roundVO.setFirstElementInGivenRoundInt(questionsTillGivenRound+1);				
				roundVO.setLastElementInGivenRound(lastElementInGivenRound);
				roundVO.setLastElementInGivenRoundInt(questionsTillGivenRound+i);
				List<DeviceVO> deviceVOs = new ArrayList<DeviceVO>();
				for(int j=questionsTillGivenRound; j<(questionsTillGivenRound + i); j++) {
					DeviceVO deviceVO = new DeviceVO();
					count++;
					deviceVO.setSerialNumber(FormaterUtil.formatNumberNoGrouping(count, locale));
					deviceVO.setId(questionSequenceVOs.get(j).getQuestionId());
					deviceVO.setNumber(questionSequenceVOs.get(j).getNumber());
					deviceVO.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(questionSequenceVOs.get(j).getNumber(), locale));
					deviceVO.setQuestionReferenceText(questionSequenceVOs.get(j).getQuestionreferenceText());
					Question q = Question.findById(Question.class, questionSequenceVOs.get(j).getQuestionId());
					/**** Member Names ****/
					Member ballotEntryMember = Member.findById(Member.class, questionSequenceVOs.get(j).getMemberId());
					String houseType = session.findHouseType();
					String allMemberNames = "";	
					String ballotEntryMemberName = "";
					String questionMemberNames = "";
					CustomParameter memberNameFormatParameter = null;
					String memberNameFormat = null;
					if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
						memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_YADI_MEMBERNAMEFORMAT_LOWERHOUSE", "");
						if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
							memberNameFormat = memberNameFormatParameter.getValue();						
						} else {
							memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;						
						}
						if(ballotEntryMember.isSupportingOrClubbedMemberToBeAddedForDevice(q)) {
							ballotEntryMemberName = ballotEntryMember.findNameInGivenFormat(memberNameFormat);
						} else {
							ballotEntryMemberName = "";
						}						
						questionMemberNames = q.findAllMemberNames(memberNameFormat);
					} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
						
						memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_YADI_MEMBERNAMEFORMAT_UPPERHOUSE", "");
						if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
							memberNameFormat = memberNameFormatParameter.getValue();						
						} else {
							memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;						
						}
						if(!ballotEntryMember.isActiveMemberInAnyOfGivenRolesOn(ApplicationConstants.NON_MEMBER_ROLES.split(","), new Date(), locale)
								&& !ballotEntryMember.isActiveMinisterOn(new Date(), locale)) {
							ballotEntryMemberName = ballotEntryMember.findNameInGivenFormat(memberNameFormat);
						} else {
							ballotEntryMemberName = "";
						}
						questionMemberNames = q.findAllMemberNames(memberNameFormat);										
					}
					if(!ballotEntryMemberName.isEmpty()) {
						String[] questionMemberNamesArr = questionMemberNames.split(",");
						StringBuffer revisedQuestionMemberNames = new StringBuffer();
						for(int k=0; k<questionMemberNamesArr.length; k++) {
							if(k==0) {
								if(!questionMemberNamesArr[k].trim().equals(ballotEntryMemberName)) {
									if(q.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
										if(q.getPrimaryMember().isSupportingOrClubbedMemberToBeAddedForDevice(q)) {
											revisedQuestionMemberNames.append("");
										} else {
											revisedQuestionMemberNames.append(questionMemberNamesArr[k]);
										}
									} else {
										revisedQuestionMemberNames.append(questionMemberNamesArr[k]);
									}									
								}							
							} else {
								if(!questionMemberNamesArr[k].trim().equals(ballotEntryMemberName)) {
									revisedQuestionMemberNames.append("," + questionMemberNamesArr[k]);
								}							
							}
						}
						questionMemberNames = revisedQuestionMemberNames.toString();									
					}				
					if(!questionMemberNames.isEmpty()) {
						if(questionMemberNames.startsWith(", ")) {
							allMemberNames = ballotEntryMemberName + questionMemberNames;						
						} else {
							if(!ballotEntryMemberName.isEmpty()) {
								allMemberNames = ballotEntryMemberName + ", " + questionMemberNames;
							} else {
								allMemberNames = questionMemberNames;
							}
						}					
					} else {
						allMemberNames = ballotEntryMemberName;
					}
					List<Title> titles = Title.findAll(Title.class, "name", ApplicationConstants.ASC, locale);
					if(titles!=null && !titles.isEmpty()) {
						for(Title t: titles) {
							if(t.getName().trim().endsWith(".")) {
								allMemberNames = allMemberNames.replace(t.getName().trim()+" ", t.getName().trim());
							}
						}
					}
					deviceVO.setMemberNames(allMemberNames);
					//=============================================================================	
					if(q.getRevisedSubject()!=null && !q.getRevisedSubject().isEmpty()) {
						deviceVO.setSubject(q.getRevisedSubject());
					} else if(q.getSubject()!=null && !q.getSubject().isEmpty()) {
						deviceVO.setSubject(q.getSubject());
					}
					String content = q.getRevisedQuestionText();
					if(content!=null && !content.isEmpty()) {
						if(content.endsWith("<br><p></p>")) {
							content = content.substring(0, content.length()-11);							
						} else if(content.endsWith("<p></p>")) {
							content = content.substring(0, content.length()-7);							
						}
						//content = FormaterUtil.formatNumbersInGivenText(content, locale);
						deviceVO.setContent(content);
					} else {
						content = q.getQuestionText();
						if(content!=null && !content.isEmpty()) {
							if(content.endsWith("<br><p></p>")) {
								content = content.substring(0, content.length()-11);								
							} else if(content.endsWith("<p></p>")) {
								content = content.substring(0, content.length()-7);							
							}
							//content = FormaterUtil.formatNumbersInGivenText(content, locale);
							deviceVO.setContent(content);
						}
					}							
					String answer = q.getAnswer();
					if(answer != null) {
						if(answer.endsWith("<br><p></p>")) {
							answer = answer.substring(0, answer.length()-11);							
						} else if(answer.endsWith("<p></p>")) {
							answer = answer.substring(0, answer.length()-7);							
						}
						//answer = FormaterUtil.formatNumbersInGivenText(answer, locale);
					}				
					deviceVO.setAnswer(answer);				
					Member answeringMember = MemberMinister.findMemberHavingMinistryInSession(session, q.getMinistry());
					List<MemberRole> memberRoles = HouseMemberRoleAssociation.findAllActiveRolesOfMemberInSession(answeringMember, session, locale);
					for(MemberRole l : memberRoles) {
						if(l.getType().equals(ApplicationConstants.CHIEF_MINISTER) || l.getType().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)) {
							deviceVO.setMinistryName(q.getMinistry().getName());
							break;
						}
					}
					if(deviceVO.getMinistryName()==null) {
						Role ministerRole = Role.findByFieldName(Role.class, "type", ApplicationConstants.MINISTER, locale);
						String localizedMinisterRoleName = ministerRole.getLocalizedName();				
						deviceVO.setMinistryName(q.getSubDepartment().getName() + " " + localizedMinisterRoleName);
					}				
					if(answeringMember != null){
						deviceVO.setAnsweredBy(answeringMember.findFirstLastName());
					}
					/** referenced question details (later should come through referenced entities) **/
					if(deviceVO.getQuestionReferenceText()==null || deviceVO.getQuestionReferenceText().isEmpty()) {
						String questionReferenceText = q.getQuestionreferenceText();
						if(questionReferenceText!=null) {
							if(questionReferenceText.endsWith("<br><p></p>")) {
								questionReferenceText = questionReferenceText.substring(0, questionReferenceText.length()-11);						
							} else if(questionReferenceText.endsWith("<p></p>")) {
								questionReferenceText = questionReferenceText.substring(0, questionReferenceText.length()-7);					
							}
							//questionReferenceText = FormaterUtil.formatNumbersInGivenText(questionReferenceText, locale);
							deviceVO.setQuestionReferenceText(questionReferenceText);
						} else {
							deviceVO.setQuestionReferenceText("");
						}
					}
					deviceVOs.add(deviceVO);
				}
				roundVO.setDeviceVOs(deviceVOs);
				roundVOs.add(roundVO);
				questionsTillGivenRound += i;
			}			
		}
		return roundVOs;
	}
	
	private static List<QuestionSequenceVO> getQuestionSequenceVOs(
			final List<DeviceSequence> sequences) {
		List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
		
		for(DeviceSequence ds : sequences) {
			Question question = (Question) ds.getDevice();
			
			Long id = question.getId();
			Integer number = question.getNumber();
			Integer sequenceNo = ds.getSequenceNo();
			
			QuestionSequenceVO seqVO = new QuestionSequenceVO(id, number, sequenceNo);
			questionSequenceVOs.add(seqVO);
		}
		
		return questionSequenceVOs;
	}
	
	//===============================================
	//
	//=============== DOMAIN METHODS ================
	//
	//===============================================
	public static Ballot create(final Ballot ballot) throws ELSException {
		Session session = ballot.getSession();
		PROCESSING_MODE processingMode = Question.getProcessingMode(session);
		
		try {
			if(processingMode == PROCESSING_MODE.LOWERHOUSE) {
				return StarredQuestionBallot.createLH(ballot);
			}
			else { // processingMode == PROCESSING_MODE.UPPERHOUSE)
				return StarredQuestionBallot.createUH(ballot);
			}
		}
		catch(ELSException e) {
			throw e;
		}
	}
	
	/**
	 * IMP: This method may need optimization. Benchmark it.
	 * Whenever a Question is removed from Ballot it should be automatically removed from
	 * corresponding PreBallot.
	 */
	public static Ballot removeStarredQuestion(final Question question,
			final boolean isResequenceDevices) throws ELSException {
		Ballot ballot = Ballot.find(question);
		
		if(ballot != null) {
			// Remove the question from corresponding PreBallot
			removeStarredQuestionFromPreBallot(question);
			
			// Remove the question from the Ballot
			List<BallotEntry> ballotEntries = ballot.getBallotEntries();
			for(int i = 0; i < ballotEntries.size(); i++) {
				BallotEntry be = ballotEntries.get(i);
				
				List<DeviceSequence> devSeqs = be.getDeviceSequences();
				for(int j = 0; j < devSeqs.size(); j++) {
					DeviceSequence ds = devSeqs.get(j);
					Question device = (Question) ds.getDevice();
					
					if(device.getId().equals(question.getId())) {
						devSeqs.remove(j);
					}
				}
				if(devSeqs.size() > 0) {
					be.setDeviceSequences(devSeqs);
				}
				else {
					ballotEntries.remove(i);
				}
			}
			
			// Re-sequence the Ballot
			Integer noOfRounds = StarredQuestionBallot.getNoOfRounds(question.getSession().getHouse().getType());
			List<BallotEntry> updatedBallotEntries = addSequenceNumbers(ballotEntries, noOfRounds);
			
			ballot.setBallotEntries(updatedBallotEntries);
			ballot.merge();
			
			return ballot;
		}
		else {
			return null;
		}
	}
	
	private static PreBallot removeStarredQuestionFromPreBallot(final Question question) throws ELSException {
		PreBallot preBallot = PreBallot.find(question);
		if(preBallot != null) {
			// Remove the question from the PreBallot
			List<BallotEntry> ballotEntries = preBallot.getBallotEntries();
			for(int i = 0; i < ballotEntries.size(); i++) {
				BallotEntry be = ballotEntries.get(i);
				
				List<DeviceSequence> devSeqs = be.getDeviceSequences();
				for(int j = 0; j < devSeqs.size(); j++) {
					DeviceSequence ds = devSeqs.get(j);
					Question device = (Question) ds.getDevice();
					
					if(device.getId().equals(question.getId())) {
						devSeqs.remove(j);
					}
				}
				if(devSeqs.size() > 0) {
					be.setDeviceSequences(devSeqs);
				}
				else {
					ballotEntries.remove(i);
				}
			}
			preBallot.setBallotEntries(ballotEntries);
			preBallot.merge();
			return preBallot;
		}
		else {
			return null;
		}
	}

	// TODO: Incomplete implementation
	public static Ballot regenerate(final Ballot ballot) throws ELSException {
		Session session = ballot.getSession();
		Group group = ballot.getGroup();
		DeviceType deviceType = ballot.getDeviceType();
		Date answeringDate = ballot.getAnsweringDate();
		String locale = ballot.getLocale();
				
		PROCESSING_MODE processingMode = Question.getProcessingMode(session);		
		if(processingMode == PROCESSING_MODE.UPPERHOUSE) {
			// Set the ballotStatus of all the Qns in the ballot as null
			List<BallotEntry> ballotEntries = ballot.getBallotEntries();
			for(BallotEntry be : ballotEntries) {
				List<DeviceSequence> dSeqs = be.getDeviceSequences();
				for(DeviceSequence ds : dSeqs) {
					Question q = (Question) ds.getDevice();
					q.setBallotStatus(null);
					q.simpleMerge();
				}
			}
			
			// Remove the ballot
			ballot.remove();
			
			// Regenerate the Ballot
			CustomParameter cp = 
				CustomParameter.findByName(CustomParameter.class, 
						"DB_DATEFORMAT", "");
			String strAnsweringDate = 
				FormaterUtil.getDateFormatter(cp.getValue(), "en_US")
					.format(answeringDate);
			
			String firstBatchSubmissionDate = 
				session.getParameter(ApplicationConstants
						.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
			
			String strTotalRounds = 
				session.getParameter(ApplicationConstants
						.QUESTION_STARRED_NO_OF_ROUNDS_MEMBERBALLOT_FINAL);
			Integer totalRounds = Integer.parseInt(strTotalRounds);
			
			MemberBallot.createFinalBallotUH(session, deviceType, 
					group, strAnsweringDate, answeringDate, 
					locale, firstBatchSubmissionDate, totalRounds);
			
			Ballot newBallot = 
				Ballot.find(session, deviceType, answeringDate, locale);
			
			// Create preballot: based on ballot. This is to be done in order to keep consistency
			// between preballot and ballot.
			// TODO
			return newBallot;
		}
		else if(processingMode == PROCESSING_MODE.LOWERHOUSE) {
			// Set the ballotStatus of all the Qns in the ballot as null
			List<BallotEntry> ballotEntries = ballot.getBallotEntries();
			for(BallotEntry be : ballotEntries) {
				List<DeviceSequence> dSeqs = be.getDeviceSequences();
				for(DeviceSequence ds : dSeqs) {
					Question q = (Question) ds.getDevice();
					q.setBallotStatus(null);
					q.simpleMerge();
				}
			}
			
			// Remove the ballot
			ballot.remove();
			
			// Remove the preballot corresponding to the ballot.
			// TODO
			
			// Create preballot
			// TODO
			
			// Create ballot
			// TODO
			return null;
		}
		else {
			throw new ELSException("Ballot.regenerate/1", 
    				"Illegal invocation. Method invoked for inappropriate" +
    				" deviceType and houseType.");
		}
	}
	
	
	//=================================================
	//
	//=============== COMMON INTERNAL METHODS =========
	//
	//=================================================
	private static Integer getNoOfRounds(final HouseType houseType) throws ELSException {
		String houseTypeType = houseType.getType();
		String upperCaseHouseTypeType = houseTypeType.toUpperCase();
		
		StringBuffer sb = new StringBuffer();
		sb.append("QUESTION_STARRED_BALLOT_NO_OF_ROUNDS_");
		sb.append(upperCaseHouseTypeType);
		
		String parameterName = sb.toString();
		CustomParameter noOfRoundsParameter = 
			CustomParameter.findByName(CustomParameter.class, 
					parameterName, "");
		
		try {
			return Integer.valueOf(noOfRoundsParameter.getValue());
		}
		catch(Exception e) {
			throw new ELSException("StarredQuestionBallot.getNoOfRounds/1",
					"Custom Parameter " +
					"'QUESTION_STARRED_BALLOT_NO_OF_ROUNDS_" + upperCaseHouseTypeType + "' is not configured");
		}
	}
	
	/**
	 * Only members having any Question eligible for this ballot will
	 * appear in the Ballot.
	 * @throws ELSException 
	 */
	private static List<BallotEntry> compute(final Session session,
			final Group group,
			final Date answeringDate,
			final Integer noOfRounds,
			final String locale) throws ELSException {
		List<BallotEntry> entries = new ArrayList<BallotEntry>();

		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
		
		Status internalStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
		Status ballotStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);
		
		// In findMembersEligibleForBallot/7, check that a minister or presiding officer is not
		// chosen. Then remove the isActiveOnlyAsMember check in the for loop.
		List<Member> eligibleMembers = Ballot.getRepository().findMembersEligibleForBallot(session, 
				deviceType, group, answeringDate, internalStatus, ballotStatus, locale);
		for(Member m : eligibleMembers) {
			boolean isActiveOnlyAsMember = StarredQuestionBallot.isActiveOnlyAsMember(m, new Date(), locale);
			
			if(isActiveOnlyAsMember) {
				BallotEntry ballotEntry = StarredQuestionBallot.compute(m, session, deviceType, group,
						answeringDate, internalStatus, ballotStatus, noOfRounds, locale);

				if(ballotEntry != null) {
					entries.add(ballotEntry);
				}
			}
		}

		return entries;
	}
	
	/**
	 * Algorithm:
	 * 1> Create a list of Questions eligible for ballot for all the answeringDates
	 * (including currentAnsweringDate and previousAnsweringDates)
	 * 
	 * 2> Create a list of Balloted Questions for the previousAnsweringDates.
	 * 
	 * 3> The difference between Step 1 list and Step 2 list is the eligible list of
	 * Questions for the current Ballot.
	 * 
	 * 4> Choose as many as @param noOfRounds Questions from Step 3 list. These are the
	 * Questions to be taken on the current ballot.
	 *
	 * Eligibility Algorithm:
	 * A Question is eligible for ballot only if its internalStatus = "ADMITTED",
	 * ballotStatus != "BALLOTED" and it has no parent Question. If a Question has a 
	 * parent, then it's parent may be considered for the Ballot. The kid will never be 
	 * considered for the Ballot.
	 *
	 * Returns a subset of @param questions sorted by priority. If there are no
	 * questions eligible for the ballot, returns an empty list.
	 * Returns null if at the end of Step 4 the @param member do not have any Questions
	 * in the list.
	 * @throws ELSException 
	 */
	private static BallotEntry compute(final Member member,
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Status internalStatus,
			final Status ballotStatus,
			final Integer noOfRounds,
			final String locale) throws ELSException {
		BallotEntry ballotEntry = null;
		
		List<Question> eligibleQuestions = 
			Ballot.getRepository().findQuestionsEligibleForBallot(member, session, deviceType, 
					group, answeringDate, internalStatus, ballotStatus, noOfRounds, locale);
		if(! eligibleQuestions.isEmpty()) {
			List<DeviceSequence> questionSequences = 
				StarredQuestionBallot.createQuestionSequences(eligibleQuestions, locale);
			ballotEntry = new BallotEntry(member, questionSequences, locale);
		}
		
		return ballotEntry;
	}
	
	/**
	 * Creates the question sequences.
	 */
	private static List<DeviceSequence> createQuestionSequences(final List<Question> questions,
			final String locale) {
		List<DeviceSequence> questionSequences = new ArrayList<DeviceSequence>();
		for(Question q : questions) {
			DeviceSequence qs = new DeviceSequence(q, locale);
			questionSequences.add(qs);
		}
		return questionSequences;
	}
	
	// In addition to checking for inactive members, checks for active ministers & handovers there questions too.
	// For this to work don't remove any of the ministers question from Chart. Moreover if the question
	// has to handover to clubbed question's member but is not happening then a strong reason is that the
	// clubbed question's status is not ADMITTED. eligibleSupportingMembers/1 works on this assumption. Besides
	// you can also check that supporting member has decision status as supportingmember_approved, else the
	// supporting member won't come in picture.
	private static List<BallotEntry> inactiveMembersQuestionHandover(
			final Session session,
			final Group group,
			final DeviceType deviceType,
			final List<BallotEntry> ballotEntries,
			final Date answeringDate,
			final Integer noOfRounds,
			final String locale) {
		// Step 1
		House house = session.getHouse();
		
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		Date questionSubmissionStartDate = FormaterUtil.formatStringToDate(
				session.getParameter(
						ApplicationConstants.QUESTION_STARRED_SUBMISSION_STARTTIME), 
				datePattern.getValue(), locale); 
			
		Date sessionToDate = session.getEndDate();		
		List<Member> inactiveMembers = 
			Member.findInactiveMembers(house, questionSubmissionStartDate, 
					sessionToDate, locale);
		
		//======== [START] Hack for 8 Dec 2014 Ballot
		List<Member> activeMinistersToday = 
				Member.findActiveMinisters(new Date(), locale);
		inactiveMembers.addAll(activeMinistersToday);
		//======== [END] Hack for 8 Dec 2014 Ballot
		
		// Step 2
		List<Question> inactiveMemberQuestions = new ArrayList<Question>();
		Status internalStatus = 
			Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
		Status ballotStatus =
			Status.findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);
		for(Member m : inactiveMembers) {			
			List<Question> questions = new ArrayList<Question>();
			try {
				questions = Ballot.getRepository().findQuestionsEligibleForBallot(m, session, 
						deviceType, group, answeringDate, internalStatus, ballotStatus, 
						locale);
			} catch (ELSException e) {
				e.printStackTrace();
			}
			inactiveMemberQuestions.addAll(questions);
		}
		
		// Step 2a
		inactiveMemberQuestions = StarredQuestionBallot.reorderQuestions(
				inactiveMemberQuestions, answeringDate);
		
		// Step 3
		Date currentDate = new Date();
		for(Question q : inactiveMemberQuestions) {
			List<Member> supportingMembers = StarredQuestionBallot.eligibleSupportingMembers(q);
			
			// Step 4
			boolean isHandovered = false;
			for(Member m : supportingMembers) {
				boolean isActiveOnlyAsMember = isActiveOnlyAsMember(m, currentDate, locale);
				if(isActiveOnlyAsMember) {
					isHandovered = 
						StarredQuestionBallot.handoverQuestionToMember(q, m, ballotEntries, 
								noOfRounds, locale);
					if(isHandovered) {
						break;
					}
				}
			}
		}
		
		// Step 5
		List<BallotEntry> sortedBallotEntries = StarredQuestionBallot.sortByMember(ballotEntries);
		return sortedBallotEntries;
	}
		
	private static List<Question> reorderQuestions(final List<Question> questions,
			final Date answeringDate) {
		List<Question> qList = new ArrayList<Question>();
		qList.addAll(questions);		
		
		Comparator<Question> c = new Comparator<Question>() {
			
			@Override
			public int compare(final Question q1, final Question q2) {
				Date q1ChartAnsweringDate = 
					q1.getChartAnsweringDate().getAnsweringDate();
				Long q1MemberId = q1.getPrimaryMember().getId();
				Integer q1Priority = q1.getPriority();
				Integer q1Number = q1.getNumber();
				
				Date q2ChartAnsweringDate = 
					q2.getChartAnsweringDate().getAnsweringDate();
				Long q2MemberId = q2.getPrimaryMember().getId();
				Integer q2Priority = q2.getPriority();
				Integer q2Number = q2.getNumber();
				
				if(q1ChartAnsweringDate.equals(answeringDate)
						&& q2ChartAnsweringDate.equals(answeringDate)) {
					if(q1MemberId.equals(q2MemberId)) {
						int i = q1Priority.compareTo(q2Priority);
	                    if(i == 0) {
	                        int j = q1Number.compareTo(q2Number);
	                        return j;
	                    }
	                    return i;
					}
					else {
						int j = q1Number.compareTo(q2Number);
						return j;
					}
				}
				else if(q1ChartAnsweringDate.equals(answeringDate)
						&& q2ChartAnsweringDate.before(answeringDate)) {
					return -1;
				}
				else if(q1ChartAnsweringDate.before(answeringDate)
						&& q2ChartAnsweringDate.equals(answeringDate)) {
					return 1;
				}
				else if(q1ChartAnsweringDate.before(answeringDate)
						&& q2ChartAnsweringDate.before(answeringDate)) {
					if(q1ChartAnsweringDate.before(q2ChartAnsweringDate)) {
						return -1;
					}
					else if(q1ChartAnsweringDate.after(q2ChartAnsweringDate)) {
						return 1;
					}
					else { //q1ChartAnsweringDate.equal(q2ChartAnsweringDate)
						if(q1MemberId.equals(q2MemberId)) {
							int i = q1Priority.compareTo(q2Priority);
		                    if(i == 0) {
		                        int j = q1Number.compareTo(q2Number);
		                        return j;
		                    }
		                    return i;
						}
						else {
							int j = q1Number.compareTo(q2Number);
							return j;
						}
					}
				}
				
				return 0;
			}
		};
		Collections.sort(qList, c);
		
		return qList;
	}
	
	private static List<Member> eligibleSupportingMembers(final Question question) {
		List<Member> members = new ArrayList<Member>();
		
		List<Member> immediateSupportingMembers = 
			StarredQuestionBallot.immediateSupportingMembers(question);
		members.addAll(immediateSupportingMembers);
		
		List<ClubbedEntity> clubbings = Question.findClubbedEntitiesByPosition(question);
		if(clubbings != null) {
			for(ClubbedEntity ce : clubbings) {
				Question q = ce.getQuestion();
				
				String internalStatus = q.getInternalStatus().getType();
				if(internalStatus.equals(
						ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
					Member primaryMember = q.getPrimaryMember();
					members.add(primaryMember);
					
					List<Member> sms = StarredQuestionBallot.immediateSupportingMembers(q);
					members.addAll(sms);
				}
			}
		}
		
		return members;
	}
	
	private static boolean handoverQuestionToMember(final Question question,
			final Member member,
			final List<BallotEntry> ballotEntries,
			final Integer noOfRounds,
			final String locale) {
		boolean isHandovered = false;
		
		BallotEntry ballotEntry = Ballot.findBallotEntry(ballotEntries, member);
		if(ballotEntry == null) {
			List<DeviceSequence> deviceSequences = 
				Ballot.createDeviceSequences(question, locale);
			ballotEntry = new BallotEntry(member, deviceSequences, locale);
			ballotEntries.add(ballotEntry);
			isHandovered = true;
		}
		else {
			List<DeviceSequence> deviceSequences = ballotEntry.getDeviceSequences();
			int size = deviceSequences.size();
			if(size < noOfRounds) {
				DeviceSequence sequence = new DeviceSequence(question, locale);
				deviceSequences.add(sequence);
				isHandovered = true;
			}
		}
		
		return isHandovered;
	}
	
	private static List<BallotEntry> sortByMember(
			final List<BallotEntry> ballotEntries) {
		List<BallotEntry> ballotEntryList = new ArrayList<BallotEntry>();
		ballotEntryList.addAll(ballotEntries);
		
		Comparator<BallotEntry> c = new Comparator<BallotEntry>() {

            @Override
            public int compare(final BallotEntry be1, final BallotEntry be2) {
            	String member1LastName = be1.getMember().getLastName();
            	String member1FirstName = be1.getMember().getFirstName();
            	
            	String member2LastName = be2.getMember().getLastName();
            	String member2FirstName = be2.getMember().getFirstName();
                
            	int i =  member1LastName.compareTo(member2LastName);
            	if(i == 0) {
            		return member1FirstName.compareTo(member2FirstName);
            	}
            	return i;
            }
        };
        Collections.sort(ballotEntryList, c);
        
		return ballotEntryList;
	}
	
	private static List<Member> immediateSupportingMembers(final Question question) {
		List<Member> members = new ArrayList<Member>();
		
		List<SupportingMember> supportingMembers = question.getSupportingMembers();
		if(supportingMembers != null) {
			for(SupportingMember sm : supportingMembers) {
				boolean isApprovedSupportingMember = 
					sm.getDecisionStatus().getType().equals(
						ApplicationConstants.SUPPORTING_MEMBER_APPROVED);
				if(isApprovedSupportingMember) {
					Member member = sm.getMember();
					members.add(member);
				}
			}
		}
		
		return members;
	}
	
	/**
	 * 3 stepped process:
	 * 1> Compute Ballot entries.
	 * 2> Randomize Ballot entries.
	 * 3> Add sequence numbers.
	 *
	 * Creates a new Ballot. If a ballot already exists then return the
	 * existing Ballot.
	 * @throws ELSException 
	 */
	// private static Ballot createStarredAssemblyBallot(final Ballot b) throws ELSException {
	private static Ballot createLH(final Ballot b) throws ELSException {
		Session session = b.getSession();
		DeviceType deviceType = b.getDeviceType();
		Date answeringDate = b.getAnsweringDate();
		String locale = b.getLocale();
		HouseType houseType = session.getHouse().getType();
		String strHouseType = houseType.getType();
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		
		if(ballot == null) {
			Integer noOfRounds = StarredQuestionBallot.getNoOfRounds(session.getHouse().getType());			
			PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
			if(preBallot == null) {
				throw new ELSException("Ballot_createStarredAssemblyBallot", "PRE_BALLOT_NOT_CREATED");
			}
			else {
				List<BallotEntry> randomizedList = new ArrayList<BallotEntry>();
				List<BallotEntry> preBallotList = 
						StarredQuestionBallot.findPreBallotEntries(preBallot.getBallotEntries()); 
				if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
					CustomParameter csptFineTuneEntries = CustomParameter.findByName(CustomParameter.class, "FINE_TUNE_STARRED_BALLOT_ENTRIES", "");
					if(csptFineTuneEntries!=null && csptFineTuneEntries.getValue()!=null
							&& csptFineTuneEntries.getValue().equals("YES")) {
						randomizedList = StarredQuestionBallot.customRandomizeWithClarificationFactor(preBallotList, session, deviceType, answeringDate, locale);
					} else {
						randomizedList = StarredQuestionBallot.randomize(preBallotList);
					}
					
				}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
					randomizedList = StarredQuestionBallot.customRandomize(preBallotList, session, deviceType, answeringDate, locale);
				}
				
				List<BallotEntry> sequencedList = StarredQuestionBallot.addSequenceNumbers(randomizedList, noOfRounds);
				b.setBallotEntries(sequencedList);
				b.persist();
				
				Status BALLOTED = 
					Status.findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);
				Ballot.getRepository().updateBallotQuestions(b, BALLOTED);
				
				return b;
			}
		}
		else {
			return ballot;
		}
	}
	
	private static List<BallotEntry> customRandomize(List<BallotEntry> ballotEntries, Session session, DeviceType deviceType, Date answeringDate, String locale) throws ELSException {
		List<BallotEntry> preBallotList = StarredQuestionBallot.randomize(ballotEntries);
		List<BallotEntry> finalBallotEntries = new ArrayList<BallotEntry>();
		List<BallotEntry> uniqueBallotEntries = new ArrayList<BallotEntry>();
		List<BallotEntry> nonUniqueBallotEntries = new ArrayList<BallotEntry>();
		/**** Check How Many Positions Cannot Be Same ****/
		CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "NUMBER_OF_UNIQUE_POSITION_ACROSS_BALLOT", "");
		int noOfUniquePositions = Integer.parseInt(customParameter.getValue());
		
		for(BallotEntry i: preBallotList){
			Boolean unique = membersNotPresentAtPositionX(session,deviceType,answeringDate,i,noOfUniquePositions,locale);
			if(unique && uniqueBallotEntries.size()<=noOfUniquePositions){					
				uniqueBallotEntries.add(i);
			}else{
				nonUniqueBallotEntries.add(i);
			}
		}		
		finalBallotEntries.addAll(uniqueBallotEntries);
		finalBallotEntries.addAll(nonUniqueBallotEntries);
		
		return finalBallotEntries;
	}
	
	private static List<BallotEntry> customRandomizeWithClarificationFactor(List<BallotEntry> ballotEntries, Session session, DeviceType deviceType, Date answeringDate, String locale) throws ELSException {
		List<BallotEntry> preBallotList = StarredQuestionBallot.randomize(ballotEntries);
		List<BallotEntry> finalBallotEntries = new ArrayList<BallotEntry>();
		List<BallotEntry> uniqueBallotEntries = new ArrayList<BallotEntry>();
		List<BallotEntry> nonUniqueBallotEntries = new ArrayList<BallotEntry>();
		/**** Check How Many Positions Cannot Be Same ****/
		CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "NUMBER_OF_CLARIFICATIONLESS_POSITION_ACROSS_BALLOT", "");
		int noOfUniquePositions = Integer.parseInt(customParameter.getValue());
		
		for(BallotEntry i: preBallotList){
			Boolean unique = membersNotPresentAtPositionXWithClarificationQuestions(session,deviceType,answeringDate,i,noOfUniquePositions,locale);
			if(unique && uniqueBallotEntries.size()<=noOfUniquePositions){					
				uniqueBallotEntries.add(i);
			}else{
				nonUniqueBallotEntries.add(i);
			}
		}	
		if(!uniqueBallotEntries.isEmpty()) {
			finalBallotEntries.addAll(uniqueBallotEntries);
		}		
		List<BallotEntry> nonUniqueBallotEntriesRandomized = StarredQuestionBallot.randomize(nonUniqueBallotEntries);
		finalBallotEntries.addAll(nonUniqueBallotEntriesRandomized);
		
		return finalBallotEntries;
	}

	private static Boolean membersNotPresentAtPositionX(Session session, DeviceType deviceType, Date answeringDate,
			BallotEntry ballotEntry, Integer noOfUniquePositions, String locale) throws ELSException {
		Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{locale.toString()});
		parametersMap.put("sessionId", new String[]{session.getId().toString()});
		parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
		parametersMap.put("membersAtPositionXId", new String[]{ballotEntry.getMember().getId().toString()});
		parametersMap.put("noOfUniquePositions", new String[]{noOfUniquePositions.toString()});
		parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});
		List counts = org.mkcl.els.domain.Query.findReport("QIS_MEMBER_POSITIONCOUNT_IN_BALLOT", parametersMap);
		Long count = null;
		try {
			for(Object i: counts) {
				if(i!=null) {
					String counter =   i.toString();
					count = Long.parseLong(counter);
													
				}
			}
			if(count==0){
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			ELSException elsException = new ELSException();
			elsException.setParameter("StarredQuestionBallot_Boolean_membersNotPrsentAtPositionX", "No member position found.");
			throw elsException;
		}
	}

	private static List<BallotEntry> findPreBallotEntries(
			final List<BallotEntry> preBallotList) {
		List<BallotEntry> preBallotEntryList = new ArrayList<BallotEntry>();
		
		for(BallotEntry be : preBallotList) {
			Member member = be.getMember();
			String locale = be.getLocale();
			
			List<Question> questions = StarredQuestionBallot.preBallotQuestions(be);
			List<DeviceSequence> questionSequences = StarredQuestionBallot.createQuestionSequences(questions, locale);
			
			BallotEntry nbe = new BallotEntry(member, questionSequences, locale);
			preBallotEntryList.add(nbe);
		}
		
		return preBallotEntryList;
	}
	
	private static List<Question> preBallotQuestions(
			final List<DeviceSequence> deviceSequences) {
		List<Question> questions = new ArrayList<Question>();
		
		for(DeviceSequence ds : deviceSequences) {
			Question q = (Question) ds.getDevice();
			questions.add(q);
		}
		
		return questions;
	}
	
	private static List<Question> preBallotQuestions(final BallotEntry be) {		
		return Ballot.getRepository().findQuestionsForBallotEntry(be);
	}

	/**
	 * Does not shuffle in place, returns a new list.
	 */
	private static List<BallotEntry> randomize(final List<BallotEntry> ballotEntryList) {
		List<BallotEntry> newBallotEntryList = new ArrayList<BallotEntry>();
		newBallotEntryList.addAll(ballotEntryList);
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		Collections.shuffle(newBallotEntryList, rnd);
		return newBallotEntryList;
	}
	
	/**
	 * Returns a new list.
	 */
	private static List<BallotEntry> addSequenceNumbers(final List<BallotEntry> ballotEntryList,
			final Integer noOfRounds) {
		List<BallotEntry> newBallotEntryList = new ArrayList<BallotEntry>();
		newBallotEntryList.addAll(ballotEntryList);

		Integer sequenceNo = new Integer(0);
		for(int i = 0; i < noOfRounds; i++) {
			for(BallotEntry be : newBallotEntryList) {
				List<DeviceSequence> qsList = be.getDeviceSequences();
				if(qsList.size() > i) {
					DeviceSequence qs = qsList.get(i);
					qs.setSequenceNo(++sequenceNo);
				}
			}
		}
		return newBallotEntryList;
	}
	
	private static Boolean membersNotPresentAtPositionXWithClarificationQuestions(Session session, DeviceType deviceType, Date answeringDate,
			BallotEntry ballotEntry, Integer noOfUniquePositions, String locale) throws ELSException {
		Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{locale.toString()});
		parametersMap.put("sessionId", new String[]{session.getId().toString()});
		parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
		parametersMap.put("membersAtPositionXId", new String[]{ballotEntry.getMember().getId().toString()});
		parametersMap.put("noOfUniquePositions", new String[]{noOfUniquePositions.toString()});
		parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});
		List counts = org.mkcl.els.domain.Query.findReport("QIS_MEMBER_POSITIONCOUNT_REGARDING_CLARIFICATION_IN_PREBALLOT", parametersMap);
		Long count = null;
		try {
			for(Object i: counts) {
				if(i!=null) {
					String counter =   i.toString();
					count = Long.parseLong(counter);
													
				}
			}
			if(count==0){
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			ELSException elsException = new ELSException();
			elsException.setParameter("StarredQuestionBallot_Boolean_membersNotPresentAtPositionXWithClarificationQuestions", "No member position found.");
			throw elsException;
		}
	}
	
	// TODO
	// private static Ballot createStarredCouncilBallot(final Ballot ballot) throws ELSException {
	private static Ballot createUH(final Ballot ballot) throws ELSException {
		return null;
	}
	
	private static boolean isActiveOnlyAsMember(final Member member,
			final Date onDate,
			final String locale) {
		String[] memberRoles = new String[] {"SPEAKER", "DEPUTY_SPEAKER", "CHAIRMAN", "DEPUTY_CHAIRMAN"};
		
		boolean isActiveMinister = member.isActiveMinisterOn(onDate, locale);
		boolean isActivePresidingOfficer = member.isActiveMemberInAnyOfGivenRolesOn(memberRoles, onDate, locale);
		boolean isActiveMember = member.isActiveMemberOn(onDate, locale);
		
		if(isActiveMember &&
				! isActiveMinister &&
				! isActivePresidingOfficer) {
			return true;
		}
		else {
			return false;
		}
	}

	public static List<StarredBallotVO> previewPreBallotVOs(final Session session,
			final DeviceType deviceType, 
			final Date answeringDate, 
			final String locale) throws ELSException {
		List<StarredBallotVO> previewPreBallotVOs = new ArrayList<StarredBallotVO>();
		Group group = Group.find(session, answeringDate, locale);
		Status internalStatus = Status.
				findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
		Status ballotStatus = Status.
				findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);
		List<Member> eligibleMembers = Ballot.getRepository().
				findMembersEligibleForBallot(session, deviceType, group, answeringDate, internalStatus, ballotStatus, locale);
		Integer noOfRounds = getNoOfRounds(group.getHouseType());
		List<Member> inactiveMembers = new ArrayList<Member>();
		for(Member m : eligibleMembers){
			if(isActiveOnlyAsMember(m,new Date(),locale)){
				List<Question> eligibleQuestions = 
						Ballot.getRepository().findQuestionsEligibleForBallot(m, session, deviceType, 
								group, answeringDate, internalStatus, ballotStatus, noOfRounds, locale);
				if(eligibleQuestions != null && !eligibleQuestions.isEmpty()){
					StarredBallotVO starredBallotVO = new StarredBallotVO();
					starredBallotVO.setMemberId(m.getId());
					starredBallotVO.setMemberName(m.getFullname());
					List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
					for(Question q : eligibleQuestions){
						QuestionSequenceVO questionSequenceVO = new QuestionSequenceVO();
						questionSequenceVO.setQuestionId(q.getId());						
						questionSequenceVO.setNumber(q.getNumber());
						questionSequenceVO.setFormattedNumber(FormaterUtil.
								formatNumberNoGrouping(questionSequenceVO.getNumber(), locale));
						questionSequenceVOs.add(questionSequenceVO);
					}
					starredBallotVO.setQuestionSequenceVOs(questionSequenceVOs);
					previewPreBallotVOs.add(starredBallotVO);
				}
			}else{
				inactiveMembers.add(m);
			}
		}
		
		House house = session.getHouse();
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		Date questionSubmissionStartDate = FormaterUtil.formatStringToDate(
				session.getParameter(
						ApplicationConstants.QUESTION_STARRED_SUBMISSION_STARTTIME), 
				datePattern.getValue(), locale); 
		Date sessionToDate = session.getEndDate();
		
		List<Member> pastMembersActiveWhileSubmission = 
			Member.findInactiveMembers(house, questionSubmissionStartDate, 
					sessionToDate, locale);
		inactiveMembers.addAll(pastMembersActiveWhileSubmission);
		List<Question> inactiveMemberQuestions = new ArrayList<Question>();
		for(Member m : inactiveMembers) {			
			List<Question> questions = new ArrayList<Question>();
			try {
				questions = Ballot.getRepository().findQuestionsEligibleForBallot(m, session, 
						deviceType, group, answeringDate, internalStatus, ballotStatus, 
						locale);
			} catch (ELSException e) {
				e.printStackTrace();
			}
			if(!questions.isEmpty()){
				inactiveMemberQuestions.addAll(questions);
			}
		}
				
		// Step 2a
		inactiveMemberQuestions = StarredQuestionBallot.reorderQuestions(
				inactiveMemberQuestions, answeringDate);
		
		// Step 3
		Date currentDate = new Date();
		for(Question q : inactiveMemberQuestions) {
			List<Member> supportingMembers = StarredQuestionBallot.eligibleSupportingMembers(q);
			// Step 4
			boolean isHandovered = false;
			for(Member m : supportingMembers) {
				boolean isActiveOnlyAsMember = isActiveOnlyAsMember(m, currentDate, locale);
				if(isActiveOnlyAsMember) {
					isHandovered = 
						StarredQuestionBallot.
						handoverQuestionToActiveMember(q, m, previewPreBallotVOs, noOfRounds, locale);
					if(isHandovered) {
						break;
					}
				}
			}
		}
		
		return previewPreBallotVOs;
	}

	private static boolean handoverQuestionToActiveMember(final Question q,
			final Member m,
			final List<StarredBallotVO> previewPreBallotVOs,
			final Integer noOfRounds,
			final String locale) {
		boolean isHandovered = false;
		StarredBallotVO starredBallotVO = null;
		for(StarredBallotVO sbVO : previewPreBallotVOs){
			if(sbVO.getMemberId().equals(m.getId())){
				starredBallotVO = sbVO;
			}
		}
		if(starredBallotVO == null) {
			StarredBallotVO newStarredBallotVO = new StarredBallotVO();
			newStarredBallotVO.setMemberId(m.getId());
			newStarredBallotVO.setMemberName(m.getFullname());
			List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
			QuestionSequenceVO questionSequenceVO = new QuestionSequenceVO();
			questionSequenceVO.setQuestionId(q.getId());						
			questionSequenceVO.setNumber(q.getNumber());
			questionSequenceVO.setFormattedNumber(FormaterUtil.
					formatNumberNoGrouping(questionSequenceVO.getNumber(), locale));
			questionSequenceVOs.add(questionSequenceVO);
			newStarredBallotVO.setQuestionSequenceVOs(questionSequenceVOs);
			previewPreBallotVOs.add(newStarredBallotVO);
			isHandovered = true;
		}
		else {
			List<QuestionSequenceVO> questionSequenceVOs = starredBallotVO.getQuestionSequenceVOs();
			if(questionSequenceVOs != null && !questionSequenceVOs.isEmpty()){
				int size = questionSequenceVOs.size();
				if(size < noOfRounds) {
					QuestionSequenceVO questionSequenceVO = new QuestionSequenceVO();
					questionSequenceVO.setQuestionId(q.getId());						
					questionSequenceVO.setNumber(q.getNumber());
					questionSequenceVO.setFormattedNumber(FormaterUtil.
							formatNumberNoGrouping(questionSequenceVO.getNumber(), locale));
					questionSequenceVOs.add(questionSequenceVO);
					isHandovered = true;
				}
			}
		}
		
		return isHandovered;
	}
}

