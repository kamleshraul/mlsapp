package org.mkcl.els.common.vo;

public class QuestionSearchVO {

	private Long id;

	private Integer number;

	private String subject;

	private String questionText;

	private String revisedSubject;

	private String revisedQuestionText;

	private String primaryMember;

	private String ministry;

	private String department;

	private String subDepartment;

	private String answeringDate;

	private String group;

	private String status;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
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

	public String getRevisedSubject() {
		return revisedSubject;
	}

	public void setRevisedSubject(final String revisedSubject) {
		this.revisedSubject = revisedSubject;
	}

	public String getRevisedQuestionText() {
		return revisedQuestionText;
	}

	public void setRevisedQuestionText(final String revisedQuestionText) {
		this.revisedQuestionText = revisedQuestionText;
	}

	public String getPrimaryMember() {
		return primaryMember;
	}

	public void setPrimaryMember(final String primaryMember) {
		this.primaryMember = primaryMember;
	}

	public String getMinistry() {
		return ministry;
	}

	public void setMinistry(final String ministry) {
		this.ministry = ministry;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(final Integer number) {
		this.number = number;
	}


    public String getAnsweringDate() {
        return answeringDate;
    }


    public void setAnsweringDate(final String answeringDate) {
        this.answeringDate = answeringDate;
    }


    public String getGroup() {
        return group;
    }


    public void setGroup(final String group) {
        this.group = group;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(final String status) {
        this.status = status;
    }


    public String getDepartment() {
        return department;
    }


    public void setDepartment(final String department) {
        this.department = department;
    }


    public String getSubDepartment() {
        return subDepartment;
    }


    public void setSubDepartment(final String subDepartment) {
        this.subDepartment = subDepartment;
    }

}
