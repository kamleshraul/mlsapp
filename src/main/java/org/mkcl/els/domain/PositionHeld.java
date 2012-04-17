/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.PositionHeld.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class PositionHeld.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "positions_held")
public class PositionHeld extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    //This is changed a/c to suggestion by ajay sir.
    /** The from date. */
    @Column(length=30)
    private String fromDate;

    /** The to date. */
    @Column(length=30)
    private String toDate;

    /** The position. */
    @Column(length = 1000)
    private String position;

    @ManyToMany
    @JoinTable(name = "members_positionsheld",
            joinColumns = { @JoinColumn(name = "positionheld_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "member_id",
                    referencedColumnName = "id") })
    private List<Member> members;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new position held.
     */
    public PositionHeld() {
        super();
    }

    /**
     * Instantiates a new position held.
     *
     * @param fromDate the from date
     * @param toDate the to date
     * @param position the position
     */
    public PositionHeld(final String fromDate, final String toDate, final String position) {
        super();
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.position = position;
    }

    // -------------------------------Domain_Methods----------------------------------------------
//    public String formatFromDate(){
//        CustomParameter parameter = CustomParameter.findByName(
//                CustomParameter.class, "SERVER_DATEFORMAT", "");
//        if(parameter!=null){
//        if(this!=null){
//        if(this.getLocale().equals("mr_IN")){
//            if(this.fromDate!=null){
//                return new SimpleDateFormat(parameter.getValue(),new Locale("hi","IN")).format(this.fromDate);
//            }else{
//                return "";
//            }
//        }else{
//            return new SimpleDateFormat(parameter.getValue(),new Locale("en","US")).format(this.fromDate);
//        }
//        }else{
//            return "";
//        }}else{
//            return "";
//        }
//    }
//    public String formatToDate(){
//        CustomParameter parameter = CustomParameter.findByName(
//                CustomParameter.class, "SERVER_DATEFORMAT", "");
//        if(parameter!=null){
//        if(this.getLocale().equals("mr_IN")){
//            if(this.fromDate!=null){
//                return new SimpleDateFormat(parameter.getValue(),new Locale("hi","IN")).format(this.toDate);
//            }else{
//                return "";
//            }
//        }else{
//            return new SimpleDateFormat(parameter.getValue(),new Locale("en","US")).format(this.toDate);
//        }
//        }else{
//            return "";
//        }
//    }
    // ------------------------------------------Getters/Setters-----------------------------------


    /**
     * Gets the position.
     *
     * @return the position
     */
    public String getPosition() {
        return position;
    }


    public String getFromDate() {
        if(this.getLocale().equals("mr_IN")){
            NumberFormat formatter=NumberFormat.getInstance(new Locale("hi","IN"));
            formatter.setGroupingUsed(false);
            return formatter.format(Long.parseLong(this.fromDate));
        }else{
            return fromDate;
        }
    }


    public void setFromDate(final String fromDate) {
        this.fromDate = fromDate;
    }


    public String getToDate() {
        if(this.getLocale().equals("mr_IN")){
            NumberFormat formatter=NumberFormat.getInstance(new Locale("hi","IN"));
            formatter.setGroupingUsed(false);
            return formatter.format(Long.parseLong(this.fromDate));
            }else{
            return toDate;
        }
    }


    public void setToDate(final String toDate) {
        this.toDate = toDate;
    }

    /**
     * Sets the position.
     *
     * @param position the new position
     */
    public void setPosition(final String position) {
        this.position = position;
    }


    public List<Member> getMembers() {
        return members;
    }

}
