package org.mkcl.els;
import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mkcl.els.AbstractTest;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.Session;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class MemberBallotAttendanceRepoTest extends AbstractTest {

	@Before
	public void setUp() throws Exception {
		logger = Logger.getLogger(MemberBallotAttendanceRepoTest.class);
	}

	@Test
	@Transactional
	public void testFindAllSessionDeviceTypeStringIntegerStringString() {
		try{
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(
				4));

		List<MemberBallotAttendance> mba = MemberBallotAttendance.findAll(
				session, deviceType, "true", 2, "member", "mr_IN");

			assertNotNull(mba);
			Assert.notEmpty(mba);
			assertTrue(mba.size() == 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindMembersByAttendanceSessionDeviceTypeBooleanIntegerString() {
		try {
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(
					4));
			List<Member> members = MemberBallotAttendance.findMembersByAttendance(
					session, deviceType, true, "mr_IN");

		
			assertNotNull(members);
			Assert.notEmpty(members);
			assertTrue(members.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindEligibleMembers() {
		try {
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(
					4));
			List<Member> members = MemberBallotAttendance.findEligibleMembers(
				session, deviceType, "mr_IN");

		
			assertNotNull(members);
			Assert.notEmpty(members);
			assertTrue(members.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testCreateMemberBallotAttendance() {
		Session session = Session.findById(Session.class, new Long(51));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
		String mb = MemberBallotAttendance.createMemberBallotAttendance(
				session, deviceType, 1, "me", "us", "mr_IN");

		try {
			assertNotNull(mb);
			assertTrue(!mb.isEmpty());
			assertTrue(mb.length() > 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testMemberBallotCreated() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Boolean mbc = MemberBallotAttendance.memberBallotCreated(session, deviceType, 1, "mr_IN");

			assertNotNull(mbc);
			assertTrue(mbc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	@Test
	@Transactional
	public void testAreMembersLocked() {

		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Boolean mbl = MemberBallotAttendance.areMembersLocked(session, deviceType, 1, true, "mr_IN");

		
			assertNotNull(mbl);
			//assertTrue(mbl);
			assertFalse(mbl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindMembersByAttendanceCount() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Integer mbac = MemberBallotAttendance.findMembersByAttendanceCount(session, deviceType, true, 1, "mr_IN");
			
			assertNotNull(mbac);
			assertTrue(mbac > 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindMembersByAttendanceSessionDeviceTypeBooleanIntStringIntInt() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			List<Member> membs = MemberBallotAttendance.findMembersByAttendance(session, deviceType, true, 1, "mr_IN", 1, 10);
			
		
			assertNotNull(membs);
			//assertNull(membs);
			//assertTrue(membs.isEmpty());
			assertFalse(membs.isEmpty());
			assertTrue(!membs.isEmpty());
			//assertFalse(!membs.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindNewMembers() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			List<Member> membs = MemberBallotAttendance.findNewMembers(session, deviceType, true, 1, "mr_IN");
		
		
			assertNotNull(membs);
			//assertTrue(membs.isEmpty());
			assertFalse(membs.isEmpty());
			assertTrue(!membs.isEmpty());
			//assertFalse(!membs.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindOldMembers() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			List<Member> membs = MemberBallotAttendance.findOldMembers(session, deviceType, true, 1, "mr_IN");
		
		
			assertNotNull(membs);
			assertTrue(membs.isEmpty());
			//assertFalse(membs.isEmpty());
			//assertTrue(!membs.isEmpty());
			assertFalse(!membs.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testCreateAttendance() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			String acreated = MemberBallotAttendance.createAttendance(session, deviceType, "mr_IN");
		
		
			assertNotNull(acreated);
			//assertTrue(acreated.isEmpty());
			assertFalse(acreated.isEmpty());
			assertTrue(!acreated.isEmpty());
			//assertFalse(!acreated.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindAllSessionDeviceTypeStringStringString() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			List<MemberBallotAttendance> mba = MemberBallotAttendance.findAll(session, deviceType, "true", "member", "mr_IN");
		
			assertNotNull(mba);
			//assertTrue(mba.isEmpty());
			assertFalse(mba.isEmpty());
			assertTrue(!mba.isEmpty());
			//assertFalse(!mba.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindMembersByAttendanceSessionDeviceTypeBooleanString() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			List<Member> mbs = MemberBallotAttendance.findMembersByAttendance(session, deviceType, true, "mr_IN");
		
			assertNotNull(mbs);
			//assertTrue(mbs.isEmpty());
			assertFalse(mbs.isEmpty());
			assertTrue(!mbs.isEmpty());
			//assertFalse(!mbs.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testCheckPositionForNullValues() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Integer position = MemberBallotAttendance.checkPositionForNullValues(session, deviceType, "true", 1, "member", "mr_IN");
		
			assertNotNull(position);
			assertTrue(position == 0);
			assertFalse(position == 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Test
	@Transactional
	public void testUpdatePositionAbsentMembers() {
		
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			String position = MemberBallotAttendance.updatePositionAbsentMembers(session, deviceType,  1, true,  "mr_IN");
		
			assertNotNull(position);
			//assertTrue(position.isEmpty());
			assertFalse(position.isEmpty());
			assertTrue(!position.isEmpty());
			//assertFalse(!position.isEmpty());
		} catch(IllegalArgumentException iae){
			iae.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testCheckPositionDiscontinous() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Boolean position = MemberBallotAttendance.checkPositionDiscontinous(session, deviceType, true, 1,  "mr_IN");
		
		
			assertNotNull(position);
			//assertTrue(position);
			assertFalse(position);
			assertTrue(!position);
			//assertFalse(!position);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
