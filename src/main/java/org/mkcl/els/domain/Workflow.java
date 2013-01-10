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
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="workflows")
@JsonIgnoreProperties({"deviceType"})
public class Workflow extends BaseDomain{

    /** The name. */
    @Column(length=1000)
    private String name;

    @Column(length=5000)
    private String type;

    @ManyToOne
    private DeviceType deviceType;

    public Workflow() {
        super();
    }

    public Workflow(final String name, final String type, final DeviceType deviceType) {
        super();
        this.name = name;
        this.type = type;
        this.deviceType = deviceType;
    }


    public String getName() {
        return name;
    }


    public void setName(final String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }


    public void setType(final String type) {
        this.type = type;
    }


    public DeviceType getDeviceType() {
        return deviceType;
    }


    public void setDeviceType(final DeviceType deviceType) {
        this.deviceType = deviceType;
    }
}
