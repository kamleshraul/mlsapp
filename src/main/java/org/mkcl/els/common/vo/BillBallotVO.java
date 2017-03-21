package org.mkcl.els.common.vo;

public class BillBallotVO {
	// =============== ATTRIBUTES ====================
	private Long id;
	private String checked;
	private String contentDraft;
	private String memberName;
	private String billNumber;
	private String billTitle;
	
	// =============== CONSTRUCTORS ==================
	public BillBallotVO() {
		super();
	}

	public BillBallotVO(String memberName, String billNumber,
			String billTitle) {
		super();
		this.memberName = memberName;
		this.billNumber = billNumber;
		this.billTitle = billTitle;
	}

	// =============== GETTERS/SETTERS ===============
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getContentDraft() {
		return contentDraft;
	}

	public void setContentDraft(String contentDraft) {
		this.contentDraft = contentDraft;
	}
	
	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getBillTitle() {
		return billTitle;
	}

	public void setBillTitle(String billTitle) {
		this.billTitle = billTitle;
	}	
}
