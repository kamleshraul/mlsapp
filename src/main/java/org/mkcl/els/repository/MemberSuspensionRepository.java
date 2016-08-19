package org.mkcl.els.repository;



import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mkcl.els.domain.MemberSuspension;
import org.springframework.stereotype.Repository;


@Repository
public class MemberSuspensionRepository extends BaseRepository<MemberSuspension, Long> {

	 /**
     * Find by member id and id.
     *
     * @param memberId the member id
     * @param Id the Id
     * @return the MemberSuspension
     */
    public MemberSuspension findByMemberIdAndId(final Long memberId,
            final Long Id) {
        String strQuery = "SELECT m FROM MemberSuspension m WHERE m.member.id=:memberId " +
        		"AND m.id=:Id";

        try {
        	Query query=this.em().createQuery(strQuery);
        	query.setParameter("memberId", memberId);
        	query.setParameter("Id", Id);
      
        	return (MemberSuspension) query.getSingleResult();
         
        }catch (NoResultException e) {
            e.printStackTrace();
            return new MemberSuspension();
        }
    }

}