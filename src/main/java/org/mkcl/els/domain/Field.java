/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Field.java
 * Created On: Mar 8, 2012
 */

package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Field.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "fields")
public class Field extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 50)
    private String name;

    /** The detail. */
    @Column(length = 100)
    private String detail;

    /** The mandatory. */
    @Column(length = 50)
    private String mandatory = "OPTIONAL";

    /** The visible. */
    @Column(length = 50)
    private String visible = "HIDDEN";

    /** The position. */
    private Integer position;

    /** The hint. */
    @Column(length = 100)
    private String hint;

    /** The form. */
    @Column(length = 50)
    private String form;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new field.
     */
    public Field() {
        super();
    }

    /**
     * Instantiates a new field.
     *
     * @param name the name
     * @param detail the detail
     * @param mandatory the mandatory
     * @param visible the visible
     * @param position the position
     * @param hint the hint
     * @param form the form
     * @param version the version
     * @param locale the locale
     */
    public Field(final String name,
            final String detail,
            final String mandatory,
            final String visible,
            final Integer position,
            final String hint,
            final String form,
            final Long version,
            final String locale) {
        super();
        this.name = name;
        this.detail = detail;
        this.mandatory = mandatory;
        this.visible = visible;
        this.position = position;
        this.hint = hint;
        this.form = form;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    /**
     * Find by name and form.
     *
     * @param name the name
     * @param form the form
     * @return the field
     * @author nileshp
     * @since v1.0.0
     */
    public Field findByNameAndForm(final String name, final String form) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", name);
        map.put("form", form);
        return findByFieldNames(Field.class, map, "");
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
        this.name = name;
    }

    /**
     * Gets the detail.
     *
     * @return the detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Sets the detail.
     *
     * @param detail the new detail
     */
    public void setDetail(final String detail) {
        this.detail = detail;
    }

    /**
     * Gets the mandatory.
     *
     * @return the mandatory
     */
    public String getMandatory() {
        return mandatory;
    }

    /**
     * Sets the mandatory.
     *
     * @param mandatory the new mandatory
     */
    public void setMandatory(final String mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * Gets the visible.
     *
     * @return the visible
     */
    public String getVisible() {
        return visible;
    }

    /**
     * Sets the visible.
     *
     * @param visible the new visible
     */
    public void setVisible(final String visible) {
        this.visible = visible;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * Sets the position.
     *
     * @param position the new position
     */
    public void setPosition(final Integer position) {
        this.position = position;
    }

    /**
     * Gets the hint.
     *
     * @return the hint
     */
    public String getHint() {
        return hint;
    }

    /**
     * Sets the hint.
     *
     * @param hint the new hint
     */
    public void setHint(final String hint) {
        this.hint = hint;
    }

    /**
     * Gets the form.
     *
     * @return the form
     */
    public String getForm() {
        return form;
    }

    /**
     * Sets the form.
     *
     * @param form the new form
     */
    public void setForm(final String form) {
        this.form = form;
    }
}
