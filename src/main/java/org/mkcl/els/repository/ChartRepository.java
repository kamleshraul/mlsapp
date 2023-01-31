package org.mkcl.els.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javassist.expr.Instanceof;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.chart.Chart;
import org.mkcl.els.domain.chart.ChartEntry;
import org.springframework.stereotype.Repository;

@Repository
public class ChartRepository extends BaseRepository<Chart, Long> {
	
	public Chart find(final Device device) throws ELSException{
		Chart chart = null;
		
		StringBuffer strQuery = new StringBuffer();
		/*strQuery.append(
			"SELECT c" +
			" FROM Chart c JOIN c.chartEntries ce" +
			" JOIN ce.devices d" +
			" WHERE d.id = :deviceId");
		
		TypedQuery<Chart> jpQuery = this.em().createQuery(strQuery.toString(), Chart.class);
		*/
		strQuery.append(
				"SELECT c.id FROM charts c"
				+" JOIN charts_chart_entries cce ON (cce.chart_id = c.id)"
				+ " JOIN chart_entries ce ON (cce.chart_entry_id = ce.id)"
				+ " JOIN chart_entries_devices ced ON (ced.chart_entry_id=ce.id)"
				+ " WHERE ced.device_id=:deviceId");
		Query jpQuery = this.em().createNativeQuery(strQuery.toString());
		jpQuery.setParameter("deviceId", device.getId());
		try {
			BigInteger chartId = (BigInteger) jpQuery.getSingleResult();
			chart = Chart.findById(Chart.class, Long.parseLong(chartId.toString()));
			//chart = jpQuery.getSingleResult();
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_Chart_find(DT)", "Chart not found.");
			throw elsException;
		}
		return chart;		
	}

	public Chart find(final Session session,
			final Group group,
			final Date answeringDate,
			final DeviceType deviceType,
			final String locale) throws ELSException{
		
		String strQuery = "SELECT c FROM Chart c WHERE" +
							" c.session.id = :sessionId" +  
							" AND c.group.id = :groupId" +
							" AND c.answeringDate = :answeringDate" +
							" AND c.deviceType.id = :deviceTypeId" + 
							" AND c.locale=:locale";
		
		TypedQuery<Chart> jpQuery = this.em().createQuery(strQuery, Chart.class);
		jpQuery.setParameter("sessionId", session.getId());
		jpQuery.setParameter("groupId", group.getId());
		jpQuery.setParameter("answeringDate", answeringDate);
		jpQuery.setParameter("deviceTypeId", deviceType.getId());
		jpQuery.setParameter("locale", locale);
		
		Chart chart = null;
		try {
			chart = jpQuery.getSingleResult();
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_Chart_find(S_G_AD_DT)", "Chart not found.");
			throw elsException;
		}
		return chart;
	}
	
	public Chart find(final Session session, 
			final DeviceType deviceType, 
			final String locale) throws ELSException{
		
		String strQuery = "SELECT c FROM Chart c WHERE" +
							" c.session.id = :sessionId " +
							" AND c.deviceType.id = :deviceTypeId " + 
							" AND c.locale=:locale";
		
		Query jpQuery = this.em().createQuery(strQuery);
		jpQuery.setParameter("sessionId", session.getId());
		jpQuery.setParameter("deviceTypeId", deviceType.getId());
		jpQuery.setParameter("locale", locale);
		
		Chart chart = null;
		try {
			chart = (Chart)jpQuery.getSingleResult();
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_Chart_find(S_DT)", "Chart not found.");
			throw elsException;
		}
		
		return chart;
	}
	
	@SuppressWarnings("unchecked")
	public List<Device> findDevices(final Chart chart) throws ELSException{
		StringBuffer strQuery = new StringBuffer();
		String strDeviceType = chart.getDeviceType().getType();
		TypedQuery<Device> jpQuery = null;
		
		if(strDeviceType.equals(ApplicationConstants.STARRED_QUESTION)) {
			String starredQuery = this.findQuestionsOnChartQuery(chart.getSession(), 
					chart.getGroup(), chart.getAnsweringDate(), chart.getDeviceType(), 
					chart.getLocale());
			
			strQuery.append(starredQuery);
						
			/*CustomParameter db_DtaeFormat = 
					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				String date = 
					FormaterUtil.formatDateToString(chart.getAnsweringDate(), db_DtaeFormat.getValue());*/
				
			jpQuery = this.em().createQuery(strQuery.toString(), Device.class);
			jpQuery.setParameter("sessionId", chart.getSession().getId());
			jpQuery.setParameter("groupId", chart.getGroup().getId());
			jpQuery.setParameter("answeringDate", chart.getAnsweringDate());
			jpQuery.setParameter("deviceTypeId", chart.getDeviceType().getId());
			jpQuery.setParameter("locale", chart.getLocale());
			
		}else if(strDeviceType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			String resolutionQuery = this.findResolutionsOnChartQuery(chart.getSession(), 
					chart.getDeviceType(), chart.getLocale());
			strQuery.append(resolutionQuery);
			
			jpQuery = (TypedQuery<Device>) this.em().createNativeQuery(strQuery.toString(), Resolution.class);
			jpQuery.setParameter("sessionId", chart.getSession().getId());
			jpQuery.setParameter("deviceTypeId", chart.getDeviceType().getId());
			jpQuery.setParameter("locale", chart.getLocale());
			
		}else if(strDeviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
			String hdsQuery = this.findHDSQuestionsOnChartQuery(chart.getSession(),chart.getDeviceType(),chart.getLocale());
			strQuery.append(hdsQuery);
			
			jpQuery = this.em().createQuery(strQuery.toString(), Device.class);
			jpQuery.setParameter("sessionId", chart.getSession().getId());
			jpQuery.setParameter("deviceTypeId", chart.getDeviceType().getId());
			jpQuery.setParameter("locale", chart.getLocale());
		}
		
		List<Device> devices = new ArrayList<Device>();
		try{
			devices = jpQuery.getResultList();
			
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_List<Device>_findDevices(C)", "No device found.");
			throw elsException;
		}
		return devices;
	}
	
	@SuppressWarnings("unchecked")
	public List<Device> findDevices(final Member member, final Chart chart) throws ELSException {
		StringBuffer strQuery = new StringBuffer();
		String strDeviceType = chart.getDeviceType().getType();
		TypedQuery<Device> jpQuery = null;
		
		if(strDeviceType.equals(ApplicationConstants.STARRED_QUESTION)) {
			String starredQuery = this.findMembersQuestionsQuery(member, chart.getSession(), 
					chart.getGroup(), chart.getAnsweringDate(), chart.getDeviceType(), 
					chart.getLocale());
			strQuery.append(starredQuery);
			
			/*CustomParameter parameter =
					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				String date = 
					FormaterUtil.formatDateToString(chart.getAnsweringDate(), parameter.getValue());*/
				
			jpQuery = this.em().createQuery(strQuery.toString(), Device.class);
			jpQuery.setParameter("sessionId", chart.getSession().getId());
			jpQuery.setParameter("groupId", chart.getGroup().getId());
			jpQuery.setParameter("answeringDate", chart.getAnsweringDate());
			jpQuery.setParameter("deviceTypeId", chart.getDeviceType().getId());
			jpQuery.setParameter("locale", chart.getLocale());
			jpQuery.setParameter("memberId", member.getId());
			
		}else if(strDeviceType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			String resolutionQuery = this.findMembersResolutionsQuery(member, chart.getSession(), 
					chart.getDeviceType(), chart.getLocale());
			strQuery.append(resolutionQuery);
			
			//jpQuery = this.em().createQuery(strQuery.toString(), Device.class);
			jpQuery = (TypedQuery<Device>) this.em().createNativeQuery(strQuery.toString(), Resolution.class);
			jpQuery.setParameter("sessionId", chart.getSession().getId());
			jpQuery.setParameter("deviceTypeId", chart.getDeviceType().getId());
			jpQuery.setParameter("locale", chart.getLocale());
			jpQuery.setParameter("memberId", member.getId());
		}else if(strDeviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
			String hdsQuery = this.findHDSMembersQuestionsQuery(member, chart.getSession(), 
					chart.getDeviceType(), chart.getLocale());
			strQuery.append(hdsQuery);
			
			jpQuery = this.em().createQuery(strQuery.toString(), Device.class);
			jpQuery.setParameter("sessionId", chart.getSession().getId());
			jpQuery.setParameter("deviceTypeId", chart.getDeviceType().getId());
			jpQuery.setParameter("locale", chart.getLocale());
			jpQuery.setParameter("memberId", member.getId());
		}
		
		List<Device> devices = new ArrayList<Device>();
		try{
			List<Device> dX = jpQuery.getResultList();
			if(dX  != null){
				devices = dX;
			}
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_List<Device>_findDevices(M_C)", "No device found.");
			throw elsException;
		}
		return devices;
	}
	
	public List<Question> findQuestions(final Member member, 
			final Session session,
			final Group group, 
			final Date answeringDate, 
			final DeviceType deviceType,
			final String locale) throws ELSException {
		StringBuffer strQuery = new StringBuffer();
		Query jpQuery = null;
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
			String hdsQuery = this.findHDSMembersQuestionsQuery(member, session, deviceType, locale);
			strQuery.append(hdsQuery);
			
			jpQuery = this.em().createQuery(strQuery.toString(), Question.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("memberId", member.getId());
		}else{
			String starredQuery = this.findMembersQuestionsQuery(member, session, group, answeringDate,deviceType, locale);
			strQuery.append(starredQuery);
								
			/*CustomParameter parameter =
					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				String date = 
					FormaterUtil.formatDateToString(answeringDate, parameter.getValue());*/
				
			jpQuery = this.em().createNativeQuery(strQuery.toString(), Question.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("groupId", group.getId());
			jpQuery.setParameter("answeringDate", answeringDate);
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("memberId", member.getId());
		}
		
		
		List<Question> questions = new ArrayList<Question>();
		try{
			questions = jpQuery.getResultList();
			
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_List<Question>_findQuestions", "No question found.");
			throw elsException;
		}
		
		return questions;
	}
	
	public List<Question> findQuestions(final Session session, 
			final Group group,
			final Date answeringDate, 
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		StringBuffer strQuery = new StringBuffer();
		TypedQuery<Question> jpQuery = null;
		if (deviceType.getType().equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			String hdsQuery = this.findHDSQuestionsOnChartQuery(session,
					deviceType, locale);
			strQuery.append(hdsQuery);

			jpQuery = this.em().createQuery(strQuery.toString(), Question.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("locale", locale);
		} else {
			String starredQuery = this.findQuestionsOnChartQuery(session,
					group, answeringDate, deviceType, locale);
			strQuery.append(starredQuery);
			
			jpQuery = this.em().createQuery(starredQuery, Question.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("groupId", group.getId());
			jpQuery.setParameter("answeringDate", answeringDate);
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("locale", locale);
		}
		
		
		List<Question> questions = new ArrayList<Question>();
		try{
			questions = jpQuery.getResultList();
			
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_List<Question>_findQuestions", "No question found.");
			throw elsException;
		}
		
		return questions;
	}
	
	public List<Resolution> findResolutions(final Member member,
			final Session session, 
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		StringBuffer strQuery = new StringBuffer();
		
		String resolutionQuery = this.findMembersResolutionsQuery(member, session, deviceType,locale);
		strQuery.append(resolutionQuery);
				
		TypedQuery<Resolution> jpQuery = this.em().createQuery(strQuery.toString(), Resolution.class);
		jpQuery.setParameter("sessionId", session.getId());
		jpQuery.setParameter("deviceTypeId", deviceType.getId());
		jpQuery.setParameter("locale", locale);
		jpQuery.setParameter("memberId", member.getId());
		
		List<Resolution> resolutions = new ArrayList<Resolution>();
		
		try{
			resolutions = jpQuery.getResultList();
		
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_List<Resolution>_findResolutions", "No resolution found.");
			throw elsException;
		}
		return resolutions;
	}

	public Integer findResolutionsCount(final Member member,
			final Session session, 
			final DeviceType deviceType,
			final Status[] includeStatuses,
			final String locale) throws ELSException {
		StringBuffer strQuery = new StringBuffer();
		
		String resolutionsCountQuery = this.findMembersResolutionsCountQuery(member, session, 
				deviceType, includeStatuses, locale);
		strQuery.append(resolutionsCountQuery);
		
		Query query = this.em().createNativeQuery(strQuery.toString());
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("locale", locale);
		query.setParameter("memberId", member.getId());
		Integer count = null;
		try{
			Object countObj = query.getSingleResult();
			if(countObj!=null) {
				String countStr = countObj.toString();
				if(!countStr.isEmpty()) {
					count = Integer.parseInt(countStr);					
				}
			}
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_Integer_findResolutionsCount", "Cann't find resolution count.");
			throw elsException;
		}
	    return count;
	}

	@SuppressWarnings("unchecked")
	public List<Resolution> findResolutions(final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		StringBuffer strQuery = new StringBuffer();
		
		String resolutionQuery = this.findResolutionsOnChartQuery(session, deviceType, locale);
		strQuery.append(resolutionQuery);
		
		Query jpQuery = this.em().createNativeQuery(strQuery.toString(), Resolution.class);
		jpQuery.setParameter("sessionId", session.getId());
		jpQuery.setParameter("deviceTypeId", deviceType.getId());
		jpQuery.setParameter("locale", locale);
		List<Resolution> resolutions = new ArrayList<Resolution>();
		try{
			resolutions = jpQuery.getResultList();
			
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_List<Resolution>_findResolutions", "Cann't find resolutions.");
			throw elsException;
		}
		return resolutions;
	}
	
	public List<Member> findMembers(final Session session, 
			final Group group,
			final Date answeringDate, 
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter =
		//	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String date = new DateFormater().formatDateToString(answeringDate, parameter.getValue());

		/*String date = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");*/
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(
			"SELECT m" +
			" FROM Chart c JOIN c.chartEntries ce JOIN ce.member m" +
			" WHERE c.session.id = :sessionId" +
			" AND c.group.id = :groupId" +
			" AND c.answeringDate = :answeringDate" +
			" AND c.deviceType=:deviceTypeId" + 
			" AND c.locale = :locale");

		TypedQuery<Member> jpQuery = this.em().createQuery(strQuery.toString(), Member.class);
		jpQuery.setParameter("sessionId", session.getId());
		jpQuery.setParameter("groupId", group.getId());
		jpQuery.setParameter("answeringDate", answeringDate);
		jpQuery.setParameter("deviceTypeId", deviceType.getId());
		jpQuery.setParameter("locale", locale);
		
		List<Member> members = new ArrayList<Member>();
		
		try{
			members = jpQuery.getResultList();
			
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_List<Member>_findMembers", "Cann't find members.");
			throw elsException;
		}
		
		return members;
	}

	public List<Member> findMembers(final Session session, 
			final DeviceType deviceType,
			final String locale) throws ELSException {
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(
			"SELECT m" +
			" FROM Chart c JOIN c.chartEntries ce JOIN ce.member m" +
			" WHERE c.session.id=:sessionId" +
			" AND c.deviceType=:deviceTypeId" +
			" AND c.locale=:locale");

		TypedQuery<Member> jpQuery = this.em().createQuery(strQuery.toString(), Member.class);
		jpQuery.setParameter("sessionId", session.getId());
		jpQuery.setParameter("deviceTypeId", deviceType.getId());
		jpQuery.setParameter("locale", locale);
		
		List<Member> members = new ArrayList<Member>();
		try{
			members = jpQuery.getResultList();
			
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_List<Member>_findMembers", "Cann't find members.");
			throw elsException;
		}
		
		return members;
	}
	
	public Boolean isProcessed(final Chart chart, final String excludeInternalStatus) throws ELSException {
		StringBuffer strQuery = new StringBuffer();
		String strDeviceType = chart.getDeviceType().getType();
		TypedQuery<Long> jpQuery = null; 
		if(strDeviceType.equals(ApplicationConstants.STARRED_QUESTION)) {
			String starredQuery = this.isProcessedQuestionQuery(chart.getSession(), 
					chart.getGroup(), chart.getAnsweringDate(), chart.getDeviceType(), 
					excludeInternalStatus, chart.getLocale()); 
			strQuery.append(starredQuery);
			
			/*CustomParameter parameter =
		            CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
				String date = 
					FormaterUtil.formatDateToString(chart.getAnsweringDate(), parameter.getValue());*/
				
			jpQuery = this.em().createQuery(strQuery.toString(), Long.class);
           	
           	jpQuery.setParameter("sessionId", chart.getSession().getId());
			jpQuery.setParameter("groupId", chart.getGroup().getId());
			jpQuery.setParameter("answeringDate", chart.getAnsweringDate());
			jpQuery.setParameter("deviceTypeId", chart.getDeviceType().getId());
			jpQuery.setParameter("locale", chart.getLocale());
			jpQuery.setParameter("excludeInternalStatus", excludeInternalStatus);
			
		}else if(strDeviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
			String hdsQuery = this.isProcessedHDSQuery(chart.getSession(), chart.getDeviceType(), excludeInternalStatus, chart.getLocale());
			strQuery.append(hdsQuery);
			
			jpQuery = this.em().createQuery(strQuery.toString(), Long.class);
			jpQuery.setParameter("sessionId", chart.getSession().getId());
			jpQuery.setParameter("deviceTypeId", chart.getDeviceType().getId());
			jpQuery.setParameter("locale", chart.getLocale());
			jpQuery.setParameter("excludeInternalStatus", excludeInternalStatus);
			
		}else if(strDeviceType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			String resolutionQuery = this.isProcessedResolutionQuery(chart.getSession(), 
					chart.getDeviceType(), excludeInternalStatus, chart.getLocale()); 
			strQuery.append(resolutionQuery);
			
			jpQuery = this.em().createQuery(strQuery.toString(), Long.class);
			jpQuery.setParameter("sessionId", chart.getSession().getId());
			jpQuery.setParameter("deviceTypeId", chart.getDeviceType().getId());
			jpQuery.setParameter("locale", chart.getLocale());
			jpQuery.setParameter("excludeInternalStatus", excludeInternalStatus);
		}

		long count = 0;
		try{
			count = jpQuery.getSingleResult().longValue();
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_Boolean_isProcessed", "Cann't find processing detail.");
			throw elsException;
		}
	    if(count == 0) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Update chartAnsweringdate, internalStatus, and recommendationStatus
	 * of all the Questions on @param chart to @param chartAnsweringDate,
	 * @param internalStatus and @param recommendationStatus respectively.
	 * @throws ELSException 
	 */
	// NATIVE QUERY	
	public void updateChartQuestions(final Session session,
		final Group group,
		final DeviceType deviceType,
		final Date answeringDate,
		final QuestionDates chartAnsweringDate,
		final Status internalStatus,
		final Status recommendationStatus,
		final String locale) throws ELSException {
		
		org.mkcl.els.domain.Query query = null;
		Query jpQuery = null;
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
			query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "CHART_UPDATE_CHART_QUESTIONS_HDS", "");
			if(query != null){
				jpQuery = this.em().createNativeQuery(query.getQuery());
				jpQuery.setParameter("internalStatusId", internalStatus.getId());
				jpQuery.setParameter("recommendationStatusId", recommendationStatus.getId());
				jpQuery.setParameter("sessionId", session.getId());
				jpQuery.setParameter("locale", locale);
			}
		}else{
			/*CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			date = FormaterUtil.formatDateToString(answeringDate, parameter.getValue());*/			
			query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "CHART_UPDATE_CHART_QUESTIONS_NON_HDS", "");
			if(query != null){
								
				jpQuery = this.em().createNativeQuery(query.getQuery());
				jpQuery.setParameter("chartAnsweringDateId", chartAnsweringDate.getId());
				jpQuery.setParameter("internalStatusId", internalStatus.getId());
				jpQuery.setParameter("recommendationStatusId", recommendationStatus.getId());
				jpQuery.setParameter("sessionId", session.getId());
				jpQuery.setParameter("groupId", group.getId());
				jpQuery.setParameter("answeringDate", answeringDate);
				jpQuery.setParameter("locale", locale);
			}
		}
		
		/*query.append(
			"UPDATE questions SET");
		if(!deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
			query.append(" chart_answering_date = " + chartAnsweringDate.getId() + ",");
		}
		query.append(" internalstatus_id = " + internalStatus.getId() + "," +
			" recommendationstatus_id = " + recommendationStatus.getId() +
			" WHERE id IN (" +
				" SELECT qid FROM (" +
					" SELECT q.id AS qid" +
					" FROM charts AS c JOIN charts_chart_entries AS cce" +
					" JOIN chart_entries_devices AS ceq JOIN questions AS q" +
					" WHERE c.id = cce.chart_id" +
					" AND cce.chart_entry_id = ceq.chart_entry_id" +
					" AND ceq.device_id = q.id" +
					" AND c.session_id = " + session.getId());
		if(!deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
			query.append(" AND c.group_id = " + group.getId() +
					" AND c.answering_date = '" + date + "'");
		}
		query.append(" AND c.locale = '" + locale + "') AS rs )");*/
		try{
			jpQuery.executeUpdate();
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_void_updateChartQuestions", "Cann't update chart questions.");
			throw elsException;
		}
	}

	// NATIVE QUERY
	public void updateChartResolutions(final Session session,
			final Status internalStatus,
			final Status recommendationStatus,
			final String locale) throws ELSException {
		StringBuffer strQuery = null;
		org.mkcl.els.domain.Query query = null;
		HouseType houseType = session.getHouse().getType();
		Query jpQuery = null;
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "CHART_UPDATE_CHART_RESOLUTIONS_LOWERHOUSE", locale);
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "CHART_UPDATE_CHART_RESOLUTIONS_UPPERHOUSE", locale);
		}

		if(query != null){
			strQuery = new StringBuffer(query.getQuery());
			jpQuery = this.em().createNativeQuery(strQuery.toString());
			
			jpQuery.setParameter("internalStatusId", internalStatus.getId());
			jpQuery.setParameter("recommendationStatusId", recommendationStatus.getId());
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("locale", locale);
			
			try{
				jpQuery.executeUpdate();
			}catch(EntityNotFoundException enfe){
				logger.error(enfe.getMessage());
			}catch (NoResultException nre) {
				logger.error(nre.getMessage());
			}catch(Exception e) {	
				e.printStackTrace();
				logger.error(e.getMessage());
				ELSException elsException = new ELSException();
				elsException.setParameter("ChartRepository_void_updateChartResolutions", "Cann't update chart resolutions.");
				throw elsException;
			}
		}
		
//		UPDATE resolutions SET 
//		lowerhouse_internalstatus_id = ?,
//		lowerhouse_recommendationstatus_id = ? 
//		WHERE id IN (
//			SELECT rs.rid FROM (SELECT r.id AS rid 
//				FROM charts AS c JOIN charts_chart_entries AS cce
//				JOIN chart_entries_devices AS ced 
//				JOIN resolutions AS r
//				WHERE c.id = cce.chart_id
//				AND cce.chart_entry_id = ced.chart_entry_id
//				AND ced.device_id = r.id
//				AND c.session_id = ?
//				AND c.locale = ?)) AS rs
		
//		UPDATE resolutions 
//		SET upperhouse_internalstatus_id = :internalStatusId,
//		 	upperhouse_recommendationstatus_id = :recommendationStatusId
//			WHERE id IN (SELECT rs.rid FROM (
//					SELECT r.id AS rid 
//					FROM charts AS c JOIN charts_chart_entries AS cce
//					JOIN chart_entries_devices AS ced JOIN resolutions AS r
//					WHERE c.id = cce.chart_id
//					AND cce.chart_entry_id = ced.chart_entry_id	
//					AND ced.device_id = r.id
//					AND c.session_id =:sessionId
//					AND c.locale = :locale) ) AS rs
	}
	
	public void updateChartStandalones(final Session session,
			final Group group,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status internalStatus,
			final Status recommendationStatus,
			final String locale) throws ELSException {
			
			org.mkcl.els.domain.Query query = null;
			Query jpQuery = null;
			
			if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "CHART_UPDATE_CHART_HDS", "");
				if(query != null){
					jpQuery = this.em().createNativeQuery(query.getQuery());
					jpQuery.setParameter("internalStatusId", internalStatus.getId());
					jpQuery.setParameter("recommendationStatusId", recommendationStatus.getId());
					jpQuery.setParameter("sessionId", session.getId());
					jpQuery.setParameter("locale", locale);
				}
			}
			
			
			try{
				jpQuery.executeUpdate();
			}catch(EntityNotFoundException enfe){
				logger.error(enfe.getMessage());
			}catch (NoResultException nre) {
				logger.error(nre.getMessage());
			}catch(Exception e) {	
				e.printStackTrace();
				logger.error(e.getMessage());
				ELSException elsException = new ELSException();
				elsException.setParameter("ChartRepository_void_updateChartQuestions", "Cann't update chart questions.");
				throw elsException;
			}
		}
	
	// NATIVE QUERY
	public Integer findMaxChartedQuestions(final Session session,
			final Group group,
			final Date answeringDate,
			final DeviceType deviceType,
			final String locale) throws ELSException {
    	CustomParameter parameter = null;
        String date = null;
        
        org.mkcl.els.domain.Query query = null;
        Query jpQuery = null;
        
        if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
        	query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "CHART_MAX_CHARTED_HDS", "");
        	        	
        	if(query != null){
        		jpQuery = this.em().createNativeQuery(query.getQuery());
        		
        		Status rejectedStatus = Status.findByFieldName(Status.class, "type", ApplicationConstants.STANDALONE_FINAL_REJECTION, locale);
        		 
        		jpQuery.setParameter("sessionId", session.getId());
        		jpQuery.setParameter("deviceTypeId", deviceType.getId());
        		jpQuery.setParameter("rejectedStatusId", rejectedStatus.getId());
				jpQuery.setParameter("locale", locale);
        		
        	}
        }else{
        	parameter = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
        	date = FormaterUtil.formatDateToString(answeringDate, parameter.getValue());
        	
        	query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "CHART_MAX_CHARTED_QUESTIONS_NON_HDS", "");
        	
        	if(query != null){
        		jpQuery = this.em().createNativeQuery(query.getQuery());
        				
        		jpQuery.setParameter("sessionId", session.getId());
        		jpQuery.setParameter("deviceTypeId", deviceType.getId());
        		jpQuery.setParameter("groupId", group.getId());
        		jpQuery.setParameter("answeringDate", date);
				jpQuery.setParameter("locale",locale);	
        	}
        	
        	
        }
        
        /*StringBuffer strQuery = new StringBuffer();
        strQuery.append(
        	"SELECT MAX(no_of_Questions)" +
        	" FROM (" +
        		" SELECT COUNT(q.number) AS no_of_questions" +
        		" FROM charts AS c JOIN charts_chart_entries AS cce" +
        		" JOIN chart_entries AS ce JOIN chart_entries_devices AS ceq" +
        		" JOIN questions AS q" +
        		" WHERE c.id = cce.chart_id " +
        		" AND cce.chart_entry_id = ce.id" +
        		" AND ce.id = ceq.chart_entry_id" +
        		" AND ceq.device_id = q.id" +
        		" AND c.session_id = " + session.getId()+
        		" AND c.device_type="+ deviceType.getId());
        if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
        	Status rejectedStatus = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_FINAL_REJECTION, locale.toString());
        	strQuery.append(" AND q.internalstatus_id<>"+rejectedStatus.getId());
        }else{
        	strQuery.append(
        		" AND c.group_id = " + group.getId() +
        		" AND c.answering_date = '" + date + "'");
        }
        strQuery.append(" AND c.locale = '" + locale + "'" + 
        			" GROUP BY q.member_id" +
        		" ) AS rs");*/
        
        
        BigInteger maxChartedQuestions = null;
        try{
        	maxChartedQuestions = (BigInteger)jpQuery.getSingleResult();
        }catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_Integer_findMaxChartedQuestions", "Cann't findMaxChartedQuestions.");
			throw elsException;
		}
        
        if(maxChartedQuestions == null) {
        	return 0;
        }
        else {
        	return maxChartedQuestions.intValue();
        }  
    }
	
	// NATIVE QUERY
	public Integer findMaxChartedResolution(final Session session, final String locale) throws ELSException {
		Status internalStatus=Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REJECTION, locale);
		org.mkcl.els.domain.Query query = null;
		Query jpQuery = null;
		/*StringBuffer strQuery = new StringBuffer();
		strQuery.append(
			"SELECT MAX(no_of_resolutions)" +
        	" FROM (" +
        		" SELECT COUNT(r.number) AS no_of_resolutions" +
        		" FROM charts AS c JOIN charts_chart_entries AS cce" +
        		" JOIN chart_entries AS ce JOIN chart_entries_devices AS ceq" +
        		" JOIN resolutions AS r" +
        		" WHERE c.id = cce.chart_id " +
        		" AND cce.chart_entry_id = ce.id" +
        		" AND ce.id = ceq.chart_entry_id" +
        		" AND ceq.device_id = r.id" +
        		" AND c.session_id = " + session.getId() +
        		" AND c.locale = '" + locale + "'" +
        		" AND (r.lowerhouse_internalstatus_id ="+internalStatus.getId()+
        		" OR r.upperhouse_internalstatus_id ="+internalStatus.getId()+")"+
        		" GROUP BY r.member_id" +
        		
        	" ) AS rs"
			);
        Query query = this.em().createNativeQuery(strQuery.toString());*/
		
		query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "CHART_MAX_CHARTED_RESOLUTIONS", locale);
    	
    	if(query != null){
    		jpQuery = this.em().createNativeQuery(query.getQuery());
    		 
    		jpQuery.setParameter("sessionId", session.getId());
    		jpQuery.setParameter("lowerhouse_internalStatusId", internalStatus.getId());
    		jpQuery.setParameter("upperhouse_internalStatusId", internalStatus.getId());
			jpQuery.setParameter("locale", locale);
    		
    	}
        BigInteger maxChartedResolutions = null;
        try{
        	maxChartedResolutions = (BigInteger) jpQuery.getSingleResult();
        }catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_Integer_findMaxChartedQuestions", "Cann't findMaxChartedResolutionss.");
			throw elsException;
		}
        
        if(maxChartedResolutions == null) {
        	return 0;
        }
        else {
        	return maxChartedResolutions.intValue();
        }
	}
	
	public Integer findQuestionsCount(final Member member,
			final Session session, 
			final DeviceType deviceType,
			final Status[] includeStatuses,
			final String locale) throws ELSException {
		StringBuffer strQuery = new StringBuffer();
		
		String questionCountQuery = this.findMembersQuestionsCountQuery(member, session, 
				deviceType, includeStatuses, locale);
		strQuery.append(questionCountQuery);
		
		TypedQuery<Long> query = this.em().createQuery(strQuery.toString(), Long.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("locale", "'"+ locale +"'");
		query.setParameter("memberId", member.getId());
		
		long count = 0;
		try{
			count = query.getSingleResult();
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_Integer_findMaxChartedQuestions", "Cann't findMaxChartedResolutionss.");
			throw elsException;
		}
	    return (int) count;
	}
	
	public Integer findStandalonesCount(final Member member,
			final Session session, 
			final DeviceType deviceType,
			final Status[] includeStatuses,
			final String locale) throws ELSException {
		StringBuffer strQuery = new StringBuffer();
		
		String questionCountQuery = this.findMembersStandalonesCountQuery(member, session, 
				deviceType, includeStatuses, locale);
		strQuery.append(questionCountQuery);
		
		Query query = this.em().createNativeQuery(strQuery.toString());
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("locale", locale);
		query.setParameter("memberId", member.getId());
		
		long count = 0;
		try{
			/*@SuppressWarnings("rawtypes")
			List data = query.getResultList();
			if(data != null){
				count = data.size();				
			}*/
			
			Object data = query.getSingleResult();
			if(data != null){
				if(data instanceof Long){
					count = ((Long)data).longValue();
				}else if(data instanceof BigInteger){
					count = ((BigInteger)data).longValue();
				}
			}
		}catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ChartRepository_Integer_findMaxChartedQuestions", "Cann't findMaxChartedResolutionss.");
			throw elsException;
		}
	    return (int) count;
	}
	
	//=============== INTERNAL METHODS ==============
	private String findQuestionsOnChartQuery(final Session session, 
			final Group group,
			final Date answeringDate, 
			final DeviceType deviceType, 
			final String locale) {
		/*CustomParameter parameter =
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		String date = 
			FormaterUtil.formatDateToString(answeringDate, parameter.getValue());*/
		
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(
			"SELECT q" +
			" FROM Question q" +
			" WHERE q.id IN (" +
				" SELECT d.id" +
				" FROM Chart c JOIN c.chartEntries ce" +
				" JOIN ce.devices d" +
				" WHERE c.session.id =:sessionId" +
				" AND c.group.id =:groupId" +
				" AND c.answeringDate =:answeringDate" +
				" AND c.deviceType.id =:deviceTypeId" +
				" AND c.locale =:locale)" +
				" ORDER BY q.number ASC");
		
		return strQuery.toString();
	}
	
	private String findHDSQuestionsOnChartQuery(final Session session,
			final DeviceType deviceType, 
			final String locale) {
		
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(
			"SELECT q" +
			" FROM Question q" +
			" WHERE q.id IN (" +
				" SELECT d.id" +
				" FROM Chart c JOIN c.chartEntries ce JOIN ce.devices d" +
				" WHERE c.session.id = :sessionId" +
				" AND c.deviceType.id =:deviceTypeId" +
				" AND c.locale = :locale)" +
				" ORDER BY q.number ASC"
			);
		
		return strQuery.toString();
	}
	
	private String findMembersQuestionsQuery(final Member member, 
			final Session session,
			final Group group, 
			final Date answeringDate, 
			final DeviceType deviceType,
			final String locale) {
		/*CustomParameter parameter =
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		String date = 
			FormaterUtil.formatDateToString(answeringDate, parameter.getValue());*/
		
		/** Added the native query to retrieve the result  **/
		
		StringBuffer strQuery = new StringBuffer();
		strQuery.append("SELECT q.*"
				+ " FROM questions q"
				+ " WHERE q.id IN ("
				+ " SELECT device_id"
				+ " FROM charts c"
				+ " JOIN charts_chart_entries  cce ON (c.id = cce.chart_id)"
				+ " JOIN chart_entries ce ON (ce.id = cce.chart_entry_id)"
				+ " JOIN chart_entries_devices ced ON (ced.chart_entry_id=ce.id)"
				+ " WHERE c.session_id = :sessionId"
				+ " AND c.group_id = :groupId" 
				+ " AND c.answering_date = :answeringDate"
				+ " AND c.device_type =:deviceTypeId" 
				+ " AND c.locale = :locale" 
				+ " AND ce.member_id =:memberId)"
				+ " ORDER BY q.number ASC");
/*		strQuery.append(
			"SELECT q" +
			" FROM Question q" +
			" WHERE q.id IN (" +
				" SELECT d.id" +
				" FROM Chart c JOIN c.chartEntries ce JOIN ce.devices d" +
				" WHERE c.session.id = :sessionId" +
				" AND c.group.id = :groupId" +
				" AND c.answeringDate = :answeringDate" +
				" AND c.deviceType.id =:deviceTypeId" +
				" AND c.locale = :locale" +
				" AND ce.member.id =:memberId)" +
				" ORDER BY q.number ASC)"
			);*/
		
		return strQuery.toString();
	}
	
	private String findHDSMembersQuestionsQuery(final Member member, 
			final Session session,
			final DeviceType deviceType,
			final String locale) {
		
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(
			"SELECT q" +
			" FROM Question q" +
			" WHERE q.id IN (" +
				" SELECT d.id" +
				" FROM Chart c JOIN c.chartEntries ce JOIN ce.devices d" +
				" WHERE c.session.id = :sessionId" +
				" AND c.deviceType.id =:deviceTypeId" +
				" AND c.locale =:locale" +
				" AND ce.member.id = :memberId)" +
				" ORDER BY q.number ASC)"
			);
		
		return strQuery.toString();
	}
	
	private String findResolutionsOnChartQuery(final Session session,
			final DeviceType deviceType,
			final String locale) {
		StringBuffer strQuery = new StringBuffer();
		
//		strQuery.append(
//			"SELECT r" +
//			" FROM Resolution r" +
//			" WHERE r.id IN ("+
//				" SELECT d.id" +
//				" FROM Chart c JOIN c.chartEntries ce JOIN ce.devices d" +
//				" WHERE c.session.id =:sessionId" +
//				" AND c.deviceType.id =:deviceTypeId" +
//				" AND c.locale = :locale" +
//				" ORDER BY r.number ASC)");
		
		strQuery.append(
				"SELECT r.*" +
				" FROM resolutions r" +
				" WHERE r.id IN ("
					+ " SELECT ced.device_id"
					+ " FROM charts c"
					+ " JOIN charts_chart_entries  cce ON (c.id = cce.chart_id)"
					+ " JOIN chart_entries ce ON (ce.id = cce.chart_entry_id)"
					+ " JOIN chart_entries_devices ced ON (ced.chart_entry_id=ce.id)"
					+ " WHERE c.session_id = :sessionId"
					+ " AND c.device_type =:deviceTypeId" 
					+ " AND c.locale = :locale)" 
					+ " ORDER BY r.number ASC");
		
		return strQuery.toString();
	}
	
	private String findMembersResolutionsQuery(final Member member,
			final Session session,
			final DeviceType deviceType,
			final String locale) {
		StringBuffer strQuery = new StringBuffer();
		
//		strQuery.append(
//			"SELECT r" +
//			" FROM Resolution r" +
//			" WHERE r.id IN ("+
//				" SELECT d.id" +
//				" FROM Chart c JOIN c.chartEntries ce JOIN ce.devices d" +
//				" WHERE c.session.id =:sessionId" +
//				" AND c.deviceType.id =:deviceTypeId" +
//				" AND c.locale =:locale" +
//				" AND ce.member.id =:memberId )" +
//				" ORDER BY r.number ASC)"
//			);
		
		strQuery.append(
				"SELECT r.*" +
				" FROM resolutions r" +
				" WHERE r.id IN ("
					+ " SELECT ced.device_id"
					+ " FROM charts c"
					+ " JOIN charts_chart_entries  cce ON (c.id = cce.chart_id)"
					+ " JOIN chart_entries ce ON (ce.id = cce.chart_entry_id)"
					+ " JOIN chart_entries_devices ced ON (ced.chart_entry_id=ce.id)"
					+ " WHERE c.session_id = :sessionId"
					+ " AND c.device_type =:deviceTypeId" 
					+ " AND c.locale = :locale" 
					+ " AND ce.member_id =:memberId)"
					+ " ORDER BY r.number ASC");
		
		return strQuery.toString();
	}
	
	private String isProcessedQuestionQuery(final Session session, 
			final Group group,
			final Date answeringDate, 
			final DeviceType deviceType,
			final String excludeInternalStatus,
			final String locale) {
		/*CustomParameter parameter =
            CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		String date = 
			FormaterUtil.formatDateToString(answeringDate, parameter.getValue());*/
		
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(
			"SELECT COUNT(q)" +
			" FROM Question q" +
			" WHERE q.id IN" +	
	        	" (SELECT d.id " +
	        	" FROM Chart c JOIN c.chartEntries ce JOIN ce.devices d" +
	        	" WHERE c.session.id = :sessionId" +
	        	" AND c.group.id = :groupId" +
	        	" AND c.answeringDate = :answeringDate" +
	           	" AND c.deviceType.id=:deviceTypeId" +
	           	" AND c.locale =:locale)" +
	           	" AND q.internalStatus.type = :excludeInternalStatus)");
		
		return strQuery.toString();
	}
	
	private String isProcessedResolutionQuery(final Session session,
			final DeviceType deviceType, 
			final String excludeInternalStatus,
			final String locale) {
		String strHouseType = session.getHouse().getType().getType();
		StringBuffer strQuery = new StringBuffer();
		
		if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
//			strQuery.append( 
//				"SELECT COUNT(r)" +
//				" FROM Resolution r" +
//				" WHERE r.id IN"+	
//	           		" (SELECT d.id" +
//	           		" FROM Chart c JOIN c.chartEntries ce JOIN ce.device d" +
//	           		" WHERE c.session.id = :sessionId" +
//	           		" AND c.deviceType.id=:deviceTypeId" +
//	           		" AND c.locale =:locale)" +
//	           		" AND r.internalStatusLowerHouse.type = :excludeInternalStatus)");
			strQuery.append(
					"SELECT COUNT(DISTINCT r.id)" +
					" FROM resolutions r" +
					" JOIN status ista ON (ista.id=r.lowerhouse_internalstatus_id)" +
					" WHERE r.id IN ("
						+ " SELECT ced.device_id"
						+ " FROM charts c"
						+ " JOIN charts_chart_entries  cce ON (c.id = cce.chart_id)"
						+ " JOIN chart_entries ce ON (ce.id = cce.chart_entry_id)"
						+ " JOIN chart_entries_devices ced ON (ced.chart_entry_id=ce.id)"
						+ " WHERE c.session_id = :sessionId"
						+ " AND c.device_type =:deviceTypeId" 
						+ " AND c.locale = :locale)"
						+ " AND ista.type = :excludeInternalStatus)");
		}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
//			strQuery.append(
//				"SELECT COUNT(r)" +
//				" FROM Resolution r" +
//				" WHERE r.id IN" +	
//	           		" (SELECT d.id" +
//	           		" FROM Chart c JOIN c.chartEntries ce JOIN ce.device d" +
//	           		" WHERE c.session.id =:session.Id" +
//	           		" AND c.deviceType.id=:deviceType.Id" +
//	           		" AND c.locale =:locale)" +
//	           		" AND r.internalStatusUpperHouse.type = :excludeInternalStatus)");
			strQuery.append(
					"SELECT COUNT(DISTINCT r.id)" +
					" FROM resolutions r" +
					" JOIN status ista ON (ista.id=r.upperhouse_internalstatus_id)" +
					" WHERE r.id IN ("
						+ " SELECT ced.device_id"
						+ " FROM charts c"
						+ " JOIN charts_chart_entries  cce ON (c.id = cce.chart_id)"
						+ " JOIN chart_entries ce ON (ce.id = cce.chart_entry_id)"
						+ " JOIN chart_entries_devices ced ON (ced.chart_entry_id=ce.id)"
						+ " WHERE c.session_id = :sessionId"
						+ " AND c.device_type =:deviceTypeId" 
						+ " AND c.locale = :locale"
						+ " AND ista.type = :excludeInternalStatus)");
		}
		
		return strQuery.toString();
	}

	private String isProcessedHDSQuery(final Session session,
			final DeviceType deviceType, 
			final String excludeInternalStatus,
			final String locale) {
		
		StringBuffer strQuery = new StringBuffer();
		
		strQuery.append(
				"SELECT COUNT(q)" +
				" FROM Question q" +
				" WHERE q.id IN"+	
	           		" (SELECT d.id" +
	           		" FROM Chart c JOIN c.chartEntries ce" +
	           		" JOIN ce.device d" +
	           		" WHERE c.session.id = :sessionId" +
	           		" AND c.deviceType.id=:deviceTypeId" +
	           		" AND c.locale =:locale)" +
	           		" AND q.internalStatus.type = :excludeInternalStatus)");
		
		return strQuery.toString();
	}
	
	private String findMembersResolutionsCountQuery(final Member member,
			final Session session, 
			final DeviceType deviceType, 
			final Status[] includeStatuses,
			final String locale) {
		HouseType houseType = session.getHouse().getType();
		
		StringBuffer strQuery = new StringBuffer();
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
//			strQuery.append(
//				"SELECT COUNT(r)" +
//				" FROM Resolution r" +
//				" WHERE r.id IN"+	
//	           		" (SELECT d.id" +
//	           		" FROM Chart c JOIN c.chartEntries ce JOIN ce.devices d" +
//	           		" WHERE c.session.id = :sessionId" +
//	           		" AND c.deviceType.id= :deviceTypeId" +
//	           		" AND c.locale = :locale" +
//	           		" AND ce.member.id = :memberId)");
			strQuery.append(
					"SELECT COUNT(DISTINCT r.id)" +
					" FROM resolutions r" +
					" WHERE r.id IN ("
						+ " SELECT ced.device_id"
						+ " FROM charts c"
						+ " JOIN charts_chart_entries  cce ON (c.id = cce.chart_id)"
						+ " JOIN chart_entries ce ON (ce.id = cce.chart_entry_id)"
						+ " JOIN chart_entries_devices ced ON (ced.chart_entry_id=ce.id)"
						+ " WHERE c.session_id = :sessionId"
						+ " AND c.device_type =:deviceTypeId" 
						+ " AND c.locale = :locale" 
						+ " AND ce.member_id =:memberId)");
			
			strQuery.append(this.getResolutionStatusFilters(houseType, includeStatuses));
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
//			strQuery.append(
//				"SELECT COUNT(r)" +
//				" FROM Resolution r" +
//				" WHERE r.id IN" +	
//	           		" (SELECT d.id" +
//	           		" FROM Chart c JOIN c.chartEntries ce JOIN ce.devices d" +
//	           		" WHERE c.session.id = :sessionId" +
//	           		" AND c.deviceType.id=:deviceTypeId" +
//	           		" AND c.locale = :locale" +
//	           		" AND ce.member.id = :memberId)");
			strQuery.append(
					"SELECT COUNT(DISTINCT r.id)" +
					" FROM resolutions r" +
					" WHERE r.id IN ("
						+ " SELECT ced.device_id"
						+ " FROM charts c"
						+ " JOIN charts_chart_entries  cce ON (c.id = cce.chart_id)"
						+ " JOIN chart_entries ce ON (ce.id = cce.chart_entry_id)"
						+ " JOIN chart_entries_devices ced ON (ced.chart_entry_id=ce.id)"
						+ " WHERE c.session_id = :sessionId"
						+ " AND c.device_type =:deviceTypeId" 
						+ " AND c.locale = :locale" 
						+ " AND ce.member_id =:memberId)");
			
			strQuery.append(this.getResolutionStatusFilters(houseType, includeStatuses));
		}
		
		return strQuery.toString();
	}
	
	private String getResolutionStatusFilters(final HouseType houseType, 
			final Status[] statuses) {
		StringBuffer sb = new StringBuffer();
		
		int n = statuses.length;
		if(n > 0) {
			sb.append(" AND(");
			
			if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				for(int i = 0; i < n; i++) {
					sb.append(" r.upperhouse_internalstatus_id = " + statuses[i].getId());
					if(i < n - 1) {
						sb.append(" OR");
					}
				}
			}
			else if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				for(int i = 0; i < n; i++) {
					sb.append(" r.lowerhouse_internalstatus_id = " + statuses[i].getId());
					if(i < n - 1) {
						sb.append(" OR");
					}
				}
			}
			
			sb.append(")");
		}

		return sb.toString();
	}
		
	private String findMembersQuestionsCountQuery(final Member member,
			final Session session, 
			final DeviceType deviceType, 
			final Status[] includeStatuses,
			final String locale) {
		
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(
			"SELECT COUNT(q)" +
			" FROM Question q" +
			" WHERE q.id IN"+	
           		" (SELECT d.id" +
           		" FROM Chart c JOIN c.chartEntries ce JOIN ce.devices d" +
           		" WHERE c.session.id = :sessionId" +
           		" AND c.deviceType.id= :deviceTypeId" +
           		" AND c.locale = :locale" +
           		" AND ce.member.id = :memberId)");
		strQuery.append(this.getQuestionStatusFilters(includeStatuses));
		
		return strQuery.toString();
	}
	
	private String getQuestionStatusFilters(final Status[] statuses) {
		StringBuffer sb = new StringBuffer();
		
		int n = statuses.length;
		if(n > 0) {
			sb.append(" AND (");
			for(int i = 0; i < n; i++) {
				sb.append(" q.internalStatus.id = " + statuses[i].getId());
				if(i < n - 1) {
					sb.append(" OR ");
				}
			}			
			sb.append(")");
		}

		return sb.toString();
	}
	
	public ChartEntry find(Chart chart, Member primaryMember) {
		String strQuery="SELECT cce"+
				" FROM Chart AS c JOIN c.chartEntries AS cce "+
				" WHERE cce.member.id=:memberId" +
				" AND c.id=:chartId";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("memberId", primaryMember.getId());
		query.setParameter("chartId", chart.getId());
		try{
			ChartEntry chartEntry=(ChartEntry) query.getSingleResult();
			return chartEntry;
		}catch(Exception e){
			return null;
		}
	}	
	
	private String findMembersStandalonesCountQuery(final Member member,
			final Session session, 
			final DeviceType deviceType, 
			final Status[] includeStatuses,
			final String locale) {
		
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(
			"SELECT COUNT(DISTINCT q.id)" +
			" FROM standalone_motions q" +
			" WHERE q.id IN"+	
           		" (SELECT ced.device_id" +
           		" FROM charts c " +
           		" INNER JOIN charts_chart_entries cce ON(cce.chart_id=c.id)" +
           		" INNER JOIN chart_entries ce ON(ce.id=cce.chart_entry_id)" +
           		" INNER JOIN chart_entries_devices ced ON(ced.chart_entry_id=ce.id)" +
           		" WHERE c.session_id = :sessionId" +
           		" AND c.device_type= :deviceTypeId" +
           		" AND c.locale = :locale" +
           		" AND ce.member_id = :memberId)");
		strQuery.append(this.getNativeDeviceStatusFilter(includeStatuses));
		
		return strQuery.toString();
	}
	
	private String getNativeDeviceStatusFilter(Status[] statuses){
		StringBuffer sb = new StringBuffer();
		
		int n = statuses.length;
		if(n > 0) {
			sb.append(" AND (");
			for(int i = 0; i < n; i++) {
				sb.append(" q.internalstatus_id = " + statuses[i].getId());
				if(i < n - 1) {
					sb.append(" OR ");
				}
			}			
			sb.append(")");
		}

		return sb.toString();
	}
	
	public List<Device> find(final ChartEntry ce) throws ELSException{
		List<Device> devices = new ArrayList<Device>();
		String strQuery = "SELECT device_id FROM chart_entries_devices WHERE chart_entry_id=:chartEntryId";
		Query query = this.em().createNativeQuery(strQuery);
		query.setParameter("chartEntryId", ce.getId());
		List<BigInteger> deviceIds = query.getResultList();
		for(BigInteger be : deviceIds){
			Question question = Question.findById(Question.class, Long.parseLong(be.toString()));
			devices.add(question);
		}
		return devices;
		
	}
	
	public List<Device> findDevicesWithChartEntry(final ChartEntry ce, final String deviceClass) throws ELSException{
		List<Device> devices = new ArrayList<Device>();
		String strQuery = "SELECT device_id FROM chart_entries_devices WHERE chart_entry_id=:chartEntryId";
		Query query = this.em().createNativeQuery(strQuery);
		query.setParameter("chartEntryId", ce.getId());
		List<BigInteger> deviceIds = query.getResultList();
		if(deviceClass!=null && !deviceClass.isEmpty()) {
			if(deviceClass.equalsIgnoreCase(ApplicationConstants.QUESTION)) {
				for(BigInteger be : deviceIds){
					Question question = Question.findById(Question.class, Long.parseLong(be.toString()));
					devices.add(question);
				}
			}
			else if(deviceClass.equalsIgnoreCase(ApplicationConstants.RESOLUTION)) {
				for(BigInteger be : deviceIds){
					Resolution resolution = Resolution.findById(Resolution.class, Long.parseLong(be.toString()));
					devices.add(resolution);
				}
			}
		}		
		return devices;
		
	}
	
	public String findNextEligibleChartQuestionDetailsOnGroupChange(final Chart chart, final Member member) {
		String strQuery = "SELECT CONCAT(c.id, '~', q.id) AS chart_question_details FROM charts c"
				+ " INNER JOIN charts_chart_entries cce ON (cce.chart_id = c.id)"
				+ " INNER JOIN chart_entries ce ON (cce.chart_entry_id = ce.id)"
				+ " INNER JOIN chart_entries_devices ced ON (ced.chart_entry_id=ce.id)"
				+ " INNER JOIN questions q ON (q.id=ced.device_id)"
				+ " INNER JOIN status ista ON (ista.id=q.internalstatus_id)"
				+ " LEFT JOIN question_dates qad ON (qad.id=q.answering_date)"
				+ " WHERE c.session_id=:sessionId"
				+ " AND c.device_type=:deviceTypeId"
				+ " AND c.group_id=:groupId"
				+ " AND c.answering_date>:answeringDate"
				+ " AND ce.member_id=:memberId"
				+ " AND DATE(q.submission_date)<=(SELECT final_submission_date FROM question_dates WHERE group_id=:groupId AND answering_date=:answeringDate)"
				+ " AND (q.answering_date IS NULL OR (q.answering_date IS NOT NULL AND qad.answering_date<=:answeringDate))"
				+ " AND q.parent IS NULL"
				+ " AND ista.type LIKE :readyToBePutUp"
				+ " ORDER BY q.number ASC LIMIT 1";
		
		Query query = this.em().createNativeQuery(strQuery);
		query.setParameter("sessionId", chart.getSession().getId());
		query.setParameter("deviceTypeId", chart.getDeviceType().getId());
		query.setParameter("groupId", chart.getGroup().getId());
		query.setParameter("answeringDate", chart.getAnsweringDate());		
		query.setParameter("memberId", member.getId());
//		Status putupStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, chart.getLocale());
//		query.setParameter("readyToBePutUp", putupStatus.getId());
		query.setParameter("readyToBePutUp", "%" + ApplicationConstants.STATUS_SYSTEM_PUTUP);
		
		@SuppressWarnings("unchecked")
		List<String> resultList = query.getResultList();
		if(resultList!=null && !resultList.isEmpty()) {
			String result = resultList.get(0);
			return result;
		} else {
			return null;
		}
	}
}