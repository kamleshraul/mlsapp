/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Group.java
 * Created On: 19 Jun, 2012
 */
package org.mkcl.els.domain;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MinistryVO;
import org.mkcl.els.common.vo.QuestionDatesVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author Dhananjay
 */
@Configurable
@Entity
@Table(name = "groups")
@JsonIgnoreProperties({"questionDates", "ministries", "subdepartments", "session"})
public class Group extends BaseDomain implements Serializable {

	private static final long serialVersionUID = -7996071320394227121L;

	//===============================================
	//
	//=============== ATTRIBUTES ====================
	//
	//===============================================
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="session_id")
    private Session session;

    @Column(name="number")
    private Integer number;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "groups_ministries", 
    		joinColumns = 
    			@JoinColumn(name = "group_id", referencedColumnName = "id"),
    		inverseJoinColumns = 
    			@JoinColumn(name = "ministry_id", referencedColumnName = "id"))
    private List<Ministry> ministries;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "groups_subdepartments", 
    		joinColumns = 
    			@JoinColumn(name = "group_id", referencedColumnName = "id"),
    		inverseJoinColumns = 
    			@JoinColumn(name = "subdepartment_id", referencedColumnName = "id"))
    private List<SubDepartment> subdepartments;
    
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private List<QuestionDates> questionDates;

    // Kept for backward compatibility. To be removed.
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="housetype_id")
    private HouseType houseType;

	// Kept for backward compatibility. To be removed.
    @Column(name="group_year")
    private Integer year;

    // Kept for backward compatibility. To be removed.
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sessiontype_id")
    private SessionType sessionType;

    @Autowired
    private transient GroupRepository groupRepository;
    
    
    //===============================================
	//
	//=============== CONSTRUCTORS ==================
	//
	//===============================================
    /**
	 * Do not use this constructor to create Group instances.
	 * This constructor is kept here because JPA needs an 
	 * Entity to have a default public Constructor.
	 */
    public Group() {
		super();
	}
    
    public Group(final Session session, 
    		final Integer number,
    		final String locale) {
    	super(locale);
    	this.setSession(session);
    	this.setNumber(number);
    	this.setMinistries(new ArrayList<Ministry>());
    	this.setQuestionDates(new ArrayList<QuestionDates>());
    }

    
    //===============================================
	//
	//=============== VIEW METHODS ==================
	//
	//===============================================
    public static List<QuestionDatesVO> findAllGroupDatesFormatted(
    		final HouseType houseType,
            final SessionType sessionType, 
            final Integer sessionYear, 
            final String str) {
		return Group.getRepository().findAllGroupDatesFormatted(houseType,
	               sessionType,sessionYear,str);
    }
    
    public static List<MasterVO> findQuestionDateByGroup(
    		final HouseType houseType,
    		final SessionType sessionType,
            final Integer sessionYear,
            final Integer groupNumber,
            final String locale) throws ELSException {
        return Group.getRepository().findQuestionDateByGroup(houseType, 
        		sessionType, sessionYear, groupNumber, locale);
    }
    
    /**
     * Used In Jsp of Member Ballot Member Wise Report
     */
	public  List<Ministry> findMinistriesByPriority() throws ELSException {
		Long groupId = this.getId();
		
		if(groupId != null){
			return Group.getRepository().findMinistriesByPriority(this);
		}
		else{
			return new ArrayList<Ministry>();
		}
	}
    
    /**
     * Used In Jsp of Member Ballot Member Wise Report
     */
	public  List<String> findMinistryDisplayNamesByPriority() throws ELSException {
		Long groupId = this.getId();
		
		if(groupId != null){
			return Group.getRepository().findMinistryDisplayNamesByPriority(this);
		}
		else{
			return new ArrayList<String>();
		}
	}
	
	/**
     * Used In Jsp of Member Ballot Member Wise Report
     */
	public  List<MasterVO> findQuestionDateByGroup() throws ELSException {
		Session session = this.getSession();
		Integer number = this.getNumber();
		String locale = this.getLocale();
		
		if(session != null && number != null && locale != null) {
			HouseType houseType = session.getHouse().getType();
			SessionType sessionType = session.getType();
			Integer year = session.getYear();
			
			return Group.getRepository().findQuestionDateByGroup(houseType,
					sessionType, year, number, locale);
	    }
		else {
	    	return new ArrayList<MasterVO>();
	    }
	}
	
	/**
	 * Used in various jsps
	 */
	public String formatNumber() {
		Integer number = this.getNumber();
		String locale = this.getLocale();
		
		if(number != null) {
			NumberFormat format = 
				FormaterUtil.getNumberFormatterNoGrouping(locale);
			return format.format(number);
		}
		else {
			return "";
		}
	}
	 
	public  List<Reference> findQuestionDateReferenceVOByGroup(
			) throws ELSException {
		Session session = this.getSession();
		Integer number = this.getNumber();
		String locale = this.getLocale();
		
		if(session != null && number != null && locale != null) {
			HouseType houseType = session.getHouse().getType();
			SessionType sessionType = session.getType();
			Integer year = session.getYear();
			
	        return Group.getRepository().findQuestionDateReferenceVOByGroup(
	        		houseType, sessionType, year, number, locale);
    	}
		else {
    		return new ArrayList<Reference>();
    	}
	}
 
    
    //===============================================
	//
	//=============== DOMAIN METHODS ================
	//
	//===============================================
    private static GroupRepository getRepository() {
    	GroupRepository groupRepository = new Group().groupRepository;
        if (groupRepository == null) {
            throw new IllegalStateException(
                    "GroupRepository has not been injected in Group Domain");
        }
        return groupRepository;
    }
    
    // Non static methods
    /**
	 * Returns null if @param answeringDate is not one of the
	 * answering dates mentioned for this Group.
	 */
	public Date getFinalSubmissionDate(final Date answeringDate) {
		List<QuestionDates> qDateList = this.getQuestionDates();
		
		for(QuestionDates qd : qDateList) {
			if(qd.getAnsweringDate().equals(answeringDate)) {
				Date date = qd.getFinalSubmissionDate();
				CustomParameter parameter =
					CustomParameter.findByName(CustomParameter.class, 
							"DB_TIMESTAMP", "");
				String formatType = parameter.getValue();
				String strDate = 
					FormaterUtil.formatDateToString(date, formatType);
				String newStrDate = strDate.replaceFirst("00:00:00", "23:59:59");
				Date submissionDate =
					FormaterUtil.formatStringToDate(newStrDate, formatType);
				return submissionDate;
			}
		}
		return null;
	}
	
    /**
	 * Returns an empty list if there are no answering dates.
	 */
	public List<Date> getAnsweringDates() {
		List<Date> answeringDates = new ArrayList<Date>();
		List<QuestionDates> qDateList = this.getQuestionDates();
		for(QuestionDates qd : qDateList) {
			answeringDates.add(qd.getAnsweringDate());
		}
		return answeringDates;
	}
	
	/**
	 * Returns a sorted list of answering dates sorted as per
	 * @param sortOrder
	 * OR
	 * Returns an empty list if there are no answering dates.
	 */
	public List<Date> getAnsweringDates(final String sortOrder) {
		List<Date> answeringDates = this.getAnsweringDates();
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			// No need to write a Comparator as Date class
			// has already implemented the Comparable interface.
			Collections.sort(answeringDates);
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			Comparator<Date> c = new Comparator<Date>() {

				@Override
				public int compare(final Date d1, final Date d2) {
					return d2.compareTo(d1);
				}
			};
			Collections.sort(answeringDates, c);
		}
		return answeringDates;
	}
	
	public QuestionDates findQuestionDatesByGroupAndAnsweringDate(
			final Date answeringDate){
		List<QuestionDates> questionDates=this.getQuestionDates();
		for(QuestionDates q:questionDates){
			if(q.getAnsweringDate().equals(answeringDate)){
				return q;
			}
		}
		return null;
    }
	
	public boolean isRotationOrderSet() {
		boolean isRotationOrderSet = false;
		List<QuestionDates> questionDates = this.getQuestionDates();
		if(questionDates != null && ! questionDates.isEmpty()) {
			isRotationOrderSet = true;
		}
		return isRotationOrderSet;
	}
	
    
    // Static methods
    public static Group find(final Ministry ministry, 
    		final Session session, 
    		final String locale) throws ELSException {
		HouseType houseType = session.getHouse().getType();
		Integer year = session.getYear();
		SessionType sessionType = session.getType();
		
		return Group.getRepository().find(ministry, houseType, 
				year, sessionType, locale);
	}
    
    public static Group find(final Ministry ministry, 
    		final HouseType houseType,
            final Integer sessionYear, 
            final SessionType sessionType, 
            final String locale) throws ELSException {
        return Group.getRepository().find(ministry, houseType, 
        		sessionYear, sessionType, locale);
    }
    
    /**
	 * Find Group based on @param session & where group has a
	 * QuestionDates object which has attribute 
	 * answeringDate = @param answeringDate. 
	 * 
	 * Returns null if such a Group does not exist.
	 * @throws ELSException 
	 */
	public static Group find(final Session session, 
			final Date answeringDate,
			final String locale) throws ELSException {
		return Group.getRepository().find(session, answeringDate, locale);
	}
    
    public static Group findByNumberHouseTypeSessionTypeYear(
    		final Integer groupNumber,
            final HouseType houseType, 
            final SessionType sessionType, 
            final Integer year) throws ELSException {
    	return Group.getRepository().findByNumberHouseTypeSessionTypeYear(
    			groupNumber, houseType, sessionType, year);
    }
    
    public static Group findByNumbersBySessionId(final Integer groupNumber, final String sessionId) throws ELSException {
    	return Group.getRepository().findByNumbersBySessionId(groupNumber, sessionId);
    }    
    
    public static List<Group> findByHouseTypeSessionTypeYear(
			final HouseType houseType, 
			final SessionType sessionType, 
			final Integer sessionYear) throws ELSException {
		return Group.getRepository().findByHouseTypeSessionTypeYear(
				houseType, sessionType, sessionYear);
	} 
    
    public static List<Group> findGroupsBySessionId(final String sessionId) throws ELSException{
    	return Group.getRepository().findGroupsBySessionId(sessionId);
    }
    
    public static List<Ministry> findMinistriesByName(
    		final Long groupid) throws ELSException {
		return Group.getRepository().findMinistriesByName(groupid);
	}
    
    public static List<Ministry> findMinistriesByNameGroupList(
    		final String[] groupids) throws ELSException {
		return Group.getRepository().findMinistriesByNameGroupList(groupids);
	}
    
    public static List<SubDepartment> findSubdepartmentsByName(
    		final Long groupid) throws ELSException {
		return Group.getRepository().findSubdepartmentsByName(groupid);
	}
	
	public static List<Ministry> findMinistriesByPriority(
			final Long groupid) throws ELSException {
		return Group.getRepository().findMinistriesByPriority(groupid);
	}	
	
	public static List<Ministry> findMinistriesByPriority(
			final Group group) throws ELSException {
		return Group.getRepository().findMinistriesByPriority(group);
	}
	
	public static List<MinistryVO> findMinistriesByMinisterView(
			final Group group, final String locale) throws ELSException {
		return Group.getRepository().findMinistriesByMinisterView(group, locale);
	}
	
	public static List<Ministry> findMinistriesInGroupsForSessionExcludingGivenGroup(
			final HouseType houseType, 
			final SessionType sessionType, 
			final Integer sessionYear, 
			final Integer groupNumber, 
			final String locale) throws ELSException {
		return Group.getRepository()
			.findMinistriesInGroupsForSessionExcludingGivenGroup(houseType, 
					sessionType, sessionYear, groupNumber, locale);
    }	
	
	public static List<Ministry> findMinistriesInGroupsForSession(
			final HouseType houseType, 
			final SessionType sessionType, 
			final Integer sessionYear, 
			final String locale) throws ELSException {
		return Group.getRepository().findMinistriesInGroupsForSession(
				houseType, sessionType, sessionYear, locale);
    }
	
    public static List<String> findAnsweringDates(final Long id, 
    		final String locale) throws ELSException {
		return Group.getRepository().findAnsweringDates(id, locale);
	}  
    
    public static List<Integer> findGroupNumbersForSessionExcludingGivenGroup(
    		final HouseType houseType, 
    		final SessionType sessionType, 
    		final Integer sessionYear, 
    		final Integer groupNumber, 
    		final String locale) throws ELSException {
		return Group.getRepository()
			.findGroupNumbersForSessionExcludingGivenGroup(houseType,
					sessionType, sessionYear, groupNumber, locale);
    }
    
    public static List<String> findGroupNumberByGroupId(final String[] groupIds) throws ELSException{
    	return Group.getRepository().findGroupNumberByGroupId(groupIds);
    }
	
	public static List<Integer> findGroupNumbersForSession(
			final HouseType houseType, 
			final SessionType sessionType, 
			final Integer sessionYear, 
			final String locale) throws ELSException {
		return Group.getRepository().findGroupNumbersForSession(houseType,
				sessionType, sessionYear, locale);
    }
	
	/**
	 * This method is the launchpad for the set of actions to be performed
	 * on devices when cabinet re-shuffle happens.
	 * @throws FileNotFoundException 
	 */
	public static void reshuffle(final Group group) throws ELSException {
		// Invoke the reshuffle method on all the concerned devices
		Question.onGroupReshuffle(group);
		
		// TODO: Invoke the same on other devices too. Ex: Resolution, Bill, Motion, etc
	}
	
	public static Group find(final SubDepartment subdepartment, 
			final Session session,
			final Locale locale) throws ELSException {
		return Group.getRepository().find(subdepartment, session, locale.toString());
	}
	
	public static Group find(final SubDepartment subdepartment, 
			final Session session,
			final String locale) throws ELSException {
		return Group.getRepository().find(subdepartment, session, locale);
	}
	
	public static Group findByAnsweringDateInHouseType(final Date answeringDate, final HouseType houseType) throws ELSException {
		return Group.getRepository().findByAnsweringDateInHouseType(answeringDate, houseType);
	}
	
	
	
	//===============================================
	//
	//=============== GETTERS/SETTERS ===============
	//
	//===============================================
	public Integer getNumber() {
		return number;
	}

	public void setNumber(final Integer number) {
		this.number = number;
	}
	
	public Session getSession() {
		return session;
	}
	
	public void setSession(Session session) {
		this.session = session;
	}

	public List<Ministry> getMinistries() {
		return ministries;
	}

	public void setMinistries(final List<Ministry> ministries) {
		this.ministries = ministries;
	}

	public List<QuestionDates> getQuestionDates() {
		return questionDates;
	}

	public void setQuestionDates(final List<QuestionDates> questionDates) {
		this.questionDates = questionDates;
	}

	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public SessionType getSessionType() {
		return sessionType;
	}

	public void setSessionType(SessionType sessionType) {
		this.sessionType = sessionType;
	}

	public List<SubDepartment> getSubdepartments() {
		return subdepartments;
	}

	public void setSubdepartments(List<SubDepartment> subdepartments) {
		this.subdepartments = subdepartments;
	}
}
