package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberBallotAttendanceRepository extends BaseRepository<MemberBallotAttendance, Serializable>{

    public List<MemberBallotAttendance> findAll(final Session session,
            final DeviceType questionType, final String locale) {
        Search search=new Search();
        search.addFilterEqual("session.id", session.getId());
        search.addFilterEqual("deviceType.id", questionType.getId());
        search.addFilterEqual("locale", locale);
        search.addSort("member.lastName",true);
        return this.search(search);
    }

}
