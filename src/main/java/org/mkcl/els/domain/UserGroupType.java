package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="usergroups_types")
public class UserGroupType extends BaseDomain implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Column(length=1000)
    private String name;

    @Column(length=1000)
    private String type;

    public UserGroupType() {
        super();
    }

    public UserGroupType(final String name, final String type) {
        super();
        this.name = name;
        this.type = type;
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
}
