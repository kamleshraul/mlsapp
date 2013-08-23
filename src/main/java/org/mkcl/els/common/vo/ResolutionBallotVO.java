package org.mkcl.els.common.vo;

public class ResolutionBallotVO {
	// =============== ATTRIBUTES ====================
	private Long id;
	private String checked;
	private String noticeContent;
	private String memberName;
	private String resolutionNumber;
	private String resolutionSubject;
	
	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(final String noticeContent) {
		this.noticeContent = noticeContent;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(final String checked) {
		this.checked = checked;
	}
	
	// =============== CONSTRUCTORS ==================
	public ResolutionBallotVO() {
		super();
	}

	public ResolutionBallotVO(final String memberName, final String resolutionNumber,
			String resolutionSubject) {
		super();
		this.memberName = memberName;
		this.resolutionNumber = resolutionNumber;
		this.resolutionSubject = resolutionSubject;
	}

	// =============== GETTERS/SETTERS ===============
	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(final String memberName) {
		this.memberName = memberName;
	}

	public String getResolutionNumber() {
		return resolutionNumber;
	}

	public void setResolutionNumber(final String resolutionNumber) {
		this.resolutionNumber = resolutionNumber;
	}

	public String getResolutionSubject() {
		return resolutionSubject;
	}

	public void setResolutionSubject(final String resolutionSubject) {
		this.resolutionSubject = resolutionSubject;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}
	
}
