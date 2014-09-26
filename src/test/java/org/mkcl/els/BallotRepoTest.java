/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.BallotRepoTest.java
 * Created On: Jun 29, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BallotMemberVO;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.common.vo.ResolutionBallotVO;
import org.mkcl.els.common.vo.StarredBallotVO;
import org.mkcl.els.domain.Ballot;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.repository.DeviceTypeRepository;
import org.springframework.transaction.annotation.Transactional;

//TODO: Auto-generated Javadoc
/**
* The Class BallotRepoTest.
*
* @author vikasg
* @since v1.0.0
*/

public class BallotRepoTest extends AbstractTest {

	@Before
	public void setUp(){
		logger = Logger.getLogger(BallotRepoTest.class);
	}
	/*@Test 
	@Transactional
	public void testBallot() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testBallotSessionDeviceTypeDateDateString() {
		fail("Not yet implemented");
	}
*/
	@Test 
	@Transactional
	public void testFindStarredBallotVOs() {
		try {
			Session session = Session.findById(Session.class, (long)51);
			//DeviceType deviceType = DeviceType.findByType("resolutions_nonofficial", "mr_IN");
			List<StarredBallotVO> starredBallotVOs = Ballot.findStarredBallotVOs(session, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT) , "mr_IN");
			assertNotNull(Ballot.findStarredBallotVOs(session, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT) , "mr_IN"));
			if(starredBallotVOs != null){
				for(StarredBallotVO sbvo : starredBallotVOs){
					logger.info(sbvo.getMemberName());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test 
	@Transactional
	public void testFindStarredPreBallotVOs() {
		/*try {
			Session session = Session.findById(Session.class, (long)51);
			//DeviceType deviceType = DeviceType.findByType("resolutions_nonofficial", "mr_IN");
			for(StarredBallotVO sbvo : Ballot.findStarredPreBallotVOs(session, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT) , "mr_IN")){
				logger.info(sbvo.getMemberName());
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	@Test 
	@Transactional
	public void testFindPreBallotVO() {
		
		try {
			Session session = Session.findById(Session.class, (long)51);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			for(BallotVO bvo : Ballot.findPreBallotVO(session, deviceType, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT) , "mr_IN")){
				logger.info(bvo.getMemberName());
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test 
	@Transactional
	public void testFindResolutionCouncilPreBallotVO() {
		try {
			Session session = Session.findById(Session.class, (long)50);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(50));
			for(BallotVO bvo: Ballot.findResolutionCouncilPreBallotVO(session, deviceType, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT) , "mr_IN")){
				logger.info("Member Name: " + bvo.getMemberName());
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test 
	@Transactional
	public void testFindPreBallotMemberVO() {
		
		try {
			Session session = Session.findById(Session.class, (long)50);
			Group group = Group.findByNumberHouseTypeSessionTypeYear(new Integer(1), session.getHouse().getType(), session.getType(), session.getYear());
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			for(BallotMemberVO bmvo: Ballot.findPreBallotMemberVO(session, deviceType, group, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT) , "mr_IN")){
				logger.info("Member Name: " + bmvo.getMemberName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test 
	@Transactional
	public void testFindPreBallotMemberVOResolutionNonOfficial() {
		try {
			Session session = Session.findById(Session.class, (long)50);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(50));
			
			List<BallotMemberVO> bmvos = Ballot.findPreBallotMemberVOResolutionNonOfficial(session, deviceType, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT) , "mr_IN");
			
			assertNotNull(bmvos);
			//assertNull(bmvos);
			
			if(bmvos != null){
				for(BallotMemberVO bmvo: bmvos){
					logger.info("Member Name: " + bmvo.getMemberName());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test 
	@Transactional
	public void testFindBallotedVO() {
		try {
			Session session = Session.findById(Session.class, (long)50);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(50));
			List<BallotVO> ballotVOs = Ballot.findBallotedVO(session, deviceType, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT) , "mr_IN");
			
			assertNull(ballotVOs);
			
			if(ballotVOs != null){
				for(BallotVO bmvo: ballotVOs){
					logger.info("Member Name: " + bmvo.getMemberName());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test 
	@Transactional
	public void testFindBallotedMemberVO() {
		try {
			Session session = Session.findById(Session.class, (long)50);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(50));
			List<BallotMemberVO> memberBallotVOs = Ballot.findBallotedMemberVO(session, deviceType, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT) , "mr_IN");
			
			assertNull(memberBallotVOs);
			
			if(memberBallotVOs != null){		
				for(BallotMemberVO bmvo: memberBallotVOs){
					logger.info("Member Name: " + bmvo.getMemberName());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test 
	@Transactional
	public void testFindResolutionMemberSubjectBallotVO() {
		try {
			Session session = Session.findById(Session.class, (long)50);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(50));
			List<ResolutionBallotVO> rbvos = Ballot.findResolutionMemberSubjectBallotVO(session, deviceType, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT) , "mr_IN");
			
			assertNull(rbvos);
			//assertNotNull(rbvos);
			
			if(rbvos != null){
				for(ResolutionBallotVO bmvo: rbvos){
					logger.info("Member Name: " + bmvo.getMemberName());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test 
	@Transactional
	public void testCreateResolutionPatrakBhagTwo() {
		try {
			Session session = Session.findById(Session.class, (long)50);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(50));
			
			List<ResolutionBallotVO> rbvos = Ballot.createResolutionPatrakBhagTwo(session, deviceType, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT) , "mr_IN");
			
			assertNull(rbvos);
			//assertNotNull(rbvos);
			
			if(rbvos != null){
				for(ResolutionBallotVO bmvo: rbvos){
					logger.info("Member Name: " + bmvo.getMemberName());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*@Test 
	@Transactional
	public void testCreateStarred() {
		Session sessionAssembly = Session.findById(Session.class, (long)50);
		Session sessionCouncil = Session.findById(Session.class, (long)51);
		
		DeviceType deviceTypeStarred = DeviceType.findById(DeviceType.class, new Long(4));
		//==========================
		Ballot ballotStarredAssembly = new Ballot(sessionAssembly, deviceTypeStarred, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT), FormaterUtil.formatStringToDate("2013-07-24", ApplicationConstants.DB_DATEFORMAT) , "mr_IN");
		Ballot ballotStarredCouncil = new Ballot(sessionCouncil, deviceTypeStarred, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT), FormaterUtil.formatStringToDate("2013-07-24", ApplicationConstants.DB_DATEFORMAT) , "mr_IN");
		
		assertNotNull(ballotStarredAssembly.create());
		assertNotNull(ballotStarredCouncil.create());
	}
	
	@Test
	@Transactional
	public void testCreateHDQ(){
			Session sessionAssembly = Session.findById(Session.class, (long)50);
			Session sessionCouncil = Session.findById(Session.class, (long)51);
			
			DeviceType deviceTypeHDQ = DeviceType.findById(DeviceType.class, new Long(4));
			
			Ballot ballotHDQAssembly = new Ballot(sessionAssembly, deviceTypeHDQ, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT), FormaterUtil.formatStringToDate("2013-07-24", ApplicationConstants.DB_DATEFORMAT) , "mr_IN");
			Ballot ballotHDQCouncil = new Ballot(sessionCouncil, deviceTypeHDQ, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT), FormaterUtil.formatStringToDate("2013-07-24", ApplicationConstants.DB_DATEFORMAT) , "mr_IN");
			
			assertNotNull(ballotHDQAssembly.create());
			assertNotNull(ballotHDQCouncil.create());
	}
	
	@Test
	@Transactional
	public void testCreateHDS(){
		Session sessionAssembly = Session.findById(Session.class, (long)50);
		Session sessionCouncil = Session.findById(Session.class, (long)51);
		
		DeviceType deviceTypeHDS = DeviceType.findById(DeviceType.class, new Long(4));
		
		Ballot ballotHDSAssembly = new Ballot(sessionAssembly, deviceTypeHDS, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT), FormaterUtil.formatStringToDate("2013-07-24", ApplicationConstants.DB_DATEFORMAT) , "mr_IN");
		Ballot ballotHDSCouncil = new Ballot(sessionCouncil, deviceTypeHDS, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT), FormaterUtil.formatStringToDate("2013-07-24", ApplicationConstants.DB_DATEFORMAT) , "mr_IN");
		
		assertNotNull(ballotHDSAssembly.create());
		assertNotNull(ballotHDSCouncil.create());
	}*/
	

	/*
	@Test 
	@Transactional
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testFind() {
	
	}
	*/
	
	@Test 
	@Transactional
	public void testFindBallotedQuestions() {
		try {
			Session session = Session.findById(Session.class, (long)50);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Member member = Member.findById(Member.class, new Long(296));
			for(Question q : Ballot.findBallotedQuestions(member, session, deviceType, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT) , "mr_IN")){
				logger.info("Member Name: " + q.getPrimaryMember().getFullnameLastNameFirst());
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*@Test 
	@Transactional
	public void testCreateStarredCouncilBallot() {
		
	}*/

	/*@Test 
	@Transactional
	public void testCreateHalfHourAssemblyBallot() {
		
	}

	@Test 
	@Transactional
	public void testCreateHalfHourAssemblyBallotStandAlone() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testCreateResolutionNonOfficialAssemblyBallot() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testCreateMemberBallot() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testCreateMemberBallotStandAlone() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testCreateBallotResolutionNonOfficial() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testCreateBallotHDS() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testFindHDSBallotVO() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testFindHDSCouncilPreBallotVO() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testCreateMemberBallotHDS() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testCreateHalfHourCouncilBallot() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testCreateHDSCouncilBallot() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testCreateCouncilBallotResolutionNonOfficial() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testCreateNoticeBallot() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testCreateMemberBallotResolutionNonOfficial() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testCreateBallot() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testViewBallot() {
		fail("Not yet implemented");
	}

	@Test 
	@Transactional
	public void testUpdateBallotQuestions() {
		fail("Not yet implemented");
	}*/
}
