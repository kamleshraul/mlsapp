/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Status.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class Status.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "status")
@JsonIgnoreProperties({"priority","locale","version","versionMismatch"})
public class Status extends BaseDomain implements Serializable{
	// ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The type. */
    @Column(length = 150)
    private String type;

    /** The name. */
    @Column(length=600)
    private String name;
    
    private Integer priority;
	
	private Integer supportOrder;

    /** The status repository. */
    @Autowired
    private transient StatusRepository statusRepository;
 // ---------------------------------Constructors----------------------------------------------

	/**
  * Instantiates a new status.
  */
 public Status() {
		super();
	}

	/**
	 * Instantiates a new status.
	 *
	 * @param type the type
	 * @param name the name
	 */
	public Status(final String type, final String name) {
		super();
		this.type = type;
		this.name = name;
	}
	// -------------------------------Domain_Methods----------------------------------------------
	/**
	 * Gets the status repository.
	 *
	 * @return the status repository
	 */
	public static StatusRepository getStatusRepository() {
	    StatusRepository statusRepository = new Status().statusRepository;
        if (statusRepository == null) {
            throw new IllegalStateException(
                    "StatusRepository has not been injected in Status Domain");
        }
        return statusRepository;
    }

    /**
     * Find starting with.
     *
     * @param pattern the pattern
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     * @throws ELSException 
     */
    public static List<Status> findStartingWith(final String pattern,final String sortBy,final String sortOrder,final String locale) throws ELSException{
        return getStatusRepository().findStartingWith(pattern,sortBy,sortOrder,locale);
    }

    /**
     * Find by type.
     *
     * @param typeName the type name
     * @param locale the locale
     * @return the status
     */
    public static Status findByType(final String typeName, final String locale) {
		return Status.findByFieldName(Status.class, "type", typeName, locale);
	}
    // ------------------------------------------Getters/Setters-----------------------------------
	/**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	public static List<Status> findAssistantQuestionStatus(final String sortBY,final String sortOrder,
			final String locale) throws ELSException {
		return getStatusRepository().findAssistantQuestionStatus(sortBY,sortOrder,locale);
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getSupportOrder() {
		return supportOrder;
	}

	public void setSupportOrder(Integer supportOrder) {
		this.supportOrder = supportOrder;
	}

	public static List<Status> findStatusContainedIn(final String commadelimitedStatusTypes,final String locale) throws ELSException {
		return getStatusRepository().findStatusContainedIn(commadelimitedStatusTypes, locale);
	}
	
	public static List<Status> findStatusContainedIn(final String commadelimitedStatusTypes,final String locale, final String sortOrder) {
		return getStatusRepository().findStatusContainedIn(commadelimitedStatusTypes, locale, sortOrder);
	}
	
	public static List<Status> findStatusWithSupportOrderContainedIn(final String commadelimitedStatusTypes,final String locale) throws ELSException {
		return getStatusRepository().findStatusWithSupportOrderContainedIn(commadelimitedStatusTypes, locale);
	}

}
