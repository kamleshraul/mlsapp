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
@JsonIgnoreProperties({"ministries","questionDates"})
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
			final HouseType houseType, final SessionType sessionType, final Integer sessionYear) {
		return getGroupRepository().findByHouseTypeSessionTypeYear(
				houseType,sessionType,sessionYear);
	}



	public static List<String> findAnsweringDates(final Long id) {
		return getGroupRepository().findAnsweringDates(id);
	}

	 public static Group findByNumberHouseTypeSessionTypeYear(final Integer groupNumber,
	            final HouseType houseType, final SessionType sessionType, final Integer year) {
	        return getGroupRepository().findByNumberHouseTypeSessionTypeYear(groupNumber,
	                houseType, sessionType, year);
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



}
