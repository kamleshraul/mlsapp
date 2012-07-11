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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;


@Configurable
@Entity
@Table(name = "questions")
@JsonIgnoreProperties({"houseType","session","type","primaryMember","supportingMembers","group","ministry","department","referencedQuestions"})
public class Question extends BaseDomain implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "houseType_id")
    private HouseType houseType;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="session_id")
    private Session session;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="questionType_id")
    private QuestionType type;

    private Integer number;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submissionDate;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member primaryMember;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "questions_supportingmembers",
            joinColumns = { @JoinColumn(name = "question_id",
                    referencedColumnName = "id") },
                    inverseJoinColumns = { @JoinColumn(name = "supportingmember_id",
                            referencedColumnName = "id") })
                            private List<Member> supportingMembers;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="group_id")
    private Group group;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ministry_id")
    private Ministry ministry;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="department")
    private Department department;

    @Temporal(TemporalType.TIMESTAMP)
    private Date answeringDate;

    @Column(length=3000)
    private String subject;

    @Column(length=30000)
    private String questionText;

    private Integer priority;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "questions_references",
            joinColumns = { @JoinColumn(name = "question_id",
                    referencedColumnName = "id") },
                    inverseJoinColumns = { @JoinColumn(name = "reference_id",
                            referencedColumnName = "id") })
                            private List<Question> referencedQuestions;

    @Column(length=100)
    private String status;

    public Question() {
        super();
    }

	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(final HouseType houseType) {
		this.houseType = houseType;
	}


	public Session getSession() {
		return session;
	}

	public void setSession(final Session session) {
		this.session = session;
	}

	public QuestionType getType() {
		return type;
	}

	public void setType(final QuestionType type) {
		this.type = type;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(final Integer number) {
		this.number = number;

	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(final Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Member getPrimaryMember() {
		return primaryMember;
	}

	public void setPrimaryMember(final Member primaryMember) {
		this.primaryMember = primaryMember;
	}

	public List<Member> getSupportingMembers() {
		return supportingMembers;
	}

	public void setSupportingMembers(final List<Member> supportingMembers) {
		this.supportingMembers = supportingMembers;
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

    public Date getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(final Date answeringDate) {
		this.answeringDate = answeringDate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

    public String getQuestionText() {
        return questionText;
    }


    public void setQuestionText(final String questionText) {
        this.questionText = questionText;
    }

    public Integer getPriority() {
		return priority;
	}

	public void setPriority(final Integer priority) {
		this.priority = priority;
	}

	public List<Question> getReferencedQuestions() {
		return referencedQuestions;
	}

	public void setReferencedQuestions(final List<Question> referencedQuestions) {
		this.referencedQuestions = referencedQuestions;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}
}
