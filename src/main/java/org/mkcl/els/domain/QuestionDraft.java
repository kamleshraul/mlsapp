package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "question_drafts")
public class QuestionDraft extends BaseDomain implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="questionType_id")
    private QuestionType type;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "questionsdrafts_supportingmembers",
            joinColumns = { @JoinColumn(name = "questiondraft_id",
                    referencedColumnName = "id") },
                    inverseJoinColumns = { @JoinColumn(name = "supportingmember_id",
                            referencedColumnName = "id") })
                            private List<Member> supportingMembers;

    @Temporal(TemporalType.TIMESTAMP)
    private Date answeringDate;

    private Integer priority;

    @Column(length=3000)
    private String subject;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="group_id")
    private Group group;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ministry_id")
    private Ministry ministry;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="department")
    private Department department;

    @Column(length=30000)
    private String content;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "questionsdrafts_references",
            joinColumns = { @JoinColumn(name = "questiondraft_id",
                    referencedColumnName = "id") },
                    inverseJoinColumns = { @JoinColumn(name = "reference_id",
                            referencedColumnName = "id") })
                            private List<Question> referencedQuestions;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="editedby_id")
    private User editedBy;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="editedas_id")
    private Role editedAs;

    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name="editedon")
    private Date editedOn;

    public QuestionDraft() {
        super();
    }


    public QuestionType getType() {
        return type;
    }


    public void setType(final QuestionType type) {
        this.type = type;
    }


    public List<Member> getSupportingMembers() {
        return supportingMembers;
    }


    public void setSupportingMembers(final List<Member> supportingMembers) {
        this.supportingMembers = supportingMembers;
    }


    public Date getAnsweringDate() {
        return answeringDate;
    }


    public void setAnsweringDate(final Date answeringDate) {
        this.answeringDate = answeringDate;
    }


    public Integer getPriority() {
        return priority;
    }


    public void setPriority(final Integer priority) {
        this.priority = priority;
    }


    public String getSubject() {
        return subject;
    }


    public void setSubject(final String subject) {
        this.subject = subject;
    }


    public Group getGroup() {
        return group;
    }


    public void setGroup(final Group group) {
        this.group = group;
    }


    public Ministry getMinistry() {
        return ministry;
    }


    public void setMinistry(final Ministry ministry) {
        this.ministry = ministry;
    }


    public Department getDepartment() {
        return department;
    }


    public void setDepartment(final Department department) {
        this.department = department;
    }


    public String getContent() {
        return content;
    }


    public void setContent(final String content) {
        this.content = content;
    }


    public List<Question> getReferencedQuestions() {
        return referencedQuestions;
    }


    public void setReferencedQuestions(final List<Question> referencedQuestions) {
        this.referencedQuestions = referencedQuestions;
    }



    public User getEditedBy() {
        return editedBy;
    }



    public void setEditedBy(final User editedBy) {
        this.editedBy = editedBy;
    }



    public Role getEditedAs() {
        return editedAs;
    }



    public void setEditedAs(final Role editedAs) {
        this.editedAs = editedAs;
    }



    public Date getEditedOn() {
        return editedOn;
    }



    public void setEditedOn(final Date editedOn) {
        this.editedOn = editedOn;
    }


}
