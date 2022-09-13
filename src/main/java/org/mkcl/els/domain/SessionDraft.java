/*
 * 
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class SessionDraft.
 */
@Configurable
@Entity
@Table(name = "session_drafts")
public class SessionDraft extends BaseDomain implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;    

	/**************************************** Attributes *************************************/	
    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name="editedon")
    private Date editedOn; 
    
    /** The edited by. */
    @Column(length=1000)
    private String editedBy;

    /** The edited as. */
    @Column(length=1000)
    private String editedAs;
    
    /** The house. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    /** The year. */
    @Column(name="session_year")
    private Integer year;

    /** The type. */
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "sessiontype_id")
    private SessionType type;

    /** The place. */
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "sessionplace_id")
    private SessionPlace place;

    /** The number. */
    private Integer number;
    
    /** The tentative start date. */
    @Temporal(TemporalType.DATE)
    private Date tentativeStartDate;

    /** The tentative end date. */
    @Temporal(TemporalType.DATE)
    private Date tentativeEndDate;
    
    /** The actual start date. */
    @Temporal(TemporalType.DATE)
    private Date actualStartDate;

    /** The actual end date. */
    @Temporal(TemporalType.DATE)
    private Date actualEndDate;

    /** The start date. */
    @Temporal(TemporalType.DATE)
    private Date startDate;

    /** The end date. */
    @Temporal(TemporalType.DATE)
    private Date endDate;   

    /*
     * devices enabled for a session.This will be a list of enabled device type separated
     * by comma
     */
    /** The device types enabled. */
    @Column(length=1000)
    private String deviceTypesEnabled;
    
    /** The parameters. */
	@ElementCollection
    @MapKeyColumn(name="parameter_key")
    @Column(name="parameter_value",length=10000)
    @CollectionTable(name="sessiondraft_devicetype_config", joinColumns={@JoinColumn(name="sessiondraft_id", referencedColumnName="id")})
	private Map<String,String> parameters;
	
    
    /**************************************** Setters and Getters *************************************/	
	/**
	 * Gets the edited on.
	 *
	 * @return the edited on
	 */
	public Date getEditedOn() {
		return editedOn;
	}

	/**
	 * Sets the edited on.
	 *
	 * @param editedOn the new edited on
	 */
	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	/**
	 * Gets the edited by.
	 *
	 * @return the edited by
	 */
	public String getEditedBy() {
		return editedBy;
	}

	/**
	 * Sets the edited by.
	 *
	 * @param editedBy the new edited by
	 */
	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	/**
	 * Gets the edited as.
	 *
	 * @return the edited as
	 */
	public String getEditedAs() {
		return editedAs;
	}

	/**
	 * Sets the edited as.
	 *
	 * @param editedAs the new edited as
	 */
	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public SessionType getType() {
		return type;
	}

	public void setType(SessionType type) {
		this.type = type;
	}

	public SessionPlace getPlace() {
		return place;
	}

	public void setPlace(SessionPlace place) {
		this.place = place;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getTentativeStartDate() {
		return tentativeStartDate;
	}

	public void setTentativeStartDate(Date tentativeStartDate) {
		this.tentativeStartDate = tentativeStartDate;
	}

	public Date getTentativeEndDate() {
		return tentativeEndDate;
	}

	public void setTentativeEndDate(Date tentativeEndDate) {
		this.tentativeEndDate = tentativeEndDate;
	}

	public Date getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public Date getActualEndDate() {
		return actualEndDate;
	}

	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}
	
	public String getDeviceTypesEnabled() {
		return deviceTypesEnabled;
	}

	public void setDeviceTypesEnabled(String deviceTypesEnabled) {
		this.deviceTypesEnabled = deviceTypesEnabled;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}	
	
}