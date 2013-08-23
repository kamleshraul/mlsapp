/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.Rule.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.common.vo;

import java.text.ParseException;
import java.util.Date;

import org.mkcl.els.common.util.FormaterUtil;

/**
 * The Class Rule.
 *
 * @author vishals
 * @version v1.0.0
 */
public class Rule {

    /** The field. */
    private String field;

    /** The op. */
    private String op;

    /** The data. */
    private String data;

    /**
     * Instantiates a new rule.
     */
    public Rule() {
    }

    /**
     * Instantiates a new rule.
     *
     * @param field the field
     * @param op the op
     * @param data the data
     */
    public Rule(final String field, final String op, final String data) {
        super();
        this.field = field;
        this.op = op;
        this.data = data;

    }

    /**
     * Gets the field.
     *
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * Sets the field.
     *
     * @param field the new field
     */
    public void setField(final String field) {
        this.field = field;
    }

    /**
     * Gets the op.
     *
     * @return the op
     */
    public String getOp() {
        return op;
    }

    /**
     * Sets the op.
     *
     * @param op the new op
     */
    public void setOp(final String op) {
        this.op = op;
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public String getData() {
        if(getField().endsWith("Date")){
            try {
                Date dateInDDMMYYYY=FormaterUtil.getDateFormatter("dd/MM/yyyy", "en_US").parse(data);
                String dateInYYYYMMDD=FormaterUtil.getDateFormatter("yyyy-MM-dd", "en_US").format(dateInDDMMYYYY);
                this.data=dateInYYYYMMDD;
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * Sets the data.
     *
     * @param data the new data
     */
    public void setData(final String data) {
        this.data = data;
    }

}
