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

    @Column(length = 600)
    private String name;

    private Integer quota;

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

}
