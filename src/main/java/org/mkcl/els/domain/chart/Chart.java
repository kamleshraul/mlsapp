package org.mkcl.els.domain.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.repository.ChartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Chart.
 * 
 * @author amitd
 * @author anandk
 * @author vikasg
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="charts")
public class Chart extends BaseDomain implements Serializable {
	
	private static final long serialVersionUID = 292896040851837645L;
	
	//===============================================
	//
	//=============== ATTRIBUTES ====================
	//
	//===============================================
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="group_id")
	private Group group;
	
	@Temporal(TemporalType.DATE)
	private Date answeringDate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private DeviceType deviceType;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="charts_chart_entries",
			joinColumns={ @JoinColumn(name="chart_id", 
					referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="chart_entry_id", 
					referencedColumnName="id") })
	private List<ChartEntry> chartEntries;
	
	@Autowired
	private transient ChartRepository repository;
	
	
	//===============================================
	//
	//=============== CONSTRUCTORS ==================
	//
	//===============================================
	/**
	 * Do not use this constructor to create Chart instances.
	 * This constructor is kept here because JPA needs an 
	 * Entity to have a default public Constructor.
	 */
	public Chart() {
		super();
	}

	/**
	 * Use this constructor to construct a new Starred Question Chart 
	 * instance.
	 */
	public Chart(final Session session,
			final Group group,
			final Date answeringDate,
			final DeviceType deviceType,
			final String locale) {
		super(locale);
		this.setSession(session);
		this.setGroup(group);
		this.setDeviceType(deviceType);
		this.setAnsweringDate(answeringDate);
		this.setChartEntries(new ArrayList<ChartEntry>());
	}
	
	/**
	 * Use this constructor to construct a new Half hour Discussion
	 * Chart instance or an Non Official Resolution Chart instance.
	 */
	public Chart(final Session session, 
			final DeviceType deviceType,
			final String locale) {
		super(locale);
		this.setSession(session);
		this.setDeviceType(deviceType);
		this.setChartEntries(new ArrayList<ChartEntry>());
	}

	
	//===============================================
	//
	//=============== VIEW METHODS ==================
	//
	//===============================================
	/**
	 * Returns null if Chart does not exist for the specified parameters
	 * OR
	 * Returns an empty list if there are no Questions asked by any Member
	 * OR
	 * Returns a list of ChartVOs.
	 * @throws ELSException
	 * 
	 * Use this method when deviceType = "Starred Question" or
	 * "Half Hour Discussion" or "Non Official Resolution"
	 */
	public static List<ChartVO> getChartVOs(
			final Chart chart) throws ELSException {
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
			
		String deviceTypeType = deviceType.getType();
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			Group group = chart.getGroup();
			Date answeringDate = chart.getAnsweringDate();
			
			return StarredQuestionChart.getChartVOs(session, 
					group, answeringDate, deviceType, locale);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			return HalfHourDiscussionChart.getChartVOs(session, 
					deviceType, locale);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return NonOfficialResolutionChart.getChartVOs(session, 
					deviceType, locale);
		}
		else {
			throw new ELSException("Chart.getChartVOs/1", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	/**
	 * Use this method when deviceType = "Non Official Resolution" 
	 */
	public static List<ChartVO> getAdmittedResolutionsChartVOs(
			final Chart chart) throws ELSException {
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
		
		String deviceTypeType = deviceType.getType();
		if(deviceTypeType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return NonOfficialResolutionChart.getAdmittedChartVOs(session, 
					deviceType, locale);
		} 
		else {
			throw new ELSException("Chart.getAdmittedChartVOs/3", 
					"Method invoked for inappropriate device type.");
		}
	}
	
	/**
	 * Use this method when deviceType = "Starred Question" or 
	 * "Half Hour Discussion" or "Non Official Resolution"
	 */
	public static Integer maxChartedDevices(
			final Chart chart) throws ELSException {
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
		
		String deviceTypeType = deviceType.getType();
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			Group group = chart.getGroup();
			Date answeringDate = chart.getAnsweringDate();
			
			return StarredQuestionChart.maxChartedQuestions(session, 
					group, answeringDate, deviceType, locale);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			return HalfHourDiscussionChart.maxChartedDiscussions(
					session, deviceType, locale);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return NonOfficialResolutionChart.maxChartedResolutions(
					session, deviceType, locale);
		}
		else {
			throw new ELSException("Chart.maxChartedQuestions/1", 
				"Method invoked for inappropriate device type.");
		}
	}
	
		
	//===============================================
	//
	//=============== DOMAIN METHODS ================
	//
	//===============================================
	public static ChartRepository getChartRepository() {
		ChartRepository repository = new Chart().repository;
		
		if(repository == null) {
			throw new IllegalStateException(
				"ChartRepository has not been injected in Chart Domain");
		}
		
		return repository;
	}
	
	/**
	 * Create a new Chart
	 * 
	 * Use this method when deviceType = "Starred Question" or 
	 * "Half Hour Discussion" or "Non Official Resolution"
	 */
	public Chart create() throws ELSException {
		String deviceTypeType = this.getDeviceType().getType();
		
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			return StarredQuestionChart.create(this);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			return HalfHourDiscussionChart.create(this);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return NonOfficialResolutionChart.create(this);
		}		
		else {
			throw new ELSException("Chart.create/0", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	public Boolean isProcessed() throws ELSException {
		String deviceTypeType = this.getDeviceType().getType();
		
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			return StarredQuestionChart.isProcessed(this);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			return HalfHourDiscussionChart.isProcessed(this);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return NonOfficialResolutionChart.isProcessed(this);
		}
		else {
			throw new ELSException("Chart.isProcessed/0", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	/**
	 * Returns true if @param device is added to Chart, else returns false.
	 * @throws ELSException 
	 */
	public static Boolean addToChart(final Device device) throws ELSException {
		if(device instanceof Question) {
			Question question = (Question) device;
			
			DeviceType deviceType = question.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				return StarredQuestionChart.addToChart(question);
			}else {
				throw new ELSException("Chart.addToChart/1", 
					"Method invoked for inappropriate device type.");
			}
		}
		else if(device instanceof Resolution) {
			Resolution resolution = (Resolution) device;
			
			DeviceType deviceType = resolution.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
				return NonOfficialResolutionChart.addToChart(resolution);
			}
			else {
				throw new ELSException("Chart.addToChart/1", 
					"Method invoked for inappropriate device type.");
			}
		}
		else if(device instanceof StandaloneMotion) {
			StandaloneMotion sm = (StandaloneMotion) device;
			
			DeviceType deviceType = sm.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(
					ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
				return HalfHourDiscussionChart.addToChart(sm);
			}
			else {
				throw new ELSException("Chart.addToChart/1", 
					"Method invoked for inappropriate device type.");
			}
		}
		else {
			throw new ELSException("Chart.addToChart/1", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	/**
	 * Forcefully adds @param device to @param Chart. This method will simply allow
	 * a device to be added to the Chart without subjecting it to stringent business
	 * rules as is the case with Chart.addToChart/1 method.
	 * 
	 * One of the cases where this method is useful is in the case of Device handover 
	 * from a particular member to other member when the former becomes minister/speaker/chairman 
	 * OR withdraws device(s) OR is suspended OR resigns OR dies OR his/her tenure is
	 * over, and the latter is the Supporting member.
	 */
	public static boolean forcefullyAddToChart(final Chart chart,
			final Device device) throws ELSException {
		if(device instanceof Question) {
			Question question = (Question) device;
			
			DeviceType deviceType = question.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				return StarredQuestionChart.forcefullyAddToChart(chart, question);
			}
			else {
				throw new ELSException("Chart.forcefullyAddToChart/1", 
					"Method invoked for inappropriate device type.");
			}
		}
		else if(device instanceof Resolution) {
			Resolution resolution = (Resolution) device;
			
			DeviceType deviceType = resolution.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
				return NonOfficialResolutionChart.forcefullyAddToChart(chart, resolution);
			}
			else {
				throw new ELSException("Chart.forcefullyAddToChart/1", 
					"Method invoked for inappropriate device type.");
			}
		}
		else if(device instanceof StandaloneMotion) {
			StandaloneMotion sm = (StandaloneMotion) device;
			
			DeviceType deviceType = sm.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(
					ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
				return HalfHourDiscussionChart.forcefullyAddToChart(chart, sm);
			}
			else {
				throw new ELSException("Chart.forcefullyAddToChart/1", 
					"Method invoked for inappropriate device type.");
			}
		}
		else {
			throw new ELSException("Chart.forcefullyAddToChart/1", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	/**
	 * Removes @device from Chart. Only chart specific attributes (viz chartAnsweringDate)
	 * will be reset. Resetting device specific attributes is the responsibility of the caller.
	 * 
	 * Updates the clubbed entities of this question.
	 */
	public static void removeFromChart(final Device device) throws ELSException {
		if(device instanceof Question) {
			Question question = (Question) device;
			
			DeviceType deviceType = question.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionChart.removeFromChart(question);
			}
			else {
				throw new ELSException("Chart.removeFromChart/1", 
					"Method invoked for inappropriate device type.");
			}
		}
		else if(device instanceof Resolution) {
			Resolution resolution = (Resolution) device;
			
			DeviceType deviceType = resolution.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
				NonOfficialResolutionChart.removeFromChart(resolution);
			}
			else {
				throw new ELSException("Chart.removeFromChart/1", 
					"Method invoked for inappropriate device type.");
			}
		}
		else if(device instanceof StandaloneMotion) {
			StandaloneMotion sm = (StandaloneMotion) device;
			
			DeviceType deviceType = sm.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(
					ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
				HalfHourDiscussionChart.removeFromChart(sm);
			}
			else {
				throw new ELSException("Chart.removeFromChart/1", 
					"Method invoked for inappropriate device type.");
			}
		}
		else {
			throw new ELSException("Chart.removeFromChart/1", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	/**
	 * Removes @device from Chart. Only chart specific attributes (viz chartAnsweringDate)
	 * will be reset. Resetting device specific attributes is the responsibility of the caller.
	 * 
	 * DOES NOT UPDATE the clubbed entities of this question.
	 */
	public static void simpleRemoveFromChart(final Device device) throws ELSException {
		if(device instanceof Question) {
			Question question = (Question) device;
			
			DeviceType deviceType = question.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionChart.simpleRemoveFromChart(question);
			}
			else {
				throw new ELSException("Chart.removeFromChart/1", 
					"Method invoked for inappropriate device type.");
			}
		}
		else {
			throw new ELSException("Chart.removeFromChart/1", 
				"Method invoked for inappropriate device type.");
		}

	}
	
	/**
	 * @param device: Could be of type STARRED only
	 * @param fromGroup: The previous group of @param device.
	 * @param isForceAddToTargetGroupChart: Whether the device should
	 * be forcefully added to the target chart or not.
	 */
	public static void groupChange(final Device device,
			final Group fromGroup,
			final boolean isForceAddToTargetGroupChart) throws ELSException {
		if(device instanceof Question) {
			Question question = (Question) device;
			
			DeviceType deviceType = question.getOriginalType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionChart.groupChange(question, 
						fromGroup, isForceAddToTargetGroupChart);
			}
			else {
				throw new ELSException("Chart.groupChange/3", 
					"Method invoked for inappropriate device type.");
			}
		}
		else {
			throw new ELSException("Chart.groupChange/3", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	// FIND CHART
	/**
	 * Find the Chart to which @param device belongs.
	 * Returns null if this device does not belong to any Chart.
	 * @throws ELSException 
	 */
	public static Chart find(final Device device) throws ELSException {
		return Chart.getChartRepository().find(device);
	}
	
	//find chart
	public static Chart find(final Session session,
			final Group group,
			final Date answeringDate,
			final DeviceType deviceType,
			final String locale) throws ELSException {
			return Chart.getChartRepository().find(session, group, answeringDate, deviceType, locale);
		}
	
	/**
	 * Returns null if there is no Chart for the specified parameters.
	 * @throws ELSException 
	 */
	public static Chart find(final Chart chart) throws ELSException {
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
		
		String deviceTypeType = deviceType.getType();
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			Group group = chart.getGroup();
			Date answeringDate = chart.getAnsweringDate();
			
			return StarredQuestionChart.find(session, group, 
					answeringDate, deviceType, locale);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			return HalfHourDiscussionChart.find(session, deviceType, locale);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return NonOfficialResolutionChart.find(session, deviceType, locale);
		}
		else {
			throw new ELSException("Chart.find/1", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	/**
	 * For @param group, check for existence of a Chart for a given
	 * answeringDate in the descending order of the answering dates.
	 * 
	 * Returns null if there is no Chart for the specified parameters.
	 * @throws ELSException 
	 * 
	 * Use this method when @deviceType = "Starred Question"
	 */
	public static Chart findLatestStarredQuestionChart(final Session session,
			final Group group,
			final DeviceType deviceType,
			final String locale) throws ELSException {
		return StarredQuestionChart.findLatestChart(session, 
				group, deviceType, locale);
	}
	
	// FIND CHARTENTRY
	public static ChartEntry find(final Chart chart, 
			final Member primaryMember) {
		return Chart.getChartRepository().find(chart, primaryMember);
	}
	
	// FIND DEVICES
	public static List<Device> findDevices(
			final Chart chart) throws ELSException {
		return Chart.getChartRepository().findDevices(chart);
	}
	
	public static List<Device> findDevices(final Member member,
			final Chart chart) throws ELSException {
		return Chart.getChartRepository().findDevices(member, chart);
	}
	
	// FIND QUESTIONS
	/**
	 * Returns an unsorted list of Questions.
	 * OR
	 * Returns an empty list if there are no Questions.
	 * @throws ELSException
	 * 
	 * Use this method when deviceType = "Starred Question" or 
	 * "Half Hour Discussion"
	 */
	public static List<Question> findQuestions(
			final Chart chart) throws ELSException {
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
		
		String deviceTypeType = deviceType.getType();
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			Group group = chart.getGroup();
			Date answeringDate = chart.getAnsweringDate();
			
			return StarredQuestionChart.findQuestions(session, group, 
					answeringDate, deviceType, locale);
		}
		else if (deviceTypeType.equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			return HalfHourDiscussionChart.findQuestions(session, deviceType,
					locale);
		}
		else {
			throw new ELSException("Chart.findQuestions/1", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	/**
	 * Returns the list of Questions of @param member taken on a Chart.
	 * Returns an empty list if there are no Questions for member.
	 * @throws ELSException
	 * 
	 * Use this method when deviceType = "Starred Question" or 
	 * "Half Hour Discussion"
	 */
	public static List<Question> findQuestions(final Member member,
			final Chart chart) throws ELSException {
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
		
		String deviceTypeType = deviceType.getType();
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			Group group = chart.getGroup();
			Date answeringDate = chart.getAnsweringDate();
			
			return StarredQuestionChart.findQuestions(member, session, group, 
					answeringDate, deviceType, locale);
		}
		else if (deviceTypeType.equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			return HalfHourDiscussionChart.findQuestions(member, session, 
					deviceType, locale);
		}
		else {
			throw new ELSException("Chart.findQuestions/2", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	// FIND RESOLUTIONS
	/**
	 * Use this method when @deviceType = "Non Official Resolution"
	 */
	public static List<Resolution> findResolutions(
			final Chart chart) throws ELSException {
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
		
		String deviceTypeType = deviceType.getType();
		if(deviceTypeType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return NonOfficialResolutionChart.findResolutions(session, 
					deviceType, locale);
		}
		else {
			throw new ELSException("Chart.findResolutions/1", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	/**
	 * Use this method when deviceType = "Non Official Resolution"
	 */
	public static List<Resolution> findResolutions(final Member member,
			final Chart chart) throws ELSException {
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
		
		String deviceTypeType = deviceType.getType();
		if(deviceTypeType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return NonOfficialResolutionChart.findResolutions(member, session, 
					deviceType, locale);
		}
		else {
			throw new ELSException("Chart.findResolutions/2", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	// FIND MEMBERS
	/**
	 * Returns a list of Members on Chart.
	 * OR
	 * Returns an empty list if there are no Members.
	 * @throws ELSException
	 * 
	 * Use this method when deviceType = "Starred Question" or 
	 * "Half Hour Discussion" or "Non Official Resolution"
	 */
	public static List<Member> findMembers(
			final Chart chart) throws ELSException {
		Session session = chart.getSession();
		DeviceType deviceType = chart.getDeviceType();
		String locale = chart.getLocale();
		
		String deviceTypeType = deviceType.getType();
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			Group group = chart.getGroup();
			Date answeringDate= chart.getAnsweringDate();
			
			return StarredQuestionChart.findMembers(session, group,
					answeringDate, deviceType, locale);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			return HalfHourDiscussionChart.findMembers(session, 
					deviceType, locale);
		}
		else if(deviceTypeType.equals(
				ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			return NonOfficialResolutionChart.findMembers(session, 
					deviceType, locale);
		}
		else {
			throw new ELSException("Chart.findMembers/1", 
				"Method invoked for inappropriate device type.");
		}
	}
	
	public static String findNextEligibleChartQuestionDetailsOnGroupChange(final Chart chart, final Member member) {
		return Chart.getChartRepository().findNextEligibleChartQuestionDetailsOnGroupChange(chart, member);
	}
	
	//===============================================
	//
	//=============== GETTERS/SETTERS ===============
	//
	//===============================================
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Date getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(Date answeringDate) {
		this.answeringDate = answeringDate;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public List<ChartEntry> getChartEntries() {
		return chartEntries;
	}

	public void setChartEntries(List<ChartEntry> chartEntries) {
		this.chartEntries = chartEntries;
	}

}
