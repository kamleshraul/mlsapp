package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="clarification_needed_from")
public class ClarificationNeededFrom extends BaseDomain implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Column(length=3000)
    private String type;

    @Column(length=3000)
    private String name;

    public ClarificationNeededFrom() {
        super();
    }


    public String getType() {
        return type;
    }


    public void setType(final String type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }


    public void setName(final String name) {
        this.name = name;
    }

}
