/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.CustomParameter.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mkcl.els.common.util.ApplicationConstants;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class CustomParameter.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "custom_parameters")
public class CustomParameter extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 1000)
    private String name;

    /** The value. */
    @Column(length = 500)
    private String value;

    /** The updateable. */
    private Boolean updateable;

    /** The description. */
    @Column(length = 2000)
    private String description;
    
    @Column(length = 2000)
    private String category;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new custom parameter.
     */
    public CustomParameter() {
        super();
    }

    /**
     * Instantiates a new custom parameter.
     *
     * @param name the name
     * @param value the value
     * @param updateable the updateable
     * @param description the description
     */
    public CustomParameter(final String name, final String value,
            final Boolean updateable, final String description, final String category) {
        super();
        this.name = name.toUpperCase();
        this.value = value.trim();
        this.updateable = updateable;
        this.description = description.trim();
        this.category = category.trim();
    }

    // -------------------------------Domain_Methods----------------------------------------------
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
    public static CustomParameter findByName(final Class persistenceClass,
    		final String fieldValue,
            final String locale) {
    	
    	if(fieldValue!=null && fieldValue.equalsIgnoreCase("DEPLOYMENT_SERVER")) {
    		
    		if(ApplicationConstants.environment!=null && ApplicationConstants.environment.acceptsProfiles("dev")) {
    			
    			return (CustomParameter) getBaseRepository().findByName(persistenceClass, "DEPLOYMENT_LOCAL_SERVER",
                        locale);
    			
    		} else {
    			return (CustomParameter) getBaseRepository().findByName(persistenceClass, "DEPLOYMENT_SERVER",
                        locale);
    		}
    		 
    	} else {
            return (CustomParameter) getBaseRepository().findByName(persistenceClass, fieldValue,
                    locale);
    	}
    }
    
    // ------------------------------------------Getters/Setters-----------------------------------
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
        this.name = name.toUpperCase();
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(final String value) {
        this.value = value.trim();
    }

    /**
     * Gets the updateable.
     *
     * @return the updateable
     */
    public Boolean getUpdateable() {
        return updateable;
    }

    /**
     * Sets the updateable.
     *
     * @param updateable the new updateable
     */
    public void setUpdateable(final Boolean updateable) {
        this.updateable = updateable;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(final String description) {
        this.description = description.trim();
    }

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
    
    
}
