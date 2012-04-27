package org.mkcl.els.common.vo;

import java.util.List;

public class MemberChildrenWiseReportVO {
	/** The total male member count. */
    private String totalMaleMemberCount;

    /** The total female member count. */
    private String totalFemaleMemberCount;

    /** The total av male member count. */
    private String totalAvMaleMemberCount;

    /** The total av female member count. */
    private String totalAvFemaleMemberCount;

    /** The male rec not found. */
    private String maleRecNotFound;

    /** The female rec not found. */
    private String femaleRecNotFound;

    private String infoFoundFor;

    private String infoNotFoundFor;

    private String grossTotal;

    /** The member children wise v os. */
    private List<MemberChildrenWiseVO> memberChildrenWiseVOs;

	public String getTotalMaleMemberCount() {
		return totalMaleMemberCount;
	}

	public void setTotalMaleMemberCount(final String totalMaleMemberCount) {
		this.totalMaleMemberCount = totalMaleMemberCount;
	}

	public String getTotalFemaleMemberCount() {
		return totalFemaleMemberCount;
	}

	public void setTotalFemaleMemberCount(final String totalFemaleMemberCount) {
		this.totalFemaleMemberCount = totalFemaleMemberCount;
	}

	public String getTotalAvMaleMemberCount() {
		return totalAvMaleMemberCount;
	}

	public void setTotalAvMaleMemberCount(final String totalAvMaleMemberCount) {
		this.totalAvMaleMemberCount = totalAvMaleMemberCount;
	}

	public String getTotalAvFemaleMemberCount() {
		return totalAvFemaleMemberCount;
	}

	public void setTotalAvFemaleMemberCount(final String totalAvFemaleMemberCount) {
		this.totalAvFemaleMemberCount = totalAvFemaleMemberCount;
	}

	public String getMaleRecNotFound() {
		return maleRecNotFound;
	}

	public void setMaleRecNotFound(final String maleRecNotFound) {
		this.maleRecNotFound = maleRecNotFound;
	}

	public String getFemaleRecNotFound() {
		return femaleRecNotFound;
	}

	public void setFemaleRecNotFound(final String femaleRecNotFound) {
		this.femaleRecNotFound = femaleRecNotFound;
	}



    public String getInfoFoundFor() {
        return infoFoundFor;
    }


    public void setInfoFoundFor(final String infoFoundFor) {
        this.infoFoundFor = infoFoundFor;
    }


    public String getInfoNotFoundFor() {
        return infoNotFoundFor;
    }


    public void setInfoNotFoundFor(final String infoNotFoundFor) {
        this.infoNotFoundFor = infoNotFoundFor;
    }


    public String getGrossTotal() {
        return grossTotal;
    }


    public void setGrossTotal(final String grossTotal) {
        this.grossTotal = grossTotal;
    }

    public List<MemberChildrenWiseVO> getMemberChildrenWiseVOs() {
		return memberChildrenWiseVOs;
	}

	public void setMemberChildrenWiseVOs(
			final List<MemberChildrenWiseVO> memberChildrenWiseVOs) {
		this.memberChildrenWiseVOs = memberChildrenWiseVOs;
	}
}
