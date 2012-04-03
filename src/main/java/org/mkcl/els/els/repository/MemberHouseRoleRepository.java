package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NoResultException;

import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberHouseRoleRepository extends
        BaseRepository<HouseMemberRoleAssociation, Serializable> {

    public HouseMemberRoleAssociation findByMemberIdAndId(Long memberId,
            int recordIndex) {
        String query = "SELECT m FROM HouseMemberRoleAssociation m WHERE m.member.id="
                + memberId + " AND m.recordIndex=" + recordIndex;

        try {
            return (HouseMemberRoleAssociation) this.em().createQuery(query)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            e.printStackTrace();
            return new HouseMemberRoleAssociation();
        }
    }

    @SuppressWarnings("unchecked")
    public int findHighestRecordIndex(Long member) {
        String query = "SELECT m FROM HouseMemberRoleAssociation m WHERE m.member.id="
                + member + " ORDER BY m.recordIndex desc LIMIT 1";
        List<HouseMemberRoleAssociation> associations = this.em()
                .createQuery(query).getResultList();
        if(associations.isEmpty()){
        	return 0;
        }else{
        return associations.get(0).getRecordIndex();
        }
    }

    public HouseMemberRoleAssociation findByPK(
            HouseMemberRoleAssociation association) {
        Search search = new Search();
        search.addFilterEqual("member", association.getMember());
        search.addFilterEqual("role", association.getRole());
        search.addFilterEqual("house", association.getHouse());
        search.addFilterEqual("fromDate", association.getFromDate());
        search.addFilterEqual("toDate", association.getToDate());
        return (HouseMemberRoleAssociation) this.searchUnique(search);
    }
}
