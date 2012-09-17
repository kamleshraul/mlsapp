package org.mkcl.els.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Entity
@Table(name="workflowactors")
@JsonIgnoreProperties({"userGroup"})
public class WorkflowActor extends BaseDomain{

    @ManyToOne(fetch=FetchType.LAZY)
    private UserGroup userGroup;

    private Integer level;

    public WorkflowActor() {
        super();
    }

    public WorkflowActor(final UserGroup userGroup, final Integer level) {
        super();
        this.userGroup = userGroup;
        this.level = level;
    }


    public UserGroup getUserGroup() {
        return userGroup;
    }


    public void setUserGroup(final UserGroup userGroup) {
        this.userGroup = userGroup;
    }


    public Integer getLevel() {
        return level;
    }


    public void setLevel(final Integer level) {
        this.level = level;
    }


}
