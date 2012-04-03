package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NoResultException;

import org.mkcl.els.domain.associations.MemberMinistryAssociation;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberMinistryRepository extends
        BaseRepository<MemberMinistryAssociation, Serializable> {

    public MemberMinistryAssociation findByMemberIdAndId(Long memberId,
            int recordIndex) {
        String query = "SELECT m FROM MemberMinistryAssociation m WHERE m.member.id="
                + memberId + " AND m.recordIndex=" + recordIndex;

        try {
            return (MemberMinistryAssociation) this.em().createQuery(query)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            e.printStackTrace();
            return new MemberMinistryAssociation();
        }
    }

    @SuppressWarnings("unchecked")
    public int findHighestRecordIndex(Long member) {
        String query = "SELECT m FROM MemberMinistryAssociation m WHERE m.member.id="
                + member + " ORDER BY m.recordIndex desc LIMIT 1";
        List<MemberMinistryAssociation> associations = this.em()
                .createQuery(query).getResultList();
        if(associations.isEmpty()){
        return 0;
        }else{
        return associations.get(0).getRecordIndex();
        }

    }

    public MemberMinistryAssociation findByPK(
            MemberMinistryAssociation association) {
        Search search = new Search();
        search.addFilterEqual("member", association.getMember());
        search.addFilterEqual("ministry", association.getMinistry());
        search.addFilterEqual("fromDate", association.getFromDate());
        search.addFilterEqual("toDate", association.getToDate());
        return (MemberMinistryAssociation) this.searchUnique(search);
    }
}
