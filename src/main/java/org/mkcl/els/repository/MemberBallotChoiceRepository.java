package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallotChoice;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

@Repository
public class MemberBallotChoiceRepository extends BaseRepository<MemberBallotChoice, Serializable>{

    @SuppressWarnings("unchecked")
    public List<MemberBallotChoice> findByMember(final Session session,
            final DeviceType deviceType, final Member member, final String locale) {
        String query="SELECT mbc FROM MemberBallot mb JOIN mb.questionChoices mbc JOIN mb.session s JOIN mb.deviceType dt "+
                     " JOIN mb.member m WHERE s.id="+session.getId()+" AND dt.id="+deviceType.getId()+" AND m.id="+member.getId()+
                     " AND mb.locale='"+locale+"'";
        return this.em().createQuery(query).getResultList();
    }

}
