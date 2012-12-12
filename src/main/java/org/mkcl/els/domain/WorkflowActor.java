package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Entity
@Table(name="workflowactors")
@JsonIgnoreProperties({"userGroup"})
public class WorkflowActor extends BaseDomain implements Serializable,Comparable<WorkflowActor>{

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch=FetchType.LAZY)
    private UserGroupType userGroupType;

    private Integer level;

    private String groupName;

    public WorkflowActor() {
        super();
    }

    public WorkflowActor(final String locale) {
        super(locale);
    }

    public UserGroupType getUserGroupType() {
        return userGroupType;
    }

    public void setUserGroupType(final UserGroupType userGroupType) {
        this.userGroupType = userGroupType;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(final Integer level) {
        this.level = level;
    }

	@Override
	public int compareTo(final WorkflowActor o) {
		int result=this.level-o.level;
		return result;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


    
}
