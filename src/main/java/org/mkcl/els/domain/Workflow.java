/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Workflow.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class Workflow.
 *
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="workflows")
public class Workflow extends BaseDomain{

    /** The name. */
    @Column(length=1000)
    private String name;

    /** The type. */
    @Column(length=5000)
    private String type;
    
    /**
     * Instantiates a new workflow.
     */
    public Workflow() {
        super();
    }

    /**
     * Instantiates a new workflow.
     *
     * @param name the name
     * @param type the type
     * @param deviceType the device type
     */
    public Workflow(final String name, final String type) {
        super();
        this.name = name;
        this.type = type;
    }
    
    public static Workflow findByType(final String typeName, final String locale) {
		return Workflow.findByFieldName(Workflow.class, "type", typeName, locale);
	}

    public static Workflow findByStatus(final Status status, final String locale) throws ELSException {
    	if(status==null) {
    		throw new ELSException("Workflow_findByStatus", "STATUS_NOT_SET");
    	}
    	String[] temp=status.getType().split("_");
		String workflowType=temp[temp.length-1]+"_workflow";
		return Workflow.findByType(workflowType, locale);	
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

    }
