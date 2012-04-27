package org.mkcl.els.common.vo;

public class MemberPartyWiseVO {
	private String party;

    /** The total member. */
    private String totalMember;

    /** The total female. */
    private String totalFemale;

    /** The total male. */
    private String totalMale;

	public String getParty() {
		return party;
	}

	public void setParty(String party) {
		this.party = party;
	}

	public String getTotalMember() {
		return totalMember;
	}

	public void setTotalMember(String totalMember) {
		this.totalMember = totalMember;
	}

	public String getTotalFemale() {
		return totalFemale;
	}

	public void setTotalFemale(String totalFemale) {
		this.totalFemale = totalFemale;
	}

	public String getTotalMale() {
		return totalMale;
	}

	public void setTotalMale(String totalMale) {
		this.totalMale = totalMale;
	}
}
