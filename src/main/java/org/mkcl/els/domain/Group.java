/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.GroupInformation.java
 * Created On: 19 Jun, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
import org.mkcl.els.common.vo.QuestionDatesVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class GroupInformation.
 *
 * @author Dhananjay
 * @since v1.1.0
 */
@Configurable
@Entity
@Table(name = "groups")
@JsonIgnoreProperties({"questionDates","ministries"})
public class Group extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The house type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="housetype_id")
    private HouseType houseType;

    /** The year. */
    @Column(name="group_year")
    private Integer year;

    /** The session type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sessiontype_id")
    private SessionType sessionType;

    /** The group. */
    private Integer number;

        /** The ministries. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "groups_ministries",
    joinColumns = @JoinColumn(name = "group_id",
    referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "ministry_id",
    referencedColumnName = "id"))
    private List<Ministry> ministries;

    /** The question dates. */
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private List<QuestionDates> questionDates;

    @Autowired
    private transient GroupRepository groupRepository;

    // ---------------------------------Constructors----------------------//

    public Group() {
		super();
	}

    public static GroupRepository getGroupRepository() {
    	GroupRepository groupRepository = new Group().groupRepository;
        if (groupRepository == null) {
            throw new IllegalStateException(
                    "GroupRepository has not been injected in Group Domain");
        }
        return groupRepository;
    }
    // ----------------------------Domain Methods-------------------------//

    public static List<Group> findByHouseTypeSessionTypeYear(
			final HouseType houseType, final SessionType sessionType, final Integer sessionYear) throws ELSException {
		return getGroupRepository().findByHouseTypeSessionTypeYear(
				houseType,sessionType,sessionYear);
	}



	public static List<String> findAnsweringDates(final Long id, final String locale) throws ELSException {
		return getGroupRepository().findAnsweringDates(id, locale);
	}

	 public static Group findByNumberHouseTypeSessionTypeYear(final Integer groupNumber,
	            final HouseType houseType, final SessionType sessionType, final Integer year) throws ELSException {
	        return getGroupRepository().findByNumberHouseTypeSessionTypeYear(groupNumber,
	                houseType, sessionType, year);
	    }

	 public static Group find(final Ministry ministry, final Session session, final String locale) throws ELSException {
			HouseType houseType = session.getHouse().getType();
			Integer year = session.getYear();
			SessionType sessionType = session.getType();
			return getGroupRepository().find(ministry, houseType, year, sessionType, locale);
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
						CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
					String formatType = parameter.getValue();
					String strDate = FormaterUtil.formatDateToString(date, formatType);
					String newStrDate = strDate.replaceFirst("00:00:00", "23:59:59");
					Date submissionDate =
						FormaterUtil.formatStringToDate(newStrDate, formatType);
					return submissionDate;
				}
			}
			return null;
		}

		//To have get the rotation order (question_dates) of the group
		 public  QuestionDates findQuestionDatesByGroupAndAnsweringDate(final Date answeringDate){
			 //Group group=Group.findById(Group.class, groupId);
			 List<QuestionDates> questionDates=this.getQuestionDates();
			 for(QuestionDates q:questionDates){
				 if(q.getAnsweringDate().equals(answeringDate)){
					 return q;
				 }
			 }
			 return null;
	    }



		 public static List<QuestionDatesVO> findAllGroupDatesFormatted(final HouseType houseType,
		            final SessionType sessionType, final Integer sessionYear, final String string) {
		        return getGroupRepository().findAllGroupDatesFormatted(houseType,
		               sessionType,sessionYear,string);
		    }
    // ----------------------------Getters/Setters------------------------//
	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(final HouseType houseType) {
		this.houseType = houseType;
	}



    public Integer getYear() {
        return year;
    }


    public void setYear(final Integer year) {
        this.year = year;
    }

    public SessionType getSessionType() {
		return sessionType;
	}

	public void setSessionType(final SessionType sessionType) {
		this.sessionType = sessionType;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(final Integer number) {
		this.number = number;
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

    public static Group find(final Ministry ministry, final HouseType houseType,
            final Integer sessionYear, final SessionType sessionType, final String locale) throws ELSException {
        return getGroupRepository().find(ministry,houseType,sessionYear,sessionType,locale);
    }   

    public static List<MasterVO> findQuestionDateByGroup(final HouseType houseType,final SessionType sessionType,
            final Integer sessionYear,final Integer groupNumber,final String locale) throws ELSException {
        return getGroupRepository().findQuestionDateByGroup(houseType,sessionType,sessionYear,groupNumber,locale);
    }   

	public static List<Ministry> findMinistriesByName(final Long groupid) throws ELSException {
		return getGroupRepository().findMinistriesByName(groupid);
	}
	
	public static List<Ministry> findMinistriesByPriority(final Long groupid) throws ELSException {
		return getGroupRepository().findMinistriesByPriority(groupid);
	}	
	
	public static List<Ministry> findMinistriesByPriority(final Group group) throws ELSException {
		return getGroupRepository().findMinistriesByPriority(group);
	}
	
	/**** Used In Jsp of Member Ballot Member Wise Report 
	 * @throws ELSException ****/
	public  List<Ministry> findMinistriesByPriority() throws ELSException {
		if(getId()!=null){
		return getGroupRepository().findMinistriesByPriority(getId());
		}else{
			return new ArrayList<Ministry>();
		}
	}
	/**** Used In Jsp of Member Ballot Member Wise Report 
	 * @throws ELSException ****/
	 public  List<MasterVO> findQuestionDateByGroup() throws ELSException {
	    	if(getHouseType()!=null&&getSessionType()!=null&&getYear()!=null&&getNumber()!=null&&getLocale()!=null){
	        return getGroupRepository().findQuestionDateByGroup(getHouseType(),getSessionType(),
	               getYear(),getNumber(),getLocale());
	    	}else{
	    		return new ArrayList<MasterVO>();
	    	}
	 }	 
	 public  List<Reference> findQuestionDateReferenceVOByGroup() throws ELSException {
	    	if(getHouseType()!=null&&getSessionType()!=null&&getYear()!=null&&getNumber()!=null&&getLocale()!=null){
	        return getGroupRepository().findQuestionDateReferenceVOByGroup(getHouseType(),getSessionType(),
	               getYear(),getNumber(),getLocale());
	    	}else{
	    		return new ArrayList<Reference>();
	    	}
	 }
	 /**** Used in various jsps ****/
	 public String formatNumber(){
		 if(getNumber()!=null){
	        NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
	        return format.format(this.getNumber());
		 }else{
			 return "";
		 }
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
		return Group.getGroupRepository().find(session, answeringDate, locale);
	}
	
	public static List<Ministry> findMinistriesInGroupsForSessionExcludingGivenGroup(final HouseType houseType, final SessionType sessionType, final Integer sessionYear, final Integer groupNumber, final String locale) throws ELSException {
		return getGroupRepository().findMinistriesInGroupsForSessionExcludingGivenGroup(houseType,sessionType,sessionYear,groupNumber,locale);
    }	
	
	public static List<Ministry> findMinistriesInGroupsForSession(final HouseType houseType, final SessionType sessionType, final Integer sessionYear, final String locale) throws ELSException {
		return getGroupRepository().findMinistriesInGroupsForSession(houseType,sessionType,sessionYear,locale);
    }
	
	public static List<Integer> findGroupNumbersForSessionExcludingGivenGroup(final HouseType houseType, final SessionType sessionType, final Integer sessionYear, final Integer groupNumber, final String locale) throws ELSException {
		return getGroupRepository().findGroupNumbersForSessionExcludingGivenGroup(houseType,sessionType,sessionYear,groupNumber,locale);
    }
	
	public static List<Integer> findGroupNumbersForSession(final HouseType houseType, final SessionType sessionType, final Integer sessionYear, final String locale) throws ELSException {
		return getGroupRepository().findGroupNumbersForSession(houseType,sessionType,sessionYear,locale);
    }
}
