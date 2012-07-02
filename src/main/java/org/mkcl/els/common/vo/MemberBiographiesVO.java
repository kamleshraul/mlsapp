package org.mkcl.els.common.vo;

import java.util.ArrayList;
import java.util.List;


public class MemberBiographiesVO {

    private List<MemberBiographyVO> memberBiographyVOs;

    public MemberBiographiesVO() {
        super();
        this.memberBiographyVOs = new ArrayList<MemberBiographyVO>();
    }

    public List<MemberBiographyVO> getMemberBiographyVOs() {
        return memberBiographyVOs;
    }

    public void setMemberBiographyVOs(final List<MemberBiographyVO> memberBiographyVOs) {
        this.memberBiographyVOs = memberBiographyVOs;
    }


}
