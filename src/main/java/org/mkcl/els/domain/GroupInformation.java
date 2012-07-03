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
@Table(name = "group_information")
public class GroupInformation extends BaseDomain implements Serializable {
    
    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    /** The group. */
    @ManyToOne
    @JoinColumn(name="group_id")
    private Group group;  
    
    /** The house type. */
    @ManyToOne
    @JoinColumn(name="housetype_id")
    private HouseType houseType;
    
    /** The year. */
    @Column
    private Integer year;
    
    /** The session type. */
    @ManyToOne
    @JoinColumn(name="sessiontype_id")
    private SessionType sessionType;
    
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
    @JoinColumn(name = "group_information_id", referencedColumnName = "id")
    private List<QuestionDates> questionDates;

    // ---------------------------------Constructors----------------------//    
    /**
     * Instantiates a new group information.
     */
    public GroupInformation() {
	super();	
    }   

    // ----------------------------Domain Methods-------------------------//

    // ----------------------------Getters/Setters------------------------//
    /**
     * Gets the group.
     *
     * @return the group
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Sets the group.
     *
     * @param group the new group
     */
    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * Gets the house type.
     *
     * @return the house type
     */
    public HouseType getHouseType() {
        return houseType;
    }

    /**
     * Sets the house type.
     *
     * @param houseType the new house type
     */
    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }

    /**
     * Gets the year.
     *
     * @return the year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * Sets the year.
     *
     * @param year the new year
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * Gets the session type.
     *
     * @return the session type
     */
    public SessionType getSessionType() {
        return sessionType;
    }

    /**
     * Sets the session type.
     *
     * @param sessionType the new session type
     */
    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    /**
     * Gets the ministries.
     *
     * @return the ministries
     */
    public List<Ministry> getMinistries() {
        return ministries;
    }

    /**
     * Sets the ministries.
     *
     * @param ministries the new ministries
     */
    public void setMinistries(List<Ministry> ministries) {
        this.ministries = ministries;
    }
    
    /**
     * Gets the question dates.
     *
     * @return the question dates
     */
    public List<QuestionDates> getQuestionDates() {
        return questionDates;
    }

    /**
     * Sets the question dates.
     *
     * @param questionDates the new question dates
     */
    public void setQuestionDates(List<QuestionDates> questionDates) {
        this.questionDates = questionDates;
    }

}
