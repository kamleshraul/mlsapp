///**
// * See the file LICENSE for redistribution information.
// *
// * Copyright (c) 2011 MKCL.  All rights reserved.
// *
// * Project: els
// * File: org.mkcl.els.repository.MemberMinistryRepository
// * Created On: Apr 5, 2012
// */
//package org.mkcl.els.repository;
//
//import java.io.Serializable;
//import java.util.List;
//
//import javax.persistence.NoResultException;
//
//import org.mkcl.els.domain.associations.MemberMinistryAssociation;
//import org.springframework.stereotype.Repository;
//
//import com.trg.search.Search;
//
///**
// * The Class MemberMinistryRepository.
// *
// * @author vishals
// * @version 1.0.0
// */
//@Repository
//public class MemberMinistryRepository extends
//BaseRepository<MemberMinistryAssociation, Serializable> {
//
//    /**
//     * Find by member id and id.
//     *
//     * @param memberId the member id
//     * @param recordIndex the record index
//     * @return the member ministry association
//     */
//    public MemberMinistryAssociation findByMemberIdAndId(final Long memberId,
//            final int recordIndex) {
//        String query = "SELECT m FROM MemberMinistryAssociation m WHERE m.member.id="
//                + memberId + " AND m.recordIndex=" + recordIndex;
//
//        try {
//            return (MemberMinistryAssociation) this.em().createQuery(query)
//                    .getSingleResult();
//        } catch (NoResultException e) {
//            e.printStackTrace();
//            return new MemberMinistryAssociation();
//        }
//    }
//
//    /**
//     * Find highest record index.
//     *
//     * @param member the member
//     * @return the int
//     */
//    @SuppressWarnings("unchecked")
//    public int findHighestRecordIndex(final Long member) {
//        String query = "SELECT m FROM MemberMinistryAssociation m WHERE m.member.id="
//                + member + " ORDER BY m.recordIndex desc LIMIT 1";
//        List<MemberMinistryAssociation> associations = this.em()
//                .createQuery(query).getResultList();
//        if (associations.isEmpty()) {
//            return 0;
//        } else {
//            return associations.get(0).getRecordIndex();
//        }
//
//    }
//
//    /**
//     * Find by pk.
//     *
//     * @param association the association
//     * @return the member ministry association
//     */
//    public MemberMinistryAssociation findByPK(final MemberMinistryAssociation association) {
//        Search search = new Search();
//        search.addFilterEqual("member", association.getMember());
//        search.addFilterEqual("ministry", association.getMinistry());
//        search.addFilterEqual("fromDate", association.getFromDate());
//        search.addFilterEqual("toDate", association.getToDate());
//        return (MemberMinistryAssociation) this.searchUnique(search);
//    }
//}
