package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.MemberBallotFinalBallotVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseReportVO;
import org.mkcl.els.common.vo.MemberBallotQuestionDistributionVO;
import org.mkcl.els.common.vo.MemberBallotVO;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallot;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.springframework.transaction.annotation.Transactional;

public class MemberBallotRepoTest extends AbstractTest {

	@Before
	public void setUp() throws Exception {
		logger = Logger.getLogger(getClass());
	}

	@Test
	@Transactional
	public void testCreateMemberBallot() {
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		String mcreated = MemberBallot.createMemberBallot(session, deviceType, true, 2, "", "", "mr_IN", new Integer(session.getParameter(ApplicationConstants.QUESTIONS_STARRED_TOTALROUNDS_MEMBERBALLOT)));
		try{
			assertNotNull(mcreated);
			//assertNull(mcreated);
			
			//assertTrue(mcreated.isEmpty());
			assertFalse(mcreated.isEmpty());
			
			assertTrue(!mcreated.isEmpty());
			//assertFalse(!mcreated.isEmpty());
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindByMember() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Member member = Member.findById(Member.class, new Long(147));
			List<MemberBallot> memBallot = MemberBallot.findByMember(session, deviceType, member, "mr_IN");
		
			assertNotNull(memBallot);
			//assertNull(mcreated);
			
			assertTrue(memBallot.isEmpty());
			//assertFalse(memBallot.isEmpty());
			
			//assertTrue(!memBallot.isEmpty());
			assertFalse(!memBallot.isEmpty());
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindByMemberRound() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Member member = Member.findById(Member.class, new Long(147));
			MemberBallot memBallot = MemberBallot.findByMemberRound(session, deviceType, member, 1, "mr_IN");
			//assertNotNull(memBallot);
			assertNull(memBallot);		
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testViewMemberBallotVOSessionDeviceTypeBooleanIntString() {
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		Member member = Member.findById(Member.class, new Long(147));
		 List<MemberBallotVO> memBallotVO = MemberBallot.viewMemberBallotVO(session, deviceType, true,  1, "mr_IN");
		try{
			assertNotNull(memBallotVO);
			//assertNull(mcreated);
			
			//assertTrue(mcreated.isEmpty());
			assertFalse(memBallotVO.isEmpty());
			
			assertTrue(!memBallotVO.isEmpty());
			//assertFalse(!mcreated.isEmpty());
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testViewMemberBallotVOSessionDeviceTypeBooleanIntGroupString() {
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		Member member = Member.findById(Member.class, new Long(147));
		Group group = Group.findById(Group.class, new Long(151));
		 List<MemberBallotVO> memBallotVO = MemberBallot.viewMemberBallotVO(session, deviceType, true,  1, group, "mr_IN");
		try{
			assertNotNull(memBallotVO);
			//assertNull(mcreated);
			
			//assertTrue(mcreated.isEmpty());
			assertFalse(memBallotVO.isEmpty());
			
			assertTrue(!memBallotVO.isEmpty());
			//assertFalse(!mcreated.isEmpty());
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testViewMemberBallotVOSessionDeviceTypeBooleanIntGroupQuestionDatesString() {
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		Member member = Member.findById(Member.class, new Long(147));
		Group group = Group.findById(Group.class, new Long(151));
		QuestionDates qds = group.getQuestionDates().get(0);
		
		List<MemberBallotVO> memBallotVO = MemberBallot.viewMemberBallotVO(session, deviceType, true,  1, group, qds, "mr_IN");
		try{
			assertNotNull(memBallotVO);
			//assertNull(mcreated);
			
			//assertTrue(mcreated.isEmpty());
			assertFalse(memBallotVO.isEmpty());
			
			assertTrue(!memBallotVO.isEmpty());
			//assertFalse(!mcreated.isEmpty());
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testGetMemberBallotVOs() {
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		Member member = Member.findById(Member.class, new Long(147));
		Group group = Group.findById(Group.class, new Long(151));
		 List<MemberBallotVO> memBallotVO = MemberBallot.getMemberBallotVOs(session.getId(), deviceType.getId(), true, 1, group.getId(), new Long(256), "mr_IN");
		try{
			assertNotNull(memBallotVO);
			//assertNull(mcreated);
			
			//assertTrue(mcreated.isEmpty());
			assertFalse(memBallotVO.isEmpty());
			
			assertTrue(!memBallotVO.isEmpty());
			//assertFalse(!mcreated.isEmpty());
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindPrimaryCount() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Member member = Member.findById(Member.class, new Long(147));
			Group group = Group.findById(Group.class, new Long(151));
			Integer count = MemberBallot.findPrimaryCount(session, deviceType, "mr_IN");
		
			assertNotNull(count);
			//assertNull(mcreated);
			
			assertTrue(count == 0);
			//assertFalse(count == 0);
			
			//assertTrue(count > 0);
			assertFalse(count > 0);
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testUpdateClubbing() {
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		Member member = Member.findById(Member.class, new Long(147));
		Group group = Group.findById(Group.class, new Long(151));
		Boolean updated = MemberBallot.updateClubbing(session, deviceType, 1, 2, "mr_IN");
		try{
			assertNotNull(updated);
			//assertNull(updated);
			
			assertTrue(updated);
			//assertFalse(updated);
			
			//assertTrue(!updated);
			assertFalse(!updated);
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testDeleteTempEntries() {
	/*	Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		Member member = Member.findById(Member.class, new Long(147));
		Group group = Group.findById(Group.class, new Long(151));*/
		Boolean deleted = MemberBallot.deleteTempEntries();
		try{
			assertNotNull(deleted);
			//assertNull(deleted);
			
			assertTrue(deleted);
			//assertFalse(deleted);
			
			//assertTrue(!deleted);
			assertFalse(!deleted);
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testCreateFinalBallot() {
//		try{
//			Session session = Session.findById(Session.class, new Long(51));
//			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
//			Member member = Member.findById(Member.class, new Long(147));
//			Group group = Group.findById(Group.class, new Long(151));
//			Boolean finalBallotCreated = MemberBallot.createFinalBallot(session, deviceType, group, "2013-07-17", "mr_IN", "2013-06-20", 5);
//		
//			assertNotNull(finalBallotCreated);
//			//assertNull(finalBallotCreated);
//			
//			assertTrue(finalBallotCreated);
//			//assertFalse(finalBallotCreated);
//			
//			//assertTrue(!finalBallotCreated);
//			assertFalse(!finalBallotCreated);
//			
//			
//		}catch (Exception e) {
//			
//			e.printStackTrace();
//		}
	}

	@Test
	@Transactional
	public void testViewFinalBallotSessionDeviceTypeStringString() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Member member = Member.findById(Member.class, new Long(147));
			Group group = Group.findById(Group.class, new Long(151));
			List<MemberBallotFinalBallotVO> vo = MemberBallot.viewFinalBallot(session, deviceType, "2013-07-17", "mr_IN");
		
			assertNotNull(vo);
			//assertNull(vo);
			
			assertTrue(vo.isEmpty());
			//assertFalse(vo.isEmpty());
			
			//assertTrue(!vo.isEmpty());
			assertFalse(!vo.isEmpty());
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}	
	
	@Test
	@Transactional
	public void testFindMemberWiseReportVO() {
		try{	
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Member member = Member.findById(Member.class, new Long(147));
			Group group = Group.findById(Group.class, new Long(151));
			MemberBallotMemberWiseReportVO vo = MemberBallot.findMemberWiseReportVO(session, deviceType, member,"mr_IN");
		
			assertNotNull(vo);
			//assertNull(vo);
			
			/*assertTrue(vo.isEmpty());
			assertFalse(vo.isEmpty());
			
			assertTrue(!vo.isEmpty());
			assertFalse(!vo.isEmpty());*/
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testViewQuestionDistribution() {
		try{	
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Member member = Member.findById(Member.class, new Long(147));
			Group group = Group.findById(Group.class, new Long(151));
			List<MemberBallotQuestionDistributionVO> vos = MemberBallot.viewQuestionDistribution(session, deviceType,"mr_IN");
		
			assertNotNull(vos);
			//assertNull(vos);
			
			//assertTrue(vos.isEmpty());
			assertFalse(vos.isEmpty());
			
			assertTrue(!vos.isEmpty());
			//assertFalse(!vos.isEmpty());
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindMembersByPosition() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Member member = Member.findById(Member.class, new Long(147));
			Group group = Group.findById(Group.class, new Long(151));
			List<Member> mems = MemberBallot.findMembersByPosition(session, deviceType, true, 1, "mr_IN", 1, 5);
		
			assertNotNull(mems);
			//assertNull(mems);
			
			//assertTrue(mems.isEmpty());
			assertFalse(mems.isEmpty());
			
			assertTrue(!mems.isEmpty());
			//assertFalse(!mems.isEmpty());
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindEntryCount() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Member member = Member.findById(Member.class, new Long(147));
			Group group = Group.findById(Group.class, new Long(151));
			Integer count = MemberBallot.findEntryCount(session, deviceType, 1, true,  "mr_IN");
		
			assertNotNull(count);
			//assertNull(count);
			
			//assertTrue(count==0);
			assertFalse(count==0);
			
			assertTrue(count>0);
			assertFalse(!(count>0));
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}
