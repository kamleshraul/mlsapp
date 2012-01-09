package org.mkcl.els;

import org.junit.Test;
import org.mkcl.els.common.vo.MemberInfo;
import org.mkcl.els.common.vo.MemberSearchPage;
import org.mkcl.els.domain.MemberDetails;
import org.springframework.transaction.annotation.Transactional;

public class MemberSearchTest extends AbstractTest {

    @Transactional
    @Test
    public void testGetData() {
        MemberSearchPage searchPage = MemberDetails.searchMemberDetails(
                "name", 1, 4, "en");
        System.out.println(searchPage.getTotalRecords());
        for (MemberInfo i : searchPage.getPageItems()) {
            System.out.println(i.getFirstName() + ":" + i.getMiddleName() + ":"
                    + i.getLastName() + ":" + i.getConstituency() + ":"
                    + i.getParty() + ":" + i.isMaritalStatus() + ":"
                    + i.getGender() + ":" + i.getNoOfTerms() + ":"
                    + i.getBirthDate());
        }
    }
}
