package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "upperhouse_constituencytype")
public class UpperHouseConstituencyType extends BaseDomain implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Column(length = 600)
    private String name;

    private Integer quota;

    @Column(length=1000)
    private String displayName;

    public UpperHouseConstituencyType() {
        super();
    }


    public String getName() {
        return name;
    }


    public void setName(final String name) {
        this.name = name;
    }


    public Integer getQuota() {
        return quota;
    }


    public void setQuota(final Integer quota) {
        this.quota = quota;
    }



    public String getDisplayName() {
        return displayName;
    }



    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }



}
