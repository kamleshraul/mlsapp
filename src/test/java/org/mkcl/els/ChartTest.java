package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mkcl.els.AbstractTest;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.domain.Chart;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.springframework.transaction.annotation.Transactional;


public class ChartTest extends AbstractTest{

	@Before
	public void setUp(){
		logger = Logger.getLogger(ChartTest.class);
	}
	
	@Test @Transactional
	public void testChart() {
		/*Session session = Session.findById(Session.class, new Long(50));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(6));
		Chart chart = new Chart(session, deviceType, "mr_IN");
		assertNotNull(chart.create());*/
		//javax.persistence.PersistenceException: 
		//org.hibernate.exception.SQLGrammarException: could not execute native bulk manipulation query
	}

	@Test @Transactional
	public void testChartSessionGroupDateDeviceTypeString() {
		/*Session session = Session.findById(Session.class, new Long(50));
		Group group = Group.findById(Group.class, new Long(151));
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(6));
		Chart chart = new Chart(session, group, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT), deviceType, "mr_IN");
		assertNotNull(chart.create());*/
		
		//javax.persistence.PersistenceException: 
		//org.hibernate.exception.SQLGrammarException: could not execute native bulk manipulation query
	}

	@Test @Transactional
	public void testChartSessionDeviceTypeString() {
		//fail("Not yet implemented");
	}

	@Test @Transactional
	public void testGetChartVOsSessionGroupDateDeviceTypeString() {
		try {
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Session session = Session.findById(Session.class, new Long(50));
			Group group = Group.findById(Group.class, new Long(151));
			List<ChartVO> chartVOSs = Chart.getChartVOs(session, group, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT), deviceType, "mr_IN");
			assertNotNull(chartVOSs);
			logger = Logger.getLogger(ChartTest.class);
			if(chartVOSs != null){
				for(ChartVO cvo : chartVOSs){
					logger.info("testGetChartVOsSessionGroupDateDeviceTypeString: " + cvo.getMemberName() + ":" + cvo.getRejectedNotices());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test @Transactional
	public void testGetChartVOsSessionDeviceTypeString() {
		
		try {
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(50));
			Session session = Session.findById(Session.class, new Long(50));
			List<ChartVO> chartVOSs = Chart.getChartVOs(session, deviceType, "mr_IN");
			assertNotNull(chartVOSs);
			logger = Logger.getLogger(ChartTest.class);
			if(chartVOSs != null){
				for(ChartVO cvo : chartVOSs){
					logger.error("testGetChartVOsSessionDeviceTypeString: " + cvo.getMemberName() + ":" + cvo.getRejectedNotices());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test @Transactional
	public void testGetAdmittedChartVOs() {
		try {
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(50));
			Session session = Session.findById(Session.class, new Long(50));
			List<ChartVO> acvo = Chart.getAdmittedChartVOs(session, deviceType, "mr_IN");
			assertNotNull(acvo);
			if(acvo != null){
				for(ChartVO vo: acvo){
					logger.info(vo.getMemberName() + ":" + vo.getRejectedNotices());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test @Transactional
	public void testCreate() {
		//fail("Not yet implemented");
	}

	@Test @Transactional
	public void testAddToChart() {
	}

	@Test
	@Transactional
	public void testGroupChange() {
		Question q = Question.findById(Question.class, new Long(7601));
		logger.info("Pre group: " + q.getGroup().getNumber());
		//Chart.groupChange(q, getGroup());
		//logger.info("Post group: " + q.getGroup().getNumber());
	}
	
	@Test @Transactional
	public void testFindDevice() {
		try {
			Chart chart = Chart.findById(Chart.class, new Long(2400));
			
			assertNotNull(Chart.findDevices(chart));
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*@Test @Transactional
	public void testFindChart() {
		Question q = Question.findById(Question.class, new Long(7601));
		Chart chart = Chart.find(q);
		assertNotNull(chart);
	}*/

	@Test @Transactional
	public void testFindLatestQuestionChart() {
		try {
			Session session = Session.findById(Session.class, new Long(50));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Group group = Group.findById(Group.class, new Long(151));
			Chart chart = Chart.findLatestQuestionChart(session, group, deviceType, "mr_IN");
			assertNotNull(chart);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test @Transactional
	public void testFindDevicesChart() {
		try {
			Chart chart = Chart.findById(Chart.class, new Long(2400));
			List<Device> devices = Chart.findDevices(chart);
			assertNotNull(devices);
			if(devices != null){
				for(Device d: devices){
					logger.info("testFindDevicesChart: " + d.getId());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test @Transactional
	public void testFindDevicesMemberChart() {
		try {
			Member member = Member.findById(Member.class, new Long(147));
			Chart chart = Chart.findById(Chart.class, new Long(2400));
			assertNotNull(Chart.findDevices(member, chart));
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test @Transactional
	public void testFindQuestionsSessionGroupDateDeviceTypeString() {
		try {
			Session session = Session.findById(Session.class, new Long(50));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Group group = Group.findById(Group.class, new Long(151));
			List<Question> qs = Chart.findQuestions(session, group, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT), deviceType, "mr_IN");
			assertNotNull(qs);
			for(Question q: qs){
				logger.info("All: " + q.getPrimaryMember().getFullnameLastNameFirst());
				
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test @Transactional
	public void testFindQuestionsMemberSessionGroupDateDeviceTypeString() {
		try {
			Member member = Member.findById(Member.class, new Long(147));
			Session session = Session.findById(Session.class, new Long(50));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(4));
			Group group = Group.findById(Group.class, new Long(151));
			List<Question> qs = Chart.findQuestions(member,session, group, FormaterUtil.formatStringToDate("2013-07-17", ApplicationConstants.DB_DATEFORMAT), deviceType, "mr_IN");
			assertNotNull(qs);
			for(Question q: qs){
				logger.info("Member: " + q.getPrimaryMember().getFullnameLastNameFirst());
				
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test @Transactional
	public void testFindResolutionsSessionDeviceTypeString() {
		try {
			Member member = Member.findById(Member.class, new Long(147));
			Session session = Session.findById(Session.class, new Long(50));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(50));
			Group group = Group.findById(Group.class, new Long(151));
			List<Resolution> rs = Chart.findResolutions(session, deviceType, "mr_IN");
			assertNotNull(rs);
			for(Resolution r: rs){
				logger.info("Resolutions: " + r.getMember().getFullnameLastNameFirst());
				
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test @Transactional
	public void testFindResolutionsMemberSessionDeviceTypeString() {
		try {
			Member member = Member.findById(Member.class, new Long(147));
			Session session = Session.findById(Session.class, new Long(50));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(50));
			List<Resolution> rs = Chart.findResolutions(member, session, deviceType, "mr_IN");
			assertNotNull(rs);
			for(Resolution r: rs){
				logger.info("Resolutions Member: " + r.getMember().getFullnameLastNameFirst());
				
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test @Transactional
	public void testFindMembers() {
		Question q = Question.findById(Question.class, new Long(7782));
		/*Chart chartQ = Chart.find(q);
		List<Member> ms = Chart.findMembers(chartQ);
		assertNotNull(ms);
		
		ms = null;
		Resolution r = Resolution.findById(Resolution.class, new Long(7648));
		Chart chartR = Chart.find(r);
		ms = Chart.findMembers(chartR);
		assertNotNull(ms);*/
	}

}
