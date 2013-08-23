package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.QuestionDatesVO;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class GroupRepoTest extends AbstractTest {

	@Before
	public void setUp() {
		logger = Logger.getLogger(GroupRepoTest.class);
	}

	@Test
	@Transactional
	public void testFindByHouseTypeSessionTypeYear() {
		try {
			HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
			SessionType sessionType = SessionType.findByFieldName(
					SessionType.class, "type", "monsoon", "mr_IN");
			List<Group> groups = Group.findByHouseTypeSessionTypeYear(houseType,
					sessionType, new Integer(2013));

			assertNotNull(groups);
			Assert.notEmpty(groups);

			if (groups != null) {
				for (Group g : groups) {
					logger.info(g.getNumber() + ":" + g.getYear());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindAnsweringDates() {
		List<String> ansDates = null;
		try {
			ansDates = Group.getGroupRepository().findAnsweringDates(new Long(168), "mr_IN");
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertNotNull(ansDates);
		Assert.isTrue(ansDates.size() == 0);

		if (ansDates != null) {
			for (String d : ansDates) {
				logger.info("testFindAnsweringDates: " + d);
			}
		}
	}

	@Test
	@Transactional
	public void testFindByNumberHouseTypeSessionTypeYear() {
		try {
			HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
			SessionType sessionType = SessionType.findByFieldName(
					SessionType.class, "type", "monsoon", "mr_IN");
			Group group = Group.findByNumberHouseTypeSessionTypeYear(
					new Integer(3), houseType, sessionType, new Integer(2013));

			assertNotNull(group);

			if (group != null) {
				for (Ministry m : group.getMinistries()) {
					logger.info("testFindByNumberHouseTypeSessionTypeYear: "
							+ m.getName());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	@Transactional
	public void testFindMinistryHouseTypeIntegerSessionTypeString() {
		try {
			Ministry ministry = Ministry.findByName(Ministry.class, "गृह मंत्री",
					"mr_IN");
			HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
			SessionType sessionType = SessionType.findByFieldName(
					SessionType.class, "type", "monsoon", "mr_IN");
			Group group = Group.find(ministry, houseType, new Integer(2013),
					sessionType, "mr_IN");

			assertNotNull(group);

			if (group != null) {
				for (Ministry m : group.getMinistries()) {
					logger.info("testFindMinistryHouseTypeIntegerSessionTypeString: "
							+ m.getName());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindAllGroupDatesFormatted() {
		HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
		SessionType sessionType = SessionType.findByFieldName(
				SessionType.class, "type", "monsoon", "mr_IN");
		List<QuestionDatesVO> qds = Group.findAllGroupDatesFormatted(houseType,
				sessionType, new Integer(2013), "mr_IN");

		assertNotNull(qds);
		Assert.notEmpty(qds);

		if (qds != null) {
			for (QuestionDatesVO qdvo : qds) {
				logger.info("testFindAllGroupDatesFormatted: "
						+ qdvo.getAnsweringDate() + ": group:"
						+ qdvo.getGroupId());
			}
		}
	}

	@Test
	@Transactional
	public void testFindQuestionDateByGroup() {
		try {
			HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
			SessionType sessionType = SessionType.findByFieldName(
					SessionType.class, "type", "monsoon", "mr_IN");
			List<MasterVO> qdmvos = Group.findQuestionDateByGroup(houseType,
					sessionType, new Integer(2013), new Integer(3), "mr_IN");

			Assert.notNull(qdmvos);
			Assert.notEmpty(qdmvos);
			Assert.isTrue(qdmvos.size() > 0);

			if (qdmvos != null) {
				for (MasterVO mvo : qdmvos) {
					logger.info("testFindQuestionDateByGroup: " + mvo.getId() + ":"
							+ mvo.getName());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	@Transactional
	public void testFindMinistriesByName() {
		try {
			List<Ministry> ministries = Group.findMinistriesByName(new Long(151));

			Assert.notNull(ministries);
			Assert.notEmpty(ministries);
			Assert.isTrue(ministries.size() > 0);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindSessionDateString() {
		try {
			Session session = Session.findById(Session.class, new Long(50));
			Group group = Group.find(session, FormaterUtil.formatStringToDate(
					"2013-07-17", ApplicationConstants.DB_DATEFORMAT), "mr_IN");
			assertNotNull(group);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindMinistriesByPriorityLong() {
		try {
			List<Ministry> ministries = Group
					.findMinistriesByPriority(new Long(151));

			assertNotNull(ministries);
			Assert.notEmpty(ministries);
			Assert.isTrue(ministries.size() > 0);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	@Transactional
	public void testFindMinistriesByPriorityGroup() {
		try {
			Group group = Group.findById(Group.class, new Long(152));
			List<Ministry> ministries = Group.findMinistriesByPriority(group);

			assertNotNull(ministries);
			Assert.notEmpty(ministries);
			Assert.isTrue(ministries.size() > 0);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindMinistriesInGroupsForSessionExcludingGivenGroup() {
		try {
			HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
			SessionType sessionType = SessionType.findByFieldName(
					SessionType.class, "type", "monsoon", "mr_IN");
			List<Ministry> ministries = Group
					.findMinistriesInGroupsForSessionExcludingGivenGroup(houseType,
							sessionType, new Integer(2013), new Integer(3), "mr_IN");

			assertNotNull(ministries);
			Assert.notEmpty(ministries);
			Assert.isTrue(ministries.size() > 0);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindMinistriesInGroupsForSession() {
		try {
			HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
			SessionType sessionType = SessionType.findByFieldName(
					SessionType.class, "type", "monsoon", "mr_IN");
			List<Ministry> ministries = Group.findMinistriesInGroupsForSession(
					houseType, sessionType, new Integer(2013), "mr_IN");

			assertNotNull(ministries);
			Assert.notEmpty(ministries);
			Assert.isTrue(ministries.size() > 0);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindGroupNumbersForSessionExcludingGivenGroup() {
		try {
			HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
			SessionType sessionType = SessionType.findByFieldName(
					SessionType.class, "type", "monsoon", "mr_IN");
			List<Integer> groups = Group
					.findGroupNumbersForSessionExcludingGivenGroup(houseType,
							sessionType, new Integer(2013), 4, "mr_IN");

			assertNotNull(groups);
			Assert.notEmpty(groups);
			Assert.isTrue(groups.size() > 0);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindGroupNumbersForSession() {
		try {
			HouseType houseType = HouseType.findById(HouseType.class, new Long(4));
			SessionType sessionType = SessionType.findByFieldName(
					SessionType.class, "type", "monsoon", "mr_IN");
			List<Integer> groups = Group.findGroupNumbersForSession(houseType,
					sessionType, new Integer(2013), "mr_IN");

			assertNotNull(groups);
			Assert.notEmpty(groups);
			Assert.isTrue(groups.size() > 0);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
