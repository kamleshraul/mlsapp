package org.mkcl.els.common.vo;

public class PartyVO {

	//=============== ATTRIBUTES ====================
	private Long partyId;

	private String shortName;
	
	private String name;
	
	private String type;

	private Integer noOfMembers;

	//=============== CONSTRUCTORS ==================
	public PartyVO() {
		super();
	}

	//=============== GETTERS SETTERS ===============
	public Long getPartyId() {
		return partyId;
	}

	public void setPartyId(final Long partyId) {
		this.partyId = partyId;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(final String shortName) {
		this.shortName = shortName;
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

	public Integer getNoOfMembers() {
		return noOfMembers;
	}

	public void setNoOfMembers(final Integer noOfMembers) {
		this.noOfMembers = noOfMembers;
	}
}