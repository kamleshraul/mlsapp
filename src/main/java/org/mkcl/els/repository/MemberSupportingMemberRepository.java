package org.mkcl.els.repository;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberSupportingMember;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
public class MemberSupportingMemberRepository extends BaseRepository<MemberSupportingMember, Long>{

	
	public List<MemberSupportingMember> findMemberSupportingMember(final DeviceType deviceType,
			final Member member,
			final Session session,
			final String locale) {
	    String strQuery = "SELECT msm" +
		" FROM MemberSupportingMember msm " +
		" WHERE msm.session.id = " + session.getId() +
		" AND msm.deviceType.id = " + deviceType.getId() +
		" AND msm.member.id = " + member.getId() +
		" AND msm.locale = '" + locale + "'";

		TypedQuery<MemberSupportingMember> query = this.em().createQuery(strQuery, MemberSupportingMember.class);
		List<MemberSupportingMember> members = query.getResultList();
		return members;
	}
	@Transactional
	public String deleteMemberSupportingMember(final DeviceType deviceType,
			final Member member,
			final Session session,
			final String locale) {
		try {
	    String strQuery = "delete " +
		" FROM MemberSupportingMember msm " +
		" WHERE msm.session.id =:sessionId " + 
		" AND msm.deviceType.id =:deviceTypeId " +
		" AND msm.member.id =:memberId " +
		" AND msm.locale =:locale";

	    Query jpQuery = this.em().createQuery(strQuery);
		jpQuery.setParameter("sessionId", session.getId());
		jpQuery.setParameter("deviceTypeId", deviceType.getId());
		jpQuery.setParameter("memberId", member.getId());
		jpQuery.setParameter("locale", locale);						

		jpQuery.executeUpdate();
		return "success";
	} catch (Exception e) {
		logger.error("FAILED",e);
		return "failed";
	}
	}
}
