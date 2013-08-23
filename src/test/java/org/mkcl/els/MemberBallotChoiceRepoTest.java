package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallotChoice;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.springframework.transaction.annotation.Transactional;

public class MemberBallotChoiceRepoTest extends AbstractTest {

	@Before
	public void setUp() throws Exception {
		logger = Logger.getLogger(getClass());
	}

	@Test
	@Transactional
	public void testFindByMember() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Member member = Member.findById(Member.class, new Long(147));
			
			List<MemberBallotChoice> mbc = MemberBallotChoice.findByMember(session, deviceType, member, "mr_IN");
		
			assertNotNull(mbc);
			//assertNull(mbc);
			
			assertTrue(mbc.isEmpty());
			//assertFalse(mbc.isEmpty());
			
			//assertTrue(!mbc.isEmpty());
			assertFalse(!mbc.isEmpty());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindFirstBatchQuestions() {
		try{
			Session session = Session.findById(Session.class, new Long(51));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Member member = Member.findById(Member.class, new Long(147));
			
			List<Question> mbqc = MemberBallotChoice.findFirstBatchQuestions(session, deviceType, member, "", "member", "ASC", "mr_IN");
		
		
			assertNotNull(mbqc);
			assertNull(mbqc);
			
			assertTrue(mbqc.isEmpty());
			assertFalse(mbqc.isEmpty());
			
			assertTrue(!mbqc.isEmpty());
			assertFalse(!mbqc.isEmpty());
		}catch(Throwable e){
			e.printStackTrace();
		}
	}

}
