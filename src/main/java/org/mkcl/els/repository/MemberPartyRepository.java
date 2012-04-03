package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NoResultException;

import org.mkcl.els.domain.associations.MemberPartyAssociation;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberPartyRepository extends
        BaseRepository<MemberPartyAssociation, Serializable> {

    public MemberPartyAssociation findByMemberIdAndId(Long memberId,
            int recordIndex) {
        String query = "SELECT m FROM MemberPartyAssociation m WHERE m.member.id="
                + memberId + " AND m.recordIndex=" + recordIndex;

        try {
            return (MemberPartyAssociation) this.em().createQuery(query)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            e.printStackTrace();
            return new MemberPartyAssociation();
        }
    }

    @SuppressWarnings("unchecked")
    public int findHighestRecordIndex(Long member) {
        String query = "SELECT m FROM MemberPartyAssociation m WHERE m.member.id="
                + member + " ORDER BY m.recordIndex desc LIMIT 1";
        List<MemberPartyAssociation> associations = this.em()
                .createQuery(query).getResultList();
        if(associations.isEmpty()){
        	return 0;
        }else{
        return associations.get(0).getRecordIndex();
        }
    }

    public MemberPartyAssociation findByPK(MemberPartyAssociation association) {
        Search search = new Search();
        search.addFilterEqual("member", association.getMember());
        search.addFilterEqual("party", association.getParty());
        search.addFilterEqual("fromDate", association.getFromDate());
        search.addFilterEqual("toDate", association.getToDate());
        return (MemberPartyAssociation) this.searchUnique(search);
    }
}
