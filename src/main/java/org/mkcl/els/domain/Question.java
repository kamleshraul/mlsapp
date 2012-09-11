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
import org.mkcl.els.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "questions")
@JsonIgnoreProperties({"houseType","session","type","supportingMembers",
	"ministry","department","subDepartment","referencedQuestions","drafts"})
public class Question extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    // No need, since session has house which in turn has houseType
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "houseType_id")
    private HouseType houseType;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="session_id")
    private Session session;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="questionType_id")
    private DeviceType type;

    private Integer number;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submissionDate;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member primaryMember;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "questions_supportingmembers",
    		joinColumns = { @JoinColumn(name = "question_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "supportingmember_id", referencedColumnName = "id") })
    private List<Member> supportingMembers;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="group_id")
    private Group group;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ministry_id")
    private Ministry ministry;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="department_id")
    private Department department;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="subdepartment_id")
    private SubDepartment subDepartment;

    @Temporal(TemporalType.TIMESTAMP)
    private Date answeringDate;

    @Column(length=3000)
    private String subject;

    @Column(length=30000)
    private String questionText;

    @Column(length=30000)
    private String answer;

    private Integer priority;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "questions_references",
            joinColumns = { @JoinColumn(name = "question_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "reference_id", referencedColumnName = "id") })
    private List<Question> referencedQuestions;

    @Column(length=100)
    private String status;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="questions_drafts_association",
    		joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")},
    		inverseJoinColumns={@JoinColumn(name="question_draft_id", referencedColumnName="id")})
    private List<QuestionDraft> drafts;

    @Autowired
    private transient QuestionRepository questionRepository;

    public Question() {
        super();
    }

    public static QuestionRepository getQuestionRepository() {
        QuestionRepository questionRepository = new Question().questionRepository;
        if (questionRepository == null) {
            throw new IllegalStateException(
                    "QuestionRepository has not been injected in Question Domain");
        }
        return questionRepository;
    }

    public static Integer findLastStarredUnstarredShortNoticeQuestionNo(final House house,final Session currentSession) {
        return getQuestionRepository().findLastStarredUnstarredShortNoticeQuestionNo(house,currentSession);
    }

    public static Integer findLastHalfHourDiscussionQuestionNo(final House house,final Session currentSession) {
        return getQuestionRepository().findLastHalfHourDiscussionQuestionNo(house,currentSession);
    }

    public static Integer assignQuestionNo(final HouseType houseType,
            final Session session, final DeviceType questionType) {
        return getQuestionRepository().assignQuestionNo(houseType,
                session,questionType);
    }

    //========== Getters & Setters ==========
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

	public DeviceType getType() {
		DeviceType qt = this.type;
		QuestionDraft draft = this.getLatestDraft();
		if(draft != null) {
			qt = draft.getType();
		}
		return qt;
	}

	public void setType(final DeviceType type) {
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
		List<Member> sm = this.supportingMembers;
		QuestionDraft draft = this.getLatestDraft();
		if(draft != null) {
			sm = draft.getSupportingMembers();
		}
		return sm;
	}

	public void setSupportingMembers(final List<Member> supportingMembers) {
		this.supportingMembers = supportingMembers;
	}

	public Group getGroup() {
		Group g = this.group;
		QuestionDraft draft = this.getLatestDraft();
		if(draft != null) {
			g = draft.getGroup();
		}
		return g;
	}

	public void setGroup(final Group group) {
		this.group = group;
	}

    public Ministry getMinistry() {
    	Ministry m = this.ministry;
		QuestionDraft draft = this.getLatestDraft();
		if(draft != null) {
			m = draft.getMinistry();
		}
		return m;
    }

    public void setMinistry(final Ministry ministry) {
        this.ministry = ministry;
    }

    public Department getDepartment() {
    	Department d = this.department;
		QuestionDraft draft = this.getLatestDraft();
		if(draft != null) {
			d = draft.getDepartment();
		}
		return d;
    }

    public void setDepartment(final Department department) {
        this.department = department;
    }

    public Date getAnsweringDate() {
    	Date date = this.answeringDate;
		QuestionDraft draft = this.getLatestDraft();
		if(draft != null) {
			date = draft.getAnsweringDate();
		}
		return date;
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
    	Integer p = this.priority;
		QuestionDraft draft = this.getLatestDraft();
		if(draft != null) {
			p = draft.getPriority();
		}
		return p;
	}

	public void setPriority(final Integer priority) {
		this.priority = priority;
	}

	public List<Question> getReferencedQuestions() {
		List<Question> rq = this.referencedQuestions;
		QuestionDraft draft = this.getLatestDraft();
		if(draft != null) {
			rq = draft.getReferencedQuestions();
		}
		return rq;
	}

	public void setReferencedQuestions(final List<Question> referencedQuestions) {
		this.referencedQuestions = referencedQuestions;
	}

	public String getStatus() {
		String s = this.status;
		QuestionDraft draft = this.getLatestDraft();
		if(draft != null) {
			s = draft.getStatus();
		}
		return s;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public SubDepartment getSubDepartment() {
		SubDepartment sd = this.subDepartment;
		QuestionDraft draft = this.getLatestDraft();
		if(draft != null) {
			sd = draft.getSubDepartment();
		}
		return sd;
    }

    public void setSubDepartment(final SubDepartment subDepartment) {
        this.subDepartment = subDepartment;
    }

    public List<QuestionDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(final List<QuestionDraft> drafts) {
		this.drafts = drafts;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(final String answer) {
		this.answer = answer;
	}

	//========== Derived Methods ==========
	public String getRevisedSubject() {
		String revisedSubject = this.getSubject();
		QuestionDraft latestDraft = this.getLatestDraft();
		if(latestDraft != null) {
			revisedSubject = latestDraft.getSubject();
		}
		return revisedSubject;
	}

	public String getRevisedQuestionText() {
		String revisedQuestionText = this.getRevisedQuestionText();
		QuestionDraft latestDraft = this.getLatestDraft();
		if(latestDraft != null) {
			revisedQuestionText = latestDraft.getQuestionText();
		}
		return revisedQuestionText;
	}

	//========== Internal Methods ==========
	private QuestionDraft getLatestDraft() {
		QuestionDraft draft = null;
		Integer size=0;
		if(this.getDrafts()!=null){
		size = this.getDrafts().size();
		}
		if(size != 0) {
			draft = this.getDrafts().get(size - 1);
		}else{
		    draft=new QuestionDraft();
		}
		return draft;
	}

}
